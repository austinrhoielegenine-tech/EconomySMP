package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;

public class DefaultLoggingEventBuilder implements LoggingEventBuilder, CallerBoundaryAware {
   static String DLEB_FQCN = DefaultLoggingEventBuilder.class.getName();
   protected DefaultLoggingEvent loggingEvent;
   protected Logger logger;

   public DefaultLoggingEventBuilder(Logger logger, Level level) {
      this.logger = logger;
      this.loggingEvent = new DefaultLoggingEvent(level, logger);
   }

   public LoggingEventBuilder addMarker(Marker marker) {
      this.loggingEvent.addMarker(marker);
      return this;
   }

   public LoggingEventBuilder setCause(Throwable t) {
      this.loggingEvent.setThrowable(t);
      return this;
   }

   public LoggingEventBuilder addArgument(Object p) {
      this.loggingEvent.addArgument(p);
      return this;
   }

   public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
      this.loggingEvent.addArgument(objectSupplier.get());
      return this;
   }

   public void setCallerBoundary(String fqcn) {
      this.loggingEvent.setCallerBoundary(fqcn);
   }

   public void log() {
      this.log((LoggingEvent)this.loggingEvent);
   }

   public LoggingEventBuilder setMessage(String message) {
      this.loggingEvent.setMessage(message);
      return this;
   }

   public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
      this.loggingEvent.setMessage((String)messageSupplier.get());
      return this;
   }

   public void log(String message) {
      this.loggingEvent.setMessage(message);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(String message, Object arg) {
      this.loggingEvent.setMessage(message);
      this.loggingEvent.addArgument(arg);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(String message, Object arg0, Object arg1) {
      this.loggingEvent.setMessage(message);
      this.loggingEvent.addArgument(arg0);
      this.loggingEvent.addArgument(arg1);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(String message, Object... args) {
      this.loggingEvent.setMessage(message);
      this.loggingEvent.addArguments(args);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(Supplier<String> messageSupplier) {
      if (messageSupplier == null) {
         this.log((String)null);
      } else {
         this.log((String)messageSupplier.get());
      }

   }

   protected void log(LoggingEvent aLoggingEvent) {
      this.setCallerBoundary(DLEB_FQCN);
      if (this.logger instanceof LoggingEventAware) {
         ((LoggingEventAware)this.logger).log(aLoggingEvent);
      } else {
         this.logViaPublicSLF4JLoggerAPI(aLoggingEvent);
      }

   }

   private void logViaPublicSLF4JLoggerAPI(LoggingEvent aLoggingEvent) {
      Object[] argArray = aLoggingEvent.getArgumentArray();
      int argLen = argArray == null ? 0 : argArray.length;
      Throwable t = aLoggingEvent.getThrowable();
      int tLen = t == null ? 0 : 1;
      String msg = aLoggingEvent.getMessage();
      Object[] combinedArguments = new Object[argLen + tLen];
      if (argArray != null) {
         System.arraycopy(argArray, 0, combinedArguments, 0, argLen);
      }

      if (t != null) {
         combinedArguments[argLen] = t;
      }

      msg = this.mergeMarkersAndKeyValuePairs(aLoggingEvent, msg);
      switch (aLoggingEvent.getLevel()) {
         case TRACE:
            this.logger.trace(msg, combinedArguments);
            break;
         case DEBUG:
            this.logger.debug(msg, combinedArguments);
            break;
         case INFO:
            this.logger.info(msg, combinedArguments);
            break;
         case WARN:
            this.logger.warn(msg, combinedArguments);
            break;
         case ERROR:
            this.logger.error(msg, combinedArguments);
      }

   }

   private String mergeMarkersAndKeyValuePairs(LoggingEvent aLoggingEvent, String msg) {
      StringBuilder sb = null;
      if (aLoggingEvent.getMarkers() != null) {
         sb = new StringBuilder();

         for(Marker marker : aLoggingEvent.getMarkers()) {
            sb.append(marker);
            sb.append(' ');
         }
      }

      if (aLoggingEvent.getKeyValuePairs() != null) {
         if (sb == null) {
            sb = new StringBuilder();
         }

         for(KeyValuePair kvp : aLoggingEvent.getKeyValuePairs()) {
            sb.append(kvp.key);
            sb.append('=');
            sb.append(kvp.value);
            sb.append(' ');
         }
      }

      if (sb != null) {
         sb.append(msg);
         return sb.toString();
      } else {
         return msg;
      }
   }

   public LoggingEventBuilder addKeyValue(String key, Object value) {
      this.loggingEvent.addKeyValue(key, value);
      return this;
   }

   public LoggingEventBuilder addKeyValue(String key, Supplier<Object> value) {
      this.loggingEvent.addKeyValue(key, value.get());
      return this;
   }
}
