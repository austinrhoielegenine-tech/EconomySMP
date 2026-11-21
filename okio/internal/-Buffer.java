package okio.internal;

import java.io.EOFException;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import okio.Options;
import okio.Segment;
import okio.SegmentPool;
import okio.SegmentedByteString;
import okio.Sink;
import okio.Source;
import okio._JvmPlatformKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0086\u0001\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\n\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0016\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\u001a0\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u0005H\u0000\u001a\u0014\u0010\u000e\u001a\u00020\u000f*\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0000\u001a?\u0010\u0013\u001a\u0002H\u0014\"\u0004\b\u0000\u0010\u0014*\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00122\u001a\u0010\u0016\u001a\u0016\u0012\u0006\u0012\u0004\u0018\u00010\t\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u0002H\u00140\u0017H\u0080\bø\u0001\u0000¢\u0006\u0002\u0010\u0018\u001a\u001e\u0010\u0019\u001a\u00020\u0005*\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u0007H\u0000\u001a%\u0010\u001d\u001a\u00020\u0010*\u00020\u00102\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\r\u0010!\u001a\u00020\u0012*\u00020\u0010H\u0080\b\u001a\r\u0010\"\u001a\u00020#*\u00020\u0010H\u0080\b\u001a\r\u0010$\u001a\u00020%*\u00020\u0010H\u0080\b\u001a\r\u0010&\u001a\u00020\u0005*\u00020\u0010H\u0080\b\u001a\r\u0010'\u001a\u00020\u0012*\u00020\u0010H\u0080\b\u001a\u0015\u0010(\u001a\u00020#*\u00020\u00102\u0006\u0010)\u001a\u00020\u0012H\u0080\b\u001a\r\u0010*\u001a\u00020+*\u00020\u0010H\u0080\b\u001a\u0015\u0010,\u001a\u00020+*\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a)\u0010-\u001a\u00020\u0010*\u00020\u00102\u0006\u0010.\u001a\u00020/2\b\b\u0002\u0010\u001f\u001a\u00020\u00052\b\b\u0002\u0010 \u001a\u00020\u0005H\u0080\b\u001a\u0015\u00100\u001a\u00020\u0010*\u00020\u00102\u0006\u00101\u001a\u00020\u0012H\u0080\b\u001a\u0010\u00102\u001a\u00020\u00052\u0006\u00101\u001a\u00020\u0012H\u0002\u001a\u0015\u00105\u001a\u00020\u0010*\u00020\u00102\u0006\u00101\u001a\u00020\u0012H\u0080\b\u001a\u0015\u00106\u001a\u00020\t*\u00020\u00102\u0006\u00107\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010-\u001a\u00020\u0010*\u00020\u00102\u0006\u00108\u001a\u00020\u0001H\u0080\b\u001a%\u0010-\u001a\u00020\u0010*\u00020\u00102\u0006\u00108\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u0005H\u0080\b\u001a\r\u00109\u001a\u00020\u0001*\u00020\u0010H\u0080\b\u001a\u0015\u00109\u001a\u00020\u0001*\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010:\u001a\u00020\u0005*\u00020\u00102\u0006\u0010;\u001a\u00020\u0001H\u0080\b\u001a\u0015\u0010<\u001a\u00020+*\u00020\u00102\u0006\u0010;\u001a\u00020\u0001H\u0080\b\u001a%\u0010:\u001a\u00020\u0005*\u00020\u00102\u0006\u0010;\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u0005H\u0080\b\u001a\r\u0010?\u001a\u00020\u0012*\u00020\u0010H\u0080\b\u001a\r\u0010@\u001a\u00020\u0012*\u00020\u0010H\u0080\b\u001a\r\u0010A\u001a\u00020/*\u00020\u0010H\u0080\b\u001a\u0015\u0010A\u001a\u00020/*\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010B\u001a\u00020\u0005*\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u001bH\u0080\b\u001a\u001d\u0010<\u001a\u00020+*\u00020\u00102\u0006\u0010;\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010C\u001a\u00020\u0012*\u00020\u00102\u0006\u0010;\u001a\u00020DH\u0080\b\u001a\u0015\u0010E\u001a\u00020\u000f*\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\u000f\u0010F\u001a\u0004\u0018\u00010\u000f*\u00020\u0010H\u0080\b\u001a\u0015\u0010G\u001a\u00020\u000f*\u00020\u00102\u0006\u0010H\u001a\u00020\u0012H\u0080\b\u001a\r\u0010I\u001a\u00020\u0005*\u00020\u0010H\u0080\b\u001a%\u0010J\u001a\u00020\u0010*\u00020\u00102\u0006\u0010K\u001a\u00020\u000f2\u0006\u0010L\u001a\u00020\u00052\u0006\u0010M\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010N\u001a\u00020\u0010*\u00020\u00102\u0006\u0010O\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010P\u001a\u00020\u0012*\u00020\u00102\u0006\u00108\u001a\u00020QH\u0080\b\u001a\u001d\u0010-\u001a\u00020\u0010*\u00020\u00102\u0006\u00108\u001a\u00020Q2\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010R\u001a\u00020\u0010*\u00020\u00102\u0006\u0010S\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010T\u001a\u00020\u0010*\u00020\u00102\u0006\u0010U\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010V\u001a\u00020\u0010*\u00020\u00102\u0006\u0010W\u001a\u00020\u0005H\u0080\b\u001a\u0015\u0010X\u001a\u00020\u0010*\u00020\u00102\u0006\u00101\u001a\u00020\u0012H\u0080\b\u001a\u001d\u0010-\u001a\u00020+*\u00020\u00102\u0006\u00108\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a\u001d\u0010:\u001a\u00020\u0012*\u00020\u00102\u0006\u0010;\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u0012H\u0080\b\u001a%\u0010Y\u001a\u00020\u0012*\u00020\u00102\u0006\u0010S\u001a\u00020#2\u0006\u0010\u0015\u001a\u00020\u00122\u0006\u0010Z\u001a\u00020\u0012H\u0080\b\u001a:\u0010Y\u001a\u00020\u0012*\u00020\u00102\u0006\u0010\u000b\u001a\u00020/2\u0006\u0010\u0015\u001a\u00020\u00122\b\b\u0002\u0010Z\u001a\u00020\u00122\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010 \u001a\u00020\u0005H\u0000\u001a\u001d\u0010[\u001a\u00020\u0012*\u00020\u00102\u0006\u0010\\\u001a\u00020/2\u0006\u0010\u0015\u001a\u00020\u0012H\u0080\b\u001a-\u0010]\u001a\u00020\u0007*\u00020\u00102\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u000b\u001a\u00020/2\u0006\u0010\f\u001a\u00020\u00052\u0006\u0010 \u001a\u00020\u0005H\u0080\b\u001a\u0017\u0010^\u001a\u00020\u0007*\u00020\u00102\b\u0010_\u001a\u0004\u0018\u00010`H\u0080\b\u001a\r\u0010a\u001a\u00020\u0005*\u00020\u0010H\u0080\b\u001a\r\u0010b\u001a\u00020\u0010*\u00020\u0010H\u0080\b\u001a\r\u0010c\u001a\u00020/*\u00020\u0010H\u0080\b\u001a\u0015\u0010c\u001a\u00020/*\u00020\u00102\u0006\u0010 \u001a\u00020\u0005H\u0080\b\u001a\u0014\u0010d\u001a\u00020e*\u00020\u00102\u0006\u0010f\u001a\u00020eH\u0000\u001a\u0014\u0010g\u001a\u00020e*\u00020\u00102\u0006\u0010f\u001a\u00020eH\u0000\u001a\r\u0010h\u001a\u00020\u0005*\u00020eH\u0080\b\u001a\u0015\u0010i\u001a\u00020\u0005*\u00020e2\u0006\u0010\u001f\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010j\u001a\u00020\u0012*\u00020e2\u0006\u0010k\u001a\u00020\u0012H\u0080\b\u001a\u0015\u0010l\u001a\u00020\u0012*\u00020e2\u0006\u0010m\u001a\u00020\u0005H\u0080\b\u001a\r\u0010n\u001a\u00020+*\u00020eH\u0080\b\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u00103\u001a\u000204X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010=\u001a\u00020\u0012X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010>\u001a\u00020\u0012X\u0080T¢\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006o"},
   d2 = {"HEX_DIGIT_BYTES", "", "getHEX_DIGIT_BYTES", "()[B", "SEGMENTING_THRESHOLD", "", "rangeEquals", "", "segment", "Lokio/Segment;", "segmentPos", "bytes", "bytesOffset", "bytesLimit", "readUtf8Line", "", "Lokio/Buffer;", "newline", "", "seek", "T", "fromIndex", "lambda", "Lkotlin/Function2;", "(Lokio/Buffer;JLkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "selectPrefix", "options", "Lokio/Options;", "selectTruncated", "commonCopyTo", "out", "offset", "byteCount", "commonCompleteSegmentByteCount", "commonReadByte", "", "commonReadShort", "", "commonReadInt", "commonReadLong", "commonGet", "pos", "commonClear", "", "commonSkip", "commonWrite", "byteString", "Lokio/ByteString;", "commonWriteDecimalLong", "v", "countDigitsIn", "DigitCountToLargestValue", "", "commonWriteHexadecimalUnsignedLong", "commonWritableSegment", "minimumCapacity", "source", "commonReadByteArray", "commonRead", "sink", "commonReadFully", "OVERFLOW_ZONE", "OVERFLOW_DIGIT_START", "commonReadDecimalLong", "commonReadHexadecimalUnsignedLong", "commonReadByteString", "commonSelect", "commonReadAll", "Lokio/Sink;", "commonReadUtf8", "commonReadUtf8Line", "commonReadUtf8LineStrict", "limit", "commonReadUtf8CodePoint", "commonWriteUtf8", "string", "beginIndex", "endIndex", "commonWriteUtf8CodePoint", "codePoint", "commonWriteAll", "Lokio/Source;", "commonWriteByte", "b", "commonWriteShort", "s", "commonWriteInt", "i", "commonWriteLong", "commonIndexOf", "toIndex", "commonIndexOfElement", "targetBytes", "commonRangeEquals", "commonEquals", "other", "", "commonHashCode", "commonCopy", "commonSnapshot", "commonReadUnsafe", "Lokio/Buffer$UnsafeCursor;", "unsafeCursor", "commonReadAndWriteUnsafe", "commonNext", "commonSeek", "commonResizeBuffer", "newSize", "commonExpandBuffer", "minByteCount", "commonClose", "okio"}
)
@JvmName(
   name = "-Buffer"
)
@SourceDebugExtension({"SMAP\nBuffer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Buffer.kt\nokio/internal/-Buffer\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1712:1\n110#1,20:1735\n110#1,20:1768\n110#1:1788\n112#1,18:1790\n110#1,20:1808\n73#2:1713\n73#2:1714\n73#2:1715\n73#2:1716\n73#2:1717\n73#2:1718\n73#2:1719\n73#2:1720\n73#2:1721\n73#2:1722\n73#2:1723\n73#2:1724\n82#2:1725\n82#2:1726\n76#2:1727\n76#2:1728\n76#2:1729\n76#2:1730\n76#2:1731\n76#2:1732\n76#2:1733\n76#2:1734\n85#2:1755\n88#2:1757\n73#2:1758\n73#2:1759\n73#2:1760\n73#2:1761\n73#2:1762\n73#2:1763\n73#2:1764\n73#2:1765\n73#2:1766\n73#2:1767\n88#2:1789\n85#2:1828\n1#3:1756\n*S KotlinDebug\n*F\n+ 1 Buffer.kt\nokio/internal/-Buffer\n*L\n413#1:1735,20\n1262#1:1768,20\n1305#1:1788\n1305#1:1790,18\n1341#1:1808,20\n176#1:1713\n200#1:1714\n319#1:1715\n324#1:1716\n347#1:1717\n348#1:1718\n349#1:1719\n350#1:1720\n356#1:1721\n357#1:1722\n358#1:1723\n359#1:1724\n383#1:1725\n384#1:1726\n390#1:1727\n391#1:1728\n392#1:1729\n393#1:1730\n394#1:1731\n395#1:1732\n396#1:1733\n397#1:1734\n425#1:1755\n858#1:1757\n876#1:1758\n878#1:1759\n882#1:1760\n884#1:1761\n888#1:1762\n890#1:1763\n894#1:1764\n896#1:1765\n916#1:1766\n919#1:1767\n1317#1:1789\n1658#1:1828\n*E\n"})
public final class -Buffer {
   @NotNull
   private static final byte[] HEX_DIGIT_BYTES = _JvmPlatformKt.asUtf8ToByteArray("0123456789abcdef");
   public static final int SEGMENTING_THRESHOLD = 4096;
   @NotNull
   private static final long[] DigitCountToLargestValue;
   public static final long OVERFLOW_ZONE = -922337203685477580L;
   public static final long OVERFLOW_DIGIT_START = -7L;

   @NotNull
   public static final byte[] getHEX_DIGIT_BYTES() {
      return HEX_DIGIT_BYTES;
   }

   public static final boolean rangeEquals(@NotNull Segment segment, int segmentPos, @NotNull byte[] bytes, int bytesOffset, int bytesLimit) {
      Intrinsics.checkNotNullParameter(segment, "segment");
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      Segment segment = segment;
      int segmentPos = segmentPos;
      int segmentLimit = segment.limit;
      byte[] data = segment.data;

      for(int i = bytesOffset; i < bytesLimit; ++i) {
         if (segmentPos == segmentLimit) {
            Segment var10000 = segment.next;
            Intrinsics.checkNotNull(var10000);
            segment = var10000;
            data = segment.data;
            segmentPos = segment.pos;
            segmentLimit = segment.limit;
         }

         if (data[segmentPos] != bytes[i]) {
            return false;
         }

         ++segmentPos;
      }

      return true;
   }

   @NotNull
   public static final String readUtf8Line(@NotNull Buffer $this$readUtf8Line, long newline) {
      Intrinsics.checkNotNullParameter($this$readUtf8Line, "<this>");
      String var10000;
      if (newline > 0L && $this$readUtf8Line.getByte(newline - 1L) == 13) {
         String result = $this$readUtf8Line.readUtf8(newline - 1L);
         $this$readUtf8Line.skip(2L);
         var10000 = result;
      } else {
         String result = $this$readUtf8Line.readUtf8(newline);
         $this$readUtf8Line.skip(1L);
         var10000 = result;
      }

      return var10000;
   }

   public static final <T> T seek(@NotNull Buffer $this$seek, long fromIndex, @NotNull Function2<? super Segment, ? super Long, ? extends T> lambda) {
      Intrinsics.checkNotNullParameter($this$seek, "<this>");
      Intrinsics.checkNotNullParameter(lambda, "lambda");
      int $i$f$seek = 0;
      Segment var10000 = $this$seek.head;
      if (var10000 == null) {
         return (T)lambda.invoke((Object)null, -1L);
      } else {
         Segment s = var10000;
         if ($this$seek.size() - fromIndex < fromIndex) {
            long offset;
            for(offset = $this$seek.size(); offset > fromIndex; offset -= (long)(s.limit - s.pos)) {
               var10000 = s.prev;
               Intrinsics.checkNotNull(var10000);
               s = var10000;
            }

            return (T)lambda.invoke(s, offset);
         } else {
            long offset = 0L;

            while(true) {
               long nextOffset = offset + (long)(s.limit - s.pos);
               if (nextOffset > fromIndex) {
                  return (T)lambda.invoke(s, offset);
               }

               var10000 = s.next;
               Intrinsics.checkNotNull(var10000);
               s = var10000;
               offset = nextOffset;
            }
         }
      }
   }

   public static final int selectPrefix(@NotNull Buffer $this$selectPrefix, @NotNull Options options, boolean selectTruncated) {
      Intrinsics.checkNotNullParameter($this$selectPrefix, "<this>");
      Intrinsics.checkNotNullParameter(options, "options");
      Segment var10000 = $this$selectPrefix.head;
      if (var10000 == null) {
         return selectTruncated ? -2 : -1;
      } else {
         Segment head = var10000;
         Segment s = head;
         byte[] data = head.data;
         int pos = head.pos;
         int limit = head.limit;
         int[] trie = options.getTrie$okio();
         int triePos = 0;
         int prefixIndex = -1;

         while(true) {
            int scanOrSelect = trie[triePos++];
            int possiblePrefixIndex = trie[triePos++];
            if (possiblePrefixIndex != -1) {
               prefixIndex = possiblePrefixIndex;
            }

            int nextStep = 0;
            if (s != null) {
               label99: {
                  if (scanOrSelect < 0) {
                     int scanByteCount = -1 * scanOrSelect;
                     int trieLimit = triePos + scanByteCount;

                     boolean scanComplete;
                     do {
                        scanComplete = (boolean)data[pos++];
                        int other$iv = 255;
                        int $i$f$and = 0;
                        int var16 = scanComplete & other$iv;
                        if (var16 != trie[triePos++]) {
                           return prefixIndex;
                        }

                        scanComplete = triePos == trieLimit;
                        if (pos == limit) {
                           Intrinsics.checkNotNull(s);
                           var10000 = s.next;
                           Intrinsics.checkNotNull(var10000);
                           s = var10000;
                           pos = s.pos;
                           data = s.data;
                           limit = s.limit;
                           if (s == head) {
                              if (!scanComplete) {
                                 break label99;
                              }

                              s = null;
                           }
                        }
                     } while(!scanComplete);

                     nextStep = trie[triePos];
                  } else {
                     int selectLimit = data[pos++];
                     int other$iv = 255;
                     int $i$f$and = 0;
                     int var23 = selectLimit & other$iv;
                     selectLimit = triePos + scanOrSelect;

                     while(true) {
                        if (triePos == selectLimit) {
                           return prefixIndex;
                        }

                        if (var23 == trie[triePos]) {
                           nextStep = trie[triePos + scanOrSelect];
                           if (pos == limit) {
                              var10000 = s.next;
                              Intrinsics.checkNotNull(var10000);
                              s = var10000;
                              pos = s.pos;
                              data = s.data;
                              limit = s.limit;
                              if (s == head) {
                                 s = null;
                              }
                           }
                           break;
                        }

                        ++triePos;
                     }
                  }

                  if (nextStep >= 0) {
                     return nextStep;
                  }

                  triePos = -nextStep;
                  continue;
               }
            }

            if (selectTruncated) {
               return -2;
            }

            return prefixIndex;
         }
      }
   }

   // $FF: synthetic method
   public static int selectPrefix$default(Buffer var0, Options var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = false;
      }

      return selectPrefix(var0, var1, var2);
   }

   @NotNull
   public static final Buffer commonCopyTo(@NotNull Buffer $this$commonCopyTo, @NotNull Buffer out, long offset, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonCopyTo, "<this>");
      Intrinsics.checkNotNullParameter(out, "out");
      int $i$f$commonCopyTo = 0;
      long offset = offset;
      long byteCount = byteCount;
      okio.-SegmentedByteString.checkOffsetAndCount($this$commonCopyTo.size(), offset, byteCount);
      if (byteCount == 0L) {
         return $this$commonCopyTo;
      } else {
         out.setSize$okio(out.size() + byteCount);
         Segment s = $this$commonCopyTo.head;

         while(true) {
            Intrinsics.checkNotNull(s);
            if (offset < (long)(s.limit - s.pos)) {
               while(byteCount > 0L) {
                  Intrinsics.checkNotNull(s);
                  Segment copy = s.sharedCopy();
                  copy.pos += (int)offset;
                  copy.limit = Math.min(copy.pos + (int)byteCount, copy.limit);
                  if (out.head == null) {
                     copy.prev = copy;
                     copy.next = copy.prev;
                     out.head = copy.next;
                  } else {
                     Segment var10000 = out.head;
                     Intrinsics.checkNotNull(var10000);
                     var10000 = var10000.prev;
                     Intrinsics.checkNotNull(var10000);
                     var10000.push(copy);
                  }

                  byteCount -= (long)(copy.limit - copy.pos);
                  offset = 0L;
                  s = s.next;
               }

               return $this$commonCopyTo;
            }

            offset -= (long)(s.limit - s.pos);
            s = s.next;
         }
      }
   }

   public static final long commonCompleteSegmentByteCount(@NotNull Buffer $this$commonCompleteSegmentByteCount) {
      Intrinsics.checkNotNullParameter($this$commonCompleteSegmentByteCount, "<this>");
      int $i$f$commonCompleteSegmentByteCount = 0;
      long result = $this$commonCompleteSegmentByteCount.size();
      if (result == 0L) {
         return 0L;
      } else {
         Segment var10000 = $this$commonCompleteSegmentByteCount.head;
         Intrinsics.checkNotNull(var10000);
         var10000 = var10000.prev;
         Intrinsics.checkNotNull(var10000);
         Segment tail = var10000;
         if (tail.limit < 8192 && tail.owner) {
            result -= (long)(tail.limit - tail.pos);
         }

         return result;
      }
   }

   public static final byte commonReadByte(@NotNull Buffer $this$commonReadByte) {
      Intrinsics.checkNotNullParameter($this$commonReadByte, "<this>");
      int $i$f$commonReadByte = 0;
      if ($this$commonReadByte.size() == 0L) {
         throw new EOFException();
      } else {
         Segment var10000 = $this$commonReadByte.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment = var10000;
         int pos = segment.pos;
         int limit = segment.limit;
         byte[] data = segment.data;
         byte b = data[pos++];
         $this$commonReadByte.setSize$okio($this$commonReadByte.size() - 1L);
         if (pos == limit) {
            $this$commonReadByte.head = segment.pop();
            SegmentPool.recycle(segment);
         } else {
            segment.pos = pos;
         }

         return b;
      }
   }

   public static final short commonReadShort(@NotNull Buffer $this$commonReadShort) {
      Intrinsics.checkNotNullParameter($this$commonReadShort, "<this>");
      int $i$f$commonReadShort = 0;
      if ($this$commonReadShort.size() < 2L) {
         throw new EOFException();
      } else {
         Segment var10000 = $this$commonReadShort.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment = var10000;
         int pos = segment.pos;
         int limit = segment.limit;
         if (limit - pos < 2) {
            byte $this$and$iv = $this$commonReadShort.readByte();
            int other$iv = 255;
            int $i$f$and = 0;
            int var23 = ($this$and$iv & other$iv) << 8;
            $this$and$iv = $this$commonReadShort.readByte();
            other$iv = 255;
            $i$f$and = 0;
            int s = var23 | $this$and$iv & other$iv;
            return (short)s;
         } else {
            byte[] data = segment.data;
            byte other$iv = data[pos++];
            int other$iv = 255;
            int $i$f$and = 0;
            int var22 = (other$iv & other$iv) << 8;
            other$iv = data[pos++];
            other$iv = 255;
            $i$f$and = 0;
            int s = var22 | other$iv & other$iv;
            $this$commonReadShort.setSize$okio($this$commonReadShort.size() - 2L);
            if (pos == limit) {
               $this$commonReadShort.head = segment.pop();
               SegmentPool.recycle(segment);
            } else {
               segment.pos = pos;
            }

            return (short)s;
         }
      }
   }

   public static final int commonReadInt(@NotNull Buffer $this$commonReadInt) {
      Intrinsics.checkNotNullParameter($this$commonReadInt, "<this>");
      int $i$f$commonReadInt = 0;
      if ($this$commonReadInt.size() < 4L) {
         throw new EOFException();
      } else {
         Segment var10000 = $this$commonReadInt.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment = var10000;
         int pos = segment.pos;
         int limit = segment.limit;
         if ((long)(limit - pos) < 4L) {
            byte $this$and$iv = $this$commonReadInt.readByte();
            int other$iv = 255;
            int $i$f$and = 0;
            int var38 = ($this$and$iv & other$iv) << 24;
            $this$and$iv = $this$commonReadInt.readByte();
            other$iv = 255;
            $i$f$and = 0;
            var38 |= ($this$and$iv & other$iv) << 16;
            $this$and$iv = $this$commonReadInt.readByte();
            other$iv = 255;
            $i$f$and = 0;
            var38 |= ($this$and$iv & other$iv) << 8;
            $this$and$iv = $this$commonReadInt.readByte();
            other$iv = 255;
            $i$f$and = 0;
            return var38 | $this$and$iv & other$iv;
         } else {
            byte[] data = segment.data;
            byte $i$f$and = data[pos++];
            int other$iv = 255;
            int $i$f$and = 0;
            int var35 = ($i$f$and & other$iv) << 24;
            $i$f$and = data[pos++];
            other$iv = 255;
            $i$f$and = 0;
            var35 |= ($i$f$and & other$iv) << 16;
            $i$f$and = data[pos++];
            other$iv = 255;
            $i$f$and = 0;
            var35 |= ($i$f$and & other$iv) << 8;
            $i$f$and = data[pos++];
            other$iv = 255;
            $i$f$and = 0;
            int i = var35 | $i$f$and & other$iv;
            $this$commonReadInt.setSize$okio($this$commonReadInt.size() - 4L);
            if (pos == limit) {
               $this$commonReadInt.head = segment.pop();
               SegmentPool.recycle(segment);
            } else {
               segment.pos = pos;
            }

            return i;
         }
      }
   }

   public static final long commonReadLong(@NotNull Buffer $this$commonReadLong) {
      Intrinsics.checkNotNullParameter($this$commonReadLong, "<this>");
      int $i$f$commonReadLong = 0;
      if ($this$commonReadLong.size() < 8L) {
         throw new EOFException();
      } else {
         Segment var10000 = $this$commonReadLong.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment = var10000;
         int pos = segment.pos;
         int limit = segment.limit;
         if ((long)(limit - pos) < 8L) {
            int $this$and$iv = $this$commonReadLong.readInt();
            long other$iv = 4294967295L;
            int $i$f$and = 0;
            long var54 = ((long)$this$and$iv & other$iv) << 32;
            $this$and$iv = $this$commonReadLong.readInt();
            other$iv = 4294967295L;
            $i$f$and = 0;
            return var54 | (long)$this$and$iv & other$iv;
         } else {
            byte[] data = segment.data;
            byte $i$f$and = data[pos++];
            long other$iv = 255L;
            int $i$f$and = 0;
            long var47 = ((long)$i$f$and & other$iv) << 56;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            var47 |= ((long)$i$f$and & other$iv) << 48;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            var47 |= ((long)$i$f$and & other$iv) << 40;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            var47 |= ((long)$i$f$and & other$iv) << 32;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            var47 |= ((long)$i$f$and & other$iv) << 24;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            var47 |= ((long)$i$f$and & other$iv) << 16;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            var47 |= ((long)$i$f$and & other$iv) << 8;
            $i$f$and = data[pos++];
            other$iv = 255L;
            $i$f$and = 0;
            long v = var47 | (long)$i$f$and & other$iv;
            $this$commonReadLong.setSize$okio($this$commonReadLong.size() - 8L);
            if (pos == limit) {
               $this$commonReadLong.head = segment.pop();
               SegmentPool.recycle(segment);
            } else {
               segment.pos = pos;
            }

            return v;
         }
      }
   }

   public static final byte commonGet(@NotNull Buffer $this$commonGet, long pos) {
      Intrinsics.checkNotNullParameter($this$commonGet, "<this>");
      int $i$f$commonGet = 0;
      okio.-SegmentedByteString.checkOffsetAndCount($this$commonGet.size(), pos, 1L);
      long fromIndex$iv = pos;
      int $i$f$seek = 0;
      Segment var10000 = $this$commonGet.head;
      if (var10000 == null) {
         long nextOffset$iv = -1L;
         Segment s = null;
         int var11 = 0;
         Intrinsics.checkNotNull(s);
         return s.data[(int)((long)s.pos + pos - nextOffset$iv)];
      } else {
         Segment s$iv = var10000;
         if ($this$commonGet.size() - pos < pos) {
            long offset$iv;
            for(offset$iv = $this$commonGet.size(); offset$iv > fromIndex$iv; offset$iv -= (long)(s$iv.limit - s$iv.pos)) {
               var10000 = s$iv.prev;
               Intrinsics.checkNotNull(var10000);
               s$iv = var10000;
            }

            int var18 = 0;
            Intrinsics.checkNotNull(s$iv);
            return s$iv.data[(int)((long)s$iv.pos + pos - offset$iv)];
         } else {
            long offset$iv = 0L;

            while(true) {
               long nextOffset$iv = offset$iv + (long)(s$iv.limit - s$iv.pos);
               if (nextOffset$iv > fromIndex$iv) {
                  int var21 = 0;
                  Intrinsics.checkNotNull(s$iv);
                  return s$iv.data[(int)((long)s$iv.pos + pos - offset$iv)];
               }

               var10000 = s$iv.next;
               Intrinsics.checkNotNull(var10000);
               s$iv = var10000;
               offset$iv = nextOffset$iv;
            }
         }
      }
   }

   public static final void commonClear(@NotNull Buffer $this$commonClear) {
      Intrinsics.checkNotNullParameter($this$commonClear, "<this>");
      int $i$f$commonClear = 0;
      $this$commonClear.skip($this$commonClear.size());
   }

   public static final void commonSkip(@NotNull Buffer $this$commonSkip, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonSkip, "<this>");
      int $i$f$commonSkip = 0;
      long byteCount = byteCount;

      while(byteCount > 0L) {
         Segment var10000 = $this$commonSkip.head;
         if (var10000 == null) {
            throw new EOFException();
         }

         Segment head = var10000;
         int b$iv = head.limit - head.pos;
         int $i$f$minOf = 0;
         int toSkip = (int)Math.min(byteCount, (long)b$iv);
         $this$commonSkip.setSize$okio($this$commonSkip.size() - (long)toSkip);
         byteCount -= (long)toSkip;
         head.pos += toSkip;
         if (head.pos == head.limit) {
            $this$commonSkip.head = head.pop();
            SegmentPool.recycle(head);
         }
      }

   }

   @NotNull
   public static final Buffer commonWrite(@NotNull Buffer $this$commonWrite, @NotNull ByteString byteString, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int $i$f$commonWrite = 0;
      byteString.write$okio($this$commonWrite, offset, byteCount);
      return $this$commonWrite;
   }

   // $FF: synthetic method
   public static Buffer commonWrite$default(Buffer $this$commonWrite_u24default, ByteString byteString, int offset, int byteCount, int $i$f$commonWrite, Object var5) {
      if (($i$f$commonWrite & 2) != 0) {
         offset = 0;
      }

      if (($i$f$commonWrite & 4) != 0) {
         byteCount = byteString.size();
      }

      Intrinsics.checkNotNullParameter($this$commonWrite_u24default, "<this>");
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      $i$f$commonWrite = 0;
      byteString.write$okio($this$commonWrite_u24default, offset, byteCount);
      return $this$commonWrite_u24default;
   }

   @NotNull
   public static final Buffer commonWriteDecimalLong(@NotNull Buffer $this$commonWriteDecimalLong, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteDecimalLong, "<this>");
      int $i$f$commonWriteDecimalLong = 0;
      long v = v;
      if (v == 0L) {
         return $this$commonWriteDecimalLong.writeByte(48);
      } else {
         boolean negative = false;
         if (v < 0L) {
            v = -v;
            if (v < 0L) {
               return $this$commonWriteDecimalLong.writeUtf8("-9223372036854775808");
            }

            negative = true;
         }

         int width = access$countDigitsIn(v);
         if (negative) {
            ++width;
         }

         Segment tail = $this$commonWriteDecimalLong.writableSegment$okio(width);
         byte[] data = tail.data;

         int pos;
         for(pos = tail.limit + width; v != 0L; v /= (long)10) {
            int digit = (int)(v % (long)10);
            --pos;
            data[pos] = getHEX_DIGIT_BYTES()[digit];
         }

         if (negative) {
            --pos;
            data[pos] = 45;
         }

         tail.limit += width;
         $this$commonWriteDecimalLong.setSize$okio($this$commonWriteDecimalLong.size() + (long)width);
         return $this$commonWriteDecimalLong;
      }
   }

   private static final int countDigitsIn(long v) {
      int guess = (64 - Long.numberOfLeadingZeros(v)) * 10 >>> 5;
      return guess + (v > DigitCountToLargestValue[guess] ? 1 : 0);
   }

   @NotNull
   public static final Buffer commonWriteHexadecimalUnsignedLong(@NotNull Buffer $this$commonWriteHexadecimalUnsignedLong, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteHexadecimalUnsignedLong, "<this>");
      int $i$f$commonWriteHexadecimalUnsignedLong = 0;
      long v = v;
      if (v == 0L) {
         return $this$commonWriteHexadecimalUnsignedLong.writeByte(48);
      } else {
         long x = v | v >>> 1;
         x |= x >>> 2;
         x |= x >>> 4;
         x |= x >>> 8;
         x |= x >>> 16;
         x |= x >>> 32;
         x -= x >>> 1 & 6148914691236517205L;
         x = (x >>> 2 & 3689348814741910323L) + (x & 3689348814741910323L);
         x = (x >>> 4) + x & 1085102592571150095L;
         x += x >>> 8;
         x += x >>> 16;
         x = (x & 63L) + (x >>> 32 & 63L);
         int width = (int)((x + (long)3) / (long)4);
         Segment tail = $this$commonWriteHexadecimalUnsignedLong.writableSegment$okio(width);
         byte[] data = tail.data;
         int pos = tail.limit + width - 1;

         for(int start = tail.limit; pos >= start; --pos) {
            data[pos] = getHEX_DIGIT_BYTES()[(int)(v & 15L)];
            v >>>= 4;
         }

         tail.limit += width;
         $this$commonWriteHexadecimalUnsignedLong.setSize$okio($this$commonWriteHexadecimalUnsignedLong.size() + (long)width);
         return $this$commonWriteHexadecimalUnsignedLong;
      }
   }

   @NotNull
   public static final Segment commonWritableSegment(@NotNull Buffer $this$commonWritableSegment, int minimumCapacity) {
      Intrinsics.checkNotNullParameter($this$commonWritableSegment, "<this>");
      int $i$f$commonWritableSegment = 0;
      if (minimumCapacity < 1 || minimumCapacity > 8192) {
         int var4 = 0;
         String var6 = "unexpected capacity";
         throw new IllegalArgumentException(var6.toString());
      } else if ($this$commonWritableSegment.head == null) {
         Segment result = SegmentPool.take();
         $this$commonWritableSegment.head = result;
         result.prev = result;
         result.next = result;
         return result;
      } else {
         Segment var10000 = $this$commonWritableSegment.head;
         Intrinsics.checkNotNull(var10000);
         Segment tail = var10000.prev;
         Intrinsics.checkNotNull(tail);
         if (tail.limit + minimumCapacity > 8192 || !tail.owner) {
            tail = tail.push(SegmentPool.take());
         }

         return tail;
      }
   }

   @NotNull
   public static final Buffer commonWrite(@NotNull Buffer $this$commonWrite, @NotNull byte[] source) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      return $this$commonWrite.write((byte[])source, 0, source.length);
   }

   @NotNull
   public static final Buffer commonWrite(@NotNull Buffer $this$commonWrite, @NotNull byte[] source, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      int offset = offset;
      okio.-SegmentedByteString.checkOffsetAndCount((long)source.length, (long)offset, (long)byteCount);

      Segment tail;
      int toCopy;
      for(int limit = offset + byteCount; offset < limit; tail.limit += toCopy) {
         tail = $this$commonWrite.writableSegment$okio(1);
         toCopy = Math.min(limit - offset, 8192 - tail.limit);
         ArraysKt.copyInto(source, tail.data, tail.limit, offset, offset + toCopy);
         offset += toCopy;
      }

      $this$commonWrite.setSize$okio($this$commonWrite.size() + (long)byteCount);
      return $this$commonWrite;
   }

   @NotNull
   public static final byte[] commonReadByteArray(@NotNull Buffer $this$commonReadByteArray) {
      Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
      int $i$f$commonReadByteArray = 0;
      return $this$commonReadByteArray.readByteArray($this$commonReadByteArray.size());
   }

   @NotNull
   public static final byte[] commonReadByteArray(@NotNull Buffer $this$commonReadByteArray, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadByteArray, "<this>");
      int $i$f$commonReadByteArray = 0;
      if (byteCount < 0L || byteCount > 2147483647L) {
         int var5 = 0;
         String var6 = "byteCount: " + byteCount;
         throw new IllegalArgumentException(var6.toString());
      } else if ($this$commonReadByteArray.size() < byteCount) {
         throw new EOFException();
      } else {
         byte[] result = new byte[(int)byteCount];
         $this$commonReadByteArray.readFully(result);
         return result;
      }
   }

   public static final int commonRead(@NotNull Buffer $this$commonRead, @NotNull byte[] sink) {
      Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      return $this$commonRead.read(sink, 0, sink.length);
   }

   public static final void commonReadFully(@NotNull Buffer $this$commonReadFully, @NotNull byte[] sink) {
      Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadFully = 0;

      int read;
      for(int offset = 0; offset < sink.length; offset += read) {
         read = $this$commonReadFully.read(sink, offset, sink.length - offset);
         if (read == -1) {
            throw new EOFException();
         }
      }

   }

   public static final int commonRead(@NotNull Buffer $this$commonRead, @NotNull byte[] sink, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      okio.-SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
      Segment var10000 = $this$commonRead.head;
      if (var10000 == null) {
         return -1;
      } else {
         Segment s = var10000;
         int toCopy = Math.min(byteCount, s.limit - s.pos);
         ArraysKt.copyInto(s.data, sink, offset, s.pos, s.pos + toCopy);
         s.pos += toCopy;
         $this$commonRead.setSize$okio($this$commonRead.size() - (long)toCopy);
         if (s.pos == s.limit) {
            $this$commonRead.head = s.pop();
            SegmentPool.recycle(s);
         }

         return toCopy;
      }
   }

   public static final long commonReadDecimalLong(@NotNull Buffer $this$commonReadDecimalLong) {
      Intrinsics.checkNotNullParameter($this$commonReadDecimalLong, "<this>");
      int $i$f$commonReadDecimalLong = 0;
      if ($this$commonReadDecimalLong.size() == 0L) {
         throw new EOFException();
      } else {
         long value = 0L;
         int seen = 0;
         boolean negative = false;
         boolean done = false;
         long overflowDigit = -7L;

         while(true) {
            Segment var10000 = $this$commonReadDecimalLong.head;
            Intrinsics.checkNotNull(var10000);
            Segment segment = var10000;
            byte[] data = segment.data;
            int pos = segment.pos;
            int limit = segment.limit;

            while(true) {
               label86: {
                  if (pos < limit) {
                     byte b = data[pos];
                     if (b >= 48 && b <= 57) {
                        int digit = 48 - b;
                        if (value < -922337203685477580L || value == -922337203685477580L && (long)digit < overflowDigit) {
                           Buffer buffer = (new Buffer()).writeDecimalLong(value).writeByte(b);
                           if (!negative) {
                              buffer.readByte();
                           }

                           throw new NumberFormatException("Number too large: " + buffer.readUtf8());
                        }

                        value *= 10L;
                        value += (long)digit;
                        break label86;
                     }

                     if (b == 45 && seen == 0) {
                        negative = true;
                        --overflowDigit;
                        break label86;
                     }

                     done = true;
                  }

                  if (pos == limit) {
                     $this$commonReadDecimalLong.head = segment.pop();
                     SegmentPool.recycle(segment);
                  } else {
                     segment.pos = pos;
                  }

                  if (!done && $this$commonReadDecimalLong.head != null) {
                     break;
                  }

                  $this$commonReadDecimalLong.setSize$okio($this$commonReadDecimalLong.size() - (long)seen);
                  int minimumSeen = negative ? 2 : 1;
                  if (seen < minimumSeen) {
                     if ($this$commonReadDecimalLong.size() == 0L) {
                        throw new EOFException();
                     }

                     String expected = negative ? "Expected a digit" : "Expected a digit or '-'";
                     throw new NumberFormatException(expected + " but was 0x" + okio.-SegmentedByteString.toHexString($this$commonReadDecimalLong.getByte(0L)));
                  }

                  return negative ? value : -value;
               }

               ++pos;
               ++seen;
            }
         }
      }
   }

   public static final long commonReadHexadecimalUnsignedLong(@NotNull Buffer $this$commonReadHexadecimalUnsignedLong) {
      Intrinsics.checkNotNullParameter($this$commonReadHexadecimalUnsignedLong, "<this>");
      int $i$f$commonReadHexadecimalUnsignedLong = 0;
      if ($this$commonReadHexadecimalUnsignedLong.size() == 0L) {
         throw new EOFException();
      } else {
         long value = 0L;
         int seen = 0;
         boolean done = false;

         do {
            Segment var10000 = $this$commonReadHexadecimalUnsignedLong.head;
            Intrinsics.checkNotNull(var10000);
            Segment segment = var10000;
            byte[] data = segment.data;
            int pos = segment.pos;

            int limit;
            for(limit = segment.limit; pos < limit; ++seen) {
               int digit = 0;
               byte b = data[pos];
               if (b >= 48 && b <= 57) {
                  digit = b - 48;
               } else if (b >= 97 && b <= 102) {
                  digit = b - 97 + 10;
               } else {
                  if (b < 65 || b > 70) {
                     if (seen == 0) {
                        throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + okio.-SegmentedByteString.toHexString(b));
                     }

                     done = true;
                     break;
                  }

                  digit = b - 65 + 10;
               }

               if ((value & -1152921504606846976L) != 0L) {
                  Buffer buffer = (new Buffer()).writeHexadecimalUnsignedLong(value).writeByte(b);
                  throw new NumberFormatException("Number too large: " + buffer.readUtf8());
               }

               value <<= 4;
               value |= (long)digit;
               ++pos;
            }

            if (pos == limit) {
               $this$commonReadHexadecimalUnsignedLong.head = segment.pop();
               SegmentPool.recycle(segment);
            } else {
               segment.pos = pos;
            }
         } while(!done && $this$commonReadHexadecimalUnsignedLong.head != null);

         $this$commonReadHexadecimalUnsignedLong.setSize$okio($this$commonReadHexadecimalUnsignedLong.size() - (long)seen);
         return value;
      }
   }

   @NotNull
   public static final ByteString commonReadByteString(@NotNull Buffer $this$commonReadByteString) {
      Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
      int $i$f$commonReadByteString = 0;
      return $this$commonReadByteString.readByteString($this$commonReadByteString.size());
   }

   @NotNull
   public static final ByteString commonReadByteString(@NotNull Buffer $this$commonReadByteString, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadByteString, "<this>");
      int $i$f$commonReadByteString = 0;
      if (byteCount < 0L || byteCount > 2147483647L) {
         int var5 = 0;
         String it = "byteCount: " + byteCount;
         throw new IllegalArgumentException(it.toString());
      } else if ($this$commonReadByteString.size() < byteCount) {
         throw new EOFException();
      } else if (byteCount >= 4096L) {
         ByteString var4 = $this$commonReadByteString.snapshot((int)byteCount);
         int var6 = 0;
         $this$commonReadByteString.skip(byteCount);
         return var4;
      } else {
         return new ByteString($this$commonReadByteString.readByteArray(byteCount));
      }
   }

   public static final int commonSelect(@NotNull Buffer $this$commonSelect, @NotNull Options options) {
      Intrinsics.checkNotNullParameter($this$commonSelect, "<this>");
      Intrinsics.checkNotNullParameter(options, "options");
      int $i$f$commonSelect = 0;
      int index = selectPrefix$default($this$commonSelect, options, false, 2, (Object)null);
      if (index == -1) {
         return -1;
      } else {
         int selectedSize = options.getByteStrings$okio()[index].size();
         $this$commonSelect.skip((long)selectedSize);
         return index;
      }
   }

   public static final void commonReadFully(@NotNull Buffer $this$commonReadFully, @NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadFully, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadFully = 0;
      if ($this$commonReadFully.size() < byteCount) {
         sink.write($this$commonReadFully, $this$commonReadFully.size());
         throw new EOFException();
      } else {
         sink.write($this$commonReadFully, byteCount);
      }
   }

   public static final long commonReadAll(@NotNull Buffer $this$commonReadAll, @NotNull Sink sink) {
      Intrinsics.checkNotNullParameter($this$commonReadAll, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadAll = 0;
      long byteCount = $this$commonReadAll.size();
      if (byteCount > 0L) {
         sink.write($this$commonReadAll, byteCount);
      }

      return byteCount;
   }

   @NotNull
   public static final String commonReadUtf8(@NotNull Buffer $this$commonReadUtf8, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8, "<this>");
      int $i$f$commonReadUtf8 = 0;
      if (byteCount < 0L || byteCount > 2147483647L) {
         int result = 0;
         String var7 = "byteCount: " + byteCount;
         throw new IllegalArgumentException(var7.toString());
      } else if ($this$commonReadUtf8.size() < byteCount) {
         throw new EOFException();
      } else if (byteCount == 0L) {
         return "";
      } else {
         Segment var10000 = $this$commonReadUtf8.head;
         Intrinsics.checkNotNull(var10000);
         Segment s = var10000;
         if ((long)s.pos + byteCount > (long)s.limit) {
            return _Utf8Kt.commonToUtf8String$default($this$commonReadUtf8.readByteArray(byteCount), 0, 0, 3, (Object)null);
         } else {
            String result = _Utf8Kt.commonToUtf8String(s.data, s.pos, s.pos + (int)byteCount);
            s.pos += (int)byteCount;
            $this$commonReadUtf8.setSize$okio($this$commonReadUtf8.size() - byteCount);
            if (s.pos == s.limit) {
               $this$commonReadUtf8.head = s.pop();
               SegmentPool.recycle(s);
            }

            return result;
         }
      }
   }

   @Nullable
   public static final String commonReadUtf8Line(@NotNull Buffer $this$commonReadUtf8Line) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8Line, "<this>");
      int $i$f$commonReadUtf8Line = 0;
      long newline = $this$commonReadUtf8Line.indexOf((byte)10);
      return newline != -1L ? readUtf8Line($this$commonReadUtf8Line, newline) : ($this$commonReadUtf8Line.size() != 0L ? $this$commonReadUtf8Line.readUtf8($this$commonReadUtf8Line.size()) : null);
   }

   @NotNull
   public static final String commonReadUtf8LineStrict(@NotNull Buffer $this$commonReadUtf8LineStrict, long limit) {
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
            return readUtf8Line($this$commonReadUtf8LineStrict, newline);
         } else if (scanLength < $this$commonReadUtf8LineStrict.size() && $this$commonReadUtf8LineStrict.getByte(scanLength - 1L) == 13 && $this$commonReadUtf8LineStrict.getByte(scanLength) == 10) {
            return readUtf8Line($this$commonReadUtf8LineStrict, scanLength);
         } else {
            Buffer data = new Buffer();
            byte a$iv = 32;
            long b$iv = $this$commonReadUtf8LineStrict.size();
            int $i$f$minOf = 0;
            $this$commonReadUtf8LineStrict.copyTo(data, 0L, Math.min((long)a$iv, b$iv));
            throw new EOFException("\\n not found: limit=" + Math.min($this$commonReadUtf8LineStrict.size(), limit) + " content=" + data.readByteString().hex() + '…');
         }
      }
   }

   public static final int commonReadUtf8CodePoint(@NotNull Buffer $this$commonReadUtf8CodePoint) {
      Intrinsics.checkNotNullParameter($this$commonReadUtf8CodePoint, "<this>");
      int $i$f$commonReadUtf8CodePoint = 0;
      if ($this$commonReadUtf8CodePoint.size() == 0L) {
         throw new EOFException();
      } else {
         byte b0 = $this$commonReadUtf8CodePoint.getByte(0L);
         int codePoint = 0;
         int byteCount = 0;
         int min = 0;
         int other$iv = 128;
         int $i$f$and = 0;
         if ((b0 & other$iv) == 0) {
            other$iv = 127;
            $i$f$and = 0;
            codePoint = b0 & other$iv;
            byteCount = 1;
            min = 0;
         } else {
            other$iv = 224;
            $i$f$and = 0;
            if ((b0 & other$iv) == 192) {
               other$iv = 31;
               $i$f$and = 0;
               codePoint = b0 & other$iv;
               byteCount = 2;
               min = 128;
            } else {
               other$iv = 240;
               $i$f$and = 0;
               if ((b0 & other$iv) == 224) {
                  other$iv = 15;
                  $i$f$and = 0;
                  codePoint = b0 & other$iv;
                  byteCount = 3;
                  min = 2048;
               } else {
                  other$iv = 248;
                  $i$f$and = 0;
                  if ((b0 & other$iv) != 240) {
                     $this$commonReadUtf8CodePoint.skip(1L);
                     return 65533;
                  }

                  other$iv = 7;
                  $i$f$and = 0;
                  codePoint = b0 & other$iv;
                  byteCount = 4;
                  min = 65536;
               }
            }
         }

         if ($this$commonReadUtf8CodePoint.size() < (long)byteCount) {
            throw new EOFException("size < " + byteCount + ": " + $this$commonReadUtf8CodePoint.size() + " (to read code point prefixed 0x" + okio.-SegmentedByteString.toHexString(b0) + ')');
         } else {
            for(int i = 1; i < byteCount; ++i) {
               other$iv = $this$commonReadUtf8CodePoint.getByte((long)i);
               int other$iv = 192;
               int $i$f$and = 0;
               if ((other$iv & other$iv) != 128) {
                  $this$commonReadUtf8CodePoint.skip((long)i);
                  return 65533;
               }

               codePoint <<= 6;
               other$iv = 63;
               $i$f$and = 0;
               codePoint |= other$iv & other$iv;
            }

            $this$commonReadUtf8CodePoint.skip((long)byteCount);
            return codePoint > 1114111 ? '�' : ((55296 <= codePoint ? codePoint < 57344 : false) ? '�' : (codePoint < min ? '�' : codePoint));
         }
      }
   }

   @NotNull
   public static final Buffer commonWriteUtf8(@NotNull Buffer $this$commonWriteUtf8, @NotNull String string, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$commonWriteUtf8, "<this>");
      Intrinsics.checkNotNullParameter(string, "string");
      int $i$f$commonWriteUtf8 = 0;
      if (beginIndex < 0) {
         int var17 = 0;
         String var18 = "beginIndex < 0: " + beginIndex;
         throw new IllegalArgumentException(var18.toString());
      } else if (endIndex < beginIndex) {
         int var15 = 0;
         String var16 = "endIndex < beginIndex: " + endIndex + " < " + beginIndex;
         throw new IllegalArgumentException(var16.toString());
      } else if (endIndex > string.length()) {
         int var13 = 0;
         String c = "endIndex > string.length: " + endIndex + " > " + string.length();
         throw new IllegalArgumentException(c.toString());
      } else {
         int i = beginIndex;

         while(i < endIndex) {
            int c = string.charAt(i);
            if (c < 128) {
               Segment tail = $this$commonWriteUtf8.writableSegment$okio(1);
               byte[] data = tail.data;
               int segmentOffset = tail.limit - i;
               int runLimit = Math.min(endIndex, 8192 - segmentOffset);

               for(data[segmentOffset + i++] = (byte)c; i < runLimit; data[segmentOffset + i++] = (byte)c) {
                  c = string.charAt(i);
                  if (c >= 128) {
                     break;
                  }
               }

               int runSize = i + segmentOffset - tail.limit;
               tail.limit += runSize;
               $this$commonWriteUtf8.setSize$okio($this$commonWriteUtf8.size() + (long)runSize);
            } else if (c < 2048) {
               Segment tail = $this$commonWriteUtf8.writableSegment$okio(2);
               tail.data[tail.limit] = (byte)(c >> 6 | 192);
               tail.data[tail.limit + 1] = (byte)(c & 63 | 128);
               tail.limit += 2;
               $this$commonWriteUtf8.setSize$okio($this$commonWriteUtf8.size() + 2L);
               ++i;
            } else if (c >= 55296 && c <= 57343) {
               int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
               if (c <= 56319 && (56320 <= low ? low < 57344 : false)) {
                  int codePoint = 65536 + ((c & 1023) << 10 | low & 1023);
                  Segment tail = $this$commonWriteUtf8.writableSegment$okio(4);
                  tail.data[tail.limit] = (byte)(codePoint >> 18 | 240);
                  tail.data[tail.limit + 1] = (byte)(codePoint >> 12 & 63 | 128);
                  tail.data[tail.limit + 2] = (byte)(codePoint >> 6 & 63 | 128);
                  tail.data[tail.limit + 3] = (byte)(codePoint & 63 | 128);
                  tail.limit += 4;
                  $this$commonWriteUtf8.setSize$okio($this$commonWriteUtf8.size() + 4L);
                  i += 2;
               } else {
                  $this$commonWriteUtf8.writeByte(63);
                  ++i;
               }
            } else {
               Segment tail = $this$commonWriteUtf8.writableSegment$okio(3);
               tail.data[tail.limit] = (byte)(c >> 12 | 224);
               tail.data[tail.limit + 1] = (byte)(c >> 6 & 63 | 128);
               tail.data[tail.limit + 2] = (byte)(c & 63 | 128);
               tail.limit += 3;
               $this$commonWriteUtf8.setSize$okio($this$commonWriteUtf8.size() + 3L);
               ++i;
            }
         }

         return $this$commonWriteUtf8;
      }
   }

   @NotNull
   public static final Buffer commonWriteUtf8CodePoint(@NotNull Buffer $this$commonWriteUtf8CodePoint, int codePoint) {
      Intrinsics.checkNotNullParameter($this$commonWriteUtf8CodePoint, "<this>");
      int $i$f$commonWriteUtf8CodePoint = 0;
      if (codePoint < 128) {
         $this$commonWriteUtf8CodePoint.writeByte(codePoint);
      } else if (codePoint < 2048) {
         Segment tail = $this$commonWriteUtf8CodePoint.writableSegment$okio(2);
         tail.data[tail.limit] = (byte)(codePoint >> 6 | 192);
         tail.data[tail.limit + 1] = (byte)(codePoint & 63 | 128);
         tail.limit += 2;
         $this$commonWriteUtf8CodePoint.setSize$okio($this$commonWriteUtf8CodePoint.size() + 2L);
      } else if (55296 <= codePoint ? codePoint < 57344 : false) {
         $this$commonWriteUtf8CodePoint.writeByte(63);
      } else if (codePoint < 65536) {
         Segment tail = $this$commonWriteUtf8CodePoint.writableSegment$okio(3);
         tail.data[tail.limit] = (byte)(codePoint >> 12 | 224);
         tail.data[tail.limit + 1] = (byte)(codePoint >> 6 & 63 | 128);
         tail.data[tail.limit + 2] = (byte)(codePoint & 63 | 128);
         tail.limit += 3;
         $this$commonWriteUtf8CodePoint.setSize$okio($this$commonWriteUtf8CodePoint.size() + 3L);
      } else {
         if (codePoint > 1114111) {
            throw new IllegalArgumentException("Unexpected code point: 0x" + okio.-SegmentedByteString.toHexString(codePoint));
         }

         Segment tail = $this$commonWriteUtf8CodePoint.writableSegment$okio(4);
         tail.data[tail.limit] = (byte)(codePoint >> 18 | 240);
         tail.data[tail.limit + 1] = (byte)(codePoint >> 12 & 63 | 128);
         tail.data[tail.limit + 2] = (byte)(codePoint >> 6 & 63 | 128);
         tail.data[tail.limit + 3] = (byte)(codePoint & 63 | 128);
         tail.limit += 4;
         $this$commonWriteUtf8CodePoint.setSize$okio($this$commonWriteUtf8CodePoint.size() + 4L);
      }

      return $this$commonWriteUtf8CodePoint;
   }

   public static final long commonWriteAll(@NotNull Buffer $this$commonWriteAll, @NotNull Source source) {
      Intrinsics.checkNotNullParameter($this$commonWriteAll, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWriteAll = 0;
      long totalBytesRead = 0L;

      while(true) {
         long readCount = source.read($this$commonWriteAll, 8192L);
         if (readCount == -1L) {
            return totalBytesRead;
         }

         totalBytesRead += readCount;
      }
   }

   @NotNull
   public static final Buffer commonWrite(@NotNull Buffer $this$commonWrite, @NotNull Source source, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;

      long read;
      for(long byteCount = byteCount; byteCount > 0L; byteCount -= read) {
         read = source.read($this$commonWrite, byteCount);
         if (read == -1L) {
            throw new EOFException();
         }
      }

      return $this$commonWrite;
   }

   @NotNull
   public static final Buffer commonWriteByte(@NotNull Buffer $this$commonWriteByte, int b) {
      Intrinsics.checkNotNullParameter($this$commonWriteByte, "<this>");
      int $i$f$commonWriteByte = 0;
      Segment tail = $this$commonWriteByte.writableSegment$okio(1);
      byte[] var10000 = tail.data;
      int var4 = tail.limit++;
      var10000[var4] = (byte)b;
      $this$commonWriteByte.setSize$okio($this$commonWriteByte.size() + 1L);
      return $this$commonWriteByte;
   }

   @NotNull
   public static final Buffer commonWriteShort(@NotNull Buffer $this$commonWriteShort, int s) {
      Intrinsics.checkNotNullParameter($this$commonWriteShort, "<this>");
      int $i$f$commonWriteShort = 0;
      Segment tail = $this$commonWriteShort.writableSegment$okio(2);
      byte[] data = tail.data;
      int limit = tail.limit;
      data[limit++] = (byte)(s >>> 8 & 255);
      data[limit++] = (byte)(s & 255);
      tail.limit = limit;
      $this$commonWriteShort.setSize$okio($this$commonWriteShort.size() + 2L);
      return $this$commonWriteShort;
   }

   @NotNull
   public static final Buffer commonWriteInt(@NotNull Buffer $this$commonWriteInt, int i) {
      Intrinsics.checkNotNullParameter($this$commonWriteInt, "<this>");
      int $i$f$commonWriteInt = 0;
      Segment tail = $this$commonWriteInt.writableSegment$okio(4);
      byte[] data = tail.data;
      int limit = tail.limit;
      data[limit++] = (byte)(i >>> 24 & 255);
      data[limit++] = (byte)(i >>> 16 & 255);
      data[limit++] = (byte)(i >>> 8 & 255);
      data[limit++] = (byte)(i & 255);
      tail.limit = limit;
      $this$commonWriteInt.setSize$okio($this$commonWriteInt.size() + 4L);
      return $this$commonWriteInt;
   }

   @NotNull
   public static final Buffer commonWriteLong(@NotNull Buffer $this$commonWriteLong, long v) {
      Intrinsics.checkNotNullParameter($this$commonWriteLong, "<this>");
      int $i$f$commonWriteLong = 0;
      Segment tail = $this$commonWriteLong.writableSegment$okio(8);
      byte[] data = tail.data;
      int limit = tail.limit;
      data[limit++] = (byte)((int)(v >>> 56 & 255L));
      data[limit++] = (byte)((int)(v >>> 48 & 255L));
      data[limit++] = (byte)((int)(v >>> 40 & 255L));
      data[limit++] = (byte)((int)(v >>> 32 & 255L));
      data[limit++] = (byte)((int)(v >>> 24 & 255L));
      data[limit++] = (byte)((int)(v >>> 16 & 255L));
      data[limit++] = (byte)((int)(v >>> 8 & 255L));
      data[limit++] = (byte)((int)(v & 255L));
      tail.limit = limit;
      $this$commonWriteLong.setSize$okio($this$commonWriteLong.size() + 8L);
      return $this$commonWriteLong;
   }

   public static final void commonWrite(@NotNull Buffer $this$commonWrite, @NotNull Buffer source, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      long byteCount = byteCount;
      if (source == $this$commonWrite) {
         int movedByteCount = 0;
         String var13 = "source == this";
         throw new IllegalArgumentException(var13.toString());
      } else {
         okio.-SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);

         while(byteCount > 0L) {
            Segment var10001 = source.head;
            Intrinsics.checkNotNull(var10001);
            int var18 = var10001.limit;
            Segment var10002 = source.head;
            Intrinsics.checkNotNull(var10002);
            if (byteCount < (long)(var18 - var10002.pos)) {
               Segment var15;
               if ($this$commonWrite.head != null) {
                  var15 = $this$commonWrite.head;
                  Intrinsics.checkNotNull(var15);
                  var15 = var15.prev;
               } else {
                  var15 = null;
               }

               Segment tail = var15;
               if (tail != null && tail.owner && byteCount + (long)tail.limit - (long)(tail.shared ? 0 : tail.pos) <= 8192L) {
                  var15 = source.head;
                  Intrinsics.checkNotNull(var15);
                  var15.writeTo(tail, (int)byteCount);
                  source.setSize$okio(source.size() - byteCount);
                  $this$commonWrite.setSize$okio($this$commonWrite.size() + byteCount);
                  return;
               }

               Segment var19 = source.head;
               Intrinsics.checkNotNull(var19);
               source.head = var19.split((int)byteCount);
            }

            Segment segmentToMove = source.head;
            Intrinsics.checkNotNull(segmentToMove);
            long movedByteCount = (long)(segmentToMove.limit - segmentToMove.pos);
            source.head = segmentToMove.pop();
            if ($this$commonWrite.head == null) {
               $this$commonWrite.head = segmentToMove;
               segmentToMove.prev = segmentToMove;
               segmentToMove.next = segmentToMove.prev;
            } else {
               Segment var16 = $this$commonWrite.head;
               Intrinsics.checkNotNull(var16);
               Segment tail = var16.prev;
               Intrinsics.checkNotNull(tail);
               tail = tail.push(segmentToMove);
               tail.compact();
            }

            source.setSize$okio(source.size() - movedByteCount);
            $this$commonWrite.setSize$okio($this$commonWrite.size() + movedByteCount);
            byteCount -= movedByteCount;
         }

      }
   }

   public static final long commonRead(@NotNull Buffer $this$commonRead, @NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRead, "<this>");
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      long byteCount = 0L;
      byteCount = byteCount;
      if (byteCount < 0L) {
         int var5 = 0;
         String var8 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var8.toString());
      } else if ($this$commonRead.size() == 0L) {
         return -1L;
      } else {
         if (byteCount > $this$commonRead.size()) {
            byteCount = $this$commonRead.size();
         }

         sink.write($this$commonRead, byteCount);
         return byteCount;
      }
   }

   public static final long commonIndexOf(@NotNull Buffer $this$commonIndexOf, byte b, long fromIndex, long toIndex) {
      Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
      int $i$f$commonIndexOf = 0;
      long fromIndex = 0L;
      fromIndex = fromIndex;
      long toIndex = 0L;
      toIndex = toIndex;
      if (!(0L <= fromIndex ? fromIndex <= toIndex : false)) {
         int fromIndex$iv = 0;
         String var40 = "size=" + $this$commonIndexOf.size() + " fromIndex=" + fromIndex + " toIndex=" + toIndex;
         throw new IllegalArgumentException(var40.toString());
      } else {
         if (toIndex > $this$commonIndexOf.size()) {
            toIndex = $this$commonIndexOf.size();
         }

         if (fromIndex == toIndex) {
            return -1L;
         } else {
            long fromIndex$iv = fromIndex;
            int $i$f$seek = 0;
            Segment var10000 = $this$commonIndexOf.head;
            if (var10000 == null) {
               long var11 = -1L;
               Segment s = null;
               int var41 = 0;
               return -1L;
            } else {
               Segment s$iv = var10000;
               if ($this$commonIndexOf.size() - fromIndex < fromIndex) {
                  long offset$iv;
                  for(offset$iv = $this$commonIndexOf.size(); offset$iv > fromIndex$iv; offset$iv -= (long)(s$iv.limit - s$iv.pos)) {
                     var10000 = s$iv.prev;
                     Intrinsics.checkNotNull(var10000);
                     s$iv = var10000;
                  }

                  int var14 = 0;
                  if (s$iv == null) {
                     return -1L;
                  } else {
                     Segment s = s$iv;

                     for(long offset = offset$iv; offset < toIndex; s = var10000) {
                        byte[] data = s.data;
                        int limit = (int)Math.min((long)s.limit, (long)s.pos + toIndex - offset);

                        for(int pos = (int)((long)s.pos + fromIndex - offset); pos < limit; ++pos) {
                           if (data[pos] == b) {
                              return (long)(pos - s.pos) + offset;
                           }
                        }

                        offset += (long)(s.limit - s.pos);
                        fromIndex = offset;
                        var10000 = s.next;
                        Intrinsics.checkNotNull(var10000);
                     }

                     return -1L;
                  }
               } else {
                  long offset$iv = 0L;

                  while(true) {
                     long nextOffset$iv = offset$iv + (long)(s$iv.limit - s$iv.pos);
                     if (nextOffset$iv > fromIndex$iv) {
                        int var29 = 0;
                        if (s$iv == null) {
                           return -1L;
                        } else {
                           Segment s = s$iv;

                           for(long offset = offset$iv; offset < toIndex; s = var10000) {
                              byte[] data = s.data;
                              int limit = (int)Math.min((long)s.limit, (long)s.pos + toIndex - offset);

                              for(int pos = (int)((long)s.pos + fromIndex - offset); pos < limit; ++pos) {
                                 if (data[pos] == b) {
                                    return (long)(pos - s.pos) + offset;
                                 }
                              }

                              offset += (long)(s.limit - s.pos);
                              fromIndex = offset;
                              var10000 = s.next;
                              Intrinsics.checkNotNull(var10000);
                           }

                           return -1L;
                        }
                     }

                     var10000 = s$iv.next;
                     Intrinsics.checkNotNull(var10000);
                     s$iv = var10000;
                     offset$iv = nextOffset$iv;
                  }
               }
            }
         }
      }
   }

   public static final long commonIndexOf(@NotNull Buffer $this$commonIndexOf, @NotNull ByteString bytes, long fromIndex, long toIndex, int bytesOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonIndexOf, "<this>");
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      okio.-SegmentedByteString.checkOffsetAndCount((long)bytes.size(), (long)bytesOffset, (long)byteCount);
      if (byteCount <= 0) {
         int var42 = 0;
         String var43 = "byteCount == 0";
         throw new IllegalArgumentException(var43.toString());
      } else if (fromIndex < 0L) {
         int var40 = 0;
         String var41 = "fromIndex < 0: " + fromIndex;
         throw new IllegalArgumentException(var41.toString());
      } else if (fromIndex > toIndex) {
         int var8 = 0;
         String var39 = "fromIndex > toIndex: " + fromIndex + " > " + toIndex;
         throw new IllegalArgumentException(var39.toString());
      } else {
         long fromIndex = 0L;
         fromIndex = fromIndex;
         long toIndex = 0L;
         toIndex = toIndex;
         if (toIndex > $this$commonIndexOf.size()) {
            toIndex = $this$commonIndexOf.size();
         }

         if (fromIndex == toIndex) {
            return -1L;
         } else {
            long fromIndex$iv = fromIndex;
            int $i$f$seek = 0;
            Segment var10000 = $this$commonIndexOf.head;
            if (var10000 == null) {
               long var13 = -1L;
               Segment s = null;
               int var45 = 0;
               return -1L;
            } else {
               Segment s$iv = var10000;
               if ($this$commonIndexOf.size() - fromIndex < fromIndex) {
                  long offset$iv;
                  for(offset$iv = $this$commonIndexOf.size(); offset$iv > fromIndex$iv; offset$iv -= (long)(s$iv.limit - s$iv.pos)) {
                     var10000 = s$iv.prev;
                     Intrinsics.checkNotNull(var10000);
                     s$iv = var10000;
                  }

                  int var44 = 0;
                  if (s$iv == null) {
                     return -1L;
                  } else {
                     Segment s = s$iv;
                     long offset = offset$iv;
                     byte[] targetByteArray = bytes.internalArray$okio();
                     byte b0 = targetByteArray[bytesOffset];

                     for(long resultLimit = Math.min(toIndex, $this$commonIndexOf.size() - (long)byteCount + 1L); offset < resultLimit; s = var10000) {
                        byte[] data = s.data;
                        int pos = s.limit;
                        long b$iv = (long)s.pos + resultLimit - offset;
                        int $i$f$minOf = 0;
                        int segmentLimit = (int)Math.min((long)pos, b$iv);

                        for(int pos = (int)((long)s.pos + fromIndex - offset); pos < segmentLimit; ++pos) {
                           if (data[pos] == b0 && rangeEquals(s, pos + 1, targetByteArray, bytesOffset + 1, byteCount)) {
                              return (long)(pos - s.pos) + offset;
                           }
                        }

                        offset += (long)(s.limit - s.pos);
                        fromIndex = offset;
                        var10000 = s.next;
                        Intrinsics.checkNotNull(var10000);
                     }

                     return -1L;
                  }
               } else {
                  long offset$iv = 0L;

                  while(true) {
                     long nextOffset$iv = offset$iv + (long)(s$iv.limit - s$iv.pos);
                     if (nextOffset$iv > fromIndex$iv) {
                        int var16 = 0;
                        if (s$iv == null) {
                           return -1L;
                        } else {
                           Segment s = s$iv;
                           long offset = offset$iv;
                           byte[] targetByteArray = bytes.internalArray$okio();
                           byte b0 = targetByteArray[bytesOffset];

                           for(long resultLimit = Math.min(toIndex, $this$commonIndexOf.size() - (long)byteCount + 1L); offset < resultLimit; s = var10000) {
                              byte[] data = s.data;
                              int a$iv = s.limit;
                              long b$iv = (long)s.pos + resultLimit - offset;
                              int $i$f$minOf = 0;
                              int segmentLimit = (int)Math.min((long)a$iv, b$iv);

                              for(int pos = (int)((long)s.pos + fromIndex - offset); pos < segmentLimit; ++pos) {
                                 if (data[pos] == b0 && rangeEquals(s, pos + 1, targetByteArray, bytesOffset + 1, byteCount)) {
                                    return (long)(pos - s.pos) + offset;
                                 }
                              }

                              offset += (long)(s.limit - s.pos);
                              fromIndex = offset;
                              var10000 = s.next;
                              Intrinsics.checkNotNull(var10000);
                           }

                           return -1L;
                        }
                     }

                     var10000 = s$iv.next;
                     Intrinsics.checkNotNull(var10000);
                     s$iv = var10000;
                     offset$iv = nextOffset$iv;
                  }
               }
            }
         }
      }
   }

   // $FF: synthetic method
   public static long commonIndexOf$default(Buffer var0, ByteString var1, long var2, long var4, int var6, int var7, int var8, Object var9) {
      if ((var8 & 4) != 0) {
         var4 = Long.MAX_VALUE;
      }

      if ((var8 & 8) != 0) {
         var6 = 0;
      }

      if ((var8 & 16) != 0) {
         var7 = var1.size();
      }

      return commonIndexOf(var0, var1, var2, var4, var6, var7);
   }

   public static final long commonIndexOfElement(@NotNull Buffer $this$commonIndexOfElement, @NotNull ByteString targetBytes, long fromIndex) {
      Intrinsics.checkNotNullParameter($this$commonIndexOfElement, "<this>");
      Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
      int $i$f$commonIndexOfElement = 0;
      long fromIndex = 0L;
      fromIndex = fromIndex;
      if (fromIndex < 0L) {
         int fromIndex$iv = 0;
         String var41 = "fromIndex < 0: " + fromIndex;
         throw new IllegalArgumentException(var41.toString());
      } else {
         long fromIndex$iv = fromIndex;
         int $i$f$seek = 0;
         Segment var10000 = $this$commonIndexOfElement.head;
         if (var10000 == null) {
            long var9 = -1L;
            Segment s = null;
            int var42 = 0;
            return -1L;
         } else {
            Segment s$iv = var10000;
            if ($this$commonIndexOfElement.size() - fromIndex < fromIndex) {
               long offset$iv;
               for(offset$iv = $this$commonIndexOfElement.size(); offset$iv > fromIndex$iv; offset$iv -= (long)(s$iv.limit - s$iv.pos)) {
                  var10000 = s$iv.prev;
                  Intrinsics.checkNotNull(var10000);
                  s$iv = var10000;
               }

               int var12 = 0;
               if (s$iv == null) {
                  return -1L;
               } else {
                  Segment s = s$iv;
                  long offset = offset$iv;
                  if (targetBytes.size() != 2) {
                     for(byte[] targetByteArray = targetBytes.internalArray$okio(); offset < $this$commonIndexOfElement.size(); s = var10000) {
                        byte[] data = s.data;
                        int pos = (int)((long)s.pos + fromIndex - offset);

                        for(int limit = s.limit; pos < limit; ++pos) {
                           int b = data[pos];
                           int var56 = 0;

                           for(int var58 = targetByteArray.length; var56 < var58; ++var56) {
                              byte t = targetByteArray[var56];
                              if (b == t) {
                                 return (long)(pos - s.pos) + offset;
                              }
                           }
                        }

                        offset += (long)(s.limit - s.pos);
                        fromIndex = offset;
                        var10000 = s.next;
                        Intrinsics.checkNotNull(var10000);
                     }
                  } else {
                     byte b0 = targetBytes.getByte(0);

                     for(byte b1 = targetBytes.getByte(1); offset < $this$commonIndexOfElement.size(); s = var10000) {
                        byte[] data = s.data;
                        int pos = (int)((long)s.pos + fromIndex - offset);

                        for(int limit = s.limit; pos < limit; ++pos) {
                           int b = data[pos];
                           if (b == b0 || b == b1) {
                              return (long)(pos - s.pos) + offset;
                           }
                        }

                        offset += (long)(s.limit - s.pos);
                        fromIndex = offset;
                        var10000 = s.next;
                        Intrinsics.checkNotNull(var10000);
                     }
                  }

                  return -1L;
               }
            } else {
               long offset$iv = 0L;

               while(true) {
                  long nextOffset$iv = offset$iv + (long)(s$iv.limit - s$iv.pos);
                  if (nextOffset$iv > fromIndex$iv) {
                     int var32 = 0;
                     if (s$iv == null) {
                        return -1L;
                     } else {
                        Segment s = s$iv;
                        long offset = offset$iv;
                        if (targetBytes.size() != 2) {
                           for(byte[] targetByteArray = targetBytes.internalArray$okio(); offset < $this$commonIndexOfElement.size(); s = var10000) {
                              byte[] data = s.data;
                              int pos = (int)((long)s.pos + fromIndex - offset);

                              for(int limit = s.limit; pos < limit; ++pos) {
                                 int b = data[pos];
                                 int b = 0;

                                 for(int var36 = targetByteArray.length; b < var36; ++b) {
                                    byte t = targetByteArray[b];
                                    if (b == t) {
                                       return (long)(pos - s.pos) + offset;
                                    }
                                 }
                              }

                              offset += (long)(s.limit - s.pos);
                              fromIndex = offset;
                              var10000 = s.next;
                              Intrinsics.checkNotNull(var10000);
                           }
                        } else {
                           byte b0 = targetBytes.getByte(0);

                           for(byte b1 = targetBytes.getByte(1); offset < $this$commonIndexOfElement.size(); s = var10000) {
                              byte[] data = s.data;
                              int pos = (int)((long)s.pos + fromIndex - offset);

                              for(int limit = s.limit; pos < limit; ++pos) {
                                 int b = data[pos];
                                 if (b == b0 || b == b1) {
                                    return (long)(pos - s.pos) + offset;
                                 }
                              }

                              offset += (long)(s.limit - s.pos);
                              fromIndex = offset;
                              var10000 = s.next;
                              Intrinsics.checkNotNull(var10000);
                           }
                        }

                        return -1L;
                     }
                  }

                  var10000 = s$iv.next;
                  Intrinsics.checkNotNull(var10000);
                  s$iv = var10000;
                  offset$iv = nextOffset$iv;
               }
            }
         }
      }
   }

   public static final boolean commonRangeEquals(@NotNull Buffer $this$commonRangeEquals, long offset, @NotNull ByteString bytes, int bytesOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      int $i$f$commonRangeEquals = 0;
      if (byteCount < 0) {
         return false;
      } else if (offset >= 0L && offset + (long)byteCount <= $this$commonRangeEquals.size()) {
         if (bytesOffset >= 0 && bytesOffset + byteCount <= bytes.size()) {
            if (byteCount == 0) {
               return true;
            } else {
               long var7 = offset + 1L;
               return commonIndexOf($this$commonRangeEquals, bytes, offset, var7, bytesOffset, byteCount) != -1L;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static final boolean commonEquals(@NotNull Buffer $this$commonEquals, @Nullable Object other) {
      Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
      int $i$f$commonEquals = 0;
      if ($this$commonEquals == other) {
         return true;
      } else if (!(other instanceof Buffer)) {
         return false;
      } else if ($this$commonEquals.size() != ((Buffer)other).size()) {
         return false;
      } else if ($this$commonEquals.size() == 0L) {
         return true;
      } else {
         Segment var10000 = $this$commonEquals.head;
         Intrinsics.checkNotNull(var10000);
         Segment sa = var10000;
         var10000 = ((Buffer)other).head;
         Intrinsics.checkNotNull(var10000);
         Segment sb = var10000;
         int posA = sa.pos;
         int posB = sb.pos;
         long pos = 0L;

         long count;
         for(count = 0L; pos < $this$commonEquals.size(); pos += count) {
            count = (long)Math.min(sa.limit - posA, sb.limit - posB);
            long i = 0L;

            for(long var13 = count; i < var13; ++i) {
               if (sa.data[posA++] != sb.data[posB++]) {
                  return false;
               }
            }

            if (posA == sa.limit) {
               var10000 = sa.next;
               Intrinsics.checkNotNull(var10000);
               sa = var10000;
               posA = sa.pos;
            }

            if (posB == sb.limit) {
               var10000 = sb.next;
               Intrinsics.checkNotNull(var10000);
               sb = var10000;
               posB = sb.pos;
            }
         }

         return true;
      }
   }

   public static final int commonHashCode(@NotNull Buffer $this$commonHashCode) {
      Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
      int $i$f$commonHashCode = 0;
      Segment var10000 = $this$commonHashCode.head;
      if (var10000 == null) {
         return 0;
      } else {
         Segment s = var10000;
         int result = 1;

         do {
            int pos = s.pos;

            for(int limit = s.limit; pos < limit; ++pos) {
               result = 31 * result + s.data[pos];
            }

            var10000 = s.next;
            Intrinsics.checkNotNull(var10000);
            s = var10000;
         } while(s != $this$commonHashCode.head);

         return result;
      }
   }

   @NotNull
   public static final Buffer commonCopy(@NotNull Buffer $this$commonCopy) {
      Intrinsics.checkNotNullParameter($this$commonCopy, "<this>");
      int $i$f$commonCopy = 0;
      Buffer result = new Buffer();
      if ($this$commonCopy.size() == 0L) {
         return result;
      } else {
         Segment var10000 = $this$commonCopy.head;
         Intrinsics.checkNotNull(var10000);
         Segment head = var10000;
         Segment headCopy = head.sharedCopy();
         result.head = headCopy;
         headCopy.prev = result.head;
         headCopy.next = headCopy.prev;

         for(Segment s = head.next; s != head; s = s.next) {
            var10000 = headCopy.prev;
            Intrinsics.checkNotNull(var10000);
            Intrinsics.checkNotNull(s);
            var10000.push(s.sharedCopy());
         }

         result.setSize$okio($this$commonCopy.size());
         return result;
      }
   }

   @NotNull
   public static final ByteString commonSnapshot(@NotNull Buffer $this$commonSnapshot) {
      Intrinsics.checkNotNullParameter($this$commonSnapshot, "<this>");
      int $i$f$commonSnapshot = 0;
      if ($this$commonSnapshot.size() > 2147483647L) {
         int var2 = 0;
         String var3 = "size > Int.MAX_VALUE: " + $this$commonSnapshot.size();
         throw new IllegalStateException(var3.toString());
      } else {
         return $this$commonSnapshot.snapshot((int)$this$commonSnapshot.size());
      }
   }

   @NotNull
   public static final ByteString commonSnapshot(@NotNull Buffer $this$commonSnapshot, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonSnapshot, "<this>");
      int $i$f$commonSnapshot = 0;
      if (byteCount == 0) {
         return ByteString.EMPTY;
      } else {
         okio.-SegmentedByteString.checkOffsetAndCount($this$commonSnapshot.size(), 0L, (long)byteCount);
         int offset = 0;
         int segmentCount = 0;

         for(Segment s = $this$commonSnapshot.head; offset < byteCount; s = s.next) {
            Intrinsics.checkNotNull(s);
            if (s.limit == s.pos) {
               throw new AssertionError("s.limit == s.pos");
            }

            offset += s.limit - s.pos;
            ++segmentCount;
         }

         byte[][] segments = new byte[segmentCount][];
         int[] directory = new int[segmentCount * 2];
         offset = 0;
         segmentCount = 0;

         for(Segment var10 = $this$commonSnapshot.head; offset < byteCount; var10 = var10.next) {
            Intrinsics.checkNotNull(var10);
            segments[segmentCount] = var10.data;
            offset += var10.limit - var10.pos;
            directory[segmentCount] = Math.min(offset, byteCount);
            directory[segmentCount + ((Object[])segments).length] = var10.pos;
            var10.shared = true;
            ++segmentCount;
         }

         return new SegmentedByteString(segments, directory);
      }
   }

   @NotNull
   public static final Buffer.UnsafeCursor commonReadUnsafe(@NotNull Buffer $this$commonReadUnsafe, @NotNull Buffer.UnsafeCursor unsafeCursor) {
      Intrinsics.checkNotNullParameter($this$commonReadUnsafe, "<this>");
      Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
      Buffer.UnsafeCursor unsafeCursor = okio.-SegmentedByteString.resolveDefaultParameter(unsafeCursor);
      if (unsafeCursor.buffer != null) {
         int var3 = 0;
         String var4 = "already attached to a buffer";
         throw new IllegalStateException(var4.toString());
      } else {
         unsafeCursor.buffer = $this$commonReadUnsafe;
         unsafeCursor.readWrite = false;
         return unsafeCursor;
      }
   }

   @NotNull
   public static final Buffer.UnsafeCursor commonReadAndWriteUnsafe(@NotNull Buffer $this$commonReadAndWriteUnsafe, @NotNull Buffer.UnsafeCursor unsafeCursor) {
      Intrinsics.checkNotNullParameter($this$commonReadAndWriteUnsafe, "<this>");
      Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
      Buffer.UnsafeCursor unsafeCursor = okio.-SegmentedByteString.resolveDefaultParameter(unsafeCursor);
      if (unsafeCursor.buffer != null) {
         int var3 = 0;
         String var4 = "already attached to a buffer";
         throw new IllegalStateException(var4.toString());
      } else {
         unsafeCursor.buffer = $this$commonReadAndWriteUnsafe;
         unsafeCursor.readWrite = true;
         return unsafeCursor;
      }
   }

   public static final int commonNext(@NotNull Buffer.UnsafeCursor $this$commonNext) {
      Intrinsics.checkNotNullParameter($this$commonNext, "<this>");
      int $i$f$commonNext = 0;
      long var10000 = $this$commonNext.offset;
      Buffer var10001 = $this$commonNext.buffer;
      Intrinsics.checkNotNull(var10001);
      if (var10000 == var10001.size()) {
         int var2 = 0;
         String var3 = "no more bytes";
         throw new IllegalStateException(var3.toString());
      } else {
         return $this$commonNext.offset == -1L ? $this$commonNext.seek(0L) : $this$commonNext.seek($this$commonNext.offset + (long)($this$commonNext.end - $this$commonNext.start));
      }
   }

   public static final int commonSeek(@NotNull Buffer.UnsafeCursor $this$commonSeek, long offset) {
      Intrinsics.checkNotNullParameter($this$commonSeek, "<this>");
      int $i$f$commonSeek = 0;
      Buffer var10000 = $this$commonSeek.buffer;
      if (var10000 == null) {
         int var6 = 0;
         String var15 = "not attached to a buffer";
         throw new IllegalStateException(var15.toString());
      } else {
         Buffer buffer = var10000;
         if (offset >= -1L && offset <= buffer.size()) {
            if (offset != -1L && offset != buffer.size()) {
               long min = 0L;
               long max = buffer.size();
               Segment head = buffer.head;
               Segment tail = buffer.head;
               if ($this$commonSeek.getSegment$okio() != null) {
                  long var19 = $this$commonSeek.offset;
                  int var10001 = $this$commonSeek.start;
                  Segment var10002 = $this$commonSeek.getSegment$okio();
                  Intrinsics.checkNotNull(var10002);
                  long segmentOffset = var19 - (long)(var10001 - var10002.pos);
                  if (segmentOffset > offset) {
                     max = segmentOffset;
                     tail = $this$commonSeek.getSegment$okio();
                  } else {
                     min = segmentOffset;
                     head = $this$commonSeek.getSegment$okio();
                  }
               }

               Segment next = null;
               long nextOffset = 0L;
               if (max - offset > offset - min) {
                  next = head;
                  nextOffset = min;

                  while(true) {
                     Intrinsics.checkNotNull(next);
                     if (offset < nextOffset + (long)(next.limit - next.pos)) {
                        break;
                     }

                     nextOffset += (long)(next.limit - next.pos);
                     next = next.next;
                  }
               } else {
                  next = tail;

                  for(nextOffset = max; nextOffset > offset; nextOffset -= (long)(next.limit - next.pos)) {
                     Intrinsics.checkNotNull(next);
                     next = next.prev;
                     Intrinsics.checkNotNull(next);
                  }
               }

               if ($this$commonSeek.readWrite) {
                  Intrinsics.checkNotNull(next);
                  if (next.shared) {
                     Segment unsharedNext = next.unsharedCopy();
                     if (buffer.head == next) {
                        buffer.head = unsharedNext;
                     }

                     next = next.push(unsharedNext);
                     Segment var20 = next.prev;
                     Intrinsics.checkNotNull(var20);
                     var20.pop();
                  }
               }

               $this$commonSeek.setSegment$okio(next);
               $this$commonSeek.offset = offset;
               Intrinsics.checkNotNull(next);
               $this$commonSeek.data = next.data;
               $this$commonSeek.start = next.pos + (int)(offset - nextOffset);
               $this$commonSeek.end = next.limit;
               return $this$commonSeek.end - $this$commonSeek.start;
            } else {
               $this$commonSeek.setSegment$okio((Segment)null);
               $this$commonSeek.offset = offset;
               $this$commonSeek.data = null;
               $this$commonSeek.start = -1;
               $this$commonSeek.end = -1;
               return -1;
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("offset=" + offset + " > size=" + buffer.size());
         }
      }
   }

   public static final long commonResizeBuffer(@NotNull Buffer.UnsafeCursor $this$commonResizeBuffer, long newSize) {
      Intrinsics.checkNotNullParameter($this$commonResizeBuffer, "<this>");
      int $i$f$commonResizeBuffer = 0;
      Buffer var10000 = $this$commonResizeBuffer.buffer;
      if (var10000 == null) {
         int var17 = 0;
         String var18 = "not attached to a buffer";
         throw new IllegalStateException(var18.toString());
      } else {
         Buffer buffer = var10000;
         if (!$this$commonResizeBuffer.readWrite) {
            int var6 = 0;
            String var16 = "resizeBuffer() only permitted for read/write buffers";
            throw new IllegalStateException(var16.toString());
         } else {
            long oldSize = buffer.size();
            if (newSize <= oldSize) {
               if (newSize < 0L) {
                  int var8 = 0;
                  String bytesToAdd = "newSize < 0: " + newSize;
                  throw new IllegalArgumentException(bytesToAdd.toString());
               }

               int tailSize;
               for(long bytesToSubtract = oldSize - newSize; bytesToSubtract > 0L; bytesToSubtract -= (long)tailSize) {
                  Segment var23 = buffer.head;
                  Intrinsics.checkNotNull(var23);
                  Segment tail = var23.prev;
                  Intrinsics.checkNotNull(tail);
                  tailSize = tail.limit - tail.pos;
                  if ((long)tailSize > bytesToSubtract) {
                     tail.limit -= (int)bytesToSubtract;
                     break;
                  }

                  buffer.head = tail.pop();
                  SegmentPool.recycle(tail);
               }

               $this$commonResizeBuffer.setSegment$okio((Segment)null);
               $this$commonResizeBuffer.offset = newSize;
               $this$commonResizeBuffer.data = null;
               $this$commonResizeBuffer.start = -1;
               $this$commonResizeBuffer.end = -1;
            } else if (newSize > oldSize) {
               boolean needsToSeek = true;
               long bytesToAdd = newSize - oldSize;

               while(bytesToAdd > 0L) {
                  Segment tail = buffer.writableSegment$okio(1);
                  int b$iv = 8192 - tail.limit;
                  int $i$f$minOf = 0;
                  int segmentBytesToAdd = (int)Math.min(bytesToAdd, (long)b$iv);
                  tail.limit += segmentBytesToAdd;
                  bytesToAdd -= (long)segmentBytesToAdd;
                  if (needsToSeek) {
                     $this$commonResizeBuffer.setSegment$okio(tail);
                     $this$commonResizeBuffer.offset = oldSize;
                     $this$commonResizeBuffer.data = tail.data;
                     $this$commonResizeBuffer.start = tail.limit - segmentBytesToAdd;
                     $this$commonResizeBuffer.end = tail.limit;
                     needsToSeek = false;
                  }
               }
            }

            buffer.setSize$okio(newSize);
            return oldSize;
         }
      }
   }

   public static final long commonExpandBuffer(@NotNull Buffer.UnsafeCursor $this$commonExpandBuffer, int minByteCount) {
      Intrinsics.checkNotNullParameter($this$commonExpandBuffer, "<this>");
      int $i$f$commonExpandBuffer = 0;
      if (minByteCount <= 0) {
         int var10 = 0;
         String var11 = "minByteCount <= 0: " + minByteCount;
         throw new IllegalArgumentException(var11.toString());
      } else if (minByteCount > 8192) {
         int var8 = 0;
         String oldSize = "minByteCount > Segment.SIZE: " + minByteCount;
         throw new IllegalArgumentException(oldSize.toString());
      } else {
         Buffer var10000 = $this$commonExpandBuffer.buffer;
         if (var10000 == null) {
            int var13 = 0;
            String var14 = "not attached to a buffer";
            throw new IllegalStateException(var14.toString());
         } else {
            Buffer buffer = var10000;
            if (!$this$commonExpandBuffer.readWrite) {
               int var5 = 0;
               String var12 = "expandBuffer() only permitted for read/write buffers";
               throw new IllegalStateException(var12.toString());
            } else {
               long oldSize = buffer.size();
               Segment tail = buffer.writableSegment$okio(minByteCount);
               int result = 8192 - tail.limit;
               tail.limit = 8192;
               buffer.setSize$okio(oldSize + (long)result);
               $this$commonExpandBuffer.setSegment$okio(tail);
               $this$commonExpandBuffer.offset = oldSize;
               $this$commonExpandBuffer.data = tail.data;
               $this$commonExpandBuffer.start = 8192 - result;
               $this$commonExpandBuffer.end = 8192;
               return (long)result;
            }
         }
      }
   }

   public static final void commonClose(@NotNull Buffer.UnsafeCursor $this$commonClose) {
      Intrinsics.checkNotNullParameter($this$commonClose, "<this>");
      int $i$f$commonClose = 0;
      if ($this$commonClose.buffer == null) {
         int var2 = 0;
         String var3 = "not attached to a buffer";
         throw new IllegalStateException(var3.toString());
      } else {
         $this$commonClose.buffer = null;
         $this$commonClose.setSegment$okio((Segment)null);
         $this$commonClose.offset = -1L;
         $this$commonClose.data = null;
         $this$commonClose.start = -1;
         $this$commonClose.end = -1;
      }
   }

   // $FF: synthetic method
   public static final int access$countDigitsIn(long v) {
      return countDigitsIn(v);
   }

   static {
      long[] var0 = new long[]{-1L, 9L, 99L, 999L, 9999L, 99999L, 999999L, 9999999L, 99999999L, 999999999L, 9999999999L, 99999999999L, 999999999999L, 9999999999999L, 99999999999999L, 999999999999999L, 9999999999999999L, 99999999999999999L, 999999999999999999L, Long.MAX_VALUE};
      DigitCountToLargestValue = var0;
   }
}
