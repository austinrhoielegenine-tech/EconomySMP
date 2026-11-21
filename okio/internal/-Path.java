package okio.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.Buffer;
import okio.ByteString;
import okio.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000J\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\f\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u0005\n\u0002\b\u0002\u001a\u000f\u0010\u0006\u001a\u0004\u0018\u00010\u0007*\u00020\u0007H\u0080\b\u001a\u0013\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t*\u00020\u0007H\u0080\b\u001a\u0013\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\t*\u00020\u0007H\u0080\b\u001a\f\u0010\f\u001a\u00020\r*\u00020\u0007H\u0002\u001a\r\u0010\u000e\u001a\u00020\u000f*\u00020\u0007H\u0080\b\u001a\r\u0010\u0010\u001a\u00020\u000f*\u00020\u0007H\u0080\b\u001a\u0014\u0010\u0011\u001a\u0004\u0018\u00010\u0012*\u00020\u0007H\u0080\b¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0017\u001a\u00020\u0001*\u00020\u0007H\u0080\b\u001a\r\u0010\u0018\u001a\u00020\n*\u00020\u0007H\u0080\b\u001a\u000f\u0010\u0019\u001a\u0004\u0018\u00010\u0007*\u00020\u0007H\u0080\b\u001a\f\u0010\u001a\u001a\u00020\u000f*\u00020\u0007H\u0002\u001a\r\u0010\u001b\u001a\u00020\u000f*\u00020\u0007H\u0080\b\u001a\u001d\u0010\u001c\u001a\u00020\u0007*\u00020\u00072\u0006\u0010\u001d\u001a\u00020\n2\u0006\u0010\u001e\u001a\u00020\u000fH\u0080\b\u001a\u001d\u0010\u001c\u001a\u00020\u0007*\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u000fH\u0080\b\u001a\u001d\u0010\u001c\u001a\u00020\u0007*\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u001f2\u0006\u0010\u001e\u001a\u00020\u000fH\u0080\b\u001a\u001c\u0010\u001c\u001a\u00020\u0007*\u00020\u00072\u0006\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u000fH\u0000\u001a\u0015\u0010 \u001a\u00020\u0007*\u00020\u00072\u0006\u0010!\u001a\u00020\u0007H\u0080\b\u001a\r\u0010\"\u001a\u00020\u0007*\u00020\u0007H\u0080\b\u001a\u0015\u0010&\u001a\u00020\r*\u00020\u00072\u0006\u0010!\u001a\u00020\u0007H\u0080\b\u001a\u0017\u0010'\u001a\u00020\u000f*\u00020\u00072\b\u0010!\u001a\u0004\u0018\u00010(H\u0080\b\u001a\r\u0010)\u001a\u00020\r*\u00020\u0007H\u0080\b\u001a\r\u0010*\u001a\u00020\n*\u00020\u0007H\u0080\b\u001a\u0014\u0010+\u001a\u00020\u0007*\u00020\n2\u0006\u0010\u001e\u001a\u00020\u000fH\u0000\u001a\u0014\u0010,\u001a\u00020\u0007*\u00020\u001f2\u0006\u0010\u001e\u001a\u00020\u000fH\u0000\u001a\f\u0010-\u001a\u00020\u0001*\u00020\nH\u0002\u001a\f\u0010-\u001a\u00020\u0001*\u00020.H\u0002\u001a\u0014\u0010/\u001a\u00020\u000f*\u00020\u001f2\u0006\u0010#\u001a\u00020\u0001H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0018\u0010\u0014\u001a\u00020\r*\u00020\u00078BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016\"\u001a\u0010#\u001a\u0004\u0018\u00010\u0001*\u00020\u00078BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b$\u0010%¨\u00060"},
   d2 = {"SLASH", "Lokio/ByteString;", "BACKSLASH", "ANY_SLASH", "DOT", "DOT_DOT", "commonRoot", "Lokio/Path;", "commonSegments", "", "", "commonSegmentsBytes", "rootLength", "", "commonIsAbsolute", "", "commonIsRelative", "commonVolumeLetter", "", "(Lokio/Path;)Ljava/lang/Character;", "indexOfLastSlash", "getIndexOfLastSlash", "(Lokio/Path;)I", "commonNameBytes", "commonName", "commonParent", "lastSegmentIsDotDot", "commonIsRoot", "commonResolve", "child", "normalize", "Lokio/Buffer;", "commonRelativeTo", "other", "commonNormalized", "slash", "getSlash", "(Lokio/Path;)Lokio/ByteString;", "commonCompareTo", "commonEquals", "", "commonHashCode", "commonToString", "commonToPath", "toPath", "toSlash", "", "startsWithVolumeLetterAndColon", "okio"}
)
@JvmName(
   name = "-Path"
)
@SourceDebugExtension({"SMAP\nPath.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Path.kt\nokio/internal/-Path\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,405:1\n53#1,22:406\n203#1:432\n203#1:433\n1563#2:428\n1634#2,3:429\n*S KotlinDebug\n*F\n+ 1 Path.kt\nokio/internal/-Path\n*L\n47#1:406,22\n193#1:432\n198#1:433\n47#1:428\n47#1:429,3\n*E\n"})
public final class -Path {
   @NotNull
   private static final ByteString SLASH;
   @NotNull
   private static final ByteString BACKSLASH;
   @NotNull
   private static final ByteString ANY_SLASH;
   @NotNull
   private static final ByteString DOT;
   @NotNull
   private static final ByteString DOT_DOT;

   @Nullable
   public static final Path commonRoot(@NotNull Path $this$commonRoot) {
      Intrinsics.checkNotNullParameter($this$commonRoot, "<this>");
      int $i$f$commonRoot = 0;
      int rootLength = access$rootLength($this$commonRoot);
      return rootLength == -1 ? null : new Path($this$commonRoot.getBytes$okio().substring(0, rootLength));
   }

   @NotNull
   public static final List<String> commonSegments(@NotNull Path $this$commonSegments) {
      Intrinsics.checkNotNullParameter($this$commonSegments, "<this>");
      int $i$f$commonSegments = 0;
      Path $this$commonSegmentsBytes$iv = $this$commonSegments;
      int $i$f$commonSegmentsBytes = 0;
      List result$iv = (List)(new ArrayList());
      int segmentStart$iv = access$rootLength($this$commonSegments);
      if (segmentStart$iv == -1) {
         segmentStart$iv = 0;
      } else if (segmentStart$iv < $this$commonSegments.getBytes$okio().size() && $this$commonSegments.getBytes$okio().getByte(segmentStart$iv) == 92) {
         ++segmentStart$iv;
      }

      int i$iv = segmentStart$iv;

      for(int var7 = $this$commonSegments.getBytes$okio().size(); i$iv < var7; ++i$iv) {
         if ($this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) == 47 || $this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) == 92) {
            ((Collection)result$iv).add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, i$iv));
            segmentStart$iv = i$iv + 1;
         }
      }

      if (segmentStart$iv < $this$commonSegmentsBytes$iv.getBytes$okio().size()) {
         ((Collection)result$iv).add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, $this$commonSegmentsBytes$iv.getBytes$okio().size()));
      }

      Iterable $this$map$iv = (Iterable)result$iv;
      $i$f$commonSegmentsBytes = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      i$iv = 0;

      for(Object item$iv$iv : $this$map$iv) {
         ByteString it = (ByteString)item$iv$iv;
         int var10 = 0;
         destination$iv$iv.add(it.utf8());
      }

      return (List)destination$iv$iv;
   }

   @NotNull
   public static final List<ByteString> commonSegmentsBytes(@NotNull Path $this$commonSegmentsBytes) {
      Intrinsics.checkNotNullParameter($this$commonSegmentsBytes, "<this>");
      int $i$f$commonSegmentsBytes = 0;
      List result = (List)(new ArrayList());
      int segmentStart = access$rootLength($this$commonSegmentsBytes);
      if (segmentStart == -1) {
         segmentStart = 0;
      } else if (segmentStart < $this$commonSegmentsBytes.getBytes$okio().size() && $this$commonSegmentsBytes.getBytes$okio().getByte(segmentStart) == 92) {
         ++segmentStart;
      }

      int i = segmentStart;

      for(int var5 = $this$commonSegmentsBytes.getBytes$okio().size(); i < var5; ++i) {
         if ($this$commonSegmentsBytes.getBytes$okio().getByte(i) == 47 || $this$commonSegmentsBytes.getBytes$okio().getByte(i) == 92) {
            ((Collection)result).add($this$commonSegmentsBytes.getBytes$okio().substring(segmentStart, i));
            segmentStart = i + 1;
         }
      }

      if (segmentStart < $this$commonSegmentsBytes.getBytes$okio().size()) {
         ((Collection)result).add($this$commonSegmentsBytes.getBytes$okio().substring(segmentStart, $this$commonSegmentsBytes.getBytes$okio().size()));
      }

      return result;
   }

   private static final int rootLength(Path $this$rootLength) {
      if ($this$rootLength.getBytes$okio().size() == 0) {
         return -1;
      } else if ($this$rootLength.getBytes$okio().getByte(0) == 47) {
         return 1;
      } else if ($this$rootLength.getBytes$okio().getByte(0) == 92) {
         if ($this$rootLength.getBytes$okio().size() > 2 && $this$rootLength.getBytes$okio().getByte(1) == 92) {
            int uncRootEnd = $this$rootLength.getBytes$okio().indexOf((ByteString)BACKSLASH, 2);
            if (uncRootEnd == -1) {
               uncRootEnd = $this$rootLength.getBytes$okio().size();
            }

            return uncRootEnd;
         } else {
            return 1;
         }
      } else if ($this$rootLength.getBytes$okio().size() > 2 && $this$rootLength.getBytes$okio().getByte(1) == 58 && $this$rootLength.getBytes$okio().getByte(2) == 92) {
         char c = (char)$this$rootLength.getBytes$okio().getByte(0);
         return !('a' <= c ? c < '{' : false) && !('A' <= c ? c < '[' : false) ? -1 : 3;
      } else {
         return -1;
      }
   }

   public static final boolean commonIsAbsolute(@NotNull Path $this$commonIsAbsolute) {
      Intrinsics.checkNotNullParameter($this$commonIsAbsolute, "<this>");
      int $i$f$commonIsAbsolute = 0;
      return access$rootLength($this$commonIsAbsolute) != -1;
   }

   public static final boolean commonIsRelative(@NotNull Path $this$commonIsRelative) {
      Intrinsics.checkNotNullParameter($this$commonIsRelative, "<this>");
      int $i$f$commonIsRelative = 0;
      return access$rootLength($this$commonIsRelative) == -1;
   }

   @Nullable
   public static final Character commonVolumeLetter(@NotNull Path $this$commonVolumeLetter) {
      Intrinsics.checkNotNullParameter($this$commonVolumeLetter, "<this>");
      int $i$f$commonVolumeLetter = 0;
      if (ByteString.indexOf$default($this$commonVolumeLetter.getBytes$okio(), (ByteString)access$getSLASH$p(), 0, 2, (Object)null) != -1) {
         return null;
      } else if ($this$commonVolumeLetter.getBytes$okio().size() < 2) {
         return null;
      } else if ($this$commonVolumeLetter.getBytes$okio().getByte(1) != 58) {
         return null;
      } else {
         char c = (char)$this$commonVolumeLetter.getBytes$okio().getByte(0);
         return !('a' <= c ? c < '{' : false) && !('A' <= c ? c < '[' : false) ? null : c;
      }
   }

   private static final int getIndexOfLastSlash(Path $this$indexOfLastSlash) {
      int lastSlash = ByteString.lastIndexOf$default($this$indexOfLastSlash.getBytes$okio(), (ByteString)SLASH, 0, 2, (Object)null);
      return lastSlash != -1 ? lastSlash : ByteString.lastIndexOf$default($this$indexOfLastSlash.getBytes$okio(), (ByteString)BACKSLASH, 0, 2, (Object)null);
   }

   @NotNull
   public static final ByteString commonNameBytes(@NotNull Path $this$commonNameBytes) {
      Intrinsics.checkNotNullParameter($this$commonNameBytes, "<this>");
      int $i$f$commonNameBytes = 0;
      int lastSlash = access$getIndexOfLastSlash($this$commonNameBytes);
      return lastSlash != -1 ? ByteString.substring$default($this$commonNameBytes.getBytes$okio(), lastSlash + 1, 0, 2, (Object)null) : ($this$commonNameBytes.volumeLetter() != null && $this$commonNameBytes.getBytes$okio().size() == 2 ? ByteString.EMPTY : $this$commonNameBytes.getBytes$okio());
   }

   @NotNull
   public static final String commonName(@NotNull Path $this$commonName) {
      Intrinsics.checkNotNullParameter($this$commonName, "<this>");
      int $i$f$commonName = 0;
      return $this$commonName.nameBytes().utf8();
   }

   @Nullable
   public static final Path commonParent(@NotNull Path $this$commonParent) {
      Intrinsics.checkNotNullParameter($this$commonParent, "<this>");
      int $i$f$commonParent = 0;
      if (!Intrinsics.areEqual((Object)$this$commonParent.getBytes$okio(), (Object)access$getDOT$p()) && !Intrinsics.areEqual((Object)$this$commonParent.getBytes$okio(), (Object)access$getSLASH$p()) && !Intrinsics.areEqual((Object)$this$commonParent.getBytes$okio(), (Object)access$getBACKSLASH$p()) && !access$lastSegmentIsDotDot($this$commonParent)) {
         int lastSlash = access$getIndexOfLastSlash($this$commonParent);
         if (lastSlash == 2 && $this$commonParent.volumeLetter() != null) {
            return $this$commonParent.getBytes$okio().size() == 3 ? null : new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 3, 1, (Object)null));
         } else if (lastSlash == 1 && $this$commonParent.getBytes$okio().startsWith(access$getBACKSLASH$p())) {
            return null;
         } else if (lastSlash == -1 && $this$commonParent.volumeLetter() != null) {
            return $this$commonParent.getBytes$okio().size() == 2 ? null : new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 2, 1, (Object)null));
         } else if (lastSlash == -1) {
            return new Path(access$getDOT$p());
         } else {
            return lastSlash == 0 ? new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, 1, 1, (Object)null)) : new Path(ByteString.substring$default($this$commonParent.getBytes$okio(), 0, lastSlash, 1, (Object)null));
         }
      } else {
         return null;
      }
   }

   private static final boolean lastSegmentIsDotDot(Path $this$lastSegmentIsDotDot) {
      if ($this$lastSegmentIsDotDot.getBytes$okio().endsWith(DOT_DOT)) {
         if ($this$lastSegmentIsDotDot.getBytes$okio().size() == 2) {
            return true;
         }

         if ($this$lastSegmentIsDotDot.getBytes$okio().rangeEquals($this$lastSegmentIsDotDot.getBytes$okio().size() - 3, (ByteString)SLASH, 0, 1)) {
            return true;
         }

         if ($this$lastSegmentIsDotDot.getBytes$okio().rangeEquals($this$lastSegmentIsDotDot.getBytes$okio().size() - 3, (ByteString)BACKSLASH, 0, 1)) {
            return true;
         }
      }

      return false;
   }

   public static final boolean commonIsRoot(@NotNull Path $this$commonIsRoot) {
      Intrinsics.checkNotNullParameter($this$commonIsRoot, "<this>");
      int $i$f$commonIsRoot = 0;
      return access$rootLength($this$commonIsRoot) == $this$commonIsRoot.getBytes$okio().size();
   }

   @NotNull
   public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull String child, boolean normalize) {
      Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
      Intrinsics.checkNotNullParameter(child, "child");
      int $i$f$commonResolve = 0;
      Buffer child$iv = (new Buffer()).writeUtf8(child);
      int $i$f$commonResolve = 0;
      return commonResolve($this$commonResolve, toPath(child$iv, false), normalize);
   }

   @NotNull
   public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull ByteString child, boolean normalize) {
      Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
      Intrinsics.checkNotNullParameter(child, "child");
      int $i$f$commonResolve = 0;
      Buffer child$iv = (new Buffer()).write(child);
      int $i$f$commonResolve = 0;
      return commonResolve($this$commonResolve, toPath(child$iv, false), normalize);
   }

   @NotNull
   public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull Buffer child, boolean normalize) {
      Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
      Intrinsics.checkNotNullParameter(child, "child");
      int $i$f$commonResolve = 0;
      return commonResolve($this$commonResolve, toPath(child, false), normalize);
   }

   @NotNull
   public static final Path commonResolve(@NotNull Path $this$commonResolve, @NotNull Path child, boolean normalize) {
      Intrinsics.checkNotNullParameter($this$commonResolve, "<this>");
      Intrinsics.checkNotNullParameter(child, "child");
      if (!child.isAbsolute() && child.volumeLetter() == null) {
         ByteString var10000 = getSlash($this$commonResolve);
         if (var10000 == null) {
            var10000 = getSlash(child);
            if (var10000 == null) {
               var10000 = toSlash(Path.DIRECTORY_SEPARATOR);
            }
         }

         ByteString slash = var10000;
         Buffer buffer = new Buffer();
         buffer.write($this$commonResolve.getBytes$okio());
         if (buffer.size() > 0L) {
            buffer.write(slash);
         }

         buffer.write(child.getBytes$okio());
         return toPath(buffer, normalize);
      } else {
         return child;
      }
   }

   @NotNull
   public static final Path commonRelativeTo(@NotNull Path $this$commonRelativeTo, @NotNull Path other) {
      Intrinsics.checkNotNullParameter($this$commonRelativeTo, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRelativeTo = 0;
      if (!Intrinsics.areEqual((Object)$this$commonRelativeTo.getRoot(), (Object)other.getRoot())) {
         int otherSegments = 0;
         String var12 = "Paths of different roots cannot be relative to each other: " + $this$commonRelativeTo + " and " + other;
         throw new IllegalArgumentException(var12.toString());
      } else {
         List thisSegments = $this$commonRelativeTo.getSegmentsBytes();
         List otherSegments = other.getSegmentsBytes();
         int firstNewSegmentIndex = 0;

         int minSegmentsSize;
         for(minSegmentsSize = Math.min(thisSegments.size(), otherSegments.size()); firstNewSegmentIndex < minSegmentsSize && Intrinsics.areEqual(thisSegments.get(firstNewSegmentIndex), otherSegments.get(firstNewSegmentIndex)); ++firstNewSegmentIndex) {
         }

         if (firstNewSegmentIndex == minSegmentsSize && $this$commonRelativeTo.getBytes$okio().size() == other.getBytes$okio().size()) {
            return Path.Companion.get$default(Path.Companion, (String)".", false, 1, (Object)null);
         } else if (otherSegments.subList(firstNewSegmentIndex, otherSegments.size()).indexOf(access$getDOT_DOT$p()) != -1) {
            int slash = 0;
            String var14 = "Impossible relative path to resolve: " + $this$commonRelativeTo + " and " + other;
            throw new IllegalArgumentException(var14.toString());
         } else if (Intrinsics.areEqual((Object)other.getBytes$okio(), (Object)access$getDOT$p())) {
            return $this$commonRelativeTo;
         } else {
            Buffer buffer = new Buffer();
            ByteString var10000 = access$getSlash(other);
            if (var10000 == null) {
               var10000 = access$getSlash($this$commonRelativeTo);
               if (var10000 == null) {
                  var10000 = access$toSlash(Path.DIRECTORY_SEPARATOR);
               }
            }

            ByteString slash = var10000;
            int i = firstNewSegmentIndex;

            for(int var10 = otherSegments.size(); i < var10; ++i) {
               buffer.write(access$getDOT_DOT$p());
               buffer.write(slash);
            }

            i = firstNewSegmentIndex;

            for(int var16 = thisSegments.size(); i < var16; ++i) {
               buffer.write((ByteString)thisSegments.get(i));
               buffer.write(slash);
            }

            return toPath(buffer, false);
         }
      }
   }

   @NotNull
   public static final Path commonNormalized(@NotNull Path $this$commonNormalized) {
      Intrinsics.checkNotNullParameter($this$commonNormalized, "<this>");
      int $i$f$commonNormalized = 0;
      return Path.Companion.get($this$commonNormalized.toString(), true);
   }

   private static final ByteString getSlash(Path $this$slash) {
      return ByteString.indexOf$default($this$slash.getBytes$okio(), (ByteString)SLASH, 0, 2, (Object)null) != -1 ? SLASH : (ByteString.indexOf$default($this$slash.getBytes$okio(), (ByteString)BACKSLASH, 0, 2, (Object)null) != -1 ? BACKSLASH : null);
   }

   public static final int commonCompareTo(@NotNull Path $this$commonCompareTo, @NotNull Path other) {
      Intrinsics.checkNotNullParameter($this$commonCompareTo, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonCompareTo = 0;
      return $this$commonCompareTo.getBytes$okio().compareTo(other.getBytes$okio());
   }

   public static final boolean commonEquals(@NotNull Path $this$commonEquals, @Nullable Object other) {
      Intrinsics.checkNotNullParameter($this$commonEquals, "<this>");
      int $i$f$commonEquals = 0;
      return other instanceof Path && Intrinsics.areEqual((Object)((Path)other).getBytes$okio(), (Object)$this$commonEquals.getBytes$okio());
   }

   public static final int commonHashCode(@NotNull Path $this$commonHashCode) {
      Intrinsics.checkNotNullParameter($this$commonHashCode, "<this>");
      int $i$f$commonHashCode = 0;
      return $this$commonHashCode.getBytes$okio().hashCode();
   }

   @NotNull
   public static final String commonToString(@NotNull Path $this$commonToString) {
      Intrinsics.checkNotNullParameter($this$commonToString, "<this>");
      int $i$f$commonToString = 0;
      return $this$commonToString.getBytes$okio().utf8();
   }

   @NotNull
   public static final Path commonToPath(@NotNull String $this$commonToPath, boolean normalize) {
      Intrinsics.checkNotNullParameter($this$commonToPath, "<this>");
      return toPath((new Buffer()).writeUtf8($this$commonToPath), normalize);
   }

   @NotNull
   public static final Path toPath(@NotNull Buffer $this$toPath, boolean normalize) {
      Intrinsics.checkNotNullParameter($this$toPath, "<this>");
      ByteString slash = null;
      Buffer result = new Buffer();

      int leadingSlashCount;
      for(leadingSlashCount = 0; $this$toPath.rangeEquals(0L, SLASH) || $this$toPath.rangeEquals(0L, BACKSLASH); ++leadingSlashCount) {
         byte var5 = $this$toPath.readByte();
         ByteString var10000 = slash;
         if (slash == null) {
            var10000 = toSlash(var5);
         }

         slash = var10000;
      }

      boolean windowsUncPath = leadingSlashCount >= 2 && Intrinsics.areEqual((Object)slash, (Object)BACKSLASH);
      if (windowsUncPath) {
         Intrinsics.checkNotNull(slash);
         result.write(slash);
         result.write(slash);
      } else if (leadingSlashCount > 0) {
         Intrinsics.checkNotNull(slash);
         result.write(slash);
      } else {
         long limit = $this$toPath.indexOfElement(ANY_SLASH);
         ByteString var15 = slash;
         if (slash == null) {
            var15 = limit == -1L ? toSlash(Path.DIRECTORY_SEPARATOR) : toSlash($this$toPath.getByte(limit));
         }

         slash = var15;
         if (startsWithVolumeLetterAndColon($this$toPath, slash)) {
            if (limit == 2L) {
               result.write($this$toPath, 3L);
            } else {
               result.write($this$toPath, 2L);
            }
         }

         Unit var16 = Unit.INSTANCE;
      }

      boolean absolute = result.size() > 0L;
      List canonicalParts = (List)(new ArrayList());

      while(!$this$toPath.exhausted()) {
         long limit = $this$toPath.indexOfElement(ANY_SLASH);
         ByteString part = null;
         if (limit == -1L) {
            part = $this$toPath.readByteString();
         } else {
            part = $this$toPath.readByteString(limit);
            $this$toPath.readByte();
         }

         if (Intrinsics.areEqual((Object)part, (Object)DOT_DOT)) {
            if (!absolute || !canonicalParts.isEmpty()) {
               if (normalize && (absolute || !canonicalParts.isEmpty() && !Intrinsics.areEqual((Object)CollectionsKt.last(canonicalParts), (Object)DOT_DOT))) {
                  if (!windowsUncPath || canonicalParts.size() != 1) {
                     CollectionsKt.removeLastOrNull(canonicalParts);
                  }
               } else {
                  canonicalParts.add(part);
               }
            }
         } else if (!Intrinsics.areEqual((Object)part, (Object)DOT) && !Intrinsics.areEqual((Object)part, (Object)ByteString.EMPTY)) {
            canonicalParts.add(part);
         }
      }

      int i = 0;

      for(int var9 = canonicalParts.size(); i < var9; ++i) {
         if (i > 0) {
            result.write(slash);
         }

         result.write((ByteString)canonicalParts.get(i));
      }

      if (result.size() == 0L) {
         result.write(DOT);
      }

      return new Path(result.readByteString());
   }

   private static final ByteString toSlash(String $this$toSlash) {
      ByteString var10000;
      if (Intrinsics.areEqual((Object)$this$toSlash, (Object)"/")) {
         var10000 = SLASH;
      } else {
         if (!Intrinsics.areEqual((Object)$this$toSlash, (Object)"\\")) {
            throw new IllegalArgumentException("not a directory separator: " + $this$toSlash);
         }

         var10000 = BACKSLASH;
      }

      return var10000;
   }

   private static final ByteString toSlash(byte $this$toSlash) {
      ByteString var10000;
      switch ($this$toSlash) {
         case 47:
            var10000 = SLASH;
            break;
         case 92:
            var10000 = BACKSLASH;
            break;
         default:
            throw new IllegalArgumentException("not a directory separator: " + $this$toSlash);
      }

      return var10000;
   }

   private static final boolean startsWithVolumeLetterAndColon(Buffer $this$startsWithVolumeLetterAndColon, ByteString slash) {
      if (!Intrinsics.areEqual((Object)slash, (Object)BACKSLASH)) {
         return false;
      } else if ($this$startsWithVolumeLetterAndColon.size() < 2L) {
         return false;
      } else if ($this$startsWithVolumeLetterAndColon.getByte(1L) != 58) {
         return false;
      } else {
         char b = (char)$this$startsWithVolumeLetterAndColon.getByte(0L);
         return ('a' <= b ? b < '{' : false) || ('A' <= b ? b < '[' : false);
      }
   }

   // $FF: synthetic method
   public static final int access$rootLength(Path $receiver) {
      return rootLength($receiver);
   }

   // $FF: synthetic method
   public static final ByteString access$getSLASH$p() {
      return SLASH;
   }

   // $FF: synthetic method
   public static final int access$getIndexOfLastSlash(Path $receiver) {
      return getIndexOfLastSlash($receiver);
   }

   // $FF: synthetic method
   public static final ByteString access$getDOT$p() {
      return DOT;
   }

   // $FF: synthetic method
   public static final ByteString access$getBACKSLASH$p() {
      return BACKSLASH;
   }

   // $FF: synthetic method
   public static final boolean access$lastSegmentIsDotDot(Path $receiver) {
      return lastSegmentIsDotDot($receiver);
   }

   // $FF: synthetic method
   public static final ByteString access$getDOT_DOT$p() {
      return DOT_DOT;
   }

   // $FF: synthetic method
   public static final ByteString access$getSlash(Path $receiver) {
      return getSlash($receiver);
   }

   // $FF: synthetic method
   public static final ByteString access$toSlash(String $receiver) {
      return toSlash($receiver);
   }

   static {
      SLASH = ByteString.Companion.encodeUtf8("/");
      BACKSLASH = ByteString.Companion.encodeUtf8("\\");
      ANY_SLASH = ByteString.Companion.encodeUtf8("/\\");
      DOT = ByteString.Companion.encodeUtf8(".");
      DOT_DOT = ByteString.Companion.encodeUtf8("..");
   }
}
