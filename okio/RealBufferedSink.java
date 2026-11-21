package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\n\u001a\u00020\u0007H\u0016J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0012\u001a\u00020\u0017H\u0016J\u0010\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J \u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u001c\u001a\u00020\u0017H\u0016J\u0010\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u0017H\u0016J\u0018\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010 \u001a\u00020!H\u0016J(\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u001c\u001a\u00020\u00172\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\"H\u0016J \u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\"2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0012\u001a\u00020\u0017H\u0016J\u0010\u0010\u000f\u001a\u00020\u00172\u0006\u0010\u0011\u001a\u00020#H\u0016J\u0010\u0010$\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020%H\u0016J\u0018\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020%2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010&\u001a\u00020\u00012\u0006\u0010'\u001a\u00020\u0017H\u0016J\u0010\u0010(\u001a\u00020\u00012\u0006\u0010)\u001a\u00020\u0017H\u0016J\u0010\u0010*\u001a\u00020\u00012\u0006\u0010)\u001a\u00020\u0017H\u0016J\u0010\u0010+\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\u0017H\u0016J\u0010\u0010-\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\u0017H\u0016J\u0010\u0010.\u001a\u00020\u00012\u0006\u0010/\u001a\u00020\u0013H\u0016J\u0010\u00100\u001a\u00020\u00012\u0006\u0010/\u001a\u00020\u0013H\u0016J\u0010\u00101\u001a\u00020\u00012\u0006\u0010/\u001a\u00020\u0013H\u0016J\u0010\u00102\u001a\u00020\u00012\u0006\u0010/\u001a\u00020\u0013H\u0016J\b\u00103\u001a\u00020\u0001H\u0016J\b\u00104\u001a\u00020\u0001H\u0016J\b\u00105\u001a\u000206H\u0016J\b\u00107\u001a\u00020\u0010H\u0016J\b\u00108\u001a\u00020\tH\u0016J\b\u00109\u001a\u00020\u0010H\u0016J\b\u0010:\u001a\u00020;H\u0016J\b\u0010<\u001a\u00020\u001aH\u0016R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001b\u0010\n\u001a\u00020\u00078Ö\u0002X\u0096\u0004¢\u0006\f\u0012\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000e¨\u0006="},
   d2 = {"Lokio/RealBufferedSink;", "Lokio/BufferedSink;", "sink", "Lokio/Sink;", "<init>", "(Lokio/Sink;)V", "bufferField", "Lokio/Buffer;", "closed", "", "buffer", "getBuffer$annotations", "()V", "getBuffer", "()Lokio/Buffer;", "write", "", "source", "byteCount", "", "byteString", "Lokio/ByteString;", "offset", "", "writeUtf8", "string", "", "beginIndex", "endIndex", "writeUtf8CodePoint", "codePoint", "writeString", "charset", "Ljava/nio/charset/Charset;", "", "Ljava/nio/ByteBuffer;", "writeAll", "Lokio/Source;", "writeByte", "b", "writeShort", "s", "writeShortLe", "writeInt", "i", "writeIntLe", "writeLong", "v", "writeLongLe", "writeDecimalLong", "writeHexadecimalUnsignedLong", "emitCompleteSegments", "emit", "outputStream", "Ljava/io/OutputStream;", "flush", "isOpen", "close", "timeout", "Lokio/Timeout;", "toString", "okio"}
)
@SourceDebugExtension({"SMAP\nRealBufferedSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 2 RealBufferedSink.kt\nokio/internal/-RealBufferedSink\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,142:1\n51#1:146\n51#1:152\n51#1:157\n51#1:162\n51#1:167\n51#1:172\n51#1:175\n51#1:176\n51#1:180\n51#1:185\n51#1:188\n51#1:192\n51#1:201\n51#1:210\n51#1:215\n51#1:220\n51#1:225\n51#1:230\n51#1:235\n51#1:240\n51#1:245\n51#1:250\n51#1:255\n51#1:261\n51#1:267\n51#1:279\n34#2:143\n35#2:145\n36#2,2:147\n40#2:149\n41#2:151\n42#2:153\n50#2:154\n51#2:156\n52#2:158\n56#2:159\n57#2:161\n58#2:163\n66#2:164\n67#2:166\n68#2:168\n72#2:169\n73#2:171\n74#2:173\n78#2:177\n79#2:179\n80#2:181\n88#2:182\n89#2:184\n90#2:186\n94#2,3:189\n97#2,5:193\n105#2,3:198\n108#2,5:202\n116#2:207\n117#2:209\n118#2:211\n122#2:212\n123#2:214\n124#2:216\n128#2:217\n129#2:219\n130#2:221\n134#2:222\n135#2:224\n136#2:226\n140#2:227\n141#2:229\n142#2:231\n146#2:232\n147#2:234\n148#2:236\n152#2:237\n153#2:239\n154#2:241\n158#2:242\n159#2:244\n160#2:246\n164#2:247\n165#2:249\n166#2:251\n170#2:252\n171#2:254\n172#2,2:256\n177#2:258\n178#2:260\n179#2,2:262\n184#2:264\n185#2:266\n186#2,4:268\n192#2,7:272\n199#2,16:280\n216#2:296\n218#2:297\n1#3:144\n1#3:150\n1#3:155\n1#3:160\n1#3:165\n1#3:170\n1#3:174\n1#3:178\n1#3:183\n1#3:187\n1#3:208\n1#3:213\n1#3:218\n1#3:223\n1#3:228\n1#3:233\n1#3:238\n1#3:243\n1#3:248\n1#3:253\n1#3:259\n1#3:265\n*S KotlinDebug\n*F\n+ 1 RealBufferedSink.kt\nokio/RealBufferedSink\n*L\n55#1:146\n56#1:152\n58#1:157\n59#1:162\n61#1:167\n63#1:172\n67#1:175\n78#1:176\n82#1:180\n84#1:185\n88#1:188\n93#1:192\n94#1:201\n95#1:210\n96#1:215\n97#1:220\n98#1:225\n99#1:230\n100#1:235\n101#1:240\n102#1:245\n103#1:250\n104#1:255\n105#1:261\n134#1:267\n138#1:279\n55#1:143\n55#1:145\n55#1:147,2\n56#1:149\n56#1:151\n56#1:153\n58#1:154\n58#1:156\n58#1:158\n59#1:159\n59#1:161\n59#1:163\n61#1:164\n61#1:166\n61#1:168\n63#1:169\n63#1:171\n63#1:173\n82#1:177\n82#1:179\n82#1:181\n84#1:182\n84#1:184\n84#1:186\n93#1:189,3\n93#1:193,5\n94#1:198,3\n94#1:202,5\n95#1:207\n95#1:209\n95#1:211\n96#1:212\n96#1:214\n96#1:216\n97#1:217\n97#1:219\n97#1:221\n98#1:222\n98#1:224\n98#1:226\n99#1:227\n99#1:229\n99#1:231\n100#1:232\n100#1:234\n100#1:236\n101#1:237\n101#1:239\n101#1:241\n102#1:242\n102#1:244\n102#1:246\n103#1:247\n103#1:249\n103#1:251\n104#1:252\n104#1:254\n104#1:256,2\n105#1:258\n105#1:260\n105#1:262,2\n134#1:264\n134#1:266\n134#1:268,4\n138#1:272,7\n138#1:280,16\n139#1:296\n140#1:297\n55#1:144\n56#1:150\n58#1:155\n59#1:160\n61#1:165\n63#1:170\n82#1:178\n84#1:183\n95#1:208\n96#1:213\n97#1:218\n98#1:223\n99#1:228\n100#1:233\n101#1:238\n102#1:243\n103#1:248\n104#1:253\n105#1:259\n134#1:265\n*E\n"})
public final class RealBufferedSink implements BufferedSink {
   @JvmField
   @NotNull
   public final Sink sink;
   @JvmField
   @NotNull
   public final Buffer bufferField;
   @JvmField
   public boolean closed;

   public RealBufferedSink(@NotNull Sink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      super();
      this.sink = sink;
      this.bufferField = new Buffer();
   }

   @NotNull
   public Buffer getBuffer() {
      int $i$f$getBuffer = 0;
      return this.bufferField;
   }

   /** @deprecated */
   // $FF: synthetic method
   public static void getBuffer$annotations() {
   }

   @NotNull
   public Buffer buffer() {
      return this.bufferField;
   }

   public void write(@NotNull Buffer source, long byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var12 = "closed";
         throw new IllegalStateException(var12.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.write(source, byteCount);
         this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink write(@NotNull ByteString byteString) {
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int $i$f$commonWrite = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.write(byteString);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink write(@NotNull ByteString byteString, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int $i$f$commonWrite = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var12 = "closed";
         throw new IllegalStateException(var12.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.write(byteString, offset, byteCount);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeUtf8(@NotNull String string) {
      Intrinsics.checkNotNullParameter(string, "string");
      int $i$f$commonWriteUtf8 = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeUtf8(string);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeUtf8(@NotNull String string, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter(string, "string");
      int $i$f$commonWriteUtf8 = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var12 = "closed";
         throw new IllegalStateException(var12.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeUtf8(string, beginIndex, endIndex);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeUtf8CodePoint(int codePoint) {
      int $i$f$commonWriteUtf8CodePoint = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeUtf8CodePoint(codePoint);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeString(@NotNull String string, @NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(string, "string");
      Intrinsics.checkNotNullParameter(charset, "charset");
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeString(string, charset);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeString(@NotNull String string, int beginIndex, int endIndex, @NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(string, "string");
      Intrinsics.checkNotNullParameter(charset, "charset");
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeString(string, beginIndex, endIndex, charset);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink write(@NotNull byte[] source) {
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.write(source);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink write(@NotNull byte[] source, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var12 = "closed";
         throw new IllegalStateException(var12.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.write(source, offset, byteCount);
         return this.emitCompleteSegments();
      }
   }

   public int write(@NotNull ByteBuffer source) {
      Intrinsics.checkNotNullParameter(source, "source");
      if (this.closed) {
         int var3 = 0;
         String this_$iv = "closed";
         throw new IllegalStateException(this_$iv.toString());
      } else {
         int $i$f$getBuffer = 0;
         int result = this.bufferField.write(source);
         this.emitCompleteSegments();
         return result;
      }
   }

   public long writeAll(@NotNull Source source) {
      Intrinsics.checkNotNullParameter(source, "source");
      RealBufferedSink $this$commonWriteAll$iv = this;
      Source source$iv = source;
      int $i$f$commonWriteAll = 0;
      long totalBytesRead$iv = 0L;

      while(true) {
         int $i$f$getBuffer = 0;
         long readCount$iv = source$iv.read($this$commonWriteAll$iv.bufferField, 8192L);
         if (readCount$iv == -1L) {
            return totalBytesRead$iv;
         }

         totalBytesRead$iv += readCount$iv;
         $this$commonWriteAll$iv.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink write(@NotNull Source source, long byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      RealBufferedSink $this$commonWrite$iv = this;
      Source source$iv = source;
      int $i$f$commonWrite = 0;
      long byteCount$iv = byteCount;

      while(byteCount$iv > 0L) {
         int $i$f$getBuffer = 0;
         long read$iv = source$iv.read($this$commonWrite$iv.bufferField, byteCount$iv);
         if (read$iv == -1L) {
            throw new EOFException();
         }

         byteCount$iv -= read$iv;
         $this$commonWrite$iv.emitCompleteSegments();
      }

      return $this$commonWrite$iv;
   }

   @NotNull
   public BufferedSink writeByte(int b) {
      int $i$f$commonWriteByte = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeByte(b);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeShort(int s) {
      int $i$f$commonWriteShort = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeShort(s);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeShortLe(int s) {
      int $i$f$commonWriteShortLe = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeShortLe(s);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeInt(int i) {
      int $i$f$commonWriteInt = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeInt(i);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeIntLe(int i) {
      int $i$f$commonWriteIntLe = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeIntLe(i);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeLong(long v) {
      int $i$f$commonWriteLong = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var10 = "closed";
         throw new IllegalStateException(var10.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeLong(v);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeLongLe(long v) {
      int $i$f$commonWriteLongLe = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var10 = "closed";
         throw new IllegalStateException(var10.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeLongLe(v);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeDecimalLong(long v) {
      int $i$f$commonWriteDecimalLong = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var10 = "closed";
         throw new IllegalStateException(var10.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeDecimalLong(v);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink writeHexadecimalUnsignedLong(long v) {
      int $i$f$commonWriteHexadecimalUnsignedLong = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var10 = "closed";
         throw new IllegalStateException(var10.toString());
      } else {
         int $i$f$getBuffer = 0;
         this.bufferField.writeHexadecimalUnsignedLong(v);
         return this.emitCompleteSegments();
      }
   }

   @NotNull
   public BufferedSink emitCompleteSegments() {
      int $i$f$commonEmitCompleteSegments = 0;
      if (this.closed) {
         int var3 = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         long byteCount$iv = this.bufferField.completeSegmentByteCount();
         if (byteCount$iv > 0L) {
            $i$f$getBuffer = 0;
            this.sink.write(this.bufferField, byteCount$iv);
         }

         return this;
      }
   }

   @NotNull
   public BufferedSink emit() {
      int $i$f$commonEmit = 0;
      if (this.closed) {
         int var3 = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         long byteCount$iv = this.bufferField.size();
         if (byteCount$iv > 0L) {
            $i$f$getBuffer = 0;
            this.sink.write(this.bufferField, byteCount$iv);
         }

         return this;
      }
   }

   @NotNull
   public OutputStream outputStream() {
      return new OutputStream() {
         public void write(int b) {
            if (RealBufferedSink.this.closed) {
               throw new IOException("closed");
            } else {
               RealBufferedSink this_$iv = RealBufferedSink.this;
               int $i$f$getBuffer = 0;
               this_$iv.bufferField.writeByte((byte)b);
               RealBufferedSink.this.emitCompleteSegments();
            }
         }

         public void write(byte[] data, int offset, int byteCount) {
            Intrinsics.checkNotNullParameter(data, "data");
            if (RealBufferedSink.this.closed) {
               throw new IOException("closed");
            } else {
               RealBufferedSink this_$iv = RealBufferedSink.this;
               int $i$f$getBuffer = 0;
               this_$iv.bufferField.write(data, offset, byteCount);
               RealBufferedSink.this.emitCompleteSegments();
            }
         }

         public void flush() {
            if (!RealBufferedSink.this.closed) {
               RealBufferedSink.this.flush();
            }

         }

         public void close() {
            RealBufferedSink.this.close();
         }

         public String toString() {
            return RealBufferedSink.this + ".outputStream()";
         }
      };
   }

   public void flush() {
      int $i$f$commonFlush = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         int $i$f$getBuffer = 0;
         if (this.bufferField.size() > 0L) {
            $i$f$getBuffer = 0;
            $i$f$getBuffer = 0;
            this.sink.write(this.bufferField, this.bufferField.size());
         }

         this.sink.flush();
      }
   }

   public boolean isOpen() {
      return !this.closed;
   }

   public void close() {
      RealBufferedSink $this$commonClose$iv = this;
      int $i$f$commonClose = 0;
      if (!this.closed) {
         Throwable thrown$iv = null;

         try {
            int $i$f$getBuffer = 0;
            if ($this$commonClose$iv.bufferField.size() > 0L) {
               $i$f$getBuffer = 0;
               $i$f$getBuffer = 0;
               $this$commonClose$iv.sink.write($this$commonClose$iv.bufferField, $this$commonClose$iv.bufferField.size());
            }
         } catch (Throwable e$iv) {
            thrown$iv = e$iv;
         }

         try {
            $this$commonClose$iv.sink.close();
         } catch (Throwable e$iv) {
            if (thrown$iv == null) {
               thrown$iv = e$iv;
            }
         }

         this.closed = true;
         if (thrown$iv != null) {
            throw thrown$iv;
         }
      }

   }

   @NotNull
   public Timeout timeout() {
      int $i$f$commonTimeout = 0;
      return this.sink.timeout();
   }

   @NotNull
   public String toString() {
      int $i$f$commonToString = 0;
      return "buffer(" + this.sink + ')';
   }
}
