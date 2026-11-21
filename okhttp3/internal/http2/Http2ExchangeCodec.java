package okhttp3.internal.http2;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RealInterceptorChain;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Sink;
import okio.Socket;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 /2\u00020\u0001:\u0001/B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0018\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0010\u0010 \u001a\u00020!2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\b\u0010\"\u001a\u00020!H\u0016J\b\u0010#\u001a\u00020!H\u0016J\u0012\u0010$\u001a\u0004\u0018\u00010%2\u0006\u0010&\u001a\u00020\u0013H\u0016J\u0010\u0010'\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020)H\u0016J\u0010\u0010*\u001a\u00020+2\u0006\u0010(\u001a\u00020)H\u0016J\n\u0010,\u001a\u0004\u0018\u00010-H\u0016J\b\u0010.\u001a\u00020!H\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\u00020\u00138VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u00178VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019¨\u00060"},
   d2 = {"Lokhttp3/internal/http2/Http2ExchangeCodec;", "Lokhttp3/internal/http/ExchangeCodec;", "client", "Lokhttp3/OkHttpClient;", "carrier", "Lokhttp3/internal/http/ExchangeCodec$Carrier;", "chain", "Lokhttp3/internal/http/RealInterceptorChain;", "http2Connection", "Lokhttp3/internal/http2/Http2Connection;", "<init>", "(Lokhttp3/OkHttpClient;Lokhttp3/internal/http/ExchangeCodec$Carrier;Lokhttp3/internal/http/RealInterceptorChain;Lokhttp3/internal/http2/Http2Connection;)V", "getCarrier", "()Lokhttp3/internal/http/ExchangeCodec$Carrier;", "stream", "Lokhttp3/internal/http2/Http2Stream;", "protocol", "Lokhttp3/Protocol;", "canceled", "", "isResponseComplete", "()Z", "socket", "Lokio/Socket;", "getSocket", "()Lokio/Socket;", "createRequestBody", "Lokio/Sink;", "request", "Lokhttp3/Request;", "contentLength", "", "writeRequestHeaders", "", "flushRequest", "finishRequest", "readResponseHeaders", "Lokhttp3/Response$Builder;", "expectContinue", "reportedContentLength", "response", "Lokhttp3/Response;", "openResponseBodySource", "Lokio/Source;", "peekTrailers", "Lokhttp3/Headers;", "cancel", "Companion", "okhttp"}
)
public final class Http2ExchangeCodec implements ExchangeCodec {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final ExchangeCodec.Carrier carrier;
   @NotNull
   private final RealInterceptorChain chain;
   @NotNull
   private final Http2Connection http2Connection;
   @Nullable
   private volatile Http2Stream stream;
   @NotNull
   private final Protocol protocol;
   private volatile boolean canceled;
   @NotNull
   private static final String CONNECTION = "connection";
   @NotNull
   private static final String HOST = "host";
   @NotNull
   private static final String KEEP_ALIVE = "keep-alive";
   @NotNull
   private static final String PROXY_CONNECTION = "proxy-connection";
   @NotNull
   private static final String TRANSFER_ENCODING = "transfer-encoding";
   @NotNull
   private static final String TE = "te";
   @NotNull
   private static final String ENCODING = "encoding";
   @NotNull
   private static final String UPGRADE = "upgrade";
   @NotNull
   private static final List<String> HTTP_2_SKIPPED_REQUEST_HEADERS;
   @NotNull
   private static final List<String> HTTP_2_SKIPPED_RESPONSE_HEADERS;

   public Http2ExchangeCodec(@NotNull OkHttpClient client, @NotNull ExchangeCodec.Carrier carrier, @NotNull RealInterceptorChain chain, @NotNull Http2Connection http2Connection) {
      Intrinsics.checkNotNullParameter(client, "client");
      Intrinsics.checkNotNullParameter(carrier, "carrier");
      Intrinsics.checkNotNullParameter(chain, "chain");
      Intrinsics.checkNotNullParameter(http2Connection, "http2Connection");
      super();
      this.carrier = carrier;
      this.chain = chain;
      this.http2Connection = http2Connection;
      this.protocol = client.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE) ? Protocol.H2_PRIOR_KNOWLEDGE : Protocol.HTTP_2;
   }

   @NotNull
   public ExchangeCodec.Carrier getCarrier() {
      return this.carrier;
   }

   public boolean isResponseComplete() {
      Http2Stream var10000 = this.stream;
      return var10000 != null ? var10000.isSourceComplete() : false;
   }

   @NotNull
   public Socket getSocket() {
      Http2Stream var10000 = this.stream;
      Intrinsics.checkNotNull(var10000);
      return var10000;
   }

   @NotNull
   public Sink createRequestBody(@NotNull Request request, long contentLength) {
      Intrinsics.checkNotNullParameter(request, "request");
      Http2Stream var10000 = this.stream;
      Intrinsics.checkNotNull(var10000);
      return var10000.getSink();
   }

   public void writeRequestHeaders(@NotNull Request request) {
      Intrinsics.checkNotNullParameter(request, "request");
      if (this.stream == null) {
         boolean hasRequestBody = request.body() != null;
         List requestHeaders = Companion.http2HeadersList(request);
         this.stream = this.http2Connection.newStream(requestHeaders, hasRequestBody);
         if (this.canceled) {
            Http2Stream var5 = this.stream;
            Intrinsics.checkNotNull(var5);
            var5.closeLater(ErrorCode.CANCEL);
            throw new IOException("Canceled");
         } else {
            Http2Stream var10000 = this.stream;
            Intrinsics.checkNotNull(var10000);
            var10000.readTimeout().timeout((long)this.chain.getReadTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
            var10000 = this.stream;
            Intrinsics.checkNotNull(var10000);
            var10000.writeTimeout().timeout((long)this.chain.getWriteTimeoutMillis$okhttp(), TimeUnit.MILLISECONDS);
         }
      }
   }

   public void flushRequest() {
      this.http2Connection.flush();
   }

   public void finishRequest() {
      Http2Stream var10000 = this.stream;
      Intrinsics.checkNotNull(var10000);
      var10000.getSink().close();
   }

   @Nullable
   public Response.Builder readResponseHeaders(boolean expectContinue) {
      Http2Stream var10000 = this.stream;
      if (var10000 == null) {
         throw new IOException("stream wasn't created");
      } else {
         Http2Stream stream = var10000;
         Headers headers = stream.takeHeaders(expectContinue);
         Response.Builder responseBuilder = Companion.readHttp2HeadersList(headers, this.protocol);
         return expectContinue && responseBuilder.getCode$okhttp() == 100 ? null : responseBuilder;
      }
   }

   public long reportedContentLength(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      return !HttpHeaders.promisesBody(response) ? 0L : _UtilJvmKt.headersContentLength(response);
   }

   @NotNull
   public Source openResponseBodySource(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      Http2Stream var10000 = this.stream;
      Intrinsics.checkNotNull(var10000);
      return var10000.getSource();
   }

   @Nullable
   public Headers peekTrailers() {
      Http2Stream var10000 = this.stream;
      Intrinsics.checkNotNull(var10000);
      return var10000.peekTrailers();
   }

   public void cancel() {
      this.canceled = true;
      Http2Stream var10000 = this.stream;
      if (var10000 != null) {
         var10000.closeLater(ErrorCode.CANCEL);
      }

   }

   static {
      String[] var0 = new String[]{"connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade", ":method", ":path", ":scheme", ":authority"};
      HTTP_2_SKIPPED_REQUEST_HEADERS = _UtilJvmKt.<String>immutableListOf(var0);
      var0 = new String[]{"connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade"};
      HTTP_2_SKIPPED_RESPONSE_HEADERS = _UtilJvmKt.<String>immutableListOf(var0);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u000e2\u0006\u0010\u0012\u001a\u00020\u0013J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001a"},
      d2 = {"Lokhttp3/internal/http2/Http2ExchangeCodec$Companion;", "", "<init>", "()V", "CONNECTION", "", "HOST", "KEEP_ALIVE", "PROXY_CONNECTION", "TRANSFER_ENCODING", "TE", "ENCODING", "UPGRADE", "HTTP_2_SKIPPED_REQUEST_HEADERS", "", "HTTP_2_SKIPPED_RESPONSE_HEADERS", "http2HeadersList", "Lokhttp3/internal/http2/Header;", "request", "Lokhttp3/Request;", "readHttp2HeadersList", "Lokhttp3/Response$Builder;", "headerBlock", "Lokhttp3/Headers;", "protocol", "Lokhttp3/Protocol;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final List<Header> http2HeadersList(@NotNull Request request) {
         Intrinsics.checkNotNullParameter(request, "request");
         Headers headers = request.headers();
         ArrayList result = new ArrayList(headers.size() + 4);
         result.add(new Header(Header.TARGET_METHOD, request.method()));
         result.add(new Header(Header.TARGET_PATH, RequestLine.INSTANCE.requestPath(request.url())));
         String host = request.header("Host");
         if (host != null) {
            result.add(new Header(Header.TARGET_AUTHORITY, host));
         }

         result.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
         int i = 0;

         for(int var6 = headers.size(); i < var6; ++i) {
            String var8 = headers.name(i);
            Locale var10000 = Locale.US;
            Intrinsics.checkNotNullExpressionValue(var10000, "US");
            String var9 = var8.toLowerCase(var10000);
            Intrinsics.checkNotNullExpressionValue(var9, "toLowerCase(...)");
            String name = var9;
            if (!Http2ExchangeCodec.HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name) || Intrinsics.areEqual((Object)name, (Object)"te") && Intrinsics.areEqual((Object)headers.value(i), (Object)"trailers")) {
               result.add(new Header(name, headers.value(i)));
            }
         }

         return (List)result;
      }

      @NotNull
      public final Response.Builder readHttp2HeadersList(@NotNull Headers headerBlock, @NotNull Protocol protocol) {
         Intrinsics.checkNotNullParameter(headerBlock, "headerBlock");
         Intrinsics.checkNotNullParameter(protocol, "protocol");
         StatusLine statusLine = null;
         Headers.Builder headersBuilder = new Headers.Builder();
         int i = 0;

         for(int var6 = headerBlock.size(); i < var6; ++i) {
            String name = headerBlock.name(i);
            String value = headerBlock.value(i);
            if (Intrinsics.areEqual((Object)name, (Object)":status")) {
               statusLine = StatusLine.Companion.parse("HTTP/1.1 " + value);
            } else if (!Http2ExchangeCodec.HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
               headersBuilder.addLenient$okhttp(name, value);
            }
         }

         if (statusLine == null) {
            throw new ProtocolException("Expected ':status' header not present");
         } else {
            return (new Response.Builder()).protocol(protocol).code(statusLine.code).message(statusLine.message).headers(headersBuilder.build());
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
