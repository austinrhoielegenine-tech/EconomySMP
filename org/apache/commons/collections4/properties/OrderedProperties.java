package org.apache.commons.collections4.properties;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderedProperties extends Properties {
   private static final long serialVersionUID = 1L;
   private final LinkedHashSet<Object> orderedKeys = new LinkedHashSet();

   public synchronized void clear() {
      this.orderedKeys.clear();
      super.clear();
   }

   public synchronized Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
      Object compute = super.compute(key, remappingFunction);
      if (compute != null) {
         this.orderedKeys.add(key);
      }

      return compute;
   }

   public synchronized Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mappingFunction) {
      Object computeIfAbsent = super.computeIfAbsent(key, mappingFunction);
      if (computeIfAbsent != null) {
         this.orderedKeys.add(key);
      }

      return computeIfAbsent;
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      return (Set)this.orderedKeys.stream().map((k) -> new AbstractMap.SimpleEntry(k, this.get(k))).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
      Objects.requireNonNull(action);
      this.orderedKeys.forEach((k) -> action.accept(k, this.get(k)));
   }

   public synchronized Enumeration<Object> keys() {
      return Collections.enumeration(this.orderedKeys);
   }

   public Set<Object> keySet() {
      return this.orderedKeys;
   }

   public synchronized Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
      this.orderedKeys.add(key);
      return super.merge(key, value, remappingFunction);
   }

   public Enumeration<?> propertyNames() {
      return Collections.enumeration(this.orderedKeys);
   }

   public synchronized Object put(Object key, Object value) {
      Object put = super.put(key, value);
      if (put == null) {
         this.orderedKeys.add(key);
      }

      return put;
   }

   public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
      this.orderedKeys.addAll(t.keySet());
      super.putAll(t);
   }

   public synchronized Object putIfAbsent(Object key, Object value) {
      Object putIfAbsent = super.putIfAbsent(key, value);
      if (putIfAbsent == null) {
         this.orderedKeys.add(key);
      }

      return putIfAbsent;
   }

   public synchronized Object remove(Object key) {
      Object remove = super.remove(key);
      if (remove != null) {
         this.orderedKeys.remove(key);
      }

      return remove;
   }

   public synchronized boolean remove(Object key, Object value) {
      boolean remove = super.remove(key, value);
      if (remove) {
         this.orderedKeys.remove(key);
      }

      return remove;
   }

   public synchronized String toString() {
      int max = this.size() - 1;
      if (max == -1) {
         return "{}";
      } else {
         StringBuilder sb = new StringBuilder();
         Iterator<Map.Entry<Object, Object>> it = this.entrySet().iterator();
         sb.append('{');
         int i = 0;

         while(true) {
            Map.Entry<Object, Object> e = (Map.Entry)it.next();
            Object key = e.getKey();
            Object value = e.getValue();
            sb.append(key == this ? "(this Map)" : key.toString());
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value.toString());
            if (i == max) {
               return sb.append('}').toString();
            }

            sb.append(", ");
            ++i;
         }
      }
   }
}
