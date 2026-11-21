package org.apache.commons.collections4.map;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections4.set.AbstractSetDecorator;

abstract class AbstractInputCheckedMapDecorator<K, V> extends AbstractMapDecorator<K, V> {
   protected AbstractInputCheckedMapDecorator() {
   }

   protected AbstractInputCheckedMapDecorator(Map<K, V> map) {
      super(map);
   }

   protected abstract V checkSetValue(V var1);

   public Set<Map.Entry<K, V>> entrySet() {
      return (Set<Map.Entry<K, V>>)(this.isSetValueChecking() ? new EntrySet(this.map.entrySet(), this) : this.map.entrySet());
   }

   protected boolean isSetValueChecking() {
      return true;
   }

   private final class EntrySet extends AbstractSetDecorator<Map.Entry<K, V>> {
      private static final long serialVersionUID = 4354731610923110264L;
      private final AbstractInputCheckedMapDecorator<K, V> parent;

      protected EntrySet(final Set<Map.Entry<K, V>> set, final AbstractInputCheckedMapDecorator<K, V> parent) {
         super(set);
         this.parent = parent;
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return AbstractInputCheckedMapDecorator.this.new EntrySetIterator(this.decorated().iterator(), this.parent);
      }

      public Object[] toArray() {
         Object[] array = this.decorated().toArray();

         for(int i = 0; i < array.length; ++i) {
            array[i] = AbstractInputCheckedMapDecorator.this.new MapEntry((Map.Entry)array[i], this.parent);
         }

         return array;
      }

      public <T> T[] toArray(T[] array) {
         Object[] result = array;
         if (array.length > 0) {
            result = Array.newInstance(array.getClass().getComponentType(), 0);
         }

         result = this.decorated().toArray(result);

         for(int i = 0; i < result.length; ++i) {
            result[i] = AbstractInputCheckedMapDecorator.this.new MapEntry((Map.Entry)result[i], this.parent);
         }

         if (result.length > array.length) {
            return (T[])result;
         } else {
            System.arraycopy(result, 0, array, 0, result.length);
            if (array.length > result.length) {
               array[result.length] = null;
            }

            return array;
         }
      }
   }

   private final class EntrySetIterator extends AbstractIteratorDecorator<Map.Entry<K, V>> {
      private final AbstractInputCheckedMapDecorator<K, V> parent;

      protected EntrySetIterator(final Iterator<Map.Entry<K, V>> iterator, final AbstractInputCheckedMapDecorator<K, V> parent) {
         super(iterator);
         this.parent = parent;
      }

      public Map.Entry<K, V> next() {
         Map.Entry<K, V> entry = (Map.Entry)this.getIterator().next();
         return AbstractInputCheckedMapDecorator.this.new MapEntry(entry, this.parent);
      }
   }

   private final class MapEntry extends AbstractMapEntryDecorator<K, V> {
      private final AbstractInputCheckedMapDecorator<K, V> parent;

      protected MapEntry(final Map.Entry<K, V> entry, final AbstractInputCheckedMapDecorator<K, V> parent) {
         super(entry);
         this.parent = parent;
      }

      public V setValue(V value) {
         value = this.parent.checkSetValue(value);
         return (V)this.getMapEntry().setValue(value);
      }
   }
}
