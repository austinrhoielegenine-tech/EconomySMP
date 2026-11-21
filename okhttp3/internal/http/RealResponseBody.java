package okhttp3.internal.http;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B!\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0004\u001a\u00020\u0005H\u0016J\n\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
   d2 = {"Lokhttp3/internal/http/RealResponseBody;", "Lokhttp3/ResponseBody;", "contentTypeString", "", "contentLength", "", "source", "Lokio/BufferedSource;", "<init>", "(Ljava/lang/String;JLokio/BufferedSource;)V", "contentType", "Lokhttp3/MediaType;", "okhttp"}
)
public final class RealResponseBody extends ResponseBody {
   @Nullable
   private final String contentTypeString;
   private final long contentLength;
   @NotNull
   private final BufferedSource source;

   public RealResponseBody(@Nullable String contentTypeString, long contentLength, @NotNull BufferedSource source) {
      Intrinsics.checkNotNullParameter(source, "source");
      super();
      this.contentTypeString = contentTypeString;
      this.contentLength = contentLength;
      this.source = source;
   }

   public long contentLength() {
      return this.contentLength;
   }

   @Nullable
   public MediaType contentType() {
      String var10000 = this.contentTypeString;
      return var10000 != null ? MediaType.Companion.parse(var10000) : null;
   }

   @NotNull
   public BufferedSource source() {
      return this.source;
   }
}
