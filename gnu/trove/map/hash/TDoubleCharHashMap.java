package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TDoubleCharHashMap extends TDoubleCharHash implements TDoubleCharMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient char[] _values;

   public TDoubleCharHashMap() {
   }

   public TDoubleCharHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TDoubleCharHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TDoubleCharHashMap(int initialCapacity, float loadFactor, double noEntryKey, char noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TDoubleCharHashMap(double[] keys, char[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TDoubleCharHashMap(TDoubleCharMap map) {
      super(map.size());
      if (map instanceof TDoubleCharHashMap) {
         TDoubleCharHashMap hashmap = (TDoubleCharHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_key = hashmap.no_entry_key;
         this.no_entry_value = hashmap.no_entry_value;
         if (this.no_entry_key != (double)0.0F) {
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
      this._values = new char[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      double[] oldKeys = this._set;
      char[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new double[newCapacity];
      this._values = new char[newCapacity];
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

   public char put(double key, char value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public char putIfAbsent(double key, char value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private char doPut(double key, char value, int index) {
      char previous = this.no_entry_value;
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

   public void putAll(Map<? extends Double, ? extends Character> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Double, ? extends Character> entry : map.entrySet()) {
         this.put((Double)entry.getKey(), (Character)entry.getValue());
      }

   }

   public void putAll(TDoubleCharMap map) {
      this.ensureCapacity(map.size());
      TDoubleCharIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public char get(double key) {
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

   public char remove(double key) {
      char prev = this.no_entry_value;
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

   public TCharCollection valueCollection() {
      return new TValueView();
   }

   public char[] values() {
      char[] vals = new char[this.size()];
      if (vals.length == 0) {
         return vals;
      } else {
         char[] v = this._values;
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

   public char[] values(char[] array) {
      int size = this.size();
      if (size == 0) {
         return array;
      } else {
         if (array.length < size) {
            array = new char[size];
         }

         char[] v = this._values;
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

   public boolean containsValue(char val) {
      byte[] states = this._states;
      char[] vals = this._values;
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

   public TDoubleCharIterator iterator() {
      return new TDoubleCharHashIterator(this);
   }

   public boolean forEachKey(TDoubleProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TCharProcedure procedure) {
      byte[] states = this._states;
      char[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TDoubleCharProcedure procedure) {
      byte[] states = this._states;
      double[] keys = this._set;
      char[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public void transformValues(TCharFunction function) {
      byte[] states = this._states;
      char[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean retainEntries(TDoubleCharProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      double[] keys = this._set;
      char[] values = this._values;
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
      return this.adjustValue(key, '\u0001');
   }

   public boolean adjustValue(double key, char amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         char[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public char adjustOrPutValue(double key, char adjust_amount, char put_amount) {
      int index = this.insertKey(key);
      boolean isNewMapping;
      char newValue;
      if (index < 0) {
         index = -index - 1;
         char[] var10000 = this._values;
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
      if (!(other instanceof TDoubleCharMap)) {
         return false;
      } else {
         TDoubleCharMap that = (TDoubleCharMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            char[] values = this._values;
            byte[] states = this._states;
            char this_no_entry_value = this.getNoEntryValue();
            char that_no_entry_value = that.getNoEntryValue();
            int i = values.length;

            while(i-- > 0) {
               if (states[i] == 1) {
                  double key = this._set[i];
                  if (!that.containsKey(key)) {
                     return false;
                  }

                  char that_value = that.get(key);
                  char this_value = values[i];
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
      this.forEachEntry(new TDoubleCharProcedure() {
         private boolean first = true;

         public boolean execute(double key, char value) {
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
            out.writeChar(this._values[i]);
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
         char val = in.readChar();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TDoubleSet {
      public TDoubleIterator iterator() {
         return TDoubleCharHashMap.this.new TDoubleCharKeyHashIterator(TDoubleCharHashMap.this);
      }

      public double getNoEntryValue() {
         return TDoubleCharHashMap.this.no_entry_key;
      }

      public int size() {
         return TDoubleCharHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TDoubleCharHashMap.this._size;
      }

      public boolean contains(double entry) {
         return TDoubleCharHashMap.this.contains(entry);
      }

      public double[] toArray() {
         return TDoubleCharHashMap.this.keys();
      }

      public double[] toArray(double[] dest) {
         return TDoubleCharHashMap.this.keys(dest);
      }

      public boolean add(double entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double entry) {
         return TDoubleCharHashMap.this.no_entry_value != TDoubleCharHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Double)) {
               return false;
            }

            double ele = (Double)element;
            if (!TDoubleCharHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TDoubleCollection collection) {
         TDoubleIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TDoubleCharHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(double[] array) {
         for(double element : array) {
            if (!TDoubleCharHashMap.this.contains(element)) {
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
         double[] set = TDoubleCharHashMap.this._set;
         byte[] states = TDoubleCharHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TDoubleCharHashMap.this.removeAt(i);
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
         TDoubleCharHashMap.this.clear();
      }

      public boolean forEach(TDoubleProcedure procedure) {
         return TDoubleCharHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TDoubleSet)) {
            return false;
         } else {
            TDoubleSet that = (TDoubleSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TDoubleCharHashMap.this._states.length;

               while(i-- > 0) {
                  if (TDoubleCharHashMap.this._states[i] == 1 && !that.contains(TDoubleCharHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TDoubleCharHashMap.this._states.length;

         while(i-- > 0) {
            if (TDoubleCharHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TDoubleCharHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TDoubleCharHashMap.this.forEachKey(new TDoubleProcedure() {
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

   protected class TValueView implements TCharCollection {
      public TCharIterator iterator() {
         return TDoubleCharHashMap.this.new TDoubleCharValueHashIterator(TDoubleCharHashMap.this);
      }

      public char getNoEntryValue() {
         return TDoubleCharHashMap.this.no_entry_value;
      }

      public int size() {
         return TDoubleCharHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TDoubleCharHashMap.this._size;
      }

      public boolean contains(char entry) {
         return TDoubleCharHashMap.this.containsValue(entry);
      }

      public char[] toArray() {
         return TDoubleCharHashMap.this.values();
      }

      public char[] toArray(char[] dest) {
         return TDoubleCharHashMap.this.values(dest);
      }

      public boolean add(char entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char entry) {
         char[] values = TDoubleCharHashMap.this._values;
         byte[] states = TDoubleCharHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TDoubleCharHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Character)) {
               return false;
            }

            char ele = (Character)element;
            if (!TDoubleCharHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TCharCollection collection) {
         TCharIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TDoubleCharHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(char[] array) {
         for(char element : array) {
            if (!TDoubleCharHashMap.this.containsValue(element)) {
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
         char[] values = TDoubleCharHashMap.this._values;
         byte[] states = TDoubleCharHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TDoubleCharHashMap.this.removeAt(i);
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
         TDoubleCharHashMap.this.clear();
      }

      public boolean forEach(TCharProcedure procedure) {
         return TDoubleCharHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TDoubleCharHashMap.this.forEachValue(new TCharProcedure() {
            private boolean first = true;

            public boolean execute(char value) {
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

   class TDoubleCharKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
      TDoubleCharKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public double next() {
         this.moveToNextIndex();
         return TDoubleCharHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TDoubleCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TDoubleCharValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public char next() {
         this.moveToNextIndex();
         return TDoubleCharHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TDoubleCharHashIterator extends THashPrimitiveIterator implements TDoubleCharIterator {
      TDoubleCharHashIterator(TDoubleCharHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public double key() {
         return TDoubleCharHashMap.this._set[this._index];
      }

      public char value() {
         return TDoubleCharHashMap.this._values[this._index];
      }

      public char setValue(char val) {
         char old = this.value();
         TDoubleCharHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TDoubleCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
