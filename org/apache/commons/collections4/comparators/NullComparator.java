package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import org.apache.commons.collections4.ComparatorUtils;

public class NullComparator<E> implements Comparator<E>, Serializable {
   private static final long serialVersionUID = -5820772575483504339L;
   private final Comparator<? super E> nonNullComparator;
   private final boolean nullsAreHigh;

   public NullComparator() {
      this(ComparatorUtils.NATURAL_COMPARATOR, true);
   }

   public NullComparator(boolean nullsAreHigh) {
      this(ComparatorUtils.NATURAL_COMPARATOR, nullsAreHigh);
   }

   public NullComparator(Comparator<? super E> nonNullComparator) {
      this(nonNullComparator, true);
   }

   public NullComparator(Comparator<? super E> nonNullComparator, boolean nullsAreHigh) {
      this.nonNullComparator = (Comparator)Objects.requireNonNull(nonNullComparator, "nonNullComparator");
      this.nullsAreHigh = nullsAreHigh;
   }

   public int compare(E o1, E o2) {
      if (o1 == o2) {
         return 0;
      } else if (o1 == null) {
         return this.nullsAreHigh ? 1 : -1;
      } else if (o2 == null) {
         return this.nullsAreHigh ? -1 : 1;
      } else {
         return this.nonNullComparator.compare(o1, o2);
      }
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else if (!obj.getClass().equals(this.getClass())) {
         return false;
      } else {
         NullComparator<?> other = (NullComparator)obj;
         return this.nullsAreHigh == other.nullsAreHigh && this.nonNullComparator.equals(other.nonNullComparator);
      }
   }

   public int hashCode() {
      return (this.nullsAreHigh ? -1 : 1) * this.nonNullComparator.hashCode();
   }
}
