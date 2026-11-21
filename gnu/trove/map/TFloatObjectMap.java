package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Collection;
import java.util.Map;

public interface TFloatObjectMap<V> {
   float getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(float var1);

   boolean containsValue(Object var1);

   V get(float var1);

   V put(float var1, V var2);

   V putIfAbsent(float var1, V var2);

   V remove(float var1);

   void putAll(Map<? extends Float, ? extends V> var1);

   void putAll(TFloatObjectMap<? extends V> var1);

   void clear();

   TFloatSet keySet();

   float[] keys();

   float[] keys(float[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TFloatObjectIterator<V> iterator();

   boolean forEachKey(TFloatProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TFloatObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TFloatObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
