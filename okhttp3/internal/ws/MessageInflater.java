package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.util.zip.Inflater;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.InflaterSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0007J\b\u0010\u000f\u001a\u00020\rH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lokhttp3/internal/ws/MessageInflater;", "Ljava/io/Closeable;", "noContextTakeover", "", "<init>", "(Z)V", "deflatedBytes", "Lokio/Buffer;", "inflater", "Ljava/util/zip/Inflater;", "inflaterSource", "Lokio/InflaterSource;", "inflate", "", "buffer", "close", "okhttp"}
)
@SourceDebugExtension({"SMAP\nMessageInflater.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageInflater.kt\nokhttp3/internal/ws/MessageInflater\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,79:1\n1#2:80\n*E\n"})
public final class MessageInflater implements Closeable {
   private final boolean noContextTakeover;
   @NotNull
   private final Buffer deflatedBytes;
   @Nullable
   private Inflater inflater;
   @Nullable
   private InflaterSource inflaterSource;

   public MessageInflater(boolean noContextTakeover) {
      this.noContextTakeover = noContextTakeover;
      this.deflatedBytes = new Buffer();
   }

   public final void inflate(@NotNull Buffer buffer) throws IOException {
      Intrinsics.checkNotNullParameter(buffer, "buffer");
      if (this.deflatedBytes.size() != 0L) {
         String var8 = "Failed requirement.";
         throw new IllegalArgumentException(var8.toString());
      } else {
         Inflater var10000 = this.inflater;
         if (var10000 == null) {
            Inflater it = new Inflater(true);
            int var6 = 0;
            this.inflater = it;
            var10000 = it;
         }

         Inflater inflater = var10000;
         InflaterSource var10 = this.inflaterSource;
         if (var10 == null) {
            InflaterSource it = new InflaterSource(this.deflatedBytes, inflater);
            int var7 = 0;
            this.inflaterSource = it;
            var10 = it;
         }

         InflaterSource inflaterSource = var10;
         if (this.noContextTakeover) {
            inflater.reset();
         }

         this.deflatedBytes.writeAll(buffer);
         this.deflatedBytes.writeInt(65535);
         long totalBytesToRead = inflater.getBytesRead() + this.deflatedBytes.size();

         do {
            inflaterSource.readOrInflate(buffer, Long.MAX_VALUE);
         } while(inflater.getBytesRead() < totalBytesToRead && !inflater.finished());

         if (inflater.getBytesRead() < totalBytesToRead) {
            this.deflatedBytes.clear();
            inflaterSource.close();
            this.inflaterSource = null;
            this.inflater = null;
         }

      }
   }

   public void close() throws IOException {
      InflaterSource var10000 = this.inflaterSource;
      if (var10000 != null) {
         var10000.close();
      }

      this.inflaterSource = null;
      this.inflater = null;
   }
}
