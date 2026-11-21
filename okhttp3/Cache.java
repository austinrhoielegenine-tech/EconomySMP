package okhttp3;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal.cache.CacheRequest;
import okhttp3.internal.cache.CacheStrategy;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.FileSystem;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Path;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010)\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 L2\u00020\u00012\u00020\u0002:\u0004IJKLB)\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fB!\b\u0016\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u000b\u0010\rB\u0019\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u000e\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u000b\u0010\u000fJ\u0017\u0010#\u001a\u0004\u0018\u00010$2\u0006\u0010%\u001a\u00020&H\u0000¢\u0006\u0002\b'J\u0017\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010*\u001a\u00020$H\u0000¢\u0006\u0002\b+J\u0015\u0010,\u001a\u00020-2\u0006\u0010%\u001a\u00020&H\u0000¢\u0006\u0002\b.J\u001d\u0010/\u001a\u00020-2\u0006\u00100\u001a\u00020$2\u0006\u00101\u001a\u00020$H\u0000¢\u0006\u0002\b2J\u0016\u00103\u001a\u00020-2\f\u00104\u001a\b\u0018\u000105R\u00020\u0011H\u0002J\u0006\u00106\u001a\u00020-J\u0006\u00107\u001a\u00020-J\u0006\u00108\u001a\u00020-J\f\u00109\u001a\b\u0012\u0004\u0012\u00020;0:J\u0006\u0010\u001a\u001a\u00020\u0015J\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010<\u001a\u00020\u0006J\u0006\u0010\u0005\u001a\u00020\u0006J\b\u0010=\u001a\u00020-H\u0016J\b\u0010>\u001a\u00020-H\u0016J\r\u0010\u0003\u001a\u00020\u000eH\u0007¢\u0006\u0002\bBJ\u0015\u0010C\u001a\u00020-2\u0006\u0010D\u001a\u00020EH\u0000¢\u0006\u0002\bFJ\r\u0010G\u001a\u00020-H\u0000¢\u0006\u0002\bHJ\u0006\u0010\u001d\u001a\u00020\u0015J\u0006\u0010\u001e\u001a\u00020\u0015J\u0006\u0010\u001f\u001a\u00020\u0015R\u0014\u0010\u0010\u001a\u00020\u0011X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\u00020\u0015X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u0015X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0017\"\u0004\b\u001c\u0010\u0019R\u000e\u0010\u001d\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010 \u001a\u00020!8F¢\u0006\u0006\u001a\u0004\b \u0010\"R\u0011\u0010\u0003\u001a\u00020\u000e8G¢\u0006\u0006\u001a\u0004\b\u0003\u0010?R\u0011\u0010@\u001a\u00020\u00048G¢\u0006\u0006\u001a\u0004\b@\u0010A¨\u0006M"},
   d2 = {"Lokhttp3/Cache;", "Ljava/io/Closeable;", "Ljava/io/Flushable;", "directory", "Lokio/Path;", "maxSize", "", "fileSystem", "Lokio/FileSystem;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "<init>", "(Lokio/Path;JLokio/FileSystem;Lokhttp3/internal/concurrent/TaskRunner;)V", "(Lokio/FileSystem;Lokio/Path;J)V", "Ljava/io/File;", "(Ljava/io/File;J)V", "cache", "Lokhttp3/internal/cache/DiskLruCache;", "getCache$okhttp", "()Lokhttp3/internal/cache/DiskLruCache;", "writeSuccessCount", "", "getWriteSuccessCount$okhttp", "()I", "setWriteSuccessCount$okhttp", "(I)V", "writeAbortCount", "getWriteAbortCount$okhttp", "setWriteAbortCount$okhttp", "networkCount", "hitCount", "requestCount", "isClosed", "", "()Z", "get", "Lokhttp3/Response;", "request", "Lokhttp3/Request;", "get$okhttp", "put", "Lokhttp3/internal/cache/CacheRequest;", "response", "put$okhttp", "remove", "", "remove$okhttp", "update", "cached", "network", "update$okhttp", "abortQuietly", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "initialize", "delete", "evictAll", "urls", "", "", "size", "flush", "close", "()Ljava/io/File;", "directoryPath", "()Lokio/Path;", "-deprecated_directory", "trackResponse", "cacheStrategy", "Lokhttp3/internal/cache/CacheStrategy;", "trackResponse$okhttp", "trackConditionalCacheHit", "trackConditionalCacheHit$okhttp", "RealCacheRequest", "Entry", "CacheResponseBody", "Companion", "okhttp"}
)
public final class Cache implements Closeable, Flushable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final DiskLruCache cache;
   private int writeSuccessCount;
   private int writeAbortCount;
   private int networkCount;
   private int hitCount;
   private int requestCount;
   private static final int VERSION = 201105;
   private static final int ENTRY_METADATA = 0;
   private static final int ENTRY_BODY = 1;
   private static final int ENTRY_COUNT = 2;

   public Cache(@NotNull Path directory, long maxSize, @NotNull FileSystem fileSystem, @NotNull TaskRunner taskRunner) {
      Intrinsics.checkNotNullParameter(directory, "directory");
      Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      super();
      this.cache = new DiskLruCache(fileSystem, directory, 201105, 2, maxSize, taskRunner);
   }

   public Cache(@NotNull FileSystem fileSystem, @NotNull Path directory, long maxSize) {
      Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
      Intrinsics.checkNotNullParameter(directory, "directory");
      this(directory, maxSize, fileSystem, TaskRunner.INSTANCE);
   }

   public Cache(@NotNull File directory, long maxSize) {
      Intrinsics.checkNotNullParameter(directory, "directory");
      this(FileSystem.SYSTEM, Path.Companion.get$default(Path.Companion, (File)directory, false, 1, (Object)null), maxSize);
   }

   @NotNull
   public final DiskLruCache getCache$okhttp() {
      return this.cache;
   }

   public final int getWriteSuccessCount$okhttp() {
      return this.writeSuccessCount;
   }

   public final void setWriteSuccessCount$okhttp(int var1) {
      this.writeSuccessCount = var1;
   }

   public final int getWriteAbortCount$okhttp() {
      return this.writeAbortCount;
   }

   public final void setWriteAbortCount$okhttp(int var1) {
      this.writeAbortCount = var1;
   }

   public final boolean isClosed() {
      return this.cache.isClosed();
   }

   @Nullable
   public final Response get$okhttp(@NotNull Request request) {
      Intrinsics.checkNotNullParameter(request, "request");
      String key = Companion.key(request.url());

      DiskLruCache.Snapshot var4;
      try {
         DiskLruCache.Snapshot var10000 = this.cache.get(key);
         if (var10000 == null) {
            return null;
         }

         var4 = var10000;
      } catch (IOException var8) {
         return null;
      }

      DiskLruCache.Snapshot snapshot = var4;

      Entry var5;
      try {
         var5 = new Entry(snapshot.getSource(0));
      } catch (IOException var7) {
         _UtilCommonKt.closeQuietly(var4);
         return null;
      }

      Entry entry = var5;
      Response response = var5.response(snapshot);
      if (!entry.matches(request, response)) {
         _UtilCommonKt.closeQuietly(response.body());
         return null;
      } else {
         return response;
      }
   }

   @Nullable
   public final CacheRequest put$okhttp(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      String requestMethod = response.request().method();
      if (HttpMethod.invalidatesCache(response.request().method())) {
         try {
            this.remove$okhttp(response.request());
         } catch (IOException var6) {
         }

         return null;
      } else if (!Intrinsics.areEqual((Object)requestMethod, (Object)"GET")) {
         return null;
      } else if (Companion.hasVaryAll(response)) {
         return null;
      } else {
         Entry entry = new Entry(response);
         DiskLruCache.Editor editor = null;

         try {
            DiskLruCache.Editor var10000 = DiskLruCache.edit$default(this.cache, Companion.key(response.request().url()), 0L, 2, (Object)null);
            if (var10000 == null) {
               return null;
            } else {
               editor = var10000;
               entry.writeTo(editor);
               return new RealCacheRequest(editor);
            }
         } catch (IOException var7) {
            this.abortQuietly(editor);
            return null;
         }
      }
   }

   public final void remove$okhttp(@NotNull Request request) throws IOException {
      Intrinsics.checkNotNullParameter(request, "request");
      this.cache.remove(Companion.key(request.url()));
   }

   public final void update$okhttp(@NotNull Response cached, @NotNull Response network) {
      Intrinsics.checkNotNullParameter(cached, "cached");
      Intrinsics.checkNotNullParameter(network, "network");
      Entry entry = new Entry(network);
      ResponseBody var10000 = cached.body();
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type okhttp3.Cache.CacheResponseBody");
      DiskLruCache.Snapshot snapshot = ((CacheResponseBody)var10000).getSnapshot();
      DiskLruCache.Editor editor = null;

      try {
         DiskLruCache.Editor var9 = snapshot.edit();
         if (var9 == null) {
            return;
         }

         editor = var9;
         entry.writeTo(editor);
         editor.commit();
      } catch (IOException var7) {
         this.abortQuietly(editor);
      }

   }

   private final void abortQuietly(DiskLruCache.Editor editor) {
      try {
         if (editor != null) {
            editor.abort();
         }
      } catch (IOException var3) {
      }

   }

   public final void initialize() throws IOException {
      this.cache.initialize();
   }

   public final void delete() throws IOException {
      this.cache.delete();
   }

   public final void evictAll() throws IOException {
      this.cache.evictAll();
   }

   @NotNull
   public final Iterator<String> urls() throws IOException {
      return new Iterator<String>(this) {
         private final Iterator<DiskLruCache.Snapshot> delegate;
         private String nextUrl;
         private boolean canRemove;

         {
            this.delegate = $receiver.getCache$okhttp().snapshots();
         }

         public boolean hasNext() {
            if (this.nextUrl != null) {
               return true;
            } else {
               this.canRemove = false;

               while(this.delegate.hasNext()) {
                  try {
                     Closeable var1 = (Closeable)this.delegate.next();
                     Throwable var2 = null;

                     boolean var6;
                     try {
                        DiskLruCache.Snapshot snapshot = (DiskLruCache.Snapshot)var1;
                        int var4 = 0;
                        BufferedSource metadata = Okio.buffer(snapshot.getSource(0));
                        this.nextUrl = metadata.readUtf8LineStrict();
                        var6 = true;
                     } catch (Throwable var10) {
                        var2 = var10;
                        throw var10;
                     } finally {
                        CloseableKt.closeFinally(var1, var2);
                     }

                     return var6;
                  } catch (IOException var12) {
                  }
               }

               return false;
            }
         }

         public String next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               String var10000 = this.nextUrl;
               Intrinsics.checkNotNull(var10000);
               String result = var10000;
               this.nextUrl = null;
               this.canRemove = true;
               return result;
            }
         }

         public void remove() {
            if (!this.canRemove) {
               int var1 = 0;
               String var2 = "remove() before next()";
               throw new IllegalStateException(var2.toString());
            } else {
               this.delegate.remove();
            }
         }
      };
   }

   public final synchronized int writeAbortCount() {
      return this.writeAbortCount;
   }

   public final synchronized int writeSuccessCount() {
      return this.writeSuccessCount;
   }

   public final long size() throws IOException {
      return this.cache.size();
   }

   public final long maxSize() {
      return this.cache.getMaxSize();
   }

   public void flush() throws IOException {
      this.cache.flush();
   }

   public void close() throws IOException {
      this.cache.close();
   }

   @JvmName(
      name = "directory"
   )
   @NotNull
   public final File directory() {
      return this.cache.getDirectory().toFile();
   }

   @JvmName(
      name = "directoryPath"
   )
   @NotNull
   public final Path directoryPath() {
      return this.cache.getDirectory();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "directory",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_directory"
   )
   @NotNull
   public final File _deprecated_directory/* $FF was: -deprecated_directory*/() {
      return this.cache.getDirectory().toFile();
   }

   public final synchronized void trackResponse$okhttp(@NotNull CacheStrategy cacheStrategy) {
      Intrinsics.checkNotNullParameter(cacheStrategy, "cacheStrategy");
      int var2 = this.requestCount++;
      if (cacheStrategy.getNetworkRequest() != null) {
         var2 = this.networkCount++;
      } else if (cacheStrategy.getCacheResponse() != null) {
         var2 = this.hitCount++;
      }

   }

   public final synchronized void trackConditionalCacheHit$okhttp() {
      int var1 = this.hitCount++;
   }

   public final synchronized int networkCount() {
      return this.networkCount;
   }

   public final synchronized int hitCount() {
      return this.hitCount;
   }

   public final synchronized int requestCount() {
      return this.requestCount;
   }

   @JvmStatic
   @NotNull
   public static final String key(@NotNull HttpUrl url) {
      return Companion.key(url);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0013\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\t\u001a\u00020\bH\u0016R\u0012\u0010\u0002\u001a\u00060\u0003R\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0012"},
      d2 = {"Lokhttp3/Cache$RealCacheRequest;", "Lokhttp3/internal/cache/CacheRequest;", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "<init>", "(Lokhttp3/Cache;Lokhttp3/internal/cache/DiskLruCache$Editor;)V", "cacheOut", "Lokio/Sink;", "body", "done", "", "getDone", "()Z", "setDone", "(Z)V", "abort", "", "okhttp"}
   )
   private final class RealCacheRequest implements CacheRequest {
      @NotNull
      private final DiskLruCache.Editor editor;
      @NotNull
      private final Sink cacheOut;
      @NotNull
      private final Sink body;
      private boolean done;

      public RealCacheRequest(@NotNull DiskLruCache.Editor editor) {
         Intrinsics.checkNotNullParameter(editor, "editor");
         super();
         this.editor = editor;
         this.cacheOut = this.editor.newSink(1);
         Sink var3 = this.cacheOut;
         this.body = new ForwardingSink(var3) {
            public void close() throws IOException {
               Cache var1 = Cache.this;
               RealCacheRequest var2 = RealCacheRequest.this;
               Cache var3 = Cache.this;
               synchronized(var1){}

               label39: {
                  try {
                     int var5 = 0;
                     if (!var2.getDone()) {
                        var2.setDone(true);
                        int var6 = var3.getWriteSuccessCount$okhttp();
                        var3.setWriteSuccessCount$okhttp(var6 + 1);
                        break label39;
                     }
                  } finally {
                     ;
                  }

                  return;
               }

               super.close();
               RealCacheRequest.this.editor.commit();
            }
         };
      }

      public final boolean getDone() {
         return this.done;
      }

      public final void setDone(boolean var1) {
         this.done = var1;
      }

      public void abort() {
         Cache var1 = Cache.this;
         Cache var2 = Cache.this;
         synchronized(var1){}

         try {
            int var4 = 0;
            if (this.done) {
               return;
            }

            this.done = true;
            int var5 = var2.getWriteAbortCount$okhttp();
            var2.setWriteAbortCount$okhttp(var5 + 1);
         } finally {
            ;
         }

         _UtilCommonKt.closeQuietly(this.cacheOut);

         try {
            this.editor.abort();
         } catch (IOException var8) {
         }

      }

      @NotNull
      public Sink body() {
         return this.body;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u0000 .2\u00020\u0001:\u0001.B\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0011\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\bJ\u0012\u0010\u001a\u001a\u00020\u001b2\n\u0010\u001c\u001a\u00060\u001dR\u00020\u001eJ\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 2\u0006\u0010\"\u001a\u00020#H\u0002J\u001e\u0010$\u001a\u00020\u001b2\u0006\u0010%\u001a\u00020&2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020!0 H\u0002J\u0016\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\u0006\u0010\u0006\u001a\u00020\u0007J\u0012\u0010\u0006\u001a\u00020\u00072\n\u0010,\u001a\u00060-R\u00020\u001eR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006/"},
      d2 = {"Lokhttp3/Cache$Entry;", "", "rawSource", "Lokio/Source;", "<init>", "(Lokio/Source;)V", "response", "Lokhttp3/Response;", "(Lokhttp3/Response;)V", "url", "Lokhttp3/HttpUrl;", "varyHeaders", "Lokhttp3/Headers;", "requestMethod", "", "protocol", "Lokhttp3/Protocol;", "code", "", "message", "responseHeaders", "handshake", "Lokhttp3/Handshake;", "sentRequestMillis", "", "receivedResponseMillis", "writeTo", "", "editor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "readCertificateList", "", "Ljava/security/cert/Certificate;", "source", "Lokio/BufferedSource;", "writeCertList", "sink", "Lokio/BufferedSink;", "certificates", "matches", "", "request", "Lokhttp3/Request;", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Companion", "okhttp"}
   )
   private static final class Entry {
      @NotNull
      public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
      @NotNull
      private final HttpUrl url;
      @NotNull
      private final Headers varyHeaders;
      @NotNull
      private final String requestMethod;
      @NotNull
      private final Protocol protocol;
      private final int code;
      @NotNull
      private final String message;
      @NotNull
      private final Headers responseHeaders;
      @Nullable
      private final Handshake handshake;
      private final long sentRequestMillis;
      private final long receivedResponseMillis;
      @NotNull
      private static final String SENT_MILLIS;
      @NotNull
      private static final String RECEIVED_MILLIS;

      public Entry(@NotNull Source rawSource) throws IOException {
         Intrinsics.checkNotNullParameter(rawSource, "rawSource");
         super();
         Closeable var2 = rawSource;
         Throwable var3 = null;

         try {
            Source it = (Source)var2;
            int var5 = 0;
            BufferedSource source = Okio.buffer(rawSource);
            String urlLine = source.readUtf8LineStrict();
            HttpUrl var10001 = HttpUrl.Companion.parse(urlLine);
            if (var10001 == null) {
               IOException var27 = new IOException("Cache corruption for " + urlLine);
               int responseHeadersBuilder = 0;
               Platform.Companion.get().log("cache corruption", 5, (Throwable)var27);
               throw (Throwable)var27;
            }

            this.url = var10001;
            this.requestMethod = source.readUtf8LineStrict();
            Headers.Builder varyHeadersBuilder = new Headers.Builder();
            int varyRequestHeaderLineCount = Cache.Companion.readInt$okhttp(source);

            for(int i = 0; i < varyRequestHeaderLineCount; ++i) {
               varyHeadersBuilder.addLenient$okhttp(source.readUtf8LineStrict());
            }

            this.varyHeaders = varyHeadersBuilder.build();
            StatusLine statusLine = StatusLine.Companion.parse(source.readUtf8LineStrict());
            this.protocol = statusLine.protocol;
            this.code = statusLine.code;
            this.message = statusLine.message;
            Headers.Builder responseHeadersBuilder = new Headers.Builder();
            int responseHeaderLineCount = Cache.Companion.readInt$okhttp(source);

            for(int i = 0; i < responseHeaderLineCount; ++i) {
               responseHeadersBuilder.addLenient$okhttp(source.readUtf8LineStrict());
            }

            String sendRequestMillisString = responseHeadersBuilder.get(SENT_MILLIS);
            String receivedResponseMillisString = responseHeadersBuilder.get(RECEIVED_MILLIS);
            responseHeadersBuilder.removeAll(SENT_MILLIS);
            responseHeadersBuilder.removeAll(RECEIVED_MILLIS);
            this.sentRequestMillis = sendRequestMillisString != null ? Long.parseLong(sendRequestMillisString) : 0L;
            this.receivedResponseMillis = receivedResponseMillisString != null ? Long.parseLong(receivedResponseMillisString) : 0L;
            this.responseHeaders = responseHeadersBuilder.build();
            if (this.url.isHttps()) {
               String blank = source.readUtf8LineStrict();
               if (((CharSequence)blank).length() > 0) {
                  throw new IOException("expected \"\" but was \"" + blank + '"');
               }

               String cipherSuiteString = source.readUtf8LineStrict();
               CipherSuite cipherSuite = CipherSuite.Companion.forJavaName(cipherSuiteString);
               List peerCertificates = this.readCertificateList(source);
               List localCertificates = this.readCertificateList(source);
               TlsVersion tlsVersion = !source.exhausted() ? TlsVersion.Companion.forJavaName(source.readUtf8LineStrict()) : TlsVersion.SSL_3_0;
               this.handshake = Handshake.Companion.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
            } else {
               this.handshake = null;
            }

            Unit var26 = Unit.INSTANCE;
         } catch (Throwable var24) {
            var3 = var24;
            throw var24;
         } finally {
            CloseableKt.closeFinally(var2, var3);
         }

      }

      public Entry(@NotNull Response response) {
         Intrinsics.checkNotNullParameter(response, "response");
         super();
         this.url = response.request().url();
         this.varyHeaders = Cache.Companion.varyHeaders(response);
         this.requestMethod = response.request().method();
         this.protocol = response.protocol();
         this.code = response.code();
         this.message = response.message();
         this.responseHeaders = response.headers();
         this.handshake = response.handshake();
         this.sentRequestMillis = response.sentRequestAtMillis();
         this.receivedResponseMillis = response.receivedResponseAtMillis();
      }

      public final void writeTo(@NotNull DiskLruCache.Editor editor) throws IOException {
         Intrinsics.checkNotNullParameter(editor, "editor");
         Closeable var2 = Okio.buffer(editor.newSink(0));
         Throwable var3 = null;

         try {
            BufferedSink sink = (BufferedSink)var2;
            int var5 = 0;
            sink.writeUtf8(this.url.toString()).writeByte(10);
            sink.writeUtf8(this.requestMethod).writeByte(10);
            sink.writeDecimalLong((long)this.varyHeaders.size()).writeByte(10);
            int i = 0;

            for(int var7 = this.varyHeaders.size(); i < var7; ++i) {
               sink.writeUtf8(this.varyHeaders.name(i)).writeUtf8(": ").writeUtf8(this.varyHeaders.value(i)).writeByte(10);
            }

            sink.writeUtf8((new StatusLine(this.protocol, this.code, this.message)).toString()).writeByte(10);
            sink.writeDecimalLong((long)(this.responseHeaders.size() + 2)).writeByte(10);
            i = 0;

            for(int var14 = this.responseHeaders.size(); i < var14; ++i) {
               sink.writeUtf8(this.responseHeaders.name(i)).writeUtf8(": ").writeUtf8(this.responseHeaders.value(i)).writeByte(10);
            }

            sink.writeUtf8(SENT_MILLIS).writeUtf8(": ").writeDecimalLong(this.sentRequestMillis).writeByte(10);
            sink.writeUtf8(RECEIVED_MILLIS).writeUtf8(": ").writeDecimalLong(this.receivedResponseMillis).writeByte(10);
            if (this.url.isHttps()) {
               sink.writeByte(10);
               Handshake var10001 = this.handshake;
               Intrinsics.checkNotNull(var10001);
               sink.writeUtf8(var10001.cipherSuite().javaName()).writeByte(10);
               this.writeCertList(sink, this.handshake.peerCertificates());
               this.writeCertList(sink, this.handshake.localCertificates());
               sink.writeUtf8(this.handshake.tlsVersion().javaName()).writeByte(10);
            }

            Unit var12 = Unit.INSTANCE;
         } catch (Throwable var10) {
            var3 = var10;
            throw var10;
         } finally {
            CloseableKt.closeFinally(var2, var3);
         }

      }

      private final List<Certificate> readCertificateList(BufferedSource source) throws IOException {
         int length = Cache.Companion.readInt$okhttp(source);
         if (length == -1) {
            return CollectionsKt.emptyList();
         } else {
            try {
               CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
               ArrayList result = new ArrayList(length);

               for(int i = 0; i < length; ++i) {
                  String line = source.readUtf8LineStrict();
                  Buffer bytes = new Buffer();
                  ByteString var10000 = ByteString.Companion.decodeBase64(line);
                  if (var10000 == null) {
                     throw new IOException("Corrupt certificate in cache entry");
                  }

                  ByteString certificateBytes = var10000;
                  bytes.write(certificateBytes);
                  result.add(certificateFactory.generateCertificate(bytes.inputStream()));
               }

               return (List)result;
            } catch (CertificateException e) {
               throw new IOException(e.getMessage());
            }
         }
      }

      private final void writeCertList(BufferedSink sink, List<? extends Certificate> certificates) throws IOException {
         try {
            sink.writeDecimalLong((long)certificates.size()).writeByte(10);

            for(Certificate element : certificates) {
               byte[] bytes = element.getEncoded();
               ByteString.Companion var10000 = ByteString.Companion;
               Intrinsics.checkNotNull(bytes);
               String line = ByteString.Companion.of$default(var10000, bytes, 0, 0, 3, (Object)null).base64();
               sink.writeUtf8(line).writeByte(10);
            }

         } catch (CertificateEncodingException e) {
            throw new IOException(e.getMessage());
         }
      }

      public final boolean matches(@NotNull Request request, @NotNull Response response) {
         Intrinsics.checkNotNullParameter(request, "request");
         Intrinsics.checkNotNullParameter(response, "response");
         return Intrinsics.areEqual((Object)this.url, (Object)request.url()) && Intrinsics.areEqual((Object)this.requestMethod, (Object)request.method()) && Cache.Companion.varyMatches(response, this.varyHeaders, request);
      }

      @NotNull
      public final Response response(@NotNull DiskLruCache.Snapshot snapshot) {
         Intrinsics.checkNotNullParameter(snapshot, "snapshot");
         String contentType = this.responseHeaders.get("Content-Type");
         String contentLength = this.responseHeaders.get("Content-Length");
         Request cacheRequest = new Request(this.url, this.varyHeaders, this.requestMethod, (RequestBody)null, 8, (DefaultConstructorMarker)null);
         return (new Response.Builder()).request(cacheRequest).protocol(this.protocol).code(this.code).message(this.message).headers(this.responseHeaders).body(new CacheResponseBody(snapshot, contentType, contentLength)).handshake(this.handshake).sentRequestAtMillis(this.sentRequestMillis).receivedResponseAtMillis(this.receivedResponseMillis).build();
      }

      static {
         SENT_MILLIS = Platform.Companion.get().getPrefix() + "-Sent-Millis";
         RECEIVED_MILLIS = Platform.Companion.get().getPrefix() + "-Received-Millis";
      }

      @Metadata(
         mv = {2, 2, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
         d2 = {"Lokhttp3/Cache$Entry$Companion;", "", "<init>", "()V", "SENT_MILLIS", "", "RECEIVED_MILLIS", "okhttp"}
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

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B'\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\b\u0010\tJ\n\u0010\u0005\u001a\u0004\u0018\u00010\u000eH\u0016J\b\u0010\u0007\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\rH\u0016R\u0015\u0010\u0002\u001a\u00060\u0003R\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
      d2 = {"Lokhttp3/Cache$CacheResponseBody;", "Lokhttp3/ResponseBody;", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Lokhttp3/internal/cache/DiskLruCache;", "contentType", "", "contentLength", "<init>", "(Lokhttp3/internal/cache/DiskLruCache$Snapshot;Ljava/lang/String;Ljava/lang/String;)V", "getSnapshot", "()Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "bodySource", "Lokio/BufferedSource;", "Lokhttp3/MediaType;", "", "source", "okhttp"}
   )
   private static final class CacheResponseBody extends ResponseBody {
      @NotNull
      private final DiskLruCache.Snapshot snapshot;
      @Nullable
      private final String contentType;
      @Nullable
      private final String contentLength;
      @NotNull
      private final BufferedSource bodySource;

      public CacheResponseBody(@NotNull DiskLruCache.Snapshot snapshot, @Nullable String contentType, @Nullable String contentLength) {
         Intrinsics.checkNotNullParameter(snapshot, "snapshot");
         super();
         this.snapshot = snapshot;
         this.contentType = contentType;
         this.contentLength = contentLength;
         Source source = this.snapshot.getSource(1);
         this.bodySource = Okio.buffer(new ForwardingSource(source) {
            public void close() throws IOException {
               CacheResponseBody.this.getSnapshot().close();
               super.close();
            }
         });
      }

      @NotNull
      public final DiskLruCache.Snapshot getSnapshot() {
         return this.snapshot;
      }

      @Nullable
      public MediaType contentType() {
         String var10000 = this.contentType;
         return var10000 != null ? MediaType.Companion.parse(var10000) : null;
      }

      public long contentLength() {
         String var10000 = this.contentLength;
         return var10000 != null ? _UtilCommonKt.toLongOrDefault(var10000, -1L) : -1L;
      }

      @NotNull
      public BufferedSource source() {
         return this.bodySource;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007J\u0015\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0000¢\u0006\u0002\b\u0010J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018J\n\u0010\u0019\u001a\u00020\u0012*\u00020\u0014J\u0012\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\n0\u001b*\u00020\u0016H\u0002J\n\u0010\u001c\u001a\u00020\u0016*\u00020\u0014J\u0018\u0010\u001c\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u0016H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001f"},
      d2 = {"Lokhttp3/Cache$Companion;", "", "<init>", "()V", "VERSION", "", "ENTRY_METADATA", "ENTRY_BODY", "ENTRY_COUNT", "key", "", "url", "Lokhttp3/HttpUrl;", "readInt", "source", "Lokio/BufferedSource;", "readInt$okhttp", "varyMatches", "", "cachedResponse", "Lokhttp3/Response;", "cachedRequest", "Lokhttp3/Headers;", "newRequest", "Lokhttp3/Request;", "hasVaryAll", "varyFields", "", "varyHeaders", "requestHeaders", "responseHeaders", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cache.kt\nokhttp3/Cache$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,838:1\n2746#2,3:839\n*S KotlinDebug\n*F\n+ 1 Cache.kt\nokhttp3/Cache$Companion\n*L\n777#1:839,3\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final String key(@NotNull HttpUrl url) {
         Intrinsics.checkNotNullParameter(url, "url");
         return ByteString.Companion.encodeUtf8(url.toString()).md5().hex();
      }

      public final int readInt$okhttp(@NotNull BufferedSource source) throws IOException {
         Intrinsics.checkNotNullParameter(source, "source");

         try {
            long result = source.readDecimalLong();
            String line = source.readUtf8LineStrict();
            if (result >= 0L && result <= 2147483647L && ((CharSequence)line).length() <= 0) {
               return (int)result;
            } else {
               throw new IOException("expected an int but was \"" + result + line + '"');
            }
         } catch (NumberFormatException e) {
            throw new IOException(e.getMessage());
         }
      }

      public final boolean varyMatches(@NotNull Response cachedResponse, @NotNull Headers cachedRequest, @NotNull Request newRequest) {
         Intrinsics.checkNotNullParameter(cachedResponse, "cachedResponse");
         Intrinsics.checkNotNullParameter(cachedRequest, "cachedRequest");
         Intrinsics.checkNotNullParameter(newRequest, "newRequest");
         Iterable $this$none$iv = (Iterable)this.varyFields(cachedResponse.headers());
         int $i$f$none = 0;
         boolean var10000;
         if ($this$none$iv instanceof Collection && ((Collection)$this$none$iv).isEmpty()) {
            var10000 = true;
         } else {
            for(Object element$iv : $this$none$iv) {
               String it = (String)element$iv;
               int var9 = 0;
               if (!Intrinsics.areEqual((Object)cachedRequest.values(it), (Object)newRequest.headers(it))) {
                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
         }

         return var10000;
      }

      public final boolean hasVaryAll(@NotNull Response $this$hasVaryAll) {
         Intrinsics.checkNotNullParameter($this$hasVaryAll, "<this>");
         return this.varyFields($this$hasVaryAll.headers()).contains("*");
      }

      private final Set<String> varyFields(Headers $this$varyFields) {
         Set result = null;
         int i = 0;

         for(int var4 = $this$varyFields.size(); i < var4; ++i) {
            if (StringsKt.equals("Vary", $this$varyFields.name(i), true)) {
               String value = $this$varyFields.value(i);
               if (result == null) {
                  result = (Set)(new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE)));
               }

               CharSequence var10000 = (CharSequence)value;
               char[] var7 = new char[]{','};

               for(String varyField : StringsKt.split$default(var10000, var7, false, 0, 6, (Object)null)) {
                  result.add(StringsKt.trim((CharSequence)varyField).toString());
               }
            }
         }

         Set var9 = result;
         if (result == null) {
            var9 = SetsKt.emptySet();
         }

         return var9;
      }

      @NotNull
      public final Headers varyHeaders(@NotNull Response $this$varyHeaders) {
         Intrinsics.checkNotNullParameter($this$varyHeaders, "<this>");
         Response var10000 = $this$varyHeaders.networkResponse();
         Intrinsics.checkNotNull(var10000);
         Headers requestHeaders = var10000.request().headers();
         Headers responseHeaders = $this$varyHeaders.headers();
         return this.varyHeaders(requestHeaders, responseHeaders);
      }

      private final Headers varyHeaders(Headers requestHeaders, Headers responseHeaders) {
         Set varyFields = this.varyFields(responseHeaders);
         if (varyFields.isEmpty()) {
            return Headers.EMPTY;
         } else {
            Headers.Builder result = new Headers.Builder();
            int i = 0;

            for(int var6 = requestHeaders.size(); i < var6; ++i) {
               String fieldName = requestHeaders.name(i);
               if (varyFields.contains(fieldName)) {
                  result.add(fieldName, requestHeaders.value(i));
               }
            }

            return result.build();
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
