package com.austynzdevv.novacore.homes;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
   private final EconomyHomes plugin;
   private FileConfiguration config;
   private FileConfiguration lang;
   private FileConfiguration mainGui;
   private FileConfiguration confirmGui;

   public ConfigManager(EconomyHomes plugin) {
      this.plugin = plugin;
   }

   public void load() {
      this.plugin.saveDefaultConfig();
      this.config = this.plugin.getConfig();
      File langFile = new File(this.plugin.getDataFolder(), "lang.yml");
      if (!langFile.exists()) {
         try {
            this.plugin.saveResource("lang.yml", false);
         } catch (Exception var8) {
         }
      }

      this.lang = langFile.exists() ? YamlConfiguration.loadConfiguration(langFile) : new YamlConfiguration();
      File guiDir = new File(this.plugin.getDataFolder(), "gui");
      guiDir.mkdirs();
      File mainFile = new File(guiDir, "main.gui.yml");
      if (!mainFile.exists()) {
         try {
            this.plugin.saveResource("gui/main.gui.yml", false);
         } catch (Exception var7) {
         }
      }

      this.mainGui = mainFile.exists() ? YamlConfiguration.loadConfiguration(mainFile) : new YamlConfiguration();
      File confirmFile = new File(guiDir, "confirm.gui.yml");
      if (!confirmFile.exists()) {
         try {
            this.plugin.saveResource("gui/confirm.gui.yml", false);
         } catch (Exception var6) {
         }
      }

      this.confirmGui = confirmFile.exists() ? YamlConfiguration.loadConfiguration(confirmFile) : new YamlConfiguration();
   }

   public void reload() {
      this.load();
   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public FileConfiguration getLang() {
      return this.lang;
   }

   public FileConfiguration getMainGui() {
      return this.mainGui;
   }

   public FileConfiguration getConfirmGui() {
      return this.confirmGui;
   }

   public String msg(String key) {
      String m = this.lang.getString("text." + key, key);
      if (this.lang.getBoolean("use-prefix", false)) {
         String var10000 = this.lang.getString("prefix", "");
         m = var10000 + " " + m;
      }

      return m;
   }

   public String bar(String key) {
      return this.lang.getString("bars." + key, "");
   }

   public String actionBar(String key) {
      String fromBar = this.lang.getString("bars." + key, (String)null);
      return fromBar != null ? fromBar : this.lang.getString("actionbar." + key, this.config.getString("actionbar." + key, ""));
   }

   public int defaultMaxHomes() {
      return this.config.getInt("max-homes", this.config.getInt("free-slots", this.config.getInt("defaults.max-homes", 2)));
   }

   public List<String> blockedWorlds() {
      List<String> list = this.config.getStringList("blocked-worlds");
      if (list == null || list.isEmpty()) {
         list = this.config.getStringList("worlds.blocked");
      }

      return list != null ? list : Collections.emptyList();
   }

   public int teleportDelay() {
      return this.config.getInt("teleport.delay", this.config.getInt("teleport-delay", this.config.getInt("settings.warmup-seconds", 5)));
   }

   public double teleportDistance() {
      return this.config.getDouble("teleport.cancel-distance", this.config.getDouble("teleport-distance", (double)0.5F));
   }

   public String sound(String key) {
      String path = "sounds." + key;
      String name = this.config.getString(path, (String)null);
      if (name == null) {
         name = this.lang.getString(path, (String)null);
      }

      if (name != null && !name.isEmpty()) {
         return name;
      } else {
         String var10000;
         switch (key) {
            case "denied":
               var10000 = "ENTITY_VILLAGER_NO";
               break;
            case "interact":
            case "click":
               var10000 = "UI_BUTTON_CLICK";
               break;
            case "teleport":
            case "success":
            case "arrive":
               var10000 = "ENTITY_ENDERMAN_TELEPORT";
               break;
            case "countdown":
               var10000 = "BLOCK_NOTE_BLOCK_PLING";
               break;
            case "abort":
               var10000 = "ENTITY_VILLAGER_NO";
               break;
            case "reload":
               var10000 = "ENTITY_PLAYER_LEVELUP";
               break;
            default:
               var10000 = "UI_BUTTON_CLICK";
         }

         return var10000;
      }
   }

   public boolean countdownChat() {
      return this.lang.getBoolean("countdown.chat.on", true);
   }

   public boolean countdownActionbar() {
      return this.lang.getBoolean("countdown.actionbar.on", true);
   }

   public List<String> countdownChatLines() {
      return this.lang.getStringList("countdown.chat.lines");
   }

   public List<String> countdownActionbarLines() {
      return this.lang.getStringList("countdown.actionbar.lines");
   }

   public boolean cooldownChat() {
      return this.countdownChat();
   }

   public boolean cooldownActionbar() {
      return this.countdownActionbar();
   }

   public List<String> cooldownChatMsgs() {
      return this.countdownChatLines();
   }

   public List<String> cooldownActionbarMsgs() {
      return this.countdownActionbarLines();
   }
}
