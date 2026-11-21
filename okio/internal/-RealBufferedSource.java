package okio.internal;

import java.io.EOFException;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.CharsKt;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Options;
import okio.PeekSource;
import okio.RealBufferedSource;
import okio.Sink;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000h\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\n\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001d\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\r\u0010\u0006\u001a\u00020\u0007*\u00020\u0002H\u0080\b\u001a\u0015\u0010\b\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\u0015\u0010\n\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\r\u0010\u000b\u001a\u00020\f*\u00020\u0002H\u0080\b\u001a\r\u0010\r\u001a\u00020\u000e*\u00020\u0002H\u0080\b\u001a\u0015\u0010\r\u001a\u00020\u000e*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\u0015\u0010\u000f\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u0012H\u0080\b\u001a\r\u0010\u0013\u001a\u00020\u0014*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u0013\u001a\u00020\u0014*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\u0015\u0010\u0015\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0014H\u0080\b\u001a%\u0010\u0000\u001a\u00020\u0010*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u0010H\u0080\b\u001a\u001d\u0010\u0015\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\u0015\u0010\u0017\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0018H\u0080\b\u001a\r\u0010\u0019\u001a\u00020\u001a*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u0019\u001a\u00020\u001a*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a\u000f\u0010\u001b\u001a\u0004\u0018\u00010\u001a*\u00020\u0002H\u0080\b\u001a\u0015\u0010\u001c\u001a\u00020\u001a*\u00020\u00022\u0006\u0010\u001d\u001a\u00020\u0001H\u0080\b\u001a\r\u0010\u001e\u001a\u00020\u0010*\u00020\u0002H\u0080\b\u001a\r\u0010\u001f\u001a\u00020 *\u00020\u0002H\u0080\b\u001a\r\u0010!\u001a\u00020 *\u00020\u0002H\u0080\b\u001a\r\u0010\"\u001a\u00020\u0010*\u00020\u0002H\u0080\b\u001a\r\u0010#\u001a\u00020\u0010*\u00020\u0002H\u0080\b\u001a\r\u0010$\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010%\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010&\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\r\u0010'\u001a\u00020\u0001*\u00020\u0002H\u0080\b\u001a\u0015\u0010(\u001a\u00020\t*\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0001H\u0080\b\u001a%\u0010)\u001a\u00020\u0001*\u00020\u00022\u0006\u0010*\u001a\u00020\f2\u0006\u0010+\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\u0001H\u0080\b\u001a:\u0010)\u001a\u00020\u0001*\u00020\u00022\u0006\u0010-\u001a\u00020\u000e2\b\b\u0002\u0010.\u001a\u00020\u00102\b\b\u0002\u0010\u0005\u001a\u00020\u00102\u0006\u0010+\u001a\u00020\u00012\b\b\u0002\u0010,\u001a\u00020\u0001H\u0000\u001a4\u0010/\u001a\u00020\u0007*\u00020\u00042\u0006\u0010-\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u00102\u0006\u0010+\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\u0001H\u0002\u001a\u001d\u00100\u001a\u00020\u0001*\u00020\u00022\u0006\u00101\u001a\u00020\u000e2\u0006\u0010+\u001a\u00020\u0001H\u0080\b\u001a-\u00102\u001a\u00020\u0007*\u00020\u00022\u0006\u0010\u0016\u001a\u00020\u00012\u0006\u0010-\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\u00102\u0006\u0010\u0005\u001a\u00020\u0010H\u0080\b\u001a\r\u00103\u001a\u000204*\u00020\u0002H\u0080\b\u001a\r\u00105\u001a\u00020\t*\u00020\u0002H\u0080\b\u001a\r\u00106\u001a\u000207*\u00020\u0002H\u0080\b\u001a\r\u00108\u001a\u00020\u001a*\u00020\u0002H\u0080\b¨\u00069"},
   d2 = {"commonRead", "", "Lokio/RealBufferedSource;", "sink", "Lokio/Buffer;", "byteCount", "commonExhausted", "", "commonRequire", "", "commonRequest", "commonReadByte", "", "commonReadByteString", "Lokio/ByteString;", "commonSelect", "", "options", "Lokio/Options;", "commonReadByteArray", "", "commonReadFully", "offset", "commonReadAll", "Lokio/Sink;", "commonReadUtf8", "", "commonReadUtf8Line", "commonReadUtf8LineStrict", "limit", "commonReadUtf8CodePoint", "commonReadShort", "", "commonReadShortLe", "commonReadInt", "commonReadIntLe", "commonReadLong", "commonReadLongLe", "commonReadDecimalLong", "commonReadHexadecimalUnsignedLong", "commonSkip", "commonIndexOf", "b", "fromIndex", "toIndex", "bytes", "bytesOffset", "isMatchPossibleByExpandingBuffer", "commonIndexOfElement", "targetBytes", "commonRangeEquals", "commonPeek", "Lokio/BufferedSource;", "commonClose", "commonTimeout", "Lokio/Timeout;", "commonToString", "okio"}
)
@JvmName(
   name = "-RealBufferedSource"
)
@SourceDebugExtension({"SMAP\nRealBufferedSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,472:1\n1#2:473\n63#3:474\n63#3:475\n63#3:476\n63#3:477\n63#3:478\n63#3:479\n63#3:480\n63#3:481\n63#3:482\n63#3:483\n63#3:484\n63#3:485\n63#3:486\n63#3:487\n63#3:488\n63#3:489\n63#3:490\n63#3:491\n63#3:492\n63#3:493\n63#3:494\n63#3:495\n63#3:496\n63#3:498\n63#3:499\n63#3:500\n63#3:501\n63#3:502\n63#3:503\n63#3:504\n63#3:505\n63#3:506\n63#3:507\n63#3:508\n63#3:509\n63#3:510\n63#3:511\n63#3:512\n63#3:513\n63#3:514\n63#3:515\n63#3:516\n63#3:517\n63#3:519\n63#3:520\n63#3:521\n63#3:522\n63#3:523\n63#3:524\n63#3:525\n63#3:526\n63#3:527\n63#3:528\n63#3:529\n63#3:530\n63#3:531\n63#3:532\n63#3:533\n63#3:534\n63#3:535\n63#3:536\n63#3:537\n63#3:538\n63#3:539\n63#3:540\n63#3:541\n63#3:543\n63#3:544\n63#3:545\n63#3:546\n88#4:497\n88#4:518\n88#4:542\n*S KotlinDebug\n*F\n+ 1 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n*L\n42#1:474\n44#1:475\n48#1:476\n49#1:477\n54#1:478\n64#1:479\n65#1:480\n72#1:481\n76#1:482\n77#1:483\n82#1:484\n89#1:485\n96#1:486\n101#1:487\n109#1:488\n110#1:489\n115#1:490\n124#1:491\n125#1:492\n132#1:493\n138#1:494\n140#1:495\n144#1:496\n145#1:498\n153#1:499\n157#1:500\n162#1:501\n163#1:502\n166#1:503\n169#1:504\n170#1:505\n171#1:506\n177#1:507\n178#1:508\n183#1:509\n190#1:510\n191#1:511\n196#1:512\n204#1:513\n206#1:514\n207#1:515\n209#1:516\n212#1:517\n214#1:519\n222#1:520\n229#1:521\n234#1:522\n239#1:523\n244#1:524\n249#1:525\n254#1:526\n259#1:527\n267#1:528\n278#1:529\n286#1:530\n300#1:531\n307#1:532\n310#1:533\n311#1:534\n322#1:535\n327#1:536\n328#1:537\n349#1:538\n358#1:539\n362#1:540\n372#1:541\n425#1:543\n428#1:544\n429#1:545\n466#1:546\n144#1:497\n212#1:518\n406#1:542\n*E\n"})
public final class -RealBufferedSource {
   public static final long commonRead(@NotNull RealBufferedSource $this$commonRead, @NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      if (byteCount < 0L) {
         int var12 = 0;
         String var13 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var13.toString());
      } else if ($this$commonRead.closed) {
         int var10 = 0;
         String $i$f$getBuffer = "closed";
         throw new IllegalStateException($i$f$getBuffer.toString());
      } else {
         int $i$f$getBuffer = 0;
         if ($this$commonRead.bufferField.size() == 0L) {
            if (byteCount == 0L) {
               return 0L;
            }

            int $i$f$getBuffer = 0;
            long read = $this$commonRead.source.read($this$commonRead.bufferField, 8192L);
            if (read == -1L) {
               return -1L;
            }
         }

         int $i$f$getBuffer = 0;
         long this_$iv = $this$commonRead.bufferField.size();
         long toRead = Math.min(byteCount, this_$iv);
         $i$f$getBuffer = 0;
         return $this$commonRead.bufferField.read(sink, toRead);
      }
   }

   public static final boolean commonExhausted(@NotNull RealBufferedSource $this$commonExhausted) {
      Intrinsics.checkNotNullParameter($this$commonExhausted, "<this>");
      int $i$f$commonExhausted = 0;
      if ($this$commonExhausted.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         boolean var10000;
         if ($this$commonExhausted.bufferField.exhausted()) {
            $i$f$getBuffer = 0;
            if ($this$commonExhausted.source.read($this$commonExhausted.bufferField, 8192L) == -1L) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public static final void commonRequire(@NotNull RealBufferedSource $this$commonRequire, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRequire, "<this>");
      int $i$f$commonRequire = 0;
      if (!$this$commonRequire.request(byteCount)) {
         throw new EOFException();
      }
   }

   public static final boolean commonRequest(@NotNull RealBufferedSource $this$commonRequest, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRequest, "<this>");
      int $i$f$commonRequest = 0;
      if (byteCount < 0L) {
         int var9 = 0;
         String var10 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var10.toString());
      } else if ($this$commonRequest.closed) {
         int $i$f$getBuffer = 0;
         String $i$f$getBuffer = "closed";
         throw new IllegalStateException($i$f$getBuffer.toString());
      } else {
         do {
            int $i$f$getBuffer = 0;
            if ($this$commonRequest.bufferField.size() >= byteCount) {
               return true;
            }

            $i$f$getBuffer = 0;
         } while($this$commonRequest.source.read($this$commonRequest.bufferField, 8192L) != -1L);

         return false;
      }
   }

   public static final byte commonReadByte(@NotNull RealBufferedSource $this$commonReadByte) {
      Intrinsics.checkNotNullParameter($this$commonReadByte, "<this>");
      int $i$f$commonReadByte = 0;
      $this$commonReadByte.require(1L);
      int $i$f$getBuffer = 0;
      return $this$commonReadByte.bufferField.readByte();
   }

   @NotNull
   public static final ByteString commonReadByteString(@NotNull RealBufferedSource $this$commonReadByteString) {
      Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
      int $i$f$commonReadByteString = 0;
      int $i$f$getBuffer = 0;
      $this$commonReadByteString.bufferField.writeAll($this$commonReadByteString.source);
      $i$f$getBuffer = 0;
      return $this$commonReadByteString.bufferField.readByteString();
   }

   @NotNull
   public static final ByteString commonReadByteString(@NotNull RealBufferedSource $this$commonReadByteString, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
      int $i$f$commonReadByteString = 0;
      $this$commonReadByteString.require(byteCount);
      int $i$f$getBuffer = 0;
      return $this$commonReadByteString.bufferField.readByteString(byteCount);
   }

   public static final int commonSelect(@NotNull RealBufferedSource $this$commonSelect, @NotNull Options options) {
      Intrinsics.checkNotNullParameter($this$commonSelect, "<this>");
      Intrinsics.checkNotNullParameter(options, "options");
      int $i$f$commonSelect = 0;
      if ($this$commonSelect.closed) {
         int var4 = 0;
         String this_$iv = "closed";
         throw new IllegalStateException(this_$iv.toString());
      } else {
         label19:
         while(true) {
            int $i$f$getBuffer = 0;
            int index = -Buffer.selectPrefix($this$commonSelect.bufferField, options, true);
            switch (index) {
               case -2:
                  int $i$f$getBuffer = 0;
                  if ($this$commonSelect.source.read($this$commonSelect.bufferField, 8192L) == -1L) {
                     break label19;
                  }
                  break;
               case -1:
                  return -1;
               default:
                  $i$f$getBuffer = options.getByteStrings$okio()[index].size();
                  int $i$f$getBuffer = 0;
                  $this$commonSelect.bufferField.skip((long)$i$f$getBuffer);
                  return index;
            }
         }

         return -1;
      }
   }

   @NotNull
   public static final byte[] commonReadByteArray(@NotNull RealBufferedSource $this$commonReadByteArray) {
      Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
      int $i$f$commonReadByteArray = 0;
      int $i$f$getBuffer = 0;
      $this$commonReadByteArray.bufferField.writeAll($this$commonReadByteArray.source);
      $i$f$getBuffer = 0;
      return $this$commonReadByteArray.bufferField.readByteArray();
   }

   @NotNull
   public static final byte[] commonReadByteArray(@NotNull RealBufferedSource $this$commonReadByteArray, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
      int $i$f$commonReadByteArray = 0;
      $this$commonReadByteArray.require(byteCount);
      int $i$f$getBuffer = 0;
      return $this$commonReadByteArray.bufferField.readByteArray(byteCount);
   }

   public static final void commonReadFully(@NotNull RealBufferedSource $this$commonReadFully, @NotNull byte[] sink) {
      Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadFully = 0;

      try {
         $this$commonReadFully.require((long)sink.length);
      } catch (EOFException e) {
         int offset = 0;

         while(true) {
            int $i$f$getBuffer = 0;
            if ($this$commonReadFully.bufferField.size() <= 0L) {
               throw e;
            }

            int $i$f$getBuffer = 0;
            $i$f$getBuffer = 0;
            int read = $this$commonReadFully.bufferField.read(sink, offset, (int)$this$commonReadFully.bufferField.size());
            if (read == -1) {
               throw new AssertionError();
            }

            offset += read;
         }
      }

      int $i$f$getBuffer = 0;
      $this$commonReadFully.bufferField.readFully(sink);
   }

   public static final int commonRead(@NotNull RealBufferedSource $this$commonRead, @NotNull byte[] sink, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      okio.-SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
      int $i$f$getBuffer = 0;
      if ($this$commonRead.bufferField.size() == 0L) {
         if (byteCount == 0) {
            return 0;
         }

         int $i$f$getBuffer = 0;
         long read = $this$commonRead.source.read($this$commonRead.bufferField, 8192L);
         if (read == -1L) {
            return -1;
         }
      }

      int $i$f$getBuffer = 0;
      long b$iv = $this$commonRead.bufferField.size();
      int $i$f$minOf = 0;
      int toRead = (int)Math.min((long)byteCount, b$iv);
      int $i$f$getBuffer = 0;
      return $this$commonRead.bufferField.read(sink, offset, toRead);
   }

   public static final void commonReadFully(@NotNull RealBufferedSource $this$commonReadFully, @NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadFully = 0;

      try {
         $this$commonReadFully.require(byteCount);
      } catch (EOFException e) {
         int $i$f$getBuffer = 0;
         sink.writeAll($this$commonReadFully.bufferField);
         throw e;
      }

      int $i$f$getBuffer = 0;
      $this$commonReadFully.bufferField.readFully(sink, byteCount);
   }

   public static final long commonReadAll(@NotNull RealBufferedSource $this$commonReadAll, @NotNull Sink sink) {
      Intrinsics.checkNotNullParameter($this$commonReadAll, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadAll = 0;
      long totalBytesWritten = 0L;

      while(true) {
         int $i$f$getBuffer = 0;
         if ($this$commonReadAll.source.read($this$commonReadAll.bufferField, 8192L) == -1L) {
            $i$f$getBuffer = 0;
            if ($this$commonReadAll.bufferField.size() > 0L) {
               $i$f$getBuffer = 0;
               totalBytesWritten += $this$commonReadAll.bufferField.size();
               $i$f$getBuffer = 0;
               $i$f$getBuffer = 0;
               sink.write($this$commonReadAll.bufferField, $this$commonReadAll.bufferField.size());
            }

            return totalBytesWritten;
         }

         int $i$f$getBuffer = 0;
         long emitByteCount = $this$commonReadAll.bufferField.completeSegmentByteCount();
         if (emitByteCount > 0L) {
            totalBytesWritten += emitByteCount;
            $i$f$getBuffer = 0;
            sink.write($this$commonReadAll.bufferField, emitByteCount);
         }
      }
   }

   @NotNull
   public static final String commonReadUtf8(@NotNull RealBufferedSource $this$commonReadUtf8) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
      int $i$f$commonReadUtf8 = 0;
      int $i$f$getBuffer = 0;
      $this$commonReadUtf8.bufferField.writeAll($this$commonReadUtf8.source);
      $i$f$getBuffer = 0;
      return $this$commonReadUtf8.bufferField.readUtf8();
   }

   @NotNull
   public static final String commonReadUtf8(@NotNull RealBufferedSource $this$commonReadUtf8, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
      int $i$f$commonReadUtf8 = 0;
      $this$commonReadUtf8.require(byteCount);
      int $i$f$getBuffer = 0;
      return $this$commonReadUtf8.bufferField.readUtf8(byteCount);
   }

   @Nullable
   public static final String commonReadUtf8Line(@NotNull RealBufferedSource $this$commonReadUtf8Line) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8Line, "<this>");
      int $i$f$commonReadUtf8Line = 0;
      long newline = $this$commonReadUtf8Line.indexOf((byte)10);
      String var10000;
      if (newline == -1L) {
         int $i$f$getBuffer = 0;
         if ($this$commonReadUtf8Line.bufferField.size() != 0L) {
            $i$f$getBuffer = 0;
            var10000 = $this$commonReadUtf8Line.readUtf8($this$commonReadUtf8Line.bufferField.size());
         } else {
            var10000 = null;
         }
      } else {
         int $i$f$getBuffer = 0;
         var10000 = -Buffer.readUtf8Line($this$commonReadUtf8Line.bufferField, newline);
      }

      return var10000;
   }

   @NotNull
   public static final String commonReadUtf8LineStrict(@NotNull RealBufferedSource $this$commonReadUtf8LineStrict, long limit) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8LineStrict, "<this>");
      int $i$f$commonReadUtf8LineStrict = 0;
      if (limit < 0L) {
         int var5 = 0;
         String var13 = "limit < 0: " + limit;
         throw new IllegalArgumentException(var13.toString());
      } else {
         long scanLength = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
         long newline = $this$commonReadUtf8LineStrict.indexOf((byte)10, 0L, scanLength);
         if (newline != -1L) {
            int $i$f$getBuffer = 0;
            return -Buffer.readUtf8Line($this$commonReadUtf8LineStrict.bufferField, newline);
         } else {
            if (scanLength < Long.MAX_VALUE && $this$commonReadUtf8LineStrict.request(scanLength)) {
               int $i$f$getBuffer = 0;
               if ($this$commonReadUtf8LineStrict.bufferField.getByte(scanLength - 1L) == 13 && $this$commonReadUtf8LineStrict.request(scanLength + 1L)) {
                  $i$f$getBuffer = 0;
                  if ($this$commonReadUtf8LineStrict.bufferField.getByte(scanLength) == 10) {
                     $i$f$getBuffer = 0;
                     return -Buffer.readUtf8Line($this$commonReadUtf8LineStrict.bufferField, scanLength);
                  }
               }
            }

            Buffer data = new Buffer();
            int $i$f$getBuffer = 0;
            Buffer var10000 = $this$commonReadUtf8LineStrict.bufferField;
            byte $i$f$getBuffer = 32;
            int $i$f$getBuffer = 0;
            long b$iv = $this$commonReadUtf8LineStrict.bufferField.size();
            int $i$f$minOf = 0;
            var10000.copyTo(data, 0L, Math.min((long)$i$f$getBuffer, b$iv));
            StringBuilder var10002 = (new StringBuilder()).append("\\n not found: limit=");
            int $i$f$getBuffer = 0;
            throw new EOFException(var10002.append(Math.min($this$commonReadUtf8LineStrict.bufferField.size(), limit)).append(" content=").append(data.readByteString().hex()).append('…').toString());
         }
      }
   }

   public static final int commonReadUtf8CodePoint(@NotNull RealBufferedSource $this$commonReadUtf8CodePoint) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8CodePoint, "<this>");
      int $i$f$commonReadUtf8CodePoint = 0;
      $this$commonReadUtf8CodePoint.require(1L);
      int $i$f$getBuffer = 0;
      int b0 = $this$commonReadUtf8CodePoint.bufferField.getByte(0L);
      if ((b0 & 224) == 192) {
         $this$commonReadUtf8CodePoint.require(2L);
      } else if ((b0 & 240) == 224) {
         $this$commonReadUtf8CodePoint.require(3L);
      } else if ((b0 & 248) == 240) {
         $this$commonReadUtf8CodePoint.require(4L);
      }

      $i$f$getBuffer = 0;
      return $this$commonReadUtf8CodePoint.bufferField.readUtf8CodePoint();
   }

   public static final short commonReadShort(@NotNull RealBufferedSource $this$commonReadShort) {
      Intrinsics.checkNotNullParameter($this$commonReadShort, "<this>");
      int $i$f$commonReadShort = 0;
      $this$commonReadShort.require(2L);
      int $i$f$getBuffer = 0;
      return $this$commonReadShort.bufferField.readShort();
   }

   public static final short commonReadShortLe(@NotNull RealBufferedSource $this$commonReadShortLe) {
      Intrinsics.checkNotNullParameter($this$commonReadShortLe, "<this>");
      int $i$f$commonReadShortLe = 0;
      $this$commonReadShortLe.require(2L);
      int $i$f$getBuffer = 0;
      return $this$commonReadShortLe.bufferField.readShortLe();
   }

   public static final int commonReadInt(@NotNull RealBufferedSource $this$commonReadInt) {
      Intrinsics.checkNotNullParameter($this$commonReadInt, "<this>");
      int $i$f$commonReadInt = 0;
      $this$commonReadInt.require(4L);
      int $i$f$getBuffer = 0;
      return $this$commonReadInt.bufferField.readInt();
   }

   public static final int commonReadIntLe(@NotNull RealBufferedSource $this$commonReadIntLe) {
      Intrinsics.checkNotNullParameter($this$commonReadIntLe, "<this>");
      int $i$f$commonReadIntLe = 0;
      $this$commonReadIntLe.require(4L);
      int $i$f$getBuffer = 0;
      return $this$commonReadIntLe.bufferField.readIntLe();
   }

   public static final long commonReadLong(@NotNull RealBufferedSource $this$commonReadLong) {
      Intrinsics.checkNotNullParameter($this$commonReadLong, "<this>");
      int $i$f$commonReadLong = 0;
      $this$commonReadLong.require(8L);
      int $i$f$getBuffer = 0;
      return $this$commonReadLong.bufferField.readLong();
   }

   public static final long commonReadLongLe(@NotNull RealBufferedSource $this$commonReadLongLe) {
      Intrinsics.checkNotNullParameter($this$commonReadLongLe, "<this>");
      int $i$f$commonReadLongLe = 0;
      $this$commonReadLongLe.require(8L);
      int $i$f$getBuffer = 0;
      return $this$commonReadLongLe.bufferField.readLongLe();
   }

   public static final long commonReadDecimalLong(@NotNull RealBufferedSource $this$commonReadDecimalLong) {
      Intrinsics.checkNotNullParameter($this$commonReadDecimalLong, "<this>");
      int $i$f$commonReadDecimalLong = 0;
      $this$commonReadDecimalLong.require(1L);

      for(long pos = 0L; $this$commonReadDecimalLong.request(pos + 1L); ++pos) {
         int $i$f$getBuffer = 0;
         byte b = $this$commonReadDecimalLong.bufferField.getByte(pos);
         if ((b < 48 || b > 57) && (pos != 0L || b != 45)) {
            if (pos == 0L) {
               StringBuilder var10002 = (new StringBuilder()).append("Expected a digit or '-' but was 0x");
               String var10003 = Integer.toString(b, CharsKt.checkRadix(16));
               Intrinsics.checkNotNullExpressionValue(var10003, "toString(...)");
               throw new NumberFormatException(var10002.append(var10003).toString());
            }
            break;
         }
      }

      int $i$f$getBuffer = 0;
      return $this$commonReadDecimalLong.bufferField.readDecimalLong();
   }

   public static final long commonReadHexadecimalUnsignedLong(@NotNull RealBufferedSource $this$commonReadHexadecimalUnsignedLong) {
      Intrinsics.checkNotNullParameter($this$commonReadHexadecimalUnsignedLong, "<this>");
      int $i$f$commonReadHexadecimalUnsignedLong = 0;
      $this$commonReadHexadecimalUnsignedLong.require(1L);

      for(int pos = 0; $this$commonReadHexadecimalUnsignedLong.request((long)(pos + 1)); ++pos) {
         int $i$f$getBuffer = 0;
         byte b = $this$commonReadHexadecimalUnsignedLong.bufferField.getByte((long)pos);
         if ((b < 48 || b > 57) && (b < 97 || b > 102) && (b < 65 || b > 70)) {
            if (pos == 0) {
               StringBuilder var10002 = (new StringBuilder()).append("Expected leading [0-9a-fA-F] character but was 0x");
               String var10003 = Integer.toString(b, CharsKt.checkRadix(16));
               Intrinsics.checkNotNullExpressionValue(var10003, "toString(...)");
               throw new NumberFormatException(var10002.append(var10003).toString());
            }
            break;
         }
      }

      int $i$f$getBuffer = 0;
      return $this$commonReadHexadecimalUnsignedLong.bufferField.readHexadecimalUnsignedLong();
   }

   public static final void commonSkip(@NotNull RealBufferedSource $this$commonSkip, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonSkip, "<this>");
      int $i$f$commonSkip = 0;
      long byteCount = byteCount;
      if ($this$commonSkip.closed) {
         int $i$f$getBuffer = 0;
         String var12 = "closed";
         throw new IllegalStateException(var12.toString());
      } else {
         while(byteCount > 0L) {
            int $i$f$getBuffer = 0;
            if ($this$commonSkip.bufferField.size() == 0L) {
               $i$f$getBuffer = 0;
               if ($this$commonSkip.source.read($this$commonSkip.bufferField, 8192L) == -1L) {
                  throw new EOFException();
               }
            }

            int $i$f$getBuffer = 0;
            long this_$iv = $this$commonSkip.bufferField.size();
            long toSkip = Math.min(byteCount, this_$iv);
            $i$f$getBuffer = 0;
            $this$commonSkip.bufferField.skip(toSkip);
            byteCount -= toSkip;
         }

      }
   }

   public static final long commonIndexOf(@NotNull RealBufferedSource $this$commonIndexOf, byte b, long fromIndex, long toIndex) {
      Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
      int $i$f$commonIndexOf = 0;
      long fromIndex = 0L;
      fromIndex = fromIndex;
      if ($this$commonIndexOf.closed) {
         int var16 = 0;
         String var17 = "closed";
         throw new IllegalStateException(var17.toString());
      } else if (!(0L <= fromIndex ? fromIndex <= toIndex : false)) {
         int var8 = 0;
         String var15 = "fromIndex=" + fromIndex + " toIndex=" + toIndex;
         throw new IllegalArgumentException(var15.toString());
      } else {
         while(true) {
            if (fromIndex >= toIndex) {
               return -1L;
            }

            int $i$f$getBuffer = 0;
            long result = $this$commonIndexOf.bufferField.indexOf(b, fromIndex, toIndex);
            if (result != -1L) {
               return result;
            }

            int $i$f$getBuffer = 0;
            long lastBufferSize = $this$commonIndexOf.bufferField.size();
            if (lastBufferSize >= toIndex) {
               break;
            }

            $i$f$getBuffer = 0;
            if ($this$commonIndexOf.source.read($this$commonIndexOf.bufferField, 8192L) == -1L) {
               break;
            }

            fromIndex = Math.max(fromIndex, lastBufferSize);
         }

         return -1L;
      }
   }

   public static final long commonIndexOf(@NotNull RealBufferedSource $this$commonIndexOf, @NotNull ByteString bytes, int bytesOffset, int byteCount, long fromIndex, long toIndex) {
      Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      okio.-SegmentedByteString.checkOffsetAndCount((long)bytes.size(), (long)bytesOffset, (long)byteCount);
      long fromIndex = fromIndex;
      if ($this$commonIndexOf.closed) {
         int var11 = 0;
         String var18 = "closed";
         throw new IllegalStateException(var18.toString());
      } else {
         while(true) {
            int $i$f$getBuffer = 0;
            long result = -Buffer.commonIndexOf($this$commonIndexOf.bufferField, bytes, fromIndex, toIndex, bytesOffset, byteCount);
            if (result != -1L) {
               return result;
            }

            int $i$f$getBuffer = 0;
            long lastBufferSize = $this$commonIndexOf.bufferField.size();
            long nextFromIndex = lastBufferSize - (long)byteCount + 1L;
            if (nextFromIndex >= toIndex) {
               return -1L;
            }

            int $i$f$getBuffer = 0;
            if (!isMatchPossibleByExpandingBuffer($this$commonIndexOf.bufferField, bytes, bytesOffset, byteCount, fromIndex, toIndex)) {
               return -1L;
            }

            $i$f$getBuffer = 0;
            if ($this$commonIndexOf.source.read($this$commonIndexOf.bufferField, 8192L) == -1L) {
               return -1L;
            }

            fromIndex = Math.max(fromIndex, nextFromIndex);
         }
      }
   }

   // $FF: synthetic method
   public static long commonIndexOf$default(RealBufferedSource var0, ByteString var1, int var2, int var3, long var4, long var6, int var8, Object var9) {
      if ((var8 & 2) != 0) {
         var2 = 0;
      }

      if ((var8 & 4) != 0) {
         var3 = var1.size();
      }

      if ((var8 & 16) != 0) {
         var6 = Long.MAX_VALUE;
      }

      return commonIndexOf(var0, var1, var2, var3, var4, var6);
   }

   private static final boolean isMatchPossibleByExpandingBuffer(Buffer $this$isMatchPossibleByExpandingBuffer, ByteString bytes, int bytesOffset, int byteCount, long fromIndex, long toIndex) {
      if ($this$isMatchPossibleByExpandingBuffer.size() < toIndex) {
         return true;
      } else {
         int begin = (int)Math.max(1L, $this$isMatchPossibleByExpandingBuffer.size() - toIndex + 1L);
         long b$iv = $this$isMatchPossibleByExpandingBuffer.size() - fromIndex + 1L;
         int $i$f$minOf = 0;
         int limit = (int)Math.min((long)byteCount, b$iv);
         int i = limit - 1;
         if (begin <= i) {
            while(true) {
               if ($this$isMatchPossibleByExpandingBuffer.rangeEquals($this$isMatchPossibleByExpandingBuffer.size() - (long)i, bytes, bytesOffset, i)) {
                  return true;
               }

               if (i == begin) {
                  break;
               }

               --i;
            }
         }

         return false;
      }
   }

   public static final long commonIndexOfElement(@NotNull RealBufferedSource $this$commonIndexOfElement, @NotNull ByteString targetBytes, long fromIndex) {
      Intrinsics.checkNotNullParameter($this$commonIndexOfElement, "<this>");
      Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
      int $i$f$commonIndexOfElement = 0;
      long fromIndex = fromIndex;
      if ($this$commonIndexOfElement.closed) {
         int var8 = 0;
         String var13 = "closed";
         throw new IllegalStateException(var13.toString());
      } else {
         while(true) {
            int $i$f$getBuffer = 0;
            long result = $this$commonIndexOfElement.bufferField.indexOfElement(targetBytes, fromIndex);
            if (result != -1L) {
               return result;
            }

            int $i$f$getBuffer = 0;
            long lastBufferSize = $this$commonIndexOfElement.bufferField.size();
            $i$f$getBuffer = 0;
            if ($this$commonIndexOfElement.source.read($this$commonIndexOfElement.bufferField, 8192L) == -1L) {
               return -1L;
            }

            fromIndex = Math.max(fromIndex, lastBufferSize);
         }
      }
   }

   public static final boolean commonRangeEquals(@NotNull RealBufferedSource $this$commonRangeEquals, long offset, @NotNull ByteString bytes, int bytesOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      int $i$f$commonRangeEquals = 0;
      if ($this$commonRangeEquals.closed) {
         int var7 = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else if (byteCount < 0) {
         return false;
      } else if (offset < 0L) {
         return false;
      } else if (bytesOffset >= 0 && bytesOffset + byteCount <= bytes.size()) {
         if (byteCount == 0) {
            return true;
         } else {
            return commonIndexOf($this$commonRangeEquals, bytes, bytesOffset, byteCount, offset, offset + 1L) != -1L;
         }
      } else {
         return false;
      }
   }

   @NotNull
   public static final BufferedSource commonPeek(@NotNull RealBufferedSource $this$commonPeek) {
      Intrinsics.checkNotNullParameter($this$commonPeek, "<this>");
      int $i$f$commonPeek = 0;
      return Okio.buffer(new PeekSource($this$commonPeek));
   }

   public static final void commonClose(@NotNull RealBufferedSource $this$commonClose) {
      Intrinsics.checkNotNullParameter($this$commonClose, "<this>");
      int $i$f$commonClose = 0;
      if (!$this$commonClose.closed) {
         $this$commonClose.closed = true;
         $this$commonClose.source.close();
         int $i$f$getBuffer = 0;
         $this$commonClose.bufferField.clear();
      }
   }

   @NotNull
   public static final Timeout commonTimeout(@NotNull RealBufferedSource $this$commonTimeout) {
      Intrinsics.checkNotNullParameter($this$commonTimeout, "<this>");
      int $i$f$commonTimeout = 0;
      return $this$commonTimeout.source.timeout();
   }

   @NotNull
   public static final String commonToString(@NotNull RealBufferedSource $this$commonToString) {
      Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
      int $i$f$commonToString = 0;
      return "buffer(" + $this$commonToString.source + ')';
   }
}
