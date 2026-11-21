package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections4.SortedBag;

public final class CollectionSortedBag<E> extends AbstractSortedBagDecorator<E> {
   private static final long serialVersionUID = -2560033712679053143L;

   public static <E> SortedBag<E> collectionSortedBag(SortedBag<E> bag) {
      return new CollectionSortedBag<E>(bag);
   }

   public CollectionSortedBag(SortedBag<E> bag) {
      super(bag);
   }

   public boolean add(E object) {
      return this.add(object, 1);
   }

   public boolean add(E object, int count) {
      this.decorated().add(object, count);
      return true;
   }

   public boolean addAll(Collection<? extends E> coll) {
      boolean changed = false;

      for(E current : coll) {
         boolean added = this.add(current, 1);
         changed = changed || added;
      }

      return changed;
   }

   public boolean containsAll(Collection<?> coll) {
      return coll.stream().allMatch(this::contains);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.setCollection((Collection)in.readObject());
   }

   public boolean remove(Object object) {
      return this.remove(object, 1);
   }

   public boolean removeAll(Collection<?> coll) {
      if (coll == null) {
         return this.decorated().removeAll((Collection)null);
      } else {
         boolean result = false;

         for(Object obj : coll) {
            boolean changed = this.remove(obj, this.getCount(obj));
            result = result || changed;
         }

         return result;
      }
   }

   public boolean retainAll(Collection<?> coll) {
      if (coll != null) {
         boolean modified = false;
         Iterator<E> e = this.iterator();

         while(e.hasNext()) {
            if (!coll.contains(e.next())) {
               e.remove();
               modified = true;
            }
         }

         return modified;
      } else {
         return this.decorated().retainAll((Collection)null);
      }
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.decorated());
   }
}
