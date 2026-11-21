package me.xyr0.economysmpcore.billford.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, Boolean> OPEN = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, OPEN);
   }
}
