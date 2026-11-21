package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Equator;

public class DefaultEquator<T> implements Equator<T>, Serializable {
   private static final long serialVersionUID = 825802648423525485L;
   public static final DefaultEquator INSTANCE = new DefaultEquator();
   public static final int HASHCODE_NULL = -1;

   public static <T> DefaultEquator<T> defaultEquator() {
      return INSTANCE;
   }

   private DefaultEquator() {
   }

   public boolean equate(T o1, T o2) {
      return Objects.equals(o1, o2);
   }

   public int hash(T o) {
      return o == null ? -1 : o.hashCode();
   }

   private Object readResolve() {
      return INSTANCE;
   }
}
