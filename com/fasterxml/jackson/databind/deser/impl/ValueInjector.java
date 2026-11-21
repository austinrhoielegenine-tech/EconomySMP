package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.IOException;

public class ValueInjector extends BeanProperty.Std {
   private static final long serialVersionUID = 1L;
   protected final Object _valueId;
   protected final Boolean _optional;
   protected final Boolean _useInput;

   public ValueInjector(PropertyName propName, JavaType type, AnnotatedMember mutator, Object valueId, Boolean optional, Boolean useInput) {
      super(propName, type, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      this._valueId = valueId;
      this._optional = optional;
      this._useInput = useInput;
   }

   /** @deprecated */
   @Deprecated
   public ValueInjector(PropertyName propName, JavaType type, AnnotatedMember mutator, Object valueId) {
      this(propName, type, mutator, valueId, (Boolean)null, (Boolean)null);
   }

   public Object findValue(DeserializationContext context, Object beanInstance) throws JsonMappingException {
      return context.findInjectableValue(this._valueId, this, beanInstance, this._optional, this._useInput);
   }

   public void inject(DeserializationContext context, Object beanInstance) throws IOException {
      Object value = this.findValue(context, beanInstance);
      if (value == null) {
         if (Boolean.FALSE.equals(this._optional)) {
            throw context.missingInjectableValueException(String.format("No injectable value with id '%s' found (for property '%s')", this._valueId, this.getName()), this._valueId, (BeanProperty)null, beanInstance);
         }
      } else if (!Boolean.TRUE.equals(this._useInput)) {
         this._member.setValue(beanInstance, value);
      }

   }
}
