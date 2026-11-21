package okhttp3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\t\b&\u0018\u0000 N2\u00020\u0001:\u0003MNOB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000eH\u0016J+\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000e2\u0011\u0010\u0010\u001a\r\u0012\t\u0012\u00070\u0012¢\u0006\u0002\b\u00130\u0011H\u0016J\u0018\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J+\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00162\u0011\u0010\u0018\u001a\r\u0012\t\u0012\u00070\u0019¢\u0006\u0002\b\u00130\u0011H\u0016J \u0010\u001a\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0012H\u0016J\u0010\u0010\u001e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u001a\u0010\u001f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J*\u0010\"\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00122\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J2\u0010%\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00122\b\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010&\u001a\u00020'H\u0016J\u0018\u0010(\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010)\u001a\u00020*H\u0016J\u0018\u0010+\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010)\u001a\u00020*H\u0016J\u0010\u0010,\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010-\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010.\u001a\u00020/H\u0016J\u0010\u00100\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u00101\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u00102\u001a\u000203H\u0016J\u0018\u00104\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010&\u001a\u00020'H\u0016J\u0010\u00105\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u00106\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u00107\u001a\u000208H\u0016J\u0010\u00109\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010:\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u00102\u001a\u000203H\u0016J\u0018\u0010;\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010&\u001a\u00020'H\u0016J\u0010\u0010<\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010=\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010&\u001a\u00020'H\u0016J\u0010\u0010>\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010?\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u00107\u001a\u000208H\u0016J\u0018\u0010@\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u00107\u001a\u000208H\u0016J\u0010\u0010A\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010B\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010C\u001a\u000208H\u0016J \u0010D\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010E\u001a\u00020'2\u0006\u0010F\u001a\u00020GH\u0016J\"\u0010H\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010I\u001a\u0002082\b\u0010J\u001a\u0004\u0018\u00010/H\u0016J\u0011\u0010K\u001a\u00020\u00002\u0006\u0010L\u001a\u00020\u0000H\u0086\u0002¨\u0006P"},
   d2 = {"Lokhttp3/EventListener;", "", "<init>", "()V", "callStart", "", "call", "Lokhttp3/Call;", "dispatcherQueueStart", "dispatcher", "Lokhttp3/Dispatcher;", "dispatcherQueueEnd", "proxySelectStart", "url", "Lokhttp3/HttpUrl;", "proxySelectEnd", "proxies", "", "Ljava/net/Proxy;", "Lkotlin/jvm/JvmSuppressWildcards;", "dnsStart", "domainName", "", "dnsEnd", "inetAddressList", "Ljava/net/InetAddress;", "connectStart", "inetSocketAddress", "Ljava/net/InetSocketAddress;", "proxy", "secureConnectStart", "secureConnectEnd", "handshake", "Lokhttp3/Handshake;", "connectEnd", "protocol", "Lokhttp3/Protocol;", "connectFailed", "ioe", "Ljava/io/IOException;", "connectionAcquired", "connection", "Lokhttp3/Connection;", "connectionReleased", "requestHeadersStart", "requestHeadersEnd", "request", "Lokhttp3/Request;", "requestBodyStart", "requestBodyEnd", "byteCount", "", "requestFailed", "responseHeadersStart", "responseHeadersEnd", "response", "Lokhttp3/Response;", "responseBodyStart", "responseBodyEnd", "responseFailed", "callEnd", "callFailed", "canceled", "satisfactionFailure", "cacheHit", "cacheMiss", "cacheConditionalHit", "cachedResponse", "retryDecision", "exception", "retry", "", "followUpDecision", "networkResponse", "nextRequest", "plus", "other", "Factory", "Companion", "AggregateEventListener", "okhttp"}
)
public abstract class EventListener {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   @NotNull
   public static final EventListener NONE = new EventListener() {
   };

   public void callStart(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void dispatcherQueueStart(@NotNull Call call, @NotNull Dispatcher dispatcher) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(dispatcher, "dispatcher");
   }

   public void dispatcherQueueEnd(@NotNull Call call, @NotNull Dispatcher dispatcher) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(dispatcher, "dispatcher");
   }

   public void proxySelectStart(@NotNull Call call, @NotNull HttpUrl url) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(url, "url");
   }

   public void proxySelectEnd(@NotNull Call call, @NotNull HttpUrl url, @NotNull List<Proxy> proxies) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(url, "url");
      Intrinsics.checkNotNullParameter(proxies, "proxies");
   }

   public void dnsStart(@NotNull Call call, @NotNull String domainName) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(domainName, "domainName");
   }

   public void dnsEnd(@NotNull Call call, @NotNull String domainName, @NotNull List<InetAddress> inetAddressList) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(domainName, "domainName");
      Intrinsics.checkNotNullParameter(inetAddressList, "inetAddressList");
   }

   public void connectStart(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(inetSocketAddress, "inetSocketAddress");
      Intrinsics.checkNotNullParameter(proxy, "proxy");
   }

   public void secureConnectStart(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void secureConnectEnd(@NotNull Call call, @Nullable Handshake handshake) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void connectEnd(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(inetSocketAddress, "inetSocketAddress");
      Intrinsics.checkNotNullParameter(proxy, "proxy");
   }

   public void connectFailed(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol, @NotNull IOException ioe) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(inetSocketAddress, "inetSocketAddress");
      Intrinsics.checkNotNullParameter(proxy, "proxy");
      Intrinsics.checkNotNullParameter(ioe, "ioe");
   }

   public void connectionAcquired(@NotNull Call call, @NotNull Connection connection) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(connection, "connection");
   }

   public void connectionReleased(@NotNull Call call, @NotNull Connection connection) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(connection, "connection");
   }

   public void requestHeadersStart(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void requestHeadersEnd(@NotNull Call call, @NotNull Request request) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(request, "request");
   }

   public void requestBodyStart(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void requestBodyEnd(@NotNull Call call, long byteCount) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void requestFailed(@NotNull Call call, @NotNull IOException ioe) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(ioe, "ioe");
   }

   public void responseHeadersStart(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void responseHeadersEnd(@NotNull Call call, @NotNull Response response) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(response, "response");
   }

   public void responseBodyStart(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void responseBodyEnd(@NotNull Call call, long byteCount) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void responseFailed(@NotNull Call call, @NotNull IOException ioe) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(ioe, "ioe");
   }

   public void callEnd(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void callFailed(@NotNull Call call, @NotNull IOException ioe) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(ioe, "ioe");
   }

   public void canceled(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void satisfactionFailure(@NotNull Call call, @NotNull Response response) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(response, "response");
   }

   public void cacheHit(@NotNull Call call, @NotNull Response response) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(response, "response");
   }

   public void cacheMiss(@NotNull Call call) {
      Intrinsics.checkNotNullParameter(call, "call");
   }

   public void cacheConditionalHit(@NotNull Call call, @NotNull Response cachedResponse) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(cachedResponse, "cachedResponse");
   }

   public void retryDecision(@NotNull Call call, @NotNull IOException exception, boolean retry) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(exception, "exception");
   }

   public void followUpDecision(@NotNull Call call, @NotNull Response networkResponse, @Nullable Request nextRequest) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(networkResponse, "networkResponse");
   }

   @NotNull
   public final EventListener plus(@NotNull EventListener other) {
      Intrinsics.checkNotNullParameter(other, "other");
      if (this == NONE) {
         return other;
      } else {
         EventListener[] var10000;
         if (this instanceof AggregateEventListener) {
            var10000 = ((AggregateEventListener)this).getEventListeners();
         } else {
            EventListener[] var3 = new EventListener[]{this};
            var10000 = var3;
         }

         EventListener[] left = var10000;
         if (other == NONE) {
            return this;
         } else {
            if (other instanceof AggregateEventListener) {
               var10000 = ((AggregateEventListener)other).getEventListeners();
            } else {
               EventListener[] var4 = new EventListener[]{other};
               var10000 = var4;
            }

            EventListener[] right = var10000;
            return new AggregateEventListener((EventListener[])ArraysKt.plus(left, right));
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/EventListener$Companion;", "", "<init>", "()V", "NONE", "Lokhttp3/EventListener;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\r\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0010\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0011\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J+\u0010\u0014\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u00132\u0011\u0010\u0015\u001a\r\u0012\t\u0012\u00070\u0017¢\u0006\u0002\b\u00180\u0016H\u0016J\u0018\u0010\u0019\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001a\u001a\u00020\u001bH\u0016J+\u0010\u001c\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001a\u001a\u00020\u001b2\u0011\u0010\u001d\u001a\r\u0012\t\u0012\u00070\u001e¢\u0006\u0002\b\u00180\u0016H\u0016J \u0010\u001f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0017H\u0016J\u0010\u0010#\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u001a\u0010$\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J*\u0010'\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00172\b\u0010(\u001a\u0004\u0018\u00010)H\u0016J2\u0010*\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00172\b\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010+\u001a\u00020,H\u0016J\u0018\u0010-\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010.\u001a\u00020/H\u0016J\u0018\u00100\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010.\u001a\u00020/H\u0016J\u0010\u00101\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u00102\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u00103\u001a\u000204H\u0016J\u0010\u00105\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u00106\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u00107\u001a\u000208H\u0016J\u0018\u00109\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010+\u001a\u00020,H\u0016J\u0010\u0010:\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010;\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010<\u001a\u00020=H\u0016J\u0010\u0010>\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010?\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u00107\u001a\u000208H\u0016J\u0018\u0010@\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010+\u001a\u00020,H\u0016J\u0010\u0010A\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010B\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010+\u001a\u00020,H\u0016J\u0010\u0010C\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010D\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010<\u001a\u00020=H\u0016J\u0018\u0010E\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010<\u001a\u00020=H\u0016J\u0010\u0010F\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010G\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010H\u001a\u00020=H\u0016J \u0010I\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010J\u001a\u00020,2\u0006\u0010K\u001a\u00020LH\u0016J\"\u0010M\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010N\u001a\u00020=2\b\u0010O\u001a\u0004\u0018\u000104H\u0016R\u0019\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007¨\u0006P"},
      d2 = {"Lokhttp3/EventListener$AggregateEventListener;", "Lokhttp3/EventListener;", "eventListeners", "", "<init>", "([Lokhttp3/EventListener;)V", "getEventListeners", "()[Lokhttp3/EventListener;", "[Lokhttp3/EventListener;", "callStart", "", "call", "Lokhttp3/Call;", "dispatcherQueueStart", "dispatcher", "Lokhttp3/Dispatcher;", "dispatcherQueueEnd", "proxySelectStart", "url", "Lokhttp3/HttpUrl;", "proxySelectEnd", "proxies", "", "Ljava/net/Proxy;", "Lkotlin/jvm/JvmSuppressWildcards;", "dnsStart", "domainName", "", "dnsEnd", "inetAddressList", "Ljava/net/InetAddress;", "connectStart", "inetSocketAddress", "Ljava/net/InetSocketAddress;", "proxy", "secureConnectStart", "secureConnectEnd", "handshake", "Lokhttp3/Handshake;", "connectEnd", "protocol", "Lokhttp3/Protocol;", "connectFailed", "ioe", "Ljava/io/IOException;", "connectionAcquired", "connection", "Lokhttp3/Connection;", "connectionReleased", "requestHeadersStart", "requestHeadersEnd", "request", "Lokhttp3/Request;", "requestBodyStart", "requestBodyEnd", "byteCount", "", "requestFailed", "responseHeadersStart", "responseHeadersEnd", "response", "Lokhttp3/Response;", "responseBodyStart", "responseBodyEnd", "responseFailed", "callEnd", "callFailed", "canceled", "satisfactionFailure", "cacheHit", "cacheMiss", "cacheConditionalHit", "cachedResponse", "retryDecision", "exception", "retry", "", "followUpDecision", "networkResponse", "nextRequest", "okhttp"}
   )
   private static final class AggregateEventListener extends EventListener {
      @NotNull
      private final EventListener[] eventListeners;

      public AggregateEventListener(@NotNull EventListener[] eventListeners) {
         Intrinsics.checkNotNullParameter(eventListeners, "eventListeners");
         super();
         this.eventListeners = eventListeners;
      }

      @NotNull
      public final EventListener[] getEventListeners() {
         return this.eventListeners;
      }

      public void callStart(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.callStart(call);
         }

      }

      public void dispatcherQueueStart(@NotNull Call call, @NotNull Dispatcher dispatcher) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(dispatcher, "dispatcher");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.dispatcherQueueStart(call, dispatcher);
         }

      }

      public void dispatcherQueueEnd(@NotNull Call call, @NotNull Dispatcher dispatcher) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(dispatcher, "dispatcher");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.dispatcherQueueEnd(call, dispatcher);
         }

      }

      public void proxySelectStart(@NotNull Call call, @NotNull HttpUrl url) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(url, "url");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.proxySelectStart(call, url);
         }

      }

      public void proxySelectEnd(@NotNull Call call, @NotNull HttpUrl url, @NotNull List<Proxy> proxies) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(url, "url");
         Intrinsics.checkNotNullParameter(proxies, "proxies");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.proxySelectEnd(call, url, proxies);
         }

      }

      public void dnsStart(@NotNull Call call, @NotNull String domainName) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(domainName, "domainName");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.dnsStart(call, domainName);
         }

      }

      public void dnsEnd(@NotNull Call call, @NotNull String domainName, @NotNull List<InetAddress> inetAddressList) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(domainName, "domainName");
         Intrinsics.checkNotNullParameter(inetAddressList, "inetAddressList");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.dnsEnd(call, domainName, inetAddressList);
         }

      }

      public void connectStart(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(inetSocketAddress, "inetSocketAddress");
         Intrinsics.checkNotNullParameter(proxy, "proxy");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.connectStart(call, inetSocketAddress, proxy);
         }

      }

      public void secureConnectStart(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.secureConnectStart(call);
         }

      }

      public void secureConnectEnd(@NotNull Call call, @Nullable Handshake handshake) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.secureConnectEnd(call, handshake);
         }

      }

      public void connectEnd(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(inetSocketAddress, "inetSocketAddress");
         Intrinsics.checkNotNullParameter(proxy, "proxy");
         EventListener[] var5 = this.eventListeners;
         int var6 = 0;

         for(int var7 = var5.length; var6 < var7; ++var6) {
            EventListener delegate = var5[var6];
            delegate.connectEnd(call, inetSocketAddress, proxy, protocol);
         }

      }

      public void connectFailed(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy, @Nullable Protocol protocol, @NotNull IOException ioe) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(inetSocketAddress, "inetSocketAddress");
         Intrinsics.checkNotNullParameter(proxy, "proxy");
         Intrinsics.checkNotNullParameter(ioe, "ioe");
         EventListener[] var6 = this.eventListeners;
         int var7 = 0;

         for(int var8 = var6.length; var7 < var8; ++var7) {
            EventListener delegate = var6[var7];
            delegate.connectFailed(call, inetSocketAddress, proxy, protocol, ioe);
         }

      }

      public void connectionAcquired(@NotNull Call call, @NotNull Connection connection) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(connection, "connection");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.connectionAcquired(call, connection);
         }

      }

      public void connectionReleased(@NotNull Call call, @NotNull Connection connection) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(connection, "connection");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.connectionReleased(call, connection);
         }

      }

      public void requestHeadersStart(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.requestHeadersStart(call);
         }

      }

      public void requestHeadersEnd(@NotNull Call call, @NotNull Request request) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(request, "request");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.requestHeadersEnd(call, request);
         }

      }

      public void requestBodyStart(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.requestBodyStart(call);
         }

      }

      public void requestBodyEnd(@NotNull Call call, long byteCount) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.requestBodyEnd(call, byteCount);
         }

      }

      public void requestFailed(@NotNull Call call, @NotNull IOException ioe) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(ioe, "ioe");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.requestFailed(call, ioe);
         }

      }

      public void responseHeadersStart(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.responseHeadersStart(call);
         }

      }

      public void responseHeadersEnd(@NotNull Call call, @NotNull Response response) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(response, "response");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.responseHeadersEnd(call, response);
         }

      }

      public void responseBodyStart(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.responseBodyStart(call);
         }

      }

      public void responseBodyEnd(@NotNull Call call, long byteCount) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.responseBodyEnd(call, byteCount);
         }

      }

      public void responseFailed(@NotNull Call call, @NotNull IOException ioe) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(ioe, "ioe");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.responseFailed(call, ioe);
         }

      }

      public void callEnd(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.callEnd(call);
         }

      }

      public void callFailed(@NotNull Call call, @NotNull IOException ioe) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(ioe, "ioe");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.callFailed(call, ioe);
         }

      }

      public void canceled(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.canceled(call);
         }

      }

      public void satisfactionFailure(@NotNull Call call, @NotNull Response response) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(response, "response");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.satisfactionFailure(call, response);
         }

      }

      public void cacheHit(@NotNull Call call, @NotNull Response response) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(response, "response");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.cacheHit(call, response);
         }

      }

      public void cacheMiss(@NotNull Call call) {
         Intrinsics.checkNotNullParameter(call, "call");
         EventListener[] var2 = this.eventListeners;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            EventListener delegate = var2[var3];
            delegate.cacheMiss(call);
         }

      }

      public void cacheConditionalHit(@NotNull Call call, @NotNull Response cachedResponse) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(cachedResponse, "cachedResponse");
         EventListener[] var3 = this.eventListeners;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            EventListener delegate = var3[var4];
            delegate.cacheConditionalHit(call, cachedResponse);
         }

      }

      public void retryDecision(@NotNull Call call, @NotNull IOException exception, boolean retry) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(exception, "exception");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.retryDecision(call, exception, retry);
         }

      }

      public void followUpDecision(@NotNull Call call, @NotNull Response networkResponse, @Nullable Request nextRequest) {
         Intrinsics.checkNotNullParameter(call, "call");
         Intrinsics.checkNotNullParameter(networkResponse, "networkResponse");
         EventListener[] var4 = this.eventListeners;
         int var5 = 0;

         for(int var6 = var4.length; var5 < var6; ++var5) {
            EventListener delegate = var4[var5];
            delegate.followUpDecision(call, networkResponse, nextRequest);
         }

      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006À\u0006\u0003"},
      d2 = {"Lokhttp3/EventListener$Factory;", "", "create", "Lokhttp3/EventListener;", "call", "Lokhttp3/Call;", "okhttp"}
   )
   public interface Factory {
      @NotNull
      EventListener create(@NotNull Call var1);
   }
}
