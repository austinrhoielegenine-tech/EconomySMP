package okio;

import java.io.Closeable;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

// $FF: synthetic class
@Metadata(
   mv = {2, 2, 0},
   k = 5,
   xi = 48,
   d1 = {"\u0000(\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0000\u001a\u00020\u0003*\u00020\u0004\u001a\r\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0002\b\u0006\u001aN\u0010\u0007\u001a\u0002H\b\"\u0010\b\u0000\u0010\t*\n\u0018\u00010\nj\u0004\u0018\u0001`\u000b\"\u0004\b\u0001\u0010\b*\u0002H\t2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u0002H\t\u0012\u0004\u0012\u0002H\b0\rH\u0086\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\u000e\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u000f"},
   d2 = {"buffer", "Lokio/BufferedSource;", "Lokio/Source;", "Lokio/BufferedSink;", "Lokio/Sink;", "blackholeSink", "blackhole", "use", "R", "T", "Ljava/io/Closeable;", "Lokio/Closeable;", "block", "Lkotlin/Function1;", "(Ljava/io/Closeable;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "okio"},
   xs = "okio/Okio"
)
final class Okio__OkioKt {
   @NotNull
   public static final BufferedSource buffer(@NotNull Source $this$buffer) {
      Intrinsics.checkNotNullParameter($this$buffer, "<this>");
      return new RealBufferedSource($this$buffer);
   }

   @NotNull
   public static final BufferedSink buffer(@NotNull Sink $this$buffer) {
      Intrinsics.checkNotNullParameter($this$buffer, "<this>");
      return new RealBufferedSink($this$buffer);
   }

   @JvmName(
      name = "blackhole"
   )
   @NotNull
   public static final Sink blackhole() {
      return new BlackholeSink();
   }

   public static final <T extends Closeable, R> R use(T $this$use, @NotNull Function1<? super T, ? extends R> block) {
      Intrinsics.checkNotNullParameter(block, "block");
      int $i$f$use = 0;
      Throwable thrown = null;
      boolean var14 = false;

      Object result;
      label140: {
         label141: {
            try {
               var14 = true;
               result = block.invoke($this$use);
               var14 = false;
               break label141;
            } catch (Throwable t) {
               thrown = t;
               result = null;
               var14 = false;
            } finally {
               if (var14) {
                  InlineMarker.finallyStart(1);

                  try {
                     if ($this$use != null) {
                        $this$use.close();
                     }
                  } catch (Throwable t) {
                     if (thrown != null) {
                        ExceptionsKt.addSuppressed(thrown, t);
                     }
                  }

                  InlineMarker.finallyEnd(1);
               }
            }

            InlineMarker.finallyStart(1);

            try {
               if ($this$use != null) {
                  $this$use.close();
               }
            } catch (Throwable t) {
               if (thrown == null) {
                  thrown = t;
               } else {
                  ExceptionsKt.addSuppressed(thrown, t);
               }
            }

            InlineMarker.finallyEnd(1);
            break label140;
         }

         InlineMarker.finallyStart(1);

         try {
            if ($this$use != null) {
               $this$use.close();
            }
         } catch (Throwable t) {
            thrown = t;
         }

         InlineMarker.finallyEnd(1);
      }

      if (thrown != null) {
         throw thrown;
      } else {
         return (R)result;
      }
   }
}
