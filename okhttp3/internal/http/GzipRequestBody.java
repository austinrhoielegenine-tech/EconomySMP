package okhttp3.internal.http;

import java.io.Closeable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\n\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0016J\b\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016R\u0011\u0010\u0002\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0011"},
   d2 = {"Lokhttp3/internal/http/GzipRequestBody;", "Lokhttp3/RequestBody;", "delegate", "<init>", "(Lokhttp3/RequestBody;)V", "getDelegate", "()Lokhttp3/RequestBody;", "contentType", "Lokhttp3/MediaType;", "contentLength", "", "writeTo", "", "sink", "Lokio/BufferedSink;", "isOneShot", "", "okhttp"}
)
@SourceDebugExtension({"SMAP\nGzipRequestBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GzipRequestBody.kt\nokhttp3/internal/http/GzipRequestBody\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,37:1\n1#2:38\n*E\n"})
public final class GzipRequestBody extends RequestBody {
   @NotNull
   private final RequestBody delegate;

   public GzipRequestBody(@NotNull RequestBody delegate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      super();
      this.delegate = delegate;
   }

   @NotNull
   public final RequestBody getDelegate() {
      return this.delegate;
   }

   @Nullable
   public MediaType contentType() {
      return this.delegate.contentType();
   }

   public long contentLength() {
      return -1L;
   }

   public void writeTo(@NotNull BufferedSink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Closeable var2 = Okio.buffer(new GzipSink(sink));
      RequestBody var3 = this.delegate;
      Throwable var4 = null;

      try {
         BufferedSink p0 = (BufferedSink)var2;
         int var6 = 0;
         var3.writeTo(p0);
         Unit var11 = Unit.INSTANCE;
      } catch (Throwable var9) {
         var4 = var9;
         throw var9;
      } finally {
         CloseableKt.closeFinally(var2, var4);
      }

   }

   public boolean isOneShot() {
      return this.delegate.isOneShot();
   }
}
