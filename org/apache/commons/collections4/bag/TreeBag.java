package org.apache.commons.collections4.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections4.SortedBag;

public class TreeBag<E> extends AbstractMapBag<E> implements SortedBag<E>, Serializable {
   private static final long serialVersionUID = -7740146511091606676L;

   public TreeBag() {
      super(new TreeMap());
   }

   public TreeBag(Collection<? extends E> coll) {
      this();
      this.addAll(coll);
   }

   public TreeBag(Comparator<? super E> comparator) {
      super(new TreeMap(comparator));
   }

   public TreeBag(Iterable<? extends E> iterable) {
      super(new TreeMap(), iterable);
   }

   public boolean add(E object) {
      if (this.comparator() == null && !(object instanceof Comparable)) {
         Objects.requireNonNull(object, "object");
         throw new IllegalArgumentException("Objects of type " + object.getClass() + " cannot be added to a naturally ordered TreeBag as it does not implement Comparable");
      } else {
         return super.add(object);
      }
   }

   public Comparator<? super E> comparator() {
      return this.getMap().comparator();
   }

   public E first() {
      return (E)this.getMap().firstKey();
   }

   protected SortedMap<E, AbstractMapBag.MutableInteger> getMap() {
      return (SortedMap)super.getMap();
   }

   public E last() {
      return (E)this.getMap().lastKey();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      Comparator<? super E> comp = (Comparator)in.readObject();
      super.doReadObject(new TreeMap(comp), in);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.comparator());
      super.doWriteObject(out);
   }
}
