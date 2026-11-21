package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectDoubleMap<K> {
   double getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(double var1);

   double get(Object var1);

   double put(K var1, double var2);

   double putIfAbsent(K var1, double var2);

   double remove(Object var1);

   void putAll(Map<? extends K, ? extends Double> var1);

   void putAll(TObjectDoubleMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TDoubleCollection valueCollection();

   double[] values();

   double[] values(double[] var1);

   TObjectDoubleIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, double var2);

   double adjustOrPutValue(K var1, double var2, double var4);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TDoubleProcedure var1);

   boolean forEachEntry(TObjectDoubleProcedure<? super K> var1);

   void transformValues(TDoubleFunction var1);

   boolean retainEntries(TObjectDoubleProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
