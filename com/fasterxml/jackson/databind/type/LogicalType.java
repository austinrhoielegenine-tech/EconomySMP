package com.fasterxml.jackson.databind.type;

import java.util.Collection;
import java.util.Map;

public enum LogicalType {
   Array,
   Collection,
   Map,
   POJO,
   Untyped,
   Integer,
   Float,
   Boolean,
   Enum,
   Textual,
   Binary,
   DateTime,
   OtherScalar;

   public static LogicalType fromClass(Class<?> raw, LogicalType defaultIfNotRecognized) {
      if (raw.isEnum()) {
         return Enum;
      } else if (raw.isArray()) {
         return raw == byte[].class ? Binary : Array;
      } else if (Collection.class.isAssignableFrom(raw)) {
         return Collection;
      } else if (Map.class.isAssignableFrom(raw)) {
         return Map;
      } else {
         return raw == String.class ? Textual : defaultIfNotRecognized;
      }
   }
}
