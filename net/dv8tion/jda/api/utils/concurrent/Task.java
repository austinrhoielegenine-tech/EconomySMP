package net.dv8tion.jda.api.utils.concurrent;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Blocking;

public interface Task<T> {
   boolean isStarted();

   @Nonnull
   Task<T> onError(@Nonnull Consumer<? super Throwable> var1);

   @Nonnull
   Task<T> onSuccess(@Nonnull Consumer<? super T> var1);

   @Nonnull
   Task<T> setTimeout(@Nonnull Duration var1);

   @Nonnull
   default Task<T> setTimeout(long timeout, @Nonnull TimeUnit unit) {
      Checks.notNull(unit, "TimeUnit");
      return this.setTimeout(Duration.ofMillis(unit.toMillis(timeout)));
   }

   @Nonnull
   @Blocking
   T get();

   void cancel();
}
