package gnu.trove.impl.hash;

import gnu.trove.impl.PrimeFinder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class THash implements Externalizable {
   static final long serialVersionUID = -1792948471915530295L;
   protected static final float DEFAULT_LOAD_FACTOR = 0.5F;
   protected static final int DEFAULT_CAPACITY = 10;
   protected transient int _size;
   protected transient int _free;
   protected float _loadFactor;
   protected int _maxSize;
   protected int _autoCompactRemovesRemaining;
   protected float _autoCompactionFactor;
   protected transient boolean _autoCompactTemporaryDisable;

   public THash() {
      this(10, 0.5F);
   }

   public THash(int initialCapacity) {
      this(initialCapacity, 0.5F);
   }

   public THash(int initialCapacity, float loadFactor) {
      this._autoCompactTemporaryDisable = false;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("negative capacity: " + initialCapacity);
      } else if (0.0F >= loadFactor) {
         throw new IllegalArgumentException("load factor out of range: " + loadFactor);
      } else {
         this._loadFactor = loadFactor;
         this._autoCompactionFactor = loadFactor;
         this.setUp(saturatedCast(fastCeil((double)initialCapacity / (double)loadFactor)));
      }
   }

   protected static long fastCeil(double v) {
      long possible_result = (long)v;
      if (v - (double)possible_result > (double)0.0F) {
         ++possible_result;
      }

      return possible_result;
   }

   protected static int saturatedCast(long v) {
      int r = (int)(v & 2147483647L);
      return (long)r != v ? Integer.MAX_VALUE : r;
   }

   public boolean isEmpty() {
      return 0 == this._size;
   }

   public int size() {
      return this._size;
   }

   public abstract int capacity();

   public void ensureCapacity(int desiredCapacity) {
      if (desiredCapacity > this._maxSize - this.size()) {
         this.rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, saturatedCast(fastCeil((double)(desiredCapacity + this._size) / (double)this._loadFactor) + 1L))));
         if (this.capacity() >= PrimeFinder.largestPrime) {
            this._loadFactor = 1.0F;
         }

         this.computeMaxSize(this.capacity());
      }

   }

   public void compact() {
      this.rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, saturatedCast(fastCeil((double)this._size / (double)this._loadFactor) + 1L))));
      this.computeMaxSize(this.capacity());
      if (this._autoCompactionFactor != 0.0F) {
         this.computeNextAutoCompactionAmount(this.size());
      }

   }

   public void setAutoCompactionFactor(float factor) {
      if (factor < 0.0F) {
         throw new IllegalArgumentException("Factor must be >= 0: " + factor);
      } else {
         this._autoCompactionFactor = factor;
      }
   }

   public float getAutoCompactionFactor() {
      return this._autoCompactionFactor;
   }

   public final void trimToSize() {
      this.compact();
   }

   protected void removeAt(int index) {
      --this._size;
      if (this._autoCompactionFactor != 0.0F) {
         --this._autoCompactRemovesRemaining;
         if (!this._autoCompactTemporaryDisable && this._autoCompactRemovesRemaining <= 0) {
            this.compact();
         }
      }

   }

   public void clear() {
      this._size = 0;
      this._free = this.capacity();
   }

   protected int setUp(int initialCapacity) {
      int capacity = PrimeFinder.nextPrime(initialCapacity);
      if (capacity >= PrimeFinder.largestPrime) {
         this._loadFactor = 1.0F;
      }

      this.computeMaxSize(capacity);
      this.computeNextAutoCompactionAmount(initialCapacity);
      return capacity;
   }

   protected abstract void rehash(int var1);

   public void tempDisableAutoCompaction() {
      this._autoCompactTemporaryDisable = true;
   }

   public void reenableAutoCompaction(boolean check_for_compaction) {
      this._autoCompactTemporaryDisable = false;
      if (check_for_compaction && this._autoCompactRemovesRemaining <= 0 && this._autoCompactionFactor != 0.0F) {
         this.compact();
      }

   }

   protected void computeMaxSize(int capacity) {
      this._maxSize = Math.min(capacity - 1, (int)((float)capacity * this._loadFactor));
      this._free = capacity - this._size;
   }

   protected void computeNextAutoCompactionAmount(int size) {
      if (this._autoCompactionFactor != 0.0F) {
         this._autoCompactRemovesRemaining = (int)((float)size * this._autoCompactionFactor + 0.5F);
      }

   }

   protected final void postInsertHook(boolean usedFreeSlot) {
      if (usedFreeSlot) {
         --this._free;
      }

      if (++this._size > this._maxSize || this._free == 0) {
         int newCapacity = this._size > this._maxSize ? PrimeFinder.nextPrime(this.capacity() << 1) : this.capacity();
         this.rehash(newCapacity);
         this.computeMaxSize(this.capacity());
      }

   }

   protected int calculateGrownCapacity() {
      return this.capacity() << 1;
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeFloat(this._loadFactor);
      out.writeFloat(this._autoCompactionFactor);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      float old_factor = this._loadFactor;
      this._loadFactor = Math.abs(in.readFloat());
      this._autoCompactionFactor = in.readFloat();
      if (old_factor != this._loadFactor) {
         this.setUp(saturatedCast((long)Math.ceil((double)10.0F / (double)this._loadFactor)));
      }

   }
}
