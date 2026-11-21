package org.apache.commons.collections4;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.queue.SynchronizedQueue;
import org.apache.commons.collections4.queue.TransformedQueue;
import org.apache.commons.collections4.queue.UnmodifiableQueue;

public class QueueUtils {
   public static final Queue EMPTY_QUEUE = UnmodifiableQueue.unmodifiableQueue(new LinkedList());

   public static <E> Queue<E> emptyQueue() {
      return EMPTY_QUEUE;
   }

   public static <E> Queue<E> predicatedQueue(Queue<E> queue, Predicate<? super E> predicate) {
      return PredicatedQueue.<E>predicatedQueue(queue, predicate);
   }

   public static <E> Queue<E> synchronizedQueue(Queue<E> queue) {
      return SynchronizedQueue.<E>synchronizedQueue(queue);
   }

   public static <E> Queue<E> transformingQueue(Queue<E> queue, Transformer<? super E, ? extends E> transformer) {
      return TransformedQueue.<E>transformingQueue(queue, transformer);
   }

   public static <E> Queue<E> unmodifiableQueue(Queue<? extends E> queue) {
      return UnmodifiableQueue.<E>unmodifiableQueue(queue);
   }

   private QueueUtils() {
   }
}
