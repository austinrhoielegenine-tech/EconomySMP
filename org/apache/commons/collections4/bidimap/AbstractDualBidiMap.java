package org.apache.commons.collections4.bidimap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;

public abstract class AbstractDualBidiMap<K, V> implements BidiMap<K, V> {
   transient Map<K, V> normalMap;
   transient Map<V, K> reverseMap;
   transient BidiMap<V, K> inverseBidiMap;
   transient Set<K> keySet;
   transient Set<V> values;
   transient Set<Map.Entry<K, V>> entrySet;

   protected AbstractDualBidiMap() {
   }

   protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap) {
      this.normalMap = normalMap;
      this.reverseMap = reverseMap;
   }

   protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
      this.normalMap = normalMap;
      this.reverseMap = reverseMap;
      this.inverseBidiMap = inverseBidiMap;
   }

   public void clear() {
      this.normalMap.clear();
      this.reverseMap.clear();
   }

   public boolean containsKey(Object key) {
      return this.normalMap.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.reverseMap.containsKey(value);
   }

   protected abstract BidiMap<V, K> createBidiMap(Map<V, K> var1, Map<K, V> var2, BidiMap<K, V> var3);

   protected Iterator<Map.Entry<K, V>> createEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
      return new EntrySetIterator(iterator, this);
   }

   protected Iterator<K> createKeySetIterator(Iterator<K> iterator) {
      return new KeySetIterator<K>(iterator, this);
   }

   protected Iterator<V> createValuesIterator(Iterator<V> iterator) {
      return new ValuesIterator<V>(iterator, this);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new EntrySet(this);
      }

      return this.entrySet;
   }

   public boolean equals(Object obj) {
      return this.normalMap.equals(obj);
   }

   public V get(Object key) {
      return (V)this.normalMap.get(key);
   }

   public K getKey(Object value) {
      return (K)this.reverseMap.get(value);
   }

   public int hashCode() {
      return this.normalMap.hashCode();
   }

   public BidiMap<V, K> inverseBidiMap() {
      if (this.inverseBidiMap == null) {
         this.inverseBidiMap = this.createBidiMap(this.reverseMap, this.normalMap, this);
      }

      return this.inverseBidiMap;
   }

   public boolean isEmpty() {
      return this.normalMap.isEmpty();
   }

   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = new KeySet<K>(this);
      }

      return this.keySet;
   }

   public MapIterator<K, V> mapIterator() {
      return new BidiMapIterator<K, V>(this);
   }

   public V put(K key, V value) {
      if (this.normalMap.containsKey(key)) {
         this.reverseMap.remove(this.normalMap.get(key));
      }

      if (this.reverseMap.containsKey(value)) {
         this.normalMap.remove(this.reverseMap.get(value));
      }

      V obj = (V)this.normalMap.put(key, value);
      this.reverseMap.put(value, key);
      return obj;
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public V remove(Object key) {
      V value = null;
      if (this.normalMap.containsKey(key)) {
         value = (V)this.normalMap.remove(key);
         this.reverseMap.remove(value);
      }

      return value;
   }

   public K removeValue(Object value) {
      K key = null;
      if (this.reverseMap.containsKey(value)) {
         key = (K)this.reverseMap.remove(value);
         this.normalMap.remove(key);
      }

      return key;
   }

   public int size() {
      return this.normalMap.size();
   }

   public String toString() {
      return this.normalMap.toString();
   }

   public Set<V> values() {
      if (this.values == null) {
         this.values = new Values<V>(this);
      }

      return this.values;
   }

   protected static class BidiMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K> {
      protected final AbstractDualBidiMap<K, V> parent;
      protected Iterator<Map.Entry<K, V>> iterator;
      protected Map.Entry<K, V> last;
      protected boolean canRemove;

      protected BidiMapIterator(AbstractDualBidiMap<K, V> parent) {
         this.parent = parent;
         this.iterator = parent.normalMap.entrySet().iterator();
      }

      public K getKey() {
         if (this.last == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         } else {
            return (K)this.last.getKey();
         }
      }

      public V getValue() {
         if (this.last == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         } else {
            return (V)this.last.getValue();
         }
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public K next() {
         this.last = (Map.Entry)this.iterator.next();
         this.canRemove = true;
         return (K)this.last.getKey();
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            V value = (V)this.last.getValue();
            this.iterator.remove();
            this.parent.reverseMap.remove(value);
            this.last = null;
            this.canRemove = false;
         }
      }

      public void reset() {
         this.iterator = this.parent.normalMap.entrySet().iterator();
         this.last = null;
         this.canRemove = false;
      }

      public V setValue(V value) {
         if (this.last == null) {
            throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
         } else if (this.parent.reverseMap.containsKey(value) && this.parent.reverseMap.get(value) != this.last.getKey()) {
            throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
         } else {
            return this.parent.put(this.last.getKey(), value);
         }
      }

      public String toString() {
         return this.last != null ? "MapIterator[" + this.getKey() + "=" + this.getValue() + "]" : "MapIterator[]";
      }
   }

   protected static class EntrySet<K, V> extends View<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {
      private static final long serialVersionUID = 4040410962603292348L;

      protected EntrySet(AbstractDualBidiMap<K, V> parent) {
         super(parent.normalMap.entrySet(), parent);
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return this.parent.createEntrySetIterator(super.iterator());
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            Object key = entry.getKey();
            if (this.parent.containsKey(key)) {
               V value = (V)this.parent.normalMap.get(key);
               if (Objects.equals(value, entry.getValue())) {
                  this.parent.normalMap.remove(key);
                  this.parent.reverseMap.remove(value);
                  return true;
               }
            }

            return false;
         }
      }
   }

   protected static class EntrySetIterator<K, V> extends AbstractIteratorDecorator<Map.Entry<K, V>> {
      protected final AbstractDualBidiMap<K, V> parent;
      protected Map.Entry<K, V> last;
      protected boolean canRemove;

      protected EntrySetIterator(Iterator<Map.Entry<K, V>> iterator, AbstractDualBidiMap<K, V> parent) {
         super(iterator);
         this.parent = parent;
      }

      public Map.Entry<K, V> next() {
         this.last = new MapEntry<K, V>((Map.Entry)super.next(), this.parent);
         this.canRemove = true;
         return this.last;
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            Object value = this.last.getValue();
            super.remove();
            this.parent.reverseMap.remove(value);
            this.last = null;
            this.canRemove = false;
         }
      }
   }

   protected static class KeySet<K> extends View<K, Object, K> implements Set<K> {
      private static final long serialVersionUID = -7107935777385040694L;

      protected KeySet(AbstractDualBidiMap<K, ?> parent) {
         super(parent.normalMap.keySet(), parent);
      }

      public boolean contains(Object key) {
         return this.parent.normalMap.containsKey(key);
      }

      public Iterator<K> iterator() {
         return this.parent.createKeySetIterator(super.iterator());
      }

      public boolean remove(Object key) {
         if (this.parent.normalMap.containsKey(key)) {
            Object value = this.parent.normalMap.remove(key);
            this.parent.reverseMap.remove(value);
            return true;
         } else {
            return false;
         }
      }
   }

   protected static class KeySetIterator<K> extends AbstractIteratorDecorator<K> {
      protected final AbstractDualBidiMap<K, ?> parent;
      protected K lastKey;
      protected boolean canRemove;

      protected KeySetIterator(Iterator<K> iterator, AbstractDualBidiMap<K, ?> parent) {
         super(iterator);
         this.parent = parent;
      }

      public K next() {
         this.lastKey = (K)super.next();
         this.canRemove = true;
         return this.lastKey;
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            Object value = this.parent.normalMap.get(this.lastKey);
            super.remove();
            this.parent.reverseMap.remove(value);
            this.lastKey = null;
            this.canRemove = false;
         }
      }
   }

   protected static class MapEntry<K, V> extends AbstractMapEntryDecorator<K, V> {
      protected final AbstractDualBidiMap<K, V> parent;

      protected MapEntry(Map.Entry<K, V> entry, AbstractDualBidiMap<K, V> parent) {
         super(entry);
         this.parent = parent;
      }

      public V setValue(V value) {
         K key = (K)this.getKey();
         if (this.parent.reverseMap.containsKey(value) && this.parent.reverseMap.get(value) != key) {
            throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
         } else {
            this.parent.put(key, value);
            return (V)super.setValue(value);
         }
      }
   }

   protected static class Values<V> extends View<Object, V, V> implements Set<V> {
      private static final long serialVersionUID = 4023777119829639864L;

      protected Values(AbstractDualBidiMap<?, V> parent) {
         super(parent.normalMap.values(), parent);
      }

      public boolean contains(Object value) {
         return this.parent.reverseMap.containsKey(value);
      }

      public Iterator<V> iterator() {
         return this.parent.createValuesIterator(super.iterator());
      }

      public boolean remove(Object value) {
         if (this.parent.reverseMap.containsKey(value)) {
            Object key = this.parent.reverseMap.remove(value);
            this.parent.normalMap.remove(key);
            return true;
         } else {
            return false;
         }
      }
   }

   protected static class ValuesIterator<V> extends AbstractIteratorDecorator<V> {
      protected final AbstractDualBidiMap<Object, V> parent;
      protected V lastValue;
      protected boolean canRemove;

      protected ValuesIterator(Iterator<V> iterator, AbstractDualBidiMap<?, V> parent) {
         super(iterator);
         this.parent = parent;
      }

      public V next() {
         this.lastValue = (V)super.next();
         this.canRemove = true;
         return this.lastValue;
      }

      public void remove() {
         if (!this.canRemove) {
            throw new IllegalStateException("Iterator remove() can only be called once after next()");
         } else {
            super.remove();
            this.parent.reverseMap.remove(this.lastValue);
            this.lastValue = null;
            this.canRemove = false;
         }
      }
   }

   protected abstract static class View<K, V, E> extends AbstractCollectionDecorator<E> {
      private static final long serialVersionUID = 4621510560119690639L;
      protected final AbstractDualBidiMap<K, V> parent;

      protected View(Collection<E> coll, AbstractDualBidiMap<K, V> parent) {
         super(coll);
         this.parent = parent;
      }

      public void clear() {
         this.parent.clear();
      }

      public boolean equals(Object object) {
         return object == this || this.decorated().equals(object);
      }

      public int hashCode() {
         return this.decorated().hashCode();
      }

      public boolean removeAll(Collection<?> coll) {
         if (!this.parent.isEmpty() && !coll.isEmpty()) {
            boolean modified = false;

            for(Object current : coll) {
               modified |= this.remove(current);
            }

            return modified;
         } else {
            return false;
         }
      }

      public boolean removeIf(Predicate<? super E> filter) {
         if (!this.parent.isEmpty() && !Objects.isNull(filter)) {
            boolean modified = false;
            Iterator<?> it = this.iterator();

            while(it.hasNext()) {
               E e = (E)it.next();
               if (filter.test(e)) {
                  it.remove();
                  modified = true;
               }
            }

            return modified;
         } else {
            return false;
         }
      }

      public boolean retainAll(Collection<?> coll) {
         if (this.parent.isEmpty()) {
            return false;
         } else if (coll.isEmpty()) {
            this.parent.clear();
            return true;
         } else {
            boolean modified = false;
            Iterator<E> it = this.iterator();

            while(it.hasNext()) {
               if (!coll.contains(it.next())) {
                  it.remove();
                  modified = true;
               }
            }

            return modified;
         }
      }
   }
}
