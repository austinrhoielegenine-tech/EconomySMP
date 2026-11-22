package com.austynzdevv.nyrethacore.sell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlacklistManager {
   private final EconomySell plugin;
   private final Set<Material> blocked = new HashSet();

   public BlacklistManager(EconomySell plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.blocked.clear();
      List<String> list = this.plugin.getConfig().getStringList("blacklist");
      if (list != null) {
         for(String s : list) {
            try {
               this.blocked.add(Material.valueOf(s.toUpperCase()));
            } catch (Exception var5) {
            }
         }

      }
   }

   public boolean isBlocked(ItemStack item) {
      return item != null && this.blocked.contains(item.getType());
   }

   public boolean isBlocked(Material material) {
      return this.blocked.contains(material);
   }

   public void add(Material material) {
      this.blocked.add(material);
      this.save();
   }

   public void remove(Material material) {
      this.blocked.remove(material);
      this.save();
   }

   private void save() {
      List<String> list = this.blocked.stream().map(Enum::name).toList();
      this.plugin.getConfig().set("blacklist", list);
      this.plugin.saveConfig();
   }
}
