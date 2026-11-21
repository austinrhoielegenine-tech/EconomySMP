package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public abstract class AccessorNamingStrategy {
   public abstract String findNameForIsGetter(AnnotatedMethod var1, String var2);

   public abstract String findNameForRegularGetter(AnnotatedMethod var1, String var2);

   public abstract String findNameForMutator(AnnotatedMethod var1, String var2);

   public abstract String modifyFieldName(AnnotatedField var1, String var2);

   public static class Base extends AccessorNamingStrategy implements Serializable {
      private static final long serialVersionUID = 1L;

      public String findNameForIsGetter(AnnotatedMethod method, String name) {
         return null;
      }

      public String findNameForRegularGetter(AnnotatedMethod method, String name) {
         return null;
      }

      public String findNameForMutator(AnnotatedMethod method, String name) {
         return null;
      }

      public String modifyFieldName(AnnotatedField field, String name) {
         return name;
      }
   }

   public abstract static class Provider implements Serializable {
      private static final long serialVersionUID = 1L;

      public abstract AccessorNamingStrategy forPOJO(MapperConfig<?> var1, AnnotatedClass var2);

      public abstract AccessorNamingStrategy forBuilder(MapperConfig<?> var1, AnnotatedClass var2, BeanDescription var3);

      public abstract AccessorNamingStrategy forRecord(MapperConfig<?> var1, AnnotatedClass var2);
   }
}
