package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

class MaybeNullDeserializer extends JsonDeserializer<MaybeNull<?>> {
   public MaybeNull<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.currentToken() == JsonToken.VALUE_NULL) {
         return new MaybeNull((Object)null);
      } else {
         Object value = ctxt.readValue(p, Object.class);
         return new MaybeNull(value);
      }
   }
}
