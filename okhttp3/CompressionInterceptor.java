package okhttp3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.http.HttpHeaders;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b\u0016\u0018\u00002\u00020\u0001:\u0001\u0018B\u001b\u0012\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0015\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u000fH\u0000¢\u0006\u0002\b\u0014J\u0017\u0010\u0015\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0016\u001a\u00020\u000bH\u0000¢\u0006\u0002\b\u0017R\u001b\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003¢\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\n\u001a\u00020\u000bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0019"},
   d2 = {"Lokhttp3/CompressionInterceptor;", "Lokhttp3/Interceptor;", "algorithms", "", "Lokhttp3/CompressionInterceptor$DecompressionAlgorithm;", "<init>", "([Lokhttp3/CompressionInterceptor$DecompressionAlgorithm;)V", "getAlgorithms", "()[Lokhttp3/CompressionInterceptor$DecompressionAlgorithm;", "[Lokhttp3/CompressionInterceptor$DecompressionAlgorithm;", "acceptEncoding", "", "getAcceptEncoding$okhttp", "()Ljava/lang/String;", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "decompress", "response", "decompress$okhttp", "lookupDecompressor", "encoding", "lookupDecompressor$okhttp", "DecompressionAlgorithm", "okhttp"}
)
@SourceDebugExtension({"SMAP\nCompressionInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CompressionInterceptor.kt\nokhttp3/CompressionInterceptor\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,96:1\n11561#2:97\n11896#2,3:98\n*S KotlinDebug\n*F\n+ 1 CompressionInterceptor.kt\nokhttp3/CompressionInterceptor\n*L\n39#1:97\n39#1:98,3\n*E\n"})
public class CompressionInterceptor implements Interceptor {
   @NotNull
   private final DecompressionAlgorithm[] algorithms;
   @NotNull
   private final String acceptEncoding;

   public CompressionInterceptor(@NotNull DecompressionAlgorithm... algorithms) {
      Intrinsics.checkNotNullParameter(algorithms, "algorithms");
      super();
      this.algorithms = algorithms;
      DecompressionAlgorithm[] $this$map$iv = this.algorithms;
      int $i$f$map = 0;
      DecompressionAlgorithm[] $this$mapTo$iv$iv = $this$map$iv;
      Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.length));
      int $i$f$mapTo = 0;
      int var7 = 0;

      for(int var8 = $this$map$iv.length; var7 < var8; ++var7) {
         Object item$iv$iv = $this$mapTo$iv$iv[var7];
         int var11 = 0;
         destination$iv$iv.add(((DecompressionAlgorithm)item$iv$iv).getEncoding());
      }

      this.acceptEncoding = CollectionsKt.joinToString$default((Iterable)((List)destination$iv$iv), (CharSequence)", ", (CharSequence)null, (CharSequence)null, 0, (CharSequence)null, (Function1)null, 62, (Object)null);
   }

   @NotNull
   public final DecompressionAlgorithm[] getAlgorithms() {
      return this.algorithms;
   }

   @NotNull
   public final String getAcceptEncoding$okhttp() {
      return this.acceptEncoding;
   }

   @NotNull
   public Response intercept(@NotNull Interceptor.Chain chain) {
      Intrinsics.checkNotNullParameter(chain, "chain");
      Response var10000;
      if (this.algorithms.length != 0 && chain.request().header("Accept-Encoding") == null) {
         Request request = chain.request().newBuilder().header("Accept-Encoding", this.acceptEncoding).build();
         Response response = chain.proceed(request);
         var10000 = this.decompress$okhttp(response);
      } else {
         var10000 = chain.proceed(chain.request());
      }

      return var10000;
   }

   @NotNull
   public final Response decompress$okhttp(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      if (!HttpHeaders.promisesBody(response)) {
         return response;
      } else {
         ResponseBody body = response.body();
         String var10000 = Response.header$default(response, "Content-Encoding", (String)null, 2, (Object)null);
         if (var10000 == null) {
            return response;
         } else {
            String encoding = var10000;
            DecompressionAlgorithm var6 = this.lookupDecompressor$okhttp(encoding);
            if (var6 == null) {
               return response;
            } else {
               DecompressionAlgorithm algorithm = var6;
               BufferedSource decompressedSource = Okio.buffer(algorithm.decompress(body.source()));
               return response.newBuilder().removeHeader("Content-Encoding").removeHeader("Content-Length").body(ResponseBody.Companion.create(decompressedSource, body.contentType(), -1L)).build();
            }
         }
      }
   }

   @Nullable
   public final DecompressionAlgorithm lookupDecompressor$okhttp(@NotNull String encoding) {
      Intrinsics.checkNotNullParameter(encoding, "encoding");
      DecompressionAlgorithm[] var2 = this.algorithms;
      int var3 = 0;
      int var4 = var2.length;

      DecompressionAlgorithm var10000;
      while(true) {
         if (var3 >= var4) {
            var10000 = null;
            break;
         }

         DecompressionAlgorithm it = var2[var3];
         int var7 = 0;
         if (StringsKt.equals(it.getEncoding(), encoding, true)) {
            var10000 = it;
            break;
         }

         ++var3;
      }

      return var10000;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005¨\u0006\nÀ\u0006\u0003"},
      d2 = {"Lokhttp3/CompressionInterceptor$DecompressionAlgorithm;", "", "encoding", "", "getEncoding", "()Ljava/lang/String;", "decompress", "Lokio/Source;", "compressedSource", "Lokio/BufferedSource;", "okhttp"}
   )
   public interface DecompressionAlgorithm {
      @NotNull
      String getEncoding();

      @NotNull
      Source decompress(@NotNull BufferedSource var1);
   }
}
