package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.Socket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0001PB\u008d\u0001\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0000\u0012\b\u0010\u0013\u001a\u0004\u0018\u00010\u0000\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0000\u0012\u0006\u0010\u0015\u001a\u00020\u0016\u0012\u0006\u0010\u0017\u001a\u00020\u0016\u0012\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019\u0012\u0006\u0010\u001a\u001a\u00020\u001b¢\u0006\u0004\b\u001c\u0010\u001dJ\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b/J\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\b0J\r\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b1J\r\u0010\u0006\u001a\u00020\u0007H\u0007¢\u0006\u0002\b5J\u000f\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0007¢\u0006\u0002\b6J\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u0007072\u0006\u00108\u001a\u00020\u0007J\u001e\u00109\u001a\u0004\u0018\u00010\u00072\u0006\u00108\u001a\u00020\u00072\n\b\u0002\u0010:\u001a\u0004\u0018\u00010\u0007H\u0007J\r\u0010\f\u001a\u00020\rH\u0007¢\u0006\u0002\b;J\u0006\u0010<\u001a\u00020\rJ\b\u0010=\u001a\u0004\u0018\u00010\rJ\u000e\u0010>\u001a\u00020\u000f2\u0006\u0010?\u001a\u00020\u0016J\r\u0010\u000e\u001a\u00020\u000fH\u0007¢\u0006\u0002\b@J\u0006\u0010A\u001a\u00020BJ\u000f\u0010\u0012\u001a\u0004\u0018\u00010\u0000H\u0007¢\u0006\u0002\bDJ\u000f\u0010\u0013\u001a\u0004\u0018\u00010\u0000H\u0007¢\u0006\u0002\bEJ\u000f\u0010\u0014\u001a\u0004\u0018\u00010\u0000H\u0007¢\u0006\u0002\bFJ\f\u0010G\u001a\b\u0012\u0004\u0012\u00020H07J\r\u0010I\u001a\u00020*H\u0007¢\u0006\u0002\bJJ\r\u0010\u0015\u001a\u00020\u0016H\u0007¢\u0006\u0002\bKJ\r\u0010\u0017\u001a\u00020\u0016H\u0007¢\u0006\u0002\bLJ\b\u0010M\u001a\u00020NH\u0016J\b\u0010O\u001a\u00020\u0007H\u0016R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u001eR\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001fR\u0013\u0010\u0006\u001a\u00020\u00078\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010 R\u0013\u0010\b\u001a\u00020\t8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010!R\u0015\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\"R\u0013\u0010\f\u001a\u00020\r8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010#R\u0013\u0010\u000e\u001a\u00020\u000f8\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010$R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\u00118\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010%R\u0015\u0010\u0012\u001a\u0004\u0018\u00010\u00008\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010&R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\u00008\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010&R\u0015\u0010\u0014\u001a\u0004\u0018\u00010\u00008\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010&R\u0013\u0010\u0015\u001a\u00020\u00168\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010'R\u0013\u0010\u0017\u001a\u00020\u00168\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010'R\u0018\u0010\u0018\u001a\u0004\u0018\u00010\u00198\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010(R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010)\u001a\u0004\u0018\u00010*X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u0011\u00102\u001a\u000203¢\u0006\b\n\u0000\u001a\u0004\b2\u00104R\u0011\u0010C\u001a\u000203¢\u0006\b\n\u0000\u001a\u0004\bC\u00104R\u0011\u0010I\u001a\u00020*8G¢\u0006\u0006\u001a\u0004\bI\u0010,¨\u0006Q"},
   d2 = {"Lokhttp3/Response;", "Ljava/io/Closeable;", "request", "Lokhttp3/Request;", "protocol", "Lokhttp3/Protocol;", "message", "", "code", "", "handshake", "Lokhttp3/Handshake;", "headers", "Lokhttp3/Headers;", "body", "Lokhttp3/ResponseBody;", "socket", "Lokio/Socket;", "networkResponse", "cacheResponse", "priorResponse", "sentRequestAtMillis", "", "receivedResponseAtMillis", "exchange", "Lokhttp3/internal/connection/Exchange;", "trailersSource", "Lokhttp3/TrailersSource;", "<init>", "(Lokhttp3/Request;Lokhttp3/Protocol;Ljava/lang/String;ILokhttp3/Handshake;Lokhttp3/Headers;Lokhttp3/ResponseBody;Lokio/Socket;Lokhttp3/Response;Lokhttp3/Response;Lokhttp3/Response;JJLokhttp3/internal/connection/Exchange;Lokhttp3/TrailersSource;)V", "()Lokhttp3/Request;", "()Lokhttp3/Protocol;", "()Ljava/lang/String;", "()I", "()Lokhttp3/Handshake;", "()Lokhttp3/Headers;", "()Lokhttp3/ResponseBody;", "()Lokio/Socket;", "()Lokhttp3/Response;", "()J", "()Lokhttp3/internal/connection/Exchange;", "lazyCacheControl", "Lokhttp3/CacheControl;", "getLazyCacheControl$okhttp", "()Lokhttp3/CacheControl;", "setLazyCacheControl$okhttp", "(Lokhttp3/CacheControl;)V", "-deprecated_request", "-deprecated_protocol", "-deprecated_code", "isSuccessful", "", "()Z", "-deprecated_message", "-deprecated_handshake", "", "name", "header", "defaultValue", "-deprecated_headers", "trailers", "peekTrailers", "peekBody", "byteCount", "-deprecated_body", "newBuilder", "Lokhttp3/Response$Builder;", "isRedirect", "-deprecated_networkResponse", "-deprecated_cacheResponse", "-deprecated_priorResponse", "challenges", "Lokhttp3/Challenge;", "cacheControl", "-deprecated_cacheControl", "-deprecated_sentRequestAtMillis", "-deprecated_receivedResponseAtMillis", "close", "", "toString", "Builder", "okhttp"}
)
public final class Response implements Closeable {
   @NotNull
   private final Request request;
   @NotNull
   private final Protocol protocol;
   @NotNull
   private final String message;
   private final int code;
   @Nullable
   private final Handshake handshake;
   @NotNull
   private final Headers headers;
   @NotNull
   private final ResponseBody body;
   @Nullable
   private final Socket socket;
   @Nullable
   private final Response networkResponse;
   @Nullable
   private final Response cacheResponse;
   @Nullable
   private final Response priorResponse;
   private final long sentRequestAtMillis;
   private final long receivedResponseAtMillis;
   @Nullable
   private final Exchange exchange;
   @NotNull
   private TrailersSource trailersSource;
   @Nullable
   private CacheControl lazyCacheControl;
   private final boolean isSuccessful;
   private final boolean isRedirect;

   public Response(@NotNull Request request, @NotNull Protocol protocol, @NotNull String message, int code, @Nullable Handshake handshake, @NotNull Headers headers, @NotNull ResponseBody body, @Nullable Socket socket, @Nullable Response networkResponse, @Nullable Response cacheResponse, @Nullable Response priorResponse, long sentRequestAtMillis, long receivedResponseAtMillis, @Nullable Exchange exchange, @NotNull TrailersSource trailersSource) {
      Intrinsics.checkNotNullParameter(request, "request");
      Intrinsics.checkNotNullParameter(protocol, "protocol");
      Intrinsics.checkNotNullParameter(message, "message");
      Intrinsics.checkNotNullParameter(headers, "headers");
      Intrinsics.checkNotNullParameter(body, "body");
      Intrinsics.checkNotNullParameter(trailersSource, "trailersSource");
      super();
      this.request = request;
      this.protocol = protocol;
      this.message = message;
      this.code = code;
      this.handshake = handshake;
      this.headers = headers;
      this.body = body;
      this.socket = socket;
      this.networkResponse = networkResponse;
      this.cacheResponse = cacheResponse;
      this.priorResponse = priorResponse;
      this.sentRequestAtMillis = sentRequestAtMillis;
      this.receivedResponseAtMillis = receivedResponseAtMillis;
      this.exchange = exchange;
      this.trailersSource = trailersSource;
      int var18 = this.code;
      this.isSuccessful = 200 <= var18 ? var18 < 300 : false;
      boolean var10001;
      switch (this.code) {
         case 300:
         case 301:
         case 302:
         case 303:
         case 307:
         case 308:
            var10001 = true;
            break;
         case 304:
         case 305:
         case 306:
         default:
            var10001 = false;
      }

      this.isRedirect = var10001;
   }

   @JvmName(
      name = "request"
   )
   @NotNull
   public final Request request() {
      return this.request;
   }

   @JvmName(
      name = "protocol"
   )
   @NotNull
   public final Protocol protocol() {
      return this.protocol;
   }

   @JvmName(
      name = "message"
   )
   @NotNull
   public final String message() {
      return this.message;
   }

   @JvmName(
      name = "code"
   )
   public final int code() {
      return this.code;
   }

   @JvmName(
      name = "handshake"
   )
   @Nullable
   public final Handshake handshake() {
      return this.handshake;
   }

   @JvmName(
      name = "headers"
   )
   @NotNull
   public final Headers headers() {
      return this.headers;
   }

   @JvmName(
      name = "body"
   )
   @NotNull
   public final ResponseBody body() {
      return this.body;
   }

   @JvmName(
      name = "socket"
   )
   @Nullable
   public final Socket socket() {
      return this.socket;
   }

   @JvmName(
      name = "networkResponse"
   )
   @Nullable
   public final Response networkResponse() {
      return this.networkResponse;
   }

   @JvmName(
      name = "cacheResponse"
   )
   @Nullable
   public final Response cacheResponse() {
      return this.cacheResponse;
   }

   @JvmName(
      name = "priorResponse"
   )
   @Nullable
   public final Response priorResponse() {
      return this.priorResponse;
   }

   @JvmName(
      name = "sentRequestAtMillis"
   )
   public final long sentRequestAtMillis() {
      return this.sentRequestAtMillis;
   }

   @JvmName(
      name = "receivedResponseAtMillis"
   )
   public final long receivedResponseAtMillis() {
      return this.receivedResponseAtMillis;
   }

   @JvmName(
      name = "exchange"
   )
   @Nullable
   public final Exchange exchange() {
      return this.exchange;
   }

   @Nullable
   public final CacheControl getLazyCacheControl$okhttp() {
      return this.lazyCacheControl;
   }

   public final void setLazyCacheControl$okhttp(@Nullable CacheControl var1) {
      this.lazyCacheControl = var1;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "request",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_request"
   )
   @NotNull
   public final Request _deprecated_request/* $FF was: -deprecated_request*/() {
      return this.request;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "protocol",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_protocol"
   )
   @NotNull
   public final Protocol _deprecated_protocol/* $FF was: -deprecated_protocol*/() {
      return this.protocol;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "code",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_code"
   )
   public final int _deprecated_code/* $FF was: -deprecated_code*/() {
      return this.code;
   }

   public final boolean isSuccessful() {
      return this.isSuccessful;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "message",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_message"
   )
   @NotNull
   public final String _deprecated_message/* $FF was: -deprecated_message*/() {
      return this.message;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "handshake",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_handshake"
   )
   @Nullable
   public final Handshake _deprecated_handshake/* $FF was: -deprecated_handshake*/() {
      return this.handshake;
   }

   @NotNull
   public final List<String> headers(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return this.headers.values(name);
   }

   @JvmOverloads
   @Nullable
   public final String header(@NotNull String name, @Nullable String defaultValue) {
      Intrinsics.checkNotNullParameter(name, "name");
      String var10000 = this.headers.get(name);
      if (var10000 == null) {
         var10000 = defaultValue;
      }

      return var10000;
   }

   // $FF: synthetic method
   public static String header$default(Response var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.header(var1, var2);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "headers",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_headers"
   )
   @NotNull
   public final Headers _deprecated_headers/* $FF was: -deprecated_headers*/() {
      return this.headers;
   }

   @NotNull
   public final Headers trailers() throws IOException {
      return this.trailersSource.get();
   }

   @Nullable
   public final Headers peekTrailers() throws IOException {
      return this.trailersSource.peek();
   }

   @NotNull
   public final ResponseBody peekBody(long byteCount) throws IOException {
      BufferedSource peeked = this.body.source().peek();
      Buffer buffer = new Buffer();
      peeked.request(byteCount);
      buffer.write(peeked, Math.min(byteCount, peeked.getBuffer().size()));
      return ResponseBody.Companion.create(buffer, this.body.contentType(), buffer.size());
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "body",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_body"
   )
   @NotNull
   public final ResponseBody _deprecated_body/* $FF was: -deprecated_body*/() {
      return this.body;
   }

   @NotNull
   public final Builder newBuilder() {
      return new Builder(this);
   }

   public final boolean isRedirect() {
      return this.isRedirect;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "networkResponse",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_networkResponse"
   )
   @Nullable
   public final Response _deprecated_networkResponse/* $FF was: -deprecated_networkResponse*/() {
      return this.networkResponse;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cacheResponse",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cacheResponse"
   )
   @Nullable
   public final Response _deprecated_cacheResponse/* $FF was: -deprecated_cacheResponse*/() {
      return this.cacheResponse;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "priorResponse",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_priorResponse"
   )
   @Nullable
   public final Response _deprecated_priorResponse/* $FF was: -deprecated_priorResponse*/() {
      return this.priorResponse;
   }

   @NotNull
   public final List<Challenge> challenges() {
      Headers var10000 = this.headers;
      String var10001;
      switch (this.code) {
         case 401:
            var10001 = "WWW-Authenticate";
            break;
         case 407:
            var10001 = "Proxy-Authenticate";
            break;
         default:
            return CollectionsKt.emptyList();
      }

      return HttpHeaders.parseChallenges(var10000, var10001);
   }

   @JvmName(
      name = "cacheControl"
   )
   @NotNull
   public final CacheControl cacheControl() {
      CacheControl result = this.lazyCacheControl;
      if (result == null) {
         result = CacheControl.Companion.parse(this.headers);
         this.lazyCacheControl = result;
      }

      return result;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "cacheControl",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_cacheControl"
   )
   @NotNull
   public final CacheControl _deprecated_cacheControl/* $FF was: -deprecated_cacheControl*/() {
      return this.cacheControl();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "sentRequestAtMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_sentRequestAtMillis"
   )
   public final long _deprecated_sentRequestAtMillis/* $FF was: -deprecated_sentRequestAtMillis*/() {
      return this.sentRequestAtMillis;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "receivedResponseAtMillis",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_receivedResponseAtMillis"
   )
   public final long _deprecated_receivedResponseAtMillis/* $FF was: -deprecated_receivedResponseAtMillis*/() {
      return this.receivedResponseAtMillis;
   }

   public void close() {
      this.body.close();
   }

   @NotNull
   public String toString() {
      return "Response{protocol=" + this.protocol + ", code=" + this.code + ", message=" + this.message + ", url=" + this.request.url() + '}';
   }

   @JvmOverloads
   @Nullable
   public final String header(@NotNull String name) {
      Intrinsics.checkNotNullParameter(name, "name");
      return header$default(this, name, (String)null, 2, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0016\u0018\u00002\u00020\u0001B\t\b\u0016¢\u0006\u0004\b\u0002\u0010\u0003B\u0011\b\u0010\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\r\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u0013\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\u0010\u0010\u0019\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0012\u0010\u001f\u001a\u00020\u00002\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0016J\u0018\u0010V\u001a\u00020\u00002\u0006\u0010W\u001a\u00020\u001a2\u0006\u0010X\u001a\u00020\u001aH\u0016J\u0018\u0010Y\u001a\u00020\u00002\u0006\u0010W\u001a\u00020\u001a2\u0006\u0010X\u001a\u00020\u001aH\u0016J\u0010\u0010Z\u001a\u00020\u00002\u0006\u0010W\u001a\u00020\u001aH\u0016J\u0010\u0010%\u001a\u00020\u00002\u0006\u0010%\u001a\u00020[H\u0016J\u0010\u0010+\u001a\u00020\u00002\u0006\u0010+\u001a\u00020,H\u0016J\u0010\u00101\u001a\u00020\u00002\u0006\u00101\u001a\u000202H\u0016J\u0012\u00107\u001a\u00020\u00002\b\u00107\u001a\u0004\u0018\u00010\u0005H\u0016J\u0012\u0010;\u001a\u00020\u00002\b\u0010;\u001a\u0004\u0018\u00010\u0005H\u0016J\u001a\u0010\\\u001a\u00020]2\u0006\u0010W\u001a\u00020\u001a2\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0002J\u0012\u0010>\u001a\u00020\u00002\b\u0010>\u001a\u0004\u0018\u00010\u0005H\u0016J\u0010\u0010^\u001a\u00020\u00002\u0006\u0010P\u001a\u00020QH\u0016J\u0010\u0010A\u001a\u00020\u00002\u0006\u0010A\u001a\u00020BH\u0016J\u0010\u0010G\u001a\u00020\u00002\u0006\u0010G\u001a\u00020BH\u0016J\u0015\u0010_\u001a\u00020]2\u0006\u0010J\u001a\u00020KH\u0000¢\u0006\u0002\b`J\b\u0010a\u001a\u00020\u0005H\u0016R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001c\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\u00020\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001c\u0010\u001f\u001a\u0004\u0018\u00010 X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001a\u0010%\u001a\u00020&X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010+\u001a\u00020,X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u001c\u00101\u001a\u0004\u0018\u000102X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106R\u001c\u00107\u001a\u0004\u0018\u00010\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b8\u00109\"\u0004\b:\u0010\u0006R\u001c\u0010;\u001a\u0004\u0018\u00010\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b<\u00109\"\u0004\b=\u0010\u0006R\u001c\u0010>\u001a\u0004\u0018\u00010\u0005X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b?\u00109\"\u0004\b@\u0010\u0006R\u001a\u0010A\u001a\u00020BX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bC\u0010D\"\u0004\bE\u0010FR\u001a\u0010G\u001a\u00020BX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bH\u0010D\"\u0004\bI\u0010FR\u001c\u0010J\u001a\u0004\u0018\u00010KX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bL\u0010M\"\u0004\bN\u0010OR\u001a\u0010P\u001a\u00020QX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bR\u0010S\"\u0004\bT\u0010U¨\u0006b"},
      d2 = {"Lokhttp3/Response$Builder;", "", "<init>", "()V", "response", "Lokhttp3/Response;", "(Lokhttp3/Response;)V", "request", "Lokhttp3/Request;", "getRequest$okhttp", "()Lokhttp3/Request;", "setRequest$okhttp", "(Lokhttp3/Request;)V", "protocol", "Lokhttp3/Protocol;", "getProtocol$okhttp", "()Lokhttp3/Protocol;", "setProtocol$okhttp", "(Lokhttp3/Protocol;)V", "code", "", "getCode$okhttp", "()I", "setCode$okhttp", "(I)V", "message", "", "getMessage$okhttp", "()Ljava/lang/String;", "setMessage$okhttp", "(Ljava/lang/String;)V", "handshake", "Lokhttp3/Handshake;", "getHandshake$okhttp", "()Lokhttp3/Handshake;", "setHandshake$okhttp", "(Lokhttp3/Handshake;)V", "headers", "Lokhttp3/Headers$Builder;", "getHeaders$okhttp", "()Lokhttp3/Headers$Builder;", "setHeaders$okhttp", "(Lokhttp3/Headers$Builder;)V", "body", "Lokhttp3/ResponseBody;", "getBody$okhttp", "()Lokhttp3/ResponseBody;", "setBody$okhttp", "(Lokhttp3/ResponseBody;)V", "socket", "Lokio/Socket;", "getSocket$okhttp", "()Lokio/Socket;", "setSocket$okhttp", "(Lokio/Socket;)V", "networkResponse", "getNetworkResponse$okhttp", "()Lokhttp3/Response;", "setNetworkResponse$okhttp", "cacheResponse", "getCacheResponse$okhttp", "setCacheResponse$okhttp", "priorResponse", "getPriorResponse$okhttp", "setPriorResponse$okhttp", "sentRequestAtMillis", "", "getSentRequestAtMillis$okhttp", "()J", "setSentRequestAtMillis$okhttp", "(J)V", "receivedResponseAtMillis", "getReceivedResponseAtMillis$okhttp", "setReceivedResponseAtMillis$okhttp", "exchange", "Lokhttp3/internal/connection/Exchange;", "getExchange$okhttp", "()Lokhttp3/internal/connection/Exchange;", "setExchange$okhttp", "(Lokhttp3/internal/connection/Exchange;)V", "trailersSource", "Lokhttp3/TrailersSource;", "getTrailersSource$okhttp", "()Lokhttp3/TrailersSource;", "setTrailersSource$okhttp", "(Lokhttp3/TrailersSource;)V", "header", "name", "value", "addHeader", "removeHeader", "Lokhttp3/Headers;", "checkSupportResponse", "", "trailers", "initExchange", "initExchange$okhttp", "build", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nResponse.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Response.kt\nokhttp3/Response$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,530:1\n1#2:531\n*E\n"})
   public static class Builder {
      @Nullable
      private Request request;
      @Nullable
      private Protocol protocol;
      private int code;
      @Nullable
      private String message;
      @Nullable
      private Handshake handshake;
      @NotNull
      private Headers.Builder headers;
      @NotNull
      private ResponseBody body;
      @Nullable
      private Socket socket;
      @Nullable
      private Response networkResponse;
      @Nullable
      private Response cacheResponse;
      @Nullable
      private Response priorResponse;
      private long sentRequestAtMillis;
      private long receivedResponseAtMillis;
      @Nullable
      private Exchange exchange;
      @NotNull
      private TrailersSource trailersSource;

      @Nullable
      public final Request getRequest$okhttp() {
         return this.request;
      }

      public final void setRequest$okhttp(@Nullable Request var1) {
         this.request = var1;
      }

      @Nullable
      public final Protocol getProtocol$okhttp() {
         return this.protocol;
      }

      public final void setProtocol$okhttp(@Nullable Protocol var1) {
         this.protocol = var1;
      }

      public final int getCode$okhttp() {
         return this.code;
      }

      public final void setCode$okhttp(int var1) {
         this.code = var1;
      }

      @Nullable
      public final String getMessage$okhttp() {
         return this.message;
      }

      public final void setMessage$okhttp(@Nullable String var1) {
         this.message = var1;
      }

      @Nullable
      public final Handshake getHandshake$okhttp() {
         return this.handshake;
      }

      public final void setHandshake$okhttp(@Nullable Handshake var1) {
         this.handshake = var1;
      }

      @NotNull
      public final Headers.Builder getHeaders$okhttp() {
         return this.headers;
      }

      public final void setHeaders$okhttp(@NotNull Headers.Builder var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.headers = var1;
      }

      @NotNull
      public final ResponseBody getBody$okhttp() {
         return this.body;
      }

      public final void setBody$okhttp(@NotNull ResponseBody var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.body = var1;
      }

      @Nullable
      public final Socket getSocket$okhttp() {
         return this.socket;
      }

      public final void setSocket$okhttp(@Nullable Socket var1) {
         this.socket = var1;
      }

      @Nullable
      public final Response getNetworkResponse$okhttp() {
         return this.networkResponse;
      }

      public final void setNetworkResponse$okhttp(@Nullable Response var1) {
         this.networkResponse = var1;
      }

      @Nullable
      public final Response getCacheResponse$okhttp() {
         return this.cacheResponse;
      }

      public final void setCacheResponse$okhttp(@Nullable Response var1) {
         this.cacheResponse = var1;
      }

      @Nullable
      public final Response getPriorResponse$okhttp() {
         return this.priorResponse;
      }

      public final void setPriorResponse$okhttp(@Nullable Response var1) {
         this.priorResponse = var1;
      }

      public final long getSentRequestAtMillis$okhttp() {
         return this.sentRequestAtMillis;
      }

      public final void setSentRequestAtMillis$okhttp(long var1) {
         this.sentRequestAtMillis = var1;
      }

      public final long getReceivedResponseAtMillis$okhttp() {
         return this.receivedResponseAtMillis;
      }

      public final void setReceivedResponseAtMillis$okhttp(long var1) {
         this.receivedResponseAtMillis = var1;
      }

      @Nullable
      public final Exchange getExchange$okhttp() {
         return this.exchange;
      }

      public final void setExchange$okhttp(@Nullable Exchange var1) {
         this.exchange = var1;
      }

      @NotNull
      public final TrailersSource getTrailersSource$okhttp() {
         return this.trailersSource;
      }

      public final void setTrailersSource$okhttp(@NotNull TrailersSource var1) {
         Intrinsics.checkNotNullParameter(var1, "<set-?>");
         this.trailersSource = var1;
      }

      public Builder() {
         this.code = -1;
         this.body = ResponseBody.EMPTY;
         this.trailersSource = TrailersSource.EMPTY;
         this.headers = new Headers.Builder();
      }

      public Builder(@NotNull Response response) {
         Intrinsics.checkNotNullParameter(response, "response");
         super();
         this.code = -1;
         this.body = ResponseBody.EMPTY;
         this.trailersSource = TrailersSource.EMPTY;
         this.request = response.request();
         this.protocol = response.protocol();
         this.code = response.code();
         this.message = response.message();
         this.handshake = response.handshake();
         this.headers = response.headers().newBuilder();
         this.body = response.body();
         this.socket = response.socket();
         this.networkResponse = response.networkResponse();
         this.cacheResponse = response.cacheResponse();
         this.priorResponse = response.priorResponse();
         this.sentRequestAtMillis = response.sentRequestAtMillis();
         this.receivedResponseAtMillis = response.receivedResponseAtMillis();
         this.exchange = response.exchange();
         this.trailersSource = response.trailersSource;
      }

      @NotNull
      public Builder request(@NotNull Request request) {
         Intrinsics.checkNotNullParameter(request, "request");
         Builder $this$request_u24lambda_u240 = this;
         int var4 = 0;
         $this$request_u24lambda_u240.request = request;
         return this;
      }

      @NotNull
      public Builder protocol(@NotNull Protocol protocol) {
         Intrinsics.checkNotNullParameter(protocol, "protocol");
         Builder $this$protocol_u24lambda_u240 = this;
         int var4 = 0;
         $this$protocol_u24lambda_u240.protocol = protocol;
         return this;
      }

      @NotNull
      public Builder code(int code) {
         Builder $this$code_u24lambda_u240 = this;
         int var4 = 0;
         $this$code_u24lambda_u240.code = code;
         return this;
      }

      @NotNull
      public Builder message(@NotNull String message) {
         Intrinsics.checkNotNullParameter(message, "message");
         Builder $this$message_u24lambda_u240 = this;
         int var4 = 0;
         $this$message_u24lambda_u240.message = message;
         return this;
      }

      @NotNull
      public Builder handshake(@Nullable Handshake handshake) {
         Builder $this$handshake_u24lambda_u240 = this;
         int var4 = 0;
         $this$handshake_u24lambda_u240.handshake = handshake;
         return this;
      }

      @NotNull
      public Builder header(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$header_u24lambda_u240 = this;
         int var5 = 0;
         $this$header_u24lambda_u240.headers.set(name, value);
         return this;
      }

      @NotNull
      public Builder addHeader(@NotNull String name, @NotNull String value) {
         Intrinsics.checkNotNullParameter(name, "name");
         Intrinsics.checkNotNullParameter(value, "value");
         Builder $this$addHeader_u24lambda_u240 = this;
         int var5 = 0;
         $this$addHeader_u24lambda_u240.headers.add(name, value);
         return this;
      }

      @NotNull
      public Builder removeHeader(@NotNull String name) {
         Intrinsics.checkNotNullParameter(name, "name");
         Builder $this$removeHeader_u24lambda_u240 = this;
         int var4 = 0;
         $this$removeHeader_u24lambda_u240.headers.removeAll(name);
         return this;
      }

      @NotNull
      public Builder headers(@NotNull Headers headers) {
         Intrinsics.checkNotNullParameter(headers, "headers");
         Builder $this$headers_u24lambda_u240 = this;
         int var4 = 0;
         $this$headers_u24lambda_u240.headers = headers.newBuilder();
         return this;
      }

      @NotNull
      public Builder body(@NotNull ResponseBody body) {
         Intrinsics.checkNotNullParameter(body, "body");
         Builder $this$body_u24lambda_u240 = this;
         int var4 = 0;
         $this$body_u24lambda_u240.body = body;
         return this;
      }

      @NotNull
      public Builder socket(@NotNull Socket socket) {
         Intrinsics.checkNotNullParameter(socket, "socket");
         Builder $this$socket_u24lambda_u240 = this;
         int var4 = 0;
         $this$socket_u24lambda_u240.socket = socket;
         return this;
      }

      @NotNull
      public Builder networkResponse(@Nullable Response networkResponse) {
         Builder $this$networkResponse_u24lambda_u240 = this;
         int var4 = 0;
         $this$networkResponse_u24lambda_u240.checkSupportResponse("networkResponse", networkResponse);
         $this$networkResponse_u24lambda_u240.networkResponse = networkResponse;
         return this;
      }

      @NotNull
      public Builder cacheResponse(@Nullable Response cacheResponse) {
         Builder $this$cacheResponse_u24lambda_u240 = this;
         int var4 = 0;
         $this$cacheResponse_u24lambda_u240.checkSupportResponse("cacheResponse", cacheResponse);
         $this$cacheResponse_u24lambda_u240.cacheResponse = cacheResponse;
         return this;
      }

      private final void checkSupportResponse(String name, Response response) {
         if (response != null) {
            int var5 = 0;
            if (response.networkResponse() != null) {
               int var10 = 0;
               String var11 = name + ".networkResponse != null";
               throw new IllegalArgumentException(var11.toString());
            }

            if (response.cacheResponse() != null) {
               int var8 = 0;
               String var9 = name + ".cacheResponse != null";
               throw new IllegalArgumentException(var9.toString());
            }

            if (response.priorResponse() != null) {
               int var6 = 0;
               String var7 = name + ".priorResponse != null";
               throw new IllegalArgumentException(var7.toString());
            }
         }

      }

      @NotNull
      public Builder priorResponse(@Nullable Response priorResponse) {
         Builder $this$priorResponse_u24lambda_u240 = this;
         int var4 = 0;
         $this$priorResponse_u24lambda_u240.priorResponse = priorResponse;
         return this;
      }

      @NotNull
      public Builder trailers(@NotNull TrailersSource trailersSource) {
         Intrinsics.checkNotNullParameter(trailersSource, "trailersSource");
         Builder $this$trailers_u24lambda_u240 = this;
         int var4 = 0;
         $this$trailers_u24lambda_u240.trailersSource = trailersSource;
         return this;
      }

      @NotNull
      public Builder sentRequestAtMillis(long sentRequestAtMillis) {
         Builder $this$sentRequestAtMillis_u24lambda_u240 = this;
         int var5 = 0;
         $this$sentRequestAtMillis_u24lambda_u240.sentRequestAtMillis = sentRequestAtMillis;
         return this;
      }

      @NotNull
      public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
         Builder $this$receivedResponseAtMillis_u24lambda_u240 = this;
         int var5 = 0;
         $this$receivedResponseAtMillis_u24lambda_u240.receivedResponseAtMillis = receivedResponseAtMillis;
         return this;
      }

      public final void initExchange$okhttp(@NotNull Exchange exchange) {
         Intrinsics.checkNotNullParameter(exchange, "exchange");
         this.exchange = exchange;
      }

      @NotNull
      public Response build() {
         if (this.code < 0) {
            int var26 = 0;
            String var27 = "code < 0: " + this.code;
            throw new IllegalStateException(var27.toString());
         } else {
            Request var10000 = this.request;
            if (var10000 == null) {
               int var24 = 0;
               String var25 = "request == null";
               throw new IllegalStateException(var25.toString());
            } else {
               Protocol var10001 = this.protocol;
               if (var10001 == null) {
                  int var22 = 0;
                  String var23 = "protocol == null";
                  throw new IllegalStateException(var23.toString());
               } else {
                  String var10002 = this.message;
                  if (var10002 == null) {
                     int var1 = 0;
                     String var4 = "message == null";
                     throw new IllegalStateException(var4.toString());
                  } else {
                     int var10003 = this.code;
                     Handshake var10004 = this.handshake;
                     Headers var10005 = this.headers.build();
                     TrailersSource var5 = this.trailersSource;
                     Exchange var6 = this.exchange;
                     long var7 = this.receivedResponseAtMillis;
                     long var9 = this.sentRequestAtMillis;
                     Response var11 = this.priorResponse;
                     Response var12 = this.cacheResponse;
                     Response var13 = this.networkResponse;
                     Socket var14 = this.socket;
                     ResponseBody var15 = this.body;
                     Headers var16 = var10005;
                     Handshake var17 = var10004;
                     int var18 = var10003;
                     String var19 = var10002;
                     Protocol var20 = var10001;
                     Request var21 = var10000;
                     return new Response(var21, var20, var19, var18, var17, var16, var15, var14, var13, var12, var11, var9, var7, var6, var5);
                  }
               }
            }
         }
      }
   }
}
