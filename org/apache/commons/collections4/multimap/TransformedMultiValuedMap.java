package org.apache.commons.collections4.multimap;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.FluentIterable;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;

public class TransformedMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V> {
   private static final long serialVersionUID = 20150612L;
   private final Transformer<? super K, ? extends K> keyTransformer;
   private final Transformer<? super V, ? extends V> valueTransformer;

   public static <K, V> TransformedMultiValuedMap<K, V> transformedMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      TransformedMultiValuedMap<K, V> decorated = new TransformedMultiValuedMap<K, V>(map, keyTransformer, valueTransformer);
      if (!map.isEmpty()) {
         MultiValuedMap<K, V> mapCopy = new ArrayListValuedHashMap<K, V>(map);
         decorated.clear();
         decorated.putAll(mapCopy);
      }

      return decorated;
   }

   public static <K, V> TransformedMultiValuedMap<K, V> transformingMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      return new TransformedMultiValuedMap<K, V>(map, keyTransformer, valueTransformer);
   }

   protected TransformedMultiValuedMap(MultiValuedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      super(map);
      this.keyTransformer = keyTransformer;
      this.valueTransformer = valueTransformer;
   }

   public boolean put(K key, V value) {
      return this.decorated().put(this.transformKey(key), this.transformValue(value));
   }

   public boolean putAll(K key, Iterable<? extends V> values) {
      Objects.requireNonNull(values, "values");
      Iterable<V> transformedValues = FluentIterable.of(values).<V>transform(this.valueTransformer);
      Iterator<? extends V> it = transformedValues.iterator();
      return it.hasNext() && CollectionUtils.addAll(this.decorated().get(this.transformKey(key)), it);
   }

   public boolean putAll(Map<? extends K, ? extends V> map) {
      Objects.requireNonNull(map, "map");
      boolean changed = false;

      for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
         changed |= this.put(entry.getKey(), entry.getValue());
      }

      return changed;
   }

   public boolean putAll(MultiValuedMap<? extends K, ? extends V> map) {
      Objects.requireNonNull(map, "map");
      boolean changed = false;

      for(Map.Entry<? extends K, ? extends V> entry : map.entries()) {
         changed |= this.put(entry.getKey(), entry.getValue());
      }

      return changed;
   }

   protected K transformKey(K object) {
      return (K)(this.keyTransformer == null ? object : this.keyTransformer.apply(object));
   }

   protected V transformValue(V object) {
      return (V)(this.valueTransformer == null ? object : this.valueTransformer.apply(object));
   }
}
