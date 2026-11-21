package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;

public interface TMap<K, V> extends Map<K, V> {
   V putIfAbsent(K var1, V var2);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TObjectProcedure<? super V> var1);

   boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> var1);

   boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> var1);

   void transformValues(TObjectFunction<V, V> var1);
}
