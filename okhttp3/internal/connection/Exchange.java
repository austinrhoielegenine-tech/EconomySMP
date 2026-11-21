package okhttp3.internal.connection;

import java.io.IOException;
import java.net.ProtocolException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.ExchangeCodec;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Socket;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0002BCB'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"J\u0016\u0010#\u001a\u00020$2\u0006\u0010!\u001a\u00020\"2\u0006\u0010%\u001a\u00020\u0013J\u0006\u0010&\u001a\u00020 J\u0006\u0010'\u001a\u00020 J\u0006\u0010(\u001a\u00020 J\u0010\u0010)\u001a\u0004\u0018\u00010*2\u0006\u0010+\u001a\u00020\u0013J\u000e\u0010,\u001a\u00020 2\u0006\u0010-\u001a\u00020.J\u000e\u0010/\u001a\u0002002\u0006\u0010-\u001a\u00020.J\b\u00101\u001a\u0004\u0018\u000102J\u0006\u00103\u001a\u000204J\u0006\u00105\u001a\u00020 J\u0006\u00106\u001a\u00020 J\u0006\u00107\u001a\u00020 J\u0010\u00108\u001a\u00020 2\u0006\u00109\u001a\u00020:H\u0002J8\u0010;\u001a\u0004\u0018\u00010:2\b\b\u0002\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020\u00132\b\b\u0002\u0010?\u001a\u00020\u00132\b\b\u0002\u0010@\u001a\u00020\u00132\b\u00109\u001a\u0004\u0018\u00010:J\u0006\u0010A\u001a\u00020 R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0006\u001a\u00020\u0007X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0013@BX\u0080\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001e\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0013@BX\u0080\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u0014\u0010\u0019\u001a\u00020\u001a8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u00138@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u0016¨\u0006D"},
   d2 = {"Lokhttp3/internal/connection/Exchange;", "", "call", "Lokhttp3/internal/connection/RealCall;", "eventListener", "Lokhttp3/EventListener;", "finder", "Lokhttp3/internal/connection/ExchangeFinder;", "codec", "Lokhttp3/internal/http/ExchangeCodec;", "<init>", "(Lokhttp3/internal/connection/RealCall;Lokhttp3/EventListener;Lokhttp3/internal/connection/ExchangeFinder;Lokhttp3/internal/http/ExchangeCodec;)V", "getCall$okhttp", "()Lokhttp3/internal/connection/RealCall;", "getEventListener$okhttp", "()Lokhttp3/EventListener;", "getFinder$okhttp", "()Lokhttp3/internal/connection/ExchangeFinder;", "value", "", "isDuplex", "isDuplex$okhttp", "()Z", "hasFailure", "getHasFailure$okhttp", "connection", "Lokhttp3/internal/connection/RealConnection;", "getConnection$okhttp", "()Lokhttp3/internal/connection/RealConnection;", "isCoalescedConnection", "isCoalescedConnection$okhttp", "writeRequestHeaders", "", "request", "Lokhttp3/Request;", "createRequestBody", "Lokio/Sink;", "duplex", "flushRequest", "finishRequest", "responseHeadersStart", "readResponseHeaders", "Lokhttp3/Response$Builder;", "expectContinue", "responseHeadersEnd", "response", "Lokhttp3/Response;", "openResponseBody", "Lokhttp3/ResponseBody;", "peekTrailers", "Lokhttp3/Headers;", "upgradeToSocket", "Lokio/Socket;", "noNewExchangesOnConnection", "cancel", "detachWithViolence", "trackFailure", "e", "Ljava/io/IOException;", "bodyComplete", "bytesRead", "", "isSocket", "responseDone", "requestDone", "noRequestBody", "RequestBodySink", "ResponseBodySource", "okhttp"}
)
public final class Exchange {
   @NotNull
   private final RealCall call;
   @NotNull
   private final EventListener eventListener;
   @NotNull
   private final ExchangeFinder finder;
   @NotNull
   private final ExchangeCodec codec;
   private boolean isDuplex;
   private boolean hasFailure;

   public Exchange(@NotNull RealCall call, @NotNull EventListener eventListener, @NotNull ExchangeFinder finder, @NotNull ExchangeCodec codec) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(eventListener, "eventListener");
      Intrinsics.checkNotNullParameter(finder, "finder");
      Intrinsics.checkNotNullParameter(codec, "codec");
      super();
      this.call = call;
      this.eventListener = eventListener;
      this.finder = finder;
      this.codec = codec;
   }

   @NotNull
   public final RealCall getCall$okhttp() {
      return this.call;
   }

   @NotNull
   public final EventListener getEventListener$okhttp() {
      return this.eventListener;
   }

   @NotNull
   public final ExchangeFinder getFinder$okhttp() {
      return this.finder;
   }

   public final boolean isDuplex$okhttp() {
      return this.isDuplex;
   }

   public final boolean getHasFailure$okhttp() {
      return this.hasFailure;
   }

   @NotNull
   public final RealConnection getConnection$okhttp() {
      ExchangeCodec.Carrier var1 = this.codec.getCarrier();
      RealConnection var10000 = var1 instanceof RealConnection ? (RealConnection)var1 : null;
      if ((var1 instanceof RealConnection ? (RealConnection)var1 : null) == null) {
         throw new IllegalStateException("no connection for CONNECT tunnels".toString());
      } else {
         return var10000;
      }
   }

   public final boolean isCoalescedConnection$okhttp() {
      return !Intrinsics.areEqual((Object)this.finder.getRoutePlanner().getAddress().url().host(), (Object)this.codec.getCarrier().getRoute().address().url().host());
   }

   public final void writeRequestHeaders(@NotNull Request request) throws IOException {
      Intrinsics.checkNotNullParameter(request, "request");

      try {
         this.eventListener.requestHeadersStart(this.call);
         this.codec.writeRequestHeaders(request);
         this.eventListener.requestHeadersEnd(this.call, request);
      } catch (IOException e) {
         this.eventListener.requestFailed(this.call, e);
         this.trackFailure(e);
         throw e;
      }
   }

   @NotNull
   public final Sink createRequestBody(@NotNull Request request, boolean duplex) throws IOException {
      Intrinsics.checkNotNullParameter(request, "request");
      this.isDuplex = duplex;
      RequestBody var10000 = request.body();
      Intrinsics.checkNotNull(var10000);
      long contentLength = var10000.contentLength();
      this.eventListener.requestBodyStart(this.call);
      Sink rawRequestBody = this.codec.createRequestBody(request, contentLength);
      return new RequestBodySink(rawRequestBody, contentLength, false);
   }

   public final void flushRequest() throws IOException {
      try {
         this.codec.flushRequest();
      } catch (IOException e) {
         this.eventListener.requestFailed(this.call, e);
         this.trackFailure(e);
         throw e;
      }
   }

   public final void finishRequest() throws IOException {
      try {
         this.codec.finishRequest();
      } catch (IOException e) {
         this.eventListener.requestFailed(this.call, e);
         this.trackFailure(e);
         throw e;
      }
   }

   public final void responseHeadersStart() {
      this.eventListener.responseHeadersStart(this.call);
   }

   @Nullable
   public final Response.Builder readResponseHeaders(boolean expectContinue) throws IOException {
      try {
         Response.Builder result = this.codec.readResponseHeaders(expectContinue);
         if (result != null) {
            result.initExchange$okhttp(this);
         }

         return result;
      } catch (IOException e) {
         this.eventListener.responseFailed(this.call, e);
         this.trackFailure(e);
         throw e;
      }
   }

   public final void responseHeadersEnd(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      this.eventListener.responseHeadersEnd(this.call, response);
   }

   @NotNull
   public final ResponseBody openResponseBody(@NotNull Response response) throws IOException {
      Intrinsics.checkNotNullParameter(response, "response");

      try {
         String contentType = Response.header$default(response, "Content-Type", (String)null, 2, (Object)null);
         long contentLength = this.codec.reportedContentLength(response);
         Source rawSource = this.codec.openResponseBodySource(response);
         ResponseBodySource source = new ResponseBodySource(rawSource, contentLength, false);
         return new RealResponseBody(contentType, contentLength, Okio.buffer(source));
      } catch (IOException e) {
         this.eventListener.responseFailed(this.call, e);
         this.trackFailure(e);
         throw e;
      }
   }

   @Nullable
   public final Headers peekTrailers() throws IOException {
      return this.codec.peekTrailers();
   }

   @NotNull
   public final Socket upgradeToSocket() {
      this.call.upgradeToSocket();
      ExchangeCodec.Carrier var10000 = this.codec.getCarrier();
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type okhttp3.internal.connection.RealConnection");
      ((RealConnection)var10000).useAsSocket$okhttp();
      return new Socket() {
         private final RequestBodySink sink;
         private final ResponseBodySource source;

         {
            this.sink = Exchange.this.new RequestBodySink(Exchange.this.codec.getSocket().getSink(), -1L, true);
            this.source = Exchange.this.new ResponseBodySource(Exchange.this.codec.getSocket().getSource(), -1L, true);
         }

         public void cancel() {
            Exchange.this.cancel();
         }

         public RequestBodySink getSink() {
            return this.sink;
         }

         public ResponseBodySource getSource() {
            return this.source;
         }
      };
   }

   public final void noNewExchangesOnConnection() {
      this.codec.getCarrier().noNewExchanges();
   }

   public final void cancel() {
      this.codec.cancel();
   }

   public final void detachWithViolence() {
      this.codec.cancel();
      this.call.messageDone$okhttp(this, true, true, true, true, (IOException)null);
   }

   private final void trackFailure(IOException e) {
      this.hasFailure = true;
      this.codec.getCarrier().trackFailure(this.call, e);
   }

   @Nullable
   public final IOException bodyComplete(long bytesRead, boolean isSocket, boolean responseDone, boolean requestDone, @Nullable IOException e) {
      if (e != null) {
         this.trackFailure(e);
      }

      if (requestDone) {
         if (e != null) {
            this.eventListener.requestFailed(this.call, e);
         } else {
            this.eventListener.requestBodyEnd(this.call, bytesRead);
         }
      }

      if (responseDone) {
         if (e != null) {
            this.eventListener.responseFailed(this.call, e);
         } else {
            this.eventListener.responseBodyEnd(this.call, bytesRead);
         }
      }

      RealCall var7 = this.call;
      boolean var8 = requestDone && !isSocket;
      boolean var9 = responseDone && !isSocket;
      boolean var10 = requestDone && isSocket;
      boolean var11 = responseDone && isSocket;
      return var7.messageDone$okhttp(this, var8, var9, var11, var10, e);
   }

   // $FF: synthetic method
   public static IOException bodyComplete$default(Exchange var0, long var1, boolean var3, boolean var4, boolean var5, IOException var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = -1L;
      }

      if ((var7 & 4) != 0) {
         var4 = false;
      }

      if ((var7 & 8) != 0) {
         var5 = false;
      }

      return var0.bodyComplete(var1, var3, var4, var5, var6);
   }

   public final void noRequestBody() {
      RealCall.messageDone$okhttp$default(this.call, this, true, false, false, false, (IOException)null, 28, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0005H\u0016J\b\u0010\u0013\u001a\u00020\u000fH\u0016J\b\u0010\u0014\u001a\u00020\u000fH\u0016J\u0014\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0016H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0018"},
      d2 = {"Lokhttp3/internal/connection/Exchange$RequestBodySink;", "Lokio/ForwardingSink;", "delegate", "Lokio/Sink;", "contentLength", "", "isSocket", "", "<init>", "(Lokhttp3/internal/connection/Exchange;Lokio/Sink;JZ)V", "completed", "bytesReceived", "invokeStartEvent", "closed", "write", "", "source", "Lokio/Buffer;", "byteCount", "flush", "close", "complete", "Ljava/io/IOException;", "e", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nExchange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Exchange.kt\nokhttp3/internal/connection/Exchange$RequestBodySink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,404:1\n1#2:405\n*E\n"})
   private final class RequestBodySink extends ForwardingSink {
      private final long contentLength;
      private final boolean isSocket;
      private boolean completed;
      private long bytesReceived;
      private boolean invokeStartEvent;
      private boolean closed;

      public RequestBodySink(@NotNull Sink delegate, long contentLength, boolean isSocket) {
         Intrinsics.checkNotNullParameter(delegate, "delegate");
         super(delegate);
         this.contentLength = contentLength;
         this.isSocket = isSocket;
         this.invokeStartEvent = this.isSocket;
      }

      public void write(@NotNull Buffer source, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(source, "source");
         if (this.closed) {
            int var5 = 0;
            String var7 = "closed";
            throw new IllegalStateException(var7.toString());
         } else if (this.contentLength != -1L && this.bytesReceived + byteCount > this.contentLength) {
            throw new ProtocolException("expected " + this.contentLength + " bytes but received " + (this.bytesReceived + byteCount));
         } else {
            try {
               if (this.invokeStartEvent) {
                  this.invokeStartEvent = false;
                  Exchange.this.getEventListener$okhttp().requestBodyStart(Exchange.this.getCall$okhttp());
               }

               super.write(source, byteCount);
               this.bytesReceived += byteCount;
            } catch (IOException e) {
               IOException var10000 = this.complete(e);
               Intrinsics.checkNotNull(var10000);
               throw var10000;
            }
         }
      }

      public void flush() throws IOException {
         try {
            super.flush();
         } catch (IOException e) {
            IOException var10000 = this.complete(e);
            Intrinsics.checkNotNull(var10000);
            throw var10000;
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            if (this.contentLength != -1L && this.bytesReceived != this.contentLength) {
               throw new ProtocolException("unexpected end of stream");
            } else {
               try {
                  super.close();
                  this.complete((IOException)null);
               } catch (IOException e) {
                  IOException var10000 = this.complete(e);
                  Intrinsics.checkNotNull(var10000);
                  throw var10000;
               }
            }
         }
      }

      private final IOException complete(IOException e) {
         if (this.completed) {
            return e;
         } else {
            this.completed = true;
            return Exchange.bodyComplete$default(Exchange.this, this.bytesReceived, this.isSocket, false, true, e, 4, (Object)null);
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0080\u0004\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0005H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0015R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0017"},
      d2 = {"Lokhttp3/internal/connection/Exchange$ResponseBodySource;", "Lokio/ForwardingSource;", "delegate", "Lokio/Source;", "contentLength", "", "isSocket", "", "<init>", "(Lokhttp3/internal/connection/Exchange;Lokio/Source;JZ)V", "bytesReceived", "invokeStartEvent", "completed", "closed", "read", "sink", "Lokio/Buffer;", "byteCount", "close", "", "complete", "Ljava/io/IOException;", "e", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nExchange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Exchange.kt\nokhttp3/internal/connection/Exchange$ResponseBodySource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,404:1\n1#2:405\n*E\n"})
   public final class ResponseBodySource extends ForwardingSource {
      private final long contentLength;
      private final boolean isSocket;
      private long bytesReceived;
      private boolean invokeStartEvent;
      private boolean completed;
      private boolean closed;

      public ResponseBodySource(@NotNull Source delegate, long contentLength, boolean isSocket) {
         Intrinsics.checkNotNullParameter(delegate, "delegate");
         super(delegate);
         this.contentLength = contentLength;
         this.isSocket = isSocket;
         this.invokeStartEvent = true;
         if (this.contentLength == 0L) {
            this.complete((IOException)null);
         }

      }

      public long read(@NotNull Buffer sink, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (this.closed) {
            int var5 = 0;
            String e = "closed";
            throw new IllegalStateException(e.toString());
         } else {
            try {
               long read = this.delegate().read(sink, byteCount);
               if (this.invokeStartEvent) {
                  this.invokeStartEvent = false;
                  Exchange.this.getEventListener$okhttp().responseBodyStart(Exchange.this.getCall$okhttp());
               }

               if (read == -1L) {
                  this.complete((IOException)null);
                  return -1L;
               } else {
                  long newBytesReceived = this.bytesReceived + read;
                  if (this.contentLength != -1L && newBytesReceived > this.contentLength) {
                     throw new ProtocolException("expected " + this.contentLength + " bytes but received " + newBytesReceived);
                  } else {
                     this.bytesReceived = newBytesReceived;
                     if (Exchange.this.codec.isResponseComplete()) {
                        this.complete((IOException)null);
                     }

                     return read;
                  }
               }
            } catch (IOException e) {
               IOException var10000 = this.complete(e);
               Intrinsics.checkNotNull(var10000);
               throw var10000;
            }
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;

            try {
               super.close();
               this.complete((IOException)null);
            } catch (IOException e) {
               IOException var10000 = this.complete(e);
               Intrinsics.checkNotNull(var10000);
               throw var10000;
            }
         }
      }

      @Nullable
      public final IOException complete(@Nullable IOException e) {
         if (this.completed) {
            return e;
         } else {
            this.completed = true;
            if (e == null && this.invokeStartEvent) {
               this.invokeStartEvent = false;
               Exchange.this.getEventListener$okhttp().responseBodyStart(Exchange.this.getCall$okhttp());
            }

            return Exchange.bodyComplete$default(Exchange.this, this.bytesReceived, this.isSocket, true, false, e, 8, (Object)null);
         }
      }
   }
}
