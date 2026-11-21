package gnu.trove.decorator;

import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
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

public class TByteShortMapDecorator extends AbstractMap<Byte, Short> implements Map<Byte, Short>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TByteShortMap _map;

   public TByteShortMapDecorator() {
   }

   public TByteShortMapDecorator(TByteShortMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TByteShortMap getMap() {
      return this._map;
   }

   public Short put(Byte key, Short value) {
      byte k;
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
      byte k;
      if (key != null) {
         if (!(key instanceof Byte)) {
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
      byte k;
      if (key != null) {
         if (!(key instanceof Byte)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      short v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Byte, Short>> entrySet() {
      return new AbstractSet<Map.Entry<Byte, Short>>() {
         public int size() {
            return TByteShortMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TByteShortMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TByteShortMapDecorator.this.containsKey(k) && TByteShortMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Byte, Short>> iterator() {
            return new Iterator<Map.Entry<Byte, Short>>() {
               private final TByteShortIterator it;

               {
                  this.it = TByteShortMapDecorator.this._map.iterator();
               }

               public Map.Entry<Byte, Short> next() {
                  this.it.advance();
                  byte ik = this.it.key();
                  final Byte key = ik == TByteShortMapDecorator.this._map.getNoEntryKey() ? null : TByteShortMapDecorator.this.wrapKey(ik);
                  short iv = this.it.value();
                  final Short v = iv == TByteShortMapDecorator.this._map.getNoEntryValue() ? null : TByteShortMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Byte, Short>() {
                     private Short val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Byte getKey() {
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
                        return TByteShortMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Byte, Short> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Byte key = (Byte)((Map.Entry)o).getKey();
               TByteShortMapDecorator.this._map.remove(TByteShortMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Byte, Short>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TByteShortMapDecorator.this.clear();
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
         return key instanceof Byte && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Byte, ? extends Short> map) {
      Iterator<? extends Map.Entry<? extends Byte, ? extends Short>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Byte, ? extends Short> e = (Map.Entry)it.next();
         this.put((Byte)e.getKey(), (Short)e.getValue());
      }

   }

   protected Byte wrapKey(byte k) {
      return k;
   }

   protected byte unwrapKey(Object key) {
      return (Byte)key;
   }

   protected Short wrapValue(short k) {
      return k;
   }

   protected short unwrapValue(Object value) {
      return (Short)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TByteShortMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
