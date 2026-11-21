package okhttp3.internal.publicsuffix;

import kotlin.Metadata;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b`\u0018\u0000 \n2\u00020\u0001:\u0001\nJ\b\u0010\u0002\u001a\u00020\u0003H&R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0012\u0010\b\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\u000bÀ\u0006\u0003"},
   d2 = {"Lokhttp3/internal/publicsuffix/PublicSuffixList;", "", "ensureLoaded", "", "bytes", "Lokio/ByteString;", "getBytes", "()Lokio/ByteString;", "exceptionBytes", "getExceptionBytes", "Companion", "okhttp"}
)
public interface PublicSuffixList {
   @NotNull
   Companion Companion = PublicSuffixList.Companion.$$INSTANCE;

   void ensureLoaded();

   @NotNull
   ByteString getBytes();

   @NotNull
   ByteString getExceptionBytes();

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
      d2 = {"Lokhttp3/internal/publicsuffix/PublicSuffixList$Companion;", "", "<init>", "()V", "okhttp"}
   )
   public static final class Companion {
      // $FF: synthetic field
      static final Companion $$INSTANCE = new Companion();

      private Companion() {
      }
   }
}
