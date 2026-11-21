package com.fasterxml.jackson.core.util;

public abstract class InternalJacksonUtil {
   public static int addOverflowSafe(int base, int length) {
      int result = base + length;
      return result < 0 ? Integer.MAX_VALUE : result;
   }
}
