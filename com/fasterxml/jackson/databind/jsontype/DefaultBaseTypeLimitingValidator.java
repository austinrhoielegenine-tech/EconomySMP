package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Closeable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DefaultBaseTypeLimitingValidator extends PolymorphicTypeValidator implements Serializable {
   private static final long serialVersionUID = 1L;

   public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
      return this.isUnsafeBaseType(config, baseType) ? PolymorphicTypeValidator.Validity.DENIED : PolymorphicTypeValidator.Validity.INDETERMINATE;
   }

   public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName) {
      return PolymorphicTypeValidator.Validity.INDETERMINATE;
   }

   public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> config, JavaType baseType, JavaType subType) {
      return this.isSafeSubType(config, baseType, subType) ? PolymorphicTypeValidator.Validity.ALLOWED : PolymorphicTypeValidator.Validity.DENIED;
   }

   protected boolean isUnsafeBaseType(MapperConfig<?> config, JavaType baseType) {
      return DefaultBaseTypeLimitingValidator.UnsafeBaseTypes.instance.isUnsafeBaseType(baseType.getRawClass());
   }

   protected boolean isSafeSubType(MapperConfig<?> config, JavaType baseType, JavaType subType) {
      return true;
   }

   private static final class UnsafeBaseTypes {
      public static final UnsafeBaseTypes instance = new UnsafeBaseTypes();
      private final Set<String> UNSAFE = new HashSet();

      private UnsafeBaseTypes() {
         this.UNSAFE.add(Object.class.getName());
         this.UNSAFE.add(Closeable.class.getName());
         this.UNSAFE.add(Serializable.class.getName());
         this.UNSAFE.add(AutoCloseable.class.getName());
         this.UNSAFE.add(Cloneable.class.getName());
         this.UNSAFE.add(Runnable.class.getName());
         this.UNSAFE.add("java.util.logging.Handler");
         this.UNSAFE.add("javax.naming.Referenceable");
         this.UNSAFE.add("javax.sql.DataSource");
      }

      public boolean isUnsafeBaseType(Class<?> rawBaseType) {
         return this.UNSAFE.contains(rawBaseType.getName());
      }
   }
}
