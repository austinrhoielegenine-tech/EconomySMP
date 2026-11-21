package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.util.JacksonFeature;

public enum StreamReadFeature implements JacksonFeature {
   AUTO_CLOSE_SOURCE(JsonParser.Feature.AUTO_CLOSE_SOURCE),
   IGNORE_UNDEFINED(JsonParser.Feature.IGNORE_UNDEFINED),
   STRICT_DUPLICATE_DETECTION(JsonParser.Feature.STRICT_DUPLICATE_DETECTION),
   CLEAR_CURRENT_TOKEN_ON_CLOSE(JsonParser.Feature.CLEAR_CURRENT_TOKEN_ON_CLOSE),
   INCLUDE_SOURCE_IN_LOCATION(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION),
   USE_FAST_BIG_NUMBER_PARSER(JsonParser.Feature.USE_FAST_BIG_NUMBER_PARSER),
   USE_FAST_DOUBLE_PARSER(JsonParser.Feature.USE_FAST_DOUBLE_PARSER);

   private final boolean _defaultState;
   private final int _mask;
   private final JsonParser.Feature _mappedFeature;

   private StreamReadFeature(JsonParser.Feature mapTo) {
      this._mappedFeature = mapTo;
      this._mask = mapTo.getMask();
      this._defaultState = mapTo.enabledByDefault();
   }

   public static int collectDefaults() {
      int flags = 0;

      for(StreamReadFeature f : values()) {
         if (f.enabledByDefault()) {
            flags |= f.getMask();
         }
      }

      return flags;
   }

   public boolean enabledByDefault() {
      return this._defaultState;
   }

   public boolean enabledIn(int flags) {
      return (flags & this._mask) != 0;
   }

   public int getMask() {
      return this._mask;
   }

   public JsonParser.Feature mappedFeature() {
      return this._mappedFeature;
   }
}
