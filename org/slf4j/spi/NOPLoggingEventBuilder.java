package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Marker;

public class NOPLoggingEventBuilder implements LoggingEventBuilder {
   static final NOPLoggingEventBuilder SINGLETON = new NOPLoggingEventBuilder();

   private NOPLoggingEventBuilder() {
   }

   public static LoggingEventBuilder singleton() {
      return SINGLETON;
   }

   public LoggingEventBuilder addMarker(Marker marker) {
      return singleton();
   }

   public LoggingEventBuilder addArgument(Object p) {
      return singleton();
   }

   public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
      return singleton();
   }

   public LoggingEventBuilder addKeyValue(String key, Object value) {
      return singleton();
   }

   public LoggingEventBuilder addKeyValue(String key, Supplier<Object> value) {
      return singleton();
   }

   public LoggingEventBuilder setCause(Throwable cause) {
      return singleton();
   }

   public void log() {
   }

   public LoggingEventBuilder setMessage(String message) {
      return this;
   }

   public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
      return this;
   }

   public void log(String message) {
   }

   public void log(Supplier<String> messageSupplier) {
   }

   public void log(String message, Object arg) {
   }

   public void log(String message, Object arg0, Object arg1) {
   }

   public void log(String message, Object... args) {
   }
}
