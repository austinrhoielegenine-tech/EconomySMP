package okhttp3.internal.connection;

import java.net.Socket;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0010\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0003¨\u0006\u0004"},
   d2 = {"asBufferedSocket", "Lokhttp3/internal/connection/BufferedSocket;", "Ljava/net/Socket;", "Lokio/Socket;", "okhttp"}
)
public final class BufferedSocketKt {
   @NotNull
   public static final BufferedSocket asBufferedSocket(@NotNull Socket $this$asBufferedSocket) {
      Intrinsics.checkNotNullParameter($this$asBufferedSocket, "<this>");
      return asBufferedSocket(Okio.socket($this$asBufferedSocket));
   }

   @NotNull
   public static final BufferedSocket asBufferedSocket(@NotNull okio.Socket $this$asBufferedSocket) {
      Intrinsics.checkNotNullParameter($this$asBufferedSocket, "<this>");
      return new BufferedSocket($this$asBufferedSocket) {
         private final okio.Socket delegate;
         private final BufferedSource source;
         private final BufferedSink sink;

         {
            this.delegate = $receiver;
            this.source = Okio.buffer(this.delegate.getSource());
            this.sink = Okio.buffer(this.delegate.getSink());
         }

         public BufferedSource getSource() {
            return this.source;
         }

         public BufferedSink getSink() {
            return this.sink;
         }

         public void cancel() {
            this.delegate.cancel();
         }
      };
   }
}
