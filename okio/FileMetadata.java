package okio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;
import kotlin.reflect.KClasses;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001Bq\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\b\u0012\u0018\b\u0002\u0010\f\u001a\u0012\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0004\b\u000f\u0010\u0010J'\u0010\u001c\u001a\u0004\u0018\u0001H\u001d\"\b\b\u0000\u0010\u001d*\u00020\u00012\u000e\u0010\u001e\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u001d0\u000e¢\u0006\u0002\u0010\u001fJu\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\b2\u0018\b\u0002\u0010\f\u001a\u0012\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\u0010!J\b\u0010\"\u001a\u00020#H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0011R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0014\u0010\u0015R\u0015\u0010\t\u001a\u0004\u0018\u00010\b¢\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0017\u0010\u0015R\u0015\u0010\n\u001a\u0004\u0018\u00010\b¢\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0018\u0010\u0015R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\b¢\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0019\u0010\u0015R!\u0010\f\u001a\u0012\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e\u0012\u0004\u0012\u00020\u00010\r¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b¨\u0006$"},
   d2 = {"Lokio/FileMetadata;", "", "isRegularFile", "", "isDirectory", "symlinkTarget", "Lokio/Path;", "size", "", "createdAtMillis", "lastModifiedAtMillis", "lastAccessedAtMillis", "extras", "", "Lkotlin/reflect/KClass;", "<init>", "(ZZLokio/Path;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/util/Map;)V", "()Z", "getSymlinkTarget", "()Lokio/Path;", "getSize", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getCreatedAtMillis", "getLastModifiedAtMillis", "getLastAccessedAtMillis", "getExtras", "()Ljava/util/Map;", "extra", "T", "type", "(Lkotlin/reflect/KClass;)Ljava/lang/Object;", "copy", "(ZZLokio/Path;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/util/Map;)Lokio/FileMetadata;", "toString", "", "okio"}
)
public final class FileMetadata {
   private final boolean isRegularFile;
   private final boolean isDirectory;
   @Nullable
   private final Path symlinkTarget;
   @Nullable
   private final Long size;
   @Nullable
   private final Long createdAtMillis;
   @Nullable
   private final Long lastModifiedAtMillis;
   @Nullable
   private final Long lastAccessedAtMillis;
   @NotNull
   private final Map<KClass<?>, Object> extras;

   public FileMetadata(boolean isRegularFile, boolean isDirectory, @Nullable Path symlinkTarget, @Nullable Long size, @Nullable Long createdAtMillis, @Nullable Long lastModifiedAtMillis, @Nullable Long lastAccessedAtMillis, @NotNull Map<KClass<?>, ? extends Object> extras) {
      Intrinsics.checkNotNullParameter(extras, "extras");
      super();
      this.isRegularFile = isRegularFile;
      this.isDirectory = isDirectory;
      this.symlinkTarget = symlinkTarget;
      this.size = size;
      this.createdAtMillis = createdAtMillis;
      this.lastModifiedAtMillis = lastModifiedAtMillis;
      this.lastAccessedAtMillis = lastAccessedAtMillis;
      this.extras = MapsKt.toMap(extras);
   }

   // $FF: synthetic method
   public FileMetadata(boolean var1, boolean var2, Path var3, Long var4, Long var5, Long var6, Long var7, Map var8, int var9, DefaultConstructorMarker var10) {
      if ((var9 & 1) != 0) {
         var1 = false;
      }

      if ((var9 & 2) != 0) {
         var2 = false;
      }

      if ((var9 & 4) != 0) {
         var3 = null;
      }

      if ((var9 & 8) != 0) {
         var4 = null;
      }

      if ((var9 & 16) != 0) {
         var5 = null;
      }

      if ((var9 & 32) != 0) {
         var6 = null;
      }

      if ((var9 & 64) != 0) {
         var7 = null;
      }

      if ((var9 & 128) != 0) {
         var8 = MapsKt.emptyMap();
      }

      this(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public final boolean isRegularFile() {
      return this.isRegularFile;
   }

   public final boolean isDirectory() {
      return this.isDirectory;
   }

   @Nullable
   public final Path getSymlinkTarget() {
      return this.symlinkTarget;
   }

   @Nullable
   public final Long getSize() {
      return this.size;
   }

   @Nullable
   public final Long getCreatedAtMillis() {
      return this.createdAtMillis;
   }

   @Nullable
   public final Long getLastModifiedAtMillis() {
      return this.lastModifiedAtMillis;
   }

   @Nullable
   public final Long getLastAccessedAtMillis() {
      return this.lastAccessedAtMillis;
   }

   @NotNull
   public final Map<KClass<?>, Object> getExtras() {
      return this.extras;
   }

   @Nullable
   public final <T> T extra(@NotNull KClass<? extends T> type) {
      Intrinsics.checkNotNullParameter(type, "type");
      Object var10000 = this.extras.get(type);
      if (var10000 == null) {
         return null;
      } else {
         Object value = var10000;
         return (T)KClasses.cast(type, value);
      }
   }

   @NotNull
   public final FileMetadata copy(boolean isRegularFile, boolean isDirectory, @Nullable Path symlinkTarget, @Nullable Long size, @Nullable Long createdAtMillis, @Nullable Long lastModifiedAtMillis, @Nullable Long lastAccessedAtMillis, @NotNull Map<KClass<?>, ? extends Object> extras) {
      Intrinsics.checkNotNullParameter(extras, "extras");
      return new FileMetadata(isRegularFile, isDirectory, symlinkTarget, size, createdAtMillis, lastModifiedAtMillis, lastAccessedAtMillis, extras);
   }

   // $FF: synthetic method
   public static FileMetadata copy$default(FileMetadata var0, boolean var1, boolean var2, Path var3, Long var4, Long var5, Long var6, Long var7, Map var8, int var9, Object var10) {
      if ((var9 & 1) != 0) {
         var1 = var0.isRegularFile;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.isDirectory;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.symlinkTarget;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.size;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.createdAtMillis;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.lastModifiedAtMillis;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.lastAccessedAtMillis;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.extras;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   public String toString() {
      List fields = (List)(new ArrayList());
      if (this.isRegularFile) {
         ((Collection)fields).add("isRegularFile");
      }

      if (this.isDirectory) {
         ((Collection)fields).add("isDirectory");
      }

      if (this.size != null) {
         ((Collection)fields).add("byteCount=" + this.size);
      }

      if (this.createdAtMillis != null) {
         ((Collection)fields).add("createdAt=" + this.createdAtMillis);
      }

      if (this.lastModifiedAtMillis != null) {
         ((Collection)fields).add("lastModifiedAt=" + this.lastModifiedAtMillis);
      }

      if (this.lastAccessedAtMillis != null) {
         ((Collection)fields).add("lastAccessedAt=" + this.lastAccessedAtMillis);
      }

      if (!this.extras.isEmpty()) {
         ((Collection)fields).add("extras=" + this.extras);
      }

      return CollectionsKt.joinToString$default((Iterable)fields, (CharSequence)", ", (CharSequence)"FileMetadata(", (CharSequence)")", 0, (CharSequence)null, (Function1)null, 56, (Object)null);
   }

   public FileMetadata() {
      this(false, false, (Path)null, (Long)null, (Long)null, (Long)null, (Long)null, (Map)null, 255, (DefaultConstructorMarker)null);
   }
}
