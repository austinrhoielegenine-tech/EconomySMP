package okio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010*\u001a\u00020+2\u0006\u0010&\u001a\u00020\u0019J&\u0010,\u001a\u00020+*\u00020\u00192\u0017\u0010-\u001a\u0013\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020+0.¢\u0006\u0002\b/H\u0082\bJ\r\u0010&\u001a\u00020\u0019H\u0007¢\u0006\u0002\b0J\r\u0010'\u001a\u00020(H\u0007¢\u0006\u0002\b1J\u0006\u00102\u001a\u00020+R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\rX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\rX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u000f\"\u0004\b\u0014\u0010\u0011R\u001a\u0010\u0015\u001a\u00020\rX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000f\"\u0004\b\u0017\u0010\u0011R\u001c\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u0011\u0010\u001e\u001a\u00020\u001f¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\"\u001a\u00020#¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0013\u0010&\u001a\u00020\u00198\u0007¢\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001bR\u0013\u0010'\u001a\u00020(8\u0007¢\u0006\b\n\u0000\u001a\u0004\b'\u0010)¨\u00063"},
   d2 = {"Lokio/Pipe;", "", "maxBufferSize", "", "<init>", "(J)V", "getMaxBufferSize$okio", "()J", "buffer", "Lokio/Buffer;", "getBuffer$okio", "()Lokio/Buffer;", "canceled", "", "getCanceled$okio", "()Z", "setCanceled$okio", "(Z)V", "sinkClosed", "getSinkClosed$okio", "setSinkClosed$okio", "sourceClosed", "getSourceClosed$okio", "setSourceClosed$okio", "foldedSink", "Lokio/Sink;", "getFoldedSink$okio", "()Lokio/Sink;", "setFoldedSink$okio", "(Lokio/Sink;)V", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "getLock", "()Ljava/util/concurrent/locks/ReentrantLock;", "condition", "Ljava/util/concurrent/locks/Condition;", "getCondition", "()Ljava/util/concurrent/locks/Condition;", "sink", "source", "Lokio/Source;", "()Lokio/Source;", "fold", "", "forward", "block", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "-deprecated_sink", "-deprecated_source", "cancel", "okio"}
)
@SourceDebugExtension({"SMAP\nPipe.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Pipe.kt\nokio/Pipe\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Timeout.kt\nokio/Timeout\n*L\n1#1,262:1\n1#2:263\n302#3,26:264\n*S KotlinDebug\n*F\n+ 1 Pipe.kt\nokio/Pipe\n*L\n222#1:264,26\n*E\n"})
public final class Pipe {
   private final long maxBufferSize;
   @NotNull
   private final Buffer buffer;
   private boolean canceled;
   private boolean sinkClosed;
   private boolean sourceClosed;
   @Nullable
   private Sink foldedSink;
   @NotNull
   private final ReentrantLock lock;
   @NotNull
   private final Condition condition;
   @NotNull
   private final Sink sink;
   @NotNull
   private final Source source;

   public Pipe(long maxBufferSize) {
      this.maxBufferSize = maxBufferSize;
      this.buffer = new Buffer();
      this.lock = new ReentrantLock();
      Condition var10001 = this.lock.newCondition();
      Intrinsics.checkNotNullExpressionValue(var10001, "newCondition(...)");
      this.condition = var10001;
      if (this.maxBufferSize < 1L) {
         int var3 = 0;
         String var4 = "maxBufferSize < 1: " + this.maxBufferSize;
         throw new IllegalArgumentException(var4.toString());
      } else {
         this.sink = new Sink() {
            private final Timeout timeout = new Timeout();

            public void write(Buffer source, long byteCountx) {
               Intrinsics.checkNotNullParameter(source, "source");
               long byteCount = 0L;
               byteCount = byteCount;
               Object delegate = null;
               Lock var5 = (Lock)Pipe.this.getLock();
               Pipe this_$iv = Pipe.this;
               var5.lock();

               try {
                  int $this$write_u24lambda_u241 = 0;
                  if (this_$iv.getSinkClosed$okio()) {
                     int var8 = 0;
                     String $i$f$forward = "closed";
                     throw new IllegalStateException($i$f$forward.toString());
                  }

                  if (this_$iv.getCanceled$okio()) {
                     throw new IOException("canceled");
                  }

                  while(true) {
                     if (byteCount <= 0L) {
                        break;
                     }

                     Sink var10000 = this_$iv.getFoldedSink$okio();
                     if (var10000 != null) {
                        Sink it = var10000;
                        int var10 = 0;
                        delegate = it;
                        break;
                     }

                     if (this_$iv.getSourceClosed$okio()) {
                        throw new IOException("source is closed");
                     }

                     long bufferSpaceAvailable = this_$iv.getMaxBufferSize$okio() - this_$iv.getBuffer$okio().size();
                     if (bufferSpaceAvailable == 0L) {
                        this.timeout.awaitSignal(this_$iv.getCondition());
                        if (this_$iv.getCanceled$okio()) {
                           throw new IOException("canceled");
                        }
                     } else {
                        long bytesToWrite = Math.min(bufferSpaceAvailable, byteCount);
                        this_$iv.getBuffer$okio().write(source, bytesToWrite);
                        byteCount -= bytesToWrite;
                        this_$iv.getCondition().signalAll();
                     }
                  }

                  Unit $this$forward$iv = Unit.INSTANCE;
               } finally {
                  var5.unlock();
               }

               if (delegate != null) {
                  this_$iv = Pipe.this;
                  Sink $this$forward$iv = (Sink)delegate;
                  int $i$f$forward = 0;
                  Timeout this_$iv$iv = ((Sink)delegate).timeout();
                  Timeout other$iv$iv = this_$iv.sink().timeout();
                  int $i$f$intersectWith = 0;
                  long originalTimeout$iv$iv = this_$iv$iv.timeoutNanos();
                  this_$iv$iv.timeout(Timeout.Companion.minTimeout(other$iv$iv.timeoutNanos(), this_$iv$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
                  if (this_$iv$iv.hasDeadline()) {
                     long originalDeadline$iv$iv = this_$iv$iv.deadlineNanoTime();
                     if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(Math.min(this_$iv$iv.deadlineNanoTime(), other$iv$iv.deadlineNanoTime()));
                     }

                     try {
                        int var16 = 0;
                        int var18 = 0;
                        $this$forward$iv.write(source, byteCount);
                        Unit var45 = Unit.INSTANCE;
                     } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.hasDeadline()) {
                           this_$iv$iv.deadlineNanoTime(originalDeadline$iv$iv);
                        }

                     }
                  } else {
                     if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(other$iv$iv.deadlineNanoTime());
                     }

                     try {
                        int var20 = 0;
                        int var46 = 0;
                        $this$forward$iv.write(source, byteCount);
                        Unit var21 = Unit.INSTANCE;
                     } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.hasDeadline()) {
                           this_$iv$iv.clearDeadline();
                        }

                     }
                  }
               }

            }

            public void flush() {
               Object delegate = null;
               Lock var2 = (Lock)Pipe.this.getLock();
               Pipe this_$iv = Pipe.this;
               var2.lock();

               try {
                  int $this$flush_u24lambda_u243 = 0;
                  if (this_$iv.getSinkClosed$okio()) {
                     int var5 = 0;
                     String $i$f$forward = "closed";
                     throw new IllegalStateException($i$f$forward.toString());
                  }

                  if (this_$iv.getCanceled$okio()) {
                     throw new IOException("canceled");
                  }

                  Sink var10000 = this_$iv.getFoldedSink$okio();
                  if (var10000 != null) {
                     Sink it = var10000;
                     int var7 = 0;
                     delegate = it;
                  } else if (this_$iv.getSourceClosed$okio() && this_$iv.getBuffer$okio().size() > 0L) {
                     throw new IOException("source is closed");
                  }

                  Unit $this$forward$iv = Unit.INSTANCE;
               } finally {
                  var2.unlock();
               }

               if (delegate != null) {
                  this_$iv = Pipe.this;
                  Sink $this$forward$iv = (Sink)delegate;
                  int $i$f$forward = 0;
                  Timeout this_$iv$iv = ((Sink)delegate).timeout();
                  Timeout other$iv$iv = this_$iv.sink().timeout();
                  int $i$f$intersectWith = 0;
                  long originalTimeout$iv$iv = this_$iv$iv.timeoutNanos();
                  this_$iv$iv.timeout(Timeout.Companion.minTimeout(other$iv$iv.timeoutNanos(), this_$iv$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
                  if (this_$iv$iv.hasDeadline()) {
                     long originalDeadline$iv$iv = this_$iv$iv.deadlineNanoTime();
                     if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(Math.min(this_$iv$iv.deadlineNanoTime(), other$iv$iv.deadlineNanoTime()));
                     }

                     try {
                        int var13 = 0;
                        int var15 = 0;
                        $this$forward$iv.flush();
                        Unit var37 = Unit.INSTANCE;
                     } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.hasDeadline()) {
                           this_$iv$iv.deadlineNanoTime(originalDeadline$iv$iv);
                        }

                     }
                  } else {
                     if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(other$iv$iv.deadlineNanoTime());
                     }

                     try {
                        int var17 = 0;
                        int var38 = 0;
                        $this$forward$iv.flush();
                        Unit $this$flush_u24lambda_u243 = Unit.INSTANCE;
                     } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.hasDeadline()) {
                           this_$iv$iv.clearDeadline();
                        }

                     }
                  }
               }

            }

            public void close() {
               Object delegate = null;
               Lock var2 = (Lock)Pipe.this.getLock();
               Pipe this_$iv = Pipe.this;
               var2.lock();

               try {
                  int $this$close_u24lambda_u245 = 0;
                  if (this_$iv.getSinkClosed$okio()) {
                     return;
                  }

                  Sink var10000 = this_$iv.getFoldedSink$okio();
                  if (var10000 != null) {
                     Sink it = var10000;
                     int var6 = 0;
                     delegate = it;
                  } else {
                     if (this_$iv.getSourceClosed$okio() && this_$iv.getBuffer$okio().size() > 0L) {
                        throw new IOException("source is closed");
                     }

                     this_$iv.setSinkClosed$okio(true);
                     this_$iv.getCondition().signalAll();
                  }

                  Unit $this$close_u24lambda_u245 = Unit.INSTANCE;
               } finally {
                  var2.unlock();
               }

               if (delegate != null) {
                  this_$iv = Pipe.this;
                  Sink $this$forward$iv = (Sink)delegate;
                  int $i$f$forward = 0;
                  Timeout this_$iv$iv = ((Sink)delegate).timeout();
                  Timeout other$iv$iv = this_$iv.sink().timeout();
                  int $i$f$intersectWith = 0;
                  long originalTimeout$iv$iv = this_$iv$iv.timeoutNanos();
                  this_$iv$iv.timeout(Timeout.Companion.minTimeout(other$iv$iv.timeoutNanos(), this_$iv$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
                  if (this_$iv$iv.hasDeadline()) {
                     long originalDeadline$iv$iv = this_$iv$iv.deadlineNanoTime();
                     if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(Math.min(this_$iv$iv.deadlineNanoTime(), other$iv$iv.deadlineNanoTime()));
                     }

                     try {
                        int var13 = 0;
                        int var15 = 0;
                        $this$forward$iv.close();
                        Unit var35 = Unit.INSTANCE;
                     } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.hasDeadline()) {
                           this_$iv$iv.deadlineNanoTime(originalDeadline$iv$iv);
                        }

                     }
                  } else {
                     if (other$iv$iv.hasDeadline()) {
                        this_$iv$iv.deadlineNanoTime(other$iv$iv.deadlineNanoTime());
                     }

                     try {
                        int var17 = 0;
                        int var36 = 0;
                        $this$forward$iv.close();
                        Unit $this$close_u24lambda_u245 = Unit.INSTANCE;
                     } finally {
                        this_$iv$iv.timeout(originalTimeout$iv$iv, TimeUnit.NANOSECONDS);
                        if (other$iv$iv.hasDeadline()) {
                           this_$iv$iv.clearDeadline();
                        }

                     }
                  }
               }

            }

            public Timeout timeout() {
               return this.timeout;
            }
         };
         this.source = new Source() {
            private final Timeout timeout = new Timeout();

            public long read(Buffer sink, long byteCount) {
               Intrinsics.checkNotNullParameter(sink, "sink");
               Lock var4 = (Lock)Pipe.this.getLock();
               Pipe var5 = Pipe.this;
               var4.lock();

               try {
                  int var6 = 0;
                  if (var5.getSourceClosed$okio()) {
                     int var7 = 0;
                     String var16 = "closed";
                     throw new IllegalStateException(var16.toString());
                  } else if (var5.getCanceled$okio()) {
                     throw new IOException("canceled");
                  } else {
                     while(var5.getBuffer$okio().size() == 0L) {
                        if (var5.getSinkClosed$okio()) {
                           long var12 = -1L;
                           return var12;
                        }

                        this.timeout.awaitSignal(var5.getCondition());
                        if (var5.getCanceled$okio()) {
                           throw new IOException("canceled");
                        }
                     }

                     long result = var5.getBuffer$okio().read(sink, byteCount);
                     var5.getCondition().signalAll();
                     long var10 = result;
                     return var10;
                  }
               } finally {
                  var4.unlock();
               }
            }

            public void close() {
               Lock var1 = (Lock)Pipe.this.getLock();
               Pipe var2 = Pipe.this;
               var1.lock();

               try {
                  int var3 = 0;
                  var2.setSourceClosed$okio(true);
                  var2.getCondition().signalAll();
                  Unit var7 = Unit.INSTANCE;
               } finally {
                  var1.unlock();
               }

            }

            public Timeout timeout() {
               return this.timeout;
            }
         };
      }
   }

   public final long getMaxBufferSize$okio() {
      return this.maxBufferSize;
   }

   @NotNull
   public final Buffer getBuffer$okio() {
      return this.buffer;
   }

   public final boolean getCanceled$okio() {
      return this.canceled;
   }

   public final void setCanceled$okio(boolean var1) {
      this.canceled = var1;
   }

   public final boolean getSinkClosed$okio() {
      return this.sinkClosed;
   }

   public final void setSinkClosed$okio(boolean var1) {
      this.sinkClosed = var1;
   }

   public final boolean getSourceClosed$okio() {
      return this.sourceClosed;
   }

   public final void setSourceClosed$okio(boolean var1) {
      this.sourceClosed = var1;
   }

   @Nullable
   public final Sink getFoldedSink$okio() {
      return this.foldedSink;
   }

   public final void setFoldedSink$okio(@Nullable Sink var1) {
      this.foldedSink = var1;
   }

   @NotNull
   public final ReentrantLock getLock() {
      return this.lock;
   }

   @NotNull
   public final Condition getCondition() {
      return this.condition;
   }

   @JvmName(
      name = "sink"
   )
   @NotNull
   public final Sink sink() {
      return this.sink;
   }

   @JvmName(
      name = "source"
   )
   @NotNull
   public final Source source() {
      return this.source;
   }

   public final void fold(@NotNull Sink sink) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");

      while(true) {
         boolean closed = false;
         boolean done = false;
         Object sinkBuffer = null;
         Lock var5 = (Lock)this.lock;
         var5.lock();

         try {
            int var6 = 0;
            if (this.foldedSink != null) {
               int var7 = 0;
               String var27 = "sink already folded";
               throw new IllegalStateException(var27.toString());
            }

            if (this.canceled) {
               this.foldedSink = sink;
               throw new IOException("canceled");
            }

            closed = this.sinkClosed;
            if (this.buffer.exhausted()) {
               this.sourceClosed = true;
               this.foldedSink = sink;
               done = true;
            } else {
               sinkBuffer = new Buffer();
               ((Buffer)sinkBuffer).write(this.buffer, this.buffer.size());
               this.condition.signalAll();
            }

            Unit var26 = Unit.INSTANCE;
         } finally {
            var5.unlock();
         }

         if (done) {
            if (closed) {
               sink.close();
            }

            return;
         }

         boolean success = false;
         boolean var16 = false;

         try {
            var16 = true;
            Object var10001 = sinkBuffer;
            if (sinkBuffer == null) {
               Intrinsics.throwUninitializedPropertyAccessException("sinkBuffer");
               var10001 = null;
            }

            sink.write((Buffer)var10001, ((Buffer)sinkBuffer).size());
            sink.flush();
            success = true;
            var16 = false;
         } finally {
            if (var16) {
               Lock var28 = (Lock)this.lock;
               var28.lock();

               try {
                  int var8 = 0;
                  this.sourceClosed = true;
                  this.condition.signalAll();
                  Unit var29 = Unit.INSTANCE;
               } finally {
                  var28.unlock();
               }
            }
         }
      }
   }

   private final void forward(Sink $this$forward, Function1<? super Sink, Unit> block) {
      int $i$f$forward = 0;
      Timeout this_$iv = $this$forward.timeout();
      Timeout other$iv = this.sink().timeout();
      int $i$f$intersectWith = 0;
      long originalTimeout$iv = this_$iv.timeoutNanos();
      this_$iv.timeout(Timeout.Companion.minTimeout(other$iv.timeoutNanos(), this_$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
      if (this_$iv.hasDeadline()) {
         long originalDeadline$iv = this_$iv.deadlineNanoTime();
         if (other$iv.hasDeadline()) {
            this_$iv.deadlineNanoTime(Math.min(this_$iv.deadlineNanoTime(), other$iv.deadlineNanoTime()));
         }

         try {
            int var11 = 0;
            block.invoke($this$forward);
            Unit var22 = Unit.INSTANCE;
         } finally {
            InlineMarker.finallyStart(1);
            this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
            if (other$iv.hasDeadline()) {
               this_$iv.deadlineNanoTime(originalDeadline$iv);
            }

            InlineMarker.finallyEnd(1);
         }
      } else {
         if (other$iv.hasDeadline()) {
            this_$iv.deadlineNanoTime(other$iv.deadlineNanoTime());
         }

         try {
            int var13 = 0;
            block.invoke($this$forward);
            Unit var14 = Unit.INSTANCE;
         } finally {
            InlineMarker.finallyStart(1);
            this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
            if (other$iv.hasDeadline()) {
               this_$iv.clearDeadline();
            }

            InlineMarker.finallyEnd(1);
         }
      }

   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "sink",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_sink"
   )
   @NotNull
   public final Sink _deprecated_sink/* $FF was: -deprecated_sink*/() {
      return this.sink;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "source",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_source"
   )
   @NotNull
   public final Source _deprecated_source/* $FF was: -deprecated_source*/() {
      return this.source;
   }

   public final void cancel() {
      Lock var1 = (Lock)this.lock;
      var1.lock();

      try {
         int var2 = 0;
         this.canceled = true;
         this.buffer.clear();
         this.condition.signalAll();
         Unit var6 = Unit.INSTANCE;
      } finally {
         var1.unlock();
      }

   }
}
