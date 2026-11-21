package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.Predicate;

public final class EqualPredicate<T> extends AbstractPredicate<T> implements Serializable {
   private static final long serialVersionUID = 5633766978029907089L;
   private final T test;
   private final Equator<T> equator;

   public static <T> Predicate<T> equalPredicate(T object) {
      return (Predicate<T>)(object == null ? NullPredicate.nullPredicate() : new EqualPredicate(object));
   }

   public static <T> Predicate<T> equalPredicate(T object, Equator<T> equator) {
      return (Predicate<T>)(object == null ? NullPredicate.nullPredicate() : new EqualPredicate(object, equator));
   }

   public EqualPredicate(T object) {
      this(object, (Equator)null);
   }

   public EqualPredicate(T test, Equator<T> equator) {
      this.test = test;
      this.equator = equator;
   }

   public Object getValue() {
      return this.test;
   }

   public boolean test(T object) {
      return this.equator != null ? this.equator.equate(this.test, object) : Objects.equals(this.test, object);
   }
}
