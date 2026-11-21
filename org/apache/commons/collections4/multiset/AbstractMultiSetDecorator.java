package org.apache.commons.collections4.multiset;

import java.util.Set;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

public abstract class AbstractMultiSetDecorator<E> extends AbstractCollectionDecorator<E> implements MultiSet<E> {
   private static final long serialVersionUID = 20150610L;

   protected AbstractMultiSetDecorator() {
   }

   protected AbstractMultiSetDecorator(MultiSet<E> multiset) {
      super(multiset);
   }

   public int add(E object, int count) {
      return this.decorated().add(object, count);
   }

   protected MultiSet<E> decorated() {
      return (MultiSet)super.decorated();
   }

   public Set<MultiSet.Entry<E>> entrySet() {
      return this.decorated().entrySet();
   }

   public boolean equals(Object object) {
      return object == this || this.decorated().equals(object);
   }

   public int getCount(Object object) {
      return this.decorated().getCount(object);
   }

   public int hashCode() {
      return this.decorated().hashCode();
   }

   public int remove(Object object, int count) {
      return this.decorated().remove(object, count);
   }

   public int setCount(E object, int count) {
      return this.decorated().setCount(object, count);
   }

   public Set<E> uniqueSet() {
      return this.decorated().uniqueSet();
   }
}
