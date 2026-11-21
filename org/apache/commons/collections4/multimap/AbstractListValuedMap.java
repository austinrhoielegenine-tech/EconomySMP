package org.apache.commons.collections4.multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.ListValuedMap;

public abstract class AbstractListValuedMap<K, V> extends AbstractMultiValuedMap<K, V> implements ListValuedMap<K, V> {
   protected AbstractListValuedMap() {
   }

   protected AbstractListValuedMap(Map<K, ? extends List<V>> map) {
      super(map);
   }

   protected abstract List<V> createCollection();

   public List<V> get(K key) {
      return this.wrappedCollection(key);
   }

   protected Map<K, List<V>> getMap() {
      return super.getMap();
   }

   public List<V> remove(Object key) {
      return ListUtils.<V>emptyIfNull((List)this.getMap().remove(key));
   }

   List<V> wrappedCollection(K key) {
      return new WrappedList(key);
   }

   private final class ValuesListIterator implements ListIterator<V> {
      private final K key;
      private List<V> values;
      private ListIterator<V> iterator;

      ValuesListIterator(final K key) {
         this.key = key;
         this.values = ListUtils.<V>emptyIfNull((List)AbstractListValuedMap.this.getMap().get(key));
         this.iterator = this.values.listIterator();
      }

      ValuesListIterator(final K key, final int index) {
         this.key = key;
         this.values = ListUtils.<V>emptyIfNull((List)AbstractListValuedMap.this.getMap().get(key));
         this.iterator = this.values.listIterator(index);
      }

      public void add(V value) {
         if (AbstractListValuedMap.this.getMap().get(this.key) == null) {
            List<V> list = AbstractListValuedMap.this.createCollection();
            AbstractListValuedMap.this.getMap().put(this.key, list);
            this.values = list;
            this.iterator = list.listIterator();
         }

         this.iterator.add(value);
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public boolean hasPrevious() {
         return this.iterator.hasPrevious();
      }

      public V next() {
         return (V)this.iterator.next();
      }

      public int nextIndex() {
         return this.iterator.nextIndex();
      }

      public V previous() {
         return (V)this.iterator.previous();
      }

      public int previousIndex() {
         return this.iterator.previousIndex();
      }

      public void remove() {
         this.iterator.remove();
         if (this.values.isEmpty()) {
            AbstractListValuedMap.this.getMap().remove(this.key);
         }

      }

      public void set(V value) {
         this.iterator.set(value);
      }
   }

   private final class WrappedList extends AbstractMultiValuedMap<K, V>.WrappedCollection implements List<V> {
      WrappedList(final K key) {
         super(key);
      }

      public void add(int index, V value) {
         List<V> list = this.getMapping();
         if (list == null) {
            list = AbstractListValuedMap.this.createCollection();
            AbstractListValuedMap.this.getMap().put(this.key, list);
         }

         list.add(index, value);
      }

      public boolean addAll(int index, Collection<? extends V> c) {
         List<V> list = this.getMapping();
         if (list == null) {
            list = AbstractListValuedMap.this.createCollection();
            boolean changed = list.addAll(index, c);
            if (changed) {
               AbstractListValuedMap.this.getMap().put(this.key, list);
            }

            return changed;
         } else {
            return list.addAll(index, c);
         }
      }

      public boolean equals(Object other) {
         List<V> list = this.getMapping();
         if (list == null) {
            return Collections.emptyList().equals(other);
         } else if (!(other instanceof List)) {
            return false;
         } else {
            List<?> otherList = (List)other;
            return ListUtils.isEqualList(list, otherList);
         }
      }

      public V get(int index) {
         List<V> list = ListUtils.<V>emptyIfNull(this.getMapping());
         return (V)list.get(index);
      }

      protected List<V> getMapping() {
         return (List)AbstractListValuedMap.this.getMap().get(this.key);
      }

      public int hashCode() {
         List<V> list = this.getMapping();
         return ListUtils.hashCodeForList(list);
      }

      public int indexOf(Object o) {
         List<V> list = ListUtils.<V>emptyIfNull(this.getMapping());
         return list.indexOf(o);
      }

      public int lastIndexOf(Object o) {
         List<V> list = ListUtils.<V>emptyIfNull(this.getMapping());
         return list.lastIndexOf(o);
      }

      public ListIterator<V> listIterator() {
         return AbstractListValuedMap.this.new ValuesListIterator(this.key);
      }

      public ListIterator<V> listIterator(int index) {
         return AbstractListValuedMap.this.new ValuesListIterator(this.key, index);
      }

      public V remove(int index) {
         List<V> list = ListUtils.<V>emptyIfNull(this.getMapping());
         V value = (V)list.remove(index);
         if (list.isEmpty()) {
            AbstractListValuedMap.this.remove(this.key);
         }

         return value;
      }

      public V set(int index, V value) {
         List<V> list = ListUtils.<V>emptyIfNull(this.getMapping());
         return (V)list.set(index, value);
      }

      public List<V> subList(int fromIndex, int toIndex) {
         List<V> list = ListUtils.<V>emptyIfNull(this.getMapping());
         return list.subList(fromIndex, toIndex);
      }
   }
}
