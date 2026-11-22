package com.austynzdevv.nyrethacore.teams;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.listener.ProtectedSignListener;
import com.austynzdevv.nyrethacore.teams.command.TeamCommand;
import com.austynzdevv.nyrethacore.teams.gui.TeamMenus;
import com.austynzdevv.nyrethacore.teams.listener.ChatListener;
import com.austynzdevv.nyrethacore.teams.listener.GuiListener;
import com.austynzdevv.nyrethacore.teams.listener.TeamListener;
import com.austynzdevv.nyrethacore.teams.placeholders.TeamsPlaceholders;
import com.austynzdevv.nyrethacore.teams.util.InputUtil;

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
