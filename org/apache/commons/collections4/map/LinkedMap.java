package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.list.UnmodifiableList;

public class LinkedMap<K, V> extends AbstractLinkedMap<K, V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 9077234323521161066L;

   public LinkedMap() {
      super(16, 0.75F, 12);
   }

   public LinkedMap(int initialCapacity) {
      super(initialCapacity);
   }

   public LinkedMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public LinkedMap(Map<? extends K, ? extends V> map) {
      super(map);
   }

   public List<K> asList() {
      return new LinkedMapList<K>(this);
   }

   public LinkedMap<K, V> clone() {
      return (LinkedMap)super.clone();
   }

   public K get(int index) {
      return (K)this.getEntry(index).getKey();
   }

   public V getValue(int index) {
      return (V)this.getEntry(index).getValue();
   }

   public int indexOf(Object key) {
      key = this.convertKey(key);
      int i = 0;

      for(AbstractLinkedMap.LinkEntry<K, V> entry = this.header.after; entry != this.header; ++i) {
         if (this.isEqualKey(key, entry.key)) {
            return i;
         }

         entry = entry.after;
      }

      return -1;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.doReadObject(in);
   }

   public V remove(int index) {
      return (V)this.remove(this.get(index));
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      this.doWriteObject(out);
   }

   static class LinkedMapList<K> extends AbstractList<K> {
      private final LinkedMap<K, ?> parent;

      LinkedMapList(LinkedMap<K, ?> parent) {
         this.parent = parent;
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public boolean contains(Object obj) {
         return this.parent.containsKey(obj);
      }

      public boolean containsAll(Collection<?> coll) {
         return this.parent.keySet().containsAll(coll);
      }

      public K get(int index) {
         return this.parent.get(index);
      }

      public int indexOf(Object obj) {
         return this.parent.indexOf(obj);
      }

      public Iterator<K> iterator() {
         return UnmodifiableIterator.<K>unmodifiableIterator(this.parent.keySet().iterator());
      }

      public int lastIndexOf(Object obj) {
         return this.parent.indexOf(obj);
      }

      public ListIterator<K> listIterator() {
         return UnmodifiableListIterator.<K>unmodifiableListIterator(super.listIterator());
      }

      public ListIterator<K> listIterator(int fromIndex) {
         return UnmodifiableListIterator.<K>unmodifiableListIterator(super.listIterator(fromIndex));
      }

      public K remove(int index) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object obj) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> coll) {
         throw new UnsupportedOperationException();
      }

      public boolean removeIf(Predicate<? super K> filter) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> coll) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.parent.size();
      }

      public List<K> subList(int fromIndexInclusive, int toIndexExclusive) {
         return UnmodifiableList.<K>unmodifiableList(super.subList(fromIndexInclusive, toIndexExclusive));
      }

      public Object[] toArray() {
         return this.parent.keySet().toArray();
      }

      public <T> T[] toArray(T[] array) {
         return (T[])this.parent.keySet().toArray(array);
      }
   }
}
