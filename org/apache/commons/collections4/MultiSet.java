package org.apache.commons.collections4;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface MultiSet<E> extends Collection<E> {
   boolean add(E var1);

   int add(E var1, int var2);

   boolean containsAll(Collection<?> var1);

   Set<Entry<E>> entrySet();

   boolean equals(Object var1);

   int getCount(Object var1);

   int hashCode();

   Iterator<E> iterator();

   boolean remove(Object var1);

   int remove(Object var1, int var2);

   boolean removeAll(Collection<?> var1);

   boolean retainAll(Collection<?> var1);

   int setCount(E var1, int var2);

   int size();

   Set<E> uniqueSet();

   public interface Entry<E> {
      boolean equals(Object var1);

      int getCount();

      E getElement();

      int hashCode();
   }
}
