package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableAnyProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public final class PropertyBasedCreator {
   protected final int _propertyCount;
   protected final ValueInstantiator _valueInstantiator;
   protected final HashMap<String, SettableBeanProperty> _propertyLookup;
   protected final SettableBeanProperty[] _allProperties;
   protected final BitSet _injectablePropIndexes;

   protected PropertyBasedCreator(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps, boolean caseInsensitive, boolean addAliases) {
      this._valueInstantiator = valueInstantiator;
      if (caseInsensitive) {
         this._propertyLookup = PropertyBasedCreator.CaseInsensitiveMap.construct(ctxt.getConfig().getLocale());
      } else {
         this._propertyLookup = new HashMap();
      }

      if (addAliases) {
         DeserializationConfig config = ctxt.getConfig();

         for(SettableBeanProperty prop : creatorProps) {
            if (!prop.isIgnorable()) {
               List<PropertyName> aliases = prop.findAliases(config);
               if (!aliases.isEmpty()) {
                  for(PropertyName pn : aliases) {
                     this._propertyLookup.put(pn.getSimpleName(), prop);
                  }
               }
            }
         }
      }

      int len = creatorProps.length;
      this._propertyCount = len;
      this._allProperties = new SettableBeanProperty[len];
      BitSet injectablePropIndexes = null;

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = creatorProps[i];
         this._allProperties[i] = prop;
         if (!prop.isIgnorable()) {
            this._propertyLookup.put(prop.getName(), prop);
         }

         if (prop.getInjectionDefinition() != null) {
            if (injectablePropIndexes == null) {
               injectablePropIndexes = new BitSet(len);
            }

            injectablePropIndexes.set(i);
         }
      }

      this._injectablePropIndexes = injectablePropIndexes;
   }

   protected PropertyBasedCreator(PropertyBasedCreator base, HashMap<String, SettableBeanProperty> propertyLookup, SettableBeanProperty[] allProperties) {
      this._propertyCount = base._propertyCount;
      this._valueInstantiator = base._valueInstantiator;
      this._injectablePropIndexes = base._injectablePropIndexes;
      this._propertyLookup = propertyLookup;
      this._allProperties = allProperties;
   }

   public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps, BeanPropertyMap allProperties) throws JsonMappingException {
      int len = srcCreatorProps.length;
      SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = srcCreatorProps[i];
         if (!prop.hasValueDeserializer() && !prop.isInjectionOnly()) {
            prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
         }

         creatorProps[i] = prop;
      }

      return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, allProperties.isCaseInsensitive(), true);
   }

   public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps, boolean caseInsensitive) throws JsonMappingException {
      int len = srcCreatorProps.length;
      SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = srcCreatorProps[i];
         if (!prop.hasValueDeserializer()) {
            prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
         }

         creatorProps[i] = prop;
      }

      return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, caseInsensitive, false);
   }

   public PropertyBasedCreator renameAll(NameTransformer transformer) {
      if (transformer != null && transformer != NameTransformer.NOP) {
         int len = this._allProperties.length;
         HashMap<String, SettableBeanProperty> newLookup = new HashMap(this._propertyLookup);
         List<SettableBeanProperty> newProps = new ArrayList(len);

         for(SettableBeanProperty prop : this._allProperties) {
            if (prop == null) {
               newProps.add((Object)null);
            } else {
               SettableBeanProperty renamedProperty = prop.unwrapped(transformer);
               String oldName = prop.getName();
               String newName = renamedProperty.getName();
               newProps.add(renamedProperty);
               if (!oldName.equals(newName) && newLookup.containsKey(oldName)) {
                  newLookup.remove(oldName);
                  newLookup.put(newName, renamedProperty);
               }
            }
         }

         return new PropertyBasedCreator(this, newLookup, (SettableBeanProperty[])newProps.toArray(new SettableBeanProperty[0]));
      } else {
         return this;
      }
   }

   public Collection<SettableBeanProperty> properties() {
      return this._propertyLookup.values();
   }

   public SettableBeanProperty findCreatorProperty(String name) {
      return (SettableBeanProperty)this._propertyLookup.get(name);
   }

   public SettableBeanProperty findCreatorProperty(int propertyIndex) {
      for(SettableBeanProperty prop : this._propertyLookup.values()) {
         if (prop.getPropertyIndex() == propertyIndex) {
            return prop;
         }
      }

      return null;
   }

   public PropertyValueBuffer startBuilding(JsonParser p, DeserializationContext ctxt, ObjectIdReader oir) {
      return new PropertyValueBuffer(p, ctxt, this._propertyCount, oir, (SettableAnyProperty)null, this._injectablePropIndexes);
   }

   public PropertyValueBuffer startBuildingWithAnySetter(JsonParser p, DeserializationContext ctxt, ObjectIdReader oir, SettableAnyProperty anySetter) {
      return new PropertyValueBuffer(p, ctxt, this._propertyCount, oir, anySetter, this._injectablePropIndexes);
   }

   public Object build(DeserializationContext ctxt, PropertyValueBuffer buffer) throws IOException {
      Object bean = this._valueInstantiator.createFromObjectWith(ctxt, this._allProperties, buffer);
      if (bean != null) {
         bean = buffer.handleIdValue(ctxt, bean);

         for(PropertyValue pv = buffer.buffered(); pv != null; pv = pv.next) {
            pv.assign(ctxt, bean);
         }
      }

      return bean;
   }

   static class CaseInsensitiveMap extends HashMap<String, SettableBeanProperty> {
      private static final long serialVersionUID = 1L;
      protected final Locale _locale;

      public CaseInsensitiveMap(Locale l) {
         this._locale = l;
      }

      public static CaseInsensitiveMap construct(Locale l) {
         return new CaseInsensitiveMap(l);
      }

      public SettableBeanProperty get(Object key0) {
         return (SettableBeanProperty)super.get(((String)key0).toLowerCase(this._locale));
      }

      public SettableBeanProperty put(String key, SettableBeanProperty value) {
         key = key.toLowerCase(this._locale);
         return (SettableBeanProperty)super.put(key, value);
      }
   }
}
