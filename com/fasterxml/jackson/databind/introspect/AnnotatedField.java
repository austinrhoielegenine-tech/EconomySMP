package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class AnnotatedField extends AnnotatedMember implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final transient Field _field;
   protected Serialization _serialization;

   public AnnotatedField(TypeResolutionContext contextClass, Field field, AnnotationMap annMap) {
      super(contextClass, annMap);
      this._field = (Field)Objects.requireNonNull(field);
   }

   public AnnotatedField withAnnotations(AnnotationMap ann) {
      return new AnnotatedField(this._typeContext, this._field, ann);
   }

   protected AnnotatedField(Serialization ser) {
      super((TypeResolutionContext)null, (AnnotationMap)null);
      this._field = null;
      this._serialization = ser;
   }

   public Field getAnnotated() {
      return this._field;
   }

   public int getModifiers() {
      return this._field.getModifiers();
   }

   public String getName() {
      return this._field.getName();
   }

   public Class<?> getRawType() {
      return this._field.getType();
   }

   public JavaType getType() {
      return this._typeContext.resolveType(this._field.getGenericType());
   }

   public Class<?> getDeclaringClass() {
      return this._field.getDeclaringClass();
   }

   public Member getMember() {
      return this._field;
   }

   public void setValue(Object pojo, Object value) throws IllegalArgumentException {
      try {
         this._field.set(pojo, value);
      } catch (IllegalAccessException e) {
         throw new IllegalArgumentException("Failed to setValue() for field " + this.getFullName() + ": " + e.getMessage(), e);
      }
   }

   public Object getValue(Object pojo) throws IllegalArgumentException {
      try {
         return this._field.get(pojo);
      } catch (IllegalAccessException e) {
         throw new IllegalArgumentException("Failed to getValue() for field " + this.getFullName() + ": " + e.getMessage(), e);
      }
   }

   public int getAnnotationCount() {
      return this._annotations.size();
   }

   public boolean isTransient() {
      return Modifier.isTransient(this.getModifiers());
   }

   public int hashCode() {
      return Objects.hashCode(this._field);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         AnnotatedField other = (AnnotatedField)o;
         return Objects.equals(this._field, other._field);
      }
   }

   public String toString() {
      return "[field " + this.getFullName() + "]";
   }

   Object writeReplace() {
      return new AnnotatedField(new Serialization(this._field));
   }

   Object readResolve() {
      Class<?> clazz = this._serialization.clazz;

      try {
         Field f = clazz.getDeclaredField(this._serialization.name);
         if (!f.isAccessible()) {
            ClassUtil.checkAndFixAccess(f, false);
         }

         return new AnnotatedField((TypeResolutionContext)null, f, (AnnotationMap)null);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
      }
   }

   private static final class Serialization implements Serializable {
      private static final long serialVersionUID = 1L;
      protected Class<?> clazz;
      protected String name;

      public Serialization(Field f) {
         this.clazz = f.getDeclaringClass();
         this.name = f.getName();
      }
   }
}
