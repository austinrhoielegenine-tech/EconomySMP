package net.dv8tion.jda.api.utils;

import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.UnknownNullability;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class MiscUtil {
   public static int getShardForGuild(long guildId, int shards) {
      return (int)((guildId >>> 22) % (long)shards);
   }

   public static int getShardForGuild(@Nonnull String guildId, int shards) {
      return getShardForGuild(parseSnowflake(guildId), shards);
   }

   public static int getShardForGuild(@Nonnull Guild guild, int shards) {
      return getShardForGuild(guild.getIdLong(), shards);
   }

   @Nonnull
   public static <T> TLongObjectMap<T> newLongMap() {
      return new TSynchronizedLongObjectMap<T>(new TLongObjectHashMap(), new Object());
   }

   public static long parseLong(@Nonnull String input) {
      return input.startsWith("-") ? Long.parseLong(input) : Long.parseUnsignedLong(input);
   }

   public static long parseSnowflake(@Nonnull String input) {
      Checks.notEmpty(input, "ID");

      try {
         return parseLong(input);
      } catch (NumberFormatException var2) {
         throw new NumberFormatException(Helpers.format("The specified ID is not a valid snowflake (%s). Expecting a valid long value!", input));
      }
   }

   @UnknownNullability
   public static <E> E locked(@Nonnull ReentrantLock lock, @Nonnull Supplier<E> task) {
      tryLock(lock);

      Object var2;
      try {
         var2 = task.get();
      } finally {
         lock.unlock();
      }

      return (E)var2;
   }

   public static void locked(@Nonnull ReentrantLock lock, @Nonnull Runnable task) {
      tryLock(lock);

      try {
         task.run();
      } finally {
         lock.unlock();
      }

   }

   public static void tryLock(@Nonnull Lock lock) {
      try {
         if (!lock.tryLock() && !lock.tryLock(10L, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Could not acquire lock in a reasonable timeframe! (10 seconds)");
         }
      } catch (InterruptedException var2) {
         throw new IllegalStateException("Unable to acquire lock while thread is interrupted!");
      }
   }

   public static void appendTo(@Nonnull Formatter formatter, int width, int precision, boolean leftJustified, @Nonnull String out) {
      try {
         Appendable appendable = formatter.out();
         if (precision > -1 && out.length() > precision) {
            appendable.append(Helpers.truncate(out, precision));
         } else {
            if (leftJustified) {
               appendable.append(Helpers.rightPad(out, width));
            } else {
               appendable.append(Helpers.leftPad(out, width));
            }

         }
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }
}
