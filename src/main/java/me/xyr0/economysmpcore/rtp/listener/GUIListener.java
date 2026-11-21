package me.xyr0.economysmpcore.rtp.listener;

import me.xyr0.economysmpcore.rtp.AstralRTP;
import me.xyr0.economysmpcore.rtp.RTPCountdown;
import me.xyr0.economysmpcore.rtp.gui.RTPGUI;
import me.xyr0.economysmpcore.rtp.util.ConfigUtil;
import me.xyr0.economysmpcore.rtp.util.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {
   private final AstralRTP plugin;

   public GUIListener(AstralRTP plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (event.getInventory().getHolder() instanceof RTPGUI.Holder) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null) {
               int slot = event.getSlot();
               FileConfiguration gui = ConfigUtil.getFile(this.plugin, "guis.yml");
               boolean nested = gui.contains("main-menu");
               String itemsPath = nested ? "main-menu.items" : "worlds";
               ConfigurationSection section = gui.getConfigurationSection(itemsPath);
               if (section != null) {
                  for(String key : section.getKeys(false)) {
                     String path = itemsPath + "." + key;
                     if (gui.getInt(path + ".slot") == slot) {
                        String world = gui.getString(path + ".world");
                        if (world == null || world.isEmpty()) {
                           String cmd = gui.getString(path + ".command", "");
                           String[] parts = cmd.trim().split("\\s+");
                           world = parts.length >= 2 ? parts[1] : key;
                        }

                        if (RTPCountdown.isRunning(player)) {
                           player.closeInventory();
                           MessageUtil.send(player, "errors.already-teleporting");
                           return;
                        }

                        player.closeInventory();
                        (new RTPCountdown(this.plugin, player, world)).start();
                        return;
                     }
                  }

               }
            }
         }
      }
   }
}
