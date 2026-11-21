package okio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u0015\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u001f\b\u0000\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u000fH\u0016J\b\u0010\u0013\u001a\u00020\u000fH\u0016J\b\u0010\u0014\u001a\u00020\u0001H\u0016J\b\u0010\u0015\u001a\u00020\u0001H\u0016J\u0015\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u000fH\u0010¢\u0006\u0002\b\u0018J\u001d\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\u0001H\u0010¢\u0006\u0002\b\u001bJ\b\u0010\u001c\u001a\u00020\u000fH\u0016J\u001c\u0010\u001d\u001a\u00020\u00012\b\b\u0002\u0010\u001e\u001a\u00020\u001f2\b\b\u0002\u0010 \u001a\u00020\u001fH\u0016J\u0015\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u001fH\u0010¢\u0006\u0002\b$J\r\u0010%\u001a\u00020\u001fH\u0010¢\u0006\u0002\b&J\b\u0010'\u001a\u00020\u0004H\u0016J\b\u0010(\u001a\u00020)H\u0016J\u0010\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-H\u0016J%\u0010*\u001a\u00020+2\u0006\u0010.\u001a\u00020/2\u0006\u00100\u001a\u00020\u001f2\u0006\u00101\u001a\u00020\u001fH\u0010¢\u0006\u0002\b2J(\u00103\u001a\u0002042\u0006\u00100\u001a\u00020\u001f2\u0006\u00105\u001a\u00020\u00012\u0006\u00106\u001a\u00020\u001f2\u0006\u00101\u001a\u00020\u001fH\u0016J(\u00103\u001a\u0002042\u0006\u00100\u001a\u00020\u001f2\u0006\u00105\u001a\u00020\u00042\u0006\u00106\u001a\u00020\u001f2\u0006\u00101\u001a\u00020\u001fH\u0016J,\u00107\u001a\u00020+2\b\b\u0002\u00100\u001a\u00020\u001f2\u0006\u00108\u001a\u00020\u00042\b\b\u0002\u00109\u001a\u00020\u001f2\u0006\u00101\u001a\u00020\u001fH\u0016J\u001a\u0010:\u001a\u00020\u001f2\u0006\u00105\u001a\u00020\u00042\b\b\u0002\u0010;\u001a\u00020\u001fH\u0016J\u001a\u0010<\u001a\u00020\u001f2\u0006\u00105\u001a\u00020\u00042\b\b\u0002\u0010;\u001a\u00020\u001fH\u0016J\b\u0010=\u001a\u00020\u0001H\u0002J\r\u0010>\u001a\u00020\u0004H\u0010¢\u0006\u0002\b?J\u0013\u0010@\u001a\u0002042\b\u00105\u001a\u0004\u0018\u00010AH\u0096\u0002J\b\u0010B\u001a\u00020\u001fH\u0016J\b\u0010C\u001a\u00020\u000fH\u0016J\b\u0010D\u001a\u00020EH\u0002R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0080\u0004¢\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006F"},
   d2 = {"Lokio/SegmentedByteString;", "Lokio/ByteString;", "segments", "", "", "directory", "", "<init>", "([[B[I)V", "getSegments$okio", "()[[B", "[[B", "getDirectory$okio", "()[I", "string", "", "charset", "Ljava/nio/charset/Charset;", "base64", "hex", "toAsciiLowercase", "toAsciiUppercase", "digest", "algorithm", "digest$okio", "hmac", "key", "hmac$okio", "base64Url", "substring", "beginIndex", "", "endIndex", "internalGet", "", "pos", "internalGet$okio", "getSize", "getSize$okio", "toByteArray", "asByteBuffer", "Ljava/nio/ByteBuffer;", "write", "", "out", "Ljava/io/OutputStream;", "buffer", "Lokio/Buffer;", "offset", "byteCount", "write$okio", "rangeEquals", "", "other", "otherOffset", "copyInto", "target", "targetOffset", "indexOf", "fromIndex", "lastIndexOf", "toByteString", "internalArray", "internalArray$okio", "equals", "", "hashCode", "toString", "writeReplace", "Ljava/lang/Object;", "okio"}
)
@SourceDebugExtension({"SMAP\nSegmentedByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SegmentedByteString.kt\nokio/SegmentedByteString\n+ 2 SegmentedByteString.kt\nokio/internal/-SegmentedByteString\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,140:1\n63#2,12:141\n63#2,12:153\n104#2,2:165\n106#2,26:168\n135#2,5:194\n142#2:199\n145#2,3:200\n63#2,8:203\n148#2,8:211\n71#2,4:219\n156#2:223\n63#2,12:224\n160#2:236\n85#2,10:237\n161#2,9:247\n95#2,4:256\n170#2,2:260\n179#2,4:262\n85#2,10:266\n183#2,3:276\n95#2,4:279\n186#2:283\n195#2,8:284\n85#2,10:292\n203#2,3:302\n95#2,4:305\n206#2:309\n215#2,5:310\n85#2,10:315\n220#2,3:325\n95#2,4:328\n223#2:332\n226#2,4:333\n234#2,6:337\n63#2,8:343\n240#2,7:351\n71#2,4:358\n247#2,2:362\n1#3:167\n*S KotlinDebug\n*F\n+ 1 SegmentedByteString.kt\nokio/SegmentedByteString\n*L\n54#1:141,12\n66#1:153,12\n78#1:165,2\n78#1:168,26\n80#1:194,5\n82#1:199\n84#1:200,3\n84#1:203,8\n84#1:211,8\n84#1:219,4\n84#1:223\n90#1:224,12\n96#1:236\n96#1:237,10\n96#1:247,9\n96#1:256,4\n96#1:260,2\n103#1:262,4\n103#1:266,10\n103#1:276,3\n103#1:279,4\n103#1:283\n110#1:284,8\n110#1:292,10\n110#1:302,3\n110#1:305,4\n110#1:309\n117#1:310,5\n117#1:315,10\n117#1:325,3\n117#1:328,4\n117#1:332\n131#1:333,4\n133#1:337,6\n133#1:343,8\n133#1:351,7\n133#1:358,4\n133#1:362,2\n78#1:167\n*E\n"})
public final class SegmentedByteString extends ByteString {
   @NotNull
   private final transient byte[][] segments;
   @NotNull
   private final transient int[] directory;

   public SegmentedByteString(@NotNull byte[][] segments, @NotNull int[] directory) {
      Intrinsics.checkNotNullParameter(segments, "segments");
      Intrinsics.checkNotNullParameter(directory, "directory");
      super(ByteString.EMPTY.getData$okio());
      this.segments = segments;
      this.directory = directory;
   }

   @NotNull
   public final byte[][] getSegments$okio() {
      return this.segments;
   }

   @NotNull
   public final int[] getDirectory$okio() {
      return this.directory;
   }

   @NotNull
   public String string(@NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(charset, "charset");
      return this.toByteString().string(charset);
   }

   @NotNull
   public String base64() {
      return this.toByteString().base64();
   }

   @NotNull
   public String hex() {
      return this.toByteString().hex();
   }

   @NotNull
   public ByteString toAsciiLowercase() {
      return this.toByteString().toAsciiLowercase();
   }

   @NotNull
   public ByteString toAsciiUppercase() {
      return this.toByteString().toAsciiUppercase();
   }

   @NotNull
   public ByteString digest$okio(@NotNull String algorithm) {
      Intrinsics.checkNotNullParameter(algorithm, "algorithm");
      MessageDigest $this$digest_u24lambda_u240 = MessageDigest.getInstance(algorithm);
      int var4 = 0;
      SegmentedByteString $this$forEachSegment$iv = this;
      int $i$f$forEachSegment = 0;
      int segmentCount$iv = ((Object[])this.getSegments$okio()).length;
      int s$iv = 0;

      for(int pos$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
         int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
         int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
         byte[] var10000 = $this$forEachSegment$iv.getSegments$okio()[s$iv];
         int byteCount = nextSegmentOffset$iv - pos$iv;
         byte[] data = var10000;
         int var15 = 0;
         $this$digest_u24lambda_u240.update(data, segmentPos$iv, byteCount);
         pos$iv = nextSegmentOffset$iv;
      }

      byte[] digestBytes = $this$digest_u24lambda_u240.digest();
      Intrinsics.checkNotNull(digestBytes);
      return new ByteString(digestBytes);
   }

   @NotNull
   public ByteString hmac$okio(@NotNull String algorithm, @NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(algorithm, "algorithm");
      Intrinsics.checkNotNullParameter(key, "key");

      try {
         Mac mac = Mac.getInstance(algorithm);
         mac.init((Key)(new SecretKeySpec(key.toByteArray(), algorithm)));
         SegmentedByteString $this$forEachSegment$iv = this;
         int $i$f$forEachSegment = 0;
         int segmentCount$iv = ((Object[])this.getSegments$okio()).length;
         int s$iv = 0;

         for(int pos$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
            int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
            int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
            byte[] var10000 = $this$forEachSegment$iv.getSegments$okio()[s$iv];
            int byteCount = nextSegmentOffset$iv - pos$iv;
            byte[] data = var10000;
            int var14 = 0;
            mac.update(data, segmentPos$iv, byteCount);
            pos$iv = nextSegmentOffset$iv;
         }

         byte[] var10002 = mac.doFinal();
         Intrinsics.checkNotNullExpressionValue(var10002, "doFinal(...)");
         return new ByteString(var10002);
      } catch (InvalidKeyException e) {
         throw new IllegalArgumentException((Throwable)e);
      }
   }

   @NotNull
   public String base64Url() {
      return this.toByteString().base64Url();
   }

   @NotNull
   public ByteString substring(int beginIndex, int endIndex) {
      SegmentedByteString $this$commonSubstring$iv = this;
      int beginIndex$iv = beginIndex;
      int $i$f$commonSubstring = 0;
      int endIndex$iv = -SegmentedByteString.resolveDefaultParameter(this, endIndex);
      if (beginIndex < 0) {
         int var18 = 0;
         String var19 = "beginIndex=" + beginIndex + " < 0";
         throw new IllegalArgumentException(var19.toString());
      } else if (endIndex$iv > this.size()) {
         int var16 = 0;
         String beginSegment$iv = "endIndex=" + endIndex$iv + " > length(" + this.size() + ')';
         throw new IllegalArgumentException(beginSegment$iv.toString());
      } else {
         int subLen$iv = endIndex$iv - beginIndex;
         if (subLen$iv < 0) {
            int endSegment$iv = 0;
            String var21 = "endIndex=" + endIndex$iv + " < beginIndex=" + beginIndex;
            throw new IllegalArgumentException(var21.toString());
         } else {
            ByteString var10000;
            if (beginIndex == 0 && endIndex$iv == this.size()) {
               var10000 = this;
            } else if (beginIndex == endIndex$iv) {
               var10000 = ByteString.EMPTY;
            } else {
               int beginSegment$iv = okio.internal.-SegmentedByteString.segment(this, beginIndex);
               int endSegment$iv = okio.internal.-SegmentedByteString.segment(this, endIndex$iv - 1);
               byte[][] newSegments$iv = (byte[][])ArraysKt.copyOfRange(this.getSegments$okio(), beginSegment$iv, endSegment$iv + 1);
               int[] newDirectory$iv = new int[((Object[])newSegments$iv).length * 2];
               int index$iv = 0;
               int s$iv = beginSegment$iv;
               if (beginSegment$iv <= endSegment$iv) {
                  while(true) {
                     newDirectory$iv[index$iv] = Math.min($this$commonSubstring$iv.getDirectory$okio()[s$iv] - beginIndex$iv, subLen$iv);
                     newDirectory$iv[index$iv++ + ((Object[])newSegments$iv).length] = $this$commonSubstring$iv.getDirectory$okio()[s$iv + ((Object[])$this$commonSubstring$iv.getSegments$okio()).length];
                     if (s$iv == endSegment$iv) {
                        break;
                     }

                     ++s$iv;
                  }
               }

               s$iv = beginSegment$iv == 0 ? 0 : $this$commonSubstring$iv.getDirectory$okio()[beginSegment$iv - 1];
               int var15 = ((Object[])newSegments$iv).length;
               newDirectory$iv[var15] += beginIndex$iv - s$iv;
               var10000 = new SegmentedByteString(newSegments$iv, newDirectory$iv);
            }

            return var10000;
         }
      }
   }

   public byte internalGet$okio(int pos) {
      int $i$f$commonInternalGet = 0;
      -SegmentedByteString.checkOffsetAndCount((long)this.getDirectory$okio()[((Object[])this.getSegments$okio()).length - 1], (long)pos, 1L);
      int segment$iv = okio.internal.-SegmentedByteString.segment(this, pos);
      int segmentOffset$iv = segment$iv == 0 ? 0 : this.getDirectory$okio()[segment$iv - 1];
      int segmentPos$iv = this.getDirectory$okio()[segment$iv + ((Object[])this.getSegments$okio()).length];
      return this.getSegments$okio()[segment$iv][pos - segmentOffset$iv + segmentPos$iv];
   }

   public int getSize$okio() {
      int $i$f$commonGetSize = 0;
      return this.getDirectory$okio()[((Object[])this.getSegments$okio()).length - 1];
   }

   @NotNull
   public byte[] toByteArray() {
      int $i$f$commonToByteArray = 0;
      byte[] result$iv = new byte[this.size()];
      int resultPos$iv = 0;
      SegmentedByteString $this$forEachSegment$iv$iv = this;
      int $i$f$forEachSegment = 0;
      int segmentCount$iv$iv = ((Object[])this.getSegments$okio()).length;
      int s$iv$iv = 0;

      for(int pos$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; ++s$iv$iv) {
         int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[segmentCount$iv$iv + s$iv$iv];
         int nextSegmentOffset$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv];
         byte[] var10000 = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
         int byteCount$iv = nextSegmentOffset$iv$iv - pos$iv$iv;
         byte[] data$iv = var10000;
         int var15 = 0;
         ArraysKt.copyInto(data$iv, result$iv, resultPos$iv, segmentPos$iv$iv, segmentPos$iv$iv + byteCount$iv);
         resultPos$iv += byteCount$iv;
         pos$iv$iv = nextSegmentOffset$iv$iv;
      }

      return result$iv;
   }

   @NotNull
   public ByteBuffer asByteBuffer() {
      ByteBuffer var10000 = ByteBuffer.wrap(this.toByteArray()).asReadOnlyBuffer();
      Intrinsics.checkNotNullExpressionValue(var10000, "asReadOnlyBuffer(...)");
      return var10000;
   }

   public void write(@NotNull OutputStream out) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      SegmentedByteString $this$forEachSegment$iv = this;
      int $i$f$forEachSegment = 0;
      int segmentCount$iv = ((Object[])this.getSegments$okio()).length;
      int s$iv = 0;

      for(int pos$iv = 0; s$iv < segmentCount$iv; ++s$iv) {
         int segmentPos$iv = $this$forEachSegment$iv.getDirectory$okio()[segmentCount$iv + s$iv];
         int nextSegmentOffset$iv = $this$forEachSegment$iv.getDirectory$okio()[s$iv];
         byte[] var10000 = $this$forEachSegment$iv.getSegments$okio()[s$iv];
         int byteCount = nextSegmentOffset$iv - pos$iv;
         byte[] data = var10000;
         int var12 = 0;
         out.write(data, segmentPos$iv, byteCount);
         pos$iv = nextSegmentOffset$iv;
      }

   }

   public void write$okio(@NotNull Buffer buffer, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(buffer, "buffer");
      Buffer buffer$iv = buffer;
      int $i$f$commonWrite = 0;
      SegmentedByteString $this$forEachSegment$iv$iv = this;
      int endIndex$iv$iv = offset + byteCount;
      int $i$f$forEachSegment = 0;
      int s$iv$iv = okio.internal.-SegmentedByteString.segment(this, offset);

      for(int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; ++s$iv$iv) {
         int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
         int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
         int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
         int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
         int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
         byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
         int var23 = 0;
         Segment segment$iv = new Segment(data$iv, offset$iv$iv, offset$iv$iv + byteCount$iv$iv, true, false);
         if (buffer$iv.head == null) {
            segment$iv.prev = segment$iv;
            segment$iv.next = segment$iv.prev;
            buffer$iv.head = segment$iv.next;
         } else {
            Segment var10000 = buffer$iv.head;
            Intrinsics.checkNotNull(var10000);
            var10000 = var10000.prev;
            Intrinsics.checkNotNull(var10000);
            var10000.push(segment$iv);
         }

         pos$iv$iv += byteCount$iv$iv;
      }

      buffer$iv.setSize$okio(buffer$iv.size() + (long)byteCount);
   }

   public boolean rangeEquals(int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(other, "other");
      ByteString other$iv = other;
      int $i$f$commonRangeEquals = 0;
      boolean var10000;
      if (offset >= 0 && offset <= this.size() - byteCount) {
         int otherOffset$iv = 0;
         otherOffset$iv = otherOffset;
         SegmentedByteString $this$forEachSegment$iv$iv = this;
         int endIndex$iv$iv = offset + byteCount;
         int $i$f$forEachSegment = 0;
         int s$iv$iv = okio.internal.-SegmentedByteString.segment(this, offset);

         for(int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; ++s$iv$iv) {
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
            int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
            int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
            byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
            int var26 = 0;
            if (!other$iv.rangeEquals(otherOffset$iv, data$iv, offset$iv$iv, byteCount$iv$iv)) {
               var10000 = false;
               return var10000;
            }

            otherOffset$iv += byteCount$iv$iv;
            pos$iv$iv += byteCount$iv$iv;
         }

         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public boolean rangeEquals(int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(other, "other");
      byte[] other$iv = other;
      int $i$f$commonRangeEquals = 0;
      boolean var10000;
      if (offset >= 0 && offset <= this.size() - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount) {
         int otherOffset$iv = 0;
         otherOffset$iv = otherOffset;
         SegmentedByteString $this$forEachSegment$iv$iv = this;
         int endIndex$iv$iv = offset + byteCount;
         int $i$f$forEachSegment = 0;
         int s$iv$iv = okio.internal.-SegmentedByteString.segment(this, offset);

         for(int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; ++s$iv$iv) {
            int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
            int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
            int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
            int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
            byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
            int var26 = 0;
            if (!-SegmentedByteString.arrayRangeEquals(data$iv, offset$iv$iv, other$iv, otherOffset$iv, byteCount$iv$iv)) {
               var10000 = false;
               return var10000;
            }

            otherOffset$iv += byteCount$iv$iv;
            pos$iv$iv += byteCount$iv$iv;
         }

         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public void copyInto(int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(target, "target");
      byte[] target$iv = target;
      int $i$f$commonCopyInto = 0;
      -SegmentedByteString.checkOffsetAndCount((long)this.size(), (long)offset, (long)byteCount);
      -SegmentedByteString.checkOffsetAndCount((long)target.length, (long)targetOffset, (long)byteCount);
      int targetOffset$iv = 0;
      targetOffset$iv = targetOffset;
      SegmentedByteString $this$forEachSegment$iv$iv = this;
      int endIndex$iv$iv = offset + byteCount;
      int $i$f$forEachSegment = 0;
      int s$iv$iv = okio.internal.-SegmentedByteString.segment(this, offset);

      for(int pos$iv$iv = offset; pos$iv$iv < endIndex$iv$iv; ++s$iv$iv) {
         int segmentOffset$iv$iv = s$iv$iv == 0 ? 0 : $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv - 1];
         int segmentSize$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv] - segmentOffset$iv$iv;
         int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[((Object[])$this$forEachSegment$iv$iv.getSegments$okio()).length + s$iv$iv];
         int byteCount$iv$iv = Math.min(endIndex$iv$iv, segmentOffset$iv$iv + segmentSize$iv$iv) - pos$iv$iv;
         int offset$iv$iv = segmentPos$iv$iv + (pos$iv$iv - segmentOffset$iv$iv);
         byte[] data$iv = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
         int var26 = 0;
         ArraysKt.copyInto(data$iv, target$iv, targetOffset$iv, offset$iv$iv, offset$iv$iv + byteCount$iv$iv);
         targetOffset$iv += byteCount$iv$iv;
         pos$iv$iv += byteCount$iv$iv;
      }

   }

   public int indexOf(@NotNull byte[] other, int fromIndex) {
      Intrinsics.checkNotNullParameter(other, "other");
      return this.toByteString().indexOf(other, fromIndex);
   }

   public int lastIndexOf(@NotNull byte[] other, int fromIndex) {
      Intrinsics.checkNotNullParameter(other, "other");
      return this.toByteString().lastIndexOf(other, fromIndex);
   }

   private final ByteString toByteString() {
      return new ByteString(this.toByteArray());
   }

   @NotNull
   public byte[] internalArray$okio() {
      return this.toByteArray();
   }

   public boolean equals(@Nullable Object other) {
      int $i$f$commonEquals = 0;
      return other == this ? true : (other instanceof ByteString ? ((ByteString)other).size() == this.size() && this.rangeEquals(0, (ByteString)((ByteString)other), 0, this.size()) : false);
   }

   public int hashCode() {
      int $i$f$commonHashCode = 0;
      int result$iv = 0;
      result$iv = this.getHashCode$okio();
      int var10000;
      if (result$iv != 0) {
         var10000 = result$iv;
      } else {
         result$iv = 1;
         SegmentedByteString $this$forEachSegment$iv$iv = this;
         int $i$f$forEachSegment = 0;
         int segmentCount$iv$iv = ((Object[])this.getSegments$okio()).length;
         int s$iv$iv = 0;

         for(int pos$iv$iv = 0; s$iv$iv < segmentCount$iv$iv; ++s$iv$iv) {
            int segmentPos$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[segmentCount$iv$iv + s$iv$iv];
            int nextSegmentOffset$iv$iv = $this$forEachSegment$iv$iv.getDirectory$okio()[s$iv$iv];
            byte[] var19 = $this$forEachSegment$iv$iv.getSegments$okio()[s$iv$iv];
            int byteCount$iv = nextSegmentOffset$iv$iv - pos$iv$iv;
            byte[] data$iv = var19;
            int var14 = 0;
            int i$iv = segmentPos$iv$iv;

            for(int limit$iv = segmentPos$iv$iv + byteCount$iv; i$iv < limit$iv; ++i$iv) {
               result$iv = 31 * result$iv + data$iv[i$iv];
            }

            pos$iv$iv = nextSegmentOffset$iv$iv;
         }

         this.setHashCode$okio(result$iv);
         var10000 = result$iv;
      }

      return var10000;
   }

   @NotNull
   public String toString() {
      return this.toByteString().toString();
   }

   private final Object writeReplace() {
      ByteString var10000 = this.toByteString();
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type java.lang.Object");
      return var10000;
   }
}
