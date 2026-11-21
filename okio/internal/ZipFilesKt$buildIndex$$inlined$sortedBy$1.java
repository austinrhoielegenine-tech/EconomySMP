package okio.internal;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 2, 0},
   k = 3,
   xi = 48
)
@SourceDebugExtension({"SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2\n+ 2 ZipFiles.kt\nokio/internal/ZipFilesKt\n*L\n1#1,328:1\n155#2:329\n*E\n"})
public final class ZipFilesKt$buildIndex$$inlined$sortedBy$1<T> implements Comparator {
   public final int compare(T a, T b) {
      ZipEntry it = (ZipEntry)a;
      int var4 = 0;
      Comparable var10000 = it.getCanonicalPath();
      it = (ZipEntry)b;
      Comparable var5 = var10000;
      var4 = 0;
      return ComparisonsKt.compareValues(var5, it.getCanonicalPath());
   }
}
