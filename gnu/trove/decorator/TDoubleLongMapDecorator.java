package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.map.TDoubleLongMap;
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

public class TDoubleLongMapDecorator extends AbstractMap<Double, Long> implements Map<Double, Long>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TDoubleLongMap _map;

   public TDoubleLongMapDecorator() {
   }

   public TDoubleLongMapDecorator(TDoubleLongMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TDoubleLongMap getMap() {
      return this._map;
   }

   public Long put(Double key, Long value) {
      double k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      long v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      long retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Long get(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      long v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Long remove(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      long v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Double, Long>> entrySet() {
      return new AbstractSet<Map.Entry<Double, Long>>() {
         public int size() {
            return TDoubleLongMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TDoubleLongMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TDoubleLongMapDecorator.this.containsKey(k) && TDoubleLongMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Double, Long>> iterator() {
            return new Iterator<Map.Entry<Double, Long>>() {
               private final TDoubleLongIterator it;

               {
                  this.it = TDoubleLongMapDecorator.this._map.iterator();
               }

               public Map.Entry<Double, Long> next() {
                  this.it.advance();
                  double ik = this.it.key();
                  final Double key = ik == TDoubleLongMapDecorator.this._map.getNoEntryKey() ? null : TDoubleLongMapDecorator.this.wrapKey(ik);
                  long iv = this.it.value();
                  final Long v = iv == TDoubleLongMapDecorator.this._map.getNoEntryValue() ? null : TDoubleLongMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Double, Long>() {
                     private Long val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Double getKey() {
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
                        return TDoubleLongMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Double, Long> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Double key = (Double)((Map.Entry)o).getKey();
               TDoubleLongMapDecorator.this._map.remove(TDoubleLongMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Double, Long>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TDoubleLongMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Long && this._map.containsValue(this.unwrapValue(val));
   }

   public boolean containsKey(Object key) {
      if (key == null) {
         return this._map.containsKey(this._map.getNoEntryKey());
      } else {
         return key instanceof Double && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Double, ? extends Long> map) {
      Iterator<? extends Map.Entry<? extends Double, ? extends Long>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Double, ? extends Long> e = (Map.Entry)it.next();
         this.put((Double)e.getKey(), (Long)e.getValue());
      }

   }

   protected Double wrapKey(double k) {
      return k;
   }

   protected double unwrapKey(Object key) {
      return (Double)key;
   }

   protected Long wrapValue(long k) {
      return k;
   }

   protected long unwrapValue(Object value) {
      return (Long)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TDoubleLongMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
