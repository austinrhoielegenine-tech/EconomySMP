package org.apache.commons.collections4.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.Transformer;

public class TransformedCollection<E> extends AbstractCollectionDecorator<E> {
   private static final long serialVersionUID = 8692300188161871514L;
   protected final Transformer<? super E, ? extends E> transformer;

   public static <E> TransformedCollection<E> transformedCollection(Collection<E> collection, Transformer<? super E, ? extends E> transformer) {
      TransformedCollection<E> decorated = new TransformedCollection<E>(collection, transformer);
      if (!collection.isEmpty()) {
         E[] values = (E[])collection.toArray();
         collection.clear();

         for(E value : values) {
            decorated.decorated().add(transformer.apply(value));
         }
      }

      return decorated;
   }

   public static <E> TransformedCollection<E> transformingCollection(Collection<E> coll, Transformer<? super E, ? extends E> transformer) {
      return new TransformedCollection<E>(coll, transformer);
   }

   protected TransformedCollection(Collection<E> collection, Transformer<? super E, ? extends E> transformer) {
      super(collection);
      this.transformer = (Transformer)Objects.requireNonNull(transformer, "transformer");
   }

   public boolean add(E object) {
      return this.decorated().add(this.transform(object));
   }

   public boolean addAll(Collection<? extends E> coll) {
      return this.decorated().addAll(this.transform(coll));
   }

   protected Collection<E> transform(Collection<? extends E> coll) {
      List<E> list = new ArrayList(coll.size());

      for(E item : coll) {
         list.add(this.transform(item));
      }

      return list;
   }

   protected E transform(E object) {
      return this.transformer.apply(object);
   }
}
