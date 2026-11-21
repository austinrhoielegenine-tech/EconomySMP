package okhttp3.internal.idn;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0000\u001a0\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00012\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010\bH\u0086\bø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\t"},
   d2 = {"read14BitInt", "", "", "index", "binarySearch", "position", "limit", "compare", "Lkotlin/Function1;", "okhttp"}
)
public final class IdnaMappingTableKt {
   public static final int read14BitInt(@NotNull String $this$read14BitInt, int index) {
      Intrinsics.checkNotNullParameter($this$read14BitInt, "<this>");
      int b0 = $this$read14BitInt.charAt(index);
      int b1 = $this$read14BitInt.charAt(index + 1);
      return (b0 << 7) + b1;
   }

   public static final int binarySearch(int position, int limit, @NotNull Function1<? super Integer, Integer> compare) {
      Intrinsics.checkNotNullParameter(compare, "compare");
      int $i$f$binarySearch = 0;
      int low = position;
      int high = limit - 1;

      while(low <= high) {
         int mid = (low + high) / 2;
         int compareResult = ((Number)compare.invoke(mid)).intValue();
         if (compareResult < 0) {
            high = mid - 1;
         } else {
            if (compareResult <= 0) {
               return mid;
            }

            low = mid + 1;
         }
      }

      return -low - 1;
   }
}
