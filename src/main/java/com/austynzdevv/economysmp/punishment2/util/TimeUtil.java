package com.austynzdevv.economysmp.punishment2.util;

public class TimeUtil {
   public static String formatTime(long millis) {
      if (millis <= 0L) {
         return "Permanent";
      } else {
         long seconds = millis / 1000L;
         long days = seconds / 86400L;
         seconds %= 86400L;
         long hours = seconds / 3600L;
         seconds %= 3600L;
         long minutes = seconds / 60L;
         seconds %= 60L;
         StringBuilder result = new StringBuilder();
         if (days > 0L) {
            result.append(days).append("d ");
         }

         if (hours > 0L) {
            result.append(hours).append("h ");
         }

         if (minutes > 0L) {
            result.append(minutes).append("m ");
         }

         if (seconds > 0L) {
            result.append(seconds).append("s");
         }

         return result.length() == 0 ? "0s" : result.toString().trim();
      }
   }

   public static long getExpireTime(String duration) {
      if (duration == null) {
         return -1L;
      } else if (duration.equalsIgnoreCase("permanent")) {
         return -1L;
      } else {
         long multiplier = 1000L;

         try {
            char unit = duration.charAt(duration.length() - 1);
            long number = Long.parseLong(duration.substring(0, duration.length() - 1));
            switch (unit) {
               case 'M' -> multiplier = 2592000000L;
               case 'd' -> multiplier = 86400000L;
               case 'h' -> multiplier = 3600000L;
               case 'm' -> multiplier = 60000L;
            }

            return System.currentTimeMillis() + number * multiplier;
         } catch (Exception var6) {
            return -1L;
         }
      }
   }

   public static boolean hasExpired(long expireTime) {
      if (expireTime <= 0L) {
         return false;
      } else {
         return System.currentTimeMillis() >= expireTime;
      }
   }
}
