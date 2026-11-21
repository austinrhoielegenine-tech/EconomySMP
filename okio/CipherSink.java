package okio;

import java.io.IOException;
import javax.crypto.Cipher;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0018\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u0013H\u0002J\b\u0010\u0016\u001a\u00020\u000fH\u0016J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u000fH\u0016J\n\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001c"},
   d2 = {"Lokio/CipherSink;", "Lokio/Sink;", "sink", "Lokio/BufferedSink;", "cipher", "Ljavax/crypto/Cipher;", "<init>", "(Lokio/BufferedSink;Ljavax/crypto/Cipher;)V", "getCipher", "()Ljavax/crypto/Cipher;", "blockSize", "", "closed", "", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "update", "remaining", "flush", "timeout", "Lokio/Timeout;", "close", "doFinal", "", "okio"}
)
@SourceDebugExtension({"SMAP\nCipherSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CipherSink.kt\nokio/CipherSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,148:1\n1#2:149\n85#3:150\n*S KotlinDebug\n*F\n+ 1 CipherSink.kt\nokio/CipherSink\n*L\n47#1:150\n*E\n"})
public final class CipherSink implements Sink {
   @NotNull
   private final BufferedSink sink;
   @NotNull
   private final Cipher cipher;
   private final int blockSize;
   private boolean closed;

   public CipherSink(@NotNull BufferedSink sink, @NotNull Cipher cipher) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(cipher, "cipher");
      super();
      this.sink = sink;
      this.cipher = cipher;
      this.blockSize = this.cipher.getBlockSize();
      if (this.blockSize <= 0) {
         int var3 = 0;
         String var4 = "Block cipher required " + this.cipher;
         throw new IllegalArgumentException(var4.toString());
      }
   }

   @NotNull
   public final Cipher getCipher() {
      return this.cipher;
   }

   public void write(@NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      if (this.closed) {
         int var5 = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int size;
         for(long remaining = byteCount; remaining > 0L; remaining -= (long)size) {
            size = this.update(source, remaining);
         }

      }
   }

   private final int update(Buffer source, long remaining) {
      Segment var10000 = source.head;
      Intrinsics.checkNotNull(var10000);
      Segment head = var10000;
      int b$iv = head.limit - head.pos;
      int $i$f$minOf = 0;
      int size = (int)Math.min(remaining, (long)b$iv);
      Buffer buffer = this.sink.getBuffer();

      int outputSize;
      for(outputSize = this.cipher.getOutputSize(size); outputSize > 8192; outputSize = this.cipher.getOutputSize(size)) {
         if (size <= this.blockSize) {
            BufferedSink var12 = this.sink;
            byte[] var10001 = this.cipher.update(source.readByteArray(remaining));
            Intrinsics.checkNotNullExpressionValue(var10001, "update(...)");
            var12.write(var10001);
            return (int)remaining;
         }

         size -= this.blockSize;
      }

      Segment s = buffer.writableSegment$okio(outputSize);
      $i$f$minOf = this.cipher.update(head.data, head.pos, size, s.data, s.limit);
      s.limit += $i$f$minOf;
      buffer.setSize$okio(buffer.size() + (long)$i$f$minOf);
      if (s.pos == s.limit) {
         buffer.head = s.pop();
         SegmentPool.recycle(s);
      }

      this.sink.emitCompleteSegments();
      source.setSize$okio(source.size() - (long)size);
      head.pos += size;
      if (head.pos == head.limit) {
         source.head = head.pop();
         SegmentPool.recycle(head);
      }

      return size;
   }

   public void flush() {
      this.sink.flush();
   }

   @NotNull
   public Timeout timeout() {
      return this.sink.timeout();
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         Throwable thrown = this.doFinal();

         try {
            this.sink.close();
         } catch (Throwable e) {
            if (thrown == null) {
               thrown = e;
            }
         }

         if (thrown != null) {
            throw thrown;
         }
      }
   }

   private final Throwable doFinal() {
      int outputSize = this.cipher.getOutputSize(0);
      if (outputSize == 0) {
         return null;
      } else if (outputSize > 8192) {
         try {
            BufferedSink var10000 = this.sink;
            byte[] var10001 = this.cipher.doFinal();
            Intrinsics.checkNotNullExpressionValue(var10001, "doFinal(...)");
            var10000.write(var10001);
            return null;
         } catch (Throwable t) {
            return t;
         }
      } else {
         Throwable thrown = null;
         Buffer buffer = this.sink.getBuffer();
         Segment s = buffer.writableSegment$okio(outputSize);

         try {
            int ciphered = this.cipher.doFinal(s.data, s.limit);
            s.limit += ciphered;
            buffer.setSize$okio(buffer.size() + (long)ciphered);
         } catch (Throwable e) {
            thrown = e;
         }

         if (s.pos == s.limit) {
            buffer.head = s.pop();
            SegmentPool.recycle(s);
         }

         return thrown;
      }
   }
}
