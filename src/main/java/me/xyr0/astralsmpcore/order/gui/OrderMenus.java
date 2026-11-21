package me.xyr0.astralsmpcore.order.gui;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.xyr0.astralsmpcore.order.AstralOrder;
import me.xyr0.astralsmpcore.order.Order;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.EconomyUtil;
import me.xyr0.astralsmpcore.util.ItemUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class OrderMenus {
   private OrderMenus() {
   }

   private static FileConfiguration yaml(String file) {
      File guiDir = new File(AstralOrder.get().getDataFolder(), "gui");
      File f = new File(guiDir, file);
      if (!f.exists()) {
         try {
            AstralOrder.get().saveResource("order/gui/" + file, false);
         } catch (Exception var5) {
         }
      }

      if (!f.exists()) {
         try {
            InputStream in = AstralOrder.get().getResource("gui/" + file);
            if (in == null) {
               in = AstralOrder.get().getCore().getResource("order/gui/" + file);
            }

            if (in != null) {
               guiDir.mkdirs();
               Files.copy(in, f.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            }
         } catch (Exception var4) {
         }
      }

      return GuiItems.load(guiDir, file);
   }

   private static void open(Player player, Inventory inv) {
      UUID id = player.getUniqueId();
      MenuSession.SWITCHING.add(id);
      SoundUtil.playConfigured(player, AstralOrder.get().getCore(), "sounds.gui-open");
      SoundUtil.play(player, player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7F, 1.0F);
      player.openInventory(inv);
      Tasks.run(AstralOrder.get().getCore(), () -> MenuSession.SWITCHING.remove(id));
   }

   public static void openBrowse(Player player, int page) {
      FileConfiguration cfg = yaml("order-view.yml");
      ConfigurationSection root = cfg.getConfigurationSection("order-items");
      if (root == null) {
         root = cfg;
      }

      List<Order> list = filteredSorted(player);
      int pageSize = 45;
      int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(player.getUniqueId(), page);
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.BROWSE);
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs (Page %page%)").replace("%page%", String.valueOf(page + 1));
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ColorUtil.parse(title));
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
      int from = page * pageSize;
      int to = Math.min(list.size(), from + pageSize);

      for(int i = from; i < to; ++i) {
         Order order = (Order)list.get(i);
         inv.setItem(i - from, orderIcon(order, symbol));
      }

      ConfigurationSection items = root.getConfigurationSection("items");
      if (items == null) {
         items = root;
      }

      int prev = items.getInt("previous-page-slot", GuiItems.slot(items, "previous-page", 45));
      int next = items.getInt("next-page-slot", GuiItems.slot(items, "next-page", 53));
      ConfigurationSection previous = items.getConfigurationSection("previous-page");
      ConfigurationSection nextPage = items.getConfigurationSection("next-page");
      inv.setItem(prev, previous == null ? ItemUtil.named(Material.ARROW, "&#00fc88ᴘʀᴇᴠɪᴏᴜꜱ", List.of("&7Previous page")) : GuiItems.fromSection(previous, Map.of()));
      inv.setItem(next, nextPage == null ? ItemUtil.named(Material.ARROW, "&#00fc88ɴᴇxᴛ", List.of("&7Next page")) : GuiItems.fromSection(nextPage, Map.of()));
      if (items.isConfigurationSection("refresh")) {
         inv.setItem(items.getInt("refresh.slot", 49), GuiItems.fromSection(items.getConfigurationSection("refresh"), Map.of()));
      }

      if (items.isConfigurationSection("your-orders")) {
         inv.setItem(items.getInt("your-orders.slot", 51), GuiItems.fromSection(items.getConfigurationSection("your-orders"), Map.of()));
      }

      if (items.isConfigurationSection("search")) {
         inv.setItem(items.getInt("search.slot", 50), GuiItems.fromSection(items.getConfigurationSection("search"), Map.of()));
      }

      placeCycle(inv, items, "sort", (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0));
      placeCycle(inv, items, "filter", (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0));
      open(player, inv);
   }

   private static void placeCycle(Inventory inv, ConfigurationSection items, String key, int index) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec != null) {
         int slot = sec.getInt("slot", key.equals("sort") ? 47 : 48);
         List<String> options = sec.getStringList("options");
         if (options.isEmpty()) {
            inv.setItem(slot, GuiItems.fromSection(sec, Map.of()));
         } else {
            if (index < 0 || index >= options.size()) {
               index = 0;
            }

            String active = sec.getString("active_prefix", "&#00fc88● ");
            String inactive = sec.getString("inactive_prefix", "&f● ");
            List<String> lore = new ArrayList();

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
   }

   public static List<Order> filteredSorted(Player player) {
      UUID id = player.getUniqueId();
      int sort = (Integer)MenuSession.SORT.getOrDefault(id, 0);
      int filter = (Integer)MenuSession.FILTER.getOrDefault(id, 0);
      String search = (String)MenuSession.SEARCH.getOrDefault(id, "");
      List<Order> list = new ArrayList(AstralOrder.get().getOrderManager().getOpen());
      if (!search.isEmpty()) {
         String q = search.toLowerCase(Locale.ROOT);
         list = (List)list.stream().filter((o) -> o.getMaterial().name().toLowerCase(Locale.ROOT).contains(q) || ItemUtil.pretty(o.getMaterial()).toLowerCase(Locale.ROOT).contains(q) || o.getOwnerName().toLowerCase(Locale.ROOT).contains(q)).collect(Collectors.toList());
      }

      if (filter > 0) {
         list = (List)list.stream().filter((o) -> matchesFilter(o.getMaterial(), filter)).collect(Collectors.toList());
      }

      Comparator<Order> cmp;
      if (sort == 1) {
         cmp = Comparator.comparingInt(Order::getDelivered).reversed();
      } else if (sort == 2) {
         cmp = Comparator.comparingLong(Order::getCreatedAt).reversed();
      } else if (sort == 3) {
         cmp = Comparator.comparingDouble(Order::getPriceEach).reversed();
      } else {
         cmp = Comparator.comparingDouble((Order o) -> (double)o.getRemaining() * o.getPriceEach()).reversed();
      }

      list.sort(cmp);
      return list;
   }

   private static boolean matchesFilter(Material mat, int filter) {
      String n = mat.name();
      boolean var10000;
      switch (filter) {
         case 1 -> var10000 = n.contains("BLOCK") || n.endsWith("_ORE") || n.contains("STONE") || n.contains("DIRT") || n.contains("WOOD") || n.contains("LOG") || n.contains("PLANKS");
         case 2 -> var10000 = n.contains("PICKAXE") || n.contains("AXE") || n.contains("SHOVEL") || n.contains("HOE") || n.contains("SHEARS");
         case 3 -> var10000 = n.contains("BEEF") || n.contains("PORK") || n.contains("CHICKEN") || n.contains("BREAD") || n.contains("APPLE") || n.contains("CARROT") || n.contains("POTATO") || n.contains("COOKIE") || n.contains("MELON");
         case 4 -> var10000 = n.contains("SWORD") || n.contains("BOW") || n.contains("ARROW") || n.contains("TRIDENT") || n.contains("SHIELD") || n.contains("ARMOR") || n.contains("HELMET") || n.contains("CHESTPLATE") || n.contains("LEGGINGS") || n.contains("BOOTS");
         case 5 -> var10000 = n.contains("POTION");
         case 6 -> var10000 = n.contains("BOOK");
         case 7 -> var10000 = n.contains("INGOT") || n.contains("DUST") || n.contains("NUGGET") || n.contains("GEM") || n.contains("DIAMOND") || n.contains("EMERALD") || n.contains("REDSTONE") || n.contains("QUARTZ");
         case 8 -> var10000 = true;
         default -> var10000 = true;
      }

      return var10000;
   }

   private static ItemStack orderIcon(Order order, String symbol) {
      ItemStack icon = order.getDisplay();
      List<String> lore = new ArrayList();
      int var10001 = order.getRemaining();
      lore.add("&#00fc88" + var10001 + " &f" + ItemUtil.pretty(order.getMaterial()));
      lore.add("&#00fc88" + EconomyUtil.formatOrder(order.getPriceEach()) + " &feach");
      lore.add("");
      var10001 = order.getDelivered();
      lore.add("&#80802a" + var10001 + "/&#008f4d" + order.getAmount() + " &7Delivered");
      lore.add("");
      String var5 = order.getOwnerName();
      lore.add("&fClick to deliver &#00fc88" + var5 + " &f" + ItemUtil.pretty(order.getMaterial()));
      return GuiItems.applyMeta(icon, "&#00fc88" + order.getOwnerName() + "s Order", lore);
   }

   public static void openYourOrders(Player player) {
      FileConfiguration cfg = yaml("your-orders.yml");
      ConfigurationSection root = cfg.getConfigurationSection("your-orders");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.YOUR_ORDERS);
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Your Orders");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null && items.isConfigurationSection("new-order")) {
         inv.setItem(items.getInt("new-order.slot", 0), GuiItems.fromSection(items.getConfigurationSection("new-order"), Map.of()));
      }

      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
      List<Order> mine = AstralOrder.get().getOrderManager().getByOwner(player.getUniqueId());
      int slot = 1;

      for(Order order : mine) {
         if (slot >= rows * 9) {
            break;
         }

         inv.setItem(slot++, orderIcon(order, symbol));
      }

      open(player, inv);
   }

   public static void openNewOrder(Player player) {
      FileConfiguration cfg = yaml("new-order.yml");
      ConfigurationSection root = cfg.getConfigurationSection("order-news");
      if (root == null) {
         root = cfg.getConfigurationSection("order-new");
      }

      if (root == null) {
         root = cfg;
      }

      UUID id = player.getUniqueId();
      MenuSession.VIEW.put(id, MenuSession.View.NEW_ORDER);
      if (!MenuSession.DRAFT_ITEM.containsKey(id)) {
         ItemStack hand = player.getInventory().getItemInMainHand();
         if (hand != null && hand.getType() != Material.AIR) {
            ItemStack copy = hand.clone();
            copy.setAmount(1);
            MenuSession.DRAFT_ITEM.put(id, copy);
         } else {
            MenuSession.DRAFT_ITEM.put(id, new ItemStack(Material.STONE));
         }
      }

      MenuSession.DRAFT_AMOUNT.putIfAbsent(id, 1);
      MenuSession.DRAFT_PRICE.putIfAbsent(id, (double)1.0F);
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> New Order");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
      ItemStack draft = MenuSession.draftItem(id);
      int amount = (Integer)MenuSession.DRAFT_AMOUNT.get(id);
      double price = (Double)MenuSession.DRAFT_PRICE.get(id);
      Map<String, String> ph = GuiItems.map("item", ItemUtil.pretty(draft.getType()), "amount_formatted", String.valueOf(amount), "price_per_item", EconomyUtil.formatOrder(price), "price", EconomyUtil.formatOrder((double)amount * price), "material_selected_item", draft.getType().name());
      if (items != null) {
         if (items.isConfigurationSection("cancel")) {
            inv.setItem(items.getInt("cancel.slot", 10), GuiItems.fromSection(items.getConfigurationSection("cancel"), ph));
         }

         if (items.isConfigurationSection("confirm")) {
            inv.setItem(items.getInt("confirm.slot", 16), GuiItems.fromSection(items.getConfigurationSection("confirm"), ph));
         }

         if (items.isConfigurationSection("item")) {
            ItemStack itemBtn = GuiItems.fromSection(items.getConfigurationSection("item"), ph);
            itemBtn.setType(draft.getType());
            inv.setItem(items.getInt("item.slot", 12), itemBtn);
         }

         if (items.isConfigurationSection("amount")) {
            inv.setItem(items.getInt("amount.slot", 13), GuiItems.fromSection(items.getConfigurationSection("amount"), ph));
         }

         if (items.isConfigurationSection("price")) {
            inv.setItem(items.getInt("price.slot", 14), GuiItems.fromSection(items.getConfigurationSection("price"), ph));
         }
      }

      open(player, inv);
   }

   public static void openMaterials(Player player, int page) {
      FileConfiguration cfg = yaml("list-materials.yml");
      ConfigurationSection root = cfg.getConfigurationSection("list-materials");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MATERIALS);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      List<Material> mats = listMaterials(player);
      int pageSize = 45;
      int maxPage = mats.isEmpty() ? 0 : (mats.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Select Item");
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ColorUtil.parse(title));
      int from = page * pageSize;
      int to = Math.min(mats.size(), from + pageSize);

      for(int i = from; i < to; ++i) {
         Material mat = (Material)mats.get(i);
         inv.setItem(i - from, ItemUtil.named(mat, "&#00fc88" + ItemUtil.pretty(mat), List.of("&fClick to select")));
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         inv.setItem(items.getInt("previous-page-slot", 45), GuiItems.fromSection(items.getConfigurationSection("previous-page"), Map.of()));
         inv.setItem(items.getInt("next-page-slot", 53), GuiItems.fromSection(items.getConfigurationSection("next-page"), Map.of()));
         if (items.isConfigurationSection("search")) {
            inv.setItem(items.getInt("search.slot", 50), GuiItems.fromSection(items.getConfigurationSection("search"), Map.of()));
         }

         placeCycle(inv, items, "sort", (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0));
         placeCycle(inv, items, "filter", (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0));
      }

      open(player, inv);
   }

   private static List<Material> listMaterials(Player player) {
      List<Material> list = new ArrayList();

      for(Material mat : Material.values()) {
         if (mat.isItem() && !mat.isAir() && !mat.name().contains("LEGACY")) {
            list.add(mat);
         }
      }

      int filter = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
      if (filter > 0) {
         list = (List)list.stream().filter((m) -> matchesFilter(m, filter)).collect(Collectors.toList());
      }

      String search = (String)MenuSession.SEARCH.getOrDefault(player.getUniqueId(), "");
      if (!search.isEmpty()) {
         String q = search.toLowerCase(Locale.ROOT);
         list = (List)list.stream().filter((m) -> m.name().toLowerCase(Locale.ROOT).contains(q)).collect(Collectors.toList());
      }

      int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
      if (sort == 1) {
         list.sort(Comparator.comparing(Material::name).reversed());
      } else {
         list.sort(Comparator.comparing(Material::name));
      }

      return list;
   }

   public static void openConfirmDeliver(Player player, Order order, int amount) {
      FileConfiguration cfg = yaml("confirm-delivery.yml");
      ConfigurationSection root = cfg.getConfigurationSection("confirm-delivery");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CONFIRM_DELIVER);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      MenuSession.DRAFT_DELIVER.put(player.getUniqueId(), amount);
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Confirm Delivery");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      Map<String, String> ph = GuiItems.map("price_receive", EconomyUtil.formatOrder((double)amount * order.getPriceEach()));
      if (items != null) {
         if (items.isConfigurationSection("cancel")) {
            inv.setItem(items.getInt("cancel.slot", 11), GuiItems.fromSection(items.getConfigurationSection("cancel"), ph));
         }

         if (items.isConfigurationSection("confirm")) {
            inv.setItem(items.getInt("confirm.slot", 15), GuiItems.fromSection(items.getConfigurationSection("confirm"), ph));
         }

         int src = items.getInt("source-item.slot", 13);
         inv.setItem(src, orderIcon(order, symbol));
      }

      open(player, inv);
   }

   public static void openConfirmCancel(Player player, Order order) {
      FileConfiguration cfg = yaml("confirm-cancel.yml");
      ConfigurationSection root = cfg.getConfigurationSection("confirm-cancel");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CONFIRM_CANCEL);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Cancel Order");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         if (items.isConfigurationSection("cancel")) {
            inv.setItem(items.getInt("cancel.slot", 11), GuiItems.fromSection(items.getConfigurationSection("cancel"), Map.of()));
         }

         if (items.isConfigurationSection("confirm")) {
            inv.setItem(items.getInt("confirm.slot", 15), GuiItems.fromSection(items.getConfigurationSection("confirm"), Map.of()));
         }

         inv.setItem(items.getInt("source-item.slot", 13), orderIcon(order, symbol));
      }

      open(player, inv);
   }

   public static void openEdit(Player player, Order order) {
      FileConfiguration cfg = yaml("edit-order.yml");
      ConfigurationSection root = cfg.getConfigurationSection("edit-order");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.EDIT_ORDER);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Edit Order");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         if (items.isConfigurationSection("background")) {
            ConfigurationSection bg = items.getConfigurationSection("background");
            String slots = bg.getString("slots", "");
            ItemStack pane = GuiItems.fromSection(bg, Map.of());

            for(String part : slots.split(",")) {
               try {
                  inv.setItem(Integer.parseInt(part.trim()), pane.clone());
               } catch (Exception var17) {
               }
            }
         }

         inv.setItem(items.getInt("source-item.slot", 10), orderIcon(order, symbol));
         if (items.isConfigurationSection("cancel-order")) {
            inv.setItem(items.getInt("cancel-order.slot", 13), GuiItems.fromSection(items.getConfigurationSection("cancel-order"), Map.of()));
         }

         if (order.getUncollected() > 0 && items.isConfigurationSection("collect-items")) {
            inv.setItem(items.getInt("collect-items.slot", 15), GuiItems.fromSection(items.getConfigurationSection("collect-items"), Map.of()));
         } else if (items.isConfigurationSection("no-collect-items")) {
            inv.setItem(items.getInt("no-collect-items.slot", 15), GuiItems.fromSection(items.getConfigurationSection("no-collect-items"), Map.of()));
         }

         if (items.isConfigurationSection("back")) {
            inv.setItem(items.getInt("back.slot", 18), GuiItems.fromSection(items.getConfigurationSection("back"), Map.of()));
         }
      }

      open(player, inv);
   }

   public static void openCollect(Player player, Order order, int page) {
      FileConfiguration cfg = yaml("collect-items.yml");
      ConfigurationSection root = cfg.getConfigurationSection("collect-items");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.COLLECT);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Collect Items");
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ColorUtil.parse(title));
      int left = order.getUncollected();

      int give;
      for(int slot = 0; left > 0 && slot < 45; left -= give) {
         give = Math.min(left, order.getDisplay().getMaxStackSize());
         ItemStack stack = order.getDisplay();
         stack.setAmount(give);
         inv.setItem(slot++, stack);
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         inv.setItem(items.getInt("previous-page-slot", 45), GuiItems.fromSection(items.getConfigurationSection("previous-page"), Map.of()));
         inv.setItem(items.getInt("next-page-slot", 53), GuiItems.fromSection(items.getConfigurationSection("next-page"), Map.of()));
         if (items.isConfigurationSection("drop-loot")) {
            inv.setItem(items.getInt("drop-loot.slot", 49), GuiItems.fromSection(items.getConfigurationSection("drop-loot"), Map.of()));
         }
      }

      inv.setItem(48, ItemUtil.named(Material.EMERALD, "&#00fc88ꜱᴇʟʟ ᴀʟʟ", List.of("&fSell all collected items")));
      open(player, inv);
   }

   public static void openDeliverChest(Player player, Order order) {
      FileConfiguration cfg = yaml("deliver-items.yml");
      ConfigurationSection root = cfg.getConfigurationSection("deliver-items");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.DELIVER_CHEST);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Place Items");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      open(player, inv);
   }

   public static void openConfirmSell(Player player, Order order, double total) {
      FileConfiguration cfg = yaml("confirm-sell.yml");
      ConfigurationSection root = cfg.getConfigurationSection("confirm-sell");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CONFIRM_SELL);
      MenuSession.SELECTED.put(player.getUniqueId(), order);
      String title = root.getString("title", "&8ᴏʀᴅᴇʀs -> Confirm Sell");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      Map<String, String> ph = GuiItems.map("total-value", EconomyUtil.formatOrder(total));
      if (items != null) {
         if (items.isConfigurationSection("cancel")) {
            inv.setItem(items.getInt("cancel.slot", 11), GuiItems.fromSection(items.getConfigurationSection("cancel"), ph));
         }

         if (items.isConfigurationSection("confirm")) {
            inv.setItem(items.getInt("confirm.slot", 15), GuiItems.fromSection(items.getConfigurationSection("confirm"), ph));
         }

         String symbol = AstralOrder.get().getConfig().getString("currency-symbol", "$");
         inv.setItem(items.getInt("source-item.slot", 13), orderIcon(order, symbol));
      }

      open(player, inv);
   }
}
