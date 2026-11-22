package com.austynzdevv.nyrethacore.tpa;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.tpa.command.TpaCommand;
import com.austynzdevv.nyrethacore.tpa.gui.TpaMenus;
import com.austynzdevv.nyrethacore.tpa.listener.GuiListener;
import com.austynzdevv.nyrethacore.tpa.listener.MoveListener;
import com.austynzdevv.nyrethacore.util.YamlMessages;
import org.bukkit.command.PluginCommand;

public final class EconomyTPA extends SystemHost {
   private static EconomyTPA instance;
   private YamlMessages messages;
   private TpaManager tpa;

   public EconomyTPA() {
      super("tpa");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = new YamlMessages(this);
      this.tpa = new TpaManager(this);
      TpaMenus.load(this);
      TpaCommand cmd = new TpaCommand(this);

      for(String name : new String[]{"tpa", "tpahere", "tpaccept", "tpadeny", "tpacancel", "tpauto", "tpatoggle", "tpaheretoggle", "tpaconfirmtoggle", "tpaignore", "tpareload"}) {
         PluginCommand c = this.getCommand(name);
         if (c != null) {
            c.setExecutor(cmd);
            c.setTabCompleter(cmd);
         }
      }

      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new MoveListener(this), this.getCore());
      this.tpa.startTpAutoBarTask();
      this.getLogger().info("EconomyTPA enabled.");
   }

   public void stop() {
      if (this.tpa != null) {
         this.tpa.stopTpAutoBarTask();
         this.tpa.saveSettings();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.tpa.reload();
      TpaMenus.load(this);
   }

   public static EconomyTPA get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public TpaManager getTpa() {
      return this.tpa;
   }
}
