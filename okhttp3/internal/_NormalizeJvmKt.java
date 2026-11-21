package okhttp3.internal;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0001H\u0000¨\u0006\u0003"},
   d2 = {"normalizeNfc", "", "string", "okhttp"}
)
public final class _NormalizeJvmKt {
   @NotNull
   public static final String normalizeNfc(@NotNull String string) {
      Intrinsics.checkNotNullParameter(string, "string");
      String var10000 = Normalizer.normalize((CharSequence)string, Form.NFC);
      Intrinsics.checkNotNullExpressionValue(var10000, "normalize(...)");
      return var10000;
   }
}
