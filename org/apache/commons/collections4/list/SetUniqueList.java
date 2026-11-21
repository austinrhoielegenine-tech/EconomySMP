package org.apache.commons.collections4.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.set.UnmodifiableSet;

public class SetUniqueList<E> extends AbstractSerializableListDecorator<E> {
   private static final long serialVersionUID = 7196982186153478694L;
   private final Set<E> set;

   public static <E> SetUniqueList<E> setUniqueList(List<E> list) {
      Objects.requireNonNull(list, "list");
      if (list.isEmpty()) {
         return new SetUniqueList<E>(list, new HashSet());
      } else {
         List<E> temp = new ArrayList(list);
         list.clear();
         SetUniqueList<E> sl = new SetUniqueList<E>(list, new HashSet());
         sl.addAll(temp);
         return sl;
      }
   }

   protected SetUniqueList(List<E> list, Set<E> set) {
      super(list);
      this.set = (Set)Objects.requireNonNull(set, "set");
   }

   public boolean add(E object) {
      int sizeBefore = this.size();
      this.add(this.size(), object);
      return sizeBefore != this.size();
   }

   public void add(int index, E object) {
      if (!this.set.contains(object)) {
         this.set.add(object);
         super.add(index, object);
      }

   }

   public boolean addAll(Collection<? extends E> coll) {
      return this.addAll(this.size(), coll);
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      List<E> temp = new ArrayList();

      for(E e : coll) {
         if (this.set.add(e)) {
            temp.add(e);
         }
      }

      return super.addAll(index, temp);
   }

   public Set<E> asSet() {
      return UnmodifiableSet.<E>unmodifiableSet(this.set);
   }

   public void clear() {
      super.clear();
      this.set.clear();
   }

   public boolean contains(Object object) {
      return this.set.contains(object);
   }

   public boolean containsAll(Collection<?> coll) {
      return this.set.containsAll(coll);
   }

   protected Set<E> createSetBasedOnList(Set<E> set, List<E> list) {
      Set<E> subSet;
      if (set.getClass().equals(HashSet.class)) {
         subSet = new HashSet(list.size());
      } else {
         try {
            subSet = (Set)set.getClass().getDeclaredConstructor(set.getClass()).newInstance(set);
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var5) {
            subSet = new HashSet();
         }
      }

      subSet.addAll(list);
      return subSet;
   }

   public Iterator<E> iterator() {
      return new SetListIterator<E>(super.iterator(), this.set);
   }

   public ListIterator<E> listIterator() {
      return new SetListListIterator<E>(super.listIterator(), this.set);
   }

   public ListIterator<E> listIterator(int index) {
      return new SetListListIterator<E>(super.listIterator(index), this.set);
   }

   public E remove(int index) {
      E result = (E)super.remove(index);
      this.set.remove(result);
      return result;
   }

   public boolean remove(Object object) {
      boolean result = this.set.remove(object);
      if (result) {
         super.remove(object);
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
      boolean result = super.removeIf(filter);
      this.set.removeIf(filter);
      return result;
   }

   public boolean retainAll(Collection<?> coll) {
      boolean result = this.set.retainAll(coll);
      if (!result) {
         return false;
      } else {
         if (this.set.isEmpty()) {
            super.clear();
         } else {
            super.retainAll(this.set);
         }

         return result;
      }
   }

   public E set(int index, E object) {
      int pos = this.indexOf(object);
      E removed = (E)super.set(index, object);
      if (pos != -1 && pos != index) {
         super.remove(pos);
      }

      this.set.remove(removed);
      this.set.add(object);
      return removed;
   }

   public List<E> subList(int fromIndex, int toIndex) {
      List<E> superSubList = super.subList(fromIndex, toIndex);
      Set<E> subSet = this.createSetBasedOnList(this.set, superSubList);
      return ListUtils.<E>unmodifiableList(new SetUniqueList(superSubList, subSet));
   }

   static class SetListIterator<E> extends AbstractIteratorDecorator<E> {
      private final Set<E> set;
      private E last;

      protected SetListIterator(Iterator<E> it, Set<E> set) {
         super(it);
         this.set = set;
      }

      public E next() {
         this.last = (E)super.next();
         return this.last;
      }

      public void remove() {
         super.remove();
         this.set.remove(this.last);
         this.last = null;
      }
   }

   static class SetListListIterator<E> extends AbstractListIteratorDecorator<E> {
      private final Set<E> set;
      private E last;

      protected SetListListIterator(ListIterator<E> it, Set<E> set) {
         super(it);
         this.set = set;
      }

      public void add(E object) {
         if (!this.set.contains(object)) {
            super.add(object);
            this.set.add(object);
         }

      }

      public E next() {
         this.last = (E)super.next();
         return this.last;
      }

      public E previous() {
         this.last = (E)super.previous();
         return this.last;
      }

      public void remove() {
         super.remove();
         this.set.remove(this.last);
         this.last = null;
      }

      public void set(E object) {
         throw new UnsupportedOperationException("ListIterator does not support set");
      }
   }
}
