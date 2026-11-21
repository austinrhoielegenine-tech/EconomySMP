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
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u0012H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0016H\u0002J\b\u0010\u0018\u001a\u00020\u0016H\u0002J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u0016H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001c"},
   d2 = {"Lokio/CipherSource;", "Lokio/Source;", "source", "Lokio/BufferedSource;", "cipher", "Ljavax/crypto/Cipher;", "<init>", "(Lokio/BufferedSource;Ljavax/crypto/Cipher;)V", "getCipher", "()Ljavax/crypto/Cipher;", "blockSize", "", "buffer", "Lokio/Buffer;", "final", "", "closed", "read", "", "sink", "byteCount", "refill", "", "update", "doFinal", "timeout", "Lokio/Timeout;", "close", "okio"}
)
@SourceDebugExtension({"SMAP\nCipherSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CipherSource.kt\nokio/CipherSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,120:1\n1#2:121\n*E\n"})
public final class CipherSource implements Source {
   @NotNull
   private final BufferedSource source;
   @NotNull
   private final Cipher cipher;
   private final int blockSize;
   @NotNull
   private final Buffer buffer;
   private boolean final;
   private boolean closed;

   public CipherSource(@NotNull BufferedSource source, @NotNull Cipher cipher) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(cipher, "cipher");
      super();
      this.source = source;
      this.cipher = cipher;
      this.blockSize = this.cipher.getBlockSize();
      this.buffer = new Buffer();
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

   public long read(@NotNull Buffer sink, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (byteCount < 0L) {
         int var6 = 0;
         String var7 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var7.toString());
      } else if (this.closed) {
         int var4 = 0;
         String var5 = "closed";
         throw new IllegalStateException(var5.toString());
      } else if (byteCount == 0L) {
         return 0L;
      } else {
         this.refill();
         return this.buffer.read(sink, byteCount);
      }
   }

   private final void refill() {
      while(true) {
         if (this.buffer.size() == 0L && !this.final) {
            if (!this.source.exhausted()) {
               this.update();
               continue;
            }

            this.final = true;
            this.doFinal();
         }

         return;
      }
   }

   private final void update() {
      Segment var10000 = this.source.getBuffer().head;
      Intrinsics.checkNotNull(var10000);
      Segment head = var10000;
      int size = head.limit - head.pos;

      int outputSize;
      for(outputSize = this.cipher.getOutputSize(size); outputSize > 8192; outputSize = this.cipher.getOutputSize(size)) {
         if (size <= this.blockSize) {
            this.final = true;
            Buffer var7 = this.buffer;
            byte[] var10001 = this.cipher.doFinal(this.source.readByteArray());
            Intrinsics.checkNotNullExpressionValue(var10001, "doFinal(...)");
            var7.write(var10001);
            return;
         }

         size -= this.blockSize;
      }

      Segment s = this.buffer.writableSegment$okio(outputSize);
      int ciphered = this.cipher.update(head.data, head.pos, size, s.data, s.pos);
      this.source.skip((long)size);
      s.limit += ciphered;
      Buffer var6 = this.buffer;
      var6.setSize$okio(var6.size() + (long)ciphered);
      if (s.pos == s.limit) {
         this.buffer.head = s.pop();
         SegmentPool.recycle(s);
      }

   }

   private final void doFinal() {
      int outputSize = this.cipher.getOutputSize(0);
      if (outputSize != 0) {
         Segment s = this.buffer.writableSegment$okio(outputSize);
         int ciphered = this.cipher.doFinal(s.data, s.pos);
         s.limit += ciphered;
         Buffer var4 = this.buffer;
         var4.setSize$okio(var4.size() + (long)ciphered);
         if (s.pos == s.limit) {
            this.buffer.head = s.pop();
            SegmentPool.recycle(s);
         }

      }
   }

   @NotNull
   public Timeout timeout() {
      return this.source.timeout();
   }

   public void close() throws IOException {
      this.closed = true;
      this.source.close();
   }
}
