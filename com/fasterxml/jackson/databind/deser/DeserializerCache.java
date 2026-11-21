package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.LookupCache;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public final class DeserializerCache implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int DEFAULT_MAX_CACHE_SIZE = 2000;
   protected final LookupCache<JavaType, JsonDeserializer<Object>> _cachedDeserializers;
   protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers;
   private final ReentrantLock _incompleteDeserializersLock;

   public DeserializerCache() {
      this(2000);
   }

   public DeserializerCache(int maxSize) {
      this(new LRUMap(Math.min(64, maxSize >> 2), maxSize));
   }

   public DeserializerCache(LookupCache<JavaType, JsonDeserializer<Object>> cache) {
      this._incompleteDeserializers = new HashMap(8);
      this._incompleteDeserializersLock = new ReentrantLock();
      this._cachedDeserializers = cache;
   }

   public DeserializerCache emptyCopy() {
      return new DeserializerCache(this._cachedDeserializers.emptyCopy());
   }

   Object writeReplace() {
      this._incompleteDeserializers.clear();
      return this;
   }

   public int cachedDeserializersCount() {
      return this._cachedDeserializers.size();
   }

   public void flushCachedDeserializers() {
      this._cachedDeserializers.clear();
   }

   public JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType) throws JsonMappingException {
      Objects.requireNonNull(propertyType, "Null 'propertyType' passed");
      JsonDeserializer<Object> deser = this._findCachedDeserializer(propertyType);
      if (deser == null) {
         deser = this._createAndCacheValueDeserializer(ctxt, factory, propertyType);
         if (deser == null) {
            deser = this._handleUnknownValueDeserializer(ctxt, propertyType);
         }
      }

      return deser;
   }

   public KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      Objects.requireNonNull(type, "Null 'type' passed");
      KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
      if (kd == null) {
         return this._handleUnknownKeyDeserializer(ctxt, type);
      } else {
         if (kd instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)kd).resolve(ctxt);
         }

         return kd;
      }
   }

   public boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      Objects.requireNonNull(type, "Null 'type' passed");
      JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
      if (deser == null) {
         deser = this._createAndCacheValueDeserializer(ctxt, factory, type);
      }

      return deser != null;
   }

   protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
      return this._hasCustomHandlers(type) ? null : (JsonDeserializer)this._cachedDeserializers.get(type);
   }

   protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      boolean isCustom = this._hasCustomHandlers(type);
      if (!isCustom) {
         JsonDeserializer<Object> deser = this._cachedDeserializers.get(type);
         if (deser != null) {
            return deser;
         }
      }

      this._incompleteDeserializersLock.lock();

      JsonDeserializer<Object> deser;
      try {
         if (!isCustom) {
            JsonDeserializer<Object> deser = this._cachedDeserializers.get(type);
            if (deser != null) {
               deser = deser;
               return deser;
            }
         }

         int count = this._incompleteDeserializers.size();
         if (count > 0) {
            deser = (JsonDeserializer)this._incompleteDeserializers.get(type);
            if (deser != null) {
               JsonDeserializer var7 = deser;
               return var7;
            }
         }

         try {
            deser = this._createAndCache2(ctxt, factory, type, isCustom);
         } finally {
            if (count == 0 && this._incompleteDeserializers.size() > 0) {
               this._incompleteDeserializers.clear();
            }

         }
      } finally {
         this._incompleteDeserializersLock.unlock();
      }

      return deser;
   }

   /** @deprecated */
   @Deprecated
   protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      return this._createAndCache2(ctxt, factory, type, this._hasCustomHandlers(type));
   }

   protected JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, boolean isCustom) throws JsonMappingException {
      JsonDeserializer<Object> deser;
      try {
         deser = this._createDeserializer(ctxt, factory, type);
      } catch (IllegalArgumentException iae) {
         ctxt.reportBadDefinition(type, ClassUtil.exceptionMessage(iae));
         deser = null;
      }

      if (deser == null) {
         return null;
      } else {
         boolean addToCache = !isCustom && deser.isCachable();
         if (deser instanceof ResolvableDeserializer) {
            this._incompleteDeserializers.put(type, deser);

            try {
               ((ResolvableDeserializer)deser).resolve(ctxt);
            } finally {
               this._incompleteDeserializers.remove(type);
            }
         }

         if (addToCache) {
            this._cachedDeserializers.put(type, deser);
         }

         return deser;
      }
   }

   protected JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
         type = factory.mapAbstractType(config, type);
      }

      BeanDescription beanDesc = config.introspect(type);
      JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
      if (deser != null) {
         return deser;
      } else {
         JavaType newType = this.modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
         if (newType != type) {
            type = newType;
            beanDesc = config.introspect(newType);
         }

         Class<?> builder = beanDesc.findPOJOBuilder();
         if (builder != null) {
            return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
         } else {
            Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
            if (conv != null) {
               JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
               if (!delegateType.hasRawClass(type.getRawClass())) {
                  beanDesc = config.introspect(delegateType);
               }

               return new StdDelegatingDeserializer<Object>(conv, delegateType, this._createDeserializer2(ctxt, factory, delegateType, beanDesc));
            } else {
               return this._createDeserializer2(ctxt, factory, type, beanDesc);
            }
         }
      }
   }

   protected JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
      DeserializationConfig config = ctxt.getConfig();
      if (type.isEnumType()) {
         return factory.createEnumDeserializer(ctxt, type, beanDesc);
      } else {
         if (type.isContainerType()) {
            if (type.isArrayType()) {
               return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc);
            }

            if (type.isMapLikeType()) {
               JsonFormat.Value format = beanDesc.findExpectedFormat();
               if (format.getShape() != JsonFormat.Shape.OBJECT) {
                  MapLikeType mlt = (MapLikeType)type;
                  if (mlt instanceof MapType) {
                     return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc);
                  }

                  return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
               }
            }

            if (type.isCollectionLikeType()) {
               JsonFormat.Value format = beanDesc.findExpectedFormat();
               if (format.getShape() != JsonFormat.Shape.OBJECT) {
                  CollectionLikeType clt = (CollectionLikeType)type;
                  if (clt instanceof CollectionType) {
                     return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc);
                  }

                  return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
               }
            }
         }

         if (type.isReferenceType()) {
            return factory.createReferenceDeserializer(ctxt, (ReferenceType)type, beanDesc);
         } else {
            return JsonNode.class.isAssignableFrom(type.getRawClass()) ? factory.createTreeDeserializer(config, type, beanDesc) : factory.createBeanDeserializer(ctxt, type, beanDesc);
         }
      }
   }

   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
      Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
      if (deserDef == null) {
         return null;
      } else {
         JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
         return this.findConvertingDeserializer(ctxt, ann, deser);
      }
   }

   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser) throws JsonMappingException {
      Converter<Object, Object> conv = this.findConverter(ctxt, a);
      if (conv == null) {
         return deser;
      } else {
         JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
         return new StdDelegatingDeserializer<Object>(conv, delegateType, deser);
      }
   }

   protected Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a) throws JsonMappingException {
      Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
      return convDef == null ? null : ctxt.converterInstance(a, convDef);
   }

   private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
      AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
      if (intr == null) {
         return type;
      } else {
         if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null && keyType.getValueHandler() == null) {
               Object kdDef = intr.findKeyDeserializer(a);
               if (kdDef != null) {
                  KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                  if (kd != null) {
                     type = ((MapLikeType)type).withKeyValueHandler(kd);
                  }
               }
            }
         }

         JavaType contentType = type.getContentType();
         if (contentType != null && contentType.getValueHandler() == null) {
            Object cdDef = intr.findContentDeserializer(a);
            if (cdDef != null) {
               JsonDeserializer<?> cd = null;
               if (cdDef instanceof JsonDeserializer) {
                  cd = (JsonDeserializer)cdDef;
               } else {
                  Class<?> cdClass = this._verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
                  if (cdClass != null) {
                     cd = ctxt.deserializerInstance(a, cdClass);
                  }
               }

               if (cd != null) {
                  type = type.withContentValueHandler(cd);
               }
            }
         }

         JavaType var9 = intr.refineDeserializationType(ctxt.getConfig(), a, type);
         return var9;
      }
   }

   private boolean _hasCustomHandlers(JavaType t) {
      if (t.isContainerType()) {
         JavaType ct = t.getContentType();
         if (ct != null && (ct.getValueHandler() != null || ct.getTypeHandler() != null)) {
            return true;
         }

         if (t.isMapLikeType()) {
            JavaType kt = t.getKeyType();
            if (kt.getValueHandler() != null) {
               return true;
            }
         }
      }

      return false;
   }

   private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
      if (src == null) {
         return null;
      } else if (!(src instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
      } else {
         Class<?> cls = (Class)src;
         return cls != noneClass && !ClassUtil.isBogusClass(cls) ? cls : null;
      }
   }

   protected JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      Class<?> rawClass = type.getRawClass();
      return !ClassUtil.isConcrete(rawClass) ? (JsonDeserializer)ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for abstract type " + type) : (JsonDeserializer)ctxt.reportBadDefinition(type, "Cannot find a Value deserializer for type " + type);
   }

   protected KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
      return (KeyDeserializer)ctxt.reportBadDefinition(type, "Cannot find a (Map) Key deserializer for type " + type);
   }
}
