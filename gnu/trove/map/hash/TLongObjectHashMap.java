package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
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

public class TLongObjectHashMap<V> extends TLongHash implements TLongObjectMap<V>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TLongObjectProcedure<V> PUT_ALL_PROC;
   protected transient V[] _values;
   protected long no_entry_key;

   public TLongObjectHashMap() {
      this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
         public boolean execute(long key, V value) {
            TLongObjectHashMap.this.put(key, value);
            return true;
         }
      };
   }

   public TLongObjectHashMap(int initialCapacity) {
      super(initialCapacity);
      this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
         public boolean execute(long key, V value) {
            TLongObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
   }

   public TLongObjectHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
         public boolean execute(long key, V value) {
            TLongObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
   }

   public TLongObjectHashMap(int initialCapacity, float loadFactor, long noEntryKey) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TLongObjectProcedure<V>() {
         public boolean execute(long key, V value) {
            TLongObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = noEntryKey;
   }

   public TLongObjectHashMap(TLongObjectMap<? extends V> map) {
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
      long[] oldKeys = this._set;
      V[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new long[newCapacity];
      this._values = (V[])(new Object[newCapacity]);
      this._states = new byte[newCapacity];
      int i = oldCapacity;

      while(i-- > 0) {
         if (oldStates[i] == 1) {
            long o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
         }
      }

   }

   public long getNoEntryKey() {
      return this.no_entry_key;
   }

   public boolean containsKey(long key) {
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

   public V get(long key) {
      int index = this.index(key);
      return (V)(index < 0 ? null : this._values[index]);
   }

   public V put(long key, V value) {
      int index = this.insertKey(key);
      return (V)this.doPut(value, index);
   }

   public V putIfAbsent(long key, V value) {
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

   public V remove(long key) {
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

   public void putAll(Map<? extends Long, ? extends V> map) {
      for(Map.Entry<? extends Long, ? extends V> entry : map.entrySet()) {
         this.put((Long)entry.getKey(), entry.getValue());
      }

   }

   public void putAll(TLongObjectMap<? extends V> map) {
      map.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
      Arrays.fill(this._values, 0, this._values.length, (Object)null);
   }

   public TLongSet keySet() {
      return new KeyView();
   }

   public long[] keys() {
      long[] keys = new long[this.size()];
      long[] k = this._set;
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

   public long[] keys(long[] dest) {
      if (dest.length < this._size) {
         dest = new long[this._size];
      }

      long[] k = this._set;
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

   public TLongObjectIterator<V> iterator() {
      return new TLongObjectHashIterator<V>(this);
   }

   public boolean forEachKey(TLongProcedure procedure) {
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

   public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) {
      byte[] states = this._states;
      long[] keys = this._set;
      V[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean retainEntries(TLongObjectProcedure<? super V> procedure) {
      boolean modified = false;
      byte[] states = this._states;
      long[] keys = this._set;
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
      if (!(other instanceof TLongObjectMap)) {
         return false;
      } else {
         TLongObjectMap that = (TLongObjectMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            try {
               TLongObjectIterator iter = this.iterator();

               while(iter.hasNext()) {
                  iter.advance();
                  long key = iter.key();
                  Object value = iter.value();
                  if (value == null) {
                     if (that.get(key) != null || !that.containsKey(key)) {
                        return false;
                     }
                  } else if (!value.equals(that.get(key))) {
                     return false;
                  }
               }
            } catch (ClassCastException var7) {
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
      out.writeLong(this.no_entry_key);
      out.writeInt(this._size);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeLong(this._set[i]);
            out.writeObject(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      this.no_entry_key = in.readLong();
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         long key = in.readLong();
         V val = (V)in.readObject();
         this.put(key, val);
      }

   }

   public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      this.forEachEntry(new TLongObjectProcedure<V>() {
         private boolean first = true;

         public boolean execute(long key, Object value) {
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

   class KeyView implements TLongSet {
      public long getNoEntryValue() {
         return TLongObjectHashMap.this.no_entry_key;
      }

      public int size() {
         return TLongObjectHashMap.this._size;
      }

      public boolean isEmpty() {
         return TLongObjectHashMap.this._size == 0;
      }

      public boolean contains(long entry) {
         return TLongObjectHashMap.this.containsKey(entry);
      }

      public TLongIterator iterator() {
         return new TLongHashIterator(TLongObjectHashMap.this);
      }

      public long[] toArray() {
         return TLongObjectHashMap.this.keys();
      }

      public long[] toArray(long[] dest) {
         return TLongObjectHashMap.this.keys(dest);
      }

      public boolean add(long entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long entry) {
         return null != TLongObjectHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!TLongObjectHashMap.this.containsKey((Long)element)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TLongCollection collection) {
         if (collection == this) {
            return true;
         } else {
            TLongIterator iter = collection.iterator();

            while(iter.hasNext()) {
               if (!TLongObjectHashMap.this.containsKey(iter.next())) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean containsAll(long[] array) {
         for(long element : array) {
            if (!TLongObjectHashMap.this.containsKey(element)) {
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
         long[] set = TLongObjectHashMap.this._set;
         byte[] states = TLongObjectHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TLongObjectHashMap.this.removeAt(i);
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
         if (collection == this) {
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
         TLongObjectHashMap.this.clear();
      }

      public boolean forEach(TLongProcedure procedure) {
         return TLongObjectHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TLongSet)) {
            return false;
         } else {
            TLongSet that = (TLongSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TLongObjectHashMap.this._states.length;

               while(i-- > 0) {
                  if (TLongObjectHashMap.this._states[i] == 1 && !that.contains(TLongObjectHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TLongObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TLongObjectHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TLongObjectHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         StringBuilder buf = new StringBuilder("{");
         boolean first = true;
         int i = TLongObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TLongObjectHashMap.this._states[i] == 1) {
               if (first) {
                  first = false;
               } else {
                  buf.append(",");
               }

               buf.append(TLongObjectHashMap.this._set[i]);
            }
         }

         return buf.toString();
      }

      class TLongHashIterator extends THashPrimitiveIterator implements TLongIterator {
         private final TLongHash _hash;

         public TLongHashIterator(TLongHash hash) {
            super(hash);
            this._hash = hash;
         }

         public long next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
         }
      }
   }

   protected class ValueView extends TLongObjectHashMap<V>.MapBackedView<V> {
      public Iterator<V> iterator() {
         return new TLongObjectHashMap<V>.ValueView.TLongObjectValueHashIterator(TLongObjectHashMap.this) {
            protected V objectAtIndex(int index) {
               return (V)TLongObjectHashMap.this._values[index];
            }
         };
      }

      public boolean containsElement(V value) {
         return TLongObjectHashMap.this.containsValue(value);
      }

      public boolean removeElement(V value) {
         V[] values = TLongObjectHashMap.this._values;
         byte[] states = TLongObjectHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && (value == values[i] || null != values[i] && values[i].equals(value))) {
               TLongObjectHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      class TLongObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
         protected final TLongObjectHashMap _map;

         public TLongObjectValueHashIterator(TLongObjectHashMap map) {
            super(map);
            this._map = map;
         }

         protected V objectAtIndex(int index) {
            byte[] states = TLongObjectHashMap.this._states;
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
         TLongObjectHashMap.this.clear();
      }

      public boolean add(E obj) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return TLongObjectHashMap.this.size();
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
         return TLongObjectHashMap.this.isEmpty();
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

   class TLongObjectHashIterator<V> extends THashPrimitiveIterator implements TLongObjectIterator<V> {
      private final TLongObjectHashMap<V> _map;

      public TLongObjectHashIterator(TLongObjectHashMap<V> map) {
         super(map);
         this._map = map;
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public long key() {
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
