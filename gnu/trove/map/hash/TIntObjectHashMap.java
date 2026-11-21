package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TIntObjectHashMap<V> extends TIntHash implements TIntObjectMap<V>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TIntObjectProcedure<V> PUT_ALL_PROC;
   protected transient V[] _values;
   protected int no_entry_key;

   public TIntObjectHashMap() {
      this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
         public boolean execute(int key, V value) {
            TIntObjectHashMap.this.put(key, value);
            return true;
         }
      };
   }

   public TIntObjectHashMap(int initialCapacity) {
      super(initialCapacity);
      this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
         public boolean execute(int key, V value) {
            TIntObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
   }

   public TIntObjectHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
         public boolean execute(int key, V value) {
            TIntObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_INT_NO_ENTRY_VALUE;
   }

   public TIntObjectHashMap(int initialCapacity, float loadFactor, int noEntryKey) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TIntObjectProcedure<V>() {
         public boolean execute(int key, V value) {
            TIntObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = noEntryKey;
   }

   public TIntObjectHashMap(TIntObjectMap<? extends V> map) {
      this(map.size(), 0.5F, map.getNoEntryKey());
      this.putAll(map);
   }

   protected int setUp(int initialCapacity) {
      int capacity = super.setUp(initialCapacity);
      this._values = (V[])(new Object[capacity]);
      return capacity;
   }

   protected void rehash(int newCapacity) {
      int oldCapacity = this._set.length;
      int[] oldKeys = this._set;
      V[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new int[newCapacity];
      this._values = (V[])(new Object[newCapacity]);
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            int o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public int getNoEntryKey() {
      return this.no_entry_key;
   }

   public boolean containsKey(int key) {
      return this.contains(key);
   }

   public boolean containsValue(Object val) {
      byte[] states = this._states;
      V[] vals = this._values;
      if (null == val) {
         int i = vals.length;

         while(i-- > 0) {
            if (states[i] == 1 && null == vals[i]) {
               return true;
            }
         }
      } else {
         int i = vals.length;

         while(i-- > 0) {
            if (states[i] == 1 && (val == vals[i] || val.equals(vals[i]))) {
               return true;
            }
         }
      }

      return false;
   }

   public V get(int key) {
      int index = this.index(key);
      return (V)(index < 0 ? null : this._values[index]);
   }

   public V put(int key, V value) {
      int index = this.insertKey(key);
      return (V)this.doPut(value, index);
   }

   public V putIfAbsent(int key, V value) {
      int index = this.insertKey(key);
      return (V)(index < 0 ? this._values[-index - 1] : this.doPut(value, index));
   }

   private V doPut(V value, int index) {
      V previous = null;
      boolean isNewMapping = true;
      if (index < 0) {
         index = -index - 1;
         previous = (V)this._values[index];
         isNewMapping = false;
      }

      this._values[index] = value;
      if (isNewMapping) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return previous;
   }

   public V remove(int key) {
      V prev = null;
      int index = this.index(key);
      if (index >= 0) {
         prev = (V)this._values[index];
         this.removeAt(index);
      }

      return prev;
   }

   protected void removeAt(int index) {
      this._values[index] = null;
      super.removeAt(index);
   }

   public void putAll(Map<? extends Integer, ? extends V> map) {
      for(Map.Entry<? extends Integer, ? extends V> entry : map.entrySet()) {
         this.put((Integer)entry.getKey(), entry.getValue());
      }

   }

   public void putAll(TIntObjectMap<? extends V> map) {
      map.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
      Arrays.fill(this._values, 0, this._values.length, (Object)null);
   }

   public TIntSet keySet() {
      return new KeyView();
   }

   public int[] keys() {
      int[] keys = new int[this.size()];
      int[] k = this._set;
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

   public int[] keys(int[] dest) {
      if (dest.length < this._size) {
         dest = new int[this._size];
      }

      int[] k = this._set;
      byte[] states = this._states;
      int i = k.length;
      int j = 0;

      while(i-- > 0) {
         if (states[i] == 1) {
            dest[j++] = k[i];
         }
      }

      return dest;
   }

   public Collection<V> valueCollection() {
      return new ValueView();
   }

   public Object[] values() {
      Object[] vals = new Object[this.size()];
      V[] v = this._values;
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

   public V[] values(V[] dest) {
      if (dest.length < this._size) {
         dest = (V[])((Object[])Array.newInstance(dest.getClass().getComponentType(), this._size));
      }

      V[] v = this._values;
      byte[] states = this._states;
      int i = v.length;
      int j = 0;

      while(i-- > 0) {
         if (states[i] == 1) {
            dest[j++] = v[i];
         }
      }

      return dest;
   }

   public TIntObjectIterator<V> iterator() {
      return new TIntObjectHashIterator<V>(this);
   }

   public boolean forEachKey(TIntProcedure procedure) {
      return this.forEach(procedure);
   }

   public boolean forEachValue(TObjectProcedure<? super V> procedure) {
      byte[] states = this._states;
      V[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachEntry(TIntObjectProcedure<? super V> procedure) {
      byte[] states = this._states;
      int[] keys = this._set;
      V[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean retainEntries(TIntObjectProcedure<? super V> procedure) {
      boolean modified = false;
      byte[] states = this._states;
      int[] keys = this._set;
      V[] values = this._values;
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

   public void transformValues(TObjectFunction<V, V> function) {
      byte[] states = this._states;
      V[] values = this._values;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            values[i] = function.execute(values[i]);
         }
      }

   }

   public boolean equals(Object other) {
      if (!(other instanceof TIntObjectMap)) {
         return false;
      } else {
         TIntObjectMap that = (TIntObjectMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            try {
               TIntObjectIterator iter = this.iterator();

               while(iter.hasNext()) {
                  iter.advance();
                  int key = iter.key();
                  Object value = iter.value();
                  if (value == null) {
                     if (that.get(key) != null || !that.containsKey(key)) {
                        return false;
                     }
                  } else if (!value.equals(that.get(key))) {
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
      V[] values = this._values;
      byte[] states = this._states;
      int i = values.length;

      while(i-- > 0) {
         if (states[i] == 1) {
            hashcode += HashFunctions.hash(this._set[i]) ^ (values[i] == null ? 0 : values[i].hashCode());
         }
      }

      return hashcode;
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      super.writeExternal(out);
      out.writeInt(this.no_entry_key);
      out.writeInt(this._size);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeInt(this._set[i]);
            out.writeObject(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      this.no_entry_key = in.readInt();
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         int key = in.readInt();
         V val = (V)in.readObject();
         this.put(key, val);
      }

   }

   public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      this.forEachEntry(new TIntObjectProcedure<V>() {
         private boolean first = true;

         public boolean execute(int key, Object value) {
            if (this.first) {
               this.first = false;
            } else {
               buf.append(",");
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

   class KeyView implements TIntSet {
      public int getNoEntryValue() {
         return TIntObjectHashMap.this.no_entry_key;
      }

      public int size() {
         return TIntObjectHashMap.this._size;
      }

      public boolean isEmpty() {
         return TIntObjectHashMap.this._size == 0;
      }

      public boolean contains(int entry) {
         return TIntObjectHashMap.this.containsKey(entry);
      }

      public TIntIterator iterator() {
         return new TIntHashIterator(TIntObjectHashMap.this);
      }

      public int[] toArray() {
         return TIntObjectHashMap.this.keys();
      }

      public int[] toArray(int[] dest) {
         return TIntObjectHashMap.this.keys(dest);
      }

      public boolean add(int entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(int entry) {
         return null != TIntObjectHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!TIntObjectHashMap.this.containsKey((Integer)element)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TIntCollection collection) {
         if (collection == this) {
            return true;
         } else {
            TIntIterator iter = collection.iterator();

            while(iter.hasNext()) {
               if (!TIntObjectHashMap.this.containsKey(iter.next())) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean containsAll(int[] array) {
         for(int element : array) {
            if (!TIntObjectHashMap.this.containsKey(element)) {
               return false;
            }
         }

         return true;
      }

      public boolean addAll(Collection<? extends Integer> collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(TIntCollection collection) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int[] array) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> collection) {
         boolean modified = false;
         TIntIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }

      public boolean retainAll(TIntCollection collection) {
         if (this == collection) {
            return false;
         } else {
            boolean modified = false;
            TIntIterator iter = this.iterator();

            while(iter.hasNext()) {
               if (!collection.contains(iter.next())) {
                  iter.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }

      public boolean retainAll(int[] array) {
         boolean changed = false;
         Arrays.sort(array);
         int[] set = TIntObjectHashMap.this._set;
         byte[] states = TIntObjectHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TIntObjectHashMap.this.removeAt(i);
               changed = true;
            }
         }

         return changed;
      }

      public boolean removeAll(Collection<?> collection) {
         boolean changed = false;

         for(Object element : collection) {
            if (element instanceof Integer) {
               int c = (Integer)element;
               if (this.remove(c)) {
                  changed = true;
               }
            }
         }

         return changed;
      }

      public boolean removeAll(TIntCollection collection) {
         if (collection == this) {
            this.clear();
            return true;
         } else {
            boolean changed = false;
            TIntIterator iter = collection.iterator();

            while(iter.hasNext()) {
               int element = iter.next();
               if (this.remove(element)) {
                  changed = true;
               }
            }

            return changed;
         }
      }

      public boolean removeAll(int[] array) {
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
         TIntObjectHashMap.this.clear();
      }

      public boolean forEach(TIntProcedure procedure) {
         return TIntObjectHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TIntSet)) {
            return false;
         } else {
            TIntSet that = (TIntSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TIntObjectHashMap.this._states.length;

               while(i-- > 0) {
                  if (TIntObjectHashMap.this._states[i] == 1 && !that.contains(TIntObjectHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TIntObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TIntObjectHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TIntObjectHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         StringBuilder buf = new StringBuilder("{");
         boolean first = true;
         int i = TIntObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TIntObjectHashMap.this._states[i] == 1) {
               if (first) {
                  first = false;
               } else {
                  buf.append(",");
               }

               buf.append(TIntObjectHashMap.this._set[i]);
            }
         }

         return buf.toString();
      }

      class TIntHashIterator extends THashPrimitiveIterator implements TIntIterator {
         private final TIntHash _hash;

         public TIntHashIterator(TIntHash hash) {
            super(hash);
            this._hash = hash;
         }

         public int next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
         }
      }
   }

   protected class ValueView extends TIntObjectHashMap<V>.MapBackedView<V> {
      public Iterator<V> iterator() {
         return new TIntObjectHashMap<V>.ValueView.TIntObjectValueHashIterator(TIntObjectHashMap.this) {
            protected V objectAtIndex(int index) {
               return (V)TIntObjectHashMap.this._values[index];
            }
         };
      }

      public boolean containsElement(V value) {
         return TIntObjectHashMap.this.containsValue(value);
      }

      public boolean removeElement(V value) {
         V[] values = TIntObjectHashMap.this._values;
         byte[] states = TIntObjectHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && (value == values[i] || null != values[i] && values[i].equals(value))) {
               TIntObjectHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      class TIntObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
         protected final TIntObjectHashMap _map;

         public TIntObjectValueHashIterator(TIntObjectHashMap map) {
            super(map);
            this._map = map;
         }

         protected V objectAtIndex(int index) {
            byte[] states = TIntObjectHashMap.this._states;
            Object value = this._map._values[index];
            return (V)(states[index] != 1 ? null : value);
         }

         public V next() {
            this.moveToNextIndex();
            return (V)this._map._values[this._index];
         }
      }
   }

   private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {
      private MapBackedView() {
      }

      public abstract Iterator<E> iterator();

      public abstract boolean removeElement(E var1);

      public abstract boolean containsElement(E var1);

      public boolean contains(Object key) {
         return this.containsElement(key);
      }

      public boolean remove(Object o) {
         return this.removeElement(o);
      }

      public void clear() {
         TIntObjectHashMap.this.clear();
      }

      public boolean add(E obj) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return TIntObjectHashMap.this.size();
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
         return TIntObjectHashMap.this.isEmpty();
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

   class TIntObjectHashIterator<V> extends THashPrimitiveIterator implements TIntObjectIterator<V> {
      private final TIntObjectHashMap<V> _map;

      public TIntObjectHashIterator(TIntObjectHashMap<V> map) {
         super(map);
         this._map = map;
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public int key() {
         return this._map._set[this._index];
      }

      public V value() {
         return (V)this._map._values[this._index];
      }

      public V setValue(V val) {
         V old = (V)this.value();
         this._map._values[this._index] = val;
         return old;
      }
   }
}
