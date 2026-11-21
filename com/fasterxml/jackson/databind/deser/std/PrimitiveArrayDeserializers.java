package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsFailProvider;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public abstract class PrimitiveArrayDeserializers<T> extends StdDeserializer<T> implements ContextualDeserializer {
   protected final Boolean _unwrapSingle;
   private transient Object _emptyValue;
   protected final NullValueProvider _nuller;

   protected PrimitiveArrayDeserializers(Class<T> cls) {
      super(cls);
      this._unwrapSingle = null;
      this._nuller = null;
   }

   protected PrimitiveArrayDeserializers(PrimitiveArrayDeserializers<?> base, NullValueProvider nuller, Boolean unwrapSingle) {
      super(base._valueClass);
      this._unwrapSingle = unwrapSingle;
      this._nuller = nuller;
   }

   public static JsonDeserializer<?> forType(Class<?> rawType) {
      if (rawType == Integer.TYPE) {
         return PrimitiveArrayDeserializers.IntDeser.instance;
      } else if (rawType == Long.TYPE) {
         return PrimitiveArrayDeserializers.LongDeser.instance;
      } else if (rawType == Byte.TYPE) {
         return new ByteDeser();
      } else if (rawType == Short.TYPE) {
         return new ShortDeser();
      } else if (rawType == Float.TYPE) {
         return new FloatDeser();
      } else if (rawType == Double.TYPE) {
         return new DoubleDeser();
      } else if (rawType == Boolean.TYPE) {
         return new BooleanDeser();
      } else if (rawType == Character.TYPE) {
         return new CharDeser();
      } else {
         throw new IllegalArgumentException("Unknown primitive array element type: " + rawType);
      }
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, this._valueClass, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      NullValueProvider nuller = null;
      Nulls nullStyle = this.findContentNullStyle(ctxt, property);
      if (nullStyle == Nulls.SKIP) {
         nuller = NullsConstantProvider.skipper();
      } else if (nullStyle == Nulls.FAIL) {
         if (property == null) {
            nuller = NullsFailProvider.constructForRootValue(ctxt.constructType(this._valueClass.getComponentType()));
         } else {
            nuller = NullsFailProvider.constructForProperty(property, property.getType().getContentType());
         }
      }

      return Objects.equals(unwrapSingle, this._unwrapSingle) && nuller == this._nuller ? this : this.withResolved(nuller, unwrapSingle);
   }

   protected abstract T _concat(T var1, T var2);

   protected abstract T handleSingleElementUnwrapped(JsonParser var1, DeserializationContext var2) throws IOException;

   protected abstract PrimitiveArrayDeserializers<?> withResolved(NullValueProvider var1, Boolean var2);

   protected abstract T _constructEmpty();

   public LogicalType logicalType() {
      return LogicalType.Array;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.TRUE;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.CONSTANT;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      Object empty = this._emptyValue;
      if (empty == null) {
         this._emptyValue = empty = this._constructEmpty();
      }

      return empty;
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws IOException {
      T newValue = (T)this.deserialize(p, ctxt);
      if (existing == null) {
         return newValue;
      } else {
         int len = Array.getLength(existing);
         return (T)(len == 0 ? newValue : this._concat(existing, newValue));
      }
   }

   protected T handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (!p.hasToken(JsonToken.VALUE_STRING) || canWrap && !_isBlank(p.getText())) {
         return (T)(canWrap ? this.handleSingleElementUnwrapped(p, ctxt) : ctxt.handleUnexpectedToken(this._valueClass, p));
      } else {
         return (T)this._deserializeFromString(p, ctxt);
      }
   }

   protected void _failOnNull(DeserializationContext ctxt) throws IOException {
      throw InvalidNullException.from(ctxt, (PropertyName)null, ctxt.constructType(this._valueClass));
   }

   @JacksonStdImpl
   static final class CharDeser extends PrimitiveArrayDeserializers<char[]> {
      private static final long serialVersionUID = 1L;

      public CharDeser() {
         super(char[].class);
      }

      protected CharDeser(CharDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return this;
      }

      protected char[] _constructEmpty() {
         return new char[0];
      }

      public char[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.hasToken(JsonToken.VALUE_STRING)) {
            char[] buffer = p.getTextCharacters();
            int offset = p.getTextOffset();
            int len = p.getTextLength();
            char[] result = new char[len];
            System.arraycopy(buffer, offset, result, 0, len);
            return result;
         } else if (!p.isExpectedStartArrayToken()) {
            if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
               Object ob = p.getEmbeddedObject();
               if (ob == null) {
                  return null;
               }

               if (ob instanceof char[]) {
                  return (char[])ob;
               }

               if (ob instanceof String) {
                  return ((String)ob).toCharArray();
               }

               if (ob instanceof byte[]) {
                  return Base64Variants.getDefaultVariant().encode((byte[])ob, false).toCharArray();
               }
            }

            return (char[])ctxt.handleUnexpectedToken(this._valueClass, p);
         } else {
            StringBuilder sb = new StringBuilder(64);

            JsonToken t;
            while((t = p.nextToken()) != JsonToken.END_ARRAY) {
               String str;
               if (t == JsonToken.VALUE_STRING) {
                  str = p.getText();
               } else if (t == JsonToken.VALUE_NULL) {
                  if (this._nuller != null) {
                     this._nuller.getNullValue(ctxt);
                     continue;
                  }

                  this._verifyNullForPrimitive(ctxt);
                  str = "\u0000";
               } else {
                  CharSequence cs = (CharSequence)ctxt.handleUnexpectedToken(Character.TYPE, p);
                  str = cs.toString();
               }

               if (str.length() != 1) {
                  ctxt.reportInputMismatch(this, "Cannot convert a JSON String of length %d into a char element of char array", str.length());
               }

               sb.append(str.charAt(0));
            }

            return sb.toString().toCharArray();
         }
      }

      protected char[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return (char[])ctxt.handleUnexpectedToken(this._valueClass, p);
      }

      protected char[] _concat(char[] oldValue, char[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         char[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }
   }

   @JacksonStdImpl
   static final class BooleanDeser extends PrimitiveArrayDeserializers<boolean[]> {
      private static final long serialVersionUID = 1L;

      public BooleanDeser() {
         super(boolean[].class);
      }

      protected BooleanDeser(BooleanDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new BooleanDeser(this, nuller, unwrapSingle);
      }

      protected boolean[] _constructEmpty() {
         return new boolean[0];
      }

      public boolean[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (!p.isExpectedStartArrayToken()) {
            return (boolean[])this.handleNonArray(p, ctxt);
         } else {
            ArrayBuilders.BooleanBuilder builder = ctxt.getArrayBuilders().getBooleanBuilder();
            boolean[] chunk = (boolean[])builder.resetAndStart();
            int ix = 0;

            JsonToken t;
            try {
               while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                  boolean value;
                  if (t == JsonToken.VALUE_TRUE) {
                     value = true;
                  } else if (t == JsonToken.VALUE_FALSE) {
                     value = false;
                  } else if (t == JsonToken.VALUE_NULL) {
                     if (this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                        continue;
                     }

                     this._verifyNullForPrimitive(ctxt);
                     value = false;
                  } else {
                     value = this._parseBooleanPrimitive(p, ctxt);
                  }

                  if (ix >= chunk.length) {
                     chunk = (boolean[])builder.appendCompletedChunk(chunk, ix);
                     ix = 0;
                  }

                  chunk[ix++] = value;
               }
            } catch (Exception e) {
               throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }

            return (boolean[])builder.completeAndClearBuffer(chunk, ix);
         }
      }

      protected boolean[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return new boolean[]{this._parseBooleanPrimitive(p, ctxt)};
      }

      protected boolean[] _concat(boolean[] oldValue, boolean[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         boolean[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }
   }

   @JacksonStdImpl
   static final class ByteDeser extends PrimitiveArrayDeserializers<byte[]> {
      private static final long serialVersionUID = 1L;

      public ByteDeser() {
         super(byte[].class);
      }

      protected ByteDeser(ByteDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new ByteDeser(this, nuller, unwrapSingle);
      }

      protected byte[] _constructEmpty() {
         return new byte[0];
      }

      public LogicalType logicalType() {
         return LogicalType.Binary;
      }

      public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.currentToken();
         if (t == JsonToken.VALUE_STRING) {
            try {
               return p.getBinaryValue(ctxt.getBase64Variant());
            } catch (DatabindException | StreamReadException e) {
               String msg = ((JsonProcessingException)e).getOriginalMessage();
               if (msg.contains("base64")) {
                  return (byte[])ctxt.handleWeirdStringValue(byte[].class, p.getText(), msg);
               } else {
                  throw e;
               }
            }
         } else {
            if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
               Object ob = p.getEmbeddedObject();
               if (ob == null) {
                  return null;
               }

               if (ob instanceof byte[]) {
                  return (byte[])ob;
               }
            }

            if (!p.isExpectedStartArrayToken()) {
               return (byte[])this.handleNonArray(p, ctxt);
            } else {
               ArrayBuilders.ByteBuilder builder = ctxt.getArrayBuilders().getByteBuilder();
               byte[] chunk = (byte[])builder.resetAndStart();
               int ix = 0;

               try {
                  while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                     byte value;
                     if (t == JsonToken.VALUE_NUMBER_INT) {
                        value = p.getByteValue();
                     } else if (t == JsonToken.VALUE_NULL) {
                        if (this._nuller != null) {
                           this._nuller.getNullValue(ctxt);
                           continue;
                        }

                        this._verifyNullForPrimitive(ctxt);
                        value = 0;
                     } else {
                        value = this._parseBytePrimitive(p, ctxt);
                     }

                     if (ix >= chunk.length) {
                        chunk = (byte[])builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                     }

                     chunk[ix++] = value;
                  }
               } catch (Exception e) {
                  throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
               }

               return (byte[])builder.completeAndClearBuffer(chunk, ix);
            }
         }
      }

      protected byte[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.currentToken();
         byte value;
         if (t == JsonToken.VALUE_NUMBER_INT) {
            value = p.getByteValue();
         } else {
            if (t == JsonToken.VALUE_NULL) {
               if (this._nuller != null) {
                  this._nuller.getNullValue(ctxt);
                  return (byte[])this.getEmptyValue(ctxt);
               }

               this._verifyNullForPrimitive(ctxt);
               return null;
            }

            Number n = (Number)ctxt.handleUnexpectedToken(this._valueClass.getComponentType(), p);
            value = n.byteValue();
         }

         return new byte[]{value};
      }

      protected byte[] _concat(byte[] oldValue, byte[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         byte[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }
   }

   @JacksonStdImpl
   static final class ShortDeser extends PrimitiveArrayDeserializers<short[]> {
      private static final long serialVersionUID = 1L;

      public ShortDeser() {
         super(short[].class);
      }

      protected ShortDeser(ShortDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new ShortDeser(this, nuller, unwrapSingle);
      }

      protected short[] _constructEmpty() {
         return new short[0];
      }

      public short[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (!p.isExpectedStartArrayToken()) {
            return (short[])this.handleNonArray(p, ctxt);
         } else {
            ArrayBuilders.ShortBuilder builder = ctxt.getArrayBuilders().getShortBuilder();
            short[] chunk = (short[])builder.resetAndStart();
            int ix = 0;

            JsonToken t;
            try {
               while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                  short value;
                  if (t == JsonToken.VALUE_NULL) {
                     if (this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                        continue;
                     }

                     this._verifyNullForPrimitive(ctxt);
                     value = 0;
                  } else {
                     value = this._parseShortPrimitive(p, ctxt);
                  }

                  if (ix >= chunk.length) {
                     chunk = (short[])builder.appendCompletedChunk(chunk, ix);
                     ix = 0;
                  }

                  chunk[ix++] = value;
               }
            } catch (Exception e) {
               throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }

            return (short[])builder.completeAndClearBuffer(chunk, ix);
         }
      }

      protected short[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return new short[]{this._parseShortPrimitive(p, ctxt)};
      }

      protected short[] _concat(short[] oldValue, short[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         short[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }
   }

   @JacksonStdImpl
   static final class IntDeser extends PrimitiveArrayDeserializers<int[]> {
      private static final long serialVersionUID = 1L;
      public static final IntDeser instance = new IntDeser();

      public IntDeser() {
         super(int[].class);
      }

      protected IntDeser(IntDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new IntDeser(this, nuller, unwrapSingle);
      }

      protected int[] _constructEmpty() {
         return new int[0];
      }

      public int[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (!p.isExpectedStartArrayToken()) {
            return (int[])this.handleNonArray(p, ctxt);
         } else {
            ArrayBuilders.IntBuilder builder = ctxt.getArrayBuilders().getIntBuilder();
            int[] chunk = (int[])builder.resetAndStart();
            int ix = 0;

            JsonToken t;
            try {
               while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                  int value;
                  if (t == JsonToken.VALUE_NUMBER_INT) {
                     value = p.getIntValue();
                  } else if (t == JsonToken.VALUE_NULL) {
                     if (this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                        continue;
                     }

                     this._verifyNullForPrimitive(ctxt);
                     value = 0;
                  } else {
                     value = this._parseIntPrimitive(p, ctxt);
                  }

                  if (ix >= chunk.length) {
                     chunk = (int[])builder.appendCompletedChunk(chunk, ix);
                     ix = 0;
                  }

                  chunk[ix++] = value;
               }
            } catch (Exception e) {
               throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }

            return (int[])builder.completeAndClearBuffer(chunk, ix);
         }
      }

      protected int[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return new int[]{this._parseIntPrimitive(p, ctxt)};
      }

      protected int[] _concat(int[] oldValue, int[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         int[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }
   }

   @JacksonStdImpl
   static final class LongDeser extends PrimitiveArrayDeserializers<long[]> {
      private static final long serialVersionUID = 1L;
      public static final LongDeser instance = new LongDeser();

      public LongDeser() {
         super(long[].class);
      }

      protected LongDeser(LongDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new LongDeser(this, nuller, unwrapSingle);
      }

      protected long[] _constructEmpty() {
         return new long[0];
      }

      public long[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (!p.isExpectedStartArrayToken()) {
            return (long[])this.handleNonArray(p, ctxt);
         } else {
            ArrayBuilders.LongBuilder builder = ctxt.getArrayBuilders().getLongBuilder();
            long[] chunk = (long[])builder.resetAndStart();
            int ix = 0;

            JsonToken t;
            try {
               while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                  long value;
                  if (t == JsonToken.VALUE_NUMBER_INT) {
                     value = p.getLongValue();
                  } else if (t == JsonToken.VALUE_NULL) {
                     if (this._nuller != null) {
                        this._nuller.getNullValue(ctxt);
                        continue;
                     }

                     this._verifyNullForPrimitive(ctxt);
                     value = 0L;
                  } else {
                     value = this._parseLongPrimitive(p, ctxt);
                  }

                  if (ix >= chunk.length) {
                     chunk = (long[])builder.appendCompletedChunk(chunk, ix);
                     ix = 0;
                  }

                  chunk[ix++] = value;
               }
            } catch (Exception e) {
               throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }

            return (long[])builder.completeAndClearBuffer(chunk, ix);
         }
      }

      protected long[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return new long[]{this._parseLongPrimitive(p, ctxt)};
      }

      protected long[] _concat(long[] oldValue, long[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         long[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }
   }

   @JacksonStdImpl
   static final class FloatDeser extends PrimitiveArrayDeserializers<float[]> {
      private static final long serialVersionUID = 1L;

      public FloatDeser() {
         super(float[].class);
      }

      protected FloatDeser(FloatDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new FloatDeser(this, nuller, unwrapSingle);
      }

      protected float[] _constructEmpty() {
         return new float[0];
      }

      public float[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (!p.isExpectedStartArrayToken()) {
            float[] decoded = this._deserializeBinaryVector(p, ctxt);
            return decoded != null ? decoded : (float[])this.handleNonArray(p, ctxt);
         } else {
            ArrayBuilders.FloatBuilder builder = ctxt.getArrayBuilders().getFloatBuilder();
            float[] chunk = (float[])builder.resetAndStart();
            int ix = 0;

            JsonToken t;
            try {
               while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                  if (t == JsonToken.VALUE_NULL && this._nuller != null) {
                     this._nuller.getNullValue(ctxt);
                  } else {
                     float value = this._parseFloatPrimitive(p, ctxt);
                     if (ix >= chunk.length) {
                        chunk = (float[])builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                     }

                     chunk[ix++] = value;
                  }
               }
            } catch (Exception e) {
               throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }

            return (float[])builder.completeAndClearBuffer(chunk, ix);
         }
      }

      protected float[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return new float[]{this._parseFloatPrimitive(p, ctxt)};
      }

      protected float[] _concat(float[] oldValue, float[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         float[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }

      private float[] _deserializeBinaryVector(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.currentToken();
         byte[] packed = null;
         if (t == JsonToken.VALUE_STRING) {
            try {
               packed = p.getBinaryValue(ctxt.getBase64Variant());
            } catch (DatabindException | StreamReadException e) {
               String msg = ((JsonProcessingException)e).getOriginalMessage();
               if (msg.contains("base64")) {
                  return (float[])ctxt.handleWeirdStringValue(float[].class, p.getText(), msg);
               }

               throw e;
            }
         } else if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob instanceof byte[]) {
               packed = (byte[])ob;
            } else if (ob == null || ob instanceof float[]) {
               return (float[])ob;
            }
         }

         if (packed != null) {
            return this._unpack(ctxt, packed);
         } else {
            return null;
         }
      }

      private float[] _unpack(DeserializationContext ctxt, byte[] bytes) throws IOException {
         int bytesLen = bytes.length;
         if ((bytesLen & 3) != 0) {
            return (float[])ctxt.reportInputMismatch(this.handledType(), "Vector length for Packed Binary Float Vector (%d) not a multiple of 4 bytes", bytesLen);
         } else {
            int vectorLen = bytesLen >> 2;
            float[] floats = new float[vectorLen];
            int in = 0;

            int packed;
            for(int out = 0; in < bytesLen; floats[out++] = Float.intBitsToFloat(packed)) {
               packed = bytes[in++] << 24 | (bytes[in++] & 255) << 16 | (bytes[in++] & 255) << 8 | bytes[in++] & 255;
            }

            return floats;
         }
      }
   }

   @JacksonStdImpl
   static final class DoubleDeser extends PrimitiveArrayDeserializers<double[]> {
      private static final long serialVersionUID = 1L;

      public DoubleDeser() {
         super(double[].class);
      }

      protected DoubleDeser(DoubleDeser base, NullValueProvider nuller, Boolean unwrapSingle) {
         super(base, nuller, unwrapSingle);
      }

      protected PrimitiveArrayDeserializers<?> withResolved(NullValueProvider nuller, Boolean unwrapSingle) {
         return new DoubleDeser(this, nuller, unwrapSingle);
      }

      protected double[] _constructEmpty() {
         return new double[0];
      }

      public double[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (!p.isExpectedStartArrayToken()) {
            double[] decoded = this._deserializeBinaryVector(p, ctxt);
            return decoded != null ? decoded : (double[])this.handleNonArray(p, ctxt);
         } else {
            ArrayBuilders.DoubleBuilder builder = ctxt.getArrayBuilders().getDoubleBuilder();
            double[] chunk = (double[])builder.resetAndStart();
            int ix = 0;

            JsonToken t;
            try {
               while((t = p.nextToken()) != JsonToken.END_ARRAY) {
                  if (t == JsonToken.VALUE_NULL && this._nuller != null) {
                     this._nuller.getNullValue(ctxt);
                  } else {
                     double value = this._parseDoublePrimitive(p, ctxt);
                     if (ix >= chunk.length) {
                        chunk = (double[])builder.appendCompletedChunk(chunk, ix);
                        ix = 0;
                     }

                     chunk[ix++] = value;
                  }
               }
            } catch (Exception e) {
               throw JsonMappingException.wrapWithPath(e, chunk, builder.bufferedSize() + ix);
            }

            return (double[])builder.completeAndClearBuffer(chunk, ix);
         }
      }

      protected double[] handleSingleElementUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
         return new double[]{this._parseDoublePrimitive(p, ctxt)};
      }

      protected double[] _concat(double[] oldValue, double[] newValue) {
         int len1 = oldValue.length;
         int len2 = newValue.length;
         double[] result = Arrays.copyOf(oldValue, len1 + len2);
         System.arraycopy(newValue, 0, result, len1, len2);
         return result;
      }

      private double[] _deserializeBinaryVector(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonToken t = p.currentToken();
         byte[] packed = null;
         if (t == JsonToken.VALUE_STRING) {
            try {
               packed = p.getBinaryValue(ctxt.getBase64Variant());
            } catch (DatabindException | StreamReadException e) {
               String msg = ((JsonProcessingException)e).getOriginalMessage();
               if (msg.contains("base64")) {
                  return (double[])ctxt.handleWeirdStringValue(double[].class, p.getText(), msg);
               }

               throw e;
            }
         } else if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob instanceof byte[]) {
               packed = (byte[])ob;
            } else if (ob == null || ob instanceof double[]) {
               return (double[])ob;
            }
         }

         if (packed != null) {
            return this._unpack(ctxt, packed);
         } else {
            return null;
         }
      }

      private double[] _unpack(DeserializationContext ctxt, byte[] bytes) throws IOException {
         int bytesLen = bytes.length;
         if ((bytesLen & 7) != 0) {
            return (double[])ctxt.reportInputMismatch(this.handledType(), "Vector length for Packed Binary Double Vector (%d) not a multiple of 8 bytes", bytesLen);
         } else {
            int vectorLen = bytesLen >> 3;
            double[] doubles = new double[vectorLen];
            int in = 0;

            long packed;
            for(int out = 0; in < bytesLen; doubles[out++] = Double.longBitsToDouble(packed)) {
               int packed1 = bytes[in++] << 24 | (bytes[in++] & 255) << 16 | (bytes[in++] & 255) << 8 | bytes[in++] & 255;
               int packed2 = bytes[in++] << 24 | (bytes[in++] & 255) << 16 | (bytes[in++] & 255) << 8 | bytes[in++] & 255;
               packed = (long)packed1 << 32 | (long)packed2 & 4294967295L;
            }

            return doubles;
         }
      }
   }
}
