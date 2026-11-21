package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonInject {
   String value() default "";

   OptBoolean useInput() default OptBoolean.DEFAULT;

   OptBoolean optional() default OptBoolean.DEFAULT;

   public static class Value implements JacksonAnnotationValue<JacksonInject>, Serializable {
      private static final long serialVersionUID = 1L;
      protected static final Value EMPTY = new Value((Object)null, (Boolean)null, (Boolean)null);
      protected final Object _id;
      protected final Boolean _useInput;
      protected final Boolean _optional;

      protected Value(Object id, Boolean useInput, Boolean optional) {
         this._id = id;
         this._useInput = useInput;
         this._optional = optional;
      }

      public Class<JacksonInject> valueFor() {
         return JacksonInject.class;
      }

      public static Value empty() {
         return EMPTY;
      }

      /** @deprecated */
      @Deprecated
      public static Value construct(Object id, Boolean useInput) {
         return construct(id, useInput, (Boolean)null);
      }

      public static Value construct(Object id, Boolean useInput, Boolean optional) {
         if ("".equals(id)) {
            id = null;
         }

         return _empty(id, useInput, optional) ? EMPTY : new Value(id, useInput, optional);
      }

      public static Value from(JacksonInject src) {
         return src == null ? EMPTY : construct(src.value(), src.useInput().asBoolean(), src.optional().asBoolean());
      }

      public static Value forId(Object id) {
         return construct(id, (Boolean)null, (Boolean)null);
      }

      public Value withId(Object id) {
         if (id == null) {
            if (this._id == null) {
               return this;
            }
         } else if (id.equals(this._id)) {
            return this;
         }

         return new Value(id, this._useInput, this._optional);
      }

      public Value withUseInput(Boolean useInput) {
         if (useInput == null) {
            if (this._useInput == null) {
               return this;
            }
         } else if (useInput.equals(this._useInput)) {
            return this;
         }

         return new Value(this._id, useInput, this._optional);
      }

      public Value withOptional(Boolean optional) {
         if (optional == null) {
            if (this._optional == null) {
               return this;
            }
         } else if (optional.equals(this._optional)) {
            return this;
         }

         return new Value(this._id, this._useInput, optional);
      }

      public Object getId() {
         return this._id;
      }

      public Boolean getUseInput() {
         return this._useInput;
      }

      public Boolean getOptional() {
         return this._optional;
      }

      public boolean hasId() {
         return this._id != null;
      }

      public boolean willUseInput(boolean defaultSetting) {
         return this._useInput == null ? defaultSetting : this._useInput;
      }

      public String toString() {
         return String.format("JacksonInject.Value(id=%s,useInput=%s,optional=%s)", this._id, this._useInput, this._optional);
      }

      public int hashCode() {
         int h = 1;
         if (this._id != null) {
            h += this._id.hashCode();
         }

         if (this._useInput != null) {
            h += this._useInput.hashCode();
         }

         if (this._optional != null) {
            h += this._optional.hashCode();
         }

         return h;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            Value other = (Value)o;
            return Objects.equals(this._id, other._id) && Objects.equals(this._useInput, other._useInput) && Objects.equals(this._optional, other._optional);
         }
      }

      private static boolean _empty(Object id, Boolean useInput, Boolean optional) {
         return id == null && useInput == null && optional == null;
      }
   }
}
