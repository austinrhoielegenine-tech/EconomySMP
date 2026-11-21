package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Set;

public class BeanSerializer extends BeanSerializerBase {
   private static final long serialVersionUID = 29L;

   public BeanSerializer(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
      super(type, builder, properties, filteredProperties);
   }

   protected BeanSerializer(BeanSerializerBase src) {
      super(src);
   }

   protected BeanSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
      super(src, objectIdWriter);
   }

   protected BeanSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId) {
      super(src, objectIdWriter, filterId);
   }

   protected BeanSerializer(BeanSerializerBase src, Set<String> toIgnore, Set<String> toInclude) {
      super(src, toIgnore, toInclude);
   }

   protected BeanSerializer(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
      super(src, properties, filteredProperties);
   }

   /** @deprecated */
   @Deprecated
   public static BeanSerializer createDummy(JavaType forType) {
      return new BeanSerializer(forType, (BeanSerializerBuilder)null, NO_PROPS, (BeanPropertyWriter[])null);
   }

   public static BeanSerializer createDummy(JavaType forType, BeanSerializerBuilder builder) {
      return new BeanSerializer(forType, builder, NO_PROPS, (BeanPropertyWriter[])null);
   }

   public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
      return new UnwrappingBeanSerializer(this, unwrapper);
   }

   public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
      return new BeanSerializer(this, objectIdWriter, this._propertyFilterId);
   }

   public BeanSerializerBase withFilterId(Object filterId) {
      return new BeanSerializer(this, this._objectIdWriter, filterId);
   }

   protected BeanSerializerBase withByNameInclusion(Set<String> toIgnore, Set<String> toInclude) {
      return new BeanSerializer(this, toIgnore, toInclude);
   }

   protected BeanSerializerBase withProperties(BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
      return new BeanSerializer(this, properties, filteredProperties);
   }

   public JsonSerializer<?> withIgnoredProperties(Set<String> toIgnore) {
      return new BeanSerializer(this, toIgnore, (Set)null);
   }

   protected BeanSerializerBase asArraySerializer() {
      return (BeanSerializerBase)(this._objectIdWriter == null && this._propertyFilterId == null ? new BeanAsArraySerializer(this) : this);
   }

   public void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (this._objectIdWriter != null) {
         gen.assignCurrentValue(bean);
         this._serializeWithObjectId(bean, gen, provider, true);
      } else {
         gen.writeStartObject(bean);
         if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
         } else {
            this.serializeFields(bean, gen, provider);
         }

         gen.writeEndObject();
      }
   }

   public String toString() {
      return "BeanSerializer for " + this.handledType().getName();
   }
}
