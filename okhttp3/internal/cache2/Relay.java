package okhttp3.internal.cache2;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal.concurrent.Lockable;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\t\u0018\u0000 <2\u00020\u0001:\u0002;<B5\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0007¢\u0006\u0004\b\u000b\u0010\fJ \u00103\u001a\u0002042\u0006\u00105\u001a\u00020\t2\u0006\u00106\u001a\u00020\u00072\u0006\u00107\u001a\u00020\u0007H\u0002J\u0010\u00108\u001a\u0002042\u0006\u00106\u001a\u00020\u0007H\u0002J\u000e\u00109\u001a\u0002042\u0006\u00106\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\tJ\b\u0010:\u001a\u0004\u0018\u00010\u0005R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u001c\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u0011\u0010 \u001a\u00020!¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u001a\u0010$\u001a\u00020%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u0011\u0010*\u001a\u00020!¢\u0006\b\n\u0000\u001a\u0004\b+\u0010#R\u001a\u0010,\u001a\u00020-X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010/\"\u0004\b0\u00101R\u0011\u00102\u001a\u00020%8F¢\u0006\u0006\u001a\u0004\b2\u0010'¨\u0006="},
   d2 = {"Lokhttp3/internal/cache2/Relay;", "Lokhttp3/internal/concurrent/Lockable;", "file", "Ljava/io/RandomAccessFile;", "upstream", "Lokio/Source;", "upstreamPos", "", "metadata", "Lokio/ByteString;", "bufferMaxSize", "<init>", "(Ljava/io/RandomAccessFile;Lokio/Source;JLokio/ByteString;J)V", "getFile", "()Ljava/io/RandomAccessFile;", "setFile", "(Ljava/io/RandomAccessFile;)V", "getUpstream", "()Lokio/Source;", "setUpstream", "(Lokio/Source;)V", "getUpstreamPos", "()J", "setUpstreamPos", "(J)V", "getBufferMaxSize", "upstreamReader", "Ljava/lang/Thread;", "getUpstreamReader", "()Ljava/lang/Thread;", "setUpstreamReader", "(Ljava/lang/Thread;)V", "upstreamBuffer", "Lokio/Buffer;", "getUpstreamBuffer", "()Lokio/Buffer;", "complete", "", "getComplete", "()Z", "setComplete", "(Z)V", "buffer", "getBuffer", "sourceCount", "", "getSourceCount", "()I", "setSourceCount", "(I)V", "isClosed", "writeHeader", "", "prefix", "upstreamSize", "metadataSize", "writeMetadata", "commit", "newSource", "RelaySource", "Companion", "okhttp"}
)
public final class Relay implements Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @Nullable
   private RandomAccessFile file;
   @Nullable
   private Source upstream;
   private long upstreamPos;
   @NotNull
   private final ByteString metadata;
   private final long bufferMaxSize;
   @Nullable
   private Thread upstreamReader;
   @NotNull
   private final Buffer upstreamBuffer;
   private boolean complete;
   @NotNull
   private final Buffer buffer;
   private int sourceCount;
   private static final int SOURCE_UPSTREAM = 1;
   private static final int SOURCE_FILE = 2;
   @JvmField
   @NotNull
   public static final ByteString PREFIX_CLEAN;
   @JvmField
   @NotNull
   public static final ByteString PREFIX_DIRTY;
   private static final long FILE_HEADER_SIZE = 32L;

   private Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize) {
      this.file = file;
      this.upstream = upstream;
      this.upstreamPos = upstreamPos;
      this.metadata = metadata;
      this.bufferMaxSize = bufferMaxSize;
      this.upstreamBuffer = new Buffer();
      this.complete = this.upstream == null;
      this.buffer = new Buffer();
   }

   @Nullable
   public final RandomAccessFile getFile() {
      return this.file;
   }

   public final void setFile(@Nullable RandomAccessFile var1) {
      this.file = var1;
   }

   @Nullable
   public final Source getUpstream() {
      return this.upstream;
   }

   public final void setUpstream(@Nullable Source var1) {
      this.upstream = var1;
   }

   public final long getUpstreamPos() {
      return this.upstreamPos;
   }

   public final void setUpstreamPos(long var1) {
      this.upstreamPos = var1;
   }

   public final long getBufferMaxSize() {
      return this.bufferMaxSize;
   }

   @Nullable
   public final Thread getUpstreamReader() {
      return this.upstreamReader;
   }

   public final void setUpstreamReader(@Nullable Thread var1) {
      this.upstreamReader = var1;
   }

   @NotNull
   public final Buffer getUpstreamBuffer() {
      return this.upstreamBuffer;
   }

   public final boolean getComplete() {
      return this.complete;
   }

   public final void setComplete(boolean var1) {
      this.complete = var1;
   }

   @NotNull
   public final Buffer getBuffer() {
      return this.buffer;
   }

   public final int getSourceCount() {
      return this.sourceCount;
   }

   public final void setSourceCount(int var1) {
      this.sourceCount = var1;
   }

   public final boolean isClosed() {
      return this.file == null;
   }

   private final void writeHeader(ByteString prefix, long upstreamSize, long metadataSize) throws IOException {
      Buffer $this$writeHeader_u24lambda_u240 = new Buffer();
      int var9 = 0;
      $this$writeHeader_u24lambda_u240.write(prefix);
      $this$writeHeader_u24lambda_u240.writeLong(upstreamSize);
      $this$writeHeader_u24lambda_u240.writeLong(metadataSize);
      if ($this$writeHeader_u24lambda_u240.size() != 32L) {
         String var10 = "Failed requirement.";
         throw new IllegalArgumentException(var10.toString());
      } else {
         Buffer header = $this$writeHeader_u24lambda_u240;
         RandomAccessFile var10002 = this.file;
         Intrinsics.checkNotNull(var10002);
         FileChannel var12 = var10002.getChannel();
         Intrinsics.checkNotNullExpressionValue(var12, "getChannel(...)");
         FileOperator fileOperator = new FileOperator(var12);
         fileOperator.write(0L, header, 32L);
      }
   }

   private final void writeMetadata(long upstreamSize) throws IOException {
      Buffer metadataBuffer = new Buffer();
      metadataBuffer.write(this.metadata);
      RandomAccessFile var10002 = this.file;
      Intrinsics.checkNotNull(var10002);
      FileChannel var5 = var10002.getChannel();
      Intrinsics.checkNotNullExpressionValue(var5, "getChannel(...)");
      FileOperator fileOperator = new FileOperator(var5);
      fileOperator.write(32L + upstreamSize, metadataBuffer, (long)this.metadata.size());
   }

   public final void commit(long upstreamSize) throws IOException {
      this.writeMetadata(upstreamSize);
      RandomAccessFile var10000 = this.file;
      Intrinsics.checkNotNull(var10000);
      var10000.getChannel().force(false);
      this.writeHeader(PREFIX_CLEAN, upstreamSize, (long)this.metadata.size());
      var10000 = this.file;
      Intrinsics.checkNotNull(var10000);
      var10000.getChannel().force(false);
      synchronized(this) {
         int var4 = 0;
         this.complete = true;
         Unit var7 = Unit.INSTANCE;
      }

      Source var9 = this.upstream;
      if (var9 != null) {
         _UtilCommonKt.closeQuietly(var9);
      }

      this.upstream = null;
   }

   @NotNull
   public final ByteString metadata() {
      return this.metadata;
   }

   @Nullable
   public final Source newSource() {
      synchronized(this){}

      Object var4;
      try {
         int var2 = 0;
         if (this.file != null) {
            int var3 = this.sourceCount++;
            return new RelaySource();
         }

         var4 = null;
      } finally {
         ;
      }

      return (Source)var4;
   }

   // $FF: synthetic method
   public Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize, DefaultConstructorMarker $constructor_marker) {
      this(file, upstream, upstreamPos, metadata, bufferMaxSize);
   }

   static {
      PREFIX_CLEAN = ByteString.Companion.encodeUtf8("OkHttp cache v1\n");
      PREFIX_DIRTY = ByteString.Companion.encodeUtf8("OkHttp DIRTY :(\n");
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\tH\u0016J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u000e\u001a\u00020\u000fH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0010"},
      d2 = {"Lokhttp3/internal/cache2/Relay$RelaySource;", "Lokio/Source;", "<init>", "(Lokhttp3/internal/cache2/Relay;)V", "timeout", "Lokio/Timeout;", "fileOperator", "Lokhttp3/internal/cache2/FileOperator;", "sourcePos", "", "read", "sink", "Lokio/Buffer;", "byteCount", "close", "", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nRelay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Relay.kt\nokhttp3/internal/cache2/Relay$RelaySource\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,362:1\n38#2:363\n*S KotlinDebug\n*F\n+ 1 Relay.kt\nokhttp3/internal/cache2/Relay$RelaySource\n*L\n272#1:363\n*E\n"})
   public final class RelaySource implements Source {
      @NotNull
      private final Timeout timeout = new Timeout();
      @Nullable
      private FileOperator fileOperator;
      private long sourcePos;

      public RelaySource() {
         RandomAccessFile var10003 = Relay.this.getFile();
         Intrinsics.checkNotNull(var10003);
         FileChannel var2 = var10003.getChannel();
         Intrinsics.checkNotNullExpressionValue(var2, "getChannel(...)");
         this.fileOperator = new FileOperator(var2);
      }

      public long read(@NotNull Buffer sink, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (this.fileOperator == null) {
            throw new IllegalStateException("Check failed.");
         } else {
            Relay var5 = Relay.this;
            Relay var6 = Relay.this;
            synchronized(var5){}

            int var41;
            try {
               var41 = 0;

               byte var10000;
               while(true) {
                  long upstreamPos = var6.getUpstreamPos();
                  if (this.sourcePos != upstreamPos) {
                     upstreamPos = var6.getUpstreamPos() - var6.getBuffer().size();
                     if (this.sourcePos >= upstreamPos) {
                        long bytesToRead = Math.min(byteCount, var6.getUpstreamPos() - this.sourcePos);
                        var6.getBuffer().copyTo(sink, this.sourcePos - upstreamPos, bytesToRead);
                        this.sourcePos += bytesToRead;
                        long var14 = bytesToRead;
                        return var14;
                     }

                     var10000 = 2;
                     break;
                  }

                  if (var6.getComplete()) {
                     long var16 = -1L;
                     return var16;
                  }

                  if (var6.getUpstreamReader() == null) {
                     var6.setUpstreamReader(Thread.currentThread());
                     var10000 = 1;
                     break;
                  }

                  this.timeout.waitUntilNotified(var6);
               }

               var41 = var10000;
            } finally {
               ;
            }

            if (var41 == 2) {
               long bytesToRead = Math.min(byteCount, Relay.this.getUpstreamPos() - this.sourcePos);
               FileOperator var66 = this.fileOperator;
               Intrinsics.checkNotNull(var66);
               var66.read(32L + this.sourcePos, sink, bytesToRead);
               this.sourcePos += bytesToRead;
               return bytesToRead;
            } else {
               boolean var24 = false;

               long bytesRead;
               label328: {
                  long var45;
                  try {
                     var24 = true;
                     Source var64 = Relay.this.getUpstream();
                     Intrinsics.checkNotNull(var64);
                     long upstreamBytesRead = var64.read(Relay.this.getUpstreamBuffer(), Relay.this.getBufferMaxSize());
                     if (upstreamBytesRead == -1L) {
                        Relay.this.commit(Relay.this.getUpstreamPos());
                        bytesRead = -1L;
                        var24 = false;
                        break label328;
                     }

                     bytesRead = Math.min(upstreamBytesRead, byteCount);
                     Relay.this.getUpstreamBuffer().copyTo(sink, 0L, bytesRead);
                     this.sourcePos += bytesRead;
                     FileOperator var65 = this.fileOperator;
                     Intrinsics.checkNotNull(var65);
                     var65.write(32L + Relay.this.getUpstreamPos(), Relay.this.getUpstreamBuffer().clone(), upstreamBytesRead);
                     Relay bufferPos = Relay.this;
                     Relay var10 = Relay.this;
                     synchronized(bufferPos) {
                        int var12 = 0;
                        var10.getBuffer().write(var10.getUpstreamBuffer(), upstreamBytesRead);
                        if (var10.getBuffer().size() > var10.getBufferMaxSize()) {
                           var10.getBuffer().skip(var10.getBuffer().size() - var10.getBufferMaxSize());
                        }

                        var10.setUpstreamPos(var10.getUpstreamPos() + upstreamBytesRead);
                        Unit var52 = Unit.INSTANCE;
                     }

                     var45 = bytesRead;
                     var24 = false;
                  } finally {
                     if (var24) {
                        Relay var40 = Relay.this;
                        Relay var42 = Relay.this;
                        synchronized(var40) {
                           int var47 = 0;
                           var42.setUpstreamReader((Thread)null);
                           Lockable $this$notifyAll$iv = var42;
                           int $i$f$notifyAll = 0;
                           Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
                           ((Object)$this$notifyAll$iv).notifyAll();
                           Unit var48 = Unit.INSTANCE;
                        }
                     }
                  }

                  Relay var51 = Relay.this;
                  Relay var54 = Relay.this;
                  synchronized(var51) {
                     int var57 = 0;
                     var54.setUpstreamReader((Thread)null);
                     Lockable $this$notifyAll$iv = var54;
                     int $i$f$notifyAll = 0;
                     Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
                     ((Object)$this$notifyAll$iv).notifyAll();
                     Unit var58 = Unit.INSTANCE;
                     return var45;
                  }
               }

               Relay var46 = Relay.this;
               Relay var49 = Relay.this;
               synchronized(var46) {
                  int var55 = 0;
                  var49.setUpstreamReader((Thread)null);
                  Lockable $this$notifyAll$iv = var49;
                  int $i$f$notifyAll = 0;
                  Intrinsics.checkNotNull($this$notifyAll$iv, "null cannot be cast to non-null type java.lang.Object");
                  ((Object)$this$notifyAll$iv).notifyAll();
                  Unit var56 = Unit.INSTANCE;
                  return bytesRead;
               }
            }
         }
      }

      @NotNull
      public Timeout timeout() {
         return this.timeout;
      }

      public void close() throws IOException {
         if (this.fileOperator != null) {
            this.fileOperator = null;
            Object fileToClose = null;
            Relay var2 = Relay.this;
            Relay var3 = Relay.this;
            synchronized(var2) {
               int var5 = 0;
               int var6 = var3.getSourceCount();
               var3.setSourceCount(var6 + -1);
               if (var3.getSourceCount() == 0) {
                  fileToClose = var3.getFile();
                  var3.setFile((RandomAccessFile)null);
               }

               Unit var8 = Unit.INSTANCE;
            }

            if (fileToClose != null) {
               _UtilCommonKt.closeQuietly((Closeable)fileToClose);
            }

         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u000bJ\u000e\u0010\u0014\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0015"},
      d2 = {"Lokhttp3/internal/cache2/Relay$Companion;", "", "<init>", "()V", "SOURCE_UPSTREAM", "", "SOURCE_FILE", "PREFIX_CLEAN", "Lokio/ByteString;", "PREFIX_DIRTY", "FILE_HEADER_SIZE", "", "edit", "Lokhttp3/internal/cache2/Relay;", "file", "Ljava/io/File;", "upstream", "Lokio/Source;", "metadata", "bufferMaxSize", "read", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Relay edit(@NotNull File file, @NotNull Source upstream, @NotNull ByteString metadata, long bufferMaxSize) throws IOException {
         Intrinsics.checkNotNullParameter(file, "file");
         Intrinsics.checkNotNullParameter(upstream, "upstream");
         Intrinsics.checkNotNullParameter(metadata, "metadata");
         RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
         Relay result = new Relay(randomAccessFile, upstream, 0L, metadata, bufferMaxSize, (DefaultConstructorMarker)null);
         randomAccessFile.setLength(0L);
         result.writeHeader(Relay.PREFIX_DIRTY, -1L, -1L);
         return result;
      }

      @NotNull
      public final Relay read(@NotNull File file) throws IOException {
         Intrinsics.checkNotNullParameter(file, "file");
         RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
         FileChannel var10002 = randomAccessFile.getChannel();
         Intrinsics.checkNotNullExpressionValue(var10002, "getChannel(...)");
         FileOperator fileOperator = new FileOperator(var10002);
         Buffer header = new Buffer();
         fileOperator.read(0L, header, 32L);
         ByteString prefix = header.readByteString((long)Relay.PREFIX_CLEAN.size());
         if (!Intrinsics.areEqual((Object)prefix, (Object)Relay.PREFIX_CLEAN)) {
            throw new IOException("unreadable cache file");
         } else {
            long upstreamSize = header.readLong();
            long metadataSize = header.readLong();
            Buffer metadataBuffer = new Buffer();
            fileOperator.read(32L + upstreamSize, metadataBuffer, metadataSize);
            ByteString metadata = metadataBuffer.readByteString();
            return new Relay(randomAccessFile, (Source)null, upstreamSize, metadata, 0L, (DefaultConstructorMarker)null);
         }
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
