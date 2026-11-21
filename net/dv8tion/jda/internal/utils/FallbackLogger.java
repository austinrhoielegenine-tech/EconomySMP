package net.dv8tion.jda.internal.utils;

import java.time.LocalDateTime;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

public class FallbackLogger extends LegacyAbstractLogger {
   public FallbackLogger(String name) {
      this.name = name;
   }

   protected String getFullyQualifiedCallerName() {
      return null;
   }

   protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
      LocalDateTime now = LocalDateTime.now();
      FormattingTuple result = MessageFormatter.arrayFormat(messagePattern, arguments);
      System.err.printf("%1$tF %1$tT [%2$s] [%3$s] %4$s%n", now, this.name, level, result.getMessage());
      if (throwable != null) {
         throwable.printStackTrace(System.err);
      }

   }

   public boolean isTraceEnabled() {
      return false;
   }

   public boolean isDebugEnabled() {
      return false;
   }

   public boolean isInfoEnabled() {
      return true;
   }

   public boolean isWarnEnabled() {
      return true;
   }

   public boolean isErrorEnabled() {
      return true;
   }
}
