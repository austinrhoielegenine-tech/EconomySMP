package com.austynzdevv.economysmp.billford.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, Boolean> OPEN = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, OPEN);
   }
}
