package org.apache.commons.collections4;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.collections4.map.AbstractMapDecorator;
import org.apache.commons.collections4.map.AbstractSortedMapDecorator;
import org.apache.commons.collections4.map.FixedSizeMap;
import org.apache.commons.collections4.map.FixedSizeSortedMap;
import org.apache.commons.collections4.map.LazyMap;
import org.apache.commons.collections4.map.LazySortedMap;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.collections4.map.PredicatedMap;
import org.apache.commons.collections4.map.PredicatedSortedMap;
import org.apache.commons.collections4.map.TransformedMap;
import org.apache.commons.collections4.map.TransformedSortedMap;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;

public class MapUtils {
   public static final SortedMap EMPTY_SORTED_MAP = UnmodifiableSortedMap.unmodifiableSortedMap(new TreeMap());
   private static final String INDENT_STRING = "    ";

   private static <K, R> R applyDefaultFunction(Map<? super K, ?> map, K key, BiFunction<Map<? super K, ?>, K, R> getFunction, Function<K, R> defaultFunction) {
      return (R)applyDefaultFunction(map, key, getFunction, defaultFunction, (Object)null);
   }

   private static <K, R> R applyDefaultFunction(Map<? super K, ?> map, K key, BiFunction<Map<? super K, ?>, K, R> getFunction, Function<K, R> defaultFunction, R defaultValue) {
      R value = (R)(map != null && getFunction != null ? getFunction.apply(map, key) : null);
      if (value == null) {
         value = (R)(defaultFunction != null ? defaultFunction.apply(key) : null);
      }

      return (R)(value != null ? value : defaultValue);
   }

   private static <K, R> R applyDefaultValue(Map<? super K, ?> map, K key, BiFunction<Map<? super K, ?>, K, R> getFunction, R defaultValue) {
      R value = (R)(map != null && getFunction != null ? getFunction.apply(map, key) : null);
      return (R)(value == null ? defaultValue : value);
   }

   public static void debugPrint(PrintStream out, Object label, Map<?, ?> map) {
      verbosePrintInternal(out, label, map, new ArrayDeque(), true);
   }

   public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
      return map == null ? Collections.emptyMap() : map;
   }

   public static <K, V> IterableMap<K, V> fixedSizeMap(Map<K, V> map) {
      return FixedSizeMap.<K, V>fixedSizeMap(map);
   }

   public static <K, V> SortedMap<K, V> fixedSizeSortedMap(SortedMap<K, V> map) {
      return FixedSizeSortedMap.<K, V>fixedSizeSortedMap(map);
   }

   public static <K> Boolean getBoolean(Map<? super K, ?> map, K key) {
      if (map != null) {
         Object answer = map.get(key);
         if (answer != null) {
            if (answer instanceof Boolean) {
               return (Boolean)answer;
            }

            if (answer instanceof String) {
               return Boolean.valueOf((String)answer);
            }

            if (answer instanceof Number) {
               Number n = (Number)answer;
               return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }
         }
      }

      return null;
   }

   public static <K> Boolean getBoolean(Map<? super K, ?> map, K key, Boolean defaultValue) {
      return (Boolean)applyDefaultValue(map, key, MapUtils::getBoolean, defaultValue);
   }

   public static <K> Boolean getBoolean(Map<? super K, ?> map, K key, Function<K, Boolean> defaultFunction) {
      return (Boolean)applyDefaultFunction(map, key, MapUtils::getBoolean, defaultFunction);
   }

   public static <K> boolean getBooleanValue(Map<? super K, ?> map, K key) {
      return Boolean.TRUE.equals(getBoolean(map, key));
   }

   public static <K> boolean getBooleanValue(Map<? super K, ?> map, K key, boolean defaultValue) {
      return (Boolean)applyDefaultValue(map, key, MapUtils::getBoolean, defaultValue);
   }

   public static <K> boolean getBooleanValue(Map<? super K, ?> map, K key, Function<K, Boolean> defaultFunction) {
      return (Boolean)applyDefaultFunction(map, key, MapUtils::getBoolean, defaultFunction, false);
   }

   public static <K> Byte getByte(Map<? super K, ?> map, K key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Byte ? (Byte)answer : answer.byteValue();
      }
   }

   public static <K> Byte getByte(Map<? super K, ?> map, K key, Byte defaultValue) {
      return (Byte)applyDefaultValue(map, key, MapUtils::getByte, defaultValue);
   }

   public static <K> Byte getByte(Map<? super K, ?> map, K key, Function<K, Byte> defaultFunction) {
      return (Byte)applyDefaultFunction(map, key, MapUtils::getByte, defaultFunction);
   }

   public static <K> byte getByteValue(Map<? super K, ?> map, K key) {
      return ((Number)applyDefaultValue(map, key, MapUtils::getByte, 0)).byteValue();
   }

   public static <K> byte getByteValue(Map<? super K, ?> map, K key, byte defaultValue) {
      return (Byte)applyDefaultValue(map, key, MapUtils::getByte, defaultValue);
   }

   public static <K> byte getByteValue(Map<? super K, ?> map, K key, Function<K, Byte> defaultFunction) {
      return (Byte)applyDefaultFunction(map, key, MapUtils::getByte, defaultFunction, (byte)0);
   }

   public static <K> Double getDouble(Map<? super K, ?> map, K key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Double ? (Double)answer : answer.doubleValue();
      }
   }

   public static <K> Double getDouble(Map<? super K, ?> map, K key, Double defaultValue) {
      return (Double)applyDefaultValue(map, key, MapUtils::getDouble, defaultValue);
   }

   public static <K> Double getDouble(Map<? super K, ?> map, K key, Function<K, Double> defaultFunction) {
      return (Double)applyDefaultFunction(map, key, MapUtils::getDouble, defaultFunction);
   }

   public static <K> double getDoubleValue(Map<? super K, ?> map, K key) {
      return (Double)applyDefaultValue(map, key, MapUtils::getDouble, (double)0.0F);
   }

   public static <K> double getDoubleValue(Map<? super K, ?> map, K key, double defaultValue) {
      return (Double)applyDefaultValue(map, key, MapUtils::getDouble, defaultValue);
   }

   public static <K> double getDoubleValue(Map<? super K, ?> map, K key, Function<K, Double> defaultFunction) {
      return (Double)applyDefaultFunction(map, key, MapUtils::getDouble, defaultFunction, (double)0.0F);
   }

   public static <K> Float getFloat(Map<? super K, ?> map, K key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Float ? (Float)answer : answer.floatValue();
      }
   }

   public static <K> Float getFloat(Map<? super K, ?> map, K key, Float defaultValue) {
      return (Float)applyDefaultValue(map, key, MapUtils::getFloat, defaultValue);
   }

   public static <K> Float getFloat(Map<? super K, ?> map, K key, Function<K, Float> defaultFunction) {
      return (Float)applyDefaultFunction(map, key, MapUtils::getFloat, defaultFunction);
   }

   public static <K> float getFloatValue(Map<? super K, ?> map, K key) {
      return (Float)applyDefaultValue(map, key, MapUtils::getFloat, 0.0F);
   }

   public static <K> float getFloatValue(Map<? super K, ?> map, K key, float defaultValue) {
      return (Float)applyDefaultValue(map, key, MapUtils::getFloat, defaultValue);
   }

   public static <K> float getFloatValue(Map<? super K, ?> map, K key, Function<K, Float> defaultFunction) {
      return (Float)applyDefaultFunction(map, key, MapUtils::getFloat, defaultFunction, 0.0F);
   }

   public static <K> Integer getInteger(Map<? super K, ?> map, K key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Integer ? (Integer)answer : answer.intValue();
      }
   }

   public static <K> Integer getInteger(Map<? super K, ?> map, K key, Function<K, Integer> defaultFunction) {
      return (Integer)applyDefaultFunction(map, key, MapUtils::getInteger, defaultFunction);
   }

   public static <K> Integer getInteger(Map<? super K, ?> map, K key, Integer defaultValue) {
      return (Integer)applyDefaultValue(map, key, MapUtils::getInteger, defaultValue);
   }

   public static <K> int getIntValue(Map<? super K, ?> map, K key) {
      return (Integer)applyDefaultValue(map, key, MapUtils::getInteger, 0);
   }

   public static <K> int getIntValue(Map<? super K, ?> map, K key, Function<K, Integer> defaultFunction) {
      return ((Integer)applyDefaultFunction(map, key, MapUtils::getInteger, defaultFunction, 0)).byteValue();
   }

   public static <K> int getIntValue(Map<? super K, ?> map, K key, int defaultValue) {
      return (Integer)applyDefaultValue(map, key, MapUtils::getInteger, defaultValue);
   }

   public static <K> Long getLong(Map<? super K, ?> map, K key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Long ? (Long)answer : answer.longValue();
      }
   }

   public static <K> Long getLong(Map<? super K, ?> map, K key, Function<K, Long> defaultFunction) {
      return (Long)applyDefaultFunction(map, key, MapUtils::getLong, defaultFunction);
   }

   public static <K> Long getLong(Map<? super K, ?> map, K key, Long defaultValue) {
      return (Long)applyDefaultValue(map, key, MapUtils::getLong, defaultValue);
   }

   public static <K> long getLongValue(Map<? super K, ?> map, K key) {
      return (Long)applyDefaultValue(map, key, MapUtils::getLong, 0L);
   }

   public static <K> long getLongValue(Map<? super K, ?> map, K key, Function<K, Long> defaultFunction) {
      return (long)((Long)applyDefaultFunction(map, key, MapUtils::getLong, defaultFunction, 0L)).byteValue();
   }

   public static <K> long getLongValue(Map<? super K, ?> map, K key, long defaultValue) {
      return (Long)applyDefaultValue(map, key, MapUtils::getLong, defaultValue);
   }

   public static <K> Map<?, ?> getMap(Map<? super K, ?> map, K key) {
      if (map != null) {
         Object answer = map.get(key);
         if (answer instanceof Map) {
            return (Map)answer;
         }
      }

      return null;
   }

   public static <K> Map<?, ?> getMap(Map<? super K, ?> map, K key, Function<K, Map<?, ?>> defaultFunction) {
      return (Map)applyDefaultFunction(map, key, MapUtils::getMap, defaultFunction);
   }

   public static <K> Map<?, ?> getMap(Map<? super K, ?> map, K key, Map<?, ?> defaultValue) {
      return (Map)applyDefaultValue(map, key, MapUtils::getMap, defaultValue);
   }

   public static <K> Number getNumber(Map<? super K, ?> map, K key) {
      if (map != null) {
         Object answer = map.get(key);
         if (answer != null) {
            if (answer instanceof Number) {
               return (Number)answer;
            }

            if (answer instanceof String) {
               try {
                  String text = (String)answer;
                  return NumberFormat.getInstance().parse(text);
               } catch (ParseException var4) {
               }
            }
         }
      }

      return null;
   }

   public static <K> Number getNumber(Map<? super K, ?> map, K key, Function<K, Number> defaultFunction) {
      return (Number)applyDefaultFunction(map, key, MapUtils::getNumber, defaultFunction);
   }

   public static <K> Number getNumber(Map<? super K, ?> map, K key, Number defaultValue) {
      return (Number)applyDefaultValue(map, key, MapUtils::getNumber, defaultValue);
   }

   public static <K, V> V getObject(Map<? super K, V> map, K key) {
      return (V)(map != null ? map.get(key) : null);
   }

   public static <K, V> V getObject(Map<K, V> map, K key, V defaultValue) {
      if (map != null) {
         V answer = (V)map.get(key);
         if (answer != null) {
            return answer;
         }
      }

      return defaultValue;
   }

   public static <K> Short getShort(Map<? super K, ?> map, K key) {
      Number answer = getNumber(map, key);
      if (answer == null) {
         return null;
      } else {
         return answer instanceof Short ? (Short)answer : answer.shortValue();
      }
   }

   public static <K> Short getShort(Map<? super K, ?> map, K key, Function<K, Short> defaultFunction) {
      return (Short)applyDefaultFunction(map, key, MapUtils::getShort, defaultFunction);
   }

   public static <K> Short getShort(Map<? super K, ?> map, K key, Short defaultValue) {
      return (Short)applyDefaultValue(map, key, MapUtils::getShort, defaultValue);
   }

   public static <K> short getShortValue(Map<? super K, ?> map, K key) {
      return ((Number)applyDefaultValue(map, key, MapUtils::getShort, 0)).shortValue();
   }

   public static <K> short getShortValue(Map<? super K, ?> map, K key, Function<K, Short> defaultFunction) {
      return (Short)applyDefaultFunction(map, key, MapUtils::getShort, defaultFunction, (short)0);
   }

   public static <K> short getShortValue(Map<? super K, ?> map, K key, short defaultValue) {
      return (Short)applyDefaultValue(map, key, MapUtils::getShort, defaultValue);
   }

   public static <K> String getString(Map<? super K, ?> map, K key) {
      if (map != null) {
         Object answer = map.get(key);
         if (answer != null) {
            return answer.toString();
         }
      }

      return null;
   }

   public static <K> String getString(Map<? super K, ?> map, K key, Function<K, String> defaultFunction) {
      return (String)applyDefaultFunction(map, key, MapUtils::getString, defaultFunction);
   }

   public static <K> String getString(Map<? super K, ?> map, K key, String defaultValue) {
      return (String)applyDefaultValue(map, key, MapUtils::getString, defaultValue);
   }

   public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
      Objects.requireNonNull(map, "map");
      Map<V, K> out = new HashMap(map.size());

      for(Map.Entry<K, V> entry : map.entrySet()) {
         out.put(entry.getValue(), entry.getKey());
      }

      return out;
   }

   public static boolean isEmpty(Map<?, ?> map) {
      return map == null || map.isEmpty();
   }

   public static boolean isNotEmpty(Map<?, ?> map) {
      return !isEmpty(map);
   }

   public static <K, V> IterableMap<K, V> iterableMap(Map<K, V> map) {
      Objects.requireNonNull(map, "map");
      return (IterableMap<K, V>)(map instanceof IterableMap ? (IterableMap)map : new AbstractMapDecorator<K, V>(map) {
      });
   }

   public static <K, V> IterableSortedMap<K, V> iterableSortedMap(SortedMap<K, V> sortedMap) {
      Objects.requireNonNull(sortedMap, "sortedMap");
      return (IterableSortedMap<K, V>)(sortedMap instanceof IterableSortedMap ? (IterableSortedMap)sortedMap : new AbstractSortedMapDecorator<K, V>(sortedMap) {
      });
   }

   public static <K, V> IterableMap<K, V> lazyMap(Map<K, V> map, Factory<? extends V> factory) {
      return LazyMap.lazyMap(map, factory);
   }

   public static <K, V> IterableMap<K, V> lazyMap(Map<K, V> map, Transformer<? super K, ? extends V> transformerFactory) {
      return LazyMap.lazyMap(map, transformerFactory);
   }

   public static <K, V> SortedMap<K, V> lazySortedMap(SortedMap<K, V> map, Factory<? extends V> factory) {
      return LazySortedMap.lazySortedMap(map, factory);
   }

   public static <K, V> SortedMap<K, V> lazySortedMap(SortedMap<K, V> map, Transformer<? super K, ? extends V> transformerFactory) {
      return LazySortedMap.lazySortedMap(map, transformerFactory);
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> MultiValueMap<K, V> multiValueMap(Map<K, ? super Collection<V>> map) {
      return MultiValueMap.<K, V>multiValueMap(map);
   }

   /** @deprecated */
   @Deprecated
   public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(Map<K, C> map, Class<C> collectionClass) {
      return MultiValueMap.multiValueMap(map, collectionClass);
   }

   /** @deprecated */
   @Deprecated
   public static <K, V, C extends Collection<V>> MultiValueMap<K, V> multiValueMap(Map<K, C> map, Factory<C> collectionFactory) {
      return MultiValueMap.multiValueMap(map, collectionFactory);
   }

   public static <K, V> OrderedMap<K, V> orderedMap(Map<K, V> map) {
      return ListOrderedMap.<K, V>listOrderedMap(map);
   }

   public static <K, V, E> void populateMap(Map<K, V> map, Iterable<? extends E> elements, Transformer<E, K> keyTransformer, Transformer<E, V> valueTransformer) {
      for(E temp : elements) {
         map.put(keyTransformer.apply(temp), valueTransformer.apply(temp));
      }

   }

   public static <K, V> void populateMap(Map<K, V> map, Iterable<? extends V> elements, Transformer<V, K> keyTransformer) {
      populateMap(map, elements, keyTransformer, TransformerUtils.nopTransformer());
   }

   public static <K, V, E> void populateMap(MultiMap<K, V> map, Iterable<? extends E> elements, Transformer<E, K> keyTransformer, Transformer<E, V> valueTransformer) {
      for(E temp : elements) {
         map.put(keyTransformer.apply(temp), valueTransformer.apply(temp));
      }

   }

   public static <K, V> void populateMap(MultiMap<K, V> map, Iterable<? extends V> elements, Transformer<V, K> keyTransformer) {
      populateMap(map, elements, keyTransformer, TransformerUtils.nopTransformer());
   }

   public static <K, V> IterableMap<K, V> predicatedMap(Map<K, V> map, Predicate<? super K> keyPred, Predicate<? super V> valuePred) {
      return PredicatedMap.<K, V>predicatedMap(map, keyPred, valuePred);
   }

   public static <K, V> SortedMap<K, V> predicatedSortedMap(SortedMap<K, V> map, Predicate<? super K> keyPred, Predicate<? super V> valuePred) {
      return PredicatedSortedMap.<K, V>predicatedSortedMap(map, keyPred, valuePred);
   }

   private static void printIndent(PrintStream out, int indent) {
      for(int i = 0; i < indent; ++i) {
         out.print("    ");
      }

   }

   public static <K, V> Map<K, V> putAll(Map<K, V> map, Object[] array) {
      Objects.requireNonNull(map, "map");
      if (array != null && array.length != 0) {
         Object obj = array[0];
         if (obj instanceof Map.Entry) {
            for(Object element : array) {
               Map.Entry<K, V> entry = (Map.Entry)element;
               map.put(entry.getKey(), entry.getValue());
            }
         } else if (obj instanceof KeyValue) {
            for(Object element : array) {
               KeyValue<K, V> keyval = (KeyValue)element;
               map.put(keyval.getKey(), keyval.getValue());
            }
         } else if (obj instanceof Object[]) {
            for(int i = 0; i < array.length; ++i) {
               Object[] sub = array[i];
               if (sub == null || sub.length < 2) {
                  throw new IllegalArgumentException("Invalid array element: " + i);
               }

               map.put(sub[0], sub[1]);
            }
         } else {
            int i = 0;

            while(i < array.length - 1) {
               map.put(array[i++], array[i++]);
            }
         }

         return map;
      } else {
         return map;
      }
   }

   public static <K> void safeAddToMap(Map<? super K, Object> map, K key, Object value) throws NullPointerException {
      Objects.requireNonNull(map, "map");
      map.put(key, value == null ? "" : value);
   }

   public static int size(Map<?, ?> map) {
      return map == null ? 0 : map.size();
   }

   public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
      return Collections.synchronizedMap(map);
   }

   public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> map) {
      return Collections.synchronizedSortedMap(map);
   }

   public static Map<String, Object> toMap(ResourceBundle resourceBundle) {
      Objects.requireNonNull(resourceBundle, "resourceBundle");
      Enumeration<String> enumeration = resourceBundle.getKeys();
      Map<String, Object> map = new HashMap();

      while(enumeration.hasMoreElements()) {
         String key = (String)enumeration.nextElement();
         Object value = resourceBundle.getObject(key);
         map.put(key, value);
      }

      return map;
   }

   public static <K, V> Properties toProperties(Map<K, V> map) {
      Properties answer = new Properties();
      if (map != null) {
         for(Map.Entry<K, V> entry2 : map.entrySet()) {
            Object key = entry2.getKey();
            Object value = entry2.getValue();
            answer.put(key, value);
         }
      }

      return answer;
   }

   public static <K, V> IterableMap<K, V> transformedMap(Map<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      return TransformedMap.<K, V>transformingMap(map, keyTransformer, valueTransformer);
   }

   public static <K, V> SortedMap<K, V> transformedSortedMap(SortedMap<K, V> map, Transformer<? super K, ? extends K> keyTransformer, Transformer<? super V, ? extends V> valueTransformer) {
      return TransformedSortedMap.<K, V>transformingSortedMap(map, keyTransformer, valueTransformer);
   }

   public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
      return UnmodifiableMap.<K, V>unmodifiableMap(map);
   }

   public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> map) {
      return UnmodifiableSortedMap.<K, V>unmodifiableSortedMap(map);
   }

   public static void verbosePrint(PrintStream out, Object label, Map<?, ?> map) {
      verbosePrintInternal(out, label, map, new ArrayDeque(), false);
   }

   private static void verbosePrintInternal(PrintStream out, Object label, Map<?, ?> map, Deque<Map<?, ?>> lineage, boolean debug) {
      printIndent(out, lineage.size());
      if (map == null) {
         if (label != null) {
            out.print(label);
            out.print(" = ");
         }

         out.println("null");
      } else {
         if (label != null) {
            out.print(label);
            out.println(" = ");
         }

         printIndent(out, lineage.size());
         out.println("{");
         lineage.addLast(map);

         for(Map.Entry<?, ?> entry : map.entrySet()) {
            Object childKey = entry.getKey();
            Object childValue = entry.getValue();
            if (childValue instanceof Map && !lineage.contains(childValue)) {
               verbosePrintInternal(out, childKey == null ? "null" : childKey, (Map)childValue, lineage, debug);
            } else {
               printIndent(out, lineage.size());
               out.print(childKey);
               out.print(" = ");
               int lineageIndex = IterableUtils.indexOf(lineage, PredicateUtils.equalPredicate(childValue));
               if (lineageIndex == -1) {
                  out.print(childValue);
               } else if (lineage.size() - 1 == lineageIndex) {
                  out.print("(this Map)");
               } else {
                  out.print("(ancestor[" + (lineage.size() - 1 - lineageIndex - 1) + "] Map)");
               }

               if (debug && childValue != null) {
                  out.print(' ');
                  out.println(childValue.getClass().getName());
               } else {
                  out.println();
               }
            }
         }

         lineage.removeLast();
         printIndent(out, lineage.size());
         out.println(debug ? "} " + map.getClass().getName() : "}");
      }
   }

   private MapUtils() {
   }
}
