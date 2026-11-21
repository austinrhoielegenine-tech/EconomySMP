package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ClassNameIdResolver extends TypeIdResolverBase implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final String JAVA_UTIL_PKG = "java.util.";
   protected final PolymorphicTypeValidator _subTypeValidator;
   protected final Set<String> _allowedSubtypes;

   /** @deprecated */
   @Deprecated
   protected ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory) {
      this(baseType, typeFactory, LaissezFaireSubTypeValidator.instance);
   }

   /** @deprecated */
   @Deprecated
   public ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory, PolymorphicTypeValidator ptv) {
      this(baseType, typeFactory, (Collection)null, ptv);
   }

   public ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory, Collection<NamedType> subtypes, PolymorphicTypeValidator ptv) {
      super(baseType, typeFactory);
      this._subTypeValidator = ptv;
      Set<String> allowedSubtypes = null;
      if (subtypes != null) {
         for(NamedType t : subtypes) {
            if (allowedSubtypes == null) {
               allowedSubtypes = new HashSet();
            }

            allowedSubtypes.add(t.getType().getName());
         }
      }

      this._allowedSubtypes = allowedSubtypes == null ? Collections.emptySet() : allowedSubtypes;
   }

   /** @deprecated */
   @Deprecated
   public static ClassNameIdResolver construct(JavaType baseType, MapperConfig<?> config, PolymorphicTypeValidator ptv) {
      return new ClassNameIdResolver(baseType, config.getTypeFactory(), ptv);
   }

   public static ClassNameIdResolver construct(JavaType baseType, MapperConfig<?> config, Collection<NamedType> subtypes, PolymorphicTypeValidator ptv) {
      return new ClassNameIdResolver(baseType, config.getTypeFactory(), subtypes, ptv);
   }

   public JsonTypeInfo.Id getMechanism() {
      return JsonTypeInfo.Id.CLASS;
   }

   /** @deprecated */
   @Deprecated
   public void registerSubtype(Class<?> type, String name) {
   }

   public String idFromValue(Object value) {
      return this._idFrom(value, value.getClass(), this._typeFactory);
   }

   public String idFromValueAndType(Object value, Class<?> type) {
      return this._idFrom(value, type, this._typeFactory);
   }

   public JavaType typeFromId(DatabindContext context, String id) throws IOException {
      return this._typeFromId(id, context);
   }

   protected JavaType _typeFromId(String id, DatabindContext ctxt) throws IOException {
      DeserializationContext deserializationContext = null;
      if (ctxt instanceof DeserializationContext) {
         deserializationContext = (DeserializationContext)ctxt;
      }

      if (this._allowedSubtypes != null && deserializationContext != null && deserializationContext.isEnabled(DeserializationFeature.FAIL_ON_SUBTYPE_CLASS_NOT_REGISTERED) && !this._allowedSubtypes.contains(id)) {
         throw deserializationContext.invalidTypeIdException(this._baseType, id, "`DeserializationFeature.FAIL_ON_SUBTYPE_CLASS_NOT_REGISTERED` is enabled and the input class is not registered using `@JsonSubTypes` annotation");
      } else {
         JavaType t = ctxt.resolveAndValidateSubType(this._baseType, id, this._subTypeValidator);
         return t == null && deserializationContext != null ? deserializationContext.handleUnknownTypeId(this._baseType, id, this, "no such class found") : t;
      }
   }

   protected String _idFrom(Object value, Class<?> cls, TypeFactory typeFactory) {
      cls = this._resolveToParentAsNecessary(cls);
      String str = cls.getName();
      if (str.startsWith("java.util.")) {
         if (value instanceof EnumSet) {
            Class<?> enumClass = ClassUtil.findEnumType((EnumSet)value);
            str = typeFactory.constructCollectionType(EnumSet.class, enumClass).toCanonical();
         } else if (value instanceof EnumMap) {
            Class<?> enumClass = ClassUtil.findEnumType((EnumMap)value);
            Class<?> valueClass = Object.class;
            str = typeFactory.constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
         }
      } else if (str.indexOf(36) >= 0) {
         Class<?> outer = ClassUtil.getOuterClass(cls);
         if (outer != null) {
            Class<?> staticType = this._baseType.getRawClass();
            if (ClassUtil.getOuterClass(staticType) == null) {
               cls = this._baseType.getRawClass();
               str = cls.getName();
            }
         }
      }

      return str;
   }

   public String getDescForKnownTypeIds() {
      return "class name used as type id";
   }
}
