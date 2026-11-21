package okhttp3.internal.publicsuffix;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.FileSystem;
import okio.Path;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0018\u0010\u0000\u001a\u00020\u0001*\u00020\u00028@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004¨\u0006\u0005"},
   d2 = {"Default", "Lokhttp3/internal/publicsuffix/PublicSuffixList;", "Lokhttp3/internal/publicsuffix/PublicSuffixList$Companion;", "getDefault", "(Lokhttp3/internal/publicsuffix/PublicSuffixList$Companion;)Lokhttp3/internal/publicsuffix/PublicSuffixList;", "okhttp"}
)
public final class PublicSuffixList_jvmKt {
   @NotNull
   public static final PublicSuffixList getDefault(@NotNull PublicSuffixList.Companion $this$Default) {
      Intrinsics.checkNotNullParameter($this$Default, "<this>");
      return new ResourcePublicSuffixList((Path)null, (FileSystem)null, 3, (DefaultConstructorMarker)null);
   }
}
