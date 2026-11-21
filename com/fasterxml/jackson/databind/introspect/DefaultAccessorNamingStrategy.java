package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jdk14.JDK14Util;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultAccessorNamingStrategy extends AccessorNamingStrategy {
   protected final MapperConfig<?> _config;
   protected final AnnotatedClass _forClass;
   protected final BaseNameValidator _baseNameValidator;
   protected final boolean _stdBeanNaming;
   protected final boolean _isGettersNonBoolean;
   protected final String _getterPrefix;
   protected final String _isGetterPrefix;
   protected final String _mutatorPrefix;

   protected DefaultAccessorNamingStrategy(MapperConfig<?> config, AnnotatedClass forClass, String mutatorPrefix, String getterPrefix, String isGetterPrefix, BaseNameValidator baseNameValidator) {
      this._config = config;
      this._forClass = forClass;
      this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
      this._isGettersNonBoolean = config.isEnabled(MapperFeature.ALLOW_IS_GETTERS_FOR_NON_BOOLEAN);
      this._mutatorPrefix = mutatorPrefix;
      this._getterPrefix = getterPrefix;
      this._isGetterPrefix = isGetterPrefix;
      this._baseNameValidator = baseNameValidator;
   }

   public String findNameForIsGetter(AnnotatedMethod am, String name) {
      if (this._isGetterPrefix != null && (this._isGettersNonBoolean || this._booleanType(am.getType())) && name.startsWith(this._isGetterPrefix)) {
         return this._stdBeanNaming ? this.stdManglePropertyName(name, this._isGetterPrefix.length()) : this.legacyManglePropertyName(name, this._isGetterPrefix.length());
      } else {
         return null;
      }
   }

   private boolean _booleanType(JavaType type) {
      if (type.isReferenceType()) {
         type = type.getReferencedType();
      }

      return type.hasRawClass(Boolean.TYPE) || type.hasRawClass(Boolean.class) || type.hasRawClass(AtomicBoolean.class);
   }

   public String findNameForRegularGetter(AnnotatedMethod am, String name) {
      if (this._getterPrefix != null && name.startsWith(this._getterPrefix)) {
         if ("getCallbacks".equals(name)) {
            if (this._isCglibGetCallbacks(am)) {
               return null;
            }
         } else if ("getMetaClass".equals(name) && this._isGroovyMetaClassGetter(am)) {
            return null;
         }

         return this._stdBeanNaming ? this.stdManglePropertyName(name, this._getterPrefix.length()) : this.legacyManglePropertyName(name, this._getterPrefix.length());
      } else {
         return null;
      }
   }

   public String findNameForMutator(AnnotatedMethod am, String name) {
      if (this._mutatorPrefix != null && name.startsWith(this._mutatorPrefix)) {
         return this._stdBeanNaming ? this.stdManglePropertyName(name, this._mutatorPrefix.length()) : this.legacyManglePropertyName(name, this._mutatorPrefix.length());
      } else {
         return null;
      }
   }

   public String modifyFieldName(AnnotatedField field, String name) {
      return name;
   }

   protected String legacyManglePropertyName(String basename, int offset) {
      int end = basename.length();
      if (end == offset) {
         return null;
      } else {
         char c = basename.charAt(offset);
         if (this._baseNameValidator != null && !this._baseNameValidator.accept(c, basename, offset)) {
            return null;
         } else {
            char d = Character.toLowerCase(c);
            if (c == d) {
               return basename.substring(offset);
            } else {
               StringBuilder sb = new StringBuilder(end - offset);
               sb.append(d);

               for(int i = offset + 1; i < end; ++i) {
                  c = basename.charAt(i);
                  d = Character.toLowerCase(c);
                  if (c == d) {
                     sb.append(basename, i, end);
                     break;
                  }

                  sb.append(d);
               }

               return sb.toString();
            }
         }
      }
   }

   protected String stdManglePropertyName(String basename, int offset) {
      int end = basename.length();
      if (end == offset) {
         return null;
      } else {
         char c0 = basename.charAt(offset);
         if (this._baseNameValidator != null && !this._baseNameValidator.accept(c0, basename, offset)) {
            return null;
         } else {
            char c1 = Character.toLowerCase(c0);
            if (c0 == c1) {
               return basename.substring(offset);
            } else if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
               return basename.substring(offset);
            } else {
               StringBuilder sb = new StringBuilder(end - offset);
               sb.append(c1);
               sb.append(basename, offset + 1, end);
               return sb.toString();
            }
         }
      }
   }

   protected boolean _isCglibGetCallbacks(AnnotatedMethod am) {
      Class<?> rt = am.getRawType();
      if (rt.isArray()) {
         Class<?> compType = rt.getComponentType();
         String className = compType.getName();
         if (className.contains(".cglib")) {
            return className.startsWith("net.sf.cglib") || className.startsWith("org.hibernate.repackage.cglib") || className.startsWith("org.springframework.cglib");
         }
      }

      return false;
   }

   protected boolean _isGroovyMetaClassGetter(AnnotatedMethod am) {
      return am.getRawType().getName().startsWith("groovy.lang");
   }

   public static class Provider extends AccessorNamingStrategy.Provider implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final String _setterPrefix;
      protected final String _withPrefix;
      protected final String _getterPrefix;
      protected final String _isGetterPrefix;
      protected final BaseNameValidator _baseNameValidator;

      public Provider() {
         this((String)"set", "with", "get", "is", (BaseNameValidator)null);
      }

      protected Provider(Provider p, String setterPrefix, String withPrefix, String getterPrefix, String isGetterPrefix) {
         this(setterPrefix, withPrefix, getterPrefix, isGetterPrefix, p._baseNameValidator);
      }

      protected Provider(Provider p, BaseNameValidator vld) {
         this(p._setterPrefix, p._withPrefix, p._getterPrefix, p._isGetterPrefix, vld);
      }

      protected Provider(String setterPrefix, String withPrefix, String getterPrefix, String isGetterPrefix, BaseNameValidator vld) {
         this._setterPrefix = setterPrefix;
         this._withPrefix = withPrefix;
         this._getterPrefix = getterPrefix;
         this._isGetterPrefix = isGetterPrefix;
         this._baseNameValidator = vld;
      }

      public Provider withSetterPrefix(String prefix) {
         return new Provider(this, prefix, this._withPrefix, this._getterPrefix, this._isGetterPrefix);
      }

      public Provider withBuilderPrefix(String prefix) {
         return new Provider(this, this._setterPrefix, prefix, this._getterPrefix, this._isGetterPrefix);
      }

      public Provider withGetterPrefix(String prefix) {
         return new Provider(this, this._setterPrefix, this._withPrefix, prefix, this._isGetterPrefix);
      }

      public Provider withIsGetterPrefix(String prefix) {
         return new Provider(this, this._setterPrefix, this._withPrefix, this._getterPrefix, prefix);
      }

      public Provider withFirstCharAcceptance(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
         return this.withBaseNameValidator(DefaultAccessorNamingStrategy.FirstCharBasedValidator.forFirstNameRule(allowLowerCaseFirstChar, allowNonLetterFirstChar));
      }

      public Provider withBaseNameValidator(BaseNameValidator vld) {
         return new Provider(this, vld);
      }

      public AccessorNamingStrategy forPOJO(MapperConfig<?> config, AnnotatedClass targetClass) {
         return new DefaultAccessorNamingStrategy(config, targetClass, this._setterPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
      }

      public AccessorNamingStrategy forBuilder(MapperConfig<?> config, AnnotatedClass builderClass, BeanDescription valueTypeDesc) {
         AnnotationIntrospector ai = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
         JsonPOJOBuilder.Value builderConfig = ai == null ? null : ai.findPOJOBuilderConfig(builderClass);
         String mutatorPrefix = builderConfig == null ? this._withPrefix : builderConfig.withPrefix;
         return new DefaultAccessorNamingStrategy(config, builderClass, mutatorPrefix, this._getterPrefix, this._isGetterPrefix, this._baseNameValidator);
      }

      public AccessorNamingStrategy forRecord(MapperConfig<?> config, AnnotatedClass recordClass) {
         return new RecordNaming(config, recordClass);
      }
   }

   public static class FirstCharBasedValidator implements BaseNameValidator {
      private final boolean _allowLowerCaseFirstChar;
      private final boolean _allowNonLetterFirstChar;

      protected FirstCharBasedValidator(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
         this._allowLowerCaseFirstChar = allowLowerCaseFirstChar;
         this._allowNonLetterFirstChar = allowNonLetterFirstChar;
      }

      public static BaseNameValidator forFirstNameRule(boolean allowLowerCaseFirstChar, boolean allowNonLetterFirstChar) {
         return allowLowerCaseFirstChar && allowNonLetterFirstChar ? null : new FirstCharBasedValidator(allowLowerCaseFirstChar, allowNonLetterFirstChar);
      }

      public boolean accept(char firstChar, String basename, int offset) {
         if (!Character.isLetter(firstChar)) {
            return this._allowNonLetterFirstChar;
         } else {
            return this._allowLowerCaseFirstChar || !Character.isLowerCase(firstChar);
         }
      }
   }

   public static class RecordNaming extends DefaultAccessorNamingStrategy {
      protected final Set<String> _fieldNames;

      public RecordNaming(MapperConfig<?> config, AnnotatedClass forClass) {
         super(config, forClass, (String)null, "get", "is", (BaseNameValidator)null);
         String[] recordFieldNames = JDK14Util.getRecordFieldNames(forClass.getRawType());
         this._fieldNames = (Set<String>)(recordFieldNames == null ? Collections.emptySet() : new HashSet(Arrays.asList(recordFieldNames)));
      }

      public String findNameForRegularGetter(AnnotatedMethod am, String name) {
         return this._fieldNames.contains(name) ? name : super.findNameForRegularGetter(am, name);
      }
   }

   public interface BaseNameValidator {
      boolean accept(char var1, String var2, int var3);
   }
}
