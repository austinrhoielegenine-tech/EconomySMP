package com.austynzdevv.novacore.help;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.help.command.HelpCommand;
import com.austynzdevv.novacore.help.listener.GUIListener;

public class EconomyHelp extends SystemHost {
   private static EconomyHelp instance;

   public EconomyHelp() {
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
      this.getLogger().info("EconomyHelp enabled successfully!");
      this.getLogger().info("Author: Xyr0");
      this.getLogger().info("--------------------------------");
   }

   public void stop() {
      this.getLogger().info("EconomyHelp disabled.");
   }

   public static EconomyHelp getInstance() {
      return instance;
   }

   public void reloadPlugin() {
      this.reloadConfig();
   }
}
