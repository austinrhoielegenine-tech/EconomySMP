package net.dv8tion.jda.internal.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;

public class EntityString {
   private final Object entity;
   private Object type;
   private String name;
   private List<String> metadata;

   public EntityString(Object entity) {
      this.entity = entity;
   }

   public EntityString setType(@Nonnull Enum<?> type) {
      this.type = type.name();
      return this;
   }

   public EntityString setType(@Nonnull Object type) {
      this.type = type;
      return this;
   }

   public EntityString setName(@Nonnull String name) {
      this.name = name;
      return this;
   }

   public EntityString addMetadata(@Nullable String key, @Nullable Object value) {
      if (this.metadata == null) {
         this.metadata = new ArrayList();
      }

      this.metadata.add(key == null ? String.valueOf(value) : key + "=" + value);
      return this;
   }

   @Nonnull
   public String toString() {
      String entityName;
      if (this.entity instanceof String) {
         entityName = (String)this.entity;
      } else if (this.entity instanceof Class) {
         entityName = getCleanedClassName((Class)this.entity);
      } else {
         entityName = getCleanedClassName(this.entity.getClass());
      }

      StringBuilder sb = new StringBuilder(entityName);
      if (this.type != null) {
         sb.append('[').append(this.type).append(']');
      }

      if (this.name != null) {
         sb.append(':').append(this.name);
      }

      boolean isSnowflake = this.entity instanceof ISnowflake;
      if (isSnowflake || this.metadata != null) {
         StringJoiner metadataJoiner = new StringJoiner(", ", "(", ")");
         if (isSnowflake) {
            metadataJoiner.add("id=" + ((ISnowflake)this.entity).getId());
         }

         if (this.metadata != null) {
            for(Object metadataItem : this.metadata) {
               metadataJoiner.add(metadataItem.toString());
            }
         }

         sb.append(metadataJoiner);
      }

      return sb.toString();
   }

   @Nonnull
   private static String getCleanedClassName(@Nonnull Class<?> clazz) {
      String packageName = clazz.getPackage().getName();
      String fullName = clazz.getName();
      String simpleName = fullName.substring(packageName.length() + 1);
      return simpleName.replace("$", ".").replace("Impl", "");
   }
}
