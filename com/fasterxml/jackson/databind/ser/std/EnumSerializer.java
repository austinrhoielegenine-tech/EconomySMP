package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.EnumNamingStrategy;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.EnumNamingStrategyFactory;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.EnumValues;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@JacksonStdImpl
public class EnumSerializer extends StdScalarSerializer<Enum<?>> implements ContextualSerializer {
   private static final long serialVersionUID = 1L;
   protected final EnumValues _values;
   protected final Boolean _serializeAsIndex;
   protected final EnumValues _valuesByEnumNaming;
   protected final EnumValues _valuesByToString;

   /** @deprecated */
   @Deprecated
   public EnumSerializer(EnumValues v, Boolean serializeAsIndex) {
      this(v, serializeAsIndex, (EnumValues)null, (EnumValues)null);
   }

   /** @deprecated */
   @Deprecated
   public EnumSerializer(EnumValues v, Boolean serializeAsIndex, EnumValues valuesByEnumNaming) {
      this(v, serializeAsIndex, valuesByEnumNaming, (EnumValues)null);
   }

   public EnumSerializer(EnumValues v, Boolean serializeAsIndex, EnumValues valuesByEnumNaming, EnumValues valuesByToString) {
      super(v.getEnumClass(), false);
      this._values = v;
      this._serializeAsIndex = serializeAsIndex;
      this._valuesByEnumNaming = valuesByEnumNaming;
      this._valuesByToString = valuesByToString;
   }

   public static EnumSerializer construct(Class<?> enumClass, SerializationConfig config, BeanDescription beanDesc, JsonFormat.Value format) {
      EnumValues v = EnumValues.constructFromName(config, beanDesc.getClassInfo());
      EnumValues valuesByEnumNaming = constructEnumNamingStrategyValues(config, enumClass, beanDesc.getClassInfo());
      EnumValues valuesByToString = EnumValues.constructFromToString(config, beanDesc.getClassInfo());
      Boolean serializeAsIndex = _isShapeWrittenUsingIndex(enumClass, format, true, (Boolean)null);
      return new EnumSerializer(v, serializeAsIndex, valuesByEnumNaming, valuesByToString);
   }

   public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
      JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
      if (format != null) {
         Class<?> type = this.handledType();
         Boolean serializeAsIndex = _isShapeWrittenUsingIndex(type, format, false, this._serializeAsIndex);
         if (!Objects.equals(serializeAsIndex, this._serializeAsIndex)) {
            return new EnumSerializer(this._values, serializeAsIndex, this._valuesByEnumNaming, this._valuesByToString);
         }
      }

      return this;
   }

   public EnumValues getEnumValues() {
      return this._values;
   }

   public final void serialize(Enum<?> en, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (this._valuesByEnumNaming != null) {
         gen.writeString(this._valuesByEnumNaming.serializedValueFor(en));
      } else if (this._serializeAsIndex(serializers)) {
         gen.writeNumber(en.ordinal());
      } else if (serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
         gen.writeString(this._valuesByToString.serializedValueFor(en));
      } else {
         gen.writeString(this._values.serializedValueFor(en));
      }
   }

   /** @deprecated */
   @Deprecated
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      if (this._serializeAsIndex(provider)) {
         return this.createSchemaNode("integer", true);
      } else {
         ObjectNode objectNode = this.createSchemaNode("string", true);
         if (typeHint != null) {
            JavaType type = provider.constructType(typeHint);
            if (type.isEnumType()) {
               ArrayNode enumNode = objectNode.putArray("enum");

               for(SerializableString value : this._values.values()) {
                  enumNode.add(value.getValue());
               }
            }
         }

         return objectNode;
      }
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      SerializerProvider serializers = visitor.getProvider();
      if (this._serializeAsIndex(serializers)) {
         this.visitIntFormat(visitor, typeHint, JsonParser.NumberType.INT);
      } else {
         JsonStringFormatVisitor stringVisitor = visitor.expectStringFormat(typeHint);
         if (stringVisitor != null) {
            Set<String> enums = new LinkedHashSet();
            if (serializers != null && serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
               for(SerializableString value : this._valuesByToString.values()) {
                  enums.add(value.getValue());
               }
            } else {
               for(SerializableString value : this._values.values()) {
                  enums.add(value.getValue());
               }
            }

            stringVisitor.enumTypes(enums);
         }

      }
   }

   protected final boolean _serializeAsIndex(SerializerProvider serializers) {
      return this._serializeAsIndex != null ? this._serializeAsIndex : serializers.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX);
   }

   protected static Boolean _isShapeWrittenUsingIndex(Class<?> enumClass, JsonFormat.Value format, boolean fromClass, Boolean defaultValue) {
      JsonFormat.Shape shape = format == null ? null : format.getShape();
      if (shape == null) {
         return defaultValue;
      } else if (shape != JsonFormat.Shape.ANY && shape != JsonFormat.Shape.SCALAR) {
         if (shape != JsonFormat.Shape.STRING && shape != JsonFormat.Shape.NATURAL) {
            if (!shape.isNumeric() && shape != JsonFormat.Shape.ARRAY) {
               throw new IllegalArgumentException(String.format("Unsupported serialization shape (%s) for Enum %s, not supported as %s annotation", shape, enumClass.getName(), fromClass ? "class" : "property"));
            } else {
               return Boolean.TRUE;
            }
         } else {
            return Boolean.FALSE;
         }
      } else {
         return defaultValue;
      }
   }

   protected static EnumValues constructEnumNamingStrategyValues(SerializationConfig config, Class<Enum<?>> enumClass, AnnotatedClass annotatedClass) {
      Object namingDef = config.getAnnotationIntrospector().findEnumNamingStrategy(config, annotatedClass);
      EnumNamingStrategy enumNamingStrategy = EnumNamingStrategyFactory.createEnumNamingStrategyInstance(namingDef, config.canOverrideAccessModifiers(), config.getEnumNamingStrategy());
      return enumNamingStrategy == null ? null : EnumValues.constructUsingEnumNamingStrategy(config, annotatedClass, enumNamingStrategy);
   }
}
