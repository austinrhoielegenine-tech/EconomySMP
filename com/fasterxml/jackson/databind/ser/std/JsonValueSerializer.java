package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@JacksonStdImpl
public class JsonValueSerializer extends StdSerializer<Object> implements ContextualSerializer, JsonFormatVisitable {
   protected final AnnotatedMember _accessor;
   protected final TypeSerializer _valueTypeSerializer;
   protected final JsonSerializer<Object> _valueSerializer;
   protected final BeanProperty _property;
   protected final JavaType _valueType;
   protected final boolean _forceTypeInformation;
   protected final Set<String> _ignoredProperties;
   protected transient PropertySerializerMap _dynamicSerializers;

   public JsonValueSerializer(AnnotatedMember accessor, TypeSerializer vts, JsonSerializer<?> ser, Set<String> ignoredProperties) {
      super(accessor.getType());
      this._accessor = accessor;
      this._valueType = accessor.getType();
      this._valueTypeSerializer = vts;
      this._valueSerializer = ser;
      this._property = null;
      this._forceTypeInformation = true;
      this._ignoredProperties = ignoredProperties;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
   }

   /** @deprecated */
   @Deprecated
   public JsonValueSerializer(AnnotatedMember accessor, TypeSerializer vts, JsonSerializer<?> ser) {
      this(accessor, vts, ser, Collections.emptySet());
   }

   /** @deprecated */
   @Deprecated
   public JsonValueSerializer(AnnotatedMember accessor, JsonSerializer<?> ser) {
      this(accessor, (TypeSerializer)null, ser, Collections.emptySet());
   }

   public JsonValueSerializer(JsonValueSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
      super(_notNullClass(src.handledType()));
      this._accessor = src._accessor;
      this._valueType = src._valueType;
      this._valueTypeSerializer = vts;
      this._valueSerializer = ser;
      this._property = property;
      this._forceTypeInformation = forceTypeInfo;
      this._ignoredProperties = src._ignoredProperties;
      this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
   }

   public static JsonValueSerializer construct(SerializationConfig config, AnnotatedMember accessor, TypeSerializer vts, JsonSerializer<?> ser) {
      JsonIgnoreProperties.Value ignorals = config.getAnnotationIntrospector().findPropertyIgnoralByName(config, accessor);
      Set<String> ignoredProperties = ignorals.findIgnoredForSerialization();
      ser = _withIgnoreProperties(ser, ignoredProperties);
      return new JsonValueSerializer(accessor, vts, ser, ignoredProperties);
   }

   private static final Class<Object> _notNullClass(Class<?> cls) {
      return cls == null ? Object.class : cls;
   }

   protected JsonValueSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> ser, boolean forceTypeInfo) {
      return this._property == property && this._valueTypeSerializer == vts && this._valueSerializer == ser && forceTypeInfo == this._forceTypeInformation ? this : new JsonValueSerializer(this, property, vts, ser, forceTypeInfo);
   }

   public boolean isEmpty(SerializerProvider ctxt, Object bean) {
      Object referenced = this._accessor.getValue(bean);
      if (referenced == null) {
         return true;
      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            try {
               ser = this._findDynamicSerializer(ctxt, referenced.getClass());
            } catch (JsonMappingException e) {
               throw new RuntimeJsonMappingException(e);
            }
         }

         return ser.isEmpty(ctxt, referenced);
      }
   }

   public JsonSerializer<?> createContextual(SerializerProvider ctxt, BeanProperty property) throws JsonMappingException {
      TypeSerializer typeSer = this._valueTypeSerializer;
      if (typeSer != null) {
         typeSer = typeSer.forProperty(property);
      }

      JsonSerializer<?> ser = this._valueSerializer;
      if (ser == null) {
         if (!ctxt.isEnabled(MapperFeature.USE_STATIC_TYPING) && !this._valueType.isFinal()) {
            return property != this._property ? this.withResolved(property, typeSer, ser, this._forceTypeInformation) : this;
         } else {
            ser = ctxt.findPrimaryPropertySerializer(this._valueType, property);
            ser = _withIgnoreProperties(ser, this._ignoredProperties);
            boolean forceTypeInformation = this.isNaturalTypeWithStdHandling(this._valueType.getRawClass(), ser);
            return this.withResolved(property, typeSer, ser, forceTypeInformation);
         }
      } else {
         ser = ctxt.handlePrimaryContextualization(ser, property);
         return this.withResolved(property, typeSer, ser, this._forceTypeInformation);
      }
   }

   public void serialize(Object bean, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
      Object value;
      try {
         value = this._accessor.getValue(bean);
      } catch (Exception e) {
         value = null;
         this.wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
      }

      if (value == null) {
         ctxt.defaultSerializeNull(gen);
      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = this._findDynamicSerializer(ctxt, value.getClass());
         }

         if (this._valueTypeSerializer != null) {
            ser.serializeWithType(value, gen, ctxt, this._valueTypeSerializer);
         } else {
            ser.serialize(value, gen, ctxt);
         }
      }

   }

   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer0) throws IOException {
      Object value;
      try {
         value = this._accessor.getValue(bean);
      } catch (Exception e) {
         value = null;
         this.wrapAndThrow(ctxt, e, bean, this._accessor.getName() + "()");
      }

      if (value == null) {
         ctxt.defaultSerializeNull(gen);
      } else {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = this._findDynamicSerializer(ctxt, value.getClass());
         } else if (this._forceTypeInformation) {
            WritableTypeId typeIdDef = typeSer0.writeTypePrefix(gen, typeSer0.typeId(bean, JsonToken.VALUE_STRING));
            ser.serialize(value, gen, ctxt);
            typeSer0.writeTypeSuffix(gen, typeIdDef);
            return;
         }

         TypeSerializerRerouter rr = new TypeSerializerRerouter(typeSer0, bean);
         ser.serializeWithType(value, gen, ctxt, rr);
      }
   }

   /** @deprecated */
   @Deprecated
   public JsonNode getSchema(SerializerProvider ctxt, Type typeHint) throws JsonMappingException {
      return this._valueSerializer instanceof SchemaAware ? ((SchemaAware)this._valueSerializer).getSchema(ctxt, (Type)null) : JsonSchema.getDefaultSchemaNode();
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      Class<?> declaring = this._accessor.getDeclaringClass();
      if (declaring == null || !ClassUtil.isEnumType(declaring) || !this._acceptJsonFormatVisitorForEnum(visitor, typeHint, declaring)) {
         JsonSerializer<Object> ser = this._valueSerializer;
         if (ser == null) {
            ser = visitor.getProvider().findTypedValueSerializer(this._valueType, false, this._property);
            if (ser == null) {
               visitor.expectAnyFormat(typeHint);
               return;
            }
         }

         ser.acceptJsonFormatVisitor(visitor, this._valueType);
      }
   }

   protected boolean _acceptJsonFormatVisitorForEnum(JsonFormatVisitorWrapper visitor, JavaType typeHint, Class<?> enumType) throws JsonMappingException {
      JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
      if (stringVisitor != null) {
         Set<String> enums = new LinkedHashSet();

         for(Object en : enumType.getEnumConstants()) {
            try {
               enums.add(String.valueOf(this._accessor.getValue(en)));
            } catch (Exception e) {
               Throwable t;
               for(t = e; t instanceof InvocationTargetException && t.getCause() != null; t = t.getCause()) {
               }

               ClassUtil.throwIfError(t);
               throw JsonMappingException.wrapWithPath(t, en, this._accessor.getName() + "()");
            }
         }

         stringVisitor.enumTypes(enums);
      }

      return true;
   }

   protected boolean isNaturalTypeWithStdHandling(Class<?> rawType, JsonSerializer<?> ser) {
      if (rawType.isPrimitive()) {
         if (rawType != Integer.TYPE && rawType != Boolean.TYPE && rawType != Double.TYPE) {
            return false;
         }
      } else if (rawType != String.class && rawType != Integer.class && rawType != Boolean.class && rawType != Double.class) {
         return false;
      }

      return this.isDefaultSerializer(ser);
   }

   protected JsonSerializer<Object> _findDynamicSerializer(SerializerProvider ctxt, Class<?> valueClass) throws JsonMappingException {
      JsonSerializer<Object> serializer = this._dynamicSerializers.serializerFor(valueClass);
      if (serializer == null) {
         if (this._valueType.hasGenericTypes()) {
            JavaType fullType = ctxt.constructSpecializedType(this._valueType, valueClass);
            JsonSerializer<Object> var6 = ctxt.findPrimaryPropertySerializer(fullType, this._property);
            serializer = _withIgnoreProperties(var6, this._ignoredProperties);
            PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(fullType, serializer);
            this._dynamicSerializers = result.map;
         } else {
            JsonSerializer<Object> var7 = ctxt.findPrimaryPropertySerializer(valueClass, this._property);
            serializer = _withIgnoreProperties(var7, this._ignoredProperties);
            PropertySerializerMap.SerializerAndMapResult result = this._dynamicSerializers.addSerializer(valueClass, serializer);
            this._dynamicSerializers = result.map;
         }
      }

      return serializer;
   }

   protected static JsonSerializer<Object> _withIgnoreProperties(JsonSerializer<?> ser, Set<String> ignoredProperties) {
      if (ser != null && !ignoredProperties.isEmpty()) {
         ser = ser.withIgnoredProperties(ignoredProperties);
      }

      return ser;
   }

   public String toString() {
      return "(@JsonValue serializer for method " + this._accessor.getDeclaringClass() + "#" + this._accessor.getName() + ")";
   }

   static class TypeSerializerRerouter extends TypeSerializer {
      protected final TypeSerializer _typeSerializer;
      protected final Object _forObject;

      public TypeSerializerRerouter(TypeSerializer ts, Object ob) {
         this._typeSerializer = ts;
         this._forObject = ob;
      }

      public TypeSerializer forProperty(BeanProperty prop) {
         throw new UnsupportedOperationException();
      }

      public JsonTypeInfo.As getTypeInclusion() {
         return this._typeSerializer.getTypeInclusion();
      }

      public String getPropertyName() {
         return this._typeSerializer.getPropertyName();
      }

      public TypeIdResolver getTypeIdResolver() {
         return this._typeSerializer.getTypeIdResolver();
      }

      public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId typeId) throws IOException {
         typeId.forValue = this._forObject;
         return this._typeSerializer.writeTypePrefix(g, typeId);
      }

      public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId typeId) throws IOException {
         return this._typeSerializer.writeTypeSuffix(g, typeId);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypePrefixForScalar(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypePrefixForObject(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypePrefixForObject(this._forObject, gen);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypePrefixForArray(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypePrefixForArray(this._forObject, gen);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypeSuffixForScalar(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypeSuffixForScalar(this._forObject, gen);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypeSuffixForObject(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypeSuffixForObject(this._forObject, gen);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypeSuffixForArray(Object value, JsonGenerator gen) throws IOException {
         this._typeSerializer.writeTypeSuffixForArray(this._forObject, gen);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypePrefixForScalar(Object value, JsonGenerator gen, Class<?> type) throws IOException {
         this._typeSerializer.writeTypePrefixForScalar(this._forObject, gen, type);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypePrefixForObject(Object value, JsonGenerator gen, Class<?> type) throws IOException {
         this._typeSerializer.writeTypePrefixForObject(this._forObject, gen, type);
      }

      /** @deprecated */
      @Deprecated
      public void writeTypePrefixForArray(Object value, JsonGenerator gen, Class<?> type) throws IOException {
         this._typeSerializer.writeTypePrefixForArray(this._forObject, gen, type);
      }

      /** @deprecated */
      @Deprecated
      public void writeCustomTypePrefixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypePrefixForScalar(this._forObject, gen, typeId);
      }

      /** @deprecated */
      @Deprecated
      public void writeCustomTypePrefixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypePrefixForObject(this._forObject, gen, typeId);
      }

      /** @deprecated */
      @Deprecated
      public void writeCustomTypePrefixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypePrefixForArray(this._forObject, gen, typeId);
      }

      /** @deprecated */
      @Deprecated
      public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypeSuffixForScalar(this._forObject, gen, typeId);
      }

      /** @deprecated */
      @Deprecated
      public void writeCustomTypeSuffixForObject(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypeSuffixForObject(this._forObject, gen, typeId);
      }

      /** @deprecated */
      @Deprecated
      public void writeCustomTypeSuffixForArray(Object value, JsonGenerator gen, String typeId) throws IOException {
         this._typeSerializer.writeCustomTypeSuffixForArray(this._forObject, gen, typeId);
      }
   }
}
