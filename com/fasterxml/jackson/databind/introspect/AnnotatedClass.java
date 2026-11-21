package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public final class AnnotatedClass extends Annotated implements TypeResolutionContext {
   private static final Creators NO_CREATORS = new Creators((AnnotatedConstructor)null, Collections.emptyList(), Collections.emptyList());
   protected final JavaType _type;
   protected final Class<?> _class;
   protected final TypeBindings _bindings;
   protected final List<JavaType> _superTypes;
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final TypeFactory _typeFactory;
   protected final ClassIntrospector.MixInResolver _mixInResolver;
   protected final Class<?> _primaryMixIn;
   protected final boolean _collectAnnotations;
   protected final Annotations _classAnnotations;
   protected Creators _creators;
   protected AnnotatedMethodMap _memberMethods;
   protected List<AnnotatedField> _fields;
   protected transient Boolean _nonStaticInnerClass;

   AnnotatedClass(JavaType type, Class<?> rawType, List<JavaType> superTypes, Class<?> primaryMixIn, Annotations classAnnotations, TypeBindings bindings, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, TypeFactory tf, boolean collectAnnotations) {
      this._type = type;
      this._class = rawType;
      this._superTypes = superTypes;
      this._primaryMixIn = primaryMixIn;
      this._classAnnotations = classAnnotations;
      this._bindings = bindings;
      this._annotationIntrospector = aintr;
      this._mixInResolver = mir;
      this._typeFactory = tf;
      this._collectAnnotations = collectAnnotations;
   }

   AnnotatedClass(Class<?> rawType) {
      this._type = null;
      this._class = rawType;
      this._superTypes = Collections.emptyList();
      this._primaryMixIn = null;
      this._classAnnotations = AnnotationCollector.emptyAnnotations();
      this._bindings = TypeBindings.emptyBindings();
      this._annotationIntrospector = null;
      this._mixInResolver = null;
      this._typeFactory = null;
      this._collectAnnotations = false;
   }

   public JavaType resolveType(Type type) {
      return this._typeFactory.resolveMemberType(type, this._bindings);
   }

   public Class<?> getAnnotated() {
      return this._class;
   }

   public int getModifiers() {
      return this._class.getModifiers();
   }

   public String getName() {
      return this._class.getName();
   }

   public <A extends Annotation> A getAnnotation(Class<A> acls) {
      return (A)this._classAnnotations.get(acls);
   }

   public boolean hasAnnotation(Class<?> acls) {
      return this._classAnnotations.has(acls);
   }

   public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
      return this._classAnnotations.hasOneOf(annoClasses);
   }

   public Class<?> getRawType() {
      return this._class;
   }

   /** @deprecated */
   @Deprecated
   public Iterable<Annotation> annotations() {
      if (this._classAnnotations instanceof AnnotationMap) {
         return ((AnnotationMap)this._classAnnotations).annotations();
      } else if (!(this._classAnnotations instanceof AnnotationCollector.OneAnnotation) && !(this._classAnnotations instanceof AnnotationCollector.TwoAnnotations)) {
         return Collections.emptyList();
      } else {
         throw new UnsupportedOperationException("please use getAnnotations/ hasAnnotation to check for Annotations");
      }
   }

   public JavaType getType() {
      return this._type;
   }

   public Annotations getAnnotations() {
      return this._classAnnotations;
   }

   public boolean hasAnnotations() {
      return this._classAnnotations.size() > 0;
   }

   public AnnotatedConstructor getDefaultConstructor() {
      return this._creators().defaultConstructor;
   }

   public List<AnnotatedConstructor> getConstructors() {
      return this._creators().constructors;
   }

   public List<AnnotatedMethod> getFactoryMethods() {
      return this._creators().creatorMethods;
   }

   public Iterable<AnnotatedMethod> memberMethods() {
      return this._methods();
   }

   public int getMemberMethodCount() {
      return this._methods().size();
   }

   public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
      return this._methods().find(name, paramTypes);
   }

   public int getFieldCount() {
      return this._fields().size();
   }

   public Iterable<AnnotatedField> fields() {
      return this._fields();
   }

   public boolean isNonStaticInnerClass() {
      Boolean B = this._nonStaticInnerClass;
      if (B == null) {
         this._nonStaticInnerClass = B = ClassUtil.isNonStaticInnerClass(this._class);
      }

      return B;
   }

   private final List<AnnotatedField> _fields() {
      List<AnnotatedField> f = this._fields;
      if (f == null) {
         if (this._type == null) {
            f = Collections.emptyList();
         } else {
            f = AnnotatedFieldCollector.collectFields(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type, this._collectAnnotations);
         }

         this._fields = f;
      }

      return f;
   }

   private final AnnotatedMethodMap _methods() {
      AnnotatedMethodMap m = this._memberMethods;
      if (m == null) {
         if (this._type == null) {
            m = new AnnotatedMethodMap();
         } else {
            m = AnnotatedMethodCollector.collectMethods(this._annotationIntrospector, this, this._mixInResolver, this._typeFactory, this._type, this._superTypes, this._primaryMixIn, this._collectAnnotations);
         }

         this._memberMethods = m;
      }

      return m;
   }

   private final Creators _creators() {
      Creators c = this._creators;
      if (c == null) {
         if (this._type == null) {
            c = NO_CREATORS;
         } else {
            c = AnnotatedCreatorCollector.collectCreators(this._annotationIntrospector, this._typeFactory, this, this._type, this._primaryMixIn, this._collectAnnotations);
         }

         this._creators = c;
      }

      return c;
   }

   public String toString() {
      return "[AnnotedClass " + this._class.getName() + "]";
   }

   public int hashCode() {
      return this._class.hashCode();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         return ((AnnotatedClass)o)._class == this._class;
      }
   }

   public static final class Creators {
      public final AnnotatedConstructor defaultConstructor;
      public final List<AnnotatedConstructor> constructors;
      public final List<AnnotatedMethod> creatorMethods;

      public Creators(AnnotatedConstructor defCtor, List<AnnotatedConstructor> ctors, List<AnnotatedMethod> ctorMethods) {
         this.defaultConstructor = defCtor;
         this.constructors = ctors;
         this.creatorMethods = ctorMethods;
      }
   }
}
