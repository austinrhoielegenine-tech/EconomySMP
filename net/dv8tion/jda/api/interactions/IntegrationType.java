package net.dv8tion.jda.api.interactions;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Helpers;

public enum IntegrationType {
   UNKNOWN("-1"),
   GUILD_INSTALL("0"),
   USER_INSTALL("1");

   public static final Set<IntegrationType> ALL = Helpers.<IntegrationType>unmodifiableEnumSet(GUILD_INSTALL, USER_INSTALL);
   private final String key;

   private IntegrationType(String key) {
      this.key = key;
   }

   @Nonnull
   public String getType() {
      return this.key;
   }

   @Nonnull
   public static IntegrationType fromKey(@Nonnull String key) {
      for(IntegrationType value : values()) {
         if (value.key.equals(key)) {
            return value;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static IntegrationType[] $values() {
      return new IntegrationType[]{UNKNOWN, GUILD_INSTALL, USER_INSTALL};
   }
}
