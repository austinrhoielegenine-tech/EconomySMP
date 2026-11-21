package com.austynzdevv.economysmp.bounties.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.bounties.BountyManager;
import com.austynzdevv.economysmp.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, BountyManager.Sort> SORT = new ConcurrentHashMap();
   public static final Map<UUID, UUID> TARGET = new ConcurrentHashMap();
   public static final Map<UUID, Double> AMOUNT = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, PAGE, SORT, TARGET, AMOUNT);
   }

   public static enum View {
      MAIN,
      CONFIRM,
      ADMIN,
      SEARCH;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{MAIN, CONFIRM, ADMIN, SEARCH};
      }
   }
}
