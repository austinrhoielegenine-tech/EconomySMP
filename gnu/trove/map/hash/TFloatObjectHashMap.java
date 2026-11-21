package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
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

public class TFloatObjectHashMap<V> extends TFloatHash implements TFloatObjectMap<V>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TFloatObjectProcedure<V> PUT_ALL_PROC;
   protected transient V[] _values;
   protected float no_entry_key;

   public TFloatObjectHashMap() {
      this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
         public boolean execute(float key, V value) {
            TFloatObjectHashMap.this.put(key, value);
            return true;
         }
      };
   }

   public TFloatObjectHashMap(int initialCapacity) {
      super(initialCapacity);
      this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
         public boolean execute(float key, V value) {
            TFloatObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
   }

   public TFloatObjectHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
         public boolean execute(float key, V value) {
            TFloatObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
   }

   public TFloatObjectHashMap(int initialCapacity, float loadFactor, float noEntryKey) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TFloatObjectProcedure<V>() {
         public boolean execute(float key, V value) {
            TFloatObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = noEntryKey;
   }

   public TFloatObjectHashMap(TFloatObjectMap<? extends V> map) {
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
      float[] oldKeys = this._set;
      V[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new float[newCapacity];
      this._values = (V[])(new Object[newCapacity]);
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

   public float getNoEntryKey() {
      return this.no_entry_key;
   }

   public boolean containsKey(float key) {
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

   public V get(float key) {
      int index = this.index(key);
      return (V)(index < 0 ? null : this._values[index]);
   }

   public V put(float key, V value) {
      int index = this.insertKey(key);
      return (V)this.doPut(value, index);
   }

   public V putIfAbsent(float key, V value) {
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

   public V remove(float key) {
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

   public void putAll(Map<? extends Float, ? extends V> map) {
      for(Map.Entry<? extends Float, ? extends V> entry : map.entrySet()) {
         this.put((Float)entry.getKey(), entry.getValue());
      }

   }

   public void putAll(TFloatObjectMap<? extends V> map) {
      map.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
      Arrays.fill(this._values, 0, this._values.length, (Object)null);
   }

   public TFloatSet keySet() {
      return new KeyView();
   }

   public float[] keys() {
      float[] keys = new float[this.size()];
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

   public float[] keys(float[] dest) {
      if (dest.length < this._size) {
         dest = new float[this._size];
      }

      float[] k = this._set;
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

   public TFloatObjectIterator<V> iterator() {
      return new TFloatObjectHashIterator<V>(this);
   }

   public boolean forEachKey(TFloatProcedure procedure) {
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

   public boolean forEachEntry(TFloatObjectProcedure<? super V> procedure) {
      byte[] states = this._states;
      float[] keys = this._set;
      V[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean retainEntries(TFloatObjectProcedure<? super V> procedure) {
      boolean modified = false;
      byte[] states = this._states;
      float[] keys = this._set;
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
      if (!(other instanceof TFloatObjectMap)) {
         return false;
      } else {
         TFloatObjectMap that = (TFloatObjectMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            try {
               TFloatObjectIterator iter = this.iterator();

               while(iter.hasNext()) {
                  iter.advance();
                  float key = iter.key();
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
      out.writeFloat(this.no_entry_key);
      out.writeInt(this._size);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeFloat(this._set[i]);
            out.writeObject(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      this.no_entry_key = in.readFloat();
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         float key = in.readFloat();
         V val = (V)in.readObject();
         this.put(key, val);
      }

   }

   public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      this.forEachEntry(new TFloatObjectProcedure<V>() {
         private boolean first = true;

         public boolean execute(float key, Object value) {
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

   class KeyView implements TFloatSet {
      public float getNoEntryValue() {
         return TFloatObjectHashMap.this.no_entry_key;
      }

      public int size() {
         return TFloatObjectHashMap.this._size;
      }

      public boolean isEmpty() {
         return TFloatObjectHashMap.this._size == 0;
      }

      public boolean contains(float entry) {
         return TFloatObjectHashMap.this.containsKey(entry);
      }

      public TFloatIterator iterator() {
         return new TFloatHashIterator(TFloatObjectHashMap.this);
      }

      public float[] toArray() {
         return TFloatObjectHashMap.this.keys();
      }

      public float[] toArray(float[] dest) {
         return TFloatObjectHashMap.this.keys(dest);
      }

      public boolean add(float entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float entry) {
         return null != TFloatObjectHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!TFloatObjectHashMap.this.containsKey((Float)element)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TFloatCollection collection) {
         if (collection == this) {
            return true;
         } else {
            TFloatIterator iter = collection.iterator();

            while(iter.hasNext()) {
               if (!TFloatObjectHashMap.this.containsKey(iter.next())) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean containsAll(float[] array) {
         for(float element : array) {
            if (!TFloatObjectHashMap.this.containsKey(element)) {
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
         float[] set = TFloatObjectHashMap.this._set;
         byte[] states = TFloatObjectHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TFloatObjectHashMap.this.removeAt(i);
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
         if (collection == this) {
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
         TFloatObjectHashMap.this.clear();
      }

      public boolean forEach(TFloatProcedure procedure) {
         return TFloatObjectHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TFloatSet)) {
            return false;
         } else {
            TFloatSet that = (TFloatSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TFloatObjectHashMap.this._states.length;

               while(i-- > 0) {
                  if (TFloatObjectHashMap.this._states[i] == 1 && !that.contains(TFloatObjectHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TFloatObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TFloatObjectHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TFloatObjectHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         StringBuilder buf = new StringBuilder("{");
         boolean first = true;
         int i = TFloatObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TFloatObjectHashMap.this._states[i] == 1) {
               if (first) {
                  first = false;
               } else {
                  buf.append(",");
               }

               buf.append(TFloatObjectHashMap.this._set[i]);
            }
         }

         return buf.toString();
      }

      class TFloatHashIterator extends THashPrimitiveIterator implements TFloatIterator {
         private final TFloatHash _hash;

         public TFloatHashIterator(TFloatHash hash) {
            super(hash);
            this._hash = hash;
         }

         public float next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
         }
      }
   }

   protected class ValueView extends TFloatObjectHashMap<V>.MapBackedView<V> {
      public Iterator<V> iterator() {
         return new TFloatObjectHashMap<V>.ValueView.TFloatObjectValueHashIterator(TFloatObjectHashMap.this) {
            protected V objectAtIndex(int index) {
               return (V)TFloatObjectHashMap.this._values[index];
            }
         };
      }

      public boolean containsElement(V value) {
         return TFloatObjectHashMap.this.containsValue(value);
      }

      public boolean removeElement(V value) {
         V[] values = TFloatObjectHashMap.this._values;
         byte[] states = TFloatObjectHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && (value == values[i] || null != values[i] && values[i].equals(value))) {
               TFloatObjectHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      class TFloatObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
         protected final TFloatObjectHashMap _map;

         public TFloatObjectValueHashIterator(TFloatObjectHashMap map) {
            super(map);
            this._map = map;
         }

         protected V objectAtIndex(int index) {
            byte[] states = TFloatObjectHashMap.this._states;
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
         TFloatObjectHashMap.this.clear();
      }

      public boolean add(E obj) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return TFloatObjectHashMap.this.size();
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
         return TFloatObjectHashMap.this.isEmpty();
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

   class TFloatObjectHashIterator<V> extends THashPrimitiveIterator implements TFloatObjectIterator<V> {
      private final TFloatObjectHashMap<V> _map;

      public TFloatObjectHashIterator(TFloatObjectHashMap<V> map) {
         super(map);
         this._map = map;
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public float key() {
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
