package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumResolver implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final Class<Enum<?>> _enumClass;
   protected final Enum<?>[] _enums;
   protected final HashMap<String, Enum<?>> _enumsById;
   protected final Enum<?> _defaultValue;
   protected final boolean _isIgnoreCase;
   protected final boolean _isFromIntValue;
   protected final boolean _hasAsValueAnnotation;

   protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue, boolean isIgnoreCase, boolean isFromIntValue, boolean hasAsValueAnnotation) {
      this._enumClass = enumClass;
      this._enums = enums;
      this._enumsById = map;
      this._defaultValue = defaultValue;
      this._isIgnoreCase = isIgnoreCase;
      this._isFromIntValue = isFromIntValue;
      this._hasAsValueAnnotation = hasAsValueAnnotation;
   }

   /** @deprecated */
   @Deprecated
   protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue, boolean isIgnoreCase, boolean isFromIntValue) {
      this(enumClass, enums, map, defaultValue, isIgnoreCase, isFromIntValue, false);
   }

   public static EnumResolver constructFor(DeserializationConfig config, AnnotatedClass annotatedClass) {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      boolean isIgnoreCase = config.isEnabled((MapperFeature)MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
      Class<?> enumCls0 = annotatedClass.getRawType();
      Class<Enum<?>> enumCls = _enumClass(enumCls0);
      Enum<?>[] enumConstants = _enumConstants(enumCls0);
      String[] names = ai.findEnumValues(config, annotatedClass, enumConstants, new String[enumConstants.length]);
      String[][] allAliases = new String[names.length][];
      ai.findEnumAliases(config, annotatedClass, enumConstants, allAliases);
      HashMap<String, Enum<?>> map = new HashMap();
      int i = 0;

      for(int len = enumConstants.length; i < len; ++i) {
         Enum<?> enumValue = enumConstants[i];
         String name = names[i];
         if (name == null) {
            name = enumValue.name();
         }

         map.put(name, enumValue);
         String[] aliases = allAliases[i];
         if (aliases != null) {
            for(String alias : aliases) {
               map.putIfAbsent(alias, enumValue);
            }
         }
      }

      return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, annotatedClass, enumConstants), isIgnoreCase, false, false);
   }

   public static EnumResolver constructUsingToString(DeserializationConfig config, AnnotatedClass annotatedClass) {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      boolean isIgnoreCase = config.isEnabled((MapperFeature)MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
      Class<?> enumCls0 = annotatedClass.getRawType();
      Class<Enum<?>> enumCls = _enumClass(enumCls0);
      Enum<?>[] enumConstants = _enumConstants(enumCls0);
      String[] names = new String[enumConstants.length];
      String[][] allAliases = new String[enumConstants.length][];
      if (ai != null) {
         ai.findEnumValues(config, annotatedClass, enumConstants, names);
         ai.findEnumAliases(config, annotatedClass, enumConstants, allAliases);
      }

      HashMap<String, Enum<?>> map = new HashMap();
      int i = enumConstants.length;

      while(true) {
         --i;
         if (i < 0) {
            return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, annotatedClass, enumConstants), isIgnoreCase, false, false);
         }

         Enum<?> enumValue = enumConstants[i];
         String name = names[i];
         if (name == null) {
            name = enumValue.toString();
         }

         map.put(name, enumValue);
         String[] aliases = allAliases[i];
         if (aliases != null) {
            for(String alias : aliases) {
               map.putIfAbsent(alias, enumValue);
            }
         }
      }
   }

   public static EnumResolver constructUsingIndex(DeserializationConfig config, AnnotatedClass annotatedClass) {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      boolean isIgnoreCase = config.isEnabled((MapperFeature)MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
      Class<?> enumCls0 = annotatedClass.getRawType();
      Class<Enum<?>> enumCls = _enumClass(enumCls0);
      Enum<?>[] enumConstants = _enumConstants(enumCls0);
      HashMap<String, Enum<?>> map = new HashMap();
      int i = enumConstants.length;

      while(true) {
         --i;
         if (i < 0) {
            return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, annotatedClass, enumConstants), isIgnoreCase, false, false);
         }

         Enum<?> enumValue = enumConstants[i];
         map.put(String.valueOf(i), enumValue);
      }
   }

   public static EnumResolver constructUsingEnumNamingStrategy(DeserializationConfig config, AnnotatedClass annotatedClass, EnumNamingStrategy enumNamingStrategy) {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      boolean isIgnoreCase = config.isEnabled((MapperFeature)MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
      Class<?> enumCls0 = annotatedClass.getRawType();
      Class<Enum<?>> enumCls = _enumClass(enumCls0);
      Enum<?>[] enumConstants = _enumConstants(enumCls0);
      String[] names = new String[enumConstants.length];
      String[][] allAliases = new String[enumConstants.length][];
      if (ai != null) {
         ai.findEnumValues(config, annotatedClass, enumConstants, names);
         ai.findEnumAliases(config, annotatedClass, enumConstants, allAliases);
      }

      HashMap<String, Enum<?>> map = new HashMap();
      int i = enumConstants.length;

      while(true) {
         --i;
         if (i < 0) {
            return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, annotatedClass, enumConstants), isIgnoreCase, false, false);
         }

         Enum<?> anEnum = enumConstants[i];
         String name = names[i];
         if (name == null) {
            name = enumNamingStrategy.convertEnumToExternalName(anEnum.name());
         }

         map.put(name, anEnum);
         String[] aliases = allAliases[i];
         if (aliases != null) {
            for(String alias : aliases) {
               map.putIfAbsent(alias, anEnum);
            }
         }
      }
   }

   public static EnumResolver constructUsingMethod(DeserializationConfig config, AnnotatedClass annotatedClass, AnnotatedMember accessor) {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      boolean isIgnoreCase = config.isEnabled((MapperFeature)MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
      Class<?> enumCls0 = annotatedClass.getRawType();
      Class<Enum<?>> enumCls = _enumClass(enumCls0);
      Enum<?>[] enumConstants = _enumConstants(enumCls0);
      HashMap<String, Enum<?>> map = new HashMap();
      int i = enumConstants.length;

      while(true) {
         --i;
         if (i < 0) {
            return new EnumResolver(enumCls, enumConstants, map, _enumDefault(ai, annotatedClass, enumConstants), isIgnoreCase, _isIntType(accessor.getRawType()), true);
         }

         Enum<?> en = enumConstants[i];

         try {
            Object o = accessor.getValue(en);
            if (o != null) {
               map.put(o.toString(), en);
            }
         } catch (Exception e) {
            throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + e.getMessage());
         }
      }
   }

   public CompactStringObjectMap constructLookup() {
      return CompactStringObjectMap.construct(this._enumsById);
   }

   protected static Class<Enum<?>> _enumClass(Class<?> enumCls0) {
      return enumCls0;
   }

   protected static Enum<?>[] _enumConstants(Class<?> enumCls) {
      Enum<?>[] enumValues = (Enum[])_enumClass(enumCls).getEnumConstants();
      if (enumValues == null) {
         throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
      } else {
         return enumValues;
      }
   }

   protected static Enum<?> _enumDefault(AnnotationIntrospector intr, AnnotatedClass annotatedClass, Enum<?>[] enums) {
      return intr != null ? intr.findDefaultEnumValue(annotatedClass, enums) : null;
   }

   protected static boolean _isIntType(Class<?> erasedType) {
      if (erasedType.isPrimitive()) {
         erasedType = ClassUtil.wrapperType(erasedType);
      }

      return erasedType == Long.class || erasedType == Integer.class || erasedType == Short.class || erasedType == Byte.class;
   }

   public Enum<?> findEnum(String key) {
      Enum<?> en = (Enum)this._enumsById.get(key);
      return en == null && this._isIgnoreCase ? this._findEnumCaseInsensitive(key) : en;
   }

   protected Enum<?> _findEnumCaseInsensitive(String key) {
      for(Map.Entry<String, Enum<?>> entry : this._enumsById.entrySet()) {
         if (key.equalsIgnoreCase((String)entry.getKey())) {
            return (Enum)entry.getValue();
         }
      }

      return null;
   }

   public Enum<?> getEnum(int index) {
      return index >= 0 && index < this._enums.length ? this._enums[index] : null;
   }

   public Enum<?> getDefaultValue() {
      return this._defaultValue;
   }

   public Enum<?>[] getRawEnums() {
      return this._enums;
   }

   public List<Enum<?>> getEnums() {
      ArrayList<Enum<?>> enums = new ArrayList(this._enums.length);

      for(Enum<?> e : this._enums) {
         enums.add(e);
      }

      return enums;
   }

   public Collection<String> getEnumIds() {
      return this._enumsById.keySet();
   }

   public Class<Enum<?>> getEnumClass() {
      return this._enumClass;
   }

   public int lastValidIndex() {
      return this._enums.length - 1;
   }

   public boolean isFromIntValue() {
      return this._isFromIntValue;
   }

   public boolean hasAsValueAnnotation() {
      return this._hasAsValueAnnotation;
   }
}
