package me.xyr0.economysmpcore.util;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SessionUtil {
   private SessionUtil() {
   }

   @SafeVarargs
   public static void clear(UUID id, Map<UUID, ?>... maps) {
      if (id != null && maps != null) {
         for(Map<UUID, ?> map : maps) {
            if (map != null) {
               map.remove(id);
            }
         }

      }
   }

   public static void clear(UUID id, Set<UUID> set) {
      if (id != null && set != null) {
         set.remove(id);
      }

   }
}
