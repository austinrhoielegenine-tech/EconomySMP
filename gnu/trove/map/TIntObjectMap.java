package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.util.Collection;
import java.util.Map;

public interface TIntObjectMap<V> {
   int getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(int var1);

   boolean containsValue(Object var1);

   V get(int var1);

   V put(int var1, V var2);

   V putIfAbsent(int var1, V var2);

   V remove(int var1);

   void putAll(Map<? extends Integer, ? extends V> var1);

   void putAll(TIntObjectMap<? extends V> var1);

   void clear();

   TIntSet keySet();

   int[] keys();

   int[] keys(int[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TIntObjectIterator<V> iterator();

   boolean forEachKey(TIntProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TIntObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TIntObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
