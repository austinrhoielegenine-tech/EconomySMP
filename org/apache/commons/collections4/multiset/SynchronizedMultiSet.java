package org.apache.commons.collections4.multiset;

import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.SynchronizedCollection;

public class SynchronizedMultiSet<E> extends SynchronizedCollection<E> implements MultiSet<E> {
   private static final long serialVersionUID = 20150629L;

   public static <E> SynchronizedMultiSet<E> synchronizedMultiSet(MultiSet<E> multiset) {
      return new SynchronizedMultiSet<E>(multiset);
   }

   protected SynchronizedMultiSet(MultiSet<E> multiset) {
      super(multiset);
   }

   protected SynchronizedMultiSet(MultiSet<E> multiset, Object lock) {
      super(multiset, lock);
   }

   public int add(E object, int count) {
      synchronized(this.lock) {
         return this.decorated().add(object, count);
      }
   }

   protected MultiSet<E> decorated() {
      return (MultiSet)super.decorated();
   }

   public Set<MultiSet.Entry<E>> entrySet() {
      synchronized(this.lock) {
         Set<MultiSet.Entry<E>> set = this.decorated().entrySet();
         return new SynchronizedSet<MultiSet.Entry<E>>(set, this.lock);
      }
   }

   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else {
         synchronized(this.lock) {
            return this.decorated().equals(object);
         }
      }
   }

   public int getCount(Object object) {
      synchronized(this.lock) {
         return this.decorated().getCount(object);
      }
   }

   public int hashCode() {
      synchronized(this.lock) {
         return this.decorated().hashCode();
      }
   }

   public int remove(Object object, int count) {
      synchronized(this.lock) {
         return this.decorated().remove(object, count);
      }
   }

   public int setCount(E object, int count) {
      synchronized(this.lock) {
         return this.decorated().setCount(object, count);
      }
   }

   public Set<E> uniqueSet() {
      synchronized(this.lock) {
         Set<E> set = this.decorated().uniqueSet();
         return new SynchronizedSet<E>(set, this.lock);
      }
   }

   static class SynchronizedSet<T> extends SynchronizedCollection<T> implements Set<T> {
      private static final long serialVersionUID = 20150629L;

      SynchronizedSet(Set<T> set, Object lock) {
         super(set, lock);
      }
   }
}
