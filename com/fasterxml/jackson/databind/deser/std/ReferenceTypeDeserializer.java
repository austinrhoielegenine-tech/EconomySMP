package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import java.io.IOException;

public abstract class ReferenceTypeDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer {
   private static final long serialVersionUID = 2L;
   protected final JavaType _fullType;
   protected final ValueInstantiator _valueInstantiator;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final JsonDeserializer<Object> _valueDeserializer;

   public ReferenceTypeDeserializer(JavaType fullType, ValueInstantiator vi, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
      super(fullType);
      this._valueInstantiator = vi;
      this._fullType = fullType;
      this._valueDeserializer = deser;
      this._valueTypeDeserializer = typeDeser;
   }

   /** @deprecated */
   @Deprecated
   public ReferenceTypeDeserializer(JavaType fullType, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
      this(fullType, (ValueInstantiator)null, typeDeser, deser);
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<?> deser = this._valueDeserializer;
      deser = this.findConvertingContentDeserializer(ctxt, property, deser);
      if (deser == null) {
         deser = ctxt.findContextualValueDeserializer(this._fullType.getReferencedType(), property);
      } else {
         deser = ctxt.handleSecondaryContextualization(deser, property, this._fullType.getReferencedType());
      }

      TypeDeserializer typeDeser = this._valueTypeDeserializer;
      if (typeDeser != null) {
         typeDeser = typeDeser.forProperty(property);
      }

      return deser == this._valueDeserializer && typeDeser == this._valueTypeDeserializer ? this : this.withResolved(typeDeser, deser);
   }

   public AccessPattern getNullAccessPattern() {
      return AccessPattern.DYNAMIC;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.DYNAMIC;
   }

   protected abstract ReferenceTypeDeserializer<T> withResolved(TypeDeserializer var1, JsonDeserializer<?> var2);

   public abstract T getNullValue(DeserializationContext var1) throws JsonMappingException;

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return this.getNullValue(ctxt);
   }

   public abstract T referenceValue(Object var1);

   public abstract T updateReference(T var1, Object var2);

   public abstract Object getReferenced(T var1);

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public JavaType getValueType() {
      return this._fullType;
   }

   public LogicalType logicalType() {
      return this._valueDeserializer != null ? this._valueDeserializer.logicalType() : super.logicalType();
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return this._valueDeserializer == null ? null : this._valueDeserializer.supportsUpdate(config);
   }

   public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._valueInstantiator != null) {
         T value = (T)this._valueInstantiator.createUsingDefault(ctxt);
         return (T)this.deserialize(p, ctxt, value);
      } else {
         Object contents = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
         return (T)this.referenceValue(contents);
      }
   }

   public T deserialize(JsonParser p, DeserializationContext ctxt, T reference) throws IOException {
      Boolean B = this._valueDeserializer.supportsUpdate(ctxt.getConfig());
      Object contents;
      if (!B.equals(Boolean.FALSE) && this._valueTypeDeserializer == null) {
         contents = this.getReferenced(reference);
         if (contents == null) {
            contents = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
            return (T)this.referenceValue(contents);
         }

         contents = this._valueDeserializer.deserialize(p, ctxt, contents);
      } else {
         contents = this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
      }

      return (T)this.updateReference(reference, contents);
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NULL)) {
         return this.getNullValue(ctxt);
      } else {
         return this._valueTypeDeserializer == null ? this.deserialize(p, ctxt) : this.referenceValue(this._valueTypeDeserializer.deserializeTypedFromAny(p, ctxt));
      }
   }
}
