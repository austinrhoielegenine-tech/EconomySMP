package me.xyr0.economysmpcore.tpa;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.tpa.command.TpaCommand;
import me.xyr0.economysmpcore.tpa.gui.TpaMenus;
import me.xyr0.economysmpcore.tpa.listener.GuiListener;
import me.xyr0.economysmpcore.tpa.listener.MoveListener;
import me.xyr0.economysmpcore.util.YamlMessages;
import org.bukkit.command.PluginCommand;

public final class AstralTPA extends SystemHost {
   private static AstralTPA instance;
   private YamlMessages messages;
   private TpaManager tpa;

   public AstralTPA() {
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
      this.getLogger().info("AstralTPA enabled.");
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

   public static AstralTPA get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public TpaManager getTpa() {
      return this.tpa;
   }
}
