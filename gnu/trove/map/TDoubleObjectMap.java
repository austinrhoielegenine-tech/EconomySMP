package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Collection;
import java.util.Map;

public interface TDoubleObjectMap<V> {
   double getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(double var1);

   boolean containsValue(Object var1);

   V get(double var1);

   V put(double var1, V var3);

   V putIfAbsent(double var1, V var3);

   V remove(double var1);

   void putAll(Map<? extends Double, ? extends V> var1);

   void putAll(TDoubleObjectMap<? extends V> var1);

   void clear();

   TDoubleSet keySet();

   double[] keys();

   double[] keys(double[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TDoubleObjectIterator<V> iterator();

   boolean forEachKey(TDoubleProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TDoubleObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TDoubleObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
