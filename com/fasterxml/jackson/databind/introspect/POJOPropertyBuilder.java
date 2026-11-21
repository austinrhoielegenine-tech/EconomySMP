package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class POJOPropertyBuilder extends BeanPropertyDefinition implements Comparable<POJOPropertyBuilder> {
   private static final AnnotationIntrospector.ReferenceProperty NOT_REFEFERENCE_PROP = AnnotationIntrospector.ReferenceProperty.managed("");
   protected final boolean _forSerialization;
   protected final MapperConfig<?> _config;
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final PropertyName _name;
   protected final PropertyName _internalName;
   protected Linked<AnnotatedField> _fields;
   protected Linked<AnnotatedParameter> _ctorParameters;
   protected Linked<AnnotatedMethod> _getters;
   protected Linked<AnnotatedMethod> _setters;
   protected transient PropertyMetadata _metadata;
   protected transient AnnotationIntrospector.ReferenceProperty _referenceInfo;

   public POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName) {
      this(config, ai, forSerialization, internalName, internalName);
   }

   protected POJOPropertyBuilder(MapperConfig<?> config, AnnotationIntrospector ai, boolean forSerialization, PropertyName internalName, PropertyName name) {
      this._config = config;
      this._annotationIntrospector = ai;
      this._internalName = internalName;
      this._name = name;
      this._forSerialization = forSerialization;
   }

   protected POJOPropertyBuilder(POJOPropertyBuilder src, PropertyName newName) {
      this._config = src._config;
      this._annotationIntrospector = src._annotationIntrospector;
      this._internalName = src._internalName;
      this._name = newName;
      this._fields = src._fields;
      this._ctorParameters = src._ctorParameters;
      this._getters = src._getters;
      this._setters = src._setters;
      this._forSerialization = src._forSerialization;
   }

   public POJOPropertyBuilder withName(PropertyName newName) {
      return new POJOPropertyBuilder(this, newName);
   }

   public POJOPropertyBuilder withSimpleName(String newSimpleName) {
      PropertyName newName = this._name.withSimpleName(newSimpleName);
      return newName == this._name ? this : new POJOPropertyBuilder(this, newName);
   }

   public int compareTo(POJOPropertyBuilder other) {
      if (this._ctorParameters != null) {
         if (other._ctorParameters == null) {
            return -1;
         }
      } else if (other._ctorParameters != null) {
         return 1;
      }

      return this.getName().compareTo(other.getName());
   }

   public String getName() {
      return this._name == null ? null : this._name.getSimpleName();
   }

   public PropertyName getFullName() {
      return this._name;
   }

   public boolean hasName(PropertyName name) {
      return this._name.equals(name);
   }

   public String getInternalName() {
      return this._internalName.getSimpleName();
   }

   public PropertyName getWrapperName() {
      AnnotatedMember member = this.getPrimaryMember();
      return member != null && this._annotationIntrospector != null ? this._annotationIntrospector.findWrapperName(member) : null;
   }

   public boolean isExplicitlyIncluded() {
      return this._anyExplicits(this._fields) || this._anyExplicits(this._getters) || this._anyExplicits(this._setters) || this._anyExplicitNames(this._ctorParameters);
   }

   public boolean isExplicitlyNamed() {
      return this._anyExplicitNames(this._fields) || this._anyExplicitNames(this._getters) || this._anyExplicitNames(this._setters) || this._anyExplicitNames(this._ctorParameters);
   }

   public PropertyMetadata getMetadata() {
      if (this._metadata == null) {
         AnnotatedMember prim = this.getPrimaryMemberUnchecked();
         if (prim == null) {
            this._metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
         } else {
            Boolean b = this._annotationIntrospector.hasRequiredMarker(prim);
            String desc = this._annotationIntrospector.findPropertyDescription(prim);
            Integer idx = this._annotationIntrospector.findPropertyIndex(prim);
            String def = this._annotationIntrospector.findPropertyDefaultValue(prim);
            if (b == null && idx == null && def == null) {
               this._metadata = desc == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : PropertyMetadata.STD_REQUIRED_OR_OPTIONAL.withDescription(desc);
            } else {
               this._metadata = PropertyMetadata.construct(b, desc, idx, def);
            }

            if (!this._forSerialization) {
               this._metadata = this._getSetterInfo(this._metadata, prim);
            }
         }
      }

      return this._metadata;
   }

   protected PropertyMetadata _getSetterInfo(PropertyMetadata metadata, AnnotatedMember primary) {
      boolean needMerge = true;
      Nulls valueNulls = null;
      Nulls contentNulls = null;
      AnnotatedMember acc = this.getAccessor();
      if (primary != null) {
         if (this._annotationIntrospector != null) {
            if (acc != null) {
               Boolean b = this._annotationIntrospector.findMergeInfo(primary);
               if (b != null) {
                  needMerge = false;
                  if (b) {
                     metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForPropertyOverride(acc));
                  }
               }
            }

            JsonSetter.Value setterInfo = this._annotationIntrospector.findSetterInfo(primary);
            if (setterInfo != null) {
               valueNulls = setterInfo.nonDefaultValueNulls();
               contentNulls = setterInfo.nonDefaultContentNulls();
            }
         }

         if (needMerge || valueNulls == null || contentNulls == null) {
            Class<?> rawType = this._rawTypeOf(primary);
            ConfigOverride co = this._config.getConfigOverride(rawType);
            JsonSetter.Value setterInfo = co.getSetterInfo();
            if (setterInfo != null) {
               if (valueNulls == null) {
                  valueNulls = setterInfo.nonDefaultValueNulls();
               }

               if (contentNulls == null) {
                  contentNulls = setterInfo.nonDefaultContentNulls();
               }
            }

            if (needMerge && acc != null) {
               Boolean b = co.getMergeable();
               if (b != null) {
                  needMerge = false;
                  if (b) {
                     metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForTypeOverride(acc));
                  }
               }
            }
         }
      }

      if (needMerge || valueNulls == null || contentNulls == null) {
         JsonSetter.Value setterInfo = this._config.getDefaultSetterInfo();
         if (valueNulls == null) {
            valueNulls = setterInfo.nonDefaultValueNulls();
         }

         if (contentNulls == null) {
            contentNulls = setterInfo.nonDefaultContentNulls();
         }

         if (needMerge) {
            Boolean b = this._config.getDefaultMergeable();
            if (Boolean.TRUE.equals(b) && acc != null) {
               metadata = metadata.withMergeInfo(PropertyMetadata.MergeInfo.createForDefaults(acc));
            }
         }
      }

      if (valueNulls != null || contentNulls != null) {
         metadata = metadata.withNulls(valueNulls, contentNulls);
      }

      return metadata;
   }

   public JavaType getPrimaryType() {
      if (this._forSerialization) {
         AnnotatedMember m = this.getGetter();
         if (m == null) {
            m = this.getField();
            if (m == null) {
               return TypeFactory.unknownType();
            }
         }

         return m.getType();
      } else {
         AnnotatedMember m = this.getConstructorParameter();
         if (m == null) {
            AnnotatedMember m = this.getSetter();
            if (m != null) {
               return ((AnnotatedMethod)m).getParameterType(0);
            }

            m = this.getField();
         }

         if (m == null) {
            m = this.getGetter();
            if (m == null) {
               return TypeFactory.unknownType();
            }
         }

         return m.getType();
      }
   }

   public Class<?> getRawPrimaryType() {
      return this.getPrimaryType().getRawClass();
   }

   public boolean hasGetter() {
      return this._getters != null;
   }

   public boolean hasSetter() {
      return this._setters != null;
   }

   public boolean hasField() {
      return this._fields != null;
   }

   public boolean hasFieldAndNothingElse() {
      return this._fields != null && this._getters == null && this._setters == null && this._ctorParameters == null;
   }

   public boolean hasConstructorParameter() {
      return this._ctorParameters != null;
   }

   public boolean couldDeserialize() {
      return this._ctorParameters != null || this._setters != null || this._fields != null && this._anyVisible(this._fields);
   }

   public boolean couldSerialize() {
      return this._getters != null || this._fields != null;
   }

   public AnnotatedMethod getGetter() {
      Linked<AnnotatedMethod> curr = this._getters;
      if (curr == null) {
         return null;
      } else {
         Linked<AnnotatedMethod> next = curr.next;
         if (next == null) {
            return (AnnotatedMethod)curr.value;
         } else {
            for(; next != null; next = next.next) {
               Class<?> currClass = ((AnnotatedMethod)curr.value).getDeclaringClass();
               Class<?> nextClass = ((AnnotatedMethod)next.value).getDeclaringClass();
               if (currClass != nextClass) {
                  if (currClass.isAssignableFrom(nextClass)) {
                     curr = next;
                     continue;
                  }

                  if (nextClass.isAssignableFrom(currClass)) {
                     continue;
                  }
               }

               int priNext = this._getterPriority((AnnotatedMethod)next.value);
               int priCurr = this._getterPriority((AnnotatedMethod)curr.value);
               if (priNext == priCurr) {
                  throw new IllegalArgumentException("Conflicting getter definitions for property \"" + this.getName() + "\": " + ((AnnotatedMethod)curr.value).getFullName() + " vs " + ((AnnotatedMethod)next.value).getFullName());
               }

               if (priNext < priCurr) {
                  curr = next;
               }
            }

            this._getters = curr.withoutNext();
            return (AnnotatedMethod)curr.value;
         }
      }
   }

   protected AnnotatedMethod getGetterUnchecked() {
      Linked<AnnotatedMethod> curr = this._getters;
      return curr == null ? null : (AnnotatedMethod)curr.value;
   }

   public AnnotatedMethod getSetter() {
      Linked<AnnotatedMethod> curr = this._setters;
      if (curr == null) {
         return null;
      } else {
         Linked<AnnotatedMethod> next = curr.next;
         if (next == null) {
            return (AnnotatedMethod)curr.value;
         } else {
            for(; next != null; next = next.next) {
               AnnotatedMethod selected = this._selectSetter((AnnotatedMethod)curr.value, (AnnotatedMethod)next.value);
               if (selected != curr.value) {
                  if (selected != next.value) {
                     return this._selectSetterFromMultiple(curr, next);
                  }

                  curr = next;
               }
            }

            this._setters = curr.withoutNext();
            return (AnnotatedMethod)curr.value;
         }
      }
   }

   protected AnnotatedMethod getSetterUnchecked() {
      Linked<AnnotatedMethod> curr = this._setters;
      return curr == null ? null : (AnnotatedMethod)curr.value;
   }

   protected AnnotatedMethod _selectSetterFromMultiple(Linked<AnnotatedMethod> curr, Linked<AnnotatedMethod> next) {
      List<AnnotatedMethod> conflicts = new ArrayList();
      conflicts.add(curr.value);
      conflicts.add(next.value);

      for(Linked<AnnotatedMethod> var5 = next.next; var5 != null; var5 = var5.next) {
         AnnotatedMethod selected = this._selectSetter((AnnotatedMethod)curr.value, (AnnotatedMethod)var5.value);
         if (selected != curr.value) {
            if (selected == var5.value) {
               conflicts.clear();
               curr = var5;
            } else {
               conflicts.add(var5.value);
            }
         }
      }

      if (conflicts.isEmpty()) {
         this._setters = curr.withoutNext();
         return (AnnotatedMethod)curr.value;
      } else {
         String desc = (String)conflicts.stream().map(AnnotatedMethod::getFullName).collect(Collectors.joining(" vs "));
         throw new IllegalArgumentException(String.format("Conflicting setter definitions for property \"%s\": %s", this.getName(), desc));
      }
   }

   protected AnnotatedMethod _selectSetter(AnnotatedMethod currM, AnnotatedMethod nextM) {
      Class<?> currClass = currM.getDeclaringClass();
      Class<?> nextClass = nextM.getDeclaringClass();
      if (currClass != nextClass) {
         if (currClass.isAssignableFrom(nextClass)) {
            return nextM;
         }

         if (nextClass.isAssignableFrom(currClass)) {
            return currM;
         }
      }

      int priNext = this._setterPriority(nextM);
      int priCurr = this._setterPriority(currM);
      if (priNext != priCurr) {
         return priNext < priCurr ? nextM : currM;
      } else {
         return this._annotationIntrospector == null ? null : this._annotationIntrospector.resolveSetterConflict(this._config, currM, nextM);
      }
   }

   public AnnotatedField getField() {
      if (this._fields == null) {
         return null;
      } else {
         AnnotatedField field = (AnnotatedField)this._fields.value;

         for(Linked<AnnotatedField> next = this._fields.next; next != null; next = next.next) {
            AnnotatedField nextField = (AnnotatedField)next.value;
            Class<?> fieldClass = field.getDeclaringClass();
            Class<?> nextClass = nextField.getDeclaringClass();
            if (fieldClass != nextClass) {
               if (fieldClass.isAssignableFrom(nextClass)) {
                  field = nextField;
                  continue;
               }

               if (nextClass.isAssignableFrom(fieldClass)) {
                  continue;
               }
            }

            boolean currStatic = field.isStatic();
            boolean nextStatic = nextField.isStatic();
            if (currStatic == nextStatic) {
               throw new IllegalArgumentException("Multiple fields representing property \"" + this.getName() + "\": " + field.getFullName() + " vs " + nextField.getFullName());
            }

            if (currStatic) {
               field = nextField;
            }
         }

         return field;
      }
   }

   protected AnnotatedField getFieldUnchecked() {
      Linked<AnnotatedField> curr = this._fields;
      return curr == null ? null : (AnnotatedField)curr.value;
   }

   public AnnotatedParameter getConstructorParameter() {
      if (this._ctorParameters == null) {
         return null;
      } else {
         Linked<AnnotatedParameter> curr = this._ctorParameters;

         while(!(((AnnotatedParameter)curr.value).getOwner() instanceof AnnotatedConstructor)) {
            curr = curr.next;
            if (curr == null) {
               return (AnnotatedParameter)this._ctorParameters.value;
            }
         }

         return (AnnotatedParameter)curr.value;
      }
   }

   public Iterator<AnnotatedParameter> getConstructorParameters() {
      return (Iterator<AnnotatedParameter>)(this._ctorParameters == null ? ClassUtil.emptyIterator() : new MemberIterator(this._ctorParameters));
   }

   public AnnotatedMember getPrimaryMember() {
      if (this._forSerialization) {
         return this.getAccessor();
      } else {
         AnnotatedMember m = this.getMutator();
         if (m == null) {
            m = this.getAccessor();
         }

         return m;
      }
   }

   protected AnnotatedMember getPrimaryMemberUnchecked() {
      if (this._forSerialization) {
         if (this._getters != null) {
            return (AnnotatedMember)this._getters.value;
         } else {
            return this._fields != null ? (AnnotatedMember)this._fields.value : null;
         }
      } else if (this._ctorParameters != null) {
         return (AnnotatedMember)this._ctorParameters.value;
      } else if (this._setters != null) {
         return (AnnotatedMember)this._setters.value;
      } else if (this._fields != null) {
         return (AnnotatedMember)this._fields.value;
      } else {
         return this._getters != null ? (AnnotatedMember)this._getters.value : null;
      }
   }

   protected int _getterPriority(AnnotatedMethod m) {
      String name = m.getName();
      if (name.startsWith("get") && name.length() > 3) {
         return 1;
      } else {
         return name.startsWith("is") && name.length() > 2 ? 2 : 3;
      }
   }

   protected int _setterPriority(AnnotatedMethod m) {
      String name = m.getName();
      return name.startsWith("set") && name.length() > 3 ? 1 : 2;
   }

   public boolean hasFieldOrGetter(AnnotatedMember member) {
      return this._hasAccessor(this._fields, member) || this._hasAccessor(this._getters, member);
   }

   private boolean _hasAccessor(Linked<? extends AnnotatedMember> node, AnnotatedMember memberToMatch) {
      for(Member rawMemberToMatch = memberToMatch.getMember(); node != null; node = node.next) {
         if (((AnnotatedMember)node.value).getMember() == rawMemberToMatch) {
            return true;
         }
      }

      return false;
   }

   public Class<?>[] findViews() {
      return (Class[])this.fromMemberAnnotations(new WithMember<Class<?>[]>() {
         public Class<?>[] withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findViews(member);
         }
      });
   }

   public AnnotationIntrospector.ReferenceProperty findReferenceType() {
      AnnotationIntrospector.ReferenceProperty result = this._referenceInfo;
      if (result != null) {
         return result == NOT_REFEFERENCE_PROP ? null : result;
      } else {
         result = (AnnotationIntrospector.ReferenceProperty)this.fromMemberAnnotations(new WithMember<AnnotationIntrospector.ReferenceProperty>() {
            public AnnotationIntrospector.ReferenceProperty withMember(AnnotatedMember member) {
               return POJOPropertyBuilder.this._annotationIntrospector.findReferenceType(member);
            }
         });
         this._referenceInfo = result == null ? NOT_REFEFERENCE_PROP : result;
         return result;
      }
   }

   public boolean isTypeId() {
      Boolean b = (Boolean)this.fromMemberAnnotations(new WithMember<Boolean>() {
         public Boolean withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.isTypeId(member);
         }
      });
      return b != null && b;
   }

   public ObjectIdInfo findObjectIdInfo() {
      return (ObjectIdInfo)this.fromMemberAnnotations(new WithMember<ObjectIdInfo>() {
         public ObjectIdInfo withMember(AnnotatedMember member) {
            ObjectIdInfo info = POJOPropertyBuilder.this._annotationIntrospector.findObjectIdInfo(member);
            if (info != null) {
               info = POJOPropertyBuilder.this._annotationIntrospector.findObjectReferenceInfo(member, info);
            }

            return info;
         }
      });
   }

   public JsonInclude.Value findInclusion() {
      AnnotatedMember a = this.getAccessor();
      JsonInclude.Value v = this._annotationIntrospector == null ? null : this._annotationIntrospector.findPropertyInclusion(a);
      return v == null ? JsonInclude.Value.empty() : v;
   }

   public List<PropertyName> findAliases() {
      AnnotatedMember ann = this.getPrimaryMember();
      if (ann != null) {
         List<PropertyName> propertyNames = this._annotationIntrospector.findPropertyAliases(ann);
         if (propertyNames != null) {
            return propertyNames;
         }
      }

      return Collections.emptyList();
   }

   public JsonProperty.Access findAccess() {
      JsonProperty.Access acc = (JsonProperty.Access)this.fromMemberAnnotationsExcept(new WithMember<JsonProperty.Access>() {
         public JsonProperty.Access withMember(AnnotatedMember member) {
            return POJOPropertyBuilder.this._annotationIntrospector.findPropertyAccess(member);
         }
      }, JsonProperty.Access.AUTO);
      if (this._config.isEnabled(MapperFeature.INVERSE_READ_WRITE_ACCESS)) {
         if (acc == JsonProperty.Access.READ_ONLY) {
            acc = JsonProperty.Access.WRITE_ONLY;
         } else if (acc == JsonProperty.Access.WRITE_ONLY) {
            acc = JsonProperty.Access.READ_ONLY;
         }
      }

      return acc;
   }

   public void addField(AnnotatedField a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._fields = new Linked<AnnotatedField>(a, this._fields, name, explName, visible, ignored);
   }

   public void addCtor(AnnotatedParameter a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._ctorParameters = new Linked<AnnotatedParameter>(a, this._ctorParameters, name, explName, visible, ignored);
   }

   public void addGetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._getters = new Linked<AnnotatedMethod>(a, this._getters, name, explName, visible, ignored);
   }

   public void addSetter(AnnotatedMethod a, PropertyName name, boolean explName, boolean visible, boolean ignored) {
      this._setters = new Linked<AnnotatedMethod>(a, this._setters, name, explName, visible, ignored);
   }

   public void addAll(POJOPropertyBuilder src) {
      this._fields = merge(this._fields, src._fields);
      this._ctorParameters = merge(this._ctorParameters, src._ctorParameters);
      this._getters = merge(this._getters, src._getters);
      this._setters = merge(this._setters, src._setters);
   }

   private static <T> Linked<T> merge(Linked<T> chain1, Linked<T> chain2) {
      if (chain1 == null) {
         return chain2;
      } else {
         return chain2 == null ? chain1 : chain1.append(chain2);
      }
   }

   public void removeIgnored() {
      this._fields = this.<AnnotatedField>_removeIgnored(this._fields);
      this._getters = this.<AnnotatedMethod>_removeIgnored(this._getters);
      this._setters = this.<AnnotatedMethod>_removeIgnored(this._setters);
      this._ctorParameters = this.<AnnotatedParameter>_removeIgnored(this._ctorParameters);
   }

   public JsonProperty.Access removeNonVisible(boolean inferMutators, POJOPropertiesCollector parent) {
      JsonProperty.Access acc = this.findAccess();
      if (acc == null) {
         acc = JsonProperty.Access.AUTO;
      }

      switch (acc) {
         case READ_ONLY:
            if (parent != null) {
               parent._collectIgnorals(this.getName());

               for(PropertyName pn : this.findExplicitNames()) {
                  parent._collectIgnorals(pn.getSimpleName());
               }
            }

            this._setters = null;
            this._ctorParameters = null;
            if (!this._forSerialization) {
               this._fields = null;
            }
         case READ_WRITE:
            break;
         case WRITE_ONLY:
            this._getters = null;
            if (this._forSerialization) {
               this._fields = null;
            }
            break;
         case AUTO:
         default:
            this._getters = this.<AnnotatedMethod>_removeNonVisible(this._getters);
            this._ctorParameters = this.<AnnotatedParameter>_removeNonVisible(this._ctorParameters);
            if (!inferMutators || this._getters == null) {
               this._fields = this.<AnnotatedField>_removeNonVisible(this._fields);
               this._setters = this.<AnnotatedMethod>_removeNonVisible(this._setters);
            }
      }

      return acc;
   }

   public void removeConstructors() {
      this._ctorParameters = null;
   }

   public void removeFields() {
      this._fields = null;
   }

   public void trimByVisibility() {
      this._fields = this.<AnnotatedField>_trimByVisibility(this._fields);
      this._getters = this.<AnnotatedMethod>_trimByVisibility(this._getters);
      this._setters = this.<AnnotatedMethod>_trimByVisibility(this._setters);
      this._ctorParameters = this.<AnnotatedParameter>_trimByVisibility(this._ctorParameters);
   }

   public void mergeAnnotations(boolean forSerialization) {
      if (forSerialization) {
         if (this._getters != null) {
            AnnotationMap ann = this._mergeAnnotations(0, this._getters, this._fields, this._ctorParameters, this._setters);
            this._getters = this.<AnnotatedMethod>_applyAnnotations(this._getters, ann);
         } else if (this._fields != null) {
            AnnotationMap ann = this._mergeAnnotations(0, this._fields, this._ctorParameters, this._setters);
            this._fields = this.<AnnotatedField>_applyAnnotations(this._fields, ann);
         }
      } else if (this._ctorParameters != null) {
         AnnotationMap ann = this._mergeAnnotations(0, this._ctorParameters, this._setters, this._fields, this._getters);
         this._ctorParameters = this.<AnnotatedParameter>_applyAnnotations(this._ctorParameters, ann);
      } else if (this._setters != null) {
         AnnotationMap ann = this._mergeAnnotations(0, this._setters, this._fields, this._getters);
         this._setters = this.<AnnotatedMethod>_applyAnnotations(this._setters, ann);
      } else if (this._fields != null) {
         AnnotationMap ann = this._mergeAnnotations(0, this._fields, this._getters);
         this._fields = this.<AnnotatedField>_applyAnnotations(this._fields, ann);
      }

   }

   private AnnotationMap _mergeAnnotations(int index, Linked<? extends AnnotatedMember>... nodes) {
      AnnotationMap ann = this._getAllAnnotations(nodes[index]);

      do {
         ++index;
         if (index >= nodes.length) {
            return ann;
         }
      } while(nodes[index] == null);

      return AnnotationMap.merge(ann, this._mergeAnnotations(index, nodes));
   }

   private <T extends AnnotatedMember> AnnotationMap _getAllAnnotations(Linked<T> node) {
      AnnotationMap ann = ((AnnotatedMember)node.value).getAllAnnotations();
      if (node.next != null) {
         ann = AnnotationMap.merge(ann, this._getAllAnnotations(node.next));
      }

      return ann;
   }

   private <T extends AnnotatedMember> Linked<T> _applyAnnotations(Linked<T> node, AnnotationMap ann) {
      T value = (T)(((AnnotatedMember)node.value).withAnnotations(ann));
      if (node.next != null) {
         node = node.withNext(this._applyAnnotations(node.next, ann));
      }

      return node.withValue(value);
   }

   private <T> Linked<T> _removeIgnored(Linked<T> node) {
      return node == null ? node : node.withoutIgnored();
   }

   private <T> Linked<T> _removeNonVisible(Linked<T> node) {
      return node == null ? node : node.withoutNonVisible();
   }

   private <T> Linked<T> _trimByVisibility(Linked<T> node) {
      return node == null ? node : node.trimByVisibility();
   }

   private <T> boolean _anyExplicits(Linked<T> n) {
      while(n != null) {
         if (n.name != null && n.name.hasSimpleName()) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   private <T> boolean _anyExplicitNames(Linked<T> n) {
      while(n != null) {
         if (n.name != null && n.isNameExplicit) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public boolean anyVisible() {
      return this._anyVisible(this._fields) || this._anyVisible(this._getters) || this._anyVisible(this._setters) || this._anyVisible(this._ctorParameters);
   }

   private <T> boolean _anyVisible(Linked<T> n) {
      while(n != null) {
         if (n.isVisible) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public boolean anyIgnorals() {
      return this._anyIgnorals(this._fields) || this._anyIgnorals(this._getters) || this._anyIgnorals(this._setters) || this._anyIgnorals(this._ctorParameters);
   }

   private <T> boolean _anyIgnorals(Linked<T> n) {
      while(n != null) {
         if (n.isMarkedIgnored) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public boolean anyExplicitsWithoutIgnoral() {
      return this._anyExplicitsWithoutIgnoral(this._fields) || this._anyExplicitsWithoutIgnoral(this._getters) || this._anyExplicitsWithoutIgnoral(this._setters) || this._anyExplicitNamesWithoutIgnoral(this._ctorParameters);
   }

   private <T> boolean _anyExplicitsWithoutIgnoral(Linked<T> n) {
      while(n != null) {
         if (!n.isMarkedIgnored && n.name != null && n.name.hasSimpleName()) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   private <T> boolean _anyExplicitNamesWithoutIgnoral(Linked<T> n) {
      while(n != null) {
         if (!n.isMarkedIgnored && n.name != null && n.isNameExplicit) {
            return true;
         }

         n = n.next;
      }

      return false;
   }

   public Set<PropertyName> findExplicitNames() {
      Set<PropertyName> renamed = null;
      renamed = this._findExplicitNames(this._fields, renamed);
      renamed = this._findExplicitNames(this._getters, renamed);
      renamed = this._findExplicitNames(this._setters, renamed);
      renamed = this._findExplicitNames(this._ctorParameters, renamed);
      return renamed == null ? Collections.emptySet() : renamed;
   }

   public boolean hasExplicitName(PropertyName name) {
      return this._hasExplicitName(this._fields, name) || this._hasExplicitName(this._getters, name) || this._hasExplicitName(this._setters, name) || this._hasExplicitName(this._ctorParameters, name);
   }

   private boolean _hasExplicitName(Linked<? extends AnnotatedMember> node, PropertyName nameToMatch) {
      while(node != null) {
         if (node.isNameExplicit && nameToMatch.equals(node.name)) {
            return true;
         }

         node = node.next;
      }

      return false;
   }

   public Collection<POJOPropertyBuilder> explode(Collection<PropertyName> newNames) {
      HashMap<PropertyName, POJOPropertyBuilder> props = new HashMap();
      this._explode(newNames, props, this._fields);
      this._explode(newNames, props, this._getters);
      this._explode(newNames, props, this._setters);
      this._explode(newNames, props, this._ctorParameters);
      return props.values();
   }

   private void _explode(Collection<PropertyName> newNames, Map<PropertyName, POJOPropertyBuilder> props, Linked<?> accessors) {
      Linked<?> firstAcc = accessors;

      for(Linked<?> node = accessors; node != null; node = node.next) {
         PropertyName name = node.name;
         if (node.isNameExplicit && name != null) {
            POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(name);
            if (prop == null) {
               prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, this._internalName, name);
               props.put(name, prop);
            }

            if (firstAcc == this._fields) {
               prop._fields = node.withNext(prop._fields);
            } else if (firstAcc == this._getters) {
               prop._getters = node.withNext(prop._getters);
            } else if (firstAcc == this._setters) {
               prop._setters = node.withNext(prop._setters);
            } else {
               if (firstAcc != this._ctorParameters) {
                  throw new IllegalStateException("Internal error: mismatched accessors, property: " + this);
               }

               prop._ctorParameters = node.withNext(prop._ctorParameters);
            }
         } else if (node.isVisible) {
            throw new IllegalStateException("Conflicting/ambiguous property name definitions (implicit name " + ClassUtil.name(this._name) + "): found multiple explicit names: " + newNames + ", but also implicit accessor: " + node);
         }
      }

   }

   private Set<PropertyName> _findExplicitNames(Linked<? extends AnnotatedMember> node, Set<PropertyName> renamed) {
      for(; node != null; node = node.next) {
         if (node.isNameExplicit && node.name != null) {
            if (renamed == null) {
               renamed = new HashSet();
            }

            renamed.add(node.name);
         }
      }

      return renamed;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[Property '").append(this._name).append("'; ctors: ").append(this._ctorParameters).append(", field(s): ").append(this._fields).append(", getter(s): ").append(this._getters).append(", setter(s): ").append(this._setters);
      sb.append("]");
      return sb.toString();
   }

   protected <T> T fromMemberAnnotations(WithMember<T> func) {
      T result = null;
      if (this._annotationIntrospector != null) {
         if (this._forSerialization) {
            if (this._getters != null) {
               result = func.withMember((AnnotatedMember)this._getters.value);
            }
         } else {
            if (this._ctorParameters != null) {
               result = func.withMember((AnnotatedMember)this._ctorParameters.value);
            }

            if (result == null && this._setters != null) {
               result = func.withMember((AnnotatedMember)this._setters.value);
            }
         }

         if (result == null && this._fields != null) {
            result = func.withMember((AnnotatedMember)this._fields.value);
         }
      }

      return result;
   }

   protected <T> T fromMemberAnnotationsExcept(WithMember<T> func, T defaultValue) {
      if (this._annotationIntrospector == null) {
         return null;
      } else if (this._forSerialization) {
         if (this._getters != null) {
            T result = func.withMember((AnnotatedMember)this._getters.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         if (this._fields != null) {
            T result = func.withMember((AnnotatedMember)this._fields.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         if (this._ctorParameters != null) {
            T result = func.withMember((AnnotatedMember)this._ctorParameters.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         if (this._setters != null) {
            T result = func.withMember((AnnotatedMember)this._setters.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         return null;
      } else {
         if (this._ctorParameters != null) {
            T result = func.withMember((AnnotatedMember)this._ctorParameters.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         if (this._setters != null) {
            T result = func.withMember((AnnotatedMember)this._setters.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         if (this._fields != null) {
            T result = func.withMember((AnnotatedMember)this._fields.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         if (this._getters != null) {
            T result = func.withMember((AnnotatedMember)this._getters.value);
            if (result != null && result != defaultValue) {
               return result;
            }
         }

         return null;
      }
   }

   protected Class<?> _rawTypeOf(AnnotatedMember m) {
      if (m instanceof AnnotatedMethod) {
         AnnotatedMethod meh = (AnnotatedMethod)m;
         if (meh.getParameterCount() > 0) {
            return meh.getParameterType(0).getRawClass();
         }
      }

      return m.getType().getRawClass();
   }

   protected static class MemberIterator<T extends AnnotatedMember> implements Iterator<T> {
      private Linked<T> next;

      public MemberIterator(Linked<T> first) {
         this.next = first;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public T next() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            T result = this.next.value;
            this.next = this.next.next;
            return result;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   protected static final class Linked<T> {
      public final T value;
      public final Linked<T> next;
      public final PropertyName name;
      public final boolean isNameExplicit;
      public final boolean isVisible;
      public final boolean isMarkedIgnored;

      public Linked(T v, Linked<T> n, PropertyName name, boolean explName, boolean visible, boolean ignored) {
         this.value = v;
         this.next = n;
         this.name = name != null && !name.isEmpty() ? name : null;
         if (explName) {
            if (this.name == null) {
               throw new IllegalArgumentException("Cannot pass true for 'explName' if name is null/empty");
            }

            if (!name.hasSimpleName()) {
               explName = false;
            }
         }

         this.isNameExplicit = explName;
         this.isVisible = visible;
         this.isMarkedIgnored = ignored;
      }

      public Linked<T> withoutNext() {
         return this.next == null ? this : new Linked(this.value, (Linked)null, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
      }

      public Linked<T> withValue(T newValue) {
         return newValue == this.value ? this : new Linked(newValue, this.next, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
      }

      public Linked<T> withNext(Linked<T> newNext) {
         return newNext == this.next ? this : new Linked(this.value, newNext, this.name, this.isNameExplicit, this.isVisible, this.isMarkedIgnored);
      }

      public Linked<T> withoutIgnored() {
         if (this.isMarkedIgnored) {
            return this.next == null ? null : this.next.withoutIgnored();
         } else {
            if (this.next != null) {
               Linked<T> newNext = this.next.withoutIgnored();
               if (newNext != this.next) {
                  return this.withNext(newNext);
               }
            }

            return this;
         }
      }

      public Linked<T> withoutNonVisible() {
         Linked<T> newNext = this.next == null ? null : this.next.withoutNonVisible();
         return this.isVisible ? this.withNext(newNext) : newNext;
      }

      protected Linked<T> append(Linked<T> appendable) {
         return this.next == null ? this.withNext(appendable) : this.withNext(this.next.append(appendable));
      }

      public Linked<T> trimByVisibility() {
         if (this.next == null) {
            return this;
         } else {
            Linked<T> newNext = this.next.trimByVisibility();
            if (this.name != null) {
               return newNext.name == null ? this.withNext((Linked)null) : this.withNext(newNext);
            } else if (newNext.name != null) {
               return newNext;
            } else if (this.isVisible == newNext.isVisible) {
               return this.withNext(newNext);
            } else {
               return this.isVisible ? this.withNext((Linked)null) : newNext;
            }
         }
      }

      public String toString() {
         String msg = String.format("%s[visible=%b,ignore=%b,explicitName=%b]", this.value.toString(), this.isVisible, this.isMarkedIgnored, this.isNameExplicit);
         if (this.next != null) {
            msg = msg + ", " + this.next.toString();
         }

         return msg;
      }
   }

   private interface WithMember<T> {
      T withMember(AnnotatedMember var1);
   }
}
