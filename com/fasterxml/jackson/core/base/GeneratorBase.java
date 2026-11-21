package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public abstract class GeneratorBase extends JsonGenerator {
   public static final int SURR1_FIRST = 55296;
   public static final int SURR1_LAST = 56319;
   public static final int SURR2_FIRST = 56320;
   public static final int SURR2_LAST = 57343;
   protected static final int DERIVED_FEATURES_MASK;
   protected static final String WRITE_BINARY = "write a binary value";
   protected static final String WRITE_BOOLEAN = "write a boolean value";
   protected static final String WRITE_NULL = "write a null";
   protected static final String WRITE_NUMBER = "write a number";
   protected static final String WRITE_RAW = "write a raw (unencoded) value";
   protected static final String WRITE_STRING = "write a string";
   protected static final int MAX_BIG_DECIMAL_SCALE = 9999;
   protected ObjectCodec _objectCodec;
   protected int _features;
   protected final IOContext _ioContext;
   protected boolean _cfgNumbersAsStrings;
   protected JsonWriteContext _writeContext;
   protected boolean _closed;

   /** @deprecated */
   @Deprecated
   protected GeneratorBase(int features, ObjectCodec codec) {
      this(features, codec, (IOContext)null);
   }

   protected GeneratorBase(int features, ObjectCodec codec, IOContext ioContext) {
      this._features = features;
      this._objectCodec = codec;
      this._ioContext = ioContext;
      DupDetector dups = JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector((JsonGenerator)this) : null;
      this._writeContext = JsonWriteContext.createRootContext(dups);
      this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
   }

   /** @deprecated */
   @Deprecated
   protected GeneratorBase(int features, ObjectCodec codec, JsonWriteContext ctxt) {
      this(features, codec, (IOContext)null, ctxt);
   }

   protected GeneratorBase(int features, ObjectCodec codec, IOContext ioContext, JsonWriteContext jsonWriteContext) {
      this._features = features;
      this._objectCodec = codec;
      this._ioContext = ioContext;
      this._writeContext = jsonWriteContext;
      this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(features);
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public Object currentValue() {
      return this._writeContext.getCurrentValue();
   }

   public void assignCurrentValue(Object v) {
      if (this._writeContext != null) {
         this._writeContext.setCurrentValue(v);
      }

   }

   public final boolean isEnabled(JsonGenerator.Feature f) {
      return (this._features & f.getMask()) != 0;
   }

   public int getFeatureMask() {
      return this._features;
   }

   public JsonGenerator enable(JsonGenerator.Feature f) {
      int mask = f.getMask();
      this._features |= mask;
      if ((mask & DERIVED_FEATURES_MASK) != 0) {
         if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
            this._cfgNumbersAsStrings = true;
         } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
            this.setHighestNonEscapedChar(127);
         } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION && this._writeContext.getDupDetector() == null) {
            this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector((JsonGenerator)this));
         }
      }

      return this;
   }

   public JsonGenerator disable(JsonGenerator.Feature f) {
      int mask = f.getMask();
      this._features &= ~mask;
      if ((mask & DERIVED_FEATURES_MASK) != 0) {
         if (f == JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS) {
            this._cfgNumbersAsStrings = false;
         } else if (f == JsonGenerator.Feature.ESCAPE_NON_ASCII) {
            this.setHighestNonEscapedChar(0);
         } else if (f == JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION) {
            this._writeContext = this._writeContext.withDupDetector((DupDetector)null);
         }
      }

      return this;
   }

   /** @deprecated */
   @Deprecated
   public JsonGenerator setFeatureMask(int newMask) {
      int changed = newMask ^ this._features;
      this._features = newMask;
      if (changed != 0) {
         this._checkStdFeatureChanges(newMask, changed);
      }

      return this;
   }

   public JsonGenerator overrideStdFeatures(int values, int mask) {
      int oldState = this._features;
      int newState = oldState & ~mask | values & mask;
      int changed = oldState ^ newState;
      if (changed != 0) {
         this._features = newState;
         this._checkStdFeatureChanges(newState, changed);
      }

      return this;
   }

   protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
      if ((changedFeatures & DERIVED_FEATURES_MASK) != 0) {
         this._cfgNumbersAsStrings = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledIn(newFeatureFlags);
         if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(changedFeatures)) {
            if (JsonGenerator.Feature.ESCAPE_NON_ASCII.enabledIn(newFeatureFlags)) {
               this.setHighestNonEscapedChar(127);
            } else {
               this.setHighestNonEscapedChar(0);
            }
         }

         if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(changedFeatures)) {
            if (JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(newFeatureFlags)) {
               if (this._writeContext.getDupDetector() == null) {
                  this._writeContext = this._writeContext.withDupDetector(DupDetector.rootDetector((JsonGenerator)this));
               }
            } else {
               this._writeContext = this._writeContext.withDupDetector((DupDetector)null);
            }
         }

      }
   }

   public JsonGenerator useDefaultPrettyPrinter() {
      return (JsonGenerator)(this.getPrettyPrinter() != null ? this : this.setPrettyPrinter(this._constructDefaultPrettyPrinter()));
   }

   public JsonGenerator setCodec(ObjectCodec oc) {
      this._objectCodec = oc;
      return this;
   }

   public ObjectCodec getCodec() {
      return this._objectCodec;
   }

   public JsonStreamContext getOutputContext() {
      return this._writeContext;
   }

   public IOContext ioContext() {
      return this._ioContext;
   }

   public void writeStartObject(Object forValue) throws IOException {
      this.writeStartObject();
      if (forValue != null) {
         this.assignCurrentValue(forValue);
      }

   }

   public void writeFieldName(SerializableString name) throws IOException {
      this.writeFieldName(name.getValue());
   }

   public void writeString(SerializableString text) throws IOException {
      this.writeString(text.getValue());
   }

   public void writeRawValue(String text) throws IOException {
      this._verifyValueWrite("write raw value");
      this.writeRaw(text);
   }

   public void writeRawValue(String text, int offset, int len) throws IOException {
      this._verifyValueWrite("write raw value");
      this.writeRaw(text, offset, len);
   }

   public void writeRawValue(char[] text, int offset, int len) throws IOException {
      this._verifyValueWrite("write raw value");
      this.writeRaw(text, offset, len);
   }

   public void writeRawValue(SerializableString text) throws IOException {
      this._verifyValueWrite("write raw value");
      this.writeRaw(text);
   }

   public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException {
      this._reportUnsupportedOperation();
      return 0;
   }

   public void writeObject(Object value) throws IOException {
      if (value == null) {
         this.writeNull();
      } else {
         if (this._objectCodec != null) {
            this._objectCodec.writeValue(this, value);
            return;
         }

         this._writeSimpleObject(value);
      }

   }

   public void writeTree(TreeNode rootNode) throws IOException {
      if (rootNode == null) {
         this.writeNull();
      } else {
         if (this._objectCodec == null) {
            throw new IllegalStateException("No ObjectCodec defined");
         }

         this._objectCodec.writeValue(this, rootNode);
      }

   }

   public abstract void flush() throws IOException;

   public void close() throws IOException {
      if (!this._closed) {
         if (this._ioContext != null) {
            this._ioContext.close();
         }

         this._closed = true;
      }

   }

   public boolean isClosed() {
      return this._closed;
   }

   protected abstract void _releaseBuffers();

   protected abstract void _verifyValueWrite(String var1) throws IOException;

   protected PrettyPrinter _constructDefaultPrettyPrinter() {
      return new DefaultPrettyPrinter();
   }

   protected String _asString(BigDecimal value) throws IOException {
      if (!JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._features)) {
         return value.toString();
      } else {
         int scale = value.scale();
         if (scale < -9999 || scale > 9999) {
            this._reportError(String.format("Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]", scale, 9999, 9999));
         }

         return value.toPlainString();
      }
   }

   protected final int _decodeSurrogate(int surr1, int surr2) throws IOException {
      if (surr2 < 56320 || surr2 > 57343) {
         String msg = String.format("Incomplete surrogate pair: first char 0x%04X, second 0x%04X", surr1, surr2);
         this._reportError(msg);
      }

      return (surr1 << 10) + surr2 + -56613888;
   }

   protected void _checkRangeBoundsForByteArray(byte[] data, int offset, int len) throws IOException {
      if (data == null) {
         this._reportError("Invalid `byte[]` argument: `null`");
      }

      int dataLen = data.length;
      int end = offset + len;
      int anyNegs = offset | len | end | dataLen - end;
      if (anyNegs < 0) {
         this._reportError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `byte[]` of length %d", offset, len, dataLen));
      }

   }

   protected void _checkRangeBoundsForCharArray(char[] data, int offset, int len) throws IOException {
      if (data == null) {
         this._reportError("Invalid `char[]` argument: `null`");
      }

      int dataLen = data.length;
      int end = offset + len;
      int anyNegs = offset | len | end | dataLen - end;
      if (anyNegs < 0) {
         this._reportError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `char[]` of length %d", offset, len, dataLen));
      }

   }

   protected void _checkRangeBoundsForString(String data, int offset, int len) throws IOException {
      if (data == null) {
         this._reportError("Invalid `String` argument: `null`");
      }

      int dataLen = data.length();
      int end = offset + len;
      int anyNegs = offset | len | end | dataLen - end;
      if (anyNegs < 0) {
         this._reportError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `String` of length %d", offset, len, dataLen));
      }

   }

   static {
      DERIVED_FEATURES_MASK = JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.getMask() | JsonGenerator.Feature.ESCAPE_NON_ASCII.getMask() | JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION.getMask();
   }
}
