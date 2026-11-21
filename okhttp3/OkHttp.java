package okhttp3;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000¨\u0006\u0006"},
   d2 = {"Lokhttp3/OkHttp;", "", "<init>", "()V", "VERSION", "", "okhttp"}
)
public final class OkHttp {
   @NotNull
   public static final OkHttp INSTANCE = new OkHttp();
   @JvmField
   @NotNull
   public static final String VERSION = "5.3.2";

   private OkHttp() {
   }
}
