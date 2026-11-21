package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.util.LookupCache;
import com.fasterxml.jackson.databind.util.TypeKey;
import java.io.Serializable;

public interface CacheProvider extends Serializable {
   LookupCache<JavaType, JsonDeserializer<Object>> forDeserializerCache(DeserializationConfig var1);

   LookupCache<TypeKey, JsonSerializer<Object>> forSerializerCache(SerializationConfig var1);

   LookupCache<Object, JavaType> forTypeFactory();
}
