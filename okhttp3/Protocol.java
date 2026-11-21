package okhttp3;

import java.io.IOException;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u0000 \u000e2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u000eB\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\r\u001a\u00020\u0003H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\u000f"},
   d2 = {"Lokhttp3/Protocol;", "", "protocol", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "HTTP_1_0", "HTTP_1_1", "SPDY_3", "HTTP_2", "H2_PRIOR_KNOWLEDGE", "QUIC", "HTTP_3", "toString", "Companion", "okhttp"}
)
public enum Protocol {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final String protocol;
   HTTP_1_0("http/1.0"),
   HTTP_1_1("http/1.1"),
   /** @deprecated */
   @Deprecated(
      message = "OkHttp has dropped support for SPDY. Prefer {@link #HTTP_2}."
   )
   SPDY_3("spdy/3.1"),
   HTTP_2("h2"),
   H2_PRIOR_KNOWLEDGE("h2_prior_knowledge"),
   QUIC("quic"),
   HTTP_3("h3");

   // $FF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

   private Protocol(String protocol) {
      this.protocol = protocol;
   }

   @NotNull
   public String toString() {
      return this.protocol;
   }

   @NotNull
   public static EnumEntries<Protocol> getEntries() {
      return $ENTRIES;
   }

   // $FF: synthetic method
   private static final Protocol[] $values() {
      Protocol[] var0 = new Protocol[]{HTTP_1_0, HTTP_1_1, SPDY_3, HTTP_2, H2_PRIOR_KNOWLEDGE, QUIC, HTTP_3};
      return var0;
   }

   @JvmStatic
   @NotNull
   public static final Protocol get(@NotNull String protocol) throws IOException {
      return Companion.get(protocol);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\b"},
      d2 = {"Lokhttp3/Protocol$Companion;", "", "<init>", "()V", "get", "Lokhttp3/Protocol;", "protocol", "", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final Protocol get(@NotNull String protocol) throws IOException {
         Intrinsics.checkNotNullParameter(protocol, "protocol");
         Protocol var10000;
         if (Intrinsics.areEqual((Object)protocol, (Object)Protocol.HTTP_1_0.protocol)) {
            var10000 = Protocol.HTTP_1_0;
         } else if (Intrinsics.areEqual((Object)protocol, (Object)Protocol.HTTP_1_1.protocol)) {
            var10000 = Protocol.HTTP_1_1;
         } else if (Intrinsics.areEqual((Object)protocol, (Object)Protocol.H2_PRIOR_KNOWLEDGE.protocol)) {
            var10000 = Protocol.H2_PRIOR_KNOWLEDGE;
         } else if (Intrinsics.areEqual((Object)protocol, (Object)Protocol.HTTP_2.protocol)) {
            var10000 = Protocol.HTTP_2;
         } else if (Intrinsics.areEqual((Object)protocol, (Object)Protocol.SPDY_3.protocol)) {
            var10000 = Protocol.SPDY_3;
         } else if (Intrinsics.areEqual((Object)protocol, (Object)Protocol.QUIC.protocol)) {
            var10000 = Protocol.QUIC;
         } else {
            if (!StringsKt.startsWith$default(protocol, Protocol.HTTP_3.protocol, false, 2, (Object)null)) {
               throw new IOException("Unexpected protocol: " + protocol);
            }

            var10000 = Protocol.HTTP_3;
         }

         return var10000;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
