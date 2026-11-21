package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Marker;
import org.slf4j.helpers.CheckReturnValue;

public interface LoggingEventBuilder {
   @CheckReturnValue
   LoggingEventBuilder setCause(Throwable var1);

   @CheckReturnValue
   LoggingEventBuilder addMarker(Marker var1);

   @CheckReturnValue
   LoggingEventBuilder addArgument(Object var1);

   @CheckReturnValue
   LoggingEventBuilder addArgument(Supplier<?> var1);

   @CheckReturnValue
   LoggingEventBuilder addKeyValue(String var1, Object var2);

   @CheckReturnValue
   LoggingEventBuilder addKeyValue(String var1, Supplier<Object> var2);

   @CheckReturnValue
   LoggingEventBuilder setMessage(String var1);

   @CheckReturnValue
   LoggingEventBuilder setMessage(Supplier<String> var1);

   void log();

   void log(String var1);

   void log(String var1, Object var2);

   void log(String var1, Object var2, Object var3);

   void log(String var1, Object... var2);

   void log(Supplier<String> var1);
}
