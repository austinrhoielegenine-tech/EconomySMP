package okio;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0014J\b\u0010\f\u001a\u00020\u000bH\u0014J(\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u000eH\u0014J(\u0010\u0014\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u000eH\u0014J\b\u0010\u0015\u001a\u00020\tH\u0014J\b\u0010\u0016\u001a\u00020\tH\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"Lokio/NioFileSystemFileHandle;", "Lokio/FileHandle;", "readWrite", "", "fileChannel", "Ljava/nio/channels/FileChannel;", "<init>", "(ZLjava/nio/channels/FileChannel;)V", "protectedResize", "", "size", "", "protectedSize", "protectedRead", "", "fileOffset", "array", "", "arrayOffset", "byteCount", "protectedWrite", "protectedFlush", "protectedClose", "okio"}
)
public final class NioFileSystemFileHandle extends FileHandle {
   @NotNull
   private final FileChannel fileChannel;

   public NioFileSystemFileHandle(boolean readWrite, @NotNull FileChannel fileChannel) {
      Intrinsics.checkNotNullParameter(fileChannel, "fileChannel");
      super(readWrite);
      this.fileChannel = fileChannel;
   }

   protected synchronized void protectedResize(long size) {
      long currentSize = this.size();
      long delta = size - currentSize;
      if (delta > 0L) {
         this.protectedWrite(currentSize, new byte[(int)delta], 0, (int)delta);
      } else {
         this.fileChannel.truncate(size);
      }

   }

   protected synchronized long protectedSize() {
      return this.fileChannel.size();
   }

   protected synchronized int protectedRead(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(array, "array");
      this.fileChannel.position(fileOffset);
      ByteBuffer byteBuffer = ByteBuffer.wrap(array, arrayOffset, byteCount);

      int bytesRead;
      int readResult;
      for(bytesRead = 0; bytesRead < byteCount; bytesRead += readResult) {
         readResult = this.fileChannel.read(byteBuffer);
         if (readResult == -1) {
            if (bytesRead == 0) {
               return -1;
            }
            break;
         }
      }

      return bytesRead;
   }

   protected synchronized void protectedWrite(long fileOffset, @NotNull byte[] array, int arrayOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(array, "array");
      this.fileChannel.position(fileOffset);
      ByteBuffer byteBuffer = ByteBuffer.wrap(array, arrayOffset, byteCount);
      this.fileChannel.write(byteBuffer);
   }

   protected synchronized void protectedFlush() {
      this.fileChannel.force(true);
   }

   protected synchronized void protectedClose() {
      this.fileChannel.close();
   }
}
