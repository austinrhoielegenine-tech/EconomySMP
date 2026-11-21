package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class InjectableValues {
   public static InjectableValues empty() {
      return InjectableValues.Empty.INSTANCE;
   }

   public Object findInjectableValue(DeserializationContext ctxt, Object valueId, BeanProperty forProperty, Object beanInstance, Boolean optional, Boolean useInput) throws JsonMappingException {
      return this.findInjectableValue(valueId, ctxt, forProperty, beanInstance);
   }

   /** @deprecated */
   @Deprecated
   public abstract Object findInjectableValue(Object var1, DeserializationContext var2, BeanProperty var3, Object var4) throws JsonMappingException;

   public abstract static class Base extends InjectableValues implements Serializable {
      private static final long serialVersionUID = 1L;

      protected String _validateKey(DeserializationContext ctxt, Object valueId, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
         if (!(valueId instanceof String)) {
            throw ctxt.missingInjectableValueException(String.format("Unsupported injectable value id type (%s), expecting String", ClassUtil.classNameOf(valueId)), valueId, forProperty, beanInstance);
         } else {
            return (String)valueId;
         }
      }

      protected Object _handleMissingValue(DeserializationContext ctxt, String key, BeanProperty forProperty, Object beanInstance, Boolean optionalConfig, Boolean useInputConfig) throws JsonMappingException {
         boolean optional = Boolean.TRUE.equals(optionalConfig);
         boolean useInput = Boolean.TRUE.equals(useInputConfig);
         if (!optional && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE) && (!useInput || beanInstance == null)) {
            throw ctxt.missingInjectableValueException(String.format("No injectable value with id '%s' found (for property '%s')", key, forProperty.getName()), key, forProperty, beanInstance);
         } else {
            return null;
         }
      }

      /** @deprecated */
      @Deprecated
      public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
         return this.findInjectableValue(ctxt, valueId, forProperty, beanInstance, (Boolean)null, (Boolean)null);
      }
   }

   private static final class Empty extends Base implements Serializable {
      private static final long serialVersionUID = 1L;
      static final Empty INSTANCE = new Empty();

      public Object findInjectableValue(DeserializationContext ctxt, Object valueId, BeanProperty forProperty, Object beanInstance, Boolean optional, Boolean useInput) throws JsonMappingException {
         String key = this._validateKey(ctxt, valueId, forProperty, beanInstance);
         return this._handleMissingValue(ctxt, key, forProperty, beanInstance, optional, useInput);
      }
   }

   public static class Std extends Base implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final Map<String, Object> _values;

      public Std() {
         this(new HashMap());
      }

      public Std(Map<String, Object> values) {
         this._values = values;
      }

      public Std addValue(String key, Object value) {
         this._values.put(key, value);
         return this;
      }

      public Std addValue(Class<?> classKey, Object value) {
         this._values.put(classKey.getName(), value);
         return this;
      }

      public Object findInjectableValue(DeserializationContext ctxt, Object valueId, BeanProperty forProperty, Object beanInstance, Boolean optional, Boolean useInput) throws JsonMappingException {
         String key = this._validateKey(ctxt, valueId, forProperty, beanInstance);
         Object ob = this._values.get(key);
         return ob == null && !this._values.containsKey(key) ? this._handleMissingValue(ctxt, key, forProperty, beanInstance, optional, useInput) : ob;
      }
   }
}
