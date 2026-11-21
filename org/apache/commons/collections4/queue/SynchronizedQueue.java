package org.apache.commons.collections4.queue;

import java.util.Queue;
import org.apache.commons.collections4.collection.SynchronizedCollection;

public class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E> {
   private static final long serialVersionUID = 1L;

   public static <E> SynchronizedQueue<E> synchronizedQueue(Queue<E> queue) {
      return new SynchronizedQueue<E>(queue);
   }

   protected SynchronizedQueue(Queue<E> queue) {
      super(queue);
   }

   protected SynchronizedQueue(Queue<E> queue, Object lock) {
      super(queue, lock);
   }

   protected Queue<E> decorated() {
      return (Queue)super.decorated();
   }

   public E element() {
      synchronized(this.lock) {
         return (E)this.decorated().element();
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

   public int hashCode() {
      synchronized(this.lock) {
         return this.decorated().hashCode();
      }
   }

   public boolean offer(E e) {
      synchronized(this.lock) {
         return this.decorated().offer(e);
      }
   }

   public E peek() {
      synchronized(this.lock) {
         return (E)this.decorated().peek();
      }
   }

   public E poll() {
      synchronized(this.lock) {
         return (E)this.decorated().poll();
      }
   }

   public E remove() {
      synchronized(this.lock) {
         return (E)this.decorated().remove();
      }
   }
}
