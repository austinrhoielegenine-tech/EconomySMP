package okhttp3.internal.ws;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u000e\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"J\u0010\u0010#\u001a\u0004\u0018\u00010\u00052\u0006\u0010$\u001a\u00020\u0007J\u000e\u0010%\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020\u0007J\u000e\u0010&\u001a\u00020\u00052\u0006\u0010!\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0016X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0016X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0007X\u0080T¢\u0006\u0002\n\u0000¨\u0006'"},
   d2 = {"Lokhttp3/internal/ws/WebSocketProtocol;", "", "<init>", "()V", "ACCEPT_MAGIC", "", "B0_FLAG_FIN", "", "B0_FLAG_RSV1", "B0_FLAG_RSV2", "B0_FLAG_RSV3", "B0_MASK_OPCODE", "OPCODE_FLAG_CONTROL", "B1_FLAG_MASK", "B1_MASK_LENGTH", "OPCODE_CONTINUATION", "OPCODE_TEXT", "OPCODE_BINARY", "OPCODE_CONTROL_CLOSE", "OPCODE_CONTROL_PING", "OPCODE_CONTROL_PONG", "PAYLOAD_BYTE_MAX", "", "CLOSE_MESSAGE_MAX", "PAYLOAD_SHORT", "PAYLOAD_SHORT_MAX", "PAYLOAD_LONG", "CLOSE_CLIENT_GOING_AWAY", "CLOSE_NO_STATUS_CODE", "toggleMask", "", "cursor", "Lokio/Buffer$UnsafeCursor;", "key", "", "closeCodeExceptionMessage", "code", "validateCloseCode", "acceptHeader", "okhttp"}
)
@SourceDebugExtension({"SMAP\nWebSocketProtocol.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WebSocketProtocol.kt\nokhttp3/internal/ws/WebSocketProtocol\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,152:1\n1#2:153\n*E\n"})
public final class WebSocketProtocol {
   @NotNull
   public static final WebSocketProtocol INSTANCE = new WebSocketProtocol();
   @NotNull
   public static final String ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   public static final int B0_FLAG_FIN = 128;
   public static final int B0_FLAG_RSV1 = 64;
   public static final int B0_FLAG_RSV2 = 32;
   public static final int B0_FLAG_RSV3 = 16;
   public static final int B0_MASK_OPCODE = 15;
   public static final int OPCODE_FLAG_CONTROL = 8;
   public static final int B1_FLAG_MASK = 128;
   public static final int B1_MASK_LENGTH = 127;
   public static final int OPCODE_CONTINUATION = 0;
   public static final int OPCODE_TEXT = 1;
   public static final int OPCODE_BINARY = 2;
   public static final int OPCODE_CONTROL_CLOSE = 8;
   public static final int OPCODE_CONTROL_PING = 9;
   public static final int OPCODE_CONTROL_PONG = 10;
   public static final long PAYLOAD_BYTE_MAX = 125L;
   public static final long CLOSE_MESSAGE_MAX = 123L;
   public static final int PAYLOAD_SHORT = 126;
   public static final long PAYLOAD_SHORT_MAX = 65535L;
   public static final int PAYLOAD_LONG = 127;
   public static final int CLOSE_CLIENT_GOING_AWAY = 1001;
   public static final int CLOSE_NO_STATUS_CODE = 1005;

   private WebSocketProtocol() {
   }

   public final void toggleMask(@NotNull Buffer.UnsafeCursor cursor, @NotNull byte[] key) {
      Intrinsics.checkNotNullParameter(cursor, "cursor");
      Intrinsics.checkNotNullParameter(key, "key");
      int keyIndex = 0;
      int keyLength = key.length;

      do {
         byte[] buffer = cursor.data;
         int i = cursor.start;
         int end = cursor.end;
         if (buffer != null) {
            while(i < end) {
               keyIndex %= keyLength;
               int bufferInt = buffer[i];
               int keyInt = key[keyIndex];
               buffer[i] = (byte)(bufferInt ^ keyInt);
               ++i;
               ++keyIndex;
            }
         }
      } while(cursor.next() != -1);

   }

   @Nullable
   public final String closeCodeExceptionMessage(int code) {
      return code >= 1000 && code < 5000 ? (!(1004 <= code ? code < 1007 : false) && !(1015 <= code ? code < 3000 : false) ? null : "Code " + code + " is reserved and may not be used.") : "Code must be in range [1000,5000): " + code;
   }

   public final void validateCloseCode(int code) {
      String message = this.closeCodeExceptionMessage(code);
      if (message != null) {
         int var3 = 0;
         Intrinsics.checkNotNull(message);
         throw new IllegalArgumentException(message.toString());
      }
   }

   @NotNull
   public final String acceptHeader(@NotNull String key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return ByteString.Companion.encodeUtf8(key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
   }
}
