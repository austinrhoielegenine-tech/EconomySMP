package gnu.trove.decorator;

import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
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

public class TByteObjectMapDecorator<V> extends AbstractMap<Byte, V> implements Map<Byte, V>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TByteObjectMap<V> _map;

   public TByteObjectMapDecorator() {
   }

   public TByteObjectMapDecorator(TByteObjectMap<V> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TByteObjectMap<V> getMap() {
      return this._map;
   }

   public V put(Byte key, V value) {
      byte k;
      if (key == null) {
         k = this._map.getNoEntryKey();
      } else {
         k = this.unwrapKey(key);
      }

      return this._map.put(k, value);
   }

   public V get(Object key) {
      byte k;
      if (key != null) {
         if (!(key instanceof Byte)) {
            return null;
         }

         k = this.unwrapKey((Byte)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.get(k);
   }

   public void clear() {
      this._map.clear();
   }

   public V remove(Object key) {
      byte k;
      if (key != null) {
         if (!(key instanceof Byte)) {
            return null;
         }

         k = this.unwrapKey((Byte)key);
      } else {
         k = this._map.getNoEntryKey();
      }

      return this._map.remove(k);
   }

   public Set<Map.Entry<Byte, V>> entrySet() {
      return new AbstractSet<Map.Entry<Byte, V>>() {
         public int size() {
            return TByteObjectMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TByteObjectMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TByteObjectMapDecorator.this.containsKey(k) && TByteObjectMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<Byte, V>> iterator() {
            return new Iterator<Map.Entry<Byte, V>>() {
               private final TByteObjectIterator<V> it;

               {
                  this.it = TByteObjectMapDecorator.this._map.iterator();
               }

               public Map.Entry<Byte, V> next() {
                  this.it.advance();
                  byte k = this.it.key();
                  final Byte key = k == TByteObjectMapDecorator.this._map.getNoEntryKey() ? null : TByteObjectMapDecorator.this.wrapKey(k);
                  final V v = this.it.value();
                  return new Map.Entry<Byte, V>() {
                     private V val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public Byte getKey() {
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
                        return (V)TByteObjectMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<Byte, V> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               Byte key = (Byte)((Map.Entry)o).getKey();
               TByteObjectMapDecorator.this._map.remove(TByteObjectMapDecorator.this.unwrapKey(key));
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<Byte, V>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TByteObjectMapDecorator.this.clear();
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
         return key instanceof Byte && this._map.containsKey((Byte)key);
      }
   }

   public int size() {
      return this._map.size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void putAll(Map<? extends Byte, ? extends V> map) {
      Iterator<? extends Map.Entry<? extends Byte, ? extends V>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends Byte, ? extends V> e = (Map.Entry)it.next();
         this.put((Byte)e.getKey(), e.getValue());
      }

   }

   protected Byte wrapKey(byte k) {
      return k;
   }

   protected byte unwrapKey(Byte key) {
      return key;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TByteObjectMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
