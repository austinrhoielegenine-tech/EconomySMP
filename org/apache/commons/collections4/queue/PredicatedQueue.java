package org.apache.commons.collections4.queue;

import java.util.Queue;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;

public class PredicatedQueue<E> extends PredicatedCollection<E> implements Queue<E> {
   private static final long serialVersionUID = 2307609000539943581L;

   public static <E> PredicatedQueue<E> predicatedQueue(Queue<E> Queue, Predicate<? super E> predicate) {
      return new PredicatedQueue<E>(Queue, predicate);
   }

   protected PredicatedQueue(Queue<E> queue, Predicate<? super E> predicate) {
      super(queue, predicate);
   }

   protected Queue<E> decorated() {
      return (Queue)super.decorated();
   }

   public E element() {
      return (E)this.decorated().element();
   }

   public boolean offer(E object) {
      this.validate(object);
      return this.decorated().offer(object);
   }

   public E peek() {
      return (E)this.decorated().peek();
   }

   public E poll() {
      return (E)this.decorated().poll();
   }

   public E remove() {
      return (E)this.decorated().remove();
   }
}
