package okhttp3;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.reflect.KClass;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001:\u0001\u001dJ\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J\b\u0010\n\u001a\u00020\u0007H&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\fH&J\b\u0010\u000e\u001a\u00020\u000fH&J'\u0010\u0010\u001a\u0004\u0018\u0001H\u0011\"\b\b\u0000\u0010\u0011*\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00110\u0014H&¢\u0006\u0002\u0010\u0015J%\u0010\u0010\u001a\u0004\u0018\u0001H\u0011\"\u0004\b\u0000\u0010\u00112\u000e\u0010\u0013\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00110\u0016H&¢\u0006\u0002\u0010\u0017J3\u0010\u0010\u001a\u0002H\u0011\"\b\b\u0000\u0010\u0011*\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00110\u00142\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00110\u0019H&¢\u0006\u0002\u0010\u001aJ3\u0010\u0010\u001a\u0002H\u0011\"\b\b\u0000\u0010\u0011*\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00110\u00162\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00110\u0019H&¢\u0006\u0002\u0010\u001bJ\b\u0010\u001c\u001a\u00020\u0000H&¨\u0006\u001eÀ\u0006\u0003"},
   d2 = {"Lokhttp3/Call;", "", "request", "Lokhttp3/Request;", "execute", "Lokhttp3/Response;", "enqueue", "", "responseCallback", "Lokhttp3/Callback;", "cancel", "isExecuted", "", "isCanceled", "timeout", "Lokio/Timeout;", "tag", "T", "", "type", "Lkotlin/reflect/KClass;", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "Ljava/lang/Class;", "(Ljava/lang/Class;)Ljava/lang/Object;", "computeIfAbsent", "Lkotlin/Function0;", "(Lkotlin/reflect/KClass;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "(Ljava/lang/Class;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "clone", "Factory", "okhttp"}
)
public interface Call extends Cloneable {
   @NotNull
   Request request();

   @NotNull
   Response execute() throws IOException;

   void enqueue(@NotNull Callback var1);

   void cancel();

   boolean isExecuted();

   boolean isCanceled();

   @NotNull
   Timeout timeout();

   @Nullable
   <T> T tag(@NotNull KClass<T> var1);

   @Nullable
   <T> T tag(@NotNull Class<? extends T> var1);

   @NotNull
   <T> T tag(@NotNull KClass<T> var1, @NotNull Function0<? extends T> var2);

   @NotNull
   <T> T tag(@NotNull Class<T> var1, @NotNull Function0<? extends T> var2);

   @NotNull
   Call clone();

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006À\u0006\u0003"},
      d2 = {"Lokhttp3/Call$Factory;", "", "newCall", "Lokhttp3/Call;", "request", "Lokhttp3/Request;", "okhttp"}
   )
   public interface Factory {
      @NotNull
      Call newCall(@NotNull Request var1);
   }
}
