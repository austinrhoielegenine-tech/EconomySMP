package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TShortIntHashMap extends TShortIntHash implements TShortIntMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient int[] _values;

   public TShortIntHashMap() {
   }

   public TShortIntHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TShortIntHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TShortIntHashMap(int initialCapacity, float loadFactor, short noEntryKey, int noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TShortIntHashMap(short[] keys, int[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TShortIntHashMap(TShortIntMap map) {
      super(map.size());
      if (map instanceof TShortIntHashMap) {
         TShortIntHashMap hashmap = (TShortIntHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != 0) {
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
      this._values = new int[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      short[] oldKeys = this._set;
      int[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new short[newCapacity];
      this._values = new int[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            short o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public int put(short key, int value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public int putIfAbsent(short key, int value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private int doPut(short key, int value, int index) {
      int previous = this.no_entry_value;
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

   public void putAll(Map<? extends Short, ? extends Integer> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Short, ? extends Integer> entry : map.entrySet()) {
         this.put((Short)entry.getKey(), (Integer)entry.getValue());
      }

   }

   public void putAll(TShortIntMap map) {
      this.ensureCapacity(map.size());
      TShortIntIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public int get(short key) {
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

   public int remove(short key) {
      int prev = this.no_entry_value;
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

   public TShortSet keySet() {
      return new TKeyView();
   }

   public short[] keys() {
      short[] keys = new short[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         short[] k = this._set;
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

   public short[] keys(short[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new short[size];
         }

         short[] keys = this._set;
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

   public TIntCollection valueCollection() {
      return new TValueView();
   }

   public int[] values() {
      int[] vals = new int[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         int[] v = this._values;
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

   public int[] values(int[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new int[size];
         }

         int[] v = this._values;
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

   public boolean containsValue(int val) {
      byte[] states = this._states;
      int[] vals = this._values;
      int i = vals.length;

      while(i-- > 0) {
         if (states[i] == 1 && val == vals[i]) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(short key) {
      return this.contains(key);
   }

   public TShortIntIterator iterator() {
      return new TShortIntHashIterator(this);
   }

   public boolean forEachKey(TShortProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TIntProcedure procedure) {
      byte[] states = this._states;
      int[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TShortIntProcedure procedure) {
      byte[] states = this._states;
      short[] keys = this._set;
      int[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TIntFunction function) {
      byte[] states = this._states;
      int[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TShortIntProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      short[] keys = this._set;
      int[] values = this._values;
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

   public boolean increment(short key) {
      return this.adjustValue(key, 1);
   }

   public boolean adjustValue(short key, int amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         int[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public int adjustOrPutValue(short key, int adjust_amount, int put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      int newValue;
      if (index < 0) {
         index = -index - 1;
         int[] var10000 = this._values;
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
      if (!(other instanceof TShortIntMap)) {
         return false;
      } else {
         TShortIntMap that = (TShortIntMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            int[] values = this._values;
            byte[] states = this._states;
            int this_no_entry_value = this.getNoEntryValue();
            int that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  short key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  int that_value = that.get(key);
                  int this_value = values[i];
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
      this.forEachEntry(new TShortIntProcedure() {
         private boolean first = true;

         public boolean execute(short key, int value) {
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
            out.writeShort(this._set[i]);
            out.writeInt(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         short key = in.readShort();
         int val = in.readInt();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TShortSet {
      public TShortIterator iterator() {
         return TShortIntHashMap.this.new TShortIntKeyHashIterator(TShortIntHashMap.this);
      }

      public short getNoEntryValue() {
         return TShortIntHashMap.this.no_entry_key;
      }

      public int size() {
         return TShortIntHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TShortIntHashMap.this._size;
      }

      public boolean contains(short entry) {
         return TShortIntHashMap.this.contains(entry);
      }

      public short[] toArray() {
         return TShortIntHashMap.this.keys();
      }

      public short[] toArray(short[] dest) {
         return TShortIntHashMap.this.keys(dest);
      }

      public boolean add(short entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short entry) {
         return TShortIntHashMap.this.no_entry_value != TShortIntHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Short)) {
               return false;
            }

            short ele = (Short)element;
            if (!TShortIntHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TShortCollection collection) {
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TShortIntHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(short[] array) {
         for(short element : array) {
            if (!TShortIntHashMap.this.contains(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Short> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TShortCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(short[] array) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean modified = false;
         TShortIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }

      public boolean retainAll(TShortCollection collection) {
         if (this == collection) {
            return false;
         } else {
            boolean modified = false;
            TShortIterator iter = this.iterator();

            while(iter.hasNext()) {
               if (!collection.contains(iter.next())) {
                  iter.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }

      public boolean retainAll(short[] array) {
         boolean changed = false;
         Arrays.sort(array);
         short[] set = TShortIntHashMap.this._set;
         byte[] states = TShortIntHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TShortIntHashMap.this.removeAt(i);
               changed = true;
            }
         }

         return changed;
      }

      public boolean removeAll(Collection<?> collection) {
         boolean changed = false;

         for(Object element : collection) {
            if (element instanceof Short) {
               short c = (Short)element;
               if (this.remove(c)) {
                  changed = true;
               }
            }
         }

         return changed;
      }

      public boolean removeAll(TShortCollection collection) {
         if (this == collection) {
            this.clear();
            return true;
         } else {
            boolean changed = false;
            TShortIterator iter = collection.iterator();

            while(iter.hasNext()) {
               short element = iter.next();
               if (this.remove(element)) {
                  changed = true;
               }
            }

            return changed;
         }
      }

      public boolean removeAll(short[] array) {
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
         TShortIntHashMap.this.clear();
      }

      public boolean forEach(TShortProcedure procedure) {
         return TShortIntHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TShortSet)) {
            return false;
         } else {
            TShortSet that = (TShortSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TShortIntHashMap.this._states.length;

               while(i-- > 0) {
                  if (TShortIntHashMap.this._states[i] == 1 && !that.contains(TShortIntHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TShortIntHashMap.this._states.length;

         while(i-- > 0) {
            if (TShortIntHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TShortIntHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TShortIntHashMap.this.forEachKey(new TShortProcedure() {
            private boolean first = true;

            public boolean execute(short key) {
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

   protected class TValueView implements TIntCollection {
      public TIntIterator iterator() {
         return TShortIntHashMap.this.new TShortIntValueHashIterator(TShortIntHashMap.this);
      }

      public int getNoEntryValue() {
         return TShortIntHashMap.this.no_entry_value;
      }

      public int size() {
         return TShortIntHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TShortIntHashMap.this._size;
      }

      public boolean contains(int entry) {
         return TShortIntHashMap.this.containsValue(entry);
      }

      public int[] toArray() {
         return TShortIntHashMap.this.values();
      }

      public int[] toArray(int[] dest) {
         return TShortIntHashMap.this.values(dest);
      }

      public boolean add(int entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(int entry) {
         int[] values = TShortIntHashMap.this._values;
         byte[] states = TShortIntHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TShortIntHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Integer)) {
               return false;
            }

            int ele = (Integer)element;
            if (!TShortIntHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TIntCollection collection) {
         TIntIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TShortIntHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(int[] array) {
         for(int element : array) {
            if (!TShortIntHashMap.this.containsValue(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Integer> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TIntCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int[] array) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean modified = false;
         TIntIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }

      public boolean retainAll(TIntCollection collection) {
         if (this == collection) {
            return false;
         } else {
            boolean modified = false;
            TIntIterator iter = this.iterator();

            while(iter.hasNext()) {
               if (!collection.contains(iter.next())) {
                  iter.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }

      public boolean retainAll(int[] array) {
         boolean changed = false;
         Arrays.sort(array);
         int[] values = TShortIntHashMap.this._values;
         byte[] states = TShortIntHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TShortIntHashMap.this.removeAt(i);
               changed = true;
            }
         }

         return changed;
      }

      public boolean removeAll(Collection<?> collection) {
         boolean changed = false;

         for(Object element : collection) {
            if (element instanceof Integer) {
               int c = (Integer)element;
               if (this.remove(c)) {
                  changed = true;
               }
            }
         }

         return changed;
      }

      public boolean removeAll(TIntCollection collection) {
         if (this == collection) {
            this.clear();
            return true;
         } else {
            boolean changed = false;
            TIntIterator iter = collection.iterator();

            while(iter.hasNext()) {
               int element = iter.next();
               if (this.remove(element)) {
                  changed = true;
               }
            }

            return changed;
         }
      }

      public boolean removeAll(int[] array) {
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
         TShortIntHashMap.this.clear();
      }

      public boolean forEach(TIntProcedure procedure) {
         return TShortIntHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TShortIntHashMap.this.forEachValue(new TIntProcedure() {
            private boolean first = true;

            public boolean execute(int value) {
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

   class TShortIntKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TShortIntKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public short next() {
         this.moveToNextIndex();
         return TShortIntHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortIntHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TShortIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {
      TShortIntValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public int next() {
         this.moveToNextIndex();
         return TShortIntHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortIntHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TShortIntHashIterator extends THashPrimitiveIterator implements TShortIntIterator {
      TShortIntHashIterator(TShortIntHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public short key() {
         return TShortIntHashMap.this._set[this._index];
      }

      public int value() {
         return TShortIntHashMap.this._values[this._index];
      }

      public int setValue(int val) {
         int old = this.value();
         TShortIntHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortIntHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
