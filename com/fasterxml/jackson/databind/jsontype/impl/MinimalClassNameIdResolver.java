package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.Collection;

public class MinimalClassNameIdResolver extends ClassNameIdResolver {
   private static final long serialVersionUID = 1L;
   protected final String _basePackageName;
   protected final String _basePackagePrefix;

   /** @deprecated */
   @Deprecated
   protected MinimalClassNameIdResolver(JavaType baseType, TypeFactory typeFactory, PolymorphicTypeValidator ptv) {
      this(baseType, typeFactory, (Collection)null, ptv);
   }

   protected MinimalClassNameIdResolver(JavaType baseType, TypeFactory typeFactory, Collection<NamedType> subtypes, PolymorphicTypeValidator ptv) {
      super(baseType, typeFactory, subtypes, ptv);
      String base = baseType.getRawClass().getName();
      int ix = base.lastIndexOf(46);
      if (ix < 0) {
         this._basePackageName = "";
         this._basePackagePrefix = ".";
      } else {
         this._basePackagePrefix = base.substring(0, ix + 1);
         this._basePackageName = base.substring(0, ix);
      }

   }

   /** @deprecated */
   @Deprecated
   public static MinimalClassNameIdResolver construct(JavaType baseType, MapperConfig<?> config, PolymorphicTypeValidator ptv) {
      return new MinimalClassNameIdResolver(baseType, config.getTypeFactory(), ptv);
   }

   public static MinimalClassNameIdResolver construct(JavaType baseType, MapperConfig<?> config, Collection<NamedType> subtypes, PolymorphicTypeValidator ptv) {
      return new MinimalClassNameIdResolver(baseType, config.getTypeFactory(), subtypes, ptv);
   }

   public JsonTypeInfo.Id getMechanism() {
      return JsonTypeInfo.Id.MINIMAL_CLASS;
   }

   public String idFromValue(Object value) {
      return this.idFromValueAndType(value, value.getClass());
   }

   public String idFromValueAndType(Object value, Class<?> rawType) {
      rawType = this._resolveToParentAsNecessary(rawType);
      String n = rawType.getName();
      return n.startsWith(this._basePackagePrefix) ? n.substring(this._basePackagePrefix.length() - 1) : n;
   }

   protected JavaType _typeFromId(String id, DatabindContext ctxt) throws IOException {
      if (id.startsWith(".")) {
         StringBuilder sb = new StringBuilder(id.length() + this._basePackageName.length());
         if (this._basePackageName.isEmpty()) {
            sb.append(id.substring(1));
         } else {
            sb.append(this._basePackageName).append(id);
         }

         id = sb.toString();
      }

      return super._typeFromId(id, ctxt);
   }
}
