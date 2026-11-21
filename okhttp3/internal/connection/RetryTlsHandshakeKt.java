package okhttp3.internal.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\n\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004¨\u0006\u0005"},
   d2 = {"retryTlsHandshake", "", "e", "Ljava/io/IOException;", "Lokio/IOException;", "okhttp"}
)
public final class RetryTlsHandshakeKt {
   public static final boolean retryTlsHandshake(@NotNull IOException e) {
      Intrinsics.checkNotNullParameter(e, "e");
      return e instanceof ProtocolException ? false : (e instanceof InterruptedIOException ? false : (e instanceof SSLHandshakeException && e.getCause() instanceof CertificateException ? false : (e instanceof SSLPeerUnverifiedException ? false : e instanceof SSLException)));
   }
}
