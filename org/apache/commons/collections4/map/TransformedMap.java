package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.Transformer;

public class TransformedMap<K, V> extends AbstractInputCheckedMapDecorator<K, V> implements Serializable {
   private static final long serialVersionUID = 7023152376788900464L;
   protected final Transformer<? super K, ? extends K> keyTransformer;
   protected final Transformer<? super V, ? extends V> valueTransformer;

   public static <K, V> TransformedMap<K, V> transformedMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      TransformedMap<K, V> decorated = new TransformedMap<K, V>(map, keyTransformer, valueTransformer);
      if (!map.isEmpty()) {
         Map<K, V> transformed = decorated.transformMap(map);
         decorated.clear();
         decorated.decorated().putAll(transformed);
      }

      return decorated;
   }

   public static <K, V> TransformedMap<K, V> transformingMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      return new TransformedMap<K, V>(map, keyTransformer, valueTransformer);
   }

   protected TransformedMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      super(map);
      this.keyTransformer = keyTransformer;
      this.valueTransformer = valueTransformer;
   }

   protected V checkSetValue(V value) {
      return this.valueTransformer.apply(value);
   }

   protected boolean isSetValueChecking() {
      return this.valueTransformer != null;
   }

   public V put(K key, V value) {
      key = (K)this.transformKey(key);
      value = (V)this.transformValue(value);
      return (V)this.decorated().put(key, value);
   }

   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      mapToCopy = this.transformMap(mapToCopy);
      this.decorated().putAll(mapToCopy);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   protected K transformKey(K object) {
      return (K)(this.keyTransformer == null ? object : this.keyTransformer.apply(object));
   }

   protected Map<K, V> transformMap(Map<? extends K, ? extends V> map) {
      if (map.isEmpty()) {
         return map;
      } else {
         Map<K, V> result = new LinkedMap<K, V>(map.size());

         for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            result.put(this.transformKey(entry.getKey()), this.transformValue(entry.getValue()));
         }

         return result;
      }
   }

   protected V transformValue(V object) {
      return (V)(this.valueTransformer == null ? object : this.valueTransformer.apply(object));
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }
}
