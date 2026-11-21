package okio.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.FileHandle;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Sink;
import okio.Source;
import okio.ZipFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u0000 02\u00020\u0001:\u00010B#\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0001¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\u0011\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\fH\u0016J\u0010\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\fH\u0002J\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\f0\n2\u0006\u0010\u0015\u001a\u00020\fH\u0016J\u0018\u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\n2\u0006\u0010\u0015\u001a\u00020\fH\u0016J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\fH\u0016J \u0010\u001a\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u0005H\u0016J\u0012\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u0012\u001a\u00020\fH\u0016J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010\u0019\u001a\u00020\fH\u0016J\u0018\u0010!\u001a\u00020\"2\u0006\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u0005H\u0016J\u0018\u0010#\u001a\u00020\"2\u0006\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\u0005H\u0016J\u0018\u0010$\u001a\u00020%2\u0006\u0010\u0015\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u0005H\u0016J\u0018\u0010&\u001a\u00020%2\u0006\u0010\u001f\u001a\u00020\f2\u0006\u0010'\u001a\u00020\fH\u0016J\u0018\u0010(\u001a\u00020%2\u0006\u0010\u0012\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\u0005H\u0016J\u0018\u0010)\u001a\u00020%2\u0006\u0010\u001f\u001a\u00020\f2\u0006\u0010'\u001a\u00020\fH\u0016J\f\u0010*\u001a\u00020+*\u00020\fH\u0002J\u001e\u0010,\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\f0\u000b0\n*\u00020\u0003H\u0002J\u001a\u0010-\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b*\u00020.H\u0002J\u001a\u0010/\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b*\u00020.H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R-\u0010\t\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\f0\u000b0\n8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u000f\u0010\u0010\u001a\u0004\b\r\u0010\u000e¨\u00061"},
   d2 = {"Lokio/internal/ResourceFileSystem;", "Lokio/FileSystem;", "classLoader", "Ljava/lang/ClassLoader;", "indexEagerly", "", "systemFileSystem", "<init>", "(Ljava/lang/ClassLoader;ZLokio/FileSystem;)V", "roots", "", "Lkotlin/Pair;", "Lokio/Path;", "getRoots", "()Ljava/util/List;", "roots$delegate", "Lkotlin/Lazy;", "canonicalize", "path", "canonicalizeInternal", "list", "dir", "listOrNull", "openReadOnly", "Lokio/FileHandle;", "file", "openReadWrite", "mustCreate", "mustExist", "metadataOrNull", "Lokio/FileMetadata;", "source", "Lokio/Source;", "sink", "Lokio/Sink;", "appendingSink", "createDirectory", "", "atomicMove", "target", "delete", "createSymlink", "toRelativePath", "", "toClasspathRoots", "toFileRoot", "Ljava/net/URL;", "toJarRoot", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nResourceFileSystem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResourceFileSystem.kt\nokio/internal/ResourceFileSystem\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,215:1\n774#2:216\n865#2,2:217\n1563#2:219\n1634#2,3:220\n774#2:223\n865#2,2:224\n1563#2:226\n1634#2,3:227\n1617#2,9:230\n1869#2:239\n1870#2:241\n1626#2:242\n1617#2,9:243\n1869#2:252\n1870#2:254\n1626#2:255\n1#3:240\n1#3:253\n*S KotlinDebug\n*F\n+ 1 ResourceFileSystem.kt\nokio/internal/ResourceFileSystem\n*L\n75#1:216\n75#1:217,2\n76#1:219\n76#1:220,3\n91#1:223\n91#1:224,2\n92#1:226\n92#1:227,3\n178#1:230,9\n178#1:239\n178#1:241\n178#1:242\n179#1:243,9\n179#1:252\n179#1:254\n179#1:255\n178#1:240\n179#1:253\n*E\n"})
public final class ResourceFileSystem extends FileSystem {
   @NotNull
   private static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final ClassLoader classLoader;
   @NotNull
   private final FileSystem systemFileSystem;
   @NotNull
   private final Lazy roots$delegate;
   @NotNull
   private static final Path ROOT;

   public ResourceFileSystem(@NotNull ClassLoader classLoader, boolean indexEagerly, @NotNull FileSystem systemFileSystem) {
      Intrinsics.checkNotNullParameter(classLoader, "classLoader");
      Intrinsics.checkNotNullParameter(systemFileSystem, "systemFileSystem");
      super();
      this.classLoader = classLoader;
      this.systemFileSystem = systemFileSystem;
      this.roots$delegate = LazyKt.lazy(ResourceFileSystem::roots_delegate$lambda$0);
      if (indexEagerly) {
         this.getRoots().size();
      }

   }

   // $FF: synthetic method
   public ResourceFileSystem(ClassLoader var1, boolean var2, FileSystem var3, int var4, DefaultConstructorMarker var5) {
      if ((var4 & 4) != 0) {
         var3 = FileSystem.SYSTEM;
      }

      this(var1, var2, var3);
   }

   private final List<Pair<FileSystem, Path>> getRoots() {
      Lazy var1 = this.roots$delegate;
      return (List)var1.getValue();
   }

   @NotNull
   public Path canonicalize(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      return this.canonicalizeInternal(path);
   }

   private final Path canonicalizeInternal(Path path) {
      return ROOT.resolve(path, true);
   }

   @NotNull
   public List<Path> list(@NotNull Path dir) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      String relativePath = this.toRelativePath(dir);
      Set result = (Set)(new LinkedHashSet());
      boolean foundAny = false;

      for(Pair var6 : this.getRoots()) {
         FileSystem fileSystem = (FileSystem)var6.component1();
         Path base = (Path)var6.component2();

         try {
            Collection var9 = (Collection)result;
            Iterable $this$filter$iv = (Iterable)fileSystem.list(base.resolve(relativePath));
            int $i$f$filter = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList());
            int $i$f$filterTo = 0;

            for(Object element$iv$iv : $this$filter$iv) {
               Path it = (Path)element$iv$iv;
               int var18 = 0;
               if (Companion.keepPath(it)) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            $this$filter$iv = (Iterable)((List)destination$iv$iv);
            $i$f$filter = 0;
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
            $i$f$filterTo = 0;

            for(Object item$iv$iv : $this$filter$iv) {
               Path it = (Path)item$iv$iv;
               int var29 = 0;
               destination$iv$iv.add(Companion.removeBase(it, base));
            }

            $this$filter$iv = (Iterable)((List)destination$iv$iv);
            CollectionsKt.addAll(var9, $this$filter$iv);
            foundAny = true;
         } catch (IOException var20) {
         }
      }

      if (!foundAny) {
         throw new FileNotFoundException("file not found: " + dir);
      } else {
         return CollectionsKt.toList((Iterable)result);
      }
   }

   @Nullable
   public List<Path> listOrNull(@NotNull Path dir) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      String relativePath = this.toRelativePath(dir);
      Set result = (Set)(new LinkedHashSet());
      boolean foundAny = false;

      for(Pair var6 : this.getRoots()) {
         FileSystem fileSystem = (FileSystem)var6.component1();
         Path base = (Path)var6.component2();
         List var10 = fileSystem.listOrNull(base.resolve(relativePath));
         List var10000;
         if (var10 == null) {
            var10000 = null;
         } else {
            Iterable $this$filter$iv = (Iterable)var10;
            int $i$f$filter = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList());
            int $i$f$filterTo = 0;

            for(Object element$iv$iv : $this$filter$iv) {
               Path it = (Path)element$iv$iv;
               int var19 = 0;
               if (Companion.keepPath(it)) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            $this$filter$iv = (Iterable)((List)destination$iv$iv);
            $i$f$filter = 0;
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$filter$iv, 10)));
            $i$f$filterTo = 0;

            for(Object item$iv$iv : $this$filter$iv) {
               Path it = (Path)item$iv$iv;
               int var28 = 0;
               destination$iv$iv.add(Companion.removeBase(it, base));
            }

            var10000 = (List)destination$iv$iv;
         }

         List baseResult = var10000;
         if (baseResult != null) {
            CollectionsKt.addAll((Collection)result, (Iterable)baseResult);
            foundAny = true;
         }
      }

      return foundAny ? CollectionsKt.toList((Iterable)result) : null;
   }

   @NotNull
   public FileHandle openReadOnly(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");
      if (!Companion.keepPath(file)) {
         throw new FileNotFoundException("file not found: " + file);
      } else {
         String relativePath = this.toRelativePath(file);

         for(Pair var4 : this.getRoots()) {
            FileSystem fileSystem = (FileSystem)var4.component1();
            Path base = (Path)var4.component2();

            try {
               return fileSystem.openReadOnly(base.resolve(relativePath));
            } catch (FileNotFoundException var8) {
            }
         }

         throw new FileNotFoundException("file not found: " + file);
      }
   }

   @NotNull
   public FileHandle openReadWrite(@NotNull Path file, boolean mustCreate, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new IOException("resources are not writable");
   }

   @Nullable
   public FileMetadata metadataOrNull(@NotNull Path path) {
      Intrinsics.checkNotNullParameter(path, "path");
      if (!Companion.keepPath(path)) {
         return null;
      } else {
         String relativePath = this.toRelativePath(path);

         for(Pair var4 : this.getRoots()) {
            FileSystem fileSystem = (FileSystem)var4.component1();
            Path base = (Path)var4.component2();
            FileMetadata var10000 = fileSystem.metadataOrNull(base.resolve(relativePath));
            if (var10000 != null) {
               return var10000;
            }
         }

         return null;
      }
   }

   @NotNull
   public Source source(@NotNull Path file) {
      Intrinsics.checkNotNullParameter(file, "file");
      if (!Companion.keepPath(file)) {
         throw new FileNotFoundException("file not found: " + file);
      } else {
         Path relativePath = Path.resolve$default(ROOT, (Path)file, false, 2, (Object)null).relativeTo(ROOT);
         URL var10000 = this.classLoader.getResource(relativePath.toString());
         if (var10000 == null) {
            throw new FileNotFoundException("file not found: " + file);
         } else {
            URL resource = var10000;
            URLConnection urlConnection = resource.openConnection();
            if (urlConnection instanceof JarURLConnection) {
               ((JarURLConnection)urlConnection).setUseCaches(false);
            }

            InputStream var5 = urlConnection.getInputStream();
            Intrinsics.checkNotNullExpressionValue(var5, "getInputStream(...)");
            return Okio.source(var5);
         }
      }
   }

   @NotNull
   public Sink sink(@NotNull Path file, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new IOException(this + " is read-only");
   }

   @NotNull
   public Sink appendingSink(@NotNull Path file, boolean mustExist) {
      Intrinsics.checkNotNullParameter(file, "file");
      throw new IOException(this + " is read-only");
   }

   public void createDirectory(@NotNull Path dir, boolean mustCreate) {
      Intrinsics.checkNotNullParameter(dir, "dir");
      throw new IOException(this + " is read-only");
   }

   public void atomicMove(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      throw new IOException(this + " is read-only");
   }

   public void delete(@NotNull Path path, boolean mustExist) {
      Intrinsics.checkNotNullParameter(path, "path");
      throw new IOException(this + " is read-only");
   }

   public void createSymlink(@NotNull Path source, @NotNull Path target) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(target, "target");
      throw new IOException(this + " is read-only");
   }

   private final String toRelativePath(Path $this$toRelativePath) {
      Path canonicalThis = this.canonicalizeInternal($this$toRelativePath);
      return canonicalThis.relativeTo(ROOT).toString();
   }

   private final List<Pair<FileSystem, Path>> toClasspathRoots(ClassLoader $this$toClasspathRoots) {
      Enumeration var10000 = $this$toClasspathRoots.getResources("");
      Intrinsics.checkNotNullExpressionValue(var10000, "getResources(...)");
      ArrayList var30 = Collections.list(var10000);
      Intrinsics.checkNotNullExpressionValue(var30, "list(...)");
      Iterable $this$mapNotNull$iv = (Iterable)((List)var30);
      int $i$f$mapNotNull = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      int $i$f$mapNotNullTo = 0;
      int $i$f$forEach = 0;

      for(Object element$iv$iv$iv : $this$mapNotNull$iv) {
         int var12 = 0;
         URL it = (URL)element$iv$iv$iv;
         int var14 = 0;
         Intrinsics.checkNotNull(it);
         Pair var31 = this.toFileRoot(it);
         if (var31 != null) {
            Object it$iv$iv = var31;
            int var16 = 0;
            destination$iv$iv.add(it$iv$iv);
         }
      }

      Collection var32 = (Collection)((List)destination$iv$iv);
      Enumeration var10001 = $this$toClasspathRoots.getResources("META-INF/MANIFEST.MF");
      Intrinsics.checkNotNullExpressionValue(var10001, "getResources(...)");
      ArrayList var34 = Collections.list(var10001);
      Intrinsics.checkNotNullExpressionValue(var34, "list(...)");
      $this$mapNotNull$iv = (Iterable)((List)var34);
      Collection var17 = var32;
      $i$f$mapNotNull = 0;
      destination$iv$iv = (Collection)(new ArrayList());
      $i$f$mapNotNullTo = 0;
      $i$f$forEach = 0;

      for(Object element$iv$iv$iv : $this$mapNotNull$iv) {
         int var25 = 0;
         URL it = (URL)element$iv$iv$iv;
         int var27 = 0;
         Intrinsics.checkNotNull(it);
         Pair var33 = this.toJarRoot(it);
         if (var33 != null) {
            Object it$iv$iv = var33;
            int var29 = 0;
            destination$iv$iv.add(it$iv$iv);
         }
      }

      return CollectionsKt.plus(var17, (Iterable)((List)destination$iv$iv));
   }

   private final Pair<FileSystem, Path> toFileRoot(URL $this$toFileRoot) {
      return !Intrinsics.areEqual((Object)$this$toFileRoot.getProtocol(), (Object)"file") ? null : TuplesKt.to(this.systemFileSystem, Path.Companion.get$default(Path.Companion, (File)(new File($this$toFileRoot.toURI())), false, 1, (Object)null));
   }

   private final Pair<FileSystem, Path> toJarRoot(URL $this$toJarRoot) {
      String var10000 = $this$toJarRoot.toString();
      Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
      String urlString = var10000;
      if (!StringsKt.startsWith$default(urlString, "jar:file:", false, 2, (Object)null)) {
         return null;
      } else {
         int suffixStart = StringsKt.lastIndexOf$default((CharSequence)urlString, "!", 0, false, 6, (Object)null);
         if (suffixStart == -1) {
            return null;
         } else {
            Path.Companion var6 = Path.Companion;
            String var10003 = urlString.substring(4, suffixStart);
            Intrinsics.checkNotNullExpressionValue(var10003, "substring(...)");
            Path path = Path.Companion.get$default(var6, (File)(new File(URI.create(var10003))), false, 1, (Object)null);
            ZipFileSystem zip = ZipFilesKt.openZip(path, this.systemFileSystem, ResourceFileSystem::toJarRoot$lambda$0);
            return TuplesKt.<FileSystem, Path>to(zip, ROOT);
         }
      }
   }

   private static final List roots_delegate$lambda$0(ResourceFileSystem this$0) {
      return this$0.toClasspathRoots(this$0.classLoader);
   }

   private static final boolean toJarRoot$lambda$0(ZipEntry entry) {
      Intrinsics.checkNotNullParameter(entry, "entry");
      return Companion.keepPath(entry.getCanonicalPath());
   }

   static {
      ROOT = Path.Companion.get$default(Path.Companion, (String)"/", false, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0082\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\b\u001a\u00020\u0005*\u00020\u00052\u0006\u0010\t\u001a\u00020\u0005J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u0002R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\r"},
      d2 = {"Lokio/internal/ResourceFileSystem$Companion;", "", "<init>", "()V", "ROOT", "Lokio/Path;", "getROOT", "()Lokio/Path;", "removeBase", "base", "keepPath", "", "path", "okio"}
   )
   private static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Path getROOT() {
         return ResourceFileSystem.ROOT;
      }

      @NotNull
      public final Path removeBase(@NotNull Path $this$removeBase, @NotNull Path base) {
         Intrinsics.checkNotNullParameter($this$removeBase, "<this>");
         Intrinsics.checkNotNullParameter(base, "base");
         String prefix = base.toString();
         return this.getROOT().resolve(StringsKt.replace$default(StringsKt.removePrefix($this$removeBase.toString(), (CharSequence)prefix), '\\', '/', false, 4, (Object)null));
      }

      private final boolean keepPath(Path path) {
         return !StringsKt.endsWith(path.name(), ".class", true);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
