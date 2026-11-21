package okhttp3;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\rB\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\r\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\b\fR\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b¨\u0006\u000e"},
   d2 = {"Lokhttp3/TlsVersion;", "", "javaName", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "()Ljava/lang/String;", "TLS_1_3", "TLS_1_2", "TLS_1_1", "TLS_1_0", "SSL_3_0", "-deprecated_javaName", "Companion", "okhttp"}
)
public enum TlsVersion {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final String javaName;
   TLS_1_3("TLSv1.3"),
   TLS_1_2("TLSv1.2"),
   TLS_1_1("TLSv1.1"),
   TLS_1_0("TLSv1"),
   SSL_3_0("SSLv3");

   // $FF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

   private TlsVersion(String javaName) {
      this.javaName = javaName;
   }

   @JvmName(
      name = "javaName"
   )
   @NotNull
   public final String javaName() {
      return this.javaName;
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "javaName",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_javaName"
   )
   @NotNull
   public final String _deprecated_javaName/* $FF was: -deprecated_javaName*/() {
      return this.javaName;
   }

   @NotNull
   public static EnumEntries<TlsVersion> getEntries() {
      return $ENTRIES;
   }

   // $FF: synthetic method
   private static final TlsVersion[] $values() {
      TlsVersion[] var0 = new TlsVersion[]{TLS_1_3, TLS_1_2, TLS_1_1, TLS_1_0, SSL_3_0};
      return var0;
   }

   @JvmStatic
   @NotNull
   public static final TlsVersion forJavaName(@NotNull String javaName) {
      return Companion.forJavaName(javaName);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\b"},
      d2 = {"Lokhttp3/TlsVersion$Companion;", "", "<init>", "()V", "forJavaName", "Lokhttp3/TlsVersion;", "javaName", "", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final TlsVersion forJavaName(@NotNull String javaName) {
         Intrinsics.checkNotNullParameter(javaName, "javaName");
         TlsVersion var10000;
         switch (javaName.hashCode()) {
            case -503070503:
               if (!javaName.equals("TLSv1.1")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.TLS_1_1;
               break;
            case -503070502:
               if (!javaName.equals("TLSv1.2")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.TLS_1_2;
               break;
            case -503070501:
               if (!javaName.equals("TLSv1.3")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.TLS_1_3;
               break;
            case 79201641:
               if (!javaName.equals("SSLv3")) {
                  throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
               }

               var10000 = TlsVersion.SSL_3_0;
               break;
            case 79923350:
               if (javaName.equals("TLSv1")) {
                  var10000 = TlsVersion.TLS_1_0;
                  break;
               }

               throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
            default:
               throw new IllegalArgumentException("Unexpected TLS version: " + javaName);
         }

         return var10000;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
