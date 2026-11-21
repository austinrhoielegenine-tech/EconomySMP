package gnu.trove.impl.hash;

import gnu.trove.iterator.TIterator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class THashIterator<V> implements TIterator, Iterator<V> {
   private final TObjectHash<V> _object_hash;
   protected final THash _hash;
   protected int _expectedSize;
   protected int _index;

   protected THashIterator(TObjectHash<V> hash) {
      this._hash = hash;
      this._expectedSize = this._hash.size();
      this._index = this._hash.capacity();
      this._object_hash = hash;
   }

   public V next() {
      this.moveToNextIndex();
      return (V)this.objectAtIndex(this._index);
   }

   public boolean hasNext() {
      return this.nextIndex() >= 0;
   }

   public void remove() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         try {
            this._hash.tempDisableAutoCompaction();
            this._hash.removeAt(this._index);
         } finally {
            this._hash.reenableAutoCompaction(false);
         }

         --this._expectedSize;
      }
   }

   protected final void moveToNextIndex() {
      if ((this._index = this.nextIndex()) < 0) {
         throw new NoSuchElementException();
      }
   }

   protected final int nextIndex() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         Object[] set = this._object_hash._set;
         int i = this._index;

         while(i-- > 0 && (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED)) {
         }

         return i;
      }
   }

   protected abstract V objectAtIndex(int var1);
}
