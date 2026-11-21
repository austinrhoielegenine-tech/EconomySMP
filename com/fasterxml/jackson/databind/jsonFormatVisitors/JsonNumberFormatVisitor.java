package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.core.JsonParser;

public interface JsonNumberFormatVisitor extends JsonValueFormatVisitor {
   void numberType(JsonParser.NumberType var1);

   public static class Base extends JsonValueFormatVisitor.Base implements JsonNumberFormatVisitor {
      public void numberType(JsonParser.NumberType type) {
      }
   }
}
