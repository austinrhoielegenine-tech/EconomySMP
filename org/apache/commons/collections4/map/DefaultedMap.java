package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.FactoryTransformer;

public class DefaultedMap<K, V> extends AbstractMapDecorator<K, V> implements Serializable {
   private static final long serialVersionUID = 19698628745827L;
   private final Transformer<? super K, ? extends V> value;

   public static <K, V> DefaultedMap<K, V> defaultedMap(Map<K, V> map, Factory<? extends V> factory) {
      return new DefaultedMap<K, V>(map, FactoryTransformer.factoryTransformer((Factory)Objects.requireNonNull(factory, "Factory must not be null")));
   }

   public static <K, V> Map<K, V> defaultedMap(Map<K, V> map, Transformer<? super K, ? extends V> transformer) {
      return new DefaultedMap<K, V>(map, (Transformer)Objects.requireNonNull(transformer, "Transformer must not be null"));
   }

   public static <K, V> DefaultedMap<K, V> defaultedMap(Map<K, V> map, V defaultValue) {
      return new DefaultedMap<K, V>(map, ConstantTransformer.constantTransformer(defaultValue));
   }

   protected DefaultedMap(Map<K, V> map, Transformer<? super K, ? extends V> defaultValueTransformer) {
      super(map);
      this.value = (Transformer)Objects.requireNonNull(defaultValueTransformer, "defaultValueTransformer");
   }

   public DefaultedMap(Transformer<? super K, ? extends V> defaultValueTransformer) {
      this(new HashMap(), defaultValueTransformer);
   }

   public DefaultedMap(V defaultValue) {
      this(ConstantTransformer.constantTransformer(defaultValue));
   }

   public V get(Object key) {
      V v;
      return (V)((v = (V)this.map.get(key)) == null && !this.map.containsKey(key) ? this.value.apply(key) : v);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }
}
