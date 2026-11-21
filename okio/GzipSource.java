package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0013H\u0016J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0018H\u0002J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\b\u0010\u001c\u001a\u00020\u0018H\u0016J \u0010\u001d\u001a\u00020\u00182\u0006\u0010\u001e\u001a\u00020\u00152\u0006\u0010\u001f\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0013H\u0002J \u0010 \u001a\u00020\u00182\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020$H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u00060\tj\u0002`\nX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\u00060\u000fj\u0002`\u0010X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0011¨\u0006&"},
   d2 = {"Lokio/GzipSource;", "Lokio/Source;", "source", "<init>", "(Lokio/Source;)V", "section", "", "Lokio/RealBufferedSource;", "inflater", "Ljava/util/zip/Inflater;", "Lokio/Inflater;", "Ljava/util/zip/Inflater;", "inflaterSource", "Lokio/InflaterSource;", "crc", "Ljava/util/zip/CRC32;", "Lokio/internal/CRC32;", "Ljava/util/zip/CRC32;", "read", "", "sink", "Lokio/Buffer;", "byteCount", "consumeHeader", "", "consumeTrailer", "timeout", "Lokio/Timeout;", "close", "updateCrc", "buffer", "offset", "checkEqual", "name", "", "expected", "", "actual", "okio"}
)
@SourceDebugExtension({"SMAP\nGzipSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GzipSource.kt\nokio/GzipSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 4 GzipSource.kt\nokio/-GzipSourceExtensions\n+ 5 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,222:1\n1#2:223\n63#3:224\n63#3:226\n63#3:228\n63#3:229\n63#3:230\n63#3:232\n63#3:234\n204#4:225\n204#4:227\n204#4:231\n204#4:233\n88#5:235\n*S KotlinDebug\n*F\n+ 1 GzipSource.kt\nokio/GzipSource\n*L\n103#1:224\n105#1:226\n117#1:228\n118#1:229\n120#1:230\n131#1:232\n142#1:234\n104#1:225\n115#1:227\n128#1:231\n139#1:233\n185#1:235\n*E\n"})
public final class GzipSource implements Source {
   private byte section;
   @NotNull
   private final RealBufferedSource source;
   @NotNull
   private final Inflater inflater;
   @NotNull
   private final InflaterSource inflaterSource;
   @NotNull
   private final CRC32 crc;

   public GzipSource(@NotNull Source source) {
      Intrinsics.checkNotNullParameter(source, "source");
      super();
      this.source = new RealBufferedSource(source);
      this.inflater = new Inflater(true);
      this.inflaterSource = new InflaterSource(this.source, this.inflater);
      this.crc = new CRC32();
   }

   public long read(@NotNull Buffer sink, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (byteCount < 0L) {
         int var5 = 0;
         String var8 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var8.toString());
      } else if (byteCount == 0L) {
         return 0L;
      } else {
         if (this.section == 0) {
            this.consumeHeader();
            this.section = 1;
         }

         if (this.section == 1) {
            long offset = sink.size();
            long result = this.inflaterSource.read(sink, byteCount);
            if (result != -1L) {
               this.updateCrc(sink, offset, result);
               return result;
            }

            this.section = 2;
         }

         if (this.section == 2) {
            this.consumeTrailer();
            this.section = 3;
            if (!this.source.exhausted()) {
               throw new IOException("gzip finished without exhausting source");
            }
         }

         return -1L;
      }
   }

   private final void consumeHeader() throws IOException {
      this.source.require(10L);
      RealBufferedSource this_$iv = this.source;
      int $i$f$getBuffer = 0;
      int flags = this_$iv.bufferField.getByte(3L);
      int bit$iv = 1;
      int $i$f$getBit = 0;
      boolean fhcrc = (flags >> bit$iv & 1) == 1;
      if (fhcrc) {
         RealBufferedSource this_$iv = this.source;
         bit$iv = 0;
         this.updateCrc(this_$iv.bufferField, 0L, 10L);
      }

      $i$f$getBuffer = this.source.readShort();
      this.checkEqual("ID1ID2", 8075, $i$f$getBuffer);
      this.source.skip(8L);
      $i$f$getBit = 2;
      int $i$f$getBit = 0;
      if ((flags >> $i$f$getBit & 1) == 1) {
         this.source.require(2L);
         if (fhcrc) {
            RealBufferedSource this_$iv = this.source;
            $i$f$getBit = 0;
            this.updateCrc(this_$iv.bufferField, 0L, 2L);
         }

         RealBufferedSource this_$iv = this.source;
         int $i$f$getBuffer = 0;
         long xlen = (long)(this_$iv.bufferField.readShortLe() & '\uffff');
         this.source.require(xlen);
         if (fhcrc) {
            this_$iv = this.source;
            $i$f$getBuffer = 0;
            this.updateCrc(this_$iv.bufferField, 0L, xlen);
         }

         this.source.skip(xlen);
      }

      $i$f$getBit = 3;
      $i$f$getBit = 0;
      if ((flags >> $i$f$getBit & 1) == 1) {
         long index = this.source.indexOf((byte)0);
         if (index == -1L) {
            throw new EOFException();
         }

         if (fhcrc) {
            RealBufferedSource this_$iv = this.source;
            int $i$f$getBuffer = 0;
            this.updateCrc(this_$iv.bufferField, 0L, index + 1L);
         }

         this.source.skip(index + 1L);
      }

      $i$f$getBit = 4;
      $i$f$getBit = 0;
      if ((flags >> $i$f$getBit & 1) == 1) {
         long index = this.source.indexOf((byte)0);
         if (index == -1L) {
            throw new EOFException();
         }

         if (fhcrc) {
            RealBufferedSource this_$iv = this.source;
            int $i$f$getBuffer = 0;
            this.updateCrc(this_$iv.bufferField, 0L, index + 1L);
         }

         this.source.skip(index + 1L);
      }

      if (fhcrc) {
         this.checkEqual("FHCRC", this.source.readShortLe(), (short)((int)this.crc.getValue()));
         this.crc.reset();
      }

   }

   private final void consumeTrailer() throws IOException {
      this.checkEqual("CRC", this.source.readIntLe(), (int)this.crc.getValue());
      this.checkEqual("ISIZE", this.source.readIntLe(), (int)this.inflater.getBytesWritten());
   }

   @NotNull
   public Timeout timeout() {
      return this.source.timeout();
   }

   public void close() throws IOException {
      this.inflaterSource.close();
   }

   private final void updateCrc(Buffer buffer, long offset, long byteCount) {
      long offset = offset;
      long byteCount = byteCount;
      Segment var10000 = buffer.head;
      Intrinsics.checkNotNull(var10000);

      Segment s;
      for(s = var10000; offset >= (long)(s.limit - s.pos); s = var10000) {
         offset -= (long)(s.limit - s.pos);
         var10000 = s.next;
         Intrinsics.checkNotNull(var10000);
      }

      while(byteCount > 0L) {
         int pos = (int)((long)s.pos + offset);
         int a$iv = s.limit - pos;
         int $i$f$minOf = 0;
         int toUpdate = (int)Math.min((long)a$iv, byteCount);
         this.crc.update(s.data, pos, toUpdate);
         byteCount -= (long)toUpdate;
         offset = 0L;
         var10000 = s.next;
         Intrinsics.checkNotNull(var10000);
         s = var10000;
      }

   }

   private final void checkEqual(String name, int expected, int actual) {
      if (actual != expected) {
         throw new IOException(name + ": actual 0x" + StringsKt.padStart(-SegmentedByteString.toHexString(actual), 8, '0') + " != expected 0x" + StringsKt.padStart(-SegmentedByteString.toHexString(expected), 8, '0'));
      }
   }
}
