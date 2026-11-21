package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@JacksonStdImpl
public class MapSerializer extends ContainerSerializer<Map<?, ?>> implements ContextualSerializer {
   private static final long serialVersionUID = 1L;
   protected static final JavaType UNSPECIFIED_TYPE = TypeFactory.unknownType();
   public static final Object MARKER_FOR_EMPTY;
   protected final BeanProperty _property;
   protected final boolean _valueTypeIsStatic;
   protected final JavaType _keyType;
   protected final JavaType _valueType;
   protected JsonSerializer<Object> _keySerializer;
   protected JsonSerializer<Object> _valueSerializer;
   protected final TypeSerializer _valueTypeSerializer;
   protected PropertySerializerMap _dynamicValueSerializers;
   protected final Set<String> _ignoredEntries;
   protected final Set<String> _includedEntries;
   protected final Object _filterId;
   protected final Object _suppressableValue;
   protected final boolean _suppressNulls;
   protected final IgnorePropertiesUtil.Checker _inclusionChecker;
   protected final boolean _sortKeys;

   protected MapSerializer(Set<String> ignoredEntries, Set<String> includedEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
      super(Map.class, false);
      this._ignoredEntries = ignoredEntries != null && !ignoredEntries.isEmpty() ? ignoredEntries : null;
      this._includedEntries = includedEntries;
      this._keyType = keyType;
      this._valueType = valueType;
      this._valueTypeIsStatic = valueTypeIsStatic;
      this._valueTypeSerializer = vts;
      this._keySerializer = keySerializer;
      this._valueSerializer = valueSerializer;
      this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
      this._property = null;
      this._filterId = null;
      this._sortKeys = false;
      this._suppressableValue = null;
      this._suppressNulls = false;
      this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignoredEntries, this._includedEntries);
   }

   /** @deprecated */
   @Deprecated
   protected MapSerializer(Set<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
      this(ignoredEntries, (Set)null, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer);
   }

   protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries, Set<String> includedEntries) {
      super(Map.class, false);
      this._ignoredEntries = ignoredEntries != null && !ignoredEntries.isEmpty() ? ignoredEntries : null;
      this._includedEntries = includedEntries;
      this._keyType = src._keyType;
      this._valueType = src._valueType;
      this._valueTypeIsStatic = src._valueTypeIsStatic;
      this._valueTypeSerializer = src._valueTypeSerializer;
      this._keySerializer = keySerializer;
      this._valueSerializer = valueSerializer;
      this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
      this._property = property;
      this._filterId = src._filterId;
      this._sortKeys = src._sortKeys;
      this._suppressableValue = src._suppressableValue;
      this._suppressNulls = src._suppressNulls;
      this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignoredEntries, this._includedEntries);
   }

   /** @deprecated */
   @Deprecated
   protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries) {
      this(src, property, keySerializer, valueSerializer, ignoredEntries, (Set)null);
   }

   protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue, boolean suppressNulls) {
      super(Map.class, false);
      this._ignoredEntries = src._ignoredEntries;
      this._includedEntries = src._includedEntries;
      this._keyType = src._keyType;
      this._valueType = src._valueType;
      this._valueTypeIsStatic = src._valueTypeIsStatic;
      this._valueTypeSerializer = vts;
      this._keySerializer = src._keySerializer;
      this._valueSerializer = src._valueSerializer;
      this._dynamicValueSerializers = src._dynamicValueSerializers;
      this._property = src._property;
      this._filterId = src._filterId;
      this._sortKeys = src._sortKeys;
      this._suppressableValue = suppressableValue;
      this._suppressNulls = suppressNulls;
      this._inclusionChecker = src._inclusionChecker;
   }

   protected MapSerializer(MapSerializer src, Object filterId, boolean sortKeys) {
      super(Map.class, false);
      this._ignoredEntries = src._ignoredEntries;
      this._includedEntries = src._includedEntries;
      this._keyType = src._keyType;
      this._valueType = src._valueType;
      this._valueTypeIsStatic = src._valueTypeIsStatic;
      this._valueTypeSerializer = src._valueTypeSerializer;
      this._keySerializer = src._keySerializer;
      this._valueSerializer = src._valueSerializer;
      this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
      this._property = src._property;
      this._filterId = filterId;
      this._sortKeys = sortKeys;
      this._suppressableValue = src._suppressableValue;
      this._suppressNulls = src._suppressNulls;
      this._inclusionChecker = src._inclusionChecker;
   }

   public MapSerializer _withValueTypeSerializer(TypeSerializer vts) {
      if (this._valueTypeSerializer == vts) {
         return this;
      } else {
         this._ensureOverride("_withValueTypeSerializer");
         return new MapSerializer(this, vts, this._suppressableValue, this._suppressNulls);
      }
   }

   public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, Set<String> included, boolean sortKeys) {
      this._ensureOverride("withResolved");
      MapSerializer ser = new MapSerializer(this, property, keySerializer, valueSerializer, ignored, included);
      if (sortKeys != ser._sortKeys) {
         ser = new MapSerializer(ser, this._filterId, sortKeys);
      }

      return ser;
   }

   public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, boolean sortKeys) {
      return this.withResolved(property, keySerializer, valueSerializer, ignored, (Set)null, sortKeys);
   }

   public MapSerializer withFilterId(Object filterId) {
      if (this._filterId == filterId) {
         return this;
      } else {
         this._ensureOverride("withFilterId");
         return new MapSerializer(this, filterId, this._sortKeys);
      }
   }

   public MapSerializer withContentInclusion(Object suppressableValue, boolean suppressNulls) {
      if (suppressableValue == this._suppressableValue && suppressNulls == this._suppressNulls) {
         return this;
      } else {
         this._ensureOverride("withContentInclusion");
         return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, suppressNulls);
      }
   }

   public static MapSerializer construct(Set<String> ignoredEntries, Set<String> includedEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
      JavaType keyType;
      JavaType valueType;
      if (mapType == null) {
         keyType = valueType = UNSPECIFIED_TYPE;
      } else {
         keyType = mapType.getKeyType();
         if (mapType.hasRawClass(Properties.class)) {
            valueType = TypeFactory.unknownType();
         } else {
            valueType = mapType.getContentType();
         }
      }

      if (!staticValueType) {
         staticValueType = valueType != null && valueType.isFinal();
      } else if (valueType.getRawClass() == Object.class) {
         staticValueType = false;
      }

      MapSerializer ser = new MapSerializer(ignoredEntries, includedEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer);
      if (filterId != null) {
         ser = ser.withFilterId(filterId);
      }

      return ser;
   }

   public static MapSerializer construct(Set<String> ignoredEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
      return construct(ignoredEntries, (Set)null, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
   }

   protected void _ensureOverride(String method) {
      ClassUtil.verifyMustOverride(MapSerializer.class, this, method);
   }

   /** @deprecated */
   @Deprecated
   protected void _ensureOverride() {
      this._ensureOverride("N/A");
   }

   /** @deprecated */
   @Deprecated
   protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue) {
      this(src, vts, suppressableValue, false);
   }

   /** @deprecated */
   @Deprecated
   public MapSerializer withContentInclusion(Object suppressableValue) {
      return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, this._suppressNulls);
   }

   /** @deprecated */
   @Deprecated
   public static MapSerializer construct(String[] ignoredList, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
      Set<String> ignoredEntries = ArrayBuilders.<String>arrayToSet(ignoredList);
      return construct(ignoredEntries, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
   }

   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      JsonSerializer<?> ser = null;
      JsonSerializer<?> keySer = null;
      AnnotationIntrospector intr = provider.getAnnotationIntrospector();
      AnnotatedMember propertyAcc = property == null ? null : property.getMember();
      if (_neitherNull(propertyAcc, intr)) {
         Object serDef = intr.findKeySerializer(propertyAcc);
         if (serDef != null) {
            keySer = provider.serializerInstance(propertyAcc, serDef);
         }

         serDef = intr.findContentSerializer(propertyAcc);
         if (serDef != null) {
            ser = provider.serializerInstance(propertyAcc, serDef);
         }
      }

      if (ser == null) {
         ser = this._valueSerializer;
      }

      ser = this.findContextualConvertingSerializer(provider, property, ser);
      if (ser == null && this._valueTypeIsStatic && !this._valueType.isJavaLangObject()) {
         ser = provider.findContentValueSerializer(this._valueType, property);
      }

      if (keySer == null) {
         keySer = this._keySerializer;
      }

      if (keySer == null) {
         keySer = provider.findKeySerializer(this._keyType, property);
      } else {
         keySer = provider.handleSecondaryContextualization(keySer, property);
      }

      Set<String> ignored = this._ignoredEntries;
      Set<String> included = this._includedEntries;
      boolean sortKeys = false;
      if (_neitherNull(propertyAcc, intr)) {
         SerializationConfig config = provider.getConfig();
         Set<String> newIgnored = intr.findPropertyIgnoralByName(config, propertyAcc).findIgnoredForSerialization();
         if (_nonEmpty(newIgnored)) {
            ignored = ignored == null ? new HashSet() : new HashSet(ignored);

            for(String str : newIgnored) {
               ignored.add(str);
            }
         }

         Set<String> newIncluded = intr.findPropertyInclusionByName(config, propertyAcc).getIncluded();
         if (newIncluded != null) {
            included = included == null ? new HashSet() : new HashSet(included);

            for(String str : newIncluded) {
               included.add(str);
            }
         }

         Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
         sortKeys = Boolean.TRUE.equals(b);
      }

      JsonFormat.Value format = this.findFormatOverrides(provider, property, Map.class);
      if (format != null) {
         Boolean B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES);
         if (B != null) {
            sortKeys = B;
         }
      }

      MapSerializer mser = this.withResolved(property, keySer, ser, ignored, included, sortKeys);
      if (propertyAcc != null) {
         Object filterId = intr.findFilterId(propertyAcc);
         if (filterId != null) {
            mser = mser.withFilterId(filterId);
         }
      }

      JsonInclude.Value inclV = this.findIncludeOverrides(provider, property, Map.class);
      if (inclV != null) {
         JsonInclude.Include incl = inclV.getContentInclusion();
         if (incl != JsonInclude.Include.USE_DEFAULTS) {
            boolean suppressNulls;
            Object valueToSuppress;
            switch (incl) {
               case NON_DEFAULT:
                  valueToSuppress = BeanUtil.getDefaultValue(this._valueType);
                  suppressNulls = true;
                  if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                     valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                  }
                  break;
               case NON_ABSENT:
                  suppressNulls = true;
                  valueToSuppress = this._valueType.isReferenceType() ? MARKER_FOR_EMPTY : null;
                  break;
               case NON_EMPTY:
                  suppressNulls = true;
                  valueToSuppress = MARKER_FOR_EMPTY;
                  break;
               case CUSTOM:
                  valueToSuppress = provider.includeFilterInstance((BeanPropertyDefinition)null, inclV.getContentFilter());
                  if (valueToSuppress == null) {
                     suppressNulls = true;
                  } else {
                     suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
                  }
                  break;
               case NON_NULL:
                  valueToSuppress = null;
                  suppressNulls = true;
                  break;
               case ALWAYS:
               default:
                  valueToSuppress = null;
                  suppressNulls = false;
            }

            mser = mser.withContentInclusion(valueToSuppress, suppressNulls);
         }
      }

      return mser;
   }

   public JavaType getContentType() {
      return this._valueType;
   }

   public JsonSerializer<?> getContentSerializer() {
      return this._valueSerializer;
   }

   public boolean isEmpty(SerializerProvider prov, Map<?, ?> value) {
      if (value.isEmpty()) {
         return true;
      } else {
         Object supp = this._suppressableValue;
         if (supp == null && !this._suppressNulls) {
            return false;
         } else {
            JsonSerializer<Object> valueSer = this._valueSerializer;
            boolean checkEmpty = MARKER_FOR_EMPTY == supp;
            if (valueSer != null) {
               for(Object elemValue : value.values()) {
                  if (elemValue != null) {
                     if (!checkEmpty) {
                        if (supp == null || !supp.equals(value)) {
                           return false;
                        }
                     } else if (!valueSer.isEmpty(prov, elemValue)) {
                        return false;
                     }
                  } else if (!this._suppressNulls) {
                     return false;
                  }
               }

               return true;
            } else {
               for(Object elemValue : value.values()) {
                  if (elemValue != null) {
                     try {
                        valueSer = this._findSerializer(prov, elemValue);
                     } catch (DatabindException var9) {
                        return false;
                     }

                     if (!checkEmpty) {
                        if (supp == null || !supp.equals(value)) {
                           return false;
                        }
                     } else if (!valueSer.isEmpty(prov, elemValue)) {
                        return false;
                     }
                  } else if (!this._suppressNulls) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public boolean hasSingleElement(Map<?, ?> value) {
      return value.size() == 1;
   }

   public JsonSerializer<?> getKeySerializer() {
      return this._keySerializer;
   }

   public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject(value);
      this.serializeWithoutTypeInfo(value, gen, provider);
      gen.writeEndObject();
   }

   public void serializeWithType(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      gen.assignCurrentValue(value);
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
      this.serializeWithoutTypeInfo(value, gen, provider);
      typeSer.writeTypeSuffix(gen, typeIdDef);
   }

   public void serializeWithoutTypeInfo(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (!value.isEmpty()) {
         if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
            value = this._orderEntries(value, gen, provider);
         }

         PropertyFilter pf;
         if (this._filterId != null && (pf = this.findPropertyFilter(provider, this._filterId, value)) != null) {
            this.serializeFilteredFields(value, gen, provider, pf, this._suppressableValue);
         } else if (this._suppressableValue == null && !this._suppressNulls) {
            if (this._valueSerializer != null) {
               this.serializeFieldsUsing(value, gen, provider, this._valueSerializer);
            } else {
               this.serializeFields(value, gen, provider);
            }
         } else {
            this.serializeOptionalFields(value, gen, provider, this._suppressableValue);
         }
      }

   }

   public void serializeFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (this._valueTypeSerializer != null) {
         this.serializeTypedFields(value, gen, provider, (Object)null);
      } else {
         JsonSerializer<Object> keySerializer = this._keySerializer;
         Object keyElem = null;

         try {
            for(Map.Entry<?, ?> entry : value.entrySet()) {
               Object valueElem = entry.getValue();
               keyElem = entry.getKey();
               if (keyElem == null) {
                  provider.findNullKeySerializer(this._keyType, this._property).serialize((Object)null, gen, provider);
               } else {
                  if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
                     continue;
                  }

                  keySerializer.serialize(keyElem, gen, provider);
               }

               if (valueElem == null) {
                  provider.defaultSerializeNull(gen);
               } else {
                  JsonSerializer<Object> serializer = this._valueSerializer;
                  if (serializer == null) {
                     serializer = this._findSerializer(provider, valueElem);
                  }

                  serializer.serialize(valueElem, gen, provider);
               }
            }
         } catch (Exception e) {
            this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
         }

      }
   }

   public void serializeOptionalFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, Object suppressableValue) throws IOException {
      if (this._valueTypeSerializer != null) {
         this.serializeTypedFields(value, gen, provider, suppressableValue);
      } else {
         boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;

         for(Map.Entry<?, ?> entry : value.entrySet()) {
            Object keyElem = entry.getKey();
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
               keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            } else {
               if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
                  continue;
               }

               keySerializer = this._keySerializer;
            }

            Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
               if (this._suppressNulls) {
                  continue;
               }

               valueSer = provider.getDefaultNullValueSerializer();
            } else {
               valueSer = this._valueSerializer;
               if (valueSer == null) {
                  valueSer = this._findSerializer(provider, valueElem);
               }

               if (checkEmpty) {
                  if (valueSer.isEmpty(provider, valueElem)) {
                     continue;
                  }
               } else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                  continue;
               }
            }

            try {
               keySerializer.serialize(keyElem, gen, provider);
               valueSer.serialize(valueElem, gen, provider);
            } catch (Exception e) {
               this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
         }

      }
   }

   public void serializeFieldsUsing(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
      JsonSerializer<Object> keySerializer = this._keySerializer;
      TypeSerializer typeSer = this._valueTypeSerializer;

      for(Map.Entry<?, ?> entry : value.entrySet()) {
         Object keyElem = entry.getKey();
         if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
            if (keyElem == null) {
               provider.findNullKeySerializer(this._keyType, this._property).serialize((Object)null, gen, provider);
            } else {
               keySerializer.serialize(keyElem, gen, provider);
            }

            Object valueElem = entry.getValue();
            if (valueElem == null) {
               provider.defaultSerializeNull(gen);
            } else {
               try {
                  if (typeSer == null) {
                     ser.serialize(valueElem, gen, provider);
                  } else {
                     ser.serializeWithType(valueElem, gen, provider, typeSer);
                  }
               } catch (Exception e) {
                  this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
               }
            }
         }
      }

   }

   public void serializeFilteredFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter, Object suppressableValue) throws IOException {
      MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
      boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;

      for(Map.Entry<?, ?> entry : value.entrySet()) {
         Object keyElem = entry.getKey();
         if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
               keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            } else {
               keySerializer = this._keySerializer;
            }

            Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
               if (this._suppressNulls) {
                  continue;
               }

               valueSer = provider.getDefaultNullValueSerializer();
            } else {
               valueSer = this._valueSerializer;
               if (valueSer == null) {
                  valueSer = this._findSerializer(provider, valueElem);
               }

               if (checkEmpty) {
                  if (valueSer.isEmpty(provider, valueElem)) {
                     continue;
                  }
               } else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                  continue;
               }
            }

            prop.reset(keyElem, valueElem, keySerializer, valueSer);

            try {
               filter.serializeAsField(value, gen, provider, prop);
            } catch (Exception e) {
               this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
         }
      }

   }

   public void serializeTypedFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, Object suppressableValue) throws IOException {
      boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;

      for(Map.Entry<?, ?> entry : value.entrySet()) {
         Object keyElem = entry.getKey();
         JsonSerializer<Object> keySerializer;
         if (keyElem == null) {
            keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
         } else {
            if (this._inclusionChecker != null && this._inclusionChecker.shouldIgnore(keyElem)) {
               continue;
            }

            keySerializer = this._keySerializer;
         }

         Object valueElem = entry.getValue();
         JsonSerializer<Object> valueSer;
         if (valueElem == null) {
            if (this._suppressNulls) {
               continue;
            }

            valueSer = provider.getDefaultNullValueSerializer();
         } else {
            valueSer = this._valueSerializer;
            if (valueSer == null) {
               valueSer = this._findSerializer(provider, valueElem);
            }

            if (checkEmpty) {
               if (valueSer.isEmpty(provider, valueElem)) {
                  continue;
               }
            } else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
               continue;
            }
         }

         keySerializer.serialize(keyElem, gen, provider);

         try {
            valueSer.serializeWithType(valueElem, gen, provider, this._valueTypeSerializer);
         } catch (Exception e) {
            this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
         }
      }

   }

   public void serializeFilteredAnyProperties(SerializerProvider provider, JsonGenerator gen, Object bean, Map<?, ?> value, PropertyFilter filter, Object suppressableValue) throws IOException {
      MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
      boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;

      for(Map.Entry<?, ?> entry : value.entrySet()) {
         Object keyElem = entry.getKey();
         if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
            JsonSerializer<Object> keySerializer;
            if (keyElem == null) {
               keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
            } else {
               keySerializer = this._keySerializer;
            }

            Object valueElem = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (valueElem == null) {
               if (this._suppressNulls) {
                  continue;
               }

               valueSer = provider.getDefaultNullValueSerializer();
            } else {
               valueSer = this._valueSerializer;
               if (valueSer == null) {
                  valueSer = this._findSerializer(provider, valueElem);
               }

               if (checkEmpty) {
                  if (valueSer.isEmpty(provider, valueElem)) {
                     continue;
                  }
               } else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                  continue;
               }
            }

            prop.reset(keyElem, valueElem, keySerializer, valueSer);

            try {
               filter.serializeAsField(bean, gen, provider, prop);
            } catch (Exception e) {
               this.wrapAndThrow(provider, e, value, String.valueOf(keyElem));
            }
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode("object", true);
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonMapFormatVisitor v2 = visitor.expectMapFormat(typeHint);
      if (v2 != null) {
         v2.keyFormat(this._keySerializer, this._keyType);
         JsonSerializer<?> valueSer = this._valueSerializer;
         if (valueSer == null) {
            valueSer = this._findAndAddDynamic(this._dynamicValueSerializers, this._valueType, visitor.getProvider());
         }

         v2.valueFormat(valueSer, this._valueType);
      }

   }

   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
      if (map != result.map) {
         this._dynamicValueSerializers = result.map;
      }

      return result.serializer;
   }

   protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
      if (map != result.map) {
         this._dynamicValueSerializers = result.map;
      }

      return result.serializer;
   }

   protected Map<?, ?> _orderEntries(Map<?, ?> input, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (input instanceof SortedMap) {
         return input;
      } else if (input.isEmpty()) {
         return input;
      } else {
         Object firstKey = input.keySet().iterator().next();
         if (!Comparable.class.isInstance(firstKey)) {
            if (!provider.isEnabled(SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY)) {
               return input;
            }

            Class<?> clazz = firstKey == null ? Object.class : firstKey.getClass();
            provider.reportBadDefinition(clazz, String.format("Cannot order Map entries by key of incomparable type %s, consider disabling `SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY` to simply skip sorting", ClassUtil.classNameOf(firstKey)));
         }

         if (this._hasNullKey(input)) {
            TreeMap<Object, Object> result = new TreeMap();

            for(Map.Entry<?, ?> entry : input.entrySet()) {
               Object key = entry.getKey();
               if (key == null) {
                  this._writeNullKeyedEntry(gen, provider, entry.getValue());
               } else {
                  result.put(key, entry.getValue());
               }
            }

            return result;
         } else {
            return new TreeMap(input);
         }
      }
   }

   protected boolean _hasNullKey(Map<?, ?> input) {
      return input instanceof HashMap && input.containsKey((Object)null);
   }

   protected void _writeNullKeyedEntry(JsonGenerator gen, SerializerProvider provider, Object value) throws IOException {
      JsonSerializer<Object> keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
      JsonSerializer<Object> valueSer;
      if (value == null) {
         if (this._suppressNulls) {
            return;
         }

         valueSer = provider.getDefaultNullValueSerializer();
      } else {
         valueSer = this._valueSerializer;
         if (valueSer == null) {
            valueSer = this._findSerializer(provider, value);
         }

         if (this._suppressableValue == MARKER_FOR_EMPTY) {
            if (valueSer.isEmpty(provider, value)) {
               return;
            }
         } else if (this._suppressableValue != null && this._suppressableValue.equals(value)) {
            return;
         }
      }

      try {
         keySerializer.serialize((Object)null, gen, provider);
         valueSer.serialize(value, gen, provider);
      } catch (Exception e) {
         this.wrapAndThrow(provider, e, value, "");
      }

   }

   private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Object value) throws JsonMappingException {
      Class<?> cc = value.getClass();
      JsonSerializer<Object> valueSer = this._dynamicValueSerializers.serializerFor(cc);
      if (valueSer != null) {
         return valueSer;
      } else {
         return this._valueType.hasGenericTypes() ? this._findAndAddDynamic(this._dynamicValueSerializers, provider.constructSpecializedType(this._valueType, cc), provider) : this._findAndAddDynamic(this._dynamicValueSerializers, cc, provider);
      }
   }

   static {
      MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
   }
}
