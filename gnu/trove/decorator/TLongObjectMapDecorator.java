package gnu.trove.decorator;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TLongObjectMapDecorator<V> extends AbstractMap<Long, V> implements Map<Long, V>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TLongObjectMap<V> _map;

   public TLongObjectMapDecorator() {
   }

   public TLongObjectMapDecorator(TLongObjectMap<V> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TLongObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Long key, V value) {
      long k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      return this._map.put(k, value);
   }

   public V get(Object key) {
      long k;
      if (key != null) {
         if (!(key instanceof Long)) {
            return null;
         }

         k = this.unwrapKey((Long)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.get(k);
   }

   public void clear() {
      this._map.clear();
   }

   public V remove(Object key) {
      long k;
      if (key != null) {
         if (!(key instanceof Long)) {
            return null;
         }

         k = this.unwrapKey((Long)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.remove(k);
   }

   public Set<Map.Entry<Long, V>> entrySet() {
      return new AbstractSet<Map.Entry<Long, V>>() {
         public int size() {
            return TLongObjectMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TLongObjectMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TLongObjectMapDecorator.this.containsKey(k) && TLongObjectMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Long, V>> iterator() {
            return new Iterator<Map.Entry<Long, V>>() {
               private final TLongObjectIterator<V> it;

               {
                  this.it = TLongObjectMapDecorator.this._map.iterator();
               }

               public Map.Entry<Long, V> next() {
                  this.it.advance();
                  long k = this.it.key();
                  final Long key = k == TLongObjectMapDecorator.this._map.getNoEntryKey() ? null : TLongObjectMapDecorator.this.wrapKey(k);
                  final V v = this.it.value();
                  return new Map.Entry<Long, V>() {
                     private V val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Long getKey() {
                        return key;
                     }

                     public V getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public V setValue(V value) {
                        this.val = value;
                        return (V)TLongObjectMapDecorator.this.put(key, value);
                     }
                  };
               }

               public boolean hasNext() {
                  return this.it.hasNext();
               }

               public void remove() {
                  this.it.remove();
               }
            };
         }

         public boolean add(Map.Entry<Long, V> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Long key = (Long)((Map.Entry)o).getKey();
               TLongObjectMapDecorator.this._map.remove(TLongObjectMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Long, V>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TLongObjectMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return this._map.containsValue(val);
   }

   public boolean containsKey(Object key) {
      if (key == null) {
         return this._map.containsKey(this._map.getNoEntryKey());
      } else {
         return key instanceof Long && this._map.containsKey((Long)key);
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Long, ? extends V> map) {
      Iterator<? extends Map.Entry<? extends Long, ? extends V>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Long, ? extends V> e = (Map.Entry)it.next();
         this.put((Long)e.getKey(), e.getValue());
      }

   }

   protected Long wrapKey(long k) {
      return k;
   }

   protected long unwrapKey(Long key) {
      return key;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TLongObjectMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
