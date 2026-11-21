package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.LookupCache;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TypeFactory implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int DEFAULT_MAX_CACHE_SIZE = 200;
   private static final JavaType[] NO_TYPES = new JavaType[0];
   protected static final TypeFactory instance = new TypeFactory();
   protected static final TypeBindings EMPTY_BINDINGS = TypeBindings.emptyBindings();
   private static final Class<?> CLS_STRING = String.class;
   private static final Class<?> CLS_OBJECT = Object.class;
   private static final Class<?> CLS_COMPARABLE = Comparable.class;
   private static final Class<?> CLS_ENUM = Enum.class;
   private static final Class<?> CLS_JSON_NODE = JsonNode.class;
   private static final Class<?> CLS_BOOL;
   private static final Class<?> CLS_DOUBLE;
   private static final Class<?> CLS_INT;
   private static final Class<?> CLS_LONG;
   protected static final SimpleType CORE_TYPE_BOOL;
   protected static final SimpleType CORE_TYPE_DOUBLE;
   protected static final SimpleType CORE_TYPE_INT;
   protected static final SimpleType CORE_TYPE_LONG;
   protected static final SimpleType CORE_TYPE_STRING;
   protected static final SimpleType CORE_TYPE_OBJECT;
   protected static final SimpleType CORE_TYPE_COMPARABLE;
   protected static final SimpleType CORE_TYPE_ENUM;
   protected static final SimpleType CORE_TYPE_JSON_NODE;
   protected final LookupCache<Object, JavaType> _typeCache;
   protected final TypeModifier[] _modifiers;
   protected final TypeParser _parser;
   protected final ClassLoader _classLoader;

   private TypeFactory() {
      this(new LRUMap(16, 200));
   }

   protected TypeFactory(LookupCache<Object, JavaType> typeCache) {
      this._typeCache = (LookupCache)Objects.requireNonNull(typeCache);
      this._parser = new TypeParser(this);
      this._modifiers = null;
      this._classLoader = null;
   }

   protected TypeFactory(LookupCache<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader) {
      if (typeCache == null) {
         typeCache = new LRUMap<Object, JavaType>(16, 200);
      }

      this._typeCache = typeCache;
      this._parser = p.withFactory(this);
      this._modifiers = mods;
      this._classLoader = classLoader;
   }

   public static TypeFactory createDefaultInstance() {
      return new TypeFactory();
   }

   public TypeFactory withModifier(TypeModifier mod) {
      LookupCache<Object, JavaType> typeCache = this._typeCache;
      TypeModifier[] mods;
      if (mod == null) {
         mods = null;
         typeCache = null;
      } else if (this._modifiers == null) {
         mods = new TypeModifier[]{mod};
         typeCache = null;
      } else {
         mods = (TypeModifier[])ArrayBuilders.insertInListNoDup(this._modifiers, mod);
      }

      return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
   }

   public TypeFactory withClassLoader(ClassLoader classLoader) {
      return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
   }

   /** @deprecated */
   @Deprecated
   public TypeFactory withCache(LRUMap<Object, JavaType> cache) {
      return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
   }

   public TypeFactory withCache(LookupCache<Object, JavaType> cache) {
      return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
   }

   public static TypeFactory defaultInstance() {
      return instance;
   }

   public void clearCache() {
      this._typeCache.clear();
   }

   public ClassLoader getClassLoader() {
      return this._classLoader;
   }

   public static JavaType unknownType() {
      return CORE_TYPE_OBJECT;
   }

   public static Class<?> rawClass(Type t) {
      if (t instanceof Class) {
         return (Class)t;
      } else if (t instanceof JavaType) {
         return ((JavaType)t).getRawClass();
      } else if (t instanceof GenericArrayType) {
         return Array.newInstance(rawClass(((GenericArrayType)t).getGenericComponentType()), 0).getClass();
      } else if (t instanceof ParameterizedType) {
         return rawClass(((ParameterizedType)t).getRawType());
      } else if (t instanceof TypeVariable) {
         return rawClass(((TypeVariable)t).getBounds()[0]);
      } else {
         return t instanceof WildcardType ? rawClass(((WildcardType)t).getUpperBounds()[0]) : defaultInstance().constructType(t).getRawClass();
      }
   }

   public Class<?> findClass(String className) throws ClassNotFoundException {
      if (className.indexOf(46) < 0) {
         Class<?> prim = this._findPrimitive(className);
         if (prim != null) {
            return prim;
         }
      }

      Throwable prob = null;
      ClassLoader loader = this.getClassLoader();
      if (loader == null) {
         loader = Thread.currentThread().getContextClassLoader();
      }

      if (loader != null) {
         try {
            return this.classForName(className, true, loader);
         } catch (Exception e) {
            prob = ClassUtil.getRootCause(e);
         }
      }

      try {
         return this.classForName(className);
      } catch (Exception e) {
         if (prob == null) {
            prob = ClassUtil.getRootCause(e);
         }

         ClassUtil.throwIfRTE(prob);
         throw new ClassNotFoundException(prob.getMessage(), prob);
      }
   }

   protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
      return Class.forName(name, true, loader);
   }

   protected Class<?> classForName(String name) throws ClassNotFoundException {
      return Class.forName(name);
   }

   protected Class<?> _findPrimitive(String className) {
      if ("int".equals(className)) {
         return Integer.TYPE;
      } else if ("long".equals(className)) {
         return Long.TYPE;
      } else if ("float".equals(className)) {
         return Float.TYPE;
      } else if ("double".equals(className)) {
         return Double.TYPE;
      } else if ("boolean".equals(className)) {
         return Boolean.TYPE;
      } else if ("byte".equals(className)) {
         return Byte.TYPE;
      } else if ("char".equals(className)) {
         return Character.TYPE;
      } else if ("short".equals(className)) {
         return Short.TYPE;
      } else {
         return "void".equals(className) ? Void.TYPE : null;
      }
   }

   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass) throws IllegalArgumentException {
      return this.constructSpecializedType(baseType, subclass, false);
   }

   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass, boolean relaxedCompatibilityCheck) throws IllegalArgumentException {
      Class<?> rawBase = baseType.getRawClass();
      if (rawBase == subclass) {
         return baseType;
      } else {
         JavaType newType;
         if (rawBase == Object.class) {
            newType = this._fromClass((ClassStack)null, subclass, EMPTY_BINDINGS);
         } else {
            label71: {
               if (!rawBase.isAssignableFrom(subclass)) {
                  throw new IllegalArgumentException(String.format("Class %s not subtype of %s", ClassUtil.nameOf(subclass), ClassUtil.getTypeDescription(baseType)));
               }

               if (baseType.isContainerType()) {
                  if (baseType.isMapLikeType()) {
                     if (subclass == HashMap.class || subclass == LinkedHashMap.class || subclass == EnumMap.class || subclass == TreeMap.class) {
                        newType = this._fromClass((ClassStack)null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
                        break label71;
                     }
                  } else if (baseType.isCollectionLikeType()) {
                     if (subclass == ArrayList.class || subclass == LinkedList.class || subclass == HashSet.class || subclass == TreeSet.class) {
                        newType = this._fromClass((ClassStack)null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
                        break label71;
                     }

                     if (rawBase == EnumSet.class) {
                        return baseType;
                     }
                  }
               }

               if (baseType.getBindings().isEmpty()) {
                  newType = this._fromClass((ClassStack)null, subclass, EMPTY_BINDINGS);
               } else {
                  int typeParamCount = subclass.getTypeParameters().length;
                  if (typeParamCount == 0) {
                     newType = this._fromClass((ClassStack)null, subclass, EMPTY_BINDINGS);
                  } else {
                     TypeBindings tb = this._bindingsForSubtype(baseType, typeParamCount, subclass, relaxedCompatibilityCheck);
                     newType = this._fromClass((ClassStack)null, subclass, tb);
                  }
               }
            }
         }

         newType = newType.withHandlersFrom(baseType);
         return newType;
      }
   }

   private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass, boolean relaxedCompatibilityCheck) {
      PlaceholderForType[] placeholders = new PlaceholderForType[typeParamCount];

      for(int i = 0; i < typeParamCount; ++i) {
         placeholders[i] = new PlaceholderForType(i);
      }

      TypeBindings b = TypeBindings.create(subclass, placeholders);
      JavaType tmpSub = this._fromClass((ClassStack)null, subclass, b);
      JavaType baseWithPlaceholders = tmpSub.findSuperType(baseType.getRawClass());
      if (baseWithPlaceholders == null) {
         throw new IllegalArgumentException(String.format("Internal error: unable to locate supertype (%s) from resolved subtype %s", baseType.getRawClass().getName(), subclass.getName()));
      } else {
         String error = this._resolveTypePlaceholders(baseType, baseWithPlaceholders);
         if (error != null && !relaxedCompatibilityCheck) {
            throw new IllegalArgumentException("Failed to specialize base type " + baseType.toCanonical() + " as " + subclass.getName() + ", problem: " + error);
         } else {
            JavaType[] typeParams = new JavaType[typeParamCount];

            for(int i = 0; i < typeParamCount; ++i) {
               JavaType t = placeholders[i].actualType();
               if (t == null) {
                  t = unknownType();
               }

               typeParams[i] = t;
            }

            return TypeBindings.create(subclass, typeParams);
         }
      }
   }

   private String _resolveTypePlaceholders(JavaType sourceType, JavaType actualType) throws IllegalArgumentException {
      List<JavaType> expectedTypes = sourceType.getBindings().getTypeParameters();
      List<JavaType> actualTypes = actualType.getBindings().getTypeParameters();
      int actCount = actualTypes.size();
      int i = 0;

      for(int expCount = expectedTypes.size(); i < expCount; ++i) {
         JavaType exp = (JavaType)expectedTypes.get(i);
         JavaType act = i < actCount ? (JavaType)actualTypes.get(i) : unknownType();
         if (!this._verifyAndResolvePlaceholders(exp, act) && !exp.hasRawClass(Object.class) && (i != 0 || !sourceType.isMapLikeType() || !act.hasRawClass(Object.class)) && (!exp.isInterface() || !exp.isTypeOrSuperTypeOf(act.getRawClass()))) {
            return String.format("Type parameter #%d/%d differs; can not specialize %s with %s", i + 1, expCount, exp.toCanonical(), act.toCanonical());
         }
      }

      return null;
   }

   private boolean _verifyAndResolvePlaceholders(JavaType exp, JavaType act) {
      if (act instanceof PlaceholderForType) {
         ((PlaceholderForType)act).actualType(exp);
         return true;
      } else if (exp.getRawClass() != act.getRawClass()) {
         return false;
      } else {
         List<JavaType> expectedTypes = exp.getBindings().getTypeParameters();
         List<JavaType> actualTypes = act.getBindings().getTypeParameters();
         int i = 0;

         for(int len = expectedTypes.size(); i < len; ++i) {
            JavaType exp2 = (JavaType)expectedTypes.get(i);
            JavaType act2 = (JavaType)actualTypes.get(i);
            if (!this._verifyAndResolvePlaceholders(exp2, act2)) {
               return false;
            }
         }

         return true;
      }
   }

   public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass) {
      Class<?> rawBase = baseType.getRawClass();
      if (rawBase == superClass) {
         return baseType;
      } else {
         JavaType superType = baseType.findSuperType(superClass);
         if (superType == null) {
            if (!superClass.isAssignableFrom(rawBase)) {
               throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", superClass.getName(), baseType));
            } else {
               throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", superClass.getName(), baseType));
            }
         } else {
            return superType;
         }
      }
   }

   public JavaType constructFromCanonical(String canonical) throws IllegalArgumentException {
      return this._parser.parse(canonical);
   }

   public JavaType[] findTypeParameters(JavaType type, Class<?> expType) {
      JavaType match = type.findSuperType(expType);
      return match == null ? NO_TYPES : match.getBindings().typeParameterArray();
   }

   public JavaType findFirstTypeParameter(JavaType type, Class<?> expType) {
      JavaType match = type.findSuperType(expType);
      if (match != null) {
         JavaType t = match.getBindings().getBoundTypeOrNull(0);
         if (t != null) {
            return t;
         }
      }

      return this._unknownType();
   }

   /** @deprecated */
   @Deprecated
   public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings) {
      return this.findTypeParameters(this.constructType(clz, bindings), expType);
   }

   /** @deprecated */
   @Deprecated
   public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType) {
      return this.findTypeParameters(this.constructType(clz), expType);
   }

   public JavaType moreSpecificType(JavaType type1, JavaType type2) {
      if (type1 == null) {
         return type2;
      } else if (type2 == null) {
         return type1;
      } else {
         Class<?> raw1 = type1.getRawClass();
         Class<?> raw2 = type2.getRawClass();
         if (raw1 == raw2) {
            return type1;
         } else {
            return raw1.isAssignableFrom(raw2) ? type2 : type1;
         }
      }
   }

   public JavaType constructType(Type type) {
      return this._fromAny((ClassStack)null, type, EMPTY_BINDINGS);
   }

   public JavaType constructType(TypeReference<?> typeRef) {
      return this._fromAny((ClassStack)null, typeRef.getType(), EMPTY_BINDINGS);
   }

   public JavaType resolveMemberType(Type type, TypeBindings contextBindings) {
      return this._fromAny((ClassStack)null, type, contextBindings);
   }

   /** @deprecated */
   @Deprecated
   public JavaType constructType(Type type, TypeBindings bindings) {
      if (type instanceof Class) {
         JavaType resultType = this._fromClass((ClassStack)null, (Class)type, bindings);
         return this._applyModifiers(type, resultType);
      } else {
         return this._fromAny((ClassStack)null, type, bindings);
      }
   }

   /** @deprecated */
   @Deprecated
   public JavaType constructType(Type type, Class<?> contextClass) {
      JavaType contextType = contextClass == null ? null : this.constructType(contextClass);
      return this.constructType(type, contextType);
   }

   /** @deprecated */
   @Deprecated
   public JavaType constructType(Type type, JavaType contextType) {
      TypeBindings bindings;
      if (contextType == null) {
         bindings = EMPTY_BINDINGS;
      } else {
         bindings = contextType.getBindings();
         if (type.getClass() != Class.class) {
            while(bindings.isEmpty()) {
               contextType = contextType.getSuperClass();
               if (contextType == null) {
                  break;
               }

               bindings = contextType.getBindings();
            }
         }
      }

      return this._fromAny((ClassStack)null, type, bindings);
   }

   public ArrayType constructArrayType(Class<?> elementType) {
      return ArrayType.construct(this._fromAny((ClassStack)null, elementType, (TypeBindings)null), (TypeBindings)null);
   }

   public ArrayType constructArrayType(JavaType elementType) {
      return ArrayType.construct(elementType, (TypeBindings)null);
   }

   public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
      return this.constructCollectionType(collectionClass, this._fromClass((ClassStack)null, elementClass, EMPTY_BINDINGS));
   }

   public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
      TypeBindings bindings = TypeBindings.createIfNeeded(collectionClass, elementType);
      CollectionType result = (CollectionType)this._fromClass((ClassStack)null, collectionClass, bindings);
      if (bindings.isEmpty() && elementType != null) {
         JavaType t = result.findSuperType(Collection.class);
         JavaType realET = t.getContentType();
         if (!realET.equals(elementType)) {
            throw new IllegalArgumentException(String.format("Non-generic Collection class %s did not resolve to something with element type %s but %s ", ClassUtil.nameOf(collectionClass), elementType, realET));
         }
      }

      return result;
   }

   public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
      return this.constructCollectionLikeType(collectionClass, this._fromClass((ClassStack)null, elementClass, EMPTY_BINDINGS));
   }

   public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType) {
      JavaType type = this._fromClass((ClassStack)null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
      return type instanceof CollectionLikeType ? (CollectionLikeType)type : CollectionLikeType.upgradeFrom(type, elementType);
   }

   public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
      JavaType kt;
      JavaType vt;
      if (mapClass == Properties.class) {
         kt = vt = CORE_TYPE_STRING;
      } else {
         kt = this._fromClass((ClassStack)null, keyClass, EMPTY_BINDINGS);
         vt = this._fromClass((ClassStack)null, valueClass, EMPTY_BINDINGS);
      }

      return this.constructMapType(mapClass, kt, vt);
   }

   public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
      TypeBindings bindings = TypeBindings.createIfNeeded(mapClass, new JavaType[]{keyType, valueType});
      MapType result = (MapType)this._fromClass((ClassStack)null, mapClass, bindings);
      if (bindings.isEmpty()) {
         JavaType t = result.findSuperType(Map.class);
         JavaType realKT = t.getKeyType();
         if (!realKT.equals(keyType)) {
            throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with key type %s but %s ", ClassUtil.nameOf(mapClass), keyType, realKT));
         }

         JavaType realVT = t.getContentType();
         if (!realVT.equals(valueType)) {
            throw new IllegalArgumentException(String.format("Non-generic Map class %s did not resolve to something with value type %s but %s ", ClassUtil.nameOf(mapClass), valueType, realVT));
         }
      }

      return result;
   }

   public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
      return this.constructMapLikeType(mapClass, this._fromClass((ClassStack)null, keyClass, EMPTY_BINDINGS), this._fromClass((ClassStack)null, valueClass, EMPTY_BINDINGS));
   }

   public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
      JavaType type = this._fromClass((ClassStack)null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[]{keyType, valueType}));
      return type instanceof MapLikeType ? (MapLikeType)type : MapLikeType.upgradeFrom(type, keyType, valueType);
   }

   public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes) {
      return this._fromClass((ClassStack)null, rawType, TypeBindings.create(rawType, parameterTypes));
   }

   /** @deprecated */
   @Deprecated
   public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes) {
      return this.constructSimpleType(rawType, parameterTypes);
   }

   public JavaType constructReferenceType(Class<?> rawType, JavaType referredType) {
      return ReferenceType.construct(rawType, TypeBindings.create(rawType, referredType), (JavaType)null, (JavaType[])null, referredType);
   }

   /** @deprecated */
   @Deprecated
   public JavaType uncheckedSimpleType(Class<?> cls) {
      return this._constructSimple(cls, EMPTY_BINDINGS, (JavaType)null, (JavaType[])null);
   }

   public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
      int len = parameterClasses.length;
      JavaType[] pt = new JavaType[len];

      for(int i = 0; i < len; ++i) {
         pt[i] = this._fromClass((ClassStack)null, parameterClasses[i], EMPTY_BINDINGS);
      }

      return this.constructParametricType(parametrized, pt);
   }

   public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes) {
      return this.constructParametricType(rawType, TypeBindings.create(rawType, parameterTypes));
   }

   public JavaType constructParametricType(Class<?> rawType, TypeBindings parameterTypes) {
      JavaType resultType = this._fromClass((ClassStack)null, rawType, parameterTypes);
      return this._applyModifiers(rawType, resultType);
   }

   /** @deprecated */
   @Deprecated
   public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes) {
      return this.constructParametricType(parametrized, parameterTypes);
   }

   /** @deprecated */
   @Deprecated
   public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
      return this.constructParametricType(parametrized, parameterClasses);
   }

   public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass) {
      return this.constructCollectionType(collectionClass, unknownType());
   }

   public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass) {
      return this.constructCollectionLikeType(collectionClass, unknownType());
   }

   public MapType constructRawMapType(Class<? extends Map> mapClass) {
      return this.constructMapType(mapClass, unknownType(), unknownType());
   }

   public MapLikeType constructRawMapLikeType(Class<?> mapClass) {
      return this.constructMapLikeType(mapClass, unknownType(), unknownType());
   }

   private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      JavaType kt;
      JavaType vt;
      if (rawClass == Properties.class) {
         kt = vt = CORE_TYPE_STRING;
      } else {
         List<JavaType> typeParams = bindings.getTypeParameters();
         int pc = typeParams.size();
         switch (pc) {
            case 0:
               kt = vt = this._unknownType();
               break;
            case 2:
               kt = (JavaType)typeParams.get(0);
               vt = (JavaType)typeParams.get(1);
               break;
            default:
               throw new IllegalArgumentException(String.format("Strange Map type %s with %d type parameter%s (%s), can not resolve", ClassUtil.nameOf(rawClass), pc, pc == 1 ? "" : "s", bindings));
         }
      }

      return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
   }

   private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      List<JavaType> typeParams = bindings.getTypeParameters();
      JavaType ct;
      if (typeParams.isEmpty()) {
         ct = this._unknownType();
      } else {
         if (typeParams.size() != 1) {
            throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": cannot determine type parameters");
         }

         ct = (JavaType)typeParams.get(0);
      }

      return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
   }

   private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      List<JavaType> typeParams = bindings.getTypeParameters();
      JavaType ct;
      if (typeParams.isEmpty()) {
         ct = this._unknownType();
      } else {
         if (typeParams.size() != 1) {
            throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": cannot determine type parameters");
         }

         ct = (JavaType)typeParams.get(0);
      }

      return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
   }

   private JavaType _iterationType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      List<JavaType> typeParams = bindings.getTypeParameters();
      JavaType ct;
      if (typeParams.isEmpty()) {
         ct = this._unknownType();
      } else {
         if (typeParams.size() != 1) {
            throw new IllegalArgumentException("Strange Iteration type " + rawClass.getName() + ": cannot determine type parameters");
         }

         ct = (JavaType)typeParams.get(0);
      }

      return this._iterationType(rawClass, bindings, superClass, superInterfaces, ct);
   }

   private JavaType _iterationType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces, JavaType iteratedType) {
      return IterationType.construct(rawClass, bindings, superClass, superInterfaces, iteratedType);
   }

   protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      if (bindings.isEmpty()) {
         JavaType result = this._findWellKnownSimple(raw);
         if (result != null) {
            return result;
         }
      }

      return this._newSimpleType(raw, bindings, superClass, superInterfaces);
   }

   protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return new SimpleType(raw, bindings, superClass, superInterfaces);
   }

   protected JavaType _unknownType() {
      return CORE_TYPE_OBJECT;
   }

   protected JavaType _findWellKnownSimple(Class<?> clz) {
      if (clz.isPrimitive()) {
         if (clz == CLS_BOOL) {
            return CORE_TYPE_BOOL;
         }

         if (clz == CLS_INT) {
            return CORE_TYPE_INT;
         }

         if (clz == CLS_LONG) {
            return CORE_TYPE_LONG;
         }

         if (clz == CLS_DOUBLE) {
            return CORE_TYPE_DOUBLE;
         }
      } else {
         if (clz == CLS_STRING) {
            return CORE_TYPE_STRING;
         }

         if (clz == CLS_OBJECT) {
            return CORE_TYPE_OBJECT;
         }

         if (clz == CLS_JSON_NODE) {
            return CORE_TYPE_JSON_NODE;
         }
      }

      return null;
   }

   protected JavaType _fromAny(ClassStack context, Type srcType, TypeBindings bindings) {
      JavaType resultType;
      if (srcType instanceof Class) {
         resultType = this._fromClass(context, (Class)srcType, EMPTY_BINDINGS);
      } else if (srcType instanceof ParameterizedType) {
         resultType = this._fromParamType(context, (ParameterizedType)srcType, bindings);
      } else {
         if (srcType instanceof JavaType) {
            return (JavaType)srcType;
         }

         if (srcType instanceof GenericArrayType) {
            resultType = this._fromArrayType(context, (GenericArrayType)srcType, bindings);
         } else if (srcType instanceof TypeVariable) {
            resultType = this._fromVariable(context, (TypeVariable)srcType, bindings);
         } else {
            if (!(srcType instanceof WildcardType)) {
               throw new IllegalArgumentException("Unrecognized Type: " + (srcType == null ? "[null]" : srcType.toString()));
            }

            resultType = this._fromWildcard(context, (WildcardType)srcType, bindings);
         }
      }

      return this._applyModifiers(srcType, resultType);
   }

   protected JavaType _applyModifiers(Type srcType, JavaType resolvedType) {
      if (this._modifiers == null) {
         return resolvedType;
      } else {
         JavaType resultType = resolvedType;
         TypeBindings b = resolvedType.getBindings();
         if (b == null) {
            b = EMPTY_BINDINGS;
         }

         for(TypeModifier mod : this._modifiers) {
            JavaType t = mod.modifyType(resultType, srcType, b, this);
            if (t == null) {
               throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", mod, mod.getClass().getName(), resultType));
            }

            resultType = t;
         }

         return resultType;
      }
   }

   protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings) {
      JavaType result = this._findWellKnownSimple(rawType);
      if (result != null) {
         return result;
      } else {
         Object key;
         if (bindings != null && !bindings.isEmpty()) {
            key = bindings.asKey(rawType);
         } else {
            key = rawType;
         }

         result = key == null ? null : (JavaType)this._typeCache.get(key);
         if (result != null) {
            return result;
         } else {
            if (context == null) {
               context = new ClassStack(rawType);
            } else {
               ClassStack prev = context.find(rawType);
               if (prev != null) {
                  ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
                  prev.addSelfReference(selfRef);
                  return selfRef;
               }

               context = context.child(rawType);
            }

            if (rawType.isArray()) {
               result = ArrayType.construct(this._fromAny(context, rawType.getComponentType(), bindings), bindings);
            } else {
               JavaType[] superInterfaces;
               JavaType superClass;
               if (rawType.isInterface()) {
                  superClass = null;
                  superInterfaces = this._resolveSuperInterfaces(context, rawType, bindings);
               } else {
                  superClass = this._resolveSuperClass(context, rawType, bindings);
                  superInterfaces = this._resolveSuperInterfaces(context, rawType, bindings);
               }

               if (rawType == Properties.class) {
                  result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
               } else if (superClass != null) {
                  result = superClass.refine(rawType, bindings, superClass, superInterfaces);
               }

               if (result == null) {
                  result = this._fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
                  if (result == null) {
                     result = this._fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
                     if (result == null) {
                        result = this._newSimpleType(rawType, bindings, superClass, superInterfaces);
                     }
                  }
               }
            }

            context.resolveSelfReferences(result);
            if (key != null && !result.hasHandlers()) {
               this._typeCache.putIfAbsent(key, result);
            }

            return result;
         }
      }
   }

   protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
      Type parent = ClassUtil.getGenericSuperclass(rawType);
      return parent == null ? null : this._fromAny(context, parent, parentBindings);
   }

   protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings) {
      Type[] types = ClassUtil.getGenericInterfaces(rawType);
      if (types != null && types.length != 0) {
         int len = types.length;
         JavaType[] resolved = new JavaType[len];

         for(int i = 0; i < len; ++i) {
            Type type = types[i];
            resolved[i] = this._fromAny(context, type, parentBindings);
         }

         return resolved;
      } else {
         return NO_TYPES;
      }
   }

   protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      if (bindings == null) {
         bindings = EMPTY_BINDINGS;
      }

      if (rawType == Map.class) {
         return this._mapType(rawType, bindings, superClass, superInterfaces);
      } else if (rawType == Collection.class) {
         return this._collectionType(rawType, bindings, superClass, superInterfaces);
      } else if (rawType == AtomicReference.class) {
         return this._referenceType(rawType, bindings, superClass, superInterfaces);
      } else if (rawType != Iterator.class && rawType != Stream.class) {
         if (BaseStream.class.isAssignableFrom(rawType)) {
            if (DoubleStream.class.isAssignableFrom(rawType)) {
               return this._iterationType(rawType, bindings, superClass, superInterfaces, CORE_TYPE_DOUBLE);
            }

            if (IntStream.class.isAssignableFrom(rawType)) {
               return this._iterationType(rawType, bindings, superClass, superInterfaces, CORE_TYPE_INT);
            }

            if (LongStream.class.isAssignableFrom(rawType)) {
               return this._iterationType(rawType, bindings, superClass, superInterfaces, CORE_TYPE_LONG);
            }
         }

         return null;
      } else {
         return this._iterationType(rawType, bindings, superClass, superInterfaces);
      }
   }

   protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      int intCount = superInterfaces.length;

      for(int i = 0; i < intCount; ++i) {
         JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
         if (result != null) {
            return result;
         }
      }

      return null;
   }

   protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
      Class<?> rawType = (Class)ptype.getRawType();
      if (rawType == CLS_ENUM) {
         return CORE_TYPE_ENUM;
      } else if (rawType == CLS_COMPARABLE) {
         return CORE_TYPE_COMPARABLE;
      } else {
         Type[] args = ptype.getActualTypeArguments();
         int paramCount = args == null ? 0 : args.length;
         TypeBindings newBindings;
         if (paramCount == 0) {
            newBindings = EMPTY_BINDINGS;
         } else {
            JavaType[] pt = new JavaType[paramCount];

            for(int i = 0; i < paramCount; ++i) {
               pt[i] = this._fromAny(context, args[i], parentBindings);
            }

            newBindings = TypeBindings.create(rawType, pt);
         }

         return this._fromClass(context, rawType, newBindings);
      }
   }

   protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings) {
      JavaType elementType = this._fromAny(context, type.getGenericComponentType(), bindings);
      return ArrayType.construct(elementType, bindings);
   }

   protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings) {
      String name = var.getName();
      if (bindings == null) {
         throw new IllegalArgumentException("Null `bindings` passed (type variable \"" + name + "\")");
      } else {
         JavaType type = bindings.findBoundType(name);
         if (type != null) {
            return type;
         } else if (bindings.hasUnbound(name)) {
            return CORE_TYPE_OBJECT;
         } else {
            bindings = bindings.withUnboundVariable(name);
            Type[] bounds;
            synchronized(var) {
               bounds = var.getBounds();
            }

            return this._fromAny(context, bounds[0], bindings);
         }
      }
   }

   protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings) {
      return this._fromAny(context, type.getUpperBounds()[0], bindings);
   }

   static {
      CLS_BOOL = Boolean.TYPE;
      CLS_DOUBLE = Double.TYPE;
      CLS_INT = Integer.TYPE;
      CLS_LONG = Long.TYPE;
      CORE_TYPE_BOOL = new SimpleType(CLS_BOOL);
      CORE_TYPE_DOUBLE = new SimpleType(CLS_DOUBLE);
      CORE_TYPE_INT = new SimpleType(CLS_INT);
      CORE_TYPE_LONG = new SimpleType(CLS_LONG);
      CORE_TYPE_STRING = new SimpleType(CLS_STRING);
      CORE_TYPE_OBJECT = new SimpleType(CLS_OBJECT);
      CORE_TYPE_COMPARABLE = new SimpleType(CLS_COMPARABLE);
      CORE_TYPE_ENUM = new SimpleType(CLS_ENUM);
      CORE_TYPE_JSON_NODE = new SimpleType(CLS_JSON_NODE);
   }
}
