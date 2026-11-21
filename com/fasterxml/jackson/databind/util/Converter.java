package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;

public interface Converter<IN, OUT> {
   OUT convert(IN var1);

   default OUT convert(DeserializationContext ctxt, IN value) {
      return (OUT)this.convert(value);
   }

   default OUT convert(SerializerProvider provider, IN value) {
      return (OUT)this.convert(value);
   }

   JavaType getInputType(TypeFactory var1);

   JavaType getOutputType(TypeFactory var1);

   public abstract static class None implements Converter<Object, Object> {
      private None() {
      }
   }
}
