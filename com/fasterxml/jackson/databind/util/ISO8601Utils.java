package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/** @deprecated */
@Deprecated
public class ISO8601Utils {
   protected static final int DEF_8601_LEN = "yyyy-MM-ddThh:mm:ss.SSS+00:00".length();
   private static final TimeZone TIMEZONE_Z = TimeZone.getTimeZone("UTC");

   public static String format(Date date) {
      return format(date, false, TIMEZONE_Z);
   }

   public static String format(Date date, boolean millis) {
      return format(date, millis, TIMEZONE_Z);
   }

   /** @deprecated */
   @Deprecated
   public static String format(Date date, boolean millis, TimeZone tz) {
      return format(date, millis, tz, Locale.US);
   }

   public static String format(Date date, boolean millis, TimeZone tz, Locale loc) {
      Calendar calendar = new GregorianCalendar(tz, loc);
      calendar.setTime(date);
      StringBuilder sb = new StringBuilder(30);
      sb.append(String.format("%04d-%02d-%02dT%02d:%02d:%02d", calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13)));
      if (millis) {
         sb.append(String.format(".%03d", calendar.get(14)));
      }

      int offset = tz.getOffset(calendar.getTimeInMillis());
      if (offset != 0) {
         int hours = Math.abs(offset / '\uea60' / 60);
         int minutes = Math.abs(offset / '\uea60' % 60);
         sb.append(String.format("%c%02d:%02d", (char)(offset < 0 ? '-' : '+'), hours, minutes));
      } else {
         sb.append('Z');
      }

      return sb.toString();
   }

   public static Date parse(String date, ParsePosition pos) throws ParseException {
      Objects.requireNonNull(date);
      Exception fail = null;

      try {
         int offset = pos.getIndex();
         int input = offset + 4;
         int year = parseInt(date, offset, input);
         if (checkOffset(date, input, '-')) {
            ++input;
         }

         offset = input + 2;
         int month = parseInt(date, input, offset);
         if (checkOffset(date, offset, '-')) {
            ++offset;
         }

         int var22 = offset + 2;
         int day = parseInt(date, offset, var22);
         int hour = 0;
         int minutes = 0;
         int seconds = 0;
         int milliseconds = 0;
         boolean hasT = checkOffset(date, var22, 'T');
         if (!hasT && date.length() <= var22) {
            Calendar calendar = new GregorianCalendar(year, month - 1, day);
            pos.setIndex(var22);
            return calendar.getTime();
         } else {
            if (hasT) {
               ++var22;
               offset = var22 + 2;
               hour = parseInt(date, var22, offset);
               if (checkOffset(date, offset, ':')) {
                  ++offset;
               }

               var22 = offset + 2;
               minutes = parseInt(date, offset, var22);
               if (checkOffset(date, var22, ':')) {
                  ++var22;
               }

               if (date.length() > var22) {
                  char c = date.charAt(var22);
                  if (c != 'Z' && c != '+' && c != '-') {
                     int var40 = var22;
                     var22 += 2;
                     seconds = parseInt(date, var40, var22);
                     if (seconds > 59 && seconds < 63) {
                        seconds = 59;
                     }

                     if (checkOffset(date, var22, '.')) {
                        ++var22;
                        int endOffset = indexOfNonDigit(date, var22 + 1);
                        int parseEndOffset = Math.min(endOffset, var22 + 3);
                        int fraction = parseInt(date, var22, parseEndOffset);
                        switch (parseEndOffset - var22) {
                           case 1:
                              milliseconds = fraction * 100;
                              break;
                           case 2:
                              milliseconds = fraction * 10;
                              break;
                           default:
                              milliseconds = fraction;
                        }

                        var22 = endOffset;
                     }
                  }
               }
            }

            if (date.length() <= var22) {
               throw new IllegalArgumentException("No time zone indicator");
            } else {
               TimeZone timezone = null;
               char timezoneIndicator = date.charAt(var22);
               if (timezoneIndicator == 'Z') {
                  timezone = TIMEZONE_Z;
                  ++var22;
               } else {
                  if (timezoneIndicator != '+' && timezoneIndicator != '-') {
                     throw new IndexOutOfBoundsException("Invalid time zone indicator '" + timezoneIndicator + "'");
                  }

                  String timezoneOffset = date.substring(var22);
                  var22 += timezoneOffset.length();
                  if (!"+0000".equals(timezoneOffset) && !"+00:00".equals(timezoneOffset)) {
                     String timezoneId = "GMT" + timezoneOffset;
                     timezone = TimeZone.getTimeZone(timezoneId);
                     String act = timezone.getID();
                     if (!act.equals(timezoneId)) {
                        String cleaned = act.replace(":", "");
                        if (!cleaned.equals(timezoneId)) {
                           throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + timezoneId + " given, resolves to " + timezone.getID());
                        }
                     }
                  } else {
                     timezone = TIMEZONE_Z;
                  }
               }

               Calendar calendar = new GregorianCalendar(timezone);
               calendar.setLenient(false);
               calendar.set(1, year);
               calendar.set(2, month - 1);
               calendar.set(5, day);
               calendar.set(11, hour);
               calendar.set(12, minutes);
               calendar.set(13, seconds);
               calendar.set(14, milliseconds);
               pos.setIndex(var22);
               return calendar.getTime();
            }
         }
      } catch (Exception e) {
         String input = date == null ? null : '"' + date + '"';
         String msg = e.getMessage();
         if (msg == null || msg.isEmpty()) {
            msg = "(" + e.getClass().getName() + ")";
         }

         ParseException ex = new ParseException("Failed to parse date " + input + ": " + msg, pos.getIndex());
         ex.initCause(e);
         throw ex;
      }
   }

   private static boolean checkOffset(String value, int offset, char expected) {
      return offset < value.length() && value.charAt(offset) == expected;
   }

   private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
      if (beginIndex >= 0 && endIndex <= value.length() && beginIndex <= endIndex) {
         int i = beginIndex;
         int result = 0;
         if (beginIndex < endIndex) {
            i = beginIndex + 1;
            int digit = Character.digit(value.charAt(beginIndex), 10);
            if (digit < 0) {
               throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }

            result = -digit;
         }

         while(i < endIndex) {
            int digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
               throw new NumberFormatException("Invalid number: " + value.substring(beginIndex, endIndex));
            }

            result *= 10;
            result -= digit;
         }

         return -result;
      } else {
         throw new NumberFormatException(value);
      }
   }

   private static int indexOfNonDigit(String string, int offset) {
      for(int i = offset; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (c < '0' || c > '9') {
            return i;
         }
      }

      return string.length();
   }
}
