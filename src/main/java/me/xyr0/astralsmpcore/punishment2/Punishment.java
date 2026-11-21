package me.xyr0.astralsmpcore.punishment2;

import java.util.UUID;

public class Punishment {
   private final UUID uuid;
   private final String player;
   private final String type;
   private final String reason;
   private final String duration;
   private final long expireTime;
   private final String staff;
   private final int offense;
   private final String banId;
   private final long date;
   private final boolean active;

   public Punishment(UUID uuid, String player, String type, String reason, String duration, long expireTime, String staff, int offense, String banId, long date, boolean active) {
      this.uuid = uuid;
      this.player = player;
      this.type = type;
      this.reason = reason;
      this.duration = duration;
      this.expireTime = expireTime;
      this.staff = staff;
      this.offense = offense;
      this.banId = banId;
      this.date = date;
      this.active = active;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getPlayer() {
      return this.player;
   }

   public String getType() {
      return this.type;
   }

   public String getReason() {
      return this.reason;
   }

   public String getDuration() {
      return this.duration;
   }

   public long getExpireTime() {
      return this.expireTime;
   }

   public String getStaff() {
      return this.staff;
   }

   public int getOffense() {
      return this.offense;
   }

   public String getBanId() {
      return this.banId;
   }

   public long getDate() {
      return this.date;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
   }
}
