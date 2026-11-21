package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collection;
import java.util.Objects;

public class StdTypeResolverBuilder implements TypeResolverBuilder<StdTypeResolverBuilder> {
   protected JsonTypeInfo.Id _idType;
   protected JsonTypeInfo.As _includeAs;
   protected String _typeProperty;
   protected boolean _typeIdVisible = false;
   protected Boolean _requireTypeIdForSubtypes;
   protected Class<?> _defaultImpl;
   protected TypeIdResolver _customIdResolver;

   public StdTypeResolverBuilder() {
   }

   protected StdTypeResolverBuilder(JsonTypeInfo.Id idType, JsonTypeInfo.As idAs, String propName) {
      this._idType = idType;
      this._includeAs = idAs;
      this._typeProperty = this._propName(propName, idType);
   }

   protected StdTypeResolverBuilder(StdTypeResolverBuilder base, Class<?> defaultImpl) {
      this._idType = base._idType;
      this._includeAs = base._includeAs;
      this._typeProperty = base._typeProperty;
      this._typeIdVisible = base._typeIdVisible;
      this._customIdResolver = base._customIdResolver;
      this._defaultImpl = defaultImpl;
      this._requireTypeIdForSubtypes = base._requireTypeIdForSubtypes;
   }

   public StdTypeResolverBuilder(JsonTypeInfo.Value settings) {
      if (settings != null) {
         this.withSettings(settings);
      }

   }

   public static StdTypeResolverBuilder noTypeInfoBuilder() {
      JsonTypeInfo.Value typeInfo = JsonTypeInfo.Value.construct(JsonTypeInfo.Id.NONE, (JsonTypeInfo.As)null, (String)null, (Class)null, false, (Boolean)null);
      return (new StdTypeResolverBuilder()).withSettings(typeInfo);
   }

   public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
      if (this._idType == JsonTypeInfo.Id.NONE) {
         return null;
      } else if (baseType.isPrimitive() && !this.allowPrimitiveTypes(config, baseType)) {
         return null;
      } else if (this._idType == JsonTypeInfo.Id.DEDUCTION) {
         return AsDeductionTypeSerializer.instance();
      } else {
         TypeIdResolver idRes = this.idResolver(config, baseType, this.subTypeValidator(config), subtypes, true, false);
         switch (this._includeAs) {
            case WRAPPER_ARRAY:
               return new AsArrayTypeSerializer(idRes, (BeanProperty)null);
            case PROPERTY:
               return new AsPropertyTypeSerializer(idRes, (BeanProperty)null, this._typeProperty);
            case WRAPPER_OBJECT:
               return new AsWrapperTypeSerializer(idRes, (BeanProperty)null);
            case EXTERNAL_PROPERTY:
               return new AsExternalTypeSerializer(idRes, (BeanProperty)null, this._typeProperty);
            case EXISTING_PROPERTY:
               return new AsExistingPropertyTypeSerializer(idRes, (BeanProperty)null, this._typeProperty);
            default:
               throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
         }
      }
   }

   public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
      if (this._idType == JsonTypeInfo.Id.NONE) {
         return null;
      } else if (baseType.isPrimitive() && !this.allowPrimitiveTypes(config, baseType)) {
         return null;
      } else {
         PolymorphicTypeValidator subTypeValidator = this.verifyBaseTypeValidity(config, baseType);
         TypeIdResolver idRes = this.idResolver(config, baseType, subTypeValidator, subtypes, false, true);
         JavaType defaultImpl = this.defineDefaultImpl(config, baseType);
         if (this._idType == JsonTypeInfo.Id.DEDUCTION) {
            return new AsDeductionTypeDeserializer(baseType, idRes, defaultImpl, config, subtypes);
         } else {
            switch (this._includeAs) {
               case WRAPPER_ARRAY:
                  return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
               case PROPERTY:
               case EXISTING_PROPERTY:
                  return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs, this._strictTypeIdHandling(config, baseType));
               case WRAPPER_OBJECT:
                  return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
               case EXTERNAL_PROPERTY:
                  return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
               default:
                  throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
            }
         }
      }
   }

   protected JavaType defineDefaultImpl(DeserializationConfig config, JavaType baseType) {
      if (this._defaultImpl != null) {
         if (this._defaultImpl == Void.class || this._defaultImpl == NoClass.class) {
            return config.getTypeFactory().constructType(this._defaultImpl);
         }

         if (baseType.hasRawClass(this._defaultImpl)) {
            return baseType;
         }

         if (baseType.isTypeOrSuperTypeOf(this._defaultImpl)) {
            return config.getTypeFactory().constructSpecializedType(baseType, this._defaultImpl);
         }

         if (baseType.hasRawClass(this._defaultImpl)) {
            return baseType;
         }
      }

      if (config.isEnabled((MapperFeature)MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) && !baseType.isAbstract()) {
         return baseType;
      } else {
         return null;
      }
   }

   public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes) {
      if (idType == null) {
         throw new IllegalArgumentException("idType cannot be null");
      } else {
         this._idType = idType;
         this._customIdResolver = idRes;
         this._typeProperty = this._propName((String)null, idType);
         return this;
      }
   }

   public StdTypeResolverBuilder init(JsonTypeInfo.Value settings, TypeIdResolver idRes) {
      this._customIdResolver = idRes;
      if (settings != null) {
         this.withSettings(settings);
      }

      return this;
   }

   public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs) {
      if (includeAs == null) {
         throw new IllegalArgumentException("includeAs cannot be null");
      } else {
         this._includeAs = includeAs;
         return this;
      }
   }

   public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
      this._typeProperty = this._propName(typeIdPropName, this._idType);
      return this;
   }

   public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
      this._defaultImpl = defaultImpl;
      return this;
   }

   public StdTypeResolverBuilder typeIdVisibility(boolean isVisible) {
      this._typeIdVisible = isVisible;
      return this;
   }

   public StdTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
      if (this._defaultImpl == defaultImpl) {
         return this;
      } else {
         ClassUtil.verifyMustOverride(StdTypeResolverBuilder.class, this, "withDefaultImpl");
         return new StdTypeResolverBuilder(this, defaultImpl);
      }
   }

   public StdTypeResolverBuilder withSettings(JsonTypeInfo.Value settings) {
      this._idType = (JsonTypeInfo.Id)Objects.requireNonNull(settings.getIdType());
      this._includeAs = settings.getInclusionType();
      this._typeProperty = this._propName(settings.getPropertyName(), this._idType);
      this._defaultImpl = settings.getDefaultImpl();
      this._typeIdVisible = settings.getIdVisible();
      this._requireTypeIdForSubtypes = settings.getRequireTypeIdForSubtypes();
      return this;
   }

   protected String _propName(String propName, JsonTypeInfo.Id idType) {
      if (propName == null || propName.isEmpty()) {
         propName = idType.getDefaultPropertyName();
      }

      return propName;
   }

   public Class<?> getDefaultImpl() {
      return this._defaultImpl;
   }

   public String getTypeProperty() {
      return this._typeProperty;
   }

   public boolean isTypeIdVisible() {
      return this._typeIdVisible;
   }

   protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator subtypeValidator, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
      if (this._customIdResolver != null) {
         return this._customIdResolver;
      } else if (this._idType == null) {
         throw new IllegalStateException("Cannot build, 'init()' not yet called");
      } else {
         switch (this._idType) {
            case DEDUCTION:
            case CLASS:
               return ClassNameIdResolver.construct(baseType, config, subtypes, subtypeValidator);
            case MINIMAL_CLASS:
               return MinimalClassNameIdResolver.construct(baseType, config, subtypes, subtypeValidator);
            case SIMPLE_NAME:
               return SimpleNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            case NAME:
               return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            case NONE:
               return null;
            case CUSTOM:
            default:
               throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
         }
      }
   }

   public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config) {
      return config.getPolymorphicTypeValidator();
   }

   protected PolymorphicTypeValidator verifyBaseTypeValidity(MapperConfig<?> config, JavaType baseType) {
      PolymorphicTypeValidator ptv = this.subTypeValidator(config);
      if (this._idType == JsonTypeInfo.Id.CLASS || this._idType == JsonTypeInfo.Id.MINIMAL_CLASS) {
         PolymorphicTypeValidator.Validity validity = ptv.validateBaseType(config, baseType);
         if (validity == PolymorphicTypeValidator.Validity.DENIED) {
            return this.reportInvalidBaseType(config, baseType, ptv);
         }

         if (validity == PolymorphicTypeValidator.Validity.ALLOWED) {
            return LaissezFaireSubTypeValidator.instance;
         }
      }

      return ptv;
   }

   protected PolymorphicTypeValidator reportInvalidBaseType(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator ptv) {
      throw new IllegalArgumentException(String.format("Configured `PolymorphicTypeValidator` (of type %s) denied resolution of all subtypes of base type %s", ClassUtil.classNameOf(ptv), ClassUtil.classNameOf(baseType.getRawClass())));
   }

   protected boolean allowPrimitiveTypes(MapperConfig<?> config, JavaType baseType) {
      return false;
   }

   protected boolean _strictTypeIdHandling(DeserializationConfig config, JavaType baseType) {
      if (this._requireTypeIdForSubtypes != null && baseType.isConcrete()) {
         return this._requireTypeIdForSubtypes;
      } else {
         return config.isEnabled((MapperFeature)MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES) ? true : this._hasTypeResolver(config, baseType);
      }
   }

   protected boolean _hasTypeResolver(DeserializationConfig config, JavaType baseType) {
      AnnotatedClass ac = AnnotatedClassResolver.resolveWithoutSuperTypes(config, baseType.getRawClass());
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      return ai.findPolymorphicTypeInfo(config, ac) != null;
   }
}
