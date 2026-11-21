package okhttp3.internal.ws;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Headers;
import okhttp3.internal._UtilCommonKt;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0016\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dBG\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003¢\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u0011J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u0011J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003JN\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003HÆ\u0001¢\u0006\u0002\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00032\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÖ\u0001J\t\u0010\u001b\u001a\u00020\u001cHÖ\u0001R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\u0004\n\u0002\u0010\fR\u0010\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\u0004\n\u0002\u0010\fR\u0010\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"},
   d2 = {"Lokhttp3/internal/ws/WebSocketExtensions;", "", "perMessageDeflate", "", "clientMaxWindowBits", "", "clientNoContextTakeover", "serverMaxWindowBits", "serverNoContextTakeover", "unknownValues", "<init>", "(ZLjava/lang/Integer;ZLjava/lang/Integer;ZZ)V", "Ljava/lang/Integer;", "noContextTakeover", "clientOriginated", "component1", "component2", "()Ljava/lang/Integer;", "component3", "component4", "component5", "component6", "copy", "(ZLjava/lang/Integer;ZLjava/lang/Integer;ZZ)Lokhttp3/internal/ws/WebSocketExtensions;", "equals", "other", "hashCode", "toString", "", "Companion", "okhttp"}
)
@IgnoreJRERequirement
public final class WebSocketExtensions {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   public final boolean perMessageDeflate;
   @JvmField
   @Nullable
   public final Integer clientMaxWindowBits;
   @JvmField
   public final boolean clientNoContextTakeover;
   @JvmField
   @Nullable
   public final Integer serverMaxWindowBits;
   @JvmField
   public final boolean serverNoContextTakeover;
   @JvmField
   public final boolean unknownValues;
   @NotNull
   private static final String HEADER_WEB_SOCKET_EXTENSION = "Sec-WebSocket-Extensions";

   public WebSocketExtensions(boolean perMessageDeflate, @Nullable Integer clientMaxWindowBits, boolean clientNoContextTakeover, @Nullable Integer serverMaxWindowBits, boolean serverNoContextTakeover, boolean unknownValues) {
      this.perMessageDeflate = perMessageDeflate;
      this.clientMaxWindowBits = clientMaxWindowBits;
      this.clientNoContextTakeover = clientNoContextTakeover;
      this.serverMaxWindowBits = serverMaxWindowBits;
      this.serverNoContextTakeover = serverNoContextTakeover;
      this.unknownValues = unknownValues;
   }

   // $FF: synthetic method
   public WebSocketExtensions(boolean var1, Integer var2, boolean var3, Integer var4, boolean var5, boolean var6, int var7, DefaultConstructorMarker var8) {
      if ((var7 & 1) != 0) {
         var1 = false;
      }

      if ((var7 & 2) != 0) {
         var2 = null;
      }

      if ((var7 & 4) != 0) {
         var3 = false;
      }

      if ((var7 & 8) != 0) {
         var4 = null;
      }

      if ((var7 & 16) != 0) {
         var5 = false;
      }

      if ((var7 & 32) != 0) {
         var6 = false;
      }

      this(var1, var2, var3, var4, var5, var6);
   }

   public final boolean noContextTakeover(boolean clientOriginated) {
      return clientOriginated ? this.clientNoContextTakeover : this.serverNoContextTakeover;
   }

   public final boolean component1() {
      return this.perMessageDeflate;
   }

   @Nullable
   public final Integer component2() {
      return this.clientMaxWindowBits;
   }

   public final boolean component3() {
      return this.clientNoContextTakeover;
   }

   @Nullable
   public final Integer component4() {
      return this.serverMaxWindowBits;
   }

   public final boolean component5() {
      return this.serverNoContextTakeover;
   }

   public final boolean component6() {
      return this.unknownValues;
   }

   @NotNull
   public final WebSocketExtensions copy(boolean perMessageDeflate, @Nullable Integer clientMaxWindowBits, boolean clientNoContextTakeover, @Nullable Integer serverMaxWindowBits, boolean serverNoContextTakeover, boolean unknownValues) {
      return new WebSocketExtensions(perMessageDeflate, clientMaxWindowBits, clientNoContextTakeover, serverMaxWindowBits, serverNoContextTakeover, unknownValues);
   }

   // $FF: synthetic method
   public static WebSocketExtensions copy$default(WebSocketExtensions var0, boolean var1, Integer var2, boolean var3, Integer var4, boolean var5, boolean var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.perMessageDeflate;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.clientMaxWindowBits;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.clientNoContextTakeover;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.serverMaxWindowBits;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.serverNoContextTakeover;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.unknownValues;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   public String toString() {
      return "WebSocketExtensions(perMessageDeflate=" + this.perMessageDeflate + ", clientMaxWindowBits=" + this.clientMaxWindowBits + ", clientNoContextTakeover=" + this.clientNoContextTakeover + ", serverMaxWindowBits=" + this.serverMaxWindowBits + ", serverNoContextTakeover=" + this.serverNoContextTakeover + ", unknownValues=" + this.unknownValues + ')';
   }

   public int hashCode() {
      int result = Boolean.hashCode(this.perMessageDeflate);
      result = result * 31 + (this.clientMaxWindowBits == null ? 0 : this.clientMaxWindowBits.hashCode());
      result = result * 31 + Boolean.hashCode(this.clientNoContextTakeover);
      result = result * 31 + (this.serverMaxWindowBits == null ? 0 : this.serverMaxWindowBits.hashCode());
      result = result * 31 + Boolean.hashCode(this.serverNoContextTakeover);
      result = result * 31 + Boolean.hashCode(this.unknownValues);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof WebSocketExtensions)) {
         return false;
      } else {
         WebSocketExtensions var2 = (WebSocketExtensions)other;
         if (this.perMessageDeflate != var2.perMessageDeflate) {
            return false;
         } else if (!Intrinsics.areEqual((Object)this.clientMaxWindowBits, (Object)var2.clientMaxWindowBits)) {
            return false;
         } else if (this.clientNoContextTakeover != var2.clientNoContextTakeover) {
            return false;
         } else if (!Intrinsics.areEqual((Object)this.serverMaxWindowBits, (Object)var2.serverMaxWindowBits)) {
            return false;
         } else if (this.serverNoContextTakeover != var2.serverNoContextTakeover) {
            return false;
         } else {
            return this.unknownValues == var2.unknownValues;
         }
      }
   }

   public WebSocketExtensions() {
      this(false, (Integer)null, false, (Integer)null, false, false, 63, (DefaultConstructorMarker)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lokhttp3/internal/ws/WebSocketExtensions$Companion;", "", "<init>", "()V", "HEADER_WEB_SOCKET_EXTENSION", "", "parse", "Lokhttp3/internal/ws/WebSocketExtensions;", "responseHeaders", "Lokhttp3/Headers;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final WebSocketExtensions parse(@NotNull Headers responseHeaders) throws IOException {
         Intrinsics.checkNotNullParameter(responseHeaders, "responseHeaders");
         boolean compressionEnabled = false;
         Integer clientMaxWindowBits = null;
         boolean clientNoContextTakeover = false;
         Integer serverMaxWindowBits = null;
         boolean serverNoContextTakeover = false;
         boolean unexpectedValues = false;
         int i = 0;

         for(int var9 = responseHeaders.size(); i < var9; ++i) {
            if (StringsKt.equals(responseHeaders.name(i), "Sec-WebSocket-Extensions", true)) {
               String header = responseHeaders.value(i);
               int pos = 0;

               while(pos < header.length()) {
                  int extensionEnd = _UtilCommonKt.delimiterOffset$default(header, ',', pos, 0, 4, (Object)null);
                  int extensionTokenEnd = _UtilCommonKt.delimiterOffset(header, ';', pos, extensionEnd);
                  String extensionToken = _UtilCommonKt.trimSubstring(header, pos, extensionTokenEnd);
                  pos = extensionTokenEnd + 1;
                  if (StringsKt.equals(extensionToken, "permessage-deflate", true)) {
                     if (compressionEnabled) {
                        unexpectedValues = true;
                     }

                     compressionEnabled = true;

                     while(pos < extensionEnd) {
                        int parameterEnd = _UtilCommonKt.delimiterOffset(header, ';', pos, extensionEnd);
                        int equals = _UtilCommonKt.delimiterOffset(header, '=', pos, parameterEnd);
                        String name = _UtilCommonKt.trimSubstring(header, pos, equals);
                        String value = equals < parameterEnd ? StringsKt.removeSurrounding(_UtilCommonKt.trimSubstring(header, equals + 1, parameterEnd), (CharSequence)"\"") : null;
                        pos = parameterEnd + 1;
                        if (StringsKt.equals(name, "client_max_window_bits", true)) {
                           if (clientMaxWindowBits != null) {
                              unexpectedValues = true;
                           }

                           clientMaxWindowBits = value != null ? StringsKt.toIntOrNull(value) : null;
                           if (clientMaxWindowBits == null) {
                              unexpectedValues = true;
                           }
                        } else if (StringsKt.equals(name, "client_no_context_takeover", true)) {
                           if (clientNoContextTakeover) {
                              unexpectedValues = true;
                           }

                           if (value != null) {
                              unexpectedValues = true;
                           }

                           clientNoContextTakeover = true;
                        } else if (StringsKt.equals(name, "server_max_window_bits", true)) {
                           if (serverMaxWindowBits != null) {
                              unexpectedValues = true;
                           }

                           serverMaxWindowBits = value != null ? StringsKt.toIntOrNull(value) : null;
                           if (serverMaxWindowBits == null) {
                              unexpectedValues = true;
                           }
                        } else if (StringsKt.equals(name, "server_no_context_takeover", true)) {
                           if (serverNoContextTakeover) {
                              unexpectedValues = true;
                           }

                           if (value != null) {
                              unexpectedValues = true;
                           }

                           serverNoContextTakeover = true;
                        } else {
                           unexpectedValues = true;
                        }
                     }
                  } else {
                     unexpectedValues = true;
                  }
               }
            }
         }

         return new WebSocketExtensions(compressionEnabled, clientMaxWindowBits, clientNoContextTakeover, serverMaxWindowBits, serverNoContextTakeover, unexpectedValues);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
