package net.dv8tion.jda.internal.utils.tuple;

public final class ImmutablePair<L, R> extends Pair<L, R> {
   public final L left;
   public final R right;

   public static <L, R> ImmutablePair<L, R> of(L left, R right) {
      return new ImmutablePair<L, R>(left, right);
   }

   public ImmutablePair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }
}
