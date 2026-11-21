package okio;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.internal.-Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\f\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u0000 /2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001/B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u001e\u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\rH\u0087\u0002¢\u0006\u0002\b J\u0016\u0010\u001e\u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\u0003H\u0087\u0002¢\u0006\u0002\b J\u0016\u0010\u001e\u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\u0000H\u0087\u0002¢\u0006\u0002\b J\u0018\u0010 \u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\r2\b\b\u0002\u0010!\u001a\u00020\u0013J\u0018\u0010 \u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\u00032\b\b\u0002\u0010!\u001a\u00020\u0013J\u0018\u0010 \u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010!\u001a\u00020\u0013J\u000e\u0010\"\u001a\u00020\u00002\u0006\u0010#\u001a\u00020\u0000J\u0006\u0010$\u001a\u00020\u0000J\u0006\u0010%\u001a\u00020&J\u0006\u0010'\u001a\u00020(J\u0011\u0010)\u001a\u00020*2\u0006\u0010#\u001a\u00020\u0000H\u0096\u0002J\u0013\u0010+\u001a\u00020\u00132\b\u0010#\u001a\u0004\u0018\u00010,H\u0096\u0002J\b\u0010-\u001a\u00020*H\u0016J\b\u0010.\u001a\u00020\rH\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\b\u001a\u0004\u0018\u00010\u00008F¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f8F¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\f8F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0012\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0014R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u00178G¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0018R\u0011\u0010\u0019\u001a\u00020\u00038G¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u0007R\u0011\u0010\u001a\u001a\u00020\r8G¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u00008G¢\u0006\u0006\u001a\u0004\b\u001c\u0010\nR\u0011\u0010\u001d\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u0014¨\u00060"},
   d2 = {"Lokio/Path;", "", "bytes", "Lokio/ByteString;", "<init>", "(Lokio/ByteString;)V", "getBytes$okio", "()Lokio/ByteString;", "root", "getRoot", "()Lokio/Path;", "segments", "", "", "getSegments", "()Ljava/util/List;", "segmentsBytes", "getSegmentsBytes", "isAbsolute", "", "()Z", "isRelative", "volumeLetter", "", "()Ljava/lang/Character;", "nameBytes", "name", "()Ljava/lang/String;", "parent", "isRoot", "div", "child", "resolve", "normalize", "relativeTo", "other", "normalized", "toFile", "Ljava/io/File;", "toNioPath", "Ljava/nio/file/Path;", "compareTo", "", "equals", "", "hashCode", "toString", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nPath.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Path.kt\nokio/Path\n+ 2 Path.kt\nokio/internal/-Path\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,132:1\n39#2,3:133\n47#2,28:136\n53#2,22:168\n106#2:190\n111#2:191\n116#2,6:192\n133#2,5:198\n143#2:203\n148#2,25:204\n188#2:229\n193#2,11:230\n198#2,6:241\n193#2,11:247\n198#2,6:258\n222#2,41:264\n267#2:305\n281#2:306\n286#2:307\n291#2:308\n296#2:309\n1563#3:164\n1634#3,3:165\n*S KotlinDebug\n*F\n+ 1 Path.kt\nokio/Path\n*L\n44#1:133,3\n47#1:136,28\n50#1:168,22\n53#1:190\n56#1:191\n60#1:192,6\n64#1:198,5\n68#1:203\n72#1:204,25\n75#1:229\n78#1:230,11\n81#1:241,6\n87#1:247,11\n90#1:258,6\n95#1:264,41\n97#1:305\n104#1:306\n106#1:307\n108#1:308\n110#1:309\n47#1:164\n47#1:165,3\n*E\n"})
public final class Path implements Comparable<Path> {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final ByteString bytes;
   @JvmField
   @NotNull
   public static final String DIRECTORY_SEPARATOR;

   public Path(@NotNull ByteString bytes) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      super();
      this.bytes = bytes;
   }

   @NotNull
   public final ByteString getBytes$okio() {
      return this.bytes;
   }

   @Nullable
   public final Path getRoot() {
      int $i$f$commonRoot = 0;
      int rootLength$iv = -Path.access$rootLength(this);
      return rootLength$iv == -1 ? null : new Path(this.getBytes$okio().substring(0, rootLength$iv));
   }

   @NotNull
   public final List<String> getSegments() {
      int $i$f$commonSegments = 0;
      Path $this$commonSegmentsBytes$iv$iv = this;
      int $i$f$commonSegmentsBytes = 0;
      List result$iv$iv = (List)(new ArrayList());
      int segmentStart$iv$iv = -Path.access$rootLength(this);
      if (segmentStart$iv$iv == -1) {
         segmentStart$iv$iv = 0;
      } else if (segmentStart$iv$iv < this.getBytes$okio().size() && this.getBytes$okio().getByte(segmentStart$iv$iv) == 92) {
         ++segmentStart$iv$iv;
      }

      int i$iv$iv = segmentStart$iv$iv;

      for(int var8 = this.getBytes$okio().size(); i$iv$iv < var8; ++i$iv$iv) {
         if ($this$commonSegmentsBytes$iv$iv.getBytes$okio().getByte(i$iv$iv) == 47 || $this$commonSegmentsBytes$iv$iv.getBytes$okio().getByte(i$iv$iv) == 92) {
            ((Collection)result$iv$iv).add($this$commonSegmentsBytes$iv$iv.getBytes$okio().substring(segmentStart$iv$iv, i$iv$iv));
            segmentStart$iv$iv = i$iv$iv + 1;
         }
      }

      if (segmentStart$iv$iv < $this$commonSegmentsBytes$iv$iv.getBytes$okio().size()) {
         ((Collection)result$iv$iv).add($this$commonSegmentsBytes$iv$iv.getBytes$okio().substring(segmentStart$iv$iv, $this$commonSegmentsBytes$iv$iv.getBytes$okio().size()));
      }

      Iterable $this$map$iv$iv = (Iterable)result$iv$iv;
      $i$f$commonSegmentsBytes = 0;
      Collection destination$iv$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv$iv, 10)));
      i$iv$iv = 0;

      for(Object item$iv$iv$iv : $this$map$iv$iv) {
         ByteString it$iv = (ByteString)item$iv$iv$iv;
         int var12 = 0;
         destination$iv$iv$iv.add(it$iv.utf8());
      }

      return (List)destination$iv$iv$iv;
   }

   @NotNull
   public final List<ByteString> getSegmentsBytes() {
      Path $this$commonSegmentsBytes$iv = this;
      int $i$f$commonSegmentsBytes = 0;
      List result$iv = (List)(new ArrayList());
      int segmentStart$iv = -Path.access$rootLength(this);
      if (segmentStart$iv == -1) {
         segmentStart$iv = 0;
      } else if (segmentStart$iv < this.getBytes$okio().size() && this.getBytes$okio().getByte(segmentStart$iv) == 92) {
         ++segmentStart$iv;
      }

      int i$iv = segmentStart$iv;

      for(int var6 = this.getBytes$okio().size(); i$iv < var6; ++i$iv) {
         if ($this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) == 47 || $this$commonSegmentsBytes$iv.getBytes$okio().getByte(i$iv) == 92) {
            ((Collection)result$iv).add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, i$iv));
            segmentStart$iv = i$iv + 1;
         }
      }

      if (segmentStart$iv < $this$commonSegmentsBytes$iv.getBytes$okio().size()) {
         ((Collection)result$iv).add($this$commonSegmentsBytes$iv.getBytes$okio().substring(segmentStart$iv, $this$commonSegmentsBytes$iv.getBytes$okio().size()));
      }

      return result$iv;
   }

   public final boolean isAbsolute() {
      int $i$f$commonIsAbsolute = 0;
      return -Path.access$rootLength(this) != -1;
   }

   public final boolean isRelative() {
      int $i$f$commonIsRelative = 0;
      return -Path.access$rootLength(this) == -1;
   }

   @JvmName(
      name = "volumeLetter"
   )
   @Nullable
   public final Character volumeLetter() {
      int $i$f$commonVolumeLetter = 0;
      Character var10000;
      if (ByteString.indexOf$default(this.getBytes$okio(), (ByteString)-Path.access$getSLASH$p(), 0, 2, (Object)null) != -1) {
         var10000 = null;
      } else if (this.getBytes$okio().size() < 2) {
         var10000 = null;
      } else if (this.getBytes$okio().getByte(1) != 58) {
         var10000 = null;
      } else {
         char c$iv = (char)this.getBytes$okio().getByte(0);
         var10000 = !('a' <= c$iv ? c$iv < '{' : false) && !('A' <= c$iv ? c$iv < '[' : false) ? null : c$iv;
      }

      return var10000;
   }

   @JvmName(
      name = "nameBytes"
   )
   @NotNull
   public final ByteString nameBytes() {
      int $i$f$commonNameBytes = 0;
      int lastSlash$iv = -Path.access$getIndexOfLastSlash(this);
      return lastSlash$iv != -1 ? ByteString.substring$default(this.getBytes$okio(), lastSlash$iv + 1, 0, 2, (Object)null) : (this.volumeLetter() != null && this.getBytes$okio().size() == 2 ? ByteString.EMPTY : this.getBytes$okio());
   }

   @JvmName(
      name = "name"
   )
   @NotNull
   public final String name() {
      int $i$f$commonName = 0;
      return this.nameBytes().utf8();
   }

   @JvmName(
      name = "parent"
   )
   @Nullable
   public final Path parent() {
      int $i$f$commonParent = 0;
      Path var10000;
      if (!Intrinsics.areEqual((Object)this.getBytes$okio(), (Object)-Path.access$getDOT$p()) && !Intrinsics.areEqual((Object)this.getBytes$okio(), (Object)-Path.access$getSLASH$p()) && !Intrinsics.areEqual((Object)this.getBytes$okio(), (Object)-Path.access$getBACKSLASH$p()) && !-Path.access$lastSegmentIsDotDot(this)) {
         int lastSlash$iv = -Path.access$getIndexOfLastSlash(this);
         var10000 = lastSlash$iv == 2 && this.volumeLetter() != null ? (this.getBytes$okio().size() == 3 ? null : new Path(ByteString.substring$default(this.getBytes$okio(), 0, 3, 1, (Object)null))) : (lastSlash$iv == 1 && this.getBytes$okio().startsWith(-Path.access$getBACKSLASH$p()) ? null : (lastSlash$iv == -1 && this.volumeLetter() != null ? (this.getBytes$okio().size() == 2 ? null : new Path(ByteString.substring$default(this.getBytes$okio(), 0, 2, 1, (Object)null))) : (lastSlash$iv == -1 ? new Path(-Path.access$getDOT$p()) : (lastSlash$iv == 0 ? new Path(ByteString.substring$default(this.getBytes$okio(), 0, 1, 1, (Object)null)) : new Path(ByteString.substring$default(this.getBytes$okio(), 0, lastSlash$iv, 1, (Object)null))))));
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public final boolean isRoot() {
      int $i$f$commonIsRoot = 0;
      return -Path.access$rootLength(this) == this.getBytes$okio().size();
   }

   @JvmName(
      name = "resolve"
   )
   @NotNull
   public final Path resolve(@NotNull String child) {
      Intrinsics.checkNotNullParameter(child, "child");
      boolean normalize$iv = false;
      int $i$f$commonResolve = 0;
      Buffer child$iv$iv = (new Buffer()).writeUtf8(child);
      int $i$f$commonResolve = 0;
      return -Path.commonResolve(this, -Path.toPath(child$iv$iv, false), normalize$iv);
   }

   @JvmName(
      name = "resolve"
   )
   @NotNull
   public final Path resolve(@NotNull ByteString child) {
      Intrinsics.checkNotNullParameter(child, "child");
      boolean normalize$iv = false;
      int $i$f$commonResolve = 0;
      Buffer child$iv$iv = (new Buffer()).write(child);
      int $i$f$commonResolve = 0;
      return -Path.commonResolve(this, -Path.toPath(child$iv$iv, false), normalize$iv);
   }

   @JvmName(
      name = "resolve"
   )
   @NotNull
   public final Path resolve(@NotNull Path child) {
      Intrinsics.checkNotNullParameter(child, "child");
      return -Path.commonResolve(this, child, false);
   }

   @NotNull
   public final Path resolve(@NotNull String child, boolean normalize) {
      Intrinsics.checkNotNullParameter(child, "child");
      int $i$f$commonResolve = 0;
      Buffer child$iv$iv = (new Buffer()).writeUtf8(child);
      int $i$f$commonResolve = 0;
      return -Path.commonResolve(this, -Path.toPath(child$iv$iv, false), normalize);
   }

   // $FF: synthetic method
   public static Path resolve$default(Path var0, String var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = false;
      }

      return var0.resolve(var1, var2);
   }

   @NotNull
   public final Path resolve(@NotNull ByteString child, boolean normalize) {
      Intrinsics.checkNotNullParameter(child, "child");
      int $i$f$commonResolve = 0;
      Buffer child$iv$iv = (new Buffer()).write(child);
      int $i$f$commonResolve = 0;
      return -Path.commonResolve(this, -Path.toPath(child$iv$iv, false), normalize);
   }

   // $FF: synthetic method
   public static Path resolve$default(Path var0, ByteString var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = false;
      }

      return var0.resolve(var1, var2);
   }

   @NotNull
   public final Path resolve(@NotNull Path child, boolean normalize) {
      Intrinsics.checkNotNullParameter(child, "child");
      return -Path.commonResolve(this, child, normalize);
   }

   // $FF: synthetic method
   public static Path resolve$default(Path var0, Path var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = false;
      }

      return var0.resolve(var1, var2);
   }

   @NotNull
   public final Path relativeTo(@NotNull Path other) {
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRelativeTo = 0;
      if (!Intrinsics.areEqual((Object)this.getRoot(), (Object)other.getRoot())) {
         int otherSegments$iv = 0;
         String var14 = "Paths of different roots cannot be relative to each other: " + this + " and " + other;
         throw new IllegalArgumentException(var14.toString());
      } else {
         List thisSegments$iv = this.getSegmentsBytes();
         List otherSegments$iv = other.getSegmentsBytes();
         int firstNewSegmentIndex$iv = 0;

         int minSegmentsSize$iv;
         for(minSegmentsSize$iv = Math.min(thisSegments$iv.size(), otherSegments$iv.size()); firstNewSegmentIndex$iv < minSegmentsSize$iv && Intrinsics.areEqual(thisSegments$iv.get(firstNewSegmentIndex$iv), otherSegments$iv.get(firstNewSegmentIndex$iv)); ++firstNewSegmentIndex$iv) {
         }

         Path var10000;
         if (firstNewSegmentIndex$iv == minSegmentsSize$iv && this.getBytes$okio().size() == other.getBytes$okio().size()) {
            var10000 = Path.Companion.get$default(Companion, (String)".", false, 1, (Object)null);
         } else {
            if (otherSegments$iv.subList(firstNewSegmentIndex$iv, otherSegments$iv.size()).indexOf(-Path.access$getDOT_DOT$p()) != -1) {
               int slash$iv = 0;
               String var16 = "Impossible relative path to resolve: " + this + " and " + other;
               throw new IllegalArgumentException(var16.toString());
            }

            if (Intrinsics.areEqual((Object)other.getBytes$okio(), (Object)-Path.access$getDOT$p())) {
               var10000 = this;
            } else {
               Buffer buffer$iv = new Buffer();
               ByteString var19 = -Path.access$getSlash(other);
               if (var19 == null) {
                  var19 = -Path.access$getSlash(this);
                  if (var19 == null) {
                     var19 = -Path.access$toSlash(DIRECTORY_SEPARATOR);
                  }
               }

               ByteString slash$iv = var19;
               int i$iv = firstNewSegmentIndex$iv;

               for(int var12 = otherSegments$iv.size(); i$iv < var12; ++i$iv) {
                  buffer$iv.write(-Path.access$getDOT_DOT$p());
                  buffer$iv.write(slash$iv);
               }

               i$iv = firstNewSegmentIndex$iv;

               for(int var18 = thisSegments$iv.size(); i$iv < var18; ++i$iv) {
                  buffer$iv.write((ByteString)thisSegments$iv.get(i$iv));
                  buffer$iv.write(slash$iv);
               }

               var10000 = -Path.toPath(buffer$iv, false);
            }
         }

         return var10000;
      }
   }

   @NotNull
   public final Path normalized() {
      int $i$f$commonNormalized = 0;
      return Companion.get(this.toString(), true);
   }

   @NotNull
   public final File toFile() {
      return new File(this.toString());
   }

   @NotNull
   public final java.nio.file.Path toNioPath() {
      java.nio.file.Path var10000 = Paths.get(this.toString());
      Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
      return var10000;
   }

   public int compareTo(@NotNull Path other) {
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonCompareTo = 0;
      return this.getBytes$okio().compareTo(other.getBytes$okio());
   }

   public boolean equals(@Nullable Object other) {
      int $i$f$commonEquals = 0;
      return other instanceof Path && Intrinsics.areEqual((Object)((Path)other).getBytes$okio(), (Object)this.getBytes$okio());
   }

   public int hashCode() {
      int $i$f$commonHashCode = 0;
      return this.getBytes$okio().hashCode();
   }

   @NotNull
   public String toString() {
      int $i$f$commonToString = 0;
      return this.getBytes$okio().utf8();
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @JvmOverloads
   @NotNull
   public static final Path get(@NotNull String $this$get, boolean normalize) {
      return Companion.get($this$get, normalize);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @JvmOverloads
   @NotNull
   public static final Path get(@NotNull File $this$get, boolean normalize) {
      return Companion.get($this$get, normalize);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @JvmOverloads
   @NotNull
   public static final Path get(@NotNull java.nio.file.Path $this$get, boolean normalize) {
      return Companion.get($this$get, normalize);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @JvmOverloads
   @NotNull
   public static final Path get(@NotNull String $this$get) {
      return Companion.get($this$get);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @JvmOverloads
   @NotNull
   public static final Path get(@NotNull File $this$get) {
      return Companion.get($this$get);
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @JvmOverloads
   @NotNull
   public static final Path get(@NotNull java.nio.file.Path $this$get) {
      return Companion.get($this$get);
   }

   static {
      String var10000 = File.separator;
      Intrinsics.checkNotNullExpressionValue(var10000, "separator");
      DIRECTORY_SEPARATOR = var10000;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001b\u0010\u0006\u001a\u00020\u0007*\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\nJ\u001b\u0010\u000b\u001a\u00020\u0007*\u00020\f2\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\nJ\u001b\u0010\u000b\u001a\u00020\u0007*\u00020\r2\b\b\u0002\u0010\b\u001a\u00020\tH\u0007¢\u0006\u0002\b\nR\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000¨\u0006\u000e"},
      d2 = {"Lokio/Path$Companion;", "", "<init>", "()V", "DIRECTORY_SEPARATOR", "", "toPath", "Lokio/Path;", "normalize", "", "get", "toOkioPath", "Ljava/io/File;", "Ljava/nio/file/Path;", "okio"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @JvmOverloads
      @NotNull
      public final Path get(@NotNull String $this$toPath, boolean normalize) {
         Intrinsics.checkNotNullParameter($this$toPath, "<this>");
         return -Path.commonToPath($this$toPath, normalize);
      }

      // $FF: synthetic method
      public static Path get$default(Companion var0, String var1, boolean var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = false;
         }

         return var0.get(var1, var2);
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @JvmOverloads
      @NotNull
      public final Path get(@NotNull File $this$toOkioPath, boolean normalize) {
         Intrinsics.checkNotNullParameter($this$toOkioPath, "<this>");
         String var10001 = $this$toOkioPath.toString();
         Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
         return this.get(var10001, normalize);
      }

      // $FF: synthetic method
      public static Path get$default(Companion var0, File var1, boolean var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = false;
         }

         return var0.get(var1, var2);
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @JvmOverloads
      @NotNull
      public final Path get(@NotNull java.nio.file.Path $this$toOkioPath, boolean normalize) {
         Intrinsics.checkNotNullParameter($this$toOkioPath, "<this>");
         return this.get($this$toOkioPath.toString(), normalize);
      }

      // $FF: synthetic method
      public static Path get$default(Companion var0, java.nio.file.Path var1, boolean var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = false;
         }

         return var0.get(var1, var2);
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @JvmOverloads
      @NotNull
      public final Path get(@NotNull String $this$toPath) {
         Intrinsics.checkNotNullParameter($this$toPath, "<this>");
         return get$default(this, (String)$this$toPath, false, 1, (Object)null);
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @JvmOverloads
      @NotNull
      public final Path get(@NotNull File $this$toOkioPath) {
         Intrinsics.checkNotNullParameter($this$toOkioPath, "<this>");
         return get$default(this, (File)$this$toOkioPath, false, 1, (Object)null);
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @JvmOverloads
      @NotNull
      public final Path get(@NotNull java.nio.file.Path $this$toOkioPath) {
         Intrinsics.checkNotNullParameter($this$toOkioPath, "<this>");
         return get$default(this, (java.nio.file.Path)$this$toOkioPath, false, 1, (Object)null);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
