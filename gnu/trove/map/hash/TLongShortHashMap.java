package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongShortHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TLongShortMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TLongShortHashMap extends TLongShortHash implements TLongShortMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient short[] _values;

   public TLongShortHashMap() {
   }

   public TLongShortHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TLongShortHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TLongShortHashMap(int initialCapacity, float loadFactor, long noEntryKey, short noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TLongShortHashMap(long[] keys, short[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TLongShortHashMap(TLongShortMap map) {
      super(map.size());
      if (map instanceof TLongShortHashMap) {
         TLongShortHashMap hashmap = (TLongShortHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != 0L) {
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
      this._values = new short[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      long[] oldKeys = this._set;
      short[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new long[newCapacity];
      this._values = new short[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            long o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public short put(long key, short value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public short putIfAbsent(long key, short value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private short doPut(long key, short value, int index) {
      short previous = this.no_entry_value;
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

   public void putAll(Map<? extends Long, ? extends Short> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Long, ? extends Short> entry : map.entrySet()) {
         this.put((Long)entry.getKey(), (Short)entry.getValue());
      }

   }

   public void putAll(TLongShortMap map) {
      this.ensureCapacity(map.size());
      TLongShortIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public short get(long key) {
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

   public short remove(long key) {
      short prev = this.no_entry_value;
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

   public TLongSet keySet() {
      return new TKeyView();
   }

   public long[] keys() {
      long[] keys = new long[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         long[] k = this._set;
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

   public long[] keys(long[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new long[size];
         }

         long[] keys = this._set;
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

   public TShortCollection valueCollection() {
      return new TValueView();
   }

   public short[] values() {
      short[] vals = new short[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         short[] v = this._values;
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

   public short[] values(short[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new short[size];
         }

         short[] v = this._values;
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

   public boolean containsValue(short val) {
      byte[] states = this._states;
      short[] vals = this._values;
      int i = vals.length;

      while(i-- > 0) {
         if (states[i] == 1 && val == vals[i]) {
            return true;
         }
      }

      return false;
   }

   public boolean containsKey(long key) {
      return this.contains(key);
   }

   public TLongShortIterator iterator() {
      return new TLongShortHashIterator(this);
   }

   public boolean forEachKey(TLongProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TShortProcedure procedure) {
      byte[] states = this._states;
      short[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TLongShortProcedure procedure) {
      byte[] states = this._states;
      long[] keys = this._set;
      short[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TShortFunction function) {
      byte[] states = this._states;
      short[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TLongShortProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      long[] keys = this._set;
      short[] values = this._values;
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

   public boolean increment(long key) {
      return this.adjustValue(key, (short)1);
   }

   public boolean adjustValue(long key, short amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         short[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public short adjustOrPutValue(long key, short adjust_amount, short put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      short newValue;
      if (index < 0) {
         index = -index - 1;
         short[] var10000 = this._values;
         newValue = var10000[index] += adjust_amount;
         isNewMapping = false;
      } else {
         newValue = this._values[index] = put_amount;
         isNewMapping = true;
      }

      byte var9 = this._states[index];
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean equals(Object other) {
      if (!(other instanceof TLongShortMap)) {
         return false;
      } else {
         TLongShortMap that = (TLongShortMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            short[] values = this._values;
            byte[] states = this._states;
            short this_no_entry_value = this.getNoEntryValue();
            short that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  long key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  short that_value = that.get(key);
                  short this_value = values[i];
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
      this.forEachEntry(new TLongShortProcedure() {
         private boolean first = true;

         public boolean execute(long key, short value) {
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
            out.writeLong(this._set[i]);
            out.writeShort(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         long key = in.readLong();
         short val = in.readShort();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TLongSet {
      public TLongIterator iterator() {
         return TLongShortHashMap.this.new TLongShortKeyHashIterator(TLongShortHashMap.this);
      }

      public long getNoEntryValue() {
         return TLongShortHashMap.this.no_entry_key;
      }

      public int size() {
         return TLongShortHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TLongShortHashMap.this._size;
      }

      public boolean contains(long entry) {
         return TLongShortHashMap.this.contains(entry);
      }

      public long[] toArray() {
         return TLongShortHashMap.this.keys();
      }

      public long[] toArray(long[] dest) {
         return TLongShortHashMap.this.keys(dest);
      }

      public boolean add(long entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long entry) {
         return TLongShortHashMap.this.no_entry_value != TLongShortHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Long)) {
               return false;
            }

            long ele = (Long)element;
            if (!TLongShortHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TLongCollection collection) {
         TLongIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TLongShortHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(long[] array) {
         for(long element : array) {
            if (!TLongShortHashMap.this.contains(element)) {
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
         long[] set = TLongShortHashMap.this._set;
         byte[] states = TLongShortHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TLongShortHashMap.this.removeAt(i);
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
         TLongShortHashMap.this.clear();
      }

      public boolean forEach(TLongProcedure procedure) {
         return TLongShortHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TLongSet)) {
            return false;
         } else {
            TLongSet that = (TLongSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TLongShortHashMap.this._states.length;

               while(i-- > 0) {
                  if (TLongShortHashMap.this._states[i] == 1 && !that.contains(TLongShortHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TLongShortHashMap.this._states.length;

         while(i-- > 0) {
            if (TLongShortHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TLongShortHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TLongShortHashMap.this.forEachKey(new TLongProcedure() {
            private boolean first = true;

            public boolean execute(long key) {
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

   protected class TValueView implements TShortCollection {
      public TShortIterator iterator() {
         return TLongShortHashMap.this.new TLongShortValueHashIterator(TLongShortHashMap.this);
      }

      public short getNoEntryValue() {
         return TLongShortHashMap.this.no_entry_value;
      }

      public int size() {
         return TLongShortHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TLongShortHashMap.this._size;
      }

      public boolean contains(short entry) {
         return TLongShortHashMap.this.containsValue(entry);
      }

      public short[] toArray() {
         return TLongShortHashMap.this.values();
      }

      public short[] toArray(short[] dest) {
         return TLongShortHashMap.this.values(dest);
      }

      public boolean add(short entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short entry) {
         short[] values = TLongShortHashMap.this._values;
         byte[] states = TLongShortHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TLongShortHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Short)) {
               return false;
            }

            short ele = (Short)element;
            if (!TLongShortHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TShortCollection collection) {
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TLongShortHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(short[] array) {
         for(short element : array) {
            if (!TLongShortHashMap.this.containsValue(element)) {
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
         short[] values = TLongShortHashMap.this._values;
         byte[] states = TLongShortHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TLongShortHashMap.this.removeAt(i);
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
         TLongShortHashMap.this.clear();
      }

      public boolean forEach(TShortProcedure procedure) {
         return TLongShortHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TLongShortHashMap.this.forEachValue(new TShortProcedure() {
            private boolean first = true;

            public boolean execute(short value) {
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

   class TLongShortKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TLongShortKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public long next() {
         this.moveToNextIndex();
         return TLongShortHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TLongShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TLongShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TLongShortValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public short next() {
         this.moveToNextIndex();
         return TLongShortHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TLongShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TLongShortHashIterator extends THashPrimitiveIterator implements TLongShortIterator {
      TLongShortHashIterator(TLongShortHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public long key() {
         return TLongShortHashMap.this._set[this._index];
      }

      public short value() {
         return TLongShortHashMap.this._values[this._index];
      }

      public short setValue(short val) {
         short old = this.value();
         TLongShortHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TLongShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
