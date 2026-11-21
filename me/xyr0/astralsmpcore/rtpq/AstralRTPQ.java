package me.xyr0.astralsmpcore.rtpq;

import java.io.File;
import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.rtpq.command.RTPQueueCommand;
import me.xyr0.astralsmpcore.rtpq.listener.JoinQuitListener;
import me.xyr0.astralsmpcore.rtpq.listener.MoveListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class AstralRTPQ extends SystemHost {
   private static AstralRTPQ instance;
   private QueueManager queueManager;
   private WorldManager worldManager;

   public AstralRTPQ() {
      super("rtp");
   }

   public void start() {
      instance = this;
      File qf = new File(this.getDataFolder(), "queue.yml");
      if (!qf.exists()) {
         try {
            this.saveResource("queue.yml", false);
         } catch (Exception var5) {
         }
      }

      if (qf.exists()) {
         FileConfiguration qc = YamlConfiguration.loadConfiguration(qf);

         for(String key : qc.getKeys(true)) {
            this.getConfig().set(key, qc.get(key));
         }
      } else {
         this.saveDefaultConfig();
      }

      this.worldManager = new WorldManager(this);
      this.queueManager = new QueueManager(this);
      new RTPQueueCommand(this);
      this.getServer().getPluginManager().registerEvents(new MoveListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this.getCore());
      this.getLogger().info("AstralRTPQ enabled.");
   }

   public void stop() {
      if (this.queueManager != null) {
         this.queueManager.shutdown();
      }

      this.getLogger().info("AstralRTPQ disabled.");
   }

   public static AstralRTPQ getInstance() {
      return instance;
   }

   public QueueManager getQueueManager() {
      return this.queueManager;
   }

   public WorldManager getWorldManager() {
      return this.worldManager;
   }
}
