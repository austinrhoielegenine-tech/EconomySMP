package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.Predicate;

public class PredicatedMap<K, V> extends AbstractInputCheckedMapDecorator<K, V> implements Serializable {
   private static final long serialVersionUID = 7412622456128415156L;
   protected final Predicate<? super K> keyPredicate;
   protected final Predicate<? super V> valuePredicate;

   public static <K, V> PredicatedMap<K, V> predicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
      return new PredicatedMap<K, V>(map, keyPredicate, valuePredicate);
   }

   protected PredicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
      super(map);
      this.keyPredicate = keyPredicate;
      this.valuePredicate = valuePredicate;
      map.forEach(this::validate);
   }

   protected V checkSetValue(V value) {
      if (!this.valuePredicate.test(value)) {
         throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
      } else {
         return value;
      }
   }

   protected boolean isSetValueChecking() {
      return this.valuePredicate != null;
   }

   public V put(K key, V value) {
      this.validate(key, value);
      return (V)this.map.put(key, value);
   }

   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      for(Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
         this.validate(entry.getKey(), entry.getValue());
      }

      super.putAll(mapToCopy);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   protected void validate(K key, V value) {
      if (this.keyPredicate != null && !this.keyPredicate.test(key)) {
         throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
      } else if (this.valuePredicate != null && !this.valuePredicate.test(value)) {
         throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
      }
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }
}
