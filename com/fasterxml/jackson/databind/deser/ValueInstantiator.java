package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class ValueInstantiator {
   public ValueInstantiator createContextual(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
      return this;
   }

   public Class<?> getValueClass() {
      return Object.class;
   }

   public String getValueTypeDesc() {
      Class<?> cls = this.getValueClass();
      return cls == null ? "UNKNOWN" : cls.getName();
   }

   public boolean canInstantiate() {
      return this.canCreateUsingDefault() || this.canCreateUsingDelegate() || this.canCreateUsingArrayDelegate() || this.canCreateFromObjectWith() || this.canCreateFromString() || this.canCreateFromInt() || this.canCreateFromLong() || this.canCreateFromDouble() || this.canCreateFromBoolean();
   }

   public boolean canCreateFromString() {
      return false;
   }

   public boolean canCreateFromInt() {
      return false;
   }

   public boolean canCreateFromLong() {
      return false;
   }

   public boolean canCreateFromBigInteger() {
      return false;
   }

   public boolean canCreateFromDouble() {
      return false;
   }

   public boolean canCreateFromBigDecimal() {
      return false;
   }

   public boolean canCreateFromBoolean() {
      return false;
   }

   public boolean canCreateUsingDefault() {
      return this.getDefaultCreator() != null;
   }

   public boolean canCreateUsingDelegate() {
      return false;
   }

   public boolean canCreateUsingArrayDelegate() {
      return false;
   }

   public boolean canCreateFromObjectWith() {
      return false;
   }

   public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
      return null;
   }

   public JavaType getDelegateType(DeserializationConfig config) {
      return null;
   }

   public JavaType getArrayDelegateType(DeserializationConfig config) {
      return null;
   }

   public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no default no-arguments constructor found");
   }

   public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no creator with arguments specified");
   }

   public Object createUsingDefaultOrWithoutArguments(DeserializationContext ctxt) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "neither default (no-arguments) nor with-arguments Creator found");
   }

   public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
      return this.createFromObjectWith(ctxt, buffer.getParameters(props));
   }

   public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no delegate creator specified");
   }

   public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no array delegate creator specified");
   }

   public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, ctxt.getParser(), "no String-argument constructor/factory method to deserialize from String value ('%s')", value);
   }

   public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no int/Int-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no long/Long-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no BigInteger-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no double/Double-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no BigDecimal/double/Double-argument constructor/factory method to deserialize from Number value (%s)", value);
   }

   public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
      return ctxt.handleMissingInstantiator(this.getValueClass(), this, (JsonParser)null, "no boolean/Boolean-argument constructor/factory method to deserialize from boolean value (%s)", value);
   }

   public AnnotatedWithParams getDefaultCreator() {
      return null;
   }

   public AnnotatedWithParams getDelegateCreator() {
      return null;
   }

   public AnnotatedWithParams getArrayDelegateCreator() {
      return null;
   }

   public AnnotatedWithParams getWithArgsCreator() {
      return null;
   }

   public static class Base extends ValueInstantiator implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final Class<?> _valueType;

      public Base(Class<?> type) {
         this._valueType = type;
      }

      public Base(JavaType type) {
         this._valueType = type.getRawClass();
      }

      public String getValueTypeDesc() {
         return this._valueType.getName();
      }

      public Class<?> getValueClass() {
         return this._valueType;
      }
   }

   public static class Delegating extends ValueInstantiator implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final ValueInstantiator _delegate;

      protected Delegating(ValueInstantiator delegate) {
         this._delegate = delegate;
      }

      public ValueInstantiator createContextual(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
         ValueInstantiator d = this._delegate.createContextual(ctxt, beanDesc);
         return d == this._delegate ? this : new Delegating(d);
      }

      protected ValueInstantiator delegate() {
         return this._delegate;
      }

      public Class<?> getValueClass() {
         return this.delegate().getValueClass();
      }

      public String getValueTypeDesc() {
         return this.delegate().getValueTypeDesc();
      }

      public boolean canInstantiate() {
         return this.delegate().canInstantiate();
      }

      public boolean canCreateFromString() {
         return this.delegate().canCreateFromString();
      }

      public boolean canCreateFromInt() {
         return this.delegate().canCreateFromInt();
      }

      public boolean canCreateFromLong() {
         return this.delegate().canCreateFromLong();
      }

      public boolean canCreateFromDouble() {
         return this.delegate().canCreateFromDouble();
      }

      public boolean canCreateFromBoolean() {
         return this.delegate().canCreateFromBoolean();
      }

      public boolean canCreateUsingDefault() {
         return this.delegate().canCreateUsingDefault();
      }

      public boolean canCreateUsingDelegate() {
         return this.delegate().canCreateUsingDelegate();
      }

      public boolean canCreateUsingArrayDelegate() {
         return this.delegate().canCreateUsingArrayDelegate();
      }

      public boolean canCreateFromObjectWith() {
         return this.delegate().canCreateFromObjectWith();
      }

      public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
         return this.delegate().getFromObjectArguments(config);
      }

      public JavaType getDelegateType(DeserializationConfig config) {
         return this.delegate().getDelegateType(config);
      }

      public JavaType getArrayDelegateType(DeserializationConfig config) {
         return this.delegate().getArrayDelegateType(config);
      }

      public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
         return this.delegate().createUsingDefault(ctxt);
      }

      public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
         return this.delegate().createFromObjectWith(ctxt, args);
      }

      public Object createFromObjectWith(DeserializationContext ctxt, SettableBeanProperty[] props, PropertyValueBuffer buffer) throws IOException {
         return this.delegate().createFromObjectWith(ctxt, props, buffer);
      }

      public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
         return this.delegate().createUsingDelegate(ctxt, delegate);
      }

      public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
         return this.delegate().createUsingArrayDelegate(ctxt, delegate);
      }

      public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
         return this.delegate().createFromString(ctxt, value);
      }

      public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
         return this.delegate().createFromInt(ctxt, value);
      }

      public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
         return this.delegate().createFromLong(ctxt, value);
      }

      public Object createFromBigInteger(DeserializationContext ctxt, BigInteger value) throws IOException {
         return this.delegate().createFromBigInteger(ctxt, value);
      }

      public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
         return this.delegate().createFromDouble(ctxt, value);
      }

      public Object createFromBigDecimal(DeserializationContext ctxt, BigDecimal value) throws IOException {
         return this.delegate().createFromBigDecimal(ctxt, value);
      }

      public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
         return this.delegate().createFromBoolean(ctxt, value);
      }

      public AnnotatedWithParams getDefaultCreator() {
         return this.delegate().getDefaultCreator();
      }

      public AnnotatedWithParams getDelegateCreator() {
         return this.delegate().getDelegateCreator();
      }

      public AnnotatedWithParams getArrayDelegateCreator() {
         return this.delegate().getArrayDelegateCreator();
      }

      public AnnotatedWithParams getWithArgsCreator() {
         return this.delegate().getWithArgsCreator();
      }
   }

   public interface Gettable {
      ValueInstantiator getValueInstantiator();
   }
}
