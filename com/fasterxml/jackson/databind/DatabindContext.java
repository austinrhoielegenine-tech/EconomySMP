package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.DatatypeFeatures;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.TimeZone;

public abstract class DatabindContext {
   private static final int MAX_ERROR_STR_LEN = 500;

   public abstract MapperConfig<?> getConfig();

   public abstract AnnotationIntrospector getAnnotationIntrospector();

   public abstract boolean isEnabled(MapperFeature var1);

   public abstract boolean isEnabled(DatatypeFeature var1);

   public abstract DatatypeFeatures getDatatypeFeatures();

   public abstract boolean canOverrideAccessModifiers();

   public abstract Class<?> getActiveView();

   public abstract Locale getLocale();

   public abstract TimeZone getTimeZone();

   public abstract JsonFormat.Value getDefaultPropertyFormat(Class<?> var1);

   public abstract Object getAttribute(Object var1);

   public abstract DatabindContext setAttribute(Object var1, Object var2);

   public JavaType constructType(Type type) {
      return type == null ? null : this.getTypeFactory().constructType(type);
   }

   public abstract JavaType constructSpecializedType(JavaType var1, Class<?> var2);

   public JavaType resolveSubType(JavaType baseType, String subClassName) throws JsonMappingException {
      if (subClassName.indexOf(60) > 0) {
         JavaType t = this.getTypeFactory().constructFromCanonical(subClassName);
         if (t.isTypeOrSubTypeOf(baseType.getRawClass())) {
            return t;
         }
      } else {
         Class<?> cls;
         try {
            cls = this.getTypeFactory().findClass(subClassName);
         } catch (ClassNotFoundException var5) {
            return null;
         } catch (Exception e) {
            throw this.invalidTypeIdException(baseType, subClassName, String.format("problem: (%s) %s", e.getClass().getName(), ClassUtil.exceptionMessage(e)));
         }

         if (baseType.isTypeOrSuperTypeOf(cls)) {
            return this.getTypeFactory().constructSpecializedType(baseType, cls);
         }
      }

      throw this.invalidTypeIdException(baseType, subClassName, "Not a subtype");
   }

   public JavaType resolveAndValidateSubType(JavaType baseType, String subClass, PolymorphicTypeValidator ptv) throws JsonMappingException {
      int ltIndex = subClass.indexOf(60);
      if (ltIndex > 0) {
         return this._resolveAndValidateGeneric(baseType, subClass, ptv, ltIndex);
      } else {
         MapperConfig<?> config = this.getConfig();
         PolymorphicTypeValidator.Validity vld = ptv.validateSubClassName(config, baseType, subClass);
         if (vld == PolymorphicTypeValidator.Validity.DENIED) {
            return (JavaType)this._throwSubtypeNameNotAllowed(baseType, subClass, ptv);
         } else {
            Class<?> cls;
            try {
               cls = this.getTypeFactory().findClass(subClass);
            } catch (ClassNotFoundException var9) {
               return null;
            } catch (Exception e) {
               throw this.invalidTypeIdException(baseType, subClass, String.format("problem: (%s) %s", e.getClass().getName(), ClassUtil.exceptionMessage(e)));
            }

            if (!baseType.isTypeOrSuperTypeOf(cls)) {
               return (JavaType)this._throwNotASubtype(baseType, subClass);
            } else {
               JavaType subType = config.getTypeFactory().constructSpecializedType(baseType, cls);
               if (vld == PolymorphicTypeValidator.Validity.INDETERMINATE) {
                  vld = ptv.validateSubType(config, baseType, subType);
                  if (vld != PolymorphicTypeValidator.Validity.ALLOWED) {
                     return (JavaType)this._throwSubtypeClassNotAllowed(baseType, subClass, ptv);
                  }
               }

               return subType;
            }
         }
      }
   }

   private JavaType _resolveAndValidateGeneric(JavaType baseType, String subClass, PolymorphicTypeValidator ptv, int ltIndex) throws JsonMappingException {
      MapperConfig<?> config = this.getConfig();
      PolymorphicTypeValidator.Validity vld = ptv.validateSubClassName(config, baseType, subClass.substring(0, ltIndex));
      if (vld == PolymorphicTypeValidator.Validity.DENIED) {
         return (JavaType)this._throwSubtypeNameNotAllowed(baseType, subClass, ptv);
      } else {
         JavaType subType = this.getTypeFactory().constructFromCanonical(subClass);
         if (!subType.isTypeOrSubTypeOf(baseType.getRawClass())) {
            return (JavaType)this._throwNotASubtype(baseType, subClass);
         } else {
            return vld != PolymorphicTypeValidator.Validity.ALLOWED && ptv.validateSubType(config, baseType, subType) != PolymorphicTypeValidator.Validity.ALLOWED ? (JavaType)this._throwSubtypeClassNotAllowed(baseType, subClass, ptv) : subType;
         }
      }
   }

   protected <T> T _throwNotASubtype(JavaType baseType, String subType) throws JsonMappingException {
      throw this.invalidTypeIdException(baseType, subType, "Not a subtype");
   }

   protected <T> T _throwSubtypeNameNotAllowed(JavaType baseType, String subType, PolymorphicTypeValidator ptv) throws JsonMappingException {
      throw this.invalidTypeIdException(baseType, subType, "Configured `PolymorphicTypeValidator` (of type " + ClassUtil.classNameOf(ptv) + ") denied resolution");
   }

   protected <T> T _throwSubtypeClassNotAllowed(JavaType baseType, String subType, PolymorphicTypeValidator ptv) throws JsonMappingException {
      throw this.invalidTypeIdException(baseType, subType, "Configured `PolymorphicTypeValidator` (of type " + ClassUtil.classNameOf(ptv) + ") denied resolution");
   }

   protected abstract JsonMappingException invalidTypeIdException(JavaType var1, String var2, String var3);

   public abstract TypeFactory getTypeFactory();

   public ObjectIdGenerator<?> objectIdGeneratorInstance(Annotated annotated, ObjectIdInfo objectIdInfo) throws JsonMappingException {
      Class<?> implClass = objectIdInfo.getGeneratorType();
      MapperConfig<?> config = this.getConfig();
      HandlerInstantiator hi = config.getHandlerInstantiator();
      ObjectIdGenerator<?> gen = hi == null ? null : hi.objectIdGeneratorInstance(config, annotated, implClass);
      if (gen == null) {
         gen = (ObjectIdGenerator)ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
      }

      return gen.forScope(objectIdInfo.getScope());
   }

   public ObjectIdResolver objectIdResolverInstance(Annotated annotated, ObjectIdInfo objectIdInfo) {
      Class<? extends ObjectIdResolver> implClass = objectIdInfo.getResolverType();
      MapperConfig<?> config = this.getConfig();
      HandlerInstantiator hi = config.getHandlerInstantiator();
      ObjectIdResolver resolver = hi == null ? null : hi.resolverIdGeneratorInstance(config, annotated, implClass);
      if (resolver == null) {
         resolver = (ObjectIdResolver)ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
      }

      return resolver;
   }

   public Converter<Object, Object> converterInstance(Annotated annotated, Object converterDef) throws JsonMappingException {
      if (converterDef == null) {
         return null;
      } else if (converterDef instanceof Converter) {
         return (Converter)converterDef;
      } else if (!(converterDef instanceof Class)) {
         throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
      } else {
         Class<?> converterClass = (Class)converterDef;
         if (converterClass != Converter.None.class && !ClassUtil.isBogusClass(converterClass)) {
            if (!Converter.class.isAssignableFrom(converterClass)) {
               throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
            } else {
               MapperConfig<?> config = this.getConfig();
               HandlerInstantiator hi = config.getHandlerInstantiator();
               Converter<?, ?> conv = hi == null ? null : hi.converterInstance(config, annotated, converterClass);
               if (conv == null) {
                  conv = (Converter)ClassUtil.createInstance(converterClass, config.canOverrideAccessModifiers());
               }

               return conv;
            }
         } else {
            return null;
         }
      }
   }

   public abstract <T> T reportBadDefinition(JavaType var1, String var2) throws JsonMappingException;

   public <T> T reportBadDefinition(Class<?> type, String msg) throws JsonMappingException {
      return (T)this.reportBadDefinition(this.constructType(type), msg);
   }

   public abstract <T> T reportBadTypeDefinition(BeanDescription var1, String var2, Object... var3) throws JsonMappingException;

   protected final String _format(String msg, Object... msgArgs) {
      return msgArgs.length > 0 ? String.format(msg, msgArgs) : msg;
   }

   protected final String _truncate(String desc) {
      if (desc == null) {
         return "";
      } else {
         return desc.length() <= 500 ? desc : desc.substring(0, 500) + "]...[" + desc.substring(desc.length() - 500);
      }
   }

   protected String _quotedString(String desc) {
      return desc == null ? "[N/A]" : String.format("\"%s\"", this._truncate(desc));
   }

   protected String _colonConcat(String msgBase, String extra) {
      return extra == null ? msgBase : msgBase + ": " + extra;
   }

   protected String _desc(String desc) {
      return desc == null ? "[N/A]" : this._truncate(desc);
   }
}
