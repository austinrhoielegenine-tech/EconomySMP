package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortCharHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
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

public class TShortCharHashMap extends TShortCharHash implements TShortCharMap, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient char[] _values;

   public TShortCharHashMap() {
   }

   public TShortCharHashMap(int initialCapacity) {
      super(initialCapacity);
   }

   public TShortCharHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public TShortCharHashMap(int initialCapacity, float loadFactor, short noEntryKey, char noEntryValue) {
      super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
   }

   public TShortCharHashMap(short[] keys, char[] values) {
      super(Math.max(keys.length, values.length));
      int size = Math.min(keys.length, values.length);

      for(int i = 0; i < size; ++i) {
         this.put(keys[i], values[i]);
      }

   }

   public TShortCharHashMap(TShortCharMap map) {
      super(map.size());
      if (map instanceof TShortCharHashMap) {
         TShortCharHashMap hashmap = (TShortCharHashMap)map;
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
      this._values = new char[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      short[] oldKeys = this._set;
      char[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new short[newCapacity];
      this._values = new char[newCapacity];
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

   public char put(short key, char value) {
      int index = this.insertKey(key);
      return this.doPut(key, value, index);
   }

   public char putIfAbsent(short key, char value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(key, value, index);
   }

   private char doPut(short key, char value, int index) {
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

   public void putAll(Map<? extends Short, ? extends Character> map) {
      this.ensureCapacity(map.size());

      for(Map.Entry<? extends Short, ? extends Character> entry : map.entrySet()) {
         this.put((Short)entry.getKey(), (Character)entry.getValue());
      }

   }

   public void putAll(TShortCharMap map) {
      this.ensureCapacity(map.size());
      TShortCharIterator iter = map.iterator();

      while(iter.hasNext()) {
         iter.advance();
         this.put(iter.key(), iter.value());
      }

   }

   public char get(short key) {
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

   public char remove(short key) {
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

   public boolean containsKey(short key) {
      return this.contains(key);
   }

   public TShortCharIterator iterator() {
      return new TShortCharHashIterator(this);
   }

   public boolean forEachKey(TShortProcedure procedure) {
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

   public boolean forEachEntry(TShortCharProcedure procedure) {
      byte[] states = this._states;
      short[] keys = this._set;
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

   public boolean retainEntries(TShortCharProcedure procedure) {
      boolean modified = false;
      byte[] states = this._states;
      short[] keys = this._set;
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

   public boolean increment(short key) {
      return this.adjustValue(key, '\u0001');
   }

   public boolean adjustValue(short key, char amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         char[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public char adjustOrPutValue(short key, char adjust_amount, char put_amount) {
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
      if (!(other instanceof TShortCharMap)) {
         return false;
      } else {
         TShortCharMap that = (TShortCharMap)other;
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
                  short key = this._set[i];
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
      this.forEachEntry(new TShortCharProcedure() {
         private boolean first = true;

         public boolean execute(short key, char value) {
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
         short key = in.readShort();
         char val = in.readChar();
         this.put(key, val);
      }

   }

   protected class TKeyView implements TShortSet {
      public TShortIterator iterator() {
         return TShortCharHashMap.this.new TShortCharKeyHashIterator(TShortCharHashMap.this);
      }

      public short getNoEntryValue() {
         return TShortCharHashMap.this.no_entry_key;
      }

      public int size() {
         return TShortCharHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TShortCharHashMap.this._size;
      }

      public boolean contains(short entry) {
         return TShortCharHashMap.this.contains(entry);
      }

      public short[] toArray() {
         return TShortCharHashMap.this.keys();
      }

      public short[] toArray(short[] dest) {
         return TShortCharHashMap.this.keys(dest);
      }

      public boolean add(short entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short entry) {
         return TShortCharHashMap.this.no_entry_value != TShortCharHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!(element instanceof Short)) {
               return false;
            }

            short ele = (Short)element;
            if (!TShortCharHashMap.this.containsKey(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TShortCollection collection) {
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TShortCharHashMap.this.containsKey(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(short[] array) {
         for(short element : array) {
            if (!TShortCharHashMap.this.contains(element)) {
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
         short[] set = TShortCharHashMap.this._set;
         byte[] states = TShortCharHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TShortCharHashMap.this.removeAt(i);
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
         TShortCharHashMap.this.clear();
      }

      public boolean forEach(TShortProcedure procedure) {
         return TShortCharHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TShortSet)) {
            return false;
         } else {
            TShortSet that = (TShortSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TShortCharHashMap.this._states.length;

               while(i-- > 0) {
                  if (TShortCharHashMap.this._states[i] == 1 && !that.contains(TShortCharHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TShortCharHashMap.this._states.length;

         while(i-- > 0) {
            if (TShortCharHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TShortCharHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TShortCharHashMap.this.forEachKey(new TShortProcedure() {
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

   protected class TValueView implements TCharCollection {
      public TCharIterator iterator() {
         return TShortCharHashMap.this.new TShortCharValueHashIterator(TShortCharHashMap.this);
      }

      public char getNoEntryValue() {
         return TShortCharHashMap.this.no_entry_value;
      }

      public int size() {
         return TShortCharHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TShortCharHashMap.this._size;
      }

      public boolean contains(char entry) {
         return TShortCharHashMap.this.containsValue(entry);
      }

      public char[] toArray() {
         return TShortCharHashMap.this.values();
      }

      public char[] toArray(char[] dest) {
         return TShortCharHashMap.this.values(dest);
      }

      public boolean add(char entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char entry) {
         char[] values = TShortCharHashMap.this._values;
         byte[] states = TShortCharHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
               TShortCharHashMap.this.removeAt(i);
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
            if (!TShortCharHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TCharCollection collection) {
         TCharIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TShortCharHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(char[] array) {
         for(char element : array) {
            if (!TShortCharHashMap.this.containsValue(element)) {
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
         char[] values = TShortCharHashMap.this._values;
         byte[] states = TShortCharHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
               TShortCharHashMap.this.removeAt(i);
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
         TShortCharHashMap.this.clear();
      }

      public boolean forEach(TCharProcedure procedure) {
         return TShortCharHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TShortCharHashMap.this.forEachValue(new TCharProcedure() {
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

   class TShortCharKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {
      TShortCharKeyHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public short next() {
         this.moveToNextIndex();
         return TShortCharHashMap.this._set[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TShortCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
      TShortCharValueHashIterator(TPrimitiveHash hash) {
         super(hash);
      }

      public char next() {
         this.moveToNextIndex();
         return TShortCharHashMap.this._values[this._index];
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }

   class TShortCharHashIterator extends THashPrimitiveIterator implements TShortCharIterator {
      TShortCharHashIterator(TShortCharHashMap map) {
         super(map);
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public short key() {
         return TShortCharHashMap.this._set[this._index];
      }

      public char value() {
         return TShortCharHashMap.this._values[this._index];
      }

      public char setValue(char val) {
         char old = this.value();
         TShortCharHashMap.this._values[this._index] = val;
         return old;
      }

      public void remove() {
         if (this._expectedSize != this._hash.size()) {
            throw new ConcurrentModificationException();
         } else {
            try {
               this._hash.tempDisableAutoCompaction();
               TShortCharHashMap.this.removeAt(this._index);
            } finally {
               this._hash.reenableAutoCompaction(false);
            }

            --this._expectedSize;
         }
      }
   }
}
