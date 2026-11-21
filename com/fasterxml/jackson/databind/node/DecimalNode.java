package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalNode extends NumericNode {
   public static final DecimalNode ZERO;
   private static final BigDecimal MIN_INTEGER;
   private static final BigDecimal MAX_INTEGER;
   private static final BigDecimal MIN_LONG;
   private static final BigDecimal MAX_LONG;
   protected final BigDecimal _value;

   public DecimalNode(BigDecimal v) {
      this._value = v;
   }

   public static DecimalNode valueOf(BigDecimal d) {
      return new DecimalNode(d);
   }

   public JsonToken asToken() {
      return JsonToken.VALUE_NUMBER_FLOAT;
   }

   public JsonParser.NumberType numberType() {
      return JsonParser.NumberType.BIG_DECIMAL;
   }

   public boolean isFloatingPointNumber() {
      return true;
   }

   public boolean isBigDecimal() {
      return true;
   }

   public boolean canConvertToInt() {
      return this._value.compareTo(MIN_INTEGER) >= 0 && this._value.compareTo(MAX_INTEGER) <= 0;
   }

   public boolean canConvertToLong() {
      return this._value.compareTo(MIN_LONG) >= 0 && this._value.compareTo(MAX_LONG) <= 0;
   }

   public boolean canConvertToExactIntegral() {
      return this._value.signum() == 0 || this._value.scale() <= 0 || this._value.stripTrailingZeros().scale() <= 0;
   }

   public Number numberValue() {
      return this._value;
   }

   public short shortValue() {
      return this._value.shortValue();
   }

   public int intValue() {
      return this._value.intValue();
   }

   public long longValue() {
      return this._value.longValue();
   }

   public BigInteger bigIntegerValue() {
      return this._bigIntFromBigDec(this._value);
   }

   public float floatValue() {
      return this._value.floatValue();
   }

   public double doubleValue() {
      return this._value.doubleValue();
   }

   public BigDecimal decimalValue() {
      return this._value;
   }

   public String asText() {
      return this._value.toString();
   }

   public final void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeNumber(this._value);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o instanceof DecimalNode) {
         DecimalNode otherNode = (DecimalNode)o;
         if (otherNode._value == null) {
            return this._value == null;
         } else if (this._value == null) {
            return false;
         } else {
            return otherNode._value.compareTo(this._value) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this._value == null ? 0 : Double.hashCode(this.doubleValue());
   }

   static {
      ZERO = new DecimalNode(BigDecimal.ZERO);
      MIN_INTEGER = BigDecimal.valueOf(-2147483648L);
      MAX_INTEGER = BigDecimal.valueOf(2147483647L);
      MIN_LONG = BigDecimal.valueOf(Long.MIN_VALUE);
      MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);
   }
}
