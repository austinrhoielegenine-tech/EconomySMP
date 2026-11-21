package okhttp3;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0003\n\u0000\b&\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u000eH\u0016J \u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\fH\u0016J \u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\fH\u0016J\"\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016¨\u0006\u0017"},
   d2 = {"Lokhttp3/WebSocketListener;", "", "<init>", "()V", "onOpen", "", "webSocket", "Lokhttp3/WebSocket;", "response", "Lokhttp3/Response;", "onMessage", "text", "", "bytes", "Lokio/ByteString;", "onClosing", "code", "", "reason", "onClosed", "onFailure", "t", "", "okhttp"}
)
public abstract class WebSocketListener {
   public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
      Intrinsics.checkNotNullParameter(webSocket, "webSocket");
      Intrinsics.checkNotNullParameter(response, "response");
   }

   public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
      Intrinsics.checkNotNullParameter(webSocket, "webSocket");
      Intrinsics.checkNotNullParameter(text, "text");
   }

   public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
      Intrinsics.checkNotNullParameter(webSocket, "webSocket");
      Intrinsics.checkNotNullParameter(bytes, "bytes");
   }

   public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
      Intrinsics.checkNotNullParameter(webSocket, "webSocket");
      Intrinsics.checkNotNullParameter(reason, "reason");
   }

   public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
      Intrinsics.checkNotNullParameter(webSocket, "webSocket");
      Intrinsics.checkNotNullParameter(reason, "reason");
   }

   public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
      Intrinsics.checkNotNullParameter(webSocket, "webSocket");
      Intrinsics.checkNotNullParameter(t, "t");
   }
}
