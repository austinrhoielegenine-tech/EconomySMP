package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.map.TDoubleShortMap;
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

public class TDoubleShortMapDecorator extends AbstractMap<Double, Short> implements Map<Double, Short>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TDoubleShortMap _map;

   public TDoubleShortMapDecorator() {
   }

   public TDoubleShortMapDecorator(TDoubleShortMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TDoubleShortMap getMap() {
      return this._map;
   }

   public Short put(Double key, Short value) {
      double k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      short v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      short retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Short get(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      short v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Short remove(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      short v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Double, Short>> entrySet() {
      return new AbstractSet<Map.Entry<Double, Short>>() {
         public int size() {
            return TDoubleShortMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TDoubleShortMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TDoubleShortMapDecorator.this.containsKey(k) && TDoubleShortMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Double, Short>> iterator() {
            return new Iterator<Map.Entry<Double, Short>>() {
               private final TDoubleShortIterator it;

               {
                  this.it = TDoubleShortMapDecorator.this._map.iterator();
               }

               public Map.Entry<Double, Short> next() {
                  this.it.advance();
                  double ik = this.it.key();
                  final Double key = ik == TDoubleShortMapDecorator.this._map.getNoEntryKey() ? null : TDoubleShortMapDecorator.this.wrapKey(ik);
                  short iv = this.it.value();
                  final Short v = iv == TDoubleShortMapDecorator.this._map.getNoEntryValue() ? null : TDoubleShortMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Double, Short>() {
                     private Short val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Double getKey() {
                        return key;
                     }

                     public Short getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Short setValue(Short value) {
                        this.val = value;
                        return TDoubleShortMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Double, Short> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Double key = (Double)((Map.Entry)o).getKey();
               TDoubleShortMapDecorator.this._map.remove(TDoubleShortMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Double, Short>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TDoubleShortMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Short && this._map.containsValue(this.unwrapValue(val));
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

   public void putAll(Map<? extends Double, ? extends Short> map) {
      Iterator<? extends Map.Entry<? extends Double, ? extends Short>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Double, ? extends Short> e = (Map.Entry)it.next();
         this.put((Double)e.getKey(), (Short)e.getValue());
      }

   }

   protected Double wrapKey(double k) {
      return k;
   }

   protected double unwrapKey(Object key) {
      return (Double)key;
   }

   protected Short wrapValue(short k) {
      return k;
   }

   protected short unwrapValue(Object value) {
      return (Short)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TDoubleShortMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
