package me.xyr0.economysmpcore.util;

import me.xyr0.economysmpcore.SettingsModule;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class HotbarUtil {
   private HotbarUtil() {
   }

   public static void send(Player player, Component component) {
      if (player != null && component != null) {
         try {
            SettingsModule settings = SettingsModule.get();
            if (settings != null && settings.isHotbarOff(player.getUniqueId())) {
               return;
            }
         } catch (Exception var3) {
         }

         player.sendActionBar(component);
      }
   }
}
