package com.austynzdevv.economysmp;

import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.logging.Level;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import com.austynzdevv.economysmp.data.SystemDataStore;
import com.austynzdevv.economysmp.economy.CoreEconomy;
import com.austynzdevv.economysmp.listener.ElytraFireworkListener;
import com.austynzdevv.economysmp.managers.ConfigManager;
import com.austynzdevv.economysmp.managers.MessageManager;
import com.austynzdevv.economysmp.managers.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomySMP extends JavaPlugin {
   private static EconomySMP instance;
   private ConfigManager configManager;
   private LicenseManager licenseManager;
   private MessageManager messageManager;
   private ModuleManager moduleManager;
   private CommandRegistry commandRegistry;
   private boolean fullyEnabled;

   @Override
   public InputStream getResource(String filename) {
      InputStream in = super.getResource(filename);
      if (in == null && filename != null && filename.endsWith("/config.yml")) {
         // Module configs are stored as <module>/<module>.yml inside the jar
         String dir = filename.substring(0, filename.length() - "/config.yml".length());
         String id = dir.substring(dir.lastIndexOf('/') + 1);
         in = super.getResource(dir + "/" + id + ".yml");
      }

      return in;
   }

   public void onEnable() {
      instance = this;
      this.licenseManager = new LicenseManager(this);
      this.licenseManager.loadAndValidate(this::onLicenseValid, this::onLicenseInvalid);
   }

   private void logCompatibility() {
      String server = Bukkit.getBukkitVersion();
      if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
         this.getLogger().info("ViaVersion detected — cross-version client support enabled.");
      }

      if (Bukkit.getPluginManager().getPlugin("ViaBackwards") != null) {
         this.getLogger().info("ViaBackwards detected — older client support enabled.");
      }

      if (Bukkit.getPluginManager().getPlugin("Geyser-Spigot") != null || Bukkit.getPluginManager().getPlugin("floodgate") != null) {
         this.getLogger().info("Geyser/Floodgate detected — Bedrock compatibility enabled.");
      }

      this.getLogger().info("Server compatibility: " + server);
   }

   private void onLicenseValid() {
      if (!this.fullyEnabled) {
         this.fullyEnabled = true;
         this.getLogger().info("License valid — loading EconomySMP...");
         this.startupStep("Compatibility logging", false, this::logCompatibility);
         this.startupStep("Config bootstrap", false, () -> {
            File mainCfg = new File(this.getDataFolder(), "main.config.yml");
            if (!mainCfg.exists()) {
               InputStream in = this.getResource("main.config.yml");

               try {
                  if (in != null) {
                     Files.copy(in, mainCfg.toPath(), new CopyOption[0]);
                  }
               } catch (Throwable var6) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (in != null) {
                  in.close();
               }
            }

            Files.deleteIfExists((new File(this.getDataFolder(), "config.yml")).toPath());
         });
         this.startupStep("PlayerDataStore init", false, () -> PlayerDataStore.init(this));
         this.startupStep("SystemDataStore init", false, () -> SystemDataStore.init(this));
         this.startupStep("ConfigManager init", false, () -> this.configManager = new ConfigManager(this));
         this.startupStep("Economy init (Vault optional)", false, () -> CoreEconomy.init(this));
         this.startupStep("MessageManager init", false, () -> this.messageManager = new MessageManager(this));
         this.startupStep("CommandRegistry", true, () -> {
            this.commandRegistry = new CommandRegistry(this);
            this.commandRegistry.registerAll();
         });
         this.startupStep("ModuleManager", true, () -> {
            this.moduleManager = new ModuleManager(this);
            this.moduleManager.loadModules();
         });
         this.startupStep("Command re-register", false, () -> {
            if (this.commandRegistry != null) {
               this.commandRegistry.registerAll();
            }

         });
         this.startupStep("PlaceholderAPI hook", false, () -> {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
               (new EconomyPlaceholders(this)).register();
               this.getLogger().info("PlaceholderAPI hooked (%economysmp_*%).");
            }

         });
         this.startupStep("Elytra firework listener", false, () -> this.getServer().getPluginManager().registerEvents(new ElytraFireworkListener(), this));
         this.getLogger().info("EconomySMP by Xyr0 enabled.");
      }
   }

   private void startupStep(String name, boolean severe, StartupAction action) {
      try {
         action.run();
      } catch (Exception ex) {
         this.getLogger().log(severe ? Level.SEVERE : Level.WARNING, name + " failed", ex);
      }

   }

   private void onLicenseInvalid(String status) {
      this.getLogger().severe("================================================");
      this.getLogger().severe(" EconomySMP LICENSE INVALID (" + status + ")");
      this.getLogger().severe(" Only license.yml was created.");
      this.getLogger().severe(" Set a valid LICENSE-KEY in license.yml and restart.");
      this.getLogger().severe("================================================");
   }

   public void onDisable() {
      if (this.moduleManager != null) {
         this.moduleManager.disableAll();
      }

      CoreEconomy.flush();

      try {
         PlayerDataStore.get().flush();
      } catch (Exception var2) {
      }

      this.fullyEnabled = false;
      instance = null;
      this.getLogger().info("EconomySMP disabled.");
   }

   public void reloadAll() {
      if (this.fullyEnabled) {
         this.startupStep("Config reload", false, this::reloadConfig);
         this.startupStep("ConfigManager reload", false, () -> {
            if (this.configManager != null) {
               this.configManager.reload();
            }

         });
         this.startupStep("MessageManager reload", false, () -> {
            if (this.messageManager != null) {
               this.messageManager.reload();
            }

         });
         this.startupStep("Module reload", true, () -> {
            if (this.moduleManager != null) {
               this.moduleManager.reloadModules();
            }

         });
      }
   }

   public static EconomySMP get() {
      return instance;
   }

   public boolean isFullyEnabled() {
      return this.fullyEnabled;
   }

   public ConfigManager configs() {
      return this.configManager;
   }

   public MessageManager messages() {
      return this.messageManager;
   }

   public ModuleManager modules() {
      return this.moduleManager;
   }

   public CommandRegistry commands() {
      return this.commandRegistry;
   }

   public LicenseManager license() {
      return this.licenseManager;
   }

   @FunctionalInterface
   private interface StartupAction {
      void run() throws Exception;
   }
}
