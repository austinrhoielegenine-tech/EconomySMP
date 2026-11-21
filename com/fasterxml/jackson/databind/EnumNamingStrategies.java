package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.util.NamingStrategyImpls;
import java.util.Locale;

public class EnumNamingStrategies {
   public static final EnumNamingStrategy LOWER_CAMEL_CASE;
   public static final EnumNamingStrategy UPPER_CAMEL_CASE;
   public static final EnumNamingStrategy SNAKE_CASE;
   public static final EnumNamingStrategy UPPER_SNAKE_CASE;
   public static final EnumNamingStrategy LOWER_CASE;
   public static final EnumNamingStrategy KEBAB_CASE;
   public static final EnumNamingStrategy LOWER_DOT_CASE;

   private EnumNamingStrategies() {
   }

   static {
      LOWER_CAMEL_CASE = EnumNamingStrategies.LowerCamelCaseStrategy.INSTANCE;
      UPPER_CAMEL_CASE = EnumNamingStrategies.UpperCamelCaseStrategy.INSTANCE;
      SNAKE_CASE = EnumNamingStrategies.SnakeCaseStrategy.INSTANCE;
      UPPER_SNAKE_CASE = EnumNamingStrategies.UpperSnakeCaseStrategy.INSTANCE;
      LOWER_CASE = EnumNamingStrategies.LowerCaseStrategy.INSTANCE;
      KEBAB_CASE = EnumNamingStrategies.KebabCaseStrategy.INSTANCE;
      LOWER_DOT_CASE = EnumNamingStrategies.LowerDotCaseStrategy.INSTANCE;
   }

   public abstract static class DelegatingEnumNamingStrategy implements EnumNamingStrategy {
      private final NamingStrategyImpls strategy;

      protected DelegatingEnumNamingStrategy(NamingStrategyImpls strategy) {
         this.strategy = strategy;
      }

      public String convertEnumToExternalName(String enumName) {
         return this.strategy.translate(toBeanName(enumName));
      }

      protected static String toBeanName(String enumName) {
         if (enumName == null) {
            return null;
         } else {
            String UNDERSCORE = "_";
            StringBuilder out = null;
            int iterationCnt = 0;
            int lastSeparatorIdx = -1;

            do {
               lastSeparatorIdx = nextIndexOfUnderscore(enumName, lastSeparatorIdx + 1);
               if (lastSeparatorIdx != -1) {
                  if (iterationCnt == 0) {
                     out = new StringBuilder(enumName.length() + 4 * "_".length());
                     out.append(enumName.substring(iterationCnt, lastSeparatorIdx).toLowerCase(Locale.ROOT));
                  } else {
                     out.append(normalizeWord(enumName.substring(iterationCnt, lastSeparatorIdx)));
                  }

                  iterationCnt = lastSeparatorIdx + "_".length();
               }
            } while(lastSeparatorIdx != -1);

            if (iterationCnt == 0) {
               return enumName.toLowerCase(Locale.ROOT);
            } else {
               out.append(normalizeWord(enumName.substring(iterationCnt)));
               return out.toString();
            }
         }
      }

      private static int nextIndexOfUnderscore(CharSequence sequence, int start) {
         int length = sequence.length();

         for(int i = start; i < length; ++i) {
            if ('_' == sequence.charAt(i)) {
               return i;
            }
         }

         return -1;
      }

      private static String normalizeWord(String word) {
         int length = word.length();
         return length == 0 ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
      }
   }

   /** @deprecated */
   @Deprecated
   public static class CamelCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final CamelCaseStrategy INSTANCE = new CamelCaseStrategy();

      protected CamelCaseStrategy() {
         super(NamingStrategyImpls.LOWER_CAMEL_CASE);
      }
   }

   public static class LowerCamelCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final LowerCamelCaseStrategy INSTANCE = new LowerCamelCaseStrategy();

      protected LowerCamelCaseStrategy() {
         super(NamingStrategyImpls.LOWER_CAMEL_CASE);
      }
   }

   public static class UpperCamelCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final UpperCamelCaseStrategy INSTANCE = new UpperCamelCaseStrategy();

      protected UpperCamelCaseStrategy() {
         super(NamingStrategyImpls.UPPER_CAMEL_CASE);
      }
   }

   public static class SnakeCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final SnakeCaseStrategy INSTANCE = new SnakeCaseStrategy();

      protected SnakeCaseStrategy() {
         super(NamingStrategyImpls.SNAKE_CASE);
      }
   }

   public static class UpperSnakeCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final UpperSnakeCaseStrategy INSTANCE = new UpperSnakeCaseStrategy();

      protected UpperSnakeCaseStrategy() {
         super(NamingStrategyImpls.UPPER_SNAKE_CASE);
      }
   }

   public static class LowerCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final LowerCaseStrategy INSTANCE = new LowerCaseStrategy();

      protected LowerCaseStrategy() {
         super(NamingStrategyImpls.LOWER_CASE);
      }
   }

   public static class KebabCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final KebabCaseStrategy INSTANCE = new KebabCaseStrategy();

      protected KebabCaseStrategy() {
         super(NamingStrategyImpls.KEBAB_CASE);
      }
   }

   public static class LowerDotCaseStrategy extends DelegatingEnumNamingStrategy {
      public static final LowerDotCaseStrategy INSTANCE = new LowerDotCaseStrategy();

      protected LowerDotCaseStrategy() {
         super(NamingStrategyImpls.LOWER_DOT_CASE);
      }
   }
}
