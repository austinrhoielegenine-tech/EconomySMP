package org.apache.commons.collections4.set;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.list.UnmodifiableList;

public class CompositeSet<E> implements Set<E>, Serializable {
   private static final long serialVersionUID = 5185069727540378940L;
   private SetMutator<E> mutator;
   private final List<Set<E>> all = new ArrayList();

   public CompositeSet() {
   }

   public CompositeSet(Set<E> set) {
      this.addComposited(set);
   }

   public CompositeSet(Set<E>... sets) {
      this.addComposited(sets);
   }

   public boolean add(E obj) {
      if (this.mutator == null) {
         throw new UnsupportedOperationException("add() is not supported on CompositeSet without a SetMutator strategy");
      } else {
         return this.mutator.add(this, this.all, obj);
      }
   }

   public boolean addAll(Collection<? extends E> coll) {
      if (this.mutator == null) {
         throw new UnsupportedOperationException("addAll() is not supported on CompositeSet without a SetMutator strategy");
      } else {
         return this.mutator.addAll(this, this.all, coll);
      }
   }

   public synchronized void addComposited(Set<E> set) {
      if (set != null) {
         for(Set<E> existingSet : this.getSets()) {
            Collection<E> intersects = CollectionUtils.<E>intersection(existingSet, set);
            if (!intersects.isEmpty()) {
               if (this.mutator == null) {
                  throw new UnsupportedOperationException("Collision adding composited set with no SetMutator set");
               }

               this.getMutator().resolveCollision(this, existingSet, set, intersects);
               if (!CollectionUtils.intersection(existingSet, set).isEmpty()) {
                  throw new IllegalArgumentException("Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
               }
            }
         }

         this.all.add(set);
      }

   }

   public void addComposited(Set<E>... sets) {
      if (sets != null) {
         for(Set<E> set : sets) {
            this.addComposited(set);
         }
      }

   }

   public void addComposited(Set<E> set1, Set<E> set2) {
      this.addComposited(set1);
      this.addComposited(set2);
   }

   public void clear() {
      for(Collection<E> coll : this.all) {
         coll.clear();
      }

   }

   public boolean contains(Object obj) {
      for(Set<E> item : this.all) {
         if (item.contains(obj)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsAll(Collection<?> coll) {
      if (coll == null) {
         return false;
      } else {
         for(Object item : coll) {
            if (!this.contains(item)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Set)) {
         return false;
      } else {
         Set<?> set = (Set)obj;
         return set.size() == this.size() && set.containsAll(this);
      }
   }

   protected SetMutator<E> getMutator() {
      return this.mutator;
   }

   public List<Set<E>> getSets() {
      return UnmodifiableList.<Set<E>>unmodifiableList(this.all);
   }

   public int hashCode() {
      int code = 0;

      for(E e : this) {
         code += e == null ? 0 : e.hashCode();
      }

      return code;
   }

   public boolean isEmpty() {
      for(Set<E> item : this.all) {
         if (!item.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public Iterator<E> iterator() {
      if (this.all.isEmpty()) {
         return EmptyIterator.<E>emptyIterator();
      } else {
         IteratorChain<E> chain = new IteratorChain<E>();
         this.all.forEach((item) -> chain.addIterator(item.iterator()));
         return chain;
      }
   }

   public boolean remove(Object obj) {
      for(Set<E> set : this.getSets()) {
         if (set.contains(obj)) {
            return set.remove(obj);
         }
      }

      return false;
   }

   public boolean removeAll(Collection<?> coll) {
      if (CollectionUtils.isEmpty(coll)) {
         return false;
      } else {
         boolean changed = false;

         for(Collection<E> item : this.all) {
            changed |= item.removeAll(coll);
         }

         return changed;
      }
   }

   public void removeComposited(Set<E> set) {
      this.all.remove(set);
   }

   public boolean removeIf(Predicate<? super E> filter) {
      if (Objects.isNull(filter)) {
         return false;
      } else {
         boolean changed = false;

         for(Collection<E> item : this.all) {
            changed |= item.removeIf(filter);
         }

         return changed;
      }
   }

   public boolean retainAll(Collection<?> coll) {
      boolean changed = false;

      for(Collection<E> item : this.all) {
         changed |= item.retainAll(coll);
      }

      return changed;
   }

   public void setMutator(SetMutator<E> mutator) {
      this.mutator = mutator;
   }

   public int size() {
      int size = 0;

      for(Set<E> item : this.all) {
         size += item.size();
      }

      return size;
   }

   public Object[] toArray() {
      Object[] result = new Object[this.size()];
      int i = 0;

      for(Iterator<E> it = this.iterator(); it.hasNext(); ++i) {
         result[i] = it.next();
      }

      return result;
   }

   public <T> T[] toArray(T[] array) {
      int size = this.size();
      Object[] result = null;
      if (array.length >= size) {
         result = array;
      } else {
         result = Array.newInstance(array.getClass().getComponentType(), size);
      }

      int offset = 0;

      for(Collection<E> item : this.all) {
         for(E e : item) {
            result[offset++] = e;
         }
      }

      if (result.length > size) {
         result[size] = null;
      }

      return (T[])result;
   }

   public Set<E> toSet() {
      return new HashSet(this);
   }

   public interface SetMutator<E> extends Serializable {
      boolean add(CompositeSet<E> var1, List<Set<E>> var2, E var3);

      boolean addAll(CompositeSet<E> var1, List<Set<E>> var2, Collection<? extends E> var3);

      void resolveCollision(CompositeSet<E> var1, Set<E> var2, Set<E> var3, Collection<E> var4);
   }
}
