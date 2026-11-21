package okio.internal;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import okio.Segment;
import okio.SegmentedByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000P\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0000\n\u0002\b\u0002\u001a$\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0000\u001a\u0014\u0010\u0006\u001a\u00020\u0001*\u00020\u00072\u0006\u0010\b\u001a\u00020\u0001H\u0000\u001a]\u0010\t\u001a\u00020\n*\u00020\u00072K\u0010\u000b\u001aG\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0010\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\n0\fH\u0080\bø\u0001\u0000\u001aj\u0010\t\u001a\u00020\n*\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00012K\u0010\u000b\u001aG\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0010\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0011\u0012\u0013\u0012\u00110\u0001¢\u0006\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\n0\fH\u0082\b\u001a\u001d\u0010\u0015\u001a\u00020\u0016*\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u0001H\u0080\b\u001a\u0015\u0010\u0017\u001a\u00020\u0018*\u00020\u00072\u0006\u0010\b\u001a\u00020\u0001H\u0080\b\u001a\r\u0010\u0019\u001a\u00020\u0001*\u00020\u0007H\u0080\b\u001a\r\u0010\u001a\u001a\u00020\r*\u00020\u0007H\u0080\b\u001a%\u0010\u001b\u001a\u00020\n*\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0001H\u0080\b\u001a-\u0010\u001e\u001a\u00020\u001f*\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010 \u001a\u00020\u00162\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0001H\u0080\b\u001a-\u0010\u001e\u001a\u00020\u001f*\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010 \u001a\u00020\r2\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0001H\u0080\b\u001a-\u0010\"\u001a\u00020\n*\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00012\u0006\u0010#\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u0001H\u0080\b\u001a\u0017\u0010%\u001a\u00020\u001f*\u00020\u00072\b\u0010 \u001a\u0004\u0018\u00010&H\u0080\b\u001a\r\u0010'\u001a\u00020\u0001*\u00020\u0007H\u0080\b\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006("},
   d2 = {"binarySearch", "", "", "value", "fromIndex", "toIndex", "segment", "Lokio/SegmentedByteString;", "pos", "forEachSegment", "", "action", "Lkotlin/Function3;", "", "Lkotlin/ParameterName;", "name", "data", "offset", "byteCount", "beginIndex", "endIndex", "commonSubstring", "Lokio/ByteString;", "commonInternalGet", "", "commonGetSize", "commonToByteArray", "commonWrite", "buffer", "Lokio/Buffer;", "commonRangeEquals", "", "other", "otherOffset", "commonCopyInto", "target", "targetOffset", "commonEquals", "", "commonHashCode", "okio"}
)
@JvmName(
   name = "-SegmentedByteString"
)
@SourceDebugExtension({"SMAP\nSegmentedByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,250:1\n63#1,12:252\n85#1,14:264\n85#1,14:278\n85#1,14:292\n85#1,14:306\n63#1,12:320\n1#2:251\n*S KotlinDebug\n*F\n+ 1 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n*L\n147#1:252,12\n160#1:264,14\n182#1:278,14\n202#1:292,14\n219#1:306,14\n239#1:320,12\n*E\n"})
public final class -SegmentedByteString {
   public static final int binarySearch(@NotNull int[] $this$binarySearch, int value, int fromIndex, int toIndex) {
      Intrinsics.checkNotNullParameter($this$binarySearch, "<this>");
      int left = fromIndex;
      int right = toIndex - 1;

      while(left <= right) {
         int mid = left + right >>> 1;
         int midVal = $this$binarySearch[mid];
         if (midVal < value) {
            left = mid + 1;
         } else {
            if (midVal <= value) {
               return mid;
            }

            right = mid - 1;
         }
      }

      return -left - 1;
   }

   public static final int segment(@NotNull SegmentedByteString $this$segment, int pos) {
      Intrinsics.checkNotNullParameter($this$segment, "<this>");
      int i = binarySearch($this$segment.getDirectory$okio(), pos + 1, 0, ((Object[])$this$segment.getSegments$okio()).length);
      return i >= 0 ? i : ~i;
   }

   public static final void forEachSegment(@NotNull SegmentedByteString $this$forEachSegment, @NotNull Function3<byte[], ? super Integer, ? super Integer, Unit> action) {
      Intrinsics.checkNotNullParameter($this$forEachSegment, "<this>");
      Intrinsics.checkNotNullParameter(action, "action");
      int $i$f$forEachSegment = 0;
      int segmentCount = ((Object[])$this$forEachSegment.getSegments$okio()).length;
      int s = 0;

      for(int pos = 0; s < segmentCount; ++s) {
         int segmentPos = $this$forEachSegment.getDirectory$okio()[segmentCount + s];
         int nextSegmentOffset = $this$forEachSegment.getDirectory$okio()[s];
         action.invoke($this$forEachSegment.getSegments$okio()[s], segmentPos, nextSegmentOffset - pos);
         pos = nextSegmentOffset;
      }

   }

   private static final void forEachSegment(SegmentedByteString $this$forEachSegment, int beginIndex, int endIndex, Function3<byte[], ? super Integer, ? super Integer, Unit> action) {
      int $i$f$forEachSegment = 0;
      int s = segment($this$forEachSegment, beginIndex);

      for(int pos = beginIndex; pos < endIndex; ++s) {
         int segmentOffset = s == 0 ? 0 : $this$forEachSegment.getDirectory$okio()[s - 1];
         int segmentSize = $this$forEachSegment.getDirectory$okio()[s] - segmentOffset;
         int segmentPos = $this$forEachSegment.getDirectory$okio()[((Object[])$this$forEachSegment.getSegments$okio()).length + s];
         int byteCount = Math.min(endIndex, segmentOffset + segmentSize) - pos;
         int offset = segmentPos + (pos - segmentOffset);
         action.invoke($this$forEachSegment.getSegments$okio()[s], offset, byteCount);
         pos += byteCount;
      }

   }

   @NotNull
   public static final ByteString commonSubstring(@NotNull SegmentedByteString $this$commonSubstring, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$commonSubstring, "<this>");
      int $i$f$commonSubstring = 0;
      int endIndex = okio.-SegmentedByteString.resolveDefaultParameter($this$commonSubstring, endIndex);
      if (beginIndex < 0) {
         int var15 = 0;
         String var16 = "beginIndex=" + beginIndex + " < 0";
         throw new IllegalArgumentException(var16.toString());
      } else if (endIndex > $this$commonSubstring.size()) {
         int var13 = 0;
         String beginSegment = "endIndex=" + endIndex + " > length(" + $this$commonSubstring.size() + ')';
         throw new IllegalArgumentException(beginSegment.toString());
      } else {
         int subLen = endIndex - beginIndex;
         if (subLen < 0) {
            int endSegment = 0;
            String var18 = "endIndex=" + endIndex + " < beginIndex=" + beginIndex;
            throw new IllegalArgumentException(var18.toString());
         } else if (beginIndex == 0 && endIndex == $this$commonSubstring.size()) {
            return $this$commonSubstring;
         } else if (beginIndex == endIndex) {
            return ByteString.EMPTY;
         } else {
            int beginSegment = segment($this$commonSubstring, beginIndex);
            int endSegment = segment($this$commonSubstring, endIndex - 1);
            byte[][] newSegments = (byte[][])ArraysKt.copyOfRange($this$commonSubstring.getSegments$okio(), beginSegment, endSegment + 1);
            int[] newDirectory = new int[((Object[])newSegments).length * 2];
            int index = 0;
            int s = beginSegment;
            if (beginSegment <= endSegment) {
               while(true) {
                  newDirectory[index] = Math.min($this$commonSubstring.getDirectory$okio()[s] - beginIndex, subLen);
                  newDirectory[index++ + ((Object[])newSegments).length] = $this$commonSubstring.getDirectory$okio()[s + ((Object[])$this$commonSubstring.getSegments$okio()).length];
                  if (s == endSegment) {
                     break;
                  }

                  ++s;
               }
            }

            s = beginSegment == 0 ? 0 : $this$commonSubstring.getDirectory$okio()[beginSegment - 1];
            int var12 = ((Object[])newSegments).length;
            newDirectory[var12] += beginIndex - s;
            return new SegmentedByteString(newSegments, newDirectory);
         }
      }
   }

   public static final byte commonInternalGet(@NotNull SegmentedByteString $this$commonInternalGet, int pos) {
      Intrinsics.checkNotNullParameter($this$commonInternalGet, "<this>");
      int $i$f$commonInternalGet = 0;
      okio.-SegmentedByteString.checkOffsetAndCount((long)$this$commonInternalGet.getDirectory$okio()[((Object[])$this$commonInternalGet.getSegments$okio()).length - 1], (long)pos, 1L);
      int segment = segment($this$commonInternalGet, pos);
      int segmentOffset = segment == 0 ? 0 : $this$commonInternalGet.getDirectory$okio()[segment - 1];
      int segmentPos = $this$commonInternalGet.getDirectory$okio()[segment + ((Object[])$this$commonInternalGet.getSegments$okio()).length];
      return $this$commonInternalGet.getSegments$okio()[segment][pos - segmentOffset + segmentPos];
   }

   public static final int commonGetSize(@NotNull SegmentedByteString $this$commonGetSize) {
      Intrinsics.checkNotNullParameter($this$commonGetSize, "<this>");
      int $i$f$commonGetSize = 0;
      return $this$commonGetSize.getDirectory$okio()[((Object[])$this$commonGetSize.getSegments$okio()).length - 1];
   }

   @NotNull
   public static final byte[] commonToByteArray(@NotNull SegmentedByteString $this$commonToByteArray) {
      Intrinsics.checkNotNullParameter($this$commonToByteArray, "<this>");
      int $i$f$commonToByteArray = 0;
      byte[] result = new byte[$this$commonToByteArray.size()];
      int resultPos = 0;
      SegmentedByteString $this$forEachSegment$iv = $this$commonToByteArray;
      int $i$f$forEachSegment = 0;
      int segmentCount$iv = ((Object[])$this$commonToByteArray.getSegments$okio()).length;
      int s$iv = 0;

      for(int pos$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
         int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
         int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
         byte[] var10000 = $this$forEachSegment$iv.getSegments$okio()[s$iv];
         int byteCount = nextSegmentOffset$iv - pos$iv;
         byte[] data = var10000;
         int var14 = 0;
         ArraysKt.copyInto(data, result, resultPos, segmentPos$iv, segmentPos$iv + byteCount);
         resultPos += byteCount;
         pos$iv = nextSegmentOffset$iv;
      }

      return result;
   }

   public static final void commonWrite(@NotNull SegmentedByteString $this$commonWrite, @NotNull Buffer buffer, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonWrite, "<this>");
      Intrinsics.checkNotNullParameter(buffer, "buffer");
      int $i$f$commonWrite = 0;
      SegmentedByteString $this$forEachSegment$iv = $this$commonWrite;
      int endIndex$iv = offset + byteCount;
      int $i$f$forEachSegment = 0;
      int s$iv = segment($this$commonWrite, offset);

      for(int pos$iv = offset; pos$iv < endIndex$iv; ++s$iv) {
         int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
         int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
         int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
         int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
         int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
         byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
         int var19 = 0;
         Segment segment = new Segment(data, offset$iv, offset$iv + byteCount$iv, true, false);
         if (buffer.head == null) {
            segment.prev = segment;
            segment.next = segment.prev;
            buffer.head = segment.next;
         } else {
            Segment var10000 = buffer.head;
            Intrinsics.checkNotNull(var10000);
            var10000 = var10000.prev;
            Intrinsics.checkNotNull(var10000);
            var10000.push(segment);
         }

         pos$iv += byteCount$iv;
      }

      buffer.setSize$okio(buffer.size() + (long)byteCount);
   }

   public static final boolean commonRangeEquals(@NotNull SegmentedByteString $this$commonRangeEquals, int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRangeEquals = 0;
      if (offset >= 0 && offset <= $this$commonRangeEquals.size() - byteCount) {
         int otherOffset = 0;
         otherOffset = otherOffset;
         SegmentedByteString $this$forEachSegment$iv = $this$commonRangeEquals;
         int endIndex$iv = offset + byteCount;
         int $i$f$forEachSegment = 0;
         int s$iv = segment($this$commonRangeEquals, offset);

         for(int pos$iv = offset; pos$iv < endIndex$iv; ++s$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
            int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            int var21 = 0;
            if (!other.rangeEquals(otherOffset, data, offset$iv, byteCount$iv)) {
               return false;
            }

            otherOffset += byteCount$iv;
            pos$iv += byteCount$iv;
         }

         return true;
      } else {
         return false;
      }
   }

   public static final boolean commonRangeEquals(@NotNull SegmentedByteString $this$commonRangeEquals, int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonRangeEquals, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRangeEquals = 0;
      if (offset >= 0 && offset <= $this$commonRangeEquals.size() - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount) {
         int otherOffset = 0;
         otherOffset = otherOffset;
         SegmentedByteString $this$forEachSegment$iv = $this$commonRangeEquals;
         int endIndex$iv = offset + byteCount;
         int $i$f$forEachSegment = 0;
         int s$iv = segment($this$commonRangeEquals, offset);

         for(int pos$iv = offset; pos$iv < endIndex$iv; ++s$iv) {
            int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
            int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
            int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
            int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
            byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            int var21 = 0;
            if (!okio.-SegmentedByteString.arrayRangeEquals(data, offset$iv, other, otherOffset, byteCount$iv)) {
               return false;
            }

            otherOffset += byteCount$iv;
            pos$iv += byteCount$iv;
         }

         return true;
      } else {
         return false;
      }
   }

   public static final void commonCopyInto(@NotNull SegmentedByteString $this$commonCopyInto, int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
      Intrinsics.checkNotNullParameter($this$commonCopyInto, "<this>");
      Intrinsics.checkNotNullParameter(target, "target");
      int $i$f$commonCopyInto = 0;
      okio.-SegmentedByteString.checkOffsetAndCount((long)$this$commonCopyInto.size(), (long)offset, (long)byteCount);
      okio.-SegmentedByteString.checkOffsetAndCount((long)target.length, (long)targetOffset, (long)byteCount);
      int targetOffset = 0;
      targetOffset = targetOffset;
      SegmentedByteString $this$forEachSegment$iv = $this$commonCopyInto;
      int endIndex$iv = offset + byteCount;
      int $i$f$forEachSegment = 0;
      int s$iv = segment($this$commonCopyInto, offset);

      for(int pos$iv = offset; pos$iv < endIndex$iv; ++s$iv) {
         int segmentOffset$iv = s$iv == 0 ? 0 : $this$forEachSegment$iv.getDirectory$okio()[s$iv - 1];
         int segmentSize$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv] - segmentOffset$iv;
         int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv.getSegments$okio()).length + s$iv];
         int byteCount$iv = Math.min(endIndex$iv, segmentOffset$iv + segmentSize$iv) - pos$iv;
         int offset$iv = segmentPos$iv + (pos$iv - segmentOffset$iv);
         byte[] data = $this$forEachSegment$iv.getSegments$okio()[s$iv];
         int var21 = 0;
         ArraysKt.copyInto(data, target, targetOffset, offset$iv, offset$iv + byteCount$iv);
         targetOffset += byteCount$iv;
         pos$iv += byteCount$iv;
      }

   }

   public static final boolean commonEquals(@NotNull SegmentedByteString $this$commonEquals, @Nullable Object other) {
      Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
      int $i$f$commonEquals = 0;
      return other == $this$commonEquals ? true : (other instanceof ByteString ? ((ByteString)other).size() == $this$commonEquals.size() && $this$commonEquals.rangeEquals(0, (ByteString)((ByteString)other), 0, $this$commonEquals.size()) : false);
   }

   public static final int commonHashCode(@NotNull SegmentedByteString $this$commonHashCode) {
      Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
      int $i$f$commonHashCode = 0;
      int result = 0;
      result = $this$commonHashCode.getHashCode$okio();
      if (result != 0) {
         return result;
      } else {
         result = 1;
         SegmentedByteString $this$forEachSegment$iv = $this$commonHashCode;
         int $i$f$forEachSegment = 0;
         int segmentCount$iv = ((Object[])$this$commonHashCode.getSegments$okio()).length;
         int s$iv = 0;

         for(int pos$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            byte[] var10000 = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            int byteCount = nextSegmentOffset$iv - pos$iv;
            byte[] data = var10000;
            int var13 = 0;
            int i = segmentPos$iv;

            for(int limit = segmentPos$iv + byteCount; i < limit; ++i) {
               result = 31 * result + data[i];
            }

            pos$iv = nextSegmentOffset$iv;
         }

         $this$commonHashCode.setHashCode$okio(result);
         return result;
      }
   }
}
