package org.slf4j.spi;

import org.slf4j.IMarkerFactory;

/** @deprecated */
public interface MarkerFactoryBinder {
   IMarkerFactory getMarkerFactory();

   String getMarkerFactoryClassStr();
}
