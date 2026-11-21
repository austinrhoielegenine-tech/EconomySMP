package org.apache.commons.collections4.bidimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;

public class DualTreeBidiMap<K, V> extends AbstractDualBidiMap<K, V> implements SortedBidiMap<K, V>, Serializable {
   private static final long serialVersionUID = 721969328361809L;
   private final Comparator<? super K> comparator;
   private final Comparator<? super V> valueComparator;

   public DualTreeBidiMap() {
      super(new TreeMap(), new TreeMap());
      this.comparator = null;
      this.valueComparator = null;
   }

   public DualTreeBidiMap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
      super(new TreeMap(keyComparator), new TreeMap(valueComparator));
      this.comparator = keyComparator;
      this.valueComparator = valueComparator;
   }

   public DualTreeBidiMap(Map<? extends K, ? extends V> map) {
      super(new TreeMap(), new TreeMap());
      this.putAll(map);
      this.comparator = null;
      this.valueComparator = null;
   }

   protected DualTreeBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
      super(normalMap, reverseMap, inverseBidiMap);
      this.comparator = ((SortedMap)normalMap).comparator();
      this.valueComparator = ((SortedMap)reverseMap).comparator();
   }

   public Comparator<? super K> comparator() {
      return ((SortedMap)this.normalMap).comparator();
   }

   protected DualTreeBidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseMap) {
      return new DualTreeBidiMap<V, K>(normalMap, reverseMap, inverseMap);
   }

   public K firstKey() {
      return (K)((SortedMap)this.normalMap).firstKey();
   }

   public SortedMap<K, V> headMap(K toKey) {
      SortedMap<K, V> sub = ((SortedMap)this.normalMap).headMap(toKey);
      return new ViewMap<K, V>(this, sub);
   }

   public SortedBidiMap<V, K> inverseBidiMap() {
      return (SortedBidiMap)super.inverseBidiMap();
   }

   public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
      return this.inverseBidiMap();
   }

   public SortedBidiMap<V, K> inverseSortedBidiMap() {
      return this.inverseBidiMap();
   }

   public K lastKey() {
      return (K)((SortedMap)this.normalMap).lastKey();
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return new BidiOrderedMapIterator<K, V>(this);
   }

   public K nextKey(K key) {
      if (this.isEmpty()) {
         return null;
      } else if (this.normalMap instanceof OrderedMap) {
         return (K)((OrderedMap)this.normalMap).nextKey(key);
      } else {
         SortedMap<K, V> sm = (SortedMap)this.normalMap;
         Iterator<K> it = sm.tailMap(key).keySet().iterator();
         it.next();
         return (K)(it.hasNext() ? it.next() : null);
      }
   }

   public K previousKey(K key) {
      if (this.isEmpty()) {
         return null;
      } else if (this.normalMap instanceof OrderedMap) {
         return (K)((OrderedMap)this.normalMap).previousKey(key);
      } else {
         SortedMap<K, V> sm = (SortedMap)this.normalMap;
         SortedMap<K, V> hm = sm.headMap(key);
         return (K)(hm.isEmpty() ? null : hm.lastKey());
      }
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.normalMap = new TreeMap(this.comparator);
      this.reverseMap = new TreeMap(this.valueComparator);
      Map<K, V> map = (Map)in.readObject();
      this.putAll(map);
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      SortedMap<K, V> sub = ((SortedMap)this.normalMap).subMap(fromKey, toKey);
      return new ViewMap<K, V>(this, sub);
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      SortedMap<K, V> sub = ((SortedMap)this.normalMap).tailMap(fromKey);
      return new ViewMap<K, V>(this, sub);
   }

   public Comparator<? super V> valueComparator() {
      return ((SortedMap)this.reverseMap).comparator();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.normalMap);
   }

   protected static class BidiOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
      private final AbstractDualBidiMap<K, V> parent;
      private ListIterator<Map.Entry<K, V>> iterator;
      private Map.Entry<K, V> last;

      protected BidiOrderedMapIterator(AbstractDualBidiMap<K, V> parent) {
         this.parent = parent;
         this.iterator = (new ArrayList(parent.entrySet())).listIterator();
      }

      public K getKey() {
         if (this.last == null) {
            throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
         } else {
            return (K)this.last.getKey();
         }
      }

      public V getValue() {
         if (this.last == null) {
            throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
         } else {
            return (V)this.last.getValue();
         }
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public boolean hasPrevious() {
         return this.iterator.hasPrevious();
      }

      public K next() {
         this.last = (Map.Entry)this.iterator.next();
         return (K)this.last.getKey();
      }

      public K previous() {
         this.last = (Map.Entry)this.iterator.previous();
         return (K)this.last.getKey();
      }

      public void remove() {
         this.iterator.remove();
         this.parent.remove(this.last.getKey());
         this.last = null;
      }

      public void reset() {
         this.iterator = (new ArrayList(this.parent.entrySet())).listIterator();
         this.last = null;
      }

      public V setValue(V value) {
         if (this.last == null) {
            throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
         } else if (this.parent.reverseMap.containsKey(value) && this.parent.reverseMap.get(value) != this.last.getKey()) {
            throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
         } else {
            V oldValue = this.parent.put(this.last.getKey(), value);
            this.last.setValue(value);
            return oldValue;
         }
      }

      public String toString() {
         return this.last != null ? "MapIterator[" + this.getKey() + "=" + this.getValue() + "]" : "MapIterator[]";
      }
   }

   protected static class ViewMap<K, V> extends AbstractSortedMapDecorator<K, V> {
      protected ViewMap(DualTreeBidiMap<K, V> bidi, SortedMap<K, V> sm) {
         super(new DualTreeBidiMap(sm, bidi.reverseMap, bidi.inverseBidiMap));
      }

      public void clear() {
         Iterator<K> it = this.keySet().iterator();

         while(it.hasNext()) {
            it.next();
            it.remove();
         }

      }

      public boolean containsValue(Object value) {
         return this.decorated().normalMap.containsValue(value);
      }

      protected DualTreeBidiMap<K, V> decorated() {
         return (DualTreeBidiMap)super.decorated();
      }

      public SortedMap<K, V> headMap(K toKey) {
         return new ViewMap<K, V>(this.decorated(), super.headMap(toKey));
      }

      public K nextKey(K key) {
         return (K)this.decorated().nextKey(key);
      }

      public K previousKey(K key) {
         return (K)this.decorated().previousKey(key);
      }

      public SortedMap<K, V> subMap(K fromKey, K toKey) {
         return new ViewMap<K, V>(this.decorated(), super.subMap(fromKey, toKey));
      }

      public SortedMap<K, V> tailMap(K fromKey) {
         return new ViewMap<K, V>(this.decorated(), super.tailMap(fromKey));
      }
   }
}
