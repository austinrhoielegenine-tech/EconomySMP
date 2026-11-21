package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

public abstract class ParserBase extends ParserMinimalBase {
   protected static final JacksonFeatureSet<StreamReadCapability> JSON_READ_CAPABILITIES;
   protected final IOContext _ioContext;
   protected boolean _closed;
   protected int _inputPtr;
   protected int _inputEnd;
   protected long _currInputProcessed;
   protected int _currInputRow = 1;
   protected int _currInputRowStart;
   protected long _tokenInputTotal;
   protected int _tokenInputRow = 1;
   protected int _tokenInputCol;
   protected JsonReadContext _parsingContext;
   protected JsonToken _nextToken;
   protected final TextBuffer _textBuffer;
   protected char[] _nameCopyBuffer;
   protected boolean _nameCopied;
   protected ByteArrayBuilder _byteArrayBuilder;
   protected byte[] _binaryValue;
   protected int _numTypesValid = 0;
   protected int _numberInt;
   protected long _numberLong;
   protected float _numberFloat;
   protected double _numberDouble;
   protected BigInteger _numberBigInt;
   protected BigDecimal _numberBigDecimal;
   protected String _numberString;
   protected boolean _numberIsNaN;
   protected boolean _numberNegative;
   protected int _intLength;
   protected int _fractLength;
   protected int _expLength;

   protected ParserBase(IOContext ctxt, int features) {
      super(features, ctxt.streamReadConstraints());
      this._ioContext = ctxt;
      this._textBuffer = ctxt.constructReadConstrainedTextBuffer();
      DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector((JsonParser)this) : null;
      this._parsingContext = JsonReadContext.createRootContext(dups);
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public Object currentValue() {
      return this._parsingContext.getCurrentValue();
   }

   public void assignCurrentValue(Object v) {
      this._parsingContext.setCurrentValue(v);
   }

   public JsonParser enable(JsonParser.Feature f) {
      this._features |= f.getMask();
      if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION && this._parsingContext.getDupDetector() == null) {
         this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector((JsonParser)this));
      }

      return this;
   }

   public JsonParser disable(JsonParser.Feature f) {
      this._features &= ~f.getMask();
      if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
         this._parsingContext = this._parsingContext.withDupDetector((DupDetector)null);
      }

      return this;
   }

   /** @deprecated */
   @Deprecated
   public JsonParser setFeatureMask(int newMask) {
      int changes = this._features ^ newMask;
      if (changes != 0) {
         this._features = newMask;
         this._checkStdFeatureChanges(newMask, changes);
      }

      return this;
   }

   public JsonParser overrideStdFeatures(int values, int mask) {
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
      int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
      if ((changedFeatures & f) != 0 && (newFeatureFlags & f) != 0) {
         if (this._parsingContext.getDupDetector() == null) {
            this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector((JsonParser)this));
         } else {
            this._parsingContext = this._parsingContext.withDupDetector((DupDetector)null);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public String getCurrentName() throws IOException {
      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
         JsonReadContext parent = this._parsingContext.getParent();
         if (parent != null) {
            return parent.getCurrentName();
         }
      }

      return this._parsingContext.getCurrentName();
   }

   public void overrideCurrentName(String name) {
      JsonReadContext ctxt = this._parsingContext;
      if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
         ctxt = ctxt.getParent();
      }

      try {
         ctxt.setCurrentName(name);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   public void close() throws IOException {
      if (!this._closed) {
         this._inputPtr = Math.max(this._inputPtr, this._inputEnd);
         this._closed = true;
         if (JsonParser.Feature.CLEAR_CURRENT_TOKEN_ON_CLOSE.enabledIn(this._features)) {
            this._currToken = null;
         }

         try {
            this._closeInput();
         } finally {
            this._releaseBuffers();
            this._ioContext.close();
         }
      }

   }

   public boolean isClosed() {
      return this._closed;
   }

   public JsonReadContext getParsingContext() {
      return this._parsingContext;
   }

   /** @deprecated */
   @Deprecated
   public JsonLocation getTokenLocation() {
      return new JsonLocation(this._contentReference(), -1L, this.getTokenCharacterOffset(), this.getTokenLineNr(), this.getTokenColumnNr());
   }

   /** @deprecated */
   @Deprecated
   public JsonLocation getCurrentLocation() {
      int col = this._inputPtr - this._currInputRowStart + 1;
      return new JsonLocation(this._contentReference(), -1L, this._currInputProcessed + (long)this._inputPtr, this._currInputRow, col);
   }

   public boolean hasTextCharacters() {
      if (this._currToken == JsonToken.VALUE_STRING) {
         return true;
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this._nameCopied : false;
      }
   }

   public byte[] getBinaryValue(Base64Variant variant) throws IOException {
      if (this._binaryValue == null) {
         if (this._currToken != JsonToken.VALUE_STRING) {
            this._reportError("Current token (" + this._currToken + ") not VALUE_STRING, can not access as binary");
         }

         ByteArrayBuilder builder = this._getByteArrayBuilder();
         this._decodeBase64(this.getText(), builder, variant);
         this._binaryValue = builder.toByteArray();
      }

      return this._binaryValue;
   }

   public long getTokenCharacterOffset() {
      return this._tokenInputTotal;
   }

   public int getTokenLineNr() {
      return this._tokenInputRow;
   }

   public int getTokenColumnNr() {
      int col = this._tokenInputCol;
      return col < 0 ? col : col + 1;
   }

   protected abstract void _closeInput() throws IOException;

   protected void _releaseBuffers() throws IOException {
      this._textBuffer.releaseBuffers();
      char[] buf = this._nameCopyBuffer;
      if (buf != null) {
         this._nameCopyBuffer = null;
         this._ioContext.releaseNameCopyBuffer(buf);
      }

   }

   protected void _handleEOF() throws JsonParseException {
      if (!this._parsingContext.inRoot()) {
         String marker = this._parsingContext.inArray() ? "Array" : "Object";
         this._reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", marker, this._parsingContext.startLocation(this._contentReference())), (JsonToken)null);
      }

   }

   protected final int _eofAsNextChar() throws JsonParseException {
      this._handleEOF();
      return -1;
   }

   public ByteArrayBuilder _getByteArrayBuilder() {
      if (this._byteArrayBuilder == null) {
         this._byteArrayBuilder = new ByteArrayBuilder();
      } else {
         this._byteArrayBuilder.reset();
      }

      return this._byteArrayBuilder;
   }

   protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) throws IOException {
      return fractLen < 1 && expLen < 1 ? this.resetInt(negative, intLen) : this.resetFloat(negative, intLen, fractLen, expLen);
   }

   protected final JsonToken resetInt(boolean negative, int intLen) throws IOException {
      this._streamReadConstraints.validateIntegerLength(intLen);
      this._numberNegative = negative;
      this._numberIsNaN = false;
      this._intLength = intLen;
      this._fractLength = 0;
      this._expLength = 0;
      this._numTypesValid = 0;
      this._numberString = null;
      return JsonToken.VALUE_NUMBER_INT;
   }

   protected final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) throws IOException {
      this._streamReadConstraints.validateFPLength(intLen + fractLen + expLen);
      this._numberNegative = negative;
      this._numberIsNaN = false;
      this._intLength = intLen;
      this._fractLength = fractLen;
      this._expLength = expLen;
      this._numTypesValid = 0;
      this._numberString = null;
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   protected final JsonToken resetAsNaN(String valueStr, double value) throws IOException {
      this._textBuffer.resetWithString(valueStr);
      this._numberDouble = value;
      this._numTypesValid = 8;
      this._numberIsNaN = true;
      this._numberString = null;
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   public boolean isNaN() throws IOException {
      return this._currToken == JsonToken.VALUE_NUMBER_FLOAT && this._numberIsNaN;
   }

   public Number getNumberValue() throws IOException {
      if (this._numTypesValid == 0) {
         this._parseNumericValue(0);
      }

      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         if ((this._numTypesValid & 1) != 0) {
            return this._numberInt;
         }

         if ((this._numTypesValid & 2) != 0) {
            return this._numberLong;
         }

         if ((this._numTypesValid & 4) != 0) {
            return this._getBigInteger();
         }

         this._throwInternal();
      }

      if ((this._numTypesValid & 16) != 0) {
         return this._getBigDecimal();
      } else if ((this._numTypesValid & 32) != 0) {
         return this._getNumberFloat();
      } else {
         if ((this._numTypesValid & 8) == 0) {
            this._throwInternal();
         }

         return this._getNumberDouble();
      }
   }

   public Number getNumberValueExact() throws IOException {
      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(0);
         }

         if ((this._numTypesValid & 1) != 0) {
            return this._numberInt;
         }

         if ((this._numTypesValid & 2) != 0) {
            return this._numberLong;
         }

         if ((this._numTypesValid & 4) != 0) {
            return this._getBigInteger();
         }

         this._throwInternal();
      }

      if (this._numTypesValid == 0) {
         this._parseNumericValue(16);
      }

      if ((this._numTypesValid & 16) != 0) {
         return this._getBigDecimal();
      } else if ((this._numTypesValid & 32) != 0) {
         return this._getNumberFloat();
      } else {
         if ((this._numTypesValid & 8) == 0) {
            this._throwInternal();
         }

         return this._getNumberDouble();
      }
   }

   public Object getNumberValueDeferred() throws IOException {
      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(0);
         }

         if ((this._numTypesValid & 1) != 0) {
            return this._numberInt;
         }

         if ((this._numTypesValid & 2) != 0) {
            return this._numberLong;
         }

         if ((this._numTypesValid & 4) != 0) {
            if (this._numberBigInt != null) {
               return this._numberBigInt;
            }

            if (this._numberString != null) {
               return this._numberString;
            }

            return this._getBigInteger();
         }

         this._throwInternal();
      }

      if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
         if ((this._numTypesValid & 16) != 0) {
            return this._getBigDecimal();
         } else if ((this._numTypesValid & 8) != 0) {
            return this._getNumberDouble();
         } else {
            return (this._numTypesValid & 32) != 0 ? this._getNumberFloat() : this._textBuffer.contentsAsString();
         }
      } else {
         return this.getNumberValue();
      }
   }

   public JsonParser.NumberType getNumberType() throws IOException {
      if (this._numTypesValid == 0) {
         this._parseNumericValue(0);
      }

      if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
         if ((this._numTypesValid & 1) != 0) {
            return JsonParser.NumberType.INT;
         } else {
            return (this._numTypesValid & 2) != 0 ? JsonParser.NumberType.LONG : JsonParser.NumberType.BIG_INTEGER;
         }
      } else if ((this._numTypesValid & 16) != 0) {
         return JsonParser.NumberType.BIG_DECIMAL;
      } else {
         return (this._numTypesValid & 32) != 0 ? JsonParser.NumberType.FLOAT : JsonParser.NumberType.DOUBLE;
      }
   }

   public int getIntValue() throws IOException {
      if ((this._numTypesValid & 1) == 0) {
         if (this._numTypesValid == 0) {
            return this._parseIntValue();
         }

         if ((this._numTypesValid & 1) == 0) {
            this.convertNumberToInt();
         }
      }

      return this._numberInt;
   }

   public long getLongValue() throws IOException {
      if ((this._numTypesValid & 2) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(2);
         }

         if ((this._numTypesValid & 2) == 0) {
            this.convertNumberToLong();
         }
      }

      return this._numberLong;
   }

   public BigInteger getBigIntegerValue() throws IOException {
      if ((this._numTypesValid & 4) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(4);
         }

         if ((this._numTypesValid & 4) == 0) {
            this.convertNumberToBigInteger();
            return this._numberBigInt;
         }
      }

      return this._getBigInteger();
   }

   public float getFloatValue() throws IOException {
      if ((this._numTypesValid & 32) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(32);
         }

         if ((this._numTypesValid & 32) == 0) {
            this.convertNumberToFloat();
            return this._numberFloat;
         }
      }

      return this._getNumberFloat();
   }

   public double getDoubleValue() throws IOException {
      if ((this._numTypesValid & 8) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(8);
         }

         if ((this._numTypesValid & 8) == 0) {
            this.convertNumberToDouble();
            return this._numberDouble;
         }
      }

      return this._getNumberDouble();
   }

   public BigDecimal getDecimalValue() throws IOException {
      if ((this._numTypesValid & 16) == 0) {
         if (this._numTypesValid == 0) {
            this._parseNumericValue(16);
         }

         if ((this._numTypesValid & 16) == 0) {
            this.convertNumberToBigDecimal();
            return this._numberBigDecimal;
         }
      }

      return this._getBigDecimal();
   }

   protected void _parseNumericValue(int expType) throws IOException {
      if (!this._closed && this._currToken == JsonToken.VALUE_NUMBER_INT) {
         int len = this._intLength;
         if (len <= 9) {
            this._numberInt = this._textBuffer.contentsAsInt(this._numberNegative);
            this._numTypesValid = 1;
         } else if (len <= 18) {
            long l = this._textBuffer.contentsAsLong(this._numberNegative);
            if (len == 10) {
               if (this._numberNegative) {
                  if (l >= -2147483648L) {
                     this._numberInt = (int)l;
                     this._numTypesValid = 1;
                     return;
                  }
               } else if (l <= 2147483647L) {
                  this._numberInt = (int)l;
                  this._numTypesValid = 1;
                  return;
               }
            }

            this._numberLong = l;
            this._numTypesValid = 2;
         } else {
            if (len == 19) {
               char[] buf = this._textBuffer.getTextBuffer();
               int offset = this._textBuffer.getTextOffset();
               if (this._numberNegative) {
                  ++offset;
               }

               if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
                  this._numberLong = NumberInput.parseLong19(buf, offset, this._numberNegative);
                  this._numTypesValid = 2;
                  return;
               }
            }

            this._parseSlowInt(expType);
         }
      } else if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
         this._parseSlowFloat(expType);
      } else {
         this._reportError("Current token (%s) not numeric, can not use numeric value accessors", this._currToken);
      }
   }

   protected int _parseIntValue() throws IOException {
      if (this._closed) {
         this._reportError("Internal error: _parseNumericValue called when parser instance closed");
      }

      if (this._currToken == JsonToken.VALUE_NUMBER_INT && this._intLength <= 9) {
         int i = this._textBuffer.contentsAsInt(this._numberNegative);
         this._numberInt = i;
         this._numTypesValid = 1;
         return i;
      } else {
         this._parseNumericValue(1);
         if ((this._numTypesValid & 1) == 0) {
            this.convertNumberToInt();
         }

         return this._numberInt;
      }
   }

   private void _parseSlowFloat(int expType) throws IOException {
      if (expType == 16) {
         this._numberBigDecimal = this._textBuffer.contentsAsDecimal(this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
         this._numTypesValid = 16;
      } else if (expType == 8) {
         this._numberDouble = this._textBuffer.contentsAsDouble(this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
         this._numTypesValid = 8;
      } else if (expType == 32) {
         this._numberFloat = this._textBuffer.contentsAsFloat(this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
         this._numTypesValid = 32;
      } else {
         this._numberDouble = (double)0.0F;
         this._numberString = this._textBuffer.contentsAsString();
         this._numTypesValid = 8;
      }

   }

   private void _parseSlowInt(int expType) throws IOException {
      String numStr = this._textBuffer.contentsAsString();
      if (expType == 1 || expType == 2) {
         this._reportTooLongIntegral(expType, numStr);
      }

      if (expType != 8 && expType != 32) {
         this._numberBigInt = null;
         this._numberString = numStr;
         this._numTypesValid = 4;
      } else {
         this._numberString = numStr;
         this._numTypesValid = 8;
      }

   }

   protected void _reportTooLongIntegral(int expType, String rawNum) throws IOException {
      if (expType == 1) {
         this.reportOverflowInt(rawNum);
      } else {
         this.reportOverflowLong(rawNum);
      }

   }

   protected void convertNumberToInt() throws IOException {
      if ((this._numTypesValid & 2) != 0) {
         int result = (int)this._numberLong;
         if ((long)result != this._numberLong) {
            this.reportOverflowInt(this.getText(), this.currentToken());
         }

         this._numberInt = result;
      } else if ((this._numTypesValid & 4) != 0) {
         BigInteger bigInteger = this._getBigInteger();
         if (BI_MIN_INT.compareTo(bigInteger) > 0 || BI_MAX_INT.compareTo(bigInteger) < 0) {
            this.reportOverflowInt();
         }

         this._numberInt = bigInteger.intValue();
      } else if ((this._numTypesValid & 8) != 0) {
         double d = this._getNumberDouble();
         if (d < (double)Integer.MIN_VALUE || d > (double)Integer.MAX_VALUE) {
            this.reportOverflowInt();
         }

         this._numberInt = (int)d;
      } else if ((this._numTypesValid & 16) != 0) {
         BigDecimal bigDecimal = this._getBigDecimal();
         if (BD_MIN_INT.compareTo(bigDecimal) > 0 || BD_MAX_INT.compareTo(bigDecimal) < 0) {
            this.reportOverflowInt();
         }

         this._numberInt = bigDecimal.intValue();
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 1;
   }

   protected void convertNumberToLong() throws IOException {
      if ((this._numTypesValid & 1) != 0) {
         this._numberLong = (long)this._numberInt;
      } else if ((this._numTypesValid & 4) != 0) {
         BigInteger bigInteger = this._getBigInteger();
         if (BI_MIN_LONG.compareTo(bigInteger) > 0 || BI_MAX_LONG.compareTo(bigInteger) < 0) {
            this.reportOverflowLong();
         }

         this._numberLong = bigInteger.longValue();
      } else if ((this._numTypesValid & 8) != 0) {
         double d = this._getNumberDouble();
         if (d < (double)Long.MIN_VALUE || d > (double)Long.MAX_VALUE) {
            this.reportOverflowLong();
         }

         this._numberLong = (long)d;
      } else if ((this._numTypesValid & 16) != 0) {
         BigDecimal bigDecimal = this._getBigDecimal();
         if (BD_MIN_LONG.compareTo(bigDecimal) > 0 || BD_MAX_LONG.compareTo(bigDecimal) < 0) {
            this.reportOverflowLong();
         }

         this._numberLong = bigDecimal.longValue();
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 2;
   }

   protected void convertNumberToBigInteger() throws IOException {
      if ((this._numTypesValid & 16) != 0) {
         this._numberBigInt = this._convertBigDecimalToBigInteger(this._getBigDecimal());
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberBigInt = BigInteger.valueOf(this._numberLong);
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberBigInt = BigInteger.valueOf((long)this._numberInt);
      } else if ((this._numTypesValid & 8) != 0) {
         if (this._numberString != null) {
            this._numberBigInt = this._convertBigDecimalToBigInteger(this._getBigDecimal());
         } else {
            this._numberBigInt = this._convertBigDecimalToBigInteger(BigDecimal.valueOf(this._getNumberDouble()));
         }
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 4;
   }

   protected void convertNumberToDouble() throws IOException {
      if ((this._numTypesValid & 16) != 0) {
         if (this._numberString != null) {
            this._numberDouble = this._getNumberDouble();
         } else {
            this._numberDouble = this._getBigDecimal().doubleValue();
         }
      } else if ((this._numTypesValid & 4) != 0) {
         if (this._numberString != null) {
            this._numberDouble = this._getNumberDouble();
         } else {
            this._numberDouble = this._getBigInteger().doubleValue();
         }
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberDouble = (double)this._numberLong;
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberDouble = (double)this._numberInt;
      } else if ((this._numTypesValid & 32) != 0) {
         if (this._numberString != null) {
            this._numberDouble = this._getNumberDouble();
         } else {
            this._numberDouble = (double)this._getNumberFloat();
         }
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 8;
   }

   protected void convertNumberToFloat() throws IOException {
      if ((this._numTypesValid & 16) != 0) {
         if (this._numberString != null) {
            this._numberFloat = this._getNumberFloat();
         } else {
            this._numberFloat = this._getBigDecimal().floatValue();
         }
      } else if ((this._numTypesValid & 4) != 0) {
         if (this._numberString != null) {
            this._numberFloat = this._getNumberFloat();
         } else {
            this._numberFloat = this._getBigInteger().floatValue();
         }
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberFloat = (float)this._numberLong;
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberFloat = (float)this._numberInt;
      } else if ((this._numTypesValid & 8) != 0) {
         if (this._numberString != null) {
            this._numberFloat = this._getNumberFloat();
         } else {
            this._numberFloat = (float)this._getNumberDouble();
         }
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 32;
   }

   protected void convertNumberToBigDecimal() throws IOException {
      if ((this._numTypesValid & 8) != 0) {
         String numStr = this._numberString == null ? this.getText() : this._numberString;
         this._numberBigDecimal = NumberInput.parseBigDecimal(numStr, this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
      } else if ((this._numTypesValid & 4) != 0) {
         this._numberBigDecimal = new BigDecimal(this._getBigInteger());
      } else if ((this._numTypesValid & 2) != 0) {
         this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
      } else if ((this._numTypesValid & 1) != 0) {
         this._numberBigDecimal = BigDecimal.valueOf((long)this._numberInt);
      } else {
         this._throwInternal();
      }

      this._numTypesValid |= 16;
   }

   protected BigInteger _convertBigDecimalToBigInteger(BigDecimal bigDec) throws IOException {
      this._streamReadConstraints.validateBigIntegerScale(bigDec.scale());
      return bigDec.toBigInteger();
   }

   protected BigInteger _getBigInteger() throws JsonParseException {
      if (this._numberBigInt != null) {
         return this._numberBigInt;
      } else if (this._numberString == null) {
         throw new IllegalStateException("cannot get BigInteger from current parser state");
      } else {
         try {
            this._numberBigInt = NumberInput.parseBigInteger(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
         } catch (NumberFormatException nex) {
            this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
         }

         this._numberString = null;
         return this._numberBigInt;
      }
   }

   protected BigDecimal _getBigDecimal() throws JsonParseException {
      if (this._numberBigDecimal != null) {
         return this._numberBigDecimal;
      } else if (this._numberString == null) {
         throw new IllegalStateException("cannot get BigDecimal from current parser state");
      } else {
         try {
            this._numberBigDecimal = NumberInput.parseBigDecimal(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
         } catch (NumberFormatException nex) {
            this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
         }

         this._numberString = null;
         return this._numberBigDecimal;
      }
   }

   protected double _getNumberDouble() throws JsonParseException {
      if (this._numberString != null) {
         try {
            this._numberDouble = NumberInput.parseDouble(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
         } catch (NumberFormatException nex) {
            this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
         }

         this._numberString = null;
      }

      return this._numberDouble;
   }

   protected float _getNumberFloat() throws JsonParseException {
      if (this._numberString != null) {
         try {
            this._numberFloat = NumberInput.parseFloat(this._numberString, this.isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
         } catch (NumberFormatException nex) {
            this._wrapError("Malformed numeric value (" + this._longNumberDesc(this._numberString) + ")", nex);
         }

         this._numberString = null;
      }

      return this._numberFloat;
   }

   protected void createChildArrayContext(int lineNr, int colNr) throws IOException {
      this._parsingContext = this._parsingContext.createChildArrayContext(lineNr, colNr);
      this._streamReadConstraints.validateNestingDepth(this._parsingContext.getNestingDepth());
   }

   protected void createChildObjectContext(int lineNr, int colNr) throws IOException {
      this._parsingContext = this._parsingContext.createChildObjectContext(lineNr, colNr);
      this._streamReadConstraints.validateNestingDepth(this._parsingContext.getNestingDepth());
   }

   protected char _handleUnrecognizedCharacterEscape(char ch) throws JsonProcessingException {
      if (this.isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
         return ch;
      } else if (ch == '\'' && this.isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
         return ch;
      } else {
         throw this._constructReadException("Unrecognized character escape " + _getCharDesc(ch), this._currentLocationMinusOne());
      }
   }

   protected void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
      JsonReadContext ctxt = this.getParsingContext();
      if (ctxt.inRoot()) {
         this._reportExtraEndMarker(actCh);
      } else {
         String msg = String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", (char)actCh, expCh, ctxt.typeDesc(), ctxt.startLocation(this._contentReference()));
         throw this._constructReadException(msg, this._currentLocationMinusOne());
      }
   }

   protected void _reportExtraEndMarker(int actCh) throws JsonParseException {
      String scopeDesc = actCh == 125 ? "Object" : "Array";
      String msg = String.format("Unexpected close marker '%s': no open %s to close", (char)actCh, scopeDesc);
      throw this._constructReadException(msg, this._currentLocationMinusOne());
   }

   protected void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
      if (!this.isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
         char c = (char)i;
         String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
         throw this._constructReadException(msg, this._currentLocationMinusOne());
      }
   }

   protected String _validJsonTokenList() throws IOException {
      return this._validJsonValueList();
   }

   protected String _validJsonValueList() throws IOException {
      return this.isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS) ? "(JSON String, Number (or 'NaN'/'+INF'/'-INF'), Array, Object or token 'null', 'true' or 'false')" : "(JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
   }

   protected char _decodeEscaped() throws IOException {
      throw new UnsupportedOperationException();
   }

   protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws IOException {
      if (ch != 92) {
         throw this.reportInvalidBase64Char(b64variant, ch, index);
      } else {
         int unescaped = this._decodeEscaped();
         if (unescaped <= 32 && index == 0) {
            return -1;
         } else {
            int bits = b64variant.decodeBase64Char(unescaped);
            if (bits < 0 && bits != -2) {
               throw this.reportInvalidBase64Char(b64variant, unescaped, index);
            } else {
               return bits;
            }
         }
      }
   }

   protected final int _decodeBase64Escape(Base64Variant b64variant, char ch, int index) throws IOException {
      if (ch != '\\') {
         throw this.reportInvalidBase64Char(b64variant, ch, index);
      } else {
         char unescaped = this._decodeEscaped();
         if (unescaped <= ' ' && index == 0) {
            return -1;
         } else {
            int bits = b64variant.decodeBase64Char(unescaped);
            if (bits >= 0 || bits == -2 && index >= 2) {
               return bits;
            } else {
               throw this.reportInvalidBase64Char(b64variant, unescaped, index);
            }
         }
      }
   }

   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex) throws IllegalArgumentException {
      return this.reportInvalidBase64Char(b64variant, ch, bindex, (String)null);
   }

   protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch, int bindex, String msg) throws IllegalArgumentException {
      String base;
      if (ch <= 32) {
         base = String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", Integer.toHexString(ch), bindex + 1);
      } else if (b64variant.usesPaddingChar(ch)) {
         base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
      } else if (Character.isDefined(ch) && !Character.isISOControl(ch)) {
         base = "Illegal character '" + (char)ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      } else {
         base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      }

      if (msg != null) {
         base = base + ": " + msg;
      }

      return new IllegalArgumentException(base);
   }

   protected void _handleBase64MissingPadding(Base64Variant b64variant) throws IOException {
      this._reportError(b64variant.missingPaddingMessage());
   }

   /** @deprecated */
   @Deprecated
   protected Object _getSourceReference() {
      return JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features) ? this._ioContext.contentReference().getRawContent() : null;
   }

   protected ContentReference _contentReference() {
      return JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features) ? this._ioContext.contentReference() : this._contentReferenceRedacted();
   }

   protected ContentReference _contentReferenceRedacted() {
      return ContentReference.redacted();
   }

   protected static int[] growArrayBy(int[] arr, int more) throws IllegalArgumentException {
      if (arr == null) {
         return new int[more];
      } else {
         int len = arr.length + more;
         if (len < 0) {
            throw new IllegalArgumentException("Unable to grow array to longer than `Integer.MAX_VALUE`");
         } else {
            return Arrays.copyOf(arr, len);
         }
      }
   }

   protected int[] _growNameDecodeBuffer(int[] arr, int more) throws StreamConstraintsException {
      this._streamReadConstraints.validateNameLength(arr.length << 2);
      return growArrayBy(arr, more);
   }

   /** @deprecated */
   @Deprecated
   protected void loadMoreGuaranteed() throws IOException {
      if (!this.loadMore()) {
         this._reportInvalidEOF();
      }

   }

   /** @deprecated */
   @Deprecated
   protected boolean loadMore() throws IOException {
      return false;
   }

   protected void _finishString() throws IOException {
   }

   static {
      JSON_READ_CAPABILITIES = DEFAULT_READ_CAPABILITIES;
   }
}
