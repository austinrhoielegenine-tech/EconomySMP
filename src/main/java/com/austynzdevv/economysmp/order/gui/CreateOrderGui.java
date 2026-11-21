package com.austynzdevv.economysmp.order.gui;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.order.EconomyOrder;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.ItemUtil;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class CreateOrderGui {
   public static final Map<UUID, Pending> PENDING = new ConcurrentHashMap();

   private CreateOrderGui() {
   }

   public static void open(Player player, ItemStack template) {
      int max = EconomyOrder.get().getConfig().getInt("maximum-item-per-order", -1);
      if (max < 0) {
         max = EconomyOrder.get().getConfig().getInt("settings.max-order-amount", Integer.MAX_VALUE);
      }

      if (max < 0) {
         max = Integer.MAX_VALUE;
      }

      Pending pending = new Pending(template.clone(), Math.min(64, max), (double)1.0F);
      PENDING.put(player.getUniqueId(), pending);
      OrdersGui.VIEW.put(player.getUniqueId(), "create");
      redraw(player);
   }

   public static void redraw(Player player) {
      Pending p = (Pending)PENDING.get(player.getUniqueId());
      if (p != null) {
         String symbol = EconomyOrder.get().getConfig().getString("currency-symbol", "$");
         Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, ColorUtil.parse("&8ᴄʀᴇᴀᴛᴇ ᴏʀᴅᴇʀ"));
         inv.setItem(4, p.item.clone());
         inv.setItem(11, ItemUtil.named(Material.RED_STAINED_GLASS_PANE, "&c-64 Amount", List.of("&7Current: &#1FFD98" + p.amount)));
         inv.setItem(12, ItemUtil.named(Material.ORANGE_STAINED_GLASS_PANE, "&c-1 Amount", List.of("&7Current: &#1FFD98" + p.amount)));
         inv.setItem(13, ItemUtil.named(Material.PAPER, "&#1FFD98Amount: &f" + p.amount, List.of("&7Total cost: &#1FFD98" + symbol + String.format("%.2f", (double)p.amount * p.price))));
         inv.setItem(14, ItemUtil.named(Material.LIME_STAINED_GLASS_PANE, "&a+1 Amount", List.of("&7Current: &#1FFD98" + p.amount)));
         inv.setItem(15, ItemUtil.named(Material.GREEN_STAINED_GLASS_PANE, "&a+64 Amount", List.of("&7Current: &#1FFD98" + p.amount)));
         inv.setItem(20, ItemUtil.named(Material.RED_STAINED_GLASS_PANE, "&c-1.00 Price", List.of("&7Each: &#1FFD98" + symbol + String.format("%.2f", p.price))));
         inv.setItem(21, ItemUtil.named(Material.ORANGE_STAINED_GLASS_PANE, "&c-0.10 Price", List.of("&7Each: &#1FFD98" + symbol + String.format("%.2f", p.price))));
         inv.setItem(22, ItemUtil.named(Material.GOLD_INGOT, "&#1FFD98Price: &f" + symbol + String.format("%.2f", p.price), List.of("&7Paid upfront for the full order")));
         inv.setItem(23, ItemUtil.named(Material.LIME_STAINED_GLASS_PANE, "&a+0.10 Price", List.of("&7Each: &#1FFD98" + symbol + String.format("%.2f", p.price))));
         inv.setItem(24, ItemUtil.named(Material.GREEN_STAINED_GLASS_PANE, "&a+1.00 Price", List.of("&7Each: &#1FFD98" + symbol + String.format("%.2f", p.price))));
         inv.setItem(18, ItemUtil.named(Material.BARRIER, "&cCancel", List.of("&7Back to orders")));
         inv.setItem(26, ItemUtil.named(Material.LIME_CONCRETE, "&#3AFF00Confirm", List.of("&7Create order for &#1FFD98" + symbol + String.format("%.2f", (double)p.amount * p.price))));
         OrdersGui.SWITCHING.add(player.getUniqueId());
         player.openInventory(inv);
         Tasks.run(EconomyOrder.get(), () -> OrdersGui.SWITCHING.remove(player.getUniqueId()));
      }
   }

   public static class Pending {
      public ItemStack item;
      public int amount;
      public double price;

      public Pending(ItemStack item, int amount, double price) {
         this.item = item;
         this.amount = amount;
         this.price = price;
      }
   }
}
