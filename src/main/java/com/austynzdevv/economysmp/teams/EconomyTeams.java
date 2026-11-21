package com.austynzdevv.economysmp.teams;

import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.listener.ProtectedSignListener;
import com.austynzdevv.economysmp.teams.command.TeamCommand;
import com.austynzdevv.economysmp.teams.gui.TeamMenus;
import com.austynzdevv.economysmp.teams.listener.ChatListener;
import com.austynzdevv.economysmp.teams.listener.GuiListener;
import com.austynzdevv.economysmp.teams.listener.TeamListener;
import com.austynzdevv.economysmp.teams.placeholders.TeamsPlaceholders;
import com.austynzdevv.economysmp.teams.util.InputUtil;

public final class EconomyTeams extends SystemHost {
   private static EconomyTeams instance;
   private MessageManager messages;
   private TeamManager teams;
   private TeamCommand command;

   public EconomyTeams() {
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

      this.getLogger().info("EconomyTeams enabled.");
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

   public static EconomyTeams get() {
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
