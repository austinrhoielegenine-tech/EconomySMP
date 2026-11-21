package gnu.trove.map.custom_hash;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class TObjectCharCustomHashMap<K> extends TCustomObjectHash<K> implements TObjectCharMap<K>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TObjectCharProcedure<K> PUT_ALL_PROC;
   protected transient char[] _values;
   protected char no_entry_value;

   public TObjectCharCustomHashMap() {
      this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
         public boolean execute(K key, char value) {
            TObjectCharCustomHashMap.this.put(key, value);
            return true;
         }
      };
   }

   public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy) {
      super(strategy);
      this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
         public boolean execute(K key, char value) {
            TObjectCharCustomHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
   }

   public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
      super(strategy, initialCapacity);
      this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
         public boolean execute(K key, char value) {
            TObjectCharCustomHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
   }

   public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
      super(strategy, initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
         public boolean execute(K key, char value) {
            TObjectCharCustomHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_value = Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
   }

   public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor, char noEntryValue) {
      super(strategy, initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TObjectCharProcedure<K>() {
         public boolean execute(K key, char value) {
            TObjectCharCustomHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_value = noEntryValue;
      if (this.no_entry_value != 0) {
         Arrays.fill(this._values, this.no_entry_value);
      }

   }

   public TObjectCharCustomHashMap(HashingStrategy<? super K> strategy, TObjectCharMap<? extends K> map) {
      this(strategy, map.size(), 0.5F, map.getNoEntryValue());
      if (map instanceof TObjectCharCustomHashMap) {
         TObjectCharCustomHashMap hashmap = (TObjectCharCustomHashMap)map;
         this._loadFactor = Math.abs(hashmap._loadFactor);
         this.no_entry_value = hashmap.no_entry_value;
         this.strategy = hashmap.strategy;
         if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp(saturatedCast(fastCeil((double)10.0F / (double)this._loadFactor)));
      }

      this.putAll(map);
   }

   public int setUp(int initialCapacity) {
      int capacity = super.setUp(initialCapacity);
      this._values = new char[capacity];
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      K[] oldKeys = (K[])(this._set);
      char[] oldVals = this._values;
      this._set = new Object[newCapacity];
      Arrays.fill(this._set, FREE);
      this._values = new char[newCapacity];
      Arrays.fill(this._values, this.no_entry_value);
      int i = oldCapacity;

      while(i-- > 0) {
         K o = (K)oldKeys[i];
         if (o != FREE && o != REMOVED) {
            int index = this.insertKey(o);
            if (index < 0) {
               this.throwObjectContractViolation(this._set[-index - 1], o);
            }

            this._values[index] = oldVals[i];
         }
      }

   }

   public char getNoEntryValue() {
      return this.no_entry_value;
   }

   public boolean containsKey(Object key) {
      return this.contains(key);
   }

   public boolean containsValue(char val) {
      Object[] keys = this._set;
      char[] vals = this._values;
      int i = vals.length;

      while(i-- > 0) {
         if (keys[i] != FREE && keys[i] != REMOVED && val == vals[i]) {
            return true;
         }
      }

      return false;
   }

   public char get(Object key) {
      int index = this.index(key);
      return index < 0 ? this.no_entry_value : this._values[index];
   }

   public char put(K key, char value) {
      int index = this.insertKey(key);
      return this.doPut(value, index);
   }

   public char putIfAbsent(K key, char value) {
      int index = this.insertKey(key);
      return index < 0 ? this._values[-index - 1] : this.doPut(value, index);
   }

   private char doPut(char value, int index) {
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

   public char remove(Object key) {
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

   public void putAll(Map<? extends K, ? extends Character> map) {
      for(Map.Entry<? extends K, ? extends Character> entry : map.entrySet()) {
         this.put(entry.getKey(), (Character)entry.getValue());
      }

   }

   public void putAll(TObjectCharMap<? extends K> map) {
      map.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, FREE);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
   }

   public Set<K> keySet() {
      return new KeyView();
   }

   public Object[] keys() {
      K[] keys = (K[])(new Object[this.size()]);
      Object[] k = this._set;
      int i = k.length;
      int j = 0;

      while(i-- > 0) {
         if (k[i] != FREE && k[i] != REMOVED) {
            keys[j++] = k[i];
         }
      }

      return keys;
   }

   public K[] keys(K[] a) {
      int size = this.size();
      if (a.length < size) {
         a = (K[])((Object[])Array.newInstance(a.getClass().getComponentType(), size));
      }

      Object[] k = this._set;
      int i = k.length;
      int j = 0;

      while(i-- > 0) {
         if (k[i] != FREE && k[i] != REMOVED) {
            a[j++] = k[i];
         }
      }

      return a;
   }

   public TCharCollection valueCollection() {
      return new TCharValueCollection();
   }

   public char[] values() {
      char[] vals = new char[this.size()];
      char[] v = this._values;
      Object[] keys = this._set;
      int i = v.length;
      int j = 0;

      while(i-- > 0) {
         if (keys[i] != FREE && keys[i] != REMOVED) {
            vals[j++] = v[i];
         }
      }

      return vals;
   }

   public char[] values(char[] array) {
      int size = this.size();
      if (array.length < size) {
         array = new char[size];
      }

      char[] v = this._values;
      Object[] keys = this._set;
      int i = v.length;
      int j = 0;

      while(i-- > 0) {
         if (keys[i] != FREE && keys[i] != REMOVED) {
            array[j++] = v[i];
         }
      }

      if (array.length > size) {
         array[size] = this.no_entry_value;
      }

      return array;
   }

   public TObjectCharIterator<K> iterator() {
      return new TObjectCharHashIterator<K>(this);
   }

   public boolean increment(K key) {
      return this.adjustValue(key, '\u0001');
   }

   public boolean adjustValue(K key, char amount) {
      int index = this.index(key);
      if (index < 0) {
         return false;
      } else {
         char[] var10000 = this._values;
         var10000[index] += amount;
         return true;
      }
   }

   public char adjustOrPutValue(K key, char adjust_amount, char put_amount) {
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

      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return newValue;
   }

   public boolean forEachKey(TObjectProcedure<? super K> procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TCharProcedure procedure) {
      Object[] keys = this._set;
      char[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (keys[i] != FREE && keys[i] != REMOVED && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TObjectCharProcedure<? super K> procedure) {
      Object[] keys = this._set;
      char[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (keys[i] != FREE && keys[i] != REMOVED && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean retainEntries(TObjectCharProcedure<? super K> procedure) {
      boolean modified = false;
      K[] keys = (K[])(this._set);
      char[] values = this._values;
      this.tempDisableAutoCompaction();

      try {
         int i = keys.length;

         while(i-- > 0) {
            if (keys[i] != FREE && keys[i] != REMOVED && !procedure.execute(keys[i], values[i])) {
               this.removeAt(i);
               modified = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return modified;
   }

   public void transformValues(TCharFunction function) {
      Object[] keys = this._set;
      char[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (keys[i] != null && keys[i] != REMOVED) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean equals(Object other) {
      if (!(other instanceof TObjectCharMap)) {
         return false;
      } else {
         TObjectCharMap that = (TObjectCharMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            try {
               TObjectCharIterator iter = this.iterator();

               while(iter.hasNext()) {
                  iter.advance();
                  Object key = iter.key();
                  char value = iter.value();
                  if (value == this.no_entry_value) {
                     if (that.get(key) != that.getNoEntryValue() || !that.containsKey(key)) {
                        return false;
                     }
                  } else if (value != that.get(key)) {
                     return false;
                  }
               }
            } catch (ClassCastException var6) {
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int hashcode = 0;
      Object[] keys = this._set;
      char[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (keys[i] != FREE && keys[i] != REMOVED) {
            hashcode += HashFunctions.hash(values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode());
         }
      }

      return hashcode;
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      super.writeExternal(out);
      out.writeObject(this.strategy);
      out.writeChar(this.no_entry_value);
      out.writeInt(this._size);
      int i = this._set.length;

      while(i-- > 0) {
         if (this._set[i] != REMOVED && this._set[i] != FREE) {
            out.writeObject(this._set[i]);
            out.writeChar(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      this.strategy = (HashingStrategy)in.readObject();
      this.no_entry_value = in.readChar();
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         K key = (K)in.readObject();
         char val = in.readChar();
         this.put(key, val);
      }

   }

   public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      this.forEachEntry(new TObjectCharProcedure<K>() {
         private boolean first = true;

         public boolean execute(K key, char value) {
            if (this.first) {
               this.first = false;
            } else {
               buf.append(",");
            }

            buf.append(key).append("=").append(value);
            return true;
         }
      });
      buf.append("}");
      return buf.toString();
   }

   protected class KeyView extends TObjectCharCustomHashMap<K>.MapBackedView<K> {
      public Iterator<K> iterator() {
         return new TObjectHashIterator<K>(TObjectCharCustomHashMap.this);
      }

      public boolean removeElement(K key) {
         return TObjectCharCustomHashMap.this.no_entry_value != TObjectCharCustomHashMap.this.remove(key);
      }

      public boolean containsElement(K key) {
         return TObjectCharCustomHashMap.this.contains(key);
      }
   }

   private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {
      private MapBackedView() {
      }

      public abstract boolean removeElement(E var1);

      public abstract boolean containsElement(E var1);

      public boolean contains(Object key) {
         return this.containsElement(key);
      }

      public boolean remove(Object o) {
         return this.removeElement(o);
      }

      public void clear() {
         TObjectCharCustomHashMap.this.clear();
      }

      public boolean add(E obj) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return TObjectCharCustomHashMap.this.size();
      }

      public Object[] toArray() {
         Object[] result = new Object[this.size()];
         Iterator<E> e = this.iterator();

         for(int i = 0; e.hasNext(); ++i) {
            result[i] = e.next();
         }

         return result;
      }

      public <T> T[] toArray(T[] a) {
         int size = this.size();
         if (a.length < size) {
            a = (T[])((Object[])Array.newInstance(a.getClass().getComponentType(), size));
         }

         Iterator<E> it = this.iterator();
         Object[] result = a;

         for(int i = 0; i < size; ++i) {
            result[i] = it.next();
         }

         if (a.length > size) {
            a[size] = null;
         }

         return a;
      }

      public boolean isEmpty() {
         return TObjectCharCustomHashMap.this.isEmpty();
      }

      public boolean addAll(Collection<? extends E> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean changed = false;
         Iterator<E> i = this.iterator();

         while(i.hasNext()) {
            if (!collection.contains(i.next())) {
               i.remove();
               changed = true;
            }
         }

         return changed;
      }
   }

   class TCharValueCollection implements TCharCollection {
      public TCharIterator iterator() {
         return new TObjectCharValueHashIterator();
      }

      public char getNoEntryValue() {
         return TObjectCharCustomHashMap.this.no_entry_value;
      }

      public int size() {
         return TObjectCharCustomHashMap.this._size;
      }

      public boolean isEmpty() {
         return 0 == TObjectCharCustomHashMap.this._size;
      }

      public boolean contains(char entry) {
         return TObjectCharCustomHashMap.this.containsValue(entry);
      }

      public char[] toArray() {
         return TObjectCharCustomHashMap.this.values();
      }

      public char[] toArray(char[] dest) {
         return TObjectCharCustomHashMap.this.values(dest);
      }

      public boolean add(char entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(char entry) {
         char[] values = TObjectCharCustomHashMap.this._values;
         Object[] set = TObjectCharCustomHashMap.this._set;
         int i = values.length;

         while(i-- > 0) {
            if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && entry == values[i]) {
               TObjectCharCustomHashMap.this.removeAt(i);
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
            if (!TObjectCharCustomHashMap.this.containsValue(ele)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TCharCollection collection) {
         TCharIterator iter = collection.iterator();

         while(iter.hasNext()) {
            if (!TObjectCharCustomHashMap.this.containsValue(iter.next())) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(char[] array) {
         for(char element : array) {
            if (!TObjectCharCustomHashMap.this.containsValue(element)) {
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
         char[] values = TObjectCharCustomHashMap.this._values;
         Object[] set = TObjectCharCustomHashMap.this._set;
         int i = set.length;

         while(i-- > 0) {
            if (set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && Arrays.binarySearch(array, values[i]) < 0) {
               TObjectCharCustomHashMap.this.removeAt(i);
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
         TObjectCharCustomHashMap.this.clear();
      }

      public boolean forEach(TCharProcedure procedure) {
         return TObjectCharCustomHashMap.this.forEachValue(procedure);
      }

      public String toString() {
         final StringBuilder buf = new StringBuilder("{");
         TObjectCharCustomHashMap.this.forEachValue(new TCharProcedure() {
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

      class TObjectCharValueHashIterator implements TCharIterator {
         protected THash _hash = TObjectCharCustomHashMap.this;
         protected int _expectedSize;
         protected int _index;

         TObjectCharValueHashIterator() {
            this._expectedSize = this._hash.size();
            this._index = this._hash.capacity();
         }

         public boolean hasNext() {
            return this.nextIndex() >= 0;
         }

         public char next() {
            this.moveToNextIndex();
            return TObjectCharCustomHashMap.this._values[this._index];
         }

         public void remove() {
            if (this._expectedSize != this._hash.size()) {
               throw new ConcurrentModificationException();
            } else {
               try {
                  this._hash.tempDisableAutoCompaction();
                  TObjectCharCustomHashMap.this.removeAt(this._index);
               } finally {
                  this._hash.reenableAutoCompaction(false);
               }

               --this._expectedSize;
            }
         }

         protected final void moveToNextIndex() {
            if ((this._index = this.nextIndex()) < 0) {
               throw new NoSuchElementException();
            }
         }

         protected final int nextIndex() {
            if (this._expectedSize != this._hash.size()) {
               throw new ConcurrentModificationException();
            } else {
               Object[] set = TObjectCharCustomHashMap.this._set;
               int i = this._index;

               while(i-- > 0 && (set[i] == TCustomObjectHash.FREE || set[i] == TCustomObjectHash.REMOVED)) {
               }

               return i;
            }
         }
      }
   }

   class TObjectCharHashIterator<K> extends TObjectHashIterator<K> implements TObjectCharIterator<K> {
      private final TObjectCharCustomHashMap<K> _map;

      public TObjectCharHashIterator(TObjectCharCustomHashMap<K> map) {
         super(map);
         this._map = map;
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public K key() {
         return (K)this._map._set[this._index];
      }

      public char value() {
         return this._map._values[this._index];
      }

      public char setValue(char val) {
         char old = this.value();
         this._map._values[this._index] = val;
         return old;
      }
   }
}
