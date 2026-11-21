package gnu.trove.decorator;

import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
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

public class TFloatDoubleMapDecorator extends AbstractMap<Float, Double> implements Map<Float, Double>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TFloatDoubleMap _map;

   public TFloatDoubleMapDecorator() {
   }

   public TFloatDoubleMapDecorator(TFloatDoubleMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TFloatDoubleMap getMap() {
      return this._map;
   }

   public Double put(Float key, Double value) {
      float k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      double v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      double retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Double get(Object key) {
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      double v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Double remove(Object key) {
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      double v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Float, Double>> entrySet() {
      return new AbstractSet<Map.Entry<Float, Double>>() {
         public int size() {
            return TFloatDoubleMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TFloatDoubleMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TFloatDoubleMapDecorator.this.containsKey(k) && TFloatDoubleMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Float, Double>> iterator() {
            return new Iterator<Map.Entry<Float, Double>>() {
               private final TFloatDoubleIterator it;

               {
                  this.it = TFloatDoubleMapDecorator.this._map.iterator();
               }

               public Map.Entry<Float, Double> next() {
                  this.it.advance();
                  float ik = this.it.key();
                  final Float key = ik == TFloatDoubleMapDecorator.this._map.getNoEntryKey() ? null : TFloatDoubleMapDecorator.this.wrapKey(ik);
                  double iv = this.it.value();
                  final Double v = iv == TFloatDoubleMapDecorator.this._map.getNoEntryValue() ? null : TFloatDoubleMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Float, Double>() {
                     private Double val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Float getKey() {
                        return key;
                     }

                     public Double getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Double setValue(Double value) {
                        this.val = value;
                        return TFloatDoubleMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Float, Double> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Float key = (Float)((Map.Entry)o).getKey();
               TFloatDoubleMapDecorator.this._map.remove(TFloatDoubleMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Float, Double>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TFloatDoubleMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Double && this._map.containsValue(this.unwrapValue(val));
   }

   public boolean containsKey(Object key) {
      if (key == null) {
         return this._map.containsKey(this._map.getNoEntryKey());
      } else {
         return key instanceof Float && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Float, ? extends Double> map) {
      Iterator<? extends Map.Entry<? extends Float, ? extends Double>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Float, ? extends Double> e = (Map.Entry)it.next();
         this.put((Float)e.getKey(), (Double)e.getValue());
      }

   }

   protected Float wrapKey(float k) {
      return k;
   }

   protected float unwrapKey(Object key) {
      return (Float)key;
   }

   protected Double wrapValue(double k) {
      return k;
   }

   protected double unwrapValue(Object value) {
      return (Double)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TFloatDoubleMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
