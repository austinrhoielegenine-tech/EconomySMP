package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;

public interface TeamMember {
   @Nonnull
   User getUser();

   @Nonnull
   MembershipState getMembershipState();

   @Nonnull
   RoleType getRoleType();

   @Nonnull
   default String getTeamId() {
      return Long.toUnsignedString(this.getTeamIdLong());
   }

   long getTeamIdLong();

   public static enum MembershipState {
      INVITED(1),
      ACCEPTED(2),
      UNKNOWN(-1);

      private final int key;

      private MembershipState(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static MembershipState fromKey(int key) {
         for(MembershipState state : values()) {
            if (state.key == key) {
               return state;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static MembershipState[] $values() {
         return new MembershipState[]{INVITED, ACCEPTED, UNKNOWN};
      }
   }

   public static enum RoleType {
      OWNER(""),
      ADMIN("admin"),
      DEVELOPER("developer"),
      READ_ONLY("read_only"),
      UNKNOWN("");

      private final String key;

      private RoleType(String key) {
         this.key = key;
      }

      @Nonnull
      public String getKey() {
         return this.key;
      }

      @Nonnull
      public static RoleType fromKey(@Nonnull String key) {
         Checks.notNull(key, "Key");
         if (key.isEmpty()) {
            return UNKNOWN;
         } else {
            for(RoleType state : values()) {
               if (state.key.equals(key)) {
                  return state;
               }
            }

            return UNKNOWN;
         }
      }

      // $FF: synthetic method
      private static RoleType[] $values() {
         return new RoleType[]{OWNER, ADMIN, DEVELOPER, READ_ONLY, UNKNOWN};
      }
   }
}
