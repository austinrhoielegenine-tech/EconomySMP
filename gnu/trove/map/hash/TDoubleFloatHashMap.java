package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TDoubleFloatHashMap extends TDoubleFloatHash implements TDoubleFloatMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient float[] _values;

   public TDoubleFloatHashMap() {
   }

   public TDoubleFloatHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TDoubleFloatHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TDoubleFloatHashMap(int initialCapacity, float loadFactor, double noEntryKey, float noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TDoubleFloatHashMap(double[] keys, float[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TDoubleFloatHashMap(TDoubleFloatMap map) {
      super(map.size());
      if (map instanceof TDoubleFloatHashMap) {
         TDoubleFloatHashMap hashmap = (TDoubleFloatHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != (double)0.0F) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0.0F) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.putAll(map);
   }

   protected int setUp(int initialCapacity) {
      int capacity = super.setUp(initialCapacity);
      this._values = new float[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      double[] oldKeys = this._set;
      float[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new double[newCapacity];
      this._values = new float[newCapacity];
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

   public float put(double key, float value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public float putIfAbsent(double key, float value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private float doPut(double key, float value, int index) {
      float previous = this.no_entry_value;
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

   public void putAll(Map<? extends Double, ? extends Float> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Double, ? extends Float> entry : map.entrySet()) {
         this.put((Double)entry.getKey(), (Float)entry.getValue());
      }

   }

   public void putAll(TDoubleFloatMap map) {
      this.ensureCapacity(map.size());
      TDoubleFloatIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public float get(double key) {
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

   public float remove(double key) {
      float prev = this.no_entry_value;
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

   public TFloatCollection valueCollection() {
      return new TValueView();
   }

   public float[] values() {
      float[] vals = new float[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         float[] v = this._values;
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

   public float[] values(float[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new float[size];
         }

         float[] v = this._values;
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

   public boolean containsValue(float val) {
      byte[] states = this._states;
      float[] vals = this._values;
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

   public TDoubleFloatIterator iterator() {
      return new TDoubleFloatHashIterator(this);
   }

   public boolean forEachKey(TDoubleProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TFloatProcedure procedure) {
      byte[] states = this._states;
      float[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TDoubleFloatProcedure procedure) {
      byte[] states = this._states;
      double[] keys = this._set;
      float[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TFloatFunction function) {
      byte[] states = this._states;
      float[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TDoubleFloatProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      double[] keys = this._set;
      float[] values = this._values;
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
      return this.adjustValue(key, 1.0F);
   }

   public boolean adjustValue(double key, float amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         float[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public float adjustOrPutValue(double key, float adjust_amount, float put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      float newValue;
      if (index < 0) {
         index = -index - 1;
         float[] var10000 = this._values;
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
      if (!(other instanceof TDoubleFloatMap)) {
         return false;
      } else {
         TDoubleFloatMap that = (TDoubleFloatMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            float[] values = this._values;
            byte[] states = this._states;
            float this_no_entry_value = this.getNoEntryValue();
            float that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  double key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  float that_value = that.get(key);
                  float this_value = values[i];
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
      this.forEachEntry(new TDoubleFloatProcedure() {
         private boolean first = true;

         public boolean execute(double key, float value) {
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
            out.writeFloat(this._values[i]);
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
         float val = in.readFloat();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TDoubleSet {
      public TDoubleIterator iterator() {
         return TDoubleFloatHashMap.this.new TDoubleFloatKeyHashIterator(TDoubleFloatHashMap.this);
      }

      public double getNoEntryValue() {
         return TDoubleFloatHashMap.this.no_entry_key;
      }

      public int size() {
         return TDoubleFloatHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TDoubleFloatHashMap.this._size;
      }

      public boolean contains(double entry) {
         return TDoubleFloatHashMap.this.contains(entry);
      }

      public double[] toArray() {
         return TDoubleFloatHashMap.this.keys();
      }

      public double[] toArray(double[] dest) {
         return TDoubleFloatHashMap.this.keys(dest);
      }

      public boolean add(double entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double entry) {
         return TDoubleFloatHashMap.this.no_entry_value != TDoubleFloatHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Double)) {
               return false;
            }

            double ele = (Double)element;
            if (!TDoubleFloatHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TDoubleCollection collection) {
         TDoubleIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TDoubleFloatHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(double[] array) {
         for(double element : array) {
            if (!TDoubleFloatHashMap.this.contains(element)) {
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
         double[] set = TDoubleFloatHashMap.this._set;
         byte[] states = TDoubleFloatHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TDoubleFloatHashMap.this.removeAt(i);
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
         TDoubleFloatHashMap.this.clear();
      }

      public boolean forEach(TDoubleProcedure procedure) {
         return TDoubleFloatHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TDoubleSet)) {
            return false;
         } else {
            TDoubleSet that = (TDoubleSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TDoubleFloatHashMap.this._states.length;

               while(i-- > 0) {
                  if (TDoubleFloatHashMap.this._states[i] == 1 && !that.contains(TDoubleFloatHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TDoubleFloatHashMap.this._states.length;

         while(i-- > 0) {
            if (TDoubleFloatHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TDoubleFloatHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TDoubleFloatHashMap.this.forEachKey(new TDoubleProcedure() {
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

   protected class TValueView implements TFloatCollection {
      public TFloatIterator iterator() {
         return TDoubleFloatHashMap.this.new TDoubleFloatValueHashIterator(TDoubleFloatHashMap.this);
      }

      public float getNoEntryValue() {
         return TDoubleFloatHashMap.this.no_entry_value;
      }

      public int size() {
         return TDoubleFloatHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TDoubleFloatHashMap.this._size;
      }

      public boolean contains(float entry) {
         return TDoubleFloatHashMap.this.containsValue(entry);
      }

      public float[] toArray() {
         return TDoubleFloatHashMap.this.values();
      }

      public float[] toArray(float[] dest) {
         return TDoubleFloatHashMap.this.values(dest);
      }

      public boolean add(float entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float entry) {
         float[] values = TDoubleFloatHashMap.this._values;
         byte[] states = TDoubleFloatHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TDoubleFloatHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Float)) {
               return false;
            }

            float ele = (Float)element;
            if (!TDoubleFloatHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TFloatCollection collection) {
         TFloatIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TDoubleFloatHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(float[] array) {
         for(float element : array) {
            if (!TDoubleFloatHashMap.this.containsValue(element)) {
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
         float[] values = TDoubleFloatHashMap.this._values;
         byte[] states = TDoubleFloatHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TDoubleFloatHashMap.this.removeAt(i);
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
         TDoubleFloatHashMap.this.clear();
      }

      public boolean forEach(TFloatProcedure procedure) {
         return TDoubleFloatHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TDoubleFloatHashMap.this.forEachValue(new TFloatProcedure() {
            private boolean first = true;

            public boolean execute(float value) {
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

   class TDoubleFloatKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TDoubleFloatKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public double next() {
         this.moveToNextIndex();
         return TDoubleFloatHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleFloatHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TDoubleFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {
      TDoubleFloatValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public float next() {
         this.moveToNextIndex();
         return TDoubleFloatHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleFloatHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TDoubleFloatHashIterator extends THashPrimitiveIterator implements TDoubleFloatIterator {
      TDoubleFloatHashIterator(TDoubleFloatHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public double key() {
         return TDoubleFloatHashMap.this._set[this._index];
      }

      public float value() {
         return TDoubleFloatHashMap.this._values[this._index];
      }

      public float setValue(float val) {
         float old = this.value();
         TDoubleFloatHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleFloatHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
