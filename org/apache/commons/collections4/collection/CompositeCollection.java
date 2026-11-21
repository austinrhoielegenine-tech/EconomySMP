package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.list.UnmodifiableList;

public class CompositeCollection<E> implements Collection<E>, Serializable {
   private static final long serialVersionUID = 8417515734108306801L;
   private CollectionMutator<E> mutator;
   private final List<Collection<E>> all = new ArrayList();

   public CompositeCollection() {
   }

   public CompositeCollection(Collection<E> compositeCollection) {
      this.addComposited(compositeCollection);
   }

   public CompositeCollection(Collection<E>... compositeCollections) {
      this.addComposited(compositeCollections);
   }

   public CompositeCollection(Collection<E> compositeCollection1, Collection<E> compositeCollection2) {
      this.addComposited(compositeCollection1, compositeCollection2);
   }

   public boolean add(E obj) {
      if (this.mutator == null) {
         throw new UnsupportedOperationException("add() is not supported on CompositeCollection without a CollectionMutator strategy");
      } else {
         return this.mutator.add(this, this.all, obj);
      }
   }

   public boolean addAll(Collection<? extends E> coll) {
      if (this.mutator == null) {
         throw new UnsupportedOperationException("addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
      } else {
         return this.mutator.addAll(this, this.all, coll);
      }
   }

   public void addComposited(Collection<E> compositeCollection) {
      if (compositeCollection != null) {
         this.all.add(compositeCollection);
      }

   }

   public void addComposited(Collection<E>... compositeCollections) {
      for(Collection<E> compositeCollection : compositeCollections) {
         if (compositeCollection != null) {
            this.all.add(compositeCollection);
         }
      }

   }

   public void addComposited(Collection<E> compositeCollection1, Collection<E> compositeCollection2) {
      if (compositeCollection1 != null) {
         this.all.add(compositeCollection1);
      }

      if (compositeCollection2 != null) {
         this.all.add(compositeCollection2);
      }

   }

   public void clear() {
      for(Collection<E> coll : this.all) {
         coll.clear();
      }

   }

   public boolean contains(Object obj) {
      for(Collection<E> item : this.all) {
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

   public List<Collection<E>> getCollections() {
      return UnmodifiableList.<Collection<E>>unmodifiableList(this.all);
   }

   protected CollectionMutator<E> getMutator() {
      return this.mutator;
   }

   public boolean isEmpty() {
      for(Collection<E> item : this.all) {
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
      if (this.mutator == null) {
         throw new UnsupportedOperationException("remove() is not supported on CompositeCollection without a CollectionMutator strategy");
      } else {
         return this.mutator.remove(this, this.all, obj);
      }
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

   public void removeComposited(Collection<E> coll) {
      this.all.remove(coll);
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
      if (coll != null) {
         for(Collection<E> item : this.all) {
            changed |= item.retainAll(coll);
         }
      }

      return changed;
   }

   public void setMutator(CollectionMutator<E> mutator) {
      this.mutator = mutator;
   }

   public int size() {
      int size = 0;

      for(Collection<E> item : this.all) {
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

   public Collection<E> toCollection() {
      return new ArrayList(this);
   }

   public interface CollectionMutator<E> extends Serializable {
      boolean add(CompositeCollection<E> var1, List<Collection<E>> var2, E var3);

      boolean addAll(CompositeCollection<E> var1, List<Collection<E>> var2, Collection<? extends E> var3);

      boolean remove(CompositeCollection<E> var1, List<Collection<E>> var2, Object var3);
   }
}
