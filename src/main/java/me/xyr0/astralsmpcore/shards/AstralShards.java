package me.xyr0.astralsmpcore.shards;

import java.io.File;
import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.shards.command.ShardsCommand;
import me.xyr0.astralsmpcore.shards.listener.AfkTrackerTask;
import me.xyr0.astralsmpcore.shards.listener.BoosterListener;
import me.xyr0.astralsmpcore.shards.placeholders.ShardsPlaceholders;
import me.xyr0.astralsmpcore.shards.util.BoosterItemFactory;
import me.xyr0.astralsmpcore.util.Tasks;

public final class AstralShards extends SystemHost {
   private static AstralShards instance;
   private ShardManager shards;
   private MessageManager messages;
   private BoosterManager boosters;
   private BoosterItemFactory boosterFactory;
   private AfkTrackerTask afkTask;
   private int afkTaskId = -1;

   public AstralShards() {
      super("shards");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.saveResourceIfMissing("tools/shardbooster.yml");
      this.messages = new MessageManager(this);
      this.shards = new ShardManager(this);
      this.boosters = new BoosterManager(this);
      this.boosterFactory = new BoosterItemFactory(this);
      new ShardsCommand(this);
      this.getServer().getPluginManager().registerEvents(new BoosterListener(this), this.getCore());
      this.afkTask = new AfkTrackerTask(this);
      this.afkTaskId = Tasks.runTimer(this.getCore(), this.afkTask, 20L, 20L);
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new ShardsPlaceholders(this)).register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      if (this.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
         this.getLogger().info("WorldGuard hooked.");
      } else {
         this.getLogger().warning("WorldGuard not found. Region AFK rewards will not work (everywhere perm still works).");
      }

      this.getLogger().info("AstralShards enabled.");
   }

   private void saveResourceIfMissing(String path) {
      File file = new File(this.getDataFolder(), path);
      if (!file.exists()) {
         this.saveResource(path, false);
      }

   }

   public void stop() {
      if (this.shards != null) {
         this.shards.save();
      }

      if (this.boosters != null) {
         this.boosters.save();
      }

      if (this.afkTaskId != -1) {
         Tasks.cancel(this.afkTaskId);
         this.afkTaskId = -1;
      }

      this.afkTask = null;
      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.shards.reload();
      this.boosters.reload();
      this.boosterFactory.reload();
   }

   public static AstralShards get() {
      return instance;
   }

   public ShardManager getShards() {
      return this.shards;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public BoosterManager getBoosters() {
      return this.boosters;
   }

   public BoosterItemFactory getBoosterFactory() {
      return this.boosterFactory;
   }
}
