package okio;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.OpenOption;
import java.util.Arrays;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "changed in Okio 2.x"
)
@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0005H\u0007J\u0010\u0010\b\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0007J\u0010\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\n\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0007J)\u0010\n\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\u0007¢\u0006\u0002\u0010\u0015J\u0010\u0010\n\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0017H\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u0018\u001a\u00020\u0019H\u0007J)\u0010\f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00140\u0013\"\u00020\u0014H\u0007¢\u0006\u0002\u0010\u001aJ\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u0017H\u0007J\b\u0010\u001b\u001a\u00020\u0005H\u0007¨\u0006\u001c"},
   d2 = {"Lokio/-DeprecatedOkio;", "", "<init>", "()V", "appendingSink", "Lokio/Sink;", "file", "Ljava/io/File;", "buffer", "Lokio/BufferedSink;", "sink", "Lokio/BufferedSource;", "source", "Lokio/Source;", "outputStream", "Ljava/io/OutputStream;", "path", "Ljava/nio/file/Path;", "options", "", "Ljava/nio/file/OpenOption;", "(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Lokio/Sink;", "socket", "Ljava/net/Socket;", "inputStream", "Ljava/io/InputStream;", "(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Lokio/Source;", "blackhole", "okio"}
)
public final class -DeprecatedOkio {
   @NotNull
   public static final -DeprecatedOkio INSTANCE = new -DeprecatedOkio();

   private _DeprecatedOkio/* $FF was: -DeprecatedOkio*/() {
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "file.appendingSink()",
   imports = {"okio.appendingSink"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Sink appendingSink(@NotNull File file) {
      Intrinsics.checkNotNullParameter(file, "file");
      return Okio.appendingSink(file);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "sink.buffer()",
   imports = {"okio.buffer"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final BufferedSink buffer(@NotNull Sink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      return Okio.buffer(sink);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "source.buffer()",
   imports = {"okio.buffer"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final BufferedSource buffer(@NotNull Source source) {
      Intrinsics.checkNotNullParameter(source, "source");
      return Okio.buffer(source);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "file.sink()",
   imports = {"okio.sink"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Sink sink(@NotNull File file) {
      Intrinsics.checkNotNullParameter(file, "file");
      return Okio.sink$default(file, false, 1, (Object)null);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "outputStream.sink()",
   imports = {"okio.sink"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Sink sink(@NotNull OutputStream outputStream) {
      Intrinsics.checkNotNullParameter(outputStream, "outputStream");
      return Okio.sink(outputStream);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "path.sink(*options)",
   imports = {"okio.sink"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Sink sink(@NotNull java.nio.file.Path path, @NotNull OpenOption... options) {
      Intrinsics.checkNotNullParameter(path, "path");
      Intrinsics.checkNotNullParameter(options, "options");
      return Okio.sink(path, (OpenOption[])Arrays.copyOf(options, options.length));
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "socket.sink()",
   imports = {"okio.sink"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Sink sink(@NotNull java.net.Socket socket) {
      Intrinsics.checkNotNullParameter(socket, "socket");
      return Okio.sink(socket);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "file.source()",
   imports = {"okio.source"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Source source(@NotNull File file) {
      Intrinsics.checkNotNullParameter(file, "file");
      return Okio.source(file);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "inputStream.source()",
   imports = {"okio.source"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Source source(@NotNull InputStream inputStream) {
      Intrinsics.checkNotNullParameter(inputStream, "inputStream");
      return Okio.source(inputStream);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "path.source(*options)",
   imports = {"okio.source"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Source source(@NotNull java.nio.file.Path path, @NotNull OpenOption... options) {
      Intrinsics.checkNotNullParameter(path, "path");
      Intrinsics.checkNotNullParameter(options, "options");
      return Okio.source(path, (OpenOption[])Arrays.copyOf(options, options.length));
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "socket.source()",
   imports = {"okio.source"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Source source(@NotNull java.net.Socket socket) {
      Intrinsics.checkNotNullParameter(socket, "socket");
      return Okio.source(socket);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to extension function",
      replaceWith = @ReplaceWith(
   expression = "blackholeSink()",
   imports = {"okio.blackholeSink"}
),
      level = DeprecationLevel.ERROR
   )
   @NotNull
   public final Sink blackhole() {
      return Okio.blackhole();
   }
}
