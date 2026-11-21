package okhttp3.internal.http1;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.Headers;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\u0006\u0010\f\u001a\u00020\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000f"},
   d2 = {"Lokhttp3/internal/http1/HeadersReader;", "", "source", "Lokio/BufferedSource;", "<init>", "(Lokio/BufferedSource;)V", "getSource", "()Lokio/BufferedSource;", "headerLimit", "", "readLine", "", "readHeaders", "Lokhttp3/Headers;", "Companion", "okhttp"}
)
public final class HeadersReader {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final BufferedSource source;
   private long headerLimit;
   private static final int HEADER_LIMIT = 262144;

   public HeadersReader(@NotNull BufferedSource source) {
      Intrinsics.checkNotNullParameter(source, "source");
      super();
      this.source = source;
      this.headerLimit = 262144L;
   }

   @NotNull
   public final BufferedSource getSource() {
      return this.source;
   }

   @NotNull
   public final String readLine() {
      String line = this.source.readUtf8LineStrict(this.headerLimit);
      this.headerLimit -= (long)line.length();
      return line;
   }

   @NotNull
   public final Headers readHeaders() {
      Headers.Builder result = new Headers.Builder();

      while(true) {
         String line = this.readLine();
         if (((CharSequence)line).length() == 0) {
            return result.build();
         }

         result.addLenient$okhttp(line);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/http1/HeadersReader$Companion;", "", "<init>", "()V", "HEADER_LIMIT", "", "okhttp"}
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
