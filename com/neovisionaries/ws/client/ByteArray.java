package com.neovisionaries.ws.client;

import java.nio.ByteBuffer;

class ByteArray {
   private static final int ADDITIONAL_BUFFER_SIZE = 1024;
   private ByteBuffer mBuffer;
   private int mLength;

   public ByteArray(int capacity) {
      this.mBuffer = ByteBuffer.allocate(capacity);
      this.mLength = 0;
   }

   public ByteArray(byte[] data) {
      this.mBuffer = ByteBuffer.wrap(data);
      this.mLength = data.length;
   }

   public int length() {
      return this.mLength;
   }

   public byte get(int index) throws IndexOutOfBoundsException {
      if (index >= 0 && this.mLength > index) {
         return this.mBuffer.get(index);
      } else {
         throw new IndexOutOfBoundsException(String.format("Bad index: index=%d, length=%d", index, this.mLength));
      }
   }

   private void expandBuffer(int newBufferSize) {
      ByteBuffer newBuffer = ByteBuffer.allocate(newBufferSize);
      int oldPosition = this.mBuffer.position();
      this.mBuffer.position(0);
      newBuffer.put(this.mBuffer);
      newBuffer.position(oldPosition);
      this.mBuffer = newBuffer;
   }

   public void put(int data) {
      if (this.mBuffer.capacity() < this.mLength + 1) {
         this.expandBuffer(this.mLength + 1024);
      }

      this.mBuffer.put((byte)data);
      ++this.mLength;
   }

   public void put(byte[] source) {
      if (this.mBuffer.capacity() < this.mLength + source.length) {
         this.expandBuffer(this.mLength + source.length + 1024);
      }

      this.mBuffer.put(source);
      this.mLength += source.length;
   }

   public void put(byte[] source, int index, int length) {
      if (this.mBuffer.capacity() < this.mLength + length) {
         this.expandBuffer(this.mLength + length + 1024);
      }

      this.mBuffer.put(source, index, length);
      this.mLength += length;
   }

   public void put(ByteArray source, int index, int length) {
      this.put(source.mBuffer.array(), index, length);
   }

   public byte[] toBytes() {
      return this.toBytes(0);
   }

   public byte[] toBytes(int beginIndex) {
      return this.toBytes(beginIndex, this.length());
   }

   public byte[] toBytes(int beginIndex, int endIndex) {
      int len = endIndex - beginIndex;
      if (len >= 0 && beginIndex >= 0 && this.mLength >= endIndex) {
         byte[] bytes = new byte[len];
         if (len != 0) {
            System.arraycopy(this.mBuffer.array(), beginIndex, bytes, 0, len);
         }

         return bytes;
      } else {
         throw new IllegalArgumentException(String.format("Bad range: beginIndex=%d, endIndex=%d, length=%d", beginIndex, endIndex, this.mLength));
      }
   }

   public void clear() {
      this.mBuffer.clear();
      this.mBuffer.position(0);
      this.mLength = 0;
   }

   public void shrink(int size) {
      if (this.mBuffer.capacity() > size) {
         int endIndex = this.mLength;
         int beginIndex = this.mLength - size;
         byte[] bytes = this.toBytes(beginIndex, endIndex);
         this.mBuffer = ByteBuffer.wrap(bytes);
         this.mBuffer.position(bytes.length);
         this.mLength = bytes.length;
      }
   }

   public boolean getBit(int bitIndex) {
      int index = bitIndex / 8;
      int shift = bitIndex % 8;
      int value = this.get(index);
      return (value & 1 << shift) != 0;
   }

   public int getBits(int bitIndex, int nBits) {
      int number = 0;
      int weight = 1;

      for(int i = 0; i < nBits; weight *= 2) {
         if (this.getBit(bitIndex + i)) {
            number += weight;
         }

         ++i;
      }

      return number;
   }

   public int getHuffmanBits(int bitIndex, int nBits) {
      int number = 0;
      int weight = 1;

      for(int i = nBits - 1; 0 <= i; weight *= 2) {
         if (this.getBit(bitIndex + i)) {
            number += weight;
         }

         --i;
      }

      return number;
   }

   public boolean readBit(int[] bitIndex) {
      boolean result = this.getBit(bitIndex[0]);
      int var10002 = bitIndex[0]++;
      return result;
   }

   public int readBits(int[] bitIndex, int nBits) {
      int number = this.getBits(bitIndex[0], nBits);
      bitIndex[0] += nBits;
      return number;
   }

   public void setBit(int bitIndex, boolean bit) {
      int index = bitIndex / 8;
      int shift = bitIndex % 8;
      int value = this.get(index);
      if (bit) {
         value |= 1 << shift;
      } else {
         value &= ~(1 << shift);
      }

      this.mBuffer.put(index, (byte)value);
   }

   public void clearBit(int bitIndex) {
      this.setBit(bitIndex, false);
   }
}
