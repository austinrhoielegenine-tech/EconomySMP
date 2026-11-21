package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
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
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@JacksonStdImpl
public class ObjectArrayDeserializer extends ContainerDeserializerBase<Object> implements ContextualDeserializer {
   private static final long serialVersionUID = 1L;
   protected final boolean _untyped;
   protected final Class<?> _elementClass;
   protected JsonDeserializer<Object> _elementDeserializer;
   protected final TypeDeserializer _elementTypeDeserializer;
   protected final Object[] _emptyValue;

   public ObjectArrayDeserializer(JavaType arrayType0, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
      super(arrayType0, (NullValueProvider)null, (Boolean)null);
      ArrayType arrayType = (ArrayType)arrayType0;
      this._elementClass = arrayType.getContentType().getRawClass();
      this._untyped = this._elementClass == Object.class;
      this._elementDeserializer = elemDeser;
      this._elementTypeDeserializer = elemTypeDeser;
      this._emptyValue = arrayType.getEmptyArray();
   }

   protected ObjectArrayDeserializer(ObjectArrayDeserializer base, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      super((ContainerDeserializerBase)base, nuller, unwrapSingle);
      this._elementClass = base._elementClass;
      this._untyped = base._untyped;
      this._emptyValue = base._emptyValue;
      this._elementDeserializer = elemDeser;
      this._elementTypeDeserializer = elemTypeDeser;
   }

   public ObjectArrayDeserializer withDeserializer(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser) {
      return this.withResolved(elemTypeDeser, elemDeser, this._nullProvider, this._unwrapSingle);
   }

   public ObjectArrayDeserializer withResolved(TypeDeserializer elemTypeDeser, JsonDeserializer<?> elemDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      return Objects.equals(unwrapSingle, this._unwrapSingle) && nuller == this._nullProvider && elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer ? this : new ObjectArrayDeserializer(this, elemDeser, elemTypeDeser, nuller, unwrapSingle);
   }

   public boolean isCachable() {
      return this._elementDeserializer == null && this._elementTypeDeserializer == null;
   }

   public LogicalType logicalType() {
      return LogicalType.Array;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<?> valueDeser = this._elementDeserializer;
      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, this._containerType.getRawClass(), JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
      JavaType vt = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = ctxt.findContextualValueDeserializer(vt, property);
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
      }

      TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
      if (elemTypeDeser != null) {
         elemTypeDeser = elemTypeDeser.forProperty(property);
      }

      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
      return this.withResolved(elemTypeDeser, valueDeser, nuller, unwrapSingle);
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._elementDeserializer;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.CONSTANT;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      return this._emptyValue;
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt);
      } else if (this._elementDeserializer.getObjectIdReader() != null) {
         return this._deserializeWithObjectId(p, ctxt);
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         Object[] chunk = buffer.resetAndStart();
         int ix = 0;
         return this._deserialize(p, ctxt, buffer, ix, chunk);
      }
   }

   protected Object[] _deserialize(JsonParser p, DeserializationContext ctxt, ObjectBuffer buffer, int ix, Object[] chunk) throws JsonMappingException {
      JsonToken t;
      try {
         while((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value;
            if (t == JsonToken.VALUE_NULL) {
               if (this._skipNullValues) {
                  continue;
               }

               value = null;
            } else {
               value = this._deserializeNoNullChecks(p, ctxt);
            }

            if (value == null) {
               value = this._nullProvider.getNullValue(ctxt);
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
         throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
      }

      Object[] result;
      if (this._untyped) {
         result = buffer.completeAndClearBuffer(chunk, ix);
      } else {
         result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
      }

      ctxt.returnObjectBuffer(buffer);
      return result;
   }

   protected Object[] _deserializeWithObjectId(JsonParser p, DeserializationContext ctxt) throws JsonMappingException {
      ObjectArrayReferringAccumulator acc = new ObjectArrayReferringAccumulator(this._untyped, this._elementClass);
      int ix = 0;

      JsonToken t;
      try {
         while((t = p.nextToken()) != JsonToken.END_ARRAY) {
            try {
               Object value;
               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = null;
               } else {
                  value = this._deserializeNoNullChecks(p, ctxt);
               }

               if (value == null) {
                  value = this._nullProvider.getNullValue(ctxt);
                  if (value == null && this._skipNullValues) {
                     continue;
                  }
               }

               acc.add(value);
            } catch (UnresolvedForwardReference reference) {
               ArrayReferring referring = new ArrayReferring(reference, this._elementClass, acc);
               reference.getRoid().appendReferring(referring);
            }

            ++ix;
         }
      } catch (Exception var9) {
         boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
         if (!wrap) {
            ClassUtil.throwIfRTE(var9);
         }

         throw JsonMappingException.wrapWithPath(var9, acc.buildArray(), ix);
      }

      return acc.buildArray();
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue0) throws IOException {
      Object[] intoValue = intoValue0;
      if (!p.isExpectedStartArrayToken()) {
         Object[] arr = this.handleNonArray(p, ctxt);
         if (arr == null) {
            return intoValue;
         } else {
            int offset = intoValue.length;
            Object[] result = Arrays.copyOf(intoValue, offset + arr.length);
            System.arraycopy(arr, 0, result, offset, arr.length);
            return result;
         }
      } else {
         ObjectBuffer buffer = ctxt.leaseObjectBuffer();
         int ix = intoValue.length;
         Object[] chunk = buffer.resetAndStart(intoValue, ix);

         JsonToken t;
         try {
            while((t = p.nextToken()) != JsonToken.END_ARRAY) {
               Object value;
               if (t == JsonToken.VALUE_NULL) {
                  if (this._skipNullValues) {
                     continue;
                  }

                  value = null;
               } else {
                  value = this._deserializeNoNullChecks(p, ctxt);
               }

               if (value == null) {
                  value = this._nullProvider.getNullValue(ctxt);
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
            throw JsonMappingException.wrapWithPath(e, chunk, buffer.bufferedSize() + ix);
         }

         Object[] result;
         if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
         } else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
         }

         ctxt.returnObjectBuffer(buffer);
         return result;
      }
   }

   protected Byte[] deserializeFromBase64(JsonParser p, DeserializationContext ctxt) throws IOException {
      byte[] b = p.getBinaryValue(ctxt.getBase64Variant());
      Byte[] result = new Byte[b.length];
      int i = 0;

      for(int len = b.length; i < len; ++i) {
         result[i] = b[i];
      }

      return result;
   }

   protected Object handleNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (!canWrap) {
         if (p.hasToken(JsonToken.VALUE_STRING)) {
            return this._elementClass == Byte.class ? this.deserializeFromBase64(p, ctxt) : this._deserializeFromString(p, ctxt);
         } else {
            return ctxt.handleUnexpectedToken(this._containerType, p);
         }
      } else if (this._elementDeserializer.getObjectIdReader() != null) {
         return this._wrapSingleWithObjectId(p, ctxt);
      } else {
         Object value;
         if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._skipNullValues) {
               return this._emptyValue;
            }

            value = null;
         } else {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
               String textValue = p.getText();
               if (textValue.isEmpty()) {
                  CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.EmptyString);
                  if (act != CoercionAction.Fail) {
                     return this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "empty String (\"\")");
                  }
               } else if (_isBlank(textValue)) {
                  CoercionAction act = ctxt.findCoercionFromBlankString(this.logicalType(), this.handledType(), CoercionAction.Fail);
                  if (act != CoercionAction.Fail) {
                     return this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "blank String (all whitespace)");
                  }
               }
            }

            value = this._deserializeNoNullChecks(p, ctxt);
         }

         if (value == null) {
            value = this._nullProvider.getNullValue(ctxt);
            if (value == null && this._skipNullValues) {
               return this._emptyValue;
            }
         }

         Object[] result;
         if (this._untyped) {
            result = new Object[1];
         } else {
            result = Array.newInstance(this._elementClass, 1);
         }

         result[0] = value;
         return result;
      }
   }

   protected Object[] _wrapSingleWithObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
      ObjectArrayReferringAccumulator acc = new ObjectArrayReferringAccumulator(this._untyped, this._elementClass);

      try {
         Object value;
         if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._skipNullValues) {
               return this._emptyValue;
            }

            value = null;
         } else {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
               String textValue = p.getText();
               if (textValue.isEmpty()) {
                  CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.EmptyString);
                  if (act != CoercionAction.Fail) {
                     return this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "empty String (\"\")");
                  }
               } else if (_isBlank(textValue)) {
                  CoercionAction act = ctxt.findCoercionFromBlankString(this.logicalType(), this.handledType(), CoercionAction.Fail);
                  if (act != CoercionAction.Fail) {
                     return this._deserializeFromEmptyString(p, ctxt, act, this.handledType(), "blank String (all whitespace)");
                  }
               }
            }

            value = this._deserializeNoNullChecks(p, ctxt);
         }

         if (value == null) {
            value = this._nullProvider.getNullValue(ctxt);
            if (value == null && this._skipNullValues) {
               return this._emptyValue;
            }
         }

         acc.add(value);
      } catch (UnresolvedForwardReference reference) {
         ArrayReferring referring = new ArrayReferring(reference, this._elementClass, acc);
         reference.getRoid().appendReferring(referring);
      }

      return acc.buildArray();
   }

   protected Object _deserializeNoNullChecks(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this._elementTypeDeserializer == null ? this._elementDeserializer.deserialize(p, ctxt) : this._elementDeserializer.deserializeWithType(p, ctxt, this._elementTypeDeserializer);
   }

   private static class ObjectArrayReferringAccumulator {
      private final boolean _untyped;
      private final Class<?> _elementType;
      private final List<Object> _accumulator = new ArrayList();
      private Object[] _array;

      ObjectArrayReferringAccumulator(boolean untyped, Class<?> elementType) {
         this._untyped = untyped;
         this._elementType = elementType;
      }

      void add(Object value) {
         this._accumulator.add(value);
      }

      Object[] buildArray() {
         if (this._untyped) {
            this._array = new Object[this._accumulator.size()];
         } else {
            this._array = Array.newInstance(this._elementType, this._accumulator.size());
         }

         for(int i = 0; i < this._accumulator.size(); ++i) {
            if (!(this._accumulator.get(i) instanceof ArrayReferring)) {
               this._array[i] = this._accumulator.get(i);
            }
         }

         return this._array;
      }
   }

   private static class ArrayReferring extends ReadableObjectId.Referring {
      private final ObjectArrayReferringAccumulator _parent;

      ArrayReferring(UnresolvedForwardReference ref, Class<?> type, ObjectArrayReferringAccumulator acc) {
         super(ref, type);
         this._parent = acc;
         this._parent._accumulator.add(this);
      }

      public void handleResolvedForwardReference(Object id, Object value) throws JacksonException {
         for(int i = 0; i < this._parent._accumulator.size(); ++i) {
            if (this._parent._accumulator.get(i) == this) {
               this._parent._array[i] = value;
               return;
            }
         }

         throw new IllegalArgumentException("Trying to resolve unknown reference: " + id);
      }
   }
}
