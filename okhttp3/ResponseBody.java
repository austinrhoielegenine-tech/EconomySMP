package okhttp3;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.Internal;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b&\u0018\u0000 \"2\u00020\u0001:\u0002!\"B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\b\u0010\b\u001a\u00020\tH&J\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\f\u001a\u00020\rH&J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u0011JD\u0010\u0012\u001a\u0002H\u0013\"\b\b\u0000\u0010\u0013*\u00020\u0014*\u00020\u00002\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u0002H\u00130\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u0002H\u0013\u0012\u0004\u0012\u00020\u00180\u0016H\u0082\b¢\u0006\u0002\u0010\u0019J\u0006\u0010\u001a\u001a\u00020\u0005J\u0006\u0010\u001b\u001a\u00020\u001cJ\b\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020 H\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006#"},
   d2 = {"Lokhttp3/ResponseBody;", "Ljava/io/Closeable;", "<init>", "()V", "reader", "Ljava/io/Reader;", "contentType", "Lokhttp3/MediaType;", "contentLength", "", "byteStream", "Ljava/io/InputStream;", "source", "Lokio/BufferedSource;", "bytes", "", "byteString", "Lokio/ByteString;", "consumeSource", "T", "", "consumer", "Lkotlin/Function1;", "sizeMapper", "", "(Lokhttp3/ResponseBody;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "charStream", "string", "", "charset", "Ljava/nio/charset/Charset;", "close", "", "BomAwareReader", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nResponseBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResponseBody.kt\nokhttp3/ResponseBody\n+ 2 Okio.kt\nokio/Okio__OkioKt\n*L\n1#1,343:1\n141#1,6:344\n147#1,5:373\n141#1,6:378\n147#1,5:407\n72#2:350\n58#2,22:351\n72#2:384\n58#2,22:385\n72#2:412\n58#2,22:413\n72#2:435\n58#2,22:436\n*S KotlinDebug\n*F\n+ 1 ResponseBody.kt\nokhttp3/ResponseBody\n*L\n125#1:344,6\n125#1:373,5\n135#1:378,6\n135#1:407,5\n125#1:350\n125#1:351,22\n135#1:384\n135#1:385,22\n146#1:412\n146#1:413,22\n189#1:435\n189#1:436,22\n*E\n"})
public abstract class ResponseBody implements Closeable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @Nullable
   private Reader reader;
   @JvmField
   @NotNull
   public static final ResponseBody EMPTY;

   @Nullable
   public abstract MediaType contentType();

   public abstract long contentLength();

   @NotNull
   public final InputStream byteStream() {
      return this.source().inputStream();
   }

   @NotNull
   public abstract BufferedSource source();

   @NotNull
   public final byte[] bytes() throws IOException {
      int $i$f$consumeSource = 0;
      long contentLength$iv = this.contentLength();
      if (contentLength$iv > 2147483647L) {
         throw new IOException("Cannot buffer entire body for content length: " + contentLength$iv);
      } else {
         Closeable $this$use$iv$iv = this.source();
         int $i$f$use = 0;
         Throwable thrown$iv$iv = null;
         boolean var22 = false;

         byte[] p0;
         label134: {
            label133: {
               try {
                  var22 = true;
                  BufferedSource result$iv$iv = (BufferedSource)$this$use$iv$iv;
                  boolean var10 = false;
                  p0 = result$iv$iv.readByteArray();
                  var22 = false;
                  break label133;
               } catch (Throwable t$iv$iv) {
                  thrown$iv$iv = t$iv$iv;
                  p0 = null;
                  var22 = false;
               } finally {
                  if (var22) {
                     try {
                        if ($this$use$iv$iv != null) {
                           $this$use$iv$iv.close();
                        }
                     } catch (Throwable t$iv$iv) {
                        if (thrown$iv$iv != null) {
                           ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
                        }
                     }

                  }
               }

               try {
                  if ($this$use$iv$iv != null) {
                     $this$use$iv$iv.close();
                  }
               } catch (Throwable t$iv$iv) {
                  if (thrown$iv$iv == null) {
                     thrown$iv$iv = t$iv$iv;
                  } else {
                     ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
                  }
               }
               break label134;
            }

            try {
               if ($this$use$iv$iv != null) {
                  $this$use$iv$iv.close();
               }
            } catch (Throwable t$iv$iv) {
               thrown$iv$iv = t$iv$iv;
            }
         }

         if (thrown$iv$iv != null) {
            throw thrown$iv$iv;
         } else {
            int var16 = 0;
            int size$iv = p0.length;
            if (contentLength$iv != -1L && contentLength$iv != (long)size$iv) {
               throw new IOException("Content-Length (" + contentLength$iv + ") and stream length (" + size$iv + ") disagree");
            } else {
               return p0;
            }
         }
      }
   }

   @NotNull
   public final ByteString byteString() throws IOException {
      int $i$f$consumeSource = 0;
      long contentLength$iv = this.contentLength();
      if (contentLength$iv > 2147483647L) {
         throw new IOException("Cannot buffer entire body for content length: " + contentLength$iv);
      } else {
         Closeable $this$use$iv$iv = this.source();
         int $i$f$use = 0;
         Throwable thrown$iv$iv = null;
         boolean var22 = false;

         ByteString p0;
         label134: {
            label133: {
               try {
                  var22 = true;
                  BufferedSource result$iv$iv = (BufferedSource)$this$use$iv$iv;
                  boolean var10 = false;
                  p0 = result$iv$iv.readByteString();
                  var22 = false;
                  break label133;
               } catch (Throwable t$iv$iv) {
                  thrown$iv$iv = t$iv$iv;
                  p0 = null;
                  var22 = false;
               } finally {
                  if (var22) {
                     try {
                        if ($this$use$iv$iv != null) {
                           $this$use$iv$iv.close();
                        }
                     } catch (Throwable t$iv$iv) {
                        if (thrown$iv$iv != null) {
                           ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
                        }
                     }

                  }
               }

               try {
                  if ($this$use$iv$iv != null) {
                     $this$use$iv$iv.close();
                  }
               } catch (Throwable t$iv$iv) {
                  if (thrown$iv$iv == null) {
                     thrown$iv$iv = t$iv$iv;
                  } else {
                     ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
                  }
               }
               break label134;
            }

            try {
               if ($this$use$iv$iv != null) {
                  $this$use$iv$iv.close();
               }
            } catch (Throwable t$iv$iv) {
               thrown$iv$iv = t$iv$iv;
            }
         }

         if (thrown$iv$iv != null) {
            throw thrown$iv$iv;
         } else {
            int var16 = 0;
            int size$iv = p0.size();
            if (contentLength$iv != -1L && contentLength$iv != (long)size$iv) {
               throw new IOException("Content-Length (" + contentLength$iv + ") and stream length (" + size$iv + ") disagree");
            } else {
               return p0;
            }
         }
      }
   }

   private final <T> T consumeSource(ResponseBody $this$consumeSource, Function1<? super BufferedSource, ? extends T> consumer, Function1<? super T, Integer> sizeMapper) {
      int $i$f$consumeSource = 0;
      long contentLength = $this$consumeSource.contentLength();
      if (contentLength > 2147483647L) {
         throw new IOException("Cannot buffer entire body for content length: " + contentLength);
      } else {
         Closeable $this$use$iv = $this$consumeSource.source();
         int $i$f$use = 0;
         Throwable thrown$iv = null;
         boolean var21 = false;

         Object result$iv;
         label159: {
            label160: {
               try {
                  var21 = true;
                  result$iv = consumer.invoke($this$use$iv);
                  var21 = false;
                  break label160;
               } catch (Throwable t$iv) {
                  thrown$iv = t$iv;
                  result$iv = null;
                  var21 = false;
               } finally {
                  if (var21) {
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
               break label159;
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
            int size = ((Number)sizeMapper.invoke(result$iv)).intValue();
            if (contentLength != -1L && contentLength != (long)size) {
               throw new IOException("Content-Length (" + contentLength + ") and stream length (" + size + ") disagree");
            } else {
               return (T)result$iv;
            }
         }
      }
   }

   @NotNull
   public final Reader charStream() {
      Reader var10000 = this.reader;
      if (var10000 == null) {
         BomAwareReader it = new BomAwareReader(this.source(), this.charset());
         int var3 = 0;
         this.reader = it;
         var10000 = it;
      }

      return var10000;
   }

   @NotNull
   public final String string() throws IOException {
      Closeable $this$use$iv = this.source();
      int $i$f$use = 0;
      Throwable thrown$iv = null;
      boolean var14 = false;

      String result$iv;
      label116: {
         label115: {
            try {
               var14 = true;
               BufferedSource source = (BufferedSource)$this$use$iv;
               int result$iv = false;
               result$iv = source.readString(_UtilJvmKt.readBomAsCharset(source, this.charset()));
               var14 = false;
               break label115;
            } catch (Throwable t$iv) {
               thrown$iv = t$iv;
               result$iv = null;
               var14 = false;
            } finally {
               if (var14) {
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
            break label116;
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
         return result$iv;
      }
   }

   private final Charset charset() {
      return Internal.charsetOrUtf8(this.contentType());
   }

   public void close() {
      _UtilCommonKt.closeQuietly(this.source());
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final ResponseBody create(@NotNull String $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final ResponseBody create(@NotNull byte[] $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final ResponseBody create(@NotNull ByteString $this$create, @Nullable MediaType contentType) {
      return Companion.create($this$create, contentType);
   }

   @JvmStatic
   @JvmName(
      name = "create"
   )
   @NotNull
   public static final ResponseBody create(@NotNull BufferedSource $this$create, @Nullable MediaType contentType, long contentLength) {
      return Companion.create($this$create, contentType, contentLength);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toResponseBody(contentType)",
   imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final ResponseBody create(@Nullable MediaType contentType, @NotNull String content) {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toResponseBody(contentType)",
   imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final ResponseBody create(@Nullable MediaType contentType, @NotNull byte[] content) {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.toResponseBody(contentType)",
   imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final ResponseBody create(@Nullable MediaType contentType, @NotNull ByteString content) {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @JvmStatic
   @Deprecated(
      message = "Moved to extension function. Put the 'content' argument first to fix Java",
      replaceWith = @ReplaceWith(
   expression = "content.asResponseBody(contentType, contentLength)",
   imports = {"okhttp3.ResponseBody.Companion.asResponseBody"}
),
      level = DeprecationLevel.WARNING
   )
   @NotNull
   public static final ResponseBody create(@Nullable MediaType contentType, long contentLength, @NotNull BufferedSource content) {
      return Companion.create(contentType, contentLength, content);
   }

   static {
      EMPTY = ResponseBody.Companion.create$default(Companion, (ByteString)ByteString.EMPTY, (MediaType)null, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0019\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J \u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\fH\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0001X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0013"},
      d2 = {"Lokhttp3/ResponseBody$BomAwareReader;", "Ljava/io/Reader;", "source", "Lokio/BufferedSource;", "charset", "Ljava/nio/charset/Charset;", "<init>", "(Lokio/BufferedSource;Ljava/nio/charset/Charset;)V", "closed", "", "delegate", "read", "", "cbuf", "", "off", "len", "close", "", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nResponseBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResponseBody.kt\nokhttp3/ResponseBody$BomAwareReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,343:1\n1#2:344\n*E\n"})
   public static final class BomAwareReader extends Reader {
      @NotNull
      private final BufferedSource source;
      @NotNull
      private final Charset charset;
      private boolean closed;
      @Nullable
      private Reader delegate;

      public BomAwareReader(@NotNull BufferedSource source, @NotNull Charset charset) {
         Intrinsics.checkNotNullParameter(source, "source");
         Intrinsics.checkNotNullParameter(charset, "charset");
         super();
         this.source = source;
         this.charset = charset;
      }

      public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
         Intrinsics.checkNotNullParameter(cbuf, "cbuf");
         if (this.closed) {
            throw new IOException("Stream closed");
         } else {
            Reader var10000 = this.delegate;
            if (var10000 == null) {
               InputStreamReader it = new InputStreamReader(this.source.inputStream(), _UtilJvmKt.readBomAsCharset(this.source, this.charset));
               int var7 = 0;
               this.delegate = (Reader)it;
               var10000 = (Reader)it;
            }

            Reader finalDelegate = var10000;
            return finalDelegate.read(cbuf, off, len);
         }
      }

      public void close() throws IOException {
         this.closed = true;
         Reader var10000 = this.delegate;
         if (var10000 != null) {
            var10000.close();
         } else {
            BomAwareReader $this$close_u24lambda_u240 = this;
            int var2 = 0;
            $this$close_u24lambda_u240.source.close();
         }

      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0006\u001a\u00020\u0005*\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ\u001d\u0010\u0006\u001a\u00020\u0005*\u00020\u000b2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ\u001d\u0010\u0006\u001a\u00020\u0005*\u00020\f2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u0007¢\u0006\u0002\b\nJ'\u0010\r\u001a\u00020\u0005*\u00020\u000e2\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u0007¢\u0006\u0002\b\nJ\u001a\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0011\u001a\u00020\u0007H\u0007J\u001a\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0011\u001a\u00020\u000bH\u0007J\u001a\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0011\u001a\u00020\fH\u0007J\"\u0010\n\u001a\u00020\u00052\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eH\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lokhttp3/ResponseBody$Companion;", "", "<init>", "()V", "EMPTY", "Lokhttp3/ResponseBody;", "toResponseBody", "", "contentType", "Lokhttp3/MediaType;", "create", "", "Lokio/ByteString;", "asResponseBody", "Lokio/BufferedSource;", "contentLength", "", "content", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @JvmName(
         name = "create"
      )
      @NotNull
      public final ResponseBody create(@NotNull String $this$toResponseBody, @Nullable MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toResponseBody, "<this>");
         Pair var3 = Internal.chooseCharset(contentType);
         Charset charset = (Charset)var3.component1();
         MediaType finalContentType = (MediaType)var3.component2();
         Buffer buffer = (new Buffer()).writeString($this$toResponseBody, charset);
         return this.create(buffer, finalContentType, buffer.size());
      }

      // $FF: synthetic method
      public static ResponseBody create$default(Companion var0, String var1, MediaType var2, int var3, Object var4) {
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
      public final ResponseBody create(@NotNull byte[] $this$toResponseBody, @Nullable MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toResponseBody, "<this>");
         return this.create((new Buffer()).write($this$toResponseBody), contentType, (long)$this$toResponseBody.length);
      }

      // $FF: synthetic method
      public static ResponseBody create$default(Companion var0, byte[] var1, MediaType var2, int var3, Object var4) {
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
      public final ResponseBody create(@NotNull ByteString $this$toResponseBody, @Nullable MediaType contentType) {
         Intrinsics.checkNotNullParameter($this$toResponseBody, "<this>");
         return this.create((new Buffer()).write($this$toResponseBody), contentType, (long)$this$toResponseBody.size());
      }

      // $FF: synthetic method
      public static ResponseBody create$default(Companion var0, ByteString var1, MediaType var2, int var3, Object var4) {
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
      public final ResponseBody create(@NotNull final BufferedSource $this$asResponseBody, @Nullable final MediaType contentType, final long contentLength) {
         Intrinsics.checkNotNullParameter($this$asResponseBody, "<this>");
         return new ResponseBody() {
            public MediaType contentType() {
               return contentType;
            }

            public long contentLength() {
               return contentLength;
            }

            public BufferedSource source() {
               return $this$asResponseBody;
            }
         };
      }

      // $FF: synthetic method
      public static ResponseBody create$default(Companion var0, BufferedSource var1, MediaType var2, long var3, int var5, Object var6) {
         if ((var5 & 1) != 0) {
            var2 = null;
         }

         if ((var5 & 2) != 0) {
            var3 = -1L;
         }

         return var0.create(var1, var2, var3);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toResponseBody(contentType)",
   imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final ResponseBody create(@Nullable MediaType contentType, @NotNull String content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toResponseBody(contentType)",
   imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final ResponseBody create(@Nullable MediaType contentType, @NotNull byte[] content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.toResponseBody(contentType)",
   imports = {"okhttp3.ResponseBody.Companion.toResponseBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final ResponseBody create(@Nullable MediaType contentType, @NotNull ByteString content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType);
      }

      /** @deprecated */
      @JvmStatic
      @Deprecated(
         message = "Moved to extension function. Put the 'content' argument first to fix Java",
         replaceWith = @ReplaceWith(
   expression = "content.asResponseBody(contentType, contentLength)",
   imports = {"okhttp3.ResponseBody.Companion.asResponseBody"}
),
         level = DeprecationLevel.WARNING
      )
      @NotNull
      public final ResponseBody create(@Nullable MediaType contentType, long contentLength, @NotNull BufferedSource content) {
         Intrinsics.checkNotNullParameter(content, "content");
         return this.create(content, contentType, contentLength);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
