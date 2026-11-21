package org.slf4j.spi;

import org.slf4j.ILoggerFactory;

/** @deprecated */
public interface LoggerFactoryBinder {
   ILoggerFactory getLoggerFactory();

   String getLoggerFactoryClassStr();
}
