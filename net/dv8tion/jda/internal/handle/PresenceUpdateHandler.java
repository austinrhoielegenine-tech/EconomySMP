package net.dv8tion.jda.internal.handle;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ClientType;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.MemberPresenceImpl;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.UnlockHook;
import org.slf4j.Logger;

public class PresenceUpdateHandler extends SocketHandler {
   private static final Logger log = JDALogger.getLog(PresenceUpdateHandler.class);

   public PresenceUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      if (content.isNull("guild_id")) {
         log.debug("Received PRESENCE_UPDATE without guild_id. Ignoring event.");
         return null;
      } else if (this.api.getCacheFlags().stream().noneMatch(CacheFlag::isPresence)) {
         return null;
      } else {
         long guildId = content.getUnsignedLong("guild_id");
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
            if (guild == null) {
               this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
               EventCache.LOG.debug((String)"Received a PRESENCE_UPDATE for a guild that is not yet cached! GuildId:{} UserId: {}", (Object)guildId, (Object)content.getObject("user").get("id"));
               return null;
            } else {
               CacheView.SimpleCacheView<MemberPresenceImpl> presences = guild.getPresenceView();
               if (presences == null) {
                  return null;
               } else {
                  DataObject jsonUser = content.getObject("user");
                  long userId = jsonUser.getUnsignedLong("id");
                  MemberImpl member = (MemberImpl)guild.getMemberById(userId);
                  MemberPresenceImpl presence = (MemberPresenceImpl)presences.get(userId);
                  OnlineStatus status = OnlineStatus.fromKey(content.getString("status"));
                  if (status == OnlineStatus.OFFLINE) {
                     presences.remove(userId);
                  }

                  if (presence == null) {
                     presence = new MemberPresenceImpl();
                     if (status != OnlineStatus.OFFLINE) {
                        UnlockHook lock = presences.writeLock();

                        try {
                           presences.getMap().put(userId, presence);
                        } catch (Throwable var17) {
                           if (lock != null) {
                              try {
                                 lock.close();
                              } catch (Throwable var16) {
                                 var17.addSuppressed(var16);
                              }
                           }

                           throw var17;
                        }

                        if (lock != null) {
                           lock.close();
                        }
                     }
                  }

                  DataArray activityArray = this.getJDA().isCacheFlagSet(CacheFlag.ACTIVITY) && !content.isNull("activities") ? content.getArray("activities") : null;
                  List<Activity> newActivities = new ArrayList();
                  boolean parsedActivity = this.parseActivities(userId, activityArray, newActivities);
                  if (this.getJDA().isCacheFlagSet(CacheFlag.CLIENT_STATUS) && !content.isNull("client_status")) {
                     this.handleClientStatus(content, presence);
                  }

                  if (parsedActivity) {
                     this.handleActivities(newActivities, member, presence);
                  }

                  if (presence.getOnlineStatus() != status) {
                     OnlineStatus oldStatus = presence.getOnlineStatus();
                     presence.setOnlineStatus(status);
                     if (member != null) {
                        this.getJDA().getEntityBuilder().updateMemberCache(member);
                        this.getJDA().handleEvent(new UserUpdateOnlineStatusEvent(this.getJDA(), this.responseNumber, member, oldStatus));
                     }
                  }

                  return null;
               }
            }
         }
      }
   }

   private boolean parseActivities(long userId, DataArray activityArray, List<Activity> newActivities) {
      boolean parsedActivity = false;

      try {
         if (activityArray != null) {
            for(int i = 0; i < activityArray.length(); ++i) {
               newActivities.add(EntityBuilder.createActivity(activityArray.getObject(i)));
            }

            parsedActivity = true;
         }
      } catch (Exception ex) {
         if (EntityBuilder.LOG.isDebugEnabled()) {
            EntityBuilder.LOG.warn("Encountered exception trying to parse a presence! UserID: {} JSON: {}", userId, activityArray, ex);
         } else {
            EntityBuilder.LOG.warn((String)"Encountered exception trying to parse a presence! UserID: {} Message: {} Enable debug for details", (Object)userId, (Object)ex.getMessage());
         }
      }

      return parsedActivity;
   }

   private void handleActivities(List<Activity> newActivities, @Nullable MemberImpl member, MemberPresenceImpl presence) {
      List<Activity> oldActivities = presence.getActivities();
      presence.setActivities(newActivities);
      if (member != null) {
         boolean unorderedEquals = Helpers.deepEqualsUnordered(oldActivities, newActivities);
         if (unorderedEquals) {
            boolean deepEquals = Helpers.deepEquals(oldActivities, newActivities);
            if (!deepEquals) {
               this.getJDA().handleEvent(new UserUpdateActivityOrderEvent(this.getJDA(), this.responseNumber, oldActivities, member));
            }
         } else {
            this.getJDA().getEntityBuilder().updateMemberCache(member);
            List<Activity> stoppedActivities = new ArrayList(oldActivities);
            List<Activity> startedActivities = new ArrayList();

            for(Activity activity : newActivities) {
               if (!stoppedActivities.remove(activity)) {
                  startedActivities.add(activity);
               }
            }

            for(Activity activity : startedActivities) {
               this.getJDA().handleEvent(new UserActivityStartEvent(this.getJDA(), this.responseNumber, member, activity));
            }

            for(Activity activity : stoppedActivities) {
               this.getJDA().handleEvent(new UserActivityEndEvent(this.getJDA(), this.responseNumber, member, activity));
            }

            this.getJDA().handleEvent(new UserUpdateActivitiesEvent(this.getJDA(), this.responseNumber, member, oldActivities));
         }

      }
   }

   private void handleClientStatus(DataObject content, MemberPresenceImpl presence) {
      DataObject json = content.getObject("client_status");
      EnumSet<ClientType> types = EnumSet.of(ClientType.UNKNOWN);

      for(String key : json.keys()) {
         ClientType type = ClientType.fromKey(key);
         types.add(type);
         String raw = String.valueOf(json.get(key));
         OnlineStatus clientStatus = OnlineStatus.fromKey(raw);
         presence.setOnlineStatus(type, clientStatus);
      }

      for(ClientType type : EnumSet.complementOf(types)) {
         presence.setOnlineStatus(type, (OnlineStatus)null);
      }

   }
}
