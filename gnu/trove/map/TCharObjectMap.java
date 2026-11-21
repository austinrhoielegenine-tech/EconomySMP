package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.util.Collection;
import java.util.Map;

public interface TCharObjectMap<V> {
   char getNoEntryKey();

   int size();

   boolean isEmpty();

   boolean containsKey(char var1);

   boolean containsValue(Object var1);

   V get(char var1);

   V put(char var1, V var2);

   V putIfAbsent(char var1, V var2);

   V remove(char var1);

   void putAll(Map<? extends Character, ? extends V> var1);

   void putAll(TCharObjectMap<? extends V> var1);

   void clear();

   TCharSet keySet();

   char[] keys();

   char[] keys(char[] var1);

   Collection<V> valueCollection();

   Object[] values();

   V[] values(V[] var1);

   TCharObjectIterator<V> iterator();

   boolean forEachKey(TCharProcedure var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TCharObjectProcedure<? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);

   boolean retainEntries(TCharObjectProcedure<? super V> var1);

   boolean equals(Object var1);

   int hashCode();
}
