package okio.internal;

import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0000¨\u0006\u0004"},
   d2 = {"setBitsOrZero", "", "Ljava/util/concurrent/atomic/AtomicInteger;", "bits", "okio"}
)
public final class _AtomicKt {
   public static final int setBitsOrZero(@NotNull AtomicInteger $this$setBitsOrZero, int bits) {
      Intrinsics.checkNotNullParameter($this$setBitsOrZero, "<this>");

      int current;
      int updated;
      do {
         current = $this$setBitsOrZero.get();
         if ((current & bits) != 0) {
            return 0;
         }

         updated = current | bits;
      } while(!$this$setBitsOrZero.compareAndSet(current, updated));

      return updated;
   }
}
