package gnu.trove.decorator;

import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.map.TShortDoubleMap;
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

public class TShortDoubleMapDecorator extends AbstractMap<Short, Double> implements Map<Short, Double>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TShortDoubleMap _map;

   public TShortDoubleMapDecorator() {
   }

   public TShortDoubleMapDecorator(TShortDoubleMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TShortDoubleMap getMap() {
      return this._map;
   }

   public Double put(Short key, Double value) {
      short k;
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
      short k;
      if (key != null) {
         if (!(key instanceof Short)) {
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
      short k;
      if (key != null) {
         if (!(key instanceof Short)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      double v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Short, Double>> entrySet() {
      return new AbstractSet<Map.Entry<Short, Double>>() {
         public int size() {
            return TShortDoubleMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TShortDoubleMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TShortDoubleMapDecorator.this.containsKey(k) && TShortDoubleMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Short, Double>> iterator() {
            return new Iterator<Map.Entry<Short, Double>>() {
               private final TShortDoubleIterator it;

               {
                  this.it = TShortDoubleMapDecorator.this._map.iterator();
               }

               public Map.Entry<Short, Double> next() {
                  this.it.advance();
                  short ik = this.it.key();
                  final Short key = ik == TShortDoubleMapDecorator.this._map.getNoEntryKey() ? null : TShortDoubleMapDecorator.this.wrapKey(ik);
                  double iv = this.it.value();
                  final Double v = iv == TShortDoubleMapDecorator.this._map.getNoEntryValue() ? null : TShortDoubleMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Short, Double>() {
                     private Double val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Short getKey() {
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
                        return TShortDoubleMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Short, Double> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Short key = (Short)((Map.Entry)o).getKey();
               TShortDoubleMapDecorator.this._map.remove(TShortDoubleMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Short, Double>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TShortDoubleMapDecorator.this.clear();
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
         return key instanceof Short && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Short, ? extends Double> map) {
      Iterator<? extends Map.Entry<? extends Short, ? extends Double>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Short, ? extends Double> e = (Map.Entry)it.next();
         this.put((Short)e.getKey(), (Double)e.getValue());
      }

   }

   protected Short wrapKey(short k) {
      return k;
   }

   protected short unwrapKey(Object key) {
      return (Short)key;
   }

   protected Double wrapValue(double k) {
      return k;
   }

   protected double unwrapValue(Object value) {
      return (Double)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TShortDoubleMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
