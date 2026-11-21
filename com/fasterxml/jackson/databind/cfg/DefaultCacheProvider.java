package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.LookupCache;
import com.fasterxml.jackson.databind.util.TypeKey;

public class DefaultCacheProvider implements CacheProvider {
   private static final long serialVersionUID = 1L;
   private static final DefaultCacheProvider DEFAULT = new DefaultCacheProvider(2000, 4000, 200);
   protected final int _maxDeserializerCacheSize;
   protected final int _maxSerializerCacheSize;
   protected final int _maxTypeFactoryCacheSize;

   protected DefaultCacheProvider(int maxDeserializerCacheSize, int maxSerializerCacheSize, int maxTypeFactoryCacheSize) {
      this._maxDeserializerCacheSize = maxDeserializerCacheSize;
      this._maxSerializerCacheSize = maxSerializerCacheSize;
      this._maxTypeFactoryCacheSize = maxTypeFactoryCacheSize;
   }

   public static CacheProvider defaultInstance() {
      return DEFAULT;
   }

   public LookupCache<JavaType, JsonDeserializer<Object>> forDeserializerCache(DeserializationConfig config) {
      return this.<JavaType, JsonDeserializer<Object>>_buildCache(this._maxDeserializerCacheSize);
   }

   public LookupCache<TypeKey, JsonSerializer<Object>> forSerializerCache(SerializationConfig config) {
      return this.<TypeKey, JsonSerializer<Object>>_buildCache(this._maxSerializerCacheSize);
   }

   public LookupCache<Object, JavaType> forTypeFactory() {
      return this.<Object, JavaType>_buildCache(this._maxTypeFactoryCacheSize);
   }

   protected <K, V> LookupCache<K, V> _buildCache(int maxSize) {
      int initialSize = Math.min(64, maxSize >> 2);
      return new LRUMap<K, V>(initialSize, maxSize);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int _maxDeserializerCacheSize;
      private int _maxSerializerCacheSize;
      private int _maxTypeFactoryCacheSize;

      Builder() {
      }

      public Builder maxDeserializerCacheSize(int maxDeserializerCacheSize) {
         if (maxDeserializerCacheSize < 0) {
            throw new IllegalArgumentException("Cannot set maxDeserializerCacheSize to a negative value");
         } else {
            this._maxDeserializerCacheSize = maxDeserializerCacheSize;
            return this;
         }
      }

      public Builder maxSerializerCacheSize(int maxSerializerCacheSize) {
         if (maxSerializerCacheSize < 0) {
            throw new IllegalArgumentException("Cannot set maxSerializerCacheSize to a negative value");
         } else {
            this._maxSerializerCacheSize = maxSerializerCacheSize;
            return this;
         }
      }

      public Builder maxTypeFactoryCacheSize(int maxTypeFactoryCacheSize) {
         if (maxTypeFactoryCacheSize < 0) {
            throw new IllegalArgumentException("Cannot set maxTypeFactoryCacheSize to a negative value");
         } else {
            this._maxTypeFactoryCacheSize = maxTypeFactoryCacheSize;
            return this;
         }
      }

      public DefaultCacheProvider build() {
         return new DefaultCacheProvider(this._maxDeserializerCacheSize, this._maxSerializerCacheSize, this._maxTypeFactoryCacheSize);
      }
   }
}
