package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.IOException;
import java.util.Calendar;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

public class CoreXMLSerializers extends Serializers.Base {
   public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
      Class<?> raw = type.getRawClass();
      if (Duration.class.isAssignableFrom(raw)) {
         return ToStringSerializer.instance;
      } else if (QName.class.isAssignableFrom(raw)) {
         return CoreXMLSerializers.QNameSerializer.instance;
      } else {
         return XMLGregorianCalendar.class.isAssignableFrom(raw) ? CoreXMLSerializers.XMLGregorianCalendarSerializer.instance : null;
      }
   }

   public static class XMLGregorianCalendarSerializer extends StdSerializer<XMLGregorianCalendar> implements ContextualSerializer {
      static final XMLGregorianCalendarSerializer instance = new XMLGregorianCalendarSerializer();
      final JsonSerializer<Object> _delegate;

      public XMLGregorianCalendarSerializer() {
         this(CalendarSerializer.instance);
      }

      protected XMLGregorianCalendarSerializer(JsonSerializer<?> del) {
         super(XMLGregorianCalendar.class);
         this._delegate = del;
      }

      public JsonSerializer<?> getDelegatee() {
         return this._delegate;
      }

      public boolean isEmpty(SerializerProvider provider, XMLGregorianCalendar value) {
         return this._delegate.isEmpty(provider, this._convert(value));
      }

      public void serialize(XMLGregorianCalendar value, JsonGenerator gen, SerializerProvider provider) throws IOException {
         this._delegate.serialize(this._convert(value), gen, provider);
      }

      public void serializeWithType(XMLGregorianCalendar value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
         WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, (Class)XMLGregorianCalendar.class, (JsonToken)JsonToken.VALUE_STRING));
         this.serialize(value, g, provider);
         typeSer.writeTypeSuffix(g, typeIdDef);
      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         this._delegate.acceptJsonFormatVisitor(visitor, (JavaType)null);
      }

      public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
         JsonSerializer<?> ser = prov.handlePrimaryContextualization(this._delegate, property);
         return ser != this._delegate ? new XMLGregorianCalendarSerializer(ser) : this;
      }

      protected Calendar _convert(XMLGregorianCalendar input) {
         return input == null ? null : input.toGregorianCalendar();
      }
   }

   public static class QNameSerializer extends StdSerializer<QName> implements ContextualSerializer {
      private static final long serialVersionUID = 1L;
      public static final JsonSerializer<?> instance = new QNameSerializer();

      public QNameSerializer() {
         super(QName.class);
      }

      public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
         JsonFormat.Value format = this.findFormatOverrides(serializers, property, this.handledType());
         if (format != null) {
            JsonFormat.Shape shape = format.getShape();
            if (shape == JsonFormat.Shape.OBJECT) {
               return this;
            }
         }

         return ToStringSerializer.instance;
      }

      public void serialize(QName value, JsonGenerator g, SerializerProvider ctxt) throws IOException {
         g.writeStartObject(value);
         this.serializeProperties(value, g, ctxt);
         g.writeEndObject();
      }

      public final void serializeWithType(QName value, JsonGenerator g, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
         WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_OBJECT));
         this.serializeProperties(value, g, ctxt);
         typeSer.writeTypeSuffix(g, typeIdDef);
      }

      private void serializeProperties(QName value, JsonGenerator g, SerializerProvider ctxt) throws IOException {
         g.writeStringField("localPart", value.getLocalPart());
         if (!value.getNamespaceURI().isEmpty()) {
            g.writeStringField("namespaceURI", value.getNamespaceURI());
         }

         if (!value.getPrefix().isEmpty()) {
            g.writeStringField("prefix", value.getPrefix());
         }

      }

      public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
         visitor.expectObjectFormat(typeHint);
      }
   }
}
