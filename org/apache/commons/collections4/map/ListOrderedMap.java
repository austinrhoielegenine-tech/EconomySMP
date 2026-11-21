package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.AbstractUntypedIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.list.UnmodifiableList;

public class ListOrderedMap<K, V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V>, Serializable {
   private static final long serialVersionUID = 2728177751851003750L;
   private final List<K> insertOrder;

   public static <K, V> ListOrderedMap<K, V> listOrderedMap(Map<K, V> map) {
      return new ListOrderedMap<K, V>(map);
   }

   public ListOrderedMap() {
      this(new HashMap());
   }

   protected ListOrderedMap(Map<K, V> map) {
      super(map);
      this.insertOrder = new ArrayList();
      this.insertOrder.addAll(this.decorated().keySet());
   }

   public List<K> asList() {
      return this.keyList();
   }

   public void clear() {
      this.decorated().clear();
      this.insertOrder.clear();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return new EntrySetView(this, this.insertOrder);
   }

   public K firstKey() {
      if (this.isEmpty()) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return (K)this.insertOrder.get(0);
      }
   }

   public K get(int index) {
      return (K)this.insertOrder.get(index);
   }

   public V getValue(int index) {
      return (V)this.get(this.insertOrder.get(index));
   }

   public int indexOf(Object key) {
      return this.insertOrder.indexOf(key);
   }

   public List<K> keyList() {
      return UnmodifiableList.<K>unmodifiableList(this.insertOrder);
   }

   public Set<K> keySet() {
      return new KeySetView<K>(this);
   }

   public K lastKey() {
      if (this.isEmpty()) {
         throw new NoSuchElementException("Map is empty");
      } else {
         return (K)this.insertOrder.get(this.size() - 1);
      }
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return new ListOrderedMapIterator<K, V>(this);
   }

   public K nextKey(Object key) {
      int index = this.insertOrder.indexOf(key);
      return (K)(index >= 0 && index < this.size() - 1 ? this.insertOrder.get(index + 1) : null);
   }

   public K previousKey(Object key) {
      int index = this.insertOrder.indexOf(key);
      return (K)(index > 0 ? this.insertOrder.get(index - 1) : null);
   }

   public V put(int index, K key, V value) {
      if (index >= 0 && index <= this.insertOrder.size()) {
         Map<K, V> m = this.decorated();
         if (m.containsKey(key)) {
            V result = (V)m.remove(key);
            int pos = this.insertOrder.indexOf(key);
            this.insertOrder.remove(pos);
            if (pos < index) {
               --index;
            }

            this.insertOrder.add(index, key);
            m.put(key, value);
            return result;
         } else {
            this.insertOrder.add(index, key);
            m.put(key, value);
            return null;
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
      }
   }

   public V put(K key, V value) {
      if (this.decorated().containsKey(key)) {
         return (V)this.decorated().put(key, value);
      } else {
         V result = (V)this.decorated().put(key, value);
         this.insertOrder.add(key);
         return result;
      }
   }

   public void putAll(int index, Map<? extends K, ? extends V> map) {
      if (index >= 0 && index <= this.insertOrder.size()) {
         for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            K key = (K)entry.getKey();
            boolean contains = this.containsKey(key);
            this.put(index, entry.getKey(), entry.getValue());
            if (!contains) {
               ++index;
            } else {
               index = this.indexOf(entry.getKey()) + 1;
            }
         }

      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.insertOrder.size());
      }
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   public V remove(int index) {
      return (V)this.remove(this.get(index));
   }

   public V remove(Object key) {
      V result = null;
      if (this.decorated().containsKey(key)) {
         result = (V)this.decorated().remove(key);
         this.insertOrder.remove(key);
      }

      return result;
   }

   public V setValue(int index, V value) {
      K key = (K)this.insertOrder.get(index);
      return (V)this.put(key, value);
   }

   public String toString() {
      if (this.isEmpty()) {
         return "{}";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append('{');
         boolean first = true;

         for(Map.Entry<K, V> entry : this.entrySet()) {
            K key = (K)entry.getKey();
            V value = (V)entry.getValue();
            if (first) {
               first = false;
            } else {
               buf.append(", ");
            }

            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            buf.append(value == this ? "(this Map)" : value);
         }

         buf.append('}');
         return buf.toString();
      }
   }

   public List<V> valueList() {
      return new ValuesView<V>(this);
   }

   public Collection<V> values() {
      return new ValuesView<V>(this);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }

   static class EntrySetView<K, V> extends AbstractSet<Map.Entry<K, V>> {
      private final ListOrderedMap<K, V> parent;
      private final List<K> insertOrder;
      private Set<Map.Entry<K, V>> entrySet;

      EntrySetView(ListOrderedMap<K, V> parent, List<K> insertOrder) {
         this.parent = parent;
         this.insertOrder = insertOrder;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object obj) {
         return this.getEntrySet().contains(obj);
      }

      public boolean containsAll(Collection<?> coll) {
         return this.getEntrySet().containsAll(coll);
      }

      public boolean equals(Object obj) {
         return obj == this ? true : this.getEntrySet().equals(obj);
      }

      private Set<Map.Entry<K, V>> getEntrySet() {
         if (this.entrySet == null) {
            this.entrySet = this.parent.decorated().entrySet();
         }

         return this.entrySet;
      }

      public int hashCode() {
         return this.getEntrySet().hashCode();
      }

      public boolean isEmpty() {
         return this.parent.isEmpty();
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return new ListOrderedIterator(this.parent, this.insertOrder);
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else if (this.getEntrySet().contains(obj)) {
            Object key = ((Map.Entry)obj).getKey();
            this.parent.remove(key);
            return true;
         } else {
            return false;
         }
      }

      public int size() {
         return this.parent.size();
      }

      public String toString() {
         return this.getEntrySet().toString();
      }
   }

   static class KeySetView<K> extends AbstractSet<K> {
      private final ListOrderedMap<K, Object> parent;

      KeySetView(ListOrderedMap<K, ?> parent) {
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object value) {
         return this.parent.containsKey(value);
      }

      public Iterator<K> iterator() {
         return new AbstractUntypedIteratorDecorator<Map.Entry<K, Object>, K>(this.parent.entrySet().iterator()) {
            public K next() {
               return (K)((Map.Entry)this.getIterator().next()).getKey();
            }
         };
      }

      public int size() {
         return this.parent.size();
      }
   }

   static class ListOrderedIterator<K, V> extends AbstractUntypedIteratorDecorator<K, Map.Entry<K, V>> {
      private final ListOrderedMap<K, V> parent;
      private K last;

      ListOrderedIterator(ListOrderedMap<K, V> parent, List<K> insertOrder) {
         super(insertOrder.iterator());
         this.parent = parent;
      }

      public Map.Entry<K, V> next() {
         this.last = (K)this.getIterator().next();
         return new ListOrderedMapEntry<K, V>(this.parent, this.last);
      }

      public void remove() {
         super.remove();
         this.parent.decorated().remove(this.last);
      }
   }

   static class ListOrderedMapEntry<K, V> extends AbstractMapEntry<K, V> {
      private final ListOrderedMap<K, V> parent;

      ListOrderedMapEntry(ListOrderedMap<K, V> parent, K key) {
         super(key, (Object)null);
         this.parent = parent;
      }

      public V getValue() {
         return (V)this.parent.get(this.getKey());
      }

      public V setValue(V value) {
         return (V)this.parent.decorated().put(this.getKey(), value);
      }
   }

   static class ListOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
      private final ListOrderedMap<K, V> parent;
      private ListIterator<K> iterator;
      private K last;
      private boolean readable;

      ListOrderedMapIterator(ListOrderedMap<K, V> parent) {
         this.parent = parent;
         this.iterator = parent.insertOrder.listIterator();
      }

      public K getKey() {
         if (!this.readable) {
            throw new IllegalStateException("getKey() can only be called after next() and before remove()");
         } else {
            return this.last;
         }
      }

      public V getValue() {
         if (!this.readable) {
            throw new IllegalStateException("getValue() can only be called after next() and before remove()");
         } else {
            return (V)this.parent.get(this.last);
         }
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public boolean hasPrevious() {
         return this.iterator.hasPrevious();
      }

      public K next() {
         this.last = (K)this.iterator.next();
         this.readable = true;
         return this.last;
      }

      public K previous() {
         this.last = (K)this.iterator.previous();
         this.readable = true;
         return this.last;
      }

      public void remove() {
         if (!this.readable) {
            throw new IllegalStateException("remove() can only be called once after next()");
         } else {
            this.iterator.remove();
            this.parent.map.remove(this.last);
            this.readable = false;
         }
      }

      public void reset() {
         this.iterator = this.parent.insertOrder.listIterator();
         this.last = null;
         this.readable = false;
      }

      public V setValue(V value) {
         if (!this.readable) {
            throw new IllegalStateException("setValue() can only be called after next() and before remove()");
         } else {
            return (V)this.parent.map.put(this.last, value);
         }
      }

      public String toString() {
         return this.readable ? "Iterator[" + this.getKey() + "=" + this.getValue() + "]" : "Iterator[]";
      }
   }

   static class ValuesView<V> extends AbstractList<V> {
      private final ListOrderedMap<Object, V> parent;

      ValuesView(ListOrderedMap<?, V> parent) {
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean contains(Object value) {
         return this.parent.containsValue(value);
      }

      public V get(int index) {
         return this.parent.getValue(index);
      }

      public Iterator<V> iterator() {
         return new AbstractUntypedIteratorDecorator<Map.Entry<Object, V>, V>(this.parent.entrySet().iterator()) {
            public V next() {
               return (V)((Map.Entry)this.getIterator().next()).getValue();
            }
         };
      }

      public V remove(int index) {
         return this.parent.remove(index);
      }

      public V set(int index, V value) {
         return this.parent.setValue(index, value);
      }

      public int size() {
         return this.parent.size();
      }
   }
}
