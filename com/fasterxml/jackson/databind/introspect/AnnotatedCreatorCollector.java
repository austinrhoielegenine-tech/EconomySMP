package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AnnotatedCreatorCollector extends CollectorBase {
   private final TypeResolutionContext _typeContext;
   private final boolean _collectAnnotations;
   private AnnotatedConstructor _defaultConstructor;

   AnnotatedCreatorCollector(AnnotationIntrospector intr, TypeResolutionContext tc, boolean collectAnnotations) {
      super(intr);
      this._typeContext = tc;
      this._collectAnnotations = collectAnnotations;
   }

   public static AnnotatedClass.Creators collectCreators(AnnotationIntrospector intr, TypeFactory typeFactory, TypeResolutionContext tc, JavaType type, Class<?> primaryMixIn, boolean collectAnnotations) {
      collectAnnotations |= primaryMixIn != null;
      return (new AnnotatedCreatorCollector(intr, tc, collectAnnotations)).collect(typeFactory, type, primaryMixIn);
   }

   AnnotatedClass.Creators collect(TypeFactory typeFactory, JavaType type, Class<?> primaryMixIn) {
      List<AnnotatedConstructor> constructors = this._findPotentialConstructors(type, primaryMixIn);
      List<AnnotatedMethod> factories = this._findPotentialFactories(typeFactory, type, primaryMixIn);
      if (this._collectAnnotations) {
         if (this._defaultConstructor != null && this._intr.hasIgnoreMarker(this._defaultConstructor)) {
            this._defaultConstructor = null;
         }

         int i = constructors.size();

         while(true) {
            --i;
            if (i < 0) {
               i = factories.size();

               while(true) {
                  --i;
                  if (i < 0) {
                     return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
                  }

                  if (this._intr.hasIgnoreMarker((AnnotatedMember)factories.get(i))) {
                     factories.remove(i);
                  }
               }
            }

            if (this._intr.hasIgnoreMarker((AnnotatedMember)constructors.get(i))) {
               constructors.remove(i);
            }
         }
      } else {
         return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
      }
   }

   private List<AnnotatedConstructor> _findPotentialConstructors(JavaType type, Class<?> primaryMixIn) {
      ClassUtil.Ctor defaultCtor = null;
      List<ClassUtil.Ctor> ctors = null;
      if (!type.isEnumType()) {
         ClassUtil.Ctor[] declaredCtors = ClassUtil.getConstructors(type.getRawClass());

         for(ClassUtil.Ctor ctor : declaredCtors) {
            if (isIncludableConstructor(ctor.getConstructor())) {
               if (ctor.getParamCount() == 0) {
                  defaultCtor = ctor;
               } else {
                  if (ctors == null) {
                     ctors = new ArrayList();
                  }

                  ctors.add(ctor);
               }
            }
         }
      }

      List<AnnotatedConstructor> result;
      int ctorCount;
      if (ctors == null) {
         result = Collections.emptyList();
         if (defaultCtor == null) {
            return result;
         }

         ctorCount = 0;
      } else {
         ctorCount = ctors.size();
         result = new ArrayList(ctorCount);

         for(int i = 0; i < ctorCount; ++i) {
            result.add((Object)null);
         }
      }

      if (primaryMixIn != null) {
         MemberKey[] ctorKeys = null;

         for(ClassUtil.Ctor mixinCtor : ClassUtil.getConstructors(primaryMixIn)) {
            if (mixinCtor.getParamCount() == 0) {
               if (defaultCtor != null) {
                  this._defaultConstructor = this.constructDefaultConstructor(defaultCtor, mixinCtor);
                  defaultCtor = null;
               }
            } else if (ctors != null) {
               if (ctorKeys == null) {
                  ctorKeys = new MemberKey[ctorCount];

                  for(int i = 0; i < ctorCount; ++i) {
                     ctorKeys[i] = new MemberKey(((ClassUtil.Ctor)ctors.get(i)).getConstructor());
                  }
               }

               MemberKey key = new MemberKey(mixinCtor.getConstructor());

               for(int i = 0; i < ctorCount; ++i) {
                  if (key.equals(ctorKeys[i])) {
                     result.set(i, this.constructNonDefaultConstructor((ClassUtil.Ctor)ctors.get(i), mixinCtor));
                     break;
                  }
               }
            }
         }
      }

      if (defaultCtor != null) {
         this._defaultConstructor = this.constructDefaultConstructor(defaultCtor, (ClassUtil.Ctor)null);
      }

      for(int i = 0; i < ctorCount; ++i) {
         AnnotatedConstructor ctor = (AnnotatedConstructor)result.get(i);
         if (ctor == null) {
            result.set(i, this.constructNonDefaultConstructor((ClassUtil.Ctor)ctors.get(i), (ClassUtil.Ctor)null));
         }
      }

      return result;
   }

   private List<AnnotatedMethod> _findPotentialFactories(TypeFactory typeFactory, JavaType type, Class<?> primaryMixIn) {
      List<Method> candidates = null;

      for(Method m : ClassUtil.getClassMethods(type.getRawClass())) {
         if (_isIncludableFactoryMethod(m)) {
            if (candidates == null) {
               candidates = new ArrayList();
            }

            candidates.add(m);
         }
      }

      if (candidates == null) {
         return Collections.emptyList();
      } else {
         TypeResolutionContext initialTypeResCtxt = this._typeContext;
         int factoryCount = candidates.size();
         List<AnnotatedMethod> result = new ArrayList(factoryCount);

         for(int i = 0; i < factoryCount; ++i) {
            result.add((Object)null);
         }

         if (primaryMixIn != null) {
            MemberKey[] methodKeys = null;

            for(Method mixinFactory : primaryMixIn.getDeclaredMethods()) {
               if (_isIncludableFactoryMethod(mixinFactory)) {
                  if (methodKeys == null) {
                     methodKeys = new MemberKey[factoryCount];

                     for(int i = 0; i < factoryCount; ++i) {
                        methodKeys[i] = new MemberKey((Method)candidates.get(i));
                     }
                  }

                  MemberKey key = new MemberKey(mixinFactory);

                  for(int i = 0; i < factoryCount; ++i) {
                     if (key.equals(methodKeys[i])) {
                        result.set(i, this.constructFactoryCreator((Method)candidates.get(i), initialTypeResCtxt, mixinFactory));
                        break;
                     }
                  }
               }
            }
         }

         for(int i = 0; i < factoryCount; ++i) {
            AnnotatedMethod factory = (AnnotatedMethod)result.get(i);
            if (factory == null) {
               Method candidate = (Method)candidates.get(i);
               TypeResolutionContext typeResCtxt = MethodGenericTypeResolver.narrowMethodTypeParameters(candidate, type, typeFactory, initialTypeResCtxt);
               result.set(i, this.constructFactoryCreator(candidate, typeResCtxt, (Method)null));
            }
         }

         return result;
      }
   }

   private static boolean _isIncludableFactoryMethod(Method m) {
      if (!Modifier.isStatic(m.getModifiers())) {
         return false;
      } else {
         return !m.isSynthetic();
      }
   }

   protected AnnotatedConstructor constructDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
      return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
   }

   protected AnnotatedConstructor constructNonDefaultConstructor(ClassUtil.Ctor ctor, ClassUtil.Ctor mixin) {
      int paramCount = ctor.getParamCount();
      if (this._intr == null) {
         return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
      } else if (paramCount == 0) {
         return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), NO_ANNOTATION_MAPS);
      } else {
         Annotation[][] paramAnns = ctor.getParameterAnnotations();
         AnnotationMap[] resolvedAnnotations;
         if (paramCount != paramAnns.length) {
            resolvedAnnotations = null;
            Class<?> dc = ctor.getDeclaringClass();
            if (ClassUtil.isEnumType(dc) && paramCount == paramAnns.length + 2) {
               Annotation[][] old = paramAnns;
               paramAnns = new Annotation[paramAnns.length + 2][];
               System.arraycopy(old, 0, paramAnns, 2, old.length);
               resolvedAnnotations = this.collectAnnotations(paramAnns, (Annotation[][])null);
            } else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
               Annotation[][] old = paramAnns;
               paramAnns = new Annotation[paramAnns.length + 1][];
               System.arraycopy(old, 0, paramAnns, 1, old.length);
               paramAnns[0] = NO_ANNOTATIONS;
               resolvedAnnotations = this.collectAnnotations(paramAnns, (Annotation[][])null);
            }

            if (resolvedAnnotations == null) {
               throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", ctor.getDeclaringClass().getName(), paramCount, paramAnns.length));
            }
         } else {
            resolvedAnnotations = this.collectAnnotations(paramAnns, mixin == null ? (Annotation[][])null : mixin.getParameterAnnotations());
         }

         return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), resolvedAnnotations);
      }
   }

   protected AnnotatedMethod constructFactoryCreator(Method m, TypeResolutionContext typeResCtxt, Method mixin) {
      int paramCount = m.getParameterCount();
      if (this._intr == null) {
         return new AnnotatedMethod(typeResCtxt, m, _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
      } else {
         return paramCount == 0 ? new AnnotatedMethod(typeResCtxt, m, this.collectAnnotations((AnnotatedElement)m, (AnnotatedElement)mixin), NO_ANNOTATION_MAPS) : new AnnotatedMethod(typeResCtxt, m, this.collectAnnotations((AnnotatedElement)m, (AnnotatedElement)mixin), this.collectAnnotations(m.getParameterAnnotations(), mixin == null ? (Annotation[][])null : mixin.getParameterAnnotations()));
      }
   }

   private AnnotationMap[] collectAnnotations(Annotation[][] mainAnns, Annotation[][] mixinAnns) {
      if (this._collectAnnotations) {
         int count = mainAnns.length;
         AnnotationMap[] result = new AnnotationMap[count];

         for(int i = 0; i < count; ++i) {
            AnnotationCollector c = this.collectAnnotations((AnnotationCollector)AnnotationCollector.emptyCollector(), (Annotation[])mainAnns[i]);
            if (mixinAnns != null) {
               c = this.collectAnnotations((AnnotationCollector)c, (Annotation[])mixinAnns[i]);
            }

            result[i] = c.asAnnotationMap();
         }

         return result;
      } else {
         return NO_ANNOTATION_MAPS;
      }
   }

   private AnnotationMap collectAnnotations(ClassUtil.Ctor main, ClassUtil.Ctor mixin) {
      if (this._collectAnnotations) {
         AnnotationCollector c = this.collectAnnotations(main.getDeclaredAnnotations());
         if (mixin != null) {
            c = this.collectAnnotations((AnnotationCollector)c, (Annotation[])mixin.getDeclaredAnnotations());
         }

         return c.asAnnotationMap();
      } else {
         return _emptyAnnotationMap();
      }
   }

   private final AnnotationMap collectAnnotations(AnnotatedElement main, AnnotatedElement mixin) {
      AnnotationCollector c = this.collectAnnotations(main.getDeclaredAnnotations());
      if (mixin != null) {
         c = this.collectAnnotations((AnnotationCollector)c, (Annotation[])mixin.getDeclaredAnnotations());
      }

      return c.asAnnotationMap();
   }

   private static boolean isIncludableConstructor(Constructor<?> c) {
      return !c.isSynthetic();
   }
}
