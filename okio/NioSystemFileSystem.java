package okio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\b\u0010\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\u0004J\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b*\u00020\fH\u0002¢\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0016J\u0018\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0007H\u0016J\b\u0010\u0013\u001a\u00020\u0014H\u0016¨\u0006\u0015"},
   d2 = {"Lokio/NioSystemFileSystem;", "Lokio/JvmSystemFileSystem;", "<init>", "()V", "metadataOrNull", "Lokio/FileMetadata;", "path", "Lokio/Path;", "nioPath", "Ljava/nio/file/Path;", "zeroToNull", "", "Ljava/nio/file/attribute/FileTime;", "(Ljava/nio/file/attribute/FileTime;)Ljava/lang/Long;", "atomicMove", "", "source", "target", "createSymlink", "toString", "", "okio"}
)
@SourceDebugExtension({"SMAP\nNioSystemFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NioSystemFileSystem.kt\nokio/NioSystemFileSystem\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,92:1\n1#2:93\n*E\n"})
public class NioSystemFileSystem extends JvmSystemFileSystem {
   @Nullable
   public FileMetadata metadataOrNull(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      return this.metadataOrNull(path.toNioPath());
   }

   @Nullable
   protected final FileMetadata metadataOrNull(@NotNull java.nio.file.Path nioPath) {
      Intrinsics.checkNotNullParameter(nioPath, "nioPath");

      BasicFileAttributes var7;
      try {
         LinkOption[] var3 = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
         var7 = Files.readAttributes(nioPath, BasicFileAttributes.class, var3);
      } catch (NoSuchFileException var5) {
         return null;
      } catch (FileSystemException var6) {
         return null;
      }

      BasicFileAttributes attributes = var7;
      java.nio.file.Path symlinkTarget = var7.isSymbolicLink() ? Files.readSymbolicLink(nioPath) : null;
      boolean var10002 = attributes.isRegularFile();
      boolean var10003 = attributes.isDirectory();
      Path var10004 = symlinkTarget != null ? Path.Companion.get$default(Path.Companion, (java.nio.file.Path)symlinkTarget, false, 1, (Object)null) : null;
      Long var10005 = attributes.size();
      FileTime var10006 = attributes.creationTime();
      Long var9 = var10006 != null ? this.zeroToNull(var10006) : null;
      FileTime var10007 = attributes.lastModifiedTime();
      Long var10 = var10007 != null ? this.zeroToNull(var10007) : null;
      FileTime var10008 = attributes.lastAccessTime();
      return new FileMetadata(var10002, var10003, var10004, var10005, var9, var10, var10008 != null ? this.zeroToNull(var10008) : null, (Map)null, 128, (DefaultConstructorMarker)null);
   }

   private final Long zeroToNull(FileTime $this$zeroToNull) {
      Long var2 = $this$zeroToNull.toMillis();
      long it = ((Number)var2).longValue();
      int var5 = 0;
      return it != 0L ? var2 : null;
   }

   public void atomicMove(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");

      try {
         java.nio.file.Path var10000 = source.toNioPath();
         java.nio.file.Path var10001 = target.toNioPath();
         CopyOption[] var3 = new CopyOption[]{StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING};
         Files.move(var10000, var10001, var3);
      } catch (NoSuchFileException e) {
         throw new FileNotFoundException(e.getMessage());
      } catch (UnsupportedOperationException var6) {
         throw new IOException("atomic move not supported");
      }
   }

   public void createSymlink(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      Files.createSymbolicLink(source.toNioPath(), target.toNioPath());
   }

   @NotNull
   public String toString() {
      return "NioSystemFileSystem";
   }
}
