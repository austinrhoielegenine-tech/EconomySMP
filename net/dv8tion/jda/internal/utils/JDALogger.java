package net.dv8tion.jda.internal.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

public class JDALogger {
   public static final String DISABLE_FALLBACK_PROPERTY_NAME = "net.dv8tion.jda.disableFallbackLogger";
   public static final boolean SLF4J_ENABLED;
   private static boolean disableFallback = Boolean.getBoolean("net.dv8tion.jda.disableFallbackLogger");
   private static final MethodHandle fallbackLoggerConstructor;
   private static final Map<String, Logger> LOGS;

   private JDALogger() {
   }

   public static void setFallbackLoggerEnabled(boolean enabled) {
      disableFallback = !enabled;
   }

   public static Logger getLog(String name) {
      synchronized(LOGS) {
         return !SLF4J_ENABLED && !disableFallback ? newFallbackLogger(name) : LoggerFactory.getLogger(name);
      }
   }

   public static Logger getLog(Class<?> clazz) {
      synchronized(LOGS) {
         return !SLF4J_ENABLED && !disableFallback ? newFallbackLogger(clazz.getSimpleName()) : LoggerFactory.getLogger(clazz);
      }
   }

   private static void printFallbackWarning() {
      Logger logger = newFallbackLogger(JDALogger.class.getSimpleName());
      logger.warn("Using fallback logger due to missing SLF4J implementation.");
      logger.warn("Please setup a logging framework to use JDA.");
      logger.warn("You can use our logging setup guide https://jda.wiki/setup/logging/");
      logger.warn("To disable the fallback logger, add the slf4j-nop dependency or use JDALogger.setFallbackLoggerEnabled(false)");
   }

   private static Logger newFallbackLogger(String name) {
      if (!disableFallback && fallbackLoggerConstructor != null) {
         try {
            synchronized(LOGS) {
               if (LOGS.containsKey(name)) {
                  return (Logger)LOGS.get(name);
               } else {
                  Logger logger = fallbackLoggerConstructor.invoke(name);
                  boolean isFirstFallback = LOGS.isEmpty();
                  LOGS.put(name, logger);
                  if (isFirstFallback) {
                     printFallbackWarning();
                  }

                  return logger;
               }
            }
         } catch (Throwable e) {
            throw new IllegalStateException("Failed to initialize fallback logger", e);
         }
      } else {
         return NOPLogger.NOP_LOGGER;
      }
   }

   public static Object getLazyString(final LazyEvaluation lazyLambda) {
      return new Object() {
         public String toString() {
            try {
               return lazyLambda.getString();
            } catch (Exception ex) {
               StringWriter sw = new StringWriter();
               ex.printStackTrace(new PrintWriter(sw));
               return "Error while evaluating lazy String... " + sw;
            }
         }
      };
   }

   static {
      boolean hasLoggerImpl = false;

      try {
         Class<?> provider = Class.forName("org.slf4j.spi.SLF4JServiceProvider");
         hasLoggerImpl = ServiceLoader.load(provider).iterator().hasNext();
      } catch (ClassNotFoundException var5) {
         disableFallback = true;
      }

      SLF4J_ENABLED = hasLoggerImpl;
      MethodHandle constructor = null;

      try {
         MethodHandles.Lookup lookup = MethodHandles.publicLookup();
         Class<?> fallbackLoggerClass = Class.forName("net.dv8tion.jda.internal.utils.FallbackLogger");
         constructor = lookup.findConstructor(fallbackLoggerClass, MethodType.methodType(Void.TYPE, String.class));
      } catch (ExceptionInInitializerError | IllegalAccessException | NoClassDefFoundError | NoSuchMethodException | ClassNotFoundException var4) {
      }

      fallbackLoggerConstructor = constructor;
      LOGS = new HashMap();
   }

   @FunctionalInterface
   public interface LazyEvaluation {
      String getString() throws Exception;
   }
}
