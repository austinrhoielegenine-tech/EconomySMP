package com.fasterxml.jackson.core.util;

import java.io.Serializable;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

public interface RecyclerPool<P extends RecyclerPool.WithPool<P>> extends Serializable {
   default P acquireAndLinkPooled() {
      return (P)this.acquirePooled().withPool(this);
   }

   P acquirePooled();

   void releasePooled(P var1);

   default boolean clear() {
      return false;
   }

   default int pooledCount() {
      return -1;
   }

   public abstract static class ThreadLocalPoolBase<P extends WithPool<P>> implements RecyclerPool<P> {
      private static final long serialVersionUID = 1L;

      protected ThreadLocalPoolBase() {
      }

      public P acquireAndLinkPooled() {
         return (P)this.acquirePooled();
      }

      public abstract P acquirePooled();

      public void releasePooled(P pooled) {
      }

      public int pooledCount() {
         return -1;
      }

      public boolean clear() {
         return false;
      }
   }

   public abstract static class NonRecyclingPoolBase<P extends WithPool<P>> implements RecyclerPool<P> {
      private static final long serialVersionUID = 1L;

      public P acquireAndLinkPooled() {
         return (P)this.acquirePooled();
      }

      public abstract P acquirePooled();

      public void releasePooled(P pooled) {
      }

      public int pooledCount() {
         return 0;
      }

      public boolean clear() {
         return true;
      }
   }

   public abstract static class StatefulImplBase<P extends WithPool<P>> implements RecyclerPool<P> {
      private static final long serialVersionUID = 1L;
      public static final int SERIALIZATION_SHARED = -1;
      public static final int SERIALIZATION_NON_SHARED = 1;
      protected final int _serialization;

      protected StatefulImplBase(int serialization) {
         this._serialization = serialization;
      }

      protected Optional<StatefulImplBase<P>> _resolveToShared(StatefulImplBase<P> shared) {
         return this._serialization == -1 ? Optional.of(shared) : Optional.empty();
      }

      public abstract P createPooled();
   }

   public abstract static class ConcurrentDequePoolBase<P extends WithPool<P>> extends StatefulImplBase<P> {
      private static final long serialVersionUID = 1L;
      protected final transient Deque<P> pool = new ConcurrentLinkedDeque();

      protected ConcurrentDequePoolBase(int serialization) {
         super(serialization);
      }

      public P acquirePooled() {
         P pooled = (P)(this.pool.pollFirst());
         if (pooled == null) {
            pooled = this.createPooled();
         }

         return pooled;
      }

      public void releasePooled(P pooled) {
         this.pool.offerLast(pooled);
      }

      public int pooledCount() {
         return this.pool.size();
      }

      public boolean clear() {
         this.pool.clear();
         return true;
      }
   }

   /** @deprecated */
   @Deprecated
   public abstract static class LockFreePoolBase<P extends WithPool<P>> extends StatefulImplBase<P> {
      private static final long serialVersionUID = 1L;
      private final transient AtomicReference<Node<P>> head = new AtomicReference();

      protected LockFreePoolBase(int serialization) {
         super(serialization);
      }

      public P acquirePooled() {
         for(int i = 0; i < 3; ++i) {
            Node<P> currentHead = (Node)this.head.get();
            if (currentHead == null) {
               return (P)this.createPooled();
            }

            if (this.head.compareAndSet(currentHead, currentHead.next)) {
               currentHead.next = null;
               return currentHead.value;
            }
         }

         return (P)this.createPooled();
      }

      public void releasePooled(P pooled) {
         Node<P> newHead = new Node<P>(pooled);

         for(int i = 0; i < 3; ++i) {
            newHead.next = (Node)this.head.get();
            if (this.head.compareAndSet(newHead.next, newHead)) {
               return;
            }
         }

      }

      public int pooledCount() {
         int count = 0;

         for(Node<P> curr = (Node)this.head.get(); curr != null; curr = curr.next) {
            ++count;
         }

         return count;
      }

      public boolean clear() {
         this.head.set((Object)null);
         return true;
      }

      protected static class Node<P> {
         final P value;
         Node<P> next;

         Node(P value) {
            this.value = value;
         }
      }
   }

   public abstract static class BoundedPoolBase<P extends WithPool<P>> extends StatefulImplBase<P> {
      private static final long serialVersionUID = 1L;
      public static final int DEFAULT_CAPACITY = 100;
      private final transient ArrayBlockingQueue<P> pool;
      private final transient int capacity;

      protected BoundedPoolBase(int capacityAsId) {
         super(capacityAsId);
         this.capacity = capacityAsId <= 0 ? 100 : capacityAsId;
         this.pool = new ArrayBlockingQueue(this.capacity);
      }

      public P acquirePooled() {
         P pooled = (P)(this.pool.poll());
         if (pooled == null) {
            pooled = this.createPooled();
         }

         return pooled;
      }

      public void releasePooled(P pooled) {
         this.pool.offer(pooled);
      }

      public int pooledCount() {
         return this.pool.size();
      }

      public boolean clear() {
         this.pool.clear();
         return true;
      }

      public int capacity() {
         return this.capacity;
      }
   }

   public interface WithPool<P extends WithPool<P>> {
      P withPool(RecyclerPool<P> var1);

      void releaseToPool();
   }
}
