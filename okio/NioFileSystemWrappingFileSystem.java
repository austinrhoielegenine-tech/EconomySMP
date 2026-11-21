package okio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.path.PathsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\f\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0002J\u0010\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\bH\u0016J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\n\u001a\u00020\bH\u0016J\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000e2\u0006\u0010\u000f\u001a\u00020\bH\u0016J\u0018\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\bH\u0016J \u0010\r\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\bH\u0016J \u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0012H\u0016J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0015\u001a\u00020\bH\u0016J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u0012H\u0016J\u0018\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\u0012H\u0016J\u0018\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u0012H\u0016J\u0018\u0010 \u001a\u00020\u001f2\u0006\u0010\u0019\u001a\u00020\b2\u0006\u0010!\u001a\u00020\bH\u0016J\u0018\u0010\"\u001a\u00020\u001f2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\u0012H\u0016J\u0018\u0010#\u001a\u00020\u001f2\u0006\u0010\u0019\u001a\u00020\b2\u0006\u0010!\u001a\u00020\bH\u0016J\b\u0010$\u001a\u00020\u001fH\u0016J\b\u0010%\u001a\u00020&H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006'"},
   d2 = {"Lokio/NioFileSystemWrappingFileSystem;", "Lokio/NioSystemFileSystem;", "nioFileSystem", "Ljava/nio/file/FileSystem;", "<init>", "(Ljava/nio/file/FileSystem;)V", "resolve", "Ljava/nio/file/Path;", "Lokio/Path;", "canonicalize", "path", "metadataOrNull", "Lokio/FileMetadata;", "list", "", "dir", "listOrNull", "throwOnFailure", "", "openReadOnly", "Lokio/FileHandle;", "file", "openReadWrite", "mustCreate", "mustExist", "source", "Lokio/Source;", "sink", "Lokio/Sink;", "appendingSink", "createDirectory", "", "atomicMove", "target", "delete", "createSymlink", "close", "toString", "", "okio"}
)
@SourceDebugExtension({"SMAP\nNioFileSystemWrappingFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NioFileSystemWrappingFileSystem.kt\nokio/NioFileSystemWrappingFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,196:1\n1634#2,3:197\n1#3:200\n37#4,2:201\n37#4,2:203\n37#4,2:205\n*S KotlinDebug\n*F\n+ 1 NioFileSystemWrappingFileSystem.kt\nokio/NioFileSystemWrappingFileSystem\n*L\n77#1:197,3\n104#1:201,2\n125#1:203,2\n138#1:205,2\n*E\n"})
public final class NioFileSystemWrappingFileSystem extends NioSystemFileSystem {
   @NotNull
   private final java.nio.file.FileSystem nioFileSystem;

   public NioFileSystemWrappingFileSystem(@NotNull java.nio.file.FileSystem nioFileSystem) {
      Intrinsics.checkNotNullParameter(nioFileSystem, "nioFileSystem");
      super();
      this.nioFileSystem = nioFileSystem;
   }

   private final java.nio.file.Path resolve(Path $this$resolve) {
      java.nio.file.Path var10000 = this.nioFileSystem.getPath($this$resolve.toString());
      Intrinsics.checkNotNullExpressionValue(var10000, "getPath(...)");
      return var10000;
   }

   @NotNull
   public Path canonicalize(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");

      try {
         Path.Companion var10000 = Path.Companion;
         java.nio.file.Path var10001 = this.resolve(path).toRealPath();
         Intrinsics.checkNotNullExpressionValue(var10001, "toRealPath(...)");
         return Path.Companion.get$default(var10000, (java.nio.file.Path)var10001, false, 1, (Object)null);
      } catch (NoSuchFileException var3) {
         throw new FileNotFoundException("no such file: " + path);
      }
   }

   @Nullable
   public FileMetadata metadataOrNull(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      return this.metadataOrNull(this.resolve(path));
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
      java.nio.file.Path nioDir = this.resolve(dir);

      List result;
      try {
         result = PathsKt.listDirectoryEntries$default(nioDir, (String)null, 1, (Object)null);
      } catch (Exception var14) {
         if (throwOnFailure) {
            LinkOption[] var10001 = new LinkOption[0];
            if (!Files.exists(nioDir, (LinkOption[])Arrays.copyOf(var10001, var10001.length))) {
               throw new FileNotFoundException("no such file: " + dir);
            }

            throw new IOException("failed to list " + dir);
         }

         return null;
      }

      Iterable e = (Iterable)result;
      Collection destination$iv = (Collection)((List)(new ArrayList()));
      int $i$f$mapTo = 0;

      for(Object item$iv : e) {
         java.nio.file.Path entry = (java.nio.file.Path)item$iv;
         int var12 = 0;
         destination$iv.add(Path.Companion.get$default(Path.Companion, (java.nio.file.Path)entry, false, 1, (Object)null));
      }

      result = (List)destination$iv;
      CollectionsKt.sort(result);
      return result;
   }

   @NotNull
   public FileHandle openReadOnly(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");

      FileChannel channel;
      try {
         java.nio.file.Path var10000 = this.resolve(file);
         OpenOption[] channel = new OpenOption[]{StandardOpenOption.READ};
         channel = FileChannel.open(var10000, channel);
      } catch (NoSuchFileException var5) {
         throw new FileNotFoundException("no such file: " + file);
      }

      Intrinsics.checkNotNull(channel);
      return new NioFileSystemFileHandle(false, channel);
   }

   @NotNull
   public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      if (mustCreate && mustExist) {
         int channel = 0;
         String $this$openReadWrite_u24lambda_u241 = "Cannot require mustCreate and mustExist at the same time.";
         throw new IllegalArgumentException($this$openReadWrite_u24lambda_u241.toString());
      } else {
         List $this$openReadWrite_u24lambda_u241 = CollectionsKt.createListBuilder();
         int thisCollection$iv = 0;
         $this$openReadWrite_u24lambda_u241.add(StandardOpenOption.READ);
         $this$openReadWrite_u24lambda_u241.add(StandardOpenOption.WRITE);
         if (mustCreate) {
            $this$openReadWrite_u24lambda_u241.add(StandardOpenOption.CREATE_NEW);
         } else if (!mustExist) {
            $this$openReadWrite_u24lambda_u241.add(StandardOpenOption.CREATE);
         }

         List openOptions = CollectionsKt.build($this$openReadWrite_u24lambda_u241);

         FileChannel channel;
         try {
            java.nio.file.Path var10000 = this.resolve(file);
            Collection $this$toTypedArray$iv = (Collection)openOptions;
            int $i$f$toTypedArray = 0;
            StandardOpenOption[] $this$openReadWrite_u24lambda_u241 = (StandardOpenOption[])$this$toTypedArray$iv.toArray(new StandardOpenOption[0]);
            channel = FileChannel.open(var10000, (OpenOption[])Arrays.copyOf($this$openReadWrite_u24lambda_u241, $this$openReadWrite_u24lambda_u241.length));
         } catch (NoSuchFileException var10) {
            throw new FileNotFoundException("no such file: " + file);
         }

         Intrinsics.checkNotNull(channel);
         return new NioFileSystemFileHandle(true, channel);
      }
   }

   @NotNull
   public Source source(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");

      try {
         java.nio.file.Path var10000 = this.resolve(file);
         OpenOption[] var10001 = new OpenOption[0];
         InputStream var4 = Files.newInputStream(var10000, (OpenOption[])Arrays.copyOf(var10001, var10001.length));
         Intrinsics.checkNotNullExpressionValue(var4, "newInputStream(...)");
         return Okio.source(var4);
      } catch (NoSuchFileException var3) {
         throw new FileNotFoundException("no such file: " + file);
      }
   }

   @NotNull
   public Sink sink(@NotNull Path file, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(file, "file");
      List $this$sink_u24lambda_u240 = CollectionsKt.createListBuilder();
      int thisCollection$iv = 0;
      if (mustCreate) {
         $this$sink_u24lambda_u240.add(StandardOpenOption.CREATE_NEW);
      }

      List openOptions = CollectionsKt.build($this$sink_u24lambda_u240);

      try {
         java.nio.file.Path $this$sink_u24lambda_u240 = this.resolve(file);
         Collection $this$toTypedArray$iv = (Collection)openOptions;
         int $i$f$toTypedArray = 0;
         StandardOpenOption[] $this$sink_u24lambda_u240 = (StandardOpenOption[])$this$toTypedArray$iv.toArray(new StandardOpenOption[0]);
         OpenOption[] e = (OpenOption[])Arrays.copyOf($this$sink_u24lambda_u240, $this$sink_u24lambda_u240.length);
         OutputStream var10000 = Files.newOutputStream($this$sink_u24lambda_u240, (OpenOption[])Arrays.copyOf(e, e.length));
         Intrinsics.checkNotNullExpressionValue(var10000, "newOutputStream(...)");
         return Okio.sink(var10000);
      } catch (NoSuchFileException var9) {
         throw new FileNotFoundException("no such file: " + file);
      }
   }

   @NotNull
   public Sink appendingSink(@NotNull Path file, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      List $this$appendingSink_u24lambda_u240 = CollectionsKt.createListBuilder();
      int thisCollection$iv = 0;
      $this$appendingSink_u24lambda_u240.add(StandardOpenOption.APPEND);
      if (!mustExist) {
         $this$appendingSink_u24lambda_u240.add(StandardOpenOption.CREATE);
      }

      List openOptions = CollectionsKt.build($this$appendingSink_u24lambda_u240);
      java.nio.file.Path $this$appendingSink_u24lambda_u240 = this.resolve(file);
      Collection $this$toTypedArray$iv = (Collection)openOptions;
      int $i$f$toTypedArray = 0;
      StandardOpenOption[] $this$appendingSink_u24lambda_u240 = (StandardOpenOption[])$this$toTypedArray$iv.toArray(new StandardOpenOption[0]);
      OpenOption[] var10 = (OpenOption[])Arrays.copyOf($this$appendingSink_u24lambda_u240, $this$appendingSink_u24lambda_u240.length);
      OutputStream var10000 = Files.newOutputStream($this$appendingSink_u24lambda_u240, (OpenOption[])Arrays.copyOf(var10, var10.length));
      Intrinsics.checkNotNullExpressionValue(var10000, "newOutputStream(...)");
      return Okio.sink(var10000);
   }

   public void createDirectory(@NotNull Path dir, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      FileMetadata var10000 = this.metadataOrNull(dir);
      boolean alreadyExist = var10000 != null ? var10000.isDirectory() : false;
      if (alreadyExist && mustCreate) {
         throw new IOException(dir + " already exists.");
      } else {
         try {
            java.nio.file.Path var6 = this.resolve(dir);
            FileAttribute[] var10001 = new FileAttribute[0];
            Intrinsics.checkNotNullExpressionValue(Files.createDirectory(var6, (FileAttribute[])Arrays.copyOf(var10001, var10001.length)), "createDirectory(...)");
         } catch (IOException e) {
            if (!alreadyExist) {
               throw new IOException("failed to create directory: " + dir, (Throwable)e);
            }
         }
      }
   }

   public void atomicMove(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");

      try {
         java.nio.file.Path var3 = this.resolve(source);
         java.nio.file.Path var4 = this.resolve(target);
         CopyOption[] var5 = new CopyOption[]{StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING};
         Intrinsics.checkNotNullExpressionValue(Files.move(var3, var4, (CopyOption[])Arrays.copyOf(var5, var5.length)), "move(...)");
      } catch (NoSuchFileException e) {
         throw new FileNotFoundException(e.getMessage());
      } catch (UnsupportedOperationException var7) {
         throw new IOException("atomic move not supported");
      }
   }

   public void delete(@NotNull Path path, boolean mustExist) {
      Intrinsics.checkNotNullParameter(path, "path");
      if (Thread.interrupted()) {
         throw new InterruptedIOException("interrupted");
      } else {
         java.nio.file.Path nioPath = this.resolve(path);

         try {
            Files.delete(nioPath);
         } catch (NoSuchFileException var5) {
            if (mustExist) {
               throw new FileNotFoundException("no such file: " + path);
            }
         } catch (IOException var6) {
            LinkOption[] var10001 = new LinkOption[0];
            if (Files.exists(nioPath, (LinkOption[])Arrays.copyOf(var10001, var10001.length))) {
               throw new IOException("failed to delete " + path);
            }
         }

      }
   }

   public void createSymlink(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      java.nio.file.Path var10000 = this.resolve(source);
      java.nio.file.Path var10001 = this.resolve(target);
      FileAttribute[] var10002 = new FileAttribute[0];
      Intrinsics.checkNotNullExpressionValue(Files.createSymbolicLink(var10000, var10001, (FileAttribute[])Arrays.copyOf(var10002, var10002.length)), "createSymbolicLink(...)");
   }

   public void close() {
      this.nioFileSystem.close();
   }

   @NotNull
   public String toString() {
      String var10000 = Reflection.getOrCreateKotlinClass(this.nioFileSystem.getClass()).getSimpleName();
      Intrinsics.checkNotNull(var10000);
      return var10000;
   }
}
