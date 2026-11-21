package org.apache.commons.collections4.properties;

import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.iterators.IteratorEnumeration;

public class SortedProperties extends Properties {
   private static final long serialVersionUID = 1L;

   public Set<Map.Entry<Object, Object>> entrySet() {
      Stream<AbstractMap.SimpleEntry<Object, Object>> stream = this.sortedKeys().map((k) -> new AbstractMap.SimpleEntry(k, this.getProperty(k)));
      return (Set)stream.collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public synchronized Enumeration<Object> keys() {
      return new IteratorEnumeration<Object>(((List)this.sortedKeys().collect(Collectors.toList())).iterator());
   }

   private Stream<String> sortedKeys() {
      return this.keySet().stream().map(Object::toString).sorted();
   }
}
