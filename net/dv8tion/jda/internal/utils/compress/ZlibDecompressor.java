package net.dv8tion.jda.internal.utils.compress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.JDALogger;

public class ZlibDecompressor implements Decompressor {
   private static final int Z_SYNC_FLUSH = 65535;
   private final int maxBufferSize;
   private final Inflater inflater = new Inflater();
   private ByteBuffer flushBuffer = null;
   private SoftReference<ByteArrayOutputStream> decompressBuffer = null;

   public ZlibDecompressor(int maxBufferSize) {
      this.maxBufferSize = maxBufferSize;
   }

   private SoftReference<ByteArrayOutputStream> newDecompressBuffer() {
      return new SoftReference(new ByteArrayOutputStream(Math.min(1024, this.maxBufferSize)));
   }

   private ByteArrayOutputStream getDecompressBuffer() {
      if (this.decompressBuffer == null) {
         this.decompressBuffer = this.newDecompressBuffer();
      }

      ByteArrayOutputStream buffer = (ByteArrayOutputStream)this.decompressBuffer.get();
      if (buffer == null) {
         this.decompressBuffer = new SoftReference(buffer = new ByteArrayOutputStream(Math.min(1024, this.maxBufferSize)));
      }

      return buffer;
   }

   private boolean isFlush(byte[] data) {
      if (data.length < 4) {
         return false;
      } else {
         int suffix = IOUtil.getIntBigEndian(data, data.length - 4);
         return suffix == 65535;
      }
   }

   private void buffer(byte[] data) {
      if (this.flushBuffer == null) {
         this.flushBuffer = ByteBuffer.allocate(data.length * 2);
      }

      if (this.flushBuffer.capacity() < data.length + this.flushBuffer.position()) {
         this.flushBuffer.flip();
         this.flushBuffer = IOUtil.reallocate(this.flushBuffer, (this.flushBuffer.capacity() + data.length) * 2);
      }

      this.flushBuffer.put(data);
   }

   private Object lazy(byte[] data) {
      return JDALogger.getLazyString(() -> Arrays.toString(data));
   }

   public Compression getType() {
      return Compression.ZLIB;
   }

   public void reset() {
      this.inflater.reset();
   }

   public void shutdown() {
      this.reset();
   }

   public byte[] decompress(byte[] data) throws DataFormatException {
      if (!this.isFlush(data)) {
         LOG.debug((String)"Received incomplete data, writing to buffer. Length: {}", (Object)data.length);
         this.buffer(data);
         return null;
      } else {
         if (this.flushBuffer != null) {
            LOG.debug("Received final part of incomplete data");
            this.buffer(data);
            byte[] arr = this.flushBuffer.array();
            data = new byte[this.flushBuffer.position()];
            System.arraycopy(arr, 0, data, 0, data.length);
            this.flushBuffer = null;
         }

         LOG.trace("Decompressing data {}", this.lazy(data));
         ByteArrayOutputStream buffer = this.getDecompressBuffer();

         byte[] var4;
         try {
            InflaterOutputStream decompressor = new InflaterOutputStream(buffer, this.inflater);

            try {
               decompressor.write(data);
               var4 = buffer.toByteArray();
            } catch (Throwable var12) {
               try {
                  decompressor.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }

               throw var12;
            }

            decompressor.close();
         } catch (IOException e) {
            throw (DataFormatException)(new DataFormatException("Malformed")).initCause(e);
         } finally {
            if (buffer.size() > this.maxBufferSize) {
               this.decompressBuffer = this.newDecompressBuffer();
            } else {
               buffer.reset();
            }

         }

         return var4;
      }
   }
}
