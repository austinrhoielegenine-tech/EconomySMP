package gnu.trove.decorator;

import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
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

public class TObjectLongMapDecorator<K> extends AbstractMap<K, Long> implements Map<K, Long>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TObjectLongMap<K> _map;

   public TObjectLongMapDecorator() {
   }

   public TObjectLongMapDecorator(TObjectLongMap<K> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TObjectLongMap<K> getMap() {
      return this._map;
   }

   public Long put(K key, Long value) {
      return value == null ? this.wrapValue(this._map.put(key, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(key, this.unwrapValue(value)));
   }

   public Long get(Object key) {
      long v = this._map.get(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Long remove(Object key) {
      long v = this._map.remove(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<K, Long>> entrySet() {
      return new AbstractSet<Map.Entry<K, Long>>() {
         public int size() {
            return TObjectLongMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TObjectLongMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TObjectLongMapDecorator.this.containsKey(k) && TObjectLongMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<K, Long>> iterator() {
            return new Iterator<Map.Entry<K, Long>>() {
               private final TObjectLongIterator<K> it;

               {
                  this.it = TObjectLongMapDecorator.this._map.iterator();
               }

               public Map.Entry<K, Long> next() {
                  this.it.advance();
                  final K key = this.it.key();
                  final Long v = TObjectLongMapDecorator.this.wrapValue(this.it.value());
                  return new Map.Entry<K, Long>() {
                     private Long val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public K getKey() {
                        return key;
                     }

                     public Long getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Long setValue(Long value) {
                        this.val = value;
                        return TObjectLongMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<K, Long> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               K key = (K)((Map.Entry)o).getKey();
               TObjectLongMapDecorator.this._map.remove(key);
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<K, Long>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TObjectLongMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Long && this._map.containsValue(this.unwrapValue(val));
   }

   public boolean containsKey(Object key) {
      return this._map.containsKey(key);
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this._map.size() == 0;
   }

   public void putAll(Map<? extends K, ? extends Long> map) {
      Iterator<? extends Map.Entry<? extends K, ? extends Long>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends K, ? extends Long> e = (Map.Entry)it.next();
         this.put(e.getKey(), (Long)e.getValue());
      }

   }

   protected Long wrapValue(long k) {
      return k;
   }

   protected long unwrapValue(Object value) {
      return (Long)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TObjectLongMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
