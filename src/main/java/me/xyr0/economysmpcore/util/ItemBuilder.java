package me.xyr0.economysmpcore.util;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemBuilder {
   private final ItemStack item;
   private final ItemMeta meta;

   public ItemBuilder(Material material) {
      this.item = new ItemStack(material);
      this.meta = this.item.getItemMeta();
   }

   public ItemBuilder name(String name) {
      if (this.meta != null && name != null) {
         this.meta.setDisplayName(ColorUtil.color(name));
      }

      return this;
   }

   public ItemBuilder lore(List<String> lore) {
      if (this.meta != null && lore != null) {
         this.meta.setLore(lore.stream().map(ColorUtil::color).toList());
      }

      return this;
   }

   public ItemBuilder glow(boolean glow) {
      if (this.meta != null) {
         this.meta.setEnchantmentGlintOverride(glow);
      }

      return this;
   }

   public ItemStack build() {
      if (this.meta != null) {
         this.item.setItemMeta(this.meta);
      }

      return this.item;
   }
}
