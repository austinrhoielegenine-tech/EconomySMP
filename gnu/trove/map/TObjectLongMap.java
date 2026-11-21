package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectLongMap<K> {
   long getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(long var1);

   long get(Object var1);

   long put(K var1, long var2);

   long putIfAbsent(K var1, long var2);

   long remove(Object var1);

   void putAll(Map<? extends K, ? extends Long> var1);

   void putAll(TObjectLongMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TLongCollection valueCollection();

   long[] values();

   long[] values(long[] var1);

   TObjectLongIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, long var2);

   long adjustOrPutValue(K var1, long var2, long var4);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TLongProcedure var1);

   boolean forEachEntry(TObjectLongProcedure<? super K> var1);

   void transformValues(TLongFunction var1);

   boolean retainEntries(TObjectLongProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
