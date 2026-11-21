package org.apache.commons.collections4.iterators;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<E> implements Iterator<E> {
   private final Collection<? super E> collection;
   private Enumeration<? extends E> enumeration;
   private E last;

   public EnumerationIterator() {
      this((Enumeration)null, (Collection)null);
   }

   public EnumerationIterator(Enumeration<? extends E> enumeration) {
      this(enumeration, (Collection)null);
   }

   public EnumerationIterator(Enumeration<? extends E> enumeration, Collection<? super E> collection) {
      this.enumeration = enumeration;
      this.collection = collection;
      this.last = null;
   }

   public Enumeration<? extends E> getEnumeration() {
      return this.enumeration;
   }

   public boolean hasNext() {
      return this.enumeration.hasMoreElements();
   }

   public E next() {
      this.last = (E)this.enumeration.nextElement();
      return this.last;
   }

   public void remove() {
      if (this.collection == null) {
         throw new UnsupportedOperationException("No Collection associated with this Iterator");
      } else if (this.last == null) {
         throw new IllegalStateException("next() must have been called for remove() to function");
      } else {
         this.collection.remove(this.last);
      }
   }

   public void setEnumeration(Enumeration<? extends E> enumeration) {
      this.enumeration = enumeration;
   }
}
