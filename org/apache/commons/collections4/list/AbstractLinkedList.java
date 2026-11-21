package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.OrderedIterator;

/** @deprecated */
@Deprecated
public abstract class AbstractLinkedList<E> implements List<E> {
   transient Node<E> header;
   transient int size;
   transient int modCount;

   protected AbstractLinkedList() {
   }

   protected AbstractLinkedList(Collection<? extends E> coll) {
      this.init();
      this.addAll(coll);
   }

   public boolean add(E value) {
      this.addLast(value);
      return true;
   }

   public void add(int index, E value) {
      Node<E> node = this.getNode(index, true);
      this.addNodeBefore(node, value);
   }

   public boolean addAll(Collection<? extends E> coll) {
      return this.addAll(this.size, coll);
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      Node<E> node = this.getNode(index, true);

      for(E e : coll) {
         this.addNodeBefore(node, e);
      }

      return true;
   }

   public boolean addFirst(E e) {
      this.addNodeAfter(this.header, e);
      return true;
   }

   public boolean addLast(E e) {
      this.addNodeBefore(this.header, e);
      return true;
   }

   protected void addNode(Node<E> nodeToInsert, Node<E> insertBeforeNode) {
      Objects.requireNonNull(nodeToInsert, "nodeToInsert");
      Objects.requireNonNull(insertBeforeNode, "insertBeforeNode");
      nodeToInsert.next = insertBeforeNode;
      nodeToInsert.previous = insertBeforeNode.previous;
      insertBeforeNode.previous.next = nodeToInsert;
      insertBeforeNode.previous = nodeToInsert;
      ++this.size;
      ++this.modCount;
   }

   protected void addNodeAfter(Node<E> node, E value) {
      Node<E> newNode = this.createNode(value);
      this.addNode(newNode, node.next);
   }

   protected void addNodeBefore(Node<E> node, E value) {
      Node<E> newNode = this.createNode(value);
      this.addNode(newNode, node);
   }

   public void clear() {
      this.removeAllNodes();
   }

   public boolean contains(Object value) {
      return this.indexOf(value) != -1;
   }

   public boolean containsAll(Collection<?> coll) {
      for(Object o : coll) {
         if (!this.contains(o)) {
            return false;
         }
      }

      return true;
   }

   protected Node<E> createHeaderNode() {
      return new Node<E>();
   }

   protected Node<E> createNode(E value) {
      return new Node<E>(value);
   }

   protected Iterator<E> createSubListIterator(LinkedSubList<E> subList) {
      return this.createSubListListIterator(subList, 0);
   }

   protected ListIterator<E> createSubListListIterator(LinkedSubList<E> subList, int fromIndex) {
      return new LinkedSubListIterator<E>(subList, fromIndex);
   }

   protected void doReadObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
      this.init();
      int size = inputStream.readInt();

      for(int i = 0; i < size; ++i) {
         this.add(inputStream.readObject());
      }

   }

   protected void doWriteObject(ObjectOutputStream outputStream) throws IOException {
      outputStream.writeInt(this.size());

      for(E e : this) {
         outputStream.writeObject(e);
      }

   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof List)) {
         return false;
      } else {
         List<?> other = (List)obj;
         if (other.size() != this.size()) {
            return false;
         } else {
            ListIterator<?> it1 = this.listIterator();
            ListIterator<?> it2 = other.listIterator();

            while(it1.hasNext() && it2.hasNext()) {
               if (!Objects.equals(it1.next(), it2.next())) {
                  return false;
               }
            }

            return !it1.hasNext() && !it2.hasNext();
         }
      }
   }

   public E get(int index) {
      Node<E> node = this.getNode(index, false);
      return node.getValue();
   }

   public E getFirst() {
      Node<E> node = this.header.next;
      if (node == this.header) {
         throw new NoSuchElementException();
      } else {
         return node.getValue();
      }
   }

   public E getLast() {
      Node<E> node = this.header.previous;
      if (node == this.header) {
         throw new NoSuchElementException();
      } else {
         return node.getValue();
      }
   }

   protected Node<E> getNode(int index, boolean endMarkerAllowed) throws IndexOutOfBoundsException {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") less than zero.");
      } else if (!endMarkerAllowed && index == this.size) {
         throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") is the size of the list.");
      } else if (index > this.size) {
         throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") greater than the size of the list (" + this.size + ").");
      } else {
         Node<E> node;
         if (index < this.size / 2) {
            node = this.header.next;

            for(int currentIndex = 0; currentIndex < index; ++currentIndex) {
               node = node.next;
            }
         } else {
            node = this.header;

            for(int currentIndex = this.size; currentIndex > index; --currentIndex) {
               node = node.previous;
            }
         }

         return node;
      }
   }

   public int hashCode() {
      int hashCode = 1;

      for(E e : this) {
         hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
      }

      return hashCode;
   }

   public int indexOf(Object value) {
      int i = 0;

      for(Node<E> node = this.header.next; node != this.header; node = node.next) {
         if (this.isEqualValue(node.getValue(), value)) {
            return i;
         }

         ++i;
      }

      return -1;
   }

   protected void init() {
      this.header = this.createHeaderNode();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   protected boolean isEqualValue(Object value1, Object value2) {
      return Objects.equals(value1, value2);
   }

   public Iterator<E> iterator() {
      return this.listIterator();
   }

   public int lastIndexOf(Object value) {
      int i = this.size - 1;

      for(Node<E> node = this.header.previous; node != this.header; node = node.previous) {
         if (this.isEqualValue(node.getValue(), value)) {
            return i;
         }

         --i;
      }

      return -1;
   }

   public ListIterator<E> listIterator() {
      return new LinkedListIterator<E>(this, 0);
   }

   public ListIterator<E> listIterator(int fromIndex) {
      return new LinkedListIterator<E>(this, fromIndex);
   }

   public E remove(int index) {
      Node<E> node = this.getNode(index, false);
      E oldValue = node.getValue();
      this.removeNode(node);
      return oldValue;
   }

   public boolean remove(Object value) {
      for(Node<E> node = this.header.next; node != this.header; node = node.next) {
         if (this.isEqualValue(node.getValue(), value)) {
            this.removeNode(node);
            return true;
         }
      }

      return false;
   }

   public boolean removeAll(Collection<?> coll) {
      boolean modified = false;
      Iterator<E> it = this.iterator();

      while(it.hasNext()) {
         if (coll.contains(it.next())) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   protected void removeAllNodes() {
      this.header.next = this.header;
      this.header.previous = this.header;
      this.size = 0;
      ++this.modCount;
   }

   public E removeFirst() {
      Node<E> node = this.header.next;
      if (node == this.header) {
         throw new NoSuchElementException();
      } else {
         E oldValue = node.getValue();
         this.removeNode(node);
         return oldValue;
      }
   }

   public E removeLast() {
      Node<E> node = this.header.previous;
      if (node == this.header) {
         throw new NoSuchElementException();
      } else {
         E oldValue = node.getValue();
         this.removeNode(node);
         return oldValue;
      }
   }

   protected void removeNode(Node<E> node) {
      Objects.requireNonNull(node, "node");
      node.previous.next = node.next;
      node.next.previous = node.previous;
      --this.size;
      ++this.modCount;
   }

   public boolean retainAll(Collection<?> coll) {
      boolean modified = false;
      Iterator<E> it = this.iterator();

      while(it.hasNext()) {
         if (!coll.contains(it.next())) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   public E set(int index, E value) {
      Node<E> node = this.getNode(index, false);
      E oldValue = node.getValue();
      this.updateNode(node, value);
      return oldValue;
   }

   public int size() {
      return this.size;
   }

   public List<E> subList(int fromIndexInclusive, int toIndexExclusive) {
      return new LinkedSubList<E>(this, fromIndexInclusive, toIndexExclusive);
   }

   public Object[] toArray() {
      return this.toArray(new Object[this.size]);
   }

   public <T> T[] toArray(T[] array) {
      if (array.length < this.size) {
         Class<?> componentType = array.getClass().getComponentType();
         array = (T[])((Object[])Array.newInstance(componentType, this.size));
      }

      int i = 0;

      for(Node<E> node = this.header.next; node != this.header; ++i) {
         array[i] = node.getValue();
         node = node.next;
      }

      if (array.length > this.size) {
         array[this.size] = null;
      }

      return array;
   }

   public String toString() {
      if (this.isEmpty()) {
         return "[]";
      } else {
         StringBuilder buf = new StringBuilder(16 * this.size());
         buf.append("[");
         Iterator<E> it = this.iterator();
         boolean hasNext = it.hasNext();

         while(hasNext) {
            Object value = it.next();
            buf.append(value == this ? "(this Collection)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
               buf.append(", ");
            }
         }

         buf.append("]");
         return buf.toString();
      }
   }

   protected void updateNode(Node<E> node, E value) {
      node.setValue(value);
   }

   protected static class LinkedListIterator<E> implements ListIterator<E>, OrderedIterator<E> {
      protected final AbstractLinkedList<E> parent;
      protected Node<E> next;
      protected int nextIndex;
      protected Node<E> current;
      protected int expectedModCount;

      protected LinkedListIterator(AbstractLinkedList<E> parent, int fromIndex) throws IndexOutOfBoundsException {
         this.parent = parent;
         this.expectedModCount = parent.modCount;
         this.next = parent.getNode(fromIndex, true);
         this.nextIndex = fromIndex;
      }

      public void add(E obj) {
         this.checkModCount();
         this.parent.addNodeBefore(this.next, obj);
         this.current = null;
         ++this.nextIndex;
         ++this.expectedModCount;
      }

      protected void checkModCount() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      protected Node<E> getLastNodeReturned() throws IllegalStateException {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return this.current;
         }
      }

      public boolean hasNext() {
         return this.next != this.parent.header;
      }

      public boolean hasPrevious() {
         return this.next.previous != this.parent.header;
      }

      public E next() {
         this.checkModCount();
         if (!this.hasNext()) {
            throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
         } else {
            E value = this.next.getValue();
            this.current = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return value;
         }
      }

      public int nextIndex() {
         return this.nextIndex;
      }

      public E previous() {
         this.checkModCount();
         if (!this.hasPrevious()) {
            throw new NoSuchElementException("Already at start of list.");
         } else {
            this.next = this.next.previous;
            E value = this.next.getValue();
            this.current = this.next;
            --this.nextIndex;
            return value;
         }
      }

      public int previousIndex() {
         return this.nextIndex() - 1;
      }

      public void remove() {
         this.checkModCount();
         if (this.current == this.next) {
            this.next = this.next.next;
            this.parent.removeNode(this.getLastNodeReturned());
         } else {
            this.parent.removeNode(this.getLastNodeReturned());
            --this.nextIndex;
         }

         this.current = null;
         ++this.expectedModCount;
      }

      public void set(E value) {
         this.checkModCount();
         this.getLastNodeReturned().setValue(value);
      }
   }

   protected static class LinkedSubList<E> extends AbstractList<E> {
      AbstractLinkedList<E> parent;
      int offset;
      int size;
      int expectedModCount;

      protected LinkedSubList(AbstractLinkedList<E> parent, int fromIndex, int toIndex) {
         if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
         } else if (toIndex > parent.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
         } else if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
         } else {
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.expectedModCount = parent.modCount;
         }
      }

      public void add(int index, E obj) {
         this.rangeCheck(index, this.size + 1);
         this.checkModCount();
         this.parent.add(index + this.offset, obj);
         this.expectedModCount = this.parent.modCount;
         ++this.size;
         ++this.modCount;
      }

      public boolean addAll(Collection<? extends E> coll) {
         return this.addAll(this.size, coll);
      }

      public boolean addAll(int index, Collection<? extends E> coll) {
         this.rangeCheck(index, this.size + 1);
         int cSize = coll.size();
         if (cSize == 0) {
            return false;
         } else {
            this.checkModCount();
            this.parent.addAll(this.offset + index, coll);
            this.expectedModCount = this.parent.modCount;
            this.size += cSize;
            ++this.modCount;
            return true;
         }
      }

      protected void checkModCount() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public void clear() {
         this.checkModCount();
         Iterator<E> it = this.iterator();

         while(it.hasNext()) {
            it.next();
            it.remove();
         }

      }

      public E get(int index) {
         this.rangeCheck(index, this.size);
         this.checkModCount();
         return this.parent.get(index + this.offset);
      }

      public Iterator<E> iterator() {
         this.checkModCount();
         return this.parent.createSubListIterator(this);
      }

      public ListIterator<E> listIterator(int index) {
         this.rangeCheck(index, this.size + 1);
         this.checkModCount();
         return this.parent.createSubListListIterator(this, index);
      }

      protected void rangeCheck(int index, int beyond) {
         if (index < 0 || index >= beyond) {
            throw new IndexOutOfBoundsException("Index '" + index + "' out of bounds for size '" + this.size + "'");
         }
      }

      public E remove(int index) {
         this.rangeCheck(index, this.size);
         this.checkModCount();
         E result = this.parent.remove(index + this.offset);
         this.expectedModCount = this.parent.modCount;
         --this.size;
         ++this.modCount;
         return result;
      }

      public E set(int index, E obj) {
         this.rangeCheck(index, this.size);
         this.checkModCount();
         return this.parent.set(index + this.offset, obj);
      }

      public int size() {
         this.checkModCount();
         return this.size;
      }

      public List<E> subList(int fromIndexInclusive, int toIndexExclusive) {
         return new LinkedSubList<E>(this.parent, fromIndexInclusive + this.offset, toIndexExclusive + this.offset);
      }
   }

   protected static class LinkedSubListIterator<E> extends LinkedListIterator<E> {
      protected final LinkedSubList<E> sub;

      protected LinkedSubListIterator(LinkedSubList<E> sub, int startIndex) {
         super(sub.parent, startIndex + sub.offset);
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

   protected static class Node<E> {
      protected Node<E> previous;
      protected Node<E> next;
      protected E value;

      protected Node() {
         this.previous = this;
         this.next = this;
      }

      protected Node(E value) {
         this.value = value;
      }

      protected Node(Node<E> previous, Node<E> next, E value) {
         this.previous = previous;
         this.next = next;
         this.value = value;
      }

      protected Node<E> getNextNode() {
         return this.next;
      }

      protected Node<E> getPreviousNode() {
         return this.previous;
      }

      protected E getValue() {
         return this.value;
      }

      protected void setNextNode(Node<E> next) {
         this.next = next;
      }

      protected void setPreviousNode(Node<E> previous) {
         this.previous = previous;
      }

      protected void setValue(E value) {
         this.value = value;
      }
   }
}
