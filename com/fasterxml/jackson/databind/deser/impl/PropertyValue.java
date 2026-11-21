package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

public abstract class PropertyValue {
   public final PropertyValue next;
   public final Object value;

   protected PropertyValue(PropertyValue next, Object value) {
      this.next = next;
      this.value = value;
   }

   /** @deprecated */
   @Deprecated
   public final void assign(Object bean) throws IOException {
      this.assign((DeserializationContext)null, bean);
   }

   public void assign(DeserializationContext ctxt, Object bean) throws IOException {
      throw new UnsupportedOperationException();
   }

   public void setValue(Object parameterObject) throws IOException {
      throw new UnsupportedOperationException("Should not be called on type: " + this.getClass().getName());
   }

   static final class Regular extends PropertyValue {
      final SettableBeanProperty _property;

      public Regular(PropertyValue next, Object value, SettableBeanProperty prop) {
         super(next, value);
         this._property = prop;
      }

      public void assign(DeserializationContext ctxt, Object bean) throws IOException {
         this._property.set(bean, this.value);
      }
   }

   static final class Any extends PropertyValue {
      final SettableAnyProperty _property;
      final String _propertyName;

      public Any(PropertyValue next, Object value, SettableAnyProperty prop, String propName) {
         super(next, value);
         this._property = prop;
         this._propertyName = propName;
      }

      public void assign(DeserializationContext ctxt, Object bean) throws IOException {
         this._property.set(bean, this._propertyName, this.value);
      }
   }

   static final class Map extends PropertyValue {
      final Object _key;

      public Map(PropertyValue next, Object value, Object key) {
         super(next, value);
         this._key = key;
      }

      public void assign(DeserializationContext ctxt, Object bean) throws IOException {
         ((java.util.Map)bean).put(this._key, this.value);
      }
   }

   static final class AnyParameter extends PropertyValue {
      final SettableAnyProperty _property;
      final String _propertyName;

      public AnyParameter(PropertyValue next, Object value, SettableAnyProperty prop, String propName) {
         super(next, value);
         this._property = prop;
         this._propertyName = propName;
      }

      public void assign(DeserializationContext ctxt, Object bean) throws IOException {
      }

      public void setValue(Object parameterObject) throws IOException {
         this._property.set(parameterObject, this._propertyName, this.value);
      }
   }

   static final class Merging extends PropertyValue {
      final SettableBeanProperty _property;

      public Merging(PropertyValue next, TokenBuffer buffered, SettableBeanProperty prop) {
         super(next, buffered);
         this._property = prop;
      }

      public void assign(DeserializationContext ctxt, Object bean) throws IOException {
         TokenBuffer buffered = (TokenBuffer)this.value;
         JsonParser p = buffered.asParser();
         Throwable var5 = null;

         try {
            p.nextToken();
            this._property.deserializeAndSet(p, ctxt, bean);
         } catch (Throwable var14) {
            var5 = var14;
            throw var14;
         } finally {
            if (p != null) {
               if (var5 != null) {
                  try {
                     p.close();
                  } catch (Throwable var13) {
                     var5.addSuppressed(var13);
                  }
               } else {
                  p.close();
               }
            }

         }

      }
   }
}
