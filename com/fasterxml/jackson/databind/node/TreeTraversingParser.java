package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

public class TreeTraversingParser extends ParserMinimalBase {
   protected ObjectCodec _objectCodec;
   protected NodeCursor _nodeCursor;
   protected boolean _closed;

   public TreeTraversingParser(JsonNode n) {
      this(n, (ObjectCodec)null);
   }

   public TreeTraversingParser(JsonNode n, ObjectCodec codec) {
      super(StreamReadConstraints.defaults());
      this._objectCodec = codec;
      this._nodeCursor = new NodeCursor.RootCursor(n, (NodeCursor)null);
   }

   public void setCodec(ObjectCodec c) {
      this._objectCodec = c;
   }

   public ObjectCodec getCodec() {
      return this._objectCodec;
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
      return DEFAULT_READ_CAPABILITIES;
   }

   public void close() throws IOException {
      if (!this._closed) {
         this._closed = true;
         this._nodeCursor = null;
         this._updateTokenToNull();
      }

   }

   public JsonToken nextToken() throws IOException {
      this._nullSafeUpdateToken(this._nodeCursor.nextToken());
      if (this._currToken == null) {
         this._closed = true;
         return null;
      } else {
         switch (this._currToken) {
            case START_OBJECT:
               this._nodeCursor = this._nodeCursor.startObject();
               break;
            case START_ARRAY:
               this._nodeCursor = this._nodeCursor.startArray();
               break;
            case END_OBJECT:
            case END_ARRAY:
               this._nodeCursor = this._nodeCursor.getParent();
         }

         return this._currToken;
      }
   }

   public JsonParser skipChildren() throws IOException {
      if (this._currToken == JsonToken.START_OBJECT) {
         this._nodeCursor = this._nodeCursor.getParent();
         this._updateToken(JsonToken.END_OBJECT);
      } else if (this._currToken == JsonToken.START_ARRAY) {
         this._nodeCursor = this._nodeCursor.getParent();
         this._updateToken(JsonToken.END_ARRAY);
      }

      return this;
   }

   public boolean isClosed() {
      return this._closed;
   }

   public String currentName() {
      NodeCursor crsr = this._nodeCursor;
      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
         crsr = crsr.getParent();
      }

      return crsr == null ? null : crsr.getCurrentName();
   }

   /** @deprecated */
   @Deprecated
   public String getCurrentName() {
      return this.currentName();
   }

   public void overrideCurrentName(String name) {
      NodeCursor crsr = this._nodeCursor;
      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
         crsr = crsr.getParent();
      }

      if (crsr != null) {
         crsr.overrideCurrentName(name);
      }

   }

   public JsonStreamContext getParsingContext() {
      return this._nodeCursor;
   }

   public JsonLocation currentLocation() {
      return JsonLocation.NA;
   }

   public JsonLocation currentTokenLocation() {
      return JsonLocation.NA;
   }

   /** @deprecated */
   @Deprecated
   public JsonLocation getTokenLocation() {
      return this.currentTokenLocation();
   }

   /** @deprecated */
   @Deprecated
   public JsonLocation getCurrentLocation() {
      return this.currentLocation();
   }

   public String getText() {
      if (this._currToken == null) {
         return null;
      } else {
         switch (this._currToken) {
            case FIELD_NAME:
               return this._nodeCursor.getCurrentName();
            case VALUE_STRING:
               return this.currentNode().textValue();
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
               return String.valueOf(this.currentNode().numberValue());
            case VALUE_EMBEDDED_OBJECT:
               JsonNode n = this.currentNode();
               if (n != null && n.isBinary()) {
                  return n.asText();
               }
            default:
               return this._currToken.asString();
         }
      }
   }

   public char[] getTextCharacters() throws IOException {
      return this.getText().toCharArray();
   }

   public int getTextLength() throws IOException {
      return this.getText().length();
   }

   public int getTextOffset() throws IOException {
      return 0;
   }

   public boolean hasTextCharacters() {
      return false;
   }

   public JsonParser.NumberType getNumberType() throws IOException {
      JsonNode n = this.currentNumericNode();
      return n == null ? null : n.numberType();
   }

   public JsonParser.NumberTypeFP getNumberTypeFP() throws IOException {
      JsonParser.NumberType nt = this.getNumberType();
      if (nt == JsonParser.NumberType.BIG_DECIMAL) {
         return JsonParser.NumberTypeFP.BIG_DECIMAL;
      } else if (nt == JsonParser.NumberType.DOUBLE) {
         return JsonParser.NumberTypeFP.DOUBLE64;
      } else {
         return nt == JsonParser.NumberType.FLOAT ? JsonParser.NumberTypeFP.FLOAT32 : JsonParser.NumberTypeFP.UNKNOWN;
      }
   }

   public BigInteger getBigIntegerValue() throws IOException {
      return this.currentNumericNode().bigIntegerValue();
   }

   public BigDecimal getDecimalValue() throws IOException {
      return this.currentNumericNode().decimalValue();
   }

   public double getDoubleValue() throws IOException {
      return this.currentNumericNode().doubleValue();
   }

   public float getFloatValue() throws IOException {
      return (float)this.currentNumericNode().doubleValue();
   }

   public int getIntValue() throws IOException {
      NumericNode node = (NumericNode)this.currentNumericNode();
      if (!node.canConvertToInt()) {
         this.reportOverflowInt();
      }

      return node.intValue();
   }

   public long getLongValue() throws IOException {
      NumericNode node = (NumericNode)this.currentNumericNode();
      if (!node.canConvertToLong()) {
         this.reportOverflowLong();
      }

      return node.longValue();
   }

   public Number getNumberValue() throws IOException {
      return this.currentNumericNode().numberValue();
   }

   public Object getEmbeddedObject() {
      if (!this._closed) {
         JsonNode n = this.currentNode();
         if (n != null) {
            if (n.isPojo()) {
               return ((POJONode)n).getPojo();
            }

            if (n.isBinary()) {
               return ((BinaryNode)n).binaryValue();
            }
         }
      }

      return null;
   }

   public boolean isNaN() {
      if (!this._closed) {
         JsonNode n = this.currentNode();
         if (n instanceof NumericNode) {
            return ((NumericNode)n).isNaN();
         }
      }

      return false;
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      JsonNode n = this.currentNode();
      if (n != null) {
         return n instanceof TextNode ? ((TextNode)n).getBinaryValue(b64variant) : n.binaryValue();
      } else {
         return null;
      }
   }

   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      byte[] data = this.getBinaryValue(b64variant);
      if (data != null) {
         out.write(data, 0, data.length);
         return data.length;
      } else {
         return 0;
      }
   }

   protected JsonNode currentNode() {
      return !this._closed && this._nodeCursor != null ? this._nodeCursor.currentNode() : null;
   }

   protected JsonNode currentNumericNode() throws JacksonException {
      JsonNode n = this.currentNode();
      if (n != null && n.isNumber()) {
         return n;
      } else {
         JsonToken t = n == null ? null : n.asToken();
         throw this._constructError("Current token (" + t + ") not numeric, cannot use numeric value accessors");
      }
   }

   protected void _handleEOF() {
      this._throwInternal();
   }
}
