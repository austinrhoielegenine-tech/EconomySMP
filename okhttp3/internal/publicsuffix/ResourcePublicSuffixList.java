package okhttp3.internal.publicsuffix;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.FileSystem;
import okio.Path;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\f\u001a\u00020\rH\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000f"},
   d2 = {"Lokhttp3/internal/publicsuffix/ResourcePublicSuffixList;", "Lokhttp3/internal/publicsuffix/BasePublicSuffixList;", "path", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "<init>", "(Lokio/Path;Lokio/FileSystem;)V", "getPath", "()Lokio/Path;", "getFileSystem", "()Lokio/FileSystem;", "listSource", "Lokio/Source;", "Companion", "okhttp"}
)
public final class ResourcePublicSuffixList extends BasePublicSuffixList {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Path path;
   @NotNull
   private final FileSystem fileSystem;
   @JvmField
   @NotNull
   public static final Path PUBLIC_SUFFIX_RESOURCE;

   public ResourcePublicSuffixList(@NotNull Path path, @NotNull FileSystem fileSystem) {
      Intrinsics.checkNotNullParameter(path, "path");
      Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
      super();
      this.path = path;
      this.fileSystem = fileSystem;
   }

   // $FF: synthetic method
   public ResourcePublicSuffixList(Path var1, FileSystem var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 1) != 0) {
         var1 = PUBLIC_SUFFIX_RESOURCE;
      }

      if ((var3 & 2) != 0) {
         var2 = FileSystem.RESOURCES;
      }

      this(var1, var2);
   }

   @NotNull
   public Path getPath() {
      return this.path;
   }

   @NotNull
   public final FileSystem getFileSystem() {
      return this.fileSystem;
   }

   @NotNull
   public Source listSource() {
      return this.fileSystem.source(this.getPath());
   }

   public ResourcePublicSuffixList() {
      this((Path)null, (FileSystem)null, 3, (DefaultConstructorMarker)null);
   }

   static {
      PUBLIC_SUFFIX_RESOURCE = Path.Companion.get$default(Path.Companion, (String)("okhttp3/internal/publicsuffix/" + PublicSuffixDatabase.class.getSimpleName() + ".list"), false, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lokhttp3/internal/publicsuffix/ResourcePublicSuffixList$Companion;", "", "<init>", "()V", "PUBLIC_SUFFIX_RESOURCE", "Lokio/Path;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
