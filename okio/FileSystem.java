package okio;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import okio.internal.-FileSystem;
import okio.internal.ResourceFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u000b\b&\u0018\u0000 72\u00060\u0001j\u0002`\u0002:\u00017B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006H&J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\u0007\u001a\u00020\u0006J\u0012\u0010\n\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0007\u001a\u00020\u0006H&J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\u0006J\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H&J\u0018\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0006H&J \u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u00122\u0006\u0010\u000f\u001a\u00020\u00062\b\b\u0002\u0010\u0013\u001a\u00020\fH\u0016J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u00122\u0006\u0010\u000f\u001a\u00020\u0006J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0006H&J$\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00062\b\b\u0002\u0010\u0018\u001a\u00020\f2\b\b\u0002\u0010\u0019\u001a\u00020\fH&J\u000e\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0006J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0016\u001a\u00020\u0006H&JG\u0010\u001c\u001a\u0002H\u001d\"\u0004\b\u0000\u0010\u001d2\u0006\u0010\u0016\u001a\u00020\u00062\u0017\u0010\u001e\u001a\u0013\u0012\u0004\u0012\u00020 \u0012\u0004\u0012\u0002H\u001d0\u001f¢\u0006\u0002\b!H\u0087\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0002 \u0001¢\u0006\u0004\b\"\u0010#J\u001a\u0010$\u001a\u00020%2\u0006\u0010\u0016\u001a\u00020\u00062\b\b\u0002\u0010\u0018\u001a\u00020\fH&J\u000e\u0010$\u001a\u00020%2\u0006\u0010\u0016\u001a\u00020\u0006JQ\u0010&\u001a\u0002H\u001d\"\u0004\b\u0000\u0010\u001d2\u0006\u0010\u0016\u001a\u00020\u00062\b\b\u0002\u0010\u0018\u001a\u00020\f2\u0017\u0010'\u001a\u0013\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u0002H\u001d0\u001f¢\u0006\u0002\b!H\u0087\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0003 \u0001¢\u0006\u0004\b)\u0010*J\u001a\u0010+\u001a\u00020%2\u0006\u0010\u0016\u001a\u00020\u00062\b\b\u0002\u0010\u0019\u001a\u00020\fH&J\u000e\u0010+\u001a\u00020%2\u0006\u0010\u0016\u001a\u00020\u0006J\u001a\u0010,\u001a\u00020-2\u0006\u0010\u000f\u001a\u00020\u00062\b\b\u0002\u0010\u0018\u001a\u00020\fH&J\u000e\u0010,\u001a\u00020-2\u0006\u0010\u000f\u001a\u00020\u0006J\u0018\u0010.\u001a\u00020-2\u0006\u0010\u000f\u001a\u00020\u00062\b\b\u0002\u0010\u0018\u001a\u00020\fJ\u000e\u0010.\u001a\u00020-2\u0006\u0010\u000f\u001a\u00020\u0006J\u0018\u0010/\u001a\u00020-2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u00100\u001a\u00020\u0006H&J\u0018\u00101\u001a\u00020-2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u00100\u001a\u00020\u0006H\u0016J\u001a\u00102\u001a\u00020-2\u0006\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\u0019\u001a\u00020\fH&J\u000e\u00102\u001a\u00020-2\u0006\u0010\u0007\u001a\u00020\u0006J\u001a\u00103\u001a\u00020-2\u0006\u00104\u001a\u00020\u00062\b\b\u0002\u0010\u0019\u001a\u00020\fH\u0016J\u000e\u00103\u001a\u00020-2\u0006\u00104\u001a\u00020\u0006J\u0018\u00105\u001a\u00020-2\u0006\u0010\u001a\u001a\u00020\u00062\u0006\u00100\u001a\u00020\u0006H&J\b\u00106\u001a\u00020-H\u0016\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u00068"},
   d2 = {"Lokio/FileSystem;", "Ljava/io/Closeable;", "Lokio/Closeable;", "<init>", "()V", "canonicalize", "Lokio/Path;", "path", "metadata", "Lokio/FileMetadata;", "metadataOrNull", "exists", "", "list", "", "dir", "listOrNull", "listRecursively", "Lkotlin/sequences/Sequence;", "followSymlinks", "openReadOnly", "Lokio/FileHandle;", "file", "openReadWrite", "mustCreate", "mustExist", "source", "Lokio/Source;", "read", "T", "readerAction", "Lkotlin/Function1;", "Lokio/BufferedSource;", "Lkotlin/ExtensionFunctionType;", "-read", "(Lokio/Path;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "sink", "Lokio/Sink;", "write", "writerAction", "Lokio/BufferedSink;", "-write", "(Lokio/Path;ZLkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "appendingSink", "createDirectory", "", "createDirectories", "atomicMove", "target", "copy", "delete", "deleteRecursively", "fileOrDirectory", "createSymlink", "close", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileSystem.kt\nokio/FileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,191:1\n58#2,22:192\n58#2,22:214\n*S KotlinDebug\n*F\n+ 1 FileSystem.kt\nokio/FileSystem\n*L\n73#1:192,22\n95#1:214,22\n*E\n"})
public abstract class FileSystem implements Closeable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   @NotNull
   public static final FileSystem SYSTEM;
   @JvmField
   @NotNull
   public static final Path SYSTEM_TEMPORARY_DIRECTORY;
   @JvmField
   @NotNull
   public static final FileSystem RESOURCES;

   @NotNull
   public abstract Path canonicalize(@NotNull Path var1) throws IOException;

   @NotNull
   public final FileMetadata metadata(@NotNull Path path) throws IOException {
      Intrinsics.checkNotNullParameter(path, "path");
      return -FileSystem.commonMetadata(this, path);
   }

   @Nullable
   public abstract FileMetadata metadataOrNull(@NotNull Path var1) throws IOException;

   public final boolean exists(@NotNull Path path) throws IOException {
      Intrinsics.checkNotNullParameter(path, "path");
      return -FileSystem.commonExists(this, path);
   }

   @NotNull
   public abstract List<Path> list(@NotNull Path var1) throws IOException;

   @Nullable
   public abstract List<Path> listOrNull(@NotNull Path var1);

   @NotNull
   public Sequence<Path> listRecursively(@NotNull Path dir, boolean followSymlinks) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      return -FileSystem.commonListRecursively(this, dir, followSymlinks);
   }

   // $FF: synthetic method
   public static Sequence listRecursively$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: listRecursively");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         return var0.listRecursively(var1, var2);
      }
   }

   @NotNull
   public final Sequence<Path> listRecursively(@NotNull Path dir) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      return this.listRecursively(dir, false);
   }

   @NotNull
   public abstract FileHandle openReadOnly(@NotNull Path var1) throws IOException;

   @NotNull
   public abstract FileHandle openReadWrite(@NotNull Path var1, boolean var2, boolean var3) throws IOException;

   // $FF: synthetic method
   public static FileHandle openReadWrite$default(FileSystem var0, Path var1, boolean var2, boolean var3, int var4, Object var5) throws IOException {
      if (var5 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: openReadWrite");
      } else {
         if ((var4 & 2) != 0) {
            var2 = false;
         }

         if ((var4 & 4) != 0) {
            var3 = false;
         }

         return var0.openReadWrite(var1, var2, var3);
      }
   }

   @NotNull
   public final FileHandle openReadWrite(@NotNull Path file) throws IOException {
      Intrinsics.checkNotNullParameter(file, "file");
      return this.openReadWrite(file, false, false);
   }

   @NotNull
   public abstract Source source(@NotNull Path var1) throws IOException;

   @JvmName(
      name = "-read"
   )
   public final <T> T _read/* $FF was: -read*/(@NotNull Path file, @NotNull Function1<? super BufferedSource, ? extends T> readerAction) throws IOException {
      Intrinsics.checkNotNullParameter(file, "file");
      Intrinsics.checkNotNullParameter(readerAction, "readerAction");
      int var3 = 0;
      Closeable $this$use$iv = Okio.buffer(this.source(file));
      int $i$f$use = 0;
      Throwable thrown$iv = null;
      boolean var17 = false;

      Object result$iv;
      label140: {
         label141: {
            try {
               var17 = true;
               BufferedSource it = (BufferedSource)$this$use$iv;
               int result$iv = false;
               result$iv = readerAction.invoke(it);
               var17 = false;
               break label141;
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
               result$iv = null;
               var17 = false;
            } finally {
               if (var17) {
                  InlineMarker.finallyStart(1);

                  try {
                     if ($this$use$iv != null) {
                        $this$use$iv.close();
                     }
                  } catch (Throwable t$iv) {
                     if (thrown$iv != null) {
                        ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                     }
                  }

                  InlineMarker.finallyEnd(1);
               }
            }

            InlineMarker.finallyStart(1);

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

            InlineMarker.finallyEnd(1);
            break label140;
         }

         InlineMarker.finallyStart(1);

         try {
            if ($this$use$iv != null) {
               $this$use$iv.close();
            }
         } catch (Throwable t$iv) {
            thrown$iv = t$iv;
         }

         InlineMarker.finallyEnd(1);
      }

      if (thrown$iv != null) {
         throw thrown$iv;
      } else {
         return (T)result$iv;
      }
   }

   @NotNull
   public abstract Sink sink(@NotNull Path var1, boolean var2) throws IOException;

   // $FF: synthetic method
   public static Sink sink$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: sink");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         return var0.sink(var1, var2);
      }
   }

   @NotNull
   public final Sink sink(@NotNull Path file) throws IOException {
      Intrinsics.checkNotNullParameter(file, "file");
      return this.sink(file, false);
   }

   @JvmName(
      name = "-write"
   )
   public final <T> T _write/* $FF was: -write*/(@NotNull Path file, boolean mustCreate, @NotNull Function1<? super BufferedSink, ? extends T> writerAction) throws IOException {
      Intrinsics.checkNotNullParameter(file, "file");
      Intrinsics.checkNotNullParameter(writerAction, "writerAction");
      int var4 = 0;
      Closeable $this$use$iv = Okio.buffer(this.sink(file, mustCreate));
      int $i$f$use = 0;
      Throwable thrown$iv = null;
      boolean var18 = false;

      Object result$iv;
      label140: {
         label141: {
            try {
               var18 = true;
               BufferedSink it = (BufferedSink)$this$use$iv;
               int result$iv = false;
               result$iv = writerAction.invoke(it);
               var18 = false;
               break label141;
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
               result$iv = null;
               var18 = false;
            } finally {
               if (var18) {
                  InlineMarker.finallyStart(1);

                  try {
                     if ($this$use$iv != null) {
                        $this$use$iv.close();
                     }
                  } catch (Throwable t$iv) {
                     if (thrown$iv != null) {
                        ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                     }
                  }

                  InlineMarker.finallyEnd(1);
               }
            }

            InlineMarker.finallyStart(1);

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

            InlineMarker.finallyEnd(1);
            break label140;
         }

         InlineMarker.finallyStart(1);

         try {
            if ($this$use$iv != null) {
               $this$use$iv.close();
            }
         } catch (Throwable t$iv) {
            thrown$iv = t$iv;
         }

         InlineMarker.finallyEnd(1);
      }

      if (thrown$iv != null) {
         throw thrown$iv;
      } else {
         return (T)result$iv;
      }
   }

   // $FF: synthetic method
   public static Object _write$default/* $FF was: -write$default*/(FileSystem $this, Path file, boolean mustCreate, Function1 writerAction, int var4, Object $this$use$iv) throws IOException {
      if ($this$use$iv != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: write");
      } else {
         if ((var4 & 2) != 0) {
            mustCreate = false;
         }

         Intrinsics.checkNotNullParameter(file, "file");
         Intrinsics.checkNotNullParameter(writerAction, "writerAction");
         var4 = 0;
         Closeable $this$use$iv = Okio.buffer($this.sink(file, mustCreate));
         int $i$f$use = 0;
         Throwable thrown$iv = null;
         boolean var18 = false;

         Object result$iv;
         label155: {
            label156: {
               try {
                  var18 = true;
                  BufferedSink it = (BufferedSink)$this$use$iv;
                  int result$iv = false;
                  result$iv = writerAction.invoke(it);
                  var18 = false;
                  break label156;
               } catch (Throwable t$iv) {
                  thrown$iv = t$iv;
                  result$iv = null;
                  var18 = false;
               } finally {
                  if (var18) {
                     InlineMarker.finallyStart(1);

                     try {
                        if ($this$use$iv != null) {
                           $this$use$iv.close();
                        }
                     } catch (Throwable t$iv) {
                        if (thrown$iv != null) {
                           ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                        }
                     }

                     InlineMarker.finallyEnd(1);
                  }
               }

               InlineMarker.finallyStart(1);

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

               InlineMarker.finallyEnd(1);
               break label155;
            }

            InlineMarker.finallyStart(1);

            try {
               if ($this$use$iv != null) {
                  $this$use$iv.close();
               }
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
            }

            InlineMarker.finallyEnd(1);
         }

         if (thrown$iv != null) {
            throw thrown$iv;
         } else {
            return result$iv;
         }
      }
   }

   @NotNull
   public abstract Sink appendingSink(@NotNull Path var1, boolean var2) throws IOException;

   // $FF: synthetic method
   public static Sink appendingSink$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: appendingSink");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         return var0.appendingSink(var1, var2);
      }
   }

   @NotNull
   public final Sink appendingSink(@NotNull Path file) throws IOException {
      Intrinsics.checkNotNullParameter(file, "file");
      return this.appendingSink(file, false);
   }

   public abstract void createDirectory(@NotNull Path var1, boolean var2) throws IOException;

   // $FF: synthetic method
   public static void createDirectory$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: createDirectory");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         var0.createDirectory(var1, var2);
      }
   }

   public final void createDirectory(@NotNull Path dir) throws IOException {
      Intrinsics.checkNotNullParameter(dir, "dir");
      this.createDirectory(dir, false);
   }

   public final void createDirectories(@NotNull Path dir, boolean mustCreate) throws IOException {
      Intrinsics.checkNotNullParameter(dir, "dir");
      -FileSystem.commonCreateDirectories(this, dir, mustCreate);
   }

   // $FF: synthetic method
   public static void createDirectories$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: createDirectories");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         var0.createDirectories(var1, var2);
      }
   }

   public final void createDirectories(@NotNull Path dir) throws IOException {
      Intrinsics.checkNotNullParameter(dir, "dir");
      this.createDirectories(dir, false);
   }

   public abstract void atomicMove(@NotNull Path var1, @NotNull Path var2) throws IOException;

   public void copy(@NotNull Path source, @NotNull Path target) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      -FileSystem.commonCopy(this, source, target);
   }

   public abstract void delete(@NotNull Path var1, boolean var2) throws IOException;

   // $FF: synthetic method
   public static void delete$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: delete");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         var0.delete(var1, var2);
      }
   }

   public final void delete(@NotNull Path path) throws IOException {
      Intrinsics.checkNotNullParameter(path, "path");
      this.delete(path, false);
   }

   public void deleteRecursively(@NotNull Path fileOrDirectory, boolean mustExist) throws IOException {
      Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
      -FileSystem.commonDeleteRecursively(this, fileOrDirectory, mustExist);
   }

   // $FF: synthetic method
   public static void deleteRecursively$default(FileSystem var0, Path var1, boolean var2, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: deleteRecursively");
      } else {
         if ((var3 & 2) != 0) {
            var2 = false;
         }

         var0.deleteRecursively(var1, var2);
      }
   }

   public final void deleteRecursively(@NotNull Path fileOrDirectory) throws IOException {
      Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
      this.deleteRecursively(fileOrDirectory, false);
   }

   public abstract void createSymlink(@NotNull Path var1, @NotNull Path var2) throws IOException;

   public void close() throws IOException {
   }

   @JvmStatic
   @JvmName(
      name = "get"
   )
   @NotNull
   public static final FileSystem get(@NotNull java.nio.file.FileSystem $this$get) {
      return Companion.get($this$get);
   }

   static {
      Companion $this$SYSTEM_u24lambda_u240 = Companion;
      int var1 = 0;

      JvmSystemFileSystem var10000;
      try {
         Class.forName("java.nio.file.Files");
         var10000 = new NioSystemFileSystem();
      } catch (ClassNotFoundException var3) {
         var10000 = new JvmSystemFileSystem();
      }

      SYSTEM = var10000;
      Path.Companion var4 = Path.Companion;
      String var10001 = System.getProperty("java.io.tmpdir");
      Intrinsics.checkNotNullExpressionValue(var10001, "getProperty(...)");
      SYSTEM_TEMPORARY_DIRECTORY = Path.Companion.get$default(var4, (String)var10001, false, 1, (Object)null);
      ClassLoader var10002 = ResourceFileSystem.class.getClassLoader();
      Intrinsics.checkNotNullExpressionValue(var10002, "getClassLoader(...)");
      RESOURCES = new ResourceFileSystem(var10002, false, (FileSystem)null, 4, (DefaultConstructorMarker)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\t\u001a\u00020\u0005*\u00020\nH\u0007¢\u0006\u0002\b\u000bR\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\f"},
      d2 = {"Lokio/FileSystem$Companion;", "", "<init>", "()V", "SYSTEM", "Lokio/FileSystem;", "SYSTEM_TEMPORARY_DIRECTORY", "Lokio/Path;", "RESOURCES", "asOkioFileSystem", "Ljava/nio/file/FileSystem;", "get", "okio"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "get"
      )
      @NotNull
      public final FileSystem get(@NotNull java.nio.file.FileSystem $this$asOkioFileSystem) {
         Intrinsics.checkNotNullParameter($this$asOkioFileSystem, "<this>");
         return new NioFileSystemWrappingFileSystem($this$asOkioFileSystem);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
