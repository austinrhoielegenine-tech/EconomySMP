package com.austynzdevv.nyrethacore.hide.util;

import java.util.Random;

public class HideNameUtil {
   private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
   private static final Random RANDOM = new Random();

   public static String generate(int length) {
      StringBuilder name = new StringBuilder();

      for(int i = 0; i < length; ++i) {
         name.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(RANDOM.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length())));
      }

      return name.toString();
   }
}
