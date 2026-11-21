package okio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.collections.AbstractList;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0015\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0006\u0018\u0000 \u00162\b\u0012\u0004\u0012\u00020\u00020\u00012\u00060\u0003j\u0002`\u0004:\u0001\u0016B!\b\u0002\u0012\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0011\u0010\u0014\u001a\u00020\u00022\u0006\u0010\u0015\u001a\u00020\u0011H\u0096\u0002R\u001e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006X\u0080\u0004¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0007\u001a\u00020\bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0017"},
   d2 = {"Lokio/Options;", "Lkotlin/collections/AbstractList;", "Lokio/ByteString;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "byteStrings", "", "trie", "", "<init>", "([Lokio/ByteString;[I)V", "getByteStrings$okio", "()[Lokio/ByteString;", "[Lokio/ByteString;", "getTrie$okio", "()[I", "size", "", "getSize", "()I", "get", "index", "Companion", "okio"}
)
public final class Options extends AbstractList<ByteString> implements RandomAccess {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final ByteString[] byteStrings;
   @NotNull
   private final int[] trie;

   private Options(ByteString[] byteStrings, int[] trie) {
      this.byteStrings = byteStrings;
      this.trie = trie;
   }

   @NotNull
   public final ByteString[] getByteStrings$okio() {
      return this.byteStrings;
   }

   @NotNull
   public final int[] getTrie$okio() {
      return this.trie;
   }

   public int getSize() {
      return this.byteStrings.length;
   }

   @NotNull
   public ByteString get(int index) {
      return this.byteStrings[index];
   }

   @JvmStatic
   @NotNull
   public static final Options of(@NotNull ByteString... byteStrings) {
      return Companion.of(byteStrings);
   }

   // $FF: synthetic method
   public Options(ByteString[] byteStrings, int[] trie, DefaultConstructorMarker $constructor_marker) {
      this(byteStrings, trie);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\u0010 \n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J!\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\n\u0012\u0006\b\u0001\u0012\u00020\b0\u0007\"\u00020\bH\u0007¢\u0006\u0002\u0010\tJT\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00112\b\b\u0002\u0010\u0014\u001a\u00020\u00112\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00110\u0012H\u0002R\u0018\u0010\u0016\u001a\u00020\r*\u00020\u000f8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018¨\u0006\u0019"},
      d2 = {"Lokio/Options$Companion;", "", "<init>", "()V", "of", "Lokio/Options;", "byteStrings", "", "Lokio/ByteString;", "([Lokio/ByteString;)Lokio/Options;", "buildTrieRecursive", "", "nodeOffset", "", "node", "Lokio/Buffer;", "byteStringOffset", "", "", "fromIndex", "toIndex", "indexes", "intCount", "getIntCount", "(Lokio/Buffer;)J", "okio"}
   )
   @SourceDebugExtension({"SMAP\nOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Options.kt\nokio/Options$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,238:1\n1#2:239\n13870#3,3:240\n73#4:243\n73#4:244\n*S KotlinDebug\n*F\n+ 1 Options.kt\nokio/Options$Companion\n*L\n48#1:240,3\n153#1:243\n210#1:244\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final Options of(@NotNull ByteString... byteStrings) {
         Intrinsics.checkNotNullParameter(byteStrings, "byteStrings");
         if (byteStrings.length == 0) {
            ByteString[] var32 = new ByteString[0];
            int[] var15 = new int[]{0, -1};
            return new Options(var32, var15, (DefaultConstructorMarker)null);
         } else {
            List list = ArraysKt.toMutableList(byteStrings);
            CollectionsKt.sort(list);
            int var4 = list.size();
            ArrayList var5 = new ArrayList(var4);

            for(int var6 = 0; var6 < var4; ++var6) {
               int trie = 0;
               var5.add(-1);
            }

            List indexes = (List)var5;
            Object[] $this$forEachIndexed$iv = byteStrings;
            int $i$f$forEachIndexed = 0;
            int index$iv = 0;
            int byteString = 0;

            for(int it = byteStrings.length; byteString < it; ++byteString) {
               Object item$iv = $this$forEachIndexed$iv[byteString];
               int callerIndex = index$iv++;
               int var12 = 0;
               int sortedIndex = CollectionsKt.binarySearch$default(list, (Comparable)item$iv, 0, 0, 6, (Object)null);
               indexes.set(sortedIndex, callerIndex);
            }

            if (((ByteString)list.get(0)).size() <= 0) {
               $i$f$forEachIndexed = 0;
               String var22 = "the empty byte string is not a supported option";
               throw new IllegalArgumentException(var22.toString());
            } else {
               for(int a = 0; a < list.size(); ++a) {
                  ByteString prefix = (ByteString)list.get(a);
                  index$iv = a + 1;

                  while(index$iv < list.size()) {
                     ByteString byteString = (ByteString)list.get(index$iv);
                     if (!byteString.startsWith(prefix)) {
                        break;
                     }

                     if (byteString.size() == prefix.size()) {
                        int var29 = 0;
                        String trie = "duplicate option: " + byteString;
                        throw new IllegalArgumentException(trie.toString());
                     }

                     if (((Number)indexes.get(index$iv)).intValue() > ((Number)indexes.get(a)).intValue()) {
                        list.remove(index$iv);
                        ((Number)indexes.remove(index$iv)).intValue();
                     } else {
                        ++index$iv;
                     }
                  }
               }

               Buffer trieBytes = new Buffer();
               buildTrieRecursive$default(this, 0L, trieBytes, 0, list, 0, 0, indexes, 53, (Object)null);
               byteString = 0;
               int var27 = (int)this.getIntCount(trieBytes);

               int[] trie;
               for(trie = new int[var27]; byteString < var27; ++byteString) {
                  trie[byteString] = trieBytes.readInt();
               }

               Object[] var10002 = Arrays.copyOf(byteStrings, byteStrings.length);
               Intrinsics.checkNotNullExpressionValue(var10002, "copyOf(...)");
               return new Options((ByteString[])var10002, trie, (DefaultConstructorMarker)null);
            }
         }
      }

      private final void buildTrieRecursive(long nodeOffset, Buffer node, int byteStringOffset, List<? extends ByteString> byteStrings, int fromIndex, int toIndex, List<Integer> indexes) {
         if (fromIndex >= toIndex) {
            String var22 = "Failed requirement.";
            throw new IllegalArgumentException(var22.toString());
         } else {
            for(int i = fromIndex; i < toIndex; ++i) {
               if (((ByteString)byteStrings.get(i)).size() < byteStringOffset) {
                  String var11 = "Failed requirement.";
                  throw new IllegalArgumentException(var11.toString());
               }
            }

            int fromIndex = fromIndex;
            ByteString from = (ByteString)byteStrings.get(fromIndex);
            ByteString to = (ByteString)byteStrings.get(toIndex - 1);
            int prefixIndex = -1;
            if (byteStringOffset == from.size()) {
               prefixIndex = ((Number)indexes.get(fromIndex)).intValue();
               fromIndex = fromIndex + 1;
               from = (ByteString)byteStrings.get(fromIndex);
            }

            if (from.getByte(byteStringOffset) != to.getByte(byteStringOffset)) {
               int selectChoiceCount = 1;

               for(int i = fromIndex + 1; i < toIndex; ++i) {
                  if (((ByteString)byteStrings.get(i - 1)).getByte(byteStringOffset) != ((ByteString)byteStrings.get(i)).getByte(byteStringOffset)) {
                     ++selectChoiceCount;
                  }
               }

               long childNodesOffset = nodeOffset + this.getIntCount(node) + (long)2 + (long)(selectChoiceCount * 2);
               node.writeInt(selectChoiceCount);
               node.writeInt(prefixIndex);

               for(int i = fromIndex; i < toIndex; ++i) {
                  byte rangeByte = ((ByteString)byteStrings.get(i)).getByte(byteStringOffset);
                  if (i == fromIndex || rangeByte != ((ByteString)byteStrings.get(i - 1)).getByte(byteStringOffset)) {
                     int other$iv = 255;
                     int $i$f$and = 0;
                     node.writeInt(rangeByte & other$iv);
                  }
               }

               Buffer childNodes = new Buffer();

               int rangeEnd;
               for(int rangeStart = fromIndex; rangeStart < toIndex; rangeStart = rangeEnd) {
                  byte rangeByte = ((ByteString)byteStrings.get(rangeStart)).getByte(byteStringOffset);
                  rangeEnd = toIndex;

                  for(int i = rangeStart + 1; i < toIndex; ++i) {
                     if (rangeByte != ((ByteString)byteStrings.get(i)).getByte(byteStringOffset)) {
                        rangeEnd = i;
                        break;
                     }
                  }

                  if (rangeStart + 1 == rangeEnd && byteStringOffset + 1 == ((ByteString)byteStrings.get(rangeStart)).size()) {
                     node.writeInt(((Number)indexes.get(rangeStart)).intValue());
                  } else {
                     node.writeInt(-1 * (int)(childNodesOffset + this.getIntCount(childNodes)));
                     this.buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + 1, byteStrings, rangeStart, rangeEnd, indexes);
                  }
               }

               Long var10000 = node.writeAll(childNodes);
            } else {
               int scanByteCount = 0;
               int i = byteStringOffset;

               for(int var15 = Math.min(from.size(), to.size()); i < var15 && from.getByte(i) == to.getByte(i); ++i) {
                  ++scanByteCount;
               }

               long childNodesOffset = nodeOffset + this.getIntCount(node) + (long)2 + (long)scanByteCount + 1L;
               node.writeInt(-scanByteCount);
               node.writeInt(prefixIndex);
               int i = byteStringOffset;

               for(int var32 = byteStringOffset + scanByteCount; i < var32; ++i) {
                  byte rangeByte = from.getByte(i);
                  int other$iv = 255;
                  int $i$f$and = 0;
                  node.writeInt(rangeByte & other$iv);
               }

               if (fromIndex + 1 == toIndex) {
                  if (byteStringOffset + scanByteCount != ((ByteString)byteStrings.get(fromIndex)).size()) {
                     throw new IllegalStateException("Check failed.");
                  }

                  node.writeInt(((Number)indexes.get(fromIndex)).intValue());
               } else {
                  Buffer childNodes = new Buffer();
                  node.writeInt(-1 * (int)(childNodesOffset + this.getIntCount(childNodes)));
                  this.buildTrieRecursive(childNodesOffset, childNodes, byteStringOffset + scanByteCount, byteStrings, fromIndex, toIndex, indexes);
                  Long var38 = node.writeAll(childNodes);
               }
            }

         }
      }

      // $FF: synthetic method
      static void buildTrieRecursive$default(Companion var0, long var1, Buffer var3, int var4, List var5, int var6, int var7, List var8, int var9, Object var10) {
         if ((var9 & 1) != 0) {
            var1 = 0L;
         }

         if ((var9 & 4) != 0) {
            var4 = 0;
         }

         if ((var9 & 16) != 0) {
            var6 = 0;
         }

         if ((var9 & 32) != 0) {
            var7 = var5.size();
         }

         var0.buildTrieRecursive(var1, var3, var4, var5, var6, var7, var8);
      }

      private final long getIntCount(Buffer $this$intCount) {
         return $this$intCount.size() / (long)4;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
