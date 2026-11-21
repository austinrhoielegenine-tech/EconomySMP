package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectByteMap<K> {
   byte getNoEntryValue();

   int size();

   boolean isEmpty();

   boolean containsKey(Object var1);

   boolean containsValue(byte var1);

   byte get(Object var1);

   byte put(K var1, byte var2);

   byte putIfAbsent(K var1, byte var2);

   byte remove(Object var1);

   void putAll(Map<? extends K, ? extends Byte> var1);

   void putAll(TObjectByteMap<? extends K> var1);

   void clear();

   Set<K> keySet();

   Object[] keys();

   K[] keys(K[] var1);

   TByteCollection valueCollection();

   byte[] values();

   byte[] values(byte[] var1);

   TObjectByteIterator<K> iterator();

   boolean increment(K var1);

   boolean adjustValue(K var1, byte var2);

   byte adjustOrPutValue(K var1, byte var2, byte var3);

   boolean forEachKey(TObjectProcedure<? super K> var1);

   boolean forEachValue(TByteProcedure var1);

   boolean forEachEntry(TObjectByteProcedure<? super K> var1);

   void transformValues(TByteFunction var1);

   boolean retainEntries(TObjectByteProcedure<? super K> var1);

   boolean equals(Object var1);

   int hashCode();
}
