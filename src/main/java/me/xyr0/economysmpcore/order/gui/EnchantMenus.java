package me.xyr0.economysmpcore.order.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.xyr0.economysmpcore.order.AstralOrder;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.ItemUtil;
import me.xyr0.economysmpcore.util.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantMenus {
   private EnchantMenus() {
   }

   private static FileConfiguration yaml() {
      File file = new File(new File(AstralOrder.get().getDataFolder(), "gui"), "enchantment-gui.yml");
      if (!file.exists()) {
         try {
            AstralOrder.get().saveResource("gui/enchantment-gui.yml", false);
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

   public static boolean isEnchantable(Material material) {
      ItemStack test = new ItemStack(material);

      for(Enchantment enchantment : Enchantment.values()) {
         if (enchantment.canEnchantItem(test)) {
            return true;
         }
      }

      return false;
   }

   public static List<Enchantment> applicable(Material material) {
      ItemStack test = new ItemStack(material);
      List<Enchantment> list = new ArrayList();

      for(Enchantment enchantment : Enchantment.values()) {
         if (enchantment.canEnchantItem(test)) {
            list.add(enchantment);
         }
      }

      list.sort((a, b) -> a.getKey().getKey().compareToIgnoreCase(b.getKey().getKey()));
      return list;
   }

   public static void open(Player player, ItemStack base, int page) {
      FileConfiguration cfg = yaml();
      UUID id = player.getUniqueId();
      MenuSession.VIEW.put(id, MenuSession.View.ENCHANTS);
      MenuSession.ENCHANT_BASE.put(id, base.clone());
      MenuSession.PAGE.put(id, page);
      MenuSession.enchantPick(id);
      String title = cfg.getString("title", "&8ᴘɪᴄᴋ ᴇɴᴄʜᴀɴᴛᴍᴇɴᴛs");
      int rows = cfg.getInt("rows", 6);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = cfg.getConfigurationSection("items");
      List<Enchantment> list = applicable(base.getType());
      int pageSize = 36;
      int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(id, page);
      Set<Enchantment> selected = MenuSession.enchantPick(id);
      int from = page * pageSize;
      int to = Math.min(list.size(), from + pageSize);
      int slot = 2;

      for(int i = from; i < to; ++i) {
         while(slot == 9 || slot == 10 || slot == 1) {
            ++slot;
         }

         if (slot >= 45) {
            break;
         }

         Enchantment ench = (Enchantment)list.get(i);
         boolean on = selected.contains(ench);
         ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
         ItemMeta meta = book.getItemMeta();
         String name = cfg.getString("items.enchanted-book.name", "&b%enchant%").replace("%enchant%", pretty(ench));
         if (meta != null) {
            meta.displayName(ColorUtil.parse(name));
            List<Component> lore = new ArrayList();
            String mark = on ? cfg.getString("items.enchanted-book.lore-selected", "&aSelected") : cfg.getString("items.enchanted-book.lore-unselected", "&fClick to select");
            lore.add(ColorUtil.parse(mark));
            lore.add(ColorUtil.parse("&7Max level: &f" + ench.getMaxLevel()));
            meta.lore(lore);
            if (meta instanceof EnchantmentStorageMeta) {
               EnchantmentStorageMeta storage = (EnchantmentStorageMeta)meta;
               storage.addStoredEnchant(ench, ench.getMaxLevel(), true);
               book.setItemMeta(storage);
            } else {
               book.setItemMeta(meta);
            }
         }

         inv.setItem(slot++, book);
      }

      if (items != null) {
         ConfigurationSection bg = items.getConfigurationSection("background");
         if (bg != null) {
            ItemStack pane = GuiItems.fromSection(bg, Map.of());

            for(int s : bg.getIntegerList("slots")) {
               inv.setItem(s, pane.clone());
            }
         }

         ConfigurationSection display = items.getConfigurationSection("display-item");
         if (display != null) {
            ItemStack show = base.clone();
            String fmt = display.getString("name-format", "&b%item%").replace("%item%", ItemUtil.pretty(base.getType()));
            ItemMeta meta = show.getItemMeta();
            if (meta != null) {
               meta.displayName(ColorUtil.parse(fmt));
               show.setItemMeta(meta);
            }

            inv.setItem(display.getInt("slot", 0), show);
         }

         if (items.isConfigurationSection("cancel")) {
            inv.setItem(items.getInt("cancel.slot", 46), GuiItems.fromSection(items.getConfigurationSection("cancel"), Map.of()));
         }

         if (items.isConfigurationSection("confirm")) {
            inv.setItem(items.getInt("confirm.slot", 52), GuiItems.fromSection(items.getConfigurationSection("confirm"), Map.of()));
         }

         if (items.isConfigurationSection("next-page")) {
            inv.setItem(items.getInt("next-page.slot", 53), GuiItems.fromSection(items.getConfigurationSection("next-page"), Map.of()));
         }

         if (items.isConfigurationSection("back-page")) {
            inv.setItem(items.getInt("back-page.slot", 45), GuiItems.fromSection(items.getConfigurationSection("back-page"), Map.of()));
         }
      }

      open(player, inv);
   }

   public static ItemStack applySelected(UUID id) {
      ItemStack base = (ItemStack)MenuSession.ENCHANT_BASE.get(id);
      if (base == null) {
         return new ItemStack(Material.STONE);
      } else {
         ItemStack out = base.clone();
         out.setAmount(1);

         for(Enchantment ench : MenuSession.enchantPick(id)) {
            out.addUnsafeEnchantment(ench, ench.getMaxLevel());
         }

         return out;
      }
   }

   private static String pretty(Enchantment ench) {
      String key = ench.getKey().getKey().replace('_', ' ');
      StringBuilder sb = new StringBuilder();

      for(String part : key.split(" ")) {
         if (!part.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase(Locale.ROOT));
         }
      }

      return sb.toString();
   }
}
