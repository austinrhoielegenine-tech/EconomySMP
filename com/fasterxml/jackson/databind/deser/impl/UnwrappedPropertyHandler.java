package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UnwrappedPropertyHandler {
   public static final String JSON_UNWRAPPED_NAME_PREFIX = "@JsonUnwrapped/";
   protected final List<SettableBeanProperty> _creatorProperties;
   protected final List<SettableBeanProperty> _properties;

   public UnwrappedPropertyHandler() {
      this._creatorProperties = new ArrayList();
      this._properties = new ArrayList();
   }

   /** @deprecated */
   @Deprecated
   protected UnwrappedPropertyHandler(List<SettableBeanProperty> props) {
      this(new ArrayList(), props);
   }

   protected UnwrappedPropertyHandler(List<SettableBeanProperty> creatorProps, List<SettableBeanProperty> props) {
      this._creatorProperties = creatorProps;
      this._properties = props;
   }

   public void addCreatorProperty(SettableBeanProperty property) {
      this._creatorProperties.add(property);
   }

   public void addProperty(SettableBeanProperty property) {
      this._properties.add(property);
   }

   public UnwrappedPropertyHandler renameAll(NameTransformer transformer) {
      return new UnwrappedPropertyHandler(this.renameProperties(this._creatorProperties, transformer), this.renameProperties(this._properties, transformer));
   }

   private List<SettableBeanProperty> renameProperties(Collection<SettableBeanProperty> properties, NameTransformer transformer) {
      List<SettableBeanProperty> newProps = new ArrayList(properties.size());

      for(SettableBeanProperty prop : properties) {
         if (prop == null) {
            newProps.add((Object)null);
         } else {
            newProps.add(prop.unwrapped(transformer));
         }
      }

      return newProps;
   }

   public PropertyValueBuffer processUnwrappedCreatorProperties(JsonParser originalParser, DeserializationContext ctxt, PropertyValueBuffer values, TokenBuffer buffered) throws IOException {
      for(SettableBeanProperty prop : this._creatorProperties) {
         JsonParser p = buffered.asParser(originalParser.streamReadConstraints());
         p.nextToken();
         values.assignParameter(prop, prop.deserialize(p, ctxt));
      }

      return values;
   }

   public Object processUnwrapped(JsonParser originalParser, DeserializationContext ctxt, Object bean, TokenBuffer buffered) throws IOException {
      for(SettableBeanProperty prop : this._properties) {
         JsonParser p = buffered.asParser(originalParser.streamReadConstraints());
         p.nextToken();
         prop.deserializeAndSet(p, ctxt, bean);
      }

      return bean;
   }

   public static PropertyName creatorParamName(int index) {
      return new PropertyName("@JsonUnwrapped/" + index);
   }
}
