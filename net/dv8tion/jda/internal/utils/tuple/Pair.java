package net.dv8tion.jda.internal.utils.tuple;

import java.io.Serializable;
import java.util.Objects;

public abstract class Pair<L, R> implements Serializable {
   public static <L, R> Pair<L, R> of(L left, R right) {
      return new ImmutablePair<L, R>(left, right);
   }

   public abstract L getLeft();

   public abstract R getRight();

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof Pair)) {
         return false;
      } else {
         Pair<?, ?> other = (Pair)obj;
         return Objects.equals(this.getLeft(), other.getLeft()) && Objects.equals(this.getRight(), other.getRight());
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.getLeft()) ^ Objects.hashCode(this.getRight());
   }

   public String toString() {
      return "(" + this.getLeft() + ',' + this.getRight() + ')';
   }
}
