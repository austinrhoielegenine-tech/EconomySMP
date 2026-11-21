package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectFloatMap<K> {
   float getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(float var1);

   float get(Object var1);

   float put(K var1, float var2);

   float putIfAbsent(K var1, float var2);

   float remove(Object var1);

   void putAll(Map<? extends K, ? extends Float> var1);

   void putAll(TObjectFloatMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TFloatCollection valueCollection();

   float[] values();

   float[] values(float[] var1);

   TObjectFloatIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, float var2);

   float adjustOrPutValue(K var1, float var2, float var3);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TFloatProcedure var1);

   boolean forEachEntry(TObjectFloatProcedure<? super K> var1);

   void transformValues(TFloatFunction var1);

   boolean retainEntries(TObjectFloatProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
