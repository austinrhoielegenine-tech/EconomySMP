package okio.internal;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b$\n\u0002\u0010!\n\u0002\b\f\b\u0000\u0018\u00002\u00020\u0001B±\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\t\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\t\u0012\b\b\u0002\u0010\u000f\u001a\u00020\r\u0012\b\b\u0002\u0010\u0010\u001a\u00020\r\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u0017\u0010\u0018J-\u00105\u001a\u00020\u00002\b\u0010\u0014\u001a\u0004\u0018\u00010\r2\b\u0010\u0015\u001a\u0004\u0018\u00010\r2\b\u0010\u0016\u001a\u0004\u0018\u00010\rH\u0000¢\u0006\u0004\b6\u00107R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\n\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001fR\u0011\u0010\u000b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001fR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u000e\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001fR\u0011\u0010\u000f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b%\u0010#R\u0011\u0010\u0010\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b&\u0010#R\u0015\u0010\u0011\u001a\u0004\u0018\u00010\t¢\u0006\n\n\u0002\u0010)\u001a\u0004\b'\u0010(R\u0015\u0010\u0012\u001a\u0004\u0018\u00010\t¢\u0006\n\n\u0002\u0010)\u001a\u0004\b*\u0010(R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\t¢\u0006\n\n\u0002\u0010)\u001a\u0004\b+\u0010(R\u0015\u0010\u0014\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010.\u001a\u0004\b,\u0010-R\u0015\u0010\u0015\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010.\u001a\u0004\b/\u0010-R\u0015\u0010\u0016\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010.\u001a\u0004\b0\u0010-R\u0017\u00101\u001a\b\u0012\u0004\u0012\u00020\u000302¢\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0016\u00108\u001a\u0004\u0018\u00010\t8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b9\u0010(R\u0016\u0010:\u001a\u0004\u0018\u00010\t8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b;\u0010(R\u0016\u0010<\u001a\u0004\u0018\u00010\t8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b=\u0010(¨\u0006>"},
   d2 = {"Lokio/internal/ZipEntry;", "", "canonicalPath", "Lokio/Path;", "isDirectory", "", "comment", "", "crc", "", "compressedSize", "size", "compressionMethod", "", "offset", "dosLastModifiedAtDate", "dosLastModifiedAtTime", "ntfsLastModifiedAtFiletime", "ntfsLastAccessedAtFiletime", "ntfsCreatedAtFiletime", "extendedLastModifiedAtSeconds", "extendedLastAccessedAtSeconds", "extendedCreatedAtSeconds", "<init>", "(Lokio/Path;ZLjava/lang/String;JJJIJIILjava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)V", "getCanonicalPath", "()Lokio/Path;", "()Z", "getComment", "()Ljava/lang/String;", "getCrc", "()J", "getCompressedSize", "getSize", "getCompressionMethod", "()I", "getOffset", "getDosLastModifiedAtDate", "getDosLastModifiedAtTime", "getNtfsLastModifiedAtFiletime", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getNtfsLastAccessedAtFiletime", "getNtfsCreatedAtFiletime", "getExtendedLastModifiedAtSeconds", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getExtendedLastAccessedAtSeconds", "getExtendedCreatedAtSeconds", "children", "", "getChildren", "()Ljava/util/List;", "copy", "copy$okio", "(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)Lokio/internal/ZipEntry;", "lastAccessedAtMillis", "getLastAccessedAtMillis$okio", "lastModifiedAtMillis", "getLastModifiedAtMillis$okio", "createdAtMillis", "getCreatedAtMillis$okio", "okio"}
)
public final class ZipEntry {
   @NotNull
   private final Path canonicalPath;
   private final boolean isDirectory;
   @NotNull
   private final String comment;
   private final long crc;
   private final long compressedSize;
   private final long size;
   private final int compressionMethod;
   private final long offset;
   private final int dosLastModifiedAtDate;
   private final int dosLastModifiedAtTime;
   @Nullable
   private final Long ntfsLastModifiedAtFiletime;
   @Nullable
   private final Long ntfsLastAccessedAtFiletime;
   @Nullable
   private final Long ntfsCreatedAtFiletime;
   @Nullable
   private final Integer extendedLastModifiedAtSeconds;
   @Nullable
   private final Integer extendedLastAccessedAtSeconds;
   @Nullable
   private final Integer extendedCreatedAtSeconds;
   @NotNull
   private final List<Path> children;

   public ZipEntry(@NotNull Path canonicalPath, boolean isDirectory, @NotNull String comment, long crc, long compressedSize, long size, int compressionMethod, long offset, int dosLastModifiedAtDate, int dosLastModifiedAtTime, @Nullable Long ntfsLastModifiedAtFiletime, @Nullable Long ntfsLastAccessedAtFiletime, @Nullable Long ntfsCreatedAtFiletime, @Nullable Integer extendedLastModifiedAtSeconds, @Nullable Integer extendedLastAccessedAtSeconds, @Nullable Integer extendedCreatedAtSeconds) {
      Intrinsics.checkNotNullParameter(canonicalPath, "canonicalPath");
      Intrinsics.checkNotNullParameter(comment, "comment");
      super();
      this.canonicalPath = canonicalPath;
      this.isDirectory = isDirectory;
      this.comment = comment;
      this.crc = crc;
      this.compressedSize = compressedSize;
      this.size = size;
      this.compressionMethod = compressionMethod;
      this.offset = offset;
      this.dosLastModifiedAtDate = dosLastModifiedAtDate;
      this.dosLastModifiedAtTime = dosLastModifiedAtTime;
      this.ntfsLastModifiedAtFiletime = ntfsLastModifiedAtFiletime;
      this.ntfsLastAccessedAtFiletime = ntfsLastAccessedAtFiletime;
      this.ntfsCreatedAtFiletime = ntfsCreatedAtFiletime;
      this.extendedLastModifiedAtSeconds = extendedLastModifiedAtSeconds;
      this.extendedLastAccessedAtSeconds = extendedLastAccessedAtSeconds;
      this.extendedCreatedAtSeconds = extendedCreatedAtSeconds;
      this.children = (List)(new ArrayList());
   }

   // $FF: synthetic method
   public ZipEntry(Path var1, boolean var2, String var3, long var4, long var6, long var8, int var10, long var11, int var13, int var14, Long var15, Long var16, Long var17, Integer var18, Integer var19, Integer var20, int var21, DefaultConstructorMarker var22) {
      if ((var21 & 2) != 0) {
         var2 = false;
      }

      if ((var21 & 4) != 0) {
         var3 = "";
      }

      if ((var21 & 8) != 0) {
         var4 = -1L;
      }

      if ((var21 & 16) != 0) {
         var6 = -1L;
      }

      if ((var21 & 32) != 0) {
         var8 = -1L;
      }

      if ((var21 & 64) != 0) {
         var10 = -1;
      }

      if ((var21 & 128) != 0) {
         var11 = -1L;
      }

      if ((var21 & 256) != 0) {
         var13 = -1;
      }

      if ((var21 & 512) != 0) {
         var14 = -1;
      }

      if ((var21 & 1024) != 0) {
         var15 = null;
      }

      if ((var21 & 2048) != 0) {
         var16 = null;
      }

      if ((var21 & 4096) != 0) {
         var17 = null;
      }

      if ((var21 & 8192) != 0) {
         var18 = null;
      }

      if ((var21 & 16384) != 0) {
         var19 = null;
      }

      if ((var21 & '耀') != 0) {
         var20 = null;
      }

      this(var1, var2, var3, var4, var6, var8, var10, var11, var13, var14, var15, var16, var17, var18, var19, var20);
   }

   @NotNull
   public final Path getCanonicalPath() {
      return this.canonicalPath;
   }

   public final boolean isDirectory() {
      return this.isDirectory;
   }

   @NotNull
   public final String getComment() {
      return this.comment;
   }

   public final long getCrc() {
      return this.crc;
   }

   public final long getCompressedSize() {
      return this.compressedSize;
   }

   public final long getSize() {
      return this.size;
   }

   public final int getCompressionMethod() {
      return this.compressionMethod;
   }

   public final long getOffset() {
      return this.offset;
   }

   public final int getDosLastModifiedAtDate() {
      return this.dosLastModifiedAtDate;
   }

   public final int getDosLastModifiedAtTime() {
      return this.dosLastModifiedAtTime;
   }

   @Nullable
   public final Long getNtfsLastModifiedAtFiletime() {
      return this.ntfsLastModifiedAtFiletime;
   }

   @Nullable
   public final Long getNtfsLastAccessedAtFiletime() {
      return this.ntfsLastAccessedAtFiletime;
   }

   @Nullable
   public final Long getNtfsCreatedAtFiletime() {
      return this.ntfsCreatedAtFiletime;
   }

   @Nullable
   public final Integer getExtendedLastModifiedAtSeconds() {
      return this.extendedLastModifiedAtSeconds;
   }

   @Nullable
   public final Integer getExtendedLastAccessedAtSeconds() {
      return this.extendedLastAccessedAtSeconds;
   }

   @Nullable
   public final Integer getExtendedCreatedAtSeconds() {
      return this.extendedCreatedAtSeconds;
   }

   @NotNull
   public final List<Path> getChildren() {
      return this.children;
   }

   @NotNull
   public final ZipEntry copy$okio(@Nullable Integer extendedLastModifiedAtSeconds, @Nullable Integer extendedLastAccessedAtSeconds, @Nullable Integer extendedCreatedAtSeconds) {
      return new ZipEntry(this.canonicalPath, this.isDirectory, this.comment, this.crc, this.compressedSize, this.size, this.compressionMethod, this.offset, this.dosLastModifiedAtDate, this.dosLastModifiedAtTime, this.ntfsLastModifiedAtFiletime, this.ntfsLastAccessedAtFiletime, this.ntfsCreatedAtFiletime, extendedLastModifiedAtSeconds, extendedLastAccessedAtSeconds, extendedCreatedAtSeconds);
   }

   @Nullable
   public final Long getLastAccessedAtMillis$okio() {
      return this.ntfsLastAccessedAtFiletime != null ? ZipFilesKt.filetimeToEpochMillis(this.ntfsLastAccessedAtFiletime) : (this.extendedLastAccessedAtSeconds != null ? (long)this.extendedLastAccessedAtSeconds * 1000L : null);
   }

   @Nullable
   public final Long getLastModifiedAtMillis$okio() {
      return this.ntfsLastModifiedAtFiletime != null ? ZipFilesKt.filetimeToEpochMillis(this.ntfsLastModifiedAtFiletime) : (this.extendedLastModifiedAtSeconds != null ? (long)this.extendedLastModifiedAtSeconds * 1000L : (this.dosLastModifiedAtTime != -1 ? ZipFilesKt.dosDateTimeToEpochMillis(this.dosLastModifiedAtDate, this.dosLastModifiedAtTime) : null));
   }

   @Nullable
   public final Long getCreatedAtMillis$okio() {
      return this.ntfsCreatedAtFiletime != null ? ZipFilesKt.filetimeToEpochMillis(this.ntfsCreatedAtFiletime) : (this.extendedCreatedAtSeconds != null ? (long)this.extendedCreatedAtSeconds * 1000L : null);
   }
}
