package org.apache.commons.collections4.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PassiveExpiringMap<K, V> extends AbstractMapDecorator<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<Object, Long> expirationMap;
   private final ExpirationPolicy<K, V> expiringPolicy;

   private static long validateAndConvertToMillis(long timeToLive, TimeUnit timeUnit) {
      Objects.requireNonNull(timeUnit, "timeUnit");
      return TimeUnit.MILLISECONDS.convert(timeToLive, timeUnit);
   }

   public PassiveExpiringMap() {
      this(-1L);
   }

   public PassiveExpiringMap(ExpirationPolicy<K, V> expiringPolicy) {
      this(expiringPolicy, new HashMap());
   }

   public PassiveExpiringMap(ExpirationPolicy<K, V> expiringPolicy, Map<K, V> map) {
      super(map);
      this.expirationMap = new HashMap();
      this.expiringPolicy = (ExpirationPolicy)Objects.requireNonNull(expiringPolicy, "expiringPolicy");
   }

   public PassiveExpiringMap(long timeToLiveMillis) {
      this(new ConstantTimeToLiveExpirationPolicy(timeToLiveMillis), new HashMap());
   }

   public PassiveExpiringMap(long timeToLiveMillis, Map<K, V> map) {
      this(new ConstantTimeToLiveExpirationPolicy(timeToLiveMillis), map);
   }

   public PassiveExpiringMap(long timeToLive, TimeUnit timeUnit) {
      this(validateAndConvertToMillis(timeToLive, timeUnit));
   }

   public PassiveExpiringMap(long timeToLive, TimeUnit timeUnit, Map<K, V> map) {
      this(validateAndConvertToMillis(timeToLive, timeUnit), map);
   }

   public PassiveExpiringMap(Map<K, V> map) {
      this(-1L, map);
   }

   public void clear() {
      super.clear();
      this.expirationMap.clear();
   }

   public boolean containsKey(Object key) {
      this.removeIfExpired(key, this.now());
      return super.containsKey(key);
   }

   public boolean containsValue(Object value) {
      this.removeAllExpired(this.now());
      return super.containsValue(value);
   }

   public Set<Map.Entry<K, V>> entrySet() {
      this.removeAllExpired(this.now());
      return super.entrySet();
   }

   public V get(Object key) {
      this.removeIfExpired(key, this.now());
      return (V)super.get(key);
   }

   public boolean isEmpty() {
      this.removeAllExpired(this.now());
      return super.isEmpty();
   }

   private boolean isExpired(long now, Long expirationTimeObject) {
      if (expirationTimeObject == null) {
         return false;
      } else {
         long expirationTime = expirationTimeObject;
         return expirationTime >= 0L && now >= expirationTime;
      }
   }

   public Set<K> keySet() {
      this.removeAllExpired(this.now());
      return super.keySet();
   }

   private long now() {
      return System.currentTimeMillis();
   }

   public V put(K key, V value) {
      this.removeIfExpired(key, this.now());
      long expirationTime = this.expiringPolicy.expirationTime(key, value);
      this.expirationMap.put(key, expirationTime);
      return (V)super.put(key, value);
   }

   public void putAll(Map<? extends K, ? extends V> mapToCopy) {
      for(Map.Entry<? extends K, ? extends V> entry : mapToCopy.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.map = (Map)in.readObject();
   }

   public V remove(Object key) {
      this.expirationMap.remove(key);
      return (V)super.remove(key);
   }

   private void removeAllExpired(long nowMillis) {
      Iterator<Map.Entry<Object, Long>> iter = this.expirationMap.entrySet().iterator();

      while(iter.hasNext()) {
         Map.Entry<Object, Long> expirationEntry = (Map.Entry)iter.next();
         if (this.isExpired(nowMillis, (Long)expirationEntry.getValue())) {
            super.remove(expirationEntry.getKey());
            iter.remove();
         }
      }

   }

   private void removeIfExpired(Object key, long nowMillis) {
      Long expirationTimeObject = (Long)this.expirationMap.get(key);
      if (this.isExpired(nowMillis, expirationTimeObject)) {
         this.remove(key);
      }

   }

   public int size() {
      this.removeAllExpired(this.now());
      return super.size();
   }

   public Collection<V> values() {
      this.removeAllExpired(this.now());
      return super.values();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(this.map);
   }

   public static class ConstantTimeToLiveExpirationPolicy<K, V> implements ExpirationPolicy<K, V> {
      private static final long serialVersionUID = 1L;
      private final long timeToLiveMillis;

      public ConstantTimeToLiveExpirationPolicy() {
         this(-1L);
      }

      public ConstantTimeToLiveExpirationPolicy(long timeToLiveMillis) {
         this.timeToLiveMillis = timeToLiveMillis;
      }

      public ConstantTimeToLiveExpirationPolicy(long timeToLive, TimeUnit timeUnit) {
         this(PassiveExpiringMap.validateAndConvertToMillis(timeToLive, timeUnit));
      }

      public long expirationTime(K key, V value) {
         if (this.timeToLiveMillis >= 0L) {
            long nowMillis = System.currentTimeMillis();
            return nowMillis > Long.MAX_VALUE - this.timeToLiveMillis ? -1L : nowMillis + this.timeToLiveMillis;
         } else {
            return -1L;
         }
      }
   }

   @FunctionalInterface
   public interface ExpirationPolicy<K, V> extends Serializable {
      long expirationTime(K var1, V var2);
   }
}
