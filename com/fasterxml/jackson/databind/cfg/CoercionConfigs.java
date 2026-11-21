package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CoercionConfigs implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int TARGET_TYPE_COUNT = LogicalType.values().length;
   protected CoercionAction _defaultAction;
   protected final MutableCoercionConfig _defaultCoercions;
   protected MutableCoercionConfig[] _perTypeCoercions;
   protected Map<Class<?>, MutableCoercionConfig> _perClassCoercions;

   public CoercionConfigs() {
      this(CoercionAction.TryConvert, new MutableCoercionConfig(), (MutableCoercionConfig[])null, (Map)null);
   }

   protected CoercionConfigs(CoercionAction defaultAction, MutableCoercionConfig defaultCoercions, MutableCoercionConfig[] perTypeCoercions, Map<Class<?>, MutableCoercionConfig> perClassCoercions) {
      this._defaultCoercions = defaultCoercions;
      this._defaultAction = defaultAction;
      this._perTypeCoercions = perTypeCoercions;
      this._perClassCoercions = perClassCoercions;
   }

   public CoercionConfigs copy() {
      MutableCoercionConfig[] newPerType;
      if (this._perTypeCoercions == null) {
         newPerType = null;
      } else {
         int size = this._perTypeCoercions.length;
         newPerType = new MutableCoercionConfig[size];

         for(int i = 0; i < size; ++i) {
            newPerType[i] = _copy(this._perTypeCoercions[i]);
         }
      }

      Map<Class<?>, MutableCoercionConfig> newPerClass;
      if (this._perClassCoercions == null) {
         newPerClass = null;
      } else {
         newPerClass = new HashMap();

         for(Map.Entry<Class<?>, MutableCoercionConfig> entry : this._perClassCoercions.entrySet()) {
            newPerClass.put(entry.getKey(), ((MutableCoercionConfig)entry.getValue()).copy());
         }
      }

      return new CoercionConfigs(this._defaultAction, this._defaultCoercions.copy(), newPerType, newPerClass);
   }

   private static MutableCoercionConfig _copy(MutableCoercionConfig src) {
      return src == null ? null : src.copy();
   }

   public MutableCoercionConfig defaultCoercions() {
      return this._defaultCoercions;
   }

   public MutableCoercionConfig findOrCreateCoercion(LogicalType type) {
      if (this._perTypeCoercions == null) {
         this._perTypeCoercions = new MutableCoercionConfig[TARGET_TYPE_COUNT];
      }

      MutableCoercionConfig config = this._perTypeCoercions[type.ordinal()];
      if (config == null) {
         this._perTypeCoercions[type.ordinal()] = config = new MutableCoercionConfig();
      }

      return config;
   }

   public MutableCoercionConfig findOrCreateCoercion(Class<?> type) {
      if (this._perClassCoercions == null) {
         this._perClassCoercions = new HashMap();
      }

      MutableCoercionConfig config = (MutableCoercionConfig)this._perClassCoercions.get(type);
      if (config == null) {
         config = new MutableCoercionConfig();
         this._perClassCoercions.put(type, config);
      }

      return config;
   }

   public CoercionAction findCoercion(DeserializationConfig config, LogicalType targetType, Class<?> targetClass, CoercionInputShape inputShape) {
      if (this._perClassCoercions != null && targetClass != null) {
         MutableCoercionConfig cc = (MutableCoercionConfig)this._perClassCoercions.get(targetClass);
         if (cc != null) {
            CoercionAction act = cc.findAction(inputShape);
            if (act != null) {
               return act;
            }
         }
      }

      if (this._perTypeCoercions != null && targetType != null) {
         MutableCoercionConfig cc = this._perTypeCoercions[targetType.ordinal()];
         if (cc != null) {
            CoercionAction act = cc.findAction(inputShape);
            if (act != null) {
               return act;
            }
         }
      }

      CoercionAction act = this._defaultCoercions.findAction(inputShape);
      if (act != null) {
         return act;
      } else {
         switch (inputShape) {
            case EmptyArray:
               return config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT) ? CoercionAction.AsNull : CoercionAction.Fail;
            case Float:
               if (targetType == LogicalType.Integer) {
                  return config.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT) ? CoercionAction.TryConvert : CoercionAction.Fail;
               }
               break;
            case Integer:
               if (targetType == LogicalType.Enum && config.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                  return CoercionAction.Fail;
               }
         }

         boolean baseScalar = this._isScalarType(targetType);
         if (!baseScalar || config.isEnabled((MapperFeature)MapperFeature.ALLOW_COERCION_OF_SCALARS) || targetType == LogicalType.Float && inputShape == CoercionInputShape.Integer) {
            if (inputShape == CoercionInputShape.EmptyString) {
               if (targetType == LogicalType.OtherScalar) {
                  return CoercionAction.TryConvert;
               } else {
                  return !baseScalar && !config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) ? CoercionAction.Fail : CoercionAction.AsNull;
               }
            } else {
               return this._defaultAction;
            }
         } else {
            return CoercionAction.Fail;
         }
      }
   }

   public CoercionAction findCoercionFromBlankString(DeserializationConfig config, LogicalType targetType, Class<?> targetClass, CoercionAction actionIfBlankNotAllowed) {
      Boolean acceptBlankAsEmpty = null;
      CoercionAction action = null;
      if (this._perClassCoercions != null && targetClass != null) {
         MutableCoercionConfig cc = (MutableCoercionConfig)this._perClassCoercions.get(targetClass);
         if (cc != null) {
            acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
            action = cc.findAction(CoercionInputShape.EmptyString);
         }
      }

      if (this._perTypeCoercions != null && targetType != null) {
         MutableCoercionConfig cc = this._perTypeCoercions[targetType.ordinal()];
         if (cc != null) {
            if (acceptBlankAsEmpty == null) {
               acceptBlankAsEmpty = cc.getAcceptBlankAsEmpty();
            }

            if (action == null) {
               action = cc.findAction(CoercionInputShape.EmptyString);
            }
         }
      }

      if (acceptBlankAsEmpty == null) {
         acceptBlankAsEmpty = this._defaultCoercions.getAcceptBlankAsEmpty();
      }

      if (action == null) {
         action = this._defaultCoercions.findAction(CoercionInputShape.EmptyString);
      }

      if (Boolean.FALSE.equals(acceptBlankAsEmpty)) {
         return actionIfBlankNotAllowed;
      } else if (action != null) {
         return action;
      } else if (this._isScalarType(targetType)) {
         return CoercionAction.AsNull;
      } else {
         return config.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) ? CoercionAction.AsNull : actionIfBlankNotAllowed;
      }
   }

   protected boolean _isScalarType(LogicalType targetType) {
      return targetType == LogicalType.Float || targetType == LogicalType.Integer || targetType == LogicalType.Boolean || targetType == LogicalType.DateTime;
   }
}
