package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.util.Objects;

@JacksonStdImpl
public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer {
   private static final long serialVersionUID = 2L;
   private static final String[] NO_STRINGS = new String[0];
   public static final StringArrayDeserializer instance = new StringArrayDeserializer();
   private final JsonDeserializer<String> _elementDeserializer;
   private final NullValueProvider _nullProvider;
   private final Boolean _unwrapSingle;
   private final boolean _skipNullValues;

   public StringArrayDeserializer() {
      this((JsonDeserializer)null, (NullValueProvider)null, (Boolean)null);
   }

   private StringArrayDeserializer(JsonDeserializer<?> deser, NullValueProvider nuller, Boolean unwrapSingle) {
      super(String[].class);
      this._elementDeserializer = deser;
      this._nullProvider = nuller;
      this._unwrapSingle = unwrapSingle;
      this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
   }

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
      return NO_STRINGS;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<?> deser = this._elementDeserializer;
      deser = this.findConvertingContentDeserializer(ctxt, property, deser);
      JavaType type = ctxt.constructType(String.class);
      if (deser == null) {
         deser = ctxt.findContextualValueDeserializer(type, property);
      } else {
         deser = ctxt.handleSecondaryContextualization(deser, property, type);
      }

      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, String[].class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, deser);
      if (deser != null && this.isDefaultDeserializer(deser)) {
         deser = null;
      }

      return this._elementDeserializer == deser && Objects.equals(this._unwrapSingle, unwrapSingle) && this._nullProvider == nuller ? this : new StringArrayDeserializer(deser, nuller, unwrapSingle);
   }

   public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt);
      } else if (this._elementDeserializer != null) {
         return this._deserializeCustom(p, ctxt, (String[])null);
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         Object[] chunk = buffer.resetAndStart();
         int ix = 0;

         try {
            while(true) {
               String value = p.nextTextValue();
               if (value == null) {
                  JsonToken t = p.currentToken();
                  if (t == JsonToken.END_ARRAY) {
                     break;
                  }

                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        continue;
                     }
                  } else {
                     value = this._parseString(p, ctxt, this._nullProvider);
                  }

                  if (value == null) {
                     value = (String)this._nullProvider.getNullValue(ctxt);
                     if (value == null && this._skipNullValues) {
                        continue;
                     }
                  }
               }

               if (ix >= chunk.length) {
                  chunk = buffer.appendCompletedChunk(chunk);
                  ix = 0;
               }

               chunk[ix++] = value;
            }
         } catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
         }

         String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   private String[] _deserializeCustom(JsonParser p, DeserializationContext ctxt, String[] old) throws IOException {
      ObjectBuffer buffer = ctxt.leaseObjectBuffer();
      int ix;
      Object[] chunk;
      if (old == null) {
         ix = 0;
         chunk = buffer.resetAndStart();
      } else {
         ix = old.length;
         chunk = buffer.resetAndStart(old, ix);
      }

      JsonDeserializer<String> deser = this._elementDeserializer;

      try {
         while(true) {
            String value;
            if (p.nextTextValue() == null) {
               JsonToken t = p.currentToken();
               if (t == JsonToken.END_ARRAY) {
                  break;
               }

               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = null;
               } else {
                  value = deser.deserialize(p, ctxt);
               }
            } else {
               value = deser.deserialize(p, ctxt);
            }

            if (value == null) {
               value = (String)this._nullProvider.getNullValue(ctxt);
               if (value == null && this._skipNullValues) {
                  continue;
               }
            }

            if (ix >= chunk.length) {
               chunk = buffer.appendCompletedChunk(chunk);
               ix = 0;
            }

            chunk[ix++] = value;
         }
      } catch (Exception e) {
         throw JsonMappingException.wrapWithPath(e, String.class, ix);
      }

      String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
      ctxt.returnObjectBuffer(buffer);
      return result;
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   public String[] deserialize(JsonParser p, DeserializationContext ctxt, String[] intoValue) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         String[] arr = this.handleNonArray(p, ctxt);
         if (arr == null) {
            return intoValue;
         } else {
            int offset = intoValue.length;
            String[] result = new String[offset + arr.length];
            System.arraycopy(intoValue, 0, result, 0, offset);
            System.arraycopy(arr, 0, result, offset, arr.length);
            return result;
         }
      } else if (this._elementDeserializer != null) {
         return this._deserializeCustom(p, ctxt, intoValue);
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         int ix = intoValue.length;
         Object[] chunk = buffer.resetAndStart(intoValue, ix);

         try {
            while(true) {
               String value = p.nextTextValue();
               if (value == null) {
                  JsonToken t = p.currentToken();
                  if (t == JsonToken.END_ARRAY) {
                     break;
                  }

                  if (t == JsonToken.VALUE_NULL) {
                     if (this._skipNullValues) {
                        return NO_STRINGS;
                     }
                  } else {
                     value = this._parseString(p, ctxt, this._nullProvider);
                  }

                  if (value == null) {
                     value = (String)this._nullProvider.getNullValue(ctxt);
                     if (value == null && this._skipNullValues) {
                        continue;
                     }
                  }
               }

               if (ix >= chunk.length) {
                  chunk = buffer.appendCompletedChunk(chunk);
                  ix = 0;
               }

               chunk[ix++] = value;
            }
         } catch (Exception e) {
            throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
         }

         String[] result = (String[])buffer.completeAndClearBuffer(chunk, ix, String.class);
         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   private final String[] handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (canWrap) {
         String value;
         if (p.hasToken(JsonToken.VALUE_NULL)) {
            value = (String)this._nullProvider.getNullValue(ctxt);
         } else {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
               String textValue = p.getText();
               if (textValue.isEmpty()) {
                  CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.EmptyString);
                  if (act != CoercionAction.Fail) {
                     return (String[])this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "empty String (\"\")");
                  }
               } else if (_isBlank(textValue)) {
                  CoercionAction act = ctxt.findCoercionFromBlankString(this.logicalType(), this.handledType(), CoercionAction.Fail);
                  if (act != CoercionAction.Fail) {
                     return (String[])this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "blank String (all whitespace)");
                  }
               }
            }

            value = this._parseString(p, ctxt, this._nullProvider);
         }

         return new String[]{value};
      } else {
         return p.hasToken(JsonToken.VALUE_STRING) ? (String[])this._deserializeFromString(p, ctxt) : (String[])((String[])ctxt.handleUnexpectedToken(this._valueClass, p));
      }
   }
}
