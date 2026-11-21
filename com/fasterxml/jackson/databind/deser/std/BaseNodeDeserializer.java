package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.DatatypeFeatures;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.RawValue;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

abstract class BaseNodeDeserializer<T extends JsonNode> extends StdDeserializer<T> implements ContextualDeserializer {
   protected final Boolean _supportsUpdates;
   protected final boolean _mergeArrays;
   protected final boolean _mergeObjects;

   public BaseNodeDeserializer(Class<T> vc, Boolean supportsUpdates) {
      super(vc);
      this._supportsUpdates = supportsUpdates;
      this._mergeArrays = true;
      this._mergeObjects = true;
   }

   protected BaseNodeDeserializer(BaseNodeDeserializer<?> base, boolean mergeArrays, boolean mergeObjects) {
      super(base);
      this._supportsUpdates = base._supportsUpdates;
      this._mergeArrays = mergeArrays;
      this._mergeObjects = mergeObjects;
   }

   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
      return typeDeserializer.deserializeTypedFromAny(p, ctxt);
   }

   public LogicalType logicalType() {
      return LogicalType.Untyped;
   }

   public boolean isCachable() {
      return true;
   }

   public Boolean supportsUpdate(DeserializationConfig config) {
      return this._supportsUpdates;
   }

   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
      DeserializationConfig cfg = ctxt.getConfig();
      Boolean mergeArr = cfg.getDefaultMergeable(ArrayNode.class);
      Boolean mergeObj = cfg.getDefaultMergeable(ObjectNode.class);
      Boolean mergeNode = cfg.getDefaultMergeable(JsonNode.class);
      boolean mergeArrays = _shouldMerge(mergeArr, mergeNode);
      boolean mergeObjects = _shouldMerge(mergeObj, mergeNode);
      return (JsonDeserializer<?>)(mergeArrays == this._mergeArrays && mergeObjects == this._mergeObjects ? this : this._createWithMerge(mergeArrays, mergeObjects));
   }

   private static boolean _shouldMerge(Boolean specificMerge, Boolean generalMerge) {
      if (specificMerge != null) {
         return specificMerge;
      } else {
         return generalMerge != null ? generalMerge : true;
      }
   }

   protected abstract JsonDeserializer<?> _createWithMerge(boolean var1, boolean var2);

   protected void _handleDuplicateField(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, String fieldName, ObjectNode objectNode, JsonNode oldValue, JsonNode newValue) throws IOException {
      if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)) {
         ctxt.reportInputMismatch(JsonNode.class, "Duplicate field '%s' for `ObjectNode`: not allowed when `DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY` enabled", fieldName);
      }

      if (ctxt.isEnabled(StreamReadCapability.DUPLICATE_PROPERTIES)) {
         if (oldValue.isArray()) {
            ((ArrayNode)oldValue).add(newValue);
            objectNode.replace(fieldName, oldValue);
         } else {
            ArrayNode arr = nodeFactory.arrayNode();
            arr.add(oldValue);
            arr.add(newValue);
            objectNode.replace(fieldName, arr);
         }
      }

   }

   protected final ObjectNode _deserializeObjectAtName(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack) throws IOException {
      ObjectNode node = nodeFactory.objectNode();

      for(String key = p.currentName(); key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         if (t == null) {
            t = JsonToken.NOT_AVAILABLE;
         }

         JsonNode value;
         switch (t.id()) {
            case 1:
               value = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
               break;
            case 3:
               value = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
               break;
            default:
               value = this._deserializeAnyScalar(p, ctxt);
         }

         JsonNode old = node.replace(key, value);
         if (old != null) {
            this._handleDuplicateField(p, ctxt, nodeFactory, key, node, old, value);
         }
      }

      return node;
   }

   protected final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, ObjectNode node, ContainerStack stack) throws IOException {
      String key;
      if (p.isExpectedStartObjectToken()) {
         key = p.nextFieldName();
      } else {
         if (!p.hasToken(JsonToken.FIELD_NAME)) {
            return (JsonNode)this.deserialize(p, ctxt);
         }

         key = p.currentName();
      }

      for(JsonNodeFactory nodeFactory = ctxt.getNodeFactory(); key != null; key = p.nextFieldName()) {
         JsonToken t = p.nextToken();
         JsonNode old = node.get(key);
         if (old != null) {
            if (old instanceof ObjectNode) {
               if (t == JsonToken.START_OBJECT && this._mergeObjects) {
                  JsonNode newValue = this.updateObject(p, ctxt, (ObjectNode)old, stack);
                  if (newValue != old) {
                     node.set(key, newValue);
                  }
                  continue;
               }
            } else if (old instanceof ArrayNode && t == JsonToken.START_ARRAY && this._mergeArrays) {
               this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, (ArrayNode)old);
               continue;
            }
         }

         if (t == null) {
            t = JsonToken.NOT_AVAILABLE;
         }

         JsonNode value;
         switch (t.id()) {
            case 1:
               value = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
               break;
            case 2:
            case 4:
            case 5:
            case 8:
            default:
               value = this._deserializeRareScalar(p, ctxt);
               break;
            case 3:
               value = this._deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
               break;
            case 6:
               value = nodeFactory.textNode(p.getText());
               break;
            case 7:
               value = this._fromInt(p, ctxt, nodeFactory);
               break;
            case 9:
               value = nodeFactory.booleanNode(true);
               break;
            case 10:
               value = nodeFactory.booleanNode(false);
               break;
            case 11:
               if (!ctxt.isEnabled((DatatypeFeature)JsonNodeFeature.READ_NULL_PROPERTIES)) {
                  continue;
               }

               value = nodeFactory.nullNode();
         }

         node.set(key, value);
      }

      return node;
   }

   protected final ContainerNode<?> _deserializeContainerNoRecursion(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory, ContainerStack stack, ContainerNode<?> root) throws IOException {
      ContainerNode<?> curr = root;
      int intCoercionFeats = ctxt.getDeserializationFeatures() & F_MASK_INT_COERCIONS;

      label90:
      do {
         if (!(curr instanceof ObjectNode)) {
            ArrayNode currArray = (ArrayNode)curr;

            label69:
            while(true) {
               JsonToken t = p.nextToken();
               if (t == null) {
                  t = JsonToken.NOT_AVAILABLE;
               }

               switch (t.id()) {
                  case 1:
                     stack.push(curr);
                     curr = nodeFactory.objectNode();
                     currArray.add((JsonNode)curr);
                     continue label90;
                  case 2:
                  case 5:
                  default:
                     currArray.add(this._deserializeRareScalar(p, ctxt));
                     break;
                  case 3:
                     stack.push(curr);
                     curr = nodeFactory.arrayNode();
                     currArray.add((JsonNode)curr);
                     continue label90;
                  case 4:
                     break label69;
                  case 6:
                     currArray.add((JsonNode)nodeFactory.textNode(p.getText()));
                     break;
                  case 7:
                     currArray.add(this._fromInt(p, intCoercionFeats, nodeFactory));
                     break;
                  case 8:
                     currArray.add(this._fromFloat(p, ctxt, nodeFactory));
                     break;
                  case 9:
                     currArray.add((JsonNode)nodeFactory.booleanNode(true));
                     break;
                  case 10:
                     currArray.add((JsonNode)nodeFactory.booleanNode(false));
                     break;
                  case 11:
                     currArray.add((JsonNode)nodeFactory.nullNode());
               }
            }
         } else {
            ObjectNode currObject = (ObjectNode)curr;

            for(String propName = p.nextFieldName(); propName != null; propName = p.nextFieldName()) {
               JsonToken t = p.nextToken();
               if (t == null) {
                  t = JsonToken.NOT_AVAILABLE;
               }

               JsonNode value;
               switch (t.id()) {
                  case 1:
                     ObjectNode newOb = nodeFactory.objectNode();
                     JsonNode old = currObject.replace(propName, newOb);
                     if (old != null) {
                        this._handleDuplicateField(p, ctxt, nodeFactory, propName, currObject, old, newOb);
                     }

                     stack.push(curr);
                     currObject = newOb;
                     curr = newOb;
                     continue;
                  case 2:
                  case 4:
                  case 5:
                  default:
                     value = this._deserializeRareScalar(p, ctxt);
                     break;
                  case 3:
                     ArrayNode newOb = nodeFactory.arrayNode();
                     JsonNode old = currObject.replace(propName, newOb);
                     if (old != null) {
                        this._handleDuplicateField(p, ctxt, nodeFactory, propName, currObject, old, newOb);
                     }

                     stack.push(curr);
                     curr = newOb;
                     continue label90;
                  case 6:
                     value = nodeFactory.textNode(p.getText());
                     break;
                  case 7:
                     value = this._fromInt(p, intCoercionFeats, nodeFactory);
                     break;
                  case 8:
                     value = this._fromFloat(p, ctxt, nodeFactory);
                     break;
                  case 9:
                     value = nodeFactory.booleanNode(true);
                     break;
                  case 10:
                     value = nodeFactory.booleanNode(false);
                     break;
                  case 11:
                     if (!ctxt.isEnabled((DatatypeFeature)JsonNodeFeature.READ_NULL_PROPERTIES)) {
                        continue;
                     }

                     value = nodeFactory.nullNode();
               }

               JsonNode old = currObject.replace(propName, value);
               if (old != null) {
                  this._handleDuplicateField(p, ctxt, nodeFactory, propName, currObject, old, value);
               }
            }
         }

         curr = stack.popOrNull();
      } while(curr != null);

      return root;
   }

   protected final JsonNode _deserializeAnyScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNodeFactory nodeF = ctxt.getNodeFactory();
      switch (p.currentTokenId()) {
         case 2:
            return nodeF.objectNode();
         case 3:
         case 4:
         case 5:
         default:
            return (JsonNode)ctxt.handleUnexpectedToken(this.handledType(), p);
         case 6:
            return nodeF.textNode(p.getText());
         case 7:
            return this._fromInt(p, ctxt, nodeF);
         case 8:
            return this._fromFloat(p, ctxt, nodeF);
         case 9:
            return nodeF.booleanNode(true);
         case 10:
            return nodeF.booleanNode(false);
         case 11:
            return nodeF.nullNode();
         case 12:
            return this._fromEmbedded(p, ctxt);
      }
   }

   protected final JsonNode _deserializeRareScalar(JsonParser p, DeserializationContext ctxt) throws IOException {
      switch (p.currentTokenId()) {
         case 2:
            return ctxt.getNodeFactory().objectNode();
         case 8:
            return this._fromFloat(p, ctxt, ctxt.getNodeFactory());
         case 12:
            return this._fromEmbedded(p, ctxt);
         default:
            return (JsonNode)ctxt.handleUnexpectedToken(this.handledType(), p);
      }
   }

   protected final JsonNode _fromInt(JsonParser p, int coercionFeatures, JsonNodeFactory nodeFactory) throws IOException {
      if (coercionFeatures != 0) {
         return (JsonNode)(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(coercionFeatures) ? nodeFactory.numberNode(p.getBigIntegerValue()) : nodeFactory.numberNode(p.getLongValue()));
      } else {
         JsonParser.NumberType nt = p.getNumberType();
         if (nt == JsonParser.NumberType.INT) {
            return nodeFactory.numberNode(p.getIntValue());
         } else {
            return (JsonNode)(nt == JsonParser.NumberType.LONG ? nodeFactory.numberNode(p.getLongValue()) : nodeFactory.numberNode(p.getBigIntegerValue()));
         }
      }
   }

   protected final JsonNode _fromInt(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      int feats = ctxt.getDeserializationFeatures();
      JsonParser.NumberType nt;
      if ((feats & F_MASK_INT_COERCIONS) != 0) {
         if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
            nt = JsonParser.NumberType.BIG_INTEGER;
         } else if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
            nt = JsonParser.NumberType.LONG;
         } else {
            nt = p.getNumberType();
         }
      } else {
         nt = p.getNumberType();
      }

      if (nt == JsonParser.NumberType.INT) {
         return nodeFactory.numberNode(p.getIntValue());
      } else {
         return (JsonNode)(nt == JsonParser.NumberType.LONG ? nodeFactory.numberNode(p.getLongValue()) : nodeFactory.numberNode(p.getBigIntegerValue()));
      }
   }

   protected final JsonNode _fromFloat(JsonParser p, DeserializationContext ctxt, JsonNodeFactory nodeFactory) throws IOException {
      JsonParser.NumberTypeFP nt = p.getNumberTypeFP();
      if (nt == JsonParser.NumberTypeFP.BIG_DECIMAL) {
         return this._fromBigDecimal(ctxt, nodeFactory, p.getDecimalValue());
      } else {
         DatatypeFeatures dtf = ctxt.getDatatypeFeatures();
         Boolean dtfState = dtf.getExplicitState(JsonNodeFeature.USE_BIG_DECIMAL_FOR_FLOATS);
         boolean useBigDecimal = dtfState == null ? ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS) : dtfState;
         if (useBigDecimal) {
            if (p.isNaN()) {
               return (JsonNode)(ctxt.isEnabled((DatatypeFeature)JsonNodeFeature.FAIL_ON_NAN_TO_BIG_DECIMAL_COERCION) ? (JsonNode)ctxt.handleWeirdNumberValue(this.handledType(), p.getDoubleValue(), "Cannot convert NaN into BigDecimal") : nodeFactory.numberNode(p.getDoubleValue()));
            } else {
               return this._fromBigDecimal(ctxt, nodeFactory, p.getDecimalValue());
            }
         } else {
            return nt == JsonParser.NumberTypeFP.FLOAT32 ? nodeFactory.numberNode(p.getFloatValue()) : nodeFactory.numberNode(p.getDoubleValue());
         }
      }
   }

   protected final JsonNode _fromBigDecimal(DeserializationContext ctxt, JsonNodeFactory nodeFactory, BigDecimal bigDec) {
      DatatypeFeatures dtf = ctxt.getDatatypeFeatures();
      boolean normalize;
      if (dtf.isExplicitlySet(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)) {
         normalize = dtf.isEnabled(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES);
      } else {
         normalize = nodeFactory.willStripTrailingBigDecimalZeroes();
      }

      if (normalize) {
         try {
            bigDec = bigDec.stripTrailingZeros();
         } catch (ArithmeticException var7) {
         }
      }

      return nodeFactory.numberNode(bigDec);
   }

   protected final JsonNode _fromEmbedded(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNodeFactory nodeF = ctxt.getNodeFactory();
      Object ob = p.getEmbeddedObject();
      if (ob == null) {
         return nodeF.nullNode();
      } else {
         Class<?> type = ob.getClass();
         if (type == byte[].class) {
            return nodeF.binaryNode((byte[])ob);
         } else if (ob instanceof RawValue) {
            return nodeF.rawValueNode((RawValue)ob);
         } else {
            return (JsonNode)(ob instanceof JsonNode ? (JsonNode)ob : nodeF.pojoNode(ob));
         }
      }
   }

   static final class ContainerStack {
      private ContainerNode[] _stack;
      private int _top;
      private int _end;

      public ContainerStack() {
      }

      public int size() {
         return this._top;
      }

      public void push(ContainerNode node) {
         if (this._top < this._end) {
            this._stack[this._top++] = node;
         } else {
            if (this._stack == null) {
               this._end = 10;
               this._stack = new ContainerNode[this._end];
            } else {
               this._end += Math.min(4000, Math.max(20, this._end >> 1));
               this._stack = (ContainerNode[])Arrays.copyOf(this._stack, this._end);
            }

            this._stack[this._top++] = node;
         }
      }

      public ContainerNode popOrNull() {
         return this._top == 0 ? null : this._stack[--this._top];
      }
   }
}
