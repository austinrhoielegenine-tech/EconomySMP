package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.collections4.ComparatorUtils;

public class ReverseComparator<E> implements Comparator<E>, Serializable {
   private static final long serialVersionUID = 2858887242028539265L;
   private final Comparator<? super E> comparator;

   public ReverseComparator() {
      this((Comparator)null);
   }

   public ReverseComparator(Comparator<? super E> comparator) {
      this.comparator = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
   }

   public int compare(E obj1, E obj2) {
      return this.comparator.compare(obj2, obj1);
   }

   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (null == object) {
         return false;
      } else if (object.getClass().equals(this.getClass())) {
         ReverseComparator<?> thatrc = (ReverseComparator)object;
         return this.comparator.equals(thatrc.comparator);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return "ReverseComparator".hashCode() ^ this.comparator.hashCode();
   }
}
