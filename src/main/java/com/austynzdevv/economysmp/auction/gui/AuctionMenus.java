package com.austynzdevv.economysmp.auction.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.austynzdevv.economysmp.auction.EconomyAuction;
import com.austynzdevv.economysmp.auction.AuctionListing;
import com.austynzdevv.economysmp.auction.Transaction;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.EconomyUtil;
import com.austynzdevv.economysmp.util.ItemUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class AuctionMenus {
   private AuctionMenus() {
   }

   private static FileConfiguration yaml(String name) {
      return YamlConfiguration.loadConfiguration(new File(EconomyAuction.get().getDataFolder(), "gui/" + name));
   }

   private static void open(Player player, Inventory inv) {
      UUID id = player.getUniqueId();
      MenuSession.SWITCHING.add(id);
      player.openInventory(inv);
      SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.gui-open");
      Tasks.run(EconomyAuction.get().getCore(), () -> MenuSession.SWITCHING.remove(id));
   }

   public static void openMain(Player player, int page) {
      FileConfiguration cfg = yaml("main.gui.yml");
      ConfigurationSection root = cfg.getConfigurationSection("main-gui");
      if (root == null) {
         root = cfg;
      }

      int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
      int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
      String search = (String)MenuSession.SEARCH.getOrDefault(player.getUniqueId(), "");
      List<AuctionListing> list = EconomyAuction.get().getAuctions().filtered(search, sort, filter, Boolean.TRUE.equals(MenuSession.SHARD_VIEW.get(player.getUniqueId())));
      int pageSize = 45;
      int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(player.getUniqueId(), page);
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MAIN);
      String title = root.getString("title", "&8ᴀᴜᴄᴛɪᴏɴ (Page %current_page%)").replace("%current_page%", String.valueOf(page + 1));
      int rows = root.getInt("rows", root.getInt("size", 6) > 6 ? root.getInt("size", 54) / 9 : root.getInt("rows", 6));
      if (rows < 1 || rows > 6) {
         rows = 6;
      }

      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      int from = page * pageSize;
      int to = Math.min(list.size(), from + pageSize);
      List<String> loreTpl = cfg.getStringList("auction-item-lore");
      if (loreTpl.isEmpty() && root.contains("auction-item-lore")) {
         loreTpl = root.getStringList("auction-item-lore");
      }

      if (loreTpl.isEmpty()) {
         loreTpl = List.of("&fPrice: &#00fc88%price%", "&fSeller: &#00fc88%seller%", "&fTime left: &#00fc88%expires%");
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items == null) {
         items = root.getConfigurationSection("items");
      }

      if (items == null) {
         items = cfg;
      }

      putNav(inv, items, "previous-page", 45);
      putNav(inv, items, "next-page", 53);
      putNav(inv, items, "refresh", 49);
      putNav(inv, items, "my-items", 51);
      putNav(inv, items, "search", 50);
      placeCycle(inv, items, "sort", sort);
      placeCycle(inv, items, "filter", filter);
      if (EconomyAuction.get().getConfig().getBoolean("Use-shards", false)) {
         boolean shardView = Boolean.TRUE.equals(MenuSession.SHARD_VIEW.get(player.getUniqueId()));
         inv.setItem(52, ItemUtil.named(shardView ? Material.GOLD_INGOT : Material.AMETHYST_SHARD, shardView ? "&#A303F9sʜᴀʀᴅ ᴀᴜᴄᴛɪᴏɴ" : "&#00fc88ᴍᴏɴᴇʏ ᴀᴜᴄᴛɪᴏɴ", List.of("&7Click to switch currency", shardView ? "&fViewing: Shards" : "&fViewing: Money")));
      }

      for(int i = from; i < to; ++i) {
         AuctionListing listing = (AuctionListing)list.get(i);
         List<String> lore = new ArrayList();

         for(String line : loreTpl) {
            String var10003 = listing.isShards() ? "★ " : "$";
            lore.add(line.replace("%price%", var10003 + EconomyUtil.formatAuction(listing.getPrice())).replace("%seller%", listing.getSellerName()).replace("%expires%", timeLeft(listing.getExpiresAt())));
         }

         ItemStack show = safeStack(listing.getItem());
         inv.setItem(i - from, GuiItems.withLore(show, (String)null, lore));
      }

      open(player, inv);
   }

   private static void putNav(Inventory inv, ConfigurationSection items, String key, int def) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec != null) {
         inv.setItem(sec.getInt("slot", def), GuiItems.fromSection(sec, Map.of()));
      }
   }

   private static void placeCycle(Inventory inv, ConfigurationSection items, String key, int index) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec != null) {
         int slot = sec.getInt("slot", key.equals("sort") ? 47 : 48);
         List<String> options = sec.getStringList("options");
         String active = sec.getString("active_prefix", "&#00fc88● ");
         String inactive = sec.getString("inactive_prefix", "&f● ");
         List<String> lore = new ArrayList(sec.getStringList("lore"));

         for(int i = 0; i < options.size(); ++i) {
            lore.add((i == index ? active : inactive) + (String)options.get(i));
         }

         String name = sec.getString("name", "&#00fc88" + key);

         Material mat;
         try {
            mat = Material.valueOf(sec.getString("material", "CAULDRON"));
         } catch (Exception var13) {
            mat = Material.CAULDRON;
         }

         inv.setItem(slot, ItemUtil.named(mat, name, lore));
      }
   }

   private static String timeLeft(long expires) {
      long left = expires - System.currentTimeMillis();
      if (left <= 0L) {
         return "Expired";
      } else {
         long h = TimeUnit.MILLISECONDS.toHours(left);
         long m = TimeUnit.MILLISECONDS.toMinutes(left) % 60L;
         if (h > 0L) {
            return h + "h " + m + "m";
         } else {
            long s = TimeUnit.MILLISECONDS.toSeconds(left) % 60L;
            return m + "m " + s + "s";
         }
      }
   }

   public static void openMyItems(Player player) {
      FileConfiguration cfg = yaml("my.items.gui.yml");
      ConfigurationSection root = cfg.getConfigurationSection("my-items");
      if (root == null) {
         root = cfg.getConfigurationSection("my-items-gui");
      }

      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MY_ITEMS);
      String title = root.getString("title", "&8ʏᴏᴜʀ ɪᴛᴇᴍꜱ");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      List<AuctionListing> mine = EconomyAuction.get().getAuctions().getBySeller(player.getUniqueId());
      int slot = 1;

      for(AuctionListing listing : mine) {
         if (slot >= inv.getSize() - 1) {
            break;
         }

         List<String> lore = List.of("&fPrice: &#00fc88" + (listing.isShards() ? "✦ " : "$") + EconomyUtil.formatAuction(listing.getPrice()), "&fTime left: &#00fc88" + timeLeft(listing.getExpiresAt()), "", "&cClick to remove");
         inv.setItem(slot++, GuiItems.withLore(listing.getItem(), (String)null, lore));
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         if (items.isConfigurationSection("info")) {
            inv.setItem(items.getInt("info.slot", 0), GuiItems.fromSection(items.getConfigurationSection("info"), Map.of()));
         }

         if (items.isConfigurationSection("transactions")) {
            inv.setItem(items.getInt("transactions.slot", 26), GuiItems.fromSection(items.getConfigurationSection("transactions"), Map.of()));
         }
      }

      open(player, inv);
   }

   public static void openConfirmBuy(Player player, AuctionListing listing) {
      FileConfiguration cfg = yaml("confirm.purchase.gui.yml");
      ConfigurationSection root = cfg.getConfigurationSection("confirm-purchase");
      if (root == null) {
         root = cfg.getConfigurationSection("confirm-purchase-gui");
      }

      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CONFIRM_BUY);
      MenuSession.SELECTED.put(player.getUniqueId(), listing);
      String title = root.getString("title", "&8ᴄᴏɴꜰɪʀᴍ ᴘᴜʀᴄʜᴀꜱᴇ");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      String[] var10000 = new String[]{"price", null, null, null, null, null};
      String var10003 = listing.isShards() ? "★ " : "$";
      var10000[1] = var10003 + EconomyUtil.formatAuction(listing.getPrice());
      var10000[2] = "seller";
      var10000[3] = listing.getSellerName();
      var10000[4] = "time_left";
      var10000[5] = timeLeft(listing.getExpiresAt());
      Map<String, String> ph = GuiItems.map(var10000);
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items == null) {
         items = cfg;
      }

      ConfigurationSection confirm = items.getConfigurationSection("confirm");
      ConfigurationSection cancel = items.getConfigurationSection("cancel");
      ConfigurationSection item = items.getConfigurationSection("source-item");
      if (item == null) {
         item = items.getConfigurationSection("item");
      }

      if (confirm != null) {
         inv.setItem(confirm.getInt("slot", 15), GuiItems.fromSection(confirm, ph));
      }

      if (cancel != null) {
         inv.setItem(cancel.getInt("slot", 11), GuiItems.fromSection(cancel, ph));
      }

      if (item != null) {
         List<String> lore = item.getStringList("lore");
         List<String> out = new ArrayList();

         for(String line : lore) {
            out.add(line.replace("%price%", EconomyUtil.formatAuction(listing.getPrice())).replace("%seller%", listing.getSellerName()).replace("%time_left%", timeLeft(listing.getExpiresAt())));
         }

         inv.setItem(item.getInt("slot", 13), GuiItems.withLore(listing.getItem(), (String)null, out));
      }

      open(player, inv);
   }

   public static void openConfirmList(Player player, ItemStack stack, double price) {
      FileConfiguration cfg = yaml("confirm.listing.gui.yml");
      ConfigurationSection root = cfg.getConfigurationSection("confirm-listing");
      if (root == null) {
         root = cfg.getConfigurationSection("confirm-listing-gui");
      }

      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CONFIRM_LIST);
      MenuSession.PENDING_ITEM.put(player.getUniqueId(), stack.clone());
      MenuSession.PENDING_PRICE.put(player.getUniqueId(), price);
      MenuSession.PENDING_SHARDS.putIfAbsent(player.getUniqueId(), false);
      boolean shardPrice = Boolean.TRUE.equals(MenuSession.PENDING_SHARDS.get(player.getUniqueId()));
      String title = root.getString("title", "&8ᴄᴏɴꜰɪʀᴍ ʟɪꜱᴛɪɴɢ");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      Map<String, String> ph = GuiItems.map("price", (shardPrice ? "✦ " : "$") + EconomyUtil.formatAuction(price));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items == null) {
         items = cfg;
      }

      ConfigurationSection confirm = items.getConfigurationSection("confirm");
      ConfigurationSection cancel = items.getConfigurationSection("cancel");
      ConfigurationSection item = items.getConfigurationSection("source-item");
      if (item == null) {
         item = items.getConfigurationSection("item");
      }

      if (confirm != null) {
         inv.setItem(confirm.getInt("slot", 15), GuiItems.fromSection(confirm, ph));
      }

      if (cancel != null) {
         inv.setItem(cancel.getInt("slot", 11), GuiItems.fromSection(cancel, ph));
      }

      if (item != null) {
         List<String> lore = item.getStringList("lore");
         List<String> out = new ArrayList();

         for(String line : lore) {
            out.add(line.replace("%price%", EconomyUtil.formatAuction(price)));
         }

         inv.setItem(item.getInt("slot", 13), GuiItems.withLore(stack, (String)null, out));
      }

      if (EconomyAuction.get().getConfig().getBoolean("Use-shards", false)) {
         inv.setItem(4, ItemUtil.named(shardPrice ? Material.GOLD_INGOT : Material.AMETHYST_SHARD, shardPrice ? "&#A303F9ᴜsɪɴɢ sʜᴀʀᴅs" : "&#00fc88ᴜѕɪɴɢ ᴍᴏɴᴇʏ", List.of("&7Click to change listing currency", shardPrice ? "&fCurrency: Shards" : "&fCurrency: Money")));
      }

      open(player, inv);
   }

   public static void openTransactions(Player player, int page) {
      FileConfiguration cfg = yaml("transactions.gui.yml");
      ConfigurationSection root = cfg.getConfigurationSection("transactions");
      if (root == null) {
         root = cfg.getConfigurationSection("transactions-gui");
      }

      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.TRANSACTIONS);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      UUID txOwner = (UUID)MenuSession.ADMIN_TARGET.getOrDefault(player.getUniqueId(), player.getUniqueId());
      List<Transaction> list = EconomyAuction.get().getAuctions().getTransactions(txOwner);
      int pageSize = 45;
      int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      String title = root.getString("title", "&8ᴛʀᴀɴꜱᴀᴄᴛɪᴏɴꜱ (Page %current_page%)").replace("%current_page%", String.valueOf(page + 1));
      int rows = root.getInt("rows", 6);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      int from = page * pageSize;
      int to = Math.min(list.size(), from + pageSize);
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         items.getConfigurationSection("source-item");
      } else {
         Object var10000 = null;
      }

      for(int i = from; i < to; ++i) {
         Transaction tx = (Transaction)list.get(i);
         String action = tx.getType() == Transaction.Type.BUY ? "Bought" : "Sold";
         List<String> lore = List.of("&f" + action + " &#00fc88" + tx.getOtherName() + "'s &f" + tx.getItem().getAmount() + " " + ItemUtil.pretty(tx.getItem()) + " &ffor &#00fc88" + EconomyUtil.formatAuction(tx.getPrice()), "&#00fc88" + timeAgo(tx.getTime()) + " ago");
         inv.setItem(i - from, GuiItems.withLore(tx.getItem(), "&f" + ItemUtil.pretty(tx.getItem()), lore));
      }

      if (items != null) {
         if (items.isConfigurationSection("stats")) {
            inv.setItem(items.getInt("stats.slot", 48), GuiItems.fromSection(items.getConfigurationSection("stats"), GuiItems.map("total_spent", EconomyUtil.formatAuction(EconomyAuction.get().getAuctions().totalSpent(txOwner)), "total_made", EconomyUtil.formatAuction(EconomyAuction.get().getAuctions().totalMade(txOwner)))));
         }

         putNav(inv, items, "refresh", 49);
         putNav(inv, items, "search", 50);
         putNav(inv, items, "next-page", 53);
         putNav(inv, items, "previous-page", 45);
      }

      open(player, inv);
   }

   private static String timeAgo(long time) {
      long diff = System.currentTimeMillis() - time;
      long h = TimeUnit.MILLISECONDS.toHours(diff);
      if (h > 24L) {
         return h / 24L + "d";
      } else if (h > 0L) {
         return h + "h";
      } else {
         long m = TimeUnit.MILLISECONDS.toMinutes(diff);
         return m + "m";
      }
   }

   public static void openShulker(Player player, ItemStack shulker) {
      FileConfiguration cfg = yaml("shulker.view.gui.yml");
      ConfigurationSection root = cfg.getConfigurationSection("shulker-view");
      if (root == null) {
         root = cfg.getConfigurationSection("gui-settings");
      }

      if (root == null) {
         root = cfg;
      }

      int rows = root.getInt("rows", 4);
      String title = root.getString("title", root.getString("titel", "&8ꜱʜᴜʟᴋᴇʀ ᴠɪᴇᴡ"));
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SHULKER);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ItemMeta var9 = shulker.getItemMeta();
      if (var9 instanceof BlockStateMeta meta) {
         BlockState var13 = meta.getBlockState();
         if (var13 instanceof ShulkerBox box) {
            ItemStack[] contents = box.getInventory().getContents();

            for(int i = 0; i < Math.min(contents.length, 27); ++i) {
               if (contents[i] != null) {
                  inv.setItem(i, contents[i].clone());
               }
            }
         }
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      ConfigurationSection back = items != null ? items.getConfigurationSection("back") : cfg.getConfigurationSection("back-button");
      if (back != null) {
         inv.setItem(back.getInt("slot", 27), GuiItems.fromSection(back, Map.of()));
      }

      open(player, inv);
   }

   private static ItemStack safeStack(ItemStack stack) {
      return stack != null && !stack.getType().isAir() ? stack : new ItemStack(Material.BARRIER);
   }
}
