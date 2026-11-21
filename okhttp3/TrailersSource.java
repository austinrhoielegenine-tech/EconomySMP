package okhttp3;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u0000 \u00052\u00020\u0001:\u0001\u0005J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0003H&¨\u0006\u0006À\u0006\u0003"},
   d2 = {"Lokhttp3/TrailersSource;", "", "peek", "Lokhttp3/Headers;", "get", "Companion", "okhttp"}
)
public interface TrailersSource {
   @NotNull
   Companion Companion = TrailersSource.Companion.$$INSTANCE;
   @JvmField
   @NotNull
   TrailersSource EMPTY = new TrailersSource() {
      public Headers peek() {
         return Headers.EMPTY;
      }

      public Headers get() {
         return Headers.EMPTY;
      }
   };

   @Nullable
   default Headers peek() throws IOException {
      return null;
   }

   @NotNull
   Headers get() throws IOException;

   @Metadata(
      mv = {2, 2, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      @Nullable
      public static Headers peek(@NotNull TrailersSource $this) throws IOException {
         return $this.peek();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0013\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0001¨\u0006\u0006"},
      d2 = {"Lokhttp3/TrailersSource$Companion;", "", "<init>", "()V", "EMPTY", "Lokhttp3/TrailersSource;", "okhttp"}
   )
   public static final class Companion {
      // $FF: synthetic field
      static final Companion $$INSTANCE = new Companion();

      private Companion() {
      }
   }
}
