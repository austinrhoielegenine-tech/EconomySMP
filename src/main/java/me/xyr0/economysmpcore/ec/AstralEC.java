package me.xyr0.economysmpcore.ec;

import java.io.File;
import java.util.logging.Logger;
import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.ec.command.EcCommand;
import me.xyr0.economysmpcore.ec.listener.EcListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AstralEC extends SystemHost {
   private static AstralEC instance;
   private EcStorage storage;
   private FileConfiguration messages;

   public AstralEC() {
      super("ec");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = this.loadMessages();
      this.storage = new EcStorage(this);
      new EcCommand(this);
      Bukkit.getPluginManager().registerEvents(new EcListener(this), this.getCore());
      Logger var10000 = this.getLogger();
      File var10001 = new File(this.getDataFolder(), "data");
      var10000.info("AstralEC enabled — data folder: " + var10001.getPath());
   }

   public void stop() {
      if (this.storage != null) {
         this.storage.flushAll();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages = this.loadMessages();
   }

   private FileConfiguration loadMessages() {
      FileConfiguration msg = new YamlConfiguration();
      ConfigurationSection sec = this.getConfig().getConfigurationSection("messages");
      if (sec != null) {
         for(String key : sec.getKeys(true)) {
            msg.set(key, sec.get(key));
         }
      }

      return msg;
   }

   public static AstralEC get() {
      return instance;
   }

   public EcStorage getStorage() {
      return this.storage;
   }

   public FileConfiguration getMessages() {
      return this.messages;
   }
}
