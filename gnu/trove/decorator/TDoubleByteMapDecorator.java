package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.map.TDoubleByteMap;
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

public class TDoubleByteMapDecorator extends AbstractMap<Double, Byte> implements Map<Double, Byte>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TDoubleByteMap _map;

   public TDoubleByteMapDecorator() {
   }

   public TDoubleByteMapDecorator(TDoubleByteMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TDoubleByteMap getMap() {
      return this._map;
   }

   public Byte put(Double key, Byte value) {
      double k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      byte v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      byte retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Byte get(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      byte v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Byte remove(Object key) {
      double k;
      if (key != null) {
         if (!(key instanceof Double)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      byte v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Double, Byte>> entrySet() {
      return new AbstractSet<Map.Entry<Double, Byte>>() {
         public int size() {
            return TDoubleByteMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TDoubleByteMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TDoubleByteMapDecorator.this.containsKey(k) && TDoubleByteMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Double, Byte>> iterator() {
            return new Iterator<Map.Entry<Double, Byte>>() {
               private final TDoubleByteIterator it;

               {
                  this.it = TDoubleByteMapDecorator.this._map.iterator();
               }

               public Map.Entry<Double, Byte> next() {
                  this.it.advance();
                  double ik = this.it.key();
                  final Double key = ik == TDoubleByteMapDecorator.this._map.getNoEntryKey() ? null : TDoubleByteMapDecorator.this.wrapKey(ik);
                  byte iv = this.it.value();
                  final Byte v = iv == TDoubleByteMapDecorator.this._map.getNoEntryValue() ? null : TDoubleByteMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Double, Byte>() {
                     private Byte val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Double getKey() {
                        return key;
                     }

                     public Byte getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Byte setValue(Byte value) {
                        this.val = value;
                        return TDoubleByteMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Double, Byte> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Double key = (Double)((Map.Entry)o).getKey();
               TDoubleByteMapDecorator.this._map.remove(TDoubleByteMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Double, Byte>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TDoubleByteMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Byte && this._map.containsValue(this.unwrapValue(val));
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

   public void putAll(Map<? extends Double, ? extends Byte> map) {
      Iterator<? extends Map.Entry<? extends Double, ? extends Byte>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Double, ? extends Byte> e = (Map.Entry)it.next();
         this.put((Double)e.getKey(), (Byte)e.getValue());
      }

   }

   protected Double wrapKey(double k) {
      return k;
   }

   protected double unwrapKey(Object key) {
      return (Double)key;
   }

   protected Byte wrapValue(byte k) {
      return k;
   }

   protected byte unwrapValue(Object value) {
      return (Byte)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TDoubleByteMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
