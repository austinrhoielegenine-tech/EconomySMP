package gnu.trove.decorator;

import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.map.TLongCharMap;
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

public class TLongCharMapDecorator extends AbstractMap<Long, Character> implements Map<Long, Character>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TLongCharMap _map;

   public TLongCharMapDecorator() {
   }

   public TLongCharMapDecorator(TLongCharMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TLongCharMap getMap() {
      return this._map;
   }

   public Character put(Long key, Character value) {
      long k;
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
      long k;
      if (key != null) {
         if (!(key instanceof Long)) {
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
      long k;
      if (key != null) {
         if (!(key instanceof Long)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      char v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Long, Character>> entrySet() {
      return new AbstractSet<Map.Entry<Long, Character>>() {
         public int size() {
            return TLongCharMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TLongCharMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TLongCharMapDecorator.this.containsKey(k) && TLongCharMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Long, Character>> iterator() {
            return new Iterator<Map.Entry<Long, Character>>() {
               private final TLongCharIterator it;

               {
                  this.it = TLongCharMapDecorator.this._map.iterator();
               }

               public Map.Entry<Long, Character> next() {
                  this.it.advance();
                  long ik = this.it.key();
                  final Long key = ik == TLongCharMapDecorator.this._map.getNoEntryKey() ? null : TLongCharMapDecorator.this.wrapKey(ik);
                  char iv = this.it.value();
                  final Character v = iv == TLongCharMapDecorator.this._map.getNoEntryValue() ? null : TLongCharMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Long, Character>() {
                     private Character val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Long getKey() {
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
                        return TLongCharMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Long, Character> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Long key = (Long)((Map.Entry)o).getKey();
               TLongCharMapDecorator.this._map.remove(TLongCharMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Long, Character>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TLongCharMapDecorator.this.clear();
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
         return key instanceof Long && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Long, ? extends Character> map) {
      Iterator<? extends Map.Entry<? extends Long, ? extends Character>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Long, ? extends Character> e = (Map.Entry)it.next();
         this.put((Long)e.getKey(), (Character)e.getValue());
      }

   }

   protected Long wrapKey(long k) {
      return k;
   }

   protected long unwrapKey(Object key) {
      return (Long)key;
   }

   protected Character wrapValue(char k) {
      return k;
   }

   protected char unwrapValue(Object value) {
      return (Character)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TLongCharMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
