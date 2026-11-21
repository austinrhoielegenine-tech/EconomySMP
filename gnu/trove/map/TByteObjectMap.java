package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.util.Collection;
import java.util.Map;

public interface TByteObjectMap<V> {
   byte getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(byte var1);

   boolean containsValue(Object var1);

   V get(byte var1);

   V put(byte var1, V var2);

   V putIfAbsent(byte var1, V var2);

   V remove(byte var1);

   void putAll(Map<? extends Byte, ? extends V> var1);

   void putAll(TByteObjectMap<? extends V> var1);

   void clear();

   TByteSet keySet();

   byte[] keys();

   byte[] keys(byte[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TByteObjectIterator<V> iterator();

   boolean forEachKey(TByteProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TByteObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TByteObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
