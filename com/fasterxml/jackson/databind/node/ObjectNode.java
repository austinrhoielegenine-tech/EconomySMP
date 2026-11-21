package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ObjectNode extends ContainerNode<ObjectNode> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final Map<String, JsonNode> _children;

   public ObjectNode(JsonNodeFactory nc) {
      super(nc);
      this._children = new LinkedHashMap();
   }

   public ObjectNode(JsonNodeFactory nc, Map<String, JsonNode> children) {
      super(nc);
      this._children = (Map)Objects.requireNonNull(children, "Must not pass `null` for 'children' argument");
   }

   protected JsonNode _at(JsonPointer ptr) {
      return this.get(ptr.getMatchingProperty());
   }

   public ObjectNode deepCopy() {
      ObjectNode ret = this.objectNode();

      for(Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
         ret._children.put(entry.getKey(), ((JsonNode)entry.getValue()).deepCopy());
      }

      return ret;
   }

   /** @deprecated */
   @Deprecated
   public ObjectNode with(String exprOrProperty) {
      JsonPointer ptr = this._jsonPointerIfValid(exprOrProperty);
      if (ptr != null) {
         return this.withObject(ptr);
      } else {
         JsonNode n = (JsonNode)this._children.get(exprOrProperty);
         if (n != null) {
            if (n instanceof ObjectNode) {
               return (ObjectNode)n;
            } else {
               throw new UnsupportedOperationException("Property '" + exprOrProperty + "' has value that is not of type `ObjectNode` (but `" + n.getClass().getName() + "`)");
            }
         } else {
            ObjectNode result = this.objectNode();
            this._put(exprOrProperty, result);
            return result;
         }
      }
   }

   public ObjectNode withObject(String exprOrProperty) {
      JsonPointer ptr = this._jsonPointerIfValid(exprOrProperty);
      return ptr != null ? this.withObject(ptr) : this.withObjectProperty(exprOrProperty);
   }

   public ObjectNode withObjectProperty(String propName) {
      JsonNode child = (JsonNode)this._children.get(propName);
      if (child != null && !child.isNull()) {
         return child.isObject() ? (ObjectNode)child : (ObjectNode)this._reportWrongNodeType("Cannot replace `JsonNode` of type `%s` with `ObjectNode` for property \"%s\" (default mode `OverwriteMode.%s`)", new Object[]{child.getClass().getName(), propName, JsonNode.OverwriteMode.NULLS});
      } else {
         return this.putObject(propName);
      }
   }

   public ArrayNode withArray(String exprOrProperty) {
      JsonPointer ptr = this._jsonPointerIfValid(exprOrProperty);
      if (ptr != null) {
         return this.withArray((JsonPointer)ptr);
      } else {
         JsonNode n = (JsonNode)this._children.get(exprOrProperty);
         if (n != null) {
            if (n instanceof ArrayNode) {
               return (ArrayNode)n;
            } else {
               throw new UnsupportedOperationException("Property '" + exprOrProperty + "' has value that is not of type `ArrayNode` (but `" + n.getClass().getName() + "`)");
            }
         } else {
            ArrayNode result = this.arrayNode();
            this._put(exprOrProperty, result);
            return result;
         }
      }
   }

   public ArrayNode withArrayProperty(String propName) {
      JsonNode child = (JsonNode)this._children.get(propName);
      if (child != null && !child.isNull()) {
         return child.isArray() ? (ArrayNode)child : (ArrayNode)this._reportWrongNodeType("Cannot replace `JsonNode` of type `%s` with `ArrayNode` for property \"%s\" with (default mode `OverwriteMode.%s`)", new Object[]{child.getClass().getName(), propName, JsonNode.OverwriteMode.NULLS});
      } else {
         return this.putArray(propName);
      }
   }

   protected ObjectNode _withObject(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
      if (currentPtr.matches()) {
         return this;
      } else {
         JsonNode n = this._at(currentPtr);
         if (n != null && n instanceof BaseJsonNode) {
            ObjectNode found = ((BaseJsonNode)n)._withObject(origPtr, currentPtr.tail(), overwriteMode, preferIndex);
            if (found != null) {
               return found;
            }

            this._withXxxVerifyReplace(origPtr, currentPtr, overwriteMode, preferIndex, n);
         }

         return this._withObjectAddTailProperty(currentPtr, preferIndex);
      }
   }

   protected ArrayNode _withArray(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
      if (currentPtr.matches()) {
         return null;
      } else {
         JsonNode n = this._at(currentPtr);
         if (n != null && n instanceof BaseJsonNode) {
            ArrayNode found = ((BaseJsonNode)n)._withArray(origPtr, currentPtr.tail(), overwriteMode, preferIndex);
            if (found != null) {
               return found;
            }

            this._withXxxVerifyReplace(origPtr, currentPtr, overwriteMode, preferIndex, n);
         }

         return this._withArrayAddTailProperty(currentPtr, preferIndex);
      }
   }

   protected ObjectNode _withObjectAddTailProperty(JsonPointer tail, boolean preferIndex) {
      String propName = tail.getMatchingProperty();
      tail = tail.tail();
      if (tail.matches()) {
         return this.putObject(propName);
      } else {
         return preferIndex && tail.mayMatchElement() ? this.putArray(propName)._withObjectAddTailElement(tail, preferIndex) : this.putObject(propName)._withObjectAddTailProperty(tail, preferIndex);
      }
   }

   protected ArrayNode _withArrayAddTailProperty(JsonPointer tail, boolean preferIndex) {
      String propName = tail.getMatchingProperty();
      tail = tail.tail();
      if (tail.matches()) {
         return this.putArray(propName);
      } else {
         return preferIndex && tail.mayMatchElement() ? this.putArray(propName)._withArrayAddTailElement(tail, preferIndex) : this.putObject(propName)._withArrayAddTailProperty(tail, preferIndex);
      }
   }

   public boolean isEmpty(SerializerProvider serializers) {
      return this._children.isEmpty();
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.OBJECT;
   }

   public final boolean isObject() {
      return true;
   }

   public JsonToken asToken() {
      return JsonToken.START_OBJECT;
   }

   public int size() {
      return this._children.size();
   }

   public boolean isEmpty() {
      return this._children.isEmpty();
   }

   public Iterator<JsonNode> elements() {
      return this._children.values().iterator();
   }

   public Iterator<JsonNode> values() {
      return this._children.values().iterator();
   }

   public JsonNode get(int index) {
      return null;
   }

   public JsonNode get(String propertyName) {
      return (JsonNode)this._children.get(propertyName);
   }

   public Optional<JsonNode> optional(String propertyName) {
      return Optional.ofNullable(this.get(propertyName));
   }

   public JsonNode path(int index) {
      return MissingNode.getInstance();
   }

   public JsonNode path(String propertyName) {
      JsonNode n = (JsonNode)this._children.get(propertyName);
      return (JsonNode)(n != null ? n : MissingNode.getInstance());
   }

   public JsonNode required(String propertyName) {
      JsonNode n = (JsonNode)this._children.get(propertyName);
      return n != null ? n : (JsonNode)this._reportRequiredViolation("No value for property '%s' of `ObjectNode`", new Object[]{propertyName});
   }

   public Iterator<String> fieldNames() {
      return this._children.keySet().iterator();
   }

   /** @deprecated */
   @Deprecated
   public Iterator<Map.Entry<String, JsonNode>> fields() {
      return this._children.entrySet().iterator();
   }

   public Set<Map.Entry<String, JsonNode>> properties() {
      return this._children.entrySet();
   }

   public Stream<JsonNode> valueStream() {
      return this._children.values().stream();
   }

   public Stream<Map.Entry<String, JsonNode>> propertyStream() {
      return this._children.entrySet().stream();
   }

   public void forEachEntry(BiConsumer<? super String, ? super JsonNode> action) {
      this._children.forEach(action);
   }

   public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
      if (!(o instanceof ObjectNode)) {
         return false;
      } else {
         ObjectNode other = (ObjectNode)o;
         Map<String, JsonNode> m1 = this._children;
         Map<String, JsonNode> m2 = other._children;
         int len = m1.size();
         if (m2.size() != len) {
            return false;
         } else {
            for(Map.Entry<String, JsonNode> entry : m1.entrySet()) {
               JsonNode v2 = (JsonNode)m2.get(entry.getKey());
               if (v2 == null || !((JsonNode)entry.getValue()).equals(comparator, v2)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public JsonNode findValue(String propertyName) {
      JsonNode jsonNode = (JsonNode)this._children.get(propertyName);
      if (jsonNode != null) {
         return jsonNode;
      } else {
         for(JsonNode child : this._children.values()) {
            JsonNode value = child.findValue(propertyName);
            if (value != null) {
               return value;
            }
         }

         return null;
      }
   }

   public List<JsonNode> findValues(String propertyName, List<JsonNode> foundSoFar) {
      for(Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
         if (propertyName.equals(entry.getKey())) {
            if (foundSoFar == null) {
               foundSoFar = new ArrayList();
            }

            foundSoFar.add(entry.getValue());
         } else {
            foundSoFar = ((JsonNode)entry.getValue()).findValues(propertyName, foundSoFar);
         }
      }

      return foundSoFar;
   }

   public List<String> findValuesAsText(String propertyName, List<String> foundSoFar) {
      for(Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
         if (propertyName.equals(entry.getKey())) {
            if (foundSoFar == null) {
               foundSoFar = new ArrayList();
            }

            foundSoFar.add(((JsonNode)entry.getValue()).asText());
         } else {
            foundSoFar = ((JsonNode)entry.getValue()).findValuesAsText(propertyName, foundSoFar);
         }
      }

      return foundSoFar;
   }

   public ObjectNode findParent(String propertyName) {
      JsonNode jsonNode = (JsonNode)this._children.get(propertyName);
      if (jsonNode != null) {
         return this;
      } else {
         for(JsonNode child : this._children.values()) {
            JsonNode value = child.findParent(propertyName);
            if (value != null) {
               return (ObjectNode)value;
            }
         }

         return null;
      }
   }

   public List<JsonNode> findParents(String propertyName, List<JsonNode> foundSoFar) {
      for(Map.Entry<String, JsonNode> entry : this._children.entrySet()) {
         if (propertyName.equals(entry.getKey())) {
            if (foundSoFar == null) {
               foundSoFar = new ArrayList();
            }

            foundSoFar.add(this);
         } else {
            foundSoFar = ((JsonNode)entry.getValue()).findParents(propertyName, foundSoFar);
         }
      }

      return foundSoFar;
   }

   public void serialize(JsonGenerator g, SerializerProvider ctxt) throws IOException {
      if (ctxt != null) {
         boolean trimEmptyArray = !ctxt.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
         boolean skipNulls = !ctxt.isEnabled((DatatypeFeature)JsonNodeFeature.WRITE_NULL_PROPERTIES);
         if (trimEmptyArray || skipNulls) {
            g.writeStartObject(this);
            this.serializeFilteredContents(g, ctxt, trimEmptyArray, skipNulls);
            g.writeEndObject();
            return;
         }
      }

      Map<String, JsonNode> contents = this._contentsToSerialize(ctxt);
      g.writeStartObject(this, contents.size());

      for(Map.Entry<String, JsonNode> en : contents.entrySet()) {
         g.writeFieldName((String)en.getKey());
         ((JsonNode)en.getValue()).serialize(g, ctxt);
      }

      g.writeEndObject();
   }

   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      boolean trimEmptyArray = false;
      boolean skipNulls = false;
      if (provider != null) {
         trimEmptyArray = !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
         skipNulls = !provider.isEnabled((DatatypeFeature)JsonNodeFeature.WRITE_NULL_PROPERTIES);
      }

      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_OBJECT));
      if (!trimEmptyArray && !skipNulls) {
         for(Map.Entry<String, JsonNode> en : this._contentsToSerialize(provider).entrySet()) {
            JsonNode value = (JsonNode)en.getValue();
            g.writeFieldName((String)en.getKey());
            value.serialize(g, provider);
         }
      } else {
         this.serializeFilteredContents(g, provider, trimEmptyArray, skipNulls);
      }

      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   protected void serializeFilteredContents(JsonGenerator g, SerializerProvider ctxt, boolean trimEmptyArray, boolean skipNulls) throws IOException {
      for(Map.Entry<String, JsonNode> en : this._contentsToSerialize(ctxt).entrySet()) {
         JsonNode value = (JsonNode)en.getValue();
         if ((!trimEmptyArray || !value.isArray() || !value.isEmpty(ctxt)) && (!skipNulls || !value.isNull())) {
            g.writeFieldName((String)en.getKey());
            value.serialize(g, ctxt);
         }
      }

   }

   protected Map<String, JsonNode> _contentsToSerialize(SerializerProvider ctxt) {
      return (Map<String, JsonNode>)(ctxt.isEnabled((DatatypeFeature)JsonNodeFeature.WRITE_PROPERTIES_SORTED) && !this._children.isEmpty() && !(this._children instanceof TreeMap) ? new TreeMap(this._children) : this._children);
   }

   public <T extends JsonNode> T set(String propertyName, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      return (T)this._put(propertyName, value);
   }

   public <T extends JsonNode> T setAll(Map<String, ? extends JsonNode> properties) {
      for(Map.Entry<String, ? extends JsonNode> en : properties.entrySet()) {
         JsonNode n = (JsonNode)en.getValue();
         if (n == null) {
            n = this.nullNode();
         }

         this._put((String)en.getKey(), n);
      }

      return (T)this;
   }

   public <T extends JsonNode> T setAll(ObjectNode other) {
      this._children.putAll(other._children);
      return (T)this;
   }

   public JsonNode replace(String propertyName, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      return (JsonNode)this._children.put(Objects.requireNonNull(propertyName), value);
   }

   public <T extends JsonNode> T without(String propertyName) {
      this._children.remove(propertyName);
      return (T)this;
   }

   public <T extends JsonNode> T without(Collection<String> propertyNames) {
      this._children.keySet().removeAll(propertyNames);
      return (T)this;
   }

   /** @deprecated */
   @Deprecated
   public JsonNode put(String propertyName, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      return (JsonNode)this._children.put(Objects.requireNonNull(propertyName), value);
   }

   public JsonNode putIfAbsent(String propertyName, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      return (JsonNode)this._children.putIfAbsent(Objects.requireNonNull(propertyName), value);
   }

   public JsonNode remove(String propertyName) {
      return (JsonNode)this._children.remove(propertyName);
   }

   public ObjectNode remove(Collection<String> propertyNames) {
      this._children.keySet().removeAll(propertyNames);
      return this;
   }

   public ObjectNode removeAll() {
      this._children.clear();
      return this;
   }

   public ObjectNode removeIf(Predicate<? super JsonNode> predicate) {
      this._children.values().removeIf(predicate);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public JsonNode putAll(Map<String, ? extends JsonNode> properties) {
      return this.setAll(properties);
   }

   /** @deprecated */
   @Deprecated
   public JsonNode putAll(ObjectNode other) {
      return this.setAll(other);
   }

   public ObjectNode retain(Collection<String> propertyNames) {
      this._children.keySet().retainAll(propertyNames);
      return this;
   }

   public ObjectNode retain(String... propertyNames) {
      return this.retain(Arrays.asList(propertyNames));
   }

   public ArrayNode putArray(String propertyName) {
      ArrayNode n = this.arrayNode();
      this._put(propertyName, n);
      return n;
   }

   public ObjectNode putObject(String propertyName) {
      ObjectNode n = this.objectNode();
      this._put(propertyName, n);
      return n;
   }

   public ObjectNode putPOJO(String propertyName, Object pojo) {
      return this._put(propertyName, this.pojoNode(pojo));
   }

   public ObjectNode putRawValue(String propertyName, RawValue raw) {
      return this._put(propertyName, this.rawValueNode(raw));
   }

   public ObjectNode putNull(String propertyName) {
      return this._put(propertyName, this.nullNode());
   }

   public ObjectNode put(String propertyName, short v) {
      return this._put(propertyName, this.numberNode(v));
   }

   public ObjectNode put(String propertyName, Short v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, int v) {
      return this._put(propertyName, this.numberNode(v));
   }

   public ObjectNode put(String propertyName, Integer v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, long v) {
      return this._put(propertyName, this.numberNode(v));
   }

   public ObjectNode put(String propertyName, Long v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, float v) {
      return this._put(propertyName, this.numberNode(v));
   }

   public ObjectNode put(String propertyName, Float v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, double v) {
      return this._put(propertyName, this.numberNode(v));
   }

   public ObjectNode put(String propertyName, Double v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, BigDecimal v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, BigInteger v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ObjectNode put(String propertyName, String v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.textNode(v)));
   }

   public ObjectNode put(String propertyName, boolean v) {
      return this._put(propertyName, this.booleanNode(v));
   }

   public ObjectNode put(String propertyName, Boolean v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.booleanNode(v)));
   }

   public ObjectNode put(String propertyName, byte[] v) {
      return this._put(propertyName, (JsonNode)(v == null ? this.nullNode() : this.binaryNode(v)));
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o instanceof ObjectNode ? this._childrenEqual((ObjectNode)o) : false;
      }
   }

   protected boolean _childrenEqual(ObjectNode other) {
      return this._children.equals(other._children);
   }

   public int hashCode() {
      return this._children.hashCode();
   }

   protected ObjectNode _put(String propertyName, JsonNode value) {
      this._children.put(Objects.requireNonNull(propertyName), value);
      return this;
   }
}
