package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotatedFieldCollector extends CollectorBase {
   private final TypeFactory _typeFactory;
   private final ClassIntrospector.MixInResolver _mixInResolver;
   private final boolean _collectAnnotations;

   AnnotatedFieldCollector(AnnotationIntrospector intr, TypeFactory types, ClassIntrospector.MixInResolver mixins, boolean collectAnnotations) {
      super(intr);
      this._typeFactory = types;
      this._mixInResolver = intr == null ? null : mixins;
      this._collectAnnotations = collectAnnotations;
   }

   public static List<AnnotatedField> collectFields(AnnotationIntrospector intr, TypeResolutionContext tc, ClassIntrospector.MixInResolver mixins, TypeFactory types, JavaType type, boolean collectAnnotations) {
      return (new AnnotatedFieldCollector(intr, types, mixins, collectAnnotations)).collect(tc, type);
   }

   List<AnnotatedField> collect(TypeResolutionContext tc, JavaType type) {
      Map<String, FieldBuilder> foundFields = this._findFields(tc, type, (Map)null);
      if (foundFields == null) {
         return Collections.emptyList();
      } else {
         List<AnnotatedField> result = new ArrayList(foundFields.size());

         for(FieldBuilder b : foundFields.values()) {
            result.add(b.build());
         }

         return result;
      }
   }

   private Map<String, FieldBuilder> _findFields(TypeResolutionContext tc, JavaType type, Map<String, FieldBuilder> fields) {
      JavaType parent = type.getSuperClass();
      if (parent == null) {
         return fields;
      } else {
         Class<?> cls = type.getRawClass();
         fields = this._findFields(new TypeResolutionContext.Basic(this._typeFactory, parent.getBindings()), parent, fields);

         for(Field f : cls.getDeclaredFields()) {
            if (this._isIncludableField(f)) {
               if (fields == null) {
                  fields = new LinkedHashMap();
               }

               FieldBuilder b = new FieldBuilder(tc, f);
               if (this._collectAnnotations) {
                  b.annotations = this.collectAnnotations(b.annotations, f.getDeclaredAnnotations());
               }

               fields.put(f.getName(), b);
            }
         }

         if (fields != null && this._mixInResolver != null) {
            Class<?> mixin = this._mixInResolver.findMixInClassFor(cls);
            if (mixin != null) {
               this._addFieldMixIns(mixin, cls, fields);
            }
         }

         return fields;
      }
   }

   private void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass, Map<String, FieldBuilder> fields) {
      for(Class<?> mixin : ClassUtil.findSuperClasses(mixInCls, targetClass, true)) {
         for(Field mixinField : mixin.getDeclaredFields()) {
            if (this._isIncludableField(mixinField)) {
               String name = mixinField.getName();
               FieldBuilder b = (FieldBuilder)fields.get(name);
               if (b != null) {
                  b.annotations = this.collectAnnotations(b.annotations, mixinField.getDeclaredAnnotations());
               }
            }
         }
      }

   }

   private boolean _isIncludableField(Field f) {
      if (f.isEnumConstant()) {
         return true;
      } else if (f.isSynthetic()) {
         return false;
      } else {
         return !Modifier.isStatic(f.getModifiers());
      }
   }

   private static final class FieldBuilder {
      public final TypeResolutionContext typeContext;
      public final Field field;
      public AnnotationCollector annotations;

      public FieldBuilder(TypeResolutionContext tc, Field f) {
         this.typeContext = tc;
         this.field = f;
         this.annotations = AnnotationCollector.emptyCollector();
      }

      public AnnotatedField build() {
         return new AnnotatedField(this.typeContext, this.field, this.annotations.asAnnotationMap());
      }
   }
}
