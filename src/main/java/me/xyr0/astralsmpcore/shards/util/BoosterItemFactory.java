package me.xyr0.astralsmpcore.shards.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.xyr0.astralsmpcore.shards.AstralShards;
import me.xyr0.astralsmpcore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class BoosterItemFactory {
   private final AstralShards plugin;
   private final NamespacedKey key;
   private FileConfiguration itemCfg;

   public BoosterItemFactory(AstralShards plugin) {
      this.plugin = plugin;
      this.key = new NamespacedKey(plugin, "shard_booster");
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "tools/shardbooster.yml");
      if (!file.exists()) {
         this.plugin.saveResource("tools/shardbooster.yml", false);
      }

      this.itemCfg = YamlConfiguration.loadConfiguration(file);
   }

   public ItemStack create() {
      Material mat = Material.GLASS_BOTTLE;

      try {
         mat = Material.valueOf(this.itemCfg.getString("material", "GLASS_BOTTLE").toUpperCase());
      } catch (Exception var12) {
      }

      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      } else {
         double speed = this.plugin.getConfig().getDouble("shard-booster.speed-multiplier", (double)4.0F);
         long hours = this.plugin.getConfig().getLong("shard-booster.duration-hours", 24L);
         String name = this.itemCfg.getString("name", "&#A20AD6ꜱʜᴀʀᴅ ʙᴏᴏꜱᴛᴇʀ");
         meta.displayName(ColorUtil.parse(name));
         List<Component> lore = new ArrayList();

         for(String line : this.itemCfg.getStringList("lore")) {
            lore.add(ColorUtil.parse(line.replace("%speed%", String.valueOf(speed).replace(".0", "")).replace("%time%", String.valueOf(hours)).replace("%amount%", String.valueOf(speed).replace(".0", ""))));
         }

         meta.lore(lore);
         int cmd = this.itemCfg.getInt("custom-model-data", 0);
         if (cmd > 0) {
            meta.setCustomModelData(cmd);
         }

         meta.getPersistentDataContainer().set(this.key, PersistentDataType.BYTE, (byte)1);
         item.setItemMeta(meta);
         return item;
      }
   }

   public boolean isBooster(ItemStack item) {
      return item != null && item.hasItemMeta() ? item.getItemMeta().getPersistentDataContainer().has(this.key, PersistentDataType.BYTE) : false;
   }

   public FileConfiguration getItemCfg() {
      return this.itemCfg;
   }
}
