package gnu.trove.decorator;

import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
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

public class TObjectShortMapDecorator<K> extends AbstractMap<K, Short> implements Map<K, Short>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TObjectShortMap<K> _map;

   public TObjectShortMapDecorator() {
   }

   public TObjectShortMapDecorator(TObjectShortMap<K> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TObjectShortMap<K> getMap() {
      return this._map;
   }

   public Short put(K key, Short value) {
      return value == null ? this.wrapValue(this._map.put(key, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(key, this.unwrapValue(value)));
   }

   public Short get(Object key) {
      short v = this._map.get(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Short remove(Object key) {
      short v = this._map.remove(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<K, Short>> entrySet() {
      return new AbstractSet<Map.Entry<K, Short>>() {
         public int size() {
            return TObjectShortMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TObjectShortMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TObjectShortMapDecorator.this.containsKey(k) && TObjectShortMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<K, Short>> iterator() {
            return new Iterator<Map.Entry<K, Short>>() {
               private final TObjectShortIterator<K> it;

               {
                  this.it = TObjectShortMapDecorator.this._map.iterator();
               }

               public Map.Entry<K, Short> next() {
                  this.it.advance();
                  final K key = this.it.key();
                  final Short v = TObjectShortMapDecorator.this.wrapValue(this.it.value());
                  return new Map.Entry<K, Short>() {
                     private Short val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public K getKey() {
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
                        return TObjectShortMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<K, Short> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               K key = (K)((Map.Entry)o).getKey();
               TObjectShortMapDecorator.this._map.remove(key);
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<K, Short>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TObjectShortMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Short && this._map.containsValue(this.unwrapValue(val));
   }

   public boolean containsKey(Object key) {
      return this._map.containsKey(key);
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this._map.size() == 0;
   }

   public void putAll(Map<? extends K, ? extends Short> map) {
      Iterator<? extends Map.Entry<? extends K, ? extends Short>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends K, ? extends Short> e = (Map.Entry)it.next();
         this.put(e.getKey(), (Short)e.getValue());
      }

   }

   protected Short wrapValue(short k) {
      return k;
   }

   protected short unwrapValue(Object value) {
      return (Short)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TObjectShortMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
