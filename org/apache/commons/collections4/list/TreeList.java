package org.apache.commons.collections4.list;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.collections4.OrderedIterator;

public class TreeList<E> extends AbstractList<E> {
   private AVLNode<E> root;
   private int size;

   public TreeList() {
   }

   public TreeList(Collection<? extends E> coll) {
      if (!coll.isEmpty()) {
         this.root = new AVLNode<E>(coll);
         this.size = coll.size();
      }

   }

   public void add(int index, E obj) {
      ++this.modCount;
      this.checkInterval(index, 0, this.size());
      if (this.root == null) {
         this.root = new AVLNode<E>(index, obj, (AVLNode)null, (AVLNode)null);
      } else {
         this.root = this.root.insert(index, obj);
      }

      ++this.size;
   }

   public boolean addAll(Collection<? extends E> c) {
      if (c.isEmpty()) {
         return false;
      } else {
         this.modCount += c.size();
         AVLNode<E> cTree = new AVLNode<E>(c);
         this.root = this.root == null ? cTree : this.root.addAll(cTree, this.size);
         this.size += c.size();
         return true;
      }
   }

   private void checkInterval(int index, int startIndex, int endIndex) {
      if (index < startIndex || index > endIndex) {
         throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + this.size());
      }
   }

   public void clear() {
      ++this.modCount;
      this.root = null;
      this.size = 0;
   }

   public boolean contains(Object object) {
      return this.indexOf(object) >= 0;
   }

   public E get(int index) {
      this.checkInterval(index, 0, this.size() - 1);
      return this.root.get(index).getValue();
   }

   public int indexOf(Object object) {
      return this.root == null ? -1 : this.root.indexOf(object, this.root.relativePosition);
   }

   public Iterator<E> iterator() {
      return this.listIterator(0);
   }

   public ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   public ListIterator<E> listIterator(int fromIndex) {
      this.checkInterval(fromIndex, 0, this.size());
      return new TreeListIterator<E>(this, fromIndex);
   }

   public E remove(int index) {
      ++this.modCount;
      this.checkInterval(index, 0, this.size() - 1);
      E result = (E)this.get(index);
      this.root = this.root.remove(index);
      --this.size;
      return result;
   }

   public E set(int index, E obj) {
      this.checkInterval(index, 0, this.size() - 1);
      AVLNode<E> node = this.root.get(index);
      E result = node.value;
      node.setValue(obj);
      return result;
   }

   public int size() {
      return this.size;
   }

   public Object[] toArray() {
      Object[] array = new Object[this.size()];
      if (this.root != null) {
         this.root.toArray(array, this.root.relativePosition);
      }

      return array;
   }

   static class AVLNode<E> {
      private AVLNode<E> left;
      private boolean leftIsPrevious;
      private AVLNode<E> right;
      private boolean rightIsNext;
      private int height;
      private int relativePosition;
      private E value;

      private AVLNode(Collection<? extends E> coll) {
         this(coll.iterator(), 0, coll.size() - 1, 0, (AVLNode)null, (AVLNode)null);
      }

      private AVLNode(int relativePosition, E obj, AVLNode<E> rightFollower, AVLNode<E> leftFollower) {
         this.relativePosition = relativePosition;
         this.value = obj;
         this.rightIsNext = true;
         this.leftIsPrevious = true;
         this.right = rightFollower;
         this.left = leftFollower;
      }

      private AVLNode(Iterator<? extends E> iterator, int start, int end, int absolutePositionOfParent, AVLNode<E> prev, AVLNode<E> next) {
         int mid = start + (end - start) / 2;
         if (start < mid) {
            this.left = new AVLNode<E>(iterator, start, mid - 1, mid, prev, this);
         } else {
            this.leftIsPrevious = true;
            this.left = prev;
         }

         this.value = (E)iterator.next();
         this.relativePosition = mid - absolutePositionOfParent;
         if (mid < end) {
            this.right = new AVLNode<E>(iterator, mid + 1, end, mid, this, next);
         } else {
            this.rightIsNext = true;
            this.right = next;
         }

         this.recalcHeight();
      }

      private AVLNode<E> addAll(AVLNode<E> otherTree, int currentSize) {
         AVLNode<E> maxNode = this.max();
         AVLNode<E> otherTreeMin = otherTree.min();
         if (otherTree.height > this.height) {
            AVLNode<E> leftSubTree = this.removeMax();
            Deque<AVLNode<E>> sAncestors = new ArrayDeque();
            AVLNode<E> s = otherTree;
            int sAbsolutePosition = otherTree.relativePosition + currentSize;
            int sParentAbsolutePosition = 0;

            while(s != null && s.height > this.getHeight(leftSubTree)) {
               sParentAbsolutePosition = sAbsolutePosition;
               sAncestors.push(s);
               s = s.left;
               if (s != null) {
                  sAbsolutePosition += s.relativePosition;
               }
            }

            maxNode.setLeft(leftSubTree, (AVLNode)null);
            maxNode.setRight(s, otherTreeMin);
            if (leftSubTree != null) {
               leftSubTree.max().setRight((AVLNode)null, maxNode);
               leftSubTree.relativePosition -= currentSize - 1;
            }

            if (s != null) {
               s.min().setLeft((AVLNode)null, maxNode);
               s.relativePosition = sAbsolutePosition - currentSize + 1;
            }

            maxNode.relativePosition = currentSize - 1 - sParentAbsolutePosition;
            otherTree.relativePosition += currentSize;

            AVLNode<E> sAncestor;
            for(s = maxNode; !sAncestors.isEmpty(); s = sAncestor.balance()) {
               sAncestor = (AVLNode)sAncestors.pop();
               sAncestor.setLeft(s, (AVLNode)null);
            }

            return s;
         } else {
            otherTree = otherTree.removeMin();
            Deque<AVLNode<E>> sAncestors = new ArrayDeque();
            AVLNode<E> s = this;
            int sAbsolutePosition = this.relativePosition;
            int sParentAbsolutePosition = 0;

            while(s != null && s.height > this.getHeight(otherTree)) {
               sParentAbsolutePosition = sAbsolutePosition;
               sAncestors.push(s);
               s = s.right;
               if (s != null) {
                  sAbsolutePosition += s.relativePosition;
               }
            }

            otherTreeMin.setRight(otherTree, (AVLNode)null);
            otherTreeMin.setLeft(s, maxNode);
            if (otherTree != null) {
               otherTree.min().setLeft((AVLNode)null, otherTreeMin);
               ++otherTree.relativePosition;
            }

            if (s != null) {
               s.max().setRight((AVLNode)null, otherTreeMin);
               s.relativePosition = sAbsolutePosition - currentSize;
            }

            otherTreeMin.relativePosition = currentSize - sParentAbsolutePosition;

            AVLNode<E> sAncestor;
            for(s = otherTreeMin; !sAncestors.isEmpty(); s = sAncestor.balance()) {
               sAncestor = (AVLNode)sAncestors.pop();
               sAncestor.setRight(s, (AVLNode)null);
            }

            return s;
         }
      }

      private AVLNode<E> balance() {
         switch (this.heightRightMinusLeft()) {
            case -2:
               if (this.left.heightRightMinusLeft() > 0) {
                  this.setLeft(this.left.rotateLeft(), (AVLNode)null);
               }

               return this.rotateRight();
            case -1:
            case 0:
            case 1:
               return this;
            case 2:
               if (this.right.heightRightMinusLeft() < 0) {
                  this.setRight(this.right.rotateRight(), (AVLNode)null);
               }

               return this.rotateLeft();
            default:
               throw new IllegalStateException("tree inconsistent!");
         }
      }

      AVLNode<E> get(int index) {
         int indexRelativeToMe = index - this.relativePosition;
         if (indexRelativeToMe == 0) {
            return this;
         } else {
            AVLNode<E> nextNode = indexRelativeToMe < 0 ? this.getLeftSubTree() : this.getRightSubTree();
            return nextNode == null ? null : nextNode.get(indexRelativeToMe);
         }
      }

      private int getHeight(AVLNode<E> node) {
         return node == null ? -1 : node.height;
      }

      private AVLNode<E> getLeftSubTree() {
         return this.leftIsPrevious ? null : this.left;
      }

      private int getOffset(AVLNode<E> node) {
         return node == null ? 0 : node.relativePosition;
      }

      private AVLNode<E> getRightSubTree() {
         return this.rightIsNext ? null : this.right;
      }

      E getValue() {
         return this.value;
      }

      private int heightRightMinusLeft() {
         return this.getHeight(this.getRightSubTree()) - this.getHeight(this.getLeftSubTree());
      }

      int indexOf(Object object, int index) {
         if (this.getLeftSubTree() != null) {
            int result = this.left.indexOf(object, index + this.left.relativePosition);
            if (result != -1) {
               return result;
            }
         }

         if (Objects.equals(this.value, object)) {
            return index;
         } else {
            return this.getRightSubTree() != null ? this.right.indexOf(object, index + this.right.relativePosition) : -1;
         }
      }

      AVLNode<E> insert(int index, E obj) {
         int indexRelativeToMe = index - this.relativePosition;
         return indexRelativeToMe <= 0 ? this.insertOnLeft(indexRelativeToMe, obj) : this.insertOnRight(indexRelativeToMe, obj);
      }

      private AVLNode<E> insertOnLeft(int indexRelativeToMe, E obj) {
         if (this.getLeftSubTree() == null) {
            this.setLeft(new AVLNode(-1, obj, this, this.left), (AVLNode)null);
         } else {
            this.setLeft(this.left.insert(indexRelativeToMe, obj), (AVLNode)null);
         }

         if (this.relativePosition >= 0) {
            ++this.relativePosition;
         }

         AVLNode<E> ret = this.balance();
         this.recalcHeight();
         return ret;
      }

      private AVLNode<E> insertOnRight(int indexRelativeToMe, E obj) {
         if (this.getRightSubTree() == null) {
            this.setRight(new AVLNode(1, obj, this.right, this), (AVLNode)null);
         } else {
            this.setRight(this.right.insert(indexRelativeToMe, obj), (AVLNode)null);
         }

         if (this.relativePosition < 0) {
            --this.relativePosition;
         }

         AVLNode<E> ret = this.balance();
         this.recalcHeight();
         return ret;
      }

      private AVLNode<E> max() {
         return this.getRightSubTree() == null ? this : this.right.max();
      }

      private AVLNode<E> min() {
         return this.getLeftSubTree() == null ? this : this.left.min();
      }

      AVLNode<E> next() {
         return !this.rightIsNext && this.right != null ? this.right.min() : this.right;
      }

      AVLNode<E> previous() {
         return !this.leftIsPrevious && this.left != null ? this.left.max() : this.left;
      }

      private void recalcHeight() {
         this.height = Math.max(this.getLeftSubTree() == null ? -1 : this.getLeftSubTree().height, this.getRightSubTree() == null ? -1 : this.getRightSubTree().height) + 1;
      }

      AVLNode<E> remove(int index) {
         int indexRelativeToMe = index - this.relativePosition;
         if (indexRelativeToMe == 0) {
            return this.removeSelf();
         } else {
            if (indexRelativeToMe > 0) {
               this.setRight(this.right.remove(indexRelativeToMe), this.right.right);
               if (this.relativePosition < 0) {
                  ++this.relativePosition;
               }
            } else {
               this.setLeft(this.left.remove(indexRelativeToMe), this.left.left);
               if (this.relativePosition > 0) {
                  --this.relativePosition;
               }
            }

            this.recalcHeight();
            return this.balance();
         }
      }

      private AVLNode<E> removeMax() {
         if (this.getRightSubTree() == null) {
            return this.removeSelf();
         } else {
            this.setRight(this.right.removeMax(), this.right.right);
            if (this.relativePosition < 0) {
               ++this.relativePosition;
            }

            this.recalcHeight();
            return this.balance();
         }
      }

      private AVLNode<E> removeMin() {
         if (this.getLeftSubTree() == null) {
            return this.removeSelf();
         } else {
            this.setLeft(this.left.removeMin(), this.left.left);
            if (this.relativePosition > 0) {
               --this.relativePosition;
            }

            this.recalcHeight();
            return this.balance();
         }
      }

      private AVLNode<E> removeSelf() {
         if (this.getRightSubTree() == null && this.getLeftSubTree() == null) {
            return null;
         } else if (this.getRightSubTree() == null) {
            if (this.relativePosition > 0) {
               AVLNode var4 = this.left;
               var4.relativePosition += this.relativePosition;
            }

            this.left.max().setRight((AVLNode)null, this.right);
            return this.left;
         } else if (this.getLeftSubTree() == null) {
            AVLNode var10000 = this.right;
            var10000.relativePosition += this.relativePosition - (this.relativePosition < 0 ? 0 : 1);
            this.right.min().setLeft((AVLNode)null, this.left);
            return this.right;
         } else {
            if (this.heightRightMinusLeft() > 0) {
               AVLNode<E> rightMin = this.right.min();
               this.value = rightMin.value;
               if (this.leftIsPrevious) {
                  this.left = rightMin.left;
               }

               this.right = this.right.removeMin();
               if (this.relativePosition < 0) {
                  ++this.relativePosition;
               }
            } else {
               AVLNode<E> leftMax = this.left.max();
               this.value = leftMax.value;
               if (this.rightIsNext) {
                  this.right = leftMax.right;
               }

               AVLNode<E> leftPrevious = this.left.left;
               this.left = this.left.removeMax();
               if (this.left == null) {
                  this.left = leftPrevious;
                  this.leftIsPrevious = true;
               }

               if (this.relativePosition > 0) {
                  --this.relativePosition;
               }
            }

            this.recalcHeight();
            return this;
         }
      }

      private AVLNode<E> rotateLeft() {
         AVLNode<E> newTop = this.right;
         AVLNode<E> movedNode = this.getRightSubTree().getLeftSubTree();
         int newTopPosition = this.relativePosition + this.getOffset(newTop);
         int myNewPosition = -newTop.relativePosition;
         int movedPosition = this.getOffset(newTop) + this.getOffset(movedNode);
         this.setRight(movedNode, newTop);
         newTop.setLeft(this, (AVLNode)null);
         this.setOffset(newTop, newTopPosition);
         this.setOffset(this, myNewPosition);
         this.setOffset(movedNode, movedPosition);
         return newTop;
      }

      private AVLNode<E> rotateRight() {
         AVLNode<E> newTop = this.left;
         AVLNode<E> movedNode = this.getLeftSubTree().getRightSubTree();
         int newTopPosition = this.relativePosition + this.getOffset(newTop);
         int myNewPosition = -newTop.relativePosition;
         int movedPosition = this.getOffset(newTop) + this.getOffset(movedNode);
         this.setLeft(movedNode, newTop);
         newTop.setRight(this, (AVLNode)null);
         this.setOffset(newTop, newTopPosition);
         this.setOffset(this, myNewPosition);
         this.setOffset(movedNode, movedPosition);
         return newTop;
      }

      private void setLeft(AVLNode<E> node, AVLNode<E> previous) {
         this.leftIsPrevious = node == null;
         this.left = this.leftIsPrevious ? previous : node;
         this.recalcHeight();
      }

      private int setOffset(AVLNode<E> node, int newOffset) {
         if (node == null) {
            return 0;
         } else {
            int oldOffset = this.getOffset(node);
            node.relativePosition = newOffset;
            return oldOffset;
         }
      }

      private void setRight(AVLNode<E> node, AVLNode<E> next) {
         this.rightIsNext = node == null;
         this.right = this.rightIsNext ? next : node;
         this.recalcHeight();
      }

      void setValue(E obj) {
         this.value = obj;
      }

      void toArray(Object[] array, int index) {
         array[index] = this.value;
         if (this.getLeftSubTree() != null) {
            this.left.toArray(array, index + this.left.relativePosition);
         }

         if (this.getRightSubTree() != null) {
            this.right.toArray(array, index + this.right.relativePosition);
         }

      }

      public String toString() {
         return "AVLNode(" + this.relativePosition + "," + (this.left != null) + "," + this.value + "," + (this.getRightSubTree() != null) + this.rightIsNext + ")";
      }
   }

   static class TreeListIterator<E> implements ListIterator<E>, OrderedIterator<E> {
      private final TreeList<E> parent;
      private AVLNode<E> next;
      private int nextIndex;
      private AVLNode<E> current;
      private int currentIndex;
      private int expectedModCount;

      protected TreeListIterator(TreeList<E> parent, int fromIndex) {
         this.parent = parent;
         this.expectedModCount = parent.modCount;
         this.next = parent.root == null ? null : parent.root.get(fromIndex);
         this.nextIndex = fromIndex;
         this.currentIndex = -1;
      }

      public void add(E obj) {
         this.checkModCount();
         this.parent.add(this.nextIndex, obj);
         this.current = null;
         this.currentIndex = -1;
         ++this.nextIndex;
         ++this.expectedModCount;
      }

      protected void checkModCount() {
         if (this.parent.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public boolean hasNext() {
         return this.nextIndex < this.parent.size();
      }

      public boolean hasPrevious() {
         return this.nextIndex > 0;
      }

      public E next() {
         this.checkModCount();
         if (!this.hasNext()) {
            throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
         } else {
            if (this.next == null) {
               this.next = this.parent.root.get(this.nextIndex);
            }

            E value = this.next.getValue();
            this.current = this.next;
            this.currentIndex = this.nextIndex++;
            this.next = this.next.next();
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
            if (this.next == null) {
               this.next = this.parent.root.get(this.nextIndex - 1);
            } else {
               this.next = this.next.previous();
            }

            E value = this.next.getValue();
            this.current = this.next;
            this.currentIndex = --this.nextIndex;
            return value;
         }
      }

      public int previousIndex() {
         return this.nextIndex() - 1;
      }

      public void remove() {
         this.checkModCount();
         if (this.currentIndex == -1) {
            throw new IllegalStateException();
         } else {
            this.parent.remove(this.currentIndex);
            if (this.nextIndex != this.currentIndex) {
               --this.nextIndex;
            }

            this.next = null;
            this.current = null;
            this.currentIndex = -1;
            ++this.expectedModCount;
         }
      }

      public void set(E obj) {
         this.checkModCount();
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            this.current.setValue(obj);
         }
      }
   }
}
