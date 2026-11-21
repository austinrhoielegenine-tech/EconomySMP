package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0016\u0018\u0000 %2\u00020\u0001:\u0002$%B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u000fJ\b\u0010\u0010\u001a\u00020\rH\u0016J\u0015\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\bH\u0000¢\u0006\u0002\b\u0013J\u0017\u0010\u0014\u001a\u00020\r2\b\b\u0002\u0010\u0012\u001a\u00020\bH\u0000¢\u0006\u0002\b\u0015J\b\u0010\u0016\u001a\u00020\rH\u0014J\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0017\u001a\u00020\u0018J\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0019\u001a\u00020\u001aJ%\u0010\u001b\u001a\u0002H\u001c\"\u0004\b\u0000\u0010\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u001c0\u001eH\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u001fJ\u0012\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010!H\u0001J\u0012\u0010#\u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010!H\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u00020\u00058\u0000@\u0000X\u0081\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\b@BX\u0080\u000e¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006&"},
   d2 = {"Lokio/AsyncTimeout;", "Lokio/Timeout;", "<init>", "()V", "state", "", "index", "value", "", "timeoutAt", "getTimeoutAt$okio", "()J", "enter", "", "exit", "", "cancel", "remainingNanos", "now", "remainingNanos$okio", "setTimeoutAt", "setTimeoutAt$okio", "timedOut", "sink", "Lokio/Sink;", "source", "Lokio/Source;", "withTimeout", "T", "block", "Lkotlin/Function0;", "(Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "access$newTimeoutException", "Ljava/io/IOException;", "cause", "newTimeoutException", "Watchdog", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nAsyncTimeout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AsyncTimeout.kt\nokio/AsyncTimeout\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,514:1\n1#2:515\n*E\n"})
public class AsyncTimeout extends Timeout {
   @NotNull
   private static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private int state;
   @JvmField
   public int index = -1;
   private long timeoutAt;
   @NotNull
   private static final PriorityQueue queue = new PriorityQueue();
   @Nullable
   private static AsyncTimeout idleSentinel;
   @NotNull
   private static final ReentrantLock lock = new ReentrantLock();
   @NotNull
   private static final Condition condition;
   private static final int TIMEOUT_WRITE_SIZE = 65536;
   private static final long IDLE_TIMEOUT_MILLIS;
   private static final long IDLE_TIMEOUT_NANOS;
   private static final int STATE_IDLE = 0;
   private static final int STATE_IN_QUEUE = 1;
   private static final int STATE_TIMED_OUT = 2;
   private static final int STATE_CANCELED = 3;

   public final long getTimeoutAt$okio() {
      return this.timeoutAt;
   }

   public final void enter() {
      long timeoutNanos = this.timeoutNanos();
      boolean hasDeadline = this.hasDeadline();
      if (timeoutNanos != 0L || hasDeadline) {
         Lock var4 = (Lock)lock;
         var4.lock();

         try {
            int var5 = 0;
            if (this.state != 0) {
               int var6 = 0;
               String var10 = "Unbalanced enter/exit";
               throw new IllegalStateException(var10.toString());
            }

            this.state = 1;
            Companion.insertIntoQueue(this);
            Unit var9 = Unit.INSTANCE;
         } finally {
            var4.unlock();
         }

      }
   }

   public final boolean exit() {
      Lock var1 = (Lock)lock;
      var1.lock();

      boolean var5;
      try {
         int var2 = 0;
         int oldState = this.state;
         this.state = 0;
         if (oldState != 1) {
            boolean var4 = oldState == 2;
            return var4;
         }

         queue.remove(this);
         var5 = false;
      } finally {
         var1.unlock();
      }

      return var5;
   }

   public void cancel() {
      super.cancel();
      Lock var1 = (Lock)lock;
      var1.lock();

      try {
         int var2 = 0;
         if (this.state == 1) {
            queue.remove(this);
            this.state = 3;
         }

         Unit var6 = Unit.INSTANCE;
      } finally {
         var1.unlock();
      }

   }

   public final long remainingNanos$okio(long now) {
      return this.timeoutAt - now;
   }

   public final void setTimeoutAt$okio(long now) {
      long timeoutNanos = this.timeoutNanos();
      boolean hasDeadline = this.hasDeadline();
      if (this.timeoutNanos() != 0L && this.hasDeadline()) {
         this.timeoutAt = now + Math.min(timeoutNanos, this.deadlineNanoTime() - now);
      } else if (timeoutNanos != 0L) {
         this.timeoutAt = now + timeoutNanos;
      } else {
         if (!hasDeadline) {
            throw new AssertionError();
         }

         this.timeoutAt = this.deadlineNanoTime();
      }

   }

   // $FF: synthetic method
   public static void setTimeoutAt$okio$default(AsyncTimeout var0, long var1, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: setTimeoutAt");
      } else {
         if ((var3 & 1) != 0) {
            var1 = System.nanoTime();
         }

         var0.setTimeoutAt$okio(var1);
      }
   }

   protected void timedOut() {
   }

   @NotNull
   public final Sink sink(@NotNull final Sink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      return new Sink() {
         public void write(Buffer source, long byteCount) {
            Intrinsics.checkNotNullParameter(source, "source");
            -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);

            long toWrite;
            for(long remaining = byteCount; remaining > 0L; remaining -= toWrite) {
               toWrite = 0L;
               Segment var10000 = source.head;
               Intrinsics.checkNotNull(var10000);

               for(Segment s = var10000; toWrite < 65536L; s = var10000) {
                  int segmentSize = s.limit - s.pos;
                  toWrite += (long)segmentSize;
                  if (toWrite >= remaining) {
                     toWrite = remaining;
                     break;
                  }

                  var10000 = s.next;
                  Intrinsics.checkNotNull(var10000);
               }

               AsyncTimeout this_$iv = AsyncTimeout.this;
               Sink var8 = sink;
               int $i$f$withTimeout = 0;
               boolean throwOnTimeout$iv = false;
               this_$iv.enter();
               boolean var18 = false;

               try {
                  var18 = true;
                  int var11 = false;
                  var8.write(source, toWrite);
                  Object result$iv = Unit.INSTANCE;
                  throwOnTimeout$iv = true;
                  var18 = false;
               } catch (IOException e$iv) {
                  throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
               } finally {
                  if (var18) {
                     boolean timedOut$iv = this_$iv.exit();
                     if (timedOut$iv && throwOnTimeout$iv) {
                        throw this_$iv.access$newTimeoutException((IOException)null);
                     }

                  }
               }

               boolean timedOut$iv = this_$iv.exit();
               if (timedOut$iv) {
                  throw this_$iv.access$newTimeoutException((IOException)null);
               }
            }

         }

         public void flush() {
            AsyncTimeout this_$iv = AsyncTimeout.this;
            Sink var2 = sink;
            int $i$f$withTimeout = 0;
            boolean throwOnTimeout$iv = false;
            this_$iv.enter();
            boolean var10 = false;

            try {
               var10 = true;
               boolean var5 = false;
               var2.flush();
               Object result$iv = Unit.INSTANCE;
               throwOnTimeout$iv = true;
               var10 = false;
            } catch (IOException e$iv) {
               throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
            } finally {
               if (var10) {
                  boolean timedOut$iv = this_$iv.exit();
                  if (timedOut$iv && throwOnTimeout$iv) {
                     throw this_$iv.access$newTimeoutException((IOException)null);
                  }

               }
            }

            boolean timedOut$iv = this_$iv.exit();
            if (timedOut$iv) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            }
         }

         public void close() {
            AsyncTimeout this_$iv = AsyncTimeout.this;
            Sink var2 = sink;
            int $i$f$withTimeout = 0;
            boolean throwOnTimeout$iv = false;
            this_$iv.enter();
            boolean var10 = false;

            try {
               var10 = true;
               boolean var5 = false;
               var2.close();
               Object result$iv = Unit.INSTANCE;
               throwOnTimeout$iv = true;
               var10 = false;
            } catch (IOException e$iv) {
               throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
            } finally {
               if (var10) {
                  boolean timedOut$iv = this_$iv.exit();
                  if (timedOut$iv && throwOnTimeout$iv) {
                     throw this_$iv.access$newTimeoutException((IOException)null);
                  }

               }
            }

            boolean timedOut$iv = this_$iv.exit();
            if (timedOut$iv) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            }
         }

         public AsyncTimeout timeout() {
            return AsyncTimeout.this;
         }

         public String toString() {
            return "AsyncTimeout.sink(" + sink + ')';
         }
      };
   }

   @NotNull
   public final Source source(@NotNull final Source source) {
      Intrinsics.checkNotNullParameter(source, "source");
      return new Source() {
         public long read(Buffer sink, long byteCount) {
            Intrinsics.checkNotNullParameter(sink, "sink");
            AsyncTimeout this_$iv = AsyncTimeout.this;
            Source var5 = source;
            int $i$f$withTimeout = 0;
            boolean throwOnTimeout$iv = false;
            this_$iv.enter();
            boolean var16 = false;

            long var11;
            try {
               var16 = true;
               int var8 = false;
               long result$iv = var5.read(sink, byteCount);
               throwOnTimeout$iv = true;
               var11 = result$iv;
               var16 = false;
            } catch (IOException e$iv) {
               throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
            } finally {
               if (var16) {
                  boolean timedOut$iv = this_$iv.exit();
                  if (timedOut$iv && throwOnTimeout$iv) {
                     throw this_$iv.access$newTimeoutException((IOException)null);
                  }

               }
            }

            boolean timedOut$iv = this_$iv.exit();
            if (timedOut$iv) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            } else {
               return var11;
            }
         }

         public void close() {
            AsyncTimeout this_$iv = AsyncTimeout.this;
            Source var2 = source;
            int $i$f$withTimeout = 0;
            boolean throwOnTimeout$iv = false;
            this_$iv.enter();
            boolean var10 = false;

            try {
               var10 = true;
               boolean var5 = false;
               var2.close();
               Object result$iv = Unit.INSTANCE;
               throwOnTimeout$iv = true;
               var10 = false;
            } catch (IOException e$iv) {
               throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
            } finally {
               if (var10) {
                  boolean timedOut$iv = this_$iv.exit();
                  if (timedOut$iv && throwOnTimeout$iv) {
                     throw this_$iv.access$newTimeoutException((IOException)null);
                  }

               }
            }

            boolean timedOut$iv = this_$iv.exit();
            if (timedOut$iv) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            }
         }

         public AsyncTimeout timeout() {
            return AsyncTimeout.this;
         }

         public String toString() {
            return "AsyncTimeout.source(" + source + ')';
         }
      };
   }

   public final <T> T withTimeout(@NotNull Function0<? extends T> block) {
      Intrinsics.checkNotNullParameter(block, "block");
      int $i$f$withTimeout = 0;
      boolean throwOnTimeout = false;
      this.enter();
      boolean var9 = false;

      Object var5;
      try {
         var9 = true;
         Object result = block.invoke();
         throwOnTimeout = true;
         var5 = result;
         var9 = false;
      } catch (IOException e) {
         throw !this.exit() ? (Throwable)e : (Throwable)this.access$newTimeoutException(e);
      } finally {
         if (var9) {
            InlineMarker.finallyStart(1);
            boolean timedOut = this.exit();
            if (timedOut && throwOnTimeout) {
               throw this.access$newTimeoutException((IOException)null);
            }

            InlineMarker.finallyEnd(1);
         }
      }

      InlineMarker.finallyStart(1);
      boolean timedOut = this.exit();
      if (timedOut) {
         throw this.access$newTimeoutException((IOException)null);
      } else {
         InlineMarker.finallyEnd(1);
         return (T)var5;
      }
   }

   @PublishedApi
   @NotNull
   public final IOException access$newTimeoutException(@Nullable IOException cause) {
      return this.newTimeoutException(cause);
   }

   @NotNull
   protected IOException newTimeoutException(@Nullable IOException cause) {
      InterruptedIOException e = new InterruptedIOException("timeout");
      if (cause != null) {
         e.initCause((Throwable)cause);
      }

      return (IOException)e;
   }

   static {
      Condition var10000 = lock.newCondition();
      Intrinsics.checkNotNullExpressionValue(var10000, "newCondition(...)");
      condition = var10000;
      IDLE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60L);
      IDLE_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(IDLE_TIMEOUT_MILLIS);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"},
      d2 = {"Lokio/AsyncTimeout$Watchdog;", "Ljava/lang/Thread;", "<init>", "()V", "run", "", "okio"}
   )
   private static final class Watchdog extends Thread {
      public Watchdog() {
         super("Okio Watchdog");
         this.setDaemon(true);
      }

      public void run() {
         while(true) {
            try {
               Object timedOut = null;
               Lock var2 = (Lock)AsyncTimeout.Companion.getLock();
               var2.lock();

               try {
                  int var3 = 0;
                  var9 = AsyncTimeout.Companion.awaitTimeout();
                  if (var9 == AsyncTimeout.Companion.getIdleSentinel()) {
                     AsyncTimeout.Companion.setIdleSentinel((AsyncTimeout)null);
                     return;
                  }

                  Unit var10 = Unit.INSTANCE;
               } finally {
                  var2.unlock();
               }

               if (var9 != null) {
                  var9.timedOut();
               }
            } catch (InterruptedException var8) {
            }
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0082\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\tH\u0002J\b\u0010\"\u001a\u0004\u0018\u00010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0017X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0017X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0017X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0017X\u0082T¢\u0006\u0002\n\u0000¨\u0006#"},
      d2 = {"Lokio/AsyncTimeout$Companion;", "", "<init>", "()V", "queue", "Lokio/PriorityQueue;", "getQueue", "()Lokio/PriorityQueue;", "idleSentinel", "Lokio/AsyncTimeout;", "getIdleSentinel", "()Lokio/AsyncTimeout;", "setIdleSentinel", "(Lokio/AsyncTimeout;)V", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "getLock", "()Ljava/util/concurrent/locks/ReentrantLock;", "condition", "Ljava/util/concurrent/locks/Condition;", "getCondition", "()Ljava/util/concurrent/locks/Condition;", "TIMEOUT_WRITE_SIZE", "", "IDLE_TIMEOUT_MILLIS", "", "IDLE_TIMEOUT_NANOS", "STATE_IDLE", "STATE_IN_QUEUE", "STATE_TIMED_OUT", "STATE_CANCELED", "insertIntoQueue", "", "node", "awaitTimeout", "okio"}
   )
   private static final class Companion {
      private Companion() {
      }

      @NotNull
      public final PriorityQueue getQueue() {
         return AsyncTimeout.queue;
      }

      @Nullable
      public final AsyncTimeout getIdleSentinel() {
         return AsyncTimeout.idleSentinel;
      }

      public final void setIdleSentinel(@Nullable AsyncTimeout var1) {
         AsyncTimeout.idleSentinel = var1;
      }

      @NotNull
      public final ReentrantLock getLock() {
         return AsyncTimeout.lock;
      }

      @NotNull
      public final Condition getCondition() {
         return AsyncTimeout.condition;
      }

      private final void insertIntoQueue(AsyncTimeout node) {
         if (this.getIdleSentinel() == null) {
            this.setIdleSentinel(new AsyncTimeout());
            (new Watchdog()).start();
         }

         AsyncTimeout.setTimeoutAt$okio$default(node, 0L, 1, (Object)null);
         this.getQueue().add(node);
         if (node.index == 1) {
            this.getCondition().signal();
         }

      }

      @Nullable
      public final AsyncTimeout awaitTimeout() throws InterruptedException {
         AsyncTimeout node = this.getQueue().first();
         if (node != null) {
            long waitNanos = node.remainingNanos$okio(System.nanoTime());
            if (waitNanos > 0L) {
               this.getCondition().await(waitNanos, TimeUnit.NANOSECONDS);
               return null;
            } else {
               this.getQueue().remove(node);
               node.state = 2;
               return node;
            }
         } else {
            long startNanos = System.nanoTime();
            this.getCondition().await(AsyncTimeout.IDLE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            return this.getQueue().first() == null && System.nanoTime() - startNanos >= AsyncTimeout.IDLE_TIMEOUT_NANOS ? this.getIdleSentinel() : null;
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
