package gnu.trove.decorator;

import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
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

public class TFloatObjectMapDecorator<V> extends AbstractMap<Float, V> implements Map<Float, V>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TFloatObjectMap<V> _map;

   public TFloatObjectMapDecorator() {
   }

   public TFloatObjectMapDecorator(TFloatObjectMap<V> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TFloatObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Float key, V value) {
      float k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      return this._map.put(k, value);
   }

   public V get(Object key) {
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey((Float)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.get(k);
   }

   public void clear() {
      this._map.clear();
   }

   public V remove(Object key) {
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey((Float)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.remove(k);
   }

   public Set<Map.Entry<Float, V>> entrySet() {
      return new AbstractSet<Map.Entry<Float, V>>() {
         public int size() {
            return TFloatObjectMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TFloatObjectMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TFloatObjectMapDecorator.this.containsKey(k) && TFloatObjectMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Float, V>> iterator() {
            return new Iterator<Map.Entry<Float, V>>() {
               private final TFloatObjectIterator<V> it;

               {
                  this.it = TFloatObjectMapDecorator.this._map.iterator();
               }

               public Map.Entry<Float, V> next() {
                  this.it.advance();
                  float k = this.it.key();
                  final Float key = k == TFloatObjectMapDecorator.this._map.getNoEntryKey() ? null : TFloatObjectMapDecorator.this.wrapKey(k);
                  final V v = this.it.value();
                  return new Map.Entry<Float, V>() {
                     private V val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Float getKey() {
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
                        return (V)TFloatObjectMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Float, V> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Float key = (Float)((Map.Entry)o).getKey();
               TFloatObjectMapDecorator.this._map.remove(TFloatObjectMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Float, V>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TFloatObjectMapDecorator.this.clear();
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
         return key instanceof Float && this._map.containsKey((Float)key);
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Float, ? extends V> map) {
      Iterator<? extends Map.Entry<? extends Float, ? extends V>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Float, ? extends V> e = (Map.Entry)it.next();
         this.put((Float)e.getKey(), e.getValue());
      }

   }

   protected Float wrapKey(float k) {
      return k;
   }

   protected float unwrapKey(Float key) {
      return key;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TFloatObjectMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
