package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
import com.fasterxml.jackson.databind.deser.impl.MergingSettableBeanProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ClassKey;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.NativeImageUtil;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, ValueInstantiator.Gettable, Serializable {
   private static final long serialVersionUID = 1L;
   protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
   protected final JavaType _beanType;
   protected final JsonFormat.Shape _serializationShape;
   protected final ValueInstantiator _valueInstantiator;
   protected JsonDeserializer<Object> _delegateDeserializer;
   protected JsonDeserializer<Object> _arrayDelegateDeserializer;
   protected PropertyBasedCreator _propertyBasedCreator;
   protected boolean _nonStandardCreation;
   protected boolean _vanillaProcessing;
   protected final BeanPropertyMap _beanProperties;
   protected final ValueInjector[] _injectables;
   protected SettableAnyProperty _anySetter;
   protected final Set<String> _ignorableProps;
   protected final Set<String> _includableProps;
   protected final boolean _ignoreAllUnknown;
   protected final boolean _needViewProcesing;
   protected final Map<String, SettableBeanProperty> _backRefs;
   protected transient ConcurrentHashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
   protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
   protected ExternalTypeHandler _externalTypeIdHandler;
   protected final ObjectIdReader _objectIdReader;

   protected BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, Set<String> includableProps, boolean hasViews) {
      super(beanDesc.getType());
      this._beanType = beanDesc.getType();
      this._valueInstantiator = builder.getValueInstantiator();
      this._delegateDeserializer = null;
      this._arrayDelegateDeserializer = null;
      this._propertyBasedCreator = null;
      this._beanProperties = properties;
      this._backRefs = backRefs;
      this._ignorableProps = ignorableProps;
      this._ignoreAllUnknown = ignoreAllUnknown;
      this._includableProps = includableProps;
      this._anySetter = builder.getAnySetter();
      List<ValueInjector> injectables = builder.getInjectables();
      this._injectables = injectables != null && !injectables.isEmpty() ? (ValueInjector[])injectables.toArray(new ValueInjector[injectables.size()]) : null;
      this._objectIdReader = builder.getObjectIdReader();
      this._nonStandardCreation = this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault();
      JsonFormat.Value format = beanDesc.findExpectedFormat();
      this._serializationShape = format.getShape();
      this._needViewProcesing = hasViews;
      this._vanillaProcessing = !this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null;
   }

   protected BeanDeserializerBase(BeanDeserializerBase src) {
      this(src, src._ignoreAllUnknown);
   }

   protected BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._beanProperties = src._beanProperties;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = ignoreAllUnknown;
      this._includableProps = src._includableProps;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._objectIdReader = src._objectIdReader;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = src._vanillaProcessing;
      this._externalTypeIdHandler = src._externalTypeIdHandler;
   }

   protected BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = unwrapper != null || src._ignoreAllUnknown;
      this._includableProps = src._includableProps;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._objectIdReader = src._objectIdReader;
      this._nonStandardCreation = src._nonStandardCreation;
      UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
      PropertyBasedCreator pbc = src._propertyBasedCreator;
      if (unwrapper != null) {
         if (uph != null) {
            uph = uph.renameAll(unwrapper);
         }

         if (pbc != null) {
            pbc = pbc.renameAll(unwrapper);
         }

         this._beanProperties = src._beanProperties.renameAll(unwrapper);
      } else {
         this._beanProperties = src._beanProperties;
      }

      this._propertyBasedCreator = pbc;
      this._unwrappedPropertyHandler = uph;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = false;
      this._externalTypeIdHandler = src._externalTypeIdHandler;
   }

   public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._includableProps = src._includableProps;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._objectIdReader = oir;
      if (oir == null) {
         this._beanProperties = src._beanProperties;
         this._vanillaProcessing = src._vanillaProcessing;
      } else {
         ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
         this._beanProperties = src._beanProperties.withProperty(idProp);
         this._vanillaProcessing = false;
      }

      this._externalTypeIdHandler = src._externalTypeIdHandler;
   }

   public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps, Set<String> includableProps) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._backRefs = src._backRefs;
      this._ignorableProps = ignorableProps;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._includableProps = includableProps;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = src._vanillaProcessing;
      this._objectIdReader = src._objectIdReader;
      this._beanProperties = src._beanProperties.withoutProperties(ignorableProps, includableProps);
      this._externalTypeIdHandler = src._externalTypeIdHandler;
   }

   protected BeanDeserializerBase(BeanDeserializerBase src, BeanPropertyMap beanProps) {
      super(src._beanType);
      this._beanType = src._beanType;
      this._valueInstantiator = src._valueInstantiator;
      this._delegateDeserializer = src._delegateDeserializer;
      this._arrayDelegateDeserializer = src._arrayDelegateDeserializer;
      this._propertyBasedCreator = src._propertyBasedCreator;
      this._beanProperties = beanProps;
      this._backRefs = src._backRefs;
      this._ignorableProps = src._ignorableProps;
      this._ignoreAllUnknown = src._ignoreAllUnknown;
      this._includableProps = src._includableProps;
      this._anySetter = src._anySetter;
      this._injectables = src._injectables;
      this._objectIdReader = src._objectIdReader;
      this._nonStandardCreation = src._nonStandardCreation;
      this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
      this._needViewProcesing = src._needViewProcesing;
      this._serializationShape = src._serializationShape;
      this._vanillaProcessing = src._vanillaProcessing;
      this._externalTypeIdHandler = src._externalTypeIdHandler;
   }

   /** @deprecated */
   @Deprecated
   protected BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps) {
      this(src, ignorableProps, src._includableProps);
   }

   public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer var1);

   public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader var1);

   public abstract BeanDeserializerBase withByNameInclusion(Set<String> var1, Set<String> var2);

   public abstract BeanDeserializerBase withIgnoreAllUnknown(boolean var1);

   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
      throw new UnsupportedOperationException("Class " + this.getClass().getName() + " does not override `withBeanProperties()`, needs to");
   }

   protected abstract BeanDeserializerBase asArrayDeserializer();

   /** @deprecated */
   @Deprecated
   public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
      return this.withByNameInclusion(ignorableProps, this._includableProps);
   }

   public void resolve(DeserializationContext ctxt) throws JsonMappingException {
      ExternalTypeHandler.Builder extTypes = null;
      SettableBeanProperty[] creatorProps;
      if (this._valueInstantiator.canCreateFromObjectWith()) {
         creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
         if (this._ignorableProps != null || this._includableProps != null) {
            int i = 0;

            for(int end = creatorProps.length; i < end; ++i) {
               SettableBeanProperty prop = creatorProps[i];
               if (IgnorePropertiesUtil.shouldIgnore(prop.getName(), this._ignorableProps, this._includableProps)) {
                  creatorProps[i].markAsIgnorable();
               }
            }
         }
      } else {
         creatorProps = null;
      }

      UnwrappedPropertyHandler unwrapped = null;

      for(SettableBeanProperty prop : this._beanProperties) {
         if (!prop.hasValueDeserializer()) {
            JsonDeserializer<?> deser = this.findConvertingDeserializer(ctxt, prop);
            if (deser == null) {
               deser = ctxt.findNonContextualValueDeserializer(prop.getType());
            }

            SettableBeanProperty newProp = prop.withValueDeserializer(deser);
            this._replaceProperty(this._beanProperties, creatorProps, prop, newProp);
         }
      }

      for(SettableBeanProperty origProp : this._beanProperties) {
         JsonDeserializer<?> deser = origProp.getValueDeserializer();
         deser = ctxt.handlePrimaryContextualization(deser, origProp, origProp.getType());
         SettableBeanProperty var19 = origProp.withValueDeserializer(deser);
         var19 = this._resolveManagedReferenceProperty(ctxt, var19);
         if (!(var19 instanceof ManagedReferenceProperty)) {
            var19 = this._resolvedObjectIdProperty(ctxt, var19);
         }

         NameTransformer xform = this._findPropertyUnwrapper(ctxt, var19);
         if (xform != null) {
            JsonDeserializer<Object> orig = var19.getValueDeserializer();
            JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(xform);
            if (unwrapping != orig && unwrapping != null) {
               var19 = var19.withValueDeserializer(unwrapping);
               if (unwrapped == null) {
                  unwrapped = new UnwrappedPropertyHandler();
               }

               if (var19 instanceof CreatorProperty) {
                  unwrapped.addCreatorProperty(var19);
               } else {
                  unwrapped.addProperty(var19);
               }

               this._beanProperties.remove(var19);
               continue;
            }
         }

         PropertyMetadata md = var19.getMetadata();
         var19 = this._resolveMergeAndNullSettings(ctxt, var19, md);
         var19 = this._resolveInnerClassValuedProperty(ctxt, var19);
         if (var19 != origProp) {
            this._replaceProperty(this._beanProperties, creatorProps, origProp, var19);
         }

         if (var19.hasValueTypeDeserializer()) {
            TypeDeserializer typeDeser = var19.getValueTypeDeserializer();
            if (typeDeser.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
               if (extTypes == null) {
                  extTypes = ExternalTypeHandler.builder(this._beanType);
               }

               extTypes.addExternal(var19, typeDeser);
               this._beanProperties.remove(var19);
            }
         }
      }

      if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
         this._anySetter = this._anySetter.withValueDeserializer(this.findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty()));
      }

      if (this._valueInstantiator.canCreateUsingDelegate()) {
         JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
         if (delegateType == null) {
            ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", ClassUtil.getTypeDescription(this._beanType), ClassUtil.classNameOf(this._valueInstantiator)));
         }

         this._delegateDeserializer = this._findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getDelegateCreator());
      }

      if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
         JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
         if (delegateType == null) {
            ctxt.reportBadDefinition(this._beanType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", ClassUtil.getTypeDescription(this._beanType), ClassUtil.classNameOf(this._valueInstantiator)));
         }

         this._arrayDelegateDeserializer = this._findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getArrayDelegateCreator());
      }

      if (creatorProps != null) {
         this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, this._beanProperties);
      }

      if (extTypes != null) {
         this._externalTypeIdHandler = extTypes.build(this._beanProperties);
         this._nonStandardCreation = true;
      }

      this._unwrappedPropertyHandler = unwrapped;
      if (unwrapped != null) {
         this._nonStandardCreation = true;
      }

      this._vanillaProcessing = this._vanillaProcessing && !this._nonStandardCreation;
   }

   protected void _replaceProperty(BeanPropertyMap props, SettableBeanProperty[] creatorProps, SettableBeanProperty origProp, SettableBeanProperty newProp) {
      props.replace(origProp, newProp);
      if (creatorProps != null) {
         int i = 0;

         for(int len = creatorProps.length; i < len; ++i) {
            if (creatorProps[i] == origProp) {
               creatorProps[i] = newProp;
               return;
            }
         }
      }

   }

   private JsonDeserializer<Object> _findDelegateDeserializer(DeserializationContext ctxt, JavaType delegateType, AnnotatedWithParams delegateCreator) throws JsonMappingException {
      BeanProperty property;
      if (delegateCreator != null && delegateCreator.getParameterCount() == 1) {
         AnnotatedMember delegator = delegateCreator.getParameter(0);
         PropertyMetadata propMd = this._getSetterInfo(ctxt, delegator, delegateType);
         property = new BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, (PropertyName)null, delegator, propMd);
      } else {
         property = new BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, (PropertyName)null, delegateCreator, PropertyMetadata.STD_OPTIONAL);
      }

      TypeDeserializer td = (TypeDeserializer)delegateType.getTypeHandler();
      if (td == null) {
         td = ctxt.getConfig().findTypeDeserializer(delegateType);
      }

      JsonDeserializer<Object> dd = (JsonDeserializer)delegateType.getValueHandler();
      if (dd == null) {
         dd = this.findDeserializer(ctxt, delegateType, property);
      } else {
         dd = ctxt.handleSecondaryContextualization(dd, property, delegateType);
      }

      if (td != null) {
         td = td.forProperty(property);
         return new TypeWrappedDeserializer(td, dd);
      } else {
         return dd;
      }
   }

   protected PropertyMetadata _getSetterInfo(DeserializationContext ctxt, AnnotatedMember accessor, JavaType type) {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      DeserializationConfig config = ctxt.getConfig();
      PropertyMetadata metadata = PropertyMetadata.STD_OPTIONAL;
      boolean needMerge = true;
      Nulls valueNulls = null;
      Nulls contentNulls = null;
      if (intr != null) {
         JsonSetter.Value setterInfo = intr.findSetterInfo(accessor);
         if (setterInfo != null) {
            valueNulls = setterInfo.nonDefaultValueNulls();
            contentNulls = setterInfo.nonDefaultContentNulls();
         }
      }

      if (needMerge || valueNulls == null || contentNulls == null) {
         ConfigOverride co = config.getConfigOverride(type.getRawClass());
         JsonSetter.Value setterInfo = co.getSetterInfo();
         if (setterInfo != null) {
            if (valueNulls == null) {
               valueNulls = setterInfo.nonDefaultValueNulls();
            }

            if (contentNulls == null) {
               contentNulls = setterInfo.nonDefaultContentNulls();
            }
         }
      }

      if (needMerge || valueNulls == null || contentNulls == null) {
         JsonSetter.Value setterInfo = config.getDefaultSetterInfo();
         if (valueNulls == null) {
            valueNulls = setterInfo.nonDefaultValueNulls();
         }

         if (contentNulls == null) {
            contentNulls = setterInfo.nonDefaultContentNulls();
         }
      }

      if (valueNulls != null || contentNulls != null) {
         metadata = metadata.withNulls(valueNulls, contentNulls);
      }

      return metadata;
   }

   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object convDef = intr.findDeserializationConverter(prop.getMember());
         if (convDef != null) {
            Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
            JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
            JsonDeserializer<?> deser = ctxt.findNonContextualValueDeserializer(delegateType);
            return new StdDelegatingDeserializer<Object>(conv, delegateType, deser);
         }
      }

      return null;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      ObjectIdReader oir = this._objectIdReader;
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      AnnotatedMember accessor = _neitherNull(property, intr) ? property.getMember() : null;
      if (accessor != null) {
         ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
         if (objectIdInfo != null) {
            objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
            Class<?> implClass = objectIdInfo.getGeneratorType();
            ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
            JavaType idType;
            SettableBeanProperty idProp;
            ObjectIdGenerator<?> idGen;
            if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
               PropertyName propName = objectIdInfo.getPropertyName();
               idProp = this.findProperty(propName);
               if (idProp == null) {
                  return (JsonDeserializer)ctxt.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.nameOf(this.handledType()), ClassUtil.name(propName)));
               }

               idType = idProp.getType();
               idGen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
            } else {
               JavaType type = ctxt.constructType(implClass);
               idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
               idProp = null;
               idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
            }

            JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
            oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
         }
      }

      BeanDeserializerBase contextual = this;
      if (oir != null && oir != this._objectIdReader) {
         contextual = this.withObjectIdReader(oir);
      }

      if (accessor != null) {
         contextual = this._handleByNameInclusion(ctxt, intr, contextual, accessor);
      }

      JsonFormat.Value format = this.findFormatOverrides(ctxt, property, this.handledType());
      JsonFormat.Shape shape = null;
      if (format != null) {
         if (format.hasShape()) {
            shape = format.getShape();
         }

         Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
         if (B != null) {
            BeanPropertyMap propsOrig = this._beanProperties;
            BeanPropertyMap props = propsOrig.withCaseInsensitivity(B);
            if (props != propsOrig) {
               contextual = contextual.withBeanProperties(props);
            }
         }
      }

      if (shape == null) {
         shape = this._serializationShape;
      }

      if (shape == JsonFormat.Shape.ARRAY) {
         contextual = contextual.asArrayDeserializer();
      }

      return contextual;
   }

   protected BeanDeserializerBase _handleByNameInclusion(DeserializationContext ctxt, AnnotationIntrospector intr, BeanDeserializerBase contextual, AnnotatedMember accessor) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(config, accessor);
      if (ignorals.getIgnoreUnknown() && !this._ignoreAllUnknown) {
         contextual = contextual.withIgnoreAllUnknown(true);
      }

      Set<String> namesToIgnore = ignorals.findIgnoredForDeserialization();
      Set<String> prevNamesToIgnore = contextual._ignorableProps;
      Set<String> newNamesToIgnore;
      if (namesToIgnore.isEmpty()) {
         newNamesToIgnore = prevNamesToIgnore;
      } else if (prevNamesToIgnore != null && !prevNamesToIgnore.isEmpty()) {
         newNamesToIgnore = new HashSet(prevNamesToIgnore);
         newNamesToIgnore.addAll(namesToIgnore);
      } else {
         newNamesToIgnore = namesToIgnore;
      }

      Set<String> prevNamesToInclude = contextual._includableProps;
      Set<String> newNamesToInclude = IgnorePropertiesUtil.combineNamesToInclude(prevNamesToInclude, intr.findPropertyInclusionByName(config, accessor).getIncluded());
      if (newNamesToIgnore != prevNamesToIgnore || newNamesToInclude != prevNamesToInclude) {
         contextual = contextual.withByNameInclusion(newNamesToIgnore, newNamesToInclude);
      }

      return contextual;
   }

   protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      String refName = prop.getManagedReferenceName();
      if (refName == null) {
         return prop;
      } else {
         JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
         SettableBeanProperty backProp = valueDeser.findBackReference(refName);
         if (backProp == null) {
            return (SettableBeanProperty)ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference %s: no back reference property found from type %s", ClassUtil.name(refName), ClassUtil.getTypeDescription(prop.getType())));
         } else {
            JavaType referredType = this._beanType;
            JavaType backRefType = backProp.getType();
            boolean isContainer = prop.getType().isContainerType();
            if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
               ctxt.reportBadDefinition(this._beanType, String.format("Cannot handle managed/back reference %s: back reference type (%s) not compatible with managed type (%s)", ClassUtil.name(refName), ClassUtil.getTypeDescription(backRefType), referredType.getRawClass().getName()));
            }

            return new ManagedReferenceProperty(prop, refName, backProp, isContainer);
         }
      }
   }

   protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
      JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
      ObjectIdReader objectIdReader = valueDeser == null ? null : valueDeser.getObjectIdReader();
      return (SettableBeanProperty)(objectIdInfo == null && objectIdReader == null ? prop : new ObjectIdReferenceProperty(prop, objectIdInfo));
   }

   protected NameTransformer _findPropertyUnwrapper(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
      AnnotatedMember am = prop.getMember();
      if (am != null) {
         NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
         if (unwrapper != null) {
            return unwrapper;
         }
      }

      return null;
   }

   protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
      JsonDeserializer<Object> deser = prop.getValueDeserializer();
      if (deser instanceof BeanDeserializerBase) {
         BeanDeserializerBase bd = (BeanDeserializerBase)deser;
         ValueInstantiator vi = bd.getValueInstantiator();
         if (!vi.canCreateUsingDefault()) {
            Class<?> valueClass = prop.getType().getRawClass();
            Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
            if (enclosing != null && enclosing == this._beanType.getRawClass()) {
               for(Constructor<?> ctor : valueClass.getConstructors()) {
                  if (ctor.getParameterCount() == 1) {
                     Class<?>[] paramTypes = ctor.getParameterTypes();
                     if (enclosing.equals(paramTypes[0])) {
                        if (ctxt.canOverrideAccessModifiers()) {
                           ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }

                        return new InnerClassProperty(prop, ctor);
                     }
                  }
               }
            }
         }
      }

      return prop;
   }

   protected SettableBeanProperty _resolveMergeAndNullSettings(DeserializationContext ctxt, SettableBeanProperty prop, PropertyMetadata propMetadata) throws JsonMappingException {
      PropertyMetadata.MergeInfo merge = propMetadata.getMergeInfo();
      if (merge != null) {
         JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
         Boolean mayMerge = valueDeser.supportsUpdate(ctxt.getConfig());
         if (mayMerge == null) {
            if (merge.fromDefaults) {
               return prop;
            }
         } else if (!mayMerge) {
            if (!merge.fromDefaults) {
               ctxt.handleBadMerge(valueDeser);
            }

            return prop;
         }

         AnnotatedMember accessor = merge.getter;
         accessor.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         if (!(prop instanceof SetterlessProperty)) {
            prop = MergingSettableBeanProperty.construct(prop, accessor);
         }
      }

      NullValueProvider nuller = this.findValueNullProvider(ctxt, prop, propMetadata);
      if (nuller != null) {
         prop = prop.withNullProvider(nuller);
      }

      return prop;
   }

   public AccessPattern getNullAccessPattern() {
      return AccessPattern.ALWAYS_NULL;
   }

   public AccessPattern getEmptyAccessPattern() {
      return AccessPattern.DYNAMIC;
   }

   public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
      try {
         return this._valueInstantiator.createUsingDefaultOrWithoutArguments(ctxt);
      } catch (IOException e) {
         return ClassUtil.throwAsMappingException(ctxt, e);
      }
   }

   public boolean isCachable() {
      return true;
   }

   public boolean isCaseInsensitive() {
      return this._beanProperties.isCaseInsensitive();
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return Boolean.TRUE;
   }

   public Class<?> handledType() {
      return this._beanType.getRawClass();
   }

   public ObjectIdReader getObjectIdReader() {
      return this._objectIdReader;
   }

   public boolean hasProperty(String propertyName) {
      return this._beanProperties.find(propertyName) != null;
   }

   public boolean hasViews() {
      return this._needViewProcesing;
   }

   public int getPropertyCount() {
      return this._beanProperties.size();
   }

   public Collection<Object> getKnownPropertyNames() {
      ArrayList<Object> names = new ArrayList();

      for(SettableBeanProperty prop : this._beanProperties) {
         names.add(prop.getName());
      }

      return names;
   }

   /** @deprecated */
   @Deprecated
   public final Class<?> getBeanClass() {
      return this._beanType.getRawClass();
   }

   public JavaType getValueType() {
      return this._beanType;
   }

   public LogicalType logicalType() {
      return LogicalType.POJO;
   }

   public Iterator<SettableBeanProperty> properties() {
      if (this._beanProperties == null) {
         throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
      } else {
         return this._beanProperties.iterator();
      }
   }

   public Iterator<SettableBeanProperty> creatorProperties() {
      return this._propertyBasedCreator == null ? Collections.emptyList().iterator() : this._propertyBasedCreator.properties().iterator();
   }

   public SettableBeanProperty findProperty(PropertyName propertyName) {
      return this.findProperty(propertyName.getSimpleName());
   }

   public SettableBeanProperty findProperty(String propertyName) {
      SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyName);
      if (prop == null && this._propertyBasedCreator != null) {
         prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
      }

      return prop;
   }

   public SettableBeanProperty findProperty(int propertyIndex) {
      SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyIndex);
      if (prop == null && this._propertyBasedCreator != null) {
         prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
      }

      return prop;
   }

   public SettableBeanProperty findBackReference(String logicalName) {
      return this._backRefs == null ? null : (SettableBeanProperty)this._backRefs.get(logicalName);
   }

   public ValueInstantiator getValueInstantiator() {
      return this._valueInstantiator;
   }

   public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement) {
      this._beanProperties.replace(original, replacement);
   }

   public abstract Object deserializeFromObject(JsonParser var1, DeserializationContext var2) throws IOException;

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      if (this._objectIdReader != null) {
         if (p.canReadObjectId()) {
            Object id = p.getObjectId();
            if (id != null) {
               Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
               return this._handleTypedObjectId(p, ctxt, ob, id);
            }
         }

         JsonToken t = p.currentToken();
         if (t != null) {
            if (t.isScalarValue()) {
               return this.deserializeFromObjectId(p, ctxt);
            }

            if (t == JsonToken.START_OBJECT) {
               t = p.nextToken();
            }

            if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.currentName(), p)) {
               return this.deserializeFromObjectId(p, ctxt);
            }
         }
      }

      return typeDeserializer.deserializeTypedFromObject(p, ctxt);
   }

   protected Object _handleTypedObjectId(JsonParser p, DeserializationContext ctxt, Object pojo, Object rawId) throws IOException {
      JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
      Object id;
      if (idDeser.handledType() == rawId.getClass()) {
         id = rawId;
      } else {
         id = this._convertObjectId(p, ctxt, rawId, idDeser);
      }

      ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
      roid.bindItem(pojo);
      SettableBeanProperty idProp = this._objectIdReader.idProperty;
      return idProp != null ? idProp.setAndReturn(pojo, id) : pojo;
   }

   protected Object _convertObjectId(JsonParser p, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser) throws IOException {
      TokenBuffer buf = ctxt.bufferForInputBuffering(p);
      if (rawId instanceof String) {
         buf.writeString((String)rawId);
      } else if (rawId instanceof Long) {
         buf.writeNumber((Long)rawId);
      } else if (rawId instanceof Integer) {
         buf.writeNumber((Integer)rawId);
      } else {
         buf.writeObject(rawId);
      }

      JsonParser bufParser = buf.asParser(p.streamReadConstraints());
      bufParser.nextToken();
      return idDeser.deserialize(bufParser, ctxt);
   }

   protected Object deserializeWithObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this.deserializeFromObject(p, ctxt);
   }

   protected Object deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
      Object id = this._objectIdReader.readObjectReference(p, ctxt);
      ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
      Object pojo = roid.resolve();
      if (pojo == null) {
         throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", p.currentLocation(), roid);
      } else {
         return pojo;
      }
   }

   protected Object deserializeFromObjectUsingNonDefault(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonDeserializer<Object> delegateDeser = this._delegateDeserializer(p);
      if (delegateDeser != null) {
         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         return bean;
      } else if (this._propertyBasedCreator != null) {
         return this._deserializeUsingPropertyBased(p, ctxt);
      } else {
         Class<?> raw = this._beanType.getRawClass();
         if (ClassUtil.isNonStaticInnerClass(raw)) {
            return ctxt.handleMissingInstantiator(raw, (ValueInstantiator)null, p, "non-static inner classes like this can only by instantiated using default, no-argument constructor");
         } else {
            return NativeImageUtil.needsReflectionConfiguration(raw) ? ctxt.handleMissingInstantiator(raw, (ValueInstantiator)null, p, "cannot deserialize from Object value (no delegate- or property-based Creator): this appears to be a native image, in which case you may need to configure reflection for the class that is to be deserialized") : ctxt.handleMissingInstantiator(raw, this.getValueInstantiator(), p, "cannot deserialize from Object value (no delegate- or property-based Creator)");
         }
      }
   }

   protected abstract Object _deserializeUsingPropertyBased(JsonParser var1, DeserializationContext var2) throws IOException;

   public Object deserializeFromNumber(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         JsonParser.NumberType nt = p.getNumberType();
         if (nt == JsonParser.NumberType.INT) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
               Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            } else {
               return this._valueInstantiator.createFromInt(ctxt, p.getIntValue());
            }
         } else if (nt == JsonParser.NumberType.LONG) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromInt()) {
               Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            } else {
               return this._valueInstantiator.createFromLong(ctxt, p.getLongValue());
            }
         } else if (nt == JsonParser.NumberType.BIG_INTEGER) {
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromBigInteger()) {
               Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            } else {
               return this._valueInstantiator.createFromBigInteger(ctxt, p.getBigIntegerValue());
            }
         } else {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
         }
      }
   }

   public Object deserializeFromString(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            return bean;
         } else {
            return this._deserializeFromString(p, ctxt);
         }
      }
   }

   public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonParser.NumberType t = p.getNumberType();
      if (t != JsonParser.NumberType.DOUBLE && t != JsonParser.NumberType.FLOAT) {
         if (t == JsonParser.NumberType.BIG_DECIMAL) {
            JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
            if (delegateDeser != null && !this._valueInstantiator.canCreateFromBigDecimal()) {
               Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
               if (this._injectables != null) {
                  this.injectValues(ctxt, bean);
               }

               return bean;
            } else {
               return this._valueInstantiator.createFromBigDecimal(ctxt, p.getDecimalValue());
            }
         } else {
            return ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), p, "no suitable creator method found to deserialize from Number value (%s)", p.getNumberValue());
         }
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         if (delegateDeser != null && !this._valueInstantiator.canCreateFromDouble()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            return bean;
         } else {
            return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
         }
      }
   }

   public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
      if (delegateDeser != null && !this._valueInstantiator.canCreateFromBoolean()) {
         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
         if (this._injectables != null) {
            this.injectValues(ctxt, bean);
         }

         return bean;
      } else {
         boolean value = p.currentToken() == JsonToken.VALUE_TRUE;
         return this._valueInstantiator.createFromBoolean(ctxt, value);
      }
   }

   /** @deprecated */
   @Deprecated
   public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
      return this._deserializeFromArray(p, ctxt);
   }

   public Object deserializeFromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (this._objectIdReader != null) {
         return this.deserializeFromObjectId(p, ctxt);
      } else {
         JsonDeserializer<Object> delegateDeser = this._delegateDeserializer();
         if (delegateDeser != null && !this._valueInstantiator.canCreateFromString()) {
            Object bean = this._valueInstantiator.createUsingDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
            if (this._injectables != null) {
               this.injectValues(ctxt, bean);
            }

            return bean;
         } else {
            Object value = p.getEmbeddedObject();
            if (value != null && !this._beanType.isTypeOrSuperTypeOf(value.getClass())) {
               value = ctxt.handleWeirdNativeValue(this._beanType, value, p);
            }

            return value;
         }
      }
   }

   protected final JsonDeserializer<Object> _delegateDeserializer() {
      JsonDeserializer<Object> deser = this._delegateDeserializer;
      if (deser == null) {
         deser = this._arrayDelegateDeserializer;
      }

      return deser;
   }

   protected final JsonDeserializer<Object> _delegateDeserializer(JsonParser p) {
      return this._delegateDeserializer != null || this._arrayDelegateDeserializer == null || !p.hasToken(JsonToken.START_ARRAY) && this._propertyBasedCreator != null ? this._delegateDeserializer : this._arrayDelegateDeserializer;
   }

   protected void injectValues(DeserializationContext ctxt, Object bean) throws IOException {
      for(ValueInjector injector : this._injectables) {
         injector.inject(ctxt, bean);
      }

   }

   protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
      unknownTokens.writeEndObject();
      JsonParser bufferParser = unknownTokens.asParser();

      while(bufferParser.nextToken() != JsonToken.END_OBJECT) {
         String propName = bufferParser.currentName();
         bufferParser.nextToken();
         this.handleUnknownProperty(bufferParser, ctxt, bean, propName);
      }

      return bean;
   }

   protected void handleUnknownVanilla(JsonParser p, DeserializationContext ctxt, Object beanOrBuilder, String propName) throws IOException {
      if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
         this.handleIgnoredProperty(p, ctxt, beanOrBuilder, propName);
      } else if (this._anySetter != null) {
         try {
            this._anySetter.deserializeAndSet(p, ctxt, beanOrBuilder, propName);
         } catch (Exception e) {
            this.wrapAndThrow(e, beanOrBuilder, propName, ctxt);
         }
      } else {
         this.handleUnknownProperty(p, ctxt, beanOrBuilder, propName);
      }

   }

   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
      if (this._ignoreAllUnknown) {
         p.skipChildren();
      } else {
         if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
            this.handleIgnoredProperty(p, ctxt, beanOrClass, propName);
         }

         super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
      }
   }

   protected void handleIgnoredProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException {
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
         throw IgnoredPropertyException.from(p, beanOrClass, propName, this.getKnownPropertyNames());
      } else {
         p.skipChildren();
      }
   }

   /** @deprecated */
   @Deprecated
   protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
      StreamReadConstraints streamReadConstraints = p == null ? StreamReadConstraints.defaults() : p.streamReadConstraints();
      return this.handlePolymorphic(p, ctxt, streamReadConstraints, bean, unknownTokens);
   }

   protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, StreamReadConstraints streamReadConstraints, Object bean, TokenBuffer unknownTokens) throws IOException {
      JsonDeserializer<Object> subDeser = this._findSubclassDeserializer(ctxt, bean, unknownTokens);
      if (subDeser != null) {
         if (unknownTokens != null) {
            unknownTokens.writeEndObject();
            JsonParser p2 = unknownTokens.asParser(streamReadConstraints);
            p2.nextToken();
            bean = subDeser.deserialize(p2, ctxt, bean);
         }

         if (p != null) {
            bean = subDeser.deserialize(p, ctxt, bean);
         }

         return bean;
      } else {
         if (unknownTokens != null) {
            bean = this.handleUnknownProperties(ctxt, bean, unknownTokens);
         }

         if (p != null) {
            bean = this.deserialize(p, ctxt, bean);
         }

         return bean;
      }
   }

   protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException {
      ClassKey classKey = new ClassKey(bean.getClass());
      JsonDeserializer<Object> subDeser = this._subDeserializers == null ? null : (JsonDeserializer)this._subDeserializers.get(classKey);
      if (subDeser != null) {
         return subDeser;
      } else {
         JavaType type = ctxt.constructType(bean.getClass());
         subDeser = ctxt.findRootValueDeserializer(type);
         if (subDeser != null) {
            if (this._subDeserializers == null) {
               synchronized(this) {
                  if (this._subDeserializers == null) {
                     this._subDeserializers = new ConcurrentHashMap();
                  }
               }
            }

            this._subDeserializers.put(classKey, subDeser);
         }

         return subDeser;
      }
   }

   public <T> T wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
      throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, fieldName);
   }

   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
      if (t instanceof IOException) {
         if (!wrap || !(t instanceof JacksonException)) {
            throw (IOException)t;
         }
      } else if (!wrap) {
         ClassUtil.throwIfRTE(t);
      }

      return t;
   }

   protected <T> T wrapInstantiationProblem(Throwable t, DeserializationContext ctxt) throws IOException {
      while(t instanceof InvocationTargetException && t.getCause() != null) {
         t = t.getCause();
      }

      ClassUtil.throwIfError(t);
      if (t instanceof IOException) {
         throw (IOException)t;
      } else if (ctxt == null) {
         throw new IllegalArgumentException(t.getMessage(), t);
      } else {
         if (!ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS)) {
            ClassUtil.throwIfRTE(t);
         }

         return (T)ctxt.handleInstantiationProblem(this._beanType.getRawClass(), (Object)null, t);
      }
   }
}
