package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.impl.ErrorThrowingDeserializer;
import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.deser.impl.UnsupportedTypeDeserializer;
import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.SubTypeValidator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Class<?>[] INIT_CAUSE_PARAMS = new Class[]{Throwable.class};
   public static final BeanDeserializerFactory instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());

   public BeanDeserializerFactory(DeserializerFactoryConfig config) {
      super(config);
   }

   public DeserializerFactory withConfig(DeserializerFactoryConfig config) {
      if (this._factoryConfig == config) {
         return this;
      } else {
         ClassUtil.verifyMustOverride(BeanDeserializerFactory.class, this, "withConfig");
         return new BeanDeserializerFactory(config);
      }
   }

   public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      JsonDeserializer<?> deser = this._findCustomBeanDeserializer(type, config, beanDesc);
      if (deser == null) {
         if (type.isThrowable()) {
            return this.buildThrowableDeserializer(ctxt, type, beanDesc);
         } else {
            if (type.isAbstract() && !type.isPrimitive() && !type.isEnumType()) {
               JavaType concreteType = this.materializeAbstractType(ctxt, type, beanDesc);
               if (concreteType != null) {
                  beanDesc = config.introspect(concreteType);
                  return this.buildBeanDeserializer(ctxt, concreteType, beanDesc);
               }
            }

            deser = this.findStdDeserializer(ctxt, type, beanDesc);
            if (deser != null) {
               return deser;
            } else if (!this.isPotentialBeanType(type.getRawClass())) {
               return null;
            } else {
               this._validateSubType(ctxt, type, beanDesc);
               deser = this._findUnsupportedTypeDeserializer(ctxt, type, beanDesc);
               return deser != null ? deser : this.buildBeanDeserializer(ctxt, type, beanDesc);
            }
         }
      } else {
         if (this._factoryConfig.hasDeserializerModifiers()) {
            for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
               deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
            }
         }

         return deser;
      }
   }

   public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription valueBeanDesc, Class<?> builderClass) throws JsonMappingException {
      JavaType builderType;
      if (ctxt.isEnabled(MapperFeature.INFER_BUILDER_TYPE_BINDINGS)) {
         builderType = ctxt.getTypeFactory().constructParametricType(builderClass, valueType.getBindings());
      } else {
         builderType = ctxt.constructType(builderClass);
      }

      BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType, valueBeanDesc);
      return this.buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
   }

   protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      JsonDeserializer<?> deser = this.findDefaultDeserializer(ctxt, type, beanDesc);
      if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
         }
      }

      return deser;
   }

   protected JsonDeserializer<Object> _findUnsupportedTypeDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      String errorMsg = BeanUtil.checkUnsupportedType(ctxt.getConfig(), type);
      return errorMsg != null && ctxt.getConfig().findMixInClassFor(type.getRawClass()) == null ? new UnsupportedTypeDeserializer(type, errorMsg) : null;
   }

   protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      for(AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
         JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
         if (concrete != null) {
            return concrete;
         }
      }

      return null;
   }

   public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      ValueInstantiator valueInstantiator;
      try {
         valueInstantiator = this.findValueInstantiator(ctxt, beanDesc);
      } catch (NoClassDefFoundError error) {
         return new ErrorThrowingDeserializer(error);
      } catch (IllegalArgumentException e0) {
         throw InvalidDefinitionException.from((JsonParser)ctxt.getParser(), ClassUtil.exceptionMessage(e0), beanDesc, (BeanPropertyDefinition)null).withCause(e0);
      }

      BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
      builder.setValueInstantiator(valueInstantiator);
      this.addBeanProps(ctxt, beanDesc, builder);
      this.addObjectIdReader(ctxt, beanDesc, builder);
      this.addBackReferenceProperties(ctxt, beanDesc, builder);
      this.addInjectables(ctxt, beanDesc, builder);
      DeserializationConfig config = ctxt.getConfig();
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            builder = mod.updateBuilder(config, beanDesc, builder);
         }
      }

      JsonDeserializer<?> deserializer;
      if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
         deserializer = builder.buildAbstract();
      } else {
         deserializer = builder.build();
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
         }
      }

      return deserializer;
   }

   protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc) throws JsonMappingException {
      ValueInstantiator valueInstantiator;
      try {
         valueInstantiator = this.findValueInstantiator(ctxt, builderDesc);
      } catch (NoClassDefFoundError error) {
         return new ErrorThrowingDeserializer(error);
      } catch (IllegalArgumentException e) {
         throw InvalidDefinitionException.from((JsonParser)ctxt.getParser(), ClassUtil.exceptionMessage(e), builderDesc, (BeanPropertyDefinition)null);
      }

      DeserializationConfig config = ctxt.getConfig();
      BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, builderDesc);
      builder.setValueInstantiator(valueInstantiator);
      this.addBeanProps(ctxt, builderDesc, builder);
      this.addObjectIdReader(ctxt, builderDesc, builder);
      this.addBackReferenceProperties(ctxt, builderDesc, builder);
      this.addInjectables(ctxt, builderDesc, builder);
      JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
      String buildMethodName = builderConfig == null ? "build" : builderConfig.buildMethodName;
      AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, (Class[])null);
      if (buildMethod != null && config.canOverrideAccessModifiers()) {
         ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled((MapperFeature)MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
      }

      builder.setPOJOBuilder(buildMethod, builderConfig);
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            builder = mod.updateBuilder(config, builderDesc, builder);
         }
      }

      JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deserializer = mod.modifyDeserializer(config, builderDesc, deserializer);
         }
      }

      return deserializer;
   }

   protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
      if (objectIdInfo != null) {
         Class<?> implClass = objectIdInfo.getGeneratorType();
         ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
         JavaType idType;
         SettableBeanProperty idProp;
         ObjectIdGenerator<?> gen;
         if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            PropertyName propName = objectIdInfo.getPropertyName();
            idProp = builder.findProperty(propName);
            if (idProp == null) {
               throw new IllegalArgumentException(String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.getTypeDescription(beanDesc.getType()), ClassUtil.name(propName)));
            }

            idType = idProp.getType();
            gen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
         } else {
            JavaType type = ctxt.constructType(implClass);
            idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            idProp = null;
            gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
         }

         JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
         builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, deser, idProp, resolver));
      }
   }

   public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
      builder.setValueInstantiator(this.findValueInstantiator(ctxt, beanDesc));
      this.addBeanProps(ctxt, beanDesc, builder);
      Iterator<SettableBeanProperty> it = builder.getProperties();

      while(it.hasNext()) {
         SettableBeanProperty prop = (SettableBeanProperty)it.next();
         if ("setCause".equals(prop.getMember().getName())) {
            it.remove();
            break;
         }
      }

      AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
      if (am != null) {
         SettableBeanProperty causeCreatorProp = builder.findProperty(PropertyName.construct("cause"));
         if (causeCreatorProp instanceof CreatorProperty) {
            ((CreatorProperty)causeCreatorProp).setFallbackSetter((SettableBeanProperty)null);
         } else {
            String name = "cause";
            PropertyNamingStrategy pts = config.getPropertyNamingStrategy();
            if (pts != null) {
               name = pts.nameForSetterMethod(config, am, "cause");
            }

            SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName(name));
            SettableBeanProperty prop = this.constructSettableProperty(ctxt, beanDesc, propDef, am.getParameterType(0));
            if (prop != null) {
               builder.addOrReplaceProperty(prop, true);
            }
         }
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            builder = mod.updateBuilder(config, beanDesc, builder);
         }
      }

      JsonDeserializer<?> deserializer = builder.build();
      if (deserializer instanceof BeanDeserializer) {
         deserializer = ThrowableDeserializer.construct(ctxt, (BeanDeserializer)deserializer);
      }

      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
         }
      }

      return deserializer;
   }

   protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc) {
      return new BeanDeserializerBuilder(beanDesc, ctxt);
   }

   protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      ValueInstantiator valueInstantiator = builder.getValueInstantiator();
      SettableBeanProperty[] creatorProps = valueInstantiator != null ? valueInstantiator.getFromObjectArguments(ctxt.getConfig()) : null;
      boolean hasCreatorProps = creatorProps != null;
      JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
      Set<String> ignored;
      if (ignorals != null) {
         boolean ignoreAny = ignorals.getIgnoreUnknown();
         builder.setIgnoreUnknownProperties(ignoreAny);
         ignored = ignorals.findIgnoredForDeserialization();

         for(String propName : ignored) {
            builder.addIgnorable(propName);
         }
      } else {
         ignored = Collections.emptySet();
      }

      JsonIncludeProperties.Value inclusions = ctxt.getConfig().getDefaultPropertyInclusions(beanDesc.getBeanClass(), beanDesc.getClassInfo());
      Set<String> included = null;
      if (inclusions != null) {
         included = inclusions.getIncluded();
         if (included != null) {
            for(String propName : included) {
               builder.addIncludable(propName);
            }
         }
      }

      SettableAnyProperty anySetter = this._resolveAnySetter(ctxt, beanDesc, creatorProps);
      if (anySetter != null) {
         builder.setAnySetter(anySetter);
      } else {
         Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
         if (ignored2 != null) {
            for(String propName : ignored2) {
               builder.addIgnorable(propName);
            }
         }
      }

      boolean useGettersAsSetters = ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS);
      List<BeanPropertyDefinition> propDefs = this.filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored, included);
      if (this._factoryConfig.hasDeserializerModifiers()) {
         for(BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
            propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
         }
      }

      for(BeanPropertyDefinition propDef : propDefs) {
         SettableBeanProperty prop = null;
         if (propDef.hasSetter()) {
            AnnotatedMethod setter = propDef.getSetter();
            JavaType propertyType = setter.getParameterType(0);
            prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
         } else if (propDef.hasField()) {
            AnnotatedField field = propDef.getField();
            JavaType propertyType = field.getType();
            prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
         } else {
            AnnotatedMethod getter = propDef.getGetter();
            if (getter != null) {
               if (useGettersAsSetters && this._isSetterlessType(getter.getRawType())) {
                  if (!builder.hasIgnorable(propDef.getName())) {
                     prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                  }
               } else if (!propDef.hasConstructorParameter()) {
                  PropertyMetadata md = propDef.getMetadata();
                  if (md.getMergeInfo() != null) {
                     prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                  }
               }
            }
         }

         if (hasCreatorProps && propDef.hasConstructorParameter()) {
            String name = propDef.getName();
            CreatorProperty cprop = null;

            for(SettableBeanProperty cp : creatorProps) {
               if (name.equals(cp.getName()) && cp instanceof CreatorProperty) {
                  cprop = (CreatorProperty)cp;
                  break;
               }
            }

            if (cprop == null) {
               List<String> n = new ArrayList();

               for(SettableBeanProperty cp : creatorProps) {
                  n.add(cp.getName());
               }

               ctxt.reportBadPropertyDefinition(beanDesc, propDef, "Could not find creator property with name %s (known Creator properties: %s)", ClassUtil.name(name), n);
            } else {
               if (prop != null) {
                  cprop.setFallbackSetter(prop);
               }

               Class<?>[] views = propDef.findViews();
               if (views == null) {
                  views = beanDesc.findDefaultViews();
               }

               cprop.setViews(views);
               builder.addCreatorProperty(cprop);
            }
         } else if (prop != null) {
            Class<?>[] views = propDef.findViews();
            if (views == null) {
               views = beanDesc.findDefaultViews();
            }

            prop.setViews(views);
            builder.addProperty(prop);
         }
      }

   }

   private SettableAnyProperty _resolveAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, SettableBeanProperty[] creatorProps) throws JsonMappingException {
      if (creatorProps != null) {
         for(SettableBeanProperty prop : creatorProps) {
            AnnotatedMember member = prop.getMember();
            if (member != null && Boolean.TRUE.equals(ctxt.getAnnotationIntrospector().hasAnySetter(member))) {
               return this.constructAnySetter(ctxt, beanDesc, member);
            }
         }
      }

      AnnotatedMember anySetter = beanDesc.findAnySetterAccessor();
      return anySetter != null ? this.constructAnySetter(ctxt, beanDesc, anySetter) : null;
   }

   private boolean _isSetterlessType(Class<?> rawType) {
      return Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType);
   }

   /** @deprecated */
   @Deprecated
   protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored) throws JsonMappingException {
      return this.filterBeanProps(ctxt, beanDesc, builder, propDefsIn, ignored, (Set)null);
   }

   protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored, Set<String> included) {
      ArrayList<BeanPropertyDefinition> result = new ArrayList(Math.max(4, propDefsIn.size()));
      HashMap<Class<?>, Boolean> ignoredTypes = new HashMap();

      for(BeanPropertyDefinition property : propDefsIn) {
         String name = property.getName();
         if (!IgnorePropertiesUtil.shouldIgnore(name, ignored, included)) {
            if (!property.hasConstructorParameter()) {
               Class<?> rawPropertyType = property.getRawPrimaryType();
               if (rawPropertyType != null && this.isIgnorableType(ctxt.getConfig(), property, rawPropertyType, ignoredTypes)) {
                  builder.addIgnorable(name);
                  continue;
               }
            }

            result.add(property);
         }
      }

      return result;
   }

   protected void addBackReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      List<BeanPropertyDefinition> refProps = beanDesc.findBackReferences();
      if (refProps != null) {
         for(BeanPropertyDefinition refProp : refProps) {
            if (beanDesc.isRecordType()) {
               ctxt.reportBadTypeDefinition(beanDesc, "Cannot add back-reference to a `java.lang.Record` type (property '%s')", refProp.getName());
            }

            String refName = refProp.findReferenceName();
            builder.addBackReferenceProperty(refName, this.constructSettableProperty(ctxt, beanDesc, refProp, refProp.getPrimaryType()));
         }
      }

   }

   /** @deprecated */
   @Deprecated
   protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      this.addBackReferenceProperties(ctxt, beanDesc, builder);
   }

   protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
      Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
      if (raw != null) {
         AnnotationIntrospector introspector = ctxt.getAnnotationIntrospector();

         for(Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
            AnnotatedMember m = (AnnotatedMember)entry.getValue();
            JacksonInject.Value injectableValue = introspector.findInjectableValue(m);
            Boolean optional;
            Boolean useInput;
            if (injectableValue == null) {
               useInput = null;
               optional = null;
            } else {
               optional = injectableValue.getOptional();
               useInput = injectableValue.getUseInput();
            }

            builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey(), optional, useInput);
         }
      }

   }

   protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator) throws JsonMappingException {
      boolean isField = mutator instanceof AnnotatedField;
      boolean isParameter = mutator instanceof AnnotatedParameter;
      int parameterIndex = -1;
      BeanProperty prop;
      JavaType keyType;
      JavaType valueType;
      if (mutator instanceof AnnotatedMethod) {
         AnnotatedMethod am = (AnnotatedMethod)mutator;
         keyType = am.getParameterType(0);
         JavaType valueType = am.getParameterType(1);
         valueType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, valueType);
         prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), valueType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      } else if (isField) {
         AnnotatedField af = (AnnotatedField)mutator;
         JavaType fieldType = af.getType();
         if (!fieldType.isMapLikeType()) {
            if (!fieldType.hasRawClass(JsonNode.class) && !fieldType.hasRawClass(ObjectNode.class)) {
               return (SettableAnyProperty)ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unsupported type for any-setter: %s -- only support `Map`s, `JsonNode` and `ObjectNode` ", ClassUtil.getTypeDescription(fieldType)));
            }

            fieldType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, fieldType);
            valueType = ctxt.constructType(JsonNode.class);
            BeanProperty prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), fieldType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
            return SettableAnyProperty.constructForJsonNodeField(ctxt, prop, mutator, valueType, ctxt.findRootValueDeserializer(valueType));
         }

         fieldType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, fieldType);
         keyType = fieldType.getKeyType();
         valueType = fieldType.getContentType();
         prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), fieldType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      } else {
         if (!isParameter) {
            return (SettableAnyProperty)ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unrecognized mutator type for any-setter: %s", ClassUtil.nameOf(mutator.getClass())));
         }

         AnnotatedParameter af = (AnnotatedParameter)mutator;
         JavaType paramType = af.getType();
         parameterIndex = af.getIndex();
         if (!paramType.isMapLikeType()) {
            if (!paramType.hasRawClass(JsonNode.class) && !paramType.hasRawClass(ObjectNode.class)) {
               return (SettableAnyProperty)ctxt.reportBadDefinition(beanDesc.getType(), String.format("Unsupported type for any-setter: %s -- only support `Map`s, `JsonNode` and `ObjectNode` ", ClassUtil.getTypeDescription(paramType)));
            }

            paramType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, paramType);
            valueType = ctxt.constructType(JsonNode.class);
            BeanProperty prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), paramType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
            return SettableAnyProperty.constructForJsonNodeParameter(ctxt, prop, mutator, valueType, ctxt.findRootValueDeserializer(valueType), parameterIndex);
         }

         paramType = this.resolveMemberAndTypeAnnotations(ctxt, mutator, paramType);
         keyType = paramType.getKeyType();
         valueType = paramType.getContentType();
         prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), paramType, (PropertyName)null, mutator, PropertyMetadata.STD_OPTIONAL);
      }

      KeyDeserializer keyDeser = this.findKeyDeserializerFromAnnotation(ctxt, mutator);
      if (keyDeser == null) {
         keyDeser = (KeyDeserializer)keyType.getValueHandler();
      }

      if (keyDeser == null) {
         keyDeser = ctxt.findKeyDeserializer(keyType, prop);
      } else if (keyDeser instanceof ContextualKeyDeserializer) {
         keyDeser = ((ContextualKeyDeserializer)keyDeser).createContextual(ctxt, prop);
      }

      JsonDeserializer<Object> deser = this.findContentDeserializerFromAnnotation(ctxt, mutator);
      if (deser == null) {
         deser = (JsonDeserializer)valueType.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, prop, valueType);
      }

      TypeDeserializer typeDeser = (TypeDeserializer)valueType.getTypeHandler();
      if (isField) {
         return SettableAnyProperty.constructForMapField(ctxt, prop, mutator, valueType, keyDeser, deser, typeDeser);
      } else {
         return isParameter ? SettableAnyProperty.constructForMapParameter(ctxt, prop, mutator, valueType, keyDeser, deser, typeDeser, parameterIndex) : SettableAnyProperty.constructForMethod(ctxt, prop, mutator, valueType, keyDeser, deser, typeDeser);
      }
   }

   protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0) throws JsonMappingException {
      AnnotatedMember mutator = propDef.getNonConstructorMutator();
      if (mutator == null) {
         ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available");
      }

      JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
      TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
      SettableBeanProperty prop;
      if (mutator instanceof AnnotatedMethod) {
         prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
      } else {
         prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
      }

      JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, mutator);
      if (deser == null) {
         deser = (JsonDeserializer)type.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, prop, type);
         prop = prop.withValueDeserializer(deser);
      }

      AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
      if (ref != null && ref.isManagedReference()) {
         prop.setManagedReferenceName(ref.getName());
      }

      ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
      if (objectIdInfo != null) {
         prop.setObjectIdInfo(objectIdInfo);
      }

      return prop;
   }

   protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef) throws JsonMappingException {
      AnnotatedMethod getter = propDef.getGetter();
      JavaType type = this.resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
      TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
      SettableBeanProperty prop = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
      JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, getter);
      if (deser == null) {
         deser = (JsonDeserializer)type.getValueHandler();
      }

      if (deser != null) {
         deser = ctxt.handlePrimaryContextualization(deser, prop, type);
         prop = prop.withValueDeserializer(deser);
      }

      return prop;
   }

   protected boolean isPotentialBeanType(Class<?> type) {
      String typeStr = ClassUtil.canBeABeanType(type);
      if (typeStr != null) {
         throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
      } else if (ClassUtil.isProxyType(type)) {
         throw new IllegalArgumentException("Cannot deserialize Proxy class " + type.getName() + " as a Bean");
      } else {
         typeStr = ClassUtil.isLocalType(type, true);
         if (typeStr != null) {
            throw new IllegalArgumentException("Cannot deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
         } else {
            return true;
         }
      }
   }

   protected boolean isIgnorableType(DeserializationConfig config, BeanPropertyDefinition propDef, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
      Boolean status = (Boolean)ignoredTypes.get(type);
      if (status != null) {
         return status;
      } else {
         if (type != String.class && !type.isPrimitive()) {
            status = config.getConfigOverride(type).getIsIgnoredType();
            if (status == null) {
               BeanDescription desc = config.introspectClassAnnotations(type);
               status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
               if (status == null) {
                  status = Boolean.FALSE;
               }
            }
         } else {
            status = Boolean.FALSE;
         }

         ignoredTypes.put(type, status);
         return status;
      }
   }

   protected void _validateSubType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      SubTypeValidator.instance().validateSubType(ctxt, type, beanDesc);
   }
}
