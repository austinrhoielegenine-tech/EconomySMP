package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortDoubleHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
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

public class TShortDoubleHashMap extends TShortDoubleHash implements TShortDoubleMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient double[] _values;

   public TShortDoubleHashMap() {
   }

   public TShortDoubleHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TShortDoubleHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TShortDoubleHashMap(int initialCapacity, float loadFactor, short noEntryKey, double noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TShortDoubleHashMap(short[] keys, double[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TShortDoubleHashMap(TShortDoubleMap map) {
      super(map.size());
      if (map instanceof TShortDoubleHashMap) {
         TShortDoubleHashMap hashmap = (TShortDoubleHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != (double)0.0F) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.putAll(map);
   }

   protected int setUp(int initialCapacity) {
      int capacity = super.setUp(initialCapacity);
      this._values = new double[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      short[] oldKeys = this._set;
      double[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new short[newCapacity];
      this._values = new double[newCapacity];
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

   public double put(short key, double value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public double putIfAbsent(short key, double value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private double doPut(short key, double value, int index) {
      double previous = this.no_entry_value;
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

   public void putAll(Map<? extends Short, ? extends Double> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Short, ? extends Double> entry : map.entrySet()) {
         this.put((Short)entry.getKey(), (Double)entry.getValue());
      }

   }

   public void putAll(TShortDoubleMap map) {
      this.ensureCapacity(map.size());
      TShortDoubleIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public double get(short key) {
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

   public double remove(short key) {
      double prev = this.no_entry_value;
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

   public TDoubleCollection valueCollection() {
      return new TValueView();
   }

   public double[] values() {
      double[] vals = new double[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         double[] v = this._values;
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

   public double[] values(double[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new double[size];
         }

         double[] v = this._values;
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

   public boolean containsValue(double val) {
      byte[] states = this._states;
      double[] vals = this._values;
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

   public TShortDoubleIterator iterator() {
      return new TShortDoubleHashIterator(this);
   }

   public boolean forEachKey(TShortProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TDoubleProcedure procedure) {
      byte[] states = this._states;
      double[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TShortDoubleProcedure procedure) {
      byte[] states = this._states;
      short[] keys = this._set;
      double[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TDoubleFunction function) {
      byte[] states = this._states;
      double[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TShortDoubleProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      short[] keys = this._set;
      double[] values = this._values;
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
      return this.adjustValue(key, (double)1.0F);
   }

   public boolean adjustValue(short key, double amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         double[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public double adjustOrPutValue(short key, double adjust_amount, double put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      double newValue;
      if (index < 0) {
         index = -index - 1;
         double[] var10000 = this._values;
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
      if (!(other instanceof TShortDoubleMap)) {
         return false;
      } else {
         TShortDoubleMap that = (TShortDoubleMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            double[] values = this._values;
            byte[] states = this._states;
            double this_no_entry_value = this.getNoEntryValue();
            double that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  short key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  double that_value = that.get(key);
                  double this_value = values[i];
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
      this.forEachEntry(new TShortDoubleProcedure() {
         private boolean first = true;

         public boolean execute(short key, double value) {
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
            out.writeDouble(this._values[i]);
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
         double val = in.readDouble();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TShortSet {
      public TShortIterator iterator() {
         return TShortDoubleHashMap.this.new TShortDoubleKeyHashIterator(TShortDoubleHashMap.this);
      }

      public short getNoEntryValue() {
         return TShortDoubleHashMap.this.no_entry_key;
      }

      public int size() {
         return TShortDoubleHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TShortDoubleHashMap.this._size;
      }

      public boolean contains(short entry) {
         return TShortDoubleHashMap.this.contains(entry);
      }

      public short[] toArray() {
         return TShortDoubleHashMap.this.keys();
      }

      public short[] toArray(short[] dest) {
         return TShortDoubleHashMap.this.keys(dest);
      }

      public boolean add(short entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short entry) {
         return TShortDoubleHashMap.this.no_entry_value != TShortDoubleHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Short)) {
               return false;
            }

            short ele = (Short)element;
            if (!TShortDoubleHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TShortCollection collection) {
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TShortDoubleHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(short[] array) {
         for(short element : array) {
            if (!TShortDoubleHashMap.this.contains(element)) {
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
         short[] set = TShortDoubleHashMap.this._set;
         byte[] states = TShortDoubleHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TShortDoubleHashMap.this.removeAt(i);
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
         TShortDoubleHashMap.this.clear();
      }

      public boolean forEach(TShortProcedure procedure) {
         return TShortDoubleHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TShortSet)) {
            return false;
         } else {
            TShortSet that = (TShortSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TShortDoubleHashMap.this._states.length;

               while(i-- > 0) {
                  if (TShortDoubleHashMap.this._states[i] == 1 && !that.contains(TShortDoubleHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TShortDoubleHashMap.this._states.length;

         while(i-- > 0) {
            if (TShortDoubleHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TShortDoubleHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TShortDoubleHashMap.this.forEachKey(new TShortProcedure() {
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

   protected class TValueView implements TDoubleCollection {
      public TDoubleIterator iterator() {
         return TShortDoubleHashMap.this.new TShortDoubleValueHashIterator(TShortDoubleHashMap.this);
      }

      public double getNoEntryValue() {
         return TShortDoubleHashMap.this.no_entry_value;
      }

      public int size() {
         return TShortDoubleHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TShortDoubleHashMap.this._size;
      }

      public boolean contains(double entry) {
         return TShortDoubleHashMap.this.containsValue(entry);
      }

      public double[] toArray() {
         return TShortDoubleHashMap.this.values();
      }

      public double[] toArray(double[] dest) {
         return TShortDoubleHashMap.this.values(dest);
      }

      public boolean add(double entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double entry) {
         double[] values = TShortDoubleHashMap.this._values;
         byte[] states = TShortDoubleHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TShortDoubleHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Double)) {
               return false;
            }

            double ele = (Double)element;
            if (!TShortDoubleHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TDoubleCollection collection) {
         TDoubleIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TShortDoubleHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(double[] array) {
         for(double element : array) {
            if (!TShortDoubleHashMap.this.containsValue(element)) {
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
         double[] values = TShortDoubleHashMap.this._values;
         byte[] states = TShortDoubleHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TShortDoubleHashMap.this.removeAt(i);
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
         TShortDoubleHashMap.this.clear();
      }

      public boolean forEach(TDoubleProcedure procedure) {
         return TShortDoubleHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TShortDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
            private boolean first = true;

            public boolean execute(double value) {
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

   class TShortDoubleKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TShortDoubleKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public short next() {
         this.moveToNextIndex();
         return TShortDoubleHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TShortDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TShortDoubleValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public double next() {
         this.moveToNextIndex();
         return TShortDoubleHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TShortDoubleHashIterator extends THashPrimitiveIterator implements TShortDoubleIterator {
      TShortDoubleHashIterator(TShortDoubleHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public short key() {
         return TShortDoubleHashMap.this._set[this._index];
      }

      public double value() {
         return TShortDoubleHashMap.this._values[this._index];
      }

      public double setValue(double val) {
         double old = this.value();
         TShortDoubleHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
