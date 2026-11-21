package com.austynzdevv.economysmp.rtpq;

import java.io.File;
import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.rtpq.command.RTPQueueCommand;
import com.austynzdevv.economysmp.rtpq.listener.JoinQuitListener;
import com.austynzdevv.economysmp.rtpq.listener.MoveListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class EconomyRTPQ extends SystemHost {
   private static EconomyRTPQ instance;
   private QueueManager queueManager;
   private WorldManager worldManager;

   public EconomyRTPQ() {
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
      this.getLogger().info("EconomyRTPQ enabled.");
   }

   public void stop() {
      if (this.queueManager != null) {
         this.queueManager.shutdown();
      }

      this.getLogger().info("EconomyRTPQ disabled.");
   }

   public static EconomyRTPQ getInstance() {
      return instance;
   }

   public QueueManager getQueueManager() {
      return this.queueManager;
   }

   public WorldManager getWorldManager() {
      return this.worldManager;
   }
}
