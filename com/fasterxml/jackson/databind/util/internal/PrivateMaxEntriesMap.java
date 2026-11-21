package com.fasterxml.jackson.databind.util.internal;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class PrivateMaxEntriesMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
   static final int NCPU = Runtime.getRuntime().availableProcessors();
   static final long MAXIMUM_CAPACITY = 9223372034707292160L;
   static final int NUMBER_OF_READ_BUFFERS;
   static final int READ_BUFFERS_MASK;
   static final int READ_BUFFER_THRESHOLD = 4;
   static final int READ_BUFFER_DRAIN_THRESHOLD = 8;
   static final int READ_BUFFER_SIZE = 16;
   static final int READ_BUFFER_INDEX_MASK = 15;
   static final int WRITE_BUFFER_DRAIN_THRESHOLD = 16;
   final ConcurrentMap<K, Node<K, V>> data;
   final int concurrencyLevel;
   final long[] readBufferReadCount;
   final LinkedDeque<Node<K, V>> evictionDeque;
   final AtomicLong weightedSize;
   final AtomicLong capacity;
   final Lock evictionLock;
   final Queue<Runnable> writeBuffer;
   final AtomicLongArray readBufferWriteCount;
   final AtomicLongArray readBufferDrainAtWriteCount;
   final AtomicReferenceArray<Node<K, V>> readBuffers;
   final AtomicReference<DrainStatus> drainStatus;
   transient Set<K> keySet;
   transient Collection<V> values;
   transient Set<Map.Entry<K, V>> entrySet;
   static final long serialVersionUID = 1L;

   static int ceilingNextPowerOfTwo(int x) {
      return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
   }

   private static int readBufferIndex(int bufferIndex, int entryIndex) {
      return 16 * bufferIndex + entryIndex;
   }

   PrivateMaxEntriesMap(Builder<K, V> builder) {
      this.concurrencyLevel = builder.concurrencyLevel;
      this.capacity = new AtomicLong(Math.min(builder.capacity, 9223372034707292160L));
      this.data = new ConcurrentHashMap(builder.initialCapacity, 0.75F, this.concurrencyLevel);
      this.evictionLock = new ReentrantLock();
      this.weightedSize = new AtomicLong();
      this.evictionDeque = new LinkedDeque<Node<K, V>>();
      this.writeBuffer = new ConcurrentLinkedQueue();
      this.drainStatus = new AtomicReference(PrivateMaxEntriesMap.DrainStatus.IDLE);
      this.readBufferReadCount = new long[NUMBER_OF_READ_BUFFERS];
      this.readBufferWriteCount = new AtomicLongArray(NUMBER_OF_READ_BUFFERS);
      this.readBufferDrainAtWriteCount = new AtomicLongArray(NUMBER_OF_READ_BUFFERS);
      this.readBuffers = new AtomicReferenceArray(NUMBER_OF_READ_BUFFERS * 16);
   }

   static void checkNotNull(Object o) {
      if (o == null) {
         throw new NullPointerException();
      }
   }

   static void checkArgument(boolean expression) {
      if (!expression) {
         throw new IllegalArgumentException();
      }
   }

   static void checkState(boolean expression) {
      if (!expression) {
         throw new IllegalStateException();
      }
   }

   public long capacity() {
      return this.capacity.get();
   }

   public void setCapacity(long capacity) {
      checkArgument(capacity >= 0L);
      this.evictionLock.lock();

      try {
         this.capacity.lazySet(Math.min(capacity, 9223372034707292160L));
         this.drainBuffers();
         this.evict();
      } finally {
         this.evictionLock.unlock();
      }

   }

   boolean hasOverflowed() {
      return this.weightedSize.get() > this.capacity.get();
   }

   void evict() {
      while(this.hasOverflowed()) {
         Node<K, V> node = (Node)this.evictionDeque.poll();
         if (node == null) {
            return;
         }

         this.data.remove(node.key, node);
         this.makeDead(node);
      }

   }

   void afterRead(Node<K, V> node) {
      int bufferIndex = readBufferIndex();
      long writeCount = this.recordRead(bufferIndex, node);
      this.drainOnReadIfNeeded(bufferIndex, writeCount);
   }

   static int readBufferIndex() {
      return (int)Thread.currentThread().getId() & READ_BUFFERS_MASK;
   }

   long recordRead(int bufferIndex, Node<K, V> node) {
      long writeCount = this.readBufferWriteCount.get(bufferIndex);
      this.readBufferWriteCount.lazySet(bufferIndex, writeCount + 1L);
      int index = (int)(writeCount & 15L);
      this.readBuffers.lazySet(readBufferIndex(bufferIndex, index), node);
      return writeCount;
   }

   void drainOnReadIfNeeded(int bufferIndex, long writeCount) {
      long pending = writeCount - this.readBufferDrainAtWriteCount.get(bufferIndex);
      boolean delayable = pending < 4L;
      DrainStatus status = (DrainStatus)this.drainStatus.get();
      if (status.shouldDrainBuffers(delayable)) {
         this.tryToDrainBuffers();
      }

   }

   void afterWrite(Runnable task) {
      this.writeBuffer.add(task);
      this.drainStatus.lazySet(PrivateMaxEntriesMap.DrainStatus.REQUIRED);
      this.tryToDrainBuffers();
   }

   void tryToDrainBuffers() {
      if (this.evictionLock.tryLock()) {
         try {
            this.drainStatus.lazySet(PrivateMaxEntriesMap.DrainStatus.PROCESSING);
            this.drainBuffers();
         } finally {
            this.drainStatus.compareAndSet(PrivateMaxEntriesMap.DrainStatus.PROCESSING, PrivateMaxEntriesMap.DrainStatus.IDLE);
            this.evictionLock.unlock();
         }
      }

   }

   void drainBuffers() {
      this.drainReadBuffers();
      this.drainWriteBuffer();
   }

   void drainReadBuffers() {
      int start = (int)Thread.currentThread().getId();
      int end = start + NUMBER_OF_READ_BUFFERS;

      for(int i = start; i < end; ++i) {
         this.drainReadBuffer(i & READ_BUFFERS_MASK);
      }

   }

   void drainReadBuffer(int bufferIndex) {
      long writeCount = this.readBufferWriteCount.get(bufferIndex);

      for(int i = 0; i < 8; ++i) {
         int index = (int)(this.readBufferReadCount[bufferIndex] & 15L);
         int arrayIndex = readBufferIndex(bufferIndex, index);
         Node<K, V> node = (Node)this.readBuffers.get(arrayIndex);
         if (node == null) {
            break;
         }

         this.readBuffers.lazySet(arrayIndex, (Object)null);
         this.applyRead(node);
         int var10002 = this.readBufferReadCount[bufferIndex]++;
      }

      this.readBufferDrainAtWriteCount.lazySet(bufferIndex, writeCount);
   }

   void applyRead(Node<K, V> node) {
      if (this.evictionDeque.contains((Linked)node)) {
         this.evictionDeque.moveToBack(node);
      }

   }

   void drainWriteBuffer() {
      for(int i = 0; i < 16; ++i) {
         Runnable task = (Runnable)this.writeBuffer.poll();
         if (task == null) {
            break;
         }

         task.run();
      }

   }

   boolean tryToRetire(Node<K, V> node, WeightedValue<V> expect) {
      if (expect.isAlive()) {
         WeightedValue<V> retired = new WeightedValue<V>(expect.value, -expect.weight);
         return node.compareAndSet(expect, retired);
      } else {
         return false;
      }
   }

   void makeRetired(Node<K, V> node) {
      WeightedValue<V> current;
      WeightedValue<V> retired;
      do {
         current = (WeightedValue)node.get();
         if (!current.isAlive()) {
            return;
         }

         retired = new WeightedValue<V>(current.value, -current.weight);
      } while(!node.compareAndSet(current, retired));

   }

   void makeDead(Node<K, V> node) {
      WeightedValue<V> current;
      WeightedValue<V> dead;
      do {
         current = (WeightedValue)node.get();
         dead = new WeightedValue<V>(current.value, 0);
      } while(!node.compareAndSet(current, dead));

      this.weightedSize.lazySet(this.weightedSize.get() - (long)Math.abs(current.weight));
   }

   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   public int size() {
      return this.data.size();
   }

   public void clear() {
      this.evictionLock.lock();

      try {
         Node<K, V> node;
         while((node = (Node)this.evictionDeque.poll()) != null) {
            this.data.remove(node.key, node);
            this.makeDead(node);
         }

         for(int i = 0; i < this.readBuffers.length(); ++i) {
            this.readBuffers.lazySet(i, (Object)null);
         }

         Runnable task;
         while((task = (Runnable)this.writeBuffer.poll()) != null) {
            task.run();
         }
      } finally {
         this.evictionLock.unlock();
      }

   }

   public boolean containsKey(Object key) {
      return this.data.containsKey(key);
   }

   public boolean containsValue(Object value) {
      checkNotNull(value);

      for(Node<K, V> node : this.data.values()) {
         if (node.getValue().equals(value)) {
            return true;
         }
      }

      return false;
   }

   public V get(Object key) {
      Node<K, V> node = (Node)this.data.get(key);
      if (node == null) {
         return null;
      } else {
         this.afterRead(node);
         return node.getValue();
      }
   }

   public V put(K key, V value) {
      return (V)this.put(key, value, false);
   }

   public V putIfAbsent(K key, V value) {
      return (V)this.put(key, value, true);
   }

   V put(K key, V value, boolean onlyIfAbsent) {
      checkNotNull(key);
      checkNotNull(value);
      int weight = 1;
      WeightedValue<V> weightedValue = new WeightedValue<V>(value, 1);
      Node<K, V> node = new Node<K, V>(key, weightedValue);

      label27:
      while(true) {
         Node<K, V> prior = (Node)this.data.putIfAbsent(node.key, node);
         if (prior == null) {
            this.afterWrite(new AddTask(node, 1));
            return null;
         }

         if (onlyIfAbsent) {
            this.afterRead(prior);
            return prior.getValue();
         }

         WeightedValue<V> oldWeightedValue;
         do {
            oldWeightedValue = (WeightedValue)prior.get();
            if (!oldWeightedValue.isAlive()) {
               continue label27;
            }
         } while(!prior.compareAndSet(oldWeightedValue, weightedValue));

         int weightedDifference = 1 - oldWeightedValue.weight;
         if (weightedDifference == 0) {
            this.afterRead(prior);
         } else {
            this.afterWrite(new UpdateTask(prior, weightedDifference));
         }

         return oldWeightedValue.value;
      }
   }

   public V remove(Object key) {
      Node<K, V> node = (Node)this.data.remove(key);
      if (node == null) {
         return null;
      } else {
         this.makeRetired(node);
         this.afterWrite(new RemovalTask(node));
         return node.getValue();
      }
   }

   public boolean remove(Object key, Object value) {
      Node<K, V> node = (Node)this.data.get(key);
      if (node != null && value != null) {
         WeightedValue<V> weightedValue = (WeightedValue)node.get();

         while(weightedValue.contains(value)) {
            if (this.tryToRetire(node, weightedValue)) {
               if (this.data.remove(key, node)) {
                  this.afterWrite(new RemovalTask(node));
                  return true;
               }
               break;
            }

            weightedValue = (WeightedValue)node.get();
            if (!weightedValue.isAlive()) {
               break;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public V replace(K key, V value) {
      checkNotNull(key);
      checkNotNull(value);
      int weight = 1;
      WeightedValue<V> weightedValue = new WeightedValue<V>(value, 1);
      Node<K, V> node = (Node)this.data.get(key);
      if (node == null) {
         return null;
      } else {
         WeightedValue<V> oldWeightedValue;
         do {
            oldWeightedValue = (WeightedValue)node.get();
            if (!oldWeightedValue.isAlive()) {
               return null;
            }
         } while(!node.compareAndSet(oldWeightedValue, weightedValue));

         int weightedDifference = 1 - oldWeightedValue.weight;
         if (weightedDifference == 0) {
            this.afterRead(node);
         } else {
            this.afterWrite(new UpdateTask(node, weightedDifference));
         }

         return oldWeightedValue.value;
      }
   }

   public boolean replace(K key, V oldValue, V newValue) {
      checkNotNull(key);
      checkNotNull(oldValue);
      checkNotNull(newValue);
      int weight = 1;
      WeightedValue<V> newWeightedValue = new WeightedValue<V>(newValue, 1);
      Node<K, V> node = (Node)this.data.get(key);
      if (node == null) {
         return false;
      } else {
         WeightedValue<V> weightedValue;
         do {
            weightedValue = (WeightedValue)node.get();
            if (!weightedValue.isAlive() || !weightedValue.contains(oldValue)) {
               return false;
            }
         } while(!node.compareAndSet(weightedValue, newWeightedValue));

         int weightedDifference = 1 - weightedValue.weight;
         if (weightedDifference == 0) {
            this.afterRead(node);
         } else {
            this.afterWrite(new UpdateTask(node, weightedDifference));
         }

         return true;
      }
   }

   public Set<K> keySet() {
      Set<K> ks = this.keySet;
      return ks == null ? (this.keySet = new KeySet()) : ks;
   }

   public Collection<V> values() {
      Collection<V> vs = this.values;
      return vs == null ? (this.values = new Values()) : vs;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> es = this.entrySet;
      return es == null ? (this.entrySet = new EntrySet()) : es;
   }

   Object writeReplace() {
      return new SerializationProxy(this);
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   static {
      NUMBER_OF_READ_BUFFERS = Math.min(4, ceilingNextPowerOfTwo(NCPU));
      READ_BUFFERS_MASK = NUMBER_OF_READ_BUFFERS - 1;
   }

   final class AddTask implements Runnable {
      final Node<K, V> node;
      final int weight;

      AddTask(Node<K, V> node, int weight) {
         this.weight = weight;
         this.node = node;
      }

      public void run() {
         PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + (long)this.weight);
         if (((WeightedValue)this.node.get()).isAlive()) {
            PrivateMaxEntriesMap.this.evictionDeque.add(this.node);
            PrivateMaxEntriesMap.this.evict();
         }

      }
   }

   final class RemovalTask implements Runnable {
      final Node<K, V> node;

      RemovalTask(Node<K, V> node) {
         this.node = node;
      }

      public void run() {
         PrivateMaxEntriesMap.this.evictionDeque.remove((Linked)this.node);
         PrivateMaxEntriesMap.this.makeDead(this.node);
      }
   }

   final class UpdateTask implements Runnable {
      final int weightDifference;
      final Node<K, V> node;

      UpdateTask(Node<K, V> node, int weightDifference) {
         this.weightDifference = weightDifference;
         this.node = node;
      }

      public void run() {
         PrivateMaxEntriesMap.this.weightedSize.lazySet(PrivateMaxEntriesMap.this.weightedSize.get() + (long)this.weightDifference);
         PrivateMaxEntriesMap.this.applyRead(this.node);
         PrivateMaxEntriesMap.this.evict();
      }
   }

   static enum DrainStatus {
      IDLE {
         boolean shouldDrainBuffers(boolean delayable) {
            return !delayable;
         }
      },
      REQUIRED {
         boolean shouldDrainBuffers(boolean delayable) {
            return true;
         }
      },
      PROCESSING {
         boolean shouldDrainBuffers(boolean delayable) {
            return false;
         }
      };

      private DrainStatus() {
      }

      abstract boolean shouldDrainBuffers(boolean var1);
   }

   static final class WeightedValue<V> {
      final int weight;
      final V value;

      WeightedValue(V value, int weight) {
         this.weight = weight;
         this.value = value;
      }

      boolean contains(Object o) {
         return o == this.value || this.value.equals(o);
      }

      boolean isAlive() {
         return this.weight > 0;
      }
   }

   static final class Node<K, V> extends AtomicReference<WeightedValue<V>> implements Linked<Node<K, V>> {
      final K key;
      Node<K, V> prev;
      Node<K, V> next;

      Node(K key, WeightedValue<V> weightedValue) {
         super(weightedValue);
         this.key = key;
      }

      public Node<K, V> getPrevious() {
         return this.prev;
      }

      public void setPrevious(Node<K, V> prev) {
         this.prev = prev;
      }

      public Node<K, V> getNext() {
         return this.next;
      }

      public void setNext(Node<K, V> next) {
         this.next = next;
      }

      V getValue() {
         return ((WeightedValue)this.get()).value;
      }
   }

   final class KeySet extends AbstractSet<K> {
      final PrivateMaxEntriesMap<K, V> map = PrivateMaxEntriesMap.this;

      public int size() {
         return this.map.size();
      }

      public void clear() {
         this.map.clear();
      }

      public Iterator<K> iterator() {
         return PrivateMaxEntriesMap.this.new KeyIterator();
      }

      public boolean contains(Object obj) {
         return PrivateMaxEntriesMap.this.containsKey(obj);
      }

      public boolean remove(Object obj) {
         return this.map.remove(obj) != null;
      }

      public Object[] toArray() {
         return this.map.data.keySet().toArray();
      }

      public <T> T[] toArray(T[] array) {
         return (T[])this.map.data.keySet().toArray(array);
      }
   }

   final class KeyIterator implements Iterator<K> {
      final Iterator<K> iterator;
      K current;

      KeyIterator() {
         this.iterator = PrivateMaxEntriesMap.this.data.keySet().iterator();
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public K next() {
         this.current = (K)this.iterator.next();
         return this.current;
      }

      public void remove() {
         PrivateMaxEntriesMap.checkState(this.current != null);
         PrivateMaxEntriesMap.this.remove(this.current);
         this.current = (K)null;
      }
   }

   final class Values extends AbstractCollection<V> {
      public int size() {
         return PrivateMaxEntriesMap.this.size();
      }

      public void clear() {
         PrivateMaxEntriesMap.this.clear();
      }

      public Iterator<V> iterator() {
         return PrivateMaxEntriesMap.this.new ValueIterator();
      }

      public boolean contains(Object o) {
         return PrivateMaxEntriesMap.this.containsValue(o);
      }
   }

   final class ValueIterator implements Iterator<V> {
      final Iterator<Node<K, V>> iterator;
      Node<K, V> current;

      ValueIterator() {
         this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public V next() {
         this.current = (Node)this.iterator.next();
         return this.current.getValue();
      }

      public void remove() {
         PrivateMaxEntriesMap.checkState(this.current != null);
         PrivateMaxEntriesMap.this.remove(this.current.key);
         this.current = null;
      }
   }

   final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      final PrivateMaxEntriesMap<K, V> map = PrivateMaxEntriesMap.this;

      public int size() {
         return this.map.size();
      }

      public void clear() {
         this.map.clear();
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return PrivateMaxEntriesMap.this.new EntryIterator();
      }

      public boolean contains(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            Node<K, V> node = (Node)this.map.data.get(entry.getKey());
            return node != null && node.getValue().equals(entry.getValue());
         }
      }

      public boolean add(Map.Entry<K, V> entry) {
         throw new UnsupportedOperationException("ConcurrentLinkedHashMap does not allow add to be called on entrySet()");
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            return this.map.remove(entry.getKey(), entry.getValue());
         }
      }
   }

   final class EntryIterator implements Iterator<Map.Entry<K, V>> {
      final Iterator<Node<K, V>> iterator;
      Node<K, V> current;

      EntryIterator() {
         this.iterator = PrivateMaxEntriesMap.this.data.values().iterator();
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Map.Entry<K, V> next() {
         this.current = (Node)this.iterator.next();
         return PrivateMaxEntriesMap.this.new WriteThroughEntry(this.current);
      }

      public void remove() {
         PrivateMaxEntriesMap.checkState(this.current != null);
         PrivateMaxEntriesMap.this.remove(this.current.key);
         this.current = null;
      }
   }

   final class WriteThroughEntry extends AbstractMap.SimpleEntry<K, V> {
      static final long serialVersionUID = 1L;

      WriteThroughEntry(Node<K, V> node) {
         super(node.key, node.getValue());
      }

      public V setValue(V value) {
         PrivateMaxEntriesMap.this.put(this.getKey(), value);
         return (V)super.setValue(value);
      }

      Object writeReplace() {
         return new AbstractMap.SimpleEntry(this);
      }
   }

   static final class SerializationProxy<K, V> implements Serializable {
      final int concurrencyLevel;
      final Map<K, V> data;
      final long capacity;
      static final long serialVersionUID = 1L;

      SerializationProxy(PrivateMaxEntriesMap<K, V> map) {
         this.concurrencyLevel = map.concurrencyLevel;
         this.data = new HashMap(map);
         this.capacity = map.capacity.get();
      }

      Object readResolve() {
         PrivateMaxEntriesMap<K, V> map = (new Builder<K, V>()).maximumCapacity(this.capacity).build();
         map.putAll(this.data);
         return map;
      }
   }

   public static final class Builder<K, V> {
      static final int DEFAULT_CONCURRENCY_LEVEL = 16;
      static final int DEFAULT_INITIAL_CAPACITY = 16;
      int concurrencyLevel = 16;
      int initialCapacity = 16;
      long capacity = -1L;

      public Builder<K, V> initialCapacity(int initialCapacity) {
         PrivateMaxEntriesMap.checkArgument(initialCapacity >= 0);
         this.initialCapacity = initialCapacity;
         return this;
      }

      public Builder<K, V> maximumCapacity(long capacity) {
         PrivateMaxEntriesMap.checkArgument(capacity >= 0L);
         this.capacity = capacity;
         return this;
      }

      public Builder<K, V> concurrencyLevel(int concurrencyLevel) {
         PrivateMaxEntriesMap.checkArgument(concurrencyLevel > 0);
         this.concurrencyLevel = concurrencyLevel;
         return this;
      }

      public PrivateMaxEntriesMap<K, V> build() {
         PrivateMaxEntriesMap.checkState(this.capacity >= 0L);
         return new PrivateMaxEntriesMap<K, V>(this);
      }
   }
}
