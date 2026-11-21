package me.xyr0.economysmpcore.media.listener;

import me.xyr0.economysmpcore.media.AstralMedia;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         AstralMedia plugin = AstralMedia.getInstance();
         if (plugin != null) {
            String cfgTitle = ColorUtil.color(plugin.getConfig().getString("media-gui.title", "&8media"));

            String viewTitle;
            try {
               viewTitle = event.getView().getTitle();
            } catch (Exception var11) {
               viewTitle = "";
            }

            String plainView = viewTitle.replace("§", "").replace("&", "").toLowerCase();
            String plainCfg = cfgTitle.replace("§", "").replace("&", "").toLowerCase();
            if (plainView.contains("media") || plainView.equals(plainCfg) || viewTitle.equals(cfgTitle)) {
               event.setCancelled(true);
               ItemStack clicked = event.getCurrentItem();
               if (clicked != null && !clicked.getType().isAir()) {
                  int slot = plugin.getConfig().getInt("media-gui.item.slot", 13);
                  if (event.getRawSlot() == slot) {
                     player.closeInventory();
                     String cmd = plugin.getConfig().getString("media-gui.item.click-command", "discord");
                     if (cmd.startsWith("/")) {
                        cmd = cmd.substring(1);
                     }

                     player.performCommand(cmd);
                  }
               }
            }
         }
      }
   }
}
