package net.dv8tion.jda.internal.utils.tuple;

public class MutablePair<L, R> extends Pair<L, R> {
   public L left;
   public R right;

   public static <L, R> MutablePair<L, R> of(L left, R right) {
      return new MutablePair<L, R>(left, right);
   }

   public MutablePair() {
   }

   public MutablePair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public void setLeft(L left) {
      this.left = left;
   }

   public R getRight() {
      return this.right;
   }

   public void setRight(R right) {
      this.right = right;
   }
}
