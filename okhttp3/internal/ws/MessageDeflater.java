package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.Deflater;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ByteString;
import okio.DeflaterSink;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0007J\b\u0010\u000f\u001a\u00020\rH\u0016J\u0014\u0010\u0010\u001a\u00020\u0003*\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0012H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lokhttp3/internal/ws/MessageDeflater;", "Ljava/io/Closeable;", "noContextTakeover", "", "<init>", "(Z)V", "deflatedBytes", "Lokio/Buffer;", "deflater", "Ljava/util/zip/Deflater;", "deflaterSink", "Lokio/DeflaterSink;", "deflate", "", "buffer", "close", "endsWith", "suffix", "Lokio/ByteString;", "okhttp"}
)
public final class MessageDeflater implements Closeable {
   private final boolean noContextTakeover;
   @NotNull
   private final Buffer deflatedBytes;
   @NotNull
   private final Deflater deflater;
   @NotNull
   private final DeflaterSink deflaterSink;

   public MessageDeflater(boolean noContextTakeover) {
      this.noContextTakeover = noContextTakeover;
      this.deflatedBytes = new Buffer();
      this.deflater = new Deflater(-1, true);
      this.deflaterSink = new DeflaterSink(this.deflatedBytes, this.deflater);
   }

   public final void deflate(@NotNull Buffer buffer) throws IOException {
      Intrinsics.checkNotNullParameter(buffer, "buffer");
      if (this.deflatedBytes.size() != 0L) {
         String var3 = "Failed requirement.";
         throw new IllegalArgumentException(var3.toString());
      } else {
         if (this.noContextTakeover) {
            this.deflater.reset();
         }

         this.deflaterSink.write(buffer, buffer.size());
         this.deflaterSink.flush();
         if (this.endsWith(this.deflatedBytes, MessageDeflaterKt.access$getEMPTY_DEFLATE_BLOCK$p())) {
            long newSize = this.deflatedBytes.size() - (long)4;
            Closeable var4 = Buffer.readAndWriteUnsafe$default(this.deflatedBytes, (Buffer.UnsafeCursor)null, 1, (Object)null);
            Throwable var5 = null;

            try {
               Buffer.UnsafeCursor cursor = (Buffer.UnsafeCursor)var4;
               int var7 = 0;
               Long var12 = cursor.resizeBuffer(newSize);
            } catch (Throwable var10) {
               var5 = var10;
               throw var10;
            } finally {
               CloseableKt.closeFinally(var4, var5);
            }
         } else {
            this.deflatedBytes.writeByte(0);
         }

         buffer.write(this.deflatedBytes, this.deflatedBytes.size());
      }
   }

   public void close() throws IOException {
      this.deflaterSink.close();
   }

   private final boolean endsWith(Buffer $this$endsWith, ByteString suffix) {
      return $this$endsWith.rangeEquals($this$endsWith.size() - (long)suffix.size(), suffix);
   }
}
