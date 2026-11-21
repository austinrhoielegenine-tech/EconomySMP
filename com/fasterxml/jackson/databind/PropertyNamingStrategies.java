package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.util.NamingStrategyImpls;
import java.io.Serializable;

public abstract class PropertyNamingStrategies implements Serializable {
   private static final long serialVersionUID = 2L;
   public static final PropertyNamingStrategy LOWER_CAMEL_CASE;
   public static final PropertyNamingStrategy UPPER_CAMEL_CASE;
   public static final PropertyNamingStrategy SNAKE_CASE;
   public static final PropertyNamingStrategy UPPER_SNAKE_CASE;
   public static final PropertyNamingStrategy LOWER_CASE;
   public static final PropertyNamingStrategy KEBAB_CASE;
   public static final PropertyNamingStrategy LOWER_DOT_CASE;

   static {
      LOWER_CAMEL_CASE = PropertyNamingStrategies.LowerCamelCaseStrategy.INSTANCE;
      UPPER_CAMEL_CASE = PropertyNamingStrategies.UpperCamelCaseStrategy.INSTANCE;
      SNAKE_CASE = PropertyNamingStrategies.SnakeCaseStrategy.INSTANCE;
      UPPER_SNAKE_CASE = PropertyNamingStrategies.UpperSnakeCaseStrategy.INSTANCE;
      LOWER_CASE = PropertyNamingStrategies.LowerCaseStrategy.INSTANCE;
      KEBAB_CASE = PropertyNamingStrategies.KebabCaseStrategy.INSTANCE;
      LOWER_DOT_CASE = PropertyNamingStrategies.LowerDotCaseStrategy.INSTANCE;
   }

   public abstract static class NamingBase extends PropertyNamingStrategy {
      private static final long serialVersionUID = 2L;

      public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
         return this.translate(defaultName);
      }

      public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
         return this.translate(defaultName);
      }

      public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
         return this.translate(defaultName);
      }

      public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
         return this.translate(defaultName);
      }

      public abstract String translate(String var1);
   }

   public static class SnakeCaseStrategy extends NamingBase {
      private static final long serialVersionUID = 2L;
      public static final SnakeCaseStrategy INSTANCE = new SnakeCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.SNAKE_CASE.translate(input);
      }
   }

   public static class UpperSnakeCaseStrategy extends SnakeCaseStrategy {
      private static final long serialVersionUID = 2L;
      public static final UpperSnakeCaseStrategy INSTANCE = new UpperSnakeCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.UPPER_SNAKE_CASE.translate(input);
      }
   }

   public static class LowerCamelCaseStrategy extends NamingBase {
      private static final long serialVersionUID = 2L;
      public static final LowerCamelCaseStrategy INSTANCE = new LowerCamelCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.LOWER_CAMEL_CASE.translate(input);
      }
   }

   public static class UpperCamelCaseStrategy extends NamingBase {
      private static final long serialVersionUID = 2L;
      public static final UpperCamelCaseStrategy INSTANCE = new UpperCamelCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.UPPER_CAMEL_CASE.translate(input);
      }
   }

   public static class LowerCaseStrategy extends NamingBase {
      private static final long serialVersionUID = 2L;
      public static final LowerCaseStrategy INSTANCE = new LowerCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.LOWER_CASE.translate(input);
      }
   }

   public static class KebabCaseStrategy extends NamingBase {
      private static final long serialVersionUID = 2L;
      public static final KebabCaseStrategy INSTANCE = new KebabCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.KEBAB_CASE.translate(input);
      }
   }

   public static class LowerDotCaseStrategy extends NamingBase {
      private static final long serialVersionUID = 2L;
      public static final LowerDotCaseStrategy INSTANCE = new LowerDotCaseStrategy();

      public String translate(String input) {
         return NamingStrategyImpls.LOWER_DOT_CASE.translate(input);
      }
   }
}
