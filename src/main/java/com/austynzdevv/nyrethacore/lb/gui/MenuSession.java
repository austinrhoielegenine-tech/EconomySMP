package com.austynzdevv.nyrethacore.lb.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.lb.StatsManager;
import com.austynzdevv.nyrethacore.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, StatsManager.Board> BOARD = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, BOARD, PAGE);
   }

   public static enum View {
      MAIN,
      BOARD,
      SEARCH;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{MAIN, BOARD, SEARCH};
      }
   }
}
