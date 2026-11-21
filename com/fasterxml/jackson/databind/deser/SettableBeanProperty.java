package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.impl.FailingDeserializer;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.ViewMatcher;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;

public abstract class SettableBeanProperty extends ConcreteBeanPropertyBase implements Serializable {
   protected static final JsonDeserializer<Object> MISSING_VALUE_DESERIALIZER = new FailingDeserializer("No _valueDeserializer assigned");
   protected final PropertyName _propName;
   protected final JavaType _type;
   protected final PropertyName _wrapperName;
   protected final transient Annotations _contextAnnotations;
   protected final JsonDeserializer<Object> _valueDeserializer;
   protected final TypeDeserializer _valueTypeDeserializer;
   protected final NullValueProvider _nullProvider;
   protected String _managedReferenceName;
   protected ObjectIdInfo _objectIdInfo;
   protected ViewMatcher _viewMatcher;
   protected int _propertyIndex;

   protected SettableBeanProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations) {
      this(propDef.getFullName(), type, propDef.getWrapperName(), typeDeser, contextAnnotations, propDef.getMetadata());
   }

   protected SettableBeanProperty(PropertyName propName, JavaType type, PropertyName wrapper, TypeDeserializer typeDeser, Annotations contextAnnotations, PropertyMetadata metadata) {
      super(metadata);
      this._propertyIndex = -1;
      if (propName == null) {
         this._propName = PropertyName.NO_NAME;
      } else {
         this._propName = propName.internSimpleName();
      }

      this._type = type;
      this._wrapperName = wrapper;
      this._contextAnnotations = contextAnnotations;
      this._viewMatcher = null;
      if (typeDeser != null) {
         typeDeser = typeDeser.forProperty(this);
      }

      this._valueTypeDeserializer = typeDeser;
      this._valueDeserializer = MISSING_VALUE_DESERIALIZER;
      this._nullProvider = MISSING_VALUE_DESERIALIZER;
   }

   protected SettableBeanProperty(PropertyName propName, JavaType type, PropertyMetadata metadata, JsonDeserializer<Object> valueDeser) {
      super(metadata);
      this._propertyIndex = -1;
      if (propName == null) {
         this._propName = PropertyName.NO_NAME;
      } else {
         this._propName = propName.internSimpleName();
      }

      this._type = type;
      this._wrapperName = null;
      this._contextAnnotations = null;
      this._viewMatcher = null;
      this._valueTypeDeserializer = null;
      this._valueDeserializer = valueDeser;
      this._nullProvider = valueDeser;
   }

   protected SettableBeanProperty(SettableBeanProperty src) {
      super((ConcreteBeanPropertyBase)src);
      this._propertyIndex = -1;
      this._propName = src._propName;
      this._type = src._type;
      this._wrapperName = src._wrapperName;
      this._contextAnnotations = src._contextAnnotations;
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._managedReferenceName = src._managedReferenceName;
      this._propertyIndex = src._propertyIndex;
      this._viewMatcher = src._viewMatcher;
      this._objectIdInfo = src._objectIdInfo;
      this._nullProvider = src._nullProvider;
   }

   protected SettableBeanProperty(SettableBeanProperty src, JsonDeserializer<?> deser, NullValueProvider nuller) {
      super((ConcreteBeanPropertyBase)src);
      this._propertyIndex = -1;
      this._propName = src._propName;
      this._type = src._type;
      this._wrapperName = src._wrapperName;
      this._contextAnnotations = src._contextAnnotations;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._managedReferenceName = src._managedReferenceName;
      this._propertyIndex = src._propertyIndex;
      if (deser == null) {
         this._valueDeserializer = MISSING_VALUE_DESERIALIZER;
      } else {
         this._valueDeserializer = deser;
      }

      this._viewMatcher = src._viewMatcher;
      this._objectIdInfo = src._objectIdInfo;
      if (nuller == MISSING_VALUE_DESERIALIZER) {
         nuller = this._valueDeserializer;
      }

      this._nullProvider = nuller;
   }

   protected SettableBeanProperty(SettableBeanProperty src, PropertyName newName) {
      super((ConcreteBeanPropertyBase)src);
      this._propertyIndex = -1;
      this._propName = newName;
      this._type = src._type;
      this._wrapperName = src._wrapperName;
      this._contextAnnotations = src._contextAnnotations;
      this._valueDeserializer = src._valueDeserializer;
      this._valueTypeDeserializer = src._valueTypeDeserializer;
      this._managedReferenceName = src._managedReferenceName;
      this._propertyIndex = src._propertyIndex;
      this._viewMatcher = src._viewMatcher;
      this._objectIdInfo = src._objectIdInfo;
      this._nullProvider = src._nullProvider;
   }

   public abstract SettableBeanProperty withValueDeserializer(JsonDeserializer<?> var1);

   public abstract SettableBeanProperty withName(PropertyName var1);

   public SettableBeanProperty withSimpleName(String simpleName) {
      PropertyName n = this._propName == null ? new PropertyName(simpleName) : this._propName.withSimpleName(simpleName);
      return n == this._propName ? this : this.withName(n);
   }

   public abstract SettableBeanProperty withNullProvider(NullValueProvider var1);

   public void setManagedReferenceName(String n) {
      this._managedReferenceName = n;
   }

   public void setObjectIdInfo(ObjectIdInfo objectIdInfo) {
      this._objectIdInfo = objectIdInfo;
   }

   public void setViews(Class<?>[] views) {
      if (views == null) {
         this._viewMatcher = null;
      } else {
         this._viewMatcher = ViewMatcher.construct(views);
      }

   }

   public void assignIndex(int index) {
      if (this._propertyIndex != -1) {
         throw new IllegalStateException("Property '" + this.getName() + "' already had index (" + this._propertyIndex + "), trying to assign " + index);
      } else {
         this._propertyIndex = index;
      }
   }

   public void fixAccess(DeserializationConfig config) {
   }

   public void markAsIgnorable() {
   }

   public boolean isIgnorable() {
      return false;
   }

   public boolean isMerging() {
      return false;
   }

   public final String getName() {
      return this._propName.getSimpleName();
   }

   public PropertyName getFullName() {
      return this._propName;
   }

   public JavaType getType() {
      return this._type;
   }

   public PropertyName getWrapperName() {
      return this._wrapperName;
   }

   public abstract AnnotatedMember getMember();

   public abstract <A extends Annotation> A getAnnotation(Class<A> var1);

   public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
      return (A)this._contextAnnotations.get(acls);
   }

   public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
      if (this.isRequired()) {
         objectVisitor.property(this);
      } else {
         objectVisitor.optionalProperty(this);
      }

   }

   protected Class<?> getDeclaringClass() {
      return this.getMember().getDeclaringClass();
   }

   public String getManagedReferenceName() {
      return this._managedReferenceName;
   }

   public ObjectIdInfo getObjectIdInfo() {
      return this._objectIdInfo;
   }

   public boolean hasValueDeserializer() {
      return this._valueDeserializer != null && this._valueDeserializer != MISSING_VALUE_DESERIALIZER;
   }

   public boolean hasValueTypeDeserializer() {
      return this._valueTypeDeserializer != null;
   }

   public JsonDeserializer<Object> getValueDeserializer() {
      JsonDeserializer<Object> deser = this._valueDeserializer;
      return deser == MISSING_VALUE_DESERIALIZER ? null : deser;
   }

   public TypeDeserializer getValueTypeDeserializer() {
      return this._valueTypeDeserializer;
   }

   public NullValueProvider getNullValueProvider() {
      return this._nullProvider;
   }

   public boolean visibleInView(Class<?> activeView) {
      return this._viewMatcher == null || this._viewMatcher.isVisibleForView(activeView);
   }

   public boolean hasViews() {
      return this._viewMatcher != null;
   }

   public int getPropertyIndex() {
      return this._propertyIndex;
   }

   public int getCreatorIndex() {
      throw new IllegalStateException(String.format("Internal error: no creator index for property '%s' (of type %s)", this.getName(), this.getClass().getName()));
   }

   public Object getInjectableValueId() {
      return null;
   }

   public JacksonInject.Value getInjectionDefinition() {
      return null;
   }

   public boolean isInjectionOnly() {
      return false;
   }

   public abstract void deserializeAndSet(JsonParser var1, DeserializationContext var2, Object var3) throws IOException;

   public abstract Object deserializeSetAndReturn(JsonParser var1, DeserializationContext var2, Object var3) throws IOException;

   public abstract void set(Object var1, Object var2) throws IOException;

   public abstract Object setAndReturn(Object var1, Object var2) throws IOException;

   public final Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NULL)) {
         return this._nullProvider.getNullValue(ctxt);
      } else if (this._valueTypeDeserializer != null) {
         return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
      } else {
         Object value = this._valueDeserializer.deserialize(p, ctxt);
         if (value == null) {
            value = this._nullProvider.getNullValue(ctxt);
         }

         return value;
      }
   }

   public final Object deserializeWith(JsonParser p, DeserializationContext ctxt, Object toUpdate) throws IOException {
      if (p.hasToken(JsonToken.VALUE_NULL)) {
         return NullsConstantProvider.isSkipper(this._nullProvider) ? toUpdate : this._nullProvider.getNullValue(ctxt);
      } else if (this._valueTypeDeserializer != null) {
         JavaType subType = ctxt.getTypeFactory().constructType(toUpdate.getClass());
         JsonDeserializer<Object> subTypeValueDeserializer = ctxt.findContextualValueDeserializer(subType, this);
         return subTypeValueDeserializer.deserialize(p, ctxt, toUpdate);
      } else {
         Object value = this._valueDeserializer.deserialize(p, ctxt, toUpdate);
         if (value == null) {
            if (NullsConstantProvider.isSkipper(this._nullProvider)) {
               return toUpdate;
            }

            value = this._nullProvider.getNullValue(ctxt);
         }

         return value;
      }
   }

   public SettableBeanProperty unwrapped(NameTransformer xf) {
      String newName = xf.transform(this.getName());
      SettableBeanProperty renamed = this.withSimpleName(newName);
      JsonDeserializer<?> deser = renamed.getValueDeserializer();
      if (deser != null) {
         JsonDeserializer<Object> newDeser = deser.unwrappingDeserializer(xf);
         if (newDeser != deser) {
            renamed = renamed.withValueDeserializer(newDeser);
         }
      }

      return renamed;
   }

   protected void _throwAsIOE(JsonParser p, Exception e, Object value) throws IOException {
      if (e instanceof IllegalArgumentException) {
         String actType = ClassUtil.classNameOf(value);
         StringBuilder msg = (new StringBuilder("Problem deserializing property '")).append(this.getName()).append("' (expected type: ").append(this.getType()).append("; actual type: ").append(actType).append(")");
         String origMsg = ClassUtil.exceptionMessage(e);
         if (origMsg != null) {
            msg.append(", problem: ").append(origMsg);
         } else {
            msg.append(" (no error message provided)");
         }

         throw JsonMappingException.from((JsonParser)p, msg.toString(), e);
      } else {
         this._throwAsIOE(p, e);
      }
   }

   protected IOException _throwAsIOE(JsonParser p, Exception e) throws IOException {
      ClassUtil.throwIfIOE(e);
      ClassUtil.throwIfRTE(e);
      Throwable th = ClassUtil.getRootCause(e);
      throw JsonMappingException.from(p, ClassUtil.exceptionMessage(th), th);
   }

   /** @deprecated */
   @Deprecated
   protected IOException _throwAsIOE(Exception e) throws IOException {
      return this._throwAsIOE((JsonParser)null, e);
   }

   protected void _throwAsIOE(Exception e, Object value) throws IOException {
      this._throwAsIOE((JsonParser)null, e, value);
   }

   public String toString() {
      return "[property '" + this.getName() + "']";
   }

   public abstract static class Delegating extends SettableBeanProperty {
      protected final SettableBeanProperty delegate;

      protected Delegating(SettableBeanProperty d) {
         super(d);
         this.delegate = d;
      }

      protected abstract SettableBeanProperty withDelegate(SettableBeanProperty var1);

      protected SettableBeanProperty _with(SettableBeanProperty newDelegate) {
         return (SettableBeanProperty)(newDelegate == this.delegate ? this : this.withDelegate(newDelegate));
      }

      public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
         return this._with(this.delegate.withValueDeserializer(deser));
      }

      public SettableBeanProperty withName(PropertyName newName) {
         return this._with(this.delegate.withName(newName));
      }

      public SettableBeanProperty withNullProvider(NullValueProvider nva) {
         return this._with(this.delegate.withNullProvider(nva));
      }

      public void assignIndex(int index) {
         this.delegate.assignIndex(index);
      }

      public void fixAccess(DeserializationConfig config) {
         this.delegate.fixAccess(config);
      }

      protected Class<?> getDeclaringClass() {
         return this.delegate.getDeclaringClass();
      }

      public String getManagedReferenceName() {
         return this.delegate.getManagedReferenceName();
      }

      public ObjectIdInfo getObjectIdInfo() {
         return this.delegate.getObjectIdInfo();
      }

      public boolean hasValueDeserializer() {
         return this.delegate.hasValueDeserializer();
      }

      public boolean hasValueTypeDeserializer() {
         return this.delegate.hasValueTypeDeserializer();
      }

      public JsonDeserializer<Object> getValueDeserializer() {
         return this.delegate.getValueDeserializer();
      }

      public TypeDeserializer getValueTypeDeserializer() {
         return this.delegate.getValueTypeDeserializer();
      }

      public boolean visibleInView(Class<?> activeView) {
         return this.delegate.visibleInView(activeView);
      }

      public boolean hasViews() {
         return this.delegate.hasViews();
      }

      public int getPropertyIndex() {
         return this.delegate.getPropertyIndex();
      }

      public int getCreatorIndex() {
         return this.delegate.getCreatorIndex();
      }

      public Object getInjectableValueId() {
         return this.delegate.getInjectableValueId();
      }

      public boolean isInjectionOnly() {
         return this.delegate.isInjectionOnly();
      }

      public AnnotatedMember getMember() {
         return this.delegate.getMember();
      }

      public <A extends Annotation> A getAnnotation(Class<A> acls) {
         return (A)this.delegate.getAnnotation(acls);
      }

      public SettableBeanProperty getDelegate() {
         return this.delegate;
      }

      public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
         this.delegate.deserializeAndSet(p, ctxt, instance);
      }

      public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
         return this.delegate.deserializeSetAndReturn(p, ctxt, instance);
      }

      public void set(Object instance, Object value) throws IOException {
         this.delegate.set(instance, value);
      }

      public Object setAndReturn(Object instance, Object value) throws IOException {
         return this.delegate.setAndReturn(instance, value);
      }
   }
}
