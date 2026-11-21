package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.ResettableIterator;

public class EntrySetMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K> {
   private final Map<K, V> map;
   private Iterator<Map.Entry<K, V>> iterator;
   private Map.Entry<K, V> last;
   private boolean canRemove;

   public EntrySetMapIterator(Map<K, V> map) {
      this.map = map;
      this.iterator = map.entrySet().iterator();
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

   public K next() {
      this.last = (Map.Entry)this.iterator.next();
      this.canRemove = true;
      return (K)this.last.getKey();
   }

   public void remove() {
      if (!this.canRemove) {
         throw new IllegalStateException("Iterator remove() can only be called once after next()");
      } else {
         this.iterator.remove();
         this.last = null;
         this.canRemove = false;
      }
   }

   public void reset() {
      this.iterator = this.map.entrySet().iterator();
      this.last = null;
      this.canRemove = false;
   }

   public V setValue(V value) {
      if (this.last == null) {
         throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
      } else {
         return (V)this.last.setValue(value);
      }
   }

   public String toString() {
      return this.last != null ? "MapIterator[" + this.getKey() + "=" + this.getValue() + "]" : "MapIterator[]";
   }
}
