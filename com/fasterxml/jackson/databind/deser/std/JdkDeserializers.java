package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JdkDeserializers {
   private static final HashSet<String> _classNames = new HashSet();

   public static JsonDeserializer<?> find(DeserializationContext ctxt, Class<?> rawType, String clsName) throws JsonMappingException {
      if (_classNames.contains(clsName)) {
         JsonDeserializer<?> d = FromStringDeserializer.findDeserializer(rawType);
         if (d != null) {
            return d;
         }

         if (rawType == UUID.class) {
            return new UUIDDeserializer();
         }

         if (rawType == StackTraceElement.class) {
            return StackTraceElementDeserializer.construct(ctxt);
         }

         if (rawType == AtomicBoolean.class) {
            return new AtomicBooleanDeserializer();
         }

         if (rawType == AtomicInteger.class) {
            return new AtomicIntegerDeserializer();
         }

         if (rawType == AtomicLong.class) {
            return new AtomicLongDeserializer();
         }

         if (rawType == ByteBuffer.class) {
            return new ByteBufferDeserializer();
         }

         if (rawType == Void.class) {
            return NullifyingDeserializer.instance;
         }

         if (rawType == ThreadGroup.class) {
            return new ThreadGroupDeserializer();
         }
      }

      return null;
   }

   public static boolean hasDeserializerFor(Class<?> rawType) {
      return _classNames.contains(rawType.getName());
   }

   static {
      Class<?>[] types = new Class[]{UUID.class, AtomicBoolean.class, AtomicInteger.class, AtomicLong.class, StackTraceElement.class, ByteBuffer.class, Void.class, ThreadGroup.class};

      for(Class<?> cls : types) {
         _classNames.add(cls.getName());
      }

      for(Class<?> cls : FromStringDeserializer.types()) {
         _classNames.add(cls.getName());
      }

   }
}
