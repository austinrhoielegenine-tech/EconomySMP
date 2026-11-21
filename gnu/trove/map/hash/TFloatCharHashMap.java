package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatCharHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
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

public class TFloatCharHashMap extends TFloatCharHash implements TFloatCharMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient char[] _values;

   public TFloatCharHashMap() {
   }

   public TFloatCharHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TFloatCharHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TFloatCharHashMap(int initialCapacity, float loadFactor, float noEntryKey, char noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TFloatCharHashMap(float[] keys, char[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TFloatCharHashMap(TFloatCharMap map) {
      super(map.size());
      if (map instanceof TFloatCharHashMap) {
         TFloatCharHashMap hashmap = (TFloatCharHashMap)map;
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
      this._values = new char[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      float[] oldKeys = this._set;
      char[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new float[newCapacity];
      this._values = new char[newCapacity];
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

   public char put(float key, char value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public char putIfAbsent(float key, char value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private char doPut(float key, char value, int index) {
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

   public void putAll(Map<? extends Float, ? extends Character> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Float, ? extends Character> entry : map.entrySet()) {
         this.put((Float)entry.getKey(), (Character)entry.getValue());
      }

   }

   public void putAll(TFloatCharMap map) {
      this.ensureCapacity(map.size());
      TFloatCharIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public char get(float key) {
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

   public char remove(float key) {
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

   public boolean containsKey(float key) {
      return this.contains(key);
   }

   public TFloatCharIterator iterator() {
      return new TFloatCharHashIterator(this);
   }

   public boolean forEachKey(TFloatProcedure procedure) {
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

   public boolean forEachEntry(TFloatCharProcedure procedure) {
      byte[] states = this._states;
      float[] keys = this._set;
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

   public boolean retainEntries(TFloatCharProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      float[] keys = this._set;
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

   public boolean increment(float key) {
      return this.adjustValue(key, '\u0001');
   }

   public boolean adjustValue(float key, char amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         char[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public char adjustOrPutValue(float key, char adjust_amount, char put_amount) {
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

      byte var8 = this._states[index];
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean equals(Object other) {
      if (!(other instanceof TFloatCharMap)) {
         return false;
      } else {
         TFloatCharMap that = (TFloatCharMap)other;
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
                  float key = this._set[i];
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
      this.forEachEntry(new TFloatCharProcedure() {
         private boolean first = true;

         public boolean execute(float key, char value) {
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
         float key = in.readFloat();
         char val = in.readChar();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TFloatSet {
      public TFloatIterator iterator() {
         return TFloatCharHashMap.this.new TFloatCharKeyHashIterator(TFloatCharHashMap.this);
      }

      public float getNoEntryValue() {
         return TFloatCharHashMap.this.no_entry_key;
      }

      public int size() {
         return TFloatCharHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TFloatCharHashMap.this._size;
      }

      public boolean contains(float entry) {
         return TFloatCharHashMap.this.contains(entry);
      }

      public float[] toArray() {
         return TFloatCharHashMap.this.keys();
      }

      public float[] toArray(float[] dest) {
         return TFloatCharHashMap.this.keys(dest);
      }

      public boolean add(float entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float entry) {
         return TFloatCharHashMap.this.no_entry_value != TFloatCharHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Float)) {
               return false;
            }

            float ele = (Float)element;
            if (!TFloatCharHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TFloatCollection collection) {
         TFloatIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TFloatCharHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(float[] array) {
         for(float element : array) {
            if (!TFloatCharHashMap.this.contains(element)) {
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
         float[] set = TFloatCharHashMap.this._set;
         byte[] states = TFloatCharHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TFloatCharHashMap.this.removeAt(i);
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
         TFloatCharHashMap.this.clear();
      }

      public boolean forEach(TFloatProcedure procedure) {
         return TFloatCharHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TFloatSet)) {
            return false;
         } else {
            TFloatSet that = (TFloatSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TFloatCharHashMap.this._states.length;

               while(i-- > 0) {
                  if (TFloatCharHashMap.this._states[i] == 1 && !that.contains(TFloatCharHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TFloatCharHashMap.this._states.length;

         while(i-- > 0) {
            if (TFloatCharHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TFloatCharHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TFloatCharHashMap.this.forEachKey(new TFloatProcedure() {
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

   protected class TValueView implements TCharCollection {
      public TCharIterator iterator() {
         return TFloatCharHashMap.this.new TFloatCharValueHashIterator(TFloatCharHashMap.this);
      }

      public char getNoEntryValue() {
         return TFloatCharHashMap.this.no_entry_value;
      }

      public int size() {
         return TFloatCharHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TFloatCharHashMap.this._size;
      }

      public boolean contains(char entry) {
         return TFloatCharHashMap.this.containsValue(entry);
      }

      public char[] toArray() {
         return TFloatCharHashMap.this.values();
      }

      public char[] toArray(char[] dest) {
         return TFloatCharHashMap.this.values(dest);
      }

      public boolean add(char entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char entry) {
         char[] values = TFloatCharHashMap.this._values;
         byte[] states = TFloatCharHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TFloatCharHashMap.this.removeAt(i);
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
            if (!TFloatCharHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TCharCollection collection) {
         TCharIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TFloatCharHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(char[] array) {
         for(char element : array) {
            if (!TFloatCharHashMap.this.containsValue(element)) {
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
         char[] values = TFloatCharHashMap.this._values;
         byte[] states = TFloatCharHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TFloatCharHashMap.this.removeAt(i);
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
         TFloatCharHashMap.this.clear();
      }

      public boolean forEach(TCharProcedure procedure) {
         return TFloatCharHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TFloatCharHashMap.this.forEachValue(new TCharProcedure() {
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

   class TFloatCharKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {
      TFloatCharKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public float next() {
         this.moveToNextIndex();
         return TFloatCharHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TFloatCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TFloatCharValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public char next() {
         this.moveToNextIndex();
         return TFloatCharHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TFloatCharHashIterator extends THashPrimitiveIterator implements TFloatCharIterator {
      TFloatCharHashIterator(TFloatCharHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public float key() {
         return TFloatCharHashMap.this._set[this._index];
      }

      public char value() {
         return TFloatCharHashMap.this._values[this._index];
      }

      public char setValue(char val) {
         char old = this.value();
         TFloatCharHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TFloatCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
