package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class ReaderBasedJsonParser extends JsonParserBase {
   protected Reader _reader;
   protected char[] _inputBuffer;
   protected boolean _bufferRecyclable;
   protected ObjectCodec _objectCodec;
   protected final CharsToNameCanonicalizer _symbols;
   protected final int _hashSeed;
   protected boolean _tokenIncomplete;
   protected long _nameStartOffset;
   protected int _nameStartRow;
   protected int _nameStartCol;

   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable) {
      super(ctxt, features, codec);
      this._reader = r;
      this._inputBuffer = inputBuffer;
      this._inputPtr = start;
      this._inputEnd = end;
      this._currInputRowStart = start;
      this._currInputProcessed = (long)(-start);
      this._symbols = st;
      this._hashSeed = st.hashSeed();
      this._bufferRecyclable = bufferRecyclable;
   }

   public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
      super(ctxt, features, codec);
      this._reader = r;
      this._inputBuffer = ctxt.allocTokenBuffer();
      this._inputPtr = 0;
      this._inputEnd = 0;
      this._symbols = st;
      this._hashSeed = st.hashSeed();
      this._bufferRecyclable = true;
   }

   public int releaseBuffered(Writer w) throws IOException {
      int count = this._inputEnd - this._inputPtr;
      if (count < 1) {
         return 0;
      } else {
         int origPtr = this._inputPtr;
         this._inputPtr += count;
         w.write(this._inputBuffer, origPtr, count);
         return count;
      }
   }

   public Object getInputSource() {
      return this._reader;
   }

   /** @deprecated */
   @Deprecated
   protected char getNextChar(String eofMsg) throws IOException {
      return this.getNextChar(eofMsg, (JsonToken)null);
   }

   protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(eofMsg, forToken);
      }

      return this._inputBuffer[this._inputPtr++];
   }

   protected void _closeInput() throws IOException {
      if (this._reader != null) {
         if (this._ioContext.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
            this._reader.close();
         }

         this._reader = null;
      }

   }

   protected void _releaseBuffers() throws IOException {
      super._releaseBuffers();
      this._symbols.release();
      if (this._bufferRecyclable) {
         char[] buf = this._inputBuffer;
         if (buf != null) {
            this._inputBuffer = null;
            this._ioContext.releaseTokenBuffer(buf);
         }
      }

   }

   protected void _loadMoreGuaranteed() throws IOException {
      if (!this._loadMore()) {
         this._reportInvalidEOF();
      }

   }

   protected boolean _loadMore() throws IOException {
      if (this._reader != null) {
         int bufSize = this._inputEnd;
         this._currInputProcessed += (long)bufSize;
         this._currInputRowStart -= bufSize;
         this._streamReadConstraints.validateDocumentLength(this._currInputProcessed);
         int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
         if (count > 0) {
            this._nameStartOffset -= (long)bufSize;
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
         }

         this._inputPtr = this._inputEnd = 0;
         this._closeInput();
         if (count == 0) {
            throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
         }
      }

      return false;
   }

   public final String getText() throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            this._finishString();
         }

         return this._textBuffer.contentsAsString();
      } else {
         return this._getText2(this._currToken);
      }
   }

   public int getText(Writer writer) throws IOException {
      JsonToken t = this._currToken;
      if (t == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            this._finishString();
         }

         return this._textBuffer.contentsToWriter(writer);
      } else if (t == JsonToken.FIELD_NAME) {
         String n = this._parsingContext.getCurrentName();
         writer.write(n);
         return n.length();
      } else if (t != null) {
         if (t.isNumeric()) {
            return this._textBuffer.contentsToWriter(writer);
         } else {
            char[] ch = t.asCharArray();
            writer.write(ch);
            return ch.length;
         }
      } else {
         return 0;
      }
   }

   public final String getValueAsString() throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            this._finishString();
         }

         return this._textBuffer.contentsAsString();
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this.currentName() : super.getValueAsString((String)null);
      }
   }

   public final String getValueAsString(String defValue) throws IOException {
      if (this._currToken == JsonToken.VALUE_STRING) {
         if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            this._finishString();
         }

         return this._textBuffer.contentsAsString();
      } else {
         return this._currToken == JsonToken.FIELD_NAME ? this.currentName() : super.getValueAsString(defValue);
      }
   }

   protected final String _getText2(JsonToken t) throws IOException {
      if (t == null) {
         return null;
      } else {
         switch (t.id()) {
            case 5:
               return this._parsingContext.getCurrentName();
            case 6:
            case 7:
            case 8:
               return this._textBuffer.contentsAsString();
            default:
               return t.asString();
         }
      }
   }

   public final char[] getTextCharacters() throws IOException {
      if (this._currToken != null) {
         switch (this._currToken.id()) {
            case 5:
               if (!this._nameCopied) {
                  String name = this._parsingContext.getCurrentName();
                  int nameLen = name.length();
                  if (this._nameCopyBuffer == null) {
                     this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
                  } else if (this._nameCopyBuffer.length < nameLen) {
                     this._nameCopyBuffer = new char[nameLen];
                  }

                  name.getChars(0, nameLen, this._nameCopyBuffer, 0);
                  this._nameCopied = true;
               }

               return this._nameCopyBuffer;
            case 6:
               if (this._tokenIncomplete) {
                  this._tokenIncomplete = false;
                  this._finishString();
               }
            case 7:
            case 8:
               return this._textBuffer.getTextBuffer();
            default:
               return this._currToken.asCharArray();
         }
      } else {
         return null;
      }
   }

   public final int getTextLength() throws IOException {
      if (this._currToken != null) {
         switch (this._currToken.id()) {
            case 5:
               return this._parsingContext.getCurrentName().length();
            case 6:
               if (this._tokenIncomplete) {
                  this._tokenIncomplete = false;
                  this._finishString();
               }
            case 7:
            case 8:
               return this._textBuffer.size();
            default:
               return this._currToken.asCharArray().length;
         }
      } else {
         return 0;
      }
   }

   public final int getTextOffset() throws IOException {
      if (this._currToken != null) {
         switch (this._currToken.id()) {
            case 5:
               return 0;
            case 6:
               if (this._tokenIncomplete) {
                  this._tokenIncomplete = false;
                  this._finishString();
               }
            case 7:
            case 8:
               return this._textBuffer.getTextOffset();
         }
      }

      return 0;
   }

   public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
      if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT && this._binaryValue != null) {
         return this._binaryValue;
      } else {
         if (this._currToken != JsonToken.VALUE_STRING) {
            this._reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
         }

         if (this._tokenIncomplete) {
            try {
               this._binaryValue = this._decodeBase64(b64variant);
            } catch (IllegalArgumentException iae) {
               throw this._constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
            }

            this._tokenIncomplete = false;
         } else if (this._binaryValue == null) {
            ByteArrayBuilder builder = this._getByteArrayBuilder();
            this._decodeBase64(this.getText(), builder, b64variant);
            this._binaryValue = builder.toByteArray();
         }

         return this._binaryValue;
      }
   }

   public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
      if (this._tokenIncomplete && this._currToken == JsonToken.VALUE_STRING) {
         byte[] buf = this._ioContext.allocBase64Buffer();

         int var4;
         try {
            var4 = this._readBinary(b64variant, out, buf);
         } finally {
            this._ioContext.releaseBase64Buffer(buf);
         }

         return var4;
      } else {
         byte[] b = this.getBinaryValue(b64variant);
         out.write(b);
         return b.length;
      }
   }

   protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
      int outputPtr = 0;
      int outputEnd = buffer.length - 3;
      int outputCount = 0;

      while(true) {
         if (this._inputPtr >= this._inputEnd) {
            this._loadMoreGuaranteed();
         }

         char ch = this._inputBuffer[this._inputPtr++];
         if (ch > ' ') {
            int bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (ch == '"') {
                  break;
               }

               bits = this._decodeBase64Escape(b64variant, ch, 0);
               if (bits < 0) {
                  continue;
               }
            }

            if (outputPtr > outputEnd) {
               outputCount += outputPtr;
               out.write(buffer, 0, outputPtr);
               outputPtr = 0;
            }

            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++];
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               bits = this._decodeBase64Escape(b64variant, ch, 1);
            }

            int var21 = bits << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++];
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  if (ch == '"') {
                     var21 >>= 4;
                     buffer[outputPtr++] = (byte)var21;
                     if (b64variant.requiresPaddingOnRead()) {
                        --this._inputPtr;
                        this._handleBase64MissingPadding(b64variant);
                     }
                     break;
                  }

                  bits = this._decodeBase64Escape(b64variant, ch, 2);
               }

               if (bits == -2) {
                  if (this._inputPtr >= this._inputEnd) {
                     this._loadMoreGuaranteed();
                  }

                  ch = this._inputBuffer[this._inputPtr++];
                  if (!b64variant.usesPaddingChar(ch) && this._decodeBase64Escape(b64variant, ch, 3) != -2) {
                     throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                  }

                  var21 >>= 4;
                  buffer[outputPtr++] = (byte)var21;
                  continue;
               }
            }

            var21 = var21 << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++];
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  if (ch == '"') {
                     var21 >>= 2;
                     buffer[outputPtr++] = (byte)(var21 >> 8);
                     buffer[outputPtr++] = (byte)var21;
                     if (b64variant.requiresPaddingOnRead()) {
                        --this._inputPtr;
                        this._handleBase64MissingPadding(b64variant);
                     }
                     break;
                  }

                  bits = this._decodeBase64Escape(b64variant, ch, 3);
               }

               if (bits == -2) {
                  var21 >>= 2;
                  buffer[outputPtr++] = (byte)(var21 >> 8);
                  buffer[outputPtr++] = (byte)var21;
                  continue;
               }
            }

            var21 = var21 << 6 | bits;
            buffer[outputPtr++] = (byte)(var21 >> 16);
            buffer[outputPtr++] = (byte)(var21 >> 8);
            buffer[outputPtr++] = (byte)var21;
         }
      }

      this._tokenIncomplete = false;
      if (outputPtr > 0) {
         outputCount += outputPtr;
         out.write(buffer, 0, outputPtr);
      }

      return outputCount;
   }

   public final JsonToken nextToken() throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         return this._nextAfterName();
      } else {
         this._numTypesValid = 0;
         if (this._tokenIncomplete) {
            this._skipString();
         }

         int i = this._skipWSOrEnd();
         if (i < 0) {
            this.close();
            return this._updateTokenToNull();
         } else {
            this._binaryValue = null;
            if ((i | 32) == 125) {
               this._closeScope(i);
               return this._currToken;
            } else {
               if (this._parsingContext.expectComma()) {
                  i = this._skipComma(i);
                  if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i | 32) == 125) {
                     this._closeScope(i);
                     return this._currToken;
                  }
               }

               boolean inObject = this._parsingContext.inObject();
               if (inObject) {
                  this._updateNameLocation();
                  String name = i == 34 ? this._parseName() : this._handleOddName(i);
                  this._parsingContext.setCurrentName(name);
                  this._updateToken(JsonToken.FIELD_NAME);
                  i = this._skipColon();
               }

               this._updateLocation();
               JsonToken t;
               switch (i) {
                  case 34:
                     this._tokenIncomplete = true;
                     t = JsonToken.VALUE_STRING;
                     break;
                  case 43:
                     if (this.isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                        t = this._parseSignedNumber(false);
                     } else {
                        t = this._handleOddValue(i);
                     }
                     break;
                  case 45:
                     t = this._parseSignedNumber(true);
                     break;
                  case 46:
                     t = this._parseFloatThatStartsWithPeriod(false);
                     break;
                  case 48:
                  case 49:
                  case 50:
                  case 51:
                  case 52:
                  case 53:
                  case 54:
                  case 55:
                  case 56:
                  case 57:
                     t = this._parseUnsignedNumber(i);
                     break;
                  case 91:
                     if (!inObject) {
                        this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                     }

                     t = JsonToken.START_ARRAY;
                     break;
                  case 102:
                     this._matchFalse();
                     t = JsonToken.VALUE_FALSE;
                     break;
                  case 110:
                     this._matchNull();
                     t = JsonToken.VALUE_NULL;
                     break;
                  case 123:
                     if (!inObject) {
                        this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                     }

                     t = JsonToken.START_OBJECT;
                     break;
                  case 125:
                     this._reportUnexpectedChar(i, "expected a value");
                  case 116:
                     this._matchTrue();
                     t = JsonToken.VALUE_TRUE;
                     break;
                  default:
                     t = this._handleOddValue(i);
               }

               if (inObject) {
                  this._nextToken = t;
                  return this._currToken;
               } else {
                  return this._updateToken(t);
               }
            }
         }
      }
   }

   private final JsonToken _nextAfterName() throws IOException {
      this._nameCopied = false;
      JsonToken t = this._nextToken;
      this._nextToken = null;
      if (t == JsonToken.START_ARRAY) {
         this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
      } else if (t == JsonToken.START_OBJECT) {
         this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
      }

      return this._updateToken(t);
   }

   public void finishToken() throws IOException {
      if (this._tokenIncomplete) {
         this._tokenIncomplete = false;
         this._finishString();
      }

   }

   public boolean nextFieldName(SerializableString sstr) throws IOException {
      this._numTypesValid = 0;
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nextAfterName();
         return false;
      } else {
         if (this._tokenIncomplete) {
            this._skipString();
         }

         int i = this._skipWSOrEnd();
         if (i < 0) {
            this.close();
            this._updateTokenToNull();
            return false;
         } else {
            this._binaryValue = null;
            if ((i | 32) == 125) {
               this._closeScope(i);
               return false;
            } else {
               if (this._parsingContext.expectComma()) {
                  i = this._skipComma(i);
                  if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i | 32) == 125) {
                     this._closeScope(i);
                     return false;
                  }
               }

               if (!this._parsingContext.inObject()) {
                  this._updateLocation();
                  this._nextTokenNotInObject(i);
                  return false;
               } else {
                  this._updateNameLocation();
                  if (i == 34) {
                     char[] nameChars = sstr.asQuotedChars();
                     int len = nameChars.length;
                     if (this._inputPtr + len + 4 < this._inputEnd) {
                        int end = this._inputPtr + len;
                        if (this._inputBuffer[end] == '"') {
                           int offset = 0;
                           int ptr = this._inputPtr;

                           while(true) {
                              if (ptr == end) {
                                 this._parsingContext.setCurrentName(sstr.getValue());
                                 this._isNextTokenNameYes(this._skipColonFast(ptr + 1));
                                 return true;
                              }

                              if (nameChars[offset] != this._inputBuffer[ptr]) {
                                 break;
                              }

                              ++offset;
                              ++ptr;
                           }
                        }
                     }
                  }

                  return this._isNextTokenNameMaybe(i, sstr.getValue());
               }
            }
         }
      }
   }

   public String nextFieldName() throws IOException {
      this._numTypesValid = 0;
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nextAfterName();
         return null;
      } else {
         if (this._tokenIncomplete) {
            this._skipString();
         }

         int i = this._skipWSOrEnd();
         if (i < 0) {
            this.close();
            this._updateTokenToNull();
            return null;
         } else {
            this._binaryValue = null;
            if ((i | 32) == 125) {
               this._closeScope(i);
               return null;
            } else {
               if (this._parsingContext.expectComma()) {
                  i = this._skipComma(i);
                  if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i | 32) == 125) {
                     this._closeScope(i);
                     return null;
                  }
               }

               if (!this._parsingContext.inObject()) {
                  this._updateLocation();
                  this._nextTokenNotInObject(i);
                  return null;
               } else {
                  this._updateNameLocation();
                  String name = i == 34 ? this._parseName() : this._handleOddName(i);
                  this._parsingContext.setCurrentName(name);
                  this._updateToken(JsonToken.FIELD_NAME);
                  i = this._skipColon();
                  this._updateLocation();
                  if (i == 34) {
                     this._tokenIncomplete = true;
                     this._nextToken = JsonToken.VALUE_STRING;
                     return name;
                  } else {
                     JsonToken t;
                     switch (i) {
                        case 43:
                           if (this.isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                              t = this._parseSignedNumber(false);
                           } else {
                              t = this._handleOddValue(i);
                           }
                           break;
                        case 45:
                           t = this._parseSignedNumber(true);
                           break;
                        case 46:
                           t = this._parseFloatThatStartsWithPeriod(false);
                           break;
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                           t = this._parseUnsignedNumber(i);
                           break;
                        case 91:
                           t = JsonToken.START_ARRAY;
                           break;
                        case 102:
                           this._matchFalse();
                           t = JsonToken.VALUE_FALSE;
                           break;
                        case 110:
                           this._matchNull();
                           t = JsonToken.VALUE_NULL;
                           break;
                        case 116:
                           this._matchTrue();
                           t = JsonToken.VALUE_TRUE;
                           break;
                        case 123:
                           t = JsonToken.START_OBJECT;
                           break;
                        default:
                           t = this._handleOddValue(i);
                     }

                     this._nextToken = t;
                     return name;
                  }
               }
            }
         }
      }
   }

   private final void _isNextTokenNameYes(int i) throws IOException {
      this._updateToken(JsonToken.FIELD_NAME);
      this._updateLocation();
      switch (i) {
         case 34:
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return;
         case 43:
            if (this.isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
               this._nextToken = this._parseSignedNumber(false);
            } else {
               this._nextToken = this._handleOddValue(i);
            }

            return;
         case 45:
            this._nextToken = this._parseSignedNumber(true);
            return;
         case 46:
            this._nextToken = this._parseFloatThatStartsWithPeriod(false);
            return;
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            this._nextToken = this._parseUnsignedNumber(i);
            return;
         case 91:
            this._nextToken = JsonToken.START_ARRAY;
            return;
         case 102:
            this._matchToken("false", 1);
            this._nextToken = JsonToken.VALUE_FALSE;
            return;
         case 110:
            this._matchToken("null", 1);
            this._nextToken = JsonToken.VALUE_NULL;
            return;
         case 116:
            this._matchToken("true", 1);
            this._nextToken = JsonToken.VALUE_TRUE;
            return;
         case 123:
            this._nextToken = JsonToken.START_OBJECT;
            return;
         default:
            this._nextToken = this._handleOddValue(i);
      }
   }

   protected boolean _isNextTokenNameMaybe(int i, String nameToMatch) throws IOException {
      String name = i == 34 ? this._parseName() : this._handleOddName(i);
      this._parsingContext.setCurrentName(name);
      this._updateToken(JsonToken.FIELD_NAME);
      i = this._skipColon();
      this._updateLocation();
      if (i == 34) {
         this._tokenIncomplete = true;
         this._nextToken = JsonToken.VALUE_STRING;
         return nameToMatch.equals(name);
      } else {
         JsonToken t;
         switch (i) {
            case 43:
               if (this.isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                  t = this._parseSignedNumber(false);
               } else {
                  t = this._handleOddValue(i);
               }
               break;
            case 45:
               t = this._parseSignedNumber(true);
               break;
            case 46:
               t = this._parseFloatThatStartsWithPeriod(false);
               break;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               t = this._parseUnsignedNumber(i);
               break;
            case 91:
               t = JsonToken.START_ARRAY;
               break;
            case 102:
               this._matchFalse();
               t = JsonToken.VALUE_FALSE;
               break;
            case 110:
               this._matchNull();
               t = JsonToken.VALUE_NULL;
               break;
            case 116:
               this._matchTrue();
               t = JsonToken.VALUE_TRUE;
               break;
            case 123:
               t = JsonToken.START_OBJECT;
               break;
            default:
               t = this._handleOddValue(i);
         }

         this._nextToken = t;
         return nameToMatch.equals(name);
      }
   }

   private final JsonToken _nextTokenNotInObject(int i) throws IOException {
      if (i == 34) {
         this._tokenIncomplete = true;
         return this._updateToken(JsonToken.VALUE_STRING);
      } else {
         switch (i) {
            case 44:
               if (!this._parsingContext.inRoot() && (this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
                  --this._inputPtr;
                  return this._updateToken(JsonToken.VALUE_NULL);
               }
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            default:
               return this._updateToken(this._handleOddValue(i));
            case 45:
               return this._updateToken(this._parseSignedNumber(true));
            case 46:
               return this._updateToken(this._parseFloatThatStartsWithPeriod(false));
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               return this._updateToken(this._parseUnsignedNumber(i));
            case 91:
               this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
               return this._updateToken(JsonToken.START_ARRAY);
            case 102:
               this._matchToken("false", 1);
               return this._updateToken(JsonToken.VALUE_FALSE);
            case 110:
               this._matchToken("null", 1);
               return this._updateToken(JsonToken.VALUE_NULL);
            case 116:
               this._matchToken("true", 1);
               return this._updateToken(JsonToken.VALUE_TRUE);
            case 123:
               this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
               return this._updateToken(JsonToken.START_OBJECT);
         }
      }
   }

   public final String nextTextValue() throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._updateToken(t);
         if (t == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
               this._tokenIncomplete = false;
               this._finishString();
            }

            return this._textBuffer.contentsAsString();
         } else {
            if (t == JsonToken.START_ARRAY) {
               this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return null;
         }
      } else {
         return this.nextToken() == JsonToken.VALUE_STRING ? this.getText() : null;
      }
   }

   public final int nextIntValue(int defaultValue) throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._updateToken(t);
         if (t == JsonToken.VALUE_NUMBER_INT) {
            return this.getIntValue();
         } else {
            if (t == JsonToken.START_ARRAY) {
               this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return defaultValue;
         }
      } else {
         return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getIntValue() : defaultValue;
      }
   }

   public final long nextLongValue(long defaultValue) throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._updateToken(t);
         if (t == JsonToken.VALUE_NUMBER_INT) {
            return this.getLongValue();
         } else {
            if (t == JsonToken.START_ARRAY) {
               this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return defaultValue;
         }
      } else {
         return this.nextToken() == JsonToken.VALUE_NUMBER_INT ? this.getLongValue() : defaultValue;
      }
   }

   public final Boolean nextBooleanValue() throws IOException {
      if (this._currToken == JsonToken.FIELD_NAME) {
         this._nameCopied = false;
         JsonToken t = this._nextToken;
         this._nextToken = null;
         this._updateToken(t);
         if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
         } else if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
         } else {
            if (t == JsonToken.START_ARRAY) {
               this.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
            } else if (t == JsonToken.START_OBJECT) {
               this.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
            }

            return null;
         }
      } else {
         JsonToken t = this.nextToken();
         if (t != null) {
            int id = t.id();
            if (id == 9) {
               return Boolean.TRUE;
            }

            if (id == 10) {
               return Boolean.FALSE;
            }
         }

         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   protected final JsonToken _parseFloatThatStartsWithPeriod() throws IOException {
      return this._parseFloatThatStartsWithPeriod(false);
   }

   protected final JsonToken _parseFloatThatStartsWithPeriod(boolean neg) throws IOException {
      if (!this.isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
         return this._handleOddValue(46);
      } else {
         int startPtr = this._inputPtr - 1;
         if (neg) {
            --startPtr;
         }

         return this._parseFloat(46, startPtr, this._inputPtr, neg, 0);
      }
   }

   protected final JsonToken _parseUnsignedNumber(int ch) throws IOException {
      int ptr = this._inputPtr;
      int startPtr = ptr - 1;
      int inputLen = this._inputEnd;
      if (ch == 48) {
         return this._parseNumber2(false, startPtr);
      } else {
         for(int intLen = 1; ptr < inputLen; ++intLen) {
            ch = this._inputBuffer[ptr++];
            if (ch < 48 || ch > 57) {
               if (ch != 46 && (ch | 32) != 101) {
                  --ptr;
                  this._inputPtr = ptr;
                  if (this._parsingContext.inRoot()) {
                     this._verifyRootSpace(ch);
                  }

                  int len = ptr - startPtr;
                  this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
                  return this.resetInt(false, intLen);
               } else {
                  this._inputPtr = ptr;
                  return this._parseFloat(ch, startPtr, ptr, false, intLen);
               }
            }
         }

         this._inputPtr = startPtr;
         return this._parseNumber2(false, startPtr);
      }
   }

   private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen) throws IOException {
      int inputLen = this._inputEnd;
      int fractLen = 0;
      if (ch == 46) {
         while(true) {
            if (ptr >= inputLen) {
               return this._parseNumber2(neg, startPtr);
            }

            ch = this._inputBuffer[ptr++];
            if (ch < 48 || ch > 57) {
               if (fractLen == 0 && !this.isEnabled(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
                  this._reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
               }
               break;
            }

            ++fractLen;
         }
      }

      int expLen = 0;
      if ((ch | 32) == 101) {
         if (ptr >= inputLen) {
            this._inputPtr = startPtr;
            return this._parseNumber2(neg, startPtr);
         }

         ch = this._inputBuffer[ptr++];
         if (ch == 45 || ch == 43) {
            if (ptr >= inputLen) {
               this._inputPtr = startPtr;
               return this._parseNumber2(neg, startPtr);
            }

            ch = this._inputBuffer[ptr++];
         }

         while(ch <= 57 && ch >= 48) {
            ++expLen;
            if (ptr >= inputLen) {
               this._inputPtr = startPtr;
               return this._parseNumber2(neg, startPtr);
            }

            ch = this._inputBuffer[ptr++];
         }

         if (expLen == 0) {
            this._reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
         }
      }

      --ptr;
      this._inputPtr = ptr;
      if (this._parsingContext.inRoot()) {
         this._verifyRootSpace(ch);
      }

      int len = ptr - startPtr;
      this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
      return this.resetFloat(neg, intLen, fractLen, expLen);
   }

   private final JsonToken _parseSignedNumber(boolean negative) throws IOException {
      int ptr = this._inputPtr;
      int startPtr = negative ? ptr - 1 : ptr;
      int inputEnd = this._inputEnd;
      if (ptr >= inputEnd) {
         return this._parseNumber2(negative, startPtr);
      } else {
         int ch = this._inputBuffer[ptr++];
         if (ch <= 57 && ch >= 48) {
            if (ch == 48) {
               return this._parseNumber2(negative, startPtr);
            } else {
               for(int intLen = 1; ptr < inputEnd; ++intLen) {
                  ch = this._inputBuffer[ptr++];
                  if (ch < 48 || ch > 57) {
                     if (ch != 46 && (ch | 32) != 101) {
                        --ptr;
                        this._inputPtr = ptr;
                        if (this._parsingContext.inRoot()) {
                           this._verifyRootSpace(ch);
                        }

                        int len = ptr - startPtr;
                        this._textBuffer.resetWithShared(this._inputBuffer, startPtr, len);
                        return this.resetInt(negative, intLen);
                     } else {
                        this._inputPtr = ptr;
                        return this._parseFloat(ch, startPtr, ptr, negative, intLen);
                     }
                  }
               }

               return this._parseNumber2(negative, startPtr);
            }
         } else {
            this._inputPtr = ptr;
            return ch == 46 ? this._parseFloatThatStartsWithPeriod(negative) : this._handleInvalidNumberStart(ch, negative, true);
         }
      }
   }

   private final JsonToken _parseNumber2(boolean neg, int startPtr) throws IOException {
      this._inputPtr = neg ? startPtr + 1 : startPtr;
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int outPtr = 0;
      if (neg) {
         outBuf[outPtr++] = '-';
      }

      int intLen = 0;
      char c = this._inputPtr < this._inputEnd ? this._inputBuffer[this._inputPtr++] : this.getNextChar("No digit following sign", JsonToken.VALUE_NUMBER_INT);
      if (c == '0') {
         c = this._verifyNoLeadingZeroes();
      }

      boolean eof;
      for(eof = false; c >= '0' && c <= '9'; c = this._inputBuffer[this._inputPtr++]) {
         ++intLen;
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = c;
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            c = 0;
            eof = true;
            break;
         }
      }

      if (intLen != 0 || c == '.' && this.isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
         int fractLen = -1;
         if (c == '.') {
            fractLen = 0;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = c;

            while(true) {
               if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
                  eof = true;
                  break;
               }

               c = this._inputBuffer[this._inputPtr++];
               if (c < '0' || c > '9') {
                  break;
               }

               ++fractLen;
               if (outPtr >= outBuf.length) {
                  outBuf = this._textBuffer.finishCurrentSegment();
                  outPtr = 0;
               }

               outBuf[outPtr++] = c;
            }

            if (fractLen == 0 && !this.isEnabled(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
               this._reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
         }

         int expLen = -1;
         if ((c | 32) == 101) {
            expLen = 0;
            if (outPtr >= outBuf.length) {
               outBuf = this._textBuffer.finishCurrentSegment();
               outPtr = 0;
            }

            outBuf[outPtr++] = c;
            c = this._inputPtr < this._inputEnd ? this._inputBuffer[this._inputPtr++] : this.getNextChar("expected a digit for number exponent", JsonToken.VALUE_NUMBER_FLOAT);
            if (c == '-' || c == '+') {
               if (outPtr >= outBuf.length) {
                  outBuf = this._textBuffer.finishCurrentSegment();
                  outPtr = 0;
               }

               outBuf[outPtr++] = c;
               c = this._inputPtr < this._inputEnd ? this._inputBuffer[this._inputPtr++] : this.getNextChar("expected a digit for number exponent", JsonToken.VALUE_NUMBER_FLOAT);
            }

            while(c <= '9' && c >= '0') {
               ++expLen;
               if (outPtr >= outBuf.length) {
                  outBuf = this._textBuffer.finishCurrentSegment();
                  outPtr = 0;
               }

               outBuf[outPtr++] = c;
               if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
                  eof = true;
                  break;
               }

               c = this._inputBuffer[this._inputPtr++];
            }

            if (expLen == 0) {
               this._reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
            }
         }

         if (!eof) {
            --this._inputPtr;
            if (this._parsingContext.inRoot()) {
               this._verifyRootSpace(c);
            }
         }

         this._textBuffer.setCurrentLength(outPtr);
         return fractLen < 0 && expLen < 0 ? this.resetInt(neg, intLen) : this.resetFloat(neg, intLen, fractLen, expLen);
      } else {
         return this._handleInvalidNumberStart(c, neg);
      }
   }

   private final char _verifyNoLeadingZeroes() throws IOException {
      if (this._inputPtr < this._inputEnd) {
         char ch = this._inputBuffer[this._inputPtr];
         if (ch < '0' || ch > '9') {
            return '0';
         }
      }

      return this._verifyNLZ2();
   }

   private char _verifyNLZ2() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         return '0';
      } else {
         char ch = this._inputBuffer[this._inputPtr];
         if (ch >= '0' && ch <= '9') {
            if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
               this.reportInvalidNumber("Leading zeroes not allowed");
            }

            ++this._inputPtr;
            if (ch == '0') {
               while(this._inputPtr < this._inputEnd || this._loadMore()) {
                  ch = this._inputBuffer[this._inputPtr];
                  if (ch < '0' || ch > '9') {
                     return '0';
                  }

                  ++this._inputPtr;
                  if (ch != '0') {
                     break;
                  }
               }
            }

            return ch;
         } else {
            return '0';
         }
      }
   }

   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative) throws IOException {
      return this._handleInvalidNumberStart(ch, negative, false);
   }

   protected JsonToken _handleInvalidNumberStart(int ch, boolean negative, boolean hasSign) throws IOException {
      if (ch == 73) {
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
         }

         ch = this._inputBuffer[this._inputPtr++];
         if (ch == 78) {
            String match = negative ? "-INF" : "+INF";
            this._matchToken(match, 3);
            if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
               return this.resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }

            this._reportError("Non-standard token '" + match + "': enable `JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS` to allow");
         } else if (ch == 110) {
            String match = negative ? "-Infinity" : "+Infinity";
            this._matchToken(match, 3);
            if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
               return this.resetAsNaN(match, negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }

            this._reportError("Non-standard token '" + match + "': enable `JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS` to allow");
         }
      }

      if (!this.isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature()) && hasSign && !negative) {
         this._reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
      }

      String message = negative ? "expected digit (0-9) to follow minus sign, for valid numeric value" : "expected digit (0-9) for valid numeric value";
      this._reportUnexpectedNumberChar(ch, message);
      return null;
   }

   private final void _verifyRootSpace(int ch) throws IOException {
      ++this._inputPtr;
      switch (ch) {
         case 9:
         case 32:
            return;
         case 10:
            ++this._currInputRow;
            this._currInputRowStart = this._inputPtr;
            return;
         case 13:
            --this._inputPtr;
            return;
         default:
            this._reportMissingRootWS(ch);
      }
   }

   protected final String _parseName() throws IOException {
      int ptr = this._inputPtr;
      int hash = this._hashSeed;

      for(int[] codes = INPUT_CODES_LATIN1; ptr < this._inputEnd; ++ptr) {
         int ch = this._inputBuffer[ptr];
         if (ch < codes.length && codes[ch] != 0) {
            if (ch == 34) {
               int start = this._inputPtr;
               this._inputPtr = ptr + 1;
               return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
            }
            break;
         }

         hash = hash * 33 + ch;
      }

      int start = this._inputPtr;
      this._inputPtr = ptr;
      return this._parseName2(start, hash, 34);
   }

   private String _parseName2(int startPtr, int hash, int endChar) throws IOException {
      this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
      char[] outBuf = this._textBuffer.getCurrentSegment();
      int outPtr = this._textBuffer.getCurrentSegmentSize();

      while(true) {
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
         }

         char c = this._inputBuffer[this._inputPtr++];
         if (c <= '\\') {
            if (c == '\\') {
               c = this._decodeEscaped();
            } else if (c <= endChar) {
               if (c == endChar) {
                  this._textBuffer.setCurrentLength(outPtr);
                  TextBuffer tb = this._textBuffer;
                  char[] buf = tb.getTextBuffer();
                  int start = tb.getTextOffset();
                  return this._symbols.findSymbol(buf, start, tb.size(), hash);
               }

               if (c < ' ') {
                  this._throwUnquotedSpace(c, "name");
               }
            }
         }

         hash = hash * 33 + c;
         outBuf[outPtr++] = c;
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }
      }
   }

   protected String _handleOddName(int i) throws IOException {
      if (i == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
         return this._parseAposName();
      } else {
         if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
            this._reportUnexpectedChar(i, "was expecting double-quote to start field name");
         }

         int[] codes = CharTypes.getInputCodeLatin1JsNames();
         int maxCode = codes.length;
         boolean firstOk;
         if (i < maxCode) {
            firstOk = codes[i] == 0;
         } else {
            firstOk = Character.isJavaIdentifierPart((char)i);
         }

         if (!firstOk) {
            this._reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
         }

         int ptr = this._inputPtr;
         int hash = this._hashSeed;
         int inputLen = this._inputEnd;
         if (ptr < inputLen) {
            do {
               int ch = this._inputBuffer[ptr];
               if (ch < maxCode) {
                  if (codes[ch] != 0) {
                     int start = this._inputPtr - 1;
                     this._inputPtr = ptr;
                     return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                  }
               } else if (!Character.isJavaIdentifierPart((char)ch)) {
                  int start = this._inputPtr - 1;
                  this._inputPtr = ptr;
                  return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
               }

               hash = hash * 33 + ch;
               ++ptr;
            } while(ptr < inputLen);
         }

         int start = this._inputPtr - 1;
         this._inputPtr = ptr;
         return this._handleOddName2(start, hash, codes);
      }
   }

   protected String _parseAposName() throws IOException {
      int ptr = this._inputPtr;
      int hash = this._hashSeed;
      int inputLen = this._inputEnd;
      if (ptr < inputLen) {
         int[] codes = INPUT_CODES_LATIN1;
         int maxCode = codes.length;

         do {
            int ch = this._inputBuffer[ptr];
            if (ch == 39) {
               int start = this._inputPtr;
               this._inputPtr = ptr + 1;
               return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
            }

            if (ch < maxCode && codes[ch] != 0) {
               break;
            }

            hash = hash * 33 + ch;
            ++ptr;
         } while(ptr < inputLen);
      }

      int start = this._inputPtr;
      this._inputPtr = ptr;
      return this._parseName2(start, hash, 39);
   }

   protected JsonToken _handleOddValue(int i) throws IOException {
      switch (i) {
         case 39:
            if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
               return this._handleApos();
            }
            break;
         case 43:
            if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
               this._reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
            }

            return this._handleInvalidNumberStart(this._inputBuffer[this._inputPtr++], false, true);
         case 73:
            this._matchToken("Infinity", 1);
            if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
               return this.resetAsNaN("Infinity", Double.POSITIVE_INFINITY);
            }

            this._reportError("Non-standard token 'Infinity': enable `JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS` to allow");
            break;
         case 78:
            this._matchToken("NaN", 1);
            if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
               return this.resetAsNaN("NaN", Double.NaN);
            }

            this._reportError("Non-standard token 'NaN': enable `JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS` to allow");
            break;
         case 93:
            if (!this._parsingContext.inArray()) {
               break;
            }
         case 44:
            if (!this._parsingContext.inRoot() && (this._features & FEAT_MASK_ALLOW_MISSING) != 0) {
               --this._inputPtr;
               return JsonToken.VALUE_NULL;
            }
      }

      if (Character.isJavaIdentifierStart(i)) {
         this._reportInvalidToken("" + (char)i, this._validJsonTokenList());
      }

      this._reportUnexpectedChar(i, "expected a valid value " + this._validJsonValueList());
      return null;
   }

   protected JsonToken _handleApos() throws IOException {
      char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
      int outPtr = this._textBuffer.getCurrentSegmentSize();

      while(true) {
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
         }

         char c = this._inputBuffer[this._inputPtr++];
         if (c <= '\\') {
            if (c == '\\') {
               c = this._decodeEscaped();
            } else if (c <= '\'') {
               if (c == '\'') {
                  this._textBuffer.setCurrentLength(outPtr);
                  return JsonToken.VALUE_STRING;
               }

               if (c < ' ') {
                  this._throwUnquotedSpace(c, "string value");
               }
            }
         }

         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = c;
      }
   }

   private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException {
      this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
      char[] outBuf = this._textBuffer.getCurrentSegment();
      int outPtr = this._textBuffer.getCurrentSegmentSize();
      int maxCode = codes.length;

      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         char c = this._inputBuffer[this._inputPtr];
         if (c < maxCode) {
            if (codes[c] != 0) {
               break;
            }
         } else if (!Character.isJavaIdentifierPart(c)) {
            break;
         }

         ++this._inputPtr;
         hash = hash * 33 + c;
         outBuf[outPtr++] = c;
         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }
      }

      this._textBuffer.setCurrentLength(outPtr);
      TextBuffer tb = this._textBuffer;
      char[] buf = tb.getTextBuffer();
      int start = tb.getTextOffset();
      return this._symbols.findSymbol(buf, start, tb.size(), hash);
   }

   protected final void _finishString() throws IOException {
      int ptr = this._inputPtr;
      int inputLen = this._inputEnd;
      if (ptr < inputLen) {
         int[] codes = INPUT_CODES_LATIN1;
         int maxCode = codes.length;

         do {
            int ch = this._inputBuffer[ptr];
            if (ch < maxCode && codes[ch] != 0) {
               if (ch == 34) {
                  this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
                  this._inputPtr = ptr + 1;
                  return;
               }
               break;
            }

            ++ptr;
         } while(ptr < inputLen);
      }

      this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
      this._inputPtr = ptr;
      this._finishString2();
   }

   protected void _finishString2() throws IOException {
      char[] outBuf = this._textBuffer.getCurrentSegment();
      int outPtr = this._textBuffer.getCurrentSegmentSize();
      int[] codes = INPUT_CODES_LATIN1;
      int maxCode = codes.length;

      while(true) {
         if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
            this._reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
         }

         char c = this._inputBuffer[this._inputPtr++];
         if (c < maxCode && codes[c] != 0) {
            if (c == '"') {
               this._textBuffer.setCurrentLength(outPtr);
               return;
            }

            if (c == '\\') {
               c = this._decodeEscaped();
            } else if (c < ' ') {
               this._throwUnquotedSpace(c, "string value");
            }
         }

         if (outPtr >= outBuf.length) {
            outBuf = this._textBuffer.finishCurrentSegment();
            outPtr = 0;
         }

         outBuf[outPtr++] = c;
      }
   }

   protected final void _skipString() throws IOException {
      this._tokenIncomplete = false;
      int inPtr = this._inputPtr;
      int inLen = this._inputEnd;
      char[] inBuf = this._inputBuffer;

      while(true) {
         if (inPtr >= inLen) {
            this._inputPtr = inPtr;
            if (!this._loadMore()) {
               this._reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
            }

            inPtr = this._inputPtr;
            inLen = this._inputEnd;
         }

         char c = inBuf[inPtr++];
         if (c <= '\\') {
            if (c == '\\') {
               this._inputPtr = inPtr;
               this._decodeEscaped();
               inPtr = this._inputPtr;
               inLen = this._inputEnd;
            } else if (c <= '"') {
               if (c == '"') {
                  this._inputPtr = inPtr;
                  return;
               }

               if (c < ' ') {
                  this._inputPtr = inPtr;
                  this._throwUnquotedSpace(c, "string value");
               }
            }
         }
      }
   }

   protected final void _skipCR() throws IOException {
      if ((this._inputPtr < this._inputEnd || this._loadMore()) && this._inputBuffer[this._inputPtr] == '\n') {
         ++this._inputPtr;
      }

      ++this._currInputRow;
      this._currInputRowStart = this._inputPtr;
   }

   private final int _skipColon() throws IOException {
      if (this._inputPtr + 4 >= this._inputEnd) {
         return this._skipColon2(false);
      } else {
         char c = this._inputBuffer[this._inputPtr];
         if (c == ':') {
            int i = this._inputBuffer[++this._inputPtr];
            if (i > 32) {
               if (i != 47 && i != 35) {
                  ++this._inputPtr;
                  return i;
               } else {
                  return this._skipColon2(true);
               }
            } else {
               if (i == 32 || i == 9) {
                  i = this._inputBuffer[++this._inputPtr];
                  if (i > 32) {
                     if (i != 47 && i != 35) {
                        ++this._inputPtr;
                        return i;
                     }

                     return this._skipColon2(true);
                  }
               }

               return this._skipColon2(true);
            }
         } else {
            if (c == ' ' || c == '\t') {
               c = this._inputBuffer[++this._inputPtr];
            }

            if (c == ':') {
               int i = this._inputBuffer[++this._inputPtr];
               if (i > 32) {
                  if (i != 47 && i != 35) {
                     ++this._inputPtr;
                     return i;
                  } else {
                     return this._skipColon2(true);
                  }
               } else {
                  if (i == 32 || i == 9) {
                     i = this._inputBuffer[++this._inputPtr];
                     if (i > 32) {
                        if (i != 47 && i != 35) {
                           ++this._inputPtr;
                           return i;
                        }

                        return this._skipColon2(true);
                     }
                  }

                  return this._skipColon2(true);
               }
            } else {
               return this._skipColon2(false);
            }
         }
      }
   }

   private final int _skipColon2(boolean gotColon) throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++];
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else if (i != 35 || !this._skipYAMLComment()) {
               if (gotColon) {
                  return i;
               }

               if (i != 58) {
                  this._reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
               }

               gotColon = true;
            }
         } else if (i < 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      this._reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", (JsonToken)null);
      return -1;
   }

   private final int _skipColonFast(int ptr) throws IOException {
      int i = this._inputBuffer[ptr++];
      if (i == 58) {
         i = this._inputBuffer[ptr++];
         if (i > 32) {
            if (i != 47 && i != 35) {
               this._inputPtr = ptr;
               return i;
            }
         } else if (i == 32 || i == 9) {
            i = this._inputBuffer[ptr++];
            if (i > 32 && i != 47 && i != 35) {
               this._inputPtr = ptr;
               return i;
            }
         }

         this._inputPtr = ptr - 1;
         return this._skipColon2(true);
      } else {
         if (i == 32 || i == 9) {
            i = this._inputBuffer[ptr++];
         }

         boolean gotColon = i == 58;
         if (gotColon) {
            i = this._inputBuffer[ptr++];
            if (i > 32) {
               if (i != 47 && i != 35) {
                  this._inputPtr = ptr;
                  return i;
               }
            } else if (i == 32 || i == 9) {
               i = this._inputBuffer[ptr++];
               if (i > 32 && i != 47 && i != 35) {
                  this._inputPtr = ptr;
                  return i;
               }
            }
         }

         this._inputPtr = ptr - 1;
         return this._skipColon2(gotColon);
      }
   }

   private final int _skipComma(int i) throws IOException {
      if (i != 44) {
         this._reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
      }

      while(this._inputPtr < this._inputEnd) {
         i = this._inputBuffer[this._inputPtr++];
         if (i > 32) {
            if (i != 47 && i != 35) {
               return i;
            }

            --this._inputPtr;
            return this._skipAfterComma2();
         }

         if (i < 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      return this._skipAfterComma2();
   }

   private final int _skipAfterComma2() throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++];
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else if (i != 35 || !this._skipYAMLComment()) {
               return i;
            }
         } else if (i < 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      throw this._constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
   }

   private final int _skipWSOrEnd() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         return this._eofAsNextChar();
      } else {
         int i = this._inputBuffer[this._inputPtr++];
         if (i > 32) {
            if (i != 47 && i != 35) {
               return i;
            } else {
               --this._inputPtr;
               return this._skipWSOrEnd2();
            }
         } else {
            if (i != 32) {
               if (i == 10) {
                  ++this._currInputRow;
                  this._currInputRowStart = this._inputPtr;
               } else if (i == 13) {
                  this._skipCR();
               } else if (i != 9 && !this._isAllowedCtrlCharRS(i)) {
                  this._throwInvalidSpace(i);
               }
            }

            while(this._inputPtr < this._inputEnd) {
               i = this._inputBuffer[this._inputPtr++];
               if (i > 32) {
                  if (i != 47 && i != 35) {
                     return i;
                  }

                  --this._inputPtr;
                  return this._skipWSOrEnd2();
               }

               if (i != 32) {
                  if (i == 10) {
                     ++this._currInputRow;
                     this._currInputRowStart = this._inputPtr;
                  } else if (i == 13) {
                     this._skipCR();
                  } else if (i != 9 && !this._isAllowedCtrlCharRS(i)) {
                     this._throwInvalidSpace(i);
                  }
               }
            }

            return this._skipWSOrEnd2();
         }
      }
   }

   private int _skipWSOrEnd2() throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++];
         if (i > 32) {
            if (i == 47) {
               this._skipComment();
            } else if (i != 35 || !this._skipYAMLComment()) {
               return i;
            }
         } else if (i != 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
            } else if (i == 13) {
               this._skipCR();
            } else if (i != 9) {
               this._throwInvalidSpace(i);
            }
         }
      }

      return this._eofAsNextChar();
   }

   private void _skipComment() throws IOException {
      if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
         this._reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
      }

      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(" in a comment", (JsonToken)null);
      }

      char c = this._inputBuffer[this._inputPtr++];
      if (c == '/') {
         this._skipLine();
      } else if (c == '*') {
         this._skipCComment();
      } else {
         this._reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
      }

   }

   private void _skipCComment() throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++];
         if (i <= 42) {
            if (i != 42) {
               if (i < 32) {
                  if (i == 10) {
                     ++this._currInputRow;
                     this._currInputRowStart = this._inputPtr;
                  } else if (i == 13) {
                     this._skipCR();
                  } else if (i != 9) {
                     this._throwInvalidSpace(i);
                  }
               }
            } else {
               if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
                  break;
               }

               if (this._inputBuffer[this._inputPtr] == '/') {
                  ++this._inputPtr;
                  return;
               }
            }
         }
      }

      this._reportInvalidEOF(" in a comment", (JsonToken)null);
   }

   private boolean _skipYAMLComment() throws IOException {
      if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
         return false;
      } else {
         this._skipLine();
         return true;
      }
   }

   private void _skipLine() throws IOException {
      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         int i = this._inputBuffer[this._inputPtr++];
         if (i < 32) {
            if (i == 10) {
               ++this._currInputRow;
               this._currInputRowStart = this._inputPtr;
               break;
            } else if (i != 13) {
               if (i != 9) {
                  this._throwInvalidSpace(i);
               }
            } else {
               this._skipCR();
               break;
            }
         }
      }

   }

   protected char _decodeEscaped() throws IOException {
      if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
         this._reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
      }

      char c = this._inputBuffer[this._inputPtr++];
      switch (c) {
         case '"':
         case '/':
         case '\\':
            return c;
         case 'b':
            return '\b';
         case 'f':
            return '\f';
         case 'n':
            return '\n';
         case 'r':
            return '\r';
         case 't':
            return '\t';
         case 'u':
            int value = 0;

            for(int i = 0; i < 4; ++i) {
               if (this._inputPtr >= this._inputEnd && !this._loadMore()) {
                  this._reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
               }

               int ch = this._inputBuffer[this._inputPtr++];
               int digit = CharTypes.charToHex(ch);
               if (digit < 0) {
                  this._reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
               }

               value = value << 4 | digit;
            }

            return (char)value;
         default:
            return this._handleUnrecognizedCharacterEscape(c);
      }
   }

   private final void _matchTrue() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 3 < this._inputEnd) {
         char[] b = this._inputBuffer;
         if (b[ptr] == 'r') {
            ++ptr;
            if (b[ptr] == 'u') {
               ++ptr;
               if (b[ptr] == 'e') {
                  ++ptr;
                  char c = b[ptr];
                  if (c < '0' || c == ']' || c == '}') {
                     this._inputPtr = ptr;
                     return;
                  }
               }
            }
         }
      }

      this._matchToken("true", 1);
   }

   private final void _matchFalse() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 4 < this._inputEnd) {
         char[] b = this._inputBuffer;
         if (b[ptr] == 'a') {
            ++ptr;
            if (b[ptr] == 'l') {
               ++ptr;
               if (b[ptr] == 's') {
                  ++ptr;
                  if (b[ptr] == 'e') {
                     ++ptr;
                     char c = b[ptr];
                     if (c < '0' || c == ']' || c == '}') {
                        this._inputPtr = ptr;
                        return;
                     }
                  }
               }
            }
         }
      }

      this._matchToken("false", 1);
   }

   private final void _matchNull() throws IOException {
      int ptr = this._inputPtr;
      if (ptr + 3 < this._inputEnd) {
         char[] b = this._inputBuffer;
         if (b[ptr] == 'u') {
            ++ptr;
            if (b[ptr] == 'l') {
               ++ptr;
               if (b[ptr] == 'l') {
                  ++ptr;
                  char c = b[ptr];
                  if (c < '0' || c == ']' || c == '}') {
                     this._inputPtr = ptr;
                     return;
                  }
               }
            }
         }
      }

      this._matchToken("null", 1);
   }

   protected final void _matchToken(String matchStr, int i) throws IOException {
      int len = matchStr.length();
      if (this._inputPtr + len >= this._inputEnd) {
         this._matchToken2(matchStr, i);
      } else {
         do {
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
               this._reportInvalidToken(matchStr.substring(0, i));
            }

            ++this._inputPtr;
            ++i;
         } while(i < len);

         int ch = this._inputBuffer[this._inputPtr];
         if (ch >= 48 && ch != 93 && ch != 125) {
            this._checkMatchEnd(matchStr, i, ch);
         }

      }
   }

   private final void _matchToken2(String matchStr, int i) throws IOException {
      int len = matchStr.length();

      do {
         if (this._inputPtr >= this._inputEnd && !this._loadMore() || this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
            this._reportInvalidToken(matchStr.substring(0, i));
         }

         ++this._inputPtr;
         ++i;
      } while(i < len);

      if (this._inputPtr < this._inputEnd || this._loadMore()) {
         int ch = this._inputBuffer[this._inputPtr];
         if (ch >= 48 && ch != 93 && ch != 125) {
            this._checkMatchEnd(matchStr, i, ch);
         }

      }
   }

   private final void _checkMatchEnd(String matchStr, int i, int c) throws IOException {
      char ch = (char)c;
      if (Character.isJavaIdentifierPart(ch)) {
         this._reportInvalidToken(matchStr.substring(0, i));
      }

   }

   protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
      ByteArrayBuilder builder = this._getByteArrayBuilder();

      while(true) {
         if (this._inputPtr >= this._inputEnd) {
            this._loadMoreGuaranteed();
         }

         char ch = this._inputBuffer[this._inputPtr++];
         if (ch > ' ') {
            int bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (ch == '"') {
                  return builder.toByteArray();
               }

               bits = this._decodeBase64Escape(b64variant, ch, 0);
               if (bits < 0) {
                  continue;
               }
            }

            int decodedData = bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++];
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               bits = this._decodeBase64Escape(b64variant, ch, 1);
            }

            decodedData = decodedData << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++];
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  if (ch == '"') {
                     decodedData >>= 4;
                     builder.append(decodedData);
                     if (b64variant.requiresPaddingOnRead()) {
                        --this._inputPtr;
                        this._handleBase64MissingPadding(b64variant);
                     }

                     return builder.toByteArray();
                  }

                  bits = this._decodeBase64Escape(b64variant, ch, 2);
               }

               if (bits == -2) {
                  if (this._inputPtr >= this._inputEnd) {
                     this._loadMoreGuaranteed();
                  }

                  ch = this._inputBuffer[this._inputPtr++];
                  if (!b64variant.usesPaddingChar(ch) && this._decodeBase64Escape(b64variant, ch, 3) != -2) {
                     throw this.reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                  }

                  decodedData >>= 4;
                  builder.append(decodedData);
                  continue;
               }
            }

            decodedData = decodedData << 6 | bits;
            if (this._inputPtr >= this._inputEnd) {
               this._loadMoreGuaranteed();
            }

            ch = this._inputBuffer[this._inputPtr++];
            bits = b64variant.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  if (ch == '"') {
                     decodedData >>= 2;
                     builder.appendTwoBytes(decodedData);
                     if (b64variant.requiresPaddingOnRead()) {
                        --this._inputPtr;
                        this._handleBase64MissingPadding(b64variant);
                     }

                     return builder.toByteArray();
                  }

                  bits = this._decodeBase64Escape(b64variant, ch, 3);
               }

               if (bits == -2) {
                  decodedData >>= 2;
                  builder.appendTwoBytes(decodedData);
                  continue;
               }
            }

            decodedData = decodedData << 6 | bits;
            builder.appendThreeBytes(decodedData);
         }
      }
   }

   public JsonLocation currentLocation() {
      int col = this._inputPtr - this._currInputRowStart + 1;
      return new JsonLocation(this._contentReference(), -1L, this._currInputProcessed + (long)this._inputPtr, this._currInputRow, col);
   }

   protected JsonLocation _currentLocationMinusOne() {
      int prevInputPtr = this._inputPtr - 1;
      int col = prevInputPtr - this._currInputRowStart + 1;
      return new JsonLocation(this._contentReference(), -1L, this._currInputProcessed + (long)prevInputPtr, this._currInputRow, col);
   }

   public JsonLocation currentTokenLocation() {
      if (this._currToken == JsonToken.FIELD_NAME) {
         long total = this._currInputProcessed + (this._nameStartOffset - 1L);
         return new JsonLocation(this._contentReference(), -1L, total, this._nameStartRow, this._nameStartCol);
      } else {
         return new JsonLocation(this._contentReference(), -1L, this._tokenInputTotal - 1L, this._tokenInputRow, this._tokenInputCol);
      }
   }

   private final void _updateLocation() {
      int ptr = this._inputPtr;
      this._tokenInputTotal = this._currInputProcessed + (long)ptr;
      this._tokenInputRow = this._currInputRow;
      this._tokenInputCol = ptr - this._currInputRowStart;
   }

   private final void _updateNameLocation() {
      int ptr = this._inputPtr;
      this._nameStartOffset = (long)ptr;
      this._nameStartRow = this._currInputRow;
      this._nameStartCol = ptr - this._currInputRowStart;
   }

   protected void _reportInvalidToken(String matchedPart) throws IOException {
      this._reportInvalidToken(matchedPart, this._validJsonTokenList());
   }

   protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
      int tokenStartPtr = this._inputPtr - matchedPart.length();
      int col = tokenStartPtr - this._currInputRowStart + 1;
      JsonLocation loc = new JsonLocation(this._contentReference(), -1L, this._currInputProcessed + (long)tokenStartPtr, this._currInputRow, col);
      StringBuilder sb = new StringBuilder(matchedPart);

      while(this._inputPtr < this._inputEnd || this._loadMore()) {
         char c = this._inputBuffer[this._inputPtr];
         if (!Character.isJavaIdentifierPart(c)) {
            break;
         }

         ++this._inputPtr;
         sb.append(c);
         if (sb.length() >= this._ioContext.errorReportConfiguration().getMaxErrorTokenLength()) {
            sb.append("...");
            break;
         }
      }

      String fullMsg = String.format("Unrecognized token '%s': was expecting %s", sb, msg);
      throw this._constructReadException(fullMsg, loc);
   }

   private void _closeScope(int i) throws IOException {
      if (i == 93) {
         this._updateLocation();
         if (!this._parsingContext.inArray()) {
            this._reportMismatchedEndMarker(i, '}');
         }

         this._parsingContext = this._parsingContext.clearAndGetParent();
         this._updateToken(JsonToken.END_ARRAY);
      }

      if (i == 125) {
         this._updateLocation();
         if (!this._parsingContext.inObject()) {
            this._reportMismatchedEndMarker(i, ']');
         }

         this._parsingContext = this._parsingContext.clearAndGetParent();
         this._updateToken(JsonToken.END_OBJECT);
      }

   }
}
