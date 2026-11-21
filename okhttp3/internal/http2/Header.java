package okhttp3.internal.http2;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0007\u0012\u0006\u0010\u0004\u001a\u00020\u0007¢\u0006\u0004\b\u0005\u0010\bB\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0007¢\u0006\u0004\b\u0005\u0010\tJ\b\u0010\f\u001a\u00020\u0007H\u0016J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0013\u001a\u00020\u000bHÖ\u0001R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lokhttp3/internal/http2/Header;", "", "name", "Lokio/ByteString;", "value", "<init>", "(Lokio/ByteString;Lokio/ByteString;)V", "", "(Ljava/lang/String;Ljava/lang/String;)V", "(Lokio/ByteString;Ljava/lang/String;)V", "hpackSize", "", "toString", "component1", "component2", "copy", "equals", "", "other", "hashCode", "Companion", "okhttp"}
)
public final class Header {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   @NotNull
   public final ByteString name;
   @JvmField
   @NotNull
   public final ByteString value;
   @JvmField
   public final int hpackSize;
   @JvmField
   @NotNull
   public static final ByteString PSEUDO_PREFIX;
   @NotNull
   public static final String RESPONSE_STATUS_UTF8 = ":status";
   @NotNull
   public static final String TARGET_METHOD_UTF8 = ":method";
   @NotNull
   public static final String TARGET_PATH_UTF8 = ":path";
   @NotNull
   public static final String TARGET_SCHEME_UTF8 = ":scheme";
   @NotNull
   public static final String TARGET_AUTHORITY_UTF8 = ":authority";
   @JvmField
   @NotNull
   public static final ByteString RESPONSE_STATUS;
   @JvmField
   @NotNull
   public static final ByteString TARGET_METHOD;
   @JvmField
   @NotNull
   public static final ByteString TARGET_PATH;
   @JvmField
   @NotNull
   public static final ByteString TARGET_SCHEME;
   @JvmField
   @NotNull
   public static final ByteString TARGET_AUTHORITY;

   public Header(@NotNull ByteString name, @NotNull ByteString value) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      super();
      this.name = name;
      this.value = value;
      this.hpackSize = 32 + this.name.size() + this.value.size();
   }

   public Header(@NotNull String name, @NotNull String value) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      this(ByteString.Companion.encodeUtf8(name), ByteString.Companion.encodeUtf8(value));
   }

   public Header(@NotNull ByteString name, @NotNull String value) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      this(name, ByteString.Companion.encodeUtf8(value));
   }

   @NotNull
   public String toString() {
      return this.name.utf8() + ": " + this.value.utf8();
   }

   @NotNull
   public final ByteString component1() {
      return this.name;
   }

   @NotNull
   public final ByteString component2() {
      return this.value;
   }

   @NotNull
   public final Header copy(@NotNull ByteString name, @NotNull ByteString value) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(value, "value");
      return new Header(name, value);
   }

   // $FF: synthetic method
   public static Header copy$default(Header var0, ByteString var1, ByteString var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.name;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.value;
      }

      return var0.copy(var1, var2);
   }

   public int hashCode() {
      int result = this.name.hashCode();
      result = result * 31 + this.value.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Header)) {
         return false;
      } else {
         Header var2 = (Header)other;
         if (!Intrinsics.areEqual((Object)this.name, (Object)var2.name)) {
            return false;
         } else {
            return Intrinsics.areEqual((Object)this.value, (Object)var2.value);
         }
      }
   }

   static {
      PSEUDO_PREFIX = ByteString.Companion.encodeUtf8(":");
      RESPONSE_STATUS = ByteString.Companion.encodeUtf8(":status");
      TARGET_METHOD = ByteString.Companion.encodeUtf8(":method");
      TARGET_PATH = ByteString.Companion.encodeUtf8(":path");
      TARGET_SCHEME = ByteString.Companion.encodeUtf8(":scheme");
      TARGET_AUTHORITY = ByteString.Companion.encodeUtf8(":authority");
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
      d2 = {"Lokhttp3/internal/http2/Header$Companion;", "", "<init>", "()V", "PSEUDO_PREFIX", "Lokio/ByteString;", "RESPONSE_STATUS_UTF8", "", "TARGET_METHOD_UTF8", "TARGET_PATH_UTF8", "TARGET_SCHEME_UTF8", "TARGET_AUTHORITY_UTF8", "RESPONSE_STATUS", "TARGET_METHOD", "TARGET_PATH", "TARGET_SCHEME", "TARGET_AUTHORITY", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
