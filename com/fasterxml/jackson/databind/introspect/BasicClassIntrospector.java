package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class BasicClassIntrospector extends ClassIntrospector implements Serializable {
   private static final long serialVersionUID = 2L;
   private static final Class<?> CLS_OBJECT = Object.class;
   private static final Class<?> CLS_STRING = String.class;
   private static final Class<?> CLS_JSON_NODE = JsonNode.class;
   protected static final BasicBeanDescription STRING_DESC;
   protected static final BasicBeanDescription BOOLEAN_DESC;
   protected static final BasicBeanDescription INT_DESC;
   protected static final BasicBeanDescription LONG_DESC;
   protected static final BasicBeanDescription OBJECT_DESC;

   public ClassIntrospector copy() {
      return new BasicClassIntrospector();
   }

   public BasicBeanDescription forSerialization(SerializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(config, type);
      if (desc == null) {
         desc = this._findStdJdkCollectionDesc(config, type);
         if (desc == null) {
            desc = BasicBeanDescription.forSerialization(this.collectProperties(config, type, r, true));
         }
      }

      return desc;
   }

   public BasicBeanDescription forDeserialization(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(config, type);
      if (desc == null) {
         desc = this._findStdJdkCollectionDesc(config, type);
         if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(config, type, r, false));
         }
      }

      return desc;
   }

   public BasicBeanDescription forDeserializationWithBuilder(DeserializationConfig config, JavaType builderType, ClassIntrospector.MixInResolver r, BeanDescription valueTypeDesc) {
      return BasicBeanDescription.forDeserialization(this.collectPropertiesWithBuilder(config, builderType, r, valueTypeDesc, false));
   }

   public BasicBeanDescription forCreation(DeserializationConfig config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(config, type);
      if (desc == null) {
         desc = this._findStdJdkCollectionDesc(config, type);
         if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(config, type, r, false));
         }
      }

      return desc;
   }

   public BasicBeanDescription forClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(config, type);
      if (desc == null) {
         desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedClass(config, type, r));
      }

      return desc;
   }

   public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      BasicBeanDescription desc = this._findStdTypeDesc(config, type);
      if (desc == null) {
         desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedWithoutSuperTypes(config, type, r));
      }

      return desc;
   }

   protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
      AnnotatedClass classDef = this._resolveAnnotatedClass(config, type, r);
      AccessorNamingStrategy accNaming = type.isRecordType() ? config.getAccessorNaming().forRecord(config, classDef) : config.getAccessorNaming().forPOJO(config, classDef);
      return this.constructPropertyCollector(config, classDef, type, forSerialization, accNaming);
   }

   protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, BeanDescription valueTypeDesc, boolean forSerialization) {
      AnnotatedClass builderClassDef = this._resolveAnnotatedClass(config, type, r);
      AccessorNamingStrategy accNaming = config.getAccessorNaming().forBuilder(config, builderClassDef, valueTypeDesc);
      return this.constructPropertyCollector(config, builderClassDef, type, forSerialization, accNaming);
   }

   protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass classDef, JavaType type, boolean forSerialization, AccessorNamingStrategy accNaming) {
      return new POJOPropertiesCollector(config, forSerialization, type, classDef, accNaming);
   }

   protected BasicBeanDescription _findStdTypeDesc(MapperConfig<?> config, JavaType type) {
      Class<?> cls = type.getRawClass();
      if (cls.isPrimitive()) {
         if (cls == Integer.TYPE) {
            return INT_DESC;
         }

         if (cls == Long.TYPE) {
            return LONG_DESC;
         }

         if (cls == Boolean.TYPE) {
            return BOOLEAN_DESC;
         }
      } else if (ClassUtil.isJDKClass(cls)) {
         if (cls == CLS_OBJECT) {
            return OBJECT_DESC;
         }

         if (cls == CLS_STRING) {
            return STRING_DESC;
         }

         if (cls == Integer.class) {
            return INT_DESC;
         }

         if (cls == Long.class) {
            return LONG_DESC;
         }

         if (cls == Boolean.class) {
            return BOOLEAN_DESC;
         }
      } else if (CLS_JSON_NODE.isAssignableFrom(cls)) {
         return BasicBeanDescription.forOtherUse(config, type, AnnotatedClassResolver.createPrimordial(cls));
      }

      return null;
   }

   protected boolean _isStdJDKCollection(JavaType type) {
      if (type.isContainerType() && !type.isArrayType()) {
         Class<?> raw = type.getRawClass();
         if (ClassUtil.isJDKClass(raw) && (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw))) {
            return raw.toString().indexOf(36) <= 0;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected BasicBeanDescription _findStdJdkCollectionDesc(MapperConfig<?> cfg, JavaType type) {
      return this._isStdJDKCollection(type) ? BasicBeanDescription.forOtherUse(cfg, type, this._resolveAnnotatedClass(cfg, type, cfg)) : null;
   }

   protected AnnotatedClass _resolveAnnotatedClass(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      return AnnotatedClassResolver.resolve(config, type, r);
   }

   protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
      return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
   }

   static {
      STRING_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(String.class), AnnotatedClassResolver.createPrimordial(CLS_STRING));
      BOOLEAN_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Boolean.TYPE), AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
      INT_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Integer.TYPE), AnnotatedClassResolver.createPrimordial(Integer.TYPE));
      LONG_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Long.TYPE), AnnotatedClassResolver.createPrimordial(Long.TYPE));
      OBJECT_DESC = BasicBeanDescription.forOtherUse((MapperConfig)null, SimpleType.constructUnsafe(Object.class), AnnotatedClassResolver.createPrimordial(CLS_OBJECT));
   }
}
