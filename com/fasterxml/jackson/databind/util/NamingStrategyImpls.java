package com.fasterxml.jackson.databind.util;

public enum NamingStrategyImpls {
   LOWER_CAMEL_CASE {
      public String translate(String beanName) {
         return beanName;
      }
   },
   UPPER_CAMEL_CASE {
      public String translate(String beanName) {
         if (beanName != null && !beanName.isEmpty()) {
            char c = beanName.charAt(0);
            char uc = Character.toUpperCase(c);
            if (c == uc) {
               return beanName;
            } else {
               StringBuilder sb = new StringBuilder(beanName);
               sb.setCharAt(0, uc);
               return sb.toString();
            }
         } else {
            return beanName;
         }
      }
   },
   SNAKE_CASE {
      public String translate(String beanName) {
         if (beanName == null) {
            return beanName;
         } else {
            int length = beanName.length();
            StringBuilder result = new StringBuilder(length * 2);
            int resultLength = 0;
            boolean wasPrevTranslated = false;

            for(int i = 0; i < length; ++i) {
               char c = beanName.charAt(i);
               if (i > 0 || c != '_') {
                  if (Character.isUpperCase(c)) {
                     if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                        result.append('_');
                        ++resultLength;
                     }

                     c = Character.toLowerCase(c);
                     wasPrevTranslated = true;
                  } else {
                     wasPrevTranslated = false;
                  }

                  result.append(c);
                  ++resultLength;
               }
            }

            return resultLength > 0 ? result.toString() : beanName;
         }
      }
   },
   UPPER_SNAKE_CASE {
      public String translate(String beanName) {
         String output = SNAKE_CASE.translate(beanName);
         return output == null ? null : output.toUpperCase();
      }
   },
   LOWER_CASE {
      public String translate(String beanName) {
         return beanName != null && !beanName.isEmpty() ? beanName.toLowerCase() : beanName;
      }
   },
   KEBAB_CASE {
      public String translate(String beanName) {
         return translateLowerCaseWithSeparator(beanName, '-');
      }
   },
   LOWER_DOT_CASE {
      public String translate(String beanName) {
         return translateLowerCaseWithSeparator(beanName, '.');
      }
   };

   private NamingStrategyImpls() {
   }

   public abstract String translate(String var1);

   static String translateLowerCaseWithSeparator(String beanName, char separator) {
      if (beanName != null && !beanName.isEmpty()) {
         int length = beanName.length();
         StringBuilder result = new StringBuilder(length + (length >> 1));
         int upperCount = 0;

         for(int i = 0; i < length; ++i) {
            char ch = beanName.charAt(i);
            char lc = Character.toLowerCase(ch);
            if (lc == ch) {
               if (upperCount > 1) {
                  result.insert(result.length() - 1, separator);
               }

               upperCount = 0;
            } else {
               if (upperCount == 0 && i > 0) {
                  result.append(separator);
               }

               ++upperCount;
            }

            result.append(lc);
         }

         return result.toString();
      } else {
         return beanName;
      }
   }
}
