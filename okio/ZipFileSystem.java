package okio;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okio.internal.FixedLengthSource;
import okio.internal.ZipEntry;
import okio.internal.ZipFilesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0000\u0018\u0000 (2\u00020\u0001:\u0001(B7\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0001\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00070\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u0003H\u0016J\u0010\u0010\u000e\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u0003H\u0002J\u0012\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\r\u001a\u00020\u0003H\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0003H\u0016J \u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0016J\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00030\u00192\u0006\u0010\u001a\u001a\u00020\u0003H\u0016J\u0018\u0010\u001b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00192\u0006\u0010\u001a\u001a\u00020\u0003H\u0016J \u0010\u0018\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00192\u0006\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u0016H\u0002J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0013\u001a\u00020\u0003H\u0016J\u0018\u0010\u001f\u001a\u00020 2\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u0018\u0010!\u001a\u00020 2\u0006\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0016H\u0016J\u0018\u0010\"\u001a\u00020#2\u0006\u0010\u001a\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u0018\u0010$\u001a\u00020#2\u0006\u0010\u001d\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\u0003H\u0016J\u0018\u0010&\u001a\u00020#2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0016H\u0016J\u0018\u0010'\u001a\u00020#2\u0006\u0010\u001d\u001a\u00020\u00032\u0006\u0010%\u001a\u00020\u0003H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006)"},
   d2 = {"Lokio/ZipFileSystem;", "Lokio/FileSystem;", "zipPath", "Lokio/Path;", "fileSystem", "entries", "", "Lokio/internal/ZipEntry;", "comment", "", "<init>", "(Lokio/Path;Lokio/FileSystem;Ljava/util/Map;Ljava/lang/String;)V", "canonicalize", "path", "canonicalizeInternal", "metadataOrNull", "Lokio/FileMetadata;", "openReadOnly", "Lokio/FileHandle;", "file", "openReadWrite", "mustCreate", "", "mustExist", "list", "", "dir", "listOrNull", "throwOnFailure", "source", "Lokio/Source;", "sink", "Lokio/Sink;", "appendingSink", "createDirectory", "", "atomicMove", "target", "delete", "createSymlink", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nZipFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipFileSystem.kt\nokio/ZipFileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,142:1\n58#2,4:143\n58#2,22:147\n66#2,10:169\n62#2,3:179\n77#2,3:182\n58#2,22:185\n*S KotlinDebug\n*F\n+ 1 ZipFileSystem.kt\nokio/ZipFileSystem\n*L\n55#1:143,4\n56#1:147,22\n55#1:169,10\n55#1:179,3\n55#1:182,3\n99#1:185,22\n*E\n"})
public final class ZipFileSystem extends FileSystem {
   @NotNull
   private static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Path zipPath;
   @NotNull
   private final FileSystem fileSystem;
   @NotNull
   private final Map<Path, ZipEntry> entries;
   @Nullable
   private final String comment;
   @NotNull
   private static final Path ROOT;

   public ZipFileSystem(@NotNull Path zipPath, @NotNull FileSystem fileSystem, @NotNull Map<Path, ZipEntry> entries, @Nullable String comment) {
      Intrinsics.checkNotNullParameter(zipPath, "zipPath");
      Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
      Intrinsics.checkNotNullParameter(entries, "entries");
      super();
      this.zipPath = zipPath;
      this.fileSystem = fileSystem;
      this.entries = entries;
      this.comment = comment;
   }

   @NotNull
   public Path canonicalize(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      Path canonical = this.canonicalizeInternal(path);
      if (!this.entries.containsKey(canonical)) {
         throw new FileNotFoundException(String.valueOf(path));
      } else {
         return canonical;
      }
   }

   private final Path canonicalizeInternal(Path path) {
      return ROOT.resolve(path, true);
   }

   @Nullable
   public FileMetadata metadataOrNull(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      Path canonicalPath = this.canonicalizeInternal(path);
      ZipEntry var10000 = (ZipEntry)this.entries.get(canonicalPath);
      if (var10000 == null) {
         return null;
      } else {
         ZipEntry centralDirectoryEntry = var10000;
         if (centralDirectoryEntry.getOffset() != -1L) {
            Closeable $this$use$iv = this.fileSystem.openReadOnly(this.zipPath);
            int $i$f$use = 0;
            Throwable thrown$iv = null;
            boolean var28 = false;

            ZipEntry result$iv;
            label405: {
               label404: {
                  try {
                     var28 = true;
                     FileHandle fileHandle = (FileHandle)$this$use$iv;
                     int result$iv = false;
                     Closeable $this$use$iv = Okio.buffer(fileHandle.source(centralDirectoryEntry.getOffset()));
                     int $i$f$use = false;
                     Throwable thrown$iv = null;
                     boolean var39 = false;

                     ZipEntry result$iv;
                     label392: {
                        label391: {
                           try {
                              var39 = true;
                              BufferedSource source = (BufferedSource)$this$use$iv;
                              int result$iv = false;
                              result$iv = ZipFilesKt.readLocalHeader(source, centralDirectoryEntry);
                              var39 = false;
                              break label391;
                           } catch (Throwable t$iv) {
                              thrown$iv = t$iv;
                              result$iv = null;
                              var39 = false;
                           } finally {
                              if (var39) {
                                 try {
                                    if ($this$use$iv != null) {
                                       $this$use$iv.close();
                                    }
                                 } catch (Throwable t$iv) {
                                    if (thrown$iv != null) {
                                       ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                                    }
                                 }

                              }
                           }

                           try {
                              if ($this$use$iv != null) {
                                 $this$use$iv.close();
                              }
                           } catch (Throwable t$iv) {
                              if (thrown$iv == null) {
                                 thrown$iv = t$iv;
                                 break label392;
                              }

                              ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                           }
                           break label392;
                        }

                        try {
                           if ($this$use$iv != null) {
                              $this$use$iv.close();
                           }
                        } catch (Throwable t$iv) {
                           thrown$iv = t$iv;
                        }
                     }

                     if (thrown$iv != null) {
                        throw thrown$iv;
                     }

                     result$iv = (ZipEntry)(result$iv);
                     var28 = false;
                     break label404;
                  } catch (Throwable t$iv) {
                     thrown$iv = t$iv;
                     result$iv = null;
                     var28 = false;
                  } finally {
                     if (var28) {
                        try {
                           if ($this$use$iv != null) {
                              $this$use$iv.close();
                           }
                        } catch (Throwable t$iv) {
                           if (thrown$iv != null) {
                              ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                           }
                        }

                     }
                  }

                  try {
                     if ($this$use$iv != null) {
                        $this$use$iv.close();
                     }
                  } catch (Throwable t$iv) {
                     if (thrown$iv == null) {
                        thrown$iv = t$iv;
                     } else {
                        ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                     }
                  }
                  break label405;
               }

               try {
                  if ($this$use$iv != null) {
                     $this$use$iv.close();
                  }
               } catch (Throwable t$iv) {
                  thrown$iv = t$iv;
               }
            }

            if (thrown$iv != null) {
               throw thrown$iv;
            }

            var10000 = (ZipEntry)(result$iv);
         } else {
            var10000 = centralDirectoryEntry;
         }

         ZipEntry fullEntry = var10000;
         return new FileMetadata(!fullEntry.isDirectory(), fullEntry.isDirectory(), (Path)null, fullEntry.isDirectory() ? null : fullEntry.getSize(), fullEntry.getCreatedAtMillis$okio(), fullEntry.getLastModifiedAtMillis$okio(), fullEntry.getLastAccessedAtMillis$okio(), (Map)null, 128, (DefaultConstructorMarker)null);
      }
   }

   @NotNull
   public FileHandle openReadOnly(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new UnsupportedOperationException("not implemented yet!");
   }

   @NotNull
   public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new IOException("zip entries are not writable");
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
      Path canonicalDir = this.canonicalizeInternal(dir);
      ZipEntry var10000 = (ZipEntry)this.entries.get(canonicalDir);
      if (var10000 == null) {
         if (throwOnFailure) {
            throw new IOException("not a directory: " + dir);
         } else {
            return null;
         }
      } else {
         ZipEntry entry = var10000;
         return CollectionsKt.toList((Iterable)entry.getChildren());
      }
   }

   @NotNull
   public Source source(@NotNull Path file) throws IOException {
      Intrinsics.checkNotNullParameter(file, "file");
      Path canonicalPath = this.canonicalizeInternal(file);
      ZipEntry var10000 = (ZipEntry)this.entries.get(canonicalPath);
      if (var10000 == null) {
         throw new FileNotFoundException("no such file: " + file);
      } else {
         ZipEntry entry = var10000;
         Closeable $this$use$iv = this.fileSystem.openReadOnly(this.zipPath);
         int $i$f$use = 0;
         Throwable thrown$iv = null;
         boolean var18 = false;

         BufferedSource result$iv;
         label132: {
            label131: {
               try {
                  var18 = true;
                  FileHandle fileHandle = (FileHandle)$this$use$iv;
                  int result$iv = false;
                  result$iv = Okio.buffer(fileHandle.source(entry.getOffset()));
                  var18 = false;
                  break label131;
               } catch (Throwable t$iv) {
                  thrown$iv = t$iv;
                  result$iv = null;
                  var18 = false;
               } finally {
                  if (var18) {
                     try {
                        if ($this$use$iv != null) {
                           $this$use$iv.close();
                        }
                     } catch (Throwable t$iv) {
                        if (thrown$iv != null) {
                           ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                        }
                     }

                  }
               }

               try {
                  if ($this$use$iv != null) {
                     $this$use$iv.close();
                  }
               } catch (Throwable t$iv) {
                  if (thrown$iv == null) {
                     thrown$iv = t$iv;
                  } else {
                     ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                  }
               }
               break label132;
            }

            try {
               if ($this$use$iv != null) {
                  $this$use$iv.close();
               }
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
            }
         }

         if (thrown$iv != null) {
            throw thrown$iv;
         } else {
            BufferedSource source = (BufferedSource)(result$iv);
            ZipFilesKt.skipLocalHeader(source);
            Source var29;
            if (entry.getCompressionMethod() == 0) {
               var29 = new FixedLengthSource(source, entry.getSize(), true);
            } else {
               InflaterSource inflaterSource = new InflaterSource(new FixedLengthSource(source, entry.getCompressedSize(), true), new Inflater(true));
               var29 = new FixedLengthSource(inflaterSource, entry.getSize(), false);
            }

            return var29;
         }
      }
   }

   @NotNull
   public Sink sink(@NotNull Path file, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new IOException("zip file systems are read-only");
   }

   @NotNull
   public Sink appendingSink(@NotNull Path file, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new IOException("zip file systems are read-only");
   }

   public void createDirectory(@NotNull Path dir, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      throw new IOException("zip file systems are read-only");
   }

   public void atomicMove(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      throw new IOException("zip file systems are read-only");
   }

   public void delete(@NotNull Path path, boolean mustExist) {
      Intrinsics.checkNotNullParameter(path, "path");
      throw new IOException("zip file systems are read-only");
   }

   public void createSymlink(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      throw new IOException("zip file systems are read-only");
   }

   static {
      ROOT = Path.Companion.get$default(Path.Companion, (String)"/", false, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0082\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lokio/ZipFileSystem$Companion;", "", "<init>", "()V", "ROOT", "Lokio/Path;", "getROOT", "()Lokio/Path;", "okio"}
   )
   private static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Path getROOT() {
         return ZipFileSystem.ROOT;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
