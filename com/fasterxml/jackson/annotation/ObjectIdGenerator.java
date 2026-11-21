package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.util.Objects;

public abstract class ObjectIdGenerator<T> implements Serializable {
   public abstract Class<?> getScope();

   public abstract boolean canUseFor(ObjectIdGenerator<?> var1);

   public boolean maySerializeAsObject() {
      return false;
   }

   public boolean isValidReferencePropertyName(String name, Object parser) {
      return false;
   }

   public abstract ObjectIdGenerator<T> forScope(Class<?> var1);

   public abstract ObjectIdGenerator<T> newForSerialization(Object var1);

   public abstract IdKey key(Object var1);

   public abstract T generateId(Object var1);

   public static final class IdKey implements Serializable {
      private static final long serialVersionUID = 1L;
      public final Class<?> type;
      public final Class<?> scope;
      public final Object key;
      private final int hashCode;

      public IdKey(Class<?> type, Class<?> scope, Object key) {
         this.type = (Class)Objects.requireNonNull(type, "Type must not be null");
         this.scope = scope;
         this.key = Objects.requireNonNull(key, "Key must not be null");
         this.hashCode = Objects.hashCode(key) + Objects.hashCode(type.getName()) ^ Objects.hashCode(scope);
      }

      public int hashCode() {
         return this.hashCode;
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (o.getClass() != this.getClass()) {
            return false;
         } else {
            IdKey other = (IdKey)o;
            return other.key.equals(this.key) && other.type == this.type && other.scope == this.scope;
         }
      }

      public String toString() {
         return String.format("[ObjectId: key=%s, type=%s, scope=%s]", this.key, this.type.getName(), this.scope == null ? "NONE" : this.scope.getName());
      }
   }
}
