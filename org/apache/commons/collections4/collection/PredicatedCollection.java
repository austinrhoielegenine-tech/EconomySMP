package org.apache.commons.collections4.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.PredicatedBag;
import org.apache.commons.collections4.functors.NotNullPredicate;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.set.PredicatedSet;

public class PredicatedCollection<E> extends AbstractCollectionDecorator<E> {
   private static final long serialVersionUID = -5259182142076705162L;
   protected final Predicate<? super E> predicate;

   public static <E> Builder<E> builder(Predicate<? super E> predicate) {
      return new Builder<E>(predicate);
   }

   public static <E> Builder<E> notNullBuilder() {
      return new Builder<E>(NotNullPredicate.notNullPredicate());
   }

   public static <T> PredicatedCollection<T> predicatedCollection(Collection<T> coll, Predicate<? super T> predicate) {
      return new PredicatedCollection<T>(coll, predicate);
   }

   protected PredicatedCollection(Collection<E> collection, Predicate<? super E> predicate) {
      super(collection);
      this.predicate = (Predicate)Objects.requireNonNull(predicate, "predicate");

      for(E item : collection) {
         this.validate(item);
      }

   }

   public boolean add(E object) {
      this.validate(object);
      return this.decorated().add(object);
   }

   public boolean addAll(Collection<? extends E> coll) {
      for(E item : coll) {
         this.validate(item);
      }

      return this.decorated().addAll(coll);
   }

   protected void validate(E object) {
      if (!this.predicate.test(object)) {
         throw new IllegalArgumentException("Cannot add Object '" + object + "' - Predicate '" + this.predicate + "' rejected it");
      }
   }

   public static class Builder<E> {
      private final Predicate<? super E> predicate;
      private final List<E> accepted = new ArrayList();
      private final List<E> rejected = new ArrayList();

      public Builder(Predicate<? super E> predicate) {
         this.predicate = (Predicate)Objects.requireNonNull(predicate, "predicate");
      }

      public Builder<E> add(E item) {
         if (this.predicate.test(item)) {
            this.accepted.add(item);
         } else {
            this.rejected.add(item);
         }

         return this;
      }

      public Builder<E> addAll(Collection<? extends E> items) {
         if (items != null) {
            for(E item : items) {
               this.add(item);
            }
         }

         return this;
      }

      public Bag<E> createPredicatedBag() {
         return this.createPredicatedBag(new HashBag());
      }

      public Bag<E> createPredicatedBag(Bag<E> bag) {
         Objects.requireNonNull(bag, "bag");
         PredicatedBag<E> predicatedBag = PredicatedBag.<E>predicatedBag(bag, this.predicate);
         predicatedBag.addAll(this.accepted);
         return predicatedBag;
      }

      public List<E> createPredicatedList() {
         return this.createPredicatedList(new ArrayList());
      }

      public List<E> createPredicatedList(List<E> list) {
         Objects.requireNonNull(list, "list");
         List<E> predicatedList = PredicatedList.<E>predicatedList(list, this.predicate);
         predicatedList.addAll(this.accepted);
         return predicatedList;
      }

      public MultiSet<E> createPredicatedMultiSet() {
         return this.createPredicatedMultiSet(new HashMultiSet());
      }

      public MultiSet<E> createPredicatedMultiSet(MultiSet<E> multiset) {
         Objects.requireNonNull(multiset, "multiset");
         PredicatedMultiSet<E> predicatedMultiSet = PredicatedMultiSet.<E>predicatedMultiSet(multiset, this.predicate);
         predicatedMultiSet.addAll(this.accepted);
         return predicatedMultiSet;
      }

      public Queue<E> createPredicatedQueue() {
         return this.createPredicatedQueue(new LinkedList());
      }

      public Queue<E> createPredicatedQueue(Queue<E> queue) {
         Objects.requireNonNull(queue, "queue");
         PredicatedQueue<E> predicatedQueue = PredicatedQueue.<E>predicatedQueue(queue, this.predicate);
         predicatedQueue.addAll(this.accepted);
         return predicatedQueue;
      }

      public Set<E> createPredicatedSet() {
         return this.createPredicatedSet(new HashSet());
      }

      public Set<E> createPredicatedSet(Set<E> set) {
         Objects.requireNonNull(set, "set");
         PredicatedSet<E> predicatedSet = PredicatedSet.<E>predicatedSet(set, this.predicate);
         predicatedSet.addAll(this.accepted);
         return predicatedSet;
      }

      public Collection<E> rejectedElements() {
         return Collections.unmodifiableCollection(this.rejected);
      }
   }
}
