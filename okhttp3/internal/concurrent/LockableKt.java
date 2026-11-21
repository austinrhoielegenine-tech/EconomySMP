package okhttp3.internal.concurrent;

import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0003\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\u0004\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u0007H\u0080\b\u001a\r\u0010\b\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010\t\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a6\u0010\n\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u000b*\u00020\u00022\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\rH\u0086\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u000e\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u000f"},
   d2 = {"wait", "", "Lokhttp3/internal/concurrent/Lockable;", "notify", "notifyAll", "awaitNanos", "nanos", "", "assertLockNotHeld", "assertLockHeld", "withLock", "T", "action", "Lkotlin/Function0;", "(Lokhttp3/internal/concurrent/Lockable;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "okhttp"}
)
public final class LockableKt {
   public static final void wait(@NotNull Lockable $this$wait) {
      Intrinsics.checkNotNullParameter($this$wait, "<this>");
      int $i$f$wait = 0;
      ((Object)$this$wait).wait();
   }

   public static final void notify(@NotNull Lockable $this$notify) {
      Intrinsics.checkNotNullParameter($this$notify, "<this>");
      int $i$f$notify = 0;
      ((Object)$this$notify).notify();
   }

   public static final void notifyAll(@NotNull Lockable $this$notifyAll) {
      Intrinsics.checkNotNullParameter($this$notifyAll, "<this>");
      int $i$f$notifyAll = 0;
      ((Object)$this$notifyAll).notifyAll();
   }

   public static final void awaitNanos(@NotNull Lockable $this$awaitNanos, long nanos) {
      Intrinsics.checkNotNullParameter($this$awaitNanos, "<this>");
      int $i$f$awaitNanos = 0;
      long ms = nanos / 1000000L;
      long ns = nanos - ms * 1000000L;
      if (ms > 0L || nanos > 0L) {
         ((Object)$this$awaitNanos).wait(ms, (int)ns);
      }

   }

   public static final void assertLockNotHeld(@NotNull Lockable $this$assertLockNotHeld) {
      Intrinsics.checkNotNullParameter($this$assertLockNotHeld, "<this>");
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld);
      }
   }

   public static final void assertLockHeld(@NotNull Lockable $this$assertLockHeld) {
      Intrinsics.checkNotNullParameter($this$assertLockHeld, "<this>");
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld);
      }
   }

   public static final <T> T withLock(@NotNull Lockable param0, @NotNull Function0<? extends T> param1) {
      // $FF: Couldn't be decompiled
   }
}
