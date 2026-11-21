package org.slf4j.helpers;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class SubstituteServiceProvider implements SLF4JServiceProvider {
   private final SubstituteLoggerFactory loggerFactory = new SubstituteLoggerFactory();
   private final IMarkerFactory markerFactory = new BasicMarkerFactory();
   private final MDCAdapter mdcAdapter = new BasicMDCAdapter();

   public ILoggerFactory getLoggerFactory() {
      return this.loggerFactory;
   }

   public SubstituteLoggerFactory getSubstituteLoggerFactory() {
      return this.loggerFactory;
   }

   public IMarkerFactory getMarkerFactory() {
      return this.markerFactory;
   }

   public MDCAdapter getMDCAdapter() {
      return this.mdcAdapter;
   }

   public String getRequestedApiVersion() {
      throw new UnsupportedOperationException();
   }

   public void initialize() {
   }
}
