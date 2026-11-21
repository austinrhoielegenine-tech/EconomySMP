package me.xyr0.astralsmpcore.homes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HomeManager {
   private final AstralHomes plugin;
   private final SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

   public HomeManager(AstralHomes plugin) {
      this.plugin = plugin;
   }

   public void reload() {
   }

   public int maxHomes(Player player) {
      int max = this.plugin.configs().defaultMaxHomes();

      for(int i = 1; i <= 50; ++i) {
         if ((player.hasPermission("home-limit." + i) || player.hasPermission("astralsmpcore.system.homes.limit." + i)) && i > max) {
            max = i;
         }
      }

      return max;
   }

   public boolean canUseSlot(Player player, int number) {
      return number >= 1 && number <= this.maxHomes(player);
   }

   public boolean set(Player player, String id) {
      int num;
      try {
         num = Integer.parseInt(id);
      } catch (NumberFormatException var8) {
         return false;
      }

      if (!this.canUseSlot(player, num)) {
         player.sendMessage(ColorUtil.color(this.plugin.configs().msg("limit").replace("%max_homes%", String.valueOf(this.maxHomes(player)))));
         this.sound(player, "denied");
         return false;
      } else {
         Location loc = player.getLocation();
         List<String> blocked = this.plugin.configs().blockedWorlds();
         if (blocked != null) {
            for(String w : blocked) {
               if (w.equalsIgnoreCase(loc.getWorld().getName())) {
                  player.sendMessage(ColorUtil.color(this.plugin.configs().msg("world-locked").replace("%world%", loc.getWorld().getName())));
                  this.sound(player, "denied");
                  return false;
               }
            }
         }

         StorageManager.PlayerData data = this.plugin.storage().get(player);
         data.set(id, new StorageManager.Home(loc.clone(), this.fmt.format(new Date())));
         data.lastSeen = this.fmt.format(new Date());
         this.plugin.storage().saveAsync(data);
         player.sendMessage(ColorUtil.color(this.plugin.configs().msg("created").replace("%home%", id)));
         this.sound(player, "interact");
         return true;
      }
   }

   public boolean delete(Player player, String id) {
      StorageManager.PlayerData data = this.plugin.storage().get(player);
      if (!data.has(id)) {
         player.sendMessage(ColorUtil.color(this.plugin.configs().msg("missing").replace("%home%", id)));
         return false;
      } else {
         data.remove(id);
         this.plugin.storage().saveAsync(data);
         player.sendMessage(ColorUtil.color(this.plugin.configs().msg("removed").replace("%home%", id)));
         this.sound(player, "interact");
         return true;
      }
   }

   public boolean teleport(Player player, String id) {
      StorageManager.Home home = this.plugin.storage().get(player).get(id);
      if (home == null) {
         player.sendMessage(ColorUtil.color(this.plugin.configs().msg("missing").replace("%home%", id)));
         return false;
      } else {
         return this.plugin.teleports().start(player, id, home.location);
      }
   }

   public void sound(Player player, String key) {
      try {
         String raw = this.plugin.configs().sound(key);
         Sound s = Sound.valueOf(raw.toUpperCase().replace('.', '_').replace('-', '_'));
         SoundUtil.play(player, player.getLocation(), s, 1.0F, 1.0F);
      } catch (Exception var6) {
         try {
            SoundUtil.play(player, player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
         } catch (Exception var5) {
         }
      }

   }
}
