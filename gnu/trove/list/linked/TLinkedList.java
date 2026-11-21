package gnu.trove.list.linked;

import gnu.trove.list.TLinkable;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TLinkedList<T extends TLinkable<T>> extends AbstractSequentialList<T> implements Externalizable {
   static final long serialVersionUID = 1L;
   protected T _head;
   protected T _tail;
   protected int _size = 0;

   public ListIterator<T> listIterator(int index) {
      return new IteratorImpl(index);
   }

   public int size() {
      return this._size;
   }

   public void add(int index, T linkable) {
      if (index >= 0 && index <= this.size()) {
         this.insert(index, linkable);
      } else {
         throw new IndexOutOfBoundsException("index:" + index);
      }
   }

   public boolean add(T linkable) {
      this.insert(this._size, linkable);
      return true;
   }

   public void addFirst(T linkable) {
      this.insert(0, linkable);
   }

   public void addLast(T linkable) {
      this.insert(this.size(), linkable);
   }

   public void clear() {
      if (null != this._head) {
         for(TLinkable<T> link = this._head.getNext(); link != null; link = link.getNext()) {
            TLinkable<T> prev = link.getPrevious();
            prev.setNext((TLinkable)null);
            link.setPrevious((TLinkable)null);
         }

         this._head = this._tail = null;
      }

      this._size = 0;
   }

   public Object[] toArray() {
      Object[] o = new Object[this._size];
      int i = 0;

      for(TLinkable link = this._head; link != null; link = link.getNext()) {
         o[i++] = link;
      }

      return o;
   }

   public Object[] toUnlinkedArray() {
      Object[] o = new Object[this._size];
      int i = 0;

      for(TLinkable<T> link = this._head; link != null; ++i) {
         o[i] = link;
         TLinkable<T> tmp = link;
         link = link.getNext();
         tmp.setNext((TLinkable)null);
         tmp.setPrevious((TLinkable)null);
      }

      this._size = 0;
      this._head = this._tail = null;
      return o;
   }

   public T[] toUnlinkedArray(T[] a) {
      int size = this.size();
      if (a.length < size) {
         a = (TLinkable[])Array.newInstance(a.getClass().getComponentType(), size);
      }

      int i = 0;

      for(T link = this._head; link != null; ++i) {
         a[i] = link;
         T tmp = link;
         link = link.getNext();
         tmp.setNext((TLinkable)null);
         tmp.setPrevious((TLinkable)null);
      }

      this._size = 0;
      this._head = this._tail = null;
      return a;
   }

   public boolean contains(Object o) {
      for(TLinkable<T> link = this._head; link != null; link = link.getNext()) {
         if (o.equals(link)) {
            return true;
         }
      }

      return false;
   }

   public T get(int index) {
      if (index >= 0 && index < this._size) {
         if (index > this._size >> 1) {
            int position = this._size - 1;

            T node;
            for(node = this._tail; position > index; --position) {
               node = node.getPrevious();
            }

            return node;
         } else {
            int position = 0;

            T node;
            for(node = this._head; position < index; ++position) {
               node = node.getNext();
            }

            return node;
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this._size);
      }
   }

   public T getFirst() {
      return this._head;
   }

   public T getLast() {
      return this._tail;
   }

   public T getNext(T current) {
      return (T)current.getNext();
   }

   public T getPrevious(T current) {
      return (T)current.getPrevious();
   }

   public T removeFirst() {
      T o = this._head;
      if (o == null) {
         return null;
      } else {
         T n = o.getNext();
         o.setNext((TLinkable)null);
         if (null != n) {
            n.setPrevious((TLinkable)null);
         }

         this._head = n;
         if (--this._size == 0) {
            this._tail = null;
         }

         return o;
      }
   }

   public T removeLast() {
      T o = this._tail;
      if (o == null) {
         return null;
      } else {
         T prev = o.getPrevious();
         o.setPrevious((TLinkable)null);
         if (null != prev) {
            prev.setNext((TLinkable)null);
         }

         this._tail = prev;
         if (--this._size == 0) {
            this._head = null;
         }

         return o;
      }
   }

   protected void insert(int index, T linkable) {
      if (this._size == 0) {
         this._head = this._tail = linkable;
      } else if (index == 0) {
         linkable.setNext(this._head);
         this._head.setPrevious(linkable);
         this._head = linkable;
      } else if (index == this._size) {
         this._tail.setNext(linkable);
         linkable.setPrevious(this._tail);
         this._tail = linkable;
      } else {
         T node = this.get(index);
         T before = node.getPrevious();
         if (before != null) {
            before.setNext(linkable);
         }

         linkable.setPrevious(before);
         linkable.setNext(node);
         node.setPrevious(linkable);
      }

      ++this._size;
   }

   public boolean remove(Object o) {
      if (!(o instanceof TLinkable)) {
         return false;
      } else {
         TLinkable<T> link = (TLinkable)o;
         T p = link.getPrevious();
         T n = link.getNext();
         if (n == null && p == null) {
            if (o != this._head) {
               return false;
            }

            this._head = this._tail = null;
         } else if (n == null) {
            link.setPrevious((TLinkable)null);
            p.setNext((TLinkable)null);
            this._tail = p;
         } else if (p == null) {
            link.setNext((TLinkable)null);
            n.setPrevious((TLinkable)null);
            this._head = n;
         } else {
            p.setNext(n);
            n.setPrevious(p);
            link.setNext((TLinkable)null);
            link.setPrevious((TLinkable)null);
         }

         --this._size;
         return true;
      }
   }

   public void addBefore(T current, T newElement) {
      if (current == this._head) {
         this.addFirst(newElement);
      } else if (current == null) {
         this.addLast(newElement);
      } else {
         T p = current.getPrevious();
         newElement.setNext(current);
         p.setNext(newElement);
         newElement.setPrevious(p);
         current.setPrevious(newElement);
         ++this._size;
      }

   }

   public void addAfter(T current, T newElement) {
      if (current == this._tail) {
         this.addLast(newElement);
      } else if (current == null) {
         this.addFirst(newElement);
      } else {
         T n = current.getNext();
         newElement.setPrevious(current);
         newElement.setNext(n);
         current.setNext(newElement);
         n.setPrevious(newElement);
         ++this._size;
      }

   }

   public boolean forEachValue(TObjectProcedure<T> procedure) {
      for(T node = this._head; node != null; node = node.getNext()) {
         boolean keep_going = procedure.execute(node);
         if (!keep_going) {
            return false;
         }
      }

      return true;
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeInt(this._size);
      out.writeObject(this._head);
      out.writeObject(this._tail);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._size = in.readInt();
      this._head = (T)(in.readObject());
      this._tail = (T)(in.readObject());
   }

   protected final class IteratorImpl implements ListIterator<T> {
      private int _nextIndex = 0;
      private T _next;
      private T _lastReturned;

      IteratorImpl(int position) {
         if (position >= 0 && position <= TLinkedList.this._size) {
            this._nextIndex = position;
            if (position == 0) {
               this._next = TLinkedList.this._head;
            } else if (position == TLinkedList.this._size) {
               this._next = (T)null;
            } else if (position < TLinkedList.this._size >> 1) {
               int pos = 0;

               for(this._next = TLinkedList.this._head; pos < position; ++pos) {
                  this._next = (T)this._next.getNext();
               }
            } else {
               int pos = TLinkedList.this._size - 1;

               for(this._next = TLinkedList.this._tail; pos > position; --pos) {
                  this._next = (T)this._next.getPrevious();
               }
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final void add(T linkable) {
         this._lastReturned = (T)null;
         ++this._nextIndex;
         if (TLinkedList.this._size == 0) {
            TLinkedList.this.add(linkable);
         } else {
            TLinkedList.this.addBefore(this._next, linkable);
         }

      }

      public final boolean hasNext() {
         return this._nextIndex != TLinkedList.this._size;
      }

      public final boolean hasPrevious() {
         return this._nextIndex != 0;
      }

      public final T next() {
         if (this._nextIndex == TLinkedList.this._size) {
            throw new NoSuchElementException();
         } else {
            this._lastReturned = this._next;
            this._next = (T)this._next.getNext();
            ++this._nextIndex;
            return this._lastReturned;
         }
      }

      public final int nextIndex() {
         return this._nextIndex;
      }

      public final T previous() {
         if (this._nextIndex == 0) {
            throw new NoSuchElementException();
         } else {
            if (this._nextIndex == TLinkedList.this._size) {
               this._lastReturned = this._next = TLinkedList.this._tail;
            } else {
               this._lastReturned = this._next = (T)this._next.getPrevious();
            }

            --this._nextIndex;
            return this._lastReturned;
         }
      }

      public final int previousIndex() {
         return this._nextIndex - 1;
      }

      public final void remove() {
         if (this._lastReturned == null) {
            throw new IllegalStateException("must invoke next or previous before invoking remove");
         } else {
            if (this._lastReturned != this._next) {
               --this._nextIndex;
            }

            this._next = (T)this._lastReturned.getNext();
            TLinkedList.this.remove(this._lastReturned);
            this._lastReturned = (T)null;
         }
      }

      public final void set(T linkable) {
         if (this._lastReturned == null) {
            throw new IllegalStateException();
         } else {
            this.swap(this._lastReturned, linkable);
            this._lastReturned = linkable;
         }
      }

      private void swap(T from, T to) {
         T from_p = (T)from.getPrevious();
         T from_n = (T)from.getNext();
         T to_p = (T)to.getPrevious();
         T to_n = (T)to.getNext();
         if (from_n == to) {
            if (from_p != null) {
               from_p.setNext(to);
            }

            to.setPrevious(from_p);
            to.setNext(from);
            from.setPrevious(to);
            from.setNext(to_n);
            if (to_n != null) {
               to_n.setPrevious(from);
            }
         } else if (to_n == from) {
            if (to_p != null) {
               to_p.setNext(to);
            }

            to.setPrevious(from);
            to.setNext(from_n);
            from.setPrevious(to_p);
            from.setNext(to);
            if (from_n != null) {
               from_n.setPrevious(to);
            }
         } else {
            from.setNext(to_n);
            from.setPrevious(to_p);
            if (to_p != null) {
               to_p.setNext(from);
            }

            if (to_n != null) {
               to_n.setPrevious(from);
            }

            to.setNext(from_n);
            to.setPrevious(from_p);
            if (from_p != null) {
               from_p.setNext(to);
            }

            if (from_n != null) {
               from_n.setPrevious(to);
            }
         }

         if (TLinkedList.this._head == from) {
            TLinkedList.this._head = to;
         } else if (TLinkedList.this._head == to) {
            TLinkedList.this._head = from;
         }

         if (TLinkedList.this._tail == from) {
            TLinkedList.this._tail = to;
         } else if (TLinkedList.this._tail == to) {
            TLinkedList.this._tail = from;
         }

         if (this._lastReturned == from) {
            this._lastReturned = to;
         } else if (this._lastReturned == to) {
            this._lastReturned = from;
         }

         if (this._next == from) {
            this._next = to;
         } else if (this._next == to) {
            this._next = from;
         }

      }
   }
}
