package com.fasterxml.jackson.annotation;

public enum PropertyAccessor {
   GETTER,
   SETTER,
   FIELD,
   IS_GETTER,
   CREATOR,
   SCALAR_CONSTRUCTOR,
   NONE,
   ALL;

   public boolean creatorEnabled() {
      return this == CREATOR || this == ALL;
   }

   public boolean scalarConstructorEnabled() {
      return this == SCALAR_CONSTRUCTOR || this == ALL;
   }

   public boolean getterEnabled() {
      return this == GETTER || this == ALL;
   }

   public boolean isGetterEnabled() {
      return this == IS_GETTER || this == ALL;
   }

   public boolean setterEnabled() {
      return this == SETTER || this == ALL;
   }

   public boolean fieldEnabled() {
      return this == FIELD || this == ALL;
   }
}
