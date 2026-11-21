package org.apache.commons.collections4.queue;

import java.util.Queue;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;

public class TransformedQueue<E> extends TransformedCollection<E> implements Queue<E> {
   private static final long serialVersionUID = -7901091318986132033L;

   public static <E> TransformedQueue<E> transformedQueue(Queue<E> queue, Transformer<? super E, ? extends E> transformer) {
      TransformedQueue<E> decorated = new TransformedQueue<E>(queue, transformer);
      if (!queue.isEmpty()) {
         E[] values = (E[])queue.toArray();
         queue.clear();

         for(E value : values) {
            decorated.decorated().add(transformer.apply(value));
         }
      }

      return decorated;
   }

   public static <E> TransformedQueue<E> transformingQueue(Queue<E> queue, Transformer<? super E, ? extends E> transformer) {
      return new TransformedQueue<E>(queue, transformer);
   }

   protected TransformedQueue(Queue<E> queue, Transformer<? super E, ? extends E> transformer) {
      super(queue, transformer);
   }

   public E element() {
      return (E)this.getQueue().element();
   }

   protected Queue<E> getQueue() {
      return (Queue)this.decorated();
   }

   public boolean offer(E obj) {
      return this.getQueue().offer(this.transform(obj));
   }

   public E peek() {
      return (E)this.getQueue().peek();
   }

   public E poll() {
      return (E)this.getQueue().poll();
   }

   public E remove() {
      return (E)this.getQueue().remove();
   }
}
