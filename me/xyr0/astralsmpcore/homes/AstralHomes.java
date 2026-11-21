package me.xyr0.astralsmpcore.homes;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;
import me.xyr0.astralsmpcore.homes.gui.ConfirmGUI;
import me.xyr0.astralsmpcore.homes.gui.MainGUI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AstralHomes {
   private static AstralHomes instance;
   private JavaPlugin core;
   private File dataFolder;
   private FileConfiguration config;
   private ConfigManager configManager;
   private StorageManager storageManager;
   private HomeManager homeManager;
   private TeleportManager teleportManager;
   private MainGUI mainGUI;
   private ConfirmGUI confirmGUI;

   public void init(JavaPlugin core, File dataFolder) {
      this.core = core;
      this.dataFolder = dataFolder;
      if (!dataFolder.exists()) {
         dataFolder.mkdirs();
      }

      instance = this;
   }

   public void start() {
      this.saveDefaultConfig();
      this.configManager = new ConfigManager(this);
      this.configManager.load();
      this.storageManager = new StorageManager(this);
      this.homeManager = new HomeManager(this);
      this.teleportManager = new TeleportManager(this);
      this.mainGUI = new MainGUI(this);
      this.confirmGUI = new ConfirmGUI(this);
      this.getLogger().info("Homes system enabled.");
   }

   public void stop() {
      if (this.storageManager != null) {
         this.storageManager.saveAll();
      }

      if (this.teleportManager != null) {
         this.teleportManager.cancelAll();
      }

      this.getLogger().info("Homes system disabled.");
   }

   public void reloadPlugin() {
      this.reloadConfig();
      if (this.configManager != null) {
         this.configManager.reload();
      }

      if (this.storageManager != null) {
         this.storageManager.reload();
      }

      if (this.homeManager != null) {
         this.homeManager.reload();
      }

   }

   public File getDataFolder() {
      return this.dataFolder;
   }

   public Logger getLogger() {
      return this.core.getLogger();
   }

   public JavaPlugin getCore() {
      return this.core;
   }

   public FileConfiguration getConfig() {
      if (this.config == null) {
         this.reloadConfig();
      }

      return this.config;
   }

   public void reloadConfig() {
      File f = new File(this.dataFolder, "config.yml");
      if (!f.exists()) {
         this.saveDefaultConfig();
      }

      this.config = YamlConfiguration.loadConfiguration(f);
   }

   public void saveDefaultConfig() {
      File f = new File(this.dataFolder, "config.yml");
      if (!f.exists()) {
         this.saveResource("config.yml", false);
      }

      if (this.config == null) {
         this.config = YamlConfiguration.loadConfiguration(f);
      }

   }

   public void saveResource(String path, boolean replace) {
      File out = new File(this.dataFolder, path);
      if (!out.exists() || replace) {
         out.getParentFile().mkdirs();
         String resourcePath = "homes/" + path;

         try {
            InputStream in = this.core.getResource(resourcePath);

            label139: {
               label153: {
                  try {
                     if (in == null) {
                        InputStream in2 = this.core.getResource(path);

                        label142: {
                           try {
                              if (in2 != null) {
                                 OutputStream os = Files.newOutputStream(out.toPath());

                                 try {
                                    in2.transferTo(os);
                                 } catch (Throwable var15) {
                                    if (os != null) {
                                       try {
                                          os.close();
                                       } catch (Throwable var13) {
                                          var15.addSuppressed(var13);
                                       }
                                    }

                                    throw var15;
                                 }

                                 if (os != null) {
                                    os.close();
                                 }
                                 break label142;
                              }
                           } catch (Throwable var16) {
                              if (in2 != null) {
                                 try {
                                    in2.close();
                                 } catch (Throwable var12) {
                                    var16.addSuppressed(var12);
                                 }
                              }

                              throw var16;
                           }

                           if (in2 != null) {
                              in2.close();
                           }
                           break label153;
                        }

                        if (in2 != null) {
                           in2.close();
                        }
                        break label139;
                     }

                     OutputStream os = Files.newOutputStream(out.toPath());

                     try {
                        in.transferTo(os);
                     } catch (Throwable var14) {
                        if (os != null) {
                           try {
                              os.close();
                           } catch (Throwable var11) {
                              var14.addSuppressed(var11);
                           }
                        }

                        throw var14;
                     }

                     if (os != null) {
                        os.close();
                     }
                  } catch (Throwable var17) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var10) {
                           var17.addSuppressed(var10);
                        }
                     }

                     throw var17;
                  }

                  if (in != null) {
                     in.close();
                  }

                  return;
               }

               if (in != null) {
                  in.close();
               }

               return;
            }

            if (in != null) {
               in.close();
            }

         } catch (Exception e) {
            this.getLogger().warning("Could not save resource " + path + ": " + e.getMessage());
         }
      }
   }

   public static AstralHomes get() {
      return instance;
   }

   public ConfigManager configs() {
      return this.configManager;
   }

   public StorageManager storage() {
      return this.storageManager;
   }

   public HomeManager homes() {
      return this.homeManager;
   }

   public TeleportManager teleports() {
      return this.teleportManager;
   }

   public MainGUI mainGui() {
      return this.mainGUI;
   }

   public ConfirmGUI confirmGui() {
      return this.confirmGUI;
   }
}
