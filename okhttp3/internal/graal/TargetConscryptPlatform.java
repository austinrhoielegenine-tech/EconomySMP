package okhttp3.internal.graal;

import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;
import kotlin.Metadata;
import okhttp3.internal.platform.ConscryptPlatform;

@TargetClass(ConscryptPlatform.class)
@Delete
@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
   d2 = {"Lokhttp3/internal/graal/TargetConscryptPlatform;", "", "<init>", "()V", "okhttp"}
)
public final class TargetConscryptPlatform {
}
