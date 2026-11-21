package okhttp3.internal.http2;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0002\b\u0014\b\u0086\u0081\u0002\u0018\u0000 \u00162\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0016B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015¨\u0006\u0017"},
   d2 = {"Lokhttp3/internal/http2/ErrorCode;", "", "httpCode", "", "<init>", "(Ljava/lang/String;II)V", "getHttpCode", "()I", "NO_ERROR", "PROTOCOL_ERROR", "INTERNAL_ERROR", "FLOW_CONTROL_ERROR", "SETTINGS_TIMEOUT", "STREAM_CLOSED", "FRAME_SIZE_ERROR", "REFUSED_STREAM", "CANCEL", "COMPRESSION_ERROR", "CONNECT_ERROR", "ENHANCE_YOUR_CALM", "INADEQUATE_SECURITY", "HTTP_1_1_REQUIRED", "Companion", "okhttp"}
)
public enum ErrorCode {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private final int httpCode;
   NO_ERROR(0),
   PROTOCOL_ERROR(1),
   INTERNAL_ERROR(2),
   FLOW_CONTROL_ERROR(3),
   SETTINGS_TIMEOUT(4),
   STREAM_CLOSED(5),
   FRAME_SIZE_ERROR(6),
   REFUSED_STREAM(7),
   CANCEL(8),
   COMPRESSION_ERROR(9),
   CONNECT_ERROR(10),
   ENHANCE_YOUR_CALM(11),
   INADEQUATE_SECURITY(12),
   HTTP_1_1_REQUIRED(13);

   // $FF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

   private ErrorCode(int httpCode) {
      this.httpCode = httpCode;
   }

   public final int getHttpCode() {
      return this.httpCode;
   }

   @NotNull
   public static EnumEntries<ErrorCode> getEntries() {
      return $ENTRIES;
   }

   // $FF: synthetic method
   private static final ErrorCode[] $values() {
      ErrorCode[] var0 = new ErrorCode[]{NO_ERROR, PROTOCOL_ERROR, INTERNAL_ERROR, FLOW_CONTROL_ERROR, SETTINGS_TIMEOUT, STREAM_CLOSED, FRAME_SIZE_ERROR, REFUSED_STREAM, CANCEL, COMPRESSION_ERROR, CONNECT_ERROR, ENHANCE_YOUR_CALM, INADEQUATE_SECURITY, HTTP_1_1_REQUIRED};
      return var0;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
      d2 = {"Lokhttp3/internal/http2/ErrorCode$Companion;", "", "<init>", "()V", "fromHttp2", "Lokhttp3/internal/http2/ErrorCode;", "code", "", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nErrorCode.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ErrorCode.kt\nokhttp3/internal/http2/ErrorCode$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,56:1\n1#2:57\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final ErrorCode fromHttp2(int code) {
         ErrorCode[] var2 = ErrorCode.values();
         int var3 = 0;
         int var4 = var2.length;

         ErrorCode var10000;
         while(true) {
            if (var3 >= var4) {
               var10000 = null;
               break;
            }

            ErrorCode it = var2[var3];
            int var7 = 0;
            if (it.getHttpCode() == code) {
               var10000 = it;
               break;
            }

            ++var3;
         }

         return var10000;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
