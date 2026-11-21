package okio;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.internal._ZlibJvmKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0014H\u0016J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\b\u0010\u001c\u001a\u00020\u0014H\u0016J\b\u0010\u001d\u001a\u00020\u0014H\u0002J\u0018\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u0013\u0010\u0006\u001a\u00060\u0007j\u0002`\bH\u0007¢\u0006\u0004\b \u0010\tR\u000e\u0010\u0002\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\u0006\u001a\u00060\u0007j\u0002`\b8\u0007¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\u0006\u0010\tR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\u00060\u0010j\u0002`\u0011X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0012¨\u0006!"},
   d2 = {"Lokio/GzipSink;", "Lokio/Sink;", "sink", "<init>", "(Lokio/Sink;)V", "Lokio/RealBufferedSink;", "deflater", "Ljava/util/zip/Deflater;", "Lokio/Deflater;", "()Ljava/util/zip/Deflater;", "Ljava/util/zip/Deflater;", "deflaterSink", "Lokio/DeflaterSink;", "closed", "", "crc", "Ljava/util/zip/CRC32;", "Lokio/internal/CRC32;", "Ljava/util/zip/CRC32;", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "flush", "timeout", "Lokio/Timeout;", "close", "writeFooter", "updateCrc", "buffer", "-deprecated_deflater", "okio"}
)
@SourceDebugExtension({"SMAP\nGzipSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GzipSink.kt\nokio/GzipSink\n+ 2 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,152:1\n51#2:153\n1#3:154\n85#4:155\n*S KotlinDebug\n*F\n+ 1 GzipSink.kt\nokio/GzipSink\n*L\n62#1:153\n130#1:155\n*E\n"})
public final class GzipSink implements Sink {
   @NotNull
   private final RealBufferedSink sink;
   @NotNull
   private final Deflater deflater;
   @NotNull
   private final DeflaterSink deflaterSink;
   private boolean closed;
   @NotNull
   private final CRC32 crc;

   public GzipSink(@NotNull Sink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      super();
      this.sink = new RealBufferedSink(sink);
      this.deflater = new Deflater(_ZlibJvmKt.getDEFAULT_COMPRESSION(), true);
      this.deflaterSink = new DeflaterSink(this.sink, this.deflater);
      this.crc = new CRC32();
      RealBufferedSink this_$iv = this.sink;
      int $i$f$getBuffer = 0;
      Buffer $this$_init__u24lambda_u240 = this_$iv.bufferField;
      int var4 = 0;
      $this$_init__u24lambda_u240.writeShort(8075);
      $this$_init__u24lambda_u240.writeByte(8);
      $this$_init__u24lambda_u240.writeByte(0);
      $this$_init__u24lambda_u240.writeInt(0);
      $this$_init__u24lambda_u240.writeByte(0);
      $this$_init__u24lambda_u240.writeByte(0);
   }

   @JvmName(
      name = "deflater"
   )
   @NotNull
   public final Deflater deflater() {
      return this.deflater;
   }

   public void write(@NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      if (byteCount < 0L) {
         int var4 = 0;
         String var5 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var5.toString());
      } else if (byteCount != 0L) {
         this.updateCrc(source, byteCount);
         this.deflaterSink.write(source, byteCount);
      }
   }

   public void flush() throws IOException {
      this.deflaterSink.flush();
   }

   @NotNull
   public Timeout timeout() {
      return this.sink.timeout();
   }

   public void close() throws IOException {
      if (!this.closed) {
         Throwable thrown = null;

         try {
            this.deflaterSink.finishDeflate$okio();
            this.writeFooter();
         } catch (Throwable e) {
            thrown = e;
         }

         try {
            this.deflater.end();
         } catch (Throwable e) {
            if (thrown == null) {
               thrown = e;
            }
         }

         try {
            this.sink.close();
         } catch (Throwable e) {
            if (thrown == null) {
               thrown = e;
            }
         }

         this.closed = true;
         if (thrown != null) {
            throw thrown;
         }
      }
   }

   private final void writeFooter() {
      this.sink.writeIntLe((int)this.crc.getValue());
      this.sink.writeIntLe((int)this.deflater.getBytesRead());
   }

   private final void updateCrc(Buffer buffer, long byteCount) {
      Segment var10000 = buffer.head;
      Intrinsics.checkNotNull(var10000);
      Segment head = var10000;

      for(long remaining = byteCount; remaining > 0L; head = var10000) {
         int b$iv = head.limit - head.pos;
         int $i$f$minOf = 0;
         int segmentLength = (int)Math.min(remaining, (long)b$iv);
         this.crc.update(head.data, head.pos, segmentLength);
         remaining -= (long)segmentLength;
         var10000 = head.next;
         Intrinsics.checkNotNull(var10000);
      }

   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "deflater",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_deflater"
   )
   @NotNull
   public final Deflater _deprecated_deflater/* $FF was: -deprecated_deflater*/() {
      return this.deflater;
   }
}
