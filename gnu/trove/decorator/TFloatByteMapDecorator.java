package gnu.trove.decorator;

import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.map.TFloatByteMap;
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

public class TFloatByteMapDecorator extends AbstractMap<Float, Byte> implements Map<Float, Byte>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TFloatByteMap _map;

   public TFloatByteMapDecorator() {
   }

   public TFloatByteMapDecorator(TFloatByteMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TFloatByteMap getMap() {
      return this._map;
   }

   public Byte put(Float key, Byte value) {
      float k;
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
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
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
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      byte v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Float, Byte>> entrySet() {
      return new AbstractSet<Map.Entry<Float, Byte>>() {
         public int size() {
            return TFloatByteMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TFloatByteMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TFloatByteMapDecorator.this.containsKey(k) && TFloatByteMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Float, Byte>> iterator() {
            return new Iterator<Map.Entry<Float, Byte>>() {
               private final TFloatByteIterator it;

               {
                  this.it = TFloatByteMapDecorator.this._map.iterator();
               }

               public Map.Entry<Float, Byte> next() {
                  this.it.advance();
                  float ik = this.it.key();
                  final Float key = ik == TFloatByteMapDecorator.this._map.getNoEntryKey() ? null : TFloatByteMapDecorator.this.wrapKey(ik);
                  byte iv = this.it.value();
                  final Byte v = iv == TFloatByteMapDecorator.this._map.getNoEntryValue() ? null : TFloatByteMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Float, Byte>() {
                     private Byte val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Float getKey() {
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
                        return TFloatByteMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Float, Byte> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Float key = (Float)((Map.Entry)o).getKey();
               TFloatByteMapDecorator.this._map.remove(TFloatByteMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Float, Byte>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TFloatByteMapDecorator.this.clear();
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
         return key instanceof Float && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Float, ? extends Byte> map) {
      Iterator<? extends Map.Entry<? extends Float, ? extends Byte>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Float, ? extends Byte> e = (Map.Entry)it.next();
         this.put((Float)e.getKey(), (Byte)e.getValue());
      }

   }

   protected Float wrapKey(float k) {
      return k;
   }

   protected float unwrapKey(Object key) {
      return (Float)key;
   }

   protected Byte wrapValue(byte k) {
      return k;
   }

   protected byte unwrapValue(Object value) {
      return (Byte)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TFloatByteMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
