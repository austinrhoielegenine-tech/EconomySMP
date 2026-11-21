package okhttp3.internal.graal;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.platform.Jdk9Platform;
import okhttp3.internal.platform.Platform;
import org.jetbrains.annotations.NotNull;

@TargetClass(Platform.Companion.class)
@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007¨\u0006\u0006"},
   d2 = {"Lokhttp3/internal/graal/TargetPlatform;", "", "<init>", "()V", "findPlatform", "Lokhttp3/internal/platform/Platform;", "okhttp"}
)
public final class TargetPlatform {
   @Substitute
   @NotNull
   public final Platform findPlatform() {
      Jdk9Platform var10000 = Jdk9Platform.Companion.buildIfSupported();
      Intrinsics.checkNotNull(var10000);
      return var10000;
   }
}
