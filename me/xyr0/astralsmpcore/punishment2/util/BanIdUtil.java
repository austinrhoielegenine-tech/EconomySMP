package me.xyr0.astralsmpcore.punishment2.util;

import java.security.SecureRandom;
import me.xyr0.astralsmpcore.punishment2.AstralPunishment;

public class BanIdUtil {
   private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   private static final SecureRandom RANDOM = new SecureRandom();

   public static String generate() {
      String id;
      do {
         StringBuilder builder = new StringBuilder();
         int length = AstralPunishment.getInstance().getConfig().getInt("settings.ban-id-length", 5);

         for(int i = 0; i < length; ++i) {
            builder.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(RANDOM.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length())));
         }

         id = builder.toString();
      } while(exists(id));

      return id;
   }

   private static boolean exists(String id) {
      try {
         return AstralPunishment.getInstance().getDatabaseManager().banIdExists(id);
      } catch (Exception var2) {
         return false;
      }
   }
}
