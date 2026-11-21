package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ArrayNode extends ContainerNode<ArrayNode> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final List<JsonNode> _children;

   public ArrayNode(JsonNodeFactory nf) {
      super(nf);
      this._children = new ArrayList();
   }

   public ArrayNode(JsonNodeFactory nf, int capacity) {
      super(nf);
      this._children = new ArrayList(capacity);
   }

   public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
      super(nf);
      this._children = (List)Objects.requireNonNull(children, "Must not pass `null` for 'children' argument");
   }

   protected JsonNode _at(JsonPointer ptr) {
      return this.get(ptr.getMatchingIndex());
   }

   public ArrayNode deepCopy() {
      ArrayNode ret = this.arrayNode(this._children.size());

      for(JsonNode element : this._children) {
         ret._children.add(element.deepCopy());
      }

      return ret;
   }

   /** @deprecated */
   @Deprecated
   public ObjectNode with(String exprOrProperty) {
      JsonPointer ptr = this._jsonPointerIfValid(exprOrProperty);
      return ptr != null ? this.withObject(ptr) : (ObjectNode)super.with(exprOrProperty);
   }

   public ArrayNode withArray(String exprOrProperty) {
      JsonPointer ptr = this._jsonPointerIfValid(exprOrProperty);
      return ptr != null ? this.withArray((JsonPointer)ptr) : (ArrayNode)super.withArray(exprOrProperty);
   }

   protected ObjectNode _withObject(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
      if (currentPtr.matches()) {
         return null;
      } else {
         JsonNode n = this._at(currentPtr);
         if (n != null && n instanceof BaseJsonNode) {
            ObjectNode found = ((BaseJsonNode)n)._withObject(origPtr, currentPtr.tail(), overwriteMode, preferIndex);
            if (found != null) {
               return found;
            }

            this._withXxxVerifyReplace(origPtr, currentPtr, overwriteMode, preferIndex, n);
         }

         return this._withObjectAddTailElement(currentPtr, preferIndex);
      }
   }

   protected ArrayNode _withArray(JsonPointer origPtr, JsonPointer currentPtr, JsonNode.OverwriteMode overwriteMode, boolean preferIndex) {
      if (currentPtr.matches()) {
         return this;
      } else {
         JsonNode n = this._at(currentPtr);
         if (n != null && n instanceof BaseJsonNode) {
            ArrayNode found = ((BaseJsonNode)n)._withArray(origPtr, currentPtr.tail(), overwriteMode, preferIndex);
            if (found != null) {
               return found;
            }

            this._withXxxVerifyReplace(origPtr, currentPtr, overwriteMode, preferIndex, n);
         }

         return this._withArrayAddTailElement(currentPtr, preferIndex);
      }
   }

   protected ObjectNode _withObjectAddTailElement(JsonPointer tail, boolean preferIndex) {
      int index = tail.getMatchingIndex();
      if (index < 0) {
         return null;
      } else {
         tail = tail.tail();
         if (tail.matches()) {
            ObjectNode result = this.objectNode();
            this._withXxxSetArrayElement(index, result);
            return result;
         } else if (preferIndex && tail.mayMatchElement()) {
            ArrayNode next = this.arrayNode();
            this._withXxxSetArrayElement(index, next);
            return next._withObjectAddTailElement(tail, preferIndex);
         } else {
            ObjectNode next = this.objectNode();
            this._withXxxSetArrayElement(index, next);
            return next._withObjectAddTailProperty(tail, preferIndex);
         }
      }
   }

   protected ArrayNode _withArrayAddTailElement(JsonPointer tail, boolean preferIndex) {
      int index = tail.getMatchingIndex();
      if (index < 0) {
         return null;
      } else {
         tail = tail.tail();
         if (tail.matches()) {
            ArrayNode result = this.arrayNode();
            this._withXxxSetArrayElement(index, result);
            return result;
         } else if (preferIndex && tail.mayMatchElement()) {
            ArrayNode next = this.arrayNode();
            this._withXxxSetArrayElement(index, next);
            return next._withArrayAddTailElement(tail, preferIndex);
         } else {
            ObjectNode next = this.objectNode();
            this._withXxxSetArrayElement(index, next);
            return next._withArrayAddTailProperty(tail, preferIndex);
         }
      }
   }

   protected void _withXxxSetArrayElement(int index, JsonNode value) {
      if (index >= this.size()) {
         int max = this._nodeFactory.getMaxElementIndexForInsert();
         if (index > max) {
            this._reportWrongNodeOperation("Too big Array index (%d; max %d) to use for insert with `JsonPointer`", new Object[]{index, max});
         }

         while(index >= this.size()) {
            this.addNull();
         }
      }

      this.set(index, value);
   }

   public boolean isEmpty(SerializerProvider serializers) {
      return this._children.isEmpty();
   }

   public JsonNodeType getNodeType() {
      return JsonNodeType.ARRAY;
   }

   public boolean isArray() {
      return true;
   }

   public JsonToken asToken() {
      return JsonToken.START_ARRAY;
   }

   public int size() {
      return this._children.size();
   }

   public boolean isEmpty() {
      return this._children.isEmpty();
   }

   public Iterator<JsonNode> elements() {
      return this._children.listIterator();
   }

   public Iterator<JsonNode> values() {
      return this._children.listIterator();
   }

   public JsonNode get(int index) {
      return index >= 0 && index < this._children.size() ? (JsonNode)this._children.get(index) : null;
   }

   public JsonNode get(String fieldName) {
      return null;
   }

   public Optional<JsonNode> optional(int index) {
      return Optional.ofNullable(this.get(index));
   }

   public JsonNode path(String fieldName) {
      return MissingNode.getInstance();
   }

   public JsonNode path(int index) {
      return (JsonNode)(index >= 0 && index < this._children.size() ? (JsonNode)this._children.get(index) : MissingNode.getInstance());
   }

   public JsonNode required(int index) {
      return index >= 0 && index < this._children.size() ? (JsonNode)this._children.get(index) : (JsonNode)this._reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`", new Object[]{index, this._children.size()});
   }

   public Stream<JsonNode> valueStream() {
      return this._children.stream();
   }

   public boolean equals(Comparator<JsonNode> comparator, JsonNode o) {
      if (!(o instanceof ArrayNode)) {
         return false;
      } else {
         ArrayNode other = (ArrayNode)o;
         int len = this._children.size();
         if (other.size() != len) {
            return false;
         } else {
            List<JsonNode> l1 = this._children;
            List<JsonNode> l2 = other._children;

            for(int i = 0; i < len; ++i) {
               if (!((JsonNode)l1.get(i)).equals(comparator, (JsonNode)l2.get(i))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public void serialize(JsonGenerator g, SerializerProvider provider) throws IOException {
      List<JsonNode> c = this._children;
      int size = c.size();
      g.writeStartArray(this, size);

      for(int i = 0; i < size; ++i) {
         JsonNode value = (JsonNode)c.get(i);
         value.serialize(g, provider);
      }

      g.writeEndArray();
   }

   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(this, JsonToken.START_ARRAY));

      for(JsonNode n : this._children) {
         ((BaseJsonNode)n).serialize(g, provider);
      }

      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   public JsonNode findValue(String fieldName) {
      for(JsonNode node : this._children) {
         JsonNode value = node.findValue(fieldName);
         if (value != null) {
            return value;
         }
      }

      return null;
   }

   public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar) {
      for(JsonNode node : this._children) {
         foundSoFar = node.findValues(fieldName, foundSoFar);
      }

      return foundSoFar;
   }

   public List<String> findValuesAsText(String fieldName, List<String> foundSoFar) {
      for(JsonNode node : this._children) {
         foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
      }

      return foundSoFar;
   }

   public ObjectNode findParent(String fieldName) {
      for(JsonNode node : this._children) {
         JsonNode parent = node.findParent(fieldName);
         if (parent != null) {
            return (ObjectNode)parent;
         }
      }

      return null;
   }

   public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar) {
      for(JsonNode node : this._children) {
         foundSoFar = node.findParents(fieldName, foundSoFar);
      }

      return foundSoFar;
   }

   public JsonNode set(int index, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      if (index >= 0 && index < this._children.size()) {
         return (JsonNode)this._children.set(index, value);
      } else {
         throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
      }
   }

   public ArrayNode add(JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      this._add(value);
      return this;
   }

   public ArrayNode addAll(ArrayNode other) {
      this._children.addAll(other._children);
      return this;
   }

   public ArrayNode addAll(Collection<? extends JsonNode> nodes) {
      for(JsonNode node : nodes) {
         this.add(node);
      }

      return this;
   }

   public ArrayNode insert(int index, JsonNode value) {
      if (value == null) {
         value = this.nullNode();
      }

      this._insert(index, value);
      return this;
   }

   public JsonNode remove(int index) {
      return index >= 0 && index < this._children.size() ? (JsonNode)this._children.remove(index) : null;
   }

   public ArrayNode removeAll() {
      this._children.clear();
      return this;
   }

   public ArrayNode removeIf(Predicate<? super JsonNode> predicate) {
      this._children.removeIf(predicate);
      return this;
   }

   public ArrayNode addArray() {
      ArrayNode n = this.arrayNode();
      this._add(n);
      return n;
   }

   public ObjectNode addObject() {
      ObjectNode n = this.objectNode();
      this._add(n);
      return n;
   }

   public ArrayNode addPOJO(Object pojo) {
      return this._add((JsonNode)(pojo == null ? this.nullNode() : this.pojoNode(pojo)));
   }

   public ArrayNode addRawValue(RawValue raw) {
      return this._add((JsonNode)(raw == null ? this.nullNode() : this.rawValueNode(raw)));
   }

   public ArrayNode addNull() {
      return this._add(this.nullNode());
   }

   public ArrayNode add(short v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Short v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(int v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Integer v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(long v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Long v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(float v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Float v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(double v) {
      return this._add(this.numberNode(v));
   }

   public ArrayNode add(Double v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(BigDecimal v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(BigInteger v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode add(String v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.textNode(v)));
   }

   public ArrayNode add(boolean v) {
      return this._add(this.booleanNode(v));
   }

   public ArrayNode add(Boolean v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.booleanNode(v)));
   }

   public ArrayNode add(byte[] v) {
      return this._add((JsonNode)(v == null ? this.nullNode() : this.binaryNode(v)));
   }

   public ArrayNode insertArray(int index) {
      ArrayNode n = this.arrayNode();
      this._insert(index, n);
      return n;
   }

   public ObjectNode insertObject(int index) {
      ObjectNode n = this.objectNode();
      this._insert(index, n);
      return n;
   }

   public ArrayNode insertNull(int index) {
      return this._insert(index, this.nullNode());
   }

   public ArrayNode insertPOJO(int index, Object pojo) {
      return this._insert(index, (JsonNode)(pojo == null ? this.nullNode() : this.pojoNode(pojo)));
   }

   public ArrayNode insertRawValue(int index, RawValue raw) {
      return this._insert(index, (JsonNode)(raw == null ? this.nullNode() : this.rawValueNode(raw)));
   }

   public ArrayNode insert(int index, short v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Short value) {
      return this._insert(index, (JsonNode)(value == null ? this.nullNode() : this.numberNode(value)));
   }

   public ArrayNode insert(int index, int v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Integer v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode insert(int index, long v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Long v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode insert(int index, float v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Float v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode insert(int index, double v) {
      return this._insert(index, this.numberNode(v));
   }

   public ArrayNode insert(int index, Double v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode insert(int index, BigDecimal v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode insert(int index, BigInteger v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode insert(int index, String v) {
      return this._insert(index, (JsonNode)(v == null ? this.nullNode() : this.textNode(v)));
   }

   public ArrayNode insert(int index, boolean v) {
      return this._insert(index, this.booleanNode(v));
   }

   public ArrayNode insert(int index, Boolean value) {
      return value == null ? this.insertNull(index) : this._insert(index, this.booleanNode(value));
   }

   public ArrayNode insert(int index, byte[] v) {
      return v == null ? this.insertNull(index) : this._insert(index, this.binaryNode(v));
   }

   public ArrayNode setNull(int index) {
      return this._set(index, this.nullNode());
   }

   public ArrayNode setPOJO(int index, Object pojo) {
      return this._set(index, (JsonNode)(pojo == null ? this.nullNode() : this.pojoNode(pojo)));
   }

   public ArrayNode setRawValue(int index, RawValue raw) {
      return this._set(index, (JsonNode)(raw == null ? this.nullNode() : this.rawValueNode(raw)));
   }

   public ArrayNode set(int index, short v) {
      return this._set(index, this.numberNode(v));
   }

   public ArrayNode set(int index, Short v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, int v) {
      return this._set(index, this.numberNode(v));
   }

   public ArrayNode set(int index, Integer v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, long v) {
      return this._set(index, this.numberNode(v));
   }

   public ArrayNode set(int index, Long v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, float v) {
      return this._set(index, this.numberNode(v));
   }

   public ArrayNode set(int index, Float v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, double v) {
      return this._set(index, this.numberNode(v));
   }

   public ArrayNode set(int index, Double v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, BigDecimal v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, BigInteger v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.numberNode(v)));
   }

   public ArrayNode set(int index, String v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.textNode(v)));
   }

   public ArrayNode set(int index, boolean v) {
      return this._set(index, this.booleanNode(v));
   }

   public ArrayNode set(int index, Boolean v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.booleanNode(v)));
   }

   public ArrayNode set(int index, byte[] v) {
      return this._set(index, (JsonNode)(v == null ? this.nullNode() : this.binaryNode(v)));
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o instanceof ArrayNode ? this._children.equals(((ArrayNode)o)._children) : false;
      }
   }

   protected boolean _childrenEqual(ArrayNode other) {
      return this._children.equals(other._children);
   }

   public int hashCode() {
      return this._children.hashCode();
   }

   protected ArrayNode _set(int index, JsonNode node) {
      if (index >= 0 && index < this._children.size()) {
         this._children.set(index, node);
         return this;
      } else {
         throw new IndexOutOfBoundsException("Illegal index " + index + ", array size " + this.size());
      }
   }

   protected ArrayNode _add(JsonNode node) {
      this._children.add(node);
      return this;
   }

   protected ArrayNode _insert(int index, JsonNode node) {
      if (index < 0) {
         this._children.add(0, node);
      } else if (index >= this._children.size()) {
         this._children.add(node);
      } else {
         this._children.add(index, node);
      }

      return this;
   }
}
