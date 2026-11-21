package org.apache.commons.collections4.set;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public final class MapBackedSet<E, V> implements Set<E>, Serializable {
   private static final long serialVersionUID = 6723912213766056587L;
   private final Map<E, ? super V> map;
   private final V dummyValue;

   public static <E, V> MapBackedSet<E, V> mapBackedSet(Map<E, ? super V> map) {
      return mapBackedSet(map, (Object)null);
   }

   public static <E, V> MapBackedSet<E, V> mapBackedSet(Map<E, ? super V> map, V dummyValue) {
      return new MapBackedSet<E, V>(map, dummyValue);
   }

   private MapBackedSet(Map<E, ? super V> map, V dummyValue) {
      this.map = (Map)Objects.requireNonNull(map, "map");
      this.dummyValue = dummyValue;
   }

   public boolean add(E obj) {
      int size = this.map.size();
      this.map.put(obj, this.dummyValue);
      return this.map.size() != size;
   }

   public boolean addAll(Collection<? extends E> coll) {
      int size = this.map.size();

      for(E e : coll) {
         this.map.put(e, this.dummyValue);
      }

      return this.map.size() != size;
   }

   public void clear() {
      this.map.clear();
   }

   public boolean contains(Object obj) {
      return this.map.containsKey(obj);
   }

   public boolean containsAll(Collection<?> coll) {
      return this.map.keySet().containsAll(coll);
   }

   public boolean equals(Object obj) {
      return this.map.keySet().equals(obj);
   }

   public int hashCode() {
      return this.map.keySet().hashCode();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public Iterator<E> iterator() {
      return this.map.keySet().iterator();
   }

   public boolean remove(Object obj) {
      int size = this.map.size();
      this.map.remove(obj);
      return this.map.size() != size;
   }

   public boolean removeAll(Collection<?> coll) {
      return this.map.keySet().removeAll(coll);
   }

   public boolean removeIf(Predicate<? super E> filter) {
      return this.map.keySet().removeIf(filter);
   }

   public boolean retainAll(Collection<?> coll) {
      return this.map.keySet().retainAll(coll);
   }

   public int size() {
      return this.map.size();
   }

   public Object[] toArray() {
      return this.map.keySet().toArray();
   }

   public <T> T[] toArray(T[] array) {
      return (T[])this.map.keySet().toArray(array);
   }
}
