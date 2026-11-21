package org.apache.commons.collections4.set;

import java.util.Set;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.TransformedCollection;

public class TransformedSet<E> extends TransformedCollection<E> implements Set<E> {
   private static final long serialVersionUID = 306127383500410386L;

   public static <E> Set<E> transformedSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
      TransformedSet<E> decorated = new TransformedSet<E>(set, transformer);
      if (!set.isEmpty()) {
         E[] values = (E[])set.toArray();
         set.clear();

         for(E value : values) {
            decorated.decorated().add(transformer.apply(value));
         }
      }

      return decorated;
   }

   public static <E> TransformedSet<E> transformingSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
      return new TransformedSet<E>(set, transformer);
   }

   protected TransformedSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
      super(set, transformer);
   }

   public boolean equals(Object object) {
      return object == this || this.decorated().equals(object);
   }

   public int hashCode() {
      return this.decorated().hashCode();
   }
}
