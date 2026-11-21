package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;

public class AsWrapperTypeDeserializer extends TypeDeserializerBase implements Serializable {
   private static final long serialVersionUID = 1L;

   public AsWrapperTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
      super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
   }

   protected AsWrapperTypeDeserializer(AsWrapperTypeDeserializer src, BeanProperty property) {
      super(src, property);
   }

   public TypeDeserializer forProperty(BeanProperty prop) {
      return prop == this._property ? this : new AsWrapperTypeDeserializer(this, prop);
   }

   public JsonTypeInfo.As getTypeInclusion() {
      return JsonTypeInfo.As.WRAPPER_OBJECT;
   }

   public Object deserializeTypedFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   public Object deserializeTypedFromArray(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   public Object deserializeTypedFromScalar(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   public Object deserializeTypedFromAny(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserialize(jp, ctxt);
   }

   protected Object _deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.canReadTypeId()) {
         Object typeId = p.getTypeId();
         if (typeId != null) {
            return this._deserializeWithNativeTypeId(p, ctxt, typeId);
         }
      }

      JsonToken t = p.currentToken();
      if (t == JsonToken.START_OBJECT) {
         if (p.nextToken() != JsonToken.FIELD_NAME) {
            ctxt.reportWrongTokenException(this.baseType(), JsonToken.FIELD_NAME, "need JSON String that contains type id (for subtype of " + this.baseTypeName() + ")");
         }
      } else if (t != JsonToken.FIELD_NAME) {
         ctxt.reportWrongTokenException(this.baseType(), JsonToken.START_OBJECT, "need JSON Object to contain As.WRAPPER_OBJECT type information for class " + this.baseTypeName());
      }

      String typeId = p.getText();
      JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
      p.nextToken();
      if (this._typeIdVisible && p.hasToken(JsonToken.START_OBJECT)) {
         TokenBuffer tb = ctxt.bufferForInputBuffering(p);
         tb.writeStartObject();
         tb.writeFieldName(this._typePropertyName);
         tb.writeString(typeId);
         p.clearCurrentToken();
         p = JsonParserSequence.createFlattened(false, tb.asParser(p), p);
         p.nextToken();
      }

      Object value = deser.deserialize(p, ctxt);
      if (p.nextToken() != JsonToken.END_OBJECT) {
         ctxt.reportWrongTokenException(this.baseType(), JsonToken.END_OBJECT, "expected closing END_OBJECT after type information and deserialized value");
      }

      return value;
   }
}
