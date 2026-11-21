package com.fasterxml.jackson.core.util;

import java.io.Serializable;

public final class JacksonFeatureSet<F extends JacksonFeature> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected int _enabled;

   protected JacksonFeatureSet(int bitmask) {
      this._enabled = bitmask;
   }

   public static <F extends JacksonFeature> JacksonFeatureSet<F> fromDefaults(F[] allFeatures) {
      if (allFeatures.length > 31) {
         String desc = allFeatures[0].getClass().getName();
         throw new IllegalArgumentException(String.format("Can not use type `%s` with JacksonFeatureSet: too many entries (%d > 31)", desc, allFeatures.length));
      } else {
         int flags = 0;

         for(F f : allFeatures) {
            if (f.enabledByDefault()) {
               flags |= f.getMask();
            }
         }

         return new JacksonFeatureSet<F>(flags);
      }
   }

   public static <F extends JacksonFeature> JacksonFeatureSet<F> fromBitmask(int bitmask) {
      return new JacksonFeatureSet<F>(bitmask);
   }

   public JacksonFeatureSet<F> with(F feature) {
      int newMask = this._enabled | feature.getMask();
      return newMask == this._enabled ? this : new JacksonFeatureSet(newMask);
   }

   public JacksonFeatureSet<F> without(F feature) {
      int newMask = this._enabled & ~feature.getMask();
      return newMask == this._enabled ? this : new JacksonFeatureSet(newMask);
   }

   public boolean isEnabled(F feature) {
      return (feature.getMask() & this._enabled) != 0;
   }

   public int asBitmask() {
      return this._enabled;
   }
}
