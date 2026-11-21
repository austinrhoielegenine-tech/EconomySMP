package me.xyr0.economysmpcore.order.gui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.xyr0.economysmpcore.order.AstralOrder;
import me.xyr0.economysmpcore.order.DeliveryRecord;
import me.xyr0.economysmpcore.order.Order;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.ItemUtil;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class AdminMenus {
   private AdminMenus() {
   }

   private static FileConfiguration yaml() {
      File file = new File(new File(AstralOrder.get().getDataFolder(), "gui"), "admin-gui.yml");
      if (!file.exists()) {
         try {
            AstralOrder.get().saveResource("gui/admin-gui.yml", false);
         } catch (Exception var2) {
         }
      }

      return YamlConfiguration.loadConfiguration(file);
   }

   private static void open(Player player, Inventory inv) {
      UUID id = player.getUniqueId();
      MenuSession.SWITCHING.add(id);
      player.openInventory(inv);
      Tasks.run(AstralOrder.get().getCore(), () -> MenuSession.SWITCHING.remove(id));
   }

   public static void openAdmin(Player player, Order order) {
      FileConfiguration cfg = yaml();
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.ADMIN);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      String title = cfg.getString("title", "&cAdmin Control: &f%requested-material%").replace("%requested-material%", ItemUtil.pretty(order.getMaterial()));
      int rows = cfg.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         place(inv, items, "history");
         place(inv, items, "delete");
         place(inv, items, "profile");
         place(inv, items, "claim");
         place(inv, items, "back");
      }

      open(player, inv);
   }

   private static void place(Inventory inv, ConfigurationSection items, String key) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec != null) {
         inv.setItem(sec.getInt("slot", 0), GuiItems.fromSection(sec, Map.of()));
      }
   }

   public static void openProfile(Player player, UUID targetId) {
      FileConfiguration cfg = yaml();
      ConfigurationSection root = cfg.getConfigurationSection("profile-gui");
      if (root != null) {
         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.ADMIN_PROFILE);
         MenuSession.PROFILE_TARGET.put(player.getUniqueId(), targetId);
         OfflinePlayer off = Bukkit.getOfflinePlayer(targetId);
         String name = off.getName() == null ? targetId.toString() : off.getName();
         int active = AstralOrder.get().getOrderManager().countOpen(targetId);
         long completed = AstralOrder.get().getOrderManager().getByOwner(targetId).stream().filter(Order::isComplete).count();
         boolean blocked = AstralOrder.get().getBlocks().isBlocked(targetId);
         String status = blocked ? "&cBlocked" : "&aActive";
         String title = root.getString("title", "&bPlayer: &f%player%").replace("%player%", name);
         Inventory inv = Bukkit.createInventory((InventoryHolder)null, root.getInt("rows", 3) * 9, ColorUtil.parse(title));
         ConfigurationSection items = root.getConfigurationSection("items");
         if (items != null) {
            ConfigurationSection stats = items.getConfigurationSection("stats");
            if (stats != null) {
               ItemStack head = GuiItems.fromSection(stats, Map.of("active_orders", String.valueOf(active), "total_completed", String.valueOf(completed), "status_display", status));
               ItemMeta var17 = head.getItemMeta();
               if (var17 instanceof SkullMeta) {
                  SkullMeta meta = (SkullMeta)var17;
                  meta.setOwningPlayer(off);
                  head.setItemMeta(meta);
               }

               inv.setItem(stats.getInt("slot", 13), head);
            }

            if (blocked) {
               place(inv, items, "unblock");
            } else {
               place(inv, items, "block");
            }

            place(inv, items, "back");
         }

         open(player, inv);
      }
   }

   public static void openHistory(Player player, Order order, int page) {
      FileConfiguration cfg = yaml();
      ConfigurationSection root = cfg.getConfigurationSection("history-gui");
      if (root != null) {
         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.ADMIN_HISTORY);
         MenuSession.SELECTED.put(player.getUniqueId(), order);
         MenuSession.PAGE.put(player.getUniqueId(), page);
         String title = root.getString("title", "&eHistory: &f%requested-material%").replace("%requested-material%", ItemUtil.pretty(order.getMaterial()));
         Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ColorUtil.parse(title));
         List<DeliveryRecord> history = order.getHistory();
         int from = page * 45;
         int to = Math.min(history.size(), from + 45);

         for(int i = from; i < to; ++i) {
            DeliveryRecord r = (DeliveryRecord)history.get(i);
            int var10001 = i - from;
            Material var10002 = Material.PAPER;
            String var10003 = "&#00fc88" + r.getPlayerName();
            String var10004 = "&7Amount: &#00fc88" + r.getAmount();
            SimpleDateFormat var10005 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date var10006 = new Date(r.getTime());
            inv.setItem(var10001, ItemUtil.named(var10002, var10003, List.of(var10004, "&7Time: &f" + var10005.format(var10006))));
         }

         ConfigurationSection nav = root.getConfigurationSection("navigation");
         if (nav != null) {
            place(inv, nav, "back");
            place(inv, nav, "prev");
            place(inv, nav, "next");
         }

         open(player, inv);
      }
   }
}
