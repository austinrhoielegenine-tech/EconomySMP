package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.io.Serializable;

public class PropertyNamingStrategy implements Serializable {
   private static final long serialVersionUID = 2L;

   public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
      return defaultName;
   }

   public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
      return defaultName;
   }

   public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
      return defaultName;
   }

   public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
      return defaultName;
   }
}
