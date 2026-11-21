package gnu.trove.decorator;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
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

public class TObjectIntMapDecorator<K> extends AbstractMap<K, Integer> implements Map<K, Integer>, Externalizable, Cloneable {
   static final long serialVersionUID = 1L;
   protected TObjectIntMap<K> _map;

   public TObjectIntMapDecorator() {
   }

   public TObjectIntMapDecorator(TObjectIntMap<K> map) {
      Objects.requireNonNull(map);
      this._map = map;
   }

   public TObjectIntMap<K> getMap() {
      return this._map;
   }

   public Integer put(K key, Integer value) {
      return value == null ? this.wrapValue(this._map.put(key, this._map.getNoEntryValue())) : this.wrapValue(this._map.put(key, this.unwrapValue(value)));
   }

   public Integer get(Object key) {
      int v = this._map.get(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public void clear() {
      this._map.clear();
   }

   public Integer remove(Object key) {
      int v = this._map.remove(key);
      return v == this._map.getNoEntryValue() ? null : this.wrapValue(v);
   }

   public Set<Map.Entry<K, Integer>> entrySet() {
      return new AbstractSet<Map.Entry<K, Integer>>() {
         public int size() {
            return TObjectIntMapDecorator.this._map.size();
         }

         public boolean isEmpty() {
            return TObjectIntMapDecorator.this.isEmpty();
         }

         public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
               return false;
            } else {
               Object k = ((Map.Entry)o).getKey();
               Object v = ((Map.Entry)o).getValue();
               return TObjectIntMapDecorator.this.containsKey(k) && TObjectIntMapDecorator.this.get(k).equals(v);
            }
         }

         public Iterator<Map.Entry<K, Integer>> iterator() {
            return new Iterator<Map.Entry<K, Integer>>() {
               private final TObjectIntIterator<K> it;

               {
                  this.it = TObjectIntMapDecorator.this._map.iterator();
               }

               public Map.Entry<K, Integer> next() {
                  this.it.advance();
                  final K key = this.it.key();
                  final Integer v = TObjectIntMapDecorator.this.wrapValue(this.it.value());
                  return new Map.Entry<K, Integer>() {
                     private Integer val = v;

                     public boolean equals(Object o) {
                        return o instanceof Map.Entry && ((Map.Entry)o).getKey().equals(key) && ((Map.Entry)o).getValue().equals(this.val);
                     }

                     public K getKey() {
                        return key;
                     }

                     public Integer getValue() {
                        return this.val;
                     }

                     public int hashCode() {
                        return key.hashCode() + this.val.hashCode();
                     }

                     public Integer setValue(Integer value) {
                        this.val = value;
                        return TObjectIntMapDecorator.this.put(key, value);
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

         public boolean add(Map.Entry<K, Integer> o) {
            throw new UnsupportedOperationException();
         }

         public boolean remove(Object o) {
            boolean modified = false;
            if (this.contains(o)) {
               K key = (K)((Map.Entry)o).getKey();
               TObjectIntMapDecorator.this._map.remove(key);
               modified = true;
            }

            return modified;
         }

         public boolean addAll(Collection<? extends Map.Entry<K, Integer>> c) {
            throw new UnsupportedOperationException();
         }

         public void clear() {
            TObjectIntMapDecorator.this.clear();
         }
      };
   }

   public boolean containsValue(Object val) {
      return val instanceof Integer && this._map.containsValue(this.unwrapValue(val));
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

   public void putAll(Map<? extends K, ? extends Integer> map) {
      Iterator<? extends Map.Entry<? extends K, ? extends Integer>> it = map.entrySet().iterator();
      int i = map.size();

      while(i-- > 0) {
         Map.Entry<? extends K, ? extends Integer> e = (Map.Entry)it.next();
         this.put(e.getKey(), (Integer)e.getValue());
      }

   }

   protected Integer wrapValue(int k) {
      return k;
   }

   protected int unwrapValue(Object value) {
      return (Integer)value;
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._map = (TObjectIntMap)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._map);
   }
}
