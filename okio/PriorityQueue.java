package okio;

import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u0004\u0018\u00010\bJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bJ\u000e\u0010\u000e\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bJ\u0018\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\bH\u0002J\u0018\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\bH\u0002J\u0015\u0010\u0012\u001a\u00020\u0005*\u00020\b2\u0006\u0010\u0013\u001a\u00020\bH\u0082\nR\u0012\u0010\u0004\u001a\u00020\u00058\u0000@\u0000X\u0081\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u00078\u0000@\u0000X\u0081\u000e¢\u0006\u0004\n\u0002\u0010\t¨\u0006\u0014"},
   d2 = {"Lokio/PriorityQueue;", "", "<init>", "()V", "size", "", "array", "", "Lokio/AsyncTimeout;", "[Lokio/AsyncTimeout;", "first", "add", "", "node", "remove", "heapifyUp", "vacantIndex", "heapifyDown", "compareTo", "other", "okio"}
)
@SourceDebugExtension({"SMAP\nAsyncTimeout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AsyncTimeout.kt\nokio/PriorityQueue\n*L\n1#1,514:1\n509#1,3:515\n509#1,3:518\n509#1,3:521\n509#1,3:524\n*S KotlinDebug\n*F\n+ 1 AsyncTimeout.kt\nokio/PriorityQueue\n*L\n415#1:515,3\n448#1:518,3\n481#1:521,3\n491#1:524,3\n*E\n"})
public final class PriorityQueue {
   @JvmField
   public int size;
   @JvmField
   @NotNull
   public AsyncTimeout[] array = new AsyncTimeout[8];

   @Nullable
   public final AsyncTimeout first() {
      return this.array[1];
   }

   public final void add(@NotNull AsyncTimeout node) {
      Intrinsics.checkNotNullParameter(node, "node");
      int newSize = this.size + 1;
      this.size = newSize;
      if (newSize == this.array.length) {
         AsyncTimeout[] doubledArray = new AsyncTimeout[newSize * 2];
         ArraysKt.copyInto$default(this.array, doubledArray, 0, 0, 0, 14, (Object)null);
         this.array = doubledArray;
      }

      this.heapifyUp(newSize, node);
   }

   public final void remove(@NotNull AsyncTimeout node) {
      Intrinsics.checkNotNullParameter(node, "node");
      if (node.index == -1) {
         String var14 = "Failed requirement.";
         throw new IllegalArgumentException(var14.toString());
      } else {
         int oldSize = this.size;
         int removedIndex = node.index;
         AsyncTimeout var10000 = this.array[oldSize];
         Intrinsics.checkNotNull(var10000);
         AsyncTimeout last = var10000;
         node.index = -1;
         this.array[oldSize] = null;
         this.size = oldSize - 1;
         if (node != last) {
            int $i$f$compareTo = 0;
            long a$iv = node.getTimeoutAt$okio();
            long b$iv = last.getTimeoutAt$okio();
            int nodeCompareToLast = Intrinsics.compare(0L, b$iv - a$iv);
            if (nodeCompareToLast == 0) {
               this.array[removedIndex] = last;
               last.index = removedIndex;
            } else if (nodeCompareToLast < 0) {
               this.heapifyDown(removedIndex, last);
            } else {
               this.heapifyUp(removedIndex, last);
            }

         }
      }
   }

   private final void heapifyUp(int vacantIndex, AsyncTimeout node) {
      int vacantIndex = vacantIndex;

      while(true) {
         int parentIndex = vacantIndex >> 1;
         if (parentIndex == 0) {
            break;
         }

         AsyncTimeout var10000 = this.array[parentIndex];
         Intrinsics.checkNotNull(var10000);
         AsyncTimeout parentNode = var10000;
         int $i$f$compareTo = 0;
         long a$iv = parentNode.getTimeoutAt$okio();
         long b$iv = node.getTimeoutAt$okio();
         if (Intrinsics.compare(0L, b$iv - a$iv) <= 0) {
            break;
         }

         parentNode.index = vacantIndex;
         this.array[vacantIndex] = parentNode;
         vacantIndex = parentIndex;
      }

      this.array[vacantIndex] = node;
      node.index = vacantIndex;
   }

   private final void heapifyDown(int vacantIndex, AsyncTimeout node) {
      int vacantIndex = vacantIndex;

      while(true) {
         int leftIndex = vacantIndex << 1;
         int rightIndex = leftIndex + 1;
         AsyncTimeout var20;
         if (rightIndex <= this.size) {
            var20 = this.array[leftIndex];
            Intrinsics.checkNotNull(var20);
            AsyncTimeout leftNode = var20;
            var20 = this.array[rightIndex];
            Intrinsics.checkNotNull(var20);
            AsyncTimeout rightNode = var20;
            int $i$f$compareTo = 0;
            long a$iv = leftNode.getTimeoutAt$okio();
            long b$iv = rightNode.getTimeoutAt$okio();
            var20 = Intrinsics.compare(0L, b$iv - a$iv) < 0 ? leftNode : rightNode;
         } else {
            if (leftIndex > this.size) {
               break;
            }

            var20 = this.array[leftIndex];
            Intrinsics.checkNotNull(var20);
         }

         AsyncTimeout smallestChild = var20;
         int $i$f$compareTo = 0;
         long a$iv = node.getTimeoutAt$okio();
         long b$iv = smallestChild.getTimeoutAt$okio();
         if (Intrinsics.compare(0L, b$iv - a$iv) <= 0) {
            break;
         }

         int newVacantIndex = smallestChild.index;
         smallestChild.index = vacantIndex;
         this.array[vacantIndex] = smallestChild;
         vacantIndex = newVacantIndex;
      }

      this.array[vacantIndex] = node;
      node.index = vacantIndex;
   }

   private final int compareTo(AsyncTimeout $this$compareTo, AsyncTimeout other) {
      Intrinsics.checkNotNullParameter($this$compareTo, "<this>");
      int $i$f$compareTo = 0;
      long a = $this$compareTo.getTimeoutAt$okio();
      long b = other.getTimeoutAt$okio();
      return Intrinsics.compare(0L, b - a);
   }
}
