package me.xyr0.economysmpcore.crates.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.crates.Reward;
import me.xyr0.economysmpcore.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, Boolean> OPEN = new ConcurrentHashMap();
   public static final Map<UUID, Mode> MODE = new ConcurrentHashMap();
   public static final Map<UUID, String> CRATE = new ConcurrentHashMap();
   public static final Map<UUID, Reward> PENDING = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, OPEN, MODE, CRATE, PENDING);
   }

   public static enum Mode {
      PREVIEW,
      VIEW,
      CONFIRM,
      ROLLING;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{PREVIEW, VIEW, CONFIRM, ROLLING};
      }
   }
}
