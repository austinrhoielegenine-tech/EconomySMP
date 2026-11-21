package org.apache.commons.collections4;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.functors.DefaultEquator;
import org.apache.commons.collections4.list.FixedSizeList;
import org.apache.commons.collections4.list.LazyList;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.collections4.list.TransformedList;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.sequence.CommandVisitor;
import org.apache.commons.collections4.sequence.EditScript;
import org.apache.commons.collections4.sequence.SequencesComparator;

public class ListUtils {
   public static <T> List<T> defaultIfNull(List<T> list, List<T> defaultList) {
      return list == null ? defaultList : list;
   }

   public static <T> List<T> emptyIfNull(List<T> list) {
      return list == null ? Collections.emptyList() : list;
   }

   public static <E> List<E> fixedSizeList(List<E> list) {
      return FixedSizeList.<E>fixedSizeList(list);
   }

   public static <T> T getFirst(List<T> list) {
      return (T)((List)Objects.requireNonNull(list, "list")).get(0);
   }

   public static <T> T getLast(List<T> list) {
      return (T)((List)Objects.requireNonNull(list, "list")).get(list.size() - 1);
   }

   public static int hashCodeForList(Collection<?> list) {
      if (list == null) {
         return 0;
      } else {
         int hashCode = 1;

         for(Object obj : list) {
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
         }

         return hashCode;
      }
   }

   public static <E> int indexOf(List<E> list, Predicate<E> predicate) {
      if (list != null && predicate != null) {
         for(int i = 0; i < list.size(); ++i) {
            E item = (E)list.get(i);
            if (predicate.test(item)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static <E> List<E> intersection(List<? extends E> list1, List<? extends E> list2) {
      List<E> result = new ArrayList();
      List<? extends E> smaller = list1;
      List<? extends E> larger = list2;
      if (list1.size() > list2.size()) {
         smaller = list2;
         larger = list1;
      }

      HashSet<E> hashSet = new HashSet(smaller);

      for(E e : larger) {
         if (hashSet.contains(e)) {
            result.add(e);
            hashSet.remove(e);
         }
      }

      return result;
   }

   public static boolean isEqualList(Collection<?> list1, Collection<?> list2) {
      if (list1 == list2) {
         return true;
      } else if (list1 != null && list2 != null && list1.size() == list2.size()) {
         Iterator<?> it1 = list1.iterator();
         Iterator<?> it2 = list2.iterator();

         while(it1.hasNext() && it2.hasNext()) {
            Object obj1 = it1.next();
            Object obj2 = it2.next();
            if (!Objects.equals(obj1, obj2)) {
               return false;
            }
         }

         return !it1.hasNext() && !it2.hasNext();
      } else {
         return false;
      }
   }

   public static <E> List<E> lazyList(List<E> list, Factory<? extends E> factory) {
      return LazyList.lazyList(list, factory);
   }

   public static <E> List<E> lazyList(List<E> list, Transformer<Integer, ? extends E> transformer) {
      return LazyList.lazyList(list, transformer);
   }

   public static String longestCommonSubsequence(CharSequence charSequenceA, CharSequence charSequenceB) {
      Objects.requireNonNull(charSequenceA, "charSequenceA");
      Objects.requireNonNull(charSequenceB, "charSequenceB");
      List<Character> lcs = longestCommonSubsequence((List)(new CharSequenceAsList(charSequenceA)), (List)(new CharSequenceAsList(charSequenceB)));
      StringBuilder sb = new StringBuilder();

      for(Character ch : lcs) {
         sb.append(ch);
      }

      return sb.toString();
   }

   public static <E> List<E> longestCommonSubsequence(List<E> a, List<E> b) {
      return longestCommonSubsequence(a, b, DefaultEquator.defaultEquator());
   }

   public static <E> List<E> longestCommonSubsequence(List<E> listA, List<E> listB, Equator<? super E> equator) {
      Objects.requireNonNull(listA, "listA");
      Objects.requireNonNull(listB, "listB");
      Objects.requireNonNull(equator, "equator");
      SequencesComparator<E> comparator = new SequencesComparator<E>(listA, listB, equator);
      EditScript<E> script = comparator.getScript();
      LcsVisitor<E> visitor = new LcsVisitor<E>();
      script.visit(visitor);
      return visitor.getSubSequence();
   }

   public static <T> List<List<T>> partition(List<T> list, int size) {
      Objects.requireNonNull(list, "list");
      if (size <= 0) {
         throw new IllegalArgumentException("Size must be greater than 0");
      } else {
         return new Partition<List<T>>(list, size);
      }
   }

   public static <E> List<E> predicatedList(List<E> list, Predicate<E> predicate) {
      return PredicatedList.<E>predicatedList(list, predicate);
   }

   public static <E> List<E> removeAll(Collection<E> collection, Collection<?> remove) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(remove, "remove");
      List<E> list = new ArrayList();

      for(E obj : collection) {
         if (!remove.contains(obj)) {
            list.add(obj);
         }
      }

      return list;
   }

   public static <E> List<E> retainAll(Collection<E> collection, Collection<?> retain) {
      List<E> list = new ArrayList(Math.min(collection.size(), retain.size()));

      for(E obj : collection) {
         if (retain.contains(obj)) {
            list.add(obj);
         }
      }

      return list;
   }

   public static <E> List<E> select(Collection<? extends E> inputCollection, Predicate<? super E> predicate) {
      return (List)CollectionUtils.select(inputCollection, predicate, new ArrayList(inputCollection.size()));
   }

   public static <E> List<E> selectRejected(Collection<? extends E> inputCollection, Predicate<? super E> predicate) {
      return (List)CollectionUtils.selectRejected(inputCollection, predicate, new ArrayList(inputCollection.size()));
   }

   public static <E> List<E> subtract(List<E> list1, List<? extends E> list2) {
      ArrayList<E> result = new ArrayList();
      HashBag<E> bag = new HashBag<E>(list2);

      for(E e : list1) {
         if (!bag.remove(e, 1)) {
            result.add(e);
         }
      }

      return result;
   }

   public static <E> List<E> sum(List<? extends E> list1, List<? extends E> list2) {
      return subtract(union(list1, list2), intersection(list1, list2));
   }

   public static <E> List<E> synchronizedList(List<E> list) {
      return Collections.synchronizedList(list);
   }

   public static <E> List<E> transformedList(List<E> list, Transformer<? super E, ? extends E> transformer) {
      return TransformedList.<E>transformingList(list, transformer);
   }

   public static <E> List<E> union(List<? extends E> list1, List<? extends E> list2) {
      ArrayList<E> result = new ArrayList(list1.size() + list2.size());
      result.addAll(list1);
      result.addAll(list2);
      return result;
   }

   public static <E> List<E> unmodifiableList(List<? extends E> list) {
      return UnmodifiableList.<E>unmodifiableList(list);
   }

   private ListUtils() {
   }

   private static final class CharSequenceAsList extends AbstractList<Character> {
      private final CharSequence sequence;

      CharSequenceAsList(CharSequence sequence) {
         this.sequence = sequence;
      }

      public Character get(int index) {
         return this.sequence.charAt(index);
      }

      public int size() {
         return this.sequence.length();
      }
   }

   private static final class LcsVisitor<E> implements CommandVisitor<E> {
      private final ArrayList<E> sequence = new ArrayList();

      LcsVisitor() {
      }

      public List<E> getSubSequence() {
         return this.sequence;
      }

      public void visitDeleteCommand(E object) {
      }

      public void visitInsertCommand(E object) {
      }

      public void visitKeepCommand(E object) {
         this.sequence.add(object);
      }
   }

   private static final class Partition<T> extends AbstractList<List<T>> {
      private final List<T> list;
      private final int size;

      private Partition(List<T> list, int size) {
         this.list = list;
         this.size = size;
      }

      public List<T> get(int index) {
         int listSize = this.size();
         if (index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
         } else if (index >= listSize) {
            throw new IndexOutOfBoundsException("Index " + index + " must be less than size " + listSize);
         } else {
            int start = index * this.size;
            int end = Math.min(start + this.size, this.list.size());
            return this.list.subList(start, end);
         }
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public int size() {
         return (int)Math.ceil((double)this.list.size() / (double)this.size);
      }
   }
}
