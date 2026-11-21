package okio.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.AsyncTimeout;
import okio.Buffer;
import okio.Segment;
import okio.SegmentPool;
import okio.Sink;
import okio.Socket;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001:\u0002\u0016\u0017B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0015H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u000bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u000e\u001a\u00020\u000fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0018"},
   d2 = {"Lokio/internal/DefaultSocket;", "Lokio/Socket;", "socket", "Ljava/net/Socket;", "<init>", "(Ljava/net/Socket;)V", "getSocket", "()Ljava/net/Socket;", "closeBits", "Ljava/util/concurrent/atomic/AtomicInteger;", "source", "Lokio/Source;", "getSource", "()Lokio/Source;", "sink", "Lokio/Sink;", "getSink", "()Lokio/Sink;", "cancel", "", "toString", "", "SocketSink", "SocketSource", "okio"}
)
public final class DefaultSocket implements Socket {
   @NotNull
   private final java.net.Socket socket;
   @NotNull
   private AtomicInteger closeBits;
   @NotNull
   private final Source source;
   @NotNull
   private final Sink sink;

   public DefaultSocket(@NotNull java.net.Socket socket) {
      Intrinsics.checkNotNullParameter(socket, "socket");
      super();
      this.socket = socket;
      this.closeBits = new AtomicInteger();
      this.source = new SocketSource();
      this.sink = new SocketSink();
   }

   @NotNull
   public final java.net.Socket getSocket() {
      return this.socket;
   }

   @NotNull
   public Source getSource() {
      return this.source;
   }

   @NotNull
   public Sink getSink() {
      return this.sink;
   }

   public void cancel() {
      this.socket.close();
   }

   @NotNull
   public String toString() {
      String var10000 = this.socket.toString();
      Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
      return var10000;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\nH\u0016J\b\u0010\u0010\u001a\u00020\nH\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
      d2 = {"Lokio/internal/DefaultSocket$SocketSink;", "Lokio/Sink;", "<init>", "(Lokio/internal/DefaultSocket;)V", "outputStream", "Ljava/io/OutputStream;", "kotlin.jvm.PlatformType", "timeout", "Lokio/internal/SocketAsyncTimeout;", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "flush", "close", "toString", "", "okio"}
   )
   @SourceDebugExtension({"SMAP\nDefaultSocket.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultSocket.kt\nokio/internal/DefaultSocket$SocketSink\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 AsyncTimeout.kt\nokio/AsyncTimeout\n*L\n1#1,176:1\n85#2:177\n195#3,11:178\n195#3,11:189\n195#3,11:200\n*S KotlinDebug\n*F\n+ 1 DefaultSocket.kt\nokio/internal/DefaultSocket$SocketSink\n*L\n60#1:177\n61#1:178,11\n77#1:189,11\n83#1:200,11\n*E\n"})
   public final class SocketSink implements Sink {
      private final OutputStream outputStream = DefaultSocket.this.getSocket().getOutputStream();
      @NotNull
      private final SocketAsyncTimeout timeout = new SocketAsyncTimeout(DefaultSocket.this.getSocket());

      public void write(@NotNull Buffer source, long byteCount) {
         Intrinsics.checkNotNullParameter(source, "source");
         okio.-SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
         long remaining = byteCount;

         while(remaining > 0L) {
            this.timeout.throwIfReached();
            Segment var10000 = source.head;
            Intrinsics.checkNotNull(var10000);
            Segment head = var10000;
            int b$iv = head.limit - head.pos;
            int $i$f$minOf = 0;
            int toCopy = (int)Math.min(remaining, (long)b$iv);
            AsyncTimeout this_$iv = this.timeout;
            int $i$f$withTimeout = 0;
            b$iv = 0;
            this_$iv.enter();
            boolean var16 = false;

            try {
               var16 = true;
               $i$f$minOf = 0;
               this.outputStream.write(head.data, head.pos, toCopy);
               Object result$iv = Unit.INSTANCE;
               b$iv = 1;
               var16 = false;
            } catch (IOException e$iv) {
               throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
            } finally {
               if (var16) {
                  $i$f$minOf = this_$iv.exit();
                  if ($i$f$minOf && b$iv) {
                     throw this_$iv.access$newTimeoutException((IOException)null);
                  }

               }
            }

            $i$f$minOf = this_$iv.exit();
            if ($i$f$minOf) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            }

            head.pos += toCopy;
            remaining -= (long)toCopy;
            source.setSize$okio(source.size() - (long)toCopy);
            if (head.pos == head.limit) {
               source.head = head.pop();
               SegmentPool.recycle(head);
            }
         }

      }

      public void flush() {
         AsyncTimeout this_$iv = this.timeout;
         int $i$f$withTimeout = 0;
         boolean throwOnTimeout$iv = false;
         this_$iv.enter();
         boolean var9 = false;

         try {
            var9 = true;
            int var4 = false;
            this.outputStream.flush();
            Object result$iv = Unit.INSTANCE;
            throwOnTimeout$iv = true;
            var9 = false;
         } catch (IOException e$iv) {
            throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
         } finally {
            if (var9) {
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
         AsyncTimeout this_$iv = this.timeout;
         DefaultSocket var2 = DefaultSocket.this;
         int $i$f$withTimeout = 0;
         boolean throwOnTimeout$iv = false;
         this_$iv.enter();
         boolean var11 = false;

         label142: {
            label143: {
               try {
                  label134: {
                     var11 = true;
                     int var5 = false;
                     switch (_AtomicKt.setBitsOrZero(var2.closeBits, 1)) {
                        case 0:
                           var11 = false;
                           break label142;
                        case 1:
                        case 2:
                        default:
                           if (var2.getSocket().isClosed()) {
                              var11 = false;
                              break label134;
                           }

                           if (var2.getSocket().isOutputShutdown()) {
                              var11 = false;
                              break label134;
                           }

                           this.outputStream.flush();

                           try {
                              var2.getSocket().shutdownOutput();
                           } catch (UnsupportedOperationException var12) {
                              this.outputStream.close();
                           }
                           break;
                        case 3:
                           var2.getSocket().close();
                     }

                     Object result$iv = Unit.INSTANCE;
                     throwOnTimeout$iv = true;
                     var11 = false;
                     break label143;
                  }
               } catch (IOException e$iv) {
                  throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
               } finally {
                  if (var11) {
                     boolean timedOut$iv = this_$iv.exit();
                     if (timedOut$iv && throwOnTimeout$iv) {
                        throw this_$iv.access$newTimeoutException((IOException)null);
                     }

                  }
               }

               if (this_$iv.exit()) {
               }

               return;
            }

            boolean timedOut$iv = this_$iv.exit();
            if (timedOut$iv) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            }

            return;
         }

         if (this_$iv.exit()) {
         }

      }

      @NotNull
      public SocketAsyncTimeout timeout() {
         return this.timeout;
      }

      @NotNull
      public String toString() {
         return "sink(" + DefaultSocket.this.getSocket() + ')';
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\nH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lokio/internal/DefaultSocket$SocketSource;", "Lokio/Source;", "<init>", "(Lokio/internal/DefaultSocket;)V", "inputStream", "Ljava/io/InputStream;", "kotlin.jvm.PlatformType", "timeout", "Lokio/internal/SocketAsyncTimeout;", "read", "", "sink", "Lokio/Buffer;", "byteCount", "close", "", "toString", "", "okio"}
   )
   @SourceDebugExtension({"SMAP\nDefaultSocket.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultSocket.kt\nokio/internal/DefaultSocket$SocketSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n+ 4 AsyncTimeout.kt\nokio/AsyncTimeout\n*L\n1#1,176:1\n1#2:177\n85#3:178\n195#4,11:179\n195#4,11:190\n*S KotlinDebug\n*F\n+ 1 DefaultSocket.kt\nokio/internal/DefaultSocket$SocketSource\n*L\n121#1:178\n123#1:179,11\n144#1:190,11\n*E\n"})
   public final class SocketSource implements Source {
      private final InputStream inputStream = DefaultSocket.this.getSocket().getInputStream();
      @NotNull
      private final SocketAsyncTimeout timeout = new SocketAsyncTimeout(DefaultSocket.this.getSocket());

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (byteCount == 0L) {
            return 0L;
         } else if (byteCount < 0L) {
            int maxToCopy = 0;
            String var21 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var21.toString());
         } else {
            this.timeout.throwIfReached();
            Segment tail = sink.writableSegment$okio(1);
            int b$iv = 8192 - tail.limit;
            int $i$f$minOf = 0;
            int maxToCopy = (int)Math.min(byteCount, (long)b$iv);

            int bytesRead;
            try {
               AsyncTimeout this_$iv = this.timeout;
               b$iv = 0;
               $i$f$minOf = 0;
               this_$iv.enter();
               boolean var16 = false;

               int var12;
               try {
                  var16 = true;
                  int var10 = false;
                  int result$iv = this.inputStream.read(tail.data, tail.limit, maxToCopy);
                  $i$f$minOf = 1;
                  var12 = result$iv;
                  var16 = false;
               } catch (IOException e$iv) {
                  throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
               } finally {
                  if (var16) {
                     boolean timedOut$iv = this_$iv.exit();
                     if (timedOut$iv && $i$f$minOf) {
                        throw this_$iv.access$newTimeoutException((IOException)null);
                     }

                  }
               }

               boolean timedOut$iv = this_$iv.exit();
               if (timedOut$iv) {
                  throw this_$iv.access$newTimeoutException((IOException)null);
               }

               bytesRead = var12;
            } catch (AssertionError e) {
               if (_JavaIoKt.isAndroidGetsocknameError(e)) {
                  throw new IOException((Throwable)e);
               }

               throw e;
            }

            if (bytesRead == -1) {
               if (tail.pos == tail.limit) {
                  sink.head = tail.pop();
                  SegmentPool.recycle(tail);
               }

               return -1L;
            } else {
               tail.limit += bytesRead;
               sink.setSize$okio(sink.size() + (long)bytesRead);
               return (long)bytesRead;
            }
         }
      }

      public void close() {
         AsyncTimeout this_$iv = this.timeout;
         DefaultSocket var2 = DefaultSocket.this;
         int $i$f$withTimeout = 0;
         boolean throwOnTimeout$iv = false;
         this_$iv.enter();
         boolean var11 = false;

         label138: {
            label139: {
               try {
                  label130: {
                     var11 = true;
                     int var5 = false;
                     switch (_AtomicKt.setBitsOrZero(var2.closeBits, 2)) {
                        case 0:
                           var11 = false;
                           break label138;
                        case 1:
                        case 2:
                        default:
                           if (var2.getSocket().isClosed()) {
                              var11 = false;
                              break label130;
                           }

                           if (var2.getSocket().isInputShutdown()) {
                              var11 = false;
                              break label130;
                           }

                           try {
                              var2.getSocket().shutdownInput();
                           } catch (UnsupportedOperationException var12) {
                              this.inputStream.close();
                           }
                           break;
                        case 3:
                           var2.getSocket().close();
                     }

                     Object result$iv = Unit.INSTANCE;
                     throwOnTimeout$iv = true;
                     var11 = false;
                     break label139;
                  }
               } catch (IOException e$iv) {
                  throw !this_$iv.exit() ? (Throwable)e$iv : (Throwable)this_$iv.access$newTimeoutException(e$iv);
               } finally {
                  if (var11) {
                     boolean timedOut$iv = this_$iv.exit();
                     if (timedOut$iv && throwOnTimeout$iv) {
                        throw this_$iv.access$newTimeoutException((IOException)null);
                     }

                  }
               }

               if (this_$iv.exit()) {
               }

               return;
            }

            boolean timedOut$iv = this_$iv.exit();
            if (timedOut$iv) {
               throw this_$iv.access$newTimeoutException((IOException)null);
            }

            return;
         }

         if (this_$iv.exit()) {
         }

      }

      @NotNull
      public SocketAsyncTimeout timeout() {
         return this.timeout;
      }

      @NotNull
      public String toString() {
         return "source(" + DefaultSocket.this.getSocket() + ')';
      }
   }
}
