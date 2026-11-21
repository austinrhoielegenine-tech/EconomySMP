package org.apache.commons.collections4.queue;

import java.util.Queue;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

public abstract class AbstractQueueDecorator<E> extends AbstractCollectionDecorator<E> implements Queue<E> {
   private static final long serialVersionUID = -2629815475789577029L;

   protected AbstractQueueDecorator() {
   }

   protected AbstractQueueDecorator(Queue<E> queue) {
      super(queue);
   }

   protected Queue<E> decorated() {
      return (Queue)super.decorated();
   }

   public E element() {
      return (E)this.decorated().element();
   }

   public boolean offer(E obj) {
      return this.decorated().offer(obj);
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
