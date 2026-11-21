package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public abstract class JsonSerializer<T> implements JsonFormatVisitable {
   public JsonSerializer<T> unwrappingSerializer(NameTransformer unwrapper) {
      return this;
   }

   public JsonSerializer<T> replaceDelegatee(JsonSerializer<?> delegatee) {
      throw new UnsupportedOperationException();
   }

   public JsonSerializer<?> withFilterId(Object filterId) {
      return this;
   }

   public JsonSerializer<?> withIgnoredProperties(Set<String> ignoredProperties) {
      return this;
   }

   public abstract void serialize(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException;

   public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
      Class<?> clz = this.handledType();
      if (clz == null) {
         clz = value.getClass();
      }

      serializers.reportBadDefinition(clz, String.format("Type id handling not implemented for type %s (by serializer of type %s)", clz.getName(), this.getClass().getName()));
   }

   public Class<T> handledType() {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public boolean isEmpty(T value) {
      return this.isEmpty((SerializerProvider)null, value);
   }

   public boolean isEmpty(SerializerProvider provider, T value) {
      return value == null;
   }

   public boolean usesObjectId() {
      return false;
   }

   public boolean isUnwrappingSerializer() {
      return false;
   }

   public JsonSerializer<?> getDelegatee() {
      return null;
   }

   public Iterator<PropertyWriter> properties() {
      return ClassUtil.<PropertyWriter>emptyIterator();
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType type) throws JsonMappingException {
      visitor.expectAnyFormat(type);
   }

   public abstract static class None extends JsonSerializer<Object> {
   }
}
