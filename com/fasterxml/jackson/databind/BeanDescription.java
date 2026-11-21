package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedAndMetadata;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.PotentialCreators;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.Converter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BeanDescription {
   protected final JavaType _type;

   protected BeanDescription(JavaType type) {
      this._type = type;
   }

   public JavaType getType() {
      return this._type;
   }

   public Class<?> getBeanClass() {
      return this._type.getRawClass();
   }

   public boolean isRecordType() {
      return this._type.isRecordType();
   }

   public boolean isNonStaticInnerClass() {
      return this.getClassInfo().isNonStaticInnerClass();
   }

   public abstract AnnotatedClass getClassInfo();

   public abstract ObjectIdInfo getObjectIdInfo();

   public abstract boolean hasKnownClassAnnotations();

   public abstract Annotations getClassAnnotations();

   public abstract List<BeanPropertyDefinition> findProperties();

   public abstract Set<String> getIgnoredPropertyNames();

   public abstract List<BeanPropertyDefinition> findBackReferences();

   public abstract List<AnnotatedConstructor> getConstructors();

   public abstract List<AnnotatedAndMetadata<AnnotatedConstructor, JsonCreator.Mode>> getConstructorsWithMode();

   public abstract List<AnnotatedMethod> getFactoryMethods();

   public abstract List<AnnotatedAndMetadata<AnnotatedMethod, JsonCreator.Mode>> getFactoryMethodsWithMode();

   public abstract AnnotatedConstructor findDefaultConstructor();

   public abstract PotentialCreators getPotentialCreators();

   public AnnotatedMember findJsonKeyAccessor() {
      return null;
   }

   public abstract AnnotatedMember findJsonValueAccessor();

   public abstract AnnotatedMember findAnyGetter();

   public abstract AnnotatedMember findAnySetterAccessor();

   public abstract AnnotatedMethod findMethod(String var1, Class<?>[] var2);

   public abstract JsonInclude.Value findPropertyInclusion(JsonInclude.Value var1);

   public abstract JsonFormat.Value findExpectedFormat();

   /** @deprecated */
   @Deprecated
   public JsonFormat.Value findExpectedFormat(JsonFormat.Value defValue) {
      JsonFormat.Value v = this.findExpectedFormat();
      if (defValue == null) {
         return v;
      } else {
         return v == null ? defValue : defValue.withOverrides(v);
      }
   }

   public abstract Converter<Object, Object> findSerializationConverter();

   public abstract Converter<Object, Object> findDeserializationConverter();

   public String findClassDescription() {
      return null;
   }

   public abstract Map<Object, AnnotatedMember> findInjectables();

   public abstract Class<?> findPOJOBuilder();

   public abstract JsonPOJOBuilder.Value findPOJOBuilderConfig();

   public abstract Object instantiateBean(boolean var1);

   public abstract Class<?>[] findDefaultViews();
}
