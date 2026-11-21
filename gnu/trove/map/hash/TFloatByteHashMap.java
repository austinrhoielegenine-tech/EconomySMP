package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatByteHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TFloatByteHashMap extends TFloatByteHash implements TFloatByteMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient byte[] _values;

   public TFloatByteHashMap() {
   }

   public TFloatByteHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TFloatByteHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TFloatByteHashMap(int initialCapacity, float loadFactor, float noEntryKey, byte noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TFloatByteHashMap(float[] keys, byte[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TFloatByteHashMap(TFloatByteMap map) {
      super(map.size());
      if (map instanceof TFloatByteHashMap) {
         TFloatByteHashMap hashmap = (TFloatByteHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != 0.0F) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.putAll(map);
   }

   protected int setUp(int initialCapacity) {
      int capacity = super.setUp(initialCapacity);
      this._values = new byte[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      float[] oldKeys = this._set;
      byte[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new float[newCapacity];
      this._values = new byte[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            float o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public byte put(float key, byte value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public byte putIfAbsent(float key, byte value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private byte doPut(float key, byte value, int index) {
      byte previous = this.no_entry_value;
      boolean isNewMapping = true;
      if (index < 0) {
         index = -index - 1;
         previous = this._values[index];
         isNewMapping = false;
      }

      this._values[index] = value;
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return previous;
   }

   public void putAll(Map<? extends Float, ? extends Byte> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Float, ? extends Byte> entry : map.entrySet()) {
         this.put((Float)entry.getKey(), (Byte)entry.getValue());
      }

   }

   public void putAll(TFloatByteMap map) {
      this.ensureCapacity(map.size());
      TFloatByteIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public byte get(float key) {
      int index = this.index(key);
      return index < 0 ? this.no_entry_value : this._values[index];
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
   }

   public boolean isEmpty() {
      return 0 == this._size;
   }

   public byte remove(float key) {
      byte prev = this.no_entry_value;
      int index = this.index(key);
      if (index >= 0) {
         prev = this._values[index];
         this.removeAt(index);
      }

      return prev;
   }

   protected void removeAt(int index) {
      this._values[index] = this.no_entry_value;
      super.removeAt(index);
   }

   public TFloatSet keySet() {
      return new TKeyView();
   }

   public float[] keys() {
      float[] keys = new float[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         float[] k = this._set;
         byte[] states = this._states;
         int i = k.length;
         int j = 0;

         while(i-- > 0) {
            if (states[i] == 1) {
               keys[j++] = k[i];
            }
         }

         return keys;
      }
   }

   public float[] keys(float[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new float[size];
         }

         float[] keys = this._set;
         byte[] states = this._states;
         int i = keys.length;
         int j = 0;

         while(i-- > 0) {
            if (states[i] == 1) {
               array[j++] = keys[i];
            }
         }

         return array;
      }
   }

   public TByteCollection valueCollection() {
      return new TValueView();
   }

   public byte[] values() {
      byte[] vals = new byte[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         byte[] v = this._values;
         byte[] states = this._states;
         int i = v.length;
         int j = 0;

         while(i-- > 0) {
            if (states[i] == 1) {
               vals[j++] = v[i];
            }
         }

         return vals;
      }
   }

   public byte[] values(byte[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new byte[size];
         }

         byte[] v = this._values;
         byte[] states = this._states;
         int i = v.length;
         int j = 0;

         while(i-- > 0) {
            if (states[i] == 1) {
               array[j++] = v[i];
            }
         }

         return array;
      }
   }

   public boolean containsValue(byte val) {
      byte[] states = this._states;
      byte[] vals = this._values;
      int i = vals.length;

      while(i-- > 0) {
         if (states[i] == 1 && val == vals[i]) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(float key) {
      return this.contains(key);
   }

   public TFloatByteIterator iterator() {
      return new TFloatByteHashIterator(this);
   }

   public boolean forEachKey(TFloatProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TByteProcedure procedure) {
      byte[] states = this._states;
      byte[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TFloatByteProcedure procedure) {
      byte[] states = this._states;
      float[] keys = this._set;
      byte[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TByteFunction function) {
      byte[] states = this._states;
      byte[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TFloatByteProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      float[] keys = this._set;
      byte[] values = this._values;
      this.tempDisableAutoCompaction();

      try {
         int i = keys.length;

         while(i-- > 0) {
            if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
               this.removeAt(i);
               modified = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return modified;
   }

   public boolean increment(float key) {
      return this.adjustValue(key, (byte)1);
   }

   public boolean adjustValue(float key, byte amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         byte[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public byte adjustOrPutValue(float key, byte adjust_amount, byte put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      byte newValue;
      if (index < 0) {
         index = -index - 1;
         byte[] var10000 = this._values;
         newValue = var10000[index] += adjust_amount;
         isNewMapping = false;
      } else {
         newValue = this._values[index] = put_amount;
         isNewMapping = true;
      }

      byte var8 = this._states[index];
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean equals(Object other) {
      if (!(other instanceof TFloatByteMap)) {
         return false;
      } else {
         TFloatByteMap that = (TFloatByteMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            byte[] values = this._values;
            byte[] states = this._states;
            byte this_no_entry_value = this.getNoEntryValue();
            byte that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  float key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  byte that_value = that.get(key);
                  byte this_value = values[i];
                  if (this_value != that_value && (this_value != this_no_entry_value || that_value != that_no_entry_value)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int hashcode = 0;
      byte[] states = this._states;
      int i = this._values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            hashcode += HashFunctions.hash(this._set[i]) ^ HashFunctions.hash(this._values[i]);
         }
      }

      return hashcode;
   }

   public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      this.forEachEntry(new TFloatByteProcedure() {
         private boolean first = true;

         public boolean execute(float key, byte value) {
            if (this.first) {
               this.first = false;
            } else {
               buf.append(", ");
            }

            buf.append(key);
            buf.append("=");
            buf.append(value);
            return true;
         }
      });
      buf.append("}");
      return buf.toString();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      super.writeExternal(out);
      out.writeInt(this._size);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeFloat(this._set[i]);
            out.writeByte(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         float key = in.readFloat();
         byte val = in.readByte();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TFloatSet {
      public TFloatIterator iterator() {
         return TFloatByteHashMap.this.new TFloatByteKeyHashIterator(TFloatByteHashMap.this);
      }

      public float getNoEntryValue() {
         return TFloatByteHashMap.this.no_entry_key;
      }

      public int size() {
         return TFloatByteHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TFloatByteHashMap.this._size;
      }

      public boolean contains(float entry) {
         return TFloatByteHashMap.this.contains(entry);
      }

      public float[] toArray() {
         return TFloatByteHashMap.this.keys();
      }

      public float[] toArray(float[] dest) {
         return TFloatByteHashMap.this.keys(dest);
      }

      public boolean add(float entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float entry) {
         return TFloatByteHashMap.this.no_entry_value != TFloatByteHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Float)) {
               return false;
            }

            float ele = (Float)element;
            if (!TFloatByteHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TFloatCollection collection) {
         TFloatIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TFloatByteHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(float[] array) {
         for(float element : array) {
            if (!TFloatByteHashMap.this.contains(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Float> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TFloatCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(float[] array) {
         throw new UnsupportedOperationException();
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
         float[] set = TFloatByteHashMap.this._set;
         byte[] states = TFloatByteHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TFloatByteHashMap.this.removeAt(i);
               changed = true;
            }
         }

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
         if (this == collection) {
            this.clear();
            return true;
         } else {
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
         TFloatByteHashMap.this.clear();
      }

      public boolean forEach(TFloatProcedure procedure) {
         return TFloatByteHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TFloatSet)) {
            return false;
         } else {
            TFloatSet that = (TFloatSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TFloatByteHashMap.this._states.length;

               while(i-- > 0) {
                  if (TFloatByteHashMap.this._states[i] == 1 && !that.contains(TFloatByteHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TFloatByteHashMap.this._states.length;

         while(i-- > 0) {
            if (TFloatByteHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TFloatByteHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TFloatByteHashMap.this.forEachKey(new TFloatProcedure() {
            private boolean first = true;

            public boolean execute(float key) {
               if (this.first) {
                  this.first = false;
               } else {
                  buf.append(", ");
               }

               buf.append(key);
               return true;
            }
         });
         buf.append("}");
         return buf.toString();
      }
   }

   protected class TValueView implements TByteCollection {
      public TByteIterator iterator() {
         return TFloatByteHashMap.this.new TFloatByteValueHashIterator(TFloatByteHashMap.this);
      }

      public byte getNoEntryValue() {
         return TFloatByteHashMap.this.no_entry_value;
      }

      public int size() {
         return TFloatByteHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TFloatByteHashMap.this._size;
      }

      public boolean contains(byte entry) {
         return TFloatByteHashMap.this.containsValue(entry);
      }

      public byte[] toArray() {
         return TFloatByteHashMap.this.values();
      }

      public byte[] toArray(byte[] dest) {
         return TFloatByteHashMap.this.values(dest);
      }

      public boolean add(byte entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte entry) {
         byte[] values = TFloatByteHashMap.this._values;
         byte[] states = TFloatByteHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TFloatByteHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Byte)) {
               return false;
            }

            byte ele = (Byte)element;
            if (!TFloatByteHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TByteCollection collection) {
         TByteIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TFloatByteHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(byte[] array) {
         for(byte element : array) {
            if (!TFloatByteHashMap.this.containsValue(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Byte> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TByteCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(byte[] array) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean modified = false;
         TByteIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }

      public boolean retainAll(TByteCollection collection) {
         if (this == collection) {
            return false;
         } else {
            boolean modified = false;
            TByteIterator iter = this.iterator();

            while(iter.hasNext()) {
               if (!collection.contains(iter.next())) {
                  iter.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }

      public boolean retainAll(byte[] array) {
         boolean changed = false;
         Arrays.sort(array);
         byte[] values = TFloatByteHashMap.this._values;
         byte[] states = TFloatByteHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TFloatByteHashMap.this.removeAt(i);
               changed = true;
            }
         }

         return changed;
      }

      public boolean removeAll(Collection<?> collection) {
         boolean changed = false;

         for(Object element : collection) {
            if (element instanceof Byte) {
               byte c = (Byte)element;
               if (this.remove(c)) {
                  changed = true;
               }
            }
         }

         return changed;
      }

      public boolean removeAll(TByteCollection collection) {
         if (this == collection) {
            this.clear();
            return true;
         } else {
            boolean changed = false;
            TByteIterator iter = collection.iterator();

            while(iter.hasNext()) {
               byte element = iter.next();
               if (this.remove(element)) {
                  changed = true;
               }
            }

            return changed;
         }
      }

      public boolean removeAll(byte[] array) {
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
         TFloatByteHashMap.this.clear();
      }

      public boolean forEach(TByteProcedure procedure) {
         return TFloatByteHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TFloatByteHashMap.this.forEachValue(new TByteProcedure() {
            private boolean first = true;

            public boolean execute(byte value) {
               if (this.first) {
                  this.first = false;
               } else {
                  buf.append(", ");
               }

               buf.append(value);
               return true;
            }
         });
         buf.append("}");
         return buf.toString();
      }
   }

   class TFloatByteKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {
      TFloatByteKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public float next() {
         this.moveToNextIndex();
         return TFloatByteHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatByteHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TFloatByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {
      TFloatByteValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public byte next() {
         this.moveToNextIndex();
         return TFloatByteHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatByteHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TFloatByteHashIterator extends THashPrimitiveIterator implements TFloatByteIterator {
      TFloatByteHashIterator(TFloatByteHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public float key() {
         return TFloatByteHashMap.this._set[this._index];
      }

      public byte value() {
         return TFloatByteHashMap.this._values[this._index];
      }

      public byte setValue(byte val) {
         byte old = this.value();
         TFloatByteHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatByteHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
