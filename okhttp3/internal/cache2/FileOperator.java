package okhttp3.internal.cache2;

import java.io.IOException;
import java.nio.channels.FileChannel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tJ\u001e\u0010\r\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"},
   d2 = {"Lokhttp3/internal/cache2/FileOperator;", "", "fileChannel", "Ljava/nio/channels/FileChannel;", "<init>", "(Ljava/nio/channels/FileChannel;)V", "write", "", "pos", "", "source", "Lokio/Buffer;", "byteCount", "read", "sink", "okhttp"}
)
public final class FileOperator {
   @NotNull
   private final FileChannel fileChannel;

   public FileOperator(@NotNull FileChannel fileChannel) {
      Intrinsics.checkNotNullParameter(fileChannel, "fileChannel");
      super();
      this.fileChannel = fileChannel;
   }

   public final void write(long pos, @NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      if (byteCount >= 0L && byteCount <= source.size()) {
         long mutablePos = pos;

         long bytesWritten;
         for(long mutableByteCount = byteCount; mutableByteCount > 0L; mutableByteCount -= bytesWritten) {
            bytesWritten = this.fileChannel.transferFrom(source, mutablePos, mutableByteCount);
            mutablePos += bytesWritten;
         }

      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final void read(long pos, @NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      if (byteCount < 0L) {
         throw new IndexOutOfBoundsException();
      } else {
         long mutablePos = pos;

         long bytesRead;
         for(long mutableByteCount = byteCount; mutableByteCount > 0L; mutableByteCount -= bytesRead) {
            bytesRead = this.fileChannel.transferTo(mutablePos, mutableByteCount, sink);
            mutablePos += bytesRead;
         }

      }
   }
}
