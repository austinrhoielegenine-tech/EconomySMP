package com.fasterxml.jackson.core.internal.shaded.fdp.v2_21_2;

final class JavaFloatBitsFromCharArray extends AbstractJavaFloatingPointBitsFromCharArray {
   public JavaFloatBitsFromCharArray() {
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

   long valueOfFloatLiteral(char[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      float result = FastFloatMath.tryDecFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return Float.isNaN(result) ? (long)Float.floatToRawIntBits(Float.parseFloat(new String(str, startIndex, endIndex - startIndex))) : (long)Float.floatToRawIntBits(result);
   }

   long valueOfHexLiteral(char[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      float d = FastFloatMath.tryHexFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return (long)Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex)) : d);
   }
}
