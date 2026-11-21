package okhttp3;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.Internal;
import okhttp3.internal._UtilCommonKt;
import okio.BufferedSink;
import okio.ByteString;
import okio.FileSystem;
import okio.HashingSink;
import okio.Okio;
import okio.Path;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b&\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0004\u001a\u0004\u0018\u00010\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH&J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\rH\u0016J\u0006\u0010\u000f\u001a\u00020\u0010¨\u0006\u0012"},
   d2 = {"Lokhttp3/RequestBody;", "", "<init>", "()V", "contentType", "Lokhttp3/MediaType;", "contentLength", "", "writeTo", "", "sink", "Lokio/BufferedSink;", "isDuplex", "", "isOneShot", "sha256", "Lokio/ByteString;", "Companion", "okhttp"}
)
public abstract class RequestBody {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @JvmField
   @NotNull
   public static final RequestBody EMPTY;

   @Nullable
   public abstract MediaType contentType();

   public long contentLength() throws IOException {
      return -1L;
   }

   public abstract void writeTo(@NotNull BufferedSink var1) throws IOException;

   public boolean isDuplex() {
      return false;
   }

   public boolean isOneShot() {
      return false;
   }

   @NotNull
   public final ByteString sha256() throws IOException {
      HashingSink hashingSink = HashingSink.Companion.sha256(Okio.blackhole());
      Closeable var2 = Okio.buffer(hashingSink);
      Throwable var3 = null;

      try {
         BufferedSink it = (BufferedSink)var2;
         int var5 = 0;
         this.writeTo(it);
         Unit var10 = Unit.INSTANCE;
      } catch (Throwable var8) {
         var3 = var8;
         throw var8;
      } finally {
         CloseableKt.closeFinally(var2, var3);
      }

      return hashingSink.hash();
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull String $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull ByteString $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull FileDescriptor $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmOverloads
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull byte[] $this$create, @Nullable MediaType contentType, int offset, int byteCount) {
      return Companion.create($this$create, contentType, offset, byteCount);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull File $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull Path $this$create, @NotNull FileSystem fileSystem, @Nullable MediaType contentType) {
      return Companion.create($this$create, fileSystem, contentType);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final RequestBody create(@Nullable MediaType contentType, @NotNull String content) {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final RequestBody create(@Nullable MediaType contentType, @NotNull ByteString content) {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType, offset, byteCount)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
      level = DeprecationLevel.WARNING
   )
   @JvmOverloads
   @NotNull
   public static final RequestBody create(@Nullable MediaType contentType, @NotNull byte[] content, int offset, int byteCount) {
      return Companion.create(contentType, content, offset, byteCount);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'file' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "file.asRequestBody(contentType)",
   imports = {"okhttp3.RequestBody.Companion.asRequestBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final RequestBody create(@Nullable MediaType contentType, @NotNull File file) {
      return Companion.create(contentType, file);
   }

   @JvmStatic
   @JvmOverloads
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull byte[] $this$create, @Nullable MediaType contentType, int offset) {
      return Companion.create($this$create, contentType, offset);
   }

   @JvmStatic
   @JvmOverloads
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull byte[] $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmOverloads
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final RequestBody create(@NotNull byte[] $this$create) {
      return Companion.create($this$create);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType, offset, byteCount)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
      level = DeprecationLevel.WARNING
   )
   @JvmOverloads
   @NotNull
   public static final RequestBody create(@Nullable MediaType contentType, @NotNull byte[] content, int offset) {
      return Companion.create(contentType, content, offset);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType, offset, byteCount)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
      level = DeprecationLevel.WARNING
   )
   @JvmOverloads
   @NotNull
   public static final RequestBody create(@Nullable MediaType contentType, @NotNull byte[] content) {
      return Companion.create(contentType, content);
   }

   static {
      EMPTY = RequestBody.Companion.create$default(Companion, (ByteString)ByteString.EMPTY, (MediaType)null, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0006\u001a\u00020\u0005*\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ\u001d\u0010\u0006\u001a\u00020\u0005*\u00020\u000b2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ\u001d\u0010\u0006\u001a\u00020\u0005*\u00020\f2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ1\u0010\u0006\u001a\u00020\u0005*\u00020\r2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000fH\u0007¢\u0006\u0002\b\nJ\u001d\u0010\u0011\u001a\u00020\u0005*\u00020\u00122\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ%\u0010\u0011\u001a\u00020\u0005*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ\u001a\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0016\u001a\u00020\u0007H\u0007J\u001a\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0016\u001a\u00020\u000bH\u0007J.\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0016\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000fH\u0007J\u001a\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0017\u001a\u00020\u0012H\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
      d2 = {"Lokhttp3/RequestBody$Companion;", "", "<init>", "()V", "EMPTY", "Lokhttp3/RequestBody;", "toRequestBody", "", "contentType", "Lokhttp3/MediaType;", "create", "Lokio/ByteString;", "Ljava/io/FileDescriptor;", "", "offset", "", "byteCount", "asRequestBody", "Ljava/io/File;", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "content", "file", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull String $this$toRequestBody, @Nullable MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         Pair var3 = Internal.chooseCharset(contentType);
         Charset charset = (Charset)var3.component1();
         MediaType finalContentType = (MediaType)var3.component2();
         byte[] var10000 = $this$toRequestBody.getBytes(charset);
         Intrinsics.checkNotNullExpressionValue(var10000, "getBytes(...)");
         byte[] bytes = var10000;
         return this.create((byte[])bytes, (MediaType)finalContentType, 0, bytes.length);
      }

      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, String var1, MediaType var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = null;
         }

         return var0.create(var1, var2);
      }

      @JvmStatic
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull final ByteString $this$toRequestBody, @Nullable final MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         return new RequestBody() {
            public MediaType contentType() {
               return contentType;
            }

            public long contentLength() {
               return (long)$this$toRequestBody.size();
            }

            public void writeTo(BufferedSink sink) {
               Intrinsics.checkNotNullParameter(sink, "sink");
               sink.write($this$toRequestBody);
            }
         };
      }

      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, ByteString var1, MediaType var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = null;
         }

         return var0.create(var1, var2);
      }

      @JvmStatic
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull final FileDescriptor $this$toRequestBody, @Nullable final MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         return new RequestBody() {
            public MediaType contentType() {
               return contentType;
            }

            public boolean isOneShot() {
               return true;
            }

            public void writeTo(BufferedSink sink) {
               Intrinsics.checkNotNullParameter(sink, "sink");
               Closeable var2 = (Closeable)(new FileInputStream($this$toRequestBody));
               Throwable var3 = null;

               try {
                  FileInputStream it = (FileInputStream)var2;
                  int var6 = 0;
                  long var11 = sink.getBuffer().writeAll(Okio.source((InputStream)it));
               } catch (Throwable var9) {
                  var3 = var9;
                  throw var9;
               } finally {
                  CloseableKt.closeFinally(var2, var3);
               }

            }
         };
      }

      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, FileDescriptor var1, MediaType var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = null;
         }

         return var0.create(var1, var2);
      }

      @JvmStatic
      @JvmOverloads
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull final byte[] $this$toRequestBody, @Nullable final MediaType contentType, final int offset, final int byteCount) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         _UtilCommonKt.checkOffsetAndCount((long)$this$toRequestBody.length, (long)offset, (long)byteCount);
         return new RequestBody() {
            public MediaType contentType() {
               return contentType;
            }

            public long contentLength() {
               return (long)byteCount;
            }

            public void writeTo(BufferedSink sink) {
               Intrinsics.checkNotNullParameter(sink, "sink");
               sink.write($this$toRequestBody, offset, byteCount);
            }
         };
      }

      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, byte[] var1, MediaType var2, int var3, int var4, int var5, Object var6) {
         if ((var5 & 1) != 0) {
            var2 = null;
         }

         if ((var5 & 2) != 0) {
            var3 = 0;
         }

         if ((var5 & 4) != 0) {
            var4 = var1.length;
         }

         return var0.create(var1, var2, var3, var4);
      }

      @JvmStatic
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull final File $this$asRequestBody, @Nullable final MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$asRequestBody, "<this>");
         return new RequestBody() {
            public MediaType contentType() {
               return contentType;
            }

            public long contentLength() {
               return $this$asRequestBody.length();
            }

            public void writeTo(BufferedSink sink) {
               Intrinsics.checkNotNullParameter(sink, "sink");
               Closeable var2 = Okio.source($this$asRequestBody);
               Throwable var3 = null;

               try {
                  Source source = (Source)var2;
                  int var6 = 0;
                  long var11 = sink.writeAll(source);
               } catch (Throwable var9) {
                  var3 = var9;
                  throw var9;
               } finally {
                  CloseableKt.closeFinally(var2, var3);
               }

            }
         };
      }

      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, File var1, MediaType var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = null;
         }

         return var0.create(var1, var2);
      }

      @JvmStatic
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull final Path $this$asRequestBody, @NotNull final FileSystem fileSystem, @Nullable final MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$asRequestBody, "<this>");
         Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
         return new RequestBody() {
            public MediaType contentType() {
               return contentType;
            }

            public long contentLength() {
               Long var10000 = fileSystem.metadata($this$asRequestBody).getSize();
               return var10000 != null ? var10000 : -1L;
            }

            public void writeTo(BufferedSink sink) {
               Intrinsics.checkNotNullParameter(sink, "sink");
               Closeable var2 = fileSystem.source($this$asRequestBody);
               Throwable var3 = null;

               try {
                  Source source = (Source)var2;
                  int var6 = 0;
                  long var11 = sink.writeAll(source);
               } catch (Throwable var9) {
                  var3 = var9;
                  throw var9;
               } finally {
                  CloseableKt.closeFinally(var2, var3);
               }

            }
         };
      }

      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, Path var1, FileSystem var2, MediaType var3, int var4, Object var5) {
         if ((var4 & 2) != 0) {
            var3 = null;
         }

         return var0.create(var1, var2, var3);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final RequestBody create(@Nullable MediaType contentType, @NotNull String content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final RequestBody create(@Nullable MediaType contentType, @NotNull ByteString content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType, offset, byteCount)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
         level = DeprecationLevel.WARNING
      )
      @JvmOverloads
      @NotNull
      public final RequestBody create(@Nullable MediaType contentType, @NotNull byte[] content, int offset, int byteCount) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType, offset, byteCount);
      }

      /** @deprecated */
      // $FF: synthetic method
      public static RequestBody create$default(Companion var0, MediaType var1, byte[] var2, int var3, int var4, int var5, Object var6) {
         if ((var5 & 4) != 0) {
            var3 = 0;
         }

         if ((var5 & 8) != 0) {
            var4 = var2.length;
         }

         return var0.create(var1, var2, var3, var4);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'file' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "file.asRequestBody(contentType)",
   imports = {"okhttp3.RequestBody.Companion.asRequestBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final RequestBody create(@Nullable MediaType contentType, @NotNull File file) {
         Intrinsics.checkNotNullParameter(file, "file");
         return this.create(file, contentType);
      }

      @JvmStatic
      @JvmOverloads
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull byte[] $this$toRequestBody, @Nullable MediaType contentType, int offset) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         return create$default(this, (byte[])$this$toRequestBody, (MediaType)contentType, offset, 0, 4, (Object)null);
      }

      @JvmStatic
      @JvmOverloads
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull byte[] $this$toRequestBody, @Nullable MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         return create$default(this, (byte[])$this$toRequestBody, (MediaType)contentType, 0, 0, 6, (Object)null);
      }

      @JvmStatic
      @JvmOverloads
      @JvmName(
         name = "create"
      )
      @NotNull
      public final RequestBody create(@NotNull byte[] $this$toRequestBody) {
         Intrinsics.checkNotNullParameter($this$toRequestBody, "<this>");
         return create$default(this, (byte[])$this$toRequestBody, (MediaType)null, 0, 0, 7, (Object)null);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType, offset, byteCount)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
         level = DeprecationLevel.WARNING
      )
      @JvmOverloads
      @NotNull
      public final RequestBody create(@Nullable MediaType contentType, @NotNull byte[] content, int offset) {
         Intrinsics.checkNotNullParameter(content, "content");
         return create$default(this, (MediaType)contentType, (byte[])content, offset, 0, 8, (Object)null);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toRequestBody(contentType, offset, byteCount)",
   imports = {"okhttp3.RequestBody.Companion.toRequestBody"}
),
         level = DeprecationLevel.WARNING
      )
      @JvmOverloads
      @NotNull
      public final RequestBody create(@Nullable MediaType contentType, @NotNull byte[] content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return create$default(this, (MediaType)contentType, (byte[])content, 0, 0, 12, (Object)null);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
