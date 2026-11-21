package okhttp3.internal;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u00012\u00020\u0002B\u0019\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\n\u0010\t\u001a\u0004\u0018\u00010\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\u00062\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"},
   d2 = {"Lokhttp3/internal/UnreadableResponseBody;", "Lokhttp3/ResponseBody;", "Lokio/Source;", "mediaType", "Lokhttp3/MediaType;", "contentLength", "", "<init>", "(Lokhttp3/MediaType;J)V", "contentType", "source", "Lokio/BufferedSource;", "read", "sink", "Lokio/Buffer;", "byteCount", "timeout", "Lokio/Timeout;", "close", "", "okhttp"}
)
public final class UnreadableResponseBody extends ResponseBody implements Source {
   @Nullable
   private final MediaType mediaType;
   private final long contentLength;

   public UnreadableResponseBody(@Nullable MediaType mediaType, long contentLength) {
      this.mediaType = mediaType;
      this.contentLength = contentLength;
   }

   @Nullable
   public MediaType contentType() {
      return this.mediaType;
   }

   public long contentLength() {
      return this.contentLength;
   }

   @NotNull
   public BufferedSource source() {
      return Okio.buffer(this);
   }

   public long read(@NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      throw new IllegalStateException("Unreadable ResponseBody! These Response objects have bodies that are stripped:\n * Response.cacheResponse\n * Response.networkResponse\n * Response.priorResponse\n * EventSourceListener\n * WebSocketListener\n(It is safe to call contentType() and contentLength() on these response bodies.)");
   }

   @NotNull
   public Timeout timeout() {
      return Timeout.NONE;
   }

   public void close() {
   }
}
