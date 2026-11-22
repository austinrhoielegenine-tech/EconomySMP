package com.austynzdevv.nyrethacore.sell.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.austynzdevv.nyrethacore.sell.EconomySell;
import com.austynzdevv.nyrethacore.sell.ProgressManager;
import com.austynzdevv.nyrethacore.sell.SellRecord;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.EconomyUtil;
import com.austynzdevv.nyrethacore.util.ItemUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class SellMenus {
   private static FileConfiguration sellConfig;
   private static long sellConfigModified = -1L;

   private SellMenus() {
   }

   private static FileConfiguration sellConfig() {
      File file = new File(EconomySell.get().getDataFolder(), "gui/sell.yml");
      long modified = file.lastModified();
      if (sellConfig == null || modified != sellConfigModified) {
         sellConfig = YamlConfiguration.loadConfiguration(file);
         sellConfigModified = modified;
      }

      return sellConfig;
   }

   private static FileConfiguration yaml(String name) {
      return YamlConfiguration.loadConfiguration(new File(EconomySell.get().getDataFolder(), name));
   }

   private static void open(Player player, Inventory inv) {
      UUID id = player.getUniqueId();
      MenuSession.SWITCHING.add(id);
      player.openInventory(inv);
      SoundUtil.playConfigured(player, EconomySell.get().getCore(), "sounds.gui-open");
      Tasks.run(EconomySell.get(), () -> MenuSession.SWITCHING.remove(id));
   }

   public static void openSell(Player player) {
      FileConfiguration cfg = sellConfig();
      ConfigurationSection root = cfg.getConfigurationSection("sell");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SELL);
      String title = root.getString("title", "&8ꜱᴇʟʟ");
      int rows = root.getInt("rows", 5);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      updateConfirm(player, inv);
      open(player, inv);
   }

   public static void updateConfirm(Player player, Inventory inv) {
      FileConfiguration cfg = sellConfig();
      ConfigurationSection items = cfg.getConfigurationSection("items");
      ConfigurationSection confirm = items != null ? items.getConfigurationSection("confirm") : null;
      int slot = confirm != null ? confirm.getInt("slot", 44) : 44;
      double total = calcTotal(player, inv, slot);
      String name = confirm != null ? confirm.getString("name", "&#00fc88%amount%") : "&#00fc88%amount%";
      name = name.replace("%amount%", EconomyUtil.formatSell(total));
      List<String> lore = confirm != null ? confirm.getStringList("lore") : List.of("&7Click to sell items");
      Material mat = Material.LIME_STAINED_GLASS_PANE;
      if (confirm != null) {
         try {
            mat = Material.valueOf(confirm.getString("material", "LIME_STAINED_GLASS_PANE"));
         } catch (Exception var12) {
         }
      }

      inv.setItem(slot, ItemUtil.named(mat, name, lore));
   }

   public static double calcTotal(Player player, Inventory inv, int confirmSlot) {
      double total = (double)0.0F;
      double multiplier = EconomySell.get().getPrices().getPlayerMultiplier(player);

      for(int i = 0; i < Math.min(36, inv.getSize()); ++i) {
         if ((i < 36 || i > 44) && i != confirmSlot) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && !stack.getType().isAir() && !stack.getType().name().contains("GLASS_PANE") && !EconomySell.get().getBlacklist().isBlocked(stack)) {
               total += EconomySell.get().getPrices().getSellPrice(player, stack, multiplier);
            }
         }
      }

      return total;
   }

   public static void openWorth(Player player, int page) {
      FileConfiguration cfg = yaml("gui/worth.yml");
      ConfigurationSection root = cfg.getConfigurationSection("worth");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.WORTH);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      int sort = (Integer)MenuSession.SORT.getOrDefault(player.getUniqueId(), 0);
      int filterIdx = (Integer)MenuSession.FILTER.getOrDefault(player.getUniqueId(), 0);
      String search = (String)MenuSession.SEARCH.getOrDefault(player.getUniqueId(), "");
      List<String> filterNames = EconomySell.get().getFilters().names();
      if (filterNames.isEmpty()) {
         filterNames = List.of("All");
      }

      if (filterIdx >= filterNames.size()) {
         filterIdx = 0;
      }

      String filterName = (String)filterNames.get(filterIdx);
      List<Material> mats = new ArrayList();

      for(Material m : Material.values()) {
         if (m.isItem() && !m.isAir() && !m.name().startsWith("LEGACY_") && !EconomySell.get().getBlacklist().isBlocked(m) && EconomySell.get().getFilters().matches(filterName, m)) {
            if (!search.isEmpty()) {
               String q = search.toLowerCase(Locale.ROOT);
               if (!m.name().toLowerCase(Locale.ROOT).contains(q) && !ItemUtil.pretty(m).toLowerCase(Locale.ROOT).contains(q)) {
                  continue;
               }
            }

            mats.add(m);
         }
      }

      Comparator<Material> cmp;
      if (sort == 1) {
         cmp = Comparator.comparingDouble((mx) -> EconomySell.get().getPrices().getBase(mx));
      } else if (sort == 2) {
         cmp = Comparator.comparing(ItemUtil::pretty);
      } else {
         cmp = Comparator.comparingDouble((Material mx) -> EconomySell.get().getPrices().getBase(mx)).reversed();
      }

      mats.sort(cmp);
      int pageSize = 45;
      int maxPage = mats.isEmpty() ? 0 : (mats.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(player.getUniqueId(), page);
      String title = root.getString("title", "&8ᴡᴏʀᴛʜ (Page %page%)").replace("%page%", String.valueOf(page + 1));
      int rows = root.getInt("rows", 6);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      List<String> loreTpl = cfg.getStringList("item-lore");
      if (loreTpl.isEmpty()) {
         loreTpl = List.of("&fPrice: &#00fc88%price%", "&fWith multiplier: &#00fc88%multi_price%");
      }

      int from = page * pageSize;
      int to = Math.min(mats.size(), from + pageSize);

      for(int i = from; i < to; ++i) {
         Material m = (Material)mats.get(i);
         double base = EconomySell.get().getPrices().getBase(m);
         double multi = EconomySell.get().getPrices().getUnitPrice(player, m);
         List<String> lore = new ArrayList();

         for(String line : loreTpl) {
            lore.add(line.replace("%price%", EconomyUtil.formatSell(base)).replace("%multi_price%", EconomyUtil.formatSell(multi)));
         }

         inv.setItem(i - from, ItemUtil.named(m, "&f" + ItemUtil.pretty(m), lore));
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         place(inv, items, "previous-page");
         place(inv, items, "next-page");
         place(inv, items, "refresh");
         place(inv, items, "search");
         if (items.isConfigurationSection("sort")) {
            List<String> lore = new ArrayList(items.getStringList("sort.lore"));
            lore.add("&7Mode: &#00fc88" + (sort == 0 ? "Highest" : (sort == 1 ? "Lowest" : "A-Z")));
            inv.setItem(items.getInt("sort.slot", 47), ItemUtil.named(Material.valueOf(items.getString("sort.material", "CAULDRON")), items.getString("sort.name", "&#00fc88ꜱᴏʀᴛ"), lore));
         }

         if (items.isConfigurationSection("filter")) {
            List<String> lore = new ArrayList(items.getStringList("filter.lore"));
            lore.add("&7Filter: &#00fc88" + filterName);
            inv.setItem(items.getInt("filter.slot", 48), ItemUtil.named(Material.valueOf(items.getString("filter.material", "HOPPER")), items.getString("filter.name", "&#00fc88ꜰɪʟᴛᴇʀ"), lore));
         }
      }

      open(player, inv);
   }

   private static void place(Inventory inv, ConfigurationSection items, String key) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec != null) {
         Material mat;
         try {
            mat = Material.valueOf(sec.getString("material", "STONE"));
         } catch (Exception var6) {
            mat = Material.STONE;
         }

         inv.setItem(sec.getInt("slot", 0), ItemUtil.named(mat, sec.getString("name", " "), sec.getStringList("lore")));
      }
   }

   public static void openSellMulti(Player player) {
      FileConfiguration cfg = yaml("gui/sellmulti.yml");
      ConfigurationSection root = cfg.getConfigurationSection("sellmulti");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SELLMULTI);
      String title = root.getString("title", "&8ꜱᴇʟʟ ᴍᴜʟᴛɪ");
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         for(String key : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(key);
            if (sec != null) {
               String category = sec.getString("category", key);
               UUID id = player.getUniqueId();
               double have = EconomySell.get().getProgress().getSold(id, category);
               double need = EconomySell.get().getProgress().getNextNeeded(id, category);
               double nextMulti = EconomySell.get().getProgress().getNextMulti(id, category);
               double progress = need <= (double)0.0F ? (double)1.0F : Math.min((double)1.0F, have / need);
               String bar = ProgressManager.bar(progress);

               Material mat;
               try {
                  mat = Material.valueOf(sec.getString("material", "STONE"));
               } catch (Exception var25) {
                  mat = Material.STONE;
               }

               List<String> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(line.replace("%next_multi%", String.format("%.1f", nextMulti)).replace("%progress%", String.format("%.0f", progress * (double)100.0F)).replace("%bar%", bar).replace("%multi%", String.format("%.2f", EconomySell.get().getProgress().getCategoryMulti(id, category))));
               }

               String name = sec.getString("name", " ");
               inv.setItem(sec.getInt("slot", 0), ItemUtil.named(mat, name, lore));
            }
         }
      }

      open(player, inv);
   }

   public static void openProgress(Player player, String category) {
      FileConfiguration cfg = yaml("gui/progress.yml");
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.PROGRESS);
      MenuSession.PROGRESS_CATEGORY.put(player.getUniqueId(), category);
      ConfigurationSection titles = cfg.getConfigurationSection("titles-progress-gui");
      String title = "&8ᴘʀᴏɢʀᴇꜱꜱ";
      if (titles != null) {
         Material var10000;
         switch (category) {
            case "crops" -> var10000 = Material.WHEAT;
            case "ores" -> var10000 = Material.DIAMOND;
            case "mobs" -> var10000 = Material.BONE;
            case "natural" -> var10000 = Material.OAK_LEAVES;
            case "tools" -> var10000 = Material.NETHERITE_HELMET;
            case "fish" -> var10000 = Material.TROPICAL_FISH;
            case "books" -> var10000 = Material.ENCHANTED_BOOK;
            case "potions" -> var10000 = Material.POTION;
            default -> var10000 = Material.BRICK;
         }

         Material iconMat = var10000;
         title = titles.getString(iconMat.name(), title);
      }

      int rows = cfg.getInt("rows", 6);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      if (cfg.getBoolean("filler.enabled", true)) {
         Material fill;
         try {
            fill = Material.valueOf(cfg.getString("filler.material", "GRAY_STAINED_GLASS_PANE"));
         } catch (Exception var33) {
            fill = Material.GRAY_STAINED_GLASS_PANE;
         }

         ItemStack pane = ItemUtil.named(fill, cfg.getString("filler.displayname", " "), cfg.getStringList("filler.lore"));

         for(int i = 0; i < inv.getSize(); ++i) {
            inv.setItem(i, pane.clone());
         }
      }

      List<Integer> slots = cfg.getIntegerList("progress-slots");
      UUID id = player.getUniqueId();
      ConfigurationSection levelSec = null;
      File levelFile = new File(EconomySell.get().getDataFolder(), "level.yml");
      FileConfiguration levelCfg = YamlConfiguration.loadConfiguration(levelFile);
      levelSec = levelCfg.getConfigurationSection(category);
      double have = EconomySell.get().getProgress().getSold(id, category);
      if (levelSec != null && !slots.isEmpty()) {
         List<String> keys = new ArrayList(levelSec.getKeys(false));
         keys.sort((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));

         for(int i = 0; i < Math.min(slots.size(), keys.size()); ++i) {
            String key = (String)keys.get(i);
            double need = levelSec.getDouble(key + ".amountNeeded");
            double multi = levelSec.getDouble(key + ".multi");
            double progress = need <= (double)0.0F ? (double)1.0F : Math.min((double)1.0F, have / need);
            String state = have >= need ? "complete" : (have > (double)0.0F && progress > (double)0.0F ? "working" : "incomplete");
            ConfigurationSection st = cfg.getConfigurationSection(state);
            if (st != null) {
               Material mat;
               try {
                  mat = Material.valueOf(st.getString("material", "WHITE_STAINED_GLASS_PANE"));
               } catch (Exception var32) {
                  mat = Material.WHITE_STAINED_GLASS_PANE;
               }

               String bar = ProgressManager.bar(progress);
               List<String> lore = new ArrayList();

               for(String line : st.getStringList("lore")) {
                  lore.add(line.replace("%loading-bar%", bar).replace("%multiplier%", String.format("%.1f", multi)).replace("%progress%", String.format("%.0f", progress * (double)100.0F)).replace("%amount-have%", EconomyUtil.formatSell(have).replace("$", "")).replace("%amount-needed%", EconomyUtil.formatSell(need).replace("$", "")));
               }

               inv.setItem((Integer)slots.get(i), ItemUtil.named(mat, st.getString("displayname", " "), lore));
            }
         }
      }

      ConfigurationSection cats = cfg.getConfigurationSection("categories." + category + ".icon");
      if (cats != null) {
         Material mat;
         try {
            mat = Material.valueOf(cats.getString("material", "STONE"));
         } catch (Exception var31) {
            mat = Material.STONE;
         }

         inv.setItem(cats.getInt("slot", 1), ItemUtil.named(mat, cats.getString("displayname", " "), cats.getStringList("lore")));
      }

      ConfigurationSection back = cfg.getConfigurationSection("back-button");
      if (back != null && back.getBoolean("enabled", true)) {
         Material mat;
         try {
            mat = Material.valueOf(back.getString("material", "RED_STAINED_GLASS_PANE"));
         } catch (Exception var30) {
            mat = Material.RED_STAINED_GLASS_PANE;
         }

         inv.setItem(back.getInt("slot", 45), ItemUtil.named(mat, back.getString("displayname", "&cBack"), back.getStringList("lore")));
      }

      open(player, inv);
   }

   public static void openCategoryItems(Player player, String category, int page) {
      FileConfiguration cfg = yaml("gui/category-items.yml");
      ConfigurationSection root = cfg.getConfigurationSection("category-items");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.CATEGORY_ITEMS);
      MenuSession.PROGRESS_CATEGORY.put(player.getUniqueId(), category);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      List<Material> mats = EconomySell.get().getProgress().materialsInCategory(category);
      mats.sort(Comparator.comparing(ItemUtil::pretty));
      int pageSize = 45;
      int maxPage = mats.isEmpty() ? 0 : (mats.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(player.getUniqueId(), page);
      String title = root.getString("title", "&8%category% ɪᴛᴇᴍꜱ (Page %page%)").replace("%category%", category).replace("%page%", String.valueOf(page + 1));
      int rows = root.getInt("rows", 6);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      List<String> loreTpl = cfg.getStringList("item-lore");
      if (loreTpl.isEmpty()) {
         loreTpl = List.of("&fPrice: &#00fc88%price%", "&fWith multi: &#00fc88%multi_price%");
      }

      int from = page * pageSize;
      int to = Math.min(mats.size(), from + pageSize);

      for(int i = from; i < to; ++i) {
         Material m = (Material)mats.get(i);
         double base = EconomySell.get().getPrices().getBase(m);
         double multi = EconomySell.get().getPrices().getUnitPrice(player, m);
         List<String> lore = new ArrayList();

         for(String line : loreTpl) {
            lore.add(line.replace("%price%", EconomyUtil.formatSell(base)).replace("%multi_price%", EconomyUtil.formatSell(multi)));
         }

         inv.setItem(i - from, ItemUtil.named(m, "&f" + ItemUtil.pretty(m), lore));
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         place(inv, items, "previous-page");
         place(inv, items, "next-page");
         place(inv, items, "back");
      }

      if (inv.getItem(45) == null) {
         inv.setItem(45, ItemUtil.named(Material.ARROW, "&#00fc88ᴘʀᴇᴠɪᴏᴜꜱ", List.of("&fPrevious page")));
      }

      if (inv.getItem(53) == null) {
         inv.setItem(53, ItemUtil.named(Material.ARROW, "&#00fc88ɴᴇxᴛ", List.of("&fNext page")));
      }

      if (inv.getItem(49) == null) {
         inv.setItem(49, ItemUtil.named(Material.ARROW, "&aʙᴀᴄᴋ", List.of("&7Back to progress")));
      }

      open(player, inv);
   }

   public static void openHistory(Player player, int page) {
      openHistory(player, player.getUniqueId(), page);
   }

   public static void openHistory(Player player, UUID target, int page) {
      FileConfiguration cfg = yaml("gui/history.yml");
      ConfigurationSection root = cfg.getConfigurationSection("history");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.HISTORY);
      MenuSession.HISTORY_TARGET.put(player.getUniqueId(), target);
      List<SellRecord> list = EconomySell.get().getHistory().get(target);
      int pageSize = 45;
      int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(player.getUniqueId(), page);
      String title = root.getString("title", "&8ꜱᴇʟʟ ʜɪꜱᴛᴏʀʏ (Page %page%)").replace("%page%", String.valueOf(page + 1));
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, root.getInt("rows", 6) * 9, ColorUtil.parse(title));
      int from = page * pageSize;
      int to = Math.min(list.size(), from + pageSize);
      int slot = 0;

      for(int i = from; i < to && slot < pageSize; ++i) {
         SellRecord r = (SellRecord)list.get(i);
         if (r.getMaterial() != null && !r.getMaterial().isAir() && !r.getMaterial().name().contains("GLASS_PANE")) {
            long ago = System.currentTimeMillis() - r.getTime();
            String time = TimeUnit.MILLISECONDS.toMinutes(ago) + "m ago";
            if (ago > 3600000L) {
               time = TimeUnit.MILLISECONDS.toHours(ago) + "h ago";
            }

            List<String> lore = List.of("&fAmount: &#00fc88" + r.getAmount(), "&fEarned: &#00fc88" + EconomyUtil.formatSell(r.getMoney()), "&fWhen: &#00fc88" + time);
            inv.setItem(slot++, ItemUtil.named(r.getMaterial(), "&f" + ItemUtil.pretty(r.getMaterial()), lore));
         }
      }

      ConfigurationSection items = cfg.getConfigurationSection("items");
      if (items != null) {
         place(inv, items, "previous-page");
         place(inv, items, "next-page");
         place(inv, items, "back");
      }

      open(player, inv);
   }
}
