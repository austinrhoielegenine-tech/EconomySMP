package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.reflect.Constructor;

public final class InnerClassProperty extends SettableBeanProperty.Delegating {
   private static final long serialVersionUID = 1L;
   protected final transient Constructor<?> _creator;
   protected AnnotatedConstructor _annotated;

   public InnerClassProperty(SettableBeanProperty delegate, Constructor<?> ctor) {
      super(delegate);
      this._creator = ctor;
   }

   protected InnerClassProperty(SettableBeanProperty src, AnnotatedConstructor ann) {
      super(src);
      this._annotated = ann;
      this._creator = this._annotated == null ? null : this._annotated.getAnnotated();
      if (this._creator == null) {
         throw new IllegalArgumentException("Missing constructor (broken JDK (de)serialization?)");
      }
   }

   protected SettableBeanProperty withDelegate(SettableBeanProperty d) {
      return d == this.delegate ? this : new InnerClassProperty(d, this._creator);
   }

   public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
      JsonToken t = p.currentToken();
      Object value;
      if (t == JsonToken.VALUE_NULL) {
         value = this._valueDeserializer.getNullValue(ctxt);
      } else if (this._valueTypeDeserializer != null) {
         value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
      } else {
         try {
            value = this._creator.newInstance(bean);
         } catch (Exception e) {
            ClassUtil.unwrapAndThrowAsIAE(e, String.format("Failed to instantiate class %s, problem: %s", this._creator.getDeclaringClass().getName(), e.getMessage()));
            value = null;
         }

         this._valueDeserializer.deserialize(p, ctxt, value);
      }

      this.set(bean, value);
   }

   public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
      return this.setAndReturn(instance, this.deserialize(p, ctxt));
   }

   Object readResolve() {
      return new InnerClassProperty(this, this._annotated);
   }

   Object writeReplace() {
      return this._annotated == null ? new InnerClassProperty(this, new AnnotatedConstructor((TypeResolutionContext)null, this._creator, (AnnotationMap)null, (AnnotationMap[])null)) : this;
   }
}
