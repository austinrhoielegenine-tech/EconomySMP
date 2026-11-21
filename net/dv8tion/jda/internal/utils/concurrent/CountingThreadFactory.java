package net.dv8tion.jda.internal.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class CountingThreadFactory implements ThreadFactory {
   private final Supplier<String> identifier;
   private final AtomicLong count;
   private final boolean daemon;

   public CountingThreadFactory(@Nonnull Supplier<String> identifier, @Nonnull String specifier) {
      this(identifier, specifier, true);
   }

   public CountingThreadFactory(@Nonnull Supplier<String> identifier, @Nonnull String specifier, boolean daemon) {
      this.count = new AtomicLong(1L);
      this.identifier = () -> (String)identifier.get() + " " + specifier;
      this.daemon = daemon;
   }

   @Nonnull
   public Thread newThread(@Nonnull Runnable r) {
      Thread thread = new Thread(r, (String)this.identifier.get() + "-Worker " + this.count.getAndIncrement());
      thread.setDaemon(this.daemon);
      return thread;
   }
}
