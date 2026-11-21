package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.BitSet;

public class PropertyValueBuffer {
   protected final JsonParser _parser;
   protected final DeserializationContext _context;
   protected final ObjectIdReader _objectIdReader;
   protected final Object[] _creatorParameters;
   protected int _paramsNeeded;
   protected int _paramsSeen;
   protected final BitSet _paramsSeenBig;
   protected PropertyValue _buffered;
   protected Object _idValue;
   protected final SettableAnyProperty _anyParamSetter;
   protected PropertyValue _anyParamBuffered;
   protected final BitSet _injectablePropIndexes;

   public PropertyValueBuffer(JsonParser p, DeserializationContext ctxt, int paramCount, ObjectIdReader oir, SettableAnyProperty anyParamSetter, BitSet injectablePropIndexes) {
      this._parser = p;
      this._context = ctxt;
      this._paramsNeeded = paramCount;
      this._objectIdReader = oir;
      this._creatorParameters = new Object[paramCount];
      if (paramCount < 32) {
         this._paramsSeenBig = null;
      } else {
         this._paramsSeenBig = new BitSet();
      }

      if (anyParamSetter != null && anyParamSetter.getParameterIndex() >= 0) {
         this._anyParamSetter = anyParamSetter;
      } else {
         this._anyParamSetter = null;
      }

      this._injectablePropIndexes = injectablePropIndexes == null ? null : (BitSet)injectablePropIndexes.clone();
   }

   /** @deprecated */
   @Deprecated
   public PropertyValueBuffer(JsonParser p, DeserializationContext ctxt, int paramCount, ObjectIdReader oir, SettableAnyProperty anyParamSetter) {
      this(p, ctxt, paramCount, oir, anyParamSetter, (BitSet)null);
   }

   public final boolean hasParameter(SettableBeanProperty prop) {
      int ix = prop.getCreatorIndex();
      if (this._paramsSeenBig == null) {
         if ((this._paramsSeen >> ix & 1) == 1) {
            return true;
         }
      } else if (this._paramsSeenBig.get(ix)) {
         return true;
      }

      return this._anyParamSetter != null && ix == this._anyParamSetter.getParameterIndex();
   }

   public Object getParameter(SettableBeanProperty prop) throws JsonMappingException {
      Object value;
      if (this.hasParameter(prop)) {
         value = this._creatorParameters[prop.getCreatorIndex()];
      } else {
         value = this._creatorParameters[prop.getCreatorIndex()] = this._findMissing(prop);
      }

      if (value == null && this._anyParamSetter != null && prop.getCreatorIndex() == this._anyParamSetter.getParameterIndex()) {
         value = this._createAndSetAnySetterValue();
      }

      return value == null && this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES) ? this._context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES` enabled", prop.getName(), prop.getCreatorIndex()) : value;
   }

   public Object[] getParameters(SettableBeanProperty[] props) throws JsonMappingException, IOException {
      if (this._paramsNeeded > 0) {
         if (this._paramsSeenBig == null) {
            int mask = this._paramsSeen;
            int ix = 0;

            for(int len = this._creatorParameters.length; ix < len; mask >>= 1) {
               if ((mask & 1) == 0) {
                  this._creatorParameters[ix] = this._findMissing(props[ix]);
               }

               ++ix;
            }
         } else {
            int len = this._creatorParameters.length;

            for(int ix = 0; (ix = this._paramsSeenBig.nextClearBit(ix)) < len; ++ix) {
               this._creatorParameters[ix] = this._findMissing(props[ix]);
            }
         }
      }

      if (this._anyParamSetter != null) {
         this._creatorParameters[this._anyParamSetter.getParameterIndex()] = this._createAndSetAnySetterValue();
      }

      if (this._injectablePropIndexes != null) {
         for(int ix = this._injectablePropIndexes.nextSetBit(0); ix >= 0; ix = this._injectablePropIndexes.nextSetBit(ix + 1)) {
            this._inject(props[ix]);
         }
      }

      if (this._context.isEnabled(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)) {
         int len = this._creatorParameters.length;

         for(int ix = 0; ix < len; ++ix) {
            if (this._creatorParameters[ix] == null) {
               SettableBeanProperty prop = props[ix];
               this._context.reportInputMismatch(prop, "Null value for creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES` enabled", prop.getName(), props[ix].getCreatorIndex());
            }
         }
      }

      return this._creatorParameters;
   }

   private Object _createAndSetAnySetterValue() throws JsonMappingException {
      Object anySetterParameterObject = this._anyParamSetter.createParameterObject();

      for(PropertyValue pv = this._anyParamBuffered; pv != null; pv = pv.next) {
         try {
            pv.setValue(anySetterParameterObject);
         } catch (JsonMappingException e) {
            throw e;
         } catch (IOException e) {
            throw JsonMappingException.fromUnexpectedIOE(e);
         }
      }

      return anySetterParameterObject;
   }

   protected Object _findMissing(SettableBeanProperty prop) throws JsonMappingException {
      if (this._anyParamSetter != null && prop.getCreatorIndex() == this._anyParamSetter.getParameterIndex() && this._anyParamBuffered != null) {
         return null;
      } else {
         JacksonInject.Value injection = prop.getInjectionDefinition();
         if (injection != null) {
            this._injectablePropIndexes.clear(prop.getCreatorIndex());
            return this._context.findInjectableValue(prop.getInjectableValueId(), prop, (Object)null, injection.getOptional(), injection.getUseInput());
         } else {
            if (prop.isRequired()) {
               this._context.reportInputMismatch(prop, "Missing required creator property '%s' (index %d)", prop.getName(), prop.getCreatorIndex());
            }

            if (this._context.isEnabled(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)) {
               this._context.reportInputMismatch(prop, "Missing creator property '%s' (index %d); `DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES` enabled", prop.getName(), prop.getCreatorIndex());
            }

            try {
               Object absentValue = prop.getNullValueProvider().getAbsentValue(this._context);
               if (absentValue != null) {
                  return absentValue;
               } else {
                  JsonDeserializer<Object> deser = prop.getValueDeserializer();
                  return deser.getAbsentValue(this._context);
               }
            } catch (DatabindException var5) {
               AnnotatedMember member = prop.getMember();
               if (member != null) {
                  var5.prependPath(member.getDeclaringClass(), prop.getName());
               }

               throw var5;
            }
         }
      }
   }

   private void _inject(SettableBeanProperty prop) throws JsonMappingException {
      JacksonInject.Value injection = prop.getInjectionDefinition();
      if (injection != null) {
         Boolean useInput = injection.getUseInput();
         if (!Boolean.TRUE.equals(useInput)) {
            Object value = this._context.findInjectableValue(injection.getId(), prop, prop.getMember(), injection.getOptional(), useInput);
            if (value != null) {
               int ix = prop.getCreatorIndex();
               this._creatorParameters[ix] = value;
            }
         }
      }

   }

   public boolean readIdProperty(String propName) throws IOException {
      if (this._objectIdReader != null && propName.equals(this._objectIdReader.propertyName.getSimpleName())) {
         this._idValue = this._objectIdReader.readObjectReference(this._parser, this._context);
         return true;
      } else {
         return false;
      }
   }

   public Object handleIdValue(DeserializationContext ctxt, Object bean) throws IOException {
      if (this._objectIdReader != null) {
         if (this._idValue != null) {
            ReadableObjectId roid = ctxt.findObjectId(this._idValue, this._objectIdReader.generator, this._objectIdReader.resolver);
            roid.bindItem(bean);
            SettableBeanProperty idProp = this._objectIdReader.idProperty;
            if (idProp != null) {
               if (idProp instanceof CreatorProperty) {
                  return bean;
               }

               return idProp.setAndReturn(bean, this._idValue);
            }
         } else {
            ctxt.reportUnresolvedObjectId(this._objectIdReader, bean);
         }
      }

      return bean;
   }

   protected PropertyValue buffered() {
      return this._buffered;
   }

   public boolean isComplete() {
      return this._paramsNeeded <= 0;
   }

   public boolean assignParameter(SettableBeanProperty prop, Object value) {
      int ix = prop.getCreatorIndex();
      this._creatorParameters[ix] = value;
      if (this._paramsSeenBig == null) {
         int old = this._paramsSeen;
         int newValue = old | 1 << ix;
         if (old != newValue) {
            this._paramsSeen = newValue;
            if (--this._paramsNeeded <= 0) {
               return this._objectIdReader == null || this._idValue != null;
            }
         }
      } else if (!this._paramsSeenBig.get(ix)) {
         this._paramsSeenBig.set(ix);
         if (--this._paramsNeeded <= 0) {
            return this._objectIdReader == null || this._idValue != null;
         }
      }

      return false;
   }

   public void bufferProperty(SettableBeanProperty prop, Object value) {
      this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
   }

   public void bufferAnyProperty(SettableAnyProperty prop, String propName, Object value) {
      this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
   }

   public void bufferMapProperty(Object key, Object value) {
      this._buffered = new PropertyValue.Map(this._buffered, value, key);
   }

   public void bufferAnyParameterProperty(SettableAnyProperty prop, String propName, Object value) {
      this._anyParamBuffered = new PropertyValue.AnyParameter(this._anyParamBuffered, value, prop, propName);
   }

   public void bufferMergingProperty(SettableBeanProperty prop, TokenBuffer buffered) {
      this._buffered = new PropertyValue.Merging(this._buffered, buffered, prop);
   }
}
