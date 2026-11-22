package com.austynzdevv.nyrethacore.baltop.gui;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, String> SEARCH = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> CHAT_SEARCH = new ConcurrentHashMap();
   public static final Map<UUID, Boolean> SIGN_SEARCH = new ConcurrentHashMap();
   public static final Set<UUID> SWITCHING = ConcurrentHashMap.newKeySet();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, PAGE, SEARCH, CHAT_SEARCH, SIGN_SEARCH);
      SessionUtil.clear(id, SWITCHING);
   }

   public static enum View {
      MAIN;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{MAIN};
      }
   }
}
