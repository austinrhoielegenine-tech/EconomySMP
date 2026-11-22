package com.austynzdevv.nyrethacore.teams.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, Sort> SORT = new ConcurrentHashMap();
   public static final Map<UUID, UUID> TARGET = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, PAGE, SORT, TARGET);
   }

   public static Sort next(Sort current) {
      Sort var10000;
      switch (current.ordinal()) {
         case 0 -> var10000 = MenuSession.Sort.ALPHABETICALLY;
         case 1 -> var10000 = MenuSession.Sort.JOIN_DATE;
         case 2 -> var10000 = MenuSession.Sort.ONLINE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static enum View {
      MAIN,
      MEMBER,
      SEARCH;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{MAIN, MEMBER, SEARCH};
      }
   }

   public static enum Sort {
      ONLINE,
      ALPHABETICALLY,
      JOIN_DATE;

      // $FF: synthetic method
      private static Sort[] $values() {
         return new Sort[]{ONLINE, ALPHABETICALLY, JOIN_DATE};
      }
   }
}
