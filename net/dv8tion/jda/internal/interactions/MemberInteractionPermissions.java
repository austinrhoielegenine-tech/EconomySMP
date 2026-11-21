package net.dv8tion.jda.internal.interactions;

public class MemberInteractionPermissions {
   private final long channelId;
   private final long permissions;

   public MemberInteractionPermissions(long channelId, long permissions) {
      this.channelId = channelId;
      this.permissions = permissions;
   }

   public long getChannelId() {
      return this.channelId;
   }

   public long getPermissions() {
      return this.permissions;
   }
}
