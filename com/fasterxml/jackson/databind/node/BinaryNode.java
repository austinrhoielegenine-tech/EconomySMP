package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Arrays;

public class BinaryNode extends ValueNode {
   private static final long serialVersionUID = 2L;
   static final BinaryNode EMPTY_BINARY_NODE = new BinaryNode(new byte[0]);
   protected final byte[] _data;

   public BinaryNode(byte[] data) {
      this._data = data;
   }

   public BinaryNode(byte[] data, int offset, int length) {
      if (offset == 0 && length == data.length) {
         this._data = data;
      } else {
         this._data = new byte[length];
         System.arraycopy(data, offset, this._data, 0, length);
      }

   }

   public static BinaryNode valueOf(byte[] data) {
      if (data == null) {
         return null;
      } else {
         return data.length == 0 ? EMPTY_BINARY_NODE : new BinaryNode(data);
      }
   }

   public static BinaryNode valueOf(byte[] data, int offset, int length) {
      if (data == null) {
         return null;
      } else {
         return length == 0 ? EMPTY_BINARY_NODE : new BinaryNode(data, offset, length);
      }
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.BINARY;
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_EMBEDDED_OBJECT;
   }

   public byte[] binaryValue() {
      return this._data;
   }

   public String asText() {
      return Base64Variants.getDefaultVariant().encode(this._data, false);
   }

   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this._data == null) {
         g.writeNull();
      } else {
         g.writeBinary(provider.getConfig().getBase64Variant(), this._data, 0, this._data.length);
      }
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o instanceof BinaryNode) {
         byte[] otherData = ((BinaryNode)o)._data;
         if (this._data == otherData) {
            return true;
         } else {
            return this._data != null && otherData != null ? Arrays.equals(this._data, otherData) : false;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this._data == null ? -1 : this._data.length;
   }
}
