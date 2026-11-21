package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;
import java.util.Objects;

public class TextNode extends ValueNode {
   private static final long serialVersionUID = 2L;
   static final TextNode EMPTY_STRING_NODE = new TextNode("");
   protected final String _value;

   public TextNode(String v) {
      this._value = v;
   }

   public static TextNode valueOf(String v) {
      if (v == null) {
         return null;
      } else {
         return v.isEmpty() ? EMPTY_STRING_NODE : new TextNode(v);
      }
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.STRING;
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_STRING;
   }

   public String textValue() {
      return this._value;
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      String str = this._value.trim();
      int initBlockSize = 4 + (str.length() >> 2) * 3;
      ByteArrayBuilder builder = new ByteArrayBuilder(Math.max(16, Math.min(65536, initBlockSize)));

      try {
         b64variant.decode(str, builder);
      } catch (IllegalArgumentException e) {
         throw InvalidFormatException.from((JsonParser)null, String.format("Cannot access contents of TextNode as binary due to broken Base64 encoding: %s", e.getMessage()), str, byte[].class);
      }

      return builder.toByteArray();
   }

   public byte[] binaryValue() throws IOException {
      return this.getBinaryValue(Base64Variants.getDefaultVariant());
   }

   public String asText() {
      return this._value;
   }

   public String asText(String defaultValue) {
      return this._value == null ? defaultValue : this._value;
   }

   public boolean asBoolean(boolean defaultValue) {
      if (this._value != null) {
         String v = this._value.trim();
         if ("true".equals(v)) {
            return true;
         }

         if ("false".equals(v)) {
            return false;
         }
      }

      return defaultValue;
   }

   public int asInt(int defaultValue) {
      return NumberInput.parseAsInt(this._value, defaultValue);
   }

   public long asLong(long defaultValue) {
      return NumberInput.parseAsLong(this._value, defaultValue);
   }

   public double asDouble(double defaultValue) {
      return NumberInput.parseAsDouble(this._value, defaultValue);
   }

   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this._value == null) {
         g.writeNull();
      } else {
         g.writeString(this._value);
      }

   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o instanceof TextNode ? Objects.equals(((TextNode)o)._value, this._value) : false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this._value);
   }

   /** @deprecated */
   @Deprecated
   protected static void appendQuoted(StringBuilder sb, String content) {
      sb.append('"');
      CharTypes.appendQuoted(sb, content);
      sb.append('"');
   }
}
