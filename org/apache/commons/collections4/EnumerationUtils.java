package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.IteratorIterable;

public class EnumerationUtils {
   public static <T> Iterable<T> asIterable(Enumeration<T> enumeration) {
      return new IteratorIterable<T>(new EnumerationIterator(enumeration));
   }

   public static <T> T get(Enumeration<T> e, int index) {
      CollectionUtils.checkIndexBounds(index);
      int i = index;

      while(e.hasMoreElements()) {
         --i;
         if (i == -1) {
            return (T)e.nextElement();
         }

         e.nextElement();
      }

      throw new IndexOutOfBoundsException("Entry does not exist: " + i);
   }

   public static <E> List<E> toList(Enumeration<? extends E> enumeration) {
      return IteratorUtils.<E>toList(new EnumerationIterator(enumeration));
   }

   public static List<String> toList(StringTokenizer stringTokenizer) {
      List<String> result = new ArrayList(stringTokenizer.countTokens());

      while(stringTokenizer.hasMoreTokens()) {
         result.add(stringTokenizer.nextToken());
      }

      return result;
   }

   public static <E> Set<E> toSet(Enumeration<? extends E> enumeration) {
      return IteratorUtils.<E>toSet(new EnumerationIterator(enumeration));
   }

   private EnumerationUtils() {
   }
}
