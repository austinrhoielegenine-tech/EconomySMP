package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TByteLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TByteLongMap;
import gnu.trove.procedure.TByteLongProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TByteLongHashMap extends TByteLongHash implements TByteLongMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TByteLongHashMap() {
   }

   public TByteLongHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TByteLongHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TByteLongHashMap(int initialCapacity, float loadFactor, byte noEntryKey, long noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TByteLongHashMap(byte[] keys, long[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TByteLongHashMap(TByteLongMap map) {
      super(map.size());
      if (map instanceof TByteLongHashMap) {
         TByteLongHashMap hashmap = (TByteLongHashMap)map;
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
      byte[] oldKeys = this._set;
      long[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new byte[newCapacity];
      this._values = new long[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            byte o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public long put(byte key, long value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public long putIfAbsent(byte key, long value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private long doPut(byte key, long value, int index) {
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

   public void putAll(Map<? extends Byte, ? extends Long> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Byte, ? extends Long> entry : map.entrySet()) {
         this.put((Byte)entry.getKey(), (Long)entry.getValue());
      }

   }

   public void putAll(TByteLongMap map) {
      this.ensureCapacity(map.size());
      TByteLongIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public long get(byte key) {
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

   public long remove(byte key) {
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

   public TByteSet keySet() {
      return new TKeyView();
   }

   public byte[] keys() {
      byte[] keys = new byte[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         byte[] k = this._set;
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

   public byte[] keys(byte[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new byte[size];
         }

         byte[] keys = this._set;
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

   public boolean containsKey(byte key) {
      return this.contains(key);
   }

   public TByteLongIterator iterator() {
      return new TByteLongHashIterator(this);
   }

   public boolean forEachKey(TByteProcedure procedure) {
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

   public boolean forEachEntry(TByteLongProcedure procedure) {
      byte[] states = this._states;
      byte[] keys = this._set;
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

   public boolean retainEntries(TByteLongProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      byte[] keys = this._set;
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

   public boolean increment(byte key) {
      return this.adjustValue(key, 1L);
   }

   public boolean adjustValue(byte key, long amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         long[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public long adjustOrPutValue(byte key, long adjust_amount, long put_amount) {
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
      if (!(other instanceof TByteLongMap)) {
         return false;
      } else {
         TByteLongMap that = (TByteLongMap)other;
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
                  byte key = this._set[i];
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
      this.forEachEntry(new TByteLongProcedure() {
         private boolean first = true;

         public boolean execute(byte key, long value) {
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
            out.writeByte(this._set[i]);
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
         byte key = in.readByte();
         long val = in.readLong();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TByteSet {
      public TByteIterator iterator() {
         return TByteLongHashMap.this.new TByteLongKeyHashIterator(TByteLongHashMap.this);
      }

      public byte getNoEntryValue() {
         return TByteLongHashMap.this.no_entry_key;
      }

      public int size() {
         return TByteLongHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TByteLongHashMap.this._size;
      }

      public boolean contains(byte entry) {
         return TByteLongHashMap.this.contains(entry);
      }

      public byte[] toArray() {
         return TByteLongHashMap.this.keys();
      }

      public byte[] toArray(byte[] dest) {
         return TByteLongHashMap.this.keys(dest);
      }

      public boolean add(byte entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte entry) {
         return TByteLongHashMap.this.no_entry_value != TByteLongHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Byte)) {
               return false;
            }

            byte ele = (Byte)element;
            if (!TByteLongHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TByteCollection collection) {
         TByteIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TByteLongHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(byte[] array) {
         for(byte element : array) {
            if (!TByteLongHashMap.this.contains(element)) {
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
         byte[] set = TByteLongHashMap.this._set;
         byte[] states = TByteLongHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TByteLongHashMap.this.removeAt(i);
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
         TByteLongHashMap.this.clear();
      }

      public boolean forEach(TByteProcedure procedure) {
         return TByteLongHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TByteSet)) {
            return false;
         } else {
            TByteSet that = (TByteSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TByteLongHashMap.this._states.length;

               while(i-- > 0) {
                  if (TByteLongHashMap.this._states[i] == 1 && !that.contains(TByteLongHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TByteLongHashMap.this._states.length;

         while(i-- > 0) {
            if (TByteLongHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TByteLongHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TByteLongHashMap.this.forEachKey(new TByteProcedure() {
            private boolean first = true;

            public boolean execute(byte key) {
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
         return TByteLongHashMap.this.new TByteLongValueHashIterator(TByteLongHashMap.this);
      }

      public long getNoEntryValue() {
         return TByteLongHashMap.this.no_entry_value;
      }

      public int size() {
         return TByteLongHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TByteLongHashMap.this._size;
      }

      public boolean contains(long entry) {
         return TByteLongHashMap.this.containsValue(entry);
      }

      public long[] toArray() {
         return TByteLongHashMap.this.values();
      }

      public long[] toArray(long[] dest) {
         return TByteLongHashMap.this.values(dest);
      }

      public boolean add(long entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long entry) {
         long[] values = TByteLongHashMap.this._values;
         byte[] states = TByteLongHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TByteLongHashMap.this.removeAt(i);
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
            if (!TByteLongHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TLongCollection collection) {
         TLongIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TByteLongHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(long[] array) {
         for(long element : array) {
            if (!TByteLongHashMap.this.containsValue(element)) {
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
         long[] values = TByteLongHashMap.this._values;
         byte[] states = TByteLongHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TByteLongHashMap.this.removeAt(i);
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
         TByteLongHashMap.this.clear();
      }

      public boolean forEach(TLongProcedure procedure) {
         return TByteLongHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TByteLongHashMap.this.forEachValue(new TLongProcedure() {
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

   class TByteLongKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {
      TByteLongKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public byte next() {
         this.moveToNextIndex();
         return TByteLongHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TByteLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TByteLongValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public long next() {
         this.moveToNextIndex();
         return TByteLongHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TByteLongHashIterator extends THashPrimitiveIterator implements TByteLongIterator {
      TByteLongHashIterator(TByteLongHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public byte key() {
         return TByteLongHashMap.this._set[this._index];
      }

      public long value() {
         return TByteLongHashMap.this._values[this._index];
      }

      public long setValue(long val) {
         long old = this.value();
         TByteLongHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TByteLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
