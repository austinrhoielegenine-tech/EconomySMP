package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumValues;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public abstract class StdKeySerializers {
   protected static final JsonSerializer<Object> DEFAULT_KEY_SERIALIZER = new StdKeySerializer();
   protected static final JsonSerializer<Object> DEFAULT_STRING_SERIALIZER = new StringKeySerializer();

   public static JsonSerializer<Object> getStdKeySerializer(SerializationConfig config, Class<?> rawKeyType, boolean useDefault) {
      if (rawKeyType != null && rawKeyType != Object.class) {
         if (rawKeyType == String.class) {
            return DEFAULT_STRING_SERIALIZER;
         } else {
            if (rawKeyType.isPrimitive()) {
               rawKeyType = ClassUtil.wrapperType(rawKeyType);
            }

            if (rawKeyType == Integer.class) {
               return new Default(5, rawKeyType);
            } else if (rawKeyType == Long.class) {
               return new Default(6, rawKeyType);
            } else if (!rawKeyType.isPrimitive() && !Number.class.isAssignableFrom(rawKeyType)) {
               if (rawKeyType == Class.class) {
                  return new Default(3, rawKeyType);
               } else if (Date.class.isAssignableFrom(rawKeyType)) {
                  return new Default(1, rawKeyType);
               } else if (Calendar.class.isAssignableFrom(rawKeyType)) {
                  return new Default(2, rawKeyType);
               } else if (rawKeyType == UUID.class) {
                  return new Default(8, rawKeyType);
               } else if (rawKeyType == byte[].class) {
                  return new Default(7, rawKeyType);
               } else {
                  return useDefault ? new Default(8, rawKeyType) : null;
               }
            } else {
               return new Default(8, rawKeyType);
            }
         }
      } else {
         return new Dynamic();
      }
   }

   public static JsonSerializer<Object> getFallbackKeySerializer(SerializationConfig config, Class<?> rawKeyType, AnnotatedClass annotatedClass) {
      if (rawKeyType != null) {
         if (rawKeyType == Enum.class) {
            return new Dynamic();
         }

         if (ClassUtil.isEnumType(rawKeyType)) {
            return StdKeySerializers.EnumKeySerializer.construct(rawKeyType, EnumValues.constructFromName(config, annotatedClass), EnumSerializer.constructEnumNamingStrategyValues(config, rawKeyType, annotatedClass));
         }
      }

      return new Default(8, rawKeyType);
   }

   /** @deprecated */
   @Deprecated
   public static JsonSerializer<Object> getDefault() {
      return DEFAULT_KEY_SERIALIZER;
   }

   public static class Default extends StdSerializer<Object> {
      static final int TYPE_DATE = 1;
      static final int TYPE_CALENDAR = 2;
      static final int TYPE_CLASS = 3;
      static final int TYPE_ENUM = 4;
      static final int TYPE_INTEGER = 5;
      static final int TYPE_LONG = 6;
      static final int TYPE_BYTE_ARRAY = 7;
      static final int TYPE_TO_STRING = 8;
      protected final int _typeId;

      public Default(int typeId, Class<?> type) {
         super(type, false);
         this._typeId = typeId;
      }

      public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
         switch (this._typeId) {
            case 1:
               provider.defaultSerializeDateKey((Date)value, g);
               break;
            case 2:
               provider.defaultSerializeDateKey(((Calendar)value).getTimeInMillis(), g);
               break;
            case 3:
               g.writeFieldName(((Class)value).getName());
               break;
            case 4:
               String key;
               if (provider.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
                  key = value.toString();
               } else {
                  Enum<?> e = (Enum)value;
                  if (provider.isEnabled(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)) {
                     key = String.valueOf(e.ordinal());
                  } else {
                     key = e.name();
                  }
               }

               g.writeFieldName(key);
               break;
            case 5:
            case 6:
               g.writeFieldId(((Number)value).longValue());
               break;
            case 7:
               String encoded = provider.getConfig().getBase64Variant().encode((byte[])value);
               g.writeFieldName(encoded);
               break;
            case 8:
            default:
               g.writeFieldName(value.toString());
         }

      }
   }

   public static class Dynamic extends StdSerializer<Object> {
      protected transient PropertySerializerMap _dynamicSerializers = PropertySerializerMap.emptyForProperties();

      public Dynamic() {
         super(String.class, false);
      }

      Object readResolve() {
         this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
         return this;
      }

      public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
         Class<?> cls = value.getClass();
         PropertySerializerMap m = this._dynamicSerializers;
         JsonSerializer<Object> ser = m.serializerFor(cls);
         if (ser == null) {
            ser = this._findAndAddDynamic(m, cls, provider);
         }

         ser.serialize(value, g, provider);
      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this.visitStringFormat(visitor, typeHint);
      }

      protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
         if (type == Object.class) {
            JsonSerializer<Object> ser = new Default(8, type);
            this._dynamicSerializers = map.newWith(type, ser);
            return ser;
         } else {
            PropertySerializerMap.SerializerAndMapResult result = map.findAndAddKeySerializer(type, provider, (BeanProperty)null);
            if (map != result.map) {
               this._dynamicSerializers = result.map;
            }

            return result.serializer;
         }
      }
   }

   public static class StringKeySerializer extends StdSerializer<Object> {
      public StringKeySerializer() {
         super(String.class, false);
      }

      public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
         g.writeFieldName((String)value);
      }
   }

   public static class EnumKeySerializer extends StdSerializer<Object> {
      protected final EnumValues _values;
      protected final EnumValues _valuesByEnumNaming;

      protected EnumKeySerializer(Class<?> enumType, EnumValues values) {
         super(enumType, false);
         this._values = values;
         this._valuesByEnumNaming = null;
      }

      protected EnumKeySerializer(Class<?> enumType, EnumValues values, EnumValues valuesByEnumNaming) {
         super(enumType, false);
         this._values = values;
         this._valuesByEnumNaming = valuesByEnumNaming;
      }

      public static EnumKeySerializer construct(Class<?> enumType, EnumValues enumValues) {
         return new EnumKeySerializer(enumType, enumValues);
      }

      public static EnumKeySerializer construct(Class<?> enumType, EnumValues enumValues, EnumValues valuesByEnumNaming) {
         return new EnumKeySerializer(enumType, enumValues, valuesByEnumNaming);
      }

      public void serialize(Object value, JsonGenerator g, SerializerProvider serializers) throws IOException {
         if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
            g.writeFieldName(value.toString());
         } else {
            Enum<?> en = (Enum)value;
            if (this._valuesByEnumNaming != null) {
               g.writeFieldName(this._valuesByEnumNaming.serializedValueFor(en));
            } else if (serializers.isEnabled(SerializationFeature.WRITE_ENUM_KEYS_USING_INDEX)) {
               g.writeFieldName(String.valueOf(en.ordinal()));
            } else {
               g.writeFieldName(this._values.serializedValueFor(en));
            }
         }
      }
   }
}
