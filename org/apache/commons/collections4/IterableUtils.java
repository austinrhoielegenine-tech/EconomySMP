package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.apache.commons.collections4.iterators.UniqueFilterIterator;

public class IterableUtils {
   static final FluentIterable EMPTY_ITERABLE = new FluentIterable<Object>() {
      public Iterator<Object> iterator() {
         return IteratorUtils.<Object>emptyIterator();
      }
   };

   public static <E> Iterable<E> boundedIterable(final Iterable<E> iterable, final long maxSize) {
      Objects.requireNonNull(iterable, "iterable");
      if (maxSize < 0L) {
         throw new IllegalArgumentException("MaxSize parameter must not be negative.");
      } else {
         return new FluentIterable<E>() {
            public Iterator<E> iterator() {
               return IteratorUtils.<E>boundedIterator(iterable.iterator(), maxSize);
            }
         };
      }
   }

   public static <E> Iterable<E> chainedIterable(final Iterable<? extends E>... iterables) {
      checkNotNull(iterables);
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return new LazyIteratorChain<E>() {
               protected Iterator<? extends E> nextIterator(int count) {
                  return count > iterables.length ? null : iterables[count - 1].iterator();
               }
            };
         }
      };
   }

   public static <E> Iterable<E> chainedIterable(Iterable<? extends E> a, Iterable<? extends E> b) {
      return chainedIterable(a, b);
   }

   public static <E> Iterable<E> chainedIterable(Iterable<? extends E> a, Iterable<? extends E> b, Iterable<? extends E> c) {
      return chainedIterable(a, b, c);
   }

   public static <E> Iterable<E> chainedIterable(Iterable<? extends E> a, Iterable<? extends E> b, Iterable<? extends E> c, Iterable<? extends E> d) {
      return chainedIterable(a, b, c, d);
   }

   static void checkNotNull(Iterable<?>... iterables) {
      Objects.requireNonNull(iterables, "iterables");

      for(Iterable<?> iterable : iterables) {
         Objects.requireNonNull(iterable, "iterable");
      }

   }

   public static <E> Iterable<E> collatedIterable(final Comparator<? super E> comparator, final Iterable<? extends E> a, final Iterable<? extends E> b) {
      checkNotNull(a, b);
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return IteratorUtils.<E>collatedIterator(comparator, a.iterator(), b.iterator());
         }
      };
   }

   public static <E> Iterable<E> collatedIterable(final Iterable<? extends E> a, final Iterable<? extends E> b) {
      checkNotNull(a, b);
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return IteratorUtils.<E>collatedIterator((Comparator)null, a.iterator(), b.iterator());
         }
      };
   }

   public static <E> boolean contains(Iterable<? extends E> iterable, E object, Equator<? super E> equator) {
      Objects.requireNonNull(equator, "equator");
      return matchesAny(iterable, EqualPredicate.equalPredicate(object, equator));
   }

   public static <E> boolean contains(Iterable<E> iterable, Object object) {
      return iterable instanceof Collection ? ((Collection)iterable).contains(object) : IteratorUtils.contains(emptyIteratorIfNull(iterable), object);
   }

   public static <E> long countMatches(Iterable<E> input, Predicate<? super E> predicate) {
      Objects.requireNonNull(predicate, "predicate");
      return (long)size(filteredIterable(emptyIfNull(input), predicate));
   }

   public static <E> List<E> duplicateList(Iterable<E> iterable) {
      return new ArrayList(duplicateSequencedSet(iterable));
   }

   public static <E> Set<E> duplicateSequencedSet(Iterable<E> iterable) {
      return (Set)duplicateSet(iterable, new LinkedHashSet());
   }

   public static <E> Set<E> duplicateSet(Iterable<E> iterable) {
      return (Set)duplicateSet(iterable, new HashSet());
   }

   static <C extends Collection<E>, E> C duplicateSet(Iterable<E> iterable, C duplicates) {
      Set<E> set = new HashSet();

      for(E e : iterable) {
         ((Collection)(set.contains(e) ? duplicates : set)).add(e);
      }

      return duplicates;
   }

   public static <E> Iterable<E> emptyIfNull(Iterable<E> iterable) {
      return iterable == null ? emptyIterable() : iterable;
   }

   public static <E> Iterable<E> emptyIterable() {
      return EMPTY_ITERABLE;
   }

   private static <E> Iterator<E> emptyIteratorIfNull(Iterable<E> iterable) {
      return (Iterator<E>)(iterable != null ? iterable.iterator() : IteratorUtils.emptyIterator());
   }

   public static <E> Iterable<E> filteredIterable(final Iterable<E> iterable, final Predicate<? super E> predicate) {
      Objects.requireNonNull(iterable, "iterable");
      Objects.requireNonNull(predicate, "predicate");
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return IteratorUtils.<E>filteredIterator(IterableUtils.emptyIteratorIfNull(iterable), predicate);
         }
      };
   }

   public static <E> E find(Iterable<E> iterable, Predicate<? super E> predicate) {
      return (E)IteratorUtils.find(emptyIteratorIfNull(iterable), predicate);
   }

   public static <T> T first(Iterable<T> iterable) {
      return (T)get(iterable, 0);
   }

   public static <E> void forEach(Iterable<E> iterable, Closure<? super E> closure) {
      IteratorUtils.forEach(emptyIteratorIfNull(iterable), closure);
   }

   public static <E> E forEachButLast(Iterable<E> iterable, Closure<? super E> closure) {
      return (E)IteratorUtils.forEachButLast(emptyIteratorIfNull(iterable), closure);
   }

   public static <E, T extends E> int frequency(Iterable<E> iterable, T obj) {
      if (iterable instanceof Set) {
         return ((Set)iterable).contains(obj) ? 1 : 0;
      } else {
         return iterable instanceof Bag ? ((Bag)iterable).getCount(obj) : size(filteredIterable(emptyIfNull(iterable), EqualPredicate.equalPredicate(obj)));
      }
   }

   public static <T> T get(Iterable<T> iterable, int index) {
      CollectionUtils.checkIndexBounds(index);
      return (T)(iterable instanceof List ? ((List)iterable).get(index) : IteratorUtils.get(emptyIteratorIfNull(iterable), index));
   }

   public static <E> int indexOf(Iterable<E> iterable, Predicate<? super E> predicate) {
      return IteratorUtils.indexOf(emptyIteratorIfNull(iterable), predicate);
   }

   public static boolean isEmpty(Iterable<?> iterable) {
      return iterable instanceof Collection ? ((Collection)iterable).isEmpty() : IteratorUtils.isEmpty(emptyIteratorIfNull(iterable));
   }

   public static <E> Iterable<E> loopingIterable(final Iterable<E> iterable) {
      Objects.requireNonNull(iterable, "iterable");
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return new LazyIteratorChain<E>() {
               protected Iterator<? extends E> nextIterator(int count) {
                  return IterableUtils.isEmpty(iterable) ? null : iterable.iterator();
               }
            };
         }
      };
   }

   public static <E> boolean matchesAll(Iterable<E> iterable, Predicate<? super E> predicate) {
      return IteratorUtils.matchesAll(emptyIteratorIfNull(iterable), predicate);
   }

   public static <E> boolean matchesAny(Iterable<E> iterable, Predicate<? super E> predicate) {
      return IteratorUtils.matchesAny(emptyIteratorIfNull(iterable), predicate);
   }

   public static <O, R extends Collection<O>> List<R> partition(Iterable<? extends O> iterable, Factory<R> partitionFactory, Predicate<? super O>... predicates) {
      if (iterable == null) {
         Iterable<O> empty = emptyIterable();
         return partition(empty, partitionFactory, predicates);
      } else {
         Objects.requireNonNull(predicates, "predicates");

         for(Predicate<?> predicate : predicates) {
            Objects.requireNonNull(predicate, "predicate");
         }

         if (predicates.length < 1) {
            R singlePartition = partitionFactory.get();
            CollectionUtils.addAll(singlePartition, iterable);
            return Collections.singletonList(singlePartition);
         } else {
            int numberOfPredicates = predicates.length;
            int numberOfPartitions = numberOfPredicates + 1;
            List<R> partitions = new ArrayList(numberOfPartitions);

            for(int i = 0; i < numberOfPartitions; ++i) {
               partitions.add(partitionFactory.get());
            }

            for(O element : iterable) {
               boolean elementAssigned = false;

               for(int i = 0; i < numberOfPredicates; ++i) {
                  if (predicates[i].test(element)) {
                     ((Collection)partitions.get(i)).add(element);
                     elementAssigned = true;
                     break;
                  }
               }

               if (!elementAssigned) {
                  ((Collection)partitions.get(numberOfPredicates)).add(element);
               }
            }

            return partitions;
         }
      }
   }

   public static <O> List<List<O>> partition(Iterable<? extends O> iterable, Predicate<? super O> predicate) {
      Objects.requireNonNull(predicate, "predicate");
      Factory<List<O>> factory = FactoryUtils.<List<O>>instantiateFactory(ArrayList.class);
      Predicate<? super O>[] predicates = new Predicate[]{predicate};
      return partition(iterable, factory, predicates);
   }

   public static <O> List<List<O>> partition(Iterable<? extends O> iterable, Predicate<? super O>... predicates) {
      Factory<List<O>> factory = FactoryUtils.<List<O>>instantiateFactory(ArrayList.class);
      return partition(iterable, factory, predicates);
   }

   public static <E> Iterable<E> reversedIterable(final Iterable<E> iterable) {
      Objects.requireNonNull(iterable, "iterable");
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            List<E> list = iterable instanceof List ? (List)iterable : IteratorUtils.toList(iterable.iterator());
            return new ReverseListIterator<E>(list);
         }
      };
   }

   public static int size(Iterable<?> iterable) {
      if (iterable == null) {
         return 0;
      } else {
         return iterable instanceof Collection ? ((Collection)iterable).size() : IteratorUtils.size(emptyIteratorIfNull(iterable));
      }
   }

   public static <E> Iterable<E> skippingIterable(final Iterable<E> iterable, final long elementsToSkip) {
      Objects.requireNonNull(iterable, "iterable");
      if (elementsToSkip < 0L) {
         throw new IllegalArgumentException("ElementsToSkip parameter must not be negative.");
      } else {
         return new FluentIterable<E>() {
            public Iterator<E> iterator() {
               return IteratorUtils.<E>skippingIterator(iterable.iterator(), elementsToSkip);
            }
         };
      }
   }

   public static <E> List<E> toList(Iterable<E> iterable) {
      return IteratorUtils.<E>toList(emptyIteratorIfNull(iterable));
   }

   public static <E> String toString(Iterable<E> iterable) {
      return IteratorUtils.toString(emptyIteratorIfNull(iterable));
   }

   public static <E> String toString(Iterable<E> iterable, Transformer<? super E, String> transformer) {
      Objects.requireNonNull(transformer, "transformer");
      return IteratorUtils.toString(emptyIteratorIfNull(iterable), transformer);
   }

   public static <E> String toString(Iterable<E> iterable, Transformer<? super E, String> transformer, String delimiter, String prefix, String suffix) {
      return IteratorUtils.toString(emptyIteratorIfNull(iterable), transformer, delimiter, prefix, suffix);
   }

   public static <I, O> Iterable<O> transformedIterable(final Iterable<I> iterable, final Transformer<? super I, ? extends O> transformer) {
      Objects.requireNonNull(iterable, "iterable");
      Objects.requireNonNull(transformer, "transformer");
      return new FluentIterable<O>() {
         public Iterator<O> iterator() {
            return IteratorUtils.transformedIterator(iterable.iterator(), transformer);
         }
      };
   }

   public static <E> Iterable<E> uniqueIterable(final Iterable<E> iterable) {
      Objects.requireNonNull(iterable, "iterable");
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return new UniqueFilterIterator<E>(iterable.iterator());
         }
      };
   }

   public static <E> Iterable<E> unmodifiableIterable(Iterable<E> iterable) {
      Objects.requireNonNull(iterable, "iterable");
      return (Iterable<E>)(iterable instanceof UnmodifiableIterable ? iterable : new UnmodifiableIterable(iterable));
   }

   public static <E> Iterable<E> zippingIterable(final Iterable<? extends E> a, final Iterable<? extends E> b) {
      Objects.requireNonNull(a, "iterable");
      Objects.requireNonNull(b, "iterable");
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            return IteratorUtils.<E>zippingIterator(a.iterator(), b.iterator());
         }
      };
   }

   public static <E> Iterable<E> zippingIterable(final Iterable<? extends E> first, final Iterable<? extends E>... others) {
      Objects.requireNonNull(first, "iterable");
      checkNotNull(others);
      return new FluentIterable<E>() {
         public Iterator<E> iterator() {
            Iterator<? extends E>[] iterators = new Iterator[others.length + 1];
            iterators[0] = first.iterator();

            for(int i = 0; i < others.length; ++i) {
               iterators[i + 1] = others[i].iterator();
            }

            return IteratorUtils.<E>zippingIterator(iterators);
         }
      };
   }

   private static final class UnmodifiableIterable<E> extends FluentIterable<E> {
      private final Iterable<E> iterable;

      UnmodifiableIterable(Iterable<E> iterable) {
         this.iterable = iterable;
      }

      public Iterator<E> iterator() {
         return IteratorUtils.<E>unmodifiableIterator(this.iterable.iterator());
      }
   }
}
