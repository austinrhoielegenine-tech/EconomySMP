package gnu.trove.decorator;

import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
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

public class TObjectByteMapDecorator<K> extends AbstractMap<K, Byte> implements Map<K, Byte>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TObjectByteMap<K> _map;

   public TObjectByteMapDecorator() {
   }

   public TObjectByteMapDecorator(TObjectByteMap<K> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TObjectByteMap<K> getMap() {
      return this._map;
   }

   public Byte put(K key, Byte value) {
      return value == null ? this.wrapValue(this._map.put(key, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(key, this.unwrapValue(value)));
   }

   public Byte get(Object key) {
      byte v = this._map.get(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Byte remove(Object key) {
      byte v = this._map.remove(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<K, Byte>> entrySet() {
      return new AbstractSet<Map.Entry<K, Byte>>() {
         public int size() {
            return TObjectByteMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TObjectByteMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TObjectByteMapDecorator.this.containsKey(k) && TObjectByteMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<K, Byte>> iterator() {
            return new Iterator<Map.Entry<K, Byte>>() {
               private final TObjectByteIterator<K> it;

               {
                  this.it = TObjectByteMapDecorator.this._map.iterator();
               }

               public Map.Entry<K, Byte> next() {
                  this.it.advance();
                  final K key = this.it.key();
                  final Byte v = TObjectByteMapDecorator.this.wrapValue(this.it.value());
                  return new Map.Entry<K, Byte>() {
                     private Byte val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public K getKey() {
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
                        return TObjectByteMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<K, Byte> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               K key = (K)((Map.Entry)o).getKey();
               TObjectByteMapDecorator.this._map.remove(key);
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<K, Byte>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TObjectByteMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Byte && this._map.containsValue(this.unwrapValue(val));
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

   public void putAll(Map<? extends K, ? extends Byte> map) {
      Iterator<? extends Map.Entry<? extends K, ? extends Byte>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends K, ? extends Byte> e = (Map.Entry)it.next();
         this.put(e.getKey(), (Byte)e.getValue());
      }

   }

   protected Byte wrapValue(byte k) {
      return k;
   }

   protected byte unwrapValue(Object value) {
      return (Byte)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TObjectByteMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
