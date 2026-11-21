package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TDoubleLongHashMap extends TDoubleLongHash implements TDoubleLongMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TDoubleLongHashMap() {
   }

   public TDoubleLongHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TDoubleLongHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TDoubleLongHashMap(int initialCapacity, float loadFactor, double noEntryKey, long noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TDoubleLongHashMap(double[] keys, long[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TDoubleLongHashMap(TDoubleLongMap map) {
      super(map.size());
      if (map instanceof TDoubleLongHashMap) {
         TDoubleLongHashMap hashmap = (TDoubleLongHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != (double)0.0F) {
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
      double[] oldKeys = this._set;
      long[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new double[newCapacity];
      this._values = new long[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            double o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public long put(double key, long value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public long putIfAbsent(double key, long value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private long doPut(double key, long value, int index) {
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

   public void putAll(Map<? extends Double, ? extends Long> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Double, ? extends Long> entry : map.entrySet()) {
         this.put((Double)entry.getKey(), (Long)entry.getValue());
      }

   }

   public void putAll(TDoubleLongMap map) {
      this.ensureCapacity(map.size());
      TDoubleLongIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public long get(double key) {
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

   public long remove(double key) {
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

   public TDoubleSet keySet() {
      return new TKeyView();
   }

   public double[] keys() {
      double[] keys = new double[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         double[] k = this._set;
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

   public double[] keys(double[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new double[size];
         }

         double[] keys = this._set;
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

   public boolean containsKey(double key) {
      return this.contains(key);
   }

   public TDoubleLongIterator iterator() {
      return new TDoubleLongHashIterator(this);
   }

   public boolean forEachKey(TDoubleProcedure procedure) {
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

   public boolean forEachEntry(TDoubleLongProcedure procedure) {
      byte[] states = this._states;
      double[] keys = this._set;
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

   public boolean retainEntries(TDoubleLongProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      double[] keys = this._set;
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

   public boolean increment(double key) {
      return this.adjustValue(key, 1L);
   }

   public boolean adjustValue(double key, long amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         long[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public long adjustOrPutValue(double key, long adjust_amount, long put_amount) {
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

      byte var12 = this._states[index];
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean equals(Object other) {
      if (!(other instanceof TDoubleLongMap)) {
         return false;
      } else {
         TDoubleLongMap that = (TDoubleLongMap)other;
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
                  double key = this._set[i];
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
      this.forEachEntry(new TDoubleLongProcedure() {
         private boolean first = true;

         public boolean execute(double key, long value) {
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
            out.writeDouble(this._set[i]);
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
         double key = in.readDouble();
         long val = in.readLong();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TDoubleSet {
      public TDoubleIterator iterator() {
         return TDoubleLongHashMap.this.new TDoubleLongKeyHashIterator(TDoubleLongHashMap.this);
      }

      public double getNoEntryValue() {
         return TDoubleLongHashMap.this.no_entry_key;
      }

      public int size() {
         return TDoubleLongHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TDoubleLongHashMap.this._size;
      }

      public boolean contains(double entry) {
         return TDoubleLongHashMap.this.contains(entry);
      }

      public double[] toArray() {
         return TDoubleLongHashMap.this.keys();
      }

      public double[] toArray(double[] dest) {
         return TDoubleLongHashMap.this.keys(dest);
      }

      public boolean add(double entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double entry) {
         return TDoubleLongHashMap.this.no_entry_value != TDoubleLongHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Double)) {
               return false;
            }

            double ele = (Double)element;
            if (!TDoubleLongHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TDoubleCollection collection) {
         TDoubleIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TDoubleLongHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(double[] array) {
         for(double element : array) {
            if (!TDoubleLongHashMap.this.contains(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Double> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TDoubleCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(double[] array) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean modified = false;
         TDoubleIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }

      public boolean retainAll(TDoubleCollection collection) {
         if (this == collection) {
            return false;
         } else {
            boolean modified = false;
            TDoubleIterator iter = this.iterator();

            while(iter.hasNext()) {
               if (!collection.contains(iter.next())) {
                  iter.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }

      public boolean retainAll(double[] array) {
         boolean changed = false;
         Arrays.sort(array);
         double[] set = TDoubleLongHashMap.this._set;
         byte[] states = TDoubleLongHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TDoubleLongHashMap.this.removeAt(i);
               changed = true;
            }
         }

         return changed;
      }

      public boolean removeAll(Collection<?> collection) {
         boolean changed = false;

         for(Object element : collection) {
            if (element instanceof Double) {
               double c = (Double)element;
               if (this.remove(c)) {
                  changed = true;
               }
            }
         }

         return changed;
      }

      public boolean removeAll(TDoubleCollection collection) {
         if (this == collection) {
            this.clear();
            return true;
         } else {
            boolean changed = false;
            TDoubleIterator iter = collection.iterator();

            while(iter.hasNext()) {
               double element = iter.next();
               if (this.remove(element)) {
                  changed = true;
               }
            }

            return changed;
         }
      }

      public boolean removeAll(double[] array) {
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
         TDoubleLongHashMap.this.clear();
      }

      public boolean forEach(TDoubleProcedure procedure) {
         return TDoubleLongHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TDoubleSet)) {
            return false;
         } else {
            TDoubleSet that = (TDoubleSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TDoubleLongHashMap.this._states.length;

               while(i-- > 0) {
                  if (TDoubleLongHashMap.this._states[i] == 1 && !that.contains(TDoubleLongHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TDoubleLongHashMap.this._states.length;

         while(i-- > 0) {
            if (TDoubleLongHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TDoubleLongHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TDoubleLongHashMap.this.forEachKey(new TDoubleProcedure() {
            private boolean first = true;

            public boolean execute(double key) {
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
         return TDoubleLongHashMap.this.new TDoubleLongValueHashIterator(TDoubleLongHashMap.this);
      }

      public long getNoEntryValue() {
         return TDoubleLongHashMap.this.no_entry_value;
      }

      public int size() {
         return TDoubleLongHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TDoubleLongHashMap.this._size;
      }

      public boolean contains(long entry) {
         return TDoubleLongHashMap.this.containsValue(entry);
      }

      public long[] toArray() {
         return TDoubleLongHashMap.this.values();
      }

      public long[] toArray(long[] dest) {
         return TDoubleLongHashMap.this.values(dest);
      }

      public boolean add(long entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long entry) {
         long[] values = TDoubleLongHashMap.this._values;
         byte[] states = TDoubleLongHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TDoubleLongHashMap.this.removeAt(i);
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
            if (!TDoubleLongHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TLongCollection collection) {
         TLongIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TDoubleLongHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(long[] array) {
         for(long element : array) {
            if (!TDoubleLongHashMap.this.containsValue(element)) {
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
         long[] values = TDoubleLongHashMap.this._values;
         byte[] states = TDoubleLongHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TDoubleLongHashMap.this.removeAt(i);
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
         TDoubleLongHashMap.this.clear();
      }

      public boolean forEach(TLongProcedure procedure) {
         return TDoubleLongHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TDoubleLongHashMap.this.forEachValue(new TLongProcedure() {
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

   class TDoubleLongKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TDoubleLongKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public double next() {
         this.moveToNextIndex();
         return TDoubleLongHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TDoubleLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
      TDoubleLongValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public long next() {
         this.moveToNextIndex();
         return TDoubleLongHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TDoubleLongHashIterator extends THashPrimitiveIterator implements TDoubleLongIterator {
      TDoubleLongHashIterator(TDoubleLongHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public double key() {
         return TDoubleLongHashMap.this._set[this._index];
      }

      public long value() {
         return TDoubleLongHashMap.this._values[this._index];
      }

      public long setValue(long val) {
         long old = this.value();
         TDoubleLongHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleLongHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
