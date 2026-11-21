package okhttp3;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016R\u0014\u0010\u0004\u001a\u00020\u00058VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\f"},
   d2 = {"Lokhttp3/Gzip;", "Lokhttp3/CompressionInterceptor$DecompressionAlgorithm;", "<init>", "()V", "encoding", "", "getEncoding", "()Ljava/lang/String;", "decompress", "Lokio/Source;", "compressedSource", "Lokio/BufferedSource;", "okhttp"}
)
public final class Gzip implements CompressionInterceptor.DecompressionAlgorithm {
   @NotNull
   public static final Gzip INSTANCE = new Gzip();

   private Gzip() {
   }

   @NotNull
   public String getEncoding() {
      return "gzip";
   }

   @NotNull
   public Source decompress(@NotNull BufferedSource compressedSource) {
      Intrinsics.checkNotNullParameter(compressedSource, "compressedSource");
      return new GzipSource(compressedSource);
   }
}
