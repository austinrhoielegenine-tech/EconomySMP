package gnu.trove.impl;

public class Constants {
   private static final boolean VERBOSE;
   public static final int DEFAULT_CAPACITY = 10;
   public static final float DEFAULT_LOAD_FACTOR = 0.5F;
   public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
   public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
   public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
   public static final int DEFAULT_INT_NO_ENTRY_VALUE;
   public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
   public static final float DEFAULT_FLOAT_NO_ENTRY_VALUE;
   public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;

   static {
      boolean verbose = (boolean)0;

      try {
         verbose = System.getProperty("gnu.trove.verbose", (String)null) != null;
      } catch (SecurityException var11) {
      }

      VERBOSE = verbose;
      String property = "0";

      try {
         property = System.getProperty("gnu.trove.no_entry.byte", property);
      } catch (SecurityException var10) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)127;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)-128;
      } else {
         verbose = (boolean)Byte.valueOf(property);
      }

      if (verbose > 127) {
         verbose = (boolean)127;
      } else if (verbose < -128) {
         verbose = (boolean)-128;
      }

      DEFAULT_BYTE_NO_ENTRY_VALUE = verbose;
      if (VERBOSE) {
         System.out.println("DEFAULT_BYTE_NO_ENTRY_VALUE: " + DEFAULT_BYTE_NO_ENTRY_VALUE);
      }

      property = "0";

      try {
         property = System.getProperty("gnu.trove.no_entry.short", property);
      } catch (SecurityException var9) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)32767;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)-32768;
      } else {
         verbose = (boolean)Short.valueOf(property);
      }

      if (verbose > 32767) {
         verbose = (boolean)32767;
      } else if (verbose < Short.MIN_VALUE) {
         verbose = (boolean)-32768;
      }

      DEFAULT_SHORT_NO_ENTRY_VALUE = verbose;
      if (VERBOSE) {
         System.out.println("DEFAULT_SHORT_NO_ENTRY_VALUE: " + DEFAULT_SHORT_NO_ENTRY_VALUE);
      }

      property = "\u0000";

      try {
         property = System.getProperty("gnu.trove.no_entry.char", property);
      } catch (SecurityException var8) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)65535;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)0;
      } else {
         verbose = (boolean)property.toCharArray()[0];
      }

      if (verbose > '\uffff') {
         verbose = (boolean)65535;
      } else if (verbose < 0) {
         verbose = (boolean)0;
      }

      DEFAULT_CHAR_NO_ENTRY_VALUE = verbose;
      if (VERBOSE) {
         System.out.println("DEFAULT_CHAR_NO_ENTRY_VALUE: " + Integer.valueOf(verbose));
      }

      property = "0";

      try {
         property = System.getProperty("gnu.trove.no_entry.int", property);
      } catch (SecurityException var7) {
      }

      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)Integer.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         verbose = (boolean)Integer.MIN_VALUE;
      } else {
         verbose = (boolean)Integer.valueOf(property);
      }

      DEFAULT_INT_NO_ENTRY_VALUE = verbose;
      if (VERBOSE) {
         System.out.println("DEFAULT_INT_NO_ENTRY_VALUE: " + DEFAULT_INT_NO_ENTRY_VALUE);
      }

      String property = "0";

      try {
         property = System.getProperty("gnu.trove.no_entry.long", property);
      } catch (SecurityException var6) {
      }

      long value;
      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         value = Long.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         value = Long.MIN_VALUE;
      } else {
         value = Long.valueOf(property);
      }

      DEFAULT_LONG_NO_ENTRY_VALUE = value;
      if (VERBOSE) {
         System.out.println("DEFAULT_LONG_NO_ENTRY_VALUE: " + DEFAULT_LONG_NO_ENTRY_VALUE);
      }

      property = "0";

      try {
         property = System.getProperty("gnu.trove.no_entry.float", property);
      } catch (SecurityException var5) {
      }

      float value;
      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         value = Float.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         value = Float.MIN_VALUE;
      } else if ("MIN_NORMAL".equalsIgnoreCase(property)) {
         value = Float.MIN_NORMAL;
      } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(property)) {
         value = Float.NEGATIVE_INFINITY;
      } else if ("POSITIVE_INFINITY".equalsIgnoreCase(property)) {
         value = Float.POSITIVE_INFINITY;
      } else {
         value = Float.valueOf(property);
      }

      DEFAULT_FLOAT_NO_ENTRY_VALUE = value;
      if (VERBOSE) {
         System.out.println("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + DEFAULT_FLOAT_NO_ENTRY_VALUE);
      }

      property = "0";

      try {
         property = System.getProperty("gnu.trove.no_entry.double", property);
      } catch (SecurityException var4) {
      }

      double value;
      if ("MAX_VALUE".equalsIgnoreCase(property)) {
         value = Double.MAX_VALUE;
      } else if ("MIN_VALUE".equalsIgnoreCase(property)) {
         value = Double.MIN_VALUE;
      } else if ("MIN_NORMAL".equalsIgnoreCase(property)) {
         value = Double.MIN_NORMAL;
      } else if ("NEGATIVE_INFINITY".equalsIgnoreCase(property)) {
         value = Double.NEGATIVE_INFINITY;
      } else if ("POSITIVE_INFINITY".equalsIgnoreCase(property)) {
         value = Double.POSITIVE_INFINITY;
      } else {
         value = Double.valueOf(property);
      }

      DEFAULT_DOUBLE_NO_ENTRY_VALUE = value;
      if (VERBOSE) {
         System.out.println("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + DEFAULT_DOUBLE_NO_ENTRY_VALUE);
      }

   }
}
