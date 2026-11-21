package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAutoDetect {
   Visibility getterVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility isGetterVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility setterVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility creatorVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility scalarConstructorVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   Visibility fieldVisibility() default JsonAutoDetect.Visibility.DEFAULT;

   public static enum Visibility {
      ANY,
      NON_PRIVATE,
      PROTECTED_AND_PUBLIC,
      PUBLIC_ONLY,
      NONE,
      DEFAULT;

      public boolean isVisible(Member m) {
         switch (this) {
            case ANY:
               return true;
            case NONE:
               return false;
            case NON_PRIVATE:
               return !Modifier.isPrivate(m.getModifiers());
            case PROTECTED_AND_PUBLIC:
               if (Modifier.isProtected(m.getModifiers())) {
                  return true;
               }
            case PUBLIC_ONLY:
               return Modifier.isPublic(m.getModifiers());
            default:
               return false;
         }
      }
   }

   public static class Value implements JacksonAnnotationValue<JsonAutoDetect>, Serializable {
      private static final long serialVersionUID = 1L;
      private static final Visibility DEFAULT_FIELD_VISIBILITY;
      protected static final Value DEFAULT;
      protected static final Value NO_OVERRIDES;
      protected final Visibility _fieldVisibility;
      protected final Visibility _getterVisibility;
      protected final Visibility _isGetterVisibility;
      protected final Visibility _setterVisibility;
      protected final Visibility _creatorVisibility;
      protected final Visibility _scalarConstructorVisibility;

      private Value(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators, Visibility scalarCtors) {
         this._fieldVisibility = fields;
         this._getterVisibility = getters;
         this._isGetterVisibility = isGetters;
         this._setterVisibility = setters;
         this._creatorVisibility = creators;
         this._scalarConstructorVisibility = scalarCtors;
      }

      public static Value defaultVisibility() {
         return DEFAULT;
      }

      public static Value noOverrides() {
         return NO_OVERRIDES;
      }

      public static Value from(JsonAutoDetect src) {
         return construct(src.fieldVisibility(), src.getterVisibility(), src.isGetterVisibility(), src.setterVisibility(), src.creatorVisibility(), src.scalarConstructorVisibility());
      }

      public static Value construct(PropertyAccessor acc, Visibility visibility) {
         Visibility fields = JsonAutoDetect.Visibility.DEFAULT;
         Visibility getters = JsonAutoDetect.Visibility.DEFAULT;
         Visibility isGetters = JsonAutoDetect.Visibility.DEFAULT;
         Visibility setters = JsonAutoDetect.Visibility.DEFAULT;
         Visibility creators = JsonAutoDetect.Visibility.DEFAULT;
         Visibility scalarCtors = JsonAutoDetect.Visibility.DEFAULT;
         switch (acc) {
            case FIELD:
               fields = visibility;
               break;
            case GETTER:
               getters = visibility;
               break;
            case IS_GETTER:
               isGetters = visibility;
               break;
            case SETTER:
               setters = visibility;
               break;
            case CREATOR:
               creators = visibility;
               break;
            case SCALAR_CONSTRUCTOR:
               scalarCtors = visibility;
            case NONE:
            default:
               break;
            case ALL:
               scalarCtors = visibility;
               creators = visibility;
               setters = visibility;
               isGetters = visibility;
               getters = visibility;
               fields = visibility;
         }

         return construct(fields, getters, isGetters, setters, creators, scalarCtors);
      }

      /** @deprecated */
      @Deprecated
      public static Value construct(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators) {
         return construct(fields, getters, isGetters, setters, creators, JsonAutoDetect.Visibility.DEFAULT);
      }

      public static Value construct(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators, Visibility scalarCtors) {
         Value v = _predefined(fields, getters, isGetters, setters, creators, scalarCtors);
         if (v == null) {
            v = new Value(fields, getters, isGetters, setters, creators, scalarCtors);
         }

         return v;
      }

      public Value withFieldVisibility(Visibility v) {
         return construct(v, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility, this._scalarConstructorVisibility);
      }

      public Value withGetterVisibility(Visibility v) {
         return construct(this._fieldVisibility, v, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility, this._scalarConstructorVisibility);
      }

      public Value withIsGetterVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, v, this._setterVisibility, this._creatorVisibility, this._scalarConstructorVisibility);
      }

      public Value withSetterVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, v, this._creatorVisibility, this._scalarConstructorVisibility);
      }

      public Value withCreatorVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, v, this._scalarConstructorVisibility);
      }

      public Value withScalarConstructorVisibility(Visibility v) {
         return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility, v);
      }

      public static Value merge(Value base, Value overrides) {
         return base == null ? overrides : base.withOverrides(overrides);
      }

      public Value withOverrides(Value overrides) {
         if (overrides != null && overrides != NO_OVERRIDES && overrides != this) {
            Visibility fields = _override(this._fieldVisibility, overrides._fieldVisibility);
            Visibility getters = _override(this._getterVisibility, overrides._getterVisibility);
            Visibility isGetters = _override(this._isGetterVisibility, overrides._isGetterVisibility);
            Visibility setters = _override(this._setterVisibility, overrides._setterVisibility);
            Visibility creators = _override(this._creatorVisibility, overrides._creatorVisibility);
            Visibility scalarCtors = _override(this._scalarConstructorVisibility, overrides._scalarConstructorVisibility);
            return _equals(this, fields, getters, isGetters, setters, creators, scalarCtors) ? this : construct(fields, getters, isGetters, setters, creators, scalarCtors);
         } else {
            return this;
         }
      }

      private static Visibility _override(Visibility base, Visibility override) {
         return override == JsonAutoDetect.Visibility.DEFAULT ? base : override;
      }

      public Class<JsonAutoDetect> valueFor() {
         return JsonAutoDetect.class;
      }

      public Visibility getFieldVisibility() {
         return this._fieldVisibility;
      }

      public Visibility getGetterVisibility() {
         return this._getterVisibility;
      }

      public Visibility getIsGetterVisibility() {
         return this._isGetterVisibility;
      }

      public Visibility getSetterVisibility() {
         return this._setterVisibility;
      }

      public Visibility getCreatorVisibility() {
         return this._creatorVisibility;
      }

      public Visibility getScalarConstructorVisibility() {
         return this._scalarConstructorVisibility;
      }

      protected Object readResolve() {
         Value v = _predefined(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility, this._scalarConstructorVisibility);
         return v == null ? this : v;
      }

      public String toString() {
         return String.format("JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s,scalarConstructors=%s)", this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility, this._scalarConstructorVisibility);
      }

      public int hashCode() {
         return 1 + this._fieldVisibility.ordinal() ^ 3 * this._getterVisibility.ordinal() - 7 * this._isGetterVisibility.ordinal() + 11 * this._setterVisibility.ordinal() ^ 13 * this._creatorVisibility.ordinal() + 17 * this._scalarConstructorVisibility.ordinal();
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            Value v = (Value)o;
            return _equals(this, v._fieldVisibility, v._getterVisibility, v._isGetterVisibility, v._setterVisibility, v._creatorVisibility, v._scalarConstructorVisibility);
         }
      }

      private static Value _predefined(Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators, Visibility scalarCtors) {
         if (fields == DEFAULT_FIELD_VISIBILITY) {
            if (getters == DEFAULT._getterVisibility && isGetters == DEFAULT._isGetterVisibility && setters == DEFAULT._setterVisibility && creators == DEFAULT._creatorVisibility && scalarCtors == DEFAULT._scalarConstructorVisibility) {
               return DEFAULT;
            }
         } else if (fields == JsonAutoDetect.Visibility.DEFAULT && getters == JsonAutoDetect.Visibility.DEFAULT && isGetters == JsonAutoDetect.Visibility.DEFAULT && setters == JsonAutoDetect.Visibility.DEFAULT && creators == JsonAutoDetect.Visibility.DEFAULT && scalarCtors == JsonAutoDetect.Visibility.DEFAULT) {
            return NO_OVERRIDES;
         }

         return null;
      }

      private static boolean _equals(Value v, Visibility fields, Visibility getters, Visibility isGetters, Visibility setters, Visibility creators, Visibility scalarCtors) {
         return v._fieldVisibility == fields && v._getterVisibility == getters && v._isGetterVisibility == isGetters && v._setterVisibility == setters && v._creatorVisibility == creators && v._scalarConstructorVisibility == scalarCtors;
      }

      static {
         DEFAULT_FIELD_VISIBILITY = JsonAutoDetect.Visibility.PUBLIC_ONLY;
         DEFAULT = new Value(DEFAULT_FIELD_VISIBILITY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.NON_PRIVATE);
         NO_OVERRIDES = new Value(JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT);
      }
   }
}
