package okio;

import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000 \n\u0000\n\u0002\u0010\u000b\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0005\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0082\b\u001a\r\u0010\r\u001a\u00020\u000e*\u00020\u000fH\u0086\b\"\u000e\u0010\u0004\u001a\u00020\u0002X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0002X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0002X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0002X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"getBit", "", "", "bit", "FHCRC", "FEXTRA", "FNAME", "FCOMMENT", "SECTION_HEADER", "", "SECTION_BODY", "SECTION_TRAILER", "SECTION_DONE", "gzip", "Lokio/GzipSource;", "Lokio/Source;", "okio"}
)
@JvmName(
   name = "-GzipSourceExtensions"
)
public final class -GzipSourceExtensions {
   private static final int FHCRC = 1;
   private static final int FEXTRA = 2;
   private static final int FNAME = 3;
   private static final int FCOMMENT = 4;
   private static final byte SECTION_HEADER = 0;
   private static final byte SECTION_BODY = 1;
   private static final byte SECTION_TRAILER = 2;
   private static final byte SECTION_DONE = 3;

   private static final boolean getBit(int $this$getBit, int bit) {
      int $i$f$getBit = 0;
      return ($this$getBit >> bit & 1) == 1;
   }

   @NotNull
   public static final GzipSource gzip(@NotNull Source $this$gzip) {
      Intrinsics.checkNotNullParameter($this$gzip, "<this>");
      int $i$f$gzip = 0;
      return new GzipSource($this$gzip);
   }
}
