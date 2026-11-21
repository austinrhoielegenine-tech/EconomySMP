package okio;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0019\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\bJ\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eH\u0016J\u0016\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000eJ\u0006\u0010\u0013\u001a\u00020\fJ\b\u0010\u0014\u001a\u00020\u0015H\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0015H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0019"},
   d2 = {"Lokio/InflaterSource;", "Lokio/Source;", "source", "Lokio/BufferedSource;", "inflater", "Ljava/util/zip/Inflater;", "<init>", "(Lokio/BufferedSource;Ljava/util/zip/Inflater;)V", "(Lokio/Source;Ljava/util/zip/Inflater;)V", "bufferBytesHeldByInflater", "", "closed", "", "read", "", "sink", "Lokio/Buffer;", "byteCount", "readOrInflate", "refill", "releaseBytesAfterInflate", "", "timeout", "Lokio/Timeout;", "close", "okio"}
)
@SourceDebugExtension({"SMAP\nInflaterSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InflaterSource.kt\nokio/InflaterSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,132:1\n1#2:133\n85#3:134\n*S KotlinDebug\n*F\n+ 1 InflaterSource.kt\nokio/InflaterSource\n*L\n66#1:134\n*E\n"})
public final class InflaterSource implements Source {
   @NotNull
   private final BufferedSource source;
   @NotNull
   private final Inflater inflater;
   private int bufferBytesHeldByInflater;
   private boolean closed;

   public InflaterSource(@NotNull BufferedSource source, @NotNull Inflater inflater) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(inflater, "inflater");
      super();
      this.source = source;
      this.inflater = inflater;
   }

   public InflaterSource(@NotNull Source source, @NotNull Inflater inflater) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(inflater, "inflater");
      this(Okio.buffer(source), inflater);
   }

   public long read(@NotNull Buffer sink, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");

      do {
         long bytesInflated = this.readOrInflate(sink, byteCount);
         if (bytesInflated > 0L) {
            return bytesInflated;
         }

         if (this.inflater.finished() || this.inflater.needsDictionary()) {
            return -1L;
         }
      } while(!this.source.exhausted());

      throw new EOFException("source exhausted prematurely");
   }

   public final long readOrInflate(@NotNull Buffer sink, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (byteCount < 0L) {
         int e = 0;
         String var14 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var14.toString());
      } else if (this.closed) {
         int var11 = 0;
         String toRead = "closed";
         throw new IllegalStateException(toRead.toString());
      } else if (byteCount == 0L) {
         return 0L;
      } else {
         try {
            Segment tail = sink.writableSegment$okio(1);
            int b$iv = 8192 - tail.limit;
            int $i$f$minOf = 0;
            int toRead = (int)Math.min(byteCount, (long)b$iv);
            this.refill();
            int bytesInflated = this.inflater.inflate(tail.data, tail.limit, toRead);
            this.releaseBytesAfterInflate();
            if (bytesInflated > 0) {
               tail.limit += bytesInflated;
               sink.setSize$okio(sink.size() + (long)bytesInflated);
               return (long)bytesInflated;
            } else {
               if (tail.pos == tail.limit) {
                  sink.head = tail.pop();
                  SegmentPool.recycle(tail);
               }

               return 0L;
            }
         } catch (DataFormatException e) {
            throw new IOException((Throwable)e);
         }
      }
   }

   public final boolean refill() throws IOException {
      if (!this.inflater.needsInput()) {
         return false;
      } else if (this.source.exhausted()) {
         return true;
      } else {
         Segment var10000 = this.source.getBuffer().head;
         Intrinsics.checkNotNull(var10000);
         Segment head = var10000;
         this.bufferBytesHeldByInflater = head.limit - head.pos;
         this.inflater.setInput(head.data, head.pos, this.bufferBytesHeldByInflater);
         return false;
      }
   }

   private final void releaseBytesAfterInflate() {
      if (this.bufferBytesHeldByInflater != 0) {
         int toRelease = this.bufferBytesHeldByInflater - this.inflater.getRemaining();
         this.bufferBytesHeldByInflater -= toRelease;
         this.source.skip((long)toRelease);
      }
   }

   @NotNull
   public Timeout timeout() {
      return this.source.timeout();
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.inflater.end();
         this.closed = true;
         this.source.close();
      }
   }
}
