package org.apache.commons.collections4;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.commons.collections4.iterators.BoundedIterator;
import org.apache.commons.collections4.iterators.CollatingIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;
import org.apache.commons.collections4.iterators.EmptyListIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.FilterListIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.apache.commons.collections4.iterators.LoopingIterator;
import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.apache.commons.collections4.iterators.NodeListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.ObjectArrayListIterator;
import org.apache.commons.collections4.iterators.ObjectGraphIterator;
import org.apache.commons.collections4.iterators.PeekingIterator;
import org.apache.commons.collections4.iterators.PushbackIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.iterators.SingletonListIterator;
import org.apache.commons.collections4.iterators.SkippingIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.iterators.ZippingIterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IteratorUtils {
   public static final ResettableIterator EMPTY_ITERATOR;
   public static final ResettableListIterator EMPTY_LIST_ITERATOR;
   public static final OrderedIterator EMPTY_ORDERED_ITERATOR;
   public static final MapIterator EMPTY_MAP_ITERATOR;
   public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR;
   private static final String DEFAULT_TOSTRING_DELIMITER = ", ";

   private static <E, C extends Collection<E>> C addAll(Iterator<? extends E> iterator, C list) {
      Objects.requireNonNull(iterator, "iterator");

      while(iterator.hasNext()) {
         list.add(iterator.next());
      }

      return list;
   }

   public static <E> ResettableIterator<E> arrayIterator(E... array) {
      return new ObjectArrayIterator<E>(array);
   }

   public static <E> ResettableIterator<E> arrayIterator(E[] array, int start) {
      return new ObjectArrayIterator<E>(array, start);
   }

   public static <E> ResettableIterator<E> arrayIterator(E[] array, int start, int end) {
      return new ObjectArrayIterator<E>(array, start, end);
   }

   public static <E> ResettableIterator<E> arrayIterator(Object array) {
      return new ArrayIterator<E>(array);
   }

   public static <E> ResettableIterator<E> arrayIterator(Object array, int start) {
      return new ArrayIterator<E>(array, start);
   }

   public static <E> ResettableIterator<E> arrayIterator(Object array, int start, int end) {
      return new ArrayIterator<E>(array, start, end);
   }

   public static <E> ResettableListIterator<E> arrayListIterator(E... array) {
      return new ObjectArrayListIterator<E>(array);
   }

   public static <E> ResettableListIterator<E> arrayListIterator(E[] array, int start) {
      return new ObjectArrayListIterator<E>(array, start);
   }

   public static <E> ResettableListIterator<E> arrayListIterator(E[] array, int start, int end) {
      return new ObjectArrayListIterator<E>(array, start, end);
   }

   public static <E> ResettableListIterator<E> arrayListIterator(Object array) {
      return new ArrayListIterator<E>(array);
   }

   public static <E> ResettableListIterator<E> arrayListIterator(Object array, int start) {
      return new ArrayListIterator<E>(array, start);
   }

   public static <E> ResettableListIterator<E> arrayListIterator(Object array, int start, int end) {
      return new ArrayListIterator<E>(array, start, end);
   }

   public static <E> Enumeration<E> asEnumeration(Iterator<? extends E> iterator) {
      return new IteratorEnumeration<E>((Iterator)Objects.requireNonNull(iterator, "iterator"));
   }

   public static <E> Iterable<E> asIterable(Iterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return new IteratorIterable<E>(iterator, false);
   }

   public static <E> Iterator<E> asIterator(Enumeration<? extends E> enumeration) {
      return new EnumerationIterator<E>((Enumeration)Objects.requireNonNull(enumeration, "enumeration"));
   }

   public static <E> Iterator<E> asIterator(Enumeration<? extends E> enumeration, Collection<? super E> removeCollection) {
      return new EnumerationIterator<E>((Enumeration)Objects.requireNonNull(enumeration, "enumeration"), (Collection)Objects.requireNonNull(removeCollection, "removeCollection"));
   }

   public static <E> Iterable<E> asMultipleUseIterable(Iterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return new IteratorIterable<E>(iterator, true);
   }

   public static <E> BoundedIterator<E> boundedIterator(Iterator<? extends E> iterator, long max) {
      return boundedIterator(iterator, 0L, max);
   }

   public static <E> BoundedIterator<E> boundedIterator(Iterator<? extends E> iterator, long offset, long max) {
      return new BoundedIterator<E>(iterator, offset, max);
   }

   public static <E> Iterator<E> chainedIterator(Collection<? extends Iterator<? extends E>> iterators) {
      return new IteratorChain<E>(iterators);
   }

   public static <E> Iterator<E> chainedIterator(Iterator<? extends E>... iterators) {
      return new IteratorChain<E>(iterators);
   }

   public static <E> Iterator<E> chainedIterator(Iterator<? extends E> iterator1, Iterator<? extends E> iterator2) {
      return new IteratorChain<E>(iterator1, iterator2);
   }

   public static <E> Iterator<E> chainedIterator(final Iterator<? extends Iterator<? extends E>> iterators) {
      return new LazyIteratorChain<E>() {
         protected Iterator<? extends E> nextIterator(int count) {
            return iterators.hasNext() ? (Iterator)iterators.next() : null;
         }
      };
   }

   public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Collection<Iterator<? extends E>> iterators) {
      Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
      return new CollatingIterator<E>(comp, iterators);
   }

   public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Iterator<? extends E>... iterators) {
      Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
      return new CollatingIterator<E>(comp, iterators);
   }

   public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Iterator<? extends E> iterator1, Iterator<? extends E> iterator2) {
      Comparator<E> comp = comparator == null ? ComparatorUtils.NATURAL_COMPARATOR : comparator;
      return new CollatingIterator<E>(comp, iterator1, iterator2);
   }

   public static <E> boolean contains(Iterator<E> iterator, Object object) {
      return matchesAny(iterator, EqualPredicate.equalPredicate(object));
   }

   public static <E> ResettableIterator<E> emptyIterator() {
      return EmptyIterator.<E>resettableEmptyIterator();
   }

   public static <E> ResettableListIterator<E> emptyListIterator() {
      return EmptyListIterator.<E>resettableEmptyListIterator();
   }

   public static <K, V> MapIterator<K, V> emptyMapIterator() {
      return EmptyMapIterator.<K, V>emptyMapIterator();
   }

   public static <E> OrderedIterator<E> emptyOrderedIterator() {
      return EmptyOrderedIterator.<E>emptyOrderedIterator();
   }

   public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator() {
      return EmptyOrderedMapIterator.<K, V>emptyOrderedMapIterator();
   }

   public static <E> Iterator<E> filteredIterator(Iterator<? extends E> iterator, Predicate<? super E> predicate) {
      Objects.requireNonNull(iterator, "iterator");
      Objects.requireNonNull(predicate, "predicate");
      return new FilterIterator<E>(iterator, predicate);
   }

   public static <E> ListIterator<E> filteredListIterator(ListIterator<? extends E> listIterator, Predicate<? super E> predicate) {
      Objects.requireNonNull(listIterator, "listIterator");
      Objects.requireNonNull(predicate, "predicate");
      return new FilterListIterator<E>(listIterator, predicate);
   }

   public static <E> E find(Iterator<E> iterator, Predicate<? super E> predicate) {
      return (E)find(iterator, predicate, (Object)null);
   }

   private static <E> E find(Iterator<E> iterator, Predicate<? super E> predicate, E defaultValue) {
      Objects.requireNonNull(predicate, "predicate");
      if (iterator != null) {
         while(iterator.hasNext()) {
            E element = (E)iterator.next();
            if (predicate.test(element)) {
               return element;
            }
         }
      }

      return defaultValue;
   }

   public static <E> E first(Iterator<E> iterator) {
      return (E)get(iterator, 0);
   }

   public static <E> void forEach(Iterator<E> iterator, Closure<? super E> closure) {
      Objects.requireNonNull(closure, "closure");
      if (iterator != null) {
         while(iterator.hasNext()) {
            closure.accept(iterator.next());
         }
      }

   }

   public static <E> E forEachButLast(Iterator<E> iterator, Closure<? super E> closure) {
      Objects.requireNonNull(closure, "closure");
      if (iterator != null) {
         while(iterator.hasNext()) {
            E element = (E)iterator.next();
            if (!iterator.hasNext()) {
               return element;
            }

            closure.accept(element);
         }
      }

      return null;
   }

   public static <E> E get(Iterator<E> iterator, int index) {
      return (E)get(iterator, index, (ioob) -> {
         throw new IndexOutOfBoundsException("Entry does not exist: " + ioob);
      });
   }

   static <E> E get(Iterator<E> iterator, int index, IntFunction<E> defaultSupplier) {
      int i = index;
      CollectionUtils.checkIndexBounds(index);

      while(iterator.hasNext()) {
         --i;
         if (i == -1) {
            return (E)iterator.next();
         }

         iterator.next();
      }

      return (E)defaultSupplier.apply(i);
   }

   public static Iterator<?> getIterator(Object obj) {
      if (obj == null) {
         return emptyIterator();
      } else if (obj instanceof Iterator) {
         return (Iterator)obj;
      } else if (obj instanceof Iterable) {
         return ((Iterable)obj).iterator();
      } else if (obj instanceof Object[]) {
         return new ObjectArrayIterator(obj);
      } else if (obj instanceof Enumeration) {
         return new EnumerationIterator((Enumeration)obj);
      } else if (obj instanceof Map) {
         return ((Map)obj).values().iterator();
      } else if (obj instanceof NodeList) {
         return new NodeListIterator((NodeList)obj);
      } else if (obj instanceof Node) {
         return new NodeListIterator((Node)obj);
      } else if (obj instanceof Dictionary) {
         return new EnumerationIterator(((Dictionary)obj).elements());
      } else if (obj.getClass().isArray()) {
         return new ArrayIterator(obj);
      } else {
         try {
            Method method = obj.getClass().getMethod("iterator", (Class[])null);
            if (Iterator.class.isAssignableFrom(method.getReturnType())) {
               Iterator<?> it = (Iterator)method.invoke(obj, (Object[])null);
               if (it != null) {
                  return it;
               }
            }
         } catch (ReflectiveOperationException | RuntimeException var3) {
         }

         return singletonIterator(obj);
      }
   }

   public static <E> int indexOf(Iterator<E> iterator, Predicate<? super E> predicate) {
      Objects.requireNonNull(predicate, "predicate");
      if (iterator != null) {
         for(int index = 0; iterator.hasNext(); ++index) {
            E element = (E)iterator.next();
            if (predicate.test(element)) {
               return index;
            }
         }
      }

      return -1;
   }

   public static boolean isEmpty(Iterator<?> iterator) {
      return iterator == null || !iterator.hasNext();
   }

   public static <E> ResettableIterator<E> loopingIterator(Collection<? extends E> collection) {
      return new LoopingIterator<E>((Collection)Objects.requireNonNull(collection, "collection"));
   }

   public static <E> ResettableListIterator<E> loopingListIterator(List<E> list) {
      return new LoopingListIterator<E>((List)Objects.requireNonNull(list, "list"));
   }

   public static <E> boolean matchesAll(Iterator<E> iterator, Predicate<? super E> predicate) {
      Objects.requireNonNull(predicate, "predicate");
      if (iterator != null) {
         while(iterator.hasNext()) {
            E element = (E)iterator.next();
            if (!predicate.test(element)) {
               return false;
            }
         }
      }

      return true;
   }

   public static <E> boolean matchesAny(Iterator<E> iterator, Predicate<? super E> predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static NodeListIterator nodeListIterator(Node node) {
      return new NodeListIterator((Node)Objects.requireNonNull(node, "node"));
   }

   public static NodeListIterator nodeListIterator(NodeList nodeList) {
      return new NodeListIterator((NodeList)Objects.requireNonNull(nodeList, "nodeList"));
   }

   public static <E> Iterator<E> objectGraphIterator(E root, Transformer<? super E, ? extends E> transformer) {
      return new ObjectGraphIterator<E>(root, transformer);
   }

   public static <E> Iterator<E> peekingIterator(Iterator<? extends E> iterator) {
      return PeekingIterator.<E>peekingIterator(iterator);
   }

   public static <E> Iterator<E> pushbackIterator(Iterator<? extends E> iterator) {
      return PushbackIterator.<E>pushbackIterator(iterator);
   }

   public static <E> ResettableIterator<E> singletonIterator(E object) {
      return new SingletonIterator<E>(object);
   }

   public static <E> ListIterator<E> singletonListIterator(E object) {
      return new SingletonListIterator<E>(object);
   }

   public static int size(Iterator<?> iterator) {
      int size = 0;
      if (iterator != null) {
         while(iterator.hasNext()) {
            iterator.next();
            ++size;
         }
      }

      return size;
   }

   public static <E> SkippingIterator<E> skippingIterator(Iterator<E> iterator, long offset) {
      return new SkippingIterator<E>(iterator, offset);
   }

   public static <E> Stream<E> stream(Iterable<E> iterable) {
      return iterable == null ? Stream.empty() : StreamSupport.stream(iterable.spliterator(), false);
   }

   public static <E> Stream<E> stream(Iterator<E> iterator) {
      return iterator == null ? Stream.empty() : StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 16), false);
   }

   public static Object[] toArray(Iterator<?> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      List<?> list = toList(iterator, 100);
      return list.toArray();
   }

   public static <E> E[] toArray(Iterator<? extends E> iterator, Class<E> arrayClass) {
      Objects.requireNonNull(iterator, "iterator");
      Objects.requireNonNull(arrayClass, "arrayClass");
      List<E> list = toList(iterator, 100);
      E[] array = (E[])((Object[])Array.newInstance(arrayClass, list.size()));
      return (E[])list.toArray(array);
   }

   public static <E> List<E> toList(Iterator<? extends E> iterator) {
      return toList(iterator, 10);
   }

   public static <E> List<E> toList(Iterator<? extends E> iterator, int estimatedSize) {
      if (estimatedSize < 1) {
         throw new IllegalArgumentException("Estimated size must be greater than 0");
      } else {
         return (List)addAll(iterator, new ArrayList(estimatedSize));
      }
   }

   public static <E> ListIterator<E> toListIterator(Iterator<? extends E> iterator) {
      Objects.requireNonNull(iterator, "iterator");
      return new ListIteratorWrapper<E>(iterator);
   }

   public static <E> Set<E> toSet(Iterator<? extends E> iterator) {
      return toSet(iterator, 10);
   }

   public static <E> Set<E> toSet(Iterator<? extends E> iterator, int estimatedSize) {
      if (estimatedSize < 1) {
         throw new IllegalArgumentException("Estimated size must be greater than 0");
      } else {
         return (Set)addAll(iterator, new HashSet(estimatedSize));
      }
   }

   public static <E> String toString(Iterator<E> iterator) {
      return toString(iterator, TransformerUtils.stringValueTransformer(), ", ", "[", "]");
   }

   public static <E> String toString(Iterator<E> iterator, Transformer<? super E, String> transformer) {
      return toString(iterator, transformer, ", ", "[", "]");
   }

   public static <E> String toString(Iterator<E> iterator, Transformer<? super E, String> transformer, String delimiter, String prefix, String suffix) {
      Objects.requireNonNull(transformer, "transformer");
      Objects.requireNonNull(delimiter, "delimiter");
      Objects.requireNonNull(prefix, "prefix");
      Objects.requireNonNull(suffix, "suffix");
      StringBuilder stringBuilder = new StringBuilder(prefix);
      if (iterator != null) {
         while(iterator.hasNext()) {
            E element = (E)iterator.next();
            stringBuilder.append(transformer.apply(element));
            stringBuilder.append(delimiter);
         }

         if (stringBuilder.length() > prefix.length()) {
            stringBuilder.setLength(stringBuilder.length() - delimiter.length());
         }
      }

      stringBuilder.append(suffix);
      return stringBuilder.toString();
   }

   public static <I, O> Iterator<O> transformedIterator(Iterator<? extends I> iterator, Transformer<? super I, ? extends O> transformer) {
      Objects.requireNonNull(iterator, "iterator");
      Objects.requireNonNull(transformer, "transformer");
      return new TransformIterator(iterator, transformer);
   }

   public static <E> Iterator<E> unmodifiableIterator(Iterator<E> iterator) {
      return UnmodifiableIterator.<E>unmodifiableIterator(iterator);
   }

   public static <E> ListIterator<E> unmodifiableListIterator(ListIterator<E> listIterator) {
      return UnmodifiableListIterator.<E>unmodifiableListIterator(listIterator);
   }

   public static <K, V> MapIterator<K, V> unmodifiableMapIterator(MapIterator<K, V> mapIterator) {
      return UnmodifiableMapIterator.<K, V>unmodifiableMapIterator(mapIterator);
   }

   public static <E> ZippingIterator<E> zippingIterator(Iterator<? extends E>... iterators) {
      return new ZippingIterator<E>(iterators);
   }

   public static <E> ZippingIterator<E> zippingIterator(Iterator<? extends E> a, Iterator<? extends E> b) {
      return new ZippingIterator<E>(a, b);
   }

   public static <E> ZippingIterator<E> zippingIterator(Iterator<? extends E> a, Iterator<? extends E> b, Iterator<? extends E> c) {
      return new ZippingIterator<E>(a, b, c);
   }

   private IteratorUtils() {
   }

   static {
      EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;
      EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;
      EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;
      EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;
      EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;
   }
}
