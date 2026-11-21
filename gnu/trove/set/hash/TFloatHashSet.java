package gnu.trove.set.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

public class TFloatHashSet extends TFloatHash implements TFloatSet, Externalizable {
   static final long serialVersionUID = 1L;

   public TFloatHashSet() {
   }

   public TFloatHashSet(int initialCapacity) {
      super(initialCapacity);
   }

   public TFloatHashSet(int initialCapacity, float load_factor) {
      super(initialCapacity, load_factor);
   }

   public TFloatHashSet(int initial_capacity, float load_factor, float no_entry_value) {
      super(initial_capacity, load_factor, no_entry_value);
      if (no_entry_value != 0.0F) {
         Arrays.fill(this._set, no_entry_value);
      }

   }

   public TFloatHashSet(Collection<? extends Float> collection) {
      this(Math.max(collection.size(), 10));
      this.addAll(collection);
   }

   public TFloatHashSet(TFloatCollection collection) {
      this(Math.max(collection.size(), 10));
      if (collection instanceof TFloatHashSet) {
         TFloatHashSet hashset = (TFloatHashSet)collection;
         this._loadFactor = hashset._loadFactor;
         this.no_entry_value = hashset.no_entry_value;
         if (this.no_entry_value != 0.0F) {
            Arrays.fill(this._set, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.addAll(collection);
   }

   public TFloatHashSet(float[] array) {
      this(Math.max(array.length, 10));
      this.addAll(array);
   }

   public TFloatIterator iterator() {
      return new TFloatHashIterator(this);
   }

   public float[] toArray() {
      return this.toArray(new float[this._size]);
   }

   public float[] toArray(float[] dest) {
      if (dest.length < this._size) {
         dest = new float[this._size];
      }

      float[] set = this._set;
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

   public boolean add(float val) {
      int index = this.insertKey(val);
      if (index < 0) {
         return false;
      } else {
         this.postInsertHook(this.consumeFreeSlot);
         return true;
      }
   }

   public boolean remove(float val) {
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
         if (!(element instanceof Float)) {
            return false;
         }

         float c = (Float)element;
         if (!this.contains(c)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(TFloatCollection collection) {
      TFloatIterator iter = collection.iterator();

      while(iter.hasNext()) {
         float element = iter.next();
         if (!this.contains(element)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(float[] array) {
      int i = array.length;

      while(i-- > 0) {
         if (!this.contains(array[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection<? extends Float> collection) {
      boolean changed = false;

      for(Float element : collection) {
         float e = element;
         if (this.add(e)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean addAll(TFloatCollection collection) {
      boolean changed = false;
      TFloatIterator iter = collection.iterator();

      while(iter.hasNext()) {
         float element = iter.next();
         if (this.add(element)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean addAll(float[] array) {
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
      TFloatIterator iter = this.iterator();

      while(iter.hasNext()) {
         if (!collection.contains(iter.next())) {
            iter.remove();
            modified = true;
         }
      }

      return modified;
   }

   public boolean retainAll(TFloatCollection collection) {
      if (this == collection) {
         return false;
      } else {
         boolean modified = false;
         TFloatIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }
   }

   public boolean retainAll(float[] array) {
      boolean changed = false;
      Arrays.sort(array);
      float[] set = this._set;
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
         if (element instanceof Float) {
            float c = (Float)element;
            if (this.remove(c)) {
               changed = true;
            }
         }
      }

      return changed;
   }

   public boolean removeAll(TFloatCollection collection) {
      boolean changed = false;
      TFloatIterator iter = collection.iterator();

      while(iter.hasNext()) {
         float element = iter.next();
         if (this.remove(element)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean removeAll(float[] array) {
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
      float[] set = this._set;
      byte[] states = this._states;

      for(int i = set.length; i-- > 0; states[i] = 0) {
         set[i] = this.no_entry_value;
      }

   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      float[] oldSet = this._set;
      byte[] oldStates = this._states;
      this._set = new float[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            float o = oldSet[i];
            this.insertKey(o);
         }
      }

   }

   public boolean equals(Object other) {
      if (!(other instanceof TFloatSet)) {
         return false;
      } else {
         TFloatSet that = (TFloatSet)other;
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
      out.writeFloat(this.no_entry_value);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeFloat(this._set[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      int version = in.readByte();
      super.readExternal(in);
      int size = in.readInt();
      if (version >= 1) {
         this._loadFactor = in.readFloat();
         this.no_entry_value = in.readFloat();
         if (this.no_entry_value != 0.0F) {
            Arrays.fill(this._set, this.no_entry_value);
         }
      }

      this.setUp(size);

      while(size-- > 0) {
         float val = in.readFloat();
         this.add(val);
      }

   }

   class TFloatHashIterator extends THashPrimitiveIterator implements TFloatIterator {
      private final TFloatHash _hash;

      public TFloatHashIterator(TFloatHash hash) {
         super(hash);
         this._hash = hash;
      }

      public float next() {
         this.moveToNextIndex();
         return this._hash._set[this._index];
      }
   }
}
