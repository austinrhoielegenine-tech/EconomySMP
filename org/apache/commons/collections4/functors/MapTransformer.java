package org.apache.commons.collections4.functors;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections4.Transformer;

public final class MapTransformer<T, R> implements Transformer<T, R>, Serializable {
   private static final long serialVersionUID = 862391807045468939L;
   private final Map<? super T, ? extends R> iMap;

   public static <I, O> Transformer<I, O> mapTransformer(Map<? super I, ? extends O> map) {
      return (Transformer<I, O>)(map == null ? ConstantTransformer.nullTransformer() : new MapTransformer(map));
   }

   private MapTransformer(Map<? super T, ? extends R> map) {
      this.iMap = map;
   }

   public Map<? super T, ? extends R> getMap() {
      return this.iMap;
   }

   public R transform(T input) {
      return (R)this.iMap.get(input);
   }
}
