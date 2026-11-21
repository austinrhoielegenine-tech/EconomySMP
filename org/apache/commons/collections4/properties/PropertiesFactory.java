package org.apache.commons.collections4.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PropertiesFactory extends AbstractPropertiesFactory<Properties> {
   public static final Properties EMPTY_PROPERTIES = new EmptyProperties();
   public static final PropertiesFactory INSTANCE = new PropertiesFactory();

   private PropertiesFactory() {
   }

   protected Properties createProperties() {
      return new Properties();
   }

   private static final class EmptyProperties extends Properties {
      private static final long serialVersionUID = 1L;

      private EmptyProperties() {
      }

      public synchronized void clear() {
      }

      public synchronized Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
         Objects.requireNonNull(key);
         throw new UnsupportedOperationException();
      }

      public synchronized Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mappingFunction) {
         Objects.requireNonNull(key);
         throw new UnsupportedOperationException();
      }

      public synchronized Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
         Objects.requireNonNull(key);
         throw new UnsupportedOperationException();
      }

      public synchronized boolean contains(Object value) {
         return false;
      }

      public synchronized boolean containsKey(Object key) {
         return false;
      }

      public boolean containsValue(Object value) {
         return false;
      }

      public synchronized Enumeration<Object> elements() {
         return Collections.emptyEnumeration();
      }

      public Set<Map.Entry<Object, Object>> entrySet() {
         return Collections.emptySet();
      }

      public synchronized boolean equals(Object o) {
         return o instanceof Properties && ((Properties)o).isEmpty();
      }

      public synchronized void forEach(BiConsumer<? super Object, ? super Object> action) {
         Objects.requireNonNull(action);
      }

      public synchronized Object get(Object key) {
         return null;
      }

      public synchronized Object getOrDefault(Object key, Object defaultValue) {
         return defaultValue;
      }

      public String getProperty(String key) {
         return null;
      }

      public String getProperty(String key, String defaultValue) {
         return defaultValue;
      }

      public synchronized int hashCode() {
         return 0;
      }

      public synchronized boolean isEmpty() {
         return true;
      }

      public synchronized Enumeration<Object> keys() {
         return Collections.emptyEnumeration();
      }

      public Set<Object> keySet() {
         return Collections.emptySet();
      }

      public synchronized void load(InputStream inStream) throws IOException {
         Objects.requireNonNull(inStream);
         throw new UnsupportedOperationException();
      }

      public synchronized void load(Reader reader) throws IOException {
         Objects.requireNonNull(reader);
         throw new UnsupportedOperationException();
      }

      public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
         Objects.requireNonNull(in);
         throw new UnsupportedOperationException();
      }

      public synchronized Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(value);
         throw new UnsupportedOperationException();
      }

      public Enumeration<?> propertyNames() {
         return Collections.emptyEnumeration();
      }

      public synchronized Object put(Object key, Object value) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(value);
         throw new UnsupportedOperationException();
      }

      public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
         Objects.requireNonNull(t);
         throw new UnsupportedOperationException();
      }

      public synchronized Object putIfAbsent(Object key, Object value) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(value);
         throw new UnsupportedOperationException();
      }

      protected void rehash() {
      }

      public synchronized Object remove(Object key) {
         Objects.requireNonNull(key);
         throw new UnsupportedOperationException();
      }

      public synchronized boolean remove(Object key, Object value) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(value);
         throw new UnsupportedOperationException();
      }

      public synchronized Object replace(Object key, Object value) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(value);
         throw new UnsupportedOperationException();
      }

      public synchronized boolean replace(Object key, Object oldValue, Object newValue) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(oldValue);
         Objects.requireNonNull(newValue);
         throw new UnsupportedOperationException();
      }

      public synchronized void replaceAll(BiFunction<? super Object, ? super Object, ? extends Object> function) {
         Objects.requireNonNull(function);
         throw new UnsupportedOperationException();
      }

      public void save(OutputStream out, String comments) {
         super.save(out, comments);
      }

      public synchronized Object setProperty(String key, String value) {
         Objects.requireNonNull(key);
         Objects.requireNonNull(value);
         throw new UnsupportedOperationException();
      }

      public synchronized int size() {
         return 0;
      }

      public Set<String> stringPropertyNames() {
         return Collections.emptySet();
      }

      public synchronized String toString() {
         return super.toString();
      }

      public Collection<Object> values() {
         return Collections.emptyList();
      }
   }
}
