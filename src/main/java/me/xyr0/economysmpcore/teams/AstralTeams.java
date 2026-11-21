package me.xyr0.economysmpcore.teams;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.listener.ProtectedSignListener;
import me.xyr0.economysmpcore.teams.command.TeamCommand;
import me.xyr0.economysmpcore.teams.gui.TeamMenus;
import me.xyr0.economysmpcore.teams.listener.ChatListener;
import me.xyr0.economysmpcore.teams.listener.GuiListener;
import me.xyr0.economysmpcore.teams.listener.TeamListener;
import me.xyr0.economysmpcore.teams.placeholders.TeamsPlaceholders;
import me.xyr0.economysmpcore.teams.util.InputUtil;

public final class AstralTeams extends SystemHost {
   private static AstralTeams instance;
   private MessageManager messages;
   private TeamManager teams;
   private TeamCommand command;

   public AstralTeams() {
      super("teams");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = new MessageManager(this);
      this.teams = new TeamManager(this);
      TeamMenus.load(this);
      this.command = new TeamCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ProtectedSignListener(InputUtil::isProtectedSign), this.getCore());
      this.getServer().getPluginManager().registerEvents(new TeamListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ChatListener(this), this.getCore());
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new TeamsPlaceholders(this)).register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      this.getLogger().info("AstralTeams enabled.");
   }

   public void stop() {
      if (this.teams != null) {
         this.teams.saveAll();
      }

      instance = null;
      this.command = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.teams.reload();
      TeamMenus.load(this);
   }

   public static AstralTeams get() {
      return instance;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public TeamManager getTeams() {
      return this.teams;
   }

   public TeamCommand getCommand() {
      return this.command;
   }
}
