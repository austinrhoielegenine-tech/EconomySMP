package org.apache.commons.collections4.iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public interface IteratorOperations<E> extends Iterator<E> {
   default <C extends Collection<E>> C addTo(C collection) {
      Objects.requireNonNull(collection);
      this.forEachRemaining(collection::add);
      return collection;
   }

   default E removeNext() {
      E result = (E)this.next();
      this.remove();
      return result;
   }

   default <C extends Collection<E>> C toCollection(Supplier<C> collectionSupplier) {
      return (C)this.addTo((Collection)collectionSupplier.get());
   }

   default List<E> toList() {
      return (List)this.toCollection(ArrayList::new);
   }

   default Set<E> toSet() {
      return (Set)this.toCollection(HashSet::new);
   }
}
