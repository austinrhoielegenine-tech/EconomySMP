package okio.internal;

import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\"\u0016\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u001c\u0010\u0003\u001a\u00020\u0004*\u00060\u0005j\u0002`\u00068@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0007¨\u0006\b"},
   d2 = {"logger", "Ljava/util/logging/Logger;", "kotlin.jvm.PlatformType", "isAndroidGetsocknameError", "", "Ljava/lang/AssertionError;", "Lkotlin/AssertionError;", "(Ljava/lang/AssertionError;)Z", "okio"}
)
public final class _JavaIoKt {
   private static final Logger logger = Logger.getLogger("okio.Okio");

   public static final boolean isAndroidGetsocknameError(@NotNull AssertionError $this$isAndroidGetsocknameError) {
      Intrinsics.checkNotNullParameter($this$isAndroidGetsocknameError, "<this>");
      boolean var1;
      if ($this$isAndroidGetsocknameError.getCause() != null) {
         String var10000 = $this$isAndroidGetsocknameError.getMessage();
         if (var10000 != null ? StringsKt.contains$default((CharSequence)var10000, (CharSequence)"getsockname failed", false, 2, (Object)null) : false) {
            var1 = true;
            return var1;
         }
      }

      var1 = false;
      return var1;
   }

   // $FF: synthetic method
   public static final Logger access$getLogger$p() {
      return logger;
   }
}
