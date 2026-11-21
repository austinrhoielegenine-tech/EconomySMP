package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.map.TDoubleDoubleMap;
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

public class TDoubleDoubleMapDecorator extends AbstractMap<Double, Double> implements Map<Double, Double>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TDoubleDoubleMap _map;

   public TDoubleDoubleMapDecorator() {
   }

   public TDoubleDoubleMapDecorator(TDoubleDoubleMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TDoubleDoubleMap getMap() {
      return this._map;
   }

   public Double put(Double key, Double value) {
      double k;
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
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
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
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      double v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Double, Double>> entrySet() {
      return new AbstractSet<Map.Entry<Double, Double>>() {
         public int size() {
            return TDoubleDoubleMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TDoubleDoubleMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TDoubleDoubleMapDecorator.this.containsKey(k) && TDoubleDoubleMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Double, Double>> iterator() {
            return new Iterator<Map.Entry<Double, Double>>() {
               private final TDoubleDoubleIterator it;

               {
                  this.it = TDoubleDoubleMapDecorator.this._map.iterator();
               }

               public Map.Entry<Double, Double> next() {
                  this.it.advance();
                  double ik = this.it.key();
                  final Double key = ik == TDoubleDoubleMapDecorator.this._map.getNoEntryKey() ? null : TDoubleDoubleMapDecorator.this.wrapKey(ik);
                  double iv = this.it.value();
                  final Double v = iv == TDoubleDoubleMapDecorator.this._map.getNoEntryValue() ? null : TDoubleDoubleMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Double, Double>() {
                     private Double val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Double getKey() {
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
                        return TDoubleDoubleMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Double, Double> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Double key = (Double)((Map.Entry)o).getKey();
               TDoubleDoubleMapDecorator.this._map.remove(TDoubleDoubleMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Double, Double>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TDoubleDoubleMapDecorator.this.clear();
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
         return key instanceof Double && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Double, ? extends Double> map) {
      Iterator<? extends Map.Entry<? extends Double, ? extends Double>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Double, ? extends Double> e = (Map.Entry)it.next();
         this.put((Double)e.getKey(), (Double)e.getValue());
      }

   }

   protected Double wrapKey(double k) {
      return k;
   }

   protected double unwrapKey(Object key) {
      return (Double)key;
   }

   protected Double wrapValue(double k) {
      return k;
   }

   protected double unwrapValue(Object value) {
      return (Double)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TDoubleDoubleMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
