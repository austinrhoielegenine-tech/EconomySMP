package gnu.trove.decorator;

import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
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

public class TLongFloatMapDecorator extends AbstractMap<Long, Float> implements Map<Long, Float>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TLongFloatMap _map;

   public TLongFloatMapDecorator() {
   }

   public TLongFloatMapDecorator(TLongFloatMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TLongFloatMap getMap() {
      return this._map;
   }

   public Float put(Long key, Float value) {
      long k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      float v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      float retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Float get(Object key) {
      long k;
      if (key != null) {
         if (!(key instanceof Long)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      float v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Float remove(Object key) {
      long k;
      if (key != null) {
         if (!(key instanceof Long)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      float v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Long, Float>> entrySet() {
      return new AbstractSet<Map.Entry<Long, Float>>() {
         public int size() {
            return TLongFloatMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TLongFloatMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TLongFloatMapDecorator.this.containsKey(k) && TLongFloatMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Long, Float>> iterator() {
            return new Iterator<Map.Entry<Long, Float>>() {
               private final TLongFloatIterator it;

               {
                  this.it = TLongFloatMapDecorator.this._map.iterator();
               }

               public Map.Entry<Long, Float> next() {
                  this.it.advance();
                  long ik = this.it.key();
                  final Long key = ik == TLongFloatMapDecorator.this._map.getNoEntryKey() ? null : TLongFloatMapDecorator.this.wrapKey(ik);
                  float iv = this.it.value();
                  final Float v = iv == TLongFloatMapDecorator.this._map.getNoEntryValue() ? null : TLongFloatMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Long, Float>() {
                     private Float val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Long getKey() {
                        return key;
                     }

                     public Float getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Float setValue(Float value) {
                        this.val = value;
                        return TLongFloatMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Long, Float> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Long key = (Long)((Map.Entry)o).getKey();
               TLongFloatMapDecorator.this._map.remove(TLongFloatMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Long, Float>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TLongFloatMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Float && this._map.containsValue(this.unwrapValue(val));
   }

   public boolean containsKey(Object key) {
      if (key == null) {
         return this._map.containsKey(this._map.getNoEntryKey());
      } else {
         return key instanceof Long && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Long, ? extends Float> map) {
      Iterator<? extends Map.Entry<? extends Long, ? extends Float>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Long, ? extends Float> e = (Map.Entry)it.next();
         this.put((Long)e.getKey(), (Float)e.getValue());
      }

   }

   protected Long wrapKey(long k) {
      return k;
   }

   protected long unwrapKey(Object key) {
      return (Long)key;
   }

   protected Float wrapValue(float k) {
      return k;
   }

   protected float unwrapValue(Object value) {
      return (Float)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TLongFloatMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
