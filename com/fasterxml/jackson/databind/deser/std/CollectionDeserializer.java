package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@JacksonStdImpl
public class CollectionDeserializer extends ContainerDeserializerBase<Collection<Object>> implements ContextualDeserializer {
   private static final long serialVersionUID = -1L;
   protected final JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final ValueInstantiator _valueInstantiator;
   protected final JsonDeserializer<Object> _delegateDeserializer;

   public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
      this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, (JsonDeserializer)null, (NullValueProvider)null, (Boolean)null);
   }

   protected CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle) {
      super(collectionType, nuller, unwrapSingle);
      this._valueDeserializer = valueDeser;
      this._valueTypeDeserializer = valueTypeDeser;
      this._valueInstantiator = valueInstantiator;
      this._delegateDeserializer = delegateDeser;
   }

   protected CollectionDeserializer(CollectionDeserializer src) {
      super((ContainerDeserializerBase)src);
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
   }

   protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle) {
      return new CollectionDeserializer(this._containerType, vd, vtd, this._valueInstantiator, dd, nuller, unwrapSingle);
   }

   public boolean isCachable() {
      return this._valueDeserializer == null && this._valueTypeDeserializer == null && this._delegateDeserializer == null;
   }

   public LogicalType logicalType() {
      return LogicalType.Collection;
   }

   public CollectionDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      JsonDeserializer<Object> delegateDeser = null;
      if (this._valueInstantiator != null) {
         if (this._valueInstantiator.canCreateUsingDelegate()) {
            JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
               ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }

            delegateDeser = this.findDeserializer(ctxt, delegateType, property);
         } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
            JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
            if (delegateType == null) {
               ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
            }

            delegateDeser = this.findDeserializer(ctxt, delegateType, property);
         }
      }

      Boolean unwrapSingle = this.findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      JsonDeserializer<?> valueDeser = this._valueDeserializer;
      valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
      JavaType vt = this._containerType.getContentType();
      if (valueDeser == null) {
         valueDeser = ctxt.findContextualValueDeserializer(vt, property);
      } else {
         valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
      }

      TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
      if (valueTypeDeser != null) {
         valueTypeDeser = valueTypeDeser.forProperty(property);
      }

      NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
      return Objects.equals(unwrapSingle, this._unwrapSingle) && nuller == this._nullProvider && delegateDeser == this._delegateDeserializer && valueDeser == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer ? this : this.withResolved(delegateDeser, valueDeser, valueTypeDeser, nuller, unwrapSingle);
   }

   public JsonDeserializer<Object> getContentDeserializer() {
      return this._valueDeserializer;
   }

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._delegateDeserializer != null) {
         return (Collection)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else if (p.isExpectedStartArrayToken()) {
         return this._deserializeFromArray(p, ctxt, this.createDefaultInstance(ctxt));
      } else {
         return p.hasToken(JsonToken.VALUE_STRING) ? this._deserializeFromString(p, ctxt, p.getText()) : this.handleNonArray(p, ctxt, this.createDefaultInstance(ctxt));
      }
   }

   protected Collection<Object> createDefaultInstance(DeserializationContext ctxt) throws IOException {
      return (Collection)this._valueInstantiator.createUsingDefault(ctxt);
   }

   public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      return p.isExpectedStartArrayToken() ? this._deserializeFromArray(p, ctxt, result) : this.handleNonArray(p, ctxt, result);
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromArray(p, ctxt);
   }

   protected Collection<Object> _deserializeFromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
      Class<?> rawTargetType = this.handledType();
      if (value.isEmpty()) {
         CoercionAction act = ctxt.findCoercionAction(this.logicalType(), rawTargetType, CoercionInputShape.EmptyString);
         if (act != null && act != CoercionAction.Fail) {
            return (Collection)this._deserializeFromEmptyString(p, ctxt, act, rawTargetType, "empty String (\"\")");
         }
      } else if (_isBlank(value)) {
         CoercionAction act = ctxt.findCoercionFromBlankString(this.logicalType(), rawTargetType, CoercionAction.Fail);
         if (act != CoercionAction.Fail) {
            return (Collection)this._deserializeFromEmptyString(p, ctxt, act, rawTargetType, "blank String (all whitespace)");
         }
      }

      return this.handleNonArray(p, ctxt, this.createDefaultInstance(ctxt));
   }

   protected Collection<Object> _deserializeFromArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      p.assignCurrentValue(result);
      if (this._valueDeserializer.getObjectIdReader() != null) {
         return this._deserializeWithObjectId(p, ctxt, result);
      } else {
         JsonToken t;
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
                  if (value == null) {
                     this._tryToAddNull(p, ctxt, result);
                     continue;
                  }
               }

               result.add(value);
            } catch (Exception var7) {
               boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
               if (!wrap) {
                  ClassUtil.throwIfRTE(var7);
               }

               throw JsonMappingException.wrapWithPath(var7, result, result.size());
            }
         }

         return result;
      }
   }

   protected final Collection<Object> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      boolean canWrap = this._unwrapSingle == Boolean.TRUE || this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
      if (!canWrap) {
         return (Collection)ctxt.handleUnexpectedToken(this._containerType, p);
      } else if (this._valueDeserializer.getObjectIdReader() != null) {
         return this._wrapSingleWithObjectId(p, ctxt, result);
      } else {
         Object value;
         try {
            if (p.hasToken(JsonToken.VALUE_NULL)) {
               if (this._skipNullValues) {
                  return result;
               }

               value = null;
            } else {
               value = this._deserializeNoNullChecks(p, ctxt);
            }

            if (value == null) {
               value = this._nullProvider.getNullValue(ctxt);
               if (value == null) {
                  this._tryToAddNull(p, ctxt, result);
                  return result;
               }
            }
         } catch (Exception var8) {
            boolean wrap = ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
            if (!wrap) {
               ClassUtil.throwIfRTE(var8);
            }

            throw JsonMappingException.wrapWithPath(var8, this._containerType.getContentType().getRawClass(), result.size());
         }

         result.add(value);
         return result;
      }
   }

   protected Collection<Object> _deserializeWithObjectId(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      if (!p.isExpectedStartArrayToken()) {
         return this.handleNonArray(p, ctxt, result);
      } else {
         p.assignCurrentValue(result);
         CollectionReferringAccumulator referringAccumulator = new CollectionReferringAccumulator(this._containerType.getContentType().getRawClass(), result);

         JsonToken t;
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

               referringAccumulator.add(value);
            } catch (UnresolvedForwardReference reference) {
               ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
               reference.getRoid().appendReferring(ref);
            } catch (Exception var9) {
               boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
               if (!wrap) {
                  ClassUtil.throwIfRTE(var9);
               }

               throw JsonMappingException.wrapWithPath(var9, result, result.size());
            }
         }

         return result;
      }
   }

   protected Collection<Object> _wrapSingleWithObjectId(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
      CollectionReferringAccumulator referringAccumulator = new CollectionReferringAccumulator(this.getContentType().getRawClass(), result);

      try {
         Object value;
         if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (this._skipNullValues) {
               return result;
            }

            value = null;
         } else {
            value = this._deserializeNoNullChecks(p, ctxt);
         }

         if (value == null) {
            value = this._nullProvider.getNullValue(ctxt);
            if (value == null) {
               this._tryToAddNull(p, ctxt, result);
               return result;
            }
         }

         referringAccumulator.add(value);
      } catch (UnresolvedForwardReference reference) {
         ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
         reference.getRoid().appendReferring(ref);
      } catch (Exception var8) {
         if (!ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
            ClassUtil.throwIfRTE(var8);
         }

         throw JsonMappingException.wrapWithPath(var8, this.getContentType().getRawClass(), result.size());
      }

      return result;
   }

   protected Object _deserializeNoNullChecks(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this._valueTypeDeserializer == null ? this._valueDeserializer.deserialize(p, ctxt) : this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
   }

   protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt, Collection<?> set) throws IOException {
      if (!this._skipNullValues) {
         try {
            set.add((Object)null);
         } catch (NullPointerException var5) {
            ctxt.handleUnexpectedToken(this._valueType, JsonToken.VALUE_NULL, p, "`java.util.Collection` of type %s does not accept `null` values", ClassUtil.getTypeDescription(this.getValueType(ctxt)));
         }

      }
   }

   public static class CollectionReferringAccumulator {
      private final Class<?> _elementType;
      private final Collection<Object> _result;
      private List<CollectionReferring> _accumulator = new ArrayList();

      public CollectionReferringAccumulator(Class<?> elementType, Collection<Object> result) {
         this._elementType = elementType;
         this._result = result;
      }

      public void add(Object value) {
         if (this._accumulator.isEmpty()) {
            this._result.add(value);
         } else {
            CollectionReferring ref = (CollectionReferring)this._accumulator.get(this._accumulator.size() - 1);
            ref.next.add(value);
         }

      }

      public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference) {
         CollectionReferring id = new CollectionReferring(this, reference, this._elementType);
         this._accumulator.add(id);
         return id;
      }

      public void resolveForwardReference(Object id, Object value) throws IOException {
         Iterator<CollectionReferring> iterator = this._accumulator.iterator();

         CollectionReferring ref;
         for(Collection<Object> previous = this._result; iterator.hasNext(); previous = ref.next) {
            ref = (CollectionReferring)iterator.next();
            if (ref.hasId(id)) {
               iterator.remove();
               previous.add(value);
               previous.addAll(ref.next);
               return;
            }
         }

         throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
      }
   }

   private static final class CollectionReferring extends ReadableObjectId.Referring {
      private final CollectionReferringAccumulator _parent;
      public final List<Object> next = new ArrayList();

      CollectionReferring(CollectionReferringAccumulator parent, UnresolvedForwardReference reference, Class<?> contentType) {
         super(reference, contentType);
         this._parent = parent;
      }

      public void handleResolvedForwardReference(Object id, Object value) throws IOException {
         this._parent.resolveForwardReference(id, value);
      }
   }
}
