package okhttp3.internal.graal;

import kotlin.Metadata;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.graalvm.nativeimage.hosted.Feature;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0017¨\u0006\b"},
   d2 = {"Lokhttp3/internal/graal/OkHttpFeature;", "Lorg/graalvm/nativeimage/hosted/Feature;", "<init>", "()V", "beforeAnalysis", "", "access", "Lorg/graalvm/nativeimage/hosted/Feature$BeforeAnalysisAccess;", "okhttp"}
)
public final class OkHttpFeature implements Feature {
   @IgnoreJRERequirement
   public void beforeAnalysis(@Nullable Feature.BeforeAnalysisAccess access) {
   }
}
