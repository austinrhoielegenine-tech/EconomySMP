package org.slf4j.helpers;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class NOP_FallbackServiceProvider implements SLF4JServiceProvider {
   public static String REQUESTED_API_VERSION = "2.0.99";
   private final ILoggerFactory loggerFactory = new NOPLoggerFactory();
   private final IMarkerFactory markerFactory = new BasicMarkerFactory();
   private final MDCAdapter mdcAdapter = new NOPMDCAdapter();

   public ILoggerFactory getLoggerFactory() {
      return this.loggerFactory;
   }

   public IMarkerFactory getMarkerFactory() {
      return this.markerFactory;
   }

   public MDCAdapter getMDCAdapter() {
      return this.mdcAdapter;
   }

   public String getRequestedApiVersion() {
      return REQUESTED_API_VERSION;
   }

   public void initialize() {
   }
}
