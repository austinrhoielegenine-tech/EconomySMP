package com.fasterxml.jackson.core.internal.shaded.fdp.v2_21_2;

final class JavaFloatBitsFromCharSequence extends AbstractJavaFloatingPointBitsFromCharSequence {
   public JavaFloatBitsFromCharSequence() {
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

   long valueOfFloatLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      float d = FastFloatMath.tryDecFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return (long)Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.subSequence(startIndex, endIndex).toString()) : d);
   }

   long valueOfHexLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
      float d = FastFloatMath.tryHexFloatToFloatTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
      return (long)Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.subSequence(startIndex, endIndex).toString()) : d);
   }
}
