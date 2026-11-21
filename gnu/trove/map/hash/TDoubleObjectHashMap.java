package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
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

public class TDoubleObjectHashMap<V> extends TDoubleHash implements TDoubleObjectMap<V>, Externalizable {
   static final long serialVersionUID = 1L;
   private final TDoubleObjectProcedure<V> PUT_ALL_PROC;
   protected transient V[] _values;
   protected double no_entry_key;

   public TDoubleObjectHashMap() {
      this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
         public boolean execute(double key, V value) {
            TDoubleObjectHashMap.this.put(key, value);
            return true;
         }
      };
   }

   public TDoubleObjectHashMap(int initialCapacity) {
      super(initialCapacity);
      this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
         public boolean execute(double key, V value) {
            TDoubleObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
   }

   public TDoubleObjectHashMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
         public boolean execute(double key, V value) {
            TDoubleObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
   }

   public TDoubleObjectHashMap(int initialCapacity, float loadFactor, double noEntryKey) {
      super(initialCapacity, loadFactor);
      this.PUT_ALL_PROC = new TDoubleObjectProcedure<V>() {
         public boolean execute(double key, V value) {
            TDoubleObjectHashMap.this.put(key, value);
            return true;
         }
      };
      this.no_entry_key = noEntryKey;
   }

   public TDoubleObjectHashMap(TDoubleObjectMap<? extends V> map) {
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
      double[] oldKeys = this._set;
      V[] oldVals = this._values;
      byte[] oldStates = this._states;
      this._set = new double[newCapacity];
      this._values = (V[])(new Object[newCapacity]);
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

   public double getNoEntryKey() {
      return this.no_entry_key;
   }

   public boolean containsKey(double key) {
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

   public V get(double key) {
      int index = this.index(key);
      return (V)(index < 0 ? null : this._values[index]);
   }

   public V put(double key, V value) {
      int index = this.insertKey(key);
      return (V)this.doPut(value, index);
   }

   public V putIfAbsent(double key, V value) {
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

   public V remove(double key) {
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

   public void putAll(Map<? extends Double, ? extends V> map) {
      for(Map.Entry<? extends Double, ? extends V> entry : map.entrySet()) {
         this.put((Double)entry.getKey(), entry.getValue());
      }

   }

   public void putAll(TDoubleObjectMap<? extends V> map) {
      map.forEachEntry(this.PUT_ALL_PROC);
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
      Arrays.fill(this._values, 0, this._values.length, (Object)null);
   }

   public TDoubleSet keySet() {
      return new KeyView();
   }

   public double[] keys() {
      double[] keys = new double[this.size()];
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

   public double[] keys(double[] dest) {
      if (dest.length < this._size) {
         dest = new double[this._size];
      }

      double[] k = this._set;
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

   public TDoubleObjectIterator<V> iterator() {
      return new TDoubleObjectHashIterator<V>(this);
   }

   public boolean forEachKey(TDoubleProcedure procedure) {
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

   public boolean forEachEntry(TDoubleObjectProcedure<? super V> procedure) {
      byte[] states = this._states;
      double[] keys = this._set;
      V[] values = this._values;
      int i = keys.length;

      while(i-- > 0) {
         if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean retainEntries(TDoubleObjectProcedure<? super V> procedure) {
      boolean modified = false;
      byte[] states = this._states;
      double[] keys = this._set;
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
      if (!(other instanceof TDoubleObjectMap)) {
         return false;
      } else {
         TDoubleObjectMap that = (TDoubleObjectMap)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            try {
               TDoubleObjectIterator iter = this.iterator();

               while(iter.hasNext()) {
                  iter.advance();
                  double key = iter.key();
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
      out.writeDouble(this.no_entry_key);
      out.writeInt(this._size);
      int i = this._states.length;

      while(i-- > 0) {
         if (this._states[i] == 1) {
            out.writeDouble(this._set[i]);
            out.writeObject(this._values[i]);
         }
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      super.readExternal(in);
      this.no_entry_key = in.readDouble();
      int size = in.readInt();
      this.setUp(size);

      while(size-- > 0) {
         double key = in.readDouble();
         V val = (V)in.readObject();
         this.put(key, val);
      }

   }

   public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      this.forEachEntry(new TDoubleObjectProcedure<V>() {
         private boolean first = true;

         public boolean execute(double key, Object value) {
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

   class KeyView implements TDoubleSet {
      public double getNoEntryValue() {
         return TDoubleObjectHashMap.this.no_entry_key;
      }

      public int size() {
         return TDoubleObjectHashMap.this._size;
      }

      public boolean isEmpty() {
         return TDoubleObjectHashMap.this._size == 0;
      }

      public boolean contains(double entry) {
         return TDoubleObjectHashMap.this.containsKey(entry);
      }

      public TDoubleIterator iterator() {
         return new TDoubleHashIterator(TDoubleObjectHashMap.this);
      }

      public double[] toArray() {
         return TDoubleObjectHashMap.this.keys();
      }

      public double[] toArray(double[] dest) {
         return TDoubleObjectHashMap.this.keys(dest);
      }

      public boolean add(double entry) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double entry) {
         return null != TDoubleObjectHashMap.this.remove(entry);
      }

      public boolean containsAll(Collection<?> collection) {
         for(Object element : collection) {
            if (!TDoubleObjectHashMap.this.containsKey((Double)element)) {
               return false;
            }
         }

         return true;
      }

      public boolean containsAll(TDoubleCollection collection) {
         if (collection == this) {
            return true;
         } else {
            TDoubleIterator iter = collection.iterator();

            while(iter.hasNext()) {
               if (!TDoubleObjectHashMap.this.containsKey(iter.next())) {
                  return false;
               }
            }

            return true;
         }
      }

      public boolean containsAll(double[] array) {
         for(double element : array) {
            if (!TDoubleObjectHashMap.this.containsKey(element)) {
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
         double[] set = TDoubleObjectHashMap.this._set;
         byte[] states = TDoubleObjectHashMap.this._states;
         int i = set.length;

         while(i-- > 0) {
            if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
               TDoubleObjectHashMap.this.removeAt(i);
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
         if (collection == this) {
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
         TDoubleObjectHashMap.this.clear();
      }

      public boolean forEach(TDoubleProcedure procedure) {
         return TDoubleObjectHashMap.this.forEachKey(procedure);
      }

      public boolean equals(Object other) {
         if (!(other instanceof TDoubleSet)) {
            return false;
         } else {
            TDoubleSet that = (TDoubleSet)other;
            if (that.size() != this.size()) {
               return false;
            } else {
               int i = TDoubleObjectHashMap.this._states.length;

               while(i-- > 0) {
                  if (TDoubleObjectHashMap.this._states[i] == 1 && !that.contains(TDoubleObjectHashMap.this._set[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hashcode = 0;
         int i = TDoubleObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TDoubleObjectHashMap.this._states[i] == 1) {
               hashcode += HashFunctions.hash(TDoubleObjectHashMap.this._set[i]);
            }
         }

         return hashcode;
      }

      public String toString() {
         StringBuilder buf = new StringBuilder("{");
         boolean first = true;
         int i = TDoubleObjectHashMap.this._states.length;

         while(i-- > 0) {
            if (TDoubleObjectHashMap.this._states[i] == 1) {
               if (first) {
                  first = false;
               } else {
                  buf.append(",");
               }

               buf.append(TDoubleObjectHashMap.this._set[i]);
            }
         }

         return buf.toString();
      }

      class TDoubleHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
         private final TDoubleHash _hash;

         public TDoubleHashIterator(TDoubleHash hash) {
            super(hash);
            this._hash = hash;
         }

         public double next() {
            this.moveToNextIndex();
            return this._hash._set[this._index];
         }
      }
   }

   protected class ValueView extends TDoubleObjectHashMap<V>.MapBackedView<V> {
      public Iterator<V> iterator() {
         return new TDoubleObjectHashMap<V>.ValueView.TDoubleObjectValueHashIterator(TDoubleObjectHashMap.this) {
            protected V objectAtIndex(int index) {
               return (V)TDoubleObjectHashMap.this._values[index];
            }
         };
      }

      public boolean containsElement(V value) {
         return TDoubleObjectHashMap.this.containsValue(value);
      }

      public boolean removeElement(V value) {
         V[] values = TDoubleObjectHashMap.this._values;
         byte[] states = TDoubleObjectHashMap.this._states;
         int i = values.length;

         while(i-- > 0) {
            if (states[i] == 1 && (value == values[i] || null != values[i] && values[i].equals(value))) {
               TDoubleObjectHashMap.this.removeAt(i);
               return true;
            }
         }

         return false;
      }

      class TDoubleObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
         protected final TDoubleObjectHashMap _map;

         public TDoubleObjectValueHashIterator(TDoubleObjectHashMap map) {
            super(map);
            this._map = map;
         }

         protected V objectAtIndex(int index) {
            byte[] states = TDoubleObjectHashMap.this._states;
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
         TDoubleObjectHashMap.this.clear();
      }

      public boolean add(E obj) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return TDoubleObjectHashMap.this.size();
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
         return TDoubleObjectHashMap.this.isEmpty();
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

   class TDoubleObjectHashIterator<V> extends THashPrimitiveIterator implements TDoubleObjectIterator<V> {
      private final TDoubleObjectHashMap<V> _map;

      public TDoubleObjectHashIterator(TDoubleObjectHashMap<V> map) {
         super(map);
         this._map = map;
      }

      public void advance() {
         this.moveToNextIndex();
      }

      public double key() {
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
