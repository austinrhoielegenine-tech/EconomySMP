package me.xyr0.astralsmpcore.help;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.help.command.HelpCommand;
import me.xyr0.astralsmpcore.help.listener.GUIListener;

public class AstralHelp extends SystemHost {
   private static AstralHelp instance;

   public AstralHelp() {
      super("help");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      if (this.getCommand("help") != null) {
         new HelpCommand(this);
      }

      this.getServer().getPluginManager().registerEvents(new GUIListener(this), this.getCore());
      this.getLogger().info("--------------------------------");
      this.getLogger().info("AstralHelp enabled successfully!");
      this.getLogger().info("Author: Xyr0");
      this.getLogger().info("--------------------------------");
   }

   public void stop() {
      this.getLogger().info("AstralHelp disabled.");
   }

   public static AstralHelp getInstance() {
      return instance;
   }

   public void reloadPlugin() {
      this.reloadConfig();
   }
}
