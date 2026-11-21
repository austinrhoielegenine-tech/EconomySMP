package me.xyr0.economysmpcore.rules.gui;

import java.util.List;
import me.xyr0.economysmpcore.rules.AstralRules;
import me.xyr0.economysmpcore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RulesGUI {
   public static final String GUI_ID = "astralrules_gui";

   public static void open(Player player, AstralRules plugin) {
      String title = plugin.getConfig().getString("gui.title", "&8EconomySMP Rules");
      int size = plugin.getConfig().getInt("gui.size", 27);
      Inventory inventory = Bukkit.createInventory(player, size, ColorUtil.parse(title));
      createItem(inventory, plugin, "items.chat-rules");
      createItem(inventory, plugin, "items.server-rules");
      player.openInventory(inventory);
   }

   private static void createItem(Inventory inventory, AstralRules plugin, String path) {
      ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
      if (section != null) {
         int slot = section.getInt("slot");

         Material material;
         try {
            material = Material.valueOf(section.getString("material", "KNOWLEDGE_BOOK"));
         } catch (Exception var9) {
            material = Material.KNOWLEDGE_BOOK;
         }

         ItemStack item = new ItemStack(material);
         ItemMeta meta = item.getItemMeta();
         if (meta != null) {
            meta.displayName(ColorUtil.parse(section.getString("name", "Rules")));
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
               meta.lore(lore.stream().map(ColorUtil::parse).toList());
            }

            item.setItemMeta(meta);
         }

         inventory.setItem(slot, item);
      }
   }
}
