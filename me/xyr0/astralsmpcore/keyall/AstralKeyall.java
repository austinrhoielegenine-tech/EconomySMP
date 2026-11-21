package me.xyr0.astralsmpcore.keyall;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.keyall.command.KeyallCommand;
import me.xyr0.astralsmpcore.keyall.listener.PlayerListener;
import me.xyr0.astralsmpcore.keyall.placeholders.KeyallPlaceholders;
import org.bukkit.Bukkit;

public final class AstralKeyall extends SystemHost {
   private static AstralKeyall instance;
   private KeyManager keys;
   private TimerManager timer;

   public AstralKeyall() {
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

      this.getLogger().info("AstralKeyall enabled.");
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

   public static AstralKeyall get() {
      return instance;
   }

   public KeyManager getKeys() {
      return this.keys;
   }

   public TimerManager getTimer() {
      return this.timer;
   }
}
