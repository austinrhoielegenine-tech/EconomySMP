package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;

public class MapEntryAsPOJOSerializer extends StdSerializer<Map.Entry<?, ?>> {
   private static final long serialVersionUID = 1L;

   protected MapEntryAsPOJOSerializer(JavaType type) {
      super(type);
   }

   public static MapEntryAsPOJOSerializer create(SerializerProvider ctxt, JavaType type) {
      return new MapEntryAsPOJOSerializer(type);
   }

   public void serialize(Map.Entry<?, ?> value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
      gen.writeStartObject(value);
      ctxt.defaultSerializeField("key", value.getKey(), gen);
      ctxt.defaultSerializeField("value", value.getValue(), gen);
      gen.writeEndObject();
   }
}
