package org.apache.commons.collections4;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.collection.UnmodifiableBoundedCollection;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.collections4.iterators.CollatingIterator;
import org.apache.commons.collections4.iterators.PermutationIterator;

public class CollectionUtils {
   public static final int INDEX_NOT_FOUND = -1;
   public static final String DEFAULT_TOSTRING_PREFIX = "[";
   public static final String DEFAULT_TOSTRING_SUFFIX = "]";
   public static final String COLON = ":";
   public static final String COMMA = ",";
   public static final Collection EMPTY_COLLECTION = Collections.emptyList();

   public static <C> boolean addAll(Collection<C> collection, C... elements) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(elements, "elements");
      boolean changed = false;

      for(C element : elements) {
         changed |= collection.add(element);
      }

      return changed;
   }

   public static <C> boolean addAll(Collection<C> collection, Enumeration<? extends C> enumeration) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(enumeration, "enumeration");

      boolean changed;
      for(changed = false; enumeration.hasMoreElements(); changed |= collection.add(enumeration.nextElement())) {
      }

      return changed;
   }

   public static <C> boolean addAll(Collection<C> collection, Iterable<? extends C> iterable) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(iterable, "iterable");
      return iterable instanceof Collection ? collection.addAll((Collection)iterable) : addAll(collection, iterable.iterator());
   }

   public static <C> boolean addAll(Collection<C> collection, Iterator<? extends C> iterator) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(iterator, "iterator");

      boolean changed;
      for(changed = false; iterator.hasNext(); changed |= collection.add(iterator.next())) {
      }

      return changed;
   }

   public static <T> boolean addIgnoreNull(Collection<T> collection, T object) {
      Objects.requireNonNull(collection, "collection");
      return object != null && collection.add(object);
   }

   /** @deprecated */
   @Deprecated
   public static <O> int cardinality(O obj, Iterable<? super O> collection) {
      return IterableUtils.frequency((Iterable)Objects.requireNonNull(collection, "collection"), obj);
   }

   static void checkIndexBounds(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
      }
   }

   public static <O extends Comparable<? super O>> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b) {
      return collate(a, b, ComparatorUtils.naturalComparator(), true);
   }

   public static <O extends Comparable<? super O>> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b, boolean includeDuplicates) {
      return collate(a, b, ComparatorUtils.naturalComparator(), includeDuplicates);
   }

   public static <O> List<O> collate(Iterable<? extends O> a, Iterable<? extends O> b, Comparator<? super O> c) {
      return collate(a, b, c, true);
   }

   public static <O> List<O> collate(Iterable<? extends O> iterableA, Iterable<? extends O> iterableB, Comparator<? super O> comparator, boolean includeDuplicates) {
      Objects.requireNonNull(iterableA, "iterableA");
      Objects.requireNonNull(iterableB, "iterableB");
      Objects.requireNonNull(comparator, "comparator");
      int totalSize = iterableA instanceof Collection && iterableB instanceof Collection ? Math.max(1, ((Collection)iterableA).size() + ((Collection)iterableB).size()) : 10;
      Iterator<O> iterator = new CollatingIterator<O>(comparator, iterableA.iterator(), iterableB.iterator());
      if (includeDuplicates) {
         return IteratorUtils.<O>toList(iterator, totalSize);
      } else {
         ArrayList<O> mergedList = new ArrayList(totalSize);

         O item;
         for(O lastItem = null; iterator.hasNext(); lastItem = item) {
            item = (O)iterator.next();
            if (lastItem == null || !lastItem.equals(item)) {
               mergedList.add(item);
            }
         }

         mergedList.trimToSize();
         return mergedList;
      }
   }

   public static <I, O, R extends Collection<? super O>> R collect(Iterable<? extends I> inputCollection, Transformer<? super I, ? extends O> transformer, R outputCollection) {
      return (R)(inputCollection != null ? collect(inputCollection.iterator(), transformer, outputCollection) : outputCollection);
   }

   public static <I, O> Collection<O> collect(Iterable<I> inputCollection, Transformer<? super I, ? extends O> transformer) {
      int size = 0;
      if (null != inputCollection) {
         size = inputCollection instanceof Collection ? ((Collection)inputCollection).size() : 0;
      }

      Collection<O> answer = size == 0 ? new ArrayList() : new ArrayList(size);
      return collect(inputCollection, transformer, answer);
   }

   public static <I, O, R extends Collection<? super O>> R collect(Iterator<? extends I> inputIterator, Transformer<? super I, ? extends O> transformer, R outputCollection) {
      if (inputIterator != null && transformer != null) {
         while(inputIterator.hasNext()) {
            I item = (I)inputIterator.next();
            O value = transformer.apply(item);
            outputCollection.add(value);
         }
      }

      return outputCollection;
   }

   public static <I, O> Collection<O> collect(Iterator<I> inputIterator, Transformer<? super I, ? extends O> transformer) {
      return collect(inputIterator, transformer, new ArrayList());
   }

   public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
      Objects.requireNonNull(coll1, "coll1");
      Objects.requireNonNull(coll2, "coll2");
      if (coll2.isEmpty()) {
         return true;
      } else {
         Set<Object> elementsAlreadySeen = new HashSet();

         for(Object nextElement : coll2) {
            if (!elementsAlreadySeen.contains(nextElement)) {
               boolean foundCurrentElement = false;

               for(Object p : coll1) {
                  elementsAlreadySeen.add(p);
                  if (Objects.equals(nextElement, p)) {
                     foundCurrentElement = true;
                     break;
                  }
               }

               if (!foundCurrentElement) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
      Objects.requireNonNull(coll1, "coll1");
      Objects.requireNonNull(coll2, "coll2");
      if (coll1.size() < coll2.size()) {
         for(Object aColl1 : coll1) {
            if (coll2.contains(aColl1)) {
               return true;
            }
         }
      } else {
         for(Object aColl2 : coll2) {
            if (coll1.contains(aColl2)) {
               return true;
            }
         }
      }

      return false;
   }

   public static <T> boolean containsAny(Collection<?> coll1, T... coll2) {
      Objects.requireNonNull(coll1, "coll1");
      Objects.requireNonNull(coll2, "coll2");
      if (coll1.size() < coll2.length) {
         for(Object aColl1 : coll1) {
            if (ArrayUtils.contains(coll2, aColl1)) {
               return true;
            }
         }
      } else {
         for(Object aColl2 : coll2) {
            if (coll1.contains(aColl2)) {
               return true;
            }
         }
      }

      return false;
   }

   /** @deprecated */
   @Deprecated
   public static <C> int countMatches(Iterable<C> input, Predicate<? super C> predicate) {
      return predicate == null ? 0 : (int)IterableUtils.countMatches(input, predicate);
   }

   public static <O> Collection<O> disjunction(Iterable<? extends O> a, Iterable<? extends O> b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);

      for(O obj : helper) {
         helper.setCardinality(obj, helper.max(obj) - helper.min(obj));
      }

      return helper.list();
   }

   public static <T> Collection<T> emptyCollection() {
      return EMPTY_COLLECTION;
   }

   public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
      return collection == null ? emptyCollection() : collection;
   }

   /** @deprecated */
   @Deprecated
   public static <C> boolean exists(Iterable<C> input, Predicate<? super C> predicate) {
      return predicate != null && IterableUtils.matchesAny(input, predicate);
   }

   public static <E> E extractSingleton(Collection<E> collection) {
      Objects.requireNonNull(collection, "collection");
      if (collection.size() != 1) {
         throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
      } else {
         return (E)collection.iterator().next();
      }
   }

   public static <T> boolean filter(Iterable<T> collection, Predicate<? super T> predicate) {
      boolean result = false;
      if (collection != null && predicate != null) {
         Iterator<T> it = collection.iterator();

         while(it.hasNext()) {
            if (!predicate.test(it.next())) {
               it.remove();
               result = true;
            }
         }
      }

      return result;
   }

   public static <T> boolean filterInverse(Iterable<T> collection, Predicate<? super T> predicate) {
      return filter(collection, predicate == null ? null : PredicateUtils.notPredicate(predicate));
   }

   /** @deprecated */
   @Deprecated
   public static <T> T find(Iterable<T> collection, Predicate<? super T> predicate) {
      return (T)(predicate != null ? IterableUtils.find(collection, predicate) : null);
   }

   /** @deprecated */
   @Deprecated
   public static <T, C extends Closure<? super T>> T forAllButLastDo(Iterable<T> collection, C closure) {
      return (T)(closure != null ? IterableUtils.forEachButLast(collection, closure) : null);
   }

   /** @deprecated */
   @Deprecated
   public static <T, C extends Closure<? super T>> T forAllButLastDo(Iterator<T> iterator, C closure) {
      return (T)(closure != null ? IteratorUtils.forEachButLast(iterator, closure) : null);
   }

   /** @deprecated */
   @Deprecated
   public static <T, C extends Closure<? super T>> C forAllDo(Iterable<T> collection, C closure) {
      if (closure != null) {
         IterableUtils.forEach(collection, closure);
      }

      return closure;
   }

   /** @deprecated */
   @Deprecated
   public static <T, C extends Closure<? super T>> C forAllDo(Iterator<T> iterator, C closure) {
      if (closure != null) {
         IteratorUtils.forEach(iterator, closure);
      }

      return closure;
   }

   /** @deprecated */
   @Deprecated
   public static <T> T get(Iterable<T> iterable, int index) {
      Objects.requireNonNull(iterable, "iterable");
      return (T)IterableUtils.get(iterable, index);
   }

   /** @deprecated */
   @Deprecated
   public static <T> T get(Iterator<T> iterator, int index) {
      Objects.requireNonNull(iterator, "iterator");
      return (T)IteratorUtils.get(iterator, index);
   }

   public static <K, V> Map.Entry<K, V> get(Map<K, V> map, int index) {
      Objects.requireNonNull(map, "map");
      checkIndexBounds(index);
      return (Map.Entry)get(map.entrySet(), index);
   }

   public static Object get(Object object, int index) {
      int i = index;
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
      } else if (object instanceof Map) {
         Map<?, ?> map = (Map)object;
         Iterator<?> iterator = map.entrySet().iterator();
         return IteratorUtils.get(iterator, index);
      } else if (object instanceof Object[]) {
         return ((Object[])object)[index];
      } else if (object instanceof Iterator) {
         Iterator<?> it = (Iterator)object;
         return IteratorUtils.get(it, index);
      } else if (object instanceof Iterable) {
         Iterable<?> iterable = (Iterable)object;
         return IterableUtils.get(iterable, index);
      } else if (object instanceof Enumeration) {
         Enumeration<?> it = (Enumeration)object;
         return EnumerationUtils.get(it, index);
      } else if (object == null) {
         throw new IllegalArgumentException("Unsupported object type: null");
      } else {
         try {
            return Array.get(object, i);
         } catch (IllegalArgumentException var5) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
         }
      }
   }

   public static <O> Map<O, Integer> getCardinalityMap(Iterable<? extends O> coll) {
      Objects.requireNonNull(coll, "coll");
      Map<O, Integer> count = new HashMap();

      for(O obj : coll) {
         Integer c = (Integer)count.get(obj);
         if (c == null) {
            count.put(obj, 1);
         } else {
            count.put(obj, c + 1);
         }
      }

      return count;
   }

   public static <E> int hashCode(Collection<? extends E> collection, Equator<? super E> equator) {
      Objects.requireNonNull(equator, "equator");
      if (null == collection) {
         return 0;
      } else {
         int hashCode = 1;

         for(E e : collection) {
            hashCode = 31 * hashCode + equator.hash(e);
         }

         return hashCode;
      }
   }

   public static <O> Collection<O> intersection(Iterable<? extends O> a, Iterable<? extends O> b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);

      for(O obj : helper) {
         helper.setCardinality(obj, helper.min(obj));
      }

      return helper.list();
   }

   public static boolean isEmpty(Collection<?> coll) {
      return coll == null || coll.isEmpty();
   }

   public static boolean isEqualCollection(Collection<?> a, Collection<?> b) {
      return CollectionUtils.CardinalityHelper.equals(a, b);
   }

   public static <E> boolean isEqualCollection(Collection<? extends E> a, Collection<? extends E> b, Equator<? super E> equator) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      Objects.requireNonNull(equator, "equator");
      if (a.size() != b.size()) {
         return false;
      } else {
         Transformer<E, ?> transformer = (input) -> new EquatorWrapper(equator, input);
         return isEqualCollection(collect(a, transformer), collect(b, transformer));
      }
   }

   public static boolean isFull(Collection<? extends Object> collection) {
      Objects.requireNonNull(collection, "collection");
      if (collection instanceof BoundedCollection) {
         return ((BoundedCollection)collection).isFull();
      } else {
         try {
            BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(collection);
            return bcoll.isFull();
         } catch (IllegalArgumentException var2) {
            return false;
         }
      }
   }

   public static boolean isNotEmpty(Collection<?> coll) {
      return !isEmpty(coll);
   }

   public static boolean isProperSubCollection(Collection<?> a, Collection<?> b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      return a.size() < b.size() && isSubCollection(a, b);
   }

   public static boolean isSubCollection(Collection<?> a, Collection<?> b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);

      for(Object obj : a) {
         if (helper.freqA(obj) > helper.freqB(obj)) {
            return false;
         }
      }

      return true;
   }

   /** @deprecated */
   @Deprecated
   public static <C> boolean matchesAll(Iterable<C> input, Predicate<? super C> predicate) {
      return predicate != null && IterableUtils.matchesAll(input, predicate);
   }

   public static int maxSize(Collection<? extends Object> collection) {
      Objects.requireNonNull(collection, "collection");
      if (collection instanceof BoundedCollection) {
         return ((BoundedCollection)collection).maxSize();
      } else {
         try {
            BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(collection);
            return bcoll.maxSize();
         } catch (IllegalArgumentException var2) {
            return -1;
         }
      }
   }

   public static <E> Collection<List<E>> permutations(Collection<E> collection) {
      Objects.requireNonNull(collection, "collection");
      PermutationIterator<E> it = new PermutationIterator<E>(collection);
      Collection<List<E>> result = new ArrayList();

      while(it.hasNext()) {
         result.add(it.next());
      }

      return result;
   }

   public static <C> Collection<C> predicatedCollection(Collection<C> collection, Predicate<? super C> predicate) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(predicate, "predicate");
      return PredicatedCollection.<C>predicatedCollection(collection, predicate);
   }

   public static <E> Collection<E> removeAll(Collection<E> collection, Collection<?> remove) {
      return ListUtils.<E>removeAll(collection, remove);
   }

   public static <E> Collection<E> removeAll(Iterable<E> collection, Iterable<? extends E> remove, Equator<? super E> equator) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(remove, "remove");
      Objects.requireNonNull(equator, "equator");
      Transformer<E, EquatorWrapper<E>> transformer = (input) -> new EquatorWrapper(equator, input);
      Set<EquatorWrapper<E>> removeSet = (Set)collect(remove, transformer, new HashSet());
      List<E> list = new ArrayList();

      for(E element : collection) {
         if (!removeSet.contains(new EquatorWrapper(equator, element))) {
            list.add(element);
         }
      }

      return list;
   }

   public static <E> Collection<E> removeCount(Collection<E> input, int startIndex, int count) {
      Objects.requireNonNull(input, "input");
      if (startIndex < 0) {
         throw new IndexOutOfBoundsException("The start index can't be less than 0.");
      } else if (count < 0) {
         throw new IndexOutOfBoundsException("The count can't be less than 0.");
      } else if (input.size() < startIndex + count) {
         throw new IndexOutOfBoundsException("The sum of start index and count can't be greater than the size of collection.");
      } else {
         Collection<E> result = new ArrayList(count);
         Iterator<E> iterator = input.iterator();

         while(count > 0) {
            if (startIndex > 0) {
               --startIndex;
               iterator.next();
            } else {
               --count;
               result.add(iterator.next());
               iterator.remove();
            }
         }

         return result;
      }
   }

   public static <E> Collection<E> removeRange(Collection<E> input, int startIndex, int endIndex) {
      Objects.requireNonNull(input, "input");
      if (endIndex < startIndex) {
         throw new IllegalArgumentException("The end index can't be less than the start index.");
      } else if (input.size() < endIndex) {
         throw new IndexOutOfBoundsException("The end index can't be greater than the size of collection.");
      } else {
         return removeCount(input, startIndex, endIndex - startIndex);
      }
   }

   public static <C> Collection<C> retainAll(Collection<C> collection, Collection<?> retain) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(retain, "retain");
      return ListUtils.<C>retainAll(collection, retain);
   }

   public static <E> Collection<E> retainAll(Iterable<E> collection, Iterable<? extends E> retain, Equator<? super E> equator) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(retain, "retain");
      Objects.requireNonNull(equator, "equator");
      Transformer<E, EquatorWrapper<E>> transformer = (input) -> new EquatorWrapper(equator, input);
      Set<EquatorWrapper<E>> retainSet = (Set)collect(retain, transformer, new HashSet());
      List<E> list = new ArrayList();

      for(E element : collection) {
         if (retainSet.contains(new EquatorWrapper(equator, element))) {
            list.add(element);
         }
      }

      return list;
   }

   public static void reverseArray(Object[] array) {
      Objects.requireNonNull(array, "array");
      int i = 0;

      for(int j = array.length - 1; j > i; ++i) {
         Object tmp = array[j];
         array[j] = array[i];
         array[i] = tmp;
         --j;
      }

   }

   public static <O> Collection<O> select(Iterable<? extends O> inputCollection, Predicate<? super O> predicate) {
      int size = 0;
      if (null != inputCollection) {
         size = inputCollection instanceof Collection ? ((Collection)inputCollection).size() : 0;
      }

      Collection<O> answer = size == 0 ? new ArrayList() : new ArrayList(size);
      return select(inputCollection, predicate, answer);
   }

   public static <O, R extends Collection<? super O>> R select(Iterable<? extends O> inputCollection, Predicate<? super O> predicate, R outputCollection) {
      if (inputCollection != null && predicate != null) {
         for(O item : inputCollection) {
            if (predicate.test(item)) {
               outputCollection.add(item);
            }
         }
      }

      return outputCollection;
   }

   public static <O, R extends Collection<? super O>> R select(Iterable<? extends O> inputCollection, Predicate<? super O> predicate, R outputCollection, R rejectedCollection) {
      if (inputCollection != null && predicate != null) {
         for(O element : inputCollection) {
            if (predicate.test(element)) {
               outputCollection.add(element);
            } else {
               rejectedCollection.add(element);
            }
         }
      }

      return outputCollection;
   }

   public static <O> Collection<O> selectRejected(Iterable<? extends O> inputCollection, Predicate<? super O> predicate) {
      int size = 0;
      if (null != inputCollection) {
         size = inputCollection instanceof Collection ? ((Collection)inputCollection).size() : 0;
      }

      Collection<O> answer = size == 0 ? new ArrayList() : new ArrayList(size);
      return selectRejected(inputCollection, predicate, answer);
   }

   public static <O, R extends Collection<? super O>> R selectRejected(Iterable<? extends O> inputCollection, Predicate<? super O> predicate, R outputCollection) {
      if (inputCollection != null && predicate != null) {
         for(O item : inputCollection) {
            if (!predicate.test(item)) {
               outputCollection.add(item);
            }
         }
      }

      return outputCollection;
   }

   public static int size(Object object) {
      if (object == null) {
         return 0;
      } else {
         int total = 0;
         if (object instanceof Map) {
            total = ((Map)object).size();
         } else if (object instanceof Collection) {
            total = ((Collection)object).size();
         } else if (object instanceof Iterable) {
            total = IterableUtils.size((Iterable)object);
         } else if (object instanceof Object[]) {
            total = ((Object[])object).length;
         } else if (object instanceof Iterator) {
            total = IteratorUtils.size((Iterator)object);
         } else if (object instanceof Enumeration) {
            Enumeration<?> it = (Enumeration)object;

            while(it.hasMoreElements()) {
               ++total;
               it.nextElement();
            }
         } else {
            try {
               total = Array.getLength(object);
            } catch (IllegalArgumentException var3) {
               throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
         }

         return total;
      }
   }

   public static boolean sizeIsEmpty(Object object) {
      if (object == null) {
         return true;
      } else if (object instanceof Collection) {
         return ((Collection)object).isEmpty();
      } else if (object instanceof Iterable) {
         return IterableUtils.isEmpty((Iterable)object);
      } else if (object instanceof Map) {
         return ((Map)object).isEmpty();
      } else if (object instanceof Object[]) {
         return ((Object[])object).length == 0;
      } else if (object instanceof Iterator) {
         return !((Iterator)object).hasNext();
      } else if (object instanceof Enumeration) {
         return !((Enumeration)object).hasMoreElements();
      } else {
         try {
            return Array.getLength(object) == 0;
         } catch (IllegalArgumentException var2) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
         }
      }
   }

   public static <O> Collection<O> subtract(Iterable<? extends O> a, Iterable<? extends O> b) {
      Predicate<O> p = TruePredicate.<O>truePredicate();
      return subtract(a, b, p);
   }

   public static <O> Collection<O> subtract(Iterable<? extends O> a, Iterable<? extends O> b, Predicate<O> p) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      Objects.requireNonNull(p, "p");
      ArrayList<O> list = new ArrayList();
      HashBag<O> bag = new HashBag<O>();

      for(O element : b) {
         if (p.test(element)) {
            bag.add(element);
         }
      }

      for(O element : a) {
         if (!bag.remove(element, 1)) {
            list.add(element);
         }
      }

      return list;
   }

   /** @deprecated */
   @Deprecated
   public static <C> Collection<C> synchronizedCollection(Collection<C> collection) {
      Objects.requireNonNull(collection, "collection");
      return SynchronizedCollection.<C>synchronizedCollection(collection);
   }

   public static <C> void transform(Collection<C> collection, Transformer<? super C, ? extends C> transformer) {
      if (collection != null && transformer != null) {
         if (collection instanceof List) {
            List<C> list = (List)collection;
            ListIterator<C> it = list.listIterator();

            while(it.hasNext()) {
               it.set(transformer.apply(it.next()));
            }
         } else {
            Collection<C> resultCollection = collect(collection, transformer);
            collection.clear();
            collection.addAll(resultCollection);
         }
      }

   }

   public static <E> Collection<E> transformingCollection(Collection<E> collection, Transformer<? super E, ? extends E> transformer) {
      Objects.requireNonNull(collection, "collection");
      Objects.requireNonNull(transformer, "transformer");
      return TransformedCollection.<E>transformingCollection(collection, transformer);
   }

   public static <O> Collection<O> union(Iterable<? extends O> a, Iterable<? extends O> b) {
      Objects.requireNonNull(a, "a");
      Objects.requireNonNull(b, "b");
      SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);

      for(O obj : helper) {
         helper.setCardinality(obj, helper.max(obj));
      }

      return helper.list();
   }

   /** @deprecated */
   @Deprecated
   public static <C> Collection<C> unmodifiableCollection(Collection<? extends C> collection) {
      Objects.requireNonNull(collection, "collection");
      return UnmodifiableCollection.<C>unmodifiableCollection(collection);
   }

   private CollectionUtils() {
   }

   private static class CardinalityHelper<O> {
      final Bag<O> cardinalityA;
      final Bag<O> cardinalityB;

      static boolean equals(Collection<?> a, Collection<?> b) {
         return (new HashBag(a)).equals(new HashBag(b));
      }

      CardinalityHelper(Iterable<? extends O> a, Iterable<? extends O> b) {
         this.cardinalityA = new HashBag<O>(a);
         this.cardinalityB = new HashBag<O>(b);
      }

      public int freqA(Object key) {
         return this.getFreq(key, this.cardinalityA);
      }

      public int freqB(Object key) {
         return this.getFreq(key, this.cardinalityB);
      }

      private int getFreq(Object key, Bag<?> freqMap) {
         return freqMap.getCount(key);
      }

      public final int max(Object obj) {
         return Math.max(this.freqA(obj), this.freqB(obj));
      }

      public final int min(Object obj) {
         return Math.min(this.freqA(obj), this.freqB(obj));
      }
   }

   private static final class EquatorWrapper<O> {
      private final Equator<? super O> equator;
      private final O object;

      EquatorWrapper(Equator<? super O> equator, O object) {
         this.equator = equator;
         this.object = object;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof EquatorWrapper)) {
            return false;
         } else {
            EquatorWrapper<O> otherObj = (EquatorWrapper)obj;
            return this.equator.equate(this.object, otherObj.getObject());
         }
      }

      public O getObject() {
         return this.object;
      }

      public int hashCode() {
         return this.equator.hash(this.object);
      }
   }

   private static final class SetOperationCardinalityHelper<O> extends CardinalityHelper<O> implements Iterable<O> {
      private final Set<O> elements = new HashSet();
      private final List<O> newList;

      SetOperationCardinalityHelper(Iterable<? extends O> a, Iterable<? extends O> b) {
         super(a, b);
         CollectionUtils.addAll(this.elements, a);
         CollectionUtils.addAll(this.elements, b);
         this.newList = new ArrayList(this.elements.size());
      }

      public Iterator<O> iterator() {
         return this.elements.iterator();
      }

      public Collection<O> list() {
         return this.newList;
      }

      public void setCardinality(O obj, int count) {
         for(int i = 0; i < count; ++i) {
            this.newList.add(obj);
         }

      }
   }
}
