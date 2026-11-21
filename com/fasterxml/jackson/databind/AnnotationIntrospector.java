package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.PotentialCreator;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AnnotationIntrospector implements Versioned, Serializable {
   public static AnnotationIntrospector nopInstance() {
      return NopAnnotationIntrospector.instance;
   }

   public static AnnotationIntrospector pair(AnnotationIntrospector a1, AnnotationIntrospector a2) {
      return new AnnotationIntrospectorPair(a1, a2);
   }

   public Collection<AnnotationIntrospector> allIntrospectors() {
      return Collections.singletonList(this);
   }

   public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
      result.add(this);
      return result;
   }

   public abstract Version version();

   public boolean isAnnotationBundle(Annotation ann) {
      return false;
   }

   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
      return null;
   }

   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
      return objectIdInfo;
   }

   public PropertyName findRootName(AnnotatedClass ac) {
      return null;
   }

   public Boolean isIgnorableType(AnnotatedClass ac) {
      return null;
   }

   public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated ann) {
      return this.findPropertyIgnorals(ann);
   }

   public JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated ann) {
      return JsonIncludeProperties.Value.all();
   }

   public Object findFilterId(Annotated ann) {
      return null;
   }

   public Object findNamingStrategy(AnnotatedClass ac) {
      return null;
   }

   public Object findEnumNamingStrategy(MapperConfig<?> config, AnnotatedClass ac) {
      return null;
   }

   public String findClassDescription(AnnotatedClass ac) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated ac) {
      return JsonIgnoreProperties.Value.empty();
   }

   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
      return checker;
   }

   public JsonTypeInfo.Value findPolymorphicTypeInfo(MapperConfig<?> config, Annotated ann) {
      return null;
   }

   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
      return null;
   }

   public List<NamedType> findSubtypes(Annotated a) {
      return null;
   }

   public String findTypeName(AnnotatedClass ac) {
      return null;
   }

   public Boolean isTypeId(AnnotatedMember am) {
      return null;
   }

   public ReferenceProperty findReferenceType(AnnotatedMember member) {
      return null;
   }

   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
      return null;
   }

   public boolean hasIgnoreMarker(AnnotatedMember m) {
      return false;
   }

   public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
      Object id = this.findInjectableValueId(m);
      return id != null ? JacksonInject.Value.forId(id) : null;
   }

   public Boolean hasRequiredMarker(AnnotatedMember m) {
      return null;
   }

   public Class<?>[] findViews(Annotated a) {
      return null;
   }

   public JsonFormat.Value findFormat(Annotated memberOrClass) {
      return JsonFormat.Value.empty();
   }

   public PropertyName findWrapperName(Annotated ann) {
      return null;
   }

   public String findPropertyDefaultValue(Annotated ann) {
      return null;
   }

   public String findPropertyDescription(Annotated ann) {
      return null;
   }

   public Integer findPropertyIndex(Annotated ann) {
      return null;
   }

   public String findImplicitPropertyName(AnnotatedMember member) {
      return null;
   }

   public List<PropertyName> findPropertyAliases(Annotated ann) {
      return null;
   }

   public JsonProperty.Access findPropertyAccess(Annotated ann) {
      return null;
   }

   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
      return null;
   }

   public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public Object findInjectableValueId(AnnotatedMember m) {
      return null;
   }

   public Object findSerializer(Annotated am) {
      return null;
   }

   public Object findKeySerializer(Annotated am) {
      return null;
   }

   public Object findContentSerializer(Annotated am) {
      return null;
   }

   public Object findNullSerializer(Annotated am) {
      return null;
   }

   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
      return null;
   }

   public Object findSerializationConverter(Annotated a) {
      return null;
   }

   public Object findSerializationContentConverter(AnnotatedMember a) {
      return null;
   }

   public JsonInclude.Value findPropertyInclusion(Annotated a) {
      return JsonInclude.Value.empty();
   }

   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      return baseType;
   }

   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
      return null;
   }

   public Boolean findSerializationSortAlphabetically(Annotated ann) {
      return null;
   }

   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
   }

   public PropertyName findNameForSerialization(Annotated a) {
      return null;
   }

   public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
      return null;
   }

   public Boolean hasAsValue(Annotated a) {
      return a instanceof AnnotatedMethod && this.hasAsValueAnnotation((AnnotatedMethod)a) ? true : null;
   }

   public Boolean hasAnyGetter(Annotated ann) {
      return ann instanceof AnnotatedMethod && this.hasAnyGetterAnnotation((AnnotatedMethod)ann) ? true : null;
   }

   /** @deprecated */
   @Deprecated
   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
      return names;
   }

   public String[] findEnumValues(MapperConfig<?> config, AnnotatedClass annotatedClass, Enum<?>[] enumValues, String[] names) {
      return names;
   }

   /** @deprecated */
   @Deprecated
   public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliases) {
   }

   public void findEnumAliases(MapperConfig<?> config, AnnotatedClass annotatedClass, Enum<?>[] enumValues, String[][] aliases) {
   }

   public Enum<?> findDefaultEnumValue(AnnotatedClass ac, Enum<?>[] enumValues) {
      Class<Enum<?>> enumCls = ac.getRawType();
      return this.findDefaultEnumValue(enumCls);
   }

   /** @deprecated */
   @Deprecated
   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public String findEnumValue(Enum<?> value) {
      return value.name();
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
      return false;
   }

   public Object findDeserializer(Annotated am) {
      return null;
   }

   public Object findKeyDeserializer(Annotated am) {
      return null;
   }

   public Object findContentDeserializer(Annotated am) {
      return null;
   }

   public Object findDeserializationConverter(Annotated a) {
      return null;
   }

   public Object findDeserializationContentConverter(AnnotatedMember a) {
      return null;
   }

   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      return baseType;
   }

   public Object findValueInstantiator(AnnotatedClass ac) {
      return null;
   }

   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
      return null;
   }

   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
      return null;
   }

   public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated ann) {
      return null;
   }

   public PotentialCreator findDefaultCreator(MapperConfig<?> config, AnnotatedClass valueClass, List<PotentialCreator> declaredConstructors, List<PotentialCreator> declaredFactories) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasCreatorAnnotation(Annotated ann) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public JsonCreator.Mode findCreatorBinding(Annotated ann) {
      return null;
   }

   public PropertyName findNameForDeserialization(Annotated ann) {
      return null;
   }

   public Boolean hasAnySetter(Annotated ann) {
      return null;
   }

   public JsonSetter.Value findSetterInfo(Annotated ann) {
      return JsonSetter.Value.empty();
   }

   public Boolean findMergeInfo(Annotated ann) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
      return false;
   }

   protected <A extends Annotation> A _findAnnotation(Annotated ann, Class<A> annoClass) {
      return (A)ann.getAnnotation(annoClass);
   }

   protected boolean _hasAnnotation(Annotated ann, Class<? extends Annotation> annoClass) {
      return ann.hasAnnotation(annoClass);
   }

   protected boolean _hasOneOf(Annotated ann, Class<? extends Annotation>[] annoClasses) {
      return ann.hasOneOf(annoClasses);
   }

   public static class ReferenceProperty {
      private final Type _type;
      private final String _name;

      public ReferenceProperty(Type t, String n) {
         this._type = t;
         this._name = n;
      }

      public static ReferenceProperty managed(String name) {
         return new ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE, name);
      }

      public static ReferenceProperty back(String name) {
         return new ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, name);
      }

      public Type getType() {
         return this._type;
      }

      public String getName() {
         return this._name;
      }

      public boolean isManagedReference() {
         return this._type == AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE;
      }

      public boolean isBackReference() {
         return this._type == AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE;
      }

      public static enum Type {
         MANAGED_REFERENCE,
         BACK_REFERENCE;
      }
   }

   public interface XmlExtensions {
      String findNamespace(MapperConfig<?> var1, Annotated var2);

      Boolean isOutputAsAttribute(MapperConfig<?> var1, Annotated var2);

      Boolean isOutputAsText(MapperConfig<?> var1, Annotated var2);

      Boolean isOutputAsCData(MapperConfig<?> var1, Annotated var2);
   }
}
