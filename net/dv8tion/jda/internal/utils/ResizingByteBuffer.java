package net.dv8tion.jda.internal.utils;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;

public class ResizingByteBuffer {
   private ByteBuffer buffer;

   public ResizingByteBuffer(@Nonnull ByteBuffer buffer) {
      this.buffer = buffer;
   }

   @Nonnull
   public ByteBuffer buffer() {
      return this.buffer;
   }

   @Nonnull
   public ResizingByteBuffer prepareWrite(int capacity) {
      if (this.buffer.capacity() < capacity) {
         this.buffer = IOUtil.allocateLike(this.buffer, (int)((double)1.25F * (double)capacity));
      } else {
         this.buffer.clear();
      }

      return this;
   }

   @Nonnull
   public ResizingByteBuffer ensureRemaining(int capacity) {
      if (this.buffer.remaining() < capacity) {
         ByteBuffer newBuffer = IOUtil.allocateLike(this.buffer, (int)((double)1.25F * (double)(this.buffer.position() + capacity)));
         this.buffer.flip();
         newBuffer.put(this.buffer);
         this.buffer = newBuffer;
      }

      return this;
   }

   @Nonnull
   public ResizingByteBuffer replace(@Nonnull ByteBuffer data) {
      this.buffer = IOUtil.replace(this.buffer, data);
      return this;
   }

   @Nonnull
   public ResizingByteBuffer clear() {
      this.buffer.clear();
      return this;
   }
}
