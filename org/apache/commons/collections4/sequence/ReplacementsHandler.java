package org.apache.commons.collections4.sequence;

import java.util.List;

@FunctionalInterface
public interface ReplacementsHandler<T> {
   void handleReplacement(int var1, List<T> var2, List<T> var3);
}
