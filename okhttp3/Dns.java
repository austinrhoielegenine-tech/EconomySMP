package okhttp3;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&¨\u0006\bÀ\u0006\u0003"},
   d2 = {"Lokhttp3/Dns;", "", "lookup", "", "Ljava/net/InetAddress;", "hostname", "", "Companion", "okhttp"}
)
public interface Dns {
   @NotNull
   Companion Companion = Dns.Companion.$$INSTANCE;
   @JvmField
   @NotNull
   Dns SYSTEM = new Companion.DnsSystem();

   @NotNull
   List<InetAddress> lookup(@NotNull String var1) throws UnknownHostException;

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0006B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0013\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0001¨\u0006\u0007"},
      d2 = {"Lokhttp3/Dns$Companion;", "", "<init>", "()V", "SYSTEM", "Lokhttp3/Dns;", "DnsSystem", "okhttp"}
   )
   public static final class Companion {
      // $FF: synthetic field
      static final Companion $$INSTANCE = new Companion();

      private Companion() {
      }

      @Metadata(
         mv = {2, 2, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\t"},
         d2 = {"Lokhttp3/Dns$Companion$DnsSystem;", "Lokhttp3/Dns;", "<init>", "()V", "lookup", "", "Ljava/net/InetAddress;", "hostname", "", "okhttp"}
      )
      private static final class DnsSystem implements Dns {
         public DnsSystem() {
         }

         @NotNull
         public List<InetAddress> lookup(@NotNull String hostname) {
            Intrinsics.checkNotNullParameter(hostname, "hostname");

            try {
               InetAddress[] var10000 = InetAddress.getAllByName(hostname);
               Intrinsics.checkNotNullExpressionValue(var10000, "getAllByName(...)");
               return ArraysKt.toList(var10000);
            } catch (NullPointerException e) {
               UnknownHostException $this$lookup_u24lambda_u240 = new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
               int var5 = 0;
               $this$lookup_u24lambda_u240.initCause((Throwable)e);
               throw (Throwable)$this$lookup_u24lambda_u240;
            }
         }
      }
   }
}
