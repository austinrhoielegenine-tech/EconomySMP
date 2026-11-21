package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.internal.shaded.fdp.v2_21_2.JavaBigDecimalParser;
import java.math.BigDecimal;

public final class BigDecimalParser {
   static final int MAX_CHARS_TO_REPORT = 1000;
   private static final int SIZE_FOR_SWITCH_TO_FASTDOUBLEPARSER = 500;

   private BigDecimalParser() {
   }

   public static BigDecimal parse(String valueStr) {
      try {
         return valueStr.length() < 500 ? new BigDecimal(valueStr) : JavaBigDecimalParser.parseBigDecimal((CharSequence)valueStr);
      } catch (NumberFormatException | ArithmeticException e) {
         throw _parseFailure(e, valueStr);
      }
   }

   public static BigDecimal parse(char[] chars, int off, int len) {
      try {
         return len < 500 ? new BigDecimal(chars, off, len) : JavaBigDecimalParser.parseBigDecimal(chars, off, len);
      } catch (NumberFormatException | ArithmeticException e) {
         throw _parseFailure(e, chars, off, len);
      }
   }

   public static BigDecimal parse(char[] chars) {
      return parse(chars, 0, chars.length);
   }

   public static BigDecimal parseWithFastParser(String valueStr) {
      try {
         return JavaBigDecimalParser.parseBigDecimal((CharSequence)valueStr);
      } catch (NumberFormatException | ArithmeticException e) {
         throw _parseFailure(e, valueStr);
      }
   }

   public static BigDecimal parseWithFastParser(char[] ch, int off, int len) {
      try {
         return JavaBigDecimalParser.parseBigDecimal(ch, off, len);
      } catch (NumberFormatException | ArithmeticException e) {
         throw _parseFailure(e, ch, off, len);
      }
   }

   private static NumberFormatException _parseFailure(Exception e, String fullValue) {
      String desc = e.getMessage();
      if (desc == null) {
         desc = "Not a valid number representation";
      }

      String valueToReport = _getValueDesc(fullValue);
      return new NumberFormatException(_generateExceptionMessage(valueToReport, desc));
   }

   private static NumberFormatException _parseFailure(Exception e, char[] array, int offset, int len) {
      String desc = e.getMessage();
      if (desc == null) {
         desc = "Not a valid number representation";
      }

      String valueToReport = _getValueDesc(array, offset, len);
      return new NumberFormatException(_generateExceptionMessage(valueToReport, desc));
   }

   private static String _getValueDesc(String fullValue) {
      int len = fullValue.length();
      return len <= 1000 ? String.format("\"%s\"", fullValue) : String.format("\"%s\" (truncated to %d chars (from %d))", fullValue.substring(0, 1000), 1000, len);
   }

   private static String _getValueDesc(char[] array, int offset, int len) {
      return len <= 1000 ? String.format("\"%s\"", new String(array, offset, len)) : String.format("\"%s\" (truncated to %d chars (from %d))", new String(array, offset, 1000), 1000, len);
   }

   private static String _generateExceptionMessage(String valueToReport, String desc) {
      return String.format("Value %s can not be deserialized as `java.math.BigDecimal`, reason:  %s", valueToReport, desc);
   }
}
