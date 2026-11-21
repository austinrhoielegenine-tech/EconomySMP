package me.xyr0.economysmpcore.punishment2.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import me.xyr0.economysmpcore.punishment2.AstralPunishment;
import me.xyr0.economysmpcore.punishment2.Punishment;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class HistoryGUI {
   private final AstralPunishment plugin;

   public HistoryGUI(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   public void open(Player viewer, OfflinePlayer target, int page) {
      Inventory inventory = Bukkit.createInventory(new HistoryHolder(), 54, ColorUtil.color("&cPunishment History"));
      List<Punishment> history = this.plugin.getHistoryManager().getVisibleHistory(target.getUniqueId());
      int start = page * 45;
      int slot = 0;

      for(int i = start; i < history.size() && slot < 45; ++i) {
         Punishment punishment = (Punishment)history.get(i);
         ItemStack item = new ItemStack(Material.PLAYER_HEAD);
         SkullMeta meta = (SkullMeta)item.getItemMeta();
         meta.setOwningPlayer(target);
         meta.setDisplayName(ColorUtil.color("&c" + punishment.getType() + " Punishment"));
         List<String> lore = new ArrayList();
         lore.add(ColorUtil.color("&7Staff: &f" + punishment.getStaff()));
         lore.add(ColorUtil.color("&7Reason: &f" + punishment.getReason()));
         lore.add(ColorUtil.color("&7Duration: &f" + punishment.getDuration()));
         lore.add(ColorUtil.color("&7Offense: &f" + punishment.getOffense()));
         SimpleDateFormat var10001 = new SimpleDateFormat("yyyy-MM-dd");
         Date var10002 = new Date(punishment.getDate());
         lore.add(ColorUtil.color("&7Date: &f" + var10001.format(var10002)));
         if (!punishment.getBanId().equalsIgnoreCase("NONE")) {
            lore.add(ColorUtil.color("&7Ban ID: &f" + punishment.getBanId()));
         }

         meta.setLore(lore);
         item.setItemMeta(meta);
         inventory.setItem(slot, item);
         ++slot;
      }

      if (history.size() > start + 45) {
         ItemStack next = new ItemStack(Material.ARROW);
         ItemMeta meta = next.getItemMeta();
         meta.setDisplayName(ColorUtil.color("&aNext Page"));
         next.setItemMeta(meta);
         inventory.setItem(53, next);
      }

      viewer.openInventory(inventory);
   }

   public static class HistoryHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }
}
