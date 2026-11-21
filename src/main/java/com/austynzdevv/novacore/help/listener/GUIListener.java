package com.austynzdevv.novacore.help.listener;

import com.austynzdevv.novacore.help.EconomyHelp;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
   private final EconomyHelp plugin;

   public GUIListener(EconomyHelp plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         String var14 = this.plugin.getConfig().getString("title", "&8Economy Info");
         if (event.getView().getTitle().equals(ColorUtil.color(var14))) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null && item.hasItemMeta()) {
               int slot = event.getSlot();
               if (this.plugin.getConfig().getConfigurationSection("items") != null) {
                  for(String key : this.plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
                     String path = "items." + key;
                     int configSlot = this.plugin.getConfig().getInt(path + ".slot");
                     if (slot == configSlot) {
                        String command = this.plugin.getConfig().getString(path + ".cmd");
                        if (command != null && !command.isEmpty()) {
                           player.closeInventory();
                           player.performCommand(command);
                        }

                        String sound = this.plugin.getConfig().getString("sounds.gui-click.sound");
                        if (sound != null) {
                           try {
                              SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.replace("minecraft:", "").toUpperCase()), 1.0F, 1.0F);
                           } catch (Exception var13) {
                           }
                        }
                        break;
                     }
                  }

               }
            }
         }
      }
   }
}
