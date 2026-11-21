package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JavaUtilCollectionsDeserializers {
   private static final int TYPE_SINGLETON_SET = 1;
   private static final int TYPE_SINGLETON_LIST = 2;
   private static final int TYPE_SINGLETON_MAP = 3;
   private static final int TYPE_UNMODIFIABLE_SET = 4;
   private static final int TYPE_UNMODIFIABLE_LIST = 5;
   private static final int TYPE_UNMODIFIABLE_MAP = 6;
   private static final int TYPE_SYNC_SET = 7;
   private static final int TYPE_SYNC_COLLECTION = 8;
   private static final int TYPE_SYNC_LIST = 9;
   private static final int TYPE_SYNC_MAP = 10;
   public static final int TYPE_AS_LIST = 11;
   private static final String PREFIX_JAVA_UTIL_COLLECTIONS = "java.util.Collections$";
   private static final String PREFIX_JAVA_UTIL_ARRAYS = "java.util.Arrays$";
   private static final String PREFIX_JAVA_UTIL_IMMUTABLE_COLL = "java.util.ImmutableCollections$";

   public static JsonDeserializer<?> findForCollection(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      String clsName = type.getRawClass().getName();
      if (!clsName.startsWith("java.util.")) {
         return null;
      } else {
         String localName = _findUtilCollectionsTypeName(clsName);
         if (localName != null) {
            JavaUtilCollectionsConverter conv = null;
            String name;
            if ((name = _findUnmodifiableTypeName(localName)) != null) {
               if (name.endsWith("Set")) {
                  conv = converter(4, type, Set.class);
               } else if (name.endsWith("List")) {
                  conv = converter(5, type, List.class);
               }
            } else if ((name = _findSingletonTypeName(localName)) != null) {
               if (name.endsWith("Set")) {
                  conv = converter(1, type, Set.class);
               } else if (name.endsWith("List")) {
                  conv = converter(2, type, List.class);
               }
            } else if ((name = _findSyncTypeName(localName)) != null) {
               if (name.endsWith("Set")) {
                  conv = converter(7, type, Set.class);
               } else if (name.endsWith("List")) {
                  conv = converter(9, type, List.class);
               } else if (name.endsWith("Collection")) {
                  conv = converter(8, type, Collection.class);
               }
            }

            return conv == null ? null : new StdDelegatingDeserializer(conv);
         } else if ((localName = _findUtilArrayTypeName(clsName)) != null) {
            return localName.contains("List") ? new StdDelegatingDeserializer(converter(11, type, List.class)) : null;
         } else if ((localName = _findUtilCollectionsImmutableTypeName(clsName)) != null) {
            if (localName.contains("List")) {
               return new StdDelegatingDeserializer(converter(11, type, List.class));
            } else {
               return localName.contains("Set") ? new StdDelegatingDeserializer(converter(4, type, Set.class)) : null;
            }
         } else {
            return null;
         }
      }
   }

   public static JsonDeserializer<?> findForMap(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      String clsName = type.getRawClass().getName();
      JavaUtilCollectionsConverter conv = null;
      String localName;
      if ((localName = _findUtilCollectionsTypeName(clsName)) != null) {
         String name;
         if ((name = _findUnmodifiableTypeName(localName)) != null) {
            if (name.contains("Map")) {
               conv = converter(6, type, Map.class);
            }
         } else if ((name = _findSingletonTypeName(localName)) != null) {
            if (name.contains("Map")) {
               conv = converter(3, type, Map.class);
            }
         } else if ((name = _findSyncTypeName(localName)) != null && name.contains("Map")) {
            conv = converter(10, type, Map.class);
         }
      } else if ((localName = _findUtilCollectionsImmutableTypeName(clsName)) != null && localName.contains("Map")) {
         conv = converter(6, type, Map.class);
      }

      return conv == null ? null : new StdDelegatingDeserializer(conv);
   }

   static JavaUtilCollectionsConverter converter(int kind, JavaType concreteType, Class<?> rawSuper) {
      return new JavaUtilCollectionsConverter(kind, concreteType.findSuperType(rawSuper));
   }

   private static String _findUtilArrayTypeName(String clsName) {
      return clsName.startsWith("java.util.Arrays$") ? clsName.substring("java.util.Arrays$".length()) : null;
   }

   private static String _findUtilCollectionsTypeName(String clsName) {
      return clsName.startsWith("java.util.Collections$") ? clsName.substring("java.util.Collections$".length()) : null;
   }

   private static String _findUtilCollectionsImmutableTypeName(String clsName) {
      return clsName.startsWith("java.util.ImmutableCollections$") ? clsName.substring("java.util.ImmutableCollections$".length()) : null;
   }

   private static String _findSingletonTypeName(String localName) {
      return localName.startsWith("Singleton") ? localName.substring(9) : null;
   }

   private static String _findSyncTypeName(String localName) {
      return localName.startsWith("Synchronized") ? localName.substring(12) : null;
   }

   private static String _findUnmodifiableTypeName(String localName) {
      return localName.startsWith("Unmodifiable") ? localName.substring(12) : null;
   }

   private static class JavaUtilCollectionsConverter implements Converter<Object, Object> {
      private final JavaType _inputType;
      private final int _kind;

      JavaUtilCollectionsConverter(int kind, JavaType inputType) {
         this._inputType = inputType;
         this._kind = kind;
      }

      public Object convert(Object value) {
         if (value == null) {
            return null;
         } else {
            switch (this._kind) {
               case 1:
                  Set<?> set = (Set)value;
                  this._checkSingleton(set.size());
                  return Collections.singleton(set.iterator().next());
               case 2:
                  List<?> list = (List)value;
                  this._checkSingleton(list.size());
                  return Collections.singletonList(list.get(0));
               case 3:
                  Map<?, ?> map = (Map)value;
                  this._checkSingleton(map.size());
                  Map.Entry<?, ?> entry = (Map.Entry)map.entrySet().iterator().next();
                  return Collections.singletonMap(entry.getKey(), entry.getValue());
               case 4:
                  return Collections.unmodifiableSet((Set)value);
               case 5:
                  return Collections.unmodifiableList((List)value);
               case 6:
                  return Collections.unmodifiableMap((Map)value);
               case 7:
                  return Collections.synchronizedSet((Set)value);
               case 8:
                  return Collections.synchronizedCollection((Collection)value);
               case 9:
                  return Collections.synchronizedList((List)value);
               case 10:
                  return Collections.synchronizedMap((Map)value);
               case 11:
               default:
                  return value;
            }
         }
      }

      public JavaType getInputType(TypeFactory typeFactory) {
         return this._inputType;
      }

      public JavaType getOutputType(TypeFactory typeFactory) {
         return this._inputType;
      }

      private void _checkSingleton(int size) {
         if (size != 1) {
            throw new IllegalArgumentException("Can not deserialize Singleton container from " + size + " entries");
         }
      }
   }
}
