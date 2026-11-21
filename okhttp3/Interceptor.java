package okhttp3;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bæ\u0080\u0001\u0018\u0000 \u00062\u00020\u0001:\u0002\u0006\u0007J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\bÀ\u0006\u0003"},
   d2 = {"Lokhttp3/Interceptor;", "", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "Companion", "Chain", "okhttp"}
)
public interface Interceptor {
   @NotNull
   Companion Companion = Interceptor.Companion.$$INSTANCE;

   @NotNull
   Response intercept(@NotNull Chain var1) throws IOException;

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J1\u0010\u0004\u001a\u00020\u00052#\b\u0004\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b¢\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007H\u0086\nø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\r"},
      d2 = {"Lokhttp3/Interceptor$Companion;", "", "<init>", "()V", "invoke", "Lokhttp3/Interceptor;", "block", "Lkotlin/Function1;", "Lokhttp3/Interceptor$Chain;", "Lkotlin/ParameterName;", "name", "chain", "Lokhttp3/Response;", "okhttp"}
   )
   public static final class Companion {
      // $FF: synthetic field
      static final Companion $$INSTANCE = new Companion();

      private Companion() {
      }

      @NotNull
      public final Interceptor invoke(@NotNull final Function1<? super Chain, Response> block) {
         Intrinsics.checkNotNullParameter(block, "block");
         int $i$f$invoke = 0;
         return new Interceptor() {
            public final Response intercept(Chain it) {
               Intrinsics.checkNotNullParameter(it, "it");
               return (Response)block.invoke(it);
            }
         };
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u0003H&J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\b\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u000bH&J\u0018\u0010\f\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000fH&J\b\u0010\u0010\u001a\u00020\u000bH&J\u0018\u0010\u0011\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000fH&J\b\u0010\u0012\u001a\u00020\u000bH&J\u0018\u0010\u0013\u001a\u00020\u00002\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000fH&¨\u0006\u0014À\u0006\u0003"},
      d2 = {"Lokhttp3/Interceptor$Chain;", "", "request", "Lokhttp3/Request;", "proceed", "Lokhttp3/Response;", "connection", "Lokhttp3/Connection;", "call", "Lokhttp3/Call;", "connectTimeoutMillis", "", "withConnectTimeout", "timeout", "unit", "Ljava/util/concurrent/TimeUnit;", "readTimeoutMillis", "withReadTimeout", "writeTimeoutMillis", "withWriteTimeout", "okhttp"}
   )
   public interface Chain {
      @NotNull
      Request request();

      @NotNull
      Response proceed(@NotNull Request var1) throws IOException;

      @Nullable
      Connection connection();

      @NotNull
      Call call();

      int connectTimeoutMillis();

      @NotNull
      Chain withConnectTimeout(int var1, @NotNull TimeUnit var2);

      int readTimeoutMillis();

      @NotNull
      Chain withReadTimeout(int var1, @NotNull TimeUnit var2);

      int writeTimeoutMillis();

      @NotNull
      Chain withWriteTimeout(int var1, @NotNull TimeUnit var2);
   }
}
