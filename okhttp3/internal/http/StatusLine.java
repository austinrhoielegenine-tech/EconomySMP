package okhttp3.internal.http;

import java.io.IOException;
import java.net.ProtocolException;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Protocol;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\n\u001a\u00020\u0007H\u0016R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
   d2 = {"Lokhttp3/internal/http/StatusLine;", "", "protocol", "Lokhttp3/Protocol;", "code", "", "message", "", "<init>", "(Lokhttp3/Protocol;ILjava/lang/String;)V", "toString", "Companion", "okhttp"}
)
public final class StatusLine {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   @NotNull
   public final Protocol protocol;
   @JvmField
   public final int code;
   @JvmField
   @NotNull
   public final String message;

   public StatusLine(@NotNull Protocol protocol, int code, @NotNull String message) {
      Intrinsics.checkNotNullParameter(protocol, "protocol");
      Intrinsics.checkNotNullParameter(message, "message");
      super();
      this.protocol = protocol;
      this.code = code;
      this.message = message;
   }

   @NotNull
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      int var3 = 0;
      if (this.protocol == Protocol.HTTP_1_0) {
         var1.append("HTTP/1.0");
      } else {
         var1.append("HTTP/1.1");
      }

      var1.append(' ').append(this.code);
      var1.append(' ').append(this.message);
      return var1.toString();
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\n¨\u0006\u000b"},
      d2 = {"Lokhttp3/internal/http/StatusLine$Companion;", "", "<init>", "()V", "get", "Lokhttp3/internal/http/StatusLine;", "response", "Lokhttp3/Response;", "parse", "statusLine", "", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final StatusLine get(@NotNull Response response) {
         Intrinsics.checkNotNullParameter(response, "response");
         return new StatusLine(response.protocol(), response.code(), response.message());
      }

      @NotNull
      public final StatusLine parse(@NotNull String statusLine) throws IOException {
         Intrinsics.checkNotNullParameter(statusLine, "statusLine");
         int codeStart = 0;
         Protocol protocol = null;
         if (StringsKt.startsWith$default(statusLine, "HTTP/1.", false, 2, (Object)null)) {
            if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
               throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            int httpMinorVersion = statusLine.charAt(7) - 48;
            codeStart = 9;
            Protocol var10000;
            switch (httpMinorVersion) {
               case 0:
                  var10000 = Protocol.HTTP_1_0;
                  break;
               case 1:
                  var10000 = Protocol.HTTP_1_1;
                  break;
               default:
                  throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            protocol = var10000;
         } else if (StringsKt.startsWith$default(statusLine, "ICY ", false, 2, (Object)null)) {
            protocol = Protocol.HTTP_1_0;
            codeStart = 4;
         } else {
            if (!StringsKt.startsWith$default(statusLine, "SOURCETABLE ", false, 2, (Object)null)) {
               throw new ProtocolException("Unexpected status line: " + statusLine);
            }

            protocol = Protocol.HTTP_1_1;
            codeStart = 12;
         }

         if (statusLine.length() < codeStart + 3) {
            throw new ProtocolException("Unexpected status line: " + statusLine);
         } else {
            String var9 = statusLine.substring(codeStart, codeStart + 3);
            Intrinsics.checkNotNullExpressionValue(var9, "substring(...)");
            Integer var10 = StringsKt.toIntOrNull(var9);
            if (var10 != null) {
               int code = var10;
               String message = "";
               if (statusLine.length() > codeStart + 3) {
                  if (statusLine.charAt(codeStart + 3) != ' ') {
                     throw new ProtocolException("Unexpected status line: " + statusLine);
                  }

                  String var11 = statusLine.substring(codeStart + 4);
                  Intrinsics.checkNotNullExpressionValue(var11, "substring(...)");
                  message = var11;
               }

               return new StatusLine(protocol, code, message);
            } else {
               throw new ProtocolException("Unexpected status line: " + statusLine);
            }
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
