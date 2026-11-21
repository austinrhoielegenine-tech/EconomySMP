package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TCharShortMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TCharShortHashMap extends TCharShortHash implements TCharShortMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient short[] _values;

   public TCharShortHashMap() {
   }

   public TCharShortHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TCharShortHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TCharShortHashMap(int initialCapacity, float loadFactor, char noEntryKey, short noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TCharShortHashMap(char[] keys, short[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TCharShortHashMap(TCharShortMap map) {
      super(map.size());
      if (map instanceof TCharShortHashMap) {
         TCharShortHashMap hashmap = (TCharShortHashMap)map;
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
      this._values = new short[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      char[] oldKeys = this._set;
      short[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new char[newCapacity];
      this._values = new short[newCapacity];
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

   public short put(char key, short value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public short putIfAbsent(char key, short value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private short doPut(char key, short value, int index) {
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

   public void putAll(Map<? extends Character, ? extends Short> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Character, ? extends Short> entry : map.entrySet()) {
         this.put((Character)entry.getKey(), (Short)entry.getValue());
      }

   }

   public void putAll(TCharShortMap map) {
      this.ensureCapacity(map.size());
      TCharShortIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public short get(char key) {
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

   public short remove(char key) {
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

   public boolean containsKey(char key) {
      return this.contains(key);
   }

   public TCharShortIterator iterator() {
      return new TCharShortHashIterator(this);
   }

   public boolean forEachKey(TCharProcedure procedure) {
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

   public boolean forEachEntry(TCharShortProcedure procedure) {
      byte[] states = this._states;
      char[] keys = this._set;
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

   public boolean retainEntries(TCharShortProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      char[] keys = this._set;
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

   public boolean increment(char key) {
      return this.adjustValue(key, (short)1);
   }

   public boolean adjustValue(char key, short amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         short[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public short adjustOrPutValue(char key, short adjust_amount, short put_amount) {
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

      byte var8 = this._states[index];
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean equals(Object other) {
      if (!(other instanceof TCharShortMap)) {
         return false;
      } else {
         TCharShortMap that = (TCharShortMap)other;
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
                  char key = this._set[i];
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
      this.forEachEntry(new TCharShortProcedure() {
         private boolean first = true;

         public boolean execute(char key, short value) {
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
         char key = in.readChar();
         short val = in.readShort();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TCharSet {
      public TCharIterator iterator() {
         return TCharShortHashMap.this.new TCharShortKeyHashIterator(TCharShortHashMap.this);
      }

      public char getNoEntryValue() {
         return TCharShortHashMap.this.no_entry_key;
      }

      public int size() {
         return TCharShortHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TCharShortHashMap.this._size;
      }

      public boolean contains(char entry) {
         return TCharShortHashMap.this.contains(entry);
      }

      public char[] toArray() {
         return TCharShortHashMap.this.keys();
      }

      public char[] toArray(char[] dest) {
         return TCharShortHashMap.this.keys(dest);
      }

      public boolean add(char entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char entry) {
         return TCharShortHashMap.this.no_entry_value != TCharShortHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Character)) {
               return false;
            }

            char ele = (Character)element;
            if (!TCharShortHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TCharCollection collection) {
         TCharIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TCharShortHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(char[] array) {
         for(char element : array) {
            if (!TCharShortHashMap.this.contains(element)) {
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
         char[] set = TCharShortHashMap.this._set;
         byte[] states = TCharShortHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TCharShortHashMap.this.removeAt(i);
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
         TCharShortHashMap.this.clear();
      }

      public boolean forEach(TCharProcedure procedure) {
         return TCharShortHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TCharSet)) {
            return false;
         } else {
            TCharSet that = (TCharSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TCharShortHashMap.this._states.length;

               while(i-- > 0) {
                  if (TCharShortHashMap.this._states[i] == 1 && !that.contains(TCharShortHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TCharShortHashMap.this._states.length;

         while(i-- > 0) {
            if (TCharShortHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TCharShortHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TCharShortHashMap.this.forEachKey(new TCharProcedure() {
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

   protected class TValueView implements TShortCollection {
      public TShortIterator iterator() {
         return TCharShortHashMap.this.new TCharShortValueHashIterator(TCharShortHashMap.this);
      }

      public short getNoEntryValue() {
         return TCharShortHashMap.this.no_entry_value;
      }

      public int size() {
         return TCharShortHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TCharShortHashMap.this._size;
      }

      public boolean contains(short entry) {
         return TCharShortHashMap.this.containsValue(entry);
      }

      public short[] toArray() {
         return TCharShortHashMap.this.values();
      }

      public short[] toArray(short[] dest) {
         return TCharShortHashMap.this.values(dest);
      }

      public boolean add(short entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short entry) {
         short[] values = TCharShortHashMap.this._values;
         byte[] states = TCharShortHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TCharShortHashMap.this.removeAt(i);
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
            if (!TCharShortHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TShortCollection collection) {
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TCharShortHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(short[] array) {
         for(short element : array) {
            if (!TCharShortHashMap.this.containsValue(element)) {
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
         short[] values = TCharShortHashMap.this._values;
         byte[] states = TCharShortHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TCharShortHashMap.this.removeAt(i);
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
         TCharShortHashMap.this.clear();
      }

      public boolean forEach(TShortProcedure procedure) {
         return TCharShortHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TCharShortHashMap.this.forEachValue(new TShortProcedure() {
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

   class TCharShortKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TCharShortKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public char next() {
         this.moveToNextIndex();
         return TCharShortHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TCharShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TCharShortValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public short next() {
         this.moveToNextIndex();
         return TCharShortHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TCharShortHashIterator extends THashPrimitiveIterator implements TCharShortIterator {
      TCharShortHashIterator(TCharShortHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public char key() {
         return TCharShortHashMap.this._set[this._index];
      }

      public short value() {
         return TCharShortHashMap.this._values[this._index];
      }

      public short setValue(short val) {
         short old = this.value();
         TCharShortHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TCharShortHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
