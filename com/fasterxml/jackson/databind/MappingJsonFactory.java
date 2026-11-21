package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import java.io.IOException;

public class MappingJsonFactory extends JsonFactory {
   private static final long serialVersionUID = -1L;

   public MappingJsonFactory() {
      this((ObjectMapper)null);
   }

   public MappingJsonFactory(ObjectMapper mapper) {
      super((ObjectCodec)mapper);
      if (mapper == null) {
         this.setCodec(new ObjectMapper(this));
      }

   }

   public MappingJsonFactory(JsonFactory src, ObjectMapper mapper) {
      super(src, mapper);
      if (mapper == null) {
         this.setCodec(new ObjectMapper(this));
      }

   }

   public final ObjectMapper getCodec() {
      return (ObjectMapper)this._objectCodec;
   }

   public JsonFactory copy() {
      this._checkInvalidCopy(MappingJsonFactory.class);
      return new MappingJsonFactory(this, (ObjectMapper)null);
   }

   public String getFormatName() {
      return "JSON";
   }

   public MatchStrength hasFormat(InputAccessor acc) throws IOException {
      return this.getClass() == MappingJsonFactory.class ? this.hasJSONFormat(acc) : null;
   }
}
