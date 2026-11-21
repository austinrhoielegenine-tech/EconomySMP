package com.fasterxml.jackson.databind.jdk14;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.PotentialCreator;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NativeImageUtil;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class JDK14Util {
   public static String[] getRecordFieldNames(Class<?> recordType) {
      return JDK14Util.RecordAccessor.instance().getRecordFieldNames(recordType);
   }

   public static PotentialCreator findCanonicalRecordConstructor(MapperConfig<?> config, AnnotatedClass recordClass, List<PotentialCreator> constructors) {
      RawTypeName[] recordFields = JDK14Util.RecordAccessor.instance().getRecordFields(recordClass.getRawType());
      if (recordFields == null) {
         return null;
      } else {
         int argCount = recordFields.length;
         if (argCount == 0) {
            AnnotatedConstructor defCtor = recordClass.getDefaultConstructor();
            if (defCtor != null) {
               return new PotentialCreator(defCtor, (JsonCreator.Mode)null);
            }
         }

         label47:
         for(PotentialCreator ctor : constructors) {
            if (ctor.paramCount() == argCount) {
               for(int i = 0; i < argCount; ++i) {
                  if (!ctor.creator().getRawParameterType(i).equals(recordFields[i].rawType)) {
                     continue label47;
                  }
               }

               PropertyName[] implicits = new PropertyName[argCount];

               for(int i = 0; i < argCount; ++i) {
                  implicits[i] = PropertyName.construct(recordFields[i].name);
               }

               return ctor.introspectParamNames(config, implicits);
            }
         }

         throw new IllegalArgumentException("Failed to find the canonical Record constructor of type " + ClassUtil.getTypeDescription(recordClass.getType()));
      }
   }

   /** @deprecated */
   @Deprecated
   public static AnnotatedConstructor findRecordConstructor(DeserializationContext ctxt, BeanDescription beanDesc, List<String> names) {
      return findRecordConstructor(beanDesc.getClassInfo(), ctxt.getAnnotationIntrospector(), ctxt.getConfig(), names);
   }

   /** @deprecated */
   @Deprecated
   public static AnnotatedConstructor findRecordConstructor(AnnotatedClass recordClass, AnnotationIntrospector intr, MapperConfig<?> config, List<String> names) {
      return (new CreatorLocator(config, recordClass)).locate(names);
   }

   static class RecordAccessor {
      private final Method RECORD_GET_RECORD_COMPONENTS;
      private final Method RECORD_COMPONENT_GET_NAME;
      private final Method RECORD_COMPONENT_GET_TYPE;
      private static final RecordAccessor INSTANCE;
      private static final RuntimeException PROBLEM;

      private RecordAccessor() throws RuntimeException {
         try {
            this.RECORD_GET_RECORD_COMPONENTS = Class.class.getMethod("getRecordComponents");
            Class<?> c = Class.forName("java.lang.reflect.RecordComponent");
            this.RECORD_COMPONENT_GET_NAME = c.getMethod("getName");
            this.RECORD_COMPONENT_GET_TYPE = c.getMethod("getType");
         } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to access Methods needed to support `java.lang.Record`: (%s) %s", e.getClass().getName(), e.getMessage()), e);
         }
      }

      public static RecordAccessor instance() {
         if (PROBLEM != null) {
            throw PROBLEM;
         } else {
            return INSTANCE;
         }
      }

      public String[] getRecordFieldNames(Class<?> recordType) throws IllegalArgumentException {
         Object[] components = this.recordComponents(recordType);
         if (components == null) {
            return null;
         } else {
            String[] names = new String[components.length];

            for(int i = 0; i < components.length; ++i) {
               try {
                  names[i] = (String)this.RECORD_COMPONENT_GET_NAME.invoke(components[i]);
               } catch (Exception e) {
                  throw new IllegalArgumentException(String.format("Failed to access name of field #%d (of %d) of Record type %s", i, components.length, ClassUtil.nameOf(recordType)), e);
               }
            }

            return names;
         }
      }

      public RawTypeName[] getRecordFields(Class<?> recordType) throws IllegalArgumentException {
         Object[] components = this.recordComponents(recordType);
         if (components == null) {
            return null;
         } else {
            RawTypeName[] results = new RawTypeName[components.length];

            for(int i = 0; i < components.length; ++i) {
               String name;
               try {
                  name = (String)this.RECORD_COMPONENT_GET_NAME.invoke(components[i]);
               } catch (Exception e) {
                  throw new IllegalArgumentException(String.format("Failed to access name of field #%d (of %d) of Record type %s", i, components.length, ClassUtil.nameOf(recordType)), e);
               }

               Class<?> type;
               try {
                  type = (Class)this.RECORD_COMPONENT_GET_TYPE.invoke(components[i]);
               } catch (Exception e) {
                  throw new IllegalArgumentException(String.format("Failed to access type of field #%d (of %d) of Record type %s", i, components.length, ClassUtil.nameOf(recordType)), e);
               }

               results[i] = new RawTypeName(type, name);
            }

            return results;
         }
      }

      protected Object[] recordComponents(Class<?> recordType) throws IllegalArgumentException {
         try {
            return this.RECORD_GET_RECORD_COMPONENTS.invoke(recordType);
         } catch (Exception e) {
            if (NativeImageUtil.isUnsupportedFeatureError(e)) {
               return null;
            } else {
               throw new IllegalArgumentException("Failed to access RecordComponents of type " + ClassUtil.nameOf(recordType));
            }
         }
      }

      static {
         RuntimeException prob = null;
         RecordAccessor inst = null;

         try {
            inst = new RecordAccessor();
         } catch (RuntimeException e) {
            prob = e;
         }

         INSTANCE = inst;
         PROBLEM = prob;
      }
   }

   static class RawTypeName {
      public final Class<?> rawType;
      public final String name;

      public RawTypeName(Class<?> rt, String n) {
         this.rawType = rt;
         this.name = n;
      }
   }

   /** @deprecated */
   @Deprecated
   static class CreatorLocator {
      protected final AnnotatedClass _recordClass;
      protected final MapperConfig<?> _config;
      protected final AnnotationIntrospector _intr;
      protected final List<AnnotatedConstructor> _constructors;
      protected final AnnotatedConstructor _primaryConstructor;
      protected final RawTypeName[] _recordFields;

      CreatorLocator(MapperConfig<?> config, AnnotatedClass recordClass) {
         this._recordClass = recordClass;
         this._intr = config.getAnnotationIntrospector();
         this._config = config;
         this._recordFields = JDK14Util.RecordAccessor.instance().getRecordFields(recordClass.getRawType());
         if (this._recordFields == null) {
            this._constructors = recordClass.getConstructors();
            this._primaryConstructor = null;
         } else {
            int argCount = this._recordFields.length;
            AnnotatedConstructor primary = null;
            if (argCount == 0) {
               primary = recordClass.getDefaultConstructor();
               this._constructors = Collections.singletonList(primary);
            } else {
               this._constructors = recordClass.getConstructors();

               label40:
               for(AnnotatedConstructor ctor : this._constructors) {
                  if (ctor.getParameterCount() == argCount) {
                     for(int i = 0; i < argCount; ++i) {
                        if (!ctor.getRawParameterType(i).equals(this._recordFields[i].rawType)) {
                           continue label40;
                        }
                     }

                     primary = ctor;
                     break;
                  }
               }
            }

            if (primary == null) {
               throw new IllegalArgumentException("Failed to find the canonical Record constructor of type " + ClassUtil.getTypeDescription(this._recordClass.getType()));
            }

            this._primaryConstructor = primary;
         }

      }

      public AnnotatedConstructor locate(List<String> names) {
         for(AnnotatedConstructor ctor : this._constructors) {
            JsonCreator.Mode creatorMode = this._intr.findCreatorAnnotation(this._config, ctor);
            if (null != creatorMode && JsonCreator.Mode.DISABLED != creatorMode) {
               if (JsonCreator.Mode.DELEGATING == creatorMode) {
                  return null;
               }

               if (ctor != this._primaryConstructor) {
                  return null;
               }
            }
         }

         if (this._recordFields == null) {
            return null;
         } else {
            for(RawTypeName field : this._recordFields) {
               names.add(field.name);
            }

            return this._primaryConstructor;
         }
      }
   }
}
