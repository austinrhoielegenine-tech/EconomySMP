package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;

public class NOPLogger extends NamedLoggerBase implements Logger {
   private static final long serialVersionUID = -517220405410904473L;
   public static final NOPLogger NOP_LOGGER = new NOPLogger();

   protected NOPLogger() {
   }

   public String getName() {
      return "NOP";
   }

   public final boolean isTraceEnabled() {
      return false;
   }

   public final void trace(String msg) {
   }

   public final void trace(String format, Object arg) {
   }

   public final void trace(String format, Object arg1, Object arg2) {
   }

   public final void trace(String format, Object... argArray) {
   }

   public final void trace(String msg, Throwable t) {
   }

   public final boolean isDebugEnabled() {
      return false;
   }

   public final void debug(String msg) {
   }

   public final void debug(String format, Object arg) {
   }

   public final void debug(String format, Object arg1, Object arg2) {
   }

   public final void debug(String format, Object... argArray) {
   }

   public final void debug(String msg, Throwable t) {
   }

   public final boolean isInfoEnabled() {
      return false;
   }

   public final void info(String msg) {
   }

   public final void info(String format, Object arg1) {
   }

   public final void info(String format, Object arg1, Object arg2) {
   }

   public final void info(String format, Object... argArray) {
   }

   public final void info(String msg, Throwable t) {
   }

   public final boolean isWarnEnabled() {
      return false;
   }

   public final void warn(String msg) {
   }

   public final void warn(String format, Object arg1) {
   }

   public final void warn(String format, Object arg1, Object arg2) {
   }

   public final void warn(String format, Object... argArray) {
   }

   public final void warn(String msg, Throwable t) {
   }

   public final boolean isErrorEnabled() {
      return false;
   }

   public final void error(String msg) {
   }

   public final void error(String format, Object arg1) {
   }

   public final void error(String format, Object arg1, Object arg2) {
   }

   public final void error(String format, Object... argArray) {
   }

   public final void error(String msg, Throwable t) {
   }

   public final boolean isTraceEnabled(Marker marker) {
      return false;
   }

   public final void trace(Marker marker, String msg) {
   }

   public final void trace(Marker marker, String format, Object arg) {
   }

   public final void trace(Marker marker, String format, Object arg1, Object arg2) {
   }

   public final void trace(Marker marker, String format, Object... argArray) {
   }

   public final void trace(Marker marker, String msg, Throwable t) {
   }

   public final boolean isDebugEnabled(Marker marker) {
      return false;
   }

   public final void debug(Marker marker, String msg) {
   }

   public final void debug(Marker marker, String format, Object arg) {
   }

   public final void debug(Marker marker, String format, Object arg1, Object arg2) {
   }

   public final void debug(Marker marker, String format, Object... arguments) {
   }

   public final void debug(Marker marker, String msg, Throwable t) {
   }

   public boolean isInfoEnabled(Marker marker) {
      return false;
   }

   public final void info(Marker marker, String msg) {
   }

   public final void info(Marker marker, String format, Object arg) {
   }

   public final void info(Marker marker, String format, Object arg1, Object arg2) {
   }

   public final void info(Marker marker, String format, Object... arguments) {
   }

   public final void info(Marker marker, String msg, Throwable t) {
   }

   public final boolean isWarnEnabled(Marker marker) {
      return false;
   }

   public final void warn(Marker marker, String msg) {
   }

   public final void warn(Marker marker, String format, Object arg) {
   }

   public final void warn(Marker marker, String format, Object arg1, Object arg2) {
   }

   public final void warn(Marker marker, String format, Object... arguments) {
   }

   public final void warn(Marker marker, String msg, Throwable t) {
   }

   public final boolean isErrorEnabled(Marker marker) {
      return false;
   }

   public final void error(Marker marker, String msg) {
   }

   public final void error(Marker marker, String format, Object arg) {
   }

   public final void error(Marker marker, String format, Object arg1, Object arg2) {
   }

   public final void error(Marker marker, String format, Object... arguments) {
   }

   public final void error(Marker marker, String msg, Throwable t) {
   }
}
