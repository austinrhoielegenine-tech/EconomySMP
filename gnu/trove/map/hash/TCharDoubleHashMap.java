package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TCharDoubleHashMap extends TCharDoubleHash implements TCharDoubleMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient double[] _values;

   public TCharDoubleHashMap() {
   }

   public TCharDoubleHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TCharDoubleHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TCharDoubleHashMap(int initialCapacity, float loadFactor, char noEntryKey, double noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TCharDoubleHashMap(char[] keys, double[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TCharDoubleHashMap(TCharDoubleMap map) {
      super(map.size());
      if (map instanceof TCharDoubleHashMap) {
         TCharDoubleHashMap hashmap = (TCharDoubleHashMap)map;
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
      char[] oldKeys = this._set;
      double[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new char[newCapacity];
      this._values = new double[newCapacity];
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            char o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public double put(char key, double value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public double putIfAbsent(char key, double value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private double doPut(char key, double value, int index) {
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

   public void putAll(Map<? extends Character, ? extends Double> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Character, ? extends Double> entry : map.entrySet()) {
         this.put((Character)entry.getKey(), (Double)entry.getValue());
      }

   }

   public void putAll(TCharDoubleMap map) {
      this.ensureCapacity(map.size());
      TCharDoubleIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public double get(char key) {
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

   public double remove(char key) {
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

   public TCharSet keySet() {
      return new TKeyView();
   }

   public char[] keys() {
      char[] keys = new char[this.size()];
      if (keys.length == 0) {
         return keys;
      } else {
         char[] k = this._set;
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

   public char[] keys(char[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new char[size];
         }

         char[] keys = this._set;
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

   public boolean containsKey(char key) {
      return this.contains(key);
   }

   public TCharDoubleIterator iterator() {
      return new TCharDoubleHashIterator(this);
   }

   public boolean forEachKey(TCharProcedure procedure) {
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

   public boolean forEachEntry(TCharDoubleProcedure procedure) {
      byte[] states = this._states;
      char[] keys = this._set;
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

   public boolean retainEntries(TCharDoubleProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      char[] keys = this._set;
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

   public boolean increment(char key) {
      return this.adjustValue(key, (double)1.0F);
   }

   public boolean adjustValue(char key, double amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         double[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public double adjustOrPutValue(char key, double adjust_amount, double put_amount) {
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
      if (!(other instanceof TCharDoubleMap)) {
         return false;
      } else {
         TCharDoubleMap that = (TCharDoubleMap)other;
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
                  char key = this._set[i];
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
      this.forEachEntry(new TCharDoubleProcedure() {
         private boolean first = true;

         public boolean execute(char key, double value) {
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
            out.writeChar(this._set[i]);
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
         char key = in.readChar();
         double val = in.readDouble();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TCharSet {
      public TCharIterator iterator() {
         return TCharDoubleHashMap.this.new TCharDoubleKeyHashIterator(TCharDoubleHashMap.this);
      }

      public char getNoEntryValue() {
         return TCharDoubleHashMap.this.no_entry_key;
      }

      public int size() {
         return TCharDoubleHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TCharDoubleHashMap.this._size;
      }

      public boolean contains(char entry) {
         return TCharDoubleHashMap.this.contains(entry);
      }

      public char[] toArray() {
         return TCharDoubleHashMap.this.keys();
      }

      public char[] toArray(char[] dest) {
         return TCharDoubleHashMap.this.keys(dest);
      }

      public boolean add(char entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char entry) {
         return TCharDoubleHashMap.this.no_entry_value != TCharDoubleHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Character)) {
               return false;
            }

            char ele = (Character)element;
            if (!TCharDoubleHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TCharCollection collection) {
         TCharIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TCharDoubleHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(char[] array) {
         for(char element : array) {
            if (!TCharDoubleHashMap.this.contains(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Character> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TCharCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(char[] array) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean modified = false;
         TCharIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }

      public boolean retainAll(TCharCollection collection) {
         if (this == collection) {
            return false;
         } else {
            boolean modified = false;
            TCharIterator iter = this.iterator();

            while(iter.hasNext()) {
               if (!collection.contains(iter.next())) {
                  iter.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }

      public boolean retainAll(char[] array) {
         boolean changed = false;
         Arrays.sort(array);
         char[] set = TCharDoubleHashMap.this._set;
         byte[] states = TCharDoubleHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TCharDoubleHashMap.this.removeAt(i);
               changed = true;
            }
         }

         return changed;
      }

      public boolean removeAll(Collection<?> collection) {
         boolean changed = false;

         for(Object element : collection) {
            if (element instanceof Character) {
               char c = (Character)element;
               if (this.remove(c)) {
                  changed = true;
               }
            }
         }

         return changed;
      }

      public boolean removeAll(TCharCollection collection) {
         if (this == collection) {
            this.clear();
            return true;
         } else {
            boolean changed = false;
            TCharIterator iter = collection.iterator();

            while(iter.hasNext()) {
               char element = iter.next();
               if (this.remove(element)) {
                  changed = true;
               }
            }

            return changed;
         }
      }

      public boolean removeAll(char[] array) {
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
         TCharDoubleHashMap.this.clear();
      }

      public boolean forEach(TCharProcedure procedure) {
         return TCharDoubleHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TCharSet)) {
            return false;
         } else {
            TCharSet that = (TCharSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TCharDoubleHashMap.this._states.length;

               while(i-- > 0) {
                  if (TCharDoubleHashMap.this._states[i] == 1 && !that.contains(TCharDoubleHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TCharDoubleHashMap.this._states.length;

         while(i-- > 0) {
            if (TCharDoubleHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TCharDoubleHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TCharDoubleHashMap.this.forEachKey(new TCharProcedure() {
            private boolean first = true;

            public boolean execute(char key) {
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
         return TCharDoubleHashMap.this.new TCharDoubleValueHashIterator(TCharDoubleHashMap.this);
      }

      public double getNoEntryValue() {
         return TCharDoubleHashMap.this.no_entry_value;
      }

      public int size() {
         return TCharDoubleHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TCharDoubleHashMap.this._size;
      }

      public boolean contains(double entry) {
         return TCharDoubleHashMap.this.containsValue(entry);
      }

      public double[] toArray() {
         return TCharDoubleHashMap.this.values();
      }

      public double[] toArray(double[] dest) {
         return TCharDoubleHashMap.this.values(dest);
      }

      public boolean add(double entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double entry) {
         double[] values = TCharDoubleHashMap.this._values;
         byte[] states = TCharDoubleHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TCharDoubleHashMap.this.removeAt(i);
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
            if (!TCharDoubleHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TDoubleCollection collection) {
         TDoubleIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TCharDoubleHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(double[] array) {
         for(double element : array) {
            if (!TCharDoubleHashMap.this.containsValue(element)) {
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
         double[] values = TCharDoubleHashMap.this._values;
         byte[] states = TCharDoubleHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TCharDoubleHashMap.this.removeAt(i);
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
         TCharDoubleHashMap.this.clear();
      }

      public boolean forEach(TDoubleProcedure procedure) {
         return TCharDoubleHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TCharDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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

   class TCharDoubleKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TCharDoubleKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public char next() {
         this.moveToNextIndex();
         return TCharDoubleHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TCharDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TCharDoubleValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public double next() {
         this.moveToNextIndex();
         return TCharDoubleHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TCharDoubleHashIterator extends THashPrimitiveIterator implements TCharDoubleIterator {
      TCharDoubleHashIterator(TCharDoubleHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public char key() {
         return TCharDoubleHashMap.this._set[this._index];
      }

      public double value() {
         return TCharDoubleHashMap.this._values[this._index];
      }

      public double setValue(double val) {
         double old = this.value();
         TCharDoubleHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharDoubleHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
