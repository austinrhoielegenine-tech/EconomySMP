package net.dv8tion.jda.api.entities.automod;

import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.Incubating;

public enum AutoModEventType {
   MESSAGE_SEND(1),
   @Incubating
   MEMBER_UPDATE(2),
   UNKNOWN(-1);

   private final int key;

   private AutoModEventType(int key) {
      this.key = key;
   }

   public int getKey() {
      return this.key;
   }

   @Nonnull
   public static AutoModEventType fromKey(int key) {
      for(AutoModEventType type : values()) {
         if (type.key == key) {
            return type;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static AutoModEventType[] $values() {
      return new AutoModEventType[]{MESSAGE_SEND, MEMBER_UPDATE, UNKNOWN};
   }
}
