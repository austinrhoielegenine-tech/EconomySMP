package org.slf4j;

import org.slf4j.event.Level;
import org.slf4j.helpers.CheckReturnValue;
import org.slf4j.spi.DefaultLoggingEventBuilder;
import org.slf4j.spi.LoggingEventBuilder;
import org.slf4j.spi.NOPLoggingEventBuilder;

public interface Logger {
   String ROOT_LOGGER_NAME = "ROOT";

   String getName();

   default LoggingEventBuilder makeLoggingEventBuilder(Level level) {
      return new DefaultLoggingEventBuilder(this, level);
   }

   @CheckReturnValue
   default LoggingEventBuilder atLevel(Level level) {
      return this.isEnabledForLevel(level) ? this.makeLoggingEventBuilder(level) : NOPLoggingEventBuilder.singleton();
   }

   default boolean isEnabledForLevel(Level level) {
      int levelInt = level.toInt();
      switch (levelInt) {
         case 0:
            return this.isTraceEnabled();
         case 10:
            return this.isDebugEnabled();
         case 20:
            return this.isInfoEnabled();
         case 30:
            return this.isWarnEnabled();
         case 40:
            return this.isErrorEnabled();
         default:
            throw new IllegalArgumentException("Level [" + level + "] not recognized.");
      }
   }

   boolean isTraceEnabled();

   void trace(String var1);

   void trace(String var1, Object var2);

   void trace(String var1, Object var2, Object var3);

   void trace(String var1, Object... var2);

   void trace(String var1, Throwable var2);

   boolean isTraceEnabled(Marker var1);

   @CheckReturnValue
   default LoggingEventBuilder atTrace() {
      return this.isTraceEnabled() ? this.makeLoggingEventBuilder(Level.TRACE) : NOPLoggingEventBuilder.singleton();
   }

   void trace(Marker var1, String var2);

   void trace(Marker var1, String var2, Object var3);

   void trace(Marker var1, String var2, Object var3, Object var4);

   void trace(Marker var1, String var2, Object... var3);

   void trace(Marker var1, String var2, Throwable var3);

   boolean isDebugEnabled();

   void debug(String var1);

   void debug(String var1, Object var2);

   void debug(String var1, Object var2, Object var3);

   void debug(String var1, Object... var2);

   void debug(String var1, Throwable var2);

   boolean isDebugEnabled(Marker var1);

   void debug(Marker var1, String var2);

   void debug(Marker var1, String var2, Object var3);

   void debug(Marker var1, String var2, Object var3, Object var4);

   void debug(Marker var1, String var2, Object... var3);

   void debug(Marker var1, String var2, Throwable var3);

   @CheckReturnValue
   default LoggingEventBuilder atDebug() {
      return this.isDebugEnabled() ? this.makeLoggingEventBuilder(Level.DEBUG) : NOPLoggingEventBuilder.singleton();
   }

   boolean isInfoEnabled();

   void info(String var1);

   void info(String var1, Object var2);

   void info(String var1, Object var2, Object var3);

   void info(String var1, Object... var2);

   void info(String var1, Throwable var2);

   boolean isInfoEnabled(Marker var1);

   void info(Marker var1, String var2);

   void info(Marker var1, String var2, Object var3);

   void info(Marker var1, String var2, Object var3, Object var4);

   void info(Marker var1, String var2, Object... var3);

   void info(Marker var1, String var2, Throwable var3);

   @CheckReturnValue
   default LoggingEventBuilder atInfo() {
      return this.isInfoEnabled() ? this.makeLoggingEventBuilder(Level.INFO) : NOPLoggingEventBuilder.singleton();
   }

   boolean isWarnEnabled();

   void warn(String var1);

   void warn(String var1, Object var2);

   void warn(String var1, Object... var2);

   void warn(String var1, Object var2, Object var3);

   void warn(String var1, Throwable var2);

   boolean isWarnEnabled(Marker var1);

   void warn(Marker var1, String var2);

   void warn(Marker var1, String var2, Object var3);

   void warn(Marker var1, String var2, Object var3, Object var4);

   void warn(Marker var1, String var2, Object... var3);

   void warn(Marker var1, String var2, Throwable var3);

   @CheckReturnValue
   default LoggingEventBuilder atWarn() {
      return this.isWarnEnabled() ? this.makeLoggingEventBuilder(Level.WARN) : NOPLoggingEventBuilder.singleton();
   }

   boolean isErrorEnabled();

   void error(String var1);

   void error(String var1, Object var2);

   void error(String var1, Object var2, Object var3);

   void error(String var1, Object... var2);

   void error(String var1, Throwable var2);

   boolean isErrorEnabled(Marker var1);

   void error(Marker var1, String var2);

   void error(Marker var1, String var2, Object var3);

   void error(Marker var1, String var2, Object var3, Object var4);

   void error(Marker var1, String var2, Object... var3);

   void error(Marker var1, String var2, Throwable var3);

   @CheckReturnValue
   default LoggingEventBuilder atError() {
      return this.isErrorEnabled() ? this.makeLoggingEventBuilder(Level.ERROR) : NOPLoggingEventBuilder.singleton();
   }
}
