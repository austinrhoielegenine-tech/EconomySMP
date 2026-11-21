package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class UUIDSerializer extends StdScalarSerializer<UUID> implements ContextualSerializer {
   static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
   protected final Boolean _asBinary;

   public UUIDSerializer() {
      this((Boolean)null);
   }

   protected UUIDSerializer(Boolean asBinary) {
      super(UUID.class);
      this._asBinary = asBinary;
   }

   public boolean isEmpty(SerializerProvider prov, UUID value) {
      return value.getLeastSignificantBits() == 0L && value.getMostSignificantBits() == 0L;
   }

   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
      Boolean asBinary = null;
      if (format != null) {
         JsonFormat.Shape shape = format.getShape();
         if (shape == JsonFormat.Shape.BINARY) {
            asBinary = true;
         } else if (shape == JsonFormat.Shape.STRING) {
            asBinary = false;
         }
      }

      return !Objects.equals(asBinary, this._asBinary) ? new UUIDSerializer(asBinary) : this;
   }

   public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (this._writeAsBinary(gen)) {
         gen.writeBinary(_asBytes(value));
      } else {
         char[] ch = new char[36];
         long msb = value.getMostSignificantBits();
         _appendInt((int)(msb >> 32), (char[])ch, 0);
         ch[8] = '-';
         int i = (int)msb;
         _appendShort(i >>> 16, ch, 9);
         ch[13] = '-';
         _appendShort(i, ch, 14);
         ch[18] = '-';
         long lsb = value.getLeastSignificantBits();
         _appendShort((int)(lsb >>> 48), ch, 19);
         ch[23] = '-';
         _appendShort((int)(lsb >>> 32), ch, 24);
         _appendInt((int)lsb, (char[])ch, 28);
         gen.writeString(ch, 0, 36);
      }
   }

   protected boolean _writeAsBinary(JsonGenerator g) {
      if (this._asBinary != null) {
         return this._asBinary;
      } else {
         return !(g instanceof TokenBuffer) && g.canWriteBinaryNatively();
      }
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      this.visitStringFormat(visitor, typeHint, JsonValueFormat.UUID);
   }

   private static void _appendInt(int bits, char[] ch, int offset) {
      _appendShort(bits >> 16, ch, offset);
      _appendShort(bits, ch, offset + 4);
   }

   private static void _appendShort(int bits, char[] ch, int offset) {
      ch[offset] = HEX_CHARS[bits >> 12 & 15];
      ++offset;
      ch[offset] = HEX_CHARS[bits >> 8 & 15];
      ++offset;
      ch[offset] = HEX_CHARS[bits >> 4 & 15];
      ++offset;
      ch[offset] = HEX_CHARS[bits & 15];
   }

   private static final byte[] _asBytes(UUID uuid) {
      byte[] buffer = new byte[16];
      long hi = uuid.getMostSignificantBits();
      long lo = uuid.getLeastSignificantBits();
      _appendInt((int)(hi >> 32), (byte[])buffer, 0);
      _appendInt((int)hi, (byte[])buffer, 4);
      _appendInt((int)(lo >> 32), (byte[])buffer, 8);
      _appendInt((int)lo, (byte[])buffer, 12);
      return buffer;
   }

   private static final void _appendInt(int value, byte[] buffer, int offset) {
      buffer[offset] = (byte)(value >> 24);
      ++offset;
      buffer[offset] = (byte)(value >> 16);
      ++offset;
      buffer[offset] = (byte)(value >> 8);
      ++offset;
      buffer[offset] = (byte)value;
   }
}
