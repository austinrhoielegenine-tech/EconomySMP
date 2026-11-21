package gnu.trove.decorator;

import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
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

public class TByteDoubleMapDecorator extends AbstractMap<Byte, Double> implements Map<Byte, Double>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TByteDoubleMap _map;

   public TByteDoubleMapDecorator() {
   }

   public TByteDoubleMapDecorator(TByteDoubleMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TByteDoubleMap getMap() {
      return this._map;
   }

   public Double put(Byte key, Double value) {
      byte k;
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
      byte k;
      if (key != null) {
         if (!(key instanceof Byte)) {
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
      byte k;
      if (key != null) {
         if (!(key instanceof Byte)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      double v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Byte, Double>> entrySet() {
      return new AbstractSet<Map.Entry<Byte, Double>>() {
         public int size() {
            return TByteDoubleMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TByteDoubleMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TByteDoubleMapDecorator.this.containsKey(k) && TByteDoubleMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Byte, Double>> iterator() {
            return new Iterator<Map.Entry<Byte, Double>>() {
               private final TByteDoubleIterator it;

               {
                  this.it = TByteDoubleMapDecorator.this._map.iterator();
               }

               public Map.Entry<Byte, Double> next() {
                  this.it.advance();
                  byte ik = this.it.key();
                  final Byte key = ik == TByteDoubleMapDecorator.this._map.getNoEntryKey() ? null : TByteDoubleMapDecorator.this.wrapKey(ik);
                  double iv = this.it.value();
                  final Double v = iv == TByteDoubleMapDecorator.this._map.getNoEntryValue() ? null : TByteDoubleMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Byte, Double>() {
                     private Double val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Byte getKey() {
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
                        return TByteDoubleMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Byte, Double> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Byte key = (Byte)((Map.Entry)o).getKey();
               TByteDoubleMapDecorator.this._map.remove(TByteDoubleMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Byte, Double>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TByteDoubleMapDecorator.this.clear();
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
         return key instanceof Byte && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Byte, ? extends Double> map) {
      Iterator<? extends Map.Entry<? extends Byte, ? extends Double>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Byte, ? extends Double> e = (Map.Entry)it.next();
         this.put((Byte)e.getKey(), (Double)e.getValue());
      }

   }

   protected Byte wrapKey(byte k) {
      return k;
   }

   protected byte unwrapKey(Object key) {
      return (Byte)key;
   }

   protected Double wrapValue(double k) {
      return k;
   }

   protected double unwrapValue(Object value) {
      return (Double)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TByteDoubleMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
