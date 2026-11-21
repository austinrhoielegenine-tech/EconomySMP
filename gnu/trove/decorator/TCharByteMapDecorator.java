package gnu.trove.decorator;

import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.map.TCharByteMap;
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

public class TCharByteMapDecorator extends AbstractMap<Character, Byte> implements Map<Character, Byte>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TCharByteMap _map;

   public TCharByteMapDecorator() {
   }

   public TCharByteMapDecorator(TCharByteMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TCharByteMap getMap() {
      return this._map;
   }

   public Byte put(Character key, Byte value) {
      char k;
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
      char k;
      if (key != null) {
         if (!(key instanceof Character)) {
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
      char k;
      if (key != null) {
         if (!(key instanceof Character)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      byte v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Character, Byte>> entrySet() {
      return new AbstractSet<Map.Entry<Character, Byte>>() {
         public int size() {
            return TCharByteMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TCharByteMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TCharByteMapDecorator.this.containsKey(k) && TCharByteMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Character, Byte>> iterator() {
            return new Iterator<Map.Entry<Character, Byte>>() {
               private final TCharByteIterator it;

               {
                  this.it = TCharByteMapDecorator.this._map.iterator();
               }

               public Map.Entry<Character, Byte> next() {
                  this.it.advance();
                  char ik = this.it.key();
                  final Character key = ik == TCharByteMapDecorator.this._map.getNoEntryKey() ? null : TCharByteMapDecorator.this.wrapKey(ik);
                  byte iv = this.it.value();
                  final Byte v = iv == TCharByteMapDecorator.this._map.getNoEntryValue() ? null : TCharByteMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Character, Byte>() {
                     private Byte val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Character getKey() {
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
                        return TCharByteMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Character, Byte> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Character key = (Character)((Map.Entry)o).getKey();
               TCharByteMapDecorator.this._map.remove(TCharByteMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Character, Byte>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TCharByteMapDecorator.this.clear();
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
         return key instanceof Character && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Character, ? extends Byte> map) {
      Iterator<? extends Map.Entry<? extends Character, ? extends Byte>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Character, ? extends Byte> e = (Map.Entry)it.next();
         this.put((Character)e.getKey(), (Byte)e.getValue());
      }

   }

   protected Character wrapKey(char k) {
      return k;
   }

   protected char unwrapKey(Object key) {
      return (Character)key;
   }

   protected Byte wrapValue(byte k) {
      return k;
   }

   protected byte unwrapValue(Object value) {
      return (Byte)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TCharByteMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
