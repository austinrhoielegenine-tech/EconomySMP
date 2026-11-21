package org.apache.commons.collections4.multimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntry;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.commons.collections4.multiset.AbstractMultiSet;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;

public abstract class AbstractMultiValuedMap<K, V> implements MultiValuedMap<K, V> {
   private transient Collection<V> valuesView;
   private transient AbstractMultiValuedMap<K, V>.EntryValues entryValuesView;
   private transient MultiSet<K> keysMultiSetView;
   private transient AbstractMultiValuedMap<K, V>.AsMap asMapView;
   private transient Map<K, Collection<V>> map;

   protected AbstractMultiValuedMap() {
   }

   protected AbstractMultiValuedMap(Map<K, ? extends Collection<V>> map) {
      this.map = (Map)Objects.requireNonNull(map, "map");
   }

   public Map<K, Collection<V>> asMap() {
      return this.asMapView != null ? this.asMapView : (this.asMapView = new AsMap(this.map));
   }

   public void clear() {
      this.getMap().clear();
   }

   public boolean containsKey(Object key) {
      return this.getMap().containsKey(key);
   }

   public boolean containsMapping(Object key, Object value) {
      Collection<V> coll = (Collection)this.getMap().get(key);
      return coll != null && coll.contains(value);
   }

   public boolean containsValue(Object value) {
      return this.values().contains(value);
   }

   protected abstract Collection<V> createCollection();

   protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      int entrySize = in.readInt();

      for(int i = 0; i < entrySize; ++i) {
         K key = (K)in.readObject();
         Collection<V> values = this.get(key);
         int valueSize = in.readInt();

         for(int j = 0; j < valueSize; ++j) {
            V value = (V)in.readObject();
            values.add(value);
         }
      }

   }

   protected void doWriteObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.map.size());

      for(Map.Entry<K, Collection<V>> entry : this.map.entrySet()) {
         out.writeObject(entry.getKey());
         out.writeInt(((Collection)entry.getValue()).size());

         for(V value : (Collection)entry.getValue()) {
            out.writeObject(value);
         }
      }

   }

   public Collection<Map.Entry<K, V>> entries() {
      return this.entryValuesView != null ? this.entryValuesView : (this.entryValuesView = new EntryValues());
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj instanceof MultiValuedMap ? this.asMap().equals(((MultiValuedMap)obj).asMap()) : false;
      }
   }

   public Collection<V> get(K key) {
      return this.wrappedCollection(key);
   }

   protected Map<K, ? extends Collection<V>> getMap() {
      return this.map;
   }

   public int hashCode() {
      return this.getMap().hashCode();
   }

   public boolean isEmpty() {
      return this.getMap().isEmpty();
   }

   public MultiSet<K> keys() {
      if (this.keysMultiSetView == null) {
         this.keysMultiSetView = UnmodifiableMultiSet.<K>unmodifiableMultiSet(new KeysMultiSet());
      }

      return this.keysMultiSetView;
   }

   public Set<K> keySet() {
      return this.getMap().keySet();
   }

   public MapIterator<K, V> mapIterator() {
      return (MapIterator<K, V>)(this.isEmpty() ? EmptyMapIterator.emptyMapIterator() : new MultiValuedMapIterator());
   }

   public boolean put(K key, V value) {
      Collection<V> coll = (Collection)this.getMap().get(key);
      if (coll == null) {
         coll = this.createCollection();
         if (coll.add(value)) {
            this.map.put(key, coll);
            return true;
         } else {
            return false;
         }
      } else {
         return coll.add(value);
      }
   }

   public boolean putAll(K key, Iterable<? extends V> values) {
      Objects.requireNonNull(values, "values");
      if (values instanceof Collection) {
         Collection<? extends V> valueCollection = (Collection)values;
         return !valueCollection.isEmpty() && this.get(key).addAll(valueCollection);
      } else {
         Iterator<? extends V> it = values.iterator();
         return it.hasNext() && CollectionUtils.addAll(this.get(key), it);
      }
   }

   public boolean putAll(Map<? extends K, ? extends V> map) {
      Objects.requireNonNull(map, "map");
      boolean changed = false;

      for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
         changed |= this.put(entry.getKey(), entry.getValue());
      }

      return changed;
   }

   public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
      Objects.requireNonNull(map, "map");
      boolean changed = false;

      for(Map.Entry<? extends K, ? extends V> entry : map.entries()) {
         changed |= this.put(entry.getKey(), entry.getValue());
      }

      return changed;
   }

   public Collection<V> remove(Object key) {
      return CollectionUtils.<V>emptyIfNull((Collection)this.getMap().remove(key));
   }

   public boolean removeMapping(Object key, Object value) {
      Collection<V> coll = (Collection)this.getMap().get(key);
      if (coll == null) {
         return false;
      } else {
         boolean changed = coll.remove(value);
         if (coll.isEmpty()) {
            this.getMap().remove(key);
         }

         return changed;
      }
   }

   protected void setMap(Map<K, ? extends Collection<V>> map) {
      this.map = map;
   }

   public int size() {
      int size = 0;

      for(Collection<V> col : this.getMap().values()) {
         size += col.size();
      }

      return size;
   }

   public String toString() {
      return this.getMap().toString();
   }

   public Collection<V> values() {
      Collection<V> vs = this.valuesView;
      return vs != null ? vs : (this.valuesView = new Values());
   }

   Collection<V> wrappedCollection(K key) {
      return new WrappedCollection(key);
   }

   private final class AsMap extends AbstractMap<K, Collection<V>> {
      final transient Map<K, Collection<V>> map;

      AsMap(final Map<K, Collection<V>> map) {
         this.map = map;
      }

      public void clear() {
         AbstractMultiValuedMap.this.clear();
      }

      public boolean containsKey(Object key) {
         return this.map.containsKey(key);
      }

      public Set<Map.Entry<K, Collection<V>>> entrySet() {
         return new AsMapEntrySet();
      }

      public boolean equals(Object object) {
         return this == object || this.map.equals(object);
      }

      public Collection<V> get(Object key) {
         Collection<V> collection = (Collection)this.map.get(key);
         return collection == null ? null : AbstractMultiValuedMap.this.wrappedCollection(key);
      }

      public int hashCode() {
         return this.map.hashCode();
      }

      public Set<K> keySet() {
         return AbstractMultiValuedMap.this.keySet();
      }

      public Collection<V> remove(Object key) {
         Collection<V> collection = (Collection)this.map.remove(key);
         if (collection == null) {
            return null;
         } else {
            Collection<V> output = AbstractMultiValuedMap.this.createCollection();
            output.addAll(collection);
            collection.clear();
            return output;
         }
      }

      public int size() {
         return this.map.size();
      }

      public String toString() {
         return this.map.toString();
      }

      final class AsMapEntrySet extends AbstractSet<Map.Entry<K, Collection<V>>> {
         public void clear() {
            AsMap.this.clear();
         }

         public boolean contains(Object o) {
            return AsMap.this.map.entrySet().contains(o);
         }

         public Iterator<Map.Entry<K, Collection<V>>> iterator() {
            return AsMap.this.new AsMapEntrySetIterator(AsMap.this.map.entrySet().iterator());
         }

         public boolean remove(Object o) {
            if (!this.contains(o)) {
               return false;
            } else {
               Map.Entry<?, ?> entry = (Map.Entry)o;
               AbstractMultiValuedMap.this.remove(entry.getKey());
               return true;
            }
         }

         public int size() {
            return AsMap.this.size();
         }
      }

      final class AsMapEntrySetIterator extends AbstractIteratorDecorator<Map.Entry<K, Collection<V>>> {
         AsMapEntrySetIterator(final Iterator<Map.Entry<K, Collection<V>>> iterator) {
            super(iterator);
         }

         public Map.Entry<K, Collection<V>> next() {
            Map.Entry<K, Collection<V>> entry = (Map.Entry)super.next();
            K key = (K)entry.getKey();
            return new UnmodifiableMapEntry<K, Collection<V>>(key, AbstractMultiValuedMap.this.wrappedCollection(key));
         }
      }
   }

   private final class EntryValues extends AbstractCollection<Map.Entry<K, V>> {
      private EntryValues() {
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return new LazyIteratorChain<Map.Entry<K, V>>() {
            final Collection<K> keysCol = new ArrayList(AbstractMultiValuedMap.this.getMap().keySet());
            final Iterator<K> keyIterator;

            {
               this.keyIterator = this.keysCol.iterator();
            }

            protected Iterator<? extends Map.Entry<K, V>> nextIterator(int count) {
               if (!this.keyIterator.hasNext()) {
                  return null;
               } else {
                  K key = (K)this.keyIterator.next();
                  Transformer<V, Map.Entry<K, V>> entryTransformer = (input) -> AbstractMultiValuedMap.this.new MultiValuedMapEntry(key, input);
                  return new TransformIterator(AbstractMultiValuedMap.this.new ValuesIterator(key), entryTransformer);
               }
            }
         };
      }

      public int size() {
         return AbstractMultiValuedMap.this.size();
      }
   }

   private final class KeysMultiSet extends AbstractMultiSet<K> {
      private KeysMultiSet() {
      }

      public boolean contains(Object o) {
         return AbstractMultiValuedMap.this.getMap().containsKey(o);
      }

      protected Iterator<MultiSet.Entry<K>> createEntrySetIterator() {
         AbstractMultiValuedMap<K, V>.KeysMultiSet.MapEntryTransformer transformer = new MapEntryTransformer();
         return IteratorUtils.transformedIterator(AbstractMultiValuedMap.this.map.entrySet().iterator(), transformer);
      }

      public int getCount(Object object) {
         int count = 0;
         Collection<V> col = (Collection)AbstractMultiValuedMap.this.getMap().get(object);
         if (col != null) {
            count = col.size();
         }

         return count;
      }

      public boolean isEmpty() {
         return AbstractMultiValuedMap.this.getMap().isEmpty();
      }

      public int size() {
         return AbstractMultiValuedMap.this.size();
      }

      protected int uniqueElements() {
         return AbstractMultiValuedMap.this.getMap().size();
      }

      private final class MapEntryTransformer implements Transformer<Map.Entry<K, Collection<V>>, MultiSet.Entry<K>> {
         private MapEntryTransformer() {
         }

         public MultiSet.Entry<K> transform(final Map.Entry<K, Collection<V>> mapEntry) {
            return new AbstractMultiSet.AbstractEntry<K>() {
               public int getCount() {
                  return ((Collection)mapEntry.getValue()).size();
               }

               public K getElement() {
                  return (K)mapEntry.getKey();
               }
            };
         }
      }
   }

   private final class MultiValuedMapEntry extends AbstractMapEntry<K, V> {
      MultiValuedMapEntry(final K key, final V value) {
         super(key, value);
      }

      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }
   }

   private final class MultiValuedMapIterator implements MapIterator<K, V> {
      private final Iterator<Map.Entry<K, V>> it = AbstractMultiValuedMap.this.entries().iterator();
      private Map.Entry<K, V> current;

      MultiValuedMapIterator() {
      }

      public K getKey() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return (K)this.current.getKey();
         }
      }

      public V getValue() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return (V)this.current.getValue();
         }
      }

      public boolean hasNext() {
         return this.it.hasNext();
      }

      public K next() {
         this.current = (Map.Entry)this.it.next();
         return (K)this.current.getKey();
      }

      public void remove() {
         this.it.remove();
      }

      public V setValue(V value) {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return (V)this.current.setValue(value);
         }
      }
   }

   private final class Values extends AbstractCollection<V> {
      private Values() {
      }

      public void clear() {
         AbstractMultiValuedMap.this.clear();
      }

      public Iterator<V> iterator() {
         IteratorChain<V> chain = new IteratorChain<V>();

         for(K k : AbstractMultiValuedMap.this.keySet()) {
            chain.addIterator(AbstractMultiValuedMap.this.new ValuesIterator(k));
         }

         return chain;
      }

      public int size() {
         return AbstractMultiValuedMap.this.size();
      }
   }

   private final class ValuesIterator implements Iterator<V> {
      private final Object key;
      private final Collection<V> values;
      private final Iterator<V> iterator;

      ValuesIterator(final Object key) {
         this.key = key;
         this.values = (Collection)AbstractMultiValuedMap.this.getMap().get(key);
         this.iterator = this.values.iterator();
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public V next() {
         return (V)this.iterator.next();
      }

      public void remove() {
         this.iterator.remove();
         if (this.values.isEmpty()) {
            AbstractMultiValuedMap.this.remove(this.key);
         }

      }
   }

   class WrappedCollection implements Collection<V> {
      protected final K key;

      WrappedCollection(final K key) {
         this.key = key;
      }

      public boolean add(V value) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            coll = AbstractMultiValuedMap.this.createCollection();
            AbstractMultiValuedMap.this.map.put(this.key, coll);
         }

         return coll.add(value);
      }

      public boolean addAll(Collection<? extends V> other) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            coll = AbstractMultiValuedMap.this.createCollection();
            AbstractMultiValuedMap.this.map.put(this.key, coll);
         }

         return coll.addAll(other);
      }

      public void clear() {
         Collection<V> coll = this.getMapping();
         if (coll != null) {
            coll.clear();
            AbstractMultiValuedMap.this.remove(this.key);
         }

      }

      public boolean contains(Object obj) {
         Collection<V> coll = this.getMapping();
         return coll != null && coll.contains(obj);
      }

      public boolean containsAll(Collection<?> other) {
         Collection<V> coll = this.getMapping();
         return coll != null && coll.containsAll(other);
      }

      protected Collection<V> getMapping() {
         return (Collection)AbstractMultiValuedMap.this.getMap().get(this.key);
      }

      public boolean isEmpty() {
         Collection<V> coll = this.getMapping();
         return coll == null || coll.isEmpty();
      }

      public Iterator<V> iterator() {
         Collection<V> coll = this.getMapping();
         return (Iterator<V>)(coll == null ? IteratorUtils.EMPTY_ITERATOR : AbstractMultiValuedMap.this.new ValuesIterator(this.key));
      }

      public boolean remove(Object item) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            return false;
         } else {
            boolean result = coll.remove(item);
            if (coll.isEmpty()) {
               AbstractMultiValuedMap.this.remove(this.key);
            }

            return result;
         }
      }

      public boolean removeAll(Collection<?> c) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            return false;
         } else {
            boolean result = coll.removeAll(c);
            if (coll.isEmpty()) {
               AbstractMultiValuedMap.this.remove(this.key);
            }

            return result;
         }
      }

      public boolean retainAll(Collection<?> c) {
         Collection<V> coll = this.getMapping();
         if (coll == null) {
            return false;
         } else {
            boolean result = coll.retainAll(c);
            if (coll.isEmpty()) {
               AbstractMultiValuedMap.this.remove(this.key);
            }

            return result;
         }
      }

      public int size() {
         Collection<V> coll = this.getMapping();
         return coll == null ? 0 : coll.size();
      }

      public Object[] toArray() {
         Collection<V> coll = this.getMapping();
         return coll == null ? CollectionUtils.EMPTY_COLLECTION.toArray() : coll.toArray();
      }

      public <T> T[] toArray(T[] a) {
         Collection<V> coll = this.getMapping();
         return (T[])(coll == null ? CollectionUtils.EMPTY_COLLECTION.toArray(a) : coll.toArray(a));
      }

      public String toString() {
         Collection<V> coll = this.getMapping();
         return coll == null ? CollectionUtils.EMPTY_COLLECTION.toString() : coll.toString();
      }
   }
}
