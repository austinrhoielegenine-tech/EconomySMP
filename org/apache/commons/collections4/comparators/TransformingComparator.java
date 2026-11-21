package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Transformer;

public class TransformingComparator<I, O> implements Comparator<I>, Serializable {
   private static final long serialVersionUID = 3456940356043606220L;
   private final Comparator<O> decorated;
   private final Transformer<? super I, ? extends O> transformer;

   public TransformingComparator(Transformer<? super I, ? extends O> transformer) {
      this(transformer, ComparatorUtils.NATURAL_COMPARATOR);
   }

   public TransformingComparator(Transformer<? super I, ? extends O> transformer, Comparator<O> decorated) {
      this.decorated = decorated;
      this.transformer = transformer;
   }

   public int compare(I obj1, I obj2) {
      O value1 = this.transformer.apply(obj1);
      O value2 = this.transformer.apply(obj2);
      return this.decorated.compare(value1, value2);
   }

   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (null == object) {
         return false;
      } else if (!object.getClass().equals(this.getClass())) {
         return false;
      } else {
         TransformingComparator<?, ?> comp = (TransformingComparator)object;
         return Objects.equals(this.decorated, comp.decorated) && Objects.equals(this.transformer, comp.transformer);
      }
   }

   public int hashCode() {
      int total = 17;
      total = total * 37 + (this.decorated == null ? 0 : this.decorated.hashCode());
      return total * 37 + (this.transformer == null ? 0 : this.transformer.hashCode());
   }
}
