package org.apache.commons.collections4;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.collections4.set.PredicatedNavigableSet;
import org.apache.commons.collections4.set.PredicatedSet;
import org.apache.commons.collections4.set.PredicatedSortedSet;
import org.apache.commons.collections4.set.TransformedNavigableSet;
import org.apache.commons.collections4.set.TransformedSet;
import org.apache.commons.collections4.set.TransformedSortedSet;
import org.apache.commons.collections4.set.UnmodifiableNavigableSet;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.set.UnmodifiableSortedSet;

public class SetUtils {
   public static final SortedSet EMPTY_SORTED_SET = UnmodifiableSortedSet.unmodifiableSortedSet(new TreeSet());

   public static <E> SetView<E> difference(final Set<? extends E> setA, final Set<? extends E> setB) {
      Objects.requireNonNull(setA, "setA");
      Objects.requireNonNull(setB, "setB");
      final Predicate<E> notContainedInB = (object) -> !setB.contains(object);
      return new SetView<E>() {
         public boolean contains(Object o) {
            return setA.contains(o) && !setB.contains(o);
         }

         public Iterator<E> createIterator() {
            return IteratorUtils.<E>filteredIterator(setA.iterator(), notContainedInB);
         }
      };
   }

   public static <E> SetView<E> disjunction(final Set<? extends E> setA, final Set<? extends E> setB) {
      Objects.requireNonNull(setA, "setA");
      Objects.requireNonNull(setB, "setB");
      final SetView<E> aMinusB = difference(setA, setB);
      final SetView<E> bMinusA = difference(setB, setA);
      return new SetView<E>() {
         public boolean contains(Object o) {
            return setA.contains(o) ^ setB.contains(o);
         }

         public Iterator<E> createIterator() {
            return IteratorUtils.<E>chainedIterator(aMinusB.iterator(), bMinusA.iterator());
         }

         public boolean isEmpty() {
            return aMinusB.isEmpty() && bMinusA.isEmpty();
         }

         public int size() {
            return aMinusB.size() + bMinusA.size();
         }
      };
   }

   public static <T> Set<T> emptyIfNull(Set<T> set) {
      return set == null ? Collections.emptySet() : set;
   }

   public static <E> Set<E> emptySet() {
      return Collections.emptySet();
   }

   public static <E> SortedSet<E> emptySortedSet() {
      return EMPTY_SORTED_SET;
   }

   public static <T> int hashCodeForSet(Collection<T> set) {
      if (set == null) {
         return 0;
      } else {
         int hashCode = 0;

         for(T obj : set) {
            if (obj != null) {
               hashCode += obj.hashCode();
            }
         }

         return hashCode;
      }
   }

   public static <E> HashSet<E> hashSet(E... items) {
      return items == null ? null : new HashSet(Arrays.asList(items));
   }

   public static <E> SetView<E> intersection(final Set<? extends E> setA, final Set<? extends E> setB) {
      Objects.requireNonNull(setA, "setA");
      Objects.requireNonNull(setB, "setB");
      return new SetView<E>() {
         public boolean contains(Object o) {
            return setA.contains(o) && setB.contains(o);
         }

         public Iterator<E> createIterator() {
            Iterator var10000 = setA.iterator();
            Set var10001 = setB;
            Objects.requireNonNull(var10001);
            return IteratorUtils.<E>filteredIterator(var10000, var10001::contains);
         }
      };
   }

   public static boolean isEqualSet(Collection<?> set1, Collection<?> set2) {
      if (set1 == set2) {
         return true;
      } else {
         return set1 != null && set2 != null && set1.size() == set2.size() ? set1.containsAll(set2) : false;
      }
   }

   public static <E> Set<E> newIdentityHashSet() {
      return Collections.newSetFromMap(new IdentityHashMap());
   }

   public static <E> Set<E> orderedSet(Set<E> set) {
      return ListOrderedSet.listOrderedSet(set);
   }

   public static <E> SortedSet<E> predicatedNavigableSet(NavigableSet<E> set, Predicate<? super E> predicate) {
      return PredicatedNavigableSet.<E>predicatedNavigableSet(set, predicate);
   }

   public static <E> Set<E> predicatedSet(Set<E> set, Predicate<? super E> predicate) {
      return PredicatedSet.<E>predicatedSet(set, predicate);
   }

   public static <E> SortedSet<E> predicatedSortedSet(SortedSet<E> set, Predicate<? super E> predicate) {
      return PredicatedSortedSet.<E>predicatedSortedSet(set, predicate);
   }

   public static <E> Set<E> synchronizedSet(Set<E> set) {
      return Collections.synchronizedSet(set);
   }

   public static <E> SortedSet<E> synchronizedSortedSet(SortedSet<E> set) {
      return Collections.synchronizedSortedSet(set);
   }

   public static <E> SortedSet<E> transformedNavigableSet(NavigableSet<E> set, Transformer<? super E, ? extends E> transformer) {
      return TransformedNavigableSet.<E>transformingNavigableSet(set, transformer);
   }

   public static <E> Set<E> transformedSet(Set<E> set, Transformer<? super E, ? extends E> transformer) {
      return TransformedSet.<E>transformingSet(set, transformer);
   }

   public static <E> SortedSet<E> transformedSortedSet(SortedSet<E> set, Transformer<? super E, ? extends E> transformer) {
      return TransformedSortedSet.<E>transformingSortedSet(set, transformer);
   }

   public static <E> SetView<E> union(final Set<? extends E> setA, final Set<? extends E> setB) {
      Objects.requireNonNull(setA, "setA");
      Objects.requireNonNull(setB, "setB");
      final SetView<E> bMinusA = difference(setB, setA);
      return new SetView<E>() {
         public boolean contains(Object o) {
            return setA.contains(o) || setB.contains(o);
         }

         public Iterator<E> createIterator() {
            return IteratorUtils.<E>chainedIterator(setA.iterator(), bMinusA.iterator());
         }

         public boolean isEmpty() {
            return setA.isEmpty() && setB.isEmpty();
         }

         public int size() {
            return setA.size() + bMinusA.size();
         }
      };
   }

   public static <E> SortedSet<E> unmodifiableNavigableSet(NavigableSet<E> set) {
      return UnmodifiableNavigableSet.<E>unmodifiableNavigableSet(set);
   }

   public static <E> Set<E> unmodifiableSet(E... items) {
      return items == null ? null : UnmodifiableSet.unmodifiableSet(hashSet(items));
   }

   public static <E> Set<E> unmodifiableSet(Set<? extends E> set) {
      return UnmodifiableSet.<E>unmodifiableSet(set);
   }

   public static <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> set) {
      return UnmodifiableSortedSet.<E>unmodifiableSortedSet(set);
   }

   private SetUtils() {
   }

   public abstract static class SetView<E> extends AbstractSet<E> {
      public <S extends Set<E>> void copyInto(S set) {
         CollectionUtils.addAll(set, this);
      }

      protected abstract Iterator<E> createIterator();

      public Iterator<E> iterator() {
         return IteratorUtils.<E>unmodifiableIterator(this.createIterator());
      }

      public int size() {
         return IteratorUtils.size(this.iterator());
      }

      public Set<E> toSet() {
         Set<E> set = new HashSet(this.size());
         this.copyInto(set);
         return set;
      }
   }
}
