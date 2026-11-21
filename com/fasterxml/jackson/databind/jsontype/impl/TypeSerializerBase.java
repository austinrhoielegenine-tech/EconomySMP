package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

public abstract class TypeSerializerBase extends TypeSerializer {
   protected final TypeIdResolver _idResolver;
   protected final BeanProperty _property;

   protected TypeSerializerBase(TypeIdResolver idRes, BeanProperty property) {
      this._idResolver = idRes;
      this._property = property;
   }

   public abstract JsonTypeInfo.As getTypeInclusion();

   public String getPropertyName() {
      return null;
   }

   public TypeIdResolver getTypeIdResolver() {
      return this._idResolver;
   }

   public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
      this._generateTypeId(idMetadata);
      return idMetadata.id == null ? this._writeTypePrefixForNull(g, idMetadata) : g.writeTypePrefix(idMetadata);
   }

   public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
      return idMetadata == null ? this._writeTypeSuffixfixForNull(g, idMetadata) : g.writeTypeSuffix(idMetadata);
   }

   private WritableTypeId _writeTypePrefixForNull(JsonGenerator g, WritableTypeId typeIdDef) throws IOException {
      JsonToken valueShape = typeIdDef.valueShape;
      typeIdDef.wrapperWritten = false;
      if (valueShape == JsonToken.START_OBJECT) {
         g.writeStartObject(typeIdDef.forValue);
      } else if (valueShape == JsonToken.START_ARRAY) {
         g.writeStartArray(typeIdDef.forValue);
      }

      return typeIdDef;
   }

   private WritableTypeId _writeTypeSuffixfixForNull(JsonGenerator g, WritableTypeId typeIdDef) throws IOException {
      JsonToken valueShape = typeIdDef.valueShape;
      if (valueShape == JsonToken.START_OBJECT) {
         g.writeEndObject();
      } else if (valueShape == JsonToken.START_ARRAY) {
         g.writeEndArray();
      }

      return typeIdDef;
   }

   protected void _generateTypeId(WritableTypeId idMetadata) {
      Object id = idMetadata.id;
      if (id == null) {
         Object value = idMetadata.forValue;
         Class<?> typeForId = idMetadata.forValueType;
         String var5;
         if (typeForId == null) {
            var5 = this.idFromValue(value);
         } else {
            var5 = this.idFromValueAndType(value, typeForId);
         }

         idMetadata.id = var5;
      }

   }

   protected String idFromValue(Object value) {
      String id = this._idResolver.idFromValue(value);
      if (id == null) {
         this.handleMissingId(value);
      }

      return id;
   }

   protected String idFromValueAndType(Object value, Class<?> type) {
      String id = this._idResolver.idFromValueAndType(value, type);
      if (id == null) {
         this.handleMissingId(value);
      }

      return id;
   }

   protected void handleMissingId(Object value) {
   }
}
