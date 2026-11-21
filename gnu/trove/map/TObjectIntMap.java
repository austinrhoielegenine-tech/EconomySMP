package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectIntMap<K> {
   int getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(int var1);

   int get(Object var1);

   int put(K var1, int var2);

   int putIfAbsent(K var1, int var2);

   int remove(Object var1);

   void putAll(Map<? extends K, ? extends Integer> var1);

   void putAll(TObjectIntMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TIntCollection valueCollection();

   int[] values();

   int[] values(int[] var1);

   TObjectIntIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, int var2);

   int adjustOrPutValue(K var1, int var2, int var3);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TIntProcedure var1);

   boolean forEachEntry(TObjectIntProcedure<? super K> var1);

   void transformValues(TIntFunction var1);

   boolean retainEntries(TObjectIntProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
