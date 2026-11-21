package org.apache.commons.collections4.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GrowthList<E> extends AbstractSerializableListDecorator<E> {
   private static final long serialVersionUID = -3620001881672L;

   public static <E> GrowthList<E> growthList(List<E> list) {
      return new GrowthList<E>(list);
   }

   public GrowthList() {
      super(new ArrayList());
   }

   public GrowthList(int initialCapacity) {
      super(new ArrayList(initialCapacity));
   }

   protected GrowthList(List<E> list) {
      super(list);
   }

   public void add(int index, E element) {
      int size = this.decorated().size();
      if (index > size) {
         this.decorated().addAll(Collections.nCopies(index - size, (Object)null));
      }

      this.decorated().add(index, element);
   }

   public boolean addAll(int index, Collection<? extends E> coll) {
      int size = this.decorated().size();
      boolean result = false;
      if (index > size) {
         this.decorated().addAll(Collections.nCopies(index - size, (Object)null));
         result = true;
      }

      return this.decorated().addAll(index, coll) || result;
   }

   public E set(int index, E element) {
      int size = this.decorated().size();
      if (index >= size) {
         this.decorated().addAll(Collections.nCopies(index - size + 1, (Object)null));
      }

      return (E)this.decorated().set(index, element);
   }
}
