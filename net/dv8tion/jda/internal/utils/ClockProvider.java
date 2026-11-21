package net.dv8tion.jda.internal.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import javax.annotation.Nonnull;

public class ClockProvider {
   private static Clock fixedTime = null;

   @Nonnull
   public static Clock getClock() {
      return fixedTime != null ? fixedTime : Clock.systemUTC();
   }

   public static void withFixedTime(@Nonnull Instant instant, @Nonnull Runnable runnable) {
      fixedTime = Clock.fixed(instant, ZoneOffset.UTC);

      try {
         runnable.run();
      } finally {
         fixedTime = null;
      }

   }
}
