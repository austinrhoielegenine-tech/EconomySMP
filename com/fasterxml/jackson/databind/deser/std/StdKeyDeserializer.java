package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.EnumFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@JacksonStdImpl
public class StdKeyDeserializer extends KeyDeserializer implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int TYPE_BOOLEAN = 1;
   public static final int TYPE_BYTE = 2;
   public static final int TYPE_SHORT = 3;
   public static final int TYPE_CHAR = 4;
   public static final int TYPE_INT = 5;
   public static final int TYPE_LONG = 6;
   public static final int TYPE_FLOAT = 7;
   public static final int TYPE_DOUBLE = 8;
   public static final int TYPE_LOCALE = 9;
   public static final int TYPE_DATE = 10;
   public static final int TYPE_CALENDAR = 11;
   public static final int TYPE_UUID = 12;
   public static final int TYPE_URI = 13;
   public static final int TYPE_URL = 14;
   public static final int TYPE_CLASS = 15;
   public static final int TYPE_CURRENCY = 16;
   public static final int TYPE_BYTE_ARRAY = 17;
   protected final int _kind;
   protected final Class<?> _keyClass;
   protected final FromStringDeserializer<?> _deser;

   protected StdKeyDeserializer(int kind, Class<?> cls) {
      this(kind, cls, (FromStringDeserializer)null);
   }

   protected StdKeyDeserializer(int kind, Class<?> cls, FromStringDeserializer<?> deser) {
      this._kind = kind;
      this._keyClass = cls;
      this._deser = deser;
   }

   public static StdKeyDeserializer forType(Class<?> raw) {
      if (raw != String.class && raw != Object.class && raw != CharSequence.class && raw != Serializable.class) {
         int kind;
         if (raw == UUID.class) {
            kind = 12;
         } else if (raw == Integer.class) {
            kind = 5;
         } else if (raw == Long.class) {
            kind = 6;
         } else if (raw == Date.class) {
            kind = 10;
         } else if (raw == Calendar.class) {
            kind = 11;
         } else if (raw == Boolean.class) {
            kind = 1;
         } else if (raw == Byte.class) {
            kind = 2;
         } else if (raw == Character.class) {
            kind = 4;
         } else if (raw == Short.class) {
            kind = 3;
         } else if (raw == Float.class) {
            kind = 7;
         } else if (raw == Double.class) {
            kind = 8;
         } else if (raw == URI.class) {
            kind = 13;
         } else if (raw == URL.class) {
            kind = 14;
         } else if (raw == Class.class) {
            kind = 15;
         } else {
            if (raw == Locale.class) {
               FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Locale.class);
               return new StdKeyDeserializer(9, raw, deser);
            }

            if (raw == Currency.class) {
               FromStringDeserializer<?> deser = FromStringDeserializer.findDeserializer(Currency.class);
               return new StdKeyDeserializer(16, raw, deser);
            }

            if (raw != byte[].class) {
               return null;
            }

            kind = 17;
         }

         return new StdKeyDeserializer(kind, raw);
      } else {
         return StdKeyDeserializer.StringKD.forType(raw);
      }
   }

   public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
      if (key == null) {
         return null;
      } else {
         try {
            Object result = this._parse(key, ctxt);
            if (result != null) {
               return result;
            }
         } catch (Exception re) {
            return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation, problem: (%s) %s", re.getClass().getName(), ClassUtil.exceptionMessage(re));
         }

         return ClassUtil.isEnumType(this._keyClass) && ctxt.getConfig().isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL) ? null : ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation");
      }
   }

   public Class<?> getKeyClass() {
      return this._keyClass;
   }

   protected Object _parse(String key, DeserializationContext ctxt) throws Exception {
      switch (this._kind) {
         case 1:
            if ("true".equals(key)) {
               return Boolean.TRUE;
            } else {
               if ("false".equals(key)) {
                  return Boolean.FALSE;
               }

               return ctxt.handleWeirdKey(this._keyClass, key, "value not 'true' or 'false'");
            }
         case 2:
            int value = this._parseInt(key);
            if (value >= -128 && value <= 255) {
               return (byte)value;
            }

            return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 8-bit value");
         case 3:
            int value = this._parseInt(key);
            if (value >= -32768 && value <= 32767) {
               return (short)value;
            }

            return ctxt.handleWeirdKey(this._keyClass, key, "overflow, value cannot be represented as 16-bit value");
         case 4:
            if (key.length() == 1) {
               return key.charAt(0);
            }

            return ctxt.handleWeirdKey(this._keyClass, key, "can only convert 1-character Strings");
         case 5:
            return this._parseInt(key);
         case 6:
            return this._parseLong(key);
         case 7:
            return (float)this._parseDouble(key);
         case 8:
            return this._parseDouble(key);
         case 9:
         case 16:
            try {
               return this._deser._deserialize(key, ctxt);
            } catch (IllegalArgumentException e) {
               return this._weirdKey(ctxt, key, e);
            }
         case 10:
            return ctxt.parseDate(key);
         case 11:
            return ctxt.constructCalendar(ctxt.parseDate(key));
         case 12:
            try {
               return UUID.fromString(key);
            } catch (Exception e) {
               return this._weirdKey(ctxt, key, e);
            }
         case 13:
            try {
               return URI.create(key);
            } catch (Exception e) {
               return this._weirdKey(ctxt, key, e);
            }
         case 14:
            try {
               return new URL(key);
            } catch (MalformedURLException e) {
               return this._weirdKey(ctxt, key, e);
            }
         case 15:
            try {
               return ctxt.findClass(key);
            } catch (Exception var5) {
               return ctxt.handleWeirdKey(this._keyClass, key, "unable to parse key as Class");
            }
         case 17:
            try {
               return ctxt.getConfig().getBase64Variant().decode(key);
            } catch (IllegalArgumentException e) {
               return this._weirdKey(ctxt, key, e);
            }
         default:
            throw new IllegalStateException("Internal error: unknown key type " + this._keyClass);
      }
   }

   protected int _parseInt(String key) throws IllegalArgumentException {
      return NumberInput.parseInt(key);
   }

   protected long _parseLong(String key) throws IllegalArgumentException {
      return NumberInput.parseLong(key);
   }

   protected double _parseDouble(String key) throws IllegalArgumentException {
      return NumberInput.parseDouble(key, false);
   }

   protected Object _weirdKey(DeserializationContext ctxt, String key, Exception e) throws IOException {
      return ctxt.handleWeirdKey(this._keyClass, key, "problem: %s", ClassUtil.exceptionMessage(e));
   }

   @JacksonStdImpl
   static final class StringKD extends StdKeyDeserializer {
      private static final long serialVersionUID = 1L;
      private static final StringKD sString = new StringKD(String.class);
      private static final StringKD sObject = new StringKD(Object.class);

      private StringKD(Class<?> nominalType) {
         super(-1, nominalType);
      }

      public static StringKD forType(Class<?> nominalType) {
         if (nominalType == String.class) {
            return sString;
         } else {
            return nominalType == Object.class ? sObject : new StringKD(nominalType);
         }
      }

      public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
         return key;
      }
   }

   static final class DelegatingKD extends KeyDeserializer implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final Class<?> _keyClass;
      protected final JsonDeserializer<?> _delegate;

      protected DelegatingKD(Class<?> cls, JsonDeserializer<?> deser) {
         this._keyClass = cls;
         this._delegate = deser;
      }

      public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
         if (key == null) {
            return null;
         } else {
            TokenBuffer tb = ctxt.bufferForInputBuffering();
            tb.writeString(key);

            try {
               JsonParser p = tb.asParser();
               p.nextToken();
               Object result = this._delegate.deserialize(p, ctxt);
               return result != null ? result : ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation");
            } catch (Exception re) {
               return ctxt.handleWeirdKey(this._keyClass, key, "not a valid representation: %s", re.getMessage());
            }
         }
      }

      public Class<?> getKeyClass() {
         return this._keyClass;
      }
   }

   @JacksonStdImpl
   static final class EnumKD extends StdKeyDeserializer {
      private static final long serialVersionUID = 1L;
      protected final EnumResolver _byNameResolver;
      protected final AnnotatedMethod _factory;
      protected volatile EnumResolver _byToStringResolver;
      protected volatile EnumResolver _byIndexResolver;
      protected final EnumResolver _byEnumNamingResolver;
      protected final Enum<?> _enumDefaultValue;

      protected EnumKD(EnumResolver er, AnnotatedMethod factory) {
         super(-1, er.getEnumClass());
         this._byNameResolver = er;
         this._factory = factory;
         this._enumDefaultValue = er.getDefaultValue();
         this._byEnumNamingResolver = null;
         this._byToStringResolver = null;
      }

      protected EnumKD(EnumResolver er, AnnotatedMethod factory, EnumResolver byEnumNamingResolver, EnumResolver byToStringResolver, EnumResolver byIndexResolver) {
         super(-1, er.getEnumClass());
         this._byNameResolver = er;
         this._factory = factory;
         this._enumDefaultValue = er.getDefaultValue();
         this._byEnumNamingResolver = byEnumNamingResolver;
         this._byToStringResolver = byToStringResolver;
         this._byIndexResolver = byIndexResolver;
      }

      public Object _parse(String key, DeserializationContext ctxt) throws IOException {
         if (this._factory != null) {
            try {
               return this._factory.call1(key);
            } catch (Exception e) {
               ClassUtil.unwrapAndThrowAsIAE(e);
            }
         }

         EnumResolver res = this._resolveCurrentResolver(ctxt);
         Enum<?> e = res.findEnum(key);
         if (e == null && ctxt.isEnabled((DatatypeFeature)EnumFeature.READ_ENUM_KEYS_USING_INDEX)) {
            res = this._getIndexResolver(ctxt);
            e = res.findEnum(key);
         }

         if (e == null) {
            if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
               e = this._enumDefaultValue;
            } else if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
               return ctxt.handleWeirdKey(this._keyClass, key, "not one of the values accepted for Enum class: %s", res.getEnumIds());
            }
         }

         return e;
      }

      protected EnumResolver _resolveCurrentResolver(DeserializationContext ctxt) {
         if (this._byEnumNamingResolver != null) {
            return this._byEnumNamingResolver;
         } else {
            return !this._byNameResolver.hasAsValueAnnotation() && ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? this._getToStringResolver(ctxt) : this._byNameResolver;
         }
      }

      /** @deprecated */
      @Deprecated
      private EnumResolver _getToStringResolver(DeserializationContext ctxt) {
         EnumResolver res = this._byToStringResolver;
         if (res == null) {
            synchronized(this) {
               res = this._byToStringResolver;
               if (res == null) {
                  DeserializationConfig config = ctxt.getConfig();
                  AnnotatedClass ac = config.introspectClassAnnotations(this._byNameResolver.getEnumClass()).getClassInfo();
                  this._byToStringResolver = EnumResolver.constructUsingToString(config, ac);
               }
            }
         }

         return res;
      }

      /** @deprecated */
      @Deprecated
      private EnumResolver _getIndexResolver(DeserializationContext ctxt) {
         EnumResolver res = this._byIndexResolver;
         if (res == null) {
            synchronized(this) {
               res = this._byIndexResolver;
               if (res == null) {
                  DeserializationConfig config = ctxt.getConfig();
                  AnnotatedClass ac = config.introspectClassAnnotations(this._byNameResolver.getEnumClass()).getClassInfo();
                  this._byIndexResolver = EnumResolver.constructUsingIndex(config, ac);
               }
            }
         }

         return res;
      }
   }

   static final class StringCtorKeyDeserializer extends StdKeyDeserializer {
      private static final long serialVersionUID = 1L;
      protected final Constructor<?> _ctor;

      public StringCtorKeyDeserializer(Constructor<?> ctor) {
         super(-1, ctor.getDeclaringClass());
         this._ctor = ctor;
      }

      public Object _parse(String key, DeserializationContext ctxt) throws Exception {
         return this._ctor.newInstance(key);
      }
   }

   static final class StringFactoryKeyDeserializer extends StdKeyDeserializer {
      private static final long serialVersionUID = 1L;
      final Method _factoryMethod;

      public StringFactoryKeyDeserializer(Method fm) {
         super(-1, fm.getDeclaringClass());
         this._factoryMethod = fm;
      }

      public Object _parse(String key, DeserializationContext ctxt) throws Exception {
         return this._factoryMethod.invoke((Object)null, key);
      }
   }
}
