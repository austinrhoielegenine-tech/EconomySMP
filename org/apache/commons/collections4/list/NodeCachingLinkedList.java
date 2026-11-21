package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

/** @deprecated */
@Deprecated
public class NodeCachingLinkedList<E> extends AbstractLinkedList<E> implements Serializable {
   private static final long serialVersionUID = 6897789178562232073L;
   private static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;
   private transient AbstractLinkedList.Node<E> firstCachedNode;
   private transient int cacheSize;
   private int maximumCacheSize;

   public NodeCachingLinkedList() {
      this(20);
   }

   public NodeCachingLinkedList(Collection<? extends E> coll) {
      super(coll);
      this.maximumCacheSize = 20;
   }

   public NodeCachingLinkedList(int maximumCacheSize) {
      this.maximumCacheSize = maximumCacheSize;
      this.init();
   }

   protected void addNodeToCache(AbstractLinkedList.Node<E> node) {
      if (!this.isCacheFull()) {
         AbstractLinkedList.Node<E> nextCachedNode = this.firstCachedNode;
         node.previous = null;
         node.next = nextCachedNode;
         node.setValue((Object)null);
         this.firstCachedNode = node;
         ++this.cacheSize;
      }
   }

   protected AbstractLinkedList.Node<E> createNode(E value) {
      AbstractLinkedList.Node<E> cachedNode = this.getNodeFromCache();
      if (cachedNode == null) {
         return super.createNode(value);
      } else {
         cachedNode.setValue(value);
         return cachedNode;
      }
   }

   protected int getMaximumCacheSize() {
      return this.maximumCacheSize;
   }

   protected AbstractLinkedList.Node<E> getNodeFromCache() {
      if (this.cacheSize == 0) {
         return null;
      } else {
         AbstractLinkedList.Node<E> cachedNode = this.firstCachedNode;
         this.firstCachedNode = cachedNode.next;
         cachedNode.next = null;
         --this.cacheSize;
         return cachedNode;
      }
   }

   protected boolean isCacheFull() {
      return this.cacheSize >= this.maximumCacheSize;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.doReadObject(in);
   }

   protected void removeAllNodes() {
      int numberOfNodesToCache = Math.min(this.size, this.maximumCacheSize - this.cacheSize);
      AbstractLinkedList.Node<E> node = this.header.next;

      for(int currentIndex = 0; currentIndex < numberOfNodesToCache; ++currentIndex) {
         AbstractLinkedList.Node<E> oldNode = node;
         node = node.next;
         this.addNodeToCache(oldNode);
      }

      super.removeAllNodes();
   }

   protected void removeNode(AbstractLinkedList.Node<E> node) {
      super.removeNode(node);
      this.addNodeToCache(node);
   }

   protected void setMaximumCacheSize(int maximumCacheSize) {
      this.maximumCacheSize = maximumCacheSize;
      this.shrinkCacheToMaximumSize();
   }

   protected void shrinkCacheToMaximumSize() {
      while(this.cacheSize > this.maximumCacheSize) {
         this.getNodeFromCache();
      }

   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      this.doWriteObject(out);
   }
}
