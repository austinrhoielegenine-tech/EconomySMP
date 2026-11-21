package okio;

import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0011\u001a\u00020\tH\u0007J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\tH\u0007J\u0010\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\rH\u0002R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\r0\fX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0007¨\u0006\u0016"},
   d2 = {"Lokio/SegmentPool;", "", "<init>", "()V", "MAX_SIZE", "", "getMAX_SIZE", "()I", "LOCK", "Lokio/Segment;", "HASH_BUCKET_COUNT", "hashBuckets", "", "Ljava/util/concurrent/atomic/AtomicReference;", "[Ljava/util/concurrent/atomic/AtomicReference;", "byteCount", "getByteCount", "take", "recycle", "", "segment", "firstRef", "okio"}
)
public final class SegmentPool {
   @NotNull
   public static final SegmentPool INSTANCE = new SegmentPool();
   private static final int MAX_SIZE = 65536;
   @NotNull
   private static final Segment LOCK = new Segment(new byte[0], 0, 0, false, false);
   private static final int HASH_BUCKET_COUNT = Integer.highestOneBit(Runtime.getRuntime().availableProcessors() * 2 - 1);
   @NotNull
   private static final AtomicReference<Segment>[] hashBuckets;

   private SegmentPool() {
   }

   public final int getMAX_SIZE() {
      return MAX_SIZE;
   }

   public final int getByteCount() {
      Segment var10000 = (Segment)this.firstRef().get();
      if (var10000 == null) {
         return 0;
      } else {
         Segment first = var10000;
         return first.limit;
      }
   }

   @JvmStatic
   @NotNull
   public static final Segment take() {
      AtomicReference firstRef = INSTANCE.firstRef();
      Segment first = (Segment)firstRef.getAndSet(LOCK);
      if (first == LOCK) {
         return new Segment();
      } else if (first == null) {
         firstRef.set((Object)null);
         return new Segment();
      } else {
         firstRef.set(first.next);
         first.next = null;
         first.limit = 0;
         return first;
      }
   }

   @JvmStatic
   public static final void recycle(@NotNull Segment segment) {
      Intrinsics.checkNotNullParameter(segment, "segment");
      if (segment.next != null || segment.prev != null) {
         String var4 = "Failed requirement.";
         throw new IllegalArgumentException(var4.toString());
      } else if (!segment.shared) {
         AtomicReference firstRef = INSTANCE.firstRef();
         Segment first = (Segment)firstRef.getAndSet(LOCK);
         if (first != LOCK) {
            int firstLimit = first != null ? first.limit : 0;
            SegmentPool var10001 = INSTANCE;
            if (firstLimit >= MAX_SIZE) {
               firstRef.set(first);
            } else {
               segment.next = first;
               segment.pos = 0;
               segment.limit = firstLimit + 8192;
               firstRef.set(segment);
            }
         }
      }
   }

   private final AtomicReference<Segment> firstRef() {
      int hashBucket = (int)(Thread.currentThread().getId() & (long)HASH_BUCKET_COUNT - 1L);
      return hashBuckets[hashBucket];
   }

   static {
      int var0 = 0;
      int var1 = HASH_BUCKET_COUNT;

      AtomicReference[] var2;
      for(var2 = new AtomicReference[var1]; var0 < var1; ++var0) {
         var2[var0] = new AtomicReference();
      }

      hashBuckets = var2;
   }
}
