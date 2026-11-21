package net.dv8tion.jda.internal.handle;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.channel.concrete.ThreadChannelImpl;
import net.dv8tion.jda.internal.utils.UnlockHook;

public class ThreadMembersUpdateHandler extends SocketHandler {
   public ThreadMembersUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.api.getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         long threadId = content.getLong("id");
         ThreadChannelImpl thread = (ThreadChannelImpl)this.getJDA().getThreadChannelById(threadId);
         if (thread == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, threadId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"THREAD_MEMBERS_UPDATE attempted to update a thread that does not exist. JSON: {}", (Object)content);
            return null;
         } else {
            if (!content.isNull("added_members")) {
               DataArray addedMembersJson = content.getArray("added_members");
               this.handleAddedThreadMembers(thread, addedMembersJson);
            }

            if (!content.isNull("removed_member_ids")) {
               List<Long> removedMemberIds = (List)content.getArray("removed_member_ids").stream(DataArray::getString).map(MiscUtil::parseSnowflake).collect(Collectors.toList());
               this.handleRemovedThreadMembers(thread, removedMemberIds);
            }

            return null;
         }
      }
   }

   private void handleAddedThreadMembers(ThreadChannelImpl thread, DataArray addedMembersJson) {
      EntityBuilder entityBuilder = this.api.getEntityBuilder();
      CacheView.SimpleCacheView<ThreadMember> view = thread.getThreadMemberView();
      List<ThreadMember> addedThreadMembers = new ArrayList();

      for(int i = 0; i < addedMembersJson.length(); ++i) {
         DataObject threadMemberJson = addedMembersJson.getObject(i);
         ThreadMember threadMember = entityBuilder.createThreadMember(thread.getGuild(), thread, threadMemberJson);
         addedThreadMembers.add(threadMember);
      }

      UnlockHook lock = view.writeLock();

      try {
         for(ThreadMember threadMember : addedThreadMembers) {
            view.getMap().put(threadMember.getIdLong(), threadMember);
         }
      } catch (Throwable var10) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (lock != null) {
         lock.close();
      }

      for(ThreadMember threadMember : addedThreadMembers) {
         this.api.handleEvent(new ThreadMemberJoinEvent(this.api, this.responseNumber, thread, threadMember));
      }

   }

   private void handleRemovedThreadMembers(ThreadChannelImpl thread, List<Long> removedMemberIds) {
      CacheView.SimpleCacheView<ThreadMember> view = thread.getThreadMemberView();
      TLongObjectMap<ThreadMember> removedThreadMembers = new TLongObjectHashMap<ThreadMember>();
      UnlockHook lock = view.writeLock();

      try {
         for(long threadMemberId : removedMemberIds) {
            ThreadMember threadMember = (ThreadMember)view.getMap().remove(threadMemberId);
            removedThreadMembers.put(threadMemberId, threadMember);
         }
      } catch (Throwable var11) {
         if (lock != null) {
            try {
               lock.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (lock != null) {
         lock.close();
      }

      for(long threadMemberId : removedMemberIds) {
         this.api.handleEvent(new ThreadMemberLeaveEvent(this.api, this.responseNumber, thread, threadMemberId, removedThreadMembers.remove(threadMemberId)));
      }

   }
}
