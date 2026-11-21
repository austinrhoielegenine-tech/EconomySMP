package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Named;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class BeanPropertyDefinition implements Named {
   protected static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();

   public abstract BeanPropertyDefinition withName(PropertyName var1);

   public abstract BeanPropertyDefinition withSimpleName(String var1);

   public abstract String getName();

   public abstract PropertyName getFullName();

   public boolean hasName(PropertyName name) {
      return this.getFullName().equals(name);
   }

   public abstract String getInternalName();

   public abstract PropertyName getWrapperName();

   public abstract boolean isExplicitlyIncluded();

   public boolean isExplicitlyNamed() {
      return this.isExplicitlyIncluded();
   }

   public abstract JavaType getPrimaryType();

   public abstract Class<?> getRawPrimaryType();

   public abstract PropertyMetadata getMetadata();

   public boolean isRequired() {
      return this.getMetadata().isRequired();
   }

   public boolean couldDeserialize() {
      return this.getMutator() != null;
   }

   public boolean couldSerialize() {
      return this.getAccessor() != null;
   }

   public abstract boolean hasGetter();

   public abstract boolean hasSetter();

   public abstract boolean hasField();

   public abstract boolean hasConstructorParameter();

   public abstract AnnotatedMethod getGetter();

   public abstract AnnotatedMethod getSetter();

   public abstract AnnotatedField getField();

   public abstract AnnotatedParameter getConstructorParameter();

   public Iterator<AnnotatedParameter> getConstructorParameters() {
      return ClassUtil.<AnnotatedParameter>emptyIterator();
   }

   public AnnotatedMember getAccessor() {
      AnnotatedMember m = this.getGetter();
      if (m == null) {
         m = this.getField();
      }

      return m;
   }

   public AnnotatedMember getMutator() {
      AnnotatedMember acc = this.getConstructorParameter();
      if (acc == null) {
         acc = this.getSetter();
         if (acc == null) {
            acc = this.getField();
         }
      }

      return acc;
   }

   public AnnotatedMember getNonConstructorMutator() {
      AnnotatedMember m = this.getSetter();
      if (m == null) {
         m = this.getField();
      }

      return m;
   }

   public abstract AnnotatedMember getPrimaryMember();

   public Class<?>[] findViews() {
      return null;
   }

   public AnnotationIntrospector.ReferenceProperty findReferenceType() {
      return null;
   }

   public String findReferenceName() {
      AnnotationIntrospector.ReferenceProperty ref = this.findReferenceType();
      return ref == null ? null : ref.getName();
   }

   public boolean isTypeId() {
      return false;
   }

   public ObjectIdInfo findObjectIdInfo() {
      return null;
   }

   public abstract JsonInclude.Value findInclusion();

   public List<PropertyName> findAliases() {
      return Collections.emptyList();
   }
}
