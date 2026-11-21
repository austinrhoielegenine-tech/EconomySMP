package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.impl.CreatorCandidate;
import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;
import com.fasterxml.jackson.databind.deser.impl.JavaUtilCollectionsDeserializers;
import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.EnumNamingStrategyFactory;
import com.fasterxml.jackson.databind.introspect.PotentialCreator;
import com.fasterxml.jackson.databind.introspect.PotentialCreators;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
   private static final Class<?> CLASS_OBJECT = Object.class;
   private static final Class<?> CLASS_STRING = String.class;
   private static final Class<?> CLASS_CHAR_SEQUENCE = CharSequence.class;
   private static final Class<?> CLASS_ITERABLE = Iterable.class;
   private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
   private static final Class<?> CLASS_SERIALIZABLE = Serializable.class;
   protected final DeserializerFactoryConfig _factoryConfig;

   protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
      this._factoryConfig = config;
   }

   public DeserializerFactoryConfig getFactoryConfig() {
      return this._factoryConfig;
   }

   protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig var1);

   public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
      return this.withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
   }

   public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
      return this.withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
   }

   public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
      return this.withConfig(this._factoryConfig.withDeserializerModifier(modifier));
   }

   public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
      return this.withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
   }

   public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
      return this.withConfig(this._factoryConfig.withValueInstantiators(instantiators));
   }

   public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
      while(true) {
         JavaType next = this._mapAbstractType2(config, type);
         if (next == null) {
            return type;
         }

         Class<?> prevCls = type.getRawClass();
         Class<?> nextCls = next.getRawClass();
         if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
            throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
         }

         type = next;
      }
   }

   private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
      Class<?> currClass = type.getRawClass();
      if (this._factoryConfig.hasAbstractTypeResolvers()) {
         for(AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
            JavaType concrete = resolver.findTypeMapping(config, type);
            if (concrete != null && !concrete.hasRawClass(currClass)) {
               return concrete;
            }
         }
      }

      return null;
   }

   public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      ValueInstantiator instantiator = null;
      AnnotatedClass ac = beanDesc.getClassInfo();
      Object instDef = config.getAnnotationIntrospector().findValueInstantiator(ac);
      if (instDef != null) {
         instantiator = this._valueInstantiatorInstance(config, ac, instDef);
      }

      if (instantiator == null) {
         instantiator = JDKValueInstantiators.findStdValueInstantiator(config, beanDesc.getBeanClass());
         if (instantiator == null) {
            instantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
         }
      }

      if (this._factoryConfig.hasValueInstantiators()) {
         for(ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
            instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
            if (instantiator == null) {
               ctxt.reportBadTypeDefinition(beanDesc, "Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
            }
         }
      }

      if (instantiator != null) {
         instantiator = instantiator.createContextual(ctxt, beanDesc);
      }

      return instantiator;
   }

   protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
      MapperConfig<?> config = ctxt.getConfig();
      PotentialCreators potentialCreators = beanDesc.getPotentialCreators();
      ConstructorDetector ctorDetector = config.getConstructorDetector();
      VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker(beanDesc.getBeanClass(), beanDesc.getClassInfo());
      CreatorCollector creators = new CreatorCollector(beanDesc, config);
      if (potentialCreators.hasPropertiesBased()) {
         PotentialCreator primaryPropsBased = potentialCreators.propertiesBased;
         if (primaryPropsBased.paramCount() == 0) {
            creators.setDefaultCreator(primaryPropsBased.creator());
         } else {
            this._addSelectedPropertiesBasedCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(config.getAnnotationIntrospector(), primaryPropsBased.creator(), primaryPropsBased.propertyDefs()));
         }
      }

      boolean hasExplicitDelegating = this._addExplicitDelegatingCreators(ctxt, beanDesc, creators, potentialCreators.getExplicitDelegating());
      if (beanDesc.getType().isConcrete()) {
         boolean isNonStaticInnerClass = beanDesc.isNonStaticInnerClass();
         if (!isNonStaticInnerClass) {
            if (!creators.hasDefaultCreator()) {
               AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
               if (defaultCtor != null) {
                  creators.setDefaultCreator(defaultCtor);
               }
            }

            boolean findImplicit = ctorDetector.shouldIntrospectorImplicitConstructors(beanDesc.getBeanClass());
            if (findImplicit) {
               this._addImplicitDelegatingConstructors(ctxt, beanDesc, vchecker, creators, potentialCreators.getImplicitDelegatingConstructors());
            }
         }
      }

      if (!hasExplicitDelegating) {
         this._addImplicitDelegatingFactories(ctxt, vchecker, creators, potentialCreators.getImplicitDelegatingFactories());
      }

      return creators.constructValueInstantiator(ctxt);
   }

   public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
      if (instDef == null) {
         return null;
      } else if (instDef instanceof ValueInstantiator) {
         return (ValueInstantiator)instDef;
      } else if (!(instDef instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
      } else {
         Class<?> instClass = (Class)instDef;
         if (ClassUtil.isBogusClass(instClass)) {
            return null;
         } else if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
         } else {
            HandlerInstantiator hi = config.getHandlerInstantiator();
            if (hi != null) {
               ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
               if (inst != null) {
                  return inst;
               }
            }

            return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
         }
      }
   }

   private boolean _addExplicitDelegatingCreators(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, List<PotentialCreator> potentials) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      boolean added = false;

      for(PotentialCreator ctor : potentials) {
         added |= this._addExplicitDelegatingCreator(ctxt, beanDesc, creators, CreatorCandidate.construct(intr, ctor.creator(), (BeanPropertyDefinition[])null));
      }

      return added;
   }

   private void _addImplicitDelegatingConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, CreatorCollector creators, List<PotentialCreator> potentials) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();

      for(PotentialCreator candidate : potentials) {
         int argCount = candidate.paramCount();
         AnnotatedWithParams ctor = candidate.creator();
         if (argCount == 1) {
            this._handleSingleArgumentCreator(creators, ctor, false, vchecker.isCreatorVisible((AnnotatedMember)ctor));
         } else {
            SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
            int injectCount = 0;

            for(int i = 0; i < argCount; ++i) {
               AnnotatedParameter param = ctor.getParameter(i);
               JacksonInject.Value injectable = intr.findInjectableValue(param);
               if (injectable != null) {
                  ++injectCount;
                  properties[i] = this.constructCreatorProperty(ctxt, beanDesc, (PropertyName)null, i, param, injectable);
               } else {
                  NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
                  if (unwrapper != null) {
                     properties[i] = this.constructCreatorProperty(ctxt, beanDesc, UnwrappedPropertyHandler.creatorParamName(i), i, param, (JacksonInject.Value)null);
                  }
               }
            }

            if (injectCount + 1 == argCount) {
               creators.addDelegatingCreator(ctor, false, properties, 0);
            }
         }
      }

   }

   private void _addImplicitDelegatingFactories(DeserializationContext ctxt, VisibilityChecker<?> vchecker, CreatorCollector creators, List<PotentialCreator> potentials) throws JsonMappingException {
      for(PotentialCreator candidate : potentials) {
         int argCount = candidate.paramCount();
         AnnotatedWithParams factory = candidate.creator();
         if (argCount == 1) {
            this._handleSingleArgumentCreator(creators, factory, false, vchecker.isCreatorVisible((AnnotatedMember)factory));
         }
      }

   }

   private boolean _addExplicitDelegatingCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
      int ix = -1;
      int argCount = candidate.paramCount();
      SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
      if (argCount == 0) {
         creators.addPropertyCreator(candidate.creator(), true, properties);
         return true;
      } else {
         for(int i = 0; i < argCount; ++i) {
            AnnotatedParameter param = candidate.parameter(i);
            JacksonInject.Value injectId = candidate.injection(i);
            if (injectId != null) {
               properties[i] = this.constructCreatorProperty(ctxt, beanDesc, (PropertyName)null, i, param, injectId);
            } else if (ix < 0) {
               ix = i;
            } else {
               ctxt.reportBadTypeDefinition(beanDesc, "More than one argument (#%d and #%d) left as delegating for Creator %s: only one allowed", ix, i, candidate);
            }
         }

         if (ix < 0) {
            ctxt.reportBadTypeDefinition(beanDesc, "No argument left as delegating for Creator %s: exactly one required", candidate);
         }

         if (argCount == 1) {
            return this._handleSingleArgumentCreator(creators, candidate.creator(), true, true);
         } else {
            creators.addDelegatingCreator(candidate.creator(), true, properties, ix);
            return true;
         }
      }
   }

   private void _addSelectedPropertiesBasedCreator(DeserializationContext ctxt, BeanDescription beanDesc, CreatorCollector creators, CreatorCandidate candidate) throws JsonMappingException {
      int paramCount = candidate.paramCount();
      SettableBeanProperty[] properties = new SettableBeanProperty[paramCount];
      int anySetterIx = -1;

      for(int i = 0; i < paramCount; ++i) {
         JacksonInject.Value injectId = candidate.injection(i);
         AnnotatedParameter param = candidate.parameter(i);
         PropertyName name = candidate.paramName(i);
         boolean isAnySetter = Boolean.TRUE.equals(ctxt.getAnnotationIntrospector().hasAnySetter(param));
         if (isAnySetter) {
            if (anySetterIx >= 0) {
               ctxt.reportBadTypeDefinition(beanDesc, "More than one 'any-setter' specified (parameter #%d vs #%d)", anySetterIx, i);
            } else {
               anySetterIx = i;
            }
         } else if (name == null) {
            NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(param);
            if (unwrapper != null) {
               properties[i] = this.constructCreatorProperty(ctxt, beanDesc, UnwrappedPropertyHandler.creatorParamName(i), i, param, (JacksonInject.Value)null);
            }

            if (name == null && injectId == null) {
               ctxt.reportBadTypeDefinition(beanDesc, "Argument #%d of Creator %s has no property name (and is not Injectable): can not use as property-based Creator", i, candidate);
            }
         }

         properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
      }

      creators.addPropertyCreator(candidate.creator(), true, properties);
   }

   private boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible) {
      Class<?> type = ctor.getRawParameterType(0);
      if (type != String.class && type != CLASS_CHAR_SEQUENCE) {
         if (type != Integer.TYPE && type != Integer.class) {
            if (type != Long.TYPE && type != Long.class) {
               if (type != Double.TYPE && type != Double.class) {
                  if (type != Boolean.TYPE && type != Boolean.class) {
                     if (type == BigInteger.class && (isCreator || isVisible)) {
                        creators.addBigIntegerCreator(ctor, isCreator);
                     }

                     if (type == BigDecimal.class && (isCreator || isVisible)) {
                        creators.addBigDecimalCreator(ctor, isCreator);
                     }

                     if (isCreator) {
                        creators.addDelegatingCreator(ctor, isCreator, (SettableBeanProperty[])null, 0);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     if (isCreator || isVisible) {
                        creators.addBooleanCreator(ctor, isCreator);
                     }

                     return true;
                  }
               } else {
                  if (isCreator || isVisible) {
                     creators.addDoubleCreator(ctor, isCreator);
                  }

                  return true;
               }
            } else {
               if (isCreator || isVisible) {
                  creators.addLongCreator(ctor, isCreator);
               }

               return true;
            }
         } else {
            if (isCreator || isVisible) {
               creators.addIntCreator(ctor, isCreator);
            }

            return true;
         }
      } else {
         if (isCreator || isVisible) {
            creators.addStringCreator(ctor, isCreator);
         }

         return true;
      }
   }

   protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, JacksonInject.Value injectable) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      PropertyMetadata metadata;
      PropertyName wrapperName;
      if (intr == null) {
         metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
         wrapperName = null;
      } else {
         Boolean b = intr.hasRequiredMarker(param);
         String desc = intr.findPropertyDescription(param);
         Integer idx = intr.findPropertyIndex(param);
         String def = intr.findPropertyDefaultValue(param);
         metadata = PropertyMetadata.construct(b, desc, idx, def);
         wrapperName = intr.findWrapperName(param);
      }

      JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
      BeanProperty.Std property = new BeanProperty.Std(name, type, wrapperName, param, metadata);
      TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
      if (typeDeser == null) {
         typeDeser = this.findTypeDeserializer(config, type);
      }

      metadata = this._getSetterInfo(config, property, metadata);
      SettableBeanProperty prop = CreatorProperty.construct(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectable, metadata);
      JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, param);
      if (deser == null) {
         deser = (JsonDeserializer)type.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, prop, type);
         prop = prop.withValueDeserializer(deser);
      }

      return prop;
   }

   private PropertyMetadata _getSetterInfo(MapperConfig<?> config, BeanProperty prop, PropertyMetadata metadata) {
      AnnotationIntrospector intr = config.getAnnotationIntrospector();
      boolean needMerge = true;
      Nulls valueNulls = null;
      Nulls contentNulls = null;
      AnnotatedMember prim = prop.getMember();
      if (prim != null) {
         if (intr != null) {
            JsonSetter.Value setterInfo = intr.findSetterInfo(prim);
            if (setterInfo != null) {
               valueNulls = setterInfo.nonDefaultValueNulls();
               contentNulls = setterInfo.nonDefaultContentNulls();
            }
         }

         if (needMerge || valueNulls == null || contentNulls == null) {
            ConfigOverride co = config.getConfigOverride(prop.getType().getRawClass());
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

   public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JavaType elemType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)elemType.getValueHandler();
      TypeDeserializer elemTypeDeser = (TypeDeserializer)elemType.getTypeHandler();
      if (elemTypeDeser == null) {
         elemTypeDeser = this.findTypeDeserializer(config, elemType);
      }

      JsonDeserializer<?> deser = this._findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
      if (deser == null) {
         if (contentDeser == null) {
            if (elemType.isPrimitive()) {
               deser = PrimitiveArrayDeserializers.forType(elemType.getRawClass());
            } else if (elemType.hasRawClass(String.class)) {
               deser = StringArrayDeserializer.instance;
            }
         }

         if (deser == null) {
            deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
         }
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
         }
      }

      return deser;
   }

   public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      DeserializationConfig config = ctxt.getConfig();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
      if (deser == null) {
         Class<?> collectionClass = type.getRawClass();
         if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
            deser = new EnumSetDeserializer(contentType, (JsonDeserializer)null);
         }
      }

      if (deser == null) {
         if (type.isInterface() || type.isAbstract()) {
            CollectionType implType = this._mapAbstractCollectionType(type, config);
            if (implType != null) {
               type = implType;
               beanDesc = config.introspectForCreation(implType);
            }
         }

         if (deser == null) {
            ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
            if (!inst.canCreateUsingDefault()) {
               if (type.hasRawClass(ArrayBlockingQueue.class)) {
                  return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
               }

               JsonDeserializer<?> var11 = JavaUtilCollectionsDeserializers.findForCollection(ctxt, type);
               if (var11 != null) {
                  return var11;
               }
            }

            if (contentType.hasRawClass(String.class)) {
               deser = new StringCollectionDeserializer(type, contentDeser, inst);
            } else {
               deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
            }
         }
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
         }
      }

      return deser;
   }

   protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
      Class<?> collectionClass = BasicDeserializerFactory.ContainerDefaultMappings.findCollectionFallback(type);
      return collectionClass != null ? (CollectionType)config.getTypeFactory().constructSpecializedType(type, collectionClass, true) : null;
   }

   public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      DeserializationConfig config = ctxt.getConfig();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
         }
      }

      return deser;
   }

   public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JavaType keyType = type.getKeyType();
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
      if (deser == null) {
         Class<?> mapClass = type.getRawClass();
         if (EnumMap.class.isAssignableFrom(mapClass)) {
            ValueInstantiator inst;
            if (mapClass == EnumMap.class) {
               inst = null;
            } else {
               inst = this.findValueInstantiator(ctxt, beanDesc);
            }

            if (!keyType.isEnumImplType()) {
               throw new IllegalArgumentException("Cannot construct EnumMap; generic (key) type not available");
            }

            deser = new EnumMapDeserializer(type, inst, (KeyDeserializer)null, contentDeser, contentTypeDeser, (NullValueProvider)null);
         }

         if (deser == null) {
            if (!type.isInterface() && !type.isAbstract()) {
               deser = JavaUtilCollectionsDeserializers.findForMap(ctxt, type);
               if (deser != null) {
                  return deser;
               }
            } else {
               MapType implType = this._mapAbstractMapType(type, config);
               if (implType != null) {
                  type = implType;
                  mapClass = implType.getRawClass();
                  beanDesc = config.introspectForCreation(implType);
               }
            }

            if (deser == null) {
               ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
               MapDeserializer md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
               JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
               Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForDeserialization();
               md.setIgnorableProperties(ignored);
               JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(Map.class, beanDesc.getClassInfo());
               Set<String> included = inclusions == null ? null : inclusions.getIncluded();
               md.setIncludableProperties(included);
               deser = md;
            }
         }
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
         }
      }

      return deser;
   }

   protected MapType _mapAbstractMapType(JavaType type, DeserializationConfig config) {
      Class<?> mapClass = BasicDeserializerFactory.ContainerDefaultMappings.findMapFallback(type);
      return mapClass != null ? (MapType)config.getTypeFactory().constructSpecializedType(type, mapClass, true) : null;
   }

   public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType keyType = type.getKeyType();
      JavaType contentType = type.getContentType();
      DeserializationConfig config = ctxt.getConfig();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
         }
      }

      return deser;
   }

   public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      Class<?> enumClass = type.getRawClass();
      JsonDeserializer<?> deser = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
      if (deser == null) {
         if (enumClass == Enum.class) {
            return AbstractDeserializer.constructForNonPOJO(beanDesc);
         }

         ValueInstantiator valueInstantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
         SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());

         for(AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (this._hasCreatorAnnotation(config, factory)) {
               if (factory.getParameterCount() == 0) {
                  deser = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
               } else {
                  Class<?> returnType = factory.getRawReturnType();
                  if (!returnType.isAssignableFrom(enumClass)) {
                     ctxt.reportBadDefinition(type, String.format("Invalid `@JsonCreator` annotated Enum factory method [%s]: needs to return compatible type", factory.toString()));
                  }

                  deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps, this.constructEnumResolver(enumClass, config, beanDesc));
               }
               break;
            }
         }

         if (deser == null) {
            deser = new EnumDeserializer(this.constructEnumResolver(enumClass, config, beanDesc), config.isEnabled((MapperFeature)MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS), this.constructEnumNamingStrategyResolver(config, beanDesc.getClassInfo()), EnumResolver.constructUsingToString(config, beanDesc.getClassInfo()));
         }
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
         }
      }

      return deser;
   }

   public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
      Class<? extends JsonNode> nodeClass = nodeType.getRawClass();
      JsonDeserializer<?> custom = this._findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
      return custom != null ? custom : JsonNodeDeserializer.getDeserializer(nodeClass);
   }

   public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
      JavaType contentType = type.getContentType();
      JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
      DeserializationConfig config = ctxt.getConfig();
      TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
      if (contentTypeDeser == null) {
         contentTypeDeser = this.findTypeDeserializer(config, contentType);
      }

      JsonDeserializer<?> deser = this._findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
      if (deser == null && type.isTypeOrSubTypeOf(AtomicReference.class)) {
         Class<?> rawType = type.getRawClass();
         ValueInstantiator inst;
         if (rawType == AtomicReference.class) {
            inst = null;
         } else {
            inst = this.findValueInstantiator(ctxt, beanDesc);
         }

         return new AtomicReferenceDeserializer(type, inst, contentTypeDeser, contentDeser);
      } else {
         if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
               deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
            }
         }

         return deser;
      }
   }

   public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
      BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
      AnnotatedClass ac = bean.getClassInfo();
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
      if (b == null) {
         b = config.getDefaultTyper(baseType);
         if (b == null) {
            return null;
         }
      }

      Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
      if (b.getDefaultImpl() == null && baseType.isAbstract()) {
         JavaType defaultType = this.mapAbstractType(config, baseType);
         if (defaultType != null && !defaultType.hasRawClass(baseType.getRawClass())) {
            b = b.withDefaultImpl(defaultType.getRawClass());
         }
      }

      try {
         return b.buildTypeDeserializer(config, baseType, subtypes);
      } catch (IllegalStateException | IllegalArgumentException e0) {
         throw InvalidDefinitionException.from((JsonParser)null, ClassUtil.exceptionMessage(e0), baseType).withCause(e0);
      }
   }

   protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
   }

   public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      BeanDescription beanDesc = config.introspectClassAnnotations(type);
      KeyDeserializer deser = this.findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
      if (deser == null && this._factoryConfig.hasKeyDeserializers()) {
         for(KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
            deser = d.findKeyDeserializer(type, config, beanDesc);
            if (deser != null) {
               break;
            }
         }
      }

      if (deser == null) {
         if (type.isEnumType()) {
            deser = this._createEnumKeyDeserializer(ctxt, type);
         } else {
            deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
         }
      }

      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyKeyDeserializer(config, type, deser);
         }
      }

      return deser;
   }

   private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      Class<?> enumClass = type.getRawClass();
      BeanDescription beanDesc = config.introspect(type);
      KeyDeserializer des = this.findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
      if (des != null) {
         return des;
      } else {
         JsonDeserializer<?> custom = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
         if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
         } else {
            JsonDeserializer<?> valueDesForKey = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
            if (valueDesForKey != null) {
               return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
            } else {
               EnumResolver enumRes = this.constructEnumResolver(enumClass, config, beanDesc);
               EnumResolver byEnumNamingResolver = this.constructEnumNamingStrategyResolver(config, beanDesc.getClassInfo());
               EnumResolver byToStringResolver = EnumResolver.constructUsingToString(config, beanDesc.getClassInfo());
               EnumResolver byIndexResolver = EnumResolver.constructUsingIndex(config, beanDesc.getClassInfo());
               Iterator var11 = beanDesc.getFactoryMethods().iterator();

               AnnotatedMethod factory;
               while(true) {
                  if (!var11.hasNext()) {
                     return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, byEnumNamingResolver, byToStringResolver, byIndexResolver);
                  }

                  factory = (AnnotatedMethod)var11.next();
                  if (this._hasCreatorAnnotation(config, factory)) {
                     int argCount = factory.getParameterCount();
                     if (argCount != 1) {
                        break;
                     }

                     Class<?> returnType = factory.getRawReturnType();
                     if (!returnType.isAssignableFrom(enumClass)) {
                        break;
                     }

                     if (factory.getRawParameterType(0) == String.class) {
                        if (config.canOverrideAccessModifiers()) {
                           ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }

                        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory, byEnumNamingResolver, byToStringResolver, byIndexResolver);
                     }
                  }
               }

               throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
         }
      }
   }

   public boolean hasExplicitDeserializerFor(DeserializationConfig config, Class<?> valueType) {
      while(valueType.isArray()) {
         valueType = valueType.getComponentType();
      }

      if (Enum.class.isAssignableFrom(valueType)) {
         return true;
      } else {
         String clsName = valueType.getName();
         if (clsName.startsWith("java.")) {
            if (Collection.class.isAssignableFrom(valueType)) {
               return true;
            } else if (Map.class.isAssignableFrom(valueType)) {
               return true;
            } else if (Number.class.isAssignableFrom(valueType)) {
               return NumberDeserializers.find(valueType, clsName) != null;
            } else if (!JdkDeserializers.hasDeserializerFor(valueType) && valueType != CLASS_STRING && valueType != Boolean.class && valueType != EnumMap.class && valueType != AtomicReference.class) {
               if (DateDeserializers.hasDeserializerFor(valueType)) {
                  return true;
               } else {
                  return false;
               }
            } else {
               return true;
            }
         } else if (!clsName.startsWith("com.fasterxml.")) {
            return OptionalHandlerFactory.instance.hasDeserializerFor(valueType);
         } else {
            return JsonNode.class.isAssignableFrom(valueType) || valueType == TokenBuffer.class;
         }
      }
   }

   public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
      if (b == null) {
         return this.findTypeDeserializer(config, baseType);
      } else {
         Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);

         try {
            return b.buildTypeDeserializer(config, baseType, subtypes);
         } catch (IllegalStateException | IllegalArgumentException e0) {
            throw InvalidDefinitionException.from((JsonParser)null, ClassUtil.exceptionMessage(e0), baseType).withCause(e0);
         }
      }
   }

   public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
      JavaType contentType = containerType.getContentType();
      if (b == null) {
         return this.findTypeDeserializer(config, contentType);
      } else {
         Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
         return b.buildTypeDeserializer(config, contentType, subtypes);
      }
   }

   public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      Class<?> rawType = type.getRawClass();
      if (rawType != CLASS_OBJECT && rawType != CLASS_SERIALIZABLE) {
         if (rawType != CLASS_STRING && rawType != CLASS_CHAR_SEQUENCE) {
            if (rawType == CLASS_ITERABLE) {
               TypeFactory tf = ctxt.getTypeFactory();
               JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
               JavaType elemType = tps != null && tps.length == 1 ? tps[0] : TypeFactory.unknownType();
               CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
               return this.createCollectionDeserializer(ctxt, ct, beanDesc);
            } else if (rawType == CLASS_MAP_ENTRY) {
               JavaType kt = type.containedTypeOrUnknown(0);
               JavaType vt = type.containedTypeOrUnknown(1);
               TypeDeserializer vts = (TypeDeserializer)vt.getTypeHandler();
               if (vts == null) {
                  vts = this.findTypeDeserializer(ctxt.getConfig(), vt);
               }

               JsonDeserializer<Object> valueDeser = (JsonDeserializer)vt.getValueHandler();
               KeyDeserializer keyDes = (KeyDeserializer)kt.getValueHandler();
               return new MapEntryDeserializer(type, keyDes, valueDeser, vts);
            } else {
               String clsName = rawType.getName();
               if (rawType.isPrimitive() || clsName.startsWith("java.")) {
                  JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
                  if (deser == null) {
                     deser = DateDeserializers.find(rawType, clsName);
                  }

                  if (deser != null) {
                     return deser;
                  }
               }

               if (rawType == TokenBuffer.class) {
                  return new TokenBufferDeserializer();
               } else {
                  JsonDeserializer<?> deser = this.findOptionalStdDeserializer(ctxt, type, beanDesc);
                  return deser != null ? deser : JdkDeserializers.find(ctxt, rawType, clsName);
               }
            }
         } else {
            return StringDeserializer.instance;
         }
      } else {
         DeserializationConfig config = ctxt.getConfig();
         JavaType lt;
         JavaType mt;
         if (this._factoryConfig.hasAbstractTypeResolvers()) {
            lt = this._findRemappedType(config, List.class);
            mt = this._findRemappedType(config, Map.class);
         } else {
            mt = null;
            lt = null;
         }

         return new UntypedObjectDeserializer(lt, mt);
      }
   }

   protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
      JavaType type = this.mapAbstractType(config, config.constructType(rawType));
      return type != null && !type.hasRawClass(rawType) ? type : null;
   }

   protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
      for(Deserializers d : this._factoryConfig.deserializers()) {
         JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
         if (deser != null) {
            return deser;
         }
      }

      return null;
   }

   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object deserDef = intr.findDeserializer(ann);
         if (deserDef != null) {
            return ctxt.deserializerInstance(ann, deserDef);
         }
      }

      return null;
   }

   protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object deserDef = intr.findKeyDeserializer(ann);
         if (deserDef != null) {
            return ctxt.keyDeserializerInstance(ann, deserDef);
         }
      }

      return null;
   }

   protected JsonDeserializer<Object> findContentDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr != null) {
         Object deserDef = intr.findContentDeserializer(ann);
         if (deserDef != null) {
            return ctxt.deserializerInstance(ann, deserDef);
         }
      }

      return null;
   }

   protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr == null) {
         return type;
      } else {
         if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null) {
               Object kdDef = intr.findKeyDeserializer(member);
               KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
               if (kd != null) {
                  type = ((MapLikeType)type).withKeyValueHandler(kd);
                  keyType = type.getKeyType();
               }
            }
         }

         if (type.hasContentType()) {
            Object cdDef = intr.findContentDeserializer(member);
            JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
               type = type.withContentValueHandler(cd);
            }

            TypeDeserializer contentTypeDeser = this.findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
            if (contentTypeDeser != null) {
               type = type.withContentTypeHandler(contentTypeDeser);
            }
         }

         TypeDeserializer valueTypeDeser = this.findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
         if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
         }

         JavaType var8 = intr.refineDeserializationType(ctxt.getConfig(), member, type);
         return var8;
      }
   }

   protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, BeanDescription beanDesc) {
      AnnotatedMember jvAcc = beanDesc.findJsonValueAccessor();
      if (jvAcc != null) {
         if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(jvAcc.getMember(), config.isEnabled((MapperFeature)MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
         }

         return EnumResolver.constructUsingMethod(config, beanDesc.getClassInfo(), jvAcc);
      } else {
         return EnumResolver.constructFor(config, beanDesc.getClassInfo());
      }
   }

   protected EnumResolver constructEnumNamingStrategyResolver(DeserializationConfig config, AnnotatedClass annotatedClass) {
      Object namingDef = config.getAnnotationIntrospector().findEnumNamingStrategy(config, annotatedClass);
      EnumNamingStrategy enumNamingStrategy = EnumNamingStrategyFactory.createEnumNamingStrategyInstance(namingDef, config.canOverrideAccessModifiers(), config.getEnumNamingStrategy());
      return enumNamingStrategy == null ? null : EnumResolver.constructUsingEnumNamingStrategy(config, annotatedClass, enumNamingStrategy);
   }

   protected boolean _hasCreatorAnnotation(MapperConfig<?> config, Annotated ann) {
      AnnotationIntrospector intr = config.getAnnotationIntrospector();
      if (intr == null) {
         return false;
      } else {
         JsonCreator.Mode mode = intr.findCreatorAnnotation(config, ann);
         return mode != null && mode != JsonCreator.Mode.DISABLED;
      }
   }

   protected static class ContainerDefaultMappings {
      static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
      static final HashMap<String, Class<? extends Map>> _mapFallbacks;

      public static Class<?> findCollectionFallback(JavaType type) {
         return (Class)_collectionFallbacks.get(type.getRawClass().getName());
      }

      public static Class<?> findMapFallback(JavaType type) {
         return (Class)_mapFallbacks.get(type.getRawClass().getName());
      }

      static {
         HashMap<String, Class<? extends Collection>> fallbacks = new HashMap();
         Class<? extends Collection> DEFAULT_LIST = ArrayList.class;
         Class<? extends Collection> DEFAULT_SET = HashSet.class;
         fallbacks.put(Collection.class.getName(), DEFAULT_LIST);
         fallbacks.put(List.class.getName(), DEFAULT_LIST);
         fallbacks.put(Set.class.getName(), DEFAULT_SET);
         fallbacks.put(SortedSet.class.getName(), TreeSet.class);
         fallbacks.put(Queue.class.getName(), LinkedList.class);
         fallbacks.put(AbstractList.class.getName(), DEFAULT_LIST);
         fallbacks.put(AbstractSet.class.getName(), DEFAULT_SET);
         fallbacks.put(Deque.class.getName(), LinkedList.class);
         fallbacks.put(NavigableSet.class.getName(), TreeSet.class);
         fallbacks.put("java.util.SequencedCollection", DEFAULT_LIST);
         fallbacks.put("java.util.SequencedSet", LinkedHashSet.class);
         _collectionFallbacks = fallbacks;
         fallbacks = new HashMap();
         DEFAULT_LIST = LinkedHashMap.class;
         fallbacks.put(Map.class.getName(), DEFAULT_LIST);
         fallbacks.put(AbstractMap.class.getName(), DEFAULT_LIST);
         fallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
         fallbacks.put(SortedMap.class.getName(), TreeMap.class);
         fallbacks.put(NavigableMap.class.getName(), TreeMap.class);
         fallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
         fallbacks.put("java.util.SequencedMap", LinkedHashMap.class);
         _mapFallbacks = fallbacks;
      }
   }
}
