package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

public class ResolvedRecursiveType extends IdentityEqualityType {
   private static final long serialVersionUID = 1L;
   protected JavaType _referencedType;

   public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings) {
      super(erasedType, bindings, (JavaType)null, (JavaType[])null, 0, (Object)null, (Object)null, false);
   }

   public void setReference(JavaType ref) {
      if (this._referencedType != null) {
         throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
      } else {
         this._referencedType = ref;
      }
   }

   public JavaType getSuperClass() {
      return this._referencedType != null ? this._referencedType.getSuperClass() : super.getSuperClass();
   }

   public JavaType getSelfReferencedType() {
      return this._referencedType;
   }

   public TypeBindings getBindings() {
      return this._referencedType != null ? this._referencedType.getBindings() : super.getBindings();
   }

   public StringBuilder getGenericSignature(StringBuilder sb) {
      return this._referencedType != null ? this._referencedType.getErasedSignature(sb) : sb.append("?");
   }

   public StringBuilder getErasedSignature(StringBuilder sb) {
      return this._referencedType != null ? this._referencedType.getErasedSignature(sb) : sb;
   }

   public JavaType withContentType(JavaType contentType) {
      return this;
   }

   public JavaType withTypeHandler(Object h) {
      return this;
   }

   public JavaType withContentTypeHandler(Object h) {
      return this;
   }

   public JavaType withValueHandler(Object h) {
      return this;
   }

   public JavaType withContentValueHandler(Object h) {
      return this;
   }

   public JavaType withStaticTyping() {
      return this;
   }

   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return null;
   }

   public boolean isContainerType() {
      return false;
   }

   public String toString() {
      StringBuilder sb = (new StringBuilder(40)).append("[recursive type; ");
      if (this._referencedType == null) {
         sb.append("UNRESOLVED");
      } else {
         sb.append(this._referencedType.getRawClass().getName());
      }

      return sb.toString();
   }
}
