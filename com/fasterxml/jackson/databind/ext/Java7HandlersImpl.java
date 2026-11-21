package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import java.nio.file.Path;

public class Java7HandlersImpl extends Java7Handlers {
   private final Class<?> _pathClass = Path.class;

   public Class<?> getClassJavaNioFilePath() {
      return this._pathClass;
   }

   public JsonDeserializer<?> getDeserializerForJavaNioFilePath(Class<?> rawType) {
      return rawType == this._pathClass ? new NioPathDeserializer() : null;
   }

   public JsonSerializer<?> getSerializerForJavaNioFilePath(Class<?> rawType) {
      return this._pathClass.isAssignableFrom(rawType) ? new NioPathSerializer() : null;
   }
}
