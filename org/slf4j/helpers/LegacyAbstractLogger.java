package org.slf4j.helpers;

import org.slf4j.Marker;

public abstract class LegacyAbstractLogger extends AbstractLogger {
   private static final long serialVersionUID = -7041884104854048950L;

   public boolean isTraceEnabled(Marker marker) {
      return this.isTraceEnabled();
   }

   public boolean isDebugEnabled(Marker marker) {
      return this.isDebugEnabled();
   }

   public boolean isInfoEnabled(Marker marker) {
      return this.isInfoEnabled();
   }

   public boolean isWarnEnabled(Marker marker) {
      return this.isWarnEnabled();
   }

   public boolean isErrorEnabled(Marker marker) {
      return this.isErrorEnabled();
   }
}
