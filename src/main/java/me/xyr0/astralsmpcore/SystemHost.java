package me.xyr0.astralsmpcore;

import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SystemHost implements Plugin {
   protected JavaPlugin core;
   protected File dataFolder;
   protected FileConfiguration config;
   protected final String systemId;
   private boolean enabled;

   public SystemHost(String systemId) {
      this.systemId = systemId;
   }

   public void init(JavaPlugin core, File dataFolder) {
      this.core = core;
      this.dataFolder = dataFolder;
      if (!dataFolder.equals(core.getDataFolder()) && !dataFolder.exists()) {
         dataFolder.mkdirs();
      }

      this.enabled = true;
   }

   private boolean isFlat() {
      return this.dataFolder.equals(this.core.getDataFolder());
   }

   private File configFile() {
      return new File(this.dataFolder, this.isFlat() ? this.systemId + ".yml" : "config.yml");
   }

   public JavaPlugin getCore() {
      return this.core;
   }

   public @NotNull File getDataFolder() {
      return this.dataFolder;
   }

   public @NotNull PluginDescriptionFile getDescription() {
      return this.core.getDescription();
   }

   public @NotNull FileConfiguration getConfig() {
      if (this.config == null) {
         this.reloadConfig();
      }

      return this.config;
   }

   public @Nullable InputStream getResource(@NotNull String filename) {
      InputStream in = this.core.getResource(this.systemId + "/" + filename);
      if (in != null) {
         return in;
      } else {
         if (filename.equals("config.yml")) {
            in = this.core.getResource(this.systemId + ".yml");
            if (in != null) {
               return in;
            }

            if (this.systemId.equals("ec")) {
               in = this.core.getResource("enderchest.yml");
               if (in != null) {
                  return in;
               }
            }

            if (this.systemId.equals("wl") || this.systemId.equals("whitelist")) {
               in = this.core.getResource("wl.yml");
               if (in != null) {
                  return in;
               }
            }
         }

         return this.core.getResource(filename);
      }
   }

   public void saveConfig() {
      try {
         if (this.config != null) {
            this.config.save(this.configFile());
         }
      } catch (Exception e) {
         Logger var10000 = this.getLogger();
         String var10001 = this.systemId;
         var10000.warning("[" + var10001 + "] saveConfig: " + e.getMessage());
      }

   }

   public void saveDefaultConfig() {
      File f = this.configFile();
      if (!f.exists()) {
         this.saveResource("config.yml", false);
      }

      if (this.config == null) {
         this.config = YamlConfiguration.loadConfiguration(f);
      }

   }

   public void saveResource(@NotNull String resourcePath, boolean replace) {
      File out = resourcePath.equals("config.yml") && this.isFlat() ? this.configFile() : new File(this.dataFolder, resourcePath);
      if (!out.exists() || replace) {
         out.getParentFile().mkdirs();
         String[] tryPaths = new String[]{this.systemId + "/" + resourcePath, resourcePath, resourcePath.equals("config.yml") ? this.systemId + ".yml" : null, resourcePath.equals("config.yml") && this.systemId.equals("ec") ? "enderchest.yml" : null, !resourcePath.equals("config.yml") || !this.systemId.equals("wl") && !this.systemId.equals("whitelist") ? null : "wl.yml"};

         for(String rp : tryPaths) {
            if (rp != null) {
               try {
                  InputStream in = this.core.getResource(rp);

                  label92: {
                     try {
                        if (in != null) {
                           OutputStream os = Files.newOutputStream(out.toPath());

                           try {
                              in.transferTo(os);
                           } catch (Throwable var15) {
                              if (os != null) {
                                 try {
                                    os.close();
                                 } catch (Throwable var14) {
                                    var15.addSuppressed(var14);
                                 }
                              }

                              throw var15;
                           }

                           if (os != null) {
                              os.close();
                           }
                           break label92;
                        }
                     } catch (Throwable var16) {
                        if (in != null) {
                           try {
                              in.close();
                           } catch (Throwable var13) {
                              var16.addSuppressed(var13);
                           }
                        }

                        throw var16;
                     }

                     if (in != null) {
                        in.close();
                     }
                     continue;
                  }

                  if (in != null) {
                     in.close();
                  }

                  return;
               } catch (Exception e) {
                  this.getLogger().warning("[" + this.systemId + "] saveResource " + resourcePath + ": " + e.getMessage());
               }
            }
         }

      }
   }

   public void reloadConfig() {
      File f = this.configFile();
      if (!f.exists()) {
         this.saveDefaultConfig();
      }

      this.config = YamlConfiguration.loadConfiguration(f);
   }

   public @NotNull PluginLoader getPluginLoader() {
      return this.core.getPluginLoader();
   }

   public @NotNull Server getServer() {
      return this.core.getServer();
   }

   public boolean isEnabled() {
      return this.enabled && this.core != null && this.core.isEnabled();
   }

   public void setEnabledState(boolean v) {
      this.enabled = v;
   }

   public void onDisable() {
      this.enabled = false;
   }

   public void onLoad() {
   }

   public void onEnable() {
      this.enabled = true;
   }

   public boolean isNaggable() {
      return this.core.isNaggable();
   }

   public void setNaggable(boolean canNag) {
      this.core.setNaggable(canNag);
   }

   public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
      return null;
   }

   public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String worldName, @Nullable String id) {
      return null;
   }

   public @NotNull Logger getLogger() {
      return this.core.getLogger();
   }

   public @NotNull String getName() {
      return "AstralSMPcore-" + this.systemId;
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      return false;
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
      return null;
   }

   public PluginCommand getCommand(String name) {
      return this.core.getCommand(name);
   }

   public LifecycleEventManager getLifecycleManager() {
      return this.core.getLifecycleManager();
   }

   public PluginMeta getPluginMeta() {
      return this.core.getPluginMeta();
   }

   public @NotNull String namespace() {
      return "astralsmpcore";
   }

   public ComponentLogger getComponentLogger() {
      return this.core.getComponentLogger();
   }

   public List<String> getPluginMetaPermissions() {
      try {
         return this.core.getDescription().getPermissions().stream().map((p) -> p.getName()).toList();
      } catch (Exception var2) {
         return List.of();
      }
   }
}
