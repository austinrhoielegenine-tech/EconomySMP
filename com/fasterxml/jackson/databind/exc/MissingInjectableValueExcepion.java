package com.fasterxml.jackson.databind.exc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.Closeable;

public class MissingInjectableValueExcepion extends JsonMappingException {
   private static final long serialVersionUID = 1L;
   protected final Object _valueId;
   protected final BeanProperty _forProperty;
   protected final Object _beanInstance;

   protected MissingInjectableValueExcepion(JsonParser p, String msg, Object valueId, BeanProperty forProperty, Object beanInstance) {
      super((Closeable)p, (String)msg);
      this._valueId = valueId;
      this._forProperty = forProperty;
      this._beanInstance = beanInstance;
   }

   public static MissingInjectableValueExcepion from(JsonParser p, String msg, Object valueId, BeanProperty forProperty, Object beanInstance) {
      return new MissingInjectableValueExcepion(p, msg, valueId, forProperty, beanInstance);
   }

   public Object getValueId() {
      return this._valueId;
   }

   public BeanProperty getForProperty() {
      return this._forProperty;
   }

   public Object getBeanInstance() {
      return this._beanInstance;
   }
}
