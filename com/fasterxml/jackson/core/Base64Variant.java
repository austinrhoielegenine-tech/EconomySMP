package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import java.io.Serializable;
import java.util.Arrays;

public final class Base64Variant implements Serializable {
   private static final int INT_SPACE = 32;
   private static final long serialVersionUID = 1L;
   protected static final char PADDING_CHAR_NONE = '\u0000';
   public static final int BASE64_VALUE_INVALID = -1;
   public static final int BASE64_VALUE_PADDING = -2;
   private final transient int[] _asciiToBase64;
   private final transient char[] _base64ToAsciiC;
   private final transient byte[] _base64ToAsciiB;
   final String _name;
   private final char _paddingChar;
   private final int _maxLineLength;
   private final boolean _writePadding;
   private final PaddingReadBehaviour _paddingReadBehaviour;

   public Base64Variant(String name, String base64Alphabet, boolean writePadding, char paddingChar, int maxLineLength) {
      this._asciiToBase64 = new int[128];
      this._base64ToAsciiC = new char[64];
      this._base64ToAsciiB = new byte[64];
      this._name = name;
      this._writePadding = writePadding;
      this._paddingChar = paddingChar;
      this._maxLineLength = maxLineLength;
      int alphaLen = base64Alphabet.length();
      if (alphaLen != 64) {
         throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
      } else {
         base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
         Arrays.fill(this._asciiToBase64, -1);

         char alpha;
         for(int i = 0; i < alphaLen; this._asciiToBase64[alpha] = i++) {
            alpha = this._base64ToAsciiC[i];
            this._base64ToAsciiB[i] = (byte)alpha;
         }

         if (writePadding) {
            this._asciiToBase64[paddingChar] = -2;
         }

         this._paddingReadBehaviour = writePadding ? Base64Variant.PaddingReadBehaviour.PADDING_REQUIRED : Base64Variant.PaddingReadBehaviour.PADDING_FORBIDDEN;
      }
   }

   public Base64Variant(Base64Variant base, String name, int maxLineLength) {
      this(base, name, base._writePadding, base._paddingChar, maxLineLength);
   }

   public Base64Variant(Base64Variant base, String name, boolean writePadding, char paddingChar, int maxLineLength) {
      this(base, name, writePadding, paddingChar, base._paddingReadBehaviour, maxLineLength);
   }

   private Base64Variant(Base64Variant base, String name, boolean writePadding, char paddingChar, PaddingReadBehaviour paddingReadBehaviour, int maxLineLength) {
      this._asciiToBase64 = new int[128];
      this._base64ToAsciiC = new char[64];
      this._base64ToAsciiB = new byte[64];
      this._name = name;
      byte[] srcB = base._base64ToAsciiB;
      System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
      char[] srcC = base._base64ToAsciiC;
      System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
      int[] srcV = base._asciiToBase64;
      System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
      this._writePadding = writePadding;
      this._paddingChar = paddingChar;
      this._maxLineLength = maxLineLength;
      this._paddingReadBehaviour = paddingReadBehaviour;
   }

   private Base64Variant(Base64Variant base, PaddingReadBehaviour paddingReadBehaviour) {
      this(base, base._name, base._writePadding, base._paddingChar, paddingReadBehaviour, base._maxLineLength);
   }

   public Base64Variant withPaddingAllowed() {
      return this.withReadPadding(Base64Variant.PaddingReadBehaviour.PADDING_ALLOWED);
   }

   public Base64Variant withPaddingRequired() {
      return this.withReadPadding(Base64Variant.PaddingReadBehaviour.PADDING_REQUIRED);
   }

   public Base64Variant withPaddingForbidden() {
      return this.withReadPadding(Base64Variant.PaddingReadBehaviour.PADDING_FORBIDDEN);
   }

   public Base64Variant withReadPadding(PaddingReadBehaviour readPadding) {
      return readPadding == this._paddingReadBehaviour ? this : new Base64Variant(this, readPadding);
   }

   public Base64Variant withWritePadding(boolean writePadding) {
      return writePadding == this._writePadding ? this : new Base64Variant(this, this._name, writePadding, this._paddingChar, this._maxLineLength);
   }

   protected Object readResolve() {
      Base64Variant base = Base64Variants.valueOf(this._name);
      return this._writePadding == base._writePadding && this._paddingChar == base._paddingChar && this._paddingReadBehaviour == base._paddingReadBehaviour && this._maxLineLength == base._maxLineLength ? base : new Base64Variant(base, this._name, this._writePadding, this._paddingChar, this._paddingReadBehaviour, this._maxLineLength);
   }

   public String getName() {
      return this._name;
   }

   public boolean usesPadding() {
      return this._writePadding;
   }

   public boolean requiresPaddingOnRead() {
      return this._paddingReadBehaviour == Base64Variant.PaddingReadBehaviour.PADDING_REQUIRED;
   }

   public boolean acceptsPaddingOnRead() {
      return this._paddingReadBehaviour != Base64Variant.PaddingReadBehaviour.PADDING_FORBIDDEN;
   }

   public boolean usesPaddingChar(char c) {
      return c == this._paddingChar;
   }

   public boolean usesPaddingChar(int ch) {
      return ch == this._paddingChar;
   }

   public PaddingReadBehaviour paddingReadBehaviour() {
      return this._paddingReadBehaviour;
   }

   public char getPaddingChar() {
      return this._paddingChar;
   }

   public byte getPaddingByte() {
      return (byte)this._paddingChar;
   }

   public int getMaxLineLength() {
      return this._maxLineLength;
   }

   public int decodeBase64Char(char c) {
      return c <= 127 ? this._asciiToBase64[c] : -1;
   }

   public int decodeBase64Char(int ch) {
      return ch <= 127 ? this._asciiToBase64[ch] : -1;
   }

   public int decodeBase64Byte(byte b) {
      return b < 0 ? -1 : this._asciiToBase64[b];
   }

   public char encodeBase64BitsAsChar(int value) {
      return this._base64ToAsciiC[value];
   }

   public int encodeBase64Chunk(int b24, char[] buffer, int outPtr) {
      buffer[outPtr++] = this._base64ToAsciiC[b24 >> 18 & 63];
      buffer[outPtr++] = this._base64ToAsciiC[b24 >> 12 & 63];
      buffer[outPtr++] = this._base64ToAsciiC[b24 >> 6 & 63];
      buffer[outPtr++] = this._base64ToAsciiC[b24 & 63];
      return outPtr;
   }

   public void encodeBase64Chunk(StringBuilder sb, int b24) {
      sb.append(this._base64ToAsciiC[b24 >> 18 & 63]);
      sb.append(this._base64ToAsciiC[b24 >> 12 & 63]);
      sb.append(this._base64ToAsciiC[b24 >> 6 & 63]);
      sb.append(this._base64ToAsciiC[b24 & 63]);
   }

   public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr) {
      buffer[outPtr++] = this._base64ToAsciiC[bits >> 18 & 63];
      buffer[outPtr++] = this._base64ToAsciiC[bits >> 12 & 63];
      if (this.usesPadding()) {
         buffer[outPtr++] = outputBytes == 2 ? this._base64ToAsciiC[bits >> 6 & 63] : this._paddingChar;
         buffer[outPtr++] = this._paddingChar;
      } else if (outputBytes == 2) {
         buffer[outPtr++] = this._base64ToAsciiC[bits >> 6 & 63];
      }

      return outPtr;
   }

   public void encodeBase64Partial(StringBuilder sb, int bits, int outputBytes) {
      sb.append(this._base64ToAsciiC[bits >> 18 & 63]);
      sb.append(this._base64ToAsciiC[bits >> 12 & 63]);
      if (this.usesPadding()) {
         sb.append(outputBytes == 2 ? this._base64ToAsciiC[bits >> 6 & 63] : this._paddingChar);
         sb.append(this._paddingChar);
      } else if (outputBytes == 2) {
         sb.append(this._base64ToAsciiC[bits >> 6 & 63]);
      }

   }

   public byte encodeBase64BitsAsByte(int value) {
      return this._base64ToAsciiB[value];
   }

   public int encodeBase64Chunk(int b24, byte[] buffer, int outPtr) {
      buffer[outPtr++] = this._base64ToAsciiB[b24 >> 18 & 63];
      buffer[outPtr++] = this._base64ToAsciiB[b24 >> 12 & 63];
      buffer[outPtr++] = this._base64ToAsciiB[b24 >> 6 & 63];
      buffer[outPtr++] = this._base64ToAsciiB[b24 & 63];
      return outPtr;
   }

   public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr) {
      buffer[outPtr++] = this._base64ToAsciiB[bits >> 18 & 63];
      buffer[outPtr++] = this._base64ToAsciiB[bits >> 12 & 63];
      if (this.usesPadding()) {
         byte pb = (byte)this._paddingChar;
         buffer[outPtr++] = outputBytes == 2 ? this._base64ToAsciiB[bits >> 6 & 63] : pb;
         buffer[outPtr++] = pb;
      } else if (outputBytes == 2) {
         buffer[outPtr++] = this._base64ToAsciiB[bits >> 6 & 63];
      }

      return outPtr;
   }

   public String encode(byte[] input) {
      return this.encode(input, false);
   }

   public String encode(byte[] input, boolean addQuotes) {
      int inputEnd = input.length;
      StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
      if (addQuotes) {
         sb.append('"');
      }

      int chunksBeforeLF = this.getMaxLineLength() >> 2;
      int inputPtr = 0;
      int safeInputEnd = inputEnd - 3;

      while(inputPtr <= safeInputEnd) {
         int b24 = input[inputPtr++] << 8;
         b24 |= input[inputPtr++] & 255;
         b24 = b24 << 8 | input[inputPtr++] & 255;
         this.encodeBase64Chunk(sb, b24);
         --chunksBeforeLF;
         if (chunksBeforeLF <= 0) {
            sb.append('\\');
            sb.append('n');
            chunksBeforeLF = this.getMaxLineLength() >> 2;
         }
      }

      int inputLeft = inputEnd - inputPtr;
      if (inputLeft > 0) {
         int b24 = input[inputPtr++] << 16;
         if (inputLeft == 2) {
            b24 |= (input[inputPtr++] & 255) << 8;
         }

         this.encodeBase64Partial(sb, b24, inputLeft);
      }

      if (addQuotes) {
         sb.append('"');
      }

      return sb.toString();
   }

   public String encode(byte[] input, boolean addQuotes, String linefeed) {
      int inputEnd = input.length;
      StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
      if (addQuotes) {
         sb.append('"');
      }

      int chunksBeforeLF = this.getMaxLineLength() >> 2;
      int inputPtr = 0;
      int safeInputEnd = inputEnd - 3;

      while(inputPtr <= safeInputEnd) {
         int b24 = input[inputPtr++] << 8;
         b24 |= input[inputPtr++] & 255;
         b24 = b24 << 8 | input[inputPtr++] & 255;
         this.encodeBase64Chunk(sb, b24);
         --chunksBeforeLF;
         if (chunksBeforeLF <= 0) {
            sb.append(linefeed);
            chunksBeforeLF = this.getMaxLineLength() >> 2;
         }
      }

      int inputLeft = inputEnd - inputPtr;
      if (inputLeft > 0) {
         int b24 = input[inputPtr++] << 16;
         if (inputLeft == 2) {
            b24 |= (input[inputPtr++] & 255) << 8;
         }

         this.encodeBase64Partial(sb, b24, inputLeft);
      }

      if (addQuotes) {
         sb.append('"');
      }

      return sb.toString();
   }

   public byte[] decode(String input) throws IllegalArgumentException {
      ByteArrayBuilder b = new ByteArrayBuilder();
      this.decode(input, b);
      return b.toByteArray();
   }

   public void decode(String str, ByteArrayBuilder builder) throws IllegalArgumentException {
      int ptr = 0;
      int len = str.length();

      while(ptr < len) {
         char ch = str.charAt(ptr++);
         if (ch > ' ') {
            int bits = this.decodeBase64Char(ch);
            if (bits < 0) {
               this._reportInvalidBase64(ch, 0, (String)null);
            }

            if (ptr >= len) {
               this._reportBase64EOF();
            }

            ch = str.charAt(ptr++);
            bits = this.decodeBase64Char(ch);
            if (bits < 0) {
               this._reportInvalidBase64(ch, 1, (String)null);
            }

            int var17 = bits << 6 | bits;
            if (ptr >= len) {
               if (!this.requiresPaddingOnRead()) {
                  var17 >>= 4;
                  builder.append(var17);
                  break;
               }

               this._reportBase64EOF();
            }

            ch = str.charAt(ptr++);
            bits = this.decodeBase64Char(ch);
            if (bits < 0) {
               if (bits != -2) {
                  this._reportInvalidBase64(ch, 2, (String)null);
               }

               if (!this.acceptsPaddingOnRead()) {
                  this._reportBase64UnexpectedPadding();
               }

               if (ptr >= len) {
                  this._reportBase64EOF();
               }

               ch = str.charAt(ptr++);
               if (!this.usesPaddingChar(ch)) {
                  this._reportInvalidBase64(ch, 3, "expected padding character '" + this.getPaddingChar() + "'");
               }

               var17 >>= 4;
               builder.append(var17);
            } else {
               var17 = var17 << 6 | bits;
               if (ptr >= len) {
                  if (!this.requiresPaddingOnRead()) {
                     var17 >>= 2;
                     builder.appendTwoBytes(var17);
                     break;
                  }

                  this._reportBase64EOF();
               }

               ch = str.charAt(ptr++);
               bits = this.decodeBase64Char(ch);
               if (bits < 0) {
                  if (bits != -2) {
                     this._reportInvalidBase64(ch, 3, (String)null);
                  }

                  if (!this.acceptsPaddingOnRead()) {
                     this._reportBase64UnexpectedPadding();
                  }

                  var17 >>= 2;
                  builder.appendTwoBytes(var17);
               } else {
                  var17 = var17 << 6 | bits;
                  builder.appendThreeBytes(var17);
               }
            }
         }
      }

   }

   public String toString() {
      return this._name;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o != null && o.getClass() == this.getClass()) {
         Base64Variant other = (Base64Variant)o;
         return other._paddingChar == this._paddingChar && other._maxLineLength == this._maxLineLength && other._writePadding == this._writePadding && other._paddingReadBehaviour == this._paddingReadBehaviour && this._name.equals(other._name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this._name.hashCode();
   }

   protected void _reportInvalidBase64(char ch, int bindex, String msg) throws IllegalArgumentException {
      String base;
      if (ch <= ' ') {
         base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
      } else if (this.usesPaddingChar(ch)) {
         base = "Unexpected padding character ('" + this.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
      } else if (Character.isDefined(ch) && !Character.isISOControl(ch)) {
         base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      } else {
         base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
      }

      if (msg != null) {
         base = base + ": " + msg;
      }

      throw new IllegalArgumentException(base);
   }

   protected void _reportBase64EOF() throws IllegalArgumentException {
      throw new IllegalArgumentException(this.missingPaddingMessage());
   }

   protected void _reportBase64UnexpectedPadding() throws IllegalArgumentException {
      throw new IllegalArgumentException(this.unexpectedPaddingMessage());
   }

   protected String unexpectedPaddingMessage() {
      return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects no padding at the end while decoding. This Base64Variant might have been incorrectly configured", this.getName());
   }

   public String missingPaddingMessage() {
      return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects padding (one or more '%c' characters) at the end. This Base64Variant might have been incorrectly configured", this.getName(), this.getPaddingChar());
   }

   public static enum PaddingReadBehaviour {
      PADDING_FORBIDDEN,
      PADDING_REQUIRED,
      PADDING_ALLOWED;
   }
}
