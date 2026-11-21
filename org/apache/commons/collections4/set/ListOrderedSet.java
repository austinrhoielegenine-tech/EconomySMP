package org.apache.commons.collections4.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.list.UnmodifiableList;

public class ListOrderedSet<E> extends AbstractSerializableSetDecorator<E> {
   private static final long serialVersionUID = -228664372470420141L;
   private final List<E> setOrder;

   public static <E> ListOrderedSet<E> listOrderedSet(List<E> list) {
      Objects.requireNonNull(list, "list");
      CollectionUtils.filter(list, UniquePredicate.uniquePredicate());
      Set<E> set = new HashSet(list);
      return new ListOrderedSet<E>(set, list);
   }

   public static <E> ListOrderedSet<E> listOrderedSet(Set<E> set) {
      return new ListOrderedSet<E>(set);
   }

   public static <E> ListOrderedSet<E> listOrderedSet(Set<E> set, List<E> list) {
      Objects.requireNonNull(set, "set");
      Objects.requireNonNull(list, "list");
      if (set.isEmpty() && list.isEmpty()) {
         return new ListOrderedSet<E>(set, list);
      } else {
         throw new IllegalArgumentException("Set and List must be empty");
      }
   }

   public ListOrderedSet() {
      super(new HashSet());
      this.setOrder = new ArrayList();
   }

   protected ListOrderedSet(Set<E> set) {
      super(set);
      this.setOrder = new ArrayList(set);
   }

   protected ListOrderedSet(Set<E> set, List<E> list) {
      super(set);
      this.setOrder = (List)Objects.requireNonNull(list, "list");
   }

   public boolean add(E object) {
      if (this.decorated().add(object)) {
         this.setOrder.add(object);
         return true;
      } else {
         return false;
      }
   }

   public void add(int index, E object) {
      if (!this.contains(object)) {
         this.decorated().add(object);
         this.setOrder.add(index, object);
      }

   }

   public boolean addAll(Collection<? extends E> coll) {
      boolean result = false;

      for(E e : coll) {
         result |= this.add(e);
      }

      return result;
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      boolean changed = false;
      List<E> toAdd = new ArrayList();

      for(E e : coll) {
         if (!this.contains(e)) {
            this.decorated().add(e);
            toAdd.add(e);
            changed = true;
         }
      }

      if (changed) {
         this.setOrder.addAll(index, toAdd);
      }

      return changed;
   }

   public List<E> asList() {
      return UnmodifiableList.<E>unmodifiableList(this.setOrder);
   }

   public void clear() {
      this.decorated().clear();
      this.setOrder.clear();
   }

   public E get(int index) {
      return (E)this.setOrder.get(index);
   }

   public int indexOf(Object object) {
      return this.setOrder.indexOf(object);
   }

   public OrderedIterator<E> iterator() {
      return new OrderedSetIterator<E>(this.setOrder.listIterator(), this.decorated());
   }

   public E remove(int index) {
      E obj = (E)this.setOrder.remove(index);
      this.remove(obj);
      return obj;
   }

   public boolean remove(Object object) {
      boolean result = this.decorated().remove(object);
      if (result) {
         this.setOrder.remove(object);
      }

      return result;
   }

   public boolean removeAll(Collection<?> coll) {
      boolean result = false;

      for(Object name : coll) {
         result |= this.remove(name);
      }

      return result;
   }

   public boolean removeIf(Predicate<? super E> filter) {
      if (Objects.isNull(filter)) {
         return false;
      } else {
         boolean result = this.decorated().removeIf(filter);
         if (result) {
            this.setOrder.removeIf(filter);
         }

         return result;
      }
   }

   public boolean retainAll(Collection<?> coll) {
      boolean result = this.decorated().retainAll(coll);
      if (!result) {
         return false;
      } else {
         if (this.decorated().isEmpty()) {
            this.setOrder.clear();
         } else {
            this.setOrder.removeIf((e) -> !this.decorated().contains(e));
         }

         return result;
      }
   }

   public Object[] toArray() {
      return this.setOrder.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return (T[])this.setOrder.toArray(a);
   }

   public String toString() {
      return this.setOrder.toString();
   }

   static class OrderedSetIterator<E> extends AbstractIteratorDecorator<E> implements OrderedIterator<E> {
      private final Collection<E> set;
      private E last;

      private OrderedSetIterator(ListIterator<E> iterator, Collection<E> set) {
         super(iterator);
         this.set = set;
      }

      public boolean hasPrevious() {
         return ((ListIterator)this.getIterator()).hasPrevious();
      }

      public E next() {
         this.last = (E)this.getIterator().next();
         return this.last;
      }

      public E previous() {
         this.last = (E)((ListIterator)this.getIterator()).previous();
         return this.last;
      }

      public void remove() {
         this.set.remove(this.last);
         this.getIterator().remove();
         this.last = null;
      }
   }
}
