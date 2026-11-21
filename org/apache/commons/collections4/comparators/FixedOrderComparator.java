package org.apache.commons.collections4.comparators;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FixedOrderComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 82794675842863201L;
   private final Map<T, Integer> map = new HashMap();
   private int counter;
   private boolean isLocked;
   private UnknownObjectBehavior unknownObjectBehavior;

   public FixedOrderComparator() {
      this.unknownObjectBehavior = FixedOrderComparator.UnknownObjectBehavior.EXCEPTION;
   }

   public FixedOrderComparator(List<T> items) {
      this.unknownObjectBehavior = FixedOrderComparator.UnknownObjectBehavior.EXCEPTION;

      for(T t : (List)Objects.requireNonNull(items, "items")) {
         this.add(t);
      }

   }

   public FixedOrderComparator(T... items) {
      this.unknownObjectBehavior = FixedOrderComparator.UnknownObjectBehavior.EXCEPTION;

      for(T item : Objects.requireNonNull(items, "items")) {
         this.add(item);
      }

   }

   public boolean add(T obj) {
      this.checkLocked();
      Integer position = (Integer)this.map.put(obj, this.counter++);
      return position == null;
   }

   public boolean addAsEqual(T existingObj, T newObj) {
      this.checkLocked();
      Integer position = (Integer)this.map.get(existingObj);
      if (position == null) {
         throw new IllegalArgumentException(existingObj + " not known to " + this);
      } else {
         Integer result = (Integer)this.map.put(newObj, position);
         return result == null;
      }
   }

   protected void checkLocked() {
      if (this.isLocked()) {
         throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
      }
   }

   public int compare(T obj1, T obj2) {
      this.isLocked = true;
      Integer position1 = (Integer)this.map.get(obj1);
      Integer position2 = (Integer)this.map.get(obj2);
      if (position1 != null && position2 != null) {
         return position1.compareTo(position2);
      } else {
         switch (this.unknownObjectBehavior.ordinal()) {
            case 0:
               return position1 == null ? (position2 == null ? 0 : -1) : 1;
            case 1:
               return position1 == null ? (position2 == null ? 0 : 1) : -1;
            case 2:
               Object unknownObj = position1 == null ? obj1 : obj2;
               throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
            default:
               throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + this.unknownObjectBehavior);
         }
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         FixedOrderComparator<?> other = (FixedOrderComparator)obj;
         return this.counter == other.counter && this.isLocked == other.isLocked && Objects.equals(this.map, other.map) && this.unknownObjectBehavior == other.unknownObjectBehavior;
      }
   }

   public UnknownObjectBehavior getUnknownObjectBehavior() {
      return this.unknownObjectBehavior;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.counter, this.isLocked, this.map, this.unknownObjectBehavior});
   }

   public boolean isLocked() {
      return this.isLocked;
   }

   public void setUnknownObjectBehavior(UnknownObjectBehavior unknownObjectBehavior) {
      this.checkLocked();
      this.unknownObjectBehavior = (UnknownObjectBehavior)Objects.requireNonNull(unknownObjectBehavior, "unknownObjectBehavior");
   }

   public static enum UnknownObjectBehavior {
      BEFORE,
      AFTER,
      EXCEPTION;

      // $FF: synthetic method
      private static UnknownObjectBehavior[] $values() {
         return new UnknownObjectBehavior[]{BEFORE, AFTER, EXCEPTION};
      }
   }
}
