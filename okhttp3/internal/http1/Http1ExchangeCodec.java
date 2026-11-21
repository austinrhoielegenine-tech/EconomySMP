package okhttp3.internal.http1;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.connection.BufferedSocket;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http.RequestLine;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingTimeout;
import okio.Sink;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 D2\u00020\u0001:\u0007>?@ABCDB!\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u001f\u001a\u00020 H\u0016J\b\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020\"2\u0006\u0010\u001e\u001a\u00020\u0016H\u0016J\u0010\u0010$\u001a\u00020 2\u0006\u0010%\u001a\u00020\u0014H\u0016J\u0010\u0010&\u001a\u00020'2\u0006\u0010%\u001a\u00020\u0014H\u0016J\n\u0010(\u001a\u0004\u0018\u00010\u0019H\u0016J\b\u0010)\u001a\u00020\"H\u0016J\b\u0010*\u001a\u00020\"H\u0016J\u0016\u0010+\u001a\u00020\"2\u0006\u0010,\u001a\u00020\u00192\u0006\u0010-\u001a\u00020.J\u0012\u0010/\u001a\u0004\u0018\u0001002\u0006\u00101\u001a\u00020\u0013H\u0016J\b\u00102\u001a\u00020\u001dH\u0002J\b\u00103\u001a\u00020\u001dH\u0002J\u0018\u00104\u001a\u00020'2\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u00020 H\u0002J\u0010\u00108\u001a\u00020'2\u0006\u00105\u001a\u000206H\u0002J\u0010\u00109\u001a\u00020'2\u0006\u00105\u001a\u000206H\u0002J\u0010\u0010:\u001a\u00020\"2\u0006\u0010;\u001a\u00020<H\u0002J\u000e\u0010=\u001a\u00020\"2\u0006\u0010%\u001a\u00020\u0014R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u0012\u001a\u00020\u0013*\u00020\u00148BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0015R\u0018\u0010\u0012\u001a\u00020\u0013*\u00020\u00168BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0017R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\u00020\u00138VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001b¨\u0006E"},
   d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec;", "Lokhttp3/internal/http/ExchangeCodec;", "client", "Lokhttp3/OkHttpClient;", "carrier", "Lokhttp3/internal/http/ExchangeCodec$Carrier;", "socket", "Lokhttp3/internal/connection/BufferedSocket;", "<init>", "(Lokhttp3/OkHttpClient;Lokhttp3/internal/http/ExchangeCodec$Carrier;Lokhttp3/internal/connection/BufferedSocket;)V", "getCarrier", "()Lokhttp3/internal/http/ExchangeCodec$Carrier;", "getSocket", "()Lokhttp3/internal/connection/BufferedSocket;", "state", "", "headersReader", "Lokhttp3/internal/http1/HeadersReader;", "isChunked", "", "Lokhttp3/Response;", "(Lokhttp3/Response;)Z", "Lokhttp3/Request;", "(Lokhttp3/Request;)Z", "trailers", "Lokhttp3/Headers;", "isResponseComplete", "()Z", "createRequestBody", "Lokio/Sink;", "request", "contentLength", "", "cancel", "", "writeRequestHeaders", "reportedContentLength", "response", "openResponseBodySource", "Lokio/Source;", "peekTrailers", "flushRequest", "finishRequest", "writeRequest", "headers", "requestLine", "", "readResponseHeaders", "Lokhttp3/Response$Builder;", "expectContinue", "newChunkedSink", "newKnownLengthSink", "newFixedLengthSource", "url", "Lokhttp3/HttpUrl;", "length", "newChunkedSource", "newUnknownLengthSource", "detachTimeout", "timeout", "Lokio/ForwardingTimeout;", "skipConnectBody", "KnownLengthSink", "ChunkedSink", "AbstractSource", "FixedLengthSource", "ChunkedSource", "UnknownLengthSource", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"})
public final class Http1ExchangeCodec implements ExchangeCodec {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @Nullable
   private final OkHttpClient client;
   @NotNull
   private final ExchangeCodec.Carrier carrier;
   @NotNull
   private final BufferedSocket socket;
   private int state;
   @NotNull
   private final HeadersReader headersReader;
   @Nullable
   private Headers trailers;
   private static final long NO_CHUNK_YET = -1L;
   private static final int STATE_IDLE = 0;
   private static final int STATE_OPEN_REQUEST_BODY = 1;
   private static final int STATE_WRITING_REQUEST_BODY = 2;
   private static final int STATE_READ_RESPONSE_HEADERS = 3;
   private static final int STATE_OPEN_RESPONSE_BODY = 4;
   private static final int STATE_READING_RESPONSE_BODY = 5;
   private static final int STATE_CLOSED = 6;
   @NotNull
   private static final Headers TRAILERS_RESPONSE_BODY_TRUNCATED;

   public Http1ExchangeCodec(@Nullable OkHttpClient client, @NotNull ExchangeCodec.Carrier carrier, @NotNull BufferedSocket socket) {
      Intrinsics.checkNotNullParameter(carrier, "carrier");
      Intrinsics.checkNotNullParameter(socket, "socket");
      super();
      this.client = client;
      this.carrier = carrier;
      this.socket = socket;
      this.headersReader = new HeadersReader(this.getSocket().getSource());
   }

   @NotNull
   public ExchangeCodec.Carrier getCarrier() {
      return this.carrier;
   }

   @NotNull
   public BufferedSocket getSocket() {
      return this.socket;
   }

   private final boolean isChunked(Response $this$isChunked) {
      return StringsKt.equals("chunked", Response.header$default($this$isChunked, "Transfer-Encoding", (String)null, 2, (Object)null), true);
   }

   private final boolean isChunked(Request $this$isChunked) {
      return StringsKt.equals("chunked", $this$isChunked.header("Transfer-Encoding"), true);
   }

   public boolean isResponseComplete() {
      return this.state == 6;
   }

   @NotNull
   public Sink createRequestBody(@NotNull Request request, long contentLength) {
      Intrinsics.checkNotNullParameter(request, "request");
      RequestBody var10000 = request.body();
      if (var10000 != null ? var10000.isDuplex() : false) {
         throw new ProtocolException("Duplex connections are not supported for HTTP/1");
      } else {
         Sink var4;
         if (this.isChunked(request)) {
            var4 = this.newChunkedSink();
         } else {
            if (contentLength == -1L) {
               throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
            }

            var4 = this.newKnownLengthSink();
         }

         return var4;
      }
   }

   public void cancel() {
      this.getCarrier().cancel();
   }

   public void writeRequestHeaders(@NotNull Request request) {
      Intrinsics.checkNotNullParameter(request, "request");
      RequestLine var10000 = RequestLine.INSTANCE;
      Proxy.Type var10002 = this.getCarrier().getRoute().proxy().type();
      Intrinsics.checkNotNullExpressionValue(var10002, "type(...)");
      String requestLine = var10000.get(request, var10002);
      this.writeRequest(request.headers(), requestLine);
   }

   public long reportedContentLength(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      return !HttpHeaders.promisesBody(response) ? 0L : (this.isChunked(response) ? -1L : _UtilJvmKt.headersContentLength(response));
   }

   @NotNull
   public Source openResponseBodySource(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      Source var10000;
      if (!HttpHeaders.promisesBody(response)) {
         var10000 = this.newFixedLengthSource(response.request().url(), 0L);
      } else if (this.isChunked(response)) {
         var10000 = this.newChunkedSource(response.request().url());
      } else {
         long contentLength = _UtilJvmKt.headersContentLength(response);
         var10000 = contentLength != -1L ? this.newFixedLengthSource(response.request().url(), contentLength) : this.newUnknownLengthSource(response.request().url());
      }

      return var10000;
   }

   @Nullable
   public Headers peekTrailers() {
      if (this.trailers == TRAILERS_RESPONSE_BODY_TRUNCATED) {
         throw new IOException("Trailers cannot be read because the response body was truncated");
      } else if (this.state != 5 && this.state != 6) {
         int var1 = 0;
         String var2 = "Trailers cannot be read because the state is " + this.state;
         throw new IllegalStateException(var2.toString());
      } else {
         return this.trailers;
      }
   }

   public void flushRequest() {
      this.getSocket().getSink().flush();
   }

   public void finishRequest() {
      this.getSocket().getSink().flush();
   }

   public final void writeRequest(@NotNull Headers headers, @NotNull String requestLine) {
      Intrinsics.checkNotNullParameter(headers, "headers");
      Intrinsics.checkNotNullParameter(requestLine, "requestLine");
      if (this.state != 0) {
         int var5 = 0;
         String var6 = "state: " + this.state;
         throw new IllegalStateException(var6.toString());
      } else {
         this.getSocket().getSink().writeUtf8(requestLine).writeUtf8("\r\n");
         int i = 0;

         for(int var4 = headers.size(); i < var4; ++i) {
            this.getSocket().getSink().writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
         }

         this.getSocket().getSink().writeUtf8("\r\n");
         this.state = 1;
      }
   }

   @Nullable
   public Response.Builder readResponseHeaders(boolean expectContinue) {
      if (this.state != 0 && this.state != 1 && this.state != 2 && this.state != 3) {
         int responseBuilder = 0;
         String e = "state: " + this.state;
         throw new IllegalStateException(e.toString());
      } else {
         try {
            StatusLine statusLine = StatusLine.Companion.parse(this.headersReader.readLine());
            Response.Builder responseBuilder = (new Response.Builder()).protocol(statusLine.protocol).code(statusLine.code).message(statusLine.message).headers(this.headersReader.readHeaders());
            Response.Builder var10000;
            if (expectContinue && statusLine.code == 100) {
               var10000 = null;
            } else if (statusLine.code == 100) {
               this.state = 3;
               var10000 = responseBuilder;
            } else {
               int var8 = statusLine.code;
               if (102 <= var8 ? var8 < 200 : false) {
                  this.state = 3;
                  var10000 = responseBuilder;
               } else {
                  this.state = 4;
                  var10000 = responseBuilder;
               }
            }

            return var10000;
         } catch (EOFException e) {
            String address = this.getCarrier().getRoute().address().url().redact();
            throw new IOException("unexpected end of stream on " + address, (Throwable)e);
         }
      }
   }

   private final Sink newChunkedSink() {
      if (this.state != 1) {
         int var1 = 0;
         String var2 = "state: " + this.state;
         throw new IllegalStateException(var2.toString());
      } else {
         this.state = 2;
         return new ChunkedSink();
      }
   }

   private final Sink newKnownLengthSink() {
      if (this.state != 1) {
         int var1 = 0;
         String var2 = "state: " + this.state;
         throw new IllegalStateException(var2.toString());
      } else {
         this.state = 2;
         return new KnownLengthSink();
      }
   }

   private final Source newFixedLengthSource(HttpUrl url, long length) {
      if (this.state != 4) {
         int var4 = 0;
         String var5 = "state: " + this.state;
         throw new IllegalStateException(var5.toString());
      } else {
         this.state = 5;
         return new FixedLengthSource(url, length);
      }
   }

   private final Source newChunkedSource(HttpUrl url) {
      if (this.state != 4) {
         int var2 = 0;
         String var3 = "state: " + this.state;
         throw new IllegalStateException(var3.toString());
      } else {
         this.state = 5;
         return new ChunkedSource(url);
      }
   }

   private final Source newUnknownLengthSource(HttpUrl url) {
      if (this.state != 4) {
         int var2 = 0;
         String var3 = "state: " + this.state;
         throw new IllegalStateException(var3.toString());
      } else {
         this.state = 5;
         this.getCarrier().noNewExchanges();
         return new UnknownLengthSource(url);
      }
   }

   private final void detachTimeout(ForwardingTimeout timeout) {
      Timeout oldDelegate = timeout.delegate();
      timeout.setDelegate(Timeout.NONE);
      oldDelegate.clearDeadline();
      oldDelegate.clearTimeout();
   }

   public final void skipConnectBody(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      long contentLength = _UtilJvmKt.headersContentLength(response);
      if (contentLength != -1L) {
         Source body = this.newFixedLengthSource(response.request().url(), contentLength);
         _UtilJvmKt.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
         body.close();
      }
   }

   static {
      Headers.Companion var10000 = Headers.Companion;
      String[] var0 = new String[]{"OkHttp-Response-Body", "Truncated"};
      TRAILERS_RESPONSE_BODY_TRUNCATED = var10000.of(var0);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\nH\u0016J\b\u0010\u0010\u001a\u00020\nH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0011"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$KnownLengthSink;", "Lokio/Sink;", "<init>", "(Lokhttp3/internal/http1/Http1ExchangeCodec;)V", "timeout", "Lokio/ForwardingTimeout;", "closed", "", "Lokio/Timeout;", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "flush", "close", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$KnownLengthSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"})
   private final class KnownLengthSink implements Sink {
      @NotNull
      private final ForwardingTimeout timeout = new ForwardingTimeout(Http1ExchangeCodec.this.getSocket().getSink().timeout());
      private boolean closed;

      public KnownLengthSink() {
      }

      @NotNull
      public Timeout timeout() {
         return this.timeout;
      }

      public void write(@NotNull Buffer source, long byteCount) {
         Intrinsics.checkNotNullParameter(source, "source");
         if (this.closed) {
            int var4 = 0;
            String var5 = "closed";
            throw new IllegalStateException(var5.toString());
         } else {
            _UtilCommonKt.checkOffsetAndCount(source.size(), 0L, byteCount);
            Http1ExchangeCodec.this.getSocket().getSink().write(source, byteCount);
         }
      }

      public void flush() {
         if (!this.closed) {
            Http1ExchangeCodec.this.getSocket().getSink().flush();
         }
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 3;
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\nH\u0016J\b\u0010\u0010\u001a\u00020\nH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0011"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$ChunkedSink;", "Lokio/Sink;", "<init>", "(Lokhttp3/internal/http1/Http1ExchangeCodec;)V", "timeout", "Lokio/ForwardingTimeout;", "closed", "", "Lokio/Timeout;", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "flush", "close", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$ChunkedSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"})
   private final class ChunkedSink implements Sink {
      @NotNull
      private final ForwardingTimeout timeout = new ForwardingTimeout(Http1ExchangeCodec.this.getSocket().getSink().timeout());
      private boolean closed;

      public ChunkedSink() {
      }

      @NotNull
      public Timeout timeout() {
         return this.timeout;
      }

      public void write(@NotNull Buffer source, long byteCount) {
         Intrinsics.checkNotNullParameter(source, "source");
         if (this.closed) {
            int $this$write_u24lambda_u241 = 0;
            String var7 = "closed";
            throw new IllegalStateException(var7.toString());
         } else if (byteCount != 0L) {
            BufferedSink $this$write_u24lambda_u241 = Http1ExchangeCodec.this.getSocket().getSink();
            int var5 = 0;
            $this$write_u24lambda_u241.writeHexadecimalUnsignedLong(byteCount);
            $this$write_u24lambda_u241.writeUtf8("\r\n");
            $this$write_u24lambda_u241.write(source, byteCount);
            $this$write_u24lambda_u241.writeUtf8("\r\n");
         }
      }

      public synchronized void flush() {
         if (!this.closed) {
            Http1ExchangeCodec.this.getSocket().getSink().flush();
         }
      }

      public synchronized void close() {
         if (!this.closed) {
            this.closed = true;
            Http1ExchangeCodec.this.getSocket().getSink().writeUtf8("0\r\n\r\n");
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 3;
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b¢\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u00020\u0012H\u0016J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0014H\u0016J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\rX\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011¨\u0006\u001c"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$AbstractSource;", "Lokio/Source;", "url", "Lokhttp3/HttpUrl;", "<init>", "(Lokhttp3/internal/http1/Http1ExchangeCodec;Lokhttp3/HttpUrl;)V", "getUrl", "()Lokhttp3/HttpUrl;", "timeout", "Lokio/ForwardingTimeout;", "getTimeout", "()Lokio/ForwardingTimeout;", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "Lokio/Timeout;", "read", "", "sink", "Lokio/Buffer;", "byteCount", "responseBodyComplete", "", "trailers", "Lokhttp3/Headers;", "okhttp"}
   )
   private abstract class AbstractSource implements Source {
      @NotNull
      private final HttpUrl url;
      @NotNull
      private final ForwardingTimeout timeout;
      private boolean closed;

      public AbstractSource(@NotNull HttpUrl url) {
         Intrinsics.checkNotNullParameter(url, "url");
         super();
         this.url = url;
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.getSocket().getSource().timeout());
      }

      @NotNull
      public final HttpUrl getUrl() {
         return this.url;
      }

      @NotNull
      protected final ForwardingTimeout getTimeout() {
         return this.timeout;
      }

      protected final boolean getClosed() {
         return this.closed;
      }

      protected final void setClosed(boolean var1) {
         this.closed = var1;
      }

      @NotNull
      public Timeout timeout() {
         return this.timeout;
      }

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");

         try {
            long var4 = Http1ExchangeCodec.this.getSocket().getSource().read(sink, byteCount);
            return var4;
         } catch (IOException e) {
            Http1ExchangeCodec.this.getCarrier().noNewExchanges();
            this.responseBodyComplete(Http1ExchangeCodec.TRAILERS_RESPONSE_BODY_TRUNCATED);
            throw e;
         }
      }

      public final void responseBodyComplete(@NotNull Headers trailers) {
         Intrinsics.checkNotNullParameter(trailers, "trailers");
         if (Http1ExchangeCodec.this.state != 6) {
            if (Http1ExchangeCodec.this.state != 5) {
               throw new IllegalStateException("state: " + Http1ExchangeCodec.this.state);
            } else {
               Http1ExchangeCodec.this.detachTimeout(this.timeout);
               Http1ExchangeCodec.this.trailers = trailers;
               Http1ExchangeCodec.this.state = 6;
               if (trailers.size() > 0) {
                  OkHttpClient var10000 = Http1ExchangeCodec.this.client;
                  if (var10000 != null) {
                     CookieJar var2 = var10000.cookieJar();
                     if (var2 != null) {
                        HttpHeaders.receiveHeaders(var2, this.url, trailers);
                     }
                  }
               }

            }
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u0018\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0006H\u0016J\b\u0010\r\u001a\u00020\u000eH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000f"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$FixedLengthSource;", "Lokhttp3/internal/http1/Http1ExchangeCodec$AbstractSource;", "Lokhttp3/internal/http1/Http1ExchangeCodec;", "url", "Lokhttp3/HttpUrl;", "bytesRemaining", "", "<init>", "(Lokhttp3/internal/http1/Http1ExchangeCodec;Lokhttp3/HttpUrl;J)V", "read", "sink", "Lokio/Buffer;", "byteCount", "close", "", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$FixedLengthSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"})
   private final class FixedLengthSource extends AbstractSource {
      private long bytesRemaining;

      public FixedLengthSource(@NotNull HttpUrl url, long bytesRemaining) {
         Intrinsics.checkNotNullParameter(url, "url");
         super(url);
         this.bytesRemaining = bytesRemaining;
         if (this.bytesRemaining == 0L) {
            this.responseBodyComplete(Headers.EMPTY);
         }

      }

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (byteCount < 0L) {
            int var8 = 0;
            String var9 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var9.toString());
         } else if (this.getClosed()) {
            int var5 = 0;
            String var7 = "closed";
            throw new IllegalStateException(var7.toString());
         } else if (this.bytesRemaining == 0L) {
            return -1L;
         } else {
            long read = super.read(sink, Math.min(this.bytesRemaining, byteCount));
            if (read == -1L) {
               Http1ExchangeCodec.this.getCarrier().noNewExchanges();
               ProtocolException e = new ProtocolException("unexpected end of stream");
               this.responseBodyComplete(Http1ExchangeCodec.TRAILERS_RESPONSE_BODY_TRUNCATED);
               throw e;
            } else {
               this.bytesRemaining -= read;
               if (this.bytesRemaining == 0L) {
                  this.responseBodyComplete(Headers.EMPTY);
               }

               return read;
            }
         }
      }

      public void close() {
         if (!this.getClosed()) {
            if (this.bytesRemaining != 0L && !_UtilJvmKt.discard(this, 100, TimeUnit.MILLISECONDS)) {
               Http1ExchangeCodec.this.getCarrier().noNewExchanges();
               this.responseBodyComplete(Http1ExchangeCodec.TRAILERS_RESPONSE_BODY_TRUNCATED);
            }

            this.setClosed(true);
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0018\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\bH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u0010H\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$ChunkedSource;", "Lokhttp3/internal/http1/Http1ExchangeCodec$AbstractSource;", "Lokhttp3/internal/http1/Http1ExchangeCodec;", "url", "Lokhttp3/HttpUrl;", "<init>", "(Lokhttp3/internal/http1/Http1ExchangeCodec;Lokhttp3/HttpUrl;)V", "bytesRemainingInChunk", "", "hasMoreChunks", "", "read", "sink", "Lokio/Buffer;", "byteCount", "readChunkSize", "", "close", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$ChunkedSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"})
   private final class ChunkedSource extends AbstractSource {
      private long bytesRemainingInChunk;
      private boolean hasMoreChunks;

      public ChunkedSource(@NotNull HttpUrl url) {
         Intrinsics.checkNotNullParameter(url, "url");
         super(url);
         this.bytesRemainingInChunk = -1L;
         this.hasMoreChunks = true;
      }

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (byteCount < 0L) {
            int var8 = 0;
            String var9 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var9.toString());
         } else if (this.getClosed()) {
            int var5 = 0;
            String var7 = "closed";
            throw new IllegalStateException(var7.toString());
         } else if (!this.hasMoreChunks) {
            return -1L;
         } else {
            if (this.bytesRemainingInChunk == 0L || this.bytesRemainingInChunk == -1L) {
               this.readChunkSize();
               if (!this.hasMoreChunks) {
                  return -1L;
               }
            }

            long read = super.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
            if (read == -1L) {
               Http1ExchangeCodec.this.getCarrier().noNewExchanges();
               ProtocolException e = new ProtocolException("unexpected end of stream");
               this.responseBodyComplete(Http1ExchangeCodec.TRAILERS_RESPONSE_BODY_TRUNCATED);
               throw e;
            } else {
               this.bytesRemainingInChunk -= read;
               return read;
            }
         }
      }

      private final void readChunkSize() {
         if (this.bytesRemainingInChunk != -1L) {
            Http1ExchangeCodec.this.getSocket().getSource().readUtf8LineStrict();
         }

         try {
            this.bytesRemainingInChunk = Http1ExchangeCodec.this.getSocket().getSource().readHexadecimalUnsignedLong();
            String extensions = StringsKt.trim((CharSequence)Http1ExchangeCodec.this.getSocket().getSource().readUtf8LineStrict()).toString();
            if (this.bytesRemainingInChunk < 0L || ((CharSequence)extensions).length() > 0 && !StringsKt.startsWith$default(extensions, ";", false, 2, (Object)null)) {
               throw new ProtocolException("expected chunk size and optional extensions but was \"" + this.bytesRemainingInChunk + extensions + '"');
            }
         } catch (NumberFormatException e) {
            throw new ProtocolException(e.getMessage());
         }

         if (this.bytesRemainingInChunk == 0L) {
            this.hasMoreChunks = false;
            Headers trailers = Http1ExchangeCodec.this.headersReader.readHeaders();
            this.responseBodyComplete(trailers);
         }

      }

      public void close() {
         if (!this.getClosed()) {
            if (this.hasMoreChunks && !_UtilJvmKt.discard(this, 100, TimeUnit.MILLISECONDS)) {
               Http1ExchangeCodec.this.getCarrier().noNewExchanges();
               this.responseBodyComplete(Http1ExchangeCodec.TRAILERS_RESPONSE_BODY_TRUNCATED);
            }

            this.setClosed(true);
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\nH\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0010"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$UnknownLengthSource;", "Lokhttp3/internal/http1/Http1ExchangeCodec$AbstractSource;", "Lokhttp3/internal/http1/Http1ExchangeCodec;", "url", "Lokhttp3/HttpUrl;", "<init>", "(Lokhttp3/internal/http1/Http1ExchangeCodec;Lokhttp3/HttpUrl;)V", "inputExhausted", "", "read", "", "sink", "Lokio/Buffer;", "byteCount", "close", "", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nHttp1ExchangeCodec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http1ExchangeCodec.kt\nokhttp3/internal/http1/Http1ExchangeCodec$UnknownLengthSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"})
   private final class UnknownLengthSource extends AbstractSource {
      private boolean inputExhausted;

      public UnknownLengthSource(@NotNull HttpUrl url) {
         Intrinsics.checkNotNullParameter(url, "url");
         super(url);
      }

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (byteCount < 0L) {
            int var7 = 0;
            String var8 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var8.toString());
         } else if (this.getClosed()) {
            int var5 = 0;
            String var6 = "closed";
            throw new IllegalStateException(var6.toString());
         } else if (this.inputExhausted) {
            return -1L;
         } else {
            long read = super.read(sink, byteCount);
            if (read == -1L) {
               this.inputExhausted = true;
               this.responseBodyComplete(Headers.EMPTY);
               return -1L;
            } else {
               return read;
            }
         }
      }

      public void close() {
         if (!this.getClosed()) {
            if (!this.inputExhausted) {
               this.responseBodyComplete(Http1ExchangeCodec.TRAILERS_RESPONSE_BODY_TRUNCATED);
            }

            this.setClosed(true);
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
      d2 = {"Lokhttp3/internal/http1/Http1ExchangeCodec$Companion;", "", "<init>", "()V", "NO_CHUNK_YET", "", "STATE_IDLE", "", "STATE_OPEN_REQUEST_BODY", "STATE_WRITING_REQUEST_BODY", "STATE_READ_RESPONSE_HEADERS", "STATE_OPEN_RESPONSE_BODY", "STATE_READING_RESPONSE_BODY", "STATE_CLOSED", "TRAILERS_RESPONSE_BODY_TRUNCATED", "Lokhttp3/Headers;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
