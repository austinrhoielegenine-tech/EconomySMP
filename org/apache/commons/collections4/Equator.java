package org.apache.commons.collections4;

public interface Equator<T> {
   boolean equate(T var1, T var2);

   int hash(T var1);
}
