package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.net.InetAddress;

public class InetAddressSerializer extends StdScalarSerializer<InetAddress> implements ContextualSerializer {
   protected final boolean _asNumeric;

   public InetAddressSerializer() {
      this(false);
   }

   public InetAddressSerializer(boolean asNumeric) {
      super(InetAddress.class);
      this._asNumeric = asNumeric;
   }

   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
      boolean asNumeric = false;
      if (format != null) {
         JsonFormat.Shape shape = format.getShape();
         if (shape.isNumeric() || shape == JsonFormat.Shape.ARRAY) {
            asNumeric = true;
         }
      }

      return asNumeric != this._asNumeric ? new InetAddressSerializer(asNumeric) : this;
   }

   public void serialize(InetAddress value, JsonGenerator g, SerializerProvider provider) throws IOException {
      String str;
      if (this._asNumeric) {
         str = value.getHostAddress();
      } else {
         str = value.toString().trim();
         int ix = str.indexOf(47);
         if (ix >= 0) {
            if (ix == 0) {
               str = str.substring(1);
            } else {
               str = str.substring(0, ix);
            }
         }
      }

      g.writeString(str);
   }

   public void serializeWithType(InetAddress value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, (Class)InetAddress.class, (JsonToken)JsonToken.VALUE_STRING));
      this.serialize(value, g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }
}
