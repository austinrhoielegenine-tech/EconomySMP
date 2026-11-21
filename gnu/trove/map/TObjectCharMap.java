package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectCharMap<K> {
   char getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(char var1);

   char get(Object var1);

   char put(K var1, char var2);

   char putIfAbsent(K var1, char var2);

   char remove(Object var1);

   void putAll(Map<? extends K, ? extends Character> var1);

   void putAll(TObjectCharMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TCharCollection valueCollection();

   char[] values();

   char[] values(char[] var1);

   TObjectCharIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, char var2);

   char adjustOrPutValue(K var1, char var2, char var3);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TCharProcedure var1);

   boolean forEachEntry(TObjectCharProcedure<? super K> var1);

   void transformValues(TCharFunction var1);

   boolean retainEntries(TObjectCharProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
