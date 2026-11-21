package com.austynzdevv.economysmp.warps.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, Boolean> OPEN = new ConcurrentHashMap();
   public static final Map<UUID, Type> MODE = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, OPEN, MODE, PAGE);
   }

   public static enum Type {
      SPAWN,
      AFK;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{SPAWN, AFK};
      }
   }
}
