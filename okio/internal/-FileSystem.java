package okio.internal;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;
import okio.BufferedSink;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u00008\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u0014\u0010\u0005\u001a\u00020\u0006*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u001c\u0010\u0007\u001a\u00020\b*\u00020\u00022\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0006H\u0000\u001a\u001c\u0010\u000b\u001a\u00020\b*\u00020\u00022\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u0004H\u0000\u001a\u001c\u0010\u000e\u001a\u00020\b*\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0006H\u0000\u001a\"\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u0012*\u00020\u00022\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0006H\u0000\u001aF\u0010\u0014\u001a\u00020\b*\b\u0012\u0004\u0012\u00020\u00040\u00152\u0006\u0010\u0016\u001a\u00020\u00022\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u00182\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u0006H\u0080@¢\u0006\u0002\u0010\u001a\u001a\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u0004*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000¨\u0006\u001c"},
   d2 = {"commonMetadata", "Lokio/FileMetadata;", "Lokio/FileSystem;", "path", "Lokio/Path;", "commonExists", "", "commonCreateDirectories", "", "dir", "mustCreate", "commonCopy", "source", "target", "commonDeleteRecursively", "fileOrDirectory", "mustExist", "commonListRecursively", "Lkotlin/sequences/Sequence;", "followSymlinks", "collectRecursively", "Lkotlin/sequences/SequenceScope;", "fileSystem", "stack", "Lkotlin/collections/ArrayDeque;", "postorder", "(Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "symlinkTarget", "okio"}
)
@JvmName(
   name = "-FileSystem"
)
@SourceDebugExtension({"SMAP\nFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileSystem.kt\nokio/internal/-FileSystem\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,155:1\n58#2,4:156\n58#2,22:160\n66#2,10:182\n62#2,3:192\n77#2,3:195\n*S KotlinDebug\n*F\n+ 1 FileSystem.kt\nokio/internal/-FileSystem\n*L\n65#1:156,4\n66#1:160,22\n65#1:182,10\n65#1:192,3\n65#1:195,3\n*E\n"})
public final class -FileSystem {
   @NotNull
   public static final FileMetadata commonMetadata(@NotNull FileSystem $this$commonMetadata, @NotNull Path path) throws IOException {
      Intrinsics.checkNotNullParameter($this$commonMetadata, "<this>");
      Intrinsics.checkNotNullParameter(path, "path");
      return $this$commonMetadata.metadataOrNull(path);
   }

   public static final boolean commonExists(@NotNull FileSystem $this$commonExists, @NotNull Path path) throws IOException {
      Intrinsics.checkNotNullParameter($this$commonExists, "<this>");
      Intrinsics.checkNotNullParameter(path, "path");
      return $this$commonExists.metadataOrNull(path) != null;
   }

   public static final void commonCreateDirectories(@NotNull FileSystem $this$commonCreateDirectories, @NotNull Path dir, boolean mustCreate) throws IOException {
      Intrinsics.checkNotNullParameter($this$commonCreateDirectories, "<this>");
      Intrinsics.checkNotNullParameter(dir, "dir");
      ArrayDeque directories = new ArrayDeque();

      for(Path path = dir; path != null && !$this$commonCreateDirectories.exists(path); path = path.parent()) {
         directories.addFirst(path);
      }

      if (mustCreate && directories.isEmpty()) {
         throw new IOException(dir + " already exists.");
      } else {
         for(Path toCreate : directories) {
            FileSystem.createDirectory$default($this$commonCreateDirectories, toCreate, false, 2, (Object)null);
         }

      }
   }

   public static final void commonCopy(@NotNull FileSystem $this$commonCopy, @NotNull Path source, @NotNull Path target) throws IOException {
      Intrinsics.checkNotNullParameter($this$commonCopy, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      Closeable $this$use$iv = $this$commonCopy.source(source);
      int $i$f$use = 0;
      Throwable thrown$iv = null;
      boolean var27 = false;

      Long bytesIn;
      label343: {
         label342: {
            try {
               var27 = true;
               Source result$iv = (Source)$this$use$iv;
               int var7 = false;
               Closeable $this$use$iv = Okio.buffer(FileSystem.sink$default($this$commonCopy, target, false, 2, (Object)null));
               int $i$f$use = false;
               Throwable thrown$iv = null;
               boolean var38 = false;

               Long result$iv;
               label329: {
                  label328: {
                     try {
                        var38 = true;
                        BufferedSink bytesOut = (BufferedSink)$this$use$iv;
                        int result$iv = false;
                        result$iv = bytesOut.writeAll(result$iv);
                        var38 = false;
                        break label328;
                     } catch (Throwable t$iv) {
                        thrown$iv = t$iv;
                        result$iv = null;
                        var38 = false;
                     } finally {
                        if (var38) {
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
                           break label329;
                        }

                        ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                     }
                     break label329;
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

               bytesIn = ((Number)((Object)result$iv)).longValue();
               var27 = false;
               break label342;
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
               bytesIn = null;
               var27 = false;
            } finally {
               if (var27) {
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
            break label343;
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
         Object var10000 = bytesIn;
      }
   }

   public static final void commonDeleteRecursively(@NotNull final FileSystem $this$commonDeleteRecursively, @NotNull final Path fileOrDirectory, boolean mustExist) throws IOException {
      Intrinsics.checkNotNullParameter($this$commonDeleteRecursively, "<this>");
      Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
      Sequence sequence = SequencesKt.sequence(new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>((Continuation)null) {
         int label;
         // $FF: synthetic field
         private Object L$0;

         public final Object invokeSuspend(Object $result) {
            SequenceScope $this$sequence = (SequenceScope)this.L$0;
            Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure($result);
                  FileSystem var10001 = $this$commonDeleteRecursively;
                  ArrayDeque var10002 = new ArrayDeque();
                  Path var10003 = fileOrDirectory;
                  Continuation var10006 = this;
                  this.L$0 = SpillingKt.nullOutSpilledVariable($this$sequence);
                  this.label = 1;
                  if (-FileSystem.collectRecursively($this$sequence, var10001, var10002, var10003, false, true, var10006) == var3) {
                     return var3;
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure($result);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return Unit.INSTANCE;
         }

         public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
            Function2 var3 = new <anonymous constructor>($completion);
            var3.L$0 = value;
            return (Continuation)var3;
         }

         public final Object invoke(SequenceScope<? super Path> p1, Continuation<? super Unit> p2) {
            return ((<undefinedtype>)this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
         }
      });
      Iterator iterator = sequence.iterator();

      while(iterator.hasNext()) {
         Path toDelete = (Path)iterator.next();
         $this$commonDeleteRecursively.delete(toDelete, mustExist && !iterator.hasNext());
      }

   }

   @NotNull
   public static final Sequence<Path> commonListRecursively(@NotNull final FileSystem $this$commonListRecursively, @NotNull final Path dir, final boolean followSymlinks) throws IOException {
      Intrinsics.checkNotNullParameter($this$commonListRecursively, "<this>");
      Intrinsics.checkNotNullParameter(dir, "dir");
      return SequencesKt.sequence(new Function2<SequenceScope<? super Path>, Continuation<? super Unit>, Object>((Continuation)null) {
         Object L$1;
         Object L$2;
         Object L$3;
         int label;
         // $FF: synthetic field
         private Object L$0;

         public final Object invokeSuspend(Object $result) {
            SequenceScope $this$sequence = (SequenceScope)this.L$0;
            Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            ArrayDeque stack;
            Iterator var4;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure($result);
                  stack = new ArrayDeque();
                  stack.addLast(dir);
                  var4 = $this$commonListRecursively.list(dir).iterator();
                  break;
               case 1:
                  Path child = (Path)this.L$3;
                  var4 = (Iterator)this.L$2;
                  stack = (ArrayDeque)this.L$1;
                  ResultKt.throwOnFailure($result);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            while(var4.hasNext()) {
               Path child = (Path)var4.next();
               FileSystem var10001 = $this$commonListRecursively;
               boolean var10004 = followSymlinks;
               Continuation var10006 = this;
               this.L$0 = $this$sequence;
               this.L$1 = stack;
               this.L$2 = var4;
               this.L$3 = SpillingKt.nullOutSpilledVariable(child);
               this.label = 1;
               if (-FileSystem.collectRecursively($this$sequence, var10001, stack, child, var10004, false, var10006) == var6) {
                  return var6;
               }
            }

            return Unit.INSTANCE;
         }

         public final Continuation<Unit> create(Object value, Continuation<?> $completion) {
            Function2 var3 = new <anonymous constructor>($completion);
            var3.L$0 = value;
            return (Continuation)var3;
         }

         public final Object invoke(SequenceScope<? super Path> p1, Continuation<? super Unit> p2) {
            return ((<undefinedtype>)this.create(p1, p2)).invokeSuspend(Unit.INSTANCE);
         }
      });
   }

   @Nullable
   public static final Object collectRecursively(@NotNull SequenceScope<? super Path> $this$collectRecursively, @NotNull FileSystem fileSystem, @NotNull ArrayDeque<Path> stack, @NotNull Path path, boolean followSymlinks, boolean postorder, @NotNull Continuation<? super Unit> $completion) {
      Continuation $continuation;
      label687: {
         if ($completion instanceof <undefinedtype>) {
            $continuation = (<undefinedtype>)$completion;
            if (($continuation.label & Integer.MIN_VALUE) != 0) {
               $continuation.label -= Integer.MIN_VALUE;
               break label687;
            }
         }

         $continuation = new ContinuationImpl($completion) {
            Object L$0;
            Object L$1;
            Object L$2;
            Object L$3;
            Object L$4;
            Object L$5;
            Object L$6;
            Object L$7;
            boolean Z$0;
            boolean Z$1;
            int I$0;
            // $FF: synthetic field
            Object result;
            int label;

            @Nullable
            public final Object invokeSuspend(@NotNull Object $result) {
               this.result = $result;
               this.label |= Integer.MIN_VALUE;
               return -FileSystem.collectRecursively((SequenceScope)null, (FileSystem)null, (ArrayDeque)null, (Path)null, false, false, this);
            }
         };
      }

      Throwable var10000;
      label714: {
         Object var14;
         List children;
         label678: {
            Path symlinkPath;
            int symlinkCount;
            Iterator var10;
            label691: {
               Object $result = $continuation.result;
               var14 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               switch ($continuation.label) {
                  case 0:
                     ResultKt.throwOnFailure($result);
                     if (!postorder) {
                        $continuation.L$0 = $this$collectRecursively;
                        $continuation.L$1 = fileSystem;
                        $continuation.L$2 = stack;
                        $continuation.L$3 = path;
                        $continuation.Z$0 = followSymlinks;
                        $continuation.Z$1 = postorder;
                        $continuation.label = 1;
                        if ($this$collectRecursively.yield(path, $continuation) == var14) {
                           return var14;
                        }
                     }
                     break;
                  case 1:
                     postorder = $continuation.Z$1;
                     followSymlinks = $continuation.Z$0;
                     path = (Path)$continuation.L$3;
                     stack = (ArrayDeque)$continuation.L$2;
                     fileSystem = (FileSystem)$continuation.L$1;
                     $this$collectRecursively = (SequenceScope)$continuation.L$0;
                     ResultKt.throwOnFailure($result);
                     break;
                  case 2:
                     symlinkCount = $continuation.I$0;
                     postorder = $continuation.Z$1;
                     followSymlinks = $continuation.Z$0;
                     Path child = (Path)$continuation.L$7;
                     var10 = (Iterator)$continuation.L$6;
                     symlinkPath = (Path)$continuation.L$5;
                     children = (List)$continuation.L$4;
                     path = (Path)$continuation.L$3;
                     stack = (ArrayDeque)$continuation.L$2;
                     fileSystem = (FileSystem)$continuation.L$1;
                     $this$collectRecursively = (SequenceScope)$continuation.L$0;

                     try {
                        ResultKt.throwOnFailure($result);
                     } catch (Throwable var36) {
                        var10000 = var36;
                        boolean var10001 = false;
                        break label714;
                     }

                     try {
                        break label691;
                     } catch (Throwable var39) {
                        var10000 = var39;
                        boolean var52 = false;
                        break label714;
                     }
                  case 3:
                     postorder = $continuation.Z$1;
                     followSymlinks = $continuation.Z$0;
                     children = (List)$continuation.L$4;
                     path = (Path)$continuation.L$3;
                     stack = (ArrayDeque)$continuation.L$2;
                     fileSystem = (FileSystem)$continuation.L$1;
                     $this$collectRecursively = (SequenceScope)$continuation.L$0;
                     ResultKt.throwOnFailure($result);
                     return Unit.INSTANCE;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               List var49 = fileSystem.listOrNull(path);
               if (var49 == null) {
                  var49 = CollectionsKt.emptyList();
               }

               children = var49;
               if (((Collection)children).isEmpty()) {
                  break label678;
               }

               symlinkPath = path;
               symlinkCount = 0;

               while(true) {
                  if (followSymlinks && stack.contains(symlinkPath)) {
                     throw new IOException("symlink cycle at " + path);
                  }

                  Path var50 = symlinkTarget(fileSystem, symlinkPath);
                  if (var50 == null) {
                     if (!followSymlinks && symlinkCount != 0) {
                        break label678;
                     }

                     stack.addLast(symlinkPath);

                     try {
                        var10 = children.iterator();
                        break;
                     } catch (Throwable var37) {
                        var10000 = var37;
                        boolean var53 = false;
                        break label714;
                     }
                  }

                  symlinkPath = var50;
                  ++symlinkCount;
               }
            }

            while(true) {
               label649: {
                  try {
                     if (var10.hasNext()) {
                        Path child = (Path)var10.next();
                        boolean var10004 = followSymlinks;
                        boolean var10005 = postorder;
                        $continuation.L$0 = $this$collectRecursively;
                        $continuation.L$1 = fileSystem;
                        $continuation.L$2 = stack;
                        $continuation.L$3 = path;
                        $continuation.L$4 = SpillingKt.nullOutSpilledVariable(children);
                        $continuation.L$5 = SpillingKt.nullOutSpilledVariable(symlinkPath);
                        $continuation.L$6 = var10;
                        $continuation.L$7 = SpillingKt.nullOutSpilledVariable(child);
                        $continuation.Z$0 = followSymlinks;
                        $continuation.Z$1 = postorder;
                        $continuation.I$0 = symlinkCount;
                        $continuation.label = 2;
                        var51 = collectRecursively($this$collectRecursively, fileSystem, stack, child, var10004, var10005, $continuation);
                        break label649;
                     }
                  } catch (Throwable var38) {
                     var10000 = var38;
                     boolean var54 = false;
                     break label714;
                  }

                  stack.removeLast();
                  break;
               }

               if (var51 == var14) {
                  return var14;
               }

               try {
                  ;
               } catch (Throwable var35) {
                  var10000 = var35;
                  boolean var55 = false;
                  break label714;
               }
            }
         }

         if (!postorder) {
            return Unit.INSTANCE;
         }

         $continuation.L$0 = SpillingKt.nullOutSpilledVariable($this$collectRecursively);
         $continuation.L$1 = SpillingKt.nullOutSpilledVariable(fileSystem);
         $continuation.L$2 = SpillingKt.nullOutSpilledVariable(stack);
         $continuation.L$3 = SpillingKt.nullOutSpilledVariable(path);
         $continuation.L$4 = SpillingKt.nullOutSpilledVariable(children);
         $continuation.L$5 = null;
         $continuation.L$6 = null;
         $continuation.L$7 = null;
         $continuation.Z$0 = followSymlinks;
         $continuation.Z$1 = postorder;
         $continuation.label = 3;
         if ($this$collectRecursively.yield(path, $continuation) == var14) {
            return var14;
         }

         return Unit.INSTANCE;
      }

      Throwable var47 = var10000;
      stack.removeLast();
      throw var47;
   }

   @Nullable
   public static final Path symlinkTarget(@NotNull FileSystem $this$symlinkTarget, @NotNull Path path) throws IOException {
      Intrinsics.checkNotNullParameter($this$symlinkTarget, "<this>");
      Intrinsics.checkNotNullParameter(path, "path");
      Path var10000 = $this$symlinkTarget.metadata(path).getSymlinkTarget();
      if (var10000 == null) {
         return null;
      } else {
         Path target = var10000;
         var10000 = path.parent();
         Intrinsics.checkNotNull(var10000);
         return var10000.resolve(target);
      }
   }
}
