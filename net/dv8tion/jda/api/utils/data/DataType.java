package net.dv8tion.jda.api.utils.data;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum DataType {
   INT,
   FLOAT,
   STRING,
   OBJECT,
   ARRAY,
   BOOLEAN,
   NULL,
   UNKNOWN;

   @Nonnull
   public static DataType getType(@Nullable Object value) {
      for(DataType type : values()) {
         if (type.isType(value)) {
            return type;
         }
      }

      return UNKNOWN;
   }

   public boolean isType(@Nullable Object value) {
      switch (this.ordinal()) {
         case 0:
            return value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte;
         case 1:
            return value instanceof Double || value instanceof Float;
         case 2:
            return value instanceof String;
         case 3:
            return value instanceof Map;
         case 4:
            return value instanceof List;
         case 5:
            return value instanceof Boolean;
         case 6:
            return value == null;
         default:
            return false;
      }
   }

   // $FF: synthetic method
   private static DataType[] $values() {
      return new DataType[]{INT, FLOAT, STRING, OBJECT, ARRAY, BOOLEAN, NULL, UNKNOWN};
   }
}
