package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TIntLongMap;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TIntLongHashMap extends TIntLongHash implements TIntLongMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TIntLongHashMap() {
   }

   public TIntLongHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TIntLongHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TIntLongHashMap(int initialCapacity, float loadFactor, int noEntryKey, long noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TIntLongHashMap(int[] keys, long[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TIntLongHashMap(TIntLongMap map) {
      super(map.size());
      if (map instanceof TIntLongHashMap) {
         TIntLongHashMap hashmap = (TIntLongHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.putAll(map);
   }

   protected int setUp(int initialCapacity) {
      int capacity = super.setUp(initialCapacity);
      this._values = new long[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      int[] oldKeys = this._set;
      long[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new int[newCapacity];
      this._values = new long[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            int o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public long put(int key, long value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public long putIfAbsent(int key, long value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private long doPut(int key, long value, int index) {
      long previous = this.no_entry_value;
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

   public void putAll(Map<? extends Integer, ? extends Long> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Integer, ? extends Long> entry : map.entrySet()) {
         this.put((Integer)entry.getKey(), (Long)entry.getValue());
      }

   }

   public void putAll(TIntLongMap map) {
      this.ensureCapacity(map.size());
      TIntLongIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public long get(int key) {
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

   public long remove(int key) {
      long prev = this.no_entry_value;
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

   public TIntSet keySet() {
      return new TKeyView();
   }

   public int[] keys() {
      int[] keys = new int[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         int[] k = this._set;
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

   public int[] keys(int[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new int[size];
         }

         int[] keys = this._set;
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

   public TLongCollection valueCollection() {
      return new TValueView();
   }

   public long[] values() {
      long[] vals = new long[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         long[] v = this._values;
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

   public long[] values(long[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new long[size];
         }

         long[] v = this._values;
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

   public boolean containsValue(long val) {
      byte[] states = this._states;
      long[] vals = this._values;
      int i = vals.length;

      while(i-- > 0) {
         if (states[i] == 1 && val == vals[i]) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(int key) {
      return this.contains(key);
   }

   public TIntLongIterator iterator() {
      return new TIntLongHashIterator(this);
   }

   public boolean forEachKey(TIntProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TLongProcedure procedure) {
      byte[] states = this._states;
      long[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TIntLongProcedure procedure) {
      byte[] states = this._states;
      int[] keys = this._set;
      long[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TLongFunction function) {
      byte[] states = this._states;
      long[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TIntLongProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      int[] keys = this._set;
      long[] values = this._values;
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

   public boolean increment(int key) {
      return this.adjustValue(key, 1L);
   }

   public boolean adjustValue(int key, long amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         long[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public long adjustOrPutValue(int key, long adjust_amount, long put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      long newValue;
      if (index < 0) {
         index = -index - 1;
         long[] var10000 = this._values;
         newValue = var10000[index] += adjust_amount;
         isNewMapping = false;
      } else {
         newValue = this._values[index] = put_amount;
         isNewMapping = true;
      }

      byte var11 = this._states[index];
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean equals(Object other) {
      if (!(other instanceof TIntLongMap)) {
         return false;
      } else {
         TIntLongMap that = (TIntLongMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            long[] values = this._values;
            byte[] states = this._states;
            long this_no_entry_value = this.getNoEntryValue();
            long that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  int key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  long that_value = that.get(key);
                  long this_value = values[i];
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
      this.forEachEntry(new TIntLongProcedure() {
         private boolean first = true;

         public boolean execute(int key, long value) {
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
            out.writeInt(this._set[i]);
            out.writeLong(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         int key = in.readInt();
         long val = in.readLong();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TIntSet {
      public TIntIterator iterator() {
         return TIntLongHashMap.this.new TIntLongKeyHashIterator(TIntLongHashMap.this);
      }

      public int getNoEntryValue() {
         return TIntLongHashMap.this.no_entry_key;
      }

      public int size() {
         return TIntLongHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TIntLongHashMap.this._size;
      }

      public boolean contains(int entry) {
         return TIntLongHashMap.this.contains(entry);
      }

      public int[] toArray() {
         return TIntLongHashMap.this.keys();
      }

      public int[] toArray(int[] dest) {
         return TIntLongHashMap.this.keys(dest);
      }

      public boolean add(int entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(int entry) {
         return TIntLongHashMap.this.no_entry_value != TIntLongHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Integer)) {
               return false;
            }

            int ele = (Integer)element;
            if (!TIntLongHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TIntCollection collection) {
         TIntIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TIntLongHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(int[] array) {
         for(int element : array) {
            if (!TIntLongHashMap.this.contains(element)) {
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
         int[] set = TIntLongHashMap.this._set;
         byte[] states = TIntLongHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TIntLongHashMap.this.removeAt(i);
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
         TIntLongHashMap.this.clear();
      }

      public boolean forEach(TIntProcedure procedure) {
         return TIntLongHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TIntSet)) {
            return false;
         } else {
            TIntSet that = (TIntSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TIntLongHashMap.this._states.length;

               while(i-- > 0) {
                  if (TIntLongHashMap.this._states[i] == 1 && !that.contains(TIntLongHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TIntLongHashMap.this._states.length;

         while(i-- > 0) {
            if (TIntLongHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TIntLongHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TIntLongHashMap.this.forEachKey(new TIntProcedure() {
            private boolean first = true;

            public boolean execute(int key) {
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

   protected class TValueView implements TLongCollection {
      public TLongIterator iterator() {
         return TIntLongHashMap.this.new TIntLongValueHashIterator(TIntLongHashMap.this);
      }

      public long getNoEntryValue() {
         return TIntLongHashMap.this.no_entry_value;
      }

      public int size() {
         return TIntLongHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TIntLongHashMap.this._size;
      }

      public boolean contains(long entry) {
         return TIntLongHashMap.this.containsValue(entry);
      }

      public long[] toArray() {
         return TIntLongHashMap.this.values();
      }

      public long[] toArray(long[] dest) {
         return TIntLongHashMap.this.values(dest);
      }

      public boolean add(long entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long entry) {
         long[] values = TIntLongHashMap.this._values;
         byte[] states = TIntLongHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TIntLongHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Long)) {
               return false;
            }

            long ele = (Long)element;
            if (!TIntLongHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TLongCollection collection) {
         TLongIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TIntLongHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(long[] array) {
         for(long element : array) {
            if (!TIntLongHashMap.this.containsValue(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Long> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TLongCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long[] array) {
         throw new UnsupportedOperationException();
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
         long[] values = TIntLongHashMap.this._values;
         byte[] states = TIntLongHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TIntLongHashMap.this.removeAt(i);
               changed = true;
            }
         }

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
         if (this == collection) {
            this.clear();
            return true;
         } else {
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
         TIntLongHashMap.this.clear();
      }

      public boolean forEach(TLongProcedure procedure) {
         return TIntLongHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TIntLongHashMap.this.forEachValue(new TLongProcedure() {
            private boolean first = true;

            public boolean execute(long value) {
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

   class TIntLongKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {
      TIntLongKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public int next() {
         this.moveToNextIndex();
         return TIntLongHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TIntLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TIntLongValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public long next() {
         this.moveToNextIndex();
         return TIntLongHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TIntLongHashIterator extends THashPrimitiveIterator implements TIntLongIterator {
      TIntLongHashIterator(TIntLongHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public int key() {
         return TIntLongHashMap.this._set[this._index];
      }

      public long value() {
         return TIntLongHashMap.this._values[this._index];
      }

      public long setValue(long val) {
         long old = this.value();
         TIntLongHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TIntLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
