package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;

public interface TypeResolutionContext {
   JavaType resolveType(Type var1);

   public static class Basic implements TypeResolutionContext {
      private final TypeFactory _typeFactory;
      private final TypeBindings _bindings;

      public Basic(TypeFactory tf, TypeBindings b) {
         this._typeFactory = tf;
         this._bindings = b;
      }

      public JavaType resolveType(Type type) {
         return this._typeFactory.resolveMemberType(type, this._bindings);
      }
   }

   public static class Empty implements TypeResolutionContext {
      private final TypeFactory _typeFactory;

      public Empty(TypeFactory tf) {
         this._typeFactory = tf;
      }

      public JavaType resolveType(Type type) {
         return this._typeFactory.constructType(type);
      }
   }
}
