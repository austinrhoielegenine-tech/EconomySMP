package gnu.trove.decorator;

import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
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

public class TShortShortMapDecorator extends AbstractMap<Short, Short> implements Map<Short, Short>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TShortShortMap _map;

   public TShortShortMapDecorator() {
   }

   public TShortShortMapDecorator(TShortShortMap map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TShortShortMap getMap() {
      return this._map;
   }

   public Short put(Short key, Short value) {
      short k;
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
      short k;
      if (key != null) {
         if (!(key instanceof Short)) {
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
      short k;
      if (key != null) {
         if (!(key instanceof Short)) {
            return null;
         }

         k = this.unwrapKey(key);
      } else {
         k = this._map.getNoEntryKey();
      }

      short v = this._map.remove(k);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<Short, Short>> entrySet() {
      return new AbstractSet<Map.Entry<Short, Short>>() {
         public int size() {
            return TShortShortMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TShortShortMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TShortShortMapDecorator.this.containsKey(k) && TShortShortMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Short, Short>> iterator() {
            return new Iterator<Map.Entry<Short, Short>>() {
               private final TShortShortIterator it;

               {
                  this.it = TShortShortMapDecorator.this._map.iterator();
               }

               public Map.Entry<Short, Short> next() {
                  this.it.advance();
                  short ik = this.it.key();
                  final Short key = ik == TShortShortMapDecorator.this._map.getNoEntryKey() ? null : TShortShortMapDecorator.this.wrapKey(ik);
                  short iv = this.it.value();
                  final Short v = iv == TShortShortMapDecorator.this._map.getNoEntryValue() ? null : TShortShortMapDecorator.this.wrapValue(iv);
                  return new Map.Entry<Short, Short>() {
                     private Short val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Short getKey() {
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
                        return TShortShortMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Short, Short> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Short key = (Short)((Map.Entry)o).getKey();
               TShortShortMapDecorator.this._map.remove(TShortShortMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Short, Short>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TShortShortMapDecorator.this.clear();
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
         return key instanceof Short && this._map.containsKey(this.unwrapKey(key));
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Short, ? extends Short> map) {
      Iterator<? extends Map.Entry<? extends Short, ? extends Short>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Short, ? extends Short> e = (Map.Entry)it.next();
         this.put((Short)e.getKey(), (Short)e.getValue());
      }

   }

   protected Short wrapKey(short k) {
      return k;
   }

   protected short unwrapKey(Object key) {
      return (Short)key;
   }

   protected Short wrapValue(short k) {
      return k;
   }

   protected short unwrapValue(Object value) {
      return (Short)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TShortShortMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
