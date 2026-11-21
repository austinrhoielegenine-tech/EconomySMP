package org.apache.commons.collections4.multimap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.collections4.SetValuedMap;

public abstract class AbstractSetValuedMap<K, V> extends AbstractMultiValuedMap<K, V> implements SetValuedMap<K, V> {
   protected AbstractSetValuedMap() {
   }

   protected AbstractSetValuedMap(Map<K, ? extends Set<V>> map) {
      super(map);
   }

   protected abstract Set<V> createCollection();

   public Set<V> get(K key) {
      return this.wrappedCollection(key);
   }

   protected Map<K, Set<V>> getMap() {
      return super.getMap();
   }

   public Set<V> remove(Object key) {
      return SetUtils.<V>emptyIfNull((Set)this.getMap().remove(key));
   }

   Set<V> wrappedCollection(K key) {
      return new WrappedSet(key);
   }

   private final class WrappedSet extends AbstractMultiValuedMap<K, V>.WrappedCollection implements Set<V> {
      WrappedSet(final K key) {
         super(key);
      }

      public boolean equals(Object other) {
         Set<V> set = (Set)this.getMapping();
         if (set == null) {
            return Collections.emptySet().equals(other);
         } else if (!(other instanceof Set)) {
            return false;
         } else {
            Set<?> otherSet = (Set)other;
            return SetUtils.isEqualSet(set, otherSet);
         }
      }

      public int hashCode() {
         Set<V> set = (Set)this.getMapping();
         return SetUtils.hashCodeForSet(set);
      }
   }
}
