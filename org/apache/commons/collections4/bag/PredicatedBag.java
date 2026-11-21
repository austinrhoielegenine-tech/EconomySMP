package org.apache.commons.collections4.bag;

import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.collection.PredicatedCollection;

public class PredicatedBag<E> extends PredicatedCollection<E> implements Bag<E> {
   private static final long serialVersionUID = -2575833140344736876L;

   public static <E> PredicatedBag<E> predicatedBag(Bag<E> bag, Predicate<? super E> predicate) {
      return new PredicatedBag<E>(bag, predicate);
   }

   protected PredicatedBag(Bag<E> bag, Predicate<? super E> predicate) {
      super(bag, predicate);
   }

   public boolean add(E object, int count) {
      this.validate(object);
      return this.decorated().add(object, count);
   }

   protected Bag<E> decorated() {
      return (Bag)super.decorated();
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

   public boolean remove(Object object, int count) {
      return this.decorated().remove(object, count);
   }

   public Set<E> uniqueSet() {
      return this.decorated().uniqueSet();
   }
}
