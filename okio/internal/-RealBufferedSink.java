package okio.internal;

import java.io.EOFException;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.RealBufferedSink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000H\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001d\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010\u0000\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\b\u001a\u00020\tH\u0080\b\u001a%\u0010\u0000\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\f\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u0080\b\u001a%\u0010\f\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u0011\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u0000\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0013H\u0080\b\u001a%\u0010\u0000\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u0014\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0015H\u0080\b\u001a\u001d\u0010\u0000\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00152\u0006\u0010\u0005\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010\u0016\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u0018\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u001a\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0019\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u001b\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u001d\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u000bH\u0080\b\u001a\u0015\u0010\u001e\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010 \u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010!\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u0006H\u0080\b\u001a\u0015\u0010\"\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u001f\u001a\u00020\u0006H\u0080\b\u001a\r\u0010#\u001a\u00020\u0007*\u00020\u0002H\u0080\b\u001a\r\u0010$\u001a\u00020\u0007*\u00020\u0002H\u0080\b\u001a\r\u0010%\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010&\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010'\u001a\u00020(*\u00020\u0002H\u0080\b\u001a\r\u0010)\u001a\u00020\u000e*\u00020\u0002H\u0080\b¨\u0006*"},
   d2 = {"commonWrite", "", "Lokio/RealBufferedSink;", "source", "Lokio/Buffer;", "byteCount", "", "Lokio/BufferedSink;", "byteString", "Lokio/ByteString;", "offset", "", "commonWriteUtf8", "string", "", "beginIndex", "endIndex", "commonWriteUtf8CodePoint", "codePoint", "", "commonWriteAll", "Lokio/Source;", "commonWriteByte", "b", "commonWriteShort", "s", "commonWriteShortLe", "commonWriteInt", "i", "commonWriteIntLe", "commonWriteLong", "v", "commonWriteLongLe", "commonWriteDecimalLong", "commonWriteHexadecimalUnsignedLong", "commonEmitCompleteSegments", "commonEmit", "commonFlush", "commonClose", "commonTimeout", "Lokio/Timeout;", "commonToString", "okio"}
)
@JvmName(
   name = "-RealBufferedSink"
)
@SourceDebugExtension({"SMAP\nRealBufferedSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSink.kt\nokio/RealBufferedSink\n*L\n1#1,219:1\n1#2:220\n51#3:221\n51#3:222\n51#3:223\n51#3:224\n51#3:225\n51#3:226\n51#3:227\n51#3:228\n51#3:229\n51#3:230\n51#3:231\n51#3:232\n51#3:233\n51#3:234\n51#3:235\n51#3:236\n51#3:237\n51#3:238\n51#3:239\n51#3:240\n51#3:241\n51#3:242\n51#3:243\n51#3:244\n51#3:245\n51#3:246\n51#3:247\n*S KotlinDebug\n*F\n+ 1 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n*L\n35#1:221\n41#1:222\n51#1:223\n57#1:224\n67#1:225\n73#1:226\n79#1:227\n89#1:228\n96#1:229\n107#1:230\n117#1:231\n123#1:232\n129#1:233\n135#1:234\n141#1:235\n147#1:236\n153#1:237\n159#1:238\n165#1:239\n171#1:240\n172#1:241\n178#1:242\n179#1:243\n185#1:244\n186#1:245\n198#1:246\n199#1:247\n*E\n"})
public final class -RealBufferedSink {
   public static final void commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull Buffer source, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      if ($this$commonWrite.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWrite.bufferField.write(source, byteCount);
         $this$commonWrite.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull ByteString byteString) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int $i$f$commonWrite = 0;
      if ($this$commonWrite.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWrite.bufferField.write(byteString);
         return $this$commonWrite.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull ByteString byteString, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int $i$f$commonWrite = 0;
      if ($this$commonWrite.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWrite.bufferField.write(byteString, offset, byteCount);
         return $this$commonWrite.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteUtf8(@NotNull RealBufferedSink $this$commonWriteUtf8, @NotNull String string) {
      Intrinsics.checkNotNullParameter($this$commonWriteUtf8, "<this>");
      Intrinsics.checkNotNullParameter(string, "string");
      int $i$f$commonWriteUtf8 = 0;
      if ($this$commonWriteUtf8.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteUtf8.bufferField.writeUtf8(string);
         return $this$commonWriteUtf8.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteUtf8(@NotNull RealBufferedSink $this$commonWriteUtf8, @NotNull String string, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$commonWriteUtf8, "<this>");
      Intrinsics.checkNotNullParameter(string, "string");
      int $i$f$commonWriteUtf8 = 0;
      if ($this$commonWriteUtf8.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteUtf8.bufferField.writeUtf8(string, beginIndex, endIndex);
         return $this$commonWriteUtf8.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteUtf8CodePoint(@NotNull RealBufferedSink $this$commonWriteUtf8CodePoint, int codePoint) {
      Intrinsics.checkNotNullParameter($this$commonWriteUtf8CodePoint, "<this>");
      int $i$f$commonWriteUtf8CodePoint = 0;
      if ($this$commonWriteUtf8CodePoint.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteUtf8CodePoint.bufferField.writeUtf8CodePoint(codePoint);
         return $this$commonWriteUtf8CodePoint.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull byte[] source) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      if ($this$commonWrite.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWrite.bufferField.write(source);
         return $this$commonWrite.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull byte[] source, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      if ($this$commonWrite.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWrite.bufferField.write(source, offset, byteCount);
         return $this$commonWrite.emitCompleteSegments();
      }
   }

   public static final long commonWriteAll(@NotNull RealBufferedSink $this$commonWriteAll, @NotNull Source source) {
      Intrinsics.checkNotNullParameter($this$commonWriteAll, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWriteAll = 0;
      long totalBytesRead = 0L;

      while(true) {
         int $i$f$getBuffer = 0;
         long readCount = source.read($this$commonWriteAll.bufferField, 8192L);
         if (readCount == -1L) {
            return totalBytesRead;
         }

         totalBytesRead += readCount;
         $this$commonWriteAll.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWrite(@NotNull RealBufferedSink $this$commonWrite, @NotNull Source source, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      long byteCount = byteCount;

      while(byteCount > 0L) {
         int $i$f$getBuffer = 0;
         long read = source.read($this$commonWrite.bufferField, byteCount);
         if (read == -1L) {
            throw new EOFException();
         }

         byteCount -= read;
         $this$commonWrite.emitCompleteSegments();
      }

      return $this$commonWrite;
   }

   @NotNull
   public static final BufferedSink commonWriteByte(@NotNull RealBufferedSink $this$commonWriteByte, int b) {
      Intrinsics.checkNotNullParameter($this$commonWriteByte, "<this>");
      int $i$f$commonWriteByte = 0;
      if ($this$commonWriteByte.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteByte.bufferField.writeByte(b);
         return $this$commonWriteByte.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteShort(@NotNull RealBufferedSink $this$commonWriteShort, int s) {
      Intrinsics.checkNotNullParameter($this$commonWriteShort, "<this>");
      int $i$f$commonWriteShort = 0;
      if ($this$commonWriteShort.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteShort.bufferField.writeShort(s);
         return $this$commonWriteShort.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteShortLe(@NotNull RealBufferedSink $this$commonWriteShortLe, int s) {
      Intrinsics.checkNotNullParameter($this$commonWriteShortLe, "<this>");
      int $i$f$commonWriteShortLe = 0;
      if ($this$commonWriteShortLe.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteShortLe.bufferField.writeShortLe(s);
         return $this$commonWriteShortLe.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteInt(@NotNull RealBufferedSink $this$commonWriteInt, int i) {
      Intrinsics.checkNotNullParameter($this$commonWriteInt, "<this>");
      int $i$f$commonWriteInt = 0;
      if ($this$commonWriteInt.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteInt.bufferField.writeInt(i);
         return $this$commonWriteInt.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteIntLe(@NotNull RealBufferedSink $this$commonWriteIntLe, int i) {
      Intrinsics.checkNotNullParameter($this$commonWriteIntLe, "<this>");
      int $i$f$commonWriteIntLe = 0;
      if ($this$commonWriteIntLe.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteIntLe.bufferField.writeIntLe(i);
         return $this$commonWriteIntLe.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteLong(@NotNull RealBufferedSink $this$commonWriteLong, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteLong, "<this>");
      int $i$f$commonWriteLong = 0;
      if ($this$commonWriteLong.closed) {
         int $i$f$getBuffer = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteLong.bufferField.writeLong(v);
         return $this$commonWriteLong.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteLongLe(@NotNull RealBufferedSink $this$commonWriteLongLe, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteLongLe, "<this>");
      int $i$f$commonWriteLongLe = 0;
      if ($this$commonWriteLongLe.closed) {
         int $i$f$getBuffer = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteLongLe.bufferField.writeLongLe(v);
         return $this$commonWriteLongLe.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteDecimalLong(@NotNull RealBufferedSink $this$commonWriteDecimalLong, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteDecimalLong, "<this>");
      int $i$f$commonWriteDecimalLong = 0;
      if ($this$commonWriteDecimalLong.closed) {
         int $i$f$getBuffer = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteDecimalLong.bufferField.writeDecimalLong(v);
         return $this$commonWriteDecimalLong.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonWriteHexadecimalUnsignedLong(@NotNull RealBufferedSink $this$commonWriteHexadecimalUnsignedLong, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteHexadecimalUnsignedLong, "<this>");
      int $i$f$commonWriteHexadecimalUnsignedLong = 0;
      if ($this$commonWriteHexadecimalUnsignedLong.closed) {
         int $i$f$getBuffer = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int $i$f$getBuffer = 0;
         $this$commonWriteHexadecimalUnsignedLong.bufferField.writeHexadecimalUnsignedLong(v);
         return $this$commonWriteHexadecimalUnsignedLong.emitCompleteSegments();
      }
   }

   @NotNull
   public static final BufferedSink commonEmitCompleteSegments(@NotNull RealBufferedSink $this$commonEmitCompleteSegments) {
      Intrinsics.checkNotNullParameter($this$commonEmitCompleteSegments, "<this>");
      int $i$f$commonEmitCompleteSegments = 0;
      if ($this$commonEmitCompleteSegments.closed) {
         int var3 = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         long byteCount = $this$commonEmitCompleteSegments.bufferField.completeSegmentByteCount();
         if (byteCount > 0L) {
            $i$f$getBuffer = 0;
            $this$commonEmitCompleteSegments.sink.write($this$commonEmitCompleteSegments.bufferField, byteCount);
         }

         return $this$commonEmitCompleteSegments;
      }
   }

   @NotNull
   public static final BufferedSink commonEmit(@NotNull RealBufferedSink $this$commonEmit) {
      Intrinsics.checkNotNullParameter($this$commonEmit, "<this>");
      int $i$f$commonEmit = 0;
      if ($this$commonEmit.closed) {
         int var3 = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         long byteCount = $this$commonEmit.bufferField.size();
         if (byteCount > 0L) {
            $i$f$getBuffer = 0;
            $this$commonEmit.sink.write($this$commonEmit.bufferField, byteCount);
         }

         return $this$commonEmit;
      }
   }

   public static final void commonFlush(@NotNull RealBufferedSink $this$commonFlush) {
      Intrinsics.checkNotNullParameter($this$commonFlush, "<this>");
      int $i$f$commonFlush = 0;
      if ($this$commonFlush.closed) {
         int $i$f$getBuffer = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int $i$f$getBuffer = 0;
         if ($this$commonFlush.bufferField.size() > 0L) {
            $i$f$getBuffer = 0;
            $i$f$getBuffer = 0;
            $this$commonFlush.sink.write($this$commonFlush.bufferField, $this$commonFlush.bufferField.size());
         }

         $this$commonFlush.sink.flush();
      }
   }

   public static final void commonClose(@NotNull RealBufferedSink $this$commonClose) {
      Intrinsics.checkNotNullParameter($this$commonClose, "<this>");
      int $i$f$commonClose = 0;
      if (!$this$commonClose.closed) {
         Throwable thrown = null;

         try {
            int $i$f$getBuffer = 0;
            if ($this$commonClose.bufferField.size() > 0L) {
               $i$f$getBuffer = 0;
               $i$f$getBuffer = 0;
               $this$commonClose.sink.write($this$commonClose.bufferField, $this$commonClose.bufferField.size());
            }
         } catch (Throwable e) {
            thrown = e;
         }

         try {
            $this$commonClose.sink.close();
         } catch (Throwable e) {
            if (thrown == null) {
               thrown = e;
            }
         }

         $this$commonClose.closed = true;
         if (thrown != null) {
            throw thrown;
         }
      }
   }

   @NotNull
   public static final Timeout commonTimeout(@NotNull RealBufferedSink $this$commonTimeout) {
      Intrinsics.checkNotNullParameter($this$commonTimeout, "<this>");
      int $i$f$commonTimeout = 0;
      return $this$commonTimeout.sink.timeout();
   }

   @NotNull
   public static final String commonToString(@NotNull RealBufferedSink $this$commonToString) {
      Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
      int $i$f$commonToString = 0;
      return "buffer(" + $this$commonToString.sink + ')';
   }
}
