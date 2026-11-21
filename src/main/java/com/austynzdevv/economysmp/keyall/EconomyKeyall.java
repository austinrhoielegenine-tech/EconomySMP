package com.austynzdevv.economysmp.keyall;

import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.keyall.command.KeyallCommand;
import com.austynzdevv.economysmp.keyall.listener.PlayerListener;
import com.austynzdevv.economysmp.keyall.placeholders.KeyallPlaceholders;
import org.bukkit.Bukkit;

public final class EconomyKeyall extends SystemHost {
   private static EconomyKeyall instance;
   private KeyManager keys;
   private TimerManager timer;

   public EconomyKeyall() {
      super("keyall");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.keys = new KeyManager(this);
      this.timer = new TimerManager(this);
      new KeyallCommand(this);
      if (this.getCommand("keyall") != null) {
      }

      this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this.getCore());
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new KeyallPlaceholders(this)).register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      this.getLogger().info("EconomyKeyall enabled.");
   }

   public void stop() {
      if (this.timer != null) {
         this.timer.stop();
         this.timer.saveData();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.keys.reload();
      this.timer.reloadSettings();
   }

   public static EconomyKeyall get() {
      return instance;
   }

   public KeyManager getKeys() {
      return this.keys;
   }

   public TimerManager getTimer() {
      return this.timer;
   }
}
