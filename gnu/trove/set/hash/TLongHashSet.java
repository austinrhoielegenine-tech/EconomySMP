package gnu.trove.set.hash;

import gnu.trove.TLongCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

public class TLongHashSet extends TLongHash implements TLongSet, Externalizable {
   static final long serialVersionUID = 1L;

   public TLongHashSet() {
   }

   public TLongHashSet(int initialCapacity) {
      super(initialCapacity);
   }

   public TLongHashSet(int initialCapacity, float load_factor) {
      super(initialCapacity, load_factor);
   }

   public TLongHashSet(int initial_capacity, float load_factor, long no_entry_value) {
      super(initial_capacity, load_factor, no_entry_value);
      if (no_entry_value != 0L) {
         Arrays.fill(this._set, no_entry_value);
      }

   }

   public TLongHashSet(Collection<? extends Long> collection) {
      this(Math.max(collection.size(), 10));
      this.addAll(collection);
   }

   public TLongHashSet(TLongCollection collection) {
      this(Math.max(collection.size(), 10));
      if (collection instanceof TLongHashSet) {
         TLongHashSet hashset = (TLongHashSet)collection;
         this._loadFactor = hashset._loadFactor;
         this.no_entry_value = hashset.no_entry_value;
         if (this.no_entry_value != 0L) {
            Arrays.fill(this._set, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.addAll(collection);
   }

   public TLongHashSet(long[] array) {
      this(Math.max(array.length, 10));
      this.addAll(array);
   }

   public TLongIterator iterator() {
      return new TLongHashIterator(this);
   }

   public long[] toArray() {
      return this.toArray(new long[this._size]);
   }

   public long[] toArray(long[] dest) {
      if (dest.length < this._size) {
         dest = new long[this._size];
      }

      long[] set = this._set;
      byte[] states = this._states;
      int i = states.length;
      int j = 0;

      while(i-- > 0) {
         if (states[i] == 1) {
            dest[j++] = set[i];
         }
      }

      if (dest.length > this._size) {
         dest[this._size] = this.no_entry_value;
      }

      return dest;
   }

   public boolean add(long val) {
      int index = this.insertKey(val);
      if (index < 0) {
         return false;
      } else {
         this.postInsertHook(this.consumeFreeSlot);
         return true;
      }
   }

   public boolean remove(long val) {
      int index = this.index(val);
      if (index >= 0) {
         this.removeAt(index);
         return true;
      } else {
         return false;
      }
   }

   public boolean containsAll(Collection<?> collection) {
      for(Object element : collection) {
         if (!(element instanceof Long)) {
            return false;
         }

         long c = (Long)element;
         if (!this.contains(c)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(TLongCollection collection) {
      TLongIterator iter = collection.iterator();

      while(iter.hasNext()) {
         long element = iter.next();
         if (!this.contains(element)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(long[] array) {
      int i = array.length;

      while(i-- > 0) {
         if (!this.contains(array[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection<? extends Long> collection) {
      boolean changed = false;

      for(Long element : collection) {
         long e = element;
         if (this.add(e)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean addAll(TLongCollection collection) {
      boolean changed = false;
      TLongIterator iter = collection.iterator();

      while(iter.hasNext()) {
         long element = iter.next();
         if (this.add(element)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean addAll(long[] array) {
      boolean changed = false;
      int i = array.length;

      while(i-- > 0) {
         if (this.add(array[i])) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TLongIterator iter = this.iterator();

      while(iter.hasNext()) {
         if (!collection.contains(iter.next())) {
            iter.remove();
            modified = true;
         }
      }

      return modified;
   }

   public boolean retainAll(TLongCollection collection) {
      if (this == collection) {
         return false;
      } else {
         boolean modified = false;
         TLongIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }
   }

   public boolean retainAll(long[] array) {
      boolean changed = false;
      Arrays.sort(array);
      long[] set = this._set;
      byte[] states = this._states;
      this._autoCompactTemporaryDisable = true;
      int i = set.length;

      while(i-- > 0) {
         if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
            this.removeAt(i);
            changed = true;
         }
      }

      this._autoCompactTemporaryDisable = false;
      return changed;
   }

   public boolean removeAll(Collection<?> collection) {
      boolean changed = false;

      for(Object element : collection) {
         if (element instanceof Long) {
            long c = (Long)element;
            if (this.remove(c)) {
               changed = true;
            }
         }
      }

      return changed;
   }

   public boolean removeAll(TLongCollection collection) {
      boolean changed = false;
      TLongIterator iter = collection.iterator();

      while(iter.hasNext()) {
         long element = iter.next();
         if (this.remove(element)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean removeAll(long[] array) {
      boolean changed = false;
      int i = array.length;

      while(i-- > 0) {
         if (this.remove(array[i])) {
            changed = true;
         }
      }

      return changed;
   }

   public void clear() {
      super.clear();
      long[] set = this._set;
      byte[] states = this._states;

      for(int i = set.length; i-- > 0; states[i] = 0) {
         set[i] = this.no_entry_value;
      }

   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      long[] oldSet = this._set;
      byte[] oldStates = this._states;
      this._set = new long[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            long o = oldSet[i];
            this.insertKey(o);
         }
      }

   }

   public boolean equals(Object other) {
      if (!(other instanceof TLongSet)) {
         return false;
      } else {
         TLongSet that = (TLongSet)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            int i = this._states.length;

            while(i-- > 0) {
               if (this._states[i] == 1 && !that.contains(this._set[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int hashcode = 0;
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            hashcode += HashFunctions.hash(this._set[i]);
         }
      }

      return hashcode;
   }

   public String toString() {
      StringBuilder buffy = new StringBuilder(this._size * 2 + 2);
      buffy.append("{");
      int i = this._states.length;
      int j = 1;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            buffy.append(this._set[i]);
            if (j++ < this._size) {
               buffy.append(",");
            }
         }
      }

      buffy.append("}");
      return buffy.toString();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(1);
      super.writeExternal(out);
      out.writeInt(this._size);
      out.writeFloat(this._loadFactor);
      out.writeLong(this.no_entry_value);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeLong(this._set[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      int version = in.readByte();
      super.readExternal(in);
      int size = in.readInt();
      if (version >= 1) {
         this._loadFactor = in.readFloat();
         this.no_entry_value = in.readLong();
         if (this.no_entry_value != 0L) {
            Arrays.fill(this._set, this.no_entry_value);
         }
      }

      this.setUp(size);

      while(size-- > 0) {
         long val = in.readLong();
         this.add(val);
      }

   }

   class TLongHashIterator extends THashPrimitiveIterator implements TLongIterator {
      private final TLongHash _hash;

      public TLongHashIterator(TLongHash hash) {
         super(hash);
         this._hash = hash;
      }

      public long next() {
         this.moveToNextIndex();
         return this._hash._set[this._index];
      }
   }
}
