package okhttp3.internal.connection;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001c\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\u0000¨\u0006\u0004"},
   d2 = {"reorderForHappyEyeballs", "", "Ljava/net/InetAddress;", "addresses", "okhttp"}
)
@SourceDebugExtension({"SMAP\nInetAddressOrder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InetAddressOrder.kt\nokhttp3/internal/connection/InetAddressOrderKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,46:1\n3301#2,10:47\n*S KotlinDebug\n*F\n+ 1 InetAddressOrder.kt\nokhttp3/internal/connection/InetAddressOrderKt\n*L\n38#1:47,10\n*E\n"})
public final class InetAddressOrderKt {
   @NotNull
   public static final List<InetAddress> reorderForHappyEyeballs(@NotNull List<? extends InetAddress> addresses) {
      Intrinsics.checkNotNullParameter(addresses, "addresses");
      if (addresses.size() < 2) {
         return addresses;
      } else {
         Iterable $this$partition$iv = (Iterable)addresses;
         int $i$f$partition = 0;
         ArrayList first$iv = new ArrayList();
         ArrayList second$iv = new ArrayList();

         for(Object element$iv : $this$partition$iv) {
            InetAddress it = (InetAddress)element$iv;
            int var9 = 0;
            if (it instanceof Inet6Address) {
               first$iv.add(element$iv);
            } else {
               second$iv.add(element$iv);
            }
         }

         Pair var1 = new Pair(first$iv, second$iv);
         List ipv6 = (List)var1.component1();
         List ipv4 = (List)var1.component2();
         return !ipv6.isEmpty() && !ipv4.isEmpty() ? _UtilCommonKt.interleave((Iterable)ipv6, (Iterable)ipv4) : addresses;
      }
   }
}
