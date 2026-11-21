package com.fasterxml.jackson.core.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class BufferRecycler implements RecyclerPool.WithPool<BufferRecycler> {
   public static final int BYTE_READ_IO_BUFFER = 0;
   public static final int BYTE_WRITE_ENCODING_BUFFER = 1;
   public static final int BYTE_WRITE_CONCAT_BUFFER = 2;
   public static final int BYTE_BASE64_CODEC_BUFFER = 3;
   public static final int CHAR_TOKEN_BUFFER = 0;
   public static final int CHAR_CONCAT_BUFFER = 1;
   public static final int CHAR_TEXT_BUFFER = 2;
   public static final int CHAR_NAME_COPY_BUFFER = 3;
   private static final int[] BYTE_BUFFER_LENGTHS = new int[]{8000, 8000, 2000, 2000};
   private static final int[] CHAR_BUFFER_LENGTHS = new int[]{4000, 4000, 200, 200};
   protected final AtomicReferenceArray<byte[]> _byteBuffers;
   protected final AtomicReferenceArray<char[]> _charBuffers;
   private RecyclerPool<BufferRecycler> _pool;

   public BufferRecycler() {
      this(4, 4);
   }

   protected BufferRecycler(int bbCount, int cbCount) {
      this._byteBuffers = new AtomicReferenceArray(bbCount);
      this._charBuffers = new AtomicReferenceArray(cbCount);
   }

   public boolean isLinkedWithPool() {
      return this._pool != null;
   }

   public final byte[] allocByteBuffer(int ix) {
      return this.allocByteBuffer(ix, 0);
   }

   public byte[] allocByteBuffer(int ix, int minSize) {
      int DEF_SIZE = this.byteBufferLength(ix);
      if (minSize < DEF_SIZE) {
         minSize = DEF_SIZE;
      }

      byte[] buffer = (byte[])this._byteBuffers.getAndSet(ix, (Object)null);
      if (buffer == null || buffer.length < minSize) {
         buffer = this.balloc(minSize);
      }

      return buffer;
   }

   public void releaseByteBuffer(int ix, byte[] buffer) {
      byte[] oldBuffer = (byte[])this._byteBuffers.get(ix);
      if (oldBuffer == null || buffer.length > oldBuffer.length) {
         this._byteBuffers.set(ix, buffer);
      }

   }

   public final char[] allocCharBuffer(int ix) {
      return this.allocCharBuffer(ix, 0);
   }

   public char[] allocCharBuffer(int ix, int minSize) {
      int DEF_SIZE = this.charBufferLength(ix);
      if (minSize < DEF_SIZE) {
         minSize = DEF_SIZE;
      }

      char[] buffer = (char[])this._charBuffers.getAndSet(ix, (Object)null);
      if (buffer == null || buffer.length < minSize) {
         buffer = this.calloc(minSize);
      }

      return buffer;
   }

   public void releaseCharBuffer(int ix, char[] buffer) {
      char[] oldBuffer = (char[])this._charBuffers.get(ix);
      if (oldBuffer == null || buffer.length > oldBuffer.length) {
         this._charBuffers.set(ix, buffer);
      }

   }

   protected int byteBufferLength(int ix) {
      return BYTE_BUFFER_LENGTHS[ix];
   }

   protected int charBufferLength(int ix) {
      return CHAR_BUFFER_LENGTHS[ix];
   }

   protected byte[] balloc(int size) {
      return new byte[size];
   }

   protected char[] calloc(int size) {
      return new char[size];
   }

   public BufferRecycler withPool(RecyclerPool<BufferRecycler> pool) {
      if (this._pool != null) {
         throw new IllegalStateException("BufferRecycler already linked to pool: " + pool);
      } else {
         this._pool = (RecyclerPool)Objects.requireNonNull(pool);
         return this;
      }
   }

   public void releaseToPool() {
      if (this._pool != null) {
         RecyclerPool<BufferRecycler> tmpPool = this._pool;
         this._pool = null;
         tmpPool.releasePooled(this);
      }

   }

   public interface Gettable {
      BufferRecycler bufferRecycler();
   }
}
