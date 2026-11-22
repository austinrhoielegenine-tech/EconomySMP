package com.austynzdevv.nyrethacore.punishment2.util;

import java.security.SecureRandom;
import com.austynzdevv.nyrethacore.punishment2.EconomyPunishment;

public class BanIdUtil {
   private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   private static final SecureRandom RANDOM = new SecureRandom();

   public static String generate() {
      String id;
      do {
         StringBuilder builder = new StringBuilder();
         int length = EconomyPunishment.getInstance().getConfig().getInt("settings.ban-id-length", 5);

         for(int i = 0; i < length; ++i) {
            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(RANDOM.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length())));
         }

         id = builder.toString();
      } while(exists(id));

      return id;
   }

   private static boolean exists(String id) {
      try {
         return EconomyPunishment.getInstance().getDatabaseManager().banIdExists(id);
      } catch (Exception var2) {
         return false;
      }
   }
}
