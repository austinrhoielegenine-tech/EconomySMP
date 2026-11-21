package me.xyr0.astralsmpcore.rules.listener;

import me.xyr0.astralsmpcore.rules.AstralRules;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
   private final AstralRules plugin;

   public GUIListener(AstralRules plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (event.getView().title().equals(ColorUtil.parse(this.plugin.getConfig().getString("gui.title")))) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
               if (this.plugin.getConfig().getBoolean("sound.enabled", true)) {
                  try {
                     Sound sound = Sound.valueOf(this.plugin.getConfig().getString("sound.type", "UI_BUTTON_CLICK"));
                     SoundUtil.play(player, player.getLocation(), sound, (float)this.plugin.getConfig().getDouble("sound.volume", (double)1.0F), (float)this.plugin.getConfig().getDouble("sound.pitch", (double)1.0F));
                  } catch (Exception var4) {
                  }
               }

               player.closeInventory();
            }
         }
      }
   }
}
