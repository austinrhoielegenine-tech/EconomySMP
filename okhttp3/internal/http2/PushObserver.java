package okhttp3.internal.http2;

import java.io.IOException;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H&J&\u0010\t\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\u000b\u001a\u00020\u0003H&J(\u0010\f\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0003H&J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0013H&¨\u0006\u0015À\u0006\u0003"},
   d2 = {"Lokhttp3/internal/http2/PushObserver;", "", "onRequest", "", "streamId", "", "requestHeaders", "", "Lokhttp3/internal/http2/Header;", "onHeaders", "responseHeaders", "last", "onData", "source", "Lokio/BufferedSource;", "byteCount", "onReset", "", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "Companion", "okhttp"}
)
public interface PushObserver {
   @NotNull
   Companion Companion = PushObserver.Companion.$$INSTANCE;
   @JvmField
   @NotNull
   PushObserver CANCEL = new Companion.PushObserverCancel();

   boolean onRequest(int var1, @NotNull List<Header> var2);

   boolean onHeaders(int var1, @NotNull List<Header> var2, boolean var3);

   boolean onData(int var1, @NotNull BufferedSource var2, int var3, boolean var4) throws IOException;

   void onReset(int var1, @NotNull ErrorCode var2);

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0006B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0013\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0001¨\u0006\u0007"},
      d2 = {"Lokhttp3/internal/http2/PushObserver$Companion;", "", "<init>", "()V", "CANCEL", "Lokhttp3/internal/http2/PushObserver;", "PushObserverCancel", "okhttp"}
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
         d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0016J&\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\r\u001a\u00020\u0005H\u0016J(\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\u0005H\u0016J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u0015H\u0016¨\u0006\u0016"},
         d2 = {"Lokhttp3/internal/http2/PushObserver$Companion$PushObserverCancel;", "Lokhttp3/internal/http2/PushObserver;", "<init>", "()V", "onRequest", "", "streamId", "", "requestHeaders", "", "Lokhttp3/internal/http2/Header;", "onHeaders", "responseHeaders", "last", "onData", "source", "Lokio/BufferedSource;", "byteCount", "onReset", "", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "okhttp"}
      )
      private static final class PushObserverCancel implements PushObserver {
         public PushObserverCancel() {
         }

         public boolean onRequest(int streamId, @NotNull List<Header> requestHeaders) {
            Intrinsics.checkNotNullParameter(requestHeaders, "requestHeaders");
            return true;
         }

         public boolean onHeaders(int streamId, @NotNull List<Header> responseHeaders, boolean last) {
            Intrinsics.checkNotNullParameter(responseHeaders, "responseHeaders");
            return true;
         }

         public boolean onData(int streamId, @NotNull BufferedSource source, int byteCount, boolean last) throws IOException {
            Intrinsics.checkNotNullParameter(source, "source");
            source.skip((long)byteCount);
            return true;
         }

         public void onReset(int streamId, @NotNull ErrorCode errorCode) {
            Intrinsics.checkNotNullParameter(errorCode, "errorCode");
         }
      }
   }
}
