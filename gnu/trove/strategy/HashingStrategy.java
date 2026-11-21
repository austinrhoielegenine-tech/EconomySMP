package gnu.trove.strategy;

import java.io.Serializable;

public interface HashingStrategy<T> extends Serializable {
   long serialVersionUID = 5674097166776615540L;

   int computeHashCode(T var1);

   boolean equals(T var1, T var2);
}
