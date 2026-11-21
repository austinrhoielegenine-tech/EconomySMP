package okhttp3.internal.http2;

import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\r\u001a\u00020\u000eJ\u0019\u0010\u0004\u001a\u00020\u00002\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005H\u0086\u0002J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u0005J\u0011\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005H\u0086\u0002J\u0006\u0010\u0014\u001a\u00020\u0005J\u000e\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0012J\u0006\u0010\u0017\u001a\u00020\u0005J\u000e\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005J\u000e\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0005J\u000e\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\f\u0010\n¨\u0006\u001d"},
   d2 = {"Lokhttp3/internal/http2/Settings;", "", "<init>", "()V", "set", "", "values", "", "headerTableSize", "getHeaderTableSize", "()I", "initialWindowSize", "getInitialWindowSize", "clear", "", "id", "value", "isSet", "", "get", "size", "getEnablePush", "defaultValue", "getMaxConcurrentStreams", "getMaxFrameSize", "getMaxHeaderListSize", "merge", "other", "Companion", "okhttp"}
)
public final class Settings {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private int set;
   @NotNull
   private final int[] values = new int[10];
   public static final int DEFAULT_INITIAL_WINDOW_SIZE = 65535;
   public static final int HEADER_TABLE_SIZE = 1;
   public static final int ENABLE_PUSH = 2;
   public static final int MAX_CONCURRENT_STREAMS = 3;
   public static final int INITIAL_WINDOW_SIZE = 4;
   public static final int MAX_FRAME_SIZE = 5;
   public static final int MAX_HEADER_LIST_SIZE = 6;
   public static final int COUNT = 10;

   public final int getHeaderTableSize() {
      int bit = 2;
      return (bit & this.set) != 0 ? this.values[1] : -1;
   }

   public final int getInitialWindowSize() {
      int bit = 16;
      return (bit & this.set) != 0 ? this.values[4] : '\uffff';
   }

   public final void clear() {
      this.set = 0;
      ArraysKt.fill$default(this.values, 0, 0, 0, 6, (Object)null);
   }

   @NotNull
   public final Settings set(int id, int value) {
      if (id >= 0 && id < this.values.length) {
         int bit = 1 << id;
         this.set |= bit;
         this.values[id] = value;
         return this;
      } else {
         return this;
      }
   }

   public final boolean isSet(int id) {
      int bit = 1 << id;
      return (this.set & bit) != 0;
   }

   public final int get(int id) {
      return this.values[id];
   }

   public final int size() {
      return Integer.bitCount(this.set);
   }

   public final boolean getEnablePush(boolean defaultValue) {
      int bit = 4;
      return (bit & this.set) != 0 ? this.values[2] == 1 : defaultValue;
   }

   public final int getMaxConcurrentStreams() {
      int bit = 8;
      return (bit & this.set) != 0 ? this.values[3] : Integer.MAX_VALUE;
   }

   public final int getMaxFrameSize(int defaultValue) {
      int bit = 32;
      return (bit & this.set) != 0 ? this.values[5] : defaultValue;
   }

   public final int getMaxHeaderListSize(int defaultValue) {
      int bit = 64;
      return (bit & this.set) != 0 ? this.values[6] : defaultValue;
   }

   public final void merge(@NotNull Settings other) {
      Intrinsics.checkNotNullParameter(other, "other");

      for(int i = 0; i < 10; ++i) {
         if (other.isSet(i)) {
            this.set(i, other.get(i));
         }
      }

   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\r"},
      d2 = {"Lokhttp3/internal/http2/Settings$Companion;", "", "<init>", "()V", "DEFAULT_INITIAL_WINDOW_SIZE", "", "HEADER_TABLE_SIZE", "ENABLE_PUSH", "MAX_CONCURRENT_STREAMS", "INITIAL_WINDOW_SIZE", "MAX_FRAME_SIZE", "MAX_HEADER_LIST_SIZE", "COUNT", "okhttp"}
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
