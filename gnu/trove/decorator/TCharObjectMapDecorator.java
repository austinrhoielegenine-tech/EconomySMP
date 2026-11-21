package gnu.trove.decorator;

import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.map.TCharObjectMap;
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

public class TCharObjectMapDecorator<V> extends AbstractMap<Character, V> implements Map<Character, V>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TCharObjectMap<V> _map;

   public TCharObjectMapDecorator() {
   }

   public TCharObjectMapDecorator(TCharObjectMap<V> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TCharObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Character key, V value) {
      char k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      return this._map.put(k, value);
   }

   public V get(Object key) {
      char k;
      if (key != null) {
         if (!(key instanceof Character)) {
            return null;
         }

         k = this.unwrapKey((Character)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.get(k);
   }

   public void clear() {
      this._map.clear();
   }

   public V remove(Object key) {
      char k;
      if (key != null) {
         if (!(key instanceof Character)) {
            return null;
         }

         k = this.unwrapKey((Character)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.remove(k);
   }

   public Set<Map.Entry<Character, V>> entrySet() {
      return new AbstractSet<Map.Entry<Character, V>>() {
         public int size() {
            return TCharObjectMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TCharObjectMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TCharObjectMapDecorator.this.containsKey(k) && TCharObjectMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Character, V>> iterator() {
            return new Iterator<Map.Entry<Character, V>>() {
               private final TCharObjectIterator<V> it;

               {
                  this.it = TCharObjectMapDecorator.this._map.iterator();
               }

               public Map.Entry<Character, V> next() {
                  this.it.advance();
                  char k = this.it.key();
                  final Character key = k == TCharObjectMapDecorator.this._map.getNoEntryKey() ? null : TCharObjectMapDecorator.this.wrapKey(k);
                  final V v = this.it.value();
                  return new Map.Entry<Character, V>() {
                     private V val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Character getKey() {
                        return key;
                     }

                     public V getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public V setValue(V value) {
                        this.val = value;
                        return (V)TCharObjectMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Character, V> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Character key = (Character)((Map.Entry)o).getKey();
               TCharObjectMapDecorator.this._map.remove(TCharObjectMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Character, V>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TCharObjectMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return this._map.containsValue(val);
   }

   public boolean containsKey(Object key) {
      if (key == null) {
         return this._map.containsKey(this._map.getNoEntryKey());
      } else {
         return key instanceof Character && this._map.containsKey((Character)key);
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Character, ? extends V> map) {
      Iterator<? extends Map.Entry<? extends Character, ? extends V>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Character, ? extends V> e = (Map.Entry)it.next();
         this.put((Character)e.getKey(), e.getValue());
      }

   }

   protected Character wrapKey(char k) {
      return k;
   }

   protected char unwrapKey(Character key) {
      return key;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TCharObjectMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
