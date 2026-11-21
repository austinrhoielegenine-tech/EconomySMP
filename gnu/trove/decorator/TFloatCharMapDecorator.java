package gnu.trove.decorator;

import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.map.TFloatCharMap;
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

public class TFloatCharMapDecorator extends AbstractMap<Float, Character> implements Map<Float, Character>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TFloatCharMap _map;

   public TFloatCharMapDecorator() {
   }

   public TFloatCharMapDecorator(TFloatCharMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TFloatCharMap getMap() {
      return this._map;
   }

   public Character put(Float key, Character value) {
      float k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      char v;
      if (value == null) {
         v = this._map.getNoEntryValue();
      } else {
         v = this.unwrapValue(value);
      }

      char retval = this._map.put(k, v);
      return retval == this._map.getNoEntryValue() ? null : this.wrapValue(retval);
   }

   public Character get(Object key) {
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      char v = this._map.get(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Character remove(Object key) {
      float k;
      if (key != null) {
         if (!(key instanceof Float)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      char v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Float, Character>> entrySet() {
      return new AbstractSet<Map.Entry<Float, Character>>() {
         public int size() {
            return TFloatCharMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TFloatCharMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TFloatCharMapDecorator.this.containsKey(k) && TFloatCharMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Float, Character>> iterator() {
            return new Iterator<Map.Entry<Float, Character>>() {
               private final TFloatCharIterator it;

               {
                  this.it = TFloatCharMapDecorator.this._map.iterator();
               }

               public Map.Entry<Float, Character> next() {
                  this.it.advance();
                  float ik = this.it.key();
                  final Float key = ik == TFloatCharMapDecorator.this._map.getNoEntryKey() ? null : TFloatCharMapDecorator.this.wrapKey(ik);
                  char iv = this.it.value();
                  final Character v = iv == TFloatCharMapDecorator.this._map.getNoEntryValue() ? null : TFloatCharMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Float, Character>() {
                     private Character val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Float getKey() {
                        return key;
                     }

                     public Character getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Character setValue(Character value) {
                        this.val = value;
                        return TFloatCharMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Float, Character> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Float key = (Float)((Map.Entry)o).getKey();
               TFloatCharMapDecorator.this._map.remove(TFloatCharMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Float, Character>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TFloatCharMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Character && this._map.containsValue(this.unwrapValue(val));
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

   public void putAll(Map<? extends Float, ? extends Character> map) {
      Iterator<? extends Map.Entry<? extends Float, ? extends Character>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Float, ? extends Character> e = (Map.Entry)it.next();
         this.put((Float)e.getKey(), (Character)e.getValue());
      }

   }

   protected Float wrapKey(float k) {
      return k;
   }

   protected float unwrapKey(Object key) {
      return (Float)key;
   }

   protected Character wrapValue(char k) {
      return k;
   }

   protected char unwrapValue(Object value) {
      return (Character)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TFloatCharMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
