package org.apache.commons.collections4.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public class SynchronizedCollection<E> implements Collection<E>, Serializable {
   private static final long serialVersionUID = 2412805092710877986L;
   private final Collection<E> collection;
   protected final Object lock;

   public static <T> SynchronizedCollection<T> synchronizedCollection(Collection<T> coll) {
      return new SynchronizedCollection<T>(coll);
   }

   protected SynchronizedCollection(Collection<E> collection) {
      this.collection = (Collection)Objects.requireNonNull(collection, "collection");
      this.lock = this;
   }

   protected SynchronizedCollection(Collection<E> collection, Object lock) {
      this.collection = (Collection)Objects.requireNonNull(collection, "collection");
      this.lock = Objects.requireNonNull(lock, "lock");
   }

   public boolean add(E object) {
      synchronized(this.lock) {
         return this.decorated().add(object);
      }
   }

   public boolean addAll(Collection<? extends E> coll) {
      synchronized(this.lock) {
         return this.decorated().addAll(coll);
      }
   }

   public void clear() {
      synchronized(this.lock) {
         this.decorated().clear();
      }
   }

   public boolean contains(Object object) {
      synchronized(this.lock) {
         return this.decorated().contains(object);
      }
   }

   public boolean containsAll(Collection<?> coll) {
      synchronized(this.lock) {
         return this.decorated().containsAll(coll);
      }
   }

   protected Collection<E> decorated() {
      return this.collection;
   }

   public boolean equals(Object object) {
      synchronized(this.lock) {
         if (object == this) {
            return true;
         } else {
            return object == this || this.decorated().equals(object);
         }
      }
   }

   public int hashCode() {
      synchronized(this.lock) {
         return this.decorated().hashCode();
      }
   }

   public boolean isEmpty() {
      synchronized(this.lock) {
         return this.decorated().isEmpty();
      }
   }

   public Iterator<E> iterator() {
      return this.decorated().iterator();
   }

   public boolean remove(Object object) {
      synchronized(this.lock) {
         return this.decorated().remove(object);
      }
   }

   public boolean removeAll(Collection<?> coll) {
      synchronized(this.lock) {
         return this.decorated().removeAll(coll);
      }
   }

   public boolean removeIf(Predicate<? super E> filter) {
      synchronized(this.lock) {
         return this.decorated().removeIf(filter);
      }
   }

   public boolean retainAll(Collection<?> coll) {
      synchronized(this.lock) {
         return this.decorated().retainAll(coll);
      }
   }

   public int size() {
      synchronized(this.lock) {
         return this.decorated().size();
      }
   }

   public Object[] toArray() {
      synchronized(this.lock) {
         return this.decorated().toArray();
      }
   }

   public <T> T[] toArray(T[] object) {
      synchronized(this.lock) {
         return (T[])this.decorated().toArray(object);
      }
   }

   public String toString() {
      synchronized(this.lock) {
         return this.decorated().toString();
      }
   }
}
