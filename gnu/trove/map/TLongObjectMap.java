package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.util.Collection;
import java.util.Map;

public interface TLongObjectMap<V> {
   long getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(long var1);

   boolean containsValue(Object var1);

   V get(long var1);

   V put(long var1, V var3);

   V putIfAbsent(long var1, V var3);

   V remove(long var1);

   void putAll(Map<? extends Long, ? extends V> var1);

   void putAll(TLongObjectMap<? extends V> var1);

   void clear();

   TLongSet keySet();

   long[] keys();

   long[] keys(long[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TLongObjectIterator<V> iterator();

   boolean forEachKey(TLongProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TLongObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TLongObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
