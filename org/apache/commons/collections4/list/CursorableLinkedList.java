package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** @deprecated */
@Deprecated
public class CursorableLinkedList<E> extends AbstractLinkedList<E> implements Serializable {
   private static final long serialVersionUID = 8836393098519411393L;
   private transient List<WeakReference<Cursor<E>>> cursors;

   public CursorableLinkedList() {
      this.init();
   }

   public CursorableLinkedList(Collection<? extends E> coll) {
      super(coll);
   }

   protected void addNode(AbstractLinkedList.Node<E> nodeToInsert, AbstractLinkedList.Node<E> insertBeforeNode) {
      super.addNode(nodeToInsert, insertBeforeNode);
      this.broadcastNodeInserted(nodeToInsert);
   }

   protected void broadcastNodeChanged(AbstractLinkedList.Node<E> node) {
      Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();

      while(it.hasNext()) {
         WeakReference<Cursor<E>> ref = (WeakReference)it.next();
         Cursor<E> cursor = (Cursor)ref.get();
         if (cursor == null) {
            it.remove();
         } else {
            cursor.nodeChanged(node);
         }
      }

   }

   protected void broadcastNodeInserted(AbstractLinkedList.Node<E> node) {
      Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();

      while(it.hasNext()) {
         WeakReference<Cursor<E>> ref = (WeakReference)it.next();
         Cursor<E> cursor = (Cursor)ref.get();
         if (cursor == null) {
            it.remove();
         } else {
            cursor.nodeInserted(node);
         }
      }

   }

   protected void broadcastNodeRemoved(AbstractLinkedList.Node<E> node) {
      Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();

      while(it.hasNext()) {
         WeakReference<Cursor<E>> ref = (WeakReference)it.next();
         Cursor<E> cursor = (Cursor)ref.get();
         if (cursor == null) {
            it.remove();
         } else {
            cursor.nodeRemoved(node);
         }
      }

   }

   protected ListIterator<E> createSubListListIterator(AbstractLinkedList.LinkedSubList<E> subList, int fromIndex) {
      SubCursor<E> cursor = new SubCursor<E>(subList, fromIndex);
      this.registerCursor(cursor);
      return cursor;
   }

   public Cursor<E> cursor() {
      return this.cursor(0);
   }

   public Cursor<E> cursor(int fromIndex) {
      Cursor<E> cursor = new Cursor<E>(this, fromIndex);
      this.registerCursor(cursor);
      return cursor;
   }

   protected void init() {
      super.init();
      this.cursors = new ArrayList();
   }

   public Iterator<E> iterator() {
      return super.listIterator(0);
   }

   public ListIterator<E> listIterator() {
      return this.cursor(0);
   }

   public ListIterator<E> listIterator(int fromIndex) {
      return this.cursor(fromIndex);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.doReadObject(in);
   }

   protected void registerCursor(Cursor<E> cursor) {
      this.cursors.removeIf((ref) -> ref.get() == null);
      this.cursors.add(new WeakReference(cursor));
   }

   protected void removeAllNodes() {
      if (!this.isEmpty()) {
         Iterator<E> it = this.iterator();

         while(it.hasNext()) {
            it.next();
            it.remove();
         }
      }

   }

   protected void removeNode(AbstractLinkedList.Node<E> node) {
      super.removeNode(node);
      this.broadcastNodeRemoved(node);
   }

   protected void unregisterCursor(Cursor<E> cursor) {
      Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();

      while(it.hasNext()) {
         WeakReference<Cursor<E>> ref = (WeakReference)it.next();
         Cursor<E> cur = (Cursor)ref.get();
         if (cur == null) {
            it.remove();
         } else if (cur == cursor) {
            ref.clear();
            it.remove();
            break;
         }
      }

   }

   protected void updateNode(AbstractLinkedList.Node<E> node, E value) {
      super.updateNode(node, value);
      this.broadcastNodeChanged(node);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      this.doWriteObject(out);
   }

   public static class Cursor<E> extends AbstractLinkedList.LinkedListIterator<E> {
      boolean valid = true;
      boolean nextIndexValid = true;
      boolean currentRemovedByAnother;

      protected Cursor(CursorableLinkedList<E> parent, int index) {
         super(parent, index);
         this.valid = true;
      }

      public void add(E obj) {
         super.add(obj);
         this.next = this.next.next;
      }

      protected void checkModCount() {
         if (!this.valid) {
            throw new ConcurrentModificationException("Cursor closed");
         }
      }

      public void close() {
         if (this.valid) {
            ((CursorableLinkedList)this.parent).unregisterCursor(this);
            this.valid = false;
         }

      }

      public int nextIndex() {
         if (!this.nextIndexValid) {
            if (this.next == this.parent.header) {
               this.nextIndex = this.parent.size();
            } else {
               int pos = 0;

               for(AbstractLinkedList.Node<E> temp = this.parent.header.next; temp != this.next; temp = temp.next) {
                  ++pos;
               }

               this.nextIndex = pos;
            }

            this.nextIndexValid = true;
         }

         return this.nextIndex;
      }

      protected void nodeChanged(AbstractLinkedList.Node<E> node) {
      }

      protected void nodeInserted(AbstractLinkedList.Node<E> node) {
         if (node.previous != this.current && this.next.previous != node) {
            this.nextIndexValid = false;
         } else {
            this.next = node;
         }

      }

      protected void nodeRemoved(AbstractLinkedList.Node<E> node) {
         if (node == this.next && node == this.current) {
            this.next = node.next;
            this.current = null;
            this.currentRemovedByAnother = true;
         } else if (node == this.next) {
            this.next = node.next;
            this.currentRemovedByAnother = false;
         } else if (node == this.current) {
            this.current = null;
            this.currentRemovedByAnother = true;
            --this.nextIndex;
         } else {
            this.nextIndexValid = false;
            this.currentRemovedByAnother = false;
         }

      }

      public void remove() {
         if (this.current != null || !this.currentRemovedByAnother) {
            this.checkModCount();
            this.parent.removeNode(this.getLastNodeReturned());
         }

         this.currentRemovedByAnother = false;
      }
   }

   protected static class SubCursor<E> extends Cursor<E> {
      protected final AbstractLinkedList.LinkedSubList<E> sub;

      protected SubCursor(AbstractLinkedList.LinkedSubList<E> sub, int index) {
         super((CursorableLinkedList)sub.parent, index + sub.offset);
         this.sub = sub;
      }

      public void add(E obj) {
         super.add(obj);
         this.sub.expectedModCount = this.parent.modCount;
         ++this.sub.size;
      }

      public boolean hasNext() {
         return this.nextIndex() < this.sub.size;
      }

      public boolean hasPrevious() {
         return this.previousIndex() >= 0;
      }

      public int nextIndex() {
         return super.nextIndex() - this.sub.offset;
      }

      public void remove() {
         super.remove();
         this.sub.expectedModCount = this.parent.modCount;
         --this.sub.size;
      }
   }
}
