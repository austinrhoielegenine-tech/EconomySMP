package net.dv8tion.jda.internal.interactions;

public class ChannelInteractionPermissions {
   private final long memberId;
   private final long permissions;

   public ChannelInteractionPermissions(long memberId, long permissions) {
      this.memberId = memberId;
      this.permissions = permissions;
   }

   public long getMemberId() {
      return this.memberId;
   }

   public long getPermissions() {
      return this.permissions;
   }
}
