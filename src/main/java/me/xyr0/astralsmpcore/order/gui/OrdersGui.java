package me.xyr0.astralsmpcore.order.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.astralsmpcore.order.AstralOrder;
import me.xyr0.astralsmpcore.order.Order;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.ItemUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class OrdersGui {
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, String> VIEW = new ConcurrentHashMap();
   public static final Set<UUID> SWITCHING = ConcurrentHashMap.newKeySet();
   public static final int PAGE_SIZE = 45;

   private OrdersGui() {
   }

   public static void openBrowse(Player player, int page) {
      List<Order> open = AstralOrder.get().getOrderManager().getOpen();
      int max = open.isEmpty() ? 0 : (open.size() - 1) / 45;
      if (page < 0) {
         page = 0;
      }

      if (page > max) {
         page = max;
      }

      PAGE.put(player.getUniqueId(), page);
      VIEW.put(player.getUniqueId(), "browse");
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ColorUtil.parse("&8ᴏʀᴅᴇʀꜱ"));
      int from = page * 45;
      int to = Math.min(open.size(), from + 45);
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");

      for(int i = from; i < to; ++i) {
         Order order = (Order)open.get(i);
         ItemStack icon = order.getDisplay();
         ItemMeta meta = icon.getItemMeta();
         if (meta != null) {
            meta.displayName(ColorUtil.parse("&#1FFD98" + ItemUtil.pretty(order.getMaterial())));
            List<Component> lore = new ArrayList();
            lore.add(ColorUtil.parse("&7Seller: &f" + order.getOwnerName()));
            int var10001 = order.getRemaining();
            lore.add(ColorUtil.parse("&7Needed: &#1FFD98" + var10001 + "&7/&f" + order.getAmount()));
            lore.add(ColorUtil.parse("&7Price: &#1FFD98" + symbol + String.format("%.2f", order.getPriceEach()) + " &7each"));
            lore.add(ColorUtil.parse("&7Total left: &#1FFD98" + symbol + String.format("%.2f", (double)order.getRemaining() * order.getPriceEach())));
            lore.add(ColorUtil.parse(""));
            lore.add(ColorUtil.parse("&aLeft-Click &7deliver 1 stack"));
            lore.add(ColorUtil.parse("&aRight-Click &7deliver all you have"));
            meta.lore(lore);
            icon.setItemMeta(meta);
         }

         inv.setItem(i - from, icon);
      }

      inv.setItem(45, ItemUtil.named(Material.ARROW, "&#1FFD98ʙᴀᴄᴋ", List.of("&7Previous page")));
      inv.setItem(49, ItemUtil.named(Material.EMERALD, "&#1FFD98ᴄʀᴇᴀᴛᴇ ᴏʀᴅᴇʀ", List.of("&7Hold an item and click", "&7to create a new order")));
      inv.setItem(50, ItemUtil.named(Material.CHEST, "&#1FFD98ᴍʏ ᴏʀᴅᴇʀꜱ", List.of("&7View and manage your orders")));
      inv.setItem(53, ItemUtil.named(Material.ARROW, "&#1FFD98ɴᴇхᴛ", List.of("&7Next page")));
      SWITCHING.add(player.getUniqueId());
      player.openInventory(inv);
      Tasks.run(AstralOrder.get().getCore(), () -> SWITCHING.remove(player.getUniqueId()));
   }

   public static void openMine(Player player, int page) {
      List<Order> mine = AstralOrder.get().getOrderManager().getByOwner(player.getUniqueId());
      int max = mine.isEmpty() ? 0 : (mine.size() - 1) / 45;
      if (page < 0) {
         page = 0;
      }

      if (page > max) {
         page = max;
      }

      PAGE.put(player.getUniqueId(), page);
      VIEW.put(player.getUniqueId(), "mine");
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ColorUtil.parse("&8ᴍʏ ᴏʀᴅᴇʀꜱ"));
      int from = page * 45;
      int to = Math.min(mine.size(), from + 45);
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");

      for(int i = from; i < to; ++i) {
         Order order = (Order)mine.get(i);
         ItemStack icon = order.getDisplay();
         ItemMeta meta = icon.getItemMeta();
         if (meta != null) {
            meta.displayName(ColorUtil.parse("&#1FFD98" + ItemUtil.pretty(order.getMaterial())));
            List<Component> lore = new ArrayList();
            int var10001 = order.getDelivered();
            lore.add(ColorUtil.parse("&7Progress: &#1FFD98" + var10001 + "&7/&f" + order.getAmount()));
            lore.add(ColorUtil.parse("&7Uncollected: &#1FFD98" + order.getUncollected()));
            lore.add(ColorUtil.parse("&7Price: &#1FFD98" + symbol + String.format("%.2f", order.getPriceEach())));
            lore.add(ColorUtil.parse(""));
            lore.add(ColorUtil.parse("&aLeft-Click &7collect items"));
            lore.add(ColorUtil.parse("&cShift-Click &7cancel order"));
            meta.lore(lore);
            icon.setItemMeta(meta);
         }

         inv.setItem(i - from, icon);
      }

      inv.setItem(45, ItemUtil.named(Material.ARROW, "&#1FFD98ʙᴀᴄᴋ", List.of("&7Previous page")));
      inv.setItem(49, ItemUtil.named(Material.BARRIER, "&cʙᴀᴄᴋ", List.of("&7Return to orders")));
      inv.setItem(53, ItemUtil.named(Material.ARROW, "&#1FFD98ɴᴇхᴛ", List.of("&7Next page")));
      SWITCHING.add(player.getUniqueId());
      player.openInventory(inv);
      Tasks.run(AstralOrder.get().getCore(), () -> SWITCHING.remove(player.getUniqueId()));
   }

   public static void clear(Player player) {
      PAGE.remove(player.getUniqueId());
      VIEW.remove(player.getUniqueId());
      CreateOrderGui.PENDING.remove(player.getUniqueId());
   }
}
