package okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\t\b\u0016¢\u0006\u0004\b\u0004\u0010\u0006J$\u0010\u0007\u001a\u00020\u00122\u0006\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003H\u0007J\u0015\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0003H\u0000¢\u0006\u0002\b\u0015J\u001d\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0003H\u0000¢\u0006\u0002\b\u0018J\f\u0010\u0019\u001a\u00020\u0003*\u00020\u0003H\u0002J\f\u0010\u001a\u001a\u00020\u0003*\u00020\u0003H\u0002J\u000e\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001d\u001a\u00020\u001eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001f"},
   d2 = {"Lokio/Throttler;", "", "allocatedUntil", "", "<init>", "(J)V", "()V", "bytesPerSecond", "waitByteCount", "maxByteCount", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "getLock", "()Ljava/util/concurrent/locks/ReentrantLock;", "condition", "Ljava/util/concurrent/locks/Condition;", "getCondition", "()Ljava/util/concurrent/locks/Condition;", "", "take", "byteCount", "take$okio", "byteCountOrWaitNanos", "now", "byteCountOrWaitNanos$okio", "nanosToBytes", "bytesToNanos", "source", "Lokio/Source;", "sink", "Lokio/Sink;", "okio"}
)
public final class Throttler {
   private long allocatedUntil;
   private long bytesPerSecond;
   private long waitByteCount;
   private long maxByteCount;
   @NotNull
   private final ReentrantLock lock;
   @NotNull
   private final Condition condition;

   public Throttler(long allocatedUntil) {
      this.allocatedUntil = allocatedUntil;
      this.waitByteCount = 8192L;
      this.maxByteCount = 262144L;
      this.lock = new ReentrantLock();
      Condition var10001 = this.lock.newCondition();
      Intrinsics.checkNotNullExpressionValue(var10001, "newCondition(...)");
      this.condition = var10001;
   }

   @NotNull
   public final ReentrantLock getLock() {
      return this.lock;
   }

   @NotNull
   public final Condition getCondition() {
      return this.condition;
   }

   public Throttler() {
      this(System.nanoTime());
   }

   @JvmOverloads
   public final void bytesPerSecond(long bytesPerSecond, long waitByteCount, long maxByteCount) {
      Lock var7 = (Lock)this.lock;
      var7.lock();

      try {
         int var8 = 0;
         if (bytesPerSecond < 0L) {
            String var14 = "Failed requirement.";
            throw new IllegalArgumentException(var14.toString());
         }

         if (waitByteCount <= 0L) {
            String var13 = "Failed requirement.";
            throw new IllegalArgumentException(var13.toString());
         }

         if (maxByteCount < waitByteCount) {
            String var9 = "Failed requirement.";
            throw new IllegalArgumentException(var9.toString());
         }

         this.bytesPerSecond = bytesPerSecond;
         this.waitByteCount = waitByteCount;
         this.maxByteCount = maxByteCount;
         this.condition.signalAll();
         Unit var12 = Unit.INSTANCE;
      } finally {
         var7.unlock();
      }

   }

   // $FF: synthetic method
   public static void bytesPerSecond$default(Throttler var0, long var1, long var3, long var5, int var7, Object var8) {
      if ((var7 & 2) != 0) {
         var3 = var0.waitByteCount;
      }

      if ((var7 & 4) != 0) {
         var5 = var0.maxByteCount;
      }

      var0.bytesPerSecond(var1, var3, var5);
   }

   public final long take$okio(long byteCount) {
      if (byteCount <= 0L) {
         String var14 = "Failed requirement.";
         throw new IllegalArgumentException(var14.toString());
      } else {
         Lock var3 = (Lock)this.lock;
         var3.lock();

         try {
            int var4 = 0;

            while(true) {
               long now = System.nanoTime();
               long byteCountOrWaitNanos = this.byteCountOrWaitNanos$okio(now, byteCount);
               if (byteCountOrWaitNanos >= 0L) {
                  long var10 = byteCountOrWaitNanos;
                  return var10;
               }

               this.condition.awaitNanos(-byteCountOrWaitNanos);
            }
         } finally {
            var3.unlock();
         }
      }
   }

   public final long byteCountOrWaitNanos$okio(long now, long byteCount) {
      if (this.bytesPerSecond == 0L) {
         return byteCount;
      } else {
         long idleInNanos = Math.max(this.allocatedUntil - now, 0L);
         long immediateBytes = this.maxByteCount - this.nanosToBytes(idleInNanos);
         if (immediateBytes >= byteCount) {
            this.allocatedUntil = now + idleInNanos + this.bytesToNanos(byteCount);
            return byteCount;
         } else if (immediateBytes >= this.waitByteCount) {
            this.allocatedUntil = now + this.bytesToNanos(this.maxByteCount);
            return immediateBytes;
         } else {
            long minByteCount = Math.min(this.waitByteCount, byteCount);
            long minWaitNanos = idleInNanos + this.bytesToNanos(minByteCount - this.maxByteCount);
            if (minWaitNanos == 0L) {
               this.allocatedUntil = now + this.bytesToNanos(this.maxByteCount);
               return minByteCount;
            } else {
               return -minWaitNanos;
            }
         }
      }
   }

   private final long nanosToBytes(long $this$nanosToBytes) {
      return $this$nanosToBytes * this.bytesPerSecond / 1000000000L;
   }

   private final long bytesToNanos(long $this$bytesToNanos) {
      return $this$bytesToNanos * 1000000000L / this.bytesPerSecond;
   }

   @NotNull
   public final Source source(@NotNull Source source) {
      Intrinsics.checkNotNullParameter(source, "source");
      return new ForwardingSource(source) {
         public long read(Buffer sink, long byteCount) {
            Intrinsics.checkNotNullParameter(sink, "sink");

            try {
               long toRead = Throttler.this.take$okio(byteCount);
               return super.read(sink, toRead);
            } catch (InterruptedException var6) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException("interrupted");
            }
         }
      };
   }

   @NotNull
   public final Sink sink(@NotNull Sink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      return new ForwardingSink(sink) {
         public void write(Buffer source, long byteCount) throws IOException {
            Intrinsics.checkNotNullParameter(source, "source");

            try {
               long toWrite;
               for(long remaining = byteCount; remaining > 0L; remaining -= toWrite) {
                  toWrite = Throttler.this.take$okio(remaining);
                  super.write(source, toWrite);
               }

            } catch (InterruptedException var8) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException("interrupted");
            }
         }
      };
   }

   @JvmOverloads
   public final void bytesPerSecond(long bytesPerSecond, long waitByteCount) {
      bytesPerSecond$default(this, bytesPerSecond, waitByteCount, 0L, 4, (Object)null);
   }

   @JvmOverloads
   public final void bytesPerSecond(long bytesPerSecond) {
      bytesPerSecond$default(this, bytesPerSecond, 0L, 0L, 6, (Object)null);
   }
}
