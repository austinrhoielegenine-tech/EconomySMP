package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

class MaybeNullSerializer extends JsonSerializer<MaybeNull<?>> {
   public void serialize(MaybeNull<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (!value.isPresent()) {
         gen.writeNull();
      } else {
         gen.writeObject(value.value());
      }

   }
}
