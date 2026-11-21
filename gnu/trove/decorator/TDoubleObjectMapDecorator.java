package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.map.TDoubleObjectMap;
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

public class TDoubleObjectMapDecorator<V> extends AbstractMap<Double, V> implements Map<Double, V>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TDoubleObjectMap<V> _map;

   public TDoubleObjectMapDecorator() {
   }

   public TDoubleObjectMapDecorator(TDoubleObjectMap<V> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TDoubleObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Double key, V value) {
      double k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      return this._map.put(k, value);
   }

   public V get(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey((Double)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.get(k);
   }

   public void clear() {
      this._map.clear();
   }

   public V remove(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey((Double)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.remove(k);
   }

   public Set<Map.Entry<Double, V>> entrySet() {
      return new AbstractSet<Map.Entry<Double, V>>() {
         public int size() {
            return TDoubleObjectMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TDoubleObjectMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TDoubleObjectMapDecorator.this.containsKey(k) && TDoubleObjectMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Double, V>> iterator() {
            return new Iterator<Map.Entry<Double, V>>() {
               private final TDoubleObjectIterator<V> it;

               {
                  this.it = TDoubleObjectMapDecorator.this._map.iterator();
               }

               public Map.Entry<Double, V> next() {
                  this.it.advance();
                  double k = this.it.key();
                  final Double key = k == TDoubleObjectMapDecorator.this._map.getNoEntryKey() ? null : TDoubleObjectMapDecorator.this.wrapKey(k);
                  final V v = this.it.value();
                  return new Map.Entry<Double, V>() {
                     private V val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Double getKey() {
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
                        return (V)TDoubleObjectMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Double, V> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Double key = (Double)((Map.Entry)o).getKey();
               TDoubleObjectMapDecorator.this._map.remove(TDoubleObjectMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Double, V>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TDoubleObjectMapDecorator.this.clear();
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
         return key instanceof Double && this._map.containsKey((Double)key);
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Double, ? extends V> map) {
      Iterator<? extends Map.Entry<? extends Double, ? extends V>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Double, ? extends V> e = (Map.Entry)it.next();
         this.put((Double)e.getKey(), e.getValue());
      }

   }

   protected Double wrapKey(double k) {
      return k;
   }

   protected double unwrapKey(Double key) {
      return key;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TDoubleObjectMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
