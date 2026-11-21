package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Collection;
import java.util.Map;

public interface TShortObjectMap<V> {
   short getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(short var1);

   boolean containsValue(Object var1);

   V get(short var1);

   V put(short var1, V var2);

   V putIfAbsent(short var1, V var2);

   V remove(short var1);

   void putAll(Map<? extends Short, ? extends V> var1);

   void putAll(TShortObjectMap<? extends V> var1);

   void clear();

   TShortSet keySet();

   short[] keys();

   short[] keys(short[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TShortObjectIterator<V> iterator();

   boolean forEachKey(TShortProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TShortObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TShortObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
