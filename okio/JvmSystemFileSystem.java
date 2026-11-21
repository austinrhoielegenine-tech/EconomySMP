package okio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0010\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0016J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0006\u001a\u00020\u0005H\u0016J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\u0006\u0010\u000b\u001a\u00020\u0005H\u0016J\u0018\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\u0005H\u0016J \u0010\t\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eH\u0002J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0005H\u0016J \u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u000eH\u0016J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u0005H\u0016J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u000eH\u0016J\u0018\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u000eH\u0016J\u0018\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u000eH\u0016J\u0018\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u0005H\u0016J\u0018\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u000eH\u0016J\u0018\u0010\u001f\u001a\u00020\u001b2\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u0005H\u0016J\b\u0010 \u001a\u00020!H\u0016J\f\u0010\"\u001a\u00020\u001b*\u00020\u0005H\u0002J\f\u0010#\u001a\u00020\u001b*\u00020\u0005H\u0002¨\u0006$"},
   d2 = {"Lokio/JvmSystemFileSystem;", "Lokio/FileSystem;", "<init>", "()V", "canonicalize", "Lokio/Path;", "path", "metadataOrNull", "Lokio/FileMetadata;", "list", "", "dir", "listOrNull", "throwOnFailure", "", "openReadOnly", "Lokio/FileHandle;", "file", "openReadWrite", "mustCreate", "mustExist", "source", "Lokio/Source;", "sink", "Lokio/Sink;", "appendingSink", "createDirectory", "", "atomicMove", "target", "delete", "createSymlink", "toString", "", "requireExist", "requireCreate", "okio"}
)
@SourceDebugExtension({"SMAP\nJvmSystemFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JvmSystemFileSystem.kt\nokio/JvmSystemFileSystem\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,158:1\n11896#2,3:159\n*S KotlinDebug\n*F\n+ 1 JvmSystemFileSystem.kt\nokio/JvmSystemFileSystem\n*L\n77#1:159,3\n*E\n"})
public class JvmSystemFileSystem extends FileSystem {
   @NotNull
   public Path canonicalize(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      File canonicalFile = path.toFile().getCanonicalFile();
      if (!canonicalFile.exists()) {
         throw new FileNotFoundException("no such file");
      } else {
         Path.Companion var10000 = Path.Companion;
         Intrinsics.checkNotNull(canonicalFile);
         return Path.Companion.get$default(var10000, (File)canonicalFile, false, 1, (Object)null);
      }
   }

   @Nullable
   public FileMetadata metadataOrNull(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      File file = path.toFile();
      boolean isRegularFile = file.isFile();
      boolean isDirectory = file.isDirectory();
      long lastModifiedAtMillis = file.lastModified();
      long size = file.length();
      return !isRegularFile && !isDirectory && lastModifiedAtMillis == 0L && size == 0L && !file.exists() ? null : new FileMetadata(isRegularFile, isDirectory, (Path)null, size, (Long)null, lastModifiedAtMillis, (Long)null, (Map)null, 128, (DefaultConstructorMarker)null);
   }

   @NotNull
   public List<Path> list(@NotNull Path dir) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      List var10000 = this.list(dir, true);
      Intrinsics.checkNotNull(var10000);
      return var10000;
   }

   @Nullable
   public List<Path> listOrNull(@NotNull Path dir) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      return this.list(dir, false);
   }

   private final List<Path> list(Path dir, boolean throwOnFailure) {
      File file = dir.toFile();
      String[] entries = file.list();
      if (entries == null) {
         if (throwOnFailure) {
            if (!file.exists()) {
               throw new FileNotFoundException("no such file: " + dir);
            } else {
               throw new IOException("failed to list " + dir);
            }
         } else {
            return null;
         }
      } else {
         String[] $this$mapTo$iv = entries;
         Collection destination$iv = (Collection)((List)(new ArrayList()));
         int $i$f$mapTo = 0;
         int var9 = 0;

         for(int var10 = entries.length; var9 < var10; ++var9) {
            Object item$iv = $this$mapTo$iv[var9];
            int var13 = 0;
            Intrinsics.checkNotNull(item$iv);
            destination$iv.add(dir.resolve((String)item$iv));
         }

         List result = (List)destination$iv;
         CollectionsKt.sort(result);
         return result;
      }
   }

   @NotNull
   public FileHandle openReadOnly(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");
      return new JvmFileHandle(false, new RandomAccessFile(file.toFile(), "r"));
   }

   @NotNull
   public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      if (mustCreate && mustExist) {
         int var4 = 0;
         String var5 = "Cannot require mustCreate and mustExist at the same time.";
         throw new IllegalArgumentException(var5.toString());
      } else {
         if (mustCreate) {
            this.requireCreate(file);
         }

         if (mustExist) {
            this.requireExist(file);
         }

         return new JvmFileHandle(true, new RandomAccessFile(file.toFile(), "rw"));
      }
   }

   @NotNull
   public Source source(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");
      return Okio.source(file.toFile());
   }

   @NotNull
   public Sink sink(@NotNull Path file, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(file, "file");
      if (mustCreate) {
         this.requireCreate(file);
      }

      return Okio.sink$default(file.toFile(), false, 1, (Object)null);
   }

   @NotNull
   public Sink appendingSink(@NotNull Path file, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      if (mustExist) {
         this.requireExist(file);
      }

      return Okio.sink(file.toFile(), true);
   }

   public void createDirectory(@NotNull Path dir, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      if (!dir.toFile().mkdir()) {
         FileMetadata var10000 = this.metadataOrNull(dir);
         boolean alreadyExist = var10000 != null ? var10000.isDirectory() : false;
         if (alreadyExist) {
            if (mustCreate) {
               throw new IOException(dir + " already exists.");
            }
         } else {
            throw new IOException("failed to create directory: " + dir);
         }
      }
   }

   public void atomicMove(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      boolean renamed = source.toFile().renameTo(target.toFile());
      if (!renamed) {
         throw new IOException("failed to move " + source + " to " + target);
      }
   }

   public void delete(@NotNull Path path, boolean mustExist) {
      Intrinsics.checkNotNullParameter(path, "path");
      if (Thread.interrupted()) {
         throw new InterruptedIOException("interrupted");
      } else {
         File file = path.toFile();
         boolean deleted = file.delete();
         if (!deleted) {
            if (file.exists()) {
               throw new IOException("failed to delete " + path);
            }

            if (mustExist) {
               throw new FileNotFoundException("no such file: " + path);
            }
         }

      }
   }

   public void createSymlink(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      throw new IOException("unsupported");
   }

   @NotNull
   public String toString() {
      return "JvmSystemFileSystem";
   }

   private final void requireExist(Path $this$requireExist) {
      if (!this.exists($this$requireExist)) {
         throw new IOException($this$requireExist + " doesn't exist.");
      }
   }

   private final void requireCreate(Path $this$requireCreate) {
      if (this.exists($this$requireCreate)) {
         throw new IOException($this$requireCreate + " already exists.");
      }
   }
}
