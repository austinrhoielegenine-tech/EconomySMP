package com.fasterxml.jackson.core.internal.shaded.fdp.v2_21_2;

import java.nio.charset.StandardCharsets;

final class JavaFloatBitsFromByteArray extends AbstractJavaFloatingPointBitsFromByteArray {
   public JavaFloatBitsFromByteArray() {
   }

   long nan() {
      return (long)Float.floatToRawIntBits(Float.NaN);
   }

   long negativeInfinity() {
      return (long)Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
   }

   long positiveInfinity() {
      return (long)Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
   }

   long valueOfFloatLiteral(byte[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      float result = FastFloatMath.tryDecFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return (long)Float.floatToRawIntBits(Float.isNaN(result) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex, StandardCharsets.ISO_8859_1)) : result);
   }

   long valueOfHexLiteral(byte[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      float d = FastFloatMath.tryHexFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return (long)Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex, StandardCharsets.ISO_8859_1)) : d);
   }
}
