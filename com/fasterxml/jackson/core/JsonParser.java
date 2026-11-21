package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.core.util.RequestPayload;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

public abstract class JsonParser implements Closeable, Versioned {
   private static final int MIN_BYTE_I = -128;
   private static final int MAX_BYTE_I = 255;
   private static final int MIN_SHORT_I = -32768;
   private static final int MAX_SHORT_I = 32767;
   protected static final JacksonFeatureSet<StreamReadCapability> DEFAULT_READ_CAPABILITIES = JacksonFeatureSet.<StreamReadCapability>fromDefaults(StreamReadCapability.values());
   protected int _features;
   protected transient RequestPayload _requestPayload;

   protected JsonParser() {
      this._features = JsonFactory.DEFAULT_PARSER_FEATURE_FLAGS;
   }

   protected JsonParser(int features) {
      this._features = features;
   }

   public abstract ObjectCodec getCodec();

   public abstract void setCodec(ObjectCodec var1);

   public Object getInputSource() {
      return null;
   }

   public void setRequestPayloadOnError(RequestPayload payload) {
      this._requestPayload = payload;
   }

   public void setRequestPayloadOnError(byte[] payload, String charset) {
      this._requestPayload = payload == null ? null : new RequestPayload(payload, charset);
   }

   public void setRequestPayloadOnError(String payload) {
      this._requestPayload = payload == null ? null : new RequestPayload(payload);
   }

   public StreamReadConstraints streamReadConstraints() {
      return StreamReadConstraints.defaults();
   }

   public void setSchema(FormatSchema schema) {
      throw new UnsupportedOperationException("Parser of type " + this.getClass().getName() + " does not support schema of type '" + schema.getSchemaType() + "'");
   }

   public FormatSchema getSchema() {
      return null;
   }

   public boolean canUseSchema(FormatSchema schema) {
      return false;
   }

   public boolean requiresCustomCodec() {
      return false;
   }

   public boolean canParseAsync() {
      return false;
   }

   public NonBlockingInputFeeder getNonBlockingInputFeeder() {
      return null;
   }

   public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
      return DEFAULT_READ_CAPABILITIES;
   }

   public abstract Version version();

   public abstract void close() throws IOException;

   public abstract boolean isClosed();

   public abstract JsonStreamContext getParsingContext();

   public JsonLocation currentLocation() {
      return this.getCurrentLocation();
   }

   public JsonLocation currentTokenLocation() {
      return this.getTokenLocation();
   }

   public long currentTokenCount() {
      return -1L;
   }

   /** @deprecated */
   @Deprecated
   public abstract JsonLocation getCurrentLocation();

   /** @deprecated */
   @Deprecated
   public abstract JsonLocation getTokenLocation();

   public Object currentValue() {
      JsonStreamContext ctxt = this.getParsingContext();
      return ctxt == null ? null : ctxt.getCurrentValue();
   }

   /** @deprecated */
   @Deprecated
   public Object getCurrentValue() {
      return this.currentValue();
   }

   public void assignCurrentValue(Object v) {
      JsonStreamContext ctxt = this.getParsingContext();
      if (ctxt != null) {
         ctxt.setCurrentValue(v);
      }

   }

   /** @deprecated */
   @Deprecated
   public void setCurrentValue(Object v) {
      this.assignCurrentValue(v);
   }

   public int releaseBuffered(OutputStream out) throws IOException {
      return -1;
   }

   public int releaseBuffered(Writer w) throws IOException {
      return -1;
   }

   public JsonParser enable(Feature f) {
      this._features |= f.getMask();
      return this;
   }

   public JsonParser disable(Feature f) {
      this._features &= ~f.getMask();
      return this;
   }

   public JsonParser configure(Feature f, boolean state) {
      if (state) {
         this.enable(f);
      } else {
         this.disable(f);
      }

      return this;
   }

   public boolean isEnabled(Feature f) {
      return f.enabledIn(this._features);
   }

   public boolean isEnabled(StreamReadFeature f) {
      return f.mappedFeature().enabledIn(this._features);
   }

   public int getFeatureMask() {
      return this._features;
   }

   /** @deprecated */
   @Deprecated
   public JsonParser setFeatureMask(int mask) {
      this._features = mask;
      return this;
   }

   public JsonParser overrideStdFeatures(int values, int mask) {
      int newState = this._features & ~mask | values & mask;
      return this.setFeatureMask(newState);
   }

   public int getFormatFeatures() {
      return 0;
   }

   public JsonParser overrideFormatFeatures(int values, int mask) {
      return this;
   }

   public abstract JsonToken nextToken() throws IOException;

   public abstract JsonToken nextValue() throws IOException;

   public boolean nextFieldName(SerializableString str) throws IOException {
      return this.nextToken() == JsonToken.FIELD_NAME && str.getValue().equals(this.currentName());
   }

   public String nextFieldName() throws IOException {
      return this.nextToken() == JsonToken.FIELD_NAME ? this.currentName() : null;
   }

   public String nextTextValue() throws IOException {
      return this.nextToken() == JsonToken.VALUE_STRING ? this.getText() : null;
   }

   public int nextIntValue(int defaultValue) throws IOException {
      return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getIntValue() : defaultValue;
   }

   public long nextLongValue(long defaultValue) throws IOException {
      return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getLongValue() : defaultValue;
   }

   public Boolean nextBooleanValue() throws IOException {
      JsonToken t = this.nextToken();
      if (t == JsonToken.VALUE_TRUE) {
         return Boolean.TRUE;
      } else {
         return t == JsonToken.VALUE_FALSE ? Boolean.FALSE : null;
      }
   }

   public abstract JsonParser skipChildren() throws IOException;

   public void finishToken() throws IOException {
   }

   public JsonToken currentToken() {
      return this.getCurrentToken();
   }

   public int currentTokenId() {
      return this.getCurrentTokenId();
   }

   public abstract JsonToken getCurrentToken();

   /** @deprecated */
   @Deprecated
   public abstract int getCurrentTokenId();

   public abstract boolean hasCurrentToken();

   public abstract boolean hasTokenId(int var1);

   public abstract boolean hasToken(JsonToken var1);

   public boolean isExpectedStartArrayToken() {
      return this.currentToken() == JsonToken.START_ARRAY;
   }

   public boolean isExpectedStartObjectToken() {
      return this.currentToken() == JsonToken.START_OBJECT;
   }

   public boolean isExpectedNumberIntToken() {
      return this.currentToken() == JsonToken.VALUE_NUMBER_INT;
   }

   public boolean isNaN() throws IOException {
      return false;
   }

   public abstract void clearCurrentToken();

   public abstract JsonToken getLastClearedToken();

   public abstract void overrideCurrentName(String var1);

   /** @deprecated */
   @Deprecated
   public abstract String getCurrentName() throws IOException;

   public String currentName() throws IOException {
      return this.getCurrentName();
   }

   public abstract String getText() throws IOException;

   public int getText(Writer writer) throws IOException, UnsupportedOperationException {
      String str = this.getText();
      if (str == null) {
         return 0;
      } else {
         writer.write(str);
         return str.length();
      }
   }

   public abstract char[] getTextCharacters() throws IOException;

   public abstract int getTextLength() throws IOException;

   public abstract int getTextOffset() throws IOException;

   public abstract boolean hasTextCharacters();

   public abstract Number getNumberValue() throws IOException;

   public Number getNumberValueExact() throws IOException {
      return this.getNumberValue();
   }

   public Object getNumberValueDeferred() throws IOException {
      return this.getNumberValue();
   }

   public abstract NumberType getNumberType() throws IOException;

   public NumberTypeFP getNumberTypeFP() throws IOException {
      NumberType nt = this.getNumberType();
      if (nt == JsonParser.NumberType.BIG_DECIMAL) {
         return JsonParser.NumberTypeFP.BIG_DECIMAL;
      } else if (nt == JsonParser.NumberType.DOUBLE) {
         return JsonParser.NumberTypeFP.DOUBLE64;
      } else {
         return nt == JsonParser.NumberType.FLOAT ? JsonParser.NumberTypeFP.FLOAT32 : JsonParser.NumberTypeFP.UNKNOWN;
      }
   }

   public byte getByteValue() throws IOException {
      int value = this.getIntValue();
      if (value >= -128 && value <= 255) {
         return (byte)value;
      } else {
         throw new InputCoercionException(this, String.format("Numeric value (%s) out of range of Java byte", this.getText()), JsonToken.VALUE_NUMBER_INT, Byte.TYPE);
      }
   }

   public short getShortValue() throws IOException {
      int value = this.getIntValue();
      if (value >= -32768 && value <= 32767) {
         return (short)value;
      } else {
         throw new InputCoercionException(this, String.format("Numeric value (%s) out of range of Java short", this.getText()), JsonToken.VALUE_NUMBER_INT, Short.TYPE);
      }
   }

   public abstract int getIntValue() throws IOException;

   public abstract long getLongValue() throws IOException;

   public abstract BigInteger getBigIntegerValue() throws IOException;

   public abstract float getFloatValue() throws IOException;

   public abstract double getDoubleValue() throws IOException;

   public abstract BigDecimal getDecimalValue() throws IOException;

   public boolean getBooleanValue() throws IOException {
      JsonToken t = this.currentToken();
      if (t == JsonToken.VALUE_TRUE) {
         return true;
      } else if (t == JsonToken.VALUE_FALSE) {
         return false;
      } else {
         throw (new JsonParseException(this, String.format("Current token (%s) not of boolean type", t))).withRequestPayload(this._requestPayload);
      }
   }

   public Object getEmbeddedObject() throws IOException {
      return null;
   }

   public abstract byte[] getBinaryValue(Base64Variant var1) throws IOException;

   public byte[] getBinaryValue() throws IOException {
      return this.getBinaryValue(Base64Variants.getDefaultVariant());
   }

   public int readBinaryValue(OutputStream out) throws IOException {
      return this.readBinaryValue(Base64Variants.getDefaultVariant(), out);
   }

   public int readBinaryValue(Base64Variant bv, OutputStream out) throws IOException {
      this._reportUnsupportedOperation();
      return 0;
   }

   public int getValueAsInt() throws IOException {
      return this.getValueAsInt(0);
   }

   public int getValueAsInt(int def) throws IOException {
      return def;
   }

   public long getValueAsLong() throws IOException {
      return this.getValueAsLong(0L);
   }

   public long getValueAsLong(long def) throws IOException {
      return def;
   }

   public double getValueAsDouble() throws IOException {
      return this.getValueAsDouble((double)0.0F);
   }

   public double getValueAsDouble(double def) throws IOException {
      return def;
   }

   public boolean getValueAsBoolean() throws IOException {
      return this.getValueAsBoolean(false);
   }

   public boolean getValueAsBoolean(boolean def) throws IOException {
      return def;
   }

   public String getValueAsString() throws IOException {
      return this.getValueAsString((String)null);
   }

   public abstract String getValueAsString(String var1) throws IOException;

   public boolean canReadObjectId() {
      return false;
   }

   public boolean canReadTypeId() {
      return false;
   }

   public Object getObjectId() throws IOException {
      return null;
   }

   public Object getTypeId() throws IOException {
      return null;
   }

   public <T> T readValueAs(Class<T> valueType) throws IOException {
      return (T)this._codec().readValue(this, valueType);
   }

   public <T> T readValueAs(TypeReference<?> valueTypeRef) throws IOException {
      return (T)this._codec().readValue(this, valueTypeRef);
   }

   public <T> Iterator<T> readValuesAs(Class<T> valueType) throws IOException {
      return this._codec().readValues(this, valueType);
   }

   public <T> Iterator<T> readValuesAs(TypeReference<T> valueTypeRef) throws IOException {
      return this._codec().readValues(this, valueTypeRef);
   }

   public <T extends TreeNode> T readValueAsTree() throws IOException {
      return (T)this._codec().readTree(this);
   }

   protected ObjectCodec _codec() {
      ObjectCodec c = this.getCodec();
      if (c == null) {
         throw new IllegalStateException("No ObjectCodec defined for parser, needed for deserialization");
      } else {
         return c;
      }
   }

   protected JsonParseException _constructError(String msg) {
      return (new JsonParseException(this, msg)).withRequestPayload(this._requestPayload);
   }

   protected void _reportUnsupportedOperation() {
      throw new UnsupportedOperationException("Operation not supported by parser of type " + this.getClass().getName());
   }

   protected JsonParseException _constructReadException(String msg) {
      return this._constructError(msg);
   }

   protected JsonParseException _constructReadException(String msg, Object arg) {
      return this._constructReadException(String.format(msg, arg));
   }

   protected JsonParseException _constructReadException(String msg, Object arg1, Object arg2) {
      return this._constructReadException(String.format(msg, arg1, arg2));
   }

   protected JsonParseException _constructReadException(String msg, Object arg1, Object arg2, Object arg3) {
      return this._constructReadException(String.format(msg, arg1, arg2, arg3));
   }

   protected JsonParseException _constructReadException(String msg, Throwable t) {
      JsonParseException e = new JsonParseException(this, msg, t);
      if (this._requestPayload != null) {
         e = e.withRequestPayload(this._requestPayload);
      }

      return e;
   }

   protected JsonParseException _constructReadException(String msg, JsonLocation loc) {
      JsonParseException e = new JsonParseException(this, msg, loc);
      if (this._requestPayload != null) {
         e = e.withRequestPayload(this._requestPayload);
      }

      return e;
   }

   public static enum NumberType {
      INT,
      LONG,
      BIG_INTEGER,
      FLOAT,
      DOUBLE,
      BIG_DECIMAL;
   }

   public static enum NumberTypeFP {
      FLOAT16,
      FLOAT32,
      DOUBLE64,
      BIG_DECIMAL,
      UNKNOWN;
   }

   public static enum Feature {
      AUTO_CLOSE_SOURCE(true),
      ALLOW_COMMENTS(false),
      ALLOW_YAML_COMMENTS(false),
      ALLOW_UNQUOTED_FIELD_NAMES(false),
      ALLOW_SINGLE_QUOTES(false),
      /** @deprecated */
      @Deprecated
      ALLOW_UNQUOTED_CONTROL_CHARS(false),
      /** @deprecated */
      @Deprecated
      ALLOW_RS_CONTROL_CHAR(false),
      /** @deprecated */
      @Deprecated
      ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER(false),
      /** @deprecated */
      @Deprecated
      ALLOW_NUMERIC_LEADING_ZEROS(false),
      /** @deprecated */
      @Deprecated
      ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS(false),
      /** @deprecated */
      @Deprecated
      ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS(false),
      /** @deprecated */
      @Deprecated
      ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS(false),
      /** @deprecated */
      @Deprecated
      ALLOW_NON_NUMERIC_NUMBERS(false),
      /** @deprecated */
      @Deprecated
      ALLOW_MISSING_VALUES(false),
      /** @deprecated */
      @Deprecated
      ALLOW_TRAILING_COMMA(false),
      STRICT_DUPLICATE_DETECTION(false),
      IGNORE_UNDEFINED(false),
      INCLUDE_SOURCE_IN_LOCATION(false),
      USE_FAST_DOUBLE_PARSER(false),
      USE_FAST_BIG_NUMBER_PARSER(false),
      CLEAR_CURRENT_TOKEN_ON_CLOSE(true);

      private final boolean _defaultState;
      private final int _mask = 1 << this.ordinal();

      public static int collectDefaults() {
         int flags = 0;

         for(Feature f : values()) {
            if (f.enabledByDefault()) {
               flags |= f.getMask();
            }
         }

         return flags;
      }

      private Feature(boolean defaultState) {
         this._defaultState = defaultState;
      }

      public boolean enabledByDefault() {
         return this._defaultState;
      }

      public boolean enabledIn(int flags) {
         return (flags & this._mask) != 0;
      }

      public int getMask() {
         return this._mask;
      }
   }
}
