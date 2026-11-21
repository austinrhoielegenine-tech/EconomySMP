package com.austynzdevv.novacore.crates;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.crates.command.CrateCommand;
import com.austynzdevv.novacore.crates.listener.CrateListener;
import com.austynzdevv.novacore.crates.listener.GuiListener;
import com.austynzdevv.novacore.crates.placeholders.CratePlaceholders;
import org.bukkit.command.PluginCommand;

public final class EconomyCrates extends SystemHost {
   private static EconomyCrates instance;
   private MessageManager messages;
   private CrateManager crates;
   private KeyManager keys;
   private DataManager data;
   private HologramManager holograms;

   public EconomyCrates() {
      super("crates");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = new MessageManager(this);
      this.crates = new CrateManager(this);
      this.keys = new KeyManager(this);
      this.data = new DataManager(this);
      this.holograms = new HologramManager(this);
      this.crates.load();
      this.keys.load();
      this.data.load();
      CrateCommand cmd = new CrateCommand(this);

      for(String name : new String[]{"crates", "crate", "c"}) {
         PluginCommand c = this.getCommand(name);
         if (c != null) {
            c.setExecutor(cmd);
            c.setTabCompleter(cmd);
         }
      }

      this.getServer().getPluginManager().registerEvents(new CrateListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new CratePlaceholders(this)).register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      this.holograms.start();
      this.getLogger().info("EconomyCrates enabled.");
   }

   public void stop() {
      if (this.holograms != null) {
         this.holograms.stop();
      }

      if (this.data != null) {
         this.data.save();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.crates.load();
      this.keys.load();
      this.data.load();
      if (this.holograms != null) {
         this.holograms.reload();
      }

   }

   public static EconomyCrates get() {
      return instance;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public CrateManager getCrates() {
      return this.crates;
   }

   public KeyManager getKeys() {
      return this.keys;
   }

   public DataManager getData() {
      return this.data;
   }

   public HologramManager getHolograms() {
      return this.holograms;
   }
}
