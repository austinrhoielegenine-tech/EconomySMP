package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

public class SubstituteLoggingEvent implements LoggingEvent {
   Level level;
   List<Marker> markers;
   String loggerName;
   SubstituteLogger logger;
   String threadName;
   String message;
   Object[] argArray;
   List<KeyValuePair> keyValuePairList;
   long timeStamp;
   Throwable throwable;

   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level level) {
      this.level = level;
   }

   public List<Marker> getMarkers() {
      return this.markers;
   }

   public void addMarker(Marker marker) {
      if (marker != null) {
         if (this.markers == null) {
            this.markers = new ArrayList(2);
         }

         this.markers.add(marker);
      }
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public void setLoggerName(String loggerName) {
      this.loggerName = loggerName;
   }

   public SubstituteLogger getLogger() {
      return this.logger;
   }

   public void setLogger(SubstituteLogger logger) {
      this.logger = logger;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public Object[] getArgumentArray() {
      return this.argArray;
   }

   public void setArgumentArray(Object[] argArray) {
      this.argArray = argArray;
   }

   public List<Object> getArguments() {
      return this.argArray == null ? null : Arrays.asList(this.argArray);
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long timeStamp) {
      this.timeStamp = timeStamp;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public void setThreadName(String threadName) {
      this.threadName = threadName;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public void setThrowable(Throwable throwable) {
      this.throwable = throwable;
   }

   public List<KeyValuePair> getKeyValuePairs() {
      return this.keyValuePairList;
   }
}
