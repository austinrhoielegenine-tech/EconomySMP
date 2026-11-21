package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.util.Arrays;

public final class JsonStringEncoder {
   private static final char[] HC = CharTypes.copyHexChars(true);
   private static final byte[] HB = CharTypes.copyHexBytes(true);
   private static final int SURR1_FIRST = 55296;
   private static final int SURR1_LAST = 56319;
   private static final int SURR2_FIRST = 56320;
   private static final int SURR2_LAST = 57343;
   static final int MIN_CHAR_BUFFER_SIZE = 16;
   static final int MAX_CHAR_BUFFER_SIZE = 32000;
   static final int MIN_BYTE_BUFFER_SIZE = 24;
   static final int MAX_BYTE_BUFFER_SIZE = 32000;
   private static final JsonStringEncoder instance = new JsonStringEncoder();

   public static JsonStringEncoder getInstance() {
      return instance;
   }

   public char[] quoteAsString(String input) {
      int inputLen = input.length();
      char[] outputBuffer = new char[_initialCharBufSize(inputLen)];
      int[] escCodes = CharTypes.get7BitOutputEscapes();
      int escCodeCount = escCodes.length;
      int inPtr = 0;
      TextBuffer textBuffer = null;
      int outPtr = 0;
      char[] qbuf = null;

      label76:
      while(inPtr < inputLen) {
         while(true) {
            char c = input.charAt(inPtr);
            if (c < escCodeCount && escCodes[c] != 0) {
               if (qbuf == null) {
                  qbuf = this._qbuf();
               }

               c = input.charAt(inPtr++);
               int escCode = escCodes[c];
               int length = escCode < 0 ? this._appendNumeric(c, qbuf) : this._appendNamed(escCode, qbuf);
               if (outPtr + length > outputBuffer.length) {
                  int first = outputBuffer.length - outPtr;
                  if (first > 0) {
                     System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
                  }

                  if (textBuffer == null) {
                     textBuffer = TextBuffer.fromInitial(outputBuffer);
                  }

                  try {
                     outputBuffer = textBuffer.finishCurrentSegment();
                  } catch (IOException e) {
                     throw new IllegalStateException(e);
                  }

                  int second = length - first;
                  System.arraycopy(qbuf, first, outputBuffer, 0, second);
                  outPtr = second;
               } else {
                  System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
                  outPtr += length;
               }
               break;
            }

            if (outPtr >= outputBuffer.length) {
               if (textBuffer == null) {
                  textBuffer = TextBuffer.fromInitial(outputBuffer);
               }

               try {
                  outputBuffer = textBuffer.finishCurrentSegment();
               } catch (IOException e) {
                  throw new IllegalStateException(e);
               }

               outPtr = 0;
            }

            outputBuffer[outPtr++] = c;
            ++inPtr;
            if (inPtr >= inputLen) {
               break label76;
            }
         }
      }

      if (textBuffer == null) {
         return Arrays.copyOfRange(outputBuffer, 0, outPtr);
      } else {
         textBuffer.setCurrentLength(outPtr);

         try {
            return textBuffer.contentsAsArray();
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }
      }
   }

   public char[] quoteAsString(CharSequence input) {
      if (input instanceof String) {
         return this.quoteAsString((String)input);
      } else {
         TextBuffer textBuffer = null;
         int inputLen = input.length();
         char[] outputBuffer = new char[_initialCharBufSize(inputLen)];
         int[] escCodes = CharTypes.get7BitOutputEscapes();
         int escCodeCount = escCodes.length;
         int inPtr = 0;
         int outPtr = 0;
         char[] qbuf = null;

         label80:
         while(inPtr < inputLen) {
            while(true) {
               char c = input.charAt(inPtr);
               if (c < escCodeCount && escCodes[c] != 0) {
                  if (qbuf == null) {
                     qbuf = this._qbuf();
                  }

                  c = input.charAt(inPtr++);
                  int escCode = escCodes[c];
                  int length = escCode < 0 ? this._appendNumeric(c, qbuf) : this._appendNamed(escCode, qbuf);
                  if (outPtr + length > outputBuffer.length) {
                     int first = outputBuffer.length - outPtr;
                     if (first > 0) {
                        System.arraycopy(qbuf, 0, outputBuffer, outPtr, first);
                     }

                     if (textBuffer == null) {
                        textBuffer = TextBuffer.fromInitial(outputBuffer);
                     }

                     try {
                        outputBuffer = textBuffer.finishCurrentSegment();
                     } catch (IOException e) {
                        throw new IllegalStateException(e);
                     }

                     int second = length - first;
                     System.arraycopy(qbuf, first, outputBuffer, 0, second);
                     outPtr = second;
                  } else {
                     System.arraycopy(qbuf, 0, outputBuffer, outPtr, length);
                     outPtr += length;
                  }
                  break;
               }

               if (outPtr >= outputBuffer.length) {
                  if (textBuffer == null) {
                     textBuffer = TextBuffer.fromInitial(outputBuffer);
                  }

                  try {
                     outputBuffer = textBuffer.finishCurrentSegment();
                  } catch (IOException e) {
                     throw new IllegalStateException(e);
                  }

                  outPtr = 0;
               }

               outputBuffer[outPtr++] = c;
               ++inPtr;
               if (inPtr >= inputLen) {
                  break label80;
               }
            }
         }

         if (textBuffer == null) {
            return Arrays.copyOfRange(outputBuffer, 0, outPtr);
         } else {
            textBuffer.setCurrentLength(outPtr);

            try {
               return textBuffer.contentsAsArray();
            } catch (IOException e) {
               throw new IllegalStateException(e);
            }
         }
      }
   }

   public void quoteAsString(CharSequence input, StringBuilder output) {
      int[] escCodes = CharTypes.get7BitOutputEscapes();
      int escCodeCount = escCodes.length;
      int inPtr = 0;
      int inputLen = input.length();
      char[] qbuf = null;

      while(inPtr < inputLen) {
         char c = input.charAt(inPtr);
         if (c < escCodeCount && escCodes[c] != 0) {
            if (qbuf == null) {
               qbuf = this._qbuf();
            }

            c = input.charAt(inPtr++);
            int escCode = escCodes[c];
            int length = escCode < 0 ? this._appendNumeric(c, qbuf) : this._appendNamed(escCode, qbuf);
            output.append(qbuf, 0, length);
         } else {
            output.append(c);
            ++inPtr;
            if (inPtr >= inputLen) {
               return;
            }
         }
      }

   }

   public byte[] quoteAsUTF8(String text) {
      int inputPtr = 0;
      int inputEnd = text.length();
      int outputPtr = 0;
      byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
      ByteArrayBuilder bb = null;

      label91:
      while(inputPtr < inputEnd) {
         int[] escCodes = CharTypes.get7BitOutputEscapes();

         do {
            int ch = text.charAt(inputPtr);
            if (ch > 127 || escCodes[ch] != 0) {
               if (bb == null) {
                  bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
               }

               if (outputPtr >= outputBuffer.length) {
                  outputBuffer = bb.finishCurrentSegment();
                  outputPtr = 0;
               }

               ch = text.charAt(inputPtr++);
               if (ch <= 127) {
                  int escape = escCodes[ch];
                  outputPtr = this._appendByte(ch, escape, bb, outputPtr);
                  outputBuffer = bb.getCurrentSegment();
               } else {
                  if (ch <= 2047) {
                     outputBuffer[outputPtr++] = (byte)(192 | ch >> 6);
                     ch = 128 | ch & 63;
                  } else if (ch >= 55296 && ch <= 57343) {
                     if (ch > 56319) {
                        _illegal(ch);
                     }

                     if (inputPtr >= inputEnd) {
                        _illegal(ch);
                     }

                     ch = _convert(ch, text.charAt(inputPtr++));
                     if (ch > 1114111) {
                        _illegal(ch);
                     }

                     outputBuffer[outputPtr++] = (byte)(240 | ch >> 18);
                     if (outputPtr >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr = 0;
                     }

                     outputBuffer[outputPtr++] = (byte)(128 | ch >> 12 & 63);
                     if (outputPtr >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr = 0;
                     }

                     outputBuffer[outputPtr++] = (byte)(128 | ch >> 6 & 63);
                     ch = 128 | ch & 63;
                  } else {
                     outputBuffer[outputPtr++] = (byte)(224 | ch >> 12);
                     if (outputPtr >= outputBuffer.length) {
                        outputBuffer = bb.finishCurrentSegment();
                        outputPtr = 0;
                     }

                     outputBuffer[outputPtr++] = (byte)(128 | ch >> 6 & 63);
                     ch = 128 | ch & 63;
                  }

                  if (outputPtr >= outputBuffer.length) {
                     outputBuffer = bb.finishCurrentSegment();
                     outputPtr = 0;
                  }

                  outputBuffer[outputPtr++] = (byte)ch;
               }
               continue label91;
            }

            if (outputPtr >= outputBuffer.length) {
               if (bb == null) {
                  bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
               }

               outputBuffer = bb.finishCurrentSegment();
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)ch;
            ++inputPtr;
         } while(inputPtr < inputEnd);

         return bb == null ? Arrays.copyOfRange(outputBuffer, 0, outputPtr) : bb.completeAndCoalesce(outputPtr);
      }

      return bb == null ? Arrays.copyOfRange(outputBuffer, 0, outputPtr) : bb.completeAndCoalesce(outputPtr);
   }

   public byte[] encodeAsUTF8(String text) {
      int inputPtr = 0;
      int inputEnd = text.length();
      int outputPtr = 0;
      byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
      int outputEnd = outputBuffer.length;

      ByteArrayBuilder bb;
      int c;
      for(bb = null; inputPtr < inputEnd; outputBuffer[outputPtr++] = (byte)(128 | c & 63)) {
         for(c = text.charAt(inputPtr++); c <= 127; c = text.charAt(inputPtr++)) {
            if (outputPtr >= outputEnd) {
               if (bb == null) {
                  bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
               }

               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)c;
            if (inputPtr >= inputEnd) {
               return bb == null ? Arrays.copyOfRange(outputBuffer, 0, outputPtr) : bb.completeAndCoalesce(outputPtr);
            }
         }

         if (bb == null) {
            bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
         }

         if (outputPtr >= outputEnd) {
            outputBuffer = bb.finishCurrentSegment();
            outputEnd = outputBuffer.length;
            outputPtr = 0;
         }

         if (c < 2048) {
            outputBuffer[outputPtr++] = (byte)(192 | c >> 6);
         } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
               _illegal(c);
            }

            if (inputPtr >= inputEnd) {
               _illegal(c);
            }

            c = _convert(c, text.charAt(inputPtr++));
            if (c > 1114111) {
               _illegal(c);
            }

            outputBuffer[outputPtr++] = (byte)(240 | c >> 18);
            if (outputPtr >= outputEnd) {
               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 12 & 63);
            if (outputPtr >= outputEnd) {
               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 6 & 63);
         } else {
            outputBuffer[outputPtr++] = (byte)(224 | c >> 12);
            if (outputPtr >= outputEnd) {
               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 6 & 63);
         }

         if (outputPtr >= outputEnd) {
            outputBuffer = bb.finishCurrentSegment();
            outputEnd = outputBuffer.length;
            outputPtr = 0;
         }
      }

      return bb == null ? Arrays.copyOfRange(outputBuffer, 0, outputPtr) : bb.completeAndCoalesce(outputPtr);
   }

   public byte[] encodeAsUTF8(CharSequence text) {
      int inputPtr = 0;
      int inputEnd = text.length();
      int outputPtr = 0;
      byte[] outputBuffer = new byte[_initialByteBufSize(inputEnd)];
      int outputEnd = outputBuffer.length;

      ByteArrayBuilder bb;
      int c;
      for(bb = null; inputPtr < inputEnd; outputBuffer[outputPtr++] = (byte)(128 | c & 63)) {
         for(c = text.charAt(inputPtr++); c <= 127; c = text.charAt(inputPtr++)) {
            if (outputPtr >= outputEnd) {
               if (bb == null) {
                  bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
               }

               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)c;
            if (inputPtr >= inputEnd) {
               return bb == null ? Arrays.copyOfRange(outputBuffer, 0, outputPtr) : bb.completeAndCoalesce(outputPtr);
            }
         }

         if (bb == null) {
            bb = ByteArrayBuilder.fromInitial(outputBuffer, outputPtr);
         }

         if (outputPtr >= outputEnd) {
            outputBuffer = bb.finishCurrentSegment();
            outputEnd = outputBuffer.length;
            outputPtr = 0;
         }

         if (c < 2048) {
            outputBuffer[outputPtr++] = (byte)(192 | c >> 6);
         } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
               _illegal(c);
            }

            if (inputPtr >= inputEnd) {
               _illegal(c);
            }

            c = _convert(c, text.charAt(inputPtr++));
            if (c > 1114111) {
               _illegal(c);
            }

            outputBuffer[outputPtr++] = (byte)(240 | c >> 18);
            if (outputPtr >= outputEnd) {
               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 12 & 63);
            if (outputPtr >= outputEnd) {
               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 6 & 63);
         } else {
            outputBuffer[outputPtr++] = (byte)(224 | c >> 12);
            if (outputPtr >= outputEnd) {
               outputBuffer = bb.finishCurrentSegment();
               outputEnd = outputBuffer.length;
               outputPtr = 0;
            }

            outputBuffer[outputPtr++] = (byte)(128 | c >> 6 & 63);
         }

         if (outputPtr >= outputEnd) {
            outputBuffer = bb.finishCurrentSegment();
            outputEnd = outputBuffer.length;
            outputPtr = 0;
         }
      }

      return bb == null ? Arrays.copyOfRange(outputBuffer, 0, outputPtr) : bb.completeAndCoalesce(outputPtr);
   }

   private char[] _qbuf() {
      char[] qbuf = new char[6];
      qbuf[0] = '\\';
      qbuf[2] = '0';
      qbuf[3] = '0';
      return qbuf;
   }

   private int _appendNumeric(int value, char[] qbuf) {
      qbuf[1] = 'u';
      qbuf[4] = HC[value >> 4];
      qbuf[5] = HC[value & 15];
      return 6;
   }

   private int _appendNamed(int esc, char[] qbuf) {
      qbuf[1] = (char)esc;
      return 2;
   }

   private int _appendByte(int ch, int esc, ByteArrayBuilder bb, int ptr) {
      bb.setCurrentSegmentLength(ptr);
      bb.append(92);
      if (esc < 0) {
         bb.append(117);
         if (ch > 255) {
            int hi = ch >> 8;
            bb.append(HB[hi >> 4]);
            bb.append(HB[hi & 15]);
            ch &= 255;
         } else {
            bb.append(48);
            bb.append(48);
         }

         bb.append(HB[ch >> 4]);
         bb.append(HB[ch & 15]);
      } else {
         bb.append((byte)esc);
      }

      return bb.getCurrentSegmentLength();
   }

   private static int _convert(int p1, int p2) {
      if (p2 >= 56320 && p2 <= 57343) {
         return (p1 << 10) + p2 + -56613888;
      } else {
         throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
      }
   }

   private static void _illegal(int c) {
      throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
   }

   static int _initialCharBufSize(int strLen) {
      int estimated = Math.max(16, strLen + Math.min(6 + (strLen >> 3), 1000));
      return Math.min(estimated, 32000);
   }

   static int _initialByteBufSize(int strLen) {
      int doubled = Math.max(24, strLen + 6 + (strLen >> 1));
      return Math.min(doubled, 32000);
   }
}
