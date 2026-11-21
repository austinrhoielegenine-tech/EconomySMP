package com.austynzdevv.economysmp.reports.listener;

import java.util.UUID;
import com.austynzdevv.economysmp.reports.EconomyReports;
import com.austynzdevv.economysmp.reports.gui.ManageReportGUI;
import com.austynzdevv.economysmp.reports.gui.ReportsGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
   private final EconomyReports plugin;

   public GUIListener(EconomyReports plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         String title = event.getView().getTitle();
         if (title.equals("§bReports") || title.equals("§bManage Player")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
               if (title.equals("§bReports")) {
                  if (item.getType() == Material.PLAYER_HEAD) {
                     UUID target = ReportsGUI.getTarget(player, event.getSlot());
                     if (target != null) {
                        ManageReportGUI.open(player, target, this.plugin);
                     }
                  }

               } else {
                  if (title.equals("§bManage Player")) {
                     UUID targetUUID = ReportsGUI.getSelected(player);
                     if (targetUUID == null) {
                        return;
                     }

                     if (item.getType() == Material.NETHERITE_AXE) {
                        for(String cmd : this.plugin.getConfig().getStringList("commands.ban")) {
                           Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", Bukkit.getOfflinePlayer(targetUUID).getName()));
                        }

                        player.closeInventory();
                     }

                     if (item.getType() == Material.PAPER) {
                        for(String cmd : this.plugin.getConfig().getStringList("commands.mute")) {
                           Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", Bukkit.getOfflinePlayer(targetUUID).getName()));
                        }

                        player.closeInventory();
                     }

                     if (item.getType() == Material.ENDER_PEARL) {
                        if (!player.hasPermission("economysmpcore.system.reports.staff")) {
                           player.sendMessage("§cYou don't have permission to teleport.");
                           return;
                        }

                        Player target = Bukkit.getPlayer(targetUUID);
                        if (target == null) {
                           player.sendMessage(this.plugin.getConfig().getString("messages.player-offline").replace("&", "§"));
                           return;
                        }

                        player.teleport(target);
                     }
                  }

               }
            }
         }
      }
   }
}
