package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectShortMap<K> {
   short getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(short var1);

   short get(Object var1);

   short put(K var1, short var2);

   short putIfAbsent(K var1, short var2);

   short remove(Object var1);

   void putAll(Map<? extends K, ? extends Short> var1);

   void putAll(TObjectShortMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TShortCollection valueCollection();

   short[] values();

   short[] values(short[] var1);

   TObjectShortIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, short var2);

   short adjustOrPutValue(K var1, short var2, short var3);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TShortProcedure var1);

   boolean forEachEntry(TObjectShortProcedure<? super K> var1);

   void transformValues(TShortFunction var1);

   boolean retainEntries(TObjectShortProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
