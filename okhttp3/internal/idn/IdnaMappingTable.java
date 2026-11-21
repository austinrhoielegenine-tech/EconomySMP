package okhttp3.internal.idn;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0000\u0018\u00002\u00020\u0001B!\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0010\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J \u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u000fH\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\u0016"},
   d2 = {"Lokhttp3/internal/idn/IdnaMappingTable;", "", "sections", "", "ranges", "mappings", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getSections", "()Ljava/lang/String;", "getRanges", "getMappings", "map", "", "codePoint", "", "sink", "Lokio/BufferedSink;", "findSectionsIndex", "findRangesOffset", "position", "limit", "okhttp"}
)
@SourceDebugExtension({"SMAP\nIdnaMappingTable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IdnaMappingTable.kt\nokhttp3/internal/idn/IdnaMappingTable\n+ 2 IdnaMappingTable.kt\nokhttp3/internal/idn/IdnaMappingTableKt\n*L\n1#1,286:1\n272#2,13:287\n272#2,13:300\n*S KotlinDebug\n*F\n+ 1 IdnaMappingTable.kt\nokhttp3/internal/idn/IdnaMappingTable\n*L\n209#1:287,13\n237#1:300,13\n*E\n"})
public final class IdnaMappingTable {
   @NotNull
   private final String sections;
   @NotNull
   private final String ranges;
   @NotNull
   private final String mappings;

   public IdnaMappingTable(@NotNull String sections, @NotNull String ranges, @NotNull String mappings) {
      Intrinsics.checkNotNullParameter(sections, "sections");
      Intrinsics.checkNotNullParameter(ranges, "ranges");
      Intrinsics.checkNotNullParameter(mappings, "mappings");
      super();
      this.sections = sections;
      this.ranges = ranges;
      this.mappings = mappings;
   }

   @NotNull
   public final String getSections() {
      return this.sections;
   }

   @NotNull
   public final String getRanges() {
      return this.ranges;
   }

   @NotNull
   public final String getMappings() {
      return this.mappings;
   }

   public final boolean map(int codePoint, @NotNull BufferedSink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int sectionsIndex = this.findSectionsIndex(codePoint);
      int rangesPosition = IdnaMappingTableKt.read14BitInt(this.sections, sectionsIndex + 2);
      int rangesLimit = sectionsIndex + 4 < this.sections.length() ? IdnaMappingTableKt.read14BitInt(this.sections, sectionsIndex + 6) : this.ranges.length() / 4;
      int rangesIndex = this.findRangesOffset(codePoint, rangesPosition, rangesLimit);
      int b1 = this.ranges.charAt(rangesIndex + 1);
      if (0 <= b1 ? b1 < 64 : false) {
         int beginIndex = IdnaMappingTableKt.read14BitInt(this.ranges, rangesIndex + 2);
         sink.writeUtf8(this.mappings, beginIndex, beginIndex + b1);
      } else if (64 <= b1 ? b1 < 80 : false) {
         int b2 = this.ranges.charAt(rangesIndex + 2);
         int b3 = this.ranges.charAt(rangesIndex + 3);
         int codepointDelta = (b1 & 15) << 14 | b2 << 7 | b3;
         sink.writeUtf8CodePoint(codePoint - codepointDelta);
      } else if (80 <= b1 ? b1 < 96 : false) {
         int b2 = this.ranges.charAt(rangesIndex + 2);
         int b3 = this.ranges.charAt(rangesIndex + 3);
         int codepointDelta = (b1 & 15) << 14 | b2 << 7 | b3;
         sink.writeUtf8CodePoint(codePoint + codepointDelta);
      } else if (b1 == 119) {
         Unit var10000 = Unit.INSTANCE;
      } else if (b1 == 120) {
         sink.writeUtf8CodePoint(codePoint);
      } else {
         if (b1 == 121) {
            sink.writeUtf8CodePoint(codePoint);
            return false;
         }

         if (b1 == 122) {
            sink.writeByte(this.ranges.charAt(rangesIndex + 2));
         } else if (b1 == 123) {
            sink.writeByte(this.ranges.charAt(rangesIndex + 2) | 128);
         } else if (b1 == 124) {
            sink.writeByte(this.ranges.charAt(rangesIndex + 2));
            sink.writeByte(this.ranges.charAt(rangesIndex + 3));
         } else if (b1 == 125) {
            sink.writeByte(this.ranges.charAt(rangesIndex + 2) | 128);
            sink.writeByte(this.ranges.charAt(rangesIndex + 3));
         } else if (b1 == 126) {
            sink.writeByte(this.ranges.charAt(rangesIndex + 2));
            sink.writeByte(this.ranges.charAt(rangesIndex + 3) | 128);
         } else {
            if (b1 != 127) {
               throw new IllegalStateException(("unexpected rangesIndex for " + codePoint).toString());
            }

            sink.writeByte(this.ranges.charAt(rangesIndex + 2) | 128);
            sink.writeByte(this.ranges.charAt(rangesIndex + 3) | 128);
         }
      }

      return true;
   }

   private final int findSectionsIndex(int codePoint) {
      int target = (codePoint & 2097024) >> 7;
      byte position$iv = 0;
      int limit$iv = this.sections.length() / 4;
      int $i$f$binarySearch = 0;
      int low$iv = position$iv;
      int high$iv = limit$iv - 1;

      int var10000;
      while(true) {
         if (low$iv <= high$iv) {
            int mid$iv = (low$iv + high$iv) / 2;
            int var11 = 0;
            int entryIndex = mid$iv * 4;
            int b0b1 = IdnaMappingTableKt.read14BitInt(this.sections, entryIndex);
            int compareResult$iv = Intrinsics.compare(target, b0b1);
            if (compareResult$iv < 0) {
               high$iv = mid$iv - 1;
               continue;
            }

            if (compareResult$iv > 0) {
               low$iv = mid$iv + 1;
               continue;
            }

            var10000 = mid$iv;
            break;
         }

         var10000 = -low$iv - 1;
         break;
      }

      int offset = var10000;
      return offset >= 0 ? offset * 4 : (-offset - 2) * 4;
   }

   private final int findRangesOffset(int codePoint, int position, int limit) {
      int target = codePoint & 127;
      int $i$f$binarySearch = 0;
      int low$iv = position;
      int high$iv = limit - 1;

      int var10000;
      while(true) {
         if (low$iv <= high$iv) {
            int mid$iv = (low$iv + high$iv) / 2;
            int var13 = 0;
            int entryIndex = mid$iv * 4;
            int b0 = this.ranges.charAt(entryIndex);
            int compareResult$iv = Intrinsics.compare(target, b0);
            if (compareResult$iv < 0) {
               high$iv = mid$iv - 1;
               continue;
            }

            if (compareResult$iv > 0) {
               low$iv = mid$iv + 1;
               continue;
            }

            var10000 = mid$iv;
            break;
         }

         var10000 = -low$iv - 1;
         break;
      }

      int offset = var10000;
      return offset >= 0 ? offset * 4 : (-offset - 2) * 4;
   }
}
