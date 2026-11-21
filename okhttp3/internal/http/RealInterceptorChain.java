package okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.connection.Exchange;
import okhttp3.internal.connection.RealCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001BO\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\b\u0012\u0006\u0010\u000e\u001a\u00020\b\u0012\u0006\u0010\u000f\u001a\u00020\b¢\u0006\u0004\b\u0010\u0010\u0011JK\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\b2\b\b\u0002\u0010\u000f\u001a\u00020\bH\u0000¢\u0006\u0002\b\u001eJ\n\u0010\u001f\u001a\u0004\u0018\u00010 H\u0016J\b\u0010\r\u001a\u00020\bH\u0016J\u0018\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\b2\u0006\u0010#\u001a\u00020$H\u0016J\b\u0010\u000e\u001a\u00020\bH\u0016J\u0018\u0010%\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\b2\u0006\u0010#\u001a\u00020$H\u0016J\b\u0010\u000f\u001a\u00020\bH\u0016J\u0018\u0010&\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\b2\u0006\u0010#\u001a\u00020$H\u0016J\b\u0010\u0002\u001a\u00020'H\u0016J\b\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010(\u001a\u00020)2\u0006\u0010\u000b\u001a\u00020\fH\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\u0004\u0018\u00010\nX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0014\u0010\u000b\u001a\u00020\fX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\r\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0014\u0010\u000e\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0014\u0010\u000f\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019R\u000e\u0010\u001c\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006*"},
   d2 = {"Lokhttp3/internal/http/RealInterceptorChain;", "Lokhttp3/Interceptor$Chain;", "call", "Lokhttp3/internal/connection/RealCall;", "interceptors", "", "Lokhttp3/Interceptor;", "index", "", "exchange", "Lokhttp3/internal/connection/Exchange;", "request", "Lokhttp3/Request;", "connectTimeoutMillis", "readTimeoutMillis", "writeTimeoutMillis", "<init>", "(Lokhttp3/internal/connection/RealCall;Ljava/util/List;ILokhttp3/internal/connection/Exchange;Lokhttp3/Request;III)V", "getCall$okhttp", "()Lokhttp3/internal/connection/RealCall;", "getExchange$okhttp", "()Lokhttp3/internal/connection/Exchange;", "getRequest$okhttp", "()Lokhttp3/Request;", "getConnectTimeoutMillis$okhttp", "()I", "getReadTimeoutMillis$okhttp", "getWriteTimeoutMillis$okhttp", "calls", "copy", "copy$okhttp", "connection", "Lokhttp3/Connection;", "withConnectTimeout", "timeout", "unit", "Ljava/util/concurrent/TimeUnit;", "withReadTimeout", "withWriteTimeout", "Lokhttp3/Call;", "proceed", "Lokhttp3/Response;", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRealInterceptorChain.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealInterceptorChain.kt\nokhttp3/internal/http/RealInterceptorChain\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,139:1\n1#2:140\n*E\n"})
public final class RealInterceptorChain implements Interceptor.Chain {
   @NotNull
   private final RealCall call;
   @NotNull
   private final List<Interceptor> interceptors;
   private final int index;
   @Nullable
   private final Exchange exchange;
   @NotNull
   private final Request request;
   private final int connectTimeoutMillis;
   private final int readTimeoutMillis;
   private final int writeTimeoutMillis;
   private int calls;

   public RealInterceptorChain(@NotNull RealCall call, @NotNull List<? extends Interceptor> interceptors, int index, @Nullable Exchange exchange, @NotNull Request request, int connectTimeoutMillis, int readTimeoutMillis, int writeTimeoutMillis) {
      Intrinsics.checkNotNullParameter(call, "call");
      Intrinsics.checkNotNullParameter(interceptors, "interceptors");
      Intrinsics.checkNotNullParameter(request, "request");
      super();
      this.call = call;
      this.interceptors = interceptors;
      this.index = index;
      this.exchange = exchange;
      this.request = request;
      this.connectTimeoutMillis = connectTimeoutMillis;
      this.readTimeoutMillis = readTimeoutMillis;
      this.writeTimeoutMillis = writeTimeoutMillis;
   }

   @NotNull
   public final RealCall getCall$okhttp() {
      return this.call;
   }

   @Nullable
   public final Exchange getExchange$okhttp() {
      return this.exchange;
   }

   @NotNull
   public final Request getRequest$okhttp() {
      return this.request;
   }

   public final int getConnectTimeoutMillis$okhttp() {
      return this.connectTimeoutMillis;
   }

   public final int getReadTimeoutMillis$okhttp() {
      return this.readTimeoutMillis;
   }

   public final int getWriteTimeoutMillis$okhttp() {
      return this.writeTimeoutMillis;
   }

   @NotNull
   public final RealInterceptorChain copy$okhttp(int index, @Nullable Exchange exchange, @NotNull Request request, int connectTimeoutMillis, int readTimeoutMillis, int writeTimeoutMillis) {
      Intrinsics.checkNotNullParameter(request, "request");
      return new RealInterceptorChain(this.call, this.interceptors, index, exchange, request, connectTimeoutMillis, readTimeoutMillis, writeTimeoutMillis);
   }

   // $FF: synthetic method
   public static RealInterceptorChain copy$okhttp$default(RealInterceptorChain var0, int var1, Exchange var2, Request var3, int var4, int var5, int var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.index;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.exchange;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.request;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.connectTimeoutMillis;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.readTimeoutMillis;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.writeTimeoutMillis;
      }

      return var0.copy$okhttp(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public Connection connection() {
      Exchange var10000 = this.exchange;
      return var10000 != null ? var10000.getConnection$okhttp() : null;
   }

   public int connectTimeoutMillis() {
      return this.connectTimeoutMillis;
   }

   @NotNull
   public Interceptor.Chain withConnectTimeout(int timeout, @NotNull TimeUnit unit) {
      Intrinsics.checkNotNullParameter(unit, "unit");
      if (this.exchange != null) {
         int var3 = 0;
         String var4 = "Timeouts can't be adjusted in a network interceptor";
         throw new IllegalStateException(var4.toString());
      } else {
         return copy$okhttp$default(this, 0, (Exchange)null, (Request)null, _UtilJvmKt.checkDuration("connectTimeout", (long)timeout, unit), 0, 0, 55, (Object)null);
      }
   }

   public int readTimeoutMillis() {
      return this.readTimeoutMillis;
   }

   @NotNull
   public Interceptor.Chain withReadTimeout(int timeout, @NotNull TimeUnit unit) {
      Intrinsics.checkNotNullParameter(unit, "unit");
      if (this.exchange != null) {
         int var3 = 0;
         String var4 = "Timeouts can't be adjusted in a network interceptor";
         throw new IllegalStateException(var4.toString());
      } else {
         return copy$okhttp$default(this, 0, (Exchange)null, (Request)null, 0, _UtilJvmKt.checkDuration("readTimeout", (long)timeout, unit), 0, 47, (Object)null);
      }
   }

   public int writeTimeoutMillis() {
      return this.writeTimeoutMillis;
   }

   @NotNull
   public Interceptor.Chain withWriteTimeout(int timeout, @NotNull TimeUnit unit) {
      Intrinsics.checkNotNullParameter(unit, "unit");
      if (this.exchange != null) {
         int var3 = 0;
         String var4 = "Timeouts can't be adjusted in a network interceptor";
         throw new IllegalStateException(var4.toString());
      } else {
         return copy$okhttp$default(this, 0, (Exchange)null, (Request)null, 0, 0, _UtilJvmKt.checkDuration("writeTimeout", (long)timeout, unit), 31, (Object)null);
      }
   }

   @NotNull
   public Call call() {
      return this.call;
   }

   @NotNull
   public Request request() {
      return this.request;
   }

   @NotNull
   public Response proceed(@NotNull Request request) throws IOException {
      Intrinsics.checkNotNullParameter(request, "request");
      if (this.index >= this.interceptors.size()) {
         throw new IllegalStateException("Check failed.");
      } else {
         int var2 = this.calls++;
         if (this.exchange != null) {
            if (!this.exchange.getFinder$okhttp().getRoutePlanner().sameHostAndPort(request.url())) {
               int var9 = 0;
               String var10 = "network interceptor " + this.interceptors.get(this.index - 1) + " must retain the same host and port";
               throw new IllegalStateException(var10.toString());
            }

            if (this.calls != 1) {
               int var7 = 0;
               String interceptor = "network interceptor " + this.interceptors.get(this.index - 1) + " must call proceed() exactly once";
               throw new IllegalStateException(interceptor.toString());
            }
         }

         RealInterceptorChain next = copy$okhttp$default(this, this.index + 1, (Exchange)null, request, 0, 0, 0, 58, (Object)null);
         Interceptor interceptor = (Interceptor)this.interceptors.get(this.index);
         Response var10000 = interceptor.intercept(next);
         if (var10000 == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
         } else {
            Response response = var10000;
            if (this.exchange != null && this.index + 1 < this.interceptors.size() && next.calls != 1) {
               int var5 = 0;
               String var11 = "network interceptor " + interceptor + " must call proceed() exactly once";
               throw new IllegalStateException(var11.toString());
            } else {
               return response;
            }
         }
      }
   }
}
