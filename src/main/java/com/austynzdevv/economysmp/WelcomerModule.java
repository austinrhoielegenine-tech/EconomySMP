package com.austynzdevv.economysmp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.Tasks;
import com.austynzdevv.economysmp.util.TextUtil;
import com.austynzdevv.economysmp.warps.EconomyWarps;
import com.austynzdevv.economysmp.warps.NamedLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WelcomerModule implements Module, Listener {
   private final EconomySMP plugin;
   private FileConfiguration cfg;
   private File configFile;
   private File dataFile;
   private FileConfiguration data;
   private int registered;

   public WelcomerModule(EconomySMP plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "welcomer";
   }

   public void onEnable() {
      File folder = this.plugin.getDataFolder();
      folder.mkdirs();
      this.configFile = new File(folder, "welcomer.yml");
      if (!this.configFile.exists()) {
         try {
            this.plugin.saveResource("welcomer.yml", false);
         } catch (Exception var3) {
         }
      }

      this.cfg = this.configFile.exists() ? YamlConfiguration.loadConfiguration(this.configFile) : new YamlConfiguration();
      this.migrateConfig();
      this.dataFile = PlayerDataStore.get().file();
      this.data = PlayerDataStore.get().config();
      this.registered = this.data.getInt("registered-count", 0);
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
   }

   public void onDisable() {
      if (this.data == null) {
         return;
      }

      this.data.set("registered-count", this.registered);
      PlayerDataStore.get().save();
   }

   public void reload() {
      if (this.configFile == null) {
         this.configFile = new File(this.plugin.getDataFolder(), "welcomer.yml");
      }

      if (this.configFile.exists()) {
         this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
         this.migrateConfig();
      }

   }

   private void migrateConfig() {
      boolean changed = false;
      if (this.cfg.contains("first-join.teleport-spawn") || this.cfg.contains("first-join.line1") || this.cfg.contains("first-join.line2")) {
         boolean firstSpawn = this.cfg.getBoolean("first-join.teleport-spawn", false);
         String line1 = this.cfg.getString("first-join.line1", "&8>> &7Welcome &#37BFF9&n%player%&7 to the server.");
         String line2 = this.cfg.getString("first-join.line2", "&8>> &7We now have &#37BFF9%count% &7players registered!");
         this.cfg.set("first-join-spawn", firstSpawn);
         this.cfg.set("first-join-announcement", List.of(line1, line2));
         this.cfg.set("first-join", (Object)null);
         changed = true;
      }

      if (this.cfg.contains("join") && !this.cfg.contains("join-message")) {
         this.cfg.set("join-message", this.cfg.getString("join"));
         this.cfg.set("join", (Object)null);
         changed = true;
      }

      if (this.cfg.contains("leave") && !this.cfg.contains("leave-message")) {
         this.cfg.set("leave-message", this.cfg.getString("leave"));
         this.cfg.set("leave", (Object)null);
         changed = true;
      }

      if (changed) {
         try {
            this.cfg.save(this.configFile);
         } catch (IOException var5) {
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onJoin(PlayerJoinEvent e) {
      Player p = e.getPlayer();
      e.joinMessage((Component)null);

      try {
         this.reload();
      } catch (Exception var10) {
      }

      FileConfiguration var10000 = this.data;
      String var10001 = String.valueOf(p.getUniqueId());
      boolean first = !var10000.getBoolean("joined." + var10001, false);
      if (first) {
         this.data.set("joined." + String.valueOf(p.getUniqueId()), true);
         ++this.registered;
         this.data.set("registered-count", this.registered);
         this.saveData();
         List<String> announcement = this.cfg.getStringList("first-join-announcement");
         if (announcement.isEmpty()) {
            String line1 = this.cfg.getString("first-join-announcement", "&8>> &7Welcome &#37BFF9&n%player%&7 to the server.");
            announcement = List.of(line1);
         }

         for(Player online : Bukkit.getOnlinePlayers()) {
            for(String line : announcement) {
               TextUtil.send(online, line.replace("%player%", p.getName()).replace("%count%", String.valueOf(this.registered)));
            }
            String rewardPrompt = this.cfg.getString("welcome-reward", "&7Say &a&lWELCOME &7to %player% to receive a reward");
            TextUtil.send(online, rewardPrompt.replace("%player%", p.getName()));

            try {
               SoundUtil.play(online, online.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.4F, 1.5F);
            } catch (Exception var9) {
            }
         }
      } else {
         String join = this.cfg.getString("join-message", "&#37BFF9%player% &7joined the server").replace("%player%", p.getName());

         for(Player online : Bukkit.getOnlinePlayers()) {
            if (!SettingsModule.isServerChatDisabled(online.getUniqueId())) {
               TextUtil.send(online, join);
            }
         }
      }

      if (first && this.cfg.getBoolean("first-join-spawn", true)) {
         Tasks.runLater(this.plugin, () -> {
            if (p.isOnline()) {
               try {
                  EconomyWarps warps = EconomyWarps.get();
                  if (warps != null && warps.getWarps() != null) {
                     NamedLocation spawn = warps.getWarps().defaultSpawn();
                     if (spawn != null && spawn.toLocation() != null) {
                        p.teleport(spawn.toLocation());
                        return;
                     }
                  }
               } catch (Exception var3) {
               }

               p.teleport(p.getWorld().getSpawnLocation());
            }
         }, 1L);
      }

      if (!first && this.cfg.getBoolean("join-spawn", false)) {
         Tasks.runLater(this.plugin, () -> this.teleportToWarpSpawn(p), 1L);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onQuit(PlayerQuitEvent e) {
      e.quitMessage((Component)null);
      String leave = this.cfg.getString("leave-message", "&#37BFF9%player% &7left the server").replace("%player%", e.getPlayer().getName());

      for(Player online : Bukkit.getOnlinePlayers()) {
         if (!SettingsModule.isServerChatDisabled(online.getUniqueId())) {
            TextUtil.send(online, leave);
         }
      }

   }

   private void teleportToWarpSpawn(Player p) {
      if (p.isOnline()) {
         try {
            EconomyWarps warps = EconomyWarps.get();
            if (warps != null && warps.getWarps() != null) {
               NamedLocation spawn = warps.getWarps().defaultSpawn();
               if (spawn != null && spawn.toLocation() != null) {
                  p.teleport(spawn.toLocation());
                  return;
               }
            }
         } catch (Exception var4) {
         }

         p.teleport(p.getWorld().getSpawnLocation());
      }
   }

   private void saveData() {
      PlayerDataStore.get().save();
   }
}
