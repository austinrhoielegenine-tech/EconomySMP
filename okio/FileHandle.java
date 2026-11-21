package okio;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\b&\u0018\u00002\u00060\u0001j\u0002`\u0002:\u0002/0B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J&\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u000bJ\u001e\u0010\u0012\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0018\u001a\u00020\u0014J\u0006\u0010\u001b\u001a\u00020\u0014J\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u0014J&\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u000bJ\u001e\u0010\u001e\u001a\u00020\u001d2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u001a2\u0006\u0010\u0018\u001a\u00020\u0014J\u0006\u0010 \u001a\u00020\u001dJ\u0010\u0010\u001f\u001a\u00020!2\b\b\u0002\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\"\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020!J\u0016\u0010#\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0014J\u0010\u0010\u0019\u001a\u00020$2\b\b\u0002\u0010\u0013\u001a\u00020\u0014J\u0006\u0010%\u001a\u00020$J\u000e\u0010\"\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020$J\u0016\u0010#\u001a\u00020\u001d2\u0006\u0010\u0019\u001a\u00020$2\u0006\u0010\"\u001a\u00020\u0014J\u0006\u0010&\u001a\u00020\u001dJ(\u0010'\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u000bH$J(\u0010(\u001a\u00020\u001d2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000b2\u0006\u0010\u0018\u001a\u00020\u000bH$J\b\u0010)\u001a\u00020\u001dH$J\u0010\u0010*\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u0014H$J\b\u0010+\u001a\u00020\u0014H$J\b\u0010,\u001a\u00020\u001dH$J \u0010-\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0018\u001a\u00020\u0014H\u0002J \u0010.\u001a\u00020\u001d2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u001a2\u0006\u0010\u0018\u001a\u00020\u0014H\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\u00060\rj\u0002`\u000e¢\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010¨\u00061"},
   d2 = {"Lokio/FileHandle;", "Ljava/io/Closeable;", "Lokio/Closeable;", "readWrite", "", "<init>", "(Z)V", "getReadWrite", "()Z", "closed", "openStreamCount", "", "lock", "Ljava/util/concurrent/locks/ReentrantLock;", "Lokio/Lock;", "getLock", "()Ljava/util/concurrent/locks/ReentrantLock;", "Ljava/util/concurrent/locks/ReentrantLock;", "read", "fileOffset", "", "array", "", "arrayOffset", "byteCount", "sink", "Lokio/Buffer;", "size", "resize", "", "write", "source", "flush", "Lokio/Source;", "position", "reposition", "Lokio/Sink;", "appendingSink", "close", "protectedRead", "protectedWrite", "protectedFlush", "protectedResize", "protectedSize", "protectedClose", "readNoCloseCheck", "writeNoCloseCheck", "FileHandleSink", "FileHandleSource", "okio"}
)
@SourceDebugExtension({"SMAP\nFileHandle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileHandle.kt\nokio/FileHandle\n+ 2 -JvmPlatform.kt\nokio/_JvmPlatformKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 5 RealBufferedSink.kt\nokio/RealBufferedSink\n+ 6 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,444:1\n40#2:445\n40#2:447\n40#2:448\n40#2:449\n40#2:450\n40#2:451\n40#2:452\n40#2:453\n40#2:457\n40#2:459\n1#3:446\n63#4:454\n63#4:455\n63#4:456\n51#5:458\n85#6:460\n85#6:461\n*S KotlinDebug\n*F\n+ 1 FileHandle.kt\nokio/FileHandle\n*L\n69#1:445\n81#1:447\n92#1:448\n105#1:449\n119#1:450\n129#1:451\n139#1:452\n151#1:453\n221#1:457\n287#1:459\n169#1:454\n195#1:455\n202#1:456\n248#1:458\n345#1:460\n374#1:461\n*E\n"})
public abstract class FileHandle implements Closeable {
   private final boolean readWrite;
   private boolean closed;
   private int openStreamCount;
   @NotNull
   private final ReentrantLock lock;

   public FileHandle(boolean readWrite) {
      this.readWrite = readWrite;
      this.lock = _JvmPlatformKt.newLock();
   }

   public final boolean getReadWrite() {
      return this.readWrite;
   }

   @NotNull
   public final ReentrantLock getLock() {
      return this.lock;
   }

   public final int read(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(array, "array");
      ReentrantLock $this$withLock$iv = this.lock;
      int $i$f$withLock = 0;
      Lock var8 = (Lock)$this$withLock$iv;
      var8.lock();

      try {
         int var9 = 0;
         if (this.closed) {
            int var10 = 0;
            String var14 = "closed";
            throw new IllegalStateException(var14.toString());
         }

         Unit var13 = Unit.INSTANCE;
      } finally {
         var8.unlock();
      }

      return this.protectedRead(fileOffset, array, arrayOffset, byteCount);
   }

   public final long read(long fileOffset, @NotNull Buffer sink, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      ReentrantLock $this$withLock$iv = this.lock;
      int $i$f$withLock = 0;
      Lock var8 = (Lock)$this$withLock$iv;
      var8.lock();

      try {
         int var9 = 0;
         if (this.closed) {
            int var10 = 0;
            String var14 = "closed";
            throw new IllegalStateException(var14.toString());
         }

         Unit var13 = Unit.INSTANCE;
      } finally {
         var8.unlock();
      }

      return this.readNoCloseCheck(fileOffset, sink, byteCount);
   }

   public final long size() throws IOException {
      ReentrantLock $this$withLock$iv = this.lock;
      int $i$f$withLock = 0;
      Lock var3 = (Lock)$this$withLock$iv;
      var3.lock();

      try {
         int var4 = 0;
         if (this.closed) {
            int var5 = 0;
            String var9 = "closed";
            throw new IllegalStateException(var9.toString());
         }

         Unit var8 = Unit.INSTANCE;
      } finally {
         var3.unlock();
      }

      return this.protectedSize();
   }

   public final void resize(long size) throws IOException {
      if (!this.readWrite) {
         int $i$f$withLock = 0;
         String var11 = "file handle is read-only";
         throw new IllegalStateException(var11.toString());
      } else {
         ReentrantLock $this$withLock$iv = this.lock;
         int $i$f$withLock = 0;
         Lock var5 = (Lock)$this$withLock$iv;
         var5.lock();

         try {
            int var6 = 0;
            if (this.closed) {
               int var7 = 0;
               String var13 = "closed";
               throw new IllegalStateException(var13.toString());
            }

            Unit var12 = Unit.INSTANCE;
         } finally {
            var5.unlock();
         }

         this.protectedResize(size);
      }
   }

   public final void write(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(array, "array");
      if (!this.readWrite) {
         int $i$f$withLock = 0;
         String var14 = "file handle is read-only";
         throw new IllegalStateException(var14.toString());
      } else {
         ReentrantLock $this$withLock$iv = this.lock;
         int $i$f$withLock = 0;
         Lock var8 = (Lock)$this$withLock$iv;
         var8.lock();

         try {
            int var9 = 0;
            if (this.closed) {
               int var10 = 0;
               String var16 = "closed";
               throw new IllegalStateException(var16.toString());
            }

            Unit var15 = Unit.INSTANCE;
         } finally {
            var8.unlock();
         }

         this.protectedWrite(fileOffset, array, arrayOffset, byteCount);
      }
   }

   public final void write(long fileOffset, @NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      if (!this.readWrite) {
         int $i$f$withLock = 0;
         String var14 = "file handle is read-only";
         throw new IllegalStateException(var14.toString());
      } else {
         ReentrantLock $this$withLock$iv = this.lock;
         int $i$f$withLock = 0;
         Lock var8 = (Lock)$this$withLock$iv;
         var8.lock();

         try {
            int var9 = 0;
            if (this.closed) {
               int var10 = 0;
               String var16 = "closed";
               throw new IllegalStateException(var16.toString());
            }

            Unit var15 = Unit.INSTANCE;
         } finally {
            var8.unlock();
         }

         this.writeNoCloseCheck(fileOffset, source, byteCount);
      }
   }

   public final void flush() throws IOException {
      if (!this.readWrite) {
         int $i$f$withLock = 0;
         String var9 = "file handle is read-only";
         throw new IllegalStateException(var9.toString());
      } else {
         ReentrantLock $this$withLock$iv = this.lock;
         int $i$f$withLock = 0;
         Lock var3 = (Lock)$this$withLock$iv;
         var3.lock();

         try {
            int var4 = 0;
            if (this.closed) {
               int var5 = 0;
               String var11 = "closed";
               throw new IllegalStateException(var11.toString());
            }

            Unit var10 = Unit.INSTANCE;
         } finally {
            var3.unlock();
         }

         this.protectedFlush();
      }
   }

   @NotNull
   public final Source source(long fileOffset) throws IOException {
      ReentrantLock $this$withLock$iv = this.lock;
      int $i$f$withLock = 0;
      Lock var5 = (Lock)$this$withLock$iv;
      var5.lock();

      try {
         int var6 = 0;
         if (this.closed) {
            int var7 = 0;
            String var11 = "closed";
            throw new IllegalStateException(var11.toString());
         }

         int var8 = this.openStreamCount++;
      } finally {
         var5.unlock();
      }

      return new FileHandleSource(this, fileOffset);
   }

   // $FF: synthetic method
   public static Source source$default(FileHandle var0, long var1, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: source");
      } else {
         if ((var3 & 1) != 0) {
            var1 = 0L;
         }

         return var0.source(var1);
      }
   }

   public final long position(@NotNull Source source) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Source source = source;
      long bufferSize = 0L;
      if (source instanceof RealBufferedSource) {
         RealBufferedSource this_$iv = (RealBufferedSource)source;
         int $i$f$getBuffer = 0;
         bufferSize = this_$iv.bufferField.size();
         source = ((RealBufferedSource)source).source;
      }

      if (!(source instanceof FileHandleSource) || ((FileHandleSource)source).getFileHandle() != this) {
         int var9 = 0;
         String var10 = "source was not created by this FileHandle";
         throw new IllegalArgumentException(var10.toString());
      } else if (((FileHandleSource)source).getClosed()) {
         int var7 = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         return ((FileHandleSource)source).getPosition() - bufferSize;
      }
   }

   public final void reposition(@NotNull Source source, long position) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      if (source instanceof RealBufferedSource) {
         Source fileHandleSource = ((RealBufferedSource)source).source;
         if (!(fileHandleSource instanceof FileHandleSource) || ((FileHandleSource)fileHandleSource).getFileHandle() != this) {
            int var16 = 0;
            String var17 = "source was not created by this FileHandle";
            throw new IllegalArgumentException(var17.toString());
         }

         if (((FileHandleSource)fileHandleSource).getClosed()) {
            int var6 = 0;
            String var15 = "closed";
            throw new IllegalStateException(var15.toString());
         }

         RealBufferedSource this_$iv = (RealBufferedSource)source;
         int $i$f$getBuffer = 0;
         long bufferSize = this_$iv.bufferField.size();
         long toSkip = position - (((FileHandleSource)fileHandleSource).getPosition() - bufferSize);
         if (0L <= toSkip ? toSkip < bufferSize : false) {
            ((RealBufferedSource)source).skip(toSkip);
         } else {
            RealBufferedSource this_$iv = (RealBufferedSource)source;
            int $i$f$getBuffer = 0;
            this_$iv.bufferField.clear();
            ((FileHandleSource)fileHandleSource).setPosition(position);
         }
      } else {
         if (!(source instanceof FileHandleSource) || ((FileHandleSource)source).getFileHandle() != this) {
            int var13 = 0;
            String var14 = "source was not created by this FileHandle";
            throw new IllegalArgumentException(var14.toString());
         }

         if (((FileHandleSource)source).getClosed()) {
            int var11 = 0;
            String var12 = "closed";
            throw new IllegalStateException(var12.toString());
         }

         ((FileHandleSource)source).setPosition(position);
      }

   }

   @NotNull
   public final Sink sink(long fileOffset) throws IOException {
      if (!this.readWrite) {
         int $i$f$withLock = 0;
         String var12 = "file handle is read-only";
         throw new IllegalStateException(var12.toString());
      } else {
         ReentrantLock $this$withLock$iv = this.lock;
         int $i$f$withLock = 0;
         Lock var5 = (Lock)$this$withLock$iv;
         var5.lock();

         try {
            int var6 = 0;
            if (this.closed) {
               int var7 = 0;
               String var13 = "closed";
               throw new IllegalStateException(var13.toString());
            }

            int var8 = this.openStreamCount++;
         } finally {
            var5.unlock();
         }

         return new FileHandleSink(this, fileOffset);
      }
   }

   // $FF: synthetic method
   public static Sink sink$default(FileHandle var0, long var1, int var3, Object var4) throws IOException {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: sink");
      } else {
         if ((var3 & 1) != 0) {
            var1 = 0L;
         }

         return var0.sink(var1);
      }
   }

   @NotNull
   public final Sink appendingSink() throws IOException {
      return this.sink(this.size());
   }

   public final long position(@NotNull Sink sink) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Sink sink = sink;
      long bufferSize = 0L;
      if (sink instanceof RealBufferedSink) {
         RealBufferedSink this_$iv = (RealBufferedSink)sink;
         int $i$f$getBuffer = 0;
         bufferSize = this_$iv.bufferField.size();
         sink = ((RealBufferedSink)sink).sink;
      }

      if (!(sink instanceof FileHandleSink) || ((FileHandleSink)sink).getFileHandle() != this) {
         int var9 = 0;
         String var10 = "sink was not created by this FileHandle";
         throw new IllegalArgumentException(var10.toString());
      } else if (((FileHandleSink)sink).getClosed()) {
         int var7 = 0;
         String var8 = "closed";
         throw new IllegalStateException(var8.toString());
      } else {
         return ((FileHandleSink)sink).getPosition() + bufferSize;
      }
   }

   public final void reposition(@NotNull Sink sink, long position) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (sink instanceof RealBufferedSink) {
         Sink fileHandleSink = ((RealBufferedSink)sink).sink;
         if (!(fileHandleSink instanceof FileHandleSink) || ((FileHandleSink)fileHandleSink).getFileHandle() != this) {
            int var11 = 0;
            String var12 = "sink was not created by this FileHandle";
            throw new IllegalArgumentException(var12.toString());
         }

         if (((FileHandleSink)fileHandleSink).getClosed()) {
            int var6 = 0;
            String var10 = "closed";
            throw new IllegalStateException(var10.toString());
         }

         ((RealBufferedSink)sink).emit();
         ((FileHandleSink)fileHandleSink).setPosition(position);
      } else {
         if (!(sink instanceof FileHandleSink) || ((FileHandleSink)sink).getFileHandle() != this) {
            int var8 = 0;
            String var9 = "sink was not created by this FileHandle";
            throw new IllegalArgumentException(var9.toString());
         }

         if (((FileHandleSink)sink).getClosed()) {
            int var5 = 0;
            String var7 = "closed";
            throw new IllegalStateException(var7.toString());
         }

         ((FileHandleSink)sink).setPosition(position);
      }

   }

   public final void close() throws IOException {
      ReentrantLock $this$withLock$iv = this.lock;
      int $i$f$withLock = 0;
      Lock var3 = (Lock)$this$withLock$iv;
      var3.lock();

      label62: {
         try {
            int var4 = 0;
            if (!this.closed) {
               this.closed = true;
               if (this.openStreamCount != 0) {
                  return;
               }

               Unit var8 = Unit.INSTANCE;
               break label62;
            }
         } finally {
            var3.unlock();
         }

         return;
      }

      this.protectedClose();
   }

   protected abstract int protectedRead(long var1, @NotNull byte[] var3, int var4, int var5) throws IOException;

   protected abstract void protectedWrite(long var1, @NotNull byte[] var3, int var4, int var5) throws IOException;

   protected abstract void protectedFlush() throws IOException;

   protected abstract void protectedResize(long var1) throws IOException;

   protected abstract long protectedSize() throws IOException;

   protected abstract void protectedClose() throws IOException;

   private final long readNoCloseCheck(long fileOffset, Buffer sink, long byteCount) {
      if (byteCount < 0L) {
         int var7 = 0;
         String var16 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var16.toString());
      } else {
         long currentOffset = fileOffset;
         long targetOffset = fileOffset + byteCount;

         while(currentOffset < targetOffset) {
            Segment tail = sink.writableSegment$okio(1);
            long a$iv = targetOffset - currentOffset;
            int b$iv = 8192 - tail.limit;
            int $i$f$minOf = 0;
            int readByteCount = this.protectedRead(currentOffset, tail.data, tail.limit, (int)Math.min(a$iv, (long)b$iv));
            if (readByteCount == -1) {
               if (tail.pos == tail.limit) {
                  sink.head = tail.pop();
                  SegmentPool.recycle(tail);
               }

               if (fileOffset == currentOffset) {
                  return -1L;
               }
               break;
            }

            tail.limit += readByteCount;
            currentOffset += (long)readByteCount;
            sink.setSize$okio(sink.size() + (long)readByteCount);
         }

         return currentOffset - fileOffset;
      }
   }

   private final void writeNoCloseCheck(long fileOffset, Buffer source, long byteCount) {
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      long currentOffset = fileOffset;
      long targetOffset = fileOffset + byteCount;

      while(currentOffset < targetOffset) {
         Segment var10000 = source.head;
         Intrinsics.checkNotNull(var10000);
         Segment head = var10000;
         long a$iv = targetOffset - currentOffset;
         int b$iv = head.limit - head.pos;
         int $i$f$minOf = 0;
         int toCopy = (int)Math.min(a$iv, (long)b$iv);
         this.protectedWrite(currentOffset, head.data, head.pos, toCopy);
         head.pos += toCopy;
         currentOffset += (long)toCopy;
         source.setSize$okio(source.size() - (long)toCopy);
         if (head.pos == head.limit) {
            source.head = head.pop();
            SegmentPool.recycle(head);
         }
      }

   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0005H\u0016J\b\u0010\u0019\u001a\u00020\u0015H\u0016J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\b\u0010\u001c\u001a\u00020\u0015H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013¨\u0006\u001d"},
      d2 = {"Lokio/FileHandle$FileHandleSink;", "Lokio/Sink;", "fileHandle", "Lokio/FileHandle;", "position", "", "<init>", "(Lokio/FileHandle;J)V", "getFileHandle", "()Lokio/FileHandle;", "getPosition", "()J", "setPosition", "(J)V", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "write", "", "source", "Lokio/Buffer;", "byteCount", "flush", "timeout", "Lokio/Timeout;", "close", "okio"}
   )
   @SourceDebugExtension({"SMAP\nFileHandle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSink\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 -JvmPlatform.kt\nokio/_JvmPlatformKt\n*L\n1#1,444:1\n1#2:445\n40#3:446\n*S KotlinDebug\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSink\n*L\n410#1:446\n*E\n"})
   private static final class FileHandleSink implements Sink {
      @NotNull
      private final FileHandle fileHandle;
      private long position;
      private boolean closed;

      public FileHandleSink(@NotNull FileHandle fileHandle, long position) {
         Intrinsics.checkNotNullParameter(fileHandle, "fileHandle");
         super();
         this.fileHandle = fileHandle;
         this.position = position;
      }

      @NotNull
      public final FileHandle getFileHandle() {
         return this.fileHandle;
      }

      public final long getPosition() {
         return this.position;
      }

      public final void setPosition(long var1) {
         this.position = var1;
      }

      public final boolean getClosed() {
         return this.closed;
      }

      public final void setClosed(boolean var1) {
         this.closed = var1;
      }

      public void write(@NotNull Buffer source, long byteCount) {
         Intrinsics.checkNotNullParameter(source, "source");
         if (this.closed) {
            int var4 = 0;
            String var5 = "closed";
            throw new IllegalStateException(var5.toString());
         } else {
            this.fileHandle.writeNoCloseCheck(this.position, source, byteCount);
            this.position += byteCount;
         }
      }

      public void flush() {
         if (this.closed) {
            int var1 = 0;
            String var2 = "closed";
            throw new IllegalStateException(var2.toString());
         } else {
            this.fileHandle.protectedFlush();
         }
      }

      @NotNull
      public Timeout timeout() {
         return Timeout.NONE;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            ReentrantLock $this$withLock$iv = this.fileHandle.getLock();
            int $i$f$withLock = 0;
            Lock var3 = (Lock)$this$withLock$iv;
            var3.lock();

            label51: {
               try {
                  int var4 = 0;
                  FileHandle var5 = this.fileHandle;
                  int var6 = var5.openStreamCount;
                  var5.openStreamCount = var6 + -1;
                  if (this.fileHandle.openStreamCount == 0 && this.fileHandle.closed) {
                     Unit var9 = Unit.INSTANCE;
                     break label51;
                  }
               } finally {
                  var3.unlock();
               }

               return;
            }

            this.fileHandle.protectedClose();
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0005H\u0016J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010\u001a\u001a\u00020\u001bH\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013¨\u0006\u001c"},
      d2 = {"Lokio/FileHandle$FileHandleSource;", "Lokio/Source;", "fileHandle", "Lokio/FileHandle;", "position", "", "<init>", "(Lokio/FileHandle;J)V", "getFileHandle", "()Lokio/FileHandle;", "getPosition", "()J", "setPosition", "(J)V", "closed", "", "getClosed", "()Z", "setClosed", "(Z)V", "read", "sink", "Lokio/Buffer;", "byteCount", "timeout", "Lokio/Timeout;", "close", "", "okio"}
   )
   @SourceDebugExtension({"SMAP\nFileHandle.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 -JvmPlatform.kt\nokio/_JvmPlatformKt\n*L\n1#1,444:1\n1#2:445\n40#3:446\n*S KotlinDebug\n*F\n+ 1 FileHandle.kt\nokio/FileHandle$FileHandleSource\n*L\n436#1:446\n*E\n"})
   private static final class FileHandleSource implements Source {
      @NotNull
      private final FileHandle fileHandle;
      private long position;
      private boolean closed;

      public FileHandleSource(@NotNull FileHandle fileHandle, long position) {
         Intrinsics.checkNotNullParameter(fileHandle, "fileHandle");
         super();
         this.fileHandle = fileHandle;
         this.position = position;
      }

      @NotNull
      public final FileHandle getFileHandle() {
         return this.fileHandle;
      }

      public final long getPosition() {
         return this.position;
      }

      public final void setPosition(long var1) {
         this.position = var1;
      }

      public final boolean getClosed() {
         return this.closed;
      }

      public final void setClosed(boolean var1) {
         this.closed = var1;
      }

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (this.closed) {
            int var5 = 0;
            String var6 = "closed";
            throw new IllegalStateException(var6.toString());
         } else {
            long result = this.fileHandle.readNoCloseCheck(this.position, sink, byteCount);
            if (result != -1L) {
               this.position += result;
            }

            return result;
         }
      }

      @NotNull
      public Timeout timeout() {
         return Timeout.NONE;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            ReentrantLock $this$withLock$iv = this.fileHandle.getLock();
            int $i$f$withLock = 0;
            Lock var3 = (Lock)$this$withLock$iv;
            var3.lock();

            label51: {
               try {
                  int var4 = 0;
                  FileHandle var5 = this.fileHandle;
                  int var6 = var5.openStreamCount;
                  var5.openStreamCount = var6 + -1;
                  if (this.fileHandle.openStreamCount == 0 && this.fileHandle.closed) {
                     Unit var9 = Unit.INSTANCE;
                     break label51;
                  }
               } finally {
                  var3.unlock();
               }

               return;
            }

            this.fileHandle.protectedClose();
         }
      }
   }
}
