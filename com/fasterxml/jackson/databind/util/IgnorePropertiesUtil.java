package com.fasterxml.jackson.databind.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IgnorePropertiesUtil {
   public static boolean shouldIgnore(Object value, Collection<String> toIgnore, Collection<String> toInclude) {
      if (toIgnore == null && toInclude == null) {
         return false;
      } else if (toInclude == null) {
         return toIgnore.contains(value);
      } else if (toIgnore == null) {
         return !toInclude.contains(value);
      } else {
         return !toInclude.contains(value) || toIgnore.contains(value);
      }
   }

   public static Checker buildCheckerIfNeeded(Set<String> toIgnore, Set<String> toInclude) {
      return toInclude != null || toIgnore != null && !toIgnore.isEmpty() ? IgnorePropertiesUtil.Checker.construct(toIgnore, toInclude) : null;
   }

   public static Set<String> combineNamesToInclude(Set<String> prevToInclude, Set<String> newToInclude) {
      if (prevToInclude == null) {
         return newToInclude;
      } else if (newToInclude == null) {
         return prevToInclude;
      } else {
         Set<String> result = new HashSet();

         for(String prop : newToInclude) {
            if (prevToInclude.contains(prop)) {
               result.add(prop);
            }
         }

         return result;
      }
   }

   public static final class Checker implements Serializable {
      private static final long serialVersionUID = 1L;
      private final Set<String> _toIgnore;
      private final Set<String> _toInclude;

      private Checker(Set<String> toIgnore, Set<String> toInclude) {
         if (toIgnore == null) {
            toIgnore = Collections.emptySet();
         }

         this._toIgnore = toIgnore;
         this._toInclude = toInclude;
      }

      public static Checker construct(Set<String> toIgnore, Set<String> toInclude) {
         return new Checker(toIgnore, toInclude);
      }

      public boolean shouldIgnore(Object propertyName) {
         return this._toInclude != null && !this._toInclude.contains(propertyName) || this._toIgnore.contains(propertyName);
      }
   }
}
