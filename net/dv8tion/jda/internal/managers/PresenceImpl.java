package net.dv8tion.jda.internal.managers;

import java.util.Collections;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.Checks;

public class PresenceImpl implements Presence {
   private final JDAImpl api;
   private boolean idle = false;
   private Activity activity = null;
   private OnlineStatus status;

   public PresenceImpl(JDAImpl jda) {
      this.status = OnlineStatus.ONLINE;
      this.api = jda;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public OnlineStatus getStatus() {
      return this.status;
   }

   public Activity getActivity() {
      return this.activity;
   }

   public boolean isIdle() {
      return this.idle;
   }

   public void setStatus(OnlineStatus status) {
      this.setPresence(status, this.activity, this.idle);
   }

   public void setActivity(Activity game) {
      this.setPresence(this.status, game);
   }

   public void setIdle(boolean idle) {
      this.setPresence(this.status, idle);
   }

   public void setPresence(OnlineStatus status, Activity activity, boolean idle) {
      Checks.check(status != OnlineStatus.UNKNOWN, "Cannot set the presence status to an unknown OnlineStatus!");
      if (status == OnlineStatus.OFFLINE || status == null) {
         status = OnlineStatus.INVISIBLE;
      }

      this.idle = idle;
      this.status = status;
      this.activity = activity;
      this.update();
   }

   public void setPresence(OnlineStatus status, Activity activity) {
      this.setPresence(status, activity, this.idle);
   }

   public void setPresence(OnlineStatus status, boolean idle) {
      this.setPresence(status, this.activity, idle);
   }

   public void setPresence(Activity game, boolean idle) {
      this.setPresence(this.status, game, idle);
   }

   public PresenceImpl setCacheStatus(OnlineStatus status) {
      if (status == null) {
         throw new NullPointerException("Null OnlineStatus is not allowed.");
      } else {
         if (status == OnlineStatus.OFFLINE) {
            status = OnlineStatus.INVISIBLE;
         }

         this.status = status;
         return this;
      }
   }

   public PresenceImpl setCacheActivity(Activity game) {
      this.activity = game;
      return this;
   }

   public PresenceImpl setCacheIdle(boolean idle) {
      this.idle = idle;
      return this;
   }

   public DataObject getFullPresence() {
      DataObject activity = getGameJson(this.activity);
      return DataObject.empty().put("afk", this.idle).put("since", System.currentTimeMillis()).put("activities", DataArray.fromCollection(activity == null ? Collections.emptyList() : Collections.singletonList(activity))).put("status", this.getStatus().getKey());
   }

   public static DataObject getGameJson(Activity activity) {
      if (activity != null && activity.getName() != null && activity.getType() != null) {
         DataObject gameObj = DataObject.empty();
         if (activity.getType() == Activity.ActivityType.CUSTOM_STATUS) {
            gameObj.put("name", "Custom Status");
            gameObj.put("state", activity.getName());
         } else {
            gameObj.put("name", activity.getName());
            String state = activity.getState();
            if (state != null) {
               gameObj.put("state", state);
            }
         }

         gameObj.put("type", activity.getType().getKey());
         if (activity.getUrl() != null) {
            gameObj.put("url", activity.getUrl());
         }

         return gameObj;
      } else {
         return null;
      }
   }

   protected void update() {
      DataObject data = this.getFullPresence();
      JDA.Status status = this.api.getStatus();
      if (status != JDA.Status.RECONNECT_QUEUED && status != JDA.Status.SHUTDOWN && status != JDA.Status.SHUTTING_DOWN) {
         this.api.getClient().send(DataObject.empty().put("d", data).put("op", 3));
      }
   }
}
