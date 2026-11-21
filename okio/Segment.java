package okio;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0000\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\t\b\u0016¢\u0006\u0004\b\u0002\u0010\u0003B1\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\u0002\u0010\fJ\u0006\u0010\u000f\u001a\u00020\u0000J\u0006\u0010\u0010\u001a\u00020\u0000J\b\u0010\u0011\u001a\u0004\u0018\u00010\u0000J\u000e\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u0000J\u000e\u0010\u0014\u001a\u00020\u00002\u0006\u0010\u0015\u001a\u00020\u0007J\u0006\u0010\u0016\u001a\u00020\u0017J\u0016\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u00002\u0006\u0010\u0015\u001a\u00020\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\t\u001a\u00020\n8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u000b\u001a\u00020\n8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\u0004\u0018\u00010\u00008\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\u0004\u0018\u00010\u00008\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u001b"},
   d2 = {"Lokio/Segment;", "", "<init>", "()V", "data", "", "pos", "", "limit", "shared", "", "owner", "([BIIZZ)V", "next", "prev", "sharedCopy", "unsharedCopy", "pop", "push", "segment", "split", "byteCount", "compact", "", "writeTo", "sink", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nSegment.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Segment.kt\nokio/Segment\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,187:1\n1#2:188\n*E\n"})
public final class Segment {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   @NotNull
   public final byte[] data;
   @JvmField
   public int pos;
   @JvmField
   public int limit;
   @JvmField
   public boolean shared;
   @JvmField
   public boolean owner;
   @JvmField
   @Nullable
   public Segment next;
   @JvmField
   @Nullable
   public Segment prev;
   public static final int SIZE = 8192;
   public static final int SHARE_MINIMUM = 1024;

   public Segment() {
      this.data = new byte[8192];
      this.owner = true;
      this.shared = false;
   }

   public Segment(@NotNull byte[] data, int pos, int limit, boolean shared, boolean owner) {
      Intrinsics.checkNotNullParameter(data, "data");
      super();
      this.data = data;
      this.pos = pos;
      this.limit = limit;
      this.shared = shared;
      this.owner = owner;
   }

   @NotNull
   public final Segment sharedCopy() {
      this.shared = true;
      return new Segment(this.data, this.pos, this.limit, true, false);
   }

   @NotNull
   public final Segment unsharedCopy() {
      byte[] var10002 = this.data;
      var10002 = Arrays.copyOf(var10002, var10002.length);
      Intrinsics.checkNotNullExpressionValue(var10002, "copyOf(...)");
      return new Segment(var10002, this.pos, this.limit, false, true);
   }

   @Nullable
   public final Segment pop() {
      Segment result = this.next != this ? this.next : null;
      Segment var10000 = this.prev;
      Intrinsics.checkNotNull(var10000);
      var10000.next = this.next;
      var10000 = this.next;
      Intrinsics.checkNotNull(var10000);
      var10000.prev = this.prev;
      this.next = null;
      this.prev = null;
      return result;
   }

   @NotNull
   public final Segment push(@NotNull Segment segment) {
      Intrinsics.checkNotNullParameter(segment, "segment");
      segment.prev = this;
      segment.next = this.next;
      Segment var10000 = this.next;
      Intrinsics.checkNotNull(var10000);
      var10000.prev = segment;
      this.next = segment;
      return segment;
   }

   @NotNull
   public final Segment split(int byteCount) {
      if (byteCount <= 0 || byteCount > this.limit - this.pos) {
         int var3 = 0;
         String var5 = "byteCount out of range";
         throw new IllegalArgumentException(var5.toString());
      } else {
         Segment prefix = null;
         if (byteCount >= 1024) {
            prefix = this.sharedCopy();
         } else {
            prefix = SegmentPool.take();
            ArraysKt.copyInto$default(this.data, prefix.data, 0, this.pos, this.pos + byteCount, 2, (Object)null);
         }

         prefix.limit = prefix.pos + byteCount;
         this.pos += byteCount;
         Segment var10000 = this.prev;
         Intrinsics.checkNotNull(var10000);
         var10000.push(prefix);
         return prefix;
      }
   }

   public final void compact() {
      if (this.prev == this) {
         int availableByteCount = 0;
         String var4 = "cannot compact";
         throw new IllegalStateException(var4.toString());
      } else {
         Segment var10000 = this.prev;
         Intrinsics.checkNotNull(var10000);
         if (var10000.owner) {
            int byteCount = this.limit - this.pos;
            Segment var10001 = this.prev;
            Intrinsics.checkNotNull(var10001);
            int var5 = 8192 - var10001.limit;
            var10001 = this.prev;
            Intrinsics.checkNotNull(var10001);
            int var7;
            if (var10001.shared) {
               var7 = 0;
            } else {
               Segment var8 = this.prev;
               Intrinsics.checkNotNull(var8);
               var7 = var8.pos;
            }

            int availableByteCount = var5 + var7;
            if (byteCount <= availableByteCount) {
               Segment var9 = this.prev;
               Intrinsics.checkNotNull(var9);
               this.writeTo(var9, byteCount);
               this.pop();
               SegmentPool.recycle(this);
            }
         }
      }
   }

   public final void writeTo(@NotNull Segment sink, int byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (!sink.owner) {
         int var3 = 0;
         String var4 = "only owner can write";
         throw new IllegalStateException(var4.toString());
      } else {
         if (sink.limit + byteCount > 8192) {
            if (sink.shared) {
               throw new IllegalArgumentException();
            }

            if (sink.limit + byteCount - sink.pos > 8192) {
               throw new IllegalArgumentException();
            }

            ArraysKt.copyInto$default(sink.data, sink.data, 0, sink.pos, sink.limit, 2, (Object)null);
            sink.limit -= sink.pos;
            sink.pos = 0;
         }

         ArraysKt.copyInto(this.data, sink.data, sink.limit, this.pos, this.pos + byteCount);
         sink.limit += byteCount;
         this.pos += byteCount;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lokio/Segment$Companion;", "", "<init>", "()V", "SIZE", "", "SHARE_MINIMUM", "okio"}
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
