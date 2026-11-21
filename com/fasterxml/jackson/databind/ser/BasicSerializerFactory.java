package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
import com.fasterxml.jackson.databind.ser.impl.IteratorSerializer;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.AtomicReferenceSerializer;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.ByteBufferSerializer;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumSetSerializer;
import com.fasterxml.jackson.databind.ser.std.InetAddressSerializer;
import com.fasterxml.jackson.databind.ser.std.InetSocketAddressSerializer;
import com.fasterxml.jackson.databind.ser.std.IterableSerializer;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.ser.std.StdJdkSerializers;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.TimeZoneSerializer;
import com.fasterxml.jackson.databind.ser.std.ToEmptyObjectSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.TokenBufferSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BasicSerializerFactory extends SerializerFactory implements Serializable {
   protected static final HashMap<String, JsonSerializer<?>> _concrete;
   protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
   protected final SerializerFactoryConfig _factoryConfig;

   protected BasicSerializerFactory(SerializerFactoryConfig config) {
      this._factoryConfig = config == null ? new SerializerFactoryConfig() : config;
   }

   public SerializerFactoryConfig getFactoryConfig() {
      return this._factoryConfig;
   }

   public abstract SerializerFactory withConfig(SerializerFactoryConfig var1);

   public final SerializerFactory withAdditionalSerializers(Serializers additional) {
      return this.withConfig(this._factoryConfig.withAdditionalSerializers(additional));
   }

   public final SerializerFactory withAdditionalKeySerializers(Serializers additional) {
      return this.withConfig(this._factoryConfig.withAdditionalKeySerializers(additional));
   }

   public final SerializerFactory withSerializerModifier(BeanSerializerModifier modifier) {
      return this.withConfig(this._factoryConfig.withSerializerModifier(modifier));
   }

   public abstract JsonSerializer<Object> createSerializer(SerializerProvider var1, JavaType var2) throws JsonMappingException;

   public JsonSerializer<Object> createKeySerializer(SerializerProvider ctxt, JavaType keyType, JsonSerializer<Object> defaultImpl) throws JsonMappingException {
      SerializationConfig config = ctxt.getConfig();
      BeanDescription beanDesc = config.introspect(keyType);
      JsonSerializer<?> ser = null;
      if (this._factoryConfig.hasKeySerializers()) {
         for(Serializers serializers : this._factoryConfig.keySerializers()) {
            ser = serializers.findSerializer(config, keyType, beanDesc);
            if (ser != null) {
               break;
            }
         }
      }

      if (ser == null) {
         ser = this._findKeySerializer(ctxt, beanDesc.getClassInfo());
         if (ser == null) {
            ser = defaultImpl;
            if (defaultImpl == null) {
               ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
               if (ser == null) {
                  AnnotatedMember acc = beanDesc.findJsonKeyAccessor();
                  if (acc == null) {
                     acc = beanDesc.findJsonValueAccessor();
                  }

                  if (acc != null) {
                     JsonSerializer<?> delegate = this.createKeySerializer(ctxt, acc.getType(), defaultImpl);
                     if (config.canOverrideAccessModifiers()) {
                        ClassUtil.checkAndFixAccess(acc.getMember(), config.isEnabled((MapperFeature)MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                     }

                     ser = JsonValueSerializer.construct(config, acc, (TypeSerializer)null, delegate);
                  } else {
                     ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass(), beanDesc.getClassInfo());
                  }
               }
            }
         }
      }

      if (this._factoryConfig.hasSerializerModifiers()) {
         for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
            ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
         }
      }

      return ser;
   }

   /** @deprecated */
   @Deprecated
   public JsonSerializer<Object> createKeySerializer(SerializationConfig config, JavaType keyType, JsonSerializer<Object> defaultImpl) {
      BeanDescription beanDesc = config.introspect(keyType);
      JsonSerializer<?> ser = null;
      if (this._factoryConfig.hasKeySerializers()) {
         for(Serializers serializers : this._factoryConfig.keySerializers()) {
            ser = serializers.findSerializer(config, keyType, beanDesc);
            if (ser != null) {
               break;
            }
         }
      }

      if (ser == null) {
         ser = defaultImpl;
         if (defaultImpl == null) {
            ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
            if (ser == null) {
               ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass(), beanDesc.getClassInfo());
            }
         }
      }

      if (this._factoryConfig.hasSerializerModifiers()) {
         for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
            ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
         }
      }

      return ser;
   }

   public TypeSerializer createTypeSerializer(SerializationConfig config, JavaType baseType) {
      BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
      AnnotatedClass ac = bean.getClassInfo();
      AnnotationIntrospector ai = config.getAnnotationIntrospector();
      TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
      Collection<NamedType> subtypes = null;
      if (b == null) {
         b = config.getDefaultTyper(baseType);
      } else {
         subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, ac);
      }

      return b == null ? null : b.buildTypeSerializer(config, baseType, subtypes);
   }

   protected abstract Iterable<Serializers> customSerializers();

   protected final JsonSerializer<?> findSerializerByLookup(JavaType type, SerializationConfig config, BeanDescription beanDesc, boolean staticTyping) {
      Class<?> raw = type.getRawClass();
      String clsName = raw.getName();
      JsonSerializer<?> ser = (JsonSerializer)_concrete.get(clsName);
      if (ser == null) {
         Class<? extends JsonSerializer<?>> serClass = (Class)_concreteLazy.get(clsName);
         if (serClass != null) {
            return (JsonSerializer)ClassUtil.createInstance(serClass, false);
         }
      }

      return ser;
   }

   protected final JsonSerializer<?> findSerializerByAnnotations(SerializerProvider prov, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      Class<?> raw = type.getRawClass();
      if (JsonSerializable.class.isAssignableFrom(raw)) {
         return SerializableSerializer.instance;
      } else {
         AnnotatedMember valueAccessor = beanDesc.findJsonValueAccessor();
         if (valueAccessor != null) {
            if (prov.canOverrideAccessModifiers()) {
               ClassUtil.checkAndFixAccess(valueAccessor.getMember(), prov.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            }

            JavaType valueType = valueAccessor.getType();
            JsonSerializer<Object> valueSerializer = this.findSerializerFromAnnotation(prov, valueAccessor);
            if (valueSerializer == null) {
               valueSerializer = (JsonSerializer)valueType.getValueHandler();
            }

            TypeSerializer typeSerializer = (TypeSerializer)valueType.getTypeHandler();
            if (typeSerializer == null) {
               typeSerializer = this.createTypeSerializer(prov.getConfig(), valueType);
            }

            return JsonValueSerializer.construct(prov.getConfig(), valueAccessor, typeSerializer, valueSerializer);
         } else {
            return null;
         }
      }
   }

   protected final JsonSerializer<?> findSerializerByPrimaryType(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
      if (type.isEnumType()) {
         return this.buildEnumSerializer(prov.getConfig(), type, beanDesc);
      } else {
         Class<?> raw = type.getRawClass();
         JsonSerializer<?> ser = this.findOptionalStdSerializer(prov, type, beanDesc, staticTyping);
         if (ser != null) {
            return ser;
         } else if (Calendar.class.isAssignableFrom(raw)) {
            return CalendarSerializer.instance;
         } else if (Date.class.isAssignableFrom(raw)) {
            return DateSerializer.instance;
         } else if (Map.Entry.class.isAssignableFrom(raw)) {
            JavaType mapEntryType = type.findSuperType(Map.Entry.class);
            JavaType kt = mapEntryType.containedTypeOrUnknown(0);
            JavaType vt = mapEntryType.containedTypeOrUnknown(1);
            return this.buildMapEntrySerializer(prov, type, beanDesc, staticTyping, kt, vt);
         } else if (ByteBuffer.class.isAssignableFrom(raw)) {
            return new ByteBufferSerializer();
         } else if (InetAddress.class.isAssignableFrom(raw)) {
            return new InetAddressSerializer();
         } else if (InetSocketAddress.class.isAssignableFrom(raw)) {
            return new InetSocketAddressSerializer();
         } else if (TimeZone.class.isAssignableFrom(raw)) {
            return new TimeZoneSerializer();
         } else if (Charset.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
         } else if (Number.class.isAssignableFrom(raw)) {
            JsonFormat.Value format = beanDesc.findExpectedFormat();
            switch (format.getShape()) {
               case STRING:
                  return ToStringSerializer.instance;
               case OBJECT:
               case ARRAY:
                  return null;
               default:
                  return NumberSerializer.instance;
            }
         } else {
            return ClassLoader.class.isAssignableFrom(raw) ? new ToEmptyObjectSerializer(type) : null;
         }
      }
   }

   protected JsonSerializer<?> findOptionalStdSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
      return OptionalHandlerFactory.instance.findSerializer(prov.getConfig(), type, beanDesc);
   }

   protected final JsonSerializer<?> findSerializerByAddonType(SerializationConfig config, JavaType javaType, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
      Class<?> rawType = javaType.getRawClass();
      if (Iterator.class.isAssignableFrom(rawType)) {
         JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterator.class);
         JavaType vt = params != null && params.length == 1 ? params[0] : TypeFactory.unknownType();
         return this.buildIteratorSerializer(config, javaType, beanDesc, staticTyping, vt);
      } else if (!Iterable.class.isAssignableFrom(rawType)) {
         return CharSequence.class.isAssignableFrom(rawType) ? ToStringSerializer.instance : null;
      } else {
         JavaType[] params = config.getTypeFactory().findTypeParameters(javaType, Iterable.class);
         JavaType vt = params != null && params.length == 1 ? params[0] : TypeFactory.unknownType();
         return this.buildIterableSerializer(config, javaType, beanDesc, staticTyping, vt);
      }
   }

   protected JsonSerializer<Object> findSerializerFromAnnotation(SerializerProvider prov, Annotated a) throws JsonMappingException {
      Object serDef = prov.getAnnotationIntrospector().findSerializer(a);
      if (serDef == null) {
         return null;
      } else {
         JsonSerializer<Object> ser = prov.serializerInstance(a, serDef);
         return this.findConvertingSerializer(prov, a, ser);
      }
   }

   protected JsonSerializer<?> findConvertingSerializer(SerializerProvider prov, Annotated a, JsonSerializer<?> ser) throws JsonMappingException {
      Converter<Object, Object> conv = this.findConverter(prov, a);
      if (conv == null) {
         return ser;
      } else {
         JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
         return new StdDelegatingSerializer(conv, delegateType, ser);
      }
   }

   protected Converter<Object, Object> findConverter(SerializerProvider prov, Annotated a) throws JsonMappingException {
      Object convDef = prov.getAnnotationIntrospector().findSerializationConverter(a);
      return convDef == null ? null : prov.converterInstance(a, convDef);
   }

   protected JsonSerializer<?> buildContainerSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
      SerializationConfig config = prov.getConfig();
      if (!staticTyping && type.useStaticType() && (!type.isContainerType() || !type.getContentType().isJavaLangObject())) {
         staticTyping = true;
      }

      JavaType elementType = type.getContentType();
      TypeSerializer elementTypeSerializer = this.createTypeSerializer(config, elementType);
      if (elementTypeSerializer != null) {
         staticTyping = false;
      }

      JsonSerializer<Object> elementValueSerializer = this._findContentSerializer(prov, beanDesc.getClassInfo());
      if (type.isMapLikeType()) {
         MapLikeType mlt = (MapLikeType)type;
         JsonSerializer<Object> keySerializer = this._findKeySerializer(prov, beanDesc.getClassInfo());
         if (mlt instanceof MapType) {
            return this.buildMapSerializer(prov, (MapType)mlt, beanDesc, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
         } else {
            JsonSerializer<?> ser = null;
            MapLikeType mlType = (MapLikeType)type;

            for(Serializers serializers : this.customSerializers()) {
               ser = serializers.findMapLikeSerializer(config, mlType, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
               if (ser != null) {
                  break;
               }
            }

            if (ser == null) {
               ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            }

            if (ser != null && this._factoryConfig.hasSerializerModifiers()) {
               for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                  ser = mod.modifyMapLikeSerializer(config, mlType, beanDesc, ser);
               }
            }

            return ser;
         }
      } else if (!type.isCollectionLikeType()) {
         return type.isArrayType() ? this.buildArraySerializer(prov, (ArrayType)type, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer) : null;
      } else {
         CollectionLikeType clt = (CollectionLikeType)type;
         if (clt instanceof CollectionType) {
            return this.buildCollectionSerializer(prov, (CollectionType)clt, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
         } else {
            JsonSerializer<?> ser = null;
            CollectionLikeType clType = (CollectionLikeType)type;

            for(Serializers serializers : this.customSerializers()) {
               ser = serializers.findCollectionLikeSerializer(config, clType, beanDesc, elementTypeSerializer, elementValueSerializer);
               if (ser != null) {
                  break;
               }
            }

            if (ser == null) {
               ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            }

            if (ser != null && this._factoryConfig.hasSerializerModifiers()) {
               for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                  ser = mod.modifyCollectionLikeSerializer(config, clType, beanDesc, ser);
               }
            }

            return ser;
         }
      }
   }

   protected JsonSerializer<?> buildCollectionSerializer(SerializerProvider prov, CollectionType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
      SerializationConfig config = prov.getConfig();
      JsonSerializer<?> ser = null;

      for(Serializers serializers : this.customSerializers()) {
         ser = serializers.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
         if (ser != null) {
            break;
         }
      }

      if (ser == null) {
         ser = this.findSerializerByAnnotations(prov, type, beanDesc);
         if (ser == null) {
            JsonFormat.Value format = beanDesc.findExpectedFormat();
            if (format.getShape() == JsonFormat.Shape.OBJECT) {
               return null;
            }

            Class<?> raw = type.getRawClass();
            if (EnumSet.class.isAssignableFrom(raw)) {
               JavaType enumType = type.getContentType();
               if (!enumType.isEnumImplType()) {
                  enumType = null;
               }

               ser = this.buildEnumSetSerializer(enumType);
            } else {
               Class<?> elementRaw = type.getContentType().getRawClass();
               if (this.isIndexedList(raw)) {
                  if (elementRaw == String.class) {
                     if (ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                        ser = IndexedStringListSerializer.instance;
                     }
                  } else {
                     ser = this.buildIndexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                  }
               } else if (elementRaw == String.class && ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
                  ser = StringCollectionSerializer.instance;
               }

               if (ser == null) {
                  ser = this.buildCollectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
               }
            }
         }
      }

      if (this._factoryConfig.hasSerializerModifiers()) {
         for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
            ser = mod.modifyCollectionSerializer(config, type, beanDesc, ser);
         }
      }

      return ser;
   }

   protected boolean isIndexedList(Class<?> cls) {
      return RandomAccess.class.isAssignableFrom(cls);
   }

   public ContainerSerializer<?> buildIndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
      return new IndexedListSerializer(elemType, staticTyping, vts, valueSerializer);
   }

   public ContainerSerializer<?> buildCollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
      return new CollectionSerializer(elemType, staticTyping, vts, valueSerializer);
   }

   public JsonSerializer<?> buildEnumSetSerializer(JavaType enumType) {
      return new EnumSetSerializer(enumType);
   }

   protected JsonSerializer<?> buildMapSerializer(SerializerProvider prov, MapType type, BeanDescription beanDesc, boolean staticTyping, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
      JsonFormat.Value format = beanDesc.findExpectedFormat();
      if (format.getShape() == JsonFormat.Shape.OBJECT) {
         return null;
      } else {
         JsonSerializer<?> ser = null;
         SerializationConfig config = prov.getConfig();

         for(Serializers serializers : this.customSerializers()) {
            ser = serializers.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
               break;
            }
         }

         if (ser == null) {
            ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            if (ser == null) {
               Object filterId = this.findFilterId(config, beanDesc);
               JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
               Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForSerialization();
               JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(Map.class, beanDesc.getClassInfo());
               Set<String> included = inclusions == null ? null : inclusions.getIncluded();
               MapSerializer mapSer = MapSerializer.construct(ignored, included, type, staticTyping, elementTypeSerializer, keySerializer, elementValueSerializer, filterId);
               ser = this._checkMapContentInclusion(prov, beanDesc, mapSer);
            }
         }

         if (this._factoryConfig.hasSerializerModifiers()) {
            for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
               ser = mod.modifyMapSerializer(config, type, beanDesc, ser);
            }
         }

         return ser;
      }
   }

   protected MapSerializer _checkMapContentInclusion(SerializerProvider prov, BeanDescription beanDesc, MapSerializer mapSer) throws JsonMappingException {
      JavaType contentType = mapSer.getContentType();
      JsonInclude.Value inclV = this._findInclusionWithContent(prov, beanDesc, contentType, Map.class);
      JsonInclude.Include incl = inclV == null ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
      if (incl != JsonInclude.Include.USE_DEFAULTS && incl != JsonInclude.Include.ALWAYS) {
         boolean suppressNulls = true;
         Object valueToSuppress;
         switch (incl) {
            case NON_DEFAULT:
               valueToSuppress = BeanUtil.getDefaultValue(contentType);
               if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                  valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
               }
               break;
            case NON_ABSENT:
               valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null;
               break;
            case NON_EMPTY:
               valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
               break;
            case CUSTOM:
               valueToSuppress = prov.includeFilterInstance((BeanPropertyDefinition)null, inclV.getContentFilter());
               if (valueToSuppress != null) {
                  suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
               }
               break;
            case NON_NULL:
            default:
               valueToSuppress = null;
         }

         return mapSer.withContentInclusion(valueToSuppress, suppressNulls);
      } else {
         return !prov.isEnabled(SerializationFeature.WRITE_NULL_MAP_VALUES) ? mapSer.withContentInclusion((Object)null, true) : mapSer;
      }
   }

   protected JsonSerializer<?> buildMapEntrySerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType keyType, JavaType valueType) throws JsonMappingException {
      JsonFormat.Value formatOverride = prov.getDefaultPropertyFormat(Map.Entry.class);
      JsonFormat.Value formatFromAnnotation = beanDesc.findExpectedFormat();
      JsonFormat.Value format = JsonFormat.Value.merge(formatFromAnnotation, formatOverride);
      if (format.getShape() == JsonFormat.Shape.OBJECT) {
         return null;
      } else {
         MapEntrySerializer ser = new MapEntrySerializer(valueType, keyType, valueType, staticTyping, this.createTypeSerializer(prov.getConfig(), valueType), (BeanProperty)null);
         JavaType contentType = ser.getContentType();
         JsonInclude.Value inclV = this._findInclusionWithContent(prov, beanDesc, contentType, Map.Entry.class);
         JsonInclude.Include incl = inclV == null ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
         if (incl != JsonInclude.Include.USE_DEFAULTS && incl != JsonInclude.Include.ALWAYS) {
            boolean suppressNulls = true;
            Object valueToSuppress;
            switch (incl) {
               case NON_DEFAULT:
                  valueToSuppress = BeanUtil.getDefaultValue(contentType);
                  if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                     valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                  }
                  break;
               case NON_ABSENT:
                  valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null;
                  break;
               case NON_EMPTY:
                  valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
                  break;
               case CUSTOM:
                  valueToSuppress = prov.includeFilterInstance((BeanPropertyDefinition)null, inclV.getContentFilter());
                  if (valueToSuppress != null) {
                     suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
                  }
                  break;
               case NON_NULL:
               default:
                  valueToSuppress = null;
            }

            return ser.withContentInclusion(valueToSuppress, suppressNulls);
         } else {
            return ser;
         }
      }
   }

   protected JsonInclude.Value _findInclusionWithContent(SerializerProvider prov, BeanDescription beanDesc, JavaType contentType, Class<?> configType) throws JsonMappingException {
      SerializationConfig config = prov.getConfig();
      JsonInclude.Value inclV = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion());
      inclV = config.getDefaultPropertyInclusion(configType, inclV);
      JsonInclude.Value valueIncl = config.getDefaultPropertyInclusion(contentType.getRawClass(), (JsonInclude.Value)null);
      if (valueIncl != null) {
         switch (valueIncl.getValueInclusion()) {
            case CUSTOM:
               inclV = inclV.withContentFilter(valueIncl.getContentFilter());
            case USE_DEFAULTS:
               break;
            default:
               inclV = inclV.withContentInclusion(valueIncl.getValueInclusion());
         }
      }

      return inclV;
   }

   protected JsonSerializer<?> buildArraySerializer(SerializerProvider prov, ArrayType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
      SerializationConfig config = prov.getConfig();
      JsonSerializer<?> ser = null;

      for(Serializers serializers : this.customSerializers()) {
         ser = serializers.findArraySerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
         if (ser != null) {
            break;
         }
      }

      if (ser == null) {
         Class<?> raw = type.getRawClass();
         if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl(elementValueSerializer)) {
            if (String[].class == raw) {
               ser = StringArraySerializer.instance;
            } else {
               ser = StdArraySerializers.findStandardImpl(raw);
            }
         }

         if (ser == null) {
            ser = new ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
         }
      }

      if (this._factoryConfig.hasSerializerModifiers()) {
         for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
            ser = mod.modifyArraySerializer(config, type, beanDesc, ser);
         }
      }

      return ser;
   }

   public JsonSerializer<?> findReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
      JavaType contentType = refType.getContentType();
      TypeSerializer contentTypeSerializer = (TypeSerializer)contentType.getTypeHandler();
      SerializationConfig config = prov.getConfig();
      if (contentTypeSerializer == null) {
         contentTypeSerializer = this.createTypeSerializer(config, contentType);
      }

      JsonSerializer<Object> contentSerializer = (JsonSerializer)contentType.getValueHandler();

      for(Serializers serializers : this.customSerializers()) {
         JsonSerializer<?> ser = serializers.findReferenceSerializer(config, refType, beanDesc, contentTypeSerializer, contentSerializer);
         if (ser != null) {
            return ser;
         }
      }

      if (refType.isTypeOrSubTypeOf(AtomicReference.class)) {
         return this.buildAtomicReferenceSerializer(prov, refType, beanDesc, staticTyping, contentTypeSerializer, contentSerializer);
      } else {
         return null;
      }
   }

   protected JsonSerializer<?> buildAtomicReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentSerializer) throws JsonMappingException {
      JavaType contentType = refType.getReferencedType();
      JsonInclude.Value inclV = this._findInclusionWithContent(prov, beanDesc, contentType, AtomicReference.class);
      JsonInclude.Include incl = inclV == null ? JsonInclude.Include.USE_DEFAULTS : inclV.getContentInclusion();
      Object valueToSuppress;
      boolean suppressNulls;
      if (incl != JsonInclude.Include.USE_DEFAULTS && incl != JsonInclude.Include.ALWAYS) {
         suppressNulls = true;
         switch (incl) {
            case NON_DEFAULT:
               valueToSuppress = BeanUtil.getDefaultValue(contentType);
               if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                  valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
               }
               break;
            case NON_ABSENT:
               valueToSuppress = contentType.isReferenceType() ? MapSerializer.MARKER_FOR_EMPTY : null;
               break;
            case NON_EMPTY:
               valueToSuppress = MapSerializer.MARKER_FOR_EMPTY;
               break;
            case CUSTOM:
               valueToSuppress = prov.includeFilterInstance((BeanPropertyDefinition)null, inclV.getContentFilter());
               if (valueToSuppress == null) {
                  suppressNulls = true;
               } else {
                  suppressNulls = prov.includeFilterSuppressNulls(valueToSuppress);
               }
               break;
            case NON_NULL:
            default:
               valueToSuppress = null;
         }
      } else {
         valueToSuppress = null;
         suppressNulls = false;
      }

      AtomicReferenceSerializer ser = new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer);
      return ser.withContentInclusion(valueToSuppress, suppressNulls);
   }

   protected JsonSerializer<?> buildIteratorSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType) throws JsonMappingException {
      return new IteratorSerializer(valueType, staticTyping, this.createTypeSerializer(config, valueType));
   }

   protected JsonSerializer<?> buildIterableSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType) throws JsonMappingException {
      return new IterableSerializer(valueType, staticTyping, this.createTypeSerializer(config, valueType));
   }

   protected JsonSerializer<?> buildEnumSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      JsonFormat.Value format = beanDesc.findExpectedFormat();
      if (format.getShape() == JsonFormat.Shape.OBJECT) {
         ((BasicBeanDescription)beanDesc).removeProperty("declaringClass");
         if (type.isEnumType()) {
            this._removeEnumSelfReferences(beanDesc);
         }

         return null;
      } else {
         Class<Enum<?>> enumClass = type.getRawClass();
         JsonSerializer<?> ser = EnumSerializer.construct(enumClass, config, beanDesc, format);
         if (this._factoryConfig.hasSerializerModifiers()) {
            for(BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
               ser = mod.modifyEnumSerializer(config, type, beanDesc, ser);
            }
         }

         return ser;
      }
   }

   private void _removeEnumSelfReferences(BeanDescription beanDesc) {
      Class<?> aClass = ClassUtil.findEnumType(beanDesc.getBeanClass());
      Iterator<BeanPropertyDefinition> it = beanDesc.findProperties().iterator();

      while(it.hasNext()) {
         BeanPropertyDefinition property = (BeanPropertyDefinition)it.next();
         JavaType propType = property.getPrimaryType();
         if (propType.isEnumType() && propType.isTypeOrSubTypeOf(aClass) && property.getAccessor().isStatic()) {
            it.remove();
         }
      }

   }

   protected JsonSerializer<Object> _findKeySerializer(SerializerProvider prov, Annotated a) throws JsonMappingException {
      AnnotationIntrospector intr = prov.getAnnotationIntrospector();
      Object serDef = intr.findKeySerializer(a);
      return serDef != null ? prov.serializerInstance(a, serDef) : null;
   }

   protected JsonSerializer<Object> _findContentSerializer(SerializerProvider prov, Annotated a) throws JsonMappingException {
      AnnotationIntrospector intr = prov.getAnnotationIntrospector();
      Object serDef = intr.findContentSerializer(a);
      return serDef != null ? prov.serializerInstance(a, serDef) : null;
   }

   protected Object findFilterId(SerializationConfig config, BeanDescription beanDesc) {
      return config.getAnnotationIntrospector().findFilterId(beanDesc.getClassInfo());
   }

   protected boolean usesStaticTyping(SerializationConfig config, BeanDescription beanDesc) {
      JsonSerialize.Typing t = config.getAnnotationIntrospector().findSerializationTyping(beanDesc.getClassInfo());
      if (t != null) {
         switch (t) {
            case DYNAMIC:
               return false;
            case STATIC:
               return true;
            case DEFAULT_TYPING:
         }
      }

      return config.isEnabled((MapperFeature)MapperFeature.USE_STATIC_TYPING);
   }

   static {
      HashMap<String, Class<? extends JsonSerializer<?>>> concLazy = new HashMap();
      HashMap<String, JsonSerializer<?>> concrete = new HashMap();
      concrete.put(String.class.getName(), new StringSerializer());
      ToStringSerializer sls = ToStringSerializer.instance;
      concrete.put(StringBuffer.class.getName(), sls);
      concrete.put(StringBuilder.class.getName(), sls);
      concrete.put(Character.class.getName(), sls);
      concrete.put(Character.TYPE.getName(), sls);
      NumberSerializers.addAll(concrete);
      concrete.put(Boolean.TYPE.getName(), new BooleanSerializer(true));
      concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
      concrete.put(BigInteger.class.getName(), new NumberSerializer(BigInteger.class));
      concrete.put(BigDecimal.class.getName(), new NumberSerializer(BigDecimal.class));
      concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
      concrete.put(Date.class.getName(), DateSerializer.instance);

      for(Map.Entry<Class<?>, Object> en : StdJdkSerializers.all()) {
         Object value = en.getValue();
         if (value instanceof JsonSerializer) {
            concrete.put(((Class)en.getKey()).getName(), (JsonSerializer)value);
         } else {
            Class<? extends JsonSerializer<?>> cls = (Class)value;
            concLazy.put(((Class)en.getKey()).getName(), cls);
         }
      }

      concLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
      _concrete = concrete;
      _concreteLazy = concLazy;
   }
}
