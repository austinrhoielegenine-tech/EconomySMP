package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Arrays;

public class ThrowableDeserializer extends BeanDeserializer {
   private static final long serialVersionUID = 1L;
   protected static final String PROP_NAME_MESSAGE = "message";
   protected static final String PROP_NAME_SUPPRESSED = "suppressed";
   protected static final String PROP_NAME_LOCALIZED_MESSAGE = "localizedMessage";
   protected static final String PROP_NAME_CAUSE = "cause";
   protected static final String PROP_NAME_STACK_TRACE = "stackTrace";

   protected ThrowableDeserializer(BeanDeserializer src, NameTransformer unwrapper) {
      super(src, (NameTransformer)unwrapper);
      this._vanillaProcessing = false;
   }

   /** @deprecated */
   @Deprecated
   public ThrowableDeserializer(BeanDeserializer baseDeserializer) {
      this(baseDeserializer, (NameTransformer)null);
   }

   public static ThrowableDeserializer construct(DeserializationContext ctxt, BeanDeserializer baseDeserializer) {
      return new ThrowableDeserializer(baseDeserializer, (NameTransformer)null);
   }

   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
      return this.getClass() != ThrowableDeserializer.class ? this : new ThrowableDeserializer(this, unwrapper);
   }

   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._propertyBasedCreator != null) {
         return this._deserializeUsingPropertyBased(p, ctxt);
      } else if (this._delegateDeserializer != null) {
         return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
      } else if (this._beanType.isAbstract()) {
         return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "abstract type (need to add/enable type information?)");
      } else {
         boolean hasStringCreator = this._valueInstantiator.canCreateFromString();
         boolean hasDefaultCtor = this._valueInstantiator.canCreateUsingDefault();
         if (!hasStringCreator && !hasDefaultCtor) {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "Throwable needs a default constructor, a single-String-arg constructor; or explicit @JsonCreator");
         } else {
            Throwable throwable = null;
            Object[] pending = null;
            Throwable[] suppressed = null;

            int pendingIx;
            for(pendingIx = 0; !p.hasToken(JsonToken.END_OBJECT); p.nextToken()) {
               String propName = p.currentName();
               SettableBeanProperty prop = this._beanProperties.find(propName);
               p.nextToken();
               if (prop != null) {
                  if (!p.hasToken(JsonToken.VALUE_NULL) || !this._shouldSkipNullValue(prop.getName())) {
                     if (throwable != null) {
                        prop.deserializeAndSet(p, ctxt, throwable);
                     } else {
                        if (pending == null) {
                           int len = this._beanProperties.size();
                           pending = new Object[len + len];
                        } else if (pendingIx == pending.length) {
                           pending = Arrays.copyOf(pending, pendingIx + 16);
                        }

                        pending[pendingIx++] = prop;
                        pending[pendingIx++] = prop.deserialize(p, ctxt);
                     }
                  }
               } else if ("message".equalsIgnoreCase(propName)) {
                  throwable = this._instantiate(ctxt, hasStringCreator, p.getValueAsString());
               } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                  p.skipChildren();
               } else if ("suppressed".equalsIgnoreCase(propName)) {
                  if (p.hasToken(JsonToken.VALUE_NULL)) {
                     suppressed = null;
                  } else {
                     JsonDeserializer<Object> deser = ctxt.findRootValueDeserializer(ctxt.constructType(Throwable[].class));
                     suppressed = (Throwable[])deser.deserialize(p, ctxt);
                  }
               } else if ("localizedMessage".equalsIgnoreCase(propName)) {
                  p.skipChildren();
               } else if (this._anySetter != null) {
                  if (throwable == null) {
                     throwable = this._instantiate(ctxt, hasStringCreator, (String)null);
                  }

                  this._anySetter.deserializeAndSet(p, ctxt, throwable, propName);
               } else if ("message".equalsIgnoreCase(propName)) {
                  p.skipChildren();
               } else {
                  this.handleUnknownProperty(p, ctxt, throwable, propName);
               }
            }

            if (throwable == null) {
               throwable = this._instantiate(ctxt, hasStringCreator, (String)null);
            }

            if (pending != null) {
               int i = 0;

               for(int len = pendingIx; i < len; i += 2) {
                  SettableBeanProperty prop = (SettableBeanProperty)pending[i];
                  Object value = pending[i + 1];
                  if (value != null || !this._shouldSkipNullValue(prop.getName())) {
                     prop.set(throwable, value);
                  }
               }
            }

            if (suppressed != null) {
               for(Throwable s : suppressed) {
                  if (s != null) {
                     throwable.addSuppressed(s);
                  }
               }
            }

            return throwable;
         }
      }
   }

   private boolean _shouldSkipNullValue(String propertyName) {
      return "cause".equals(propertyName) || "stackTrace".equals(propertyName);
   }

   private Throwable _instantiate(DeserializationContext ctxt, boolean hasStringCreator, String valueAsString) throws IOException {
      if (hasStringCreator) {
         return valueAsString != null ? (Throwable)this._valueInstantiator.createFromString(ctxt, valueAsString) : (Throwable)this._valueInstantiator.createFromString(ctxt, (String)null);
      } else {
         return (Throwable)this._valueInstantiator.createUsingDefault(ctxt);
      }
   }
}
