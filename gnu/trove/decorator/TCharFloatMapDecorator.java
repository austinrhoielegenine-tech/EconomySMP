package gnu.trove.decorator;

import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.map.TCharFloatMap;
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

public class TCharFloatMapDecorator extends AbstractMap<Character, Float> implements Map<Character, Float>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TCharFloatMap _map;

   public TCharFloatMapDecorator() {
   }

   public TCharFloatMapDecorator(TCharFloatMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TCharFloatMap getMap() {
      return this._map;
   }

   public Float put(Character key, Float value) {
      char k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      float v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      float retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Float get(Object key) {
      char k;
      if (key != null) {
         if (!(key instanceof Character)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      float v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Float remove(Object key) {
      char k;
      if (key != null) {
         if (!(key instanceof Character)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      float v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Character, Float>> entrySet() {
      return new AbstractSet<Map.Entry<Character, Float>>() {
         public int size() {
            return TCharFloatMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TCharFloatMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TCharFloatMapDecorator.this.containsKey(k) && TCharFloatMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Character, Float>> iterator() {
            return new Iterator<Map.Entry<Character, Float>>() {
               private final TCharFloatIterator it;

               {
                  this.it = TCharFloatMapDecorator.this._map.iterator();
               }

               public Map.Entry<Character, Float> next() {
                  this.it.advance();
                  char ik = this.it.key();
                  final Character key = ik == TCharFloatMapDecorator.this._map.getNoEntryKey() ? null : TCharFloatMapDecorator.this.wrapKey(ik);
                  float iv = this.it.value();
                  final Float v = iv == TCharFloatMapDecorator.this._map.getNoEntryValue() ? null : TCharFloatMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Character, Float>() {
                     private Float val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Character getKey() {
                        return key;
                     }

                     public Float getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Float setValue(Float value) {
                        this.val = value;
                        return TCharFloatMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Character, Float> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Character key = (Character)((Map.Entry)o).getKey();
               TCharFloatMapDecorator.this._map.remove(TCharFloatMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Character, Float>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TCharFloatMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Float && this._map.containsValue(this.unwrapValue(val));
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

   public void putAll(Map<? extends Character, ? extends Float> map) {
      Iterator<? extends Map.Entry<? extends Character, ? extends Float>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Character, ? extends Float> e = (Map.Entry)it.next();
         this.put((Character)e.getKey(), (Float)e.getValue());
      }

   }

   protected Character wrapKey(char k) {
      return k;
   }

   protected char unwrapKey(Object key) {
      return (Character)key;
   }

   protected Float wrapValue(float k) {
      return k;
   }

   protected float unwrapValue(Object value) {
      return (Float)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TCharFloatMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
