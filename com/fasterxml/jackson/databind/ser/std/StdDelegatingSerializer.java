package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.IOException;
import java.lang.reflect.Type;

public class StdDelegatingSerializer extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable {
   protected final Converter<Object, ?> _converter;
   protected final JavaType _delegateType;
   protected final JsonSerializer<Object> _delegateSerializer;

   public StdDelegatingSerializer(Converter<?, ?> converter) {
      super(Object.class);
      this._converter = converter;
      this._delegateType = null;
      this._delegateSerializer = null;
   }

   public <T> StdDelegatingSerializer(Class<T> cls, Converter<T, ?> converter) {
      super(cls, false);
      this._converter = converter;
      this._delegateType = null;
      this._delegateSerializer = null;
   }

   public StdDelegatingSerializer(Converter<Object, ?> converter, JavaType delegateType, JsonSerializer<?> delegateSerializer) {
      super(delegateType);
      this._converter = converter;
      this._delegateType = delegateType;
      this._delegateSerializer = delegateSerializer;
   }

   protected StdDelegatingSerializer withDelegate(Converter<Object, ?> converter, JavaType delegateType, JsonSerializer<?> delegateSerializer) {
      ClassUtil.verifyMustOverride(StdDelegatingSerializer.class, this, "withDelegate");
      return new StdDelegatingSerializer(converter, delegateType, delegateSerializer);
   }

   public void resolve(SerializerProvider provider) throws JsonMappingException {
      if (this._delegateSerializer != null && this._delegateSerializer instanceof ResolvableSerializer) {
         ((ResolvableSerializer)this._delegateSerializer).resolve(provider);
      }

   }

   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<?> delSer = this._delegateSerializer;
      JavaType delegateType = this._delegateType;
      if (delSer == null) {
         if (delegateType == null) {
            delegateType = this._converter.getOutputType(provider.getTypeFactory());
         }

         if (!delegateType.isJavaLangObject()) {
            delSer = provider.findValueSerializer(delegateType);
         }
      }

      if (delSer instanceof ContextualSerializer) {
         delSer = provider.handleSecondaryContextualization(delSer, property);
      }

      return delSer == this._delegateSerializer && delegateType == this._delegateType ? this : this.withDelegate(this._converter, delegateType, delSer);
   }

   public Converter<Object, ?> getConverter() {
      return this._converter;
   }

   public JsonSerializer<?> getDelegatee() {
      return this._delegateSerializer;
   }

   public void serialize(Object value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
      Object delegateValue = this.convertValue(ctxt, value);
      if (delegateValue == null) {
         ctxt.defaultSerializeNull(gen);
      } else {
         JsonSerializer<Object> ser = this._delegateSerializer;
         if (ser == null) {
            ser = this._findSerializer(delegateValue, ctxt);
         }

         ser.serialize(delegateValue, gen, ctxt);
      }
   }

   public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
      Object delegateValue = this.convertValue(ctxt, value);
      if (delegateValue == null) {
         ctxt.defaultSerializeNull(gen);
      } else {
         JsonSerializer<Object> ser = this._delegateSerializer;
         if (ser == null) {
            ser = this._findSerializer(delegateValue, ctxt);
         }

         ser.serializeWithType(delegateValue, gen, ctxt, typeSer);
      }
   }

   public boolean isEmpty(SerializerProvider ctxt, Object value) {
      Object delegateValue = this.convertValue(ctxt, value);
      if (delegateValue == null) {
         return true;
      } else if (this._delegateSerializer == null) {
         return value == null;
      } else {
         return this._delegateSerializer.isEmpty(ctxt, delegateValue);
      }
   }

   /** @deprecated */
   @Deprecated
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
      return this._delegateSerializer instanceof SchemaAware ? ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint) : super.getSchema(provider, typeHint);
   }

   /** @deprecated */
   @Deprecated
   public JsonNode getSchema(SerializerProvider provider, Type typeHint, boolean isOptional) throws JsonMappingException {
      return this._delegateSerializer instanceof SchemaAware ? ((SchemaAware)this._delegateSerializer).getSchema(provider, typeHint, isOptional) : super.getSchema(provider, typeHint);
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      if (this._delegateSerializer != null) {
         this._delegateSerializer.acceptJsonFormatVisitor(visitor, typeHint);
      }

   }

   /** @deprecated */
   @Deprecated
   protected Object convertValue(Object value) {
      return this._converter.convert(value);
   }

   protected Object convertValue(SerializerProvider ctxt, Object value) {
      return this._converter.convert(ctxt, value);
   }

   protected JsonSerializer<Object> _findSerializer(Object value, SerializerProvider serializers) throws JsonMappingException {
      JsonSerializer<Object> ser = serializers.findValueSerializer(value.getClass());
      if (ser instanceof ContextualSerializer) {
         ser = (ser).createContextual(serializers, (BeanProperty)null);
      }

      return ser;
   }
}
