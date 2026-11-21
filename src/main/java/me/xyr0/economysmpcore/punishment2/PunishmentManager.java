package me.xyr0.economysmpcore.punishment2;

import java.io.File;
import java.util.UUID;
import me.xyr0.economysmpcore.punishment2.util.BanIdUtil;
import me.xyr0.economysmpcore.punishment2.util.TimeUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PunishmentManager {
   private final AstralPunishment plugin;

   public PunishmentManager(AstralPunishment plugin) {
      this.plugin = plugin;
   }

   public Punishment punish(UUID uuid, String player, String reason, String staff) {
      File file = new File(this.plugin.getDataFolder(), "reasons.yml");
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      ConfigurationSection selected = null;

      for(String key : config.getKeys(false)) {
         ConfigurationSection section = config.getConfigurationSection(key);
         if (section != null && section.getString("reason").equalsIgnoreCase(reason)) {
            selected = section;
            break;
         }
      }

      if (selected == null) {
         return null;
      } else {
         String type = selected.getString("type", "BAN");
         int offense = this.getNextOffense(uuid, reason);
         String duration = selected.getString("offenses." + offense);
         if (duration == null) {
            int var10001 = this.getLastOffense(selected);
            duration = selected.getString("offenses." + var10001);
         }

         long expire = TimeUtil.getExpireTime(duration);
         String banId = type.equalsIgnoreCase("BAN") ? BanIdUtil.generate() : "NONE";
         Punishment punishment = new Punishment(uuid, player, type, reason, duration, expire, staff, offense, banId, System.currentTimeMillis(), true);
         this.plugin.getDatabaseManager().savePunishment(punishment);
         return punishment;
      }
   }

   private int getNextOffense(UUID uuid, String reason) {
      int amount = 0;

      for(Punishment p : this.plugin.getDatabaseManager().getHistory(uuid)) {
         if (p.getReason().equalsIgnoreCase(reason)) {
            ++amount;
         }
      }

      return amount + 1;
   }

   private int getLastOffense(ConfigurationSection section) {
      int last = 1;

      for(String key : section.getConfigurationSection("offenses").getKeys(false)) {
         int number = Integer.parseInt(key);
         if (number > last) {
            last = number;
         }
      }

      return last;
   }
}
