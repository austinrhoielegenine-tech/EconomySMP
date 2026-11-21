package net.dv8tion.jda.internal.handle;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audio.hooks.ConnectionListener;
import net.dv8tion.jda.api.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.AbstractCacheView;

public class GuildSetupNode {
   private final long id;
   private final GuildSetupController controller;
   private final List<DataObject> cachedEvents = new LinkedList();
   private TLongObjectMap<DataObject> members;
   private TLongSet removedMembers;
   private DataObject partialGuild;
   private int expectedMemberCount = 1;
   boolean requestedChunk;
   final Type type;
   boolean firedUnavailableJoin = false;
   boolean markedUnavailable = false;
   GuildSetupController.Status status;

   GuildSetupNode(long id, GuildSetupController controller, Type type) {
      this.status = GuildSetupController.Status.INIT;
      this.id = id;
      this.controller = controller;
      this.type = type;
   }

   public long getIdLong() {
      return this.id;
   }

   public String getId() {
      return Long.toUnsignedString(this.id);
   }

   public GuildSetupController.Status getStatus() {
      return this.status;
   }

   @Nullable
   public DataObject getGuildPayload() {
      return this.partialGuild;
   }

   public int getExpectedMemberCount() {
      return this.expectedMemberCount;
   }

   public int getCurrentMemberCount() {
      TLongHashSet knownMembers = new TLongHashSet(this.members.keySet());
      knownMembers.removeAll(this.removedMembers);
      return knownMembers.size();
   }

   public Type getType() {
      return this.type;
   }

   public boolean isJoin() {
      return this.type == GuildSetupNode.Type.JOIN;
   }

   public boolean isMarkedUnavailable() {
      return this.markedUnavailable;
   }

   public boolean requestedChunks() {
      return this.requestedChunk;
   }

   public boolean containsMember(long userId) {
      return this.members != null && !this.members.isEmpty() ? this.members.containsKey(userId) : false;
   }

   public String toString() {
      return (new EntityString(this)).setType(this.type).addMetadata("id", this.id).addMetadata("status", this.status).addMetadata("expectedMemberCount", this.expectedMemberCount).addMetadata("requestedChunk", this.requestedChunk).addMetadata("markedUnavailable", this.markedUnavailable).toString();
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof GuildSetupNode)) {
         return false;
      } else {
         GuildSetupNode node = (GuildSetupNode)obj;
         return node.id == this.id;
      }
   }

   private GuildSetupController getController() {
      return this.controller;
   }

   void updateStatus(GuildSetupController.Status status) {
      if (status != this.status) {
         try {
            this.getController().listener.onStatusChange(this.id, this.status, status);
         } catch (Exception ex) {
            GuildSetupController.log.error((String)"Uncaught exception in status listener", (Throwable)ex);
         }

         this.status = status;
      }
   }

   void reset() {
      this.updateStatus(GuildSetupController.Status.UNAVAILABLE);
      this.expectedMemberCount = 1;
      this.partialGuild = null;
      this.requestedChunk = false;
      if (this.members != null) {
         this.members.clear();
      }

      if (this.removedMembers != null) {
         this.removedMembers.clear();
      }

      this.cachedEvents.clear();
   }

   void handleReady(DataObject obj) {
   }

   void handleCreate(DataObject obj) {
      if (this.partialGuild == null) {
         this.partialGuild = obj;
      } else {
         for(String key : obj.keys()) {
            this.partialGuild.put(key, obj.opt(key).orElse((Object)null));
         }
      }

      boolean unavailable = this.partialGuild.getBoolean("unavailable");
      this.markedUnavailable = unavailable;
      if (unavailable) {
         if (!this.firedUnavailableJoin && this.isJoin()) {
            this.firedUnavailableJoin = true;
            JDAImpl api = this.getController().getJDA();
            api.handleEvent(new UnavailableGuildJoinedEvent(api, api.getResponseTotal(), this.id));
         }

      } else {
         this.ensureMembers();
      }
   }

   void handleSync(DataObject obj) {
      if (this.partialGuild == null) {
         GuildSetupController.log.debug("Dropping sync update due to unavailable guild");
      } else {
         for(String key : obj.keys()) {
            this.partialGuild.put(key, obj.opt(key).orElse((Object)null));
         }

         this.ensureMembers();
      }
   }

   boolean handleMemberChunk(boolean last, DataArray arr) {
      if (this.partialGuild == null) {
         GuildSetupController.log.debug("Dropping member chunk due to unavailable guild");
         return true;
      } else {
         for(int index = 0; index < arr.length(); ++index) {
            DataObject obj = arr.getObject(index);
            long id = obj.getObject("user").getLong("id");
            this.members.put(id, obj);
         }

         if (!last && this.members.size() < this.expectedMemberCount && this.getController().getJDA().chunkGuild(this.id)) {
            return true;
         } else {
            this.completeSetup();
            return false;
         }
      }
   }

   void handleAddMember(DataObject member) {
      if (this.members != null && this.removedMembers != null) {
         ++this.expectedMemberCount;
         long userId = member.getObject("user").getLong("id");
         this.members.put(userId, member);
         this.removedMembers.remove(userId);
      }
   }

   void handleRemoveMember(DataObject member) {
      if (this.members != null && this.removedMembers != null) {
         --this.expectedMemberCount;
         long userId = member.getObject("user").getLong("id");
         this.members.remove(userId);
         this.removedMembers.add(userId);
         EventCache eventCache = this.getController().getJDA().getEventCache();
         if (!this.getController().containsMember(userId, this)) {
            eventCache.clear(EventCache.Type.USER, userId);
         }

      }
   }

   void cacheEvent(DataObject event) {
      GuildSetupController.log.trace((String)"Caching {} event during init. GuildId: {}", (Object)event.getString("t"), (Object)this.id);
      this.cachedEvents.add(event);
      int cacheSize = this.cachedEvents.size();
      if (cacheSize >= 2000 && cacheSize % 1000 == 0) {
         GuildSetupController controller = this.getController();
         GuildSetupController.log.warn("Accumulating suspicious amounts of cached events during guild setup, something might be wrong. Cached: {} Members: {}/{} Status: {} GuildId: {} Incomplete: {}/{}", cacheSize, this.getCurrentMemberCount(), this.getExpectedMemberCount(), this.status, this.id, controller.getChunkingCount(), controller.getIncompleteCount());
         if (this.status == GuildSetupController.Status.CHUNKING) {
            GuildSetupController.log.debug((String)"Forcing new chunk request for guild: {}", (Object)this.id);
            controller.sendChunkRequest(this.id);
         }
      }

   }

   void cleanup() {
      this.updateStatus(GuildSetupController.Status.REMOVED);
      EventCache eventCache = this.getController().getJDA().getEventCache();
      eventCache.clear(EventCache.Type.GUILD, this.id);
      if (this.partialGuild != null) {
         Optional<DataArray> channels = this.partialGuild.optArray("channels");
         Optional<DataArray> roles = this.partialGuild.optArray("roles");
         channels.ifPresent((arr) -> {
            for(int i = 0; i < arr.length(); ++i) {
               DataObject json = arr.getObject(i);
               long id = json.getLong("id");
               eventCache.clear(EventCache.Type.CHANNEL, id);
            }

         });
         roles.ifPresent((arr) -> {
            for(int i = 0; i < arr.length(); ++i) {
               DataObject json = arr.getObject(i);
               long id = json.getLong("id");
               eventCache.clear(EventCache.Type.ROLE, id);
            }

         });
         if (this.members != null) {
            TLongObjectIterator<DataObject> it = this.members.iterator();

            while(it.hasNext()) {
               it.advance();
               long userId = it.key();
               if (!this.getController().containsMember(userId, this)) {
                  eventCache.clear(EventCache.Type.USER, userId);
               }
            }
         }

      }
   }

   private void completeSetup() {
      this.updateStatus(GuildSetupController.Status.BUILDING);
      JDAImpl api = this.getController().getJDA();
      TLongIterator it = this.removedMembers.iterator();

      while(it.hasNext()) {
         this.members.remove(it.next());
      }

      this.removedMembers.clear();
      GuildImpl guild = api.getEntityBuilder().createGuild(this.id, this.partialGuild, this.members, this.expectedMemberCount);
      this.updateAudioManagerReference(guild);
      switch (this.type.ordinal()) {
         case 1:
            api.handleEvent(new GuildJoinEvent(api, api.getResponseTotal(), guild));
            if (this.requestedChunk) {
               this.getController().ready(this.id);
            } else {
               this.getController().remove(this.id);
            }
            break;
         case 2:
            api.handleEvent(new GuildAvailableEvent(api, api.getResponseTotal(), guild));
            this.getController().remove(this.id);
            break;
         default:
            api.handleEvent(new GuildReadyEvent(api, api.getResponseTotal(), guild));
            this.getController().ready(this.id);
      }

      this.updateStatus(GuildSetupController.Status.READY);
      GuildSetupController.log.debug((String)"Finished setup for guild {} firing cached events {}", (Object)this.id, (Object)this.cachedEvents.size());
      api.getClient().handle(this.cachedEvents);
      api.getEventCache().playbackCache(EventCache.Type.GUILD, this.id);
   }

   private void ensureMembers() {
      this.expectedMemberCount = this.partialGuild.getInt("member_count");
      this.members = new TLongObjectHashMap<DataObject>(this.expectedMemberCount);
      this.removedMembers = new TLongHashSet();
      DataArray memberArray = this.partialGuild.getArray("members");
      if (!this.getController().getJDA().chunkGuild(this.id)) {
         this.handleMemberChunk(true, memberArray);
      } else if (memberArray.length() < this.expectedMemberCount && !this.requestedChunk) {
         this.updateStatus(GuildSetupController.Status.CHUNKING);
         this.getController().addGuildForChunking(this.id, this.isJoin());
         this.requestedChunk = true;
      } else if (this.handleMemberChunk(false, memberArray) && !this.requestedChunk) {
         GuildSetupController.log.trace("Received suspicious members with a guild payload. Attempting to chunk. member_count: {} members: {} actual_members: {} guild_id: {}", this.expectedMemberCount, memberArray.length(), this.members.size(), this.id);
         this.members.clear();
         this.updateStatus(GuildSetupController.Status.CHUNKING);
         this.getController().addGuildForChunking(this.id, this.isJoin());
         this.requestedChunk = true;
      }

   }

   private void updateAudioManagerReference(GuildImpl guild) {
      JDAImpl api = this.getController().getJDA();
      AbstractCacheView<AudioManager> managerView = api.getAudioManagersView();
      UnlockHook hook = managerView.writeLock();

      label53: {
         try {
            TLongObjectMap<AudioManager> audioManagerMap = managerView.getMap();
            AudioManagerImpl mng = (AudioManagerImpl)audioManagerMap.get(this.id);
            if (mng == null) {
               break label53;
            }

            ConnectionListener listener = mng.getConnectionListener();
            AudioManagerImpl newMng = new AudioManagerImpl(guild);
            newMng.setSelfMuted(mng.isSelfMuted());
            newMng.setSelfDeafened(mng.isSelfDeafened());
            newMng.setQueueTimeout(mng.getConnectTimeout());
            newMng.setSendingHandler(mng.getSendingHandler());
            newMng.setReceivingHandler(mng.getReceivingHandler());
            newMng.setConnectionListener(listener);
            newMng.setAutoReconnect(mng.isAutoReconnect());
            if (mng.isConnected()) {
               long channelId = mng.getConnectedChannel().getIdLong();
               VoiceChannel channel = api.getVoiceChannelById(channelId);
               if (channel != null) {
                  if (mng.isConnected()) {
                     mng.closeAudioConnection(ConnectionStatus.ERROR_CANNOT_RESUME);
                  }
               } else {
                  api.getClient().removeAudioConnection(this.id);
                  if (listener != null) {
                     listener.onStatusChange(ConnectionStatus.DISCONNECTED_CHANNEL_DELETED);
                  }
               }
            }

            audioManagerMap.put(this.id, newMng);
         } catch (Throwable var13) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (hook != null) {
            hook.close();
         }

         return;
      }

      if (hook != null) {
         hook.close();
      }

   }

   public static enum Type {
      INIT,
      JOIN,
      AVAILABLE;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{INIT, JOIN, AVAILABLE};
      }
   }
}
