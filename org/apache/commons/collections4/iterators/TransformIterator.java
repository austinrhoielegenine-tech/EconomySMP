package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import org.apache.commons.collections4.Transformer;

public class TransformIterator<I, O> implements Iterator<O> {
   private Iterator<? extends I> iterator;
   private Transformer<? super I, ? extends O> transformer;

   public TransformIterator() {
   }

   public TransformIterator(Iterator<? extends I> iterator) {
      this.iterator = iterator;
   }

   public TransformIterator(Iterator<? extends I> iterator, Transformer<? super I, ? extends O> transformer) {
      this.iterator = iterator;
      this.transformer = transformer;
   }

   public Iterator<? extends I> getIterator() {
      return this.iterator;
   }

   public Transformer<? super I, ? extends O> getTransformer() {
      return this.transformer;
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public O next() {
      return (O)this.transform(this.iterator.next());
   }

   public void remove() {
      this.iterator.remove();
   }

   public void setIterator(Iterator<? extends I> iterator) {
      this.iterator = iterator;
   }

   public void setTransformer(Transformer<? super I, ? extends O> transformer) {
      this.transformer = transformer;
   }

   protected O transform(I source) {
      return this.transformer.apply(source);
   }
}
