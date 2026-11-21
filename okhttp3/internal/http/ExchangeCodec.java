package okhttp3.internal.http;

import java.io.IOException;
import kotlin.Metadata;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.connection.RealCall;
import okio.Sink;
import okio.Socket;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u0000 #2\u00020\u0001:\u0002\"#J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H&J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u000f\u001a\u00020\u0010H&J\b\u0010\u0015\u001a\u00020\u0014H&J\b\u0010\u0016\u001a\u00020\u0014H&J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\u0007H&J\u0010\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cH&J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001b\u001a\u00020\u001cH&J\n\u0010\u001f\u001a\u0004\u0018\u00010 H&J\b\u0010!\u001a\u00020\u0014H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\bR\u0012\u0010\t\u001a\u00020\nX¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f¨\u0006$À\u0006\u0003"},
   d2 = {"Lokhttp3/internal/http/ExchangeCodec;", "", "carrier", "Lokhttp3/internal/http/ExchangeCodec$Carrier;", "getCarrier", "()Lokhttp3/internal/http/ExchangeCodec$Carrier;", "isResponseComplete", "", "()Z", "socket", "Lokio/Socket;", "getSocket", "()Lokio/Socket;", "createRequestBody", "Lokio/Sink;", "request", "Lokhttp3/Request;", "contentLength", "", "writeRequestHeaders", "", "flushRequest", "finishRequest", "readResponseHeaders", "Lokhttp3/Response$Builder;", "expectContinue", "reportedContentLength", "response", "Lokhttp3/Response;", "openResponseBodySource", "Lokio/Source;", "peekTrailers", "Lokhttp3/Headers;", "cancel", "Carrier", "Companion", "okhttp"}
)
public interface ExchangeCodec {
   @NotNull
   Companion Companion = ExchangeCodec.Companion.$$INSTANCE;
   int DISCARD_STREAM_TIMEOUT_MILLIS = 100;

   @NotNull
   Carrier getCarrier();

   boolean isResponseComplete();

   @NotNull
   Socket getSocket();

   @NotNull
   Sink createRequestBody(@NotNull Request var1, long var2) throws IOException;

   void writeRequestHeaders(@NotNull Request var1) throws IOException;

   void flushRequest() throws IOException;

   void finishRequest() throws IOException;

   @Nullable
   Response.Builder readResponseHeaders(boolean var1) throws IOException;

   long reportedContentLength(@NotNull Response var1) throws IOException;

   @NotNull
   Source openResponseBodySource(@NotNull Response var1) throws IOException;

   @Nullable
   Headers peekTrailers() throws IOException;

   void cancel();

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/http/ExchangeCodec$Companion;", "", "<init>", "()V", "DISCARD_STREAM_TIMEOUT_MILLIS", "", "okhttp"}
   )
   public static final class Companion {
      // $FF: synthetic field
      static final Companion $$INSTANCE = new Companion();
      public static final int DISCARD_STREAM_TIMEOUT_MILLIS = 100;

      private Companion() {
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u001a\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH&J\b\u0010\f\u001a\u00020\u0007H&J\b\u0010\r\u001a\u00020\u0007H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005¨\u0006\u000eÀ\u0006\u0003"},
      d2 = {"Lokhttp3/internal/http/ExchangeCodec$Carrier;", "", "route", "Lokhttp3/Route;", "getRoute", "()Lokhttp3/Route;", "trackFailure", "", "call", "Lokhttp3/internal/connection/RealCall;", "e", "Ljava/io/IOException;", "noNewExchanges", "cancel", "okhttp"}
   )
   public interface Carrier {
      @NotNull
      Route getRoute();

      void trackFailure(@NotNull RealCall var1, @Nullable IOException var2);

      void noNewExchanges();

      void cancel();
   }
}
