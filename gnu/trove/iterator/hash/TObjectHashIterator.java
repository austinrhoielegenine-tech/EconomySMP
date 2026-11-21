package gnu.trove.iterator.hash;

import gnu.trove.impl.hash.THashIterator;
import gnu.trove.impl.hash.TObjectHash;

public class TObjectHashIterator<E> extends THashIterator<E> {
   protected final TObjectHash _objectHash;

   public TObjectHashIterator(TObjectHash<E> hash) {
      super(hash);
      this._objectHash = hash;
   }

   protected E objectAtIndex(int index) {
      Object obj = this._objectHash._set[index];
      return (E)(obj != TObjectHash.FREE && obj != TObjectHash.REMOVED ? obj : null);
   }
}
