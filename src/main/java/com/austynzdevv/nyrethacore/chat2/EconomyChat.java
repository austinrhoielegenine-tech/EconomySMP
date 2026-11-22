package com.austynzdevv.nyrethacore.chat2;

import java.io.File;
import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.chat2.command.ChatGamesCommand;
import com.austynzdevv.nyrethacore.chat2.listener.ChatGameListener;
import com.austynzdevv.nyrethacore.chat2.listener.ChatListener;
import com.austynzdevv.nyrethacore.chat2.listener.ChatSecurityListener;
import com.austynzdevv.nyrethacore.chat2.listener.ChatToggleListener;

public class EconomyChat extends SystemHost {
   private static EconomyChat instance;
   private ConfigManager configManager;
   private ChatManager chatManager;
   private DiscordSRVHook discordSRVHook;
   private SecurityLogger securityLogger;
   private ChatGameManager chatGames;

   public EconomyChat() {
      super("chat2");
   }

   public void stop() {
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.configManager = new ConfigManager(this);
      this.configManager.loadConfigs();
      this.securityLogger = new SecurityLogger(this);
      this.chatManager = new ChatManager(this);
      this.chatManager.loadState();
      this.discordSRVHook = new DiscordSRVHook(this);
      if (!(new File(this.getDataFolder(), "chatgames.yml")).exists()) {
         this.saveResource("chatgames.yml", false);
      }

      this.chatGames = new ChatGameManager(this);
      this.registerCommands();
      this.registerListeners();
      this.getLogger().info("EconomyChat enabled!");
   }

   private void registerCommands() {
      new ChatGamesCommand(this);
   }

   private void registerListeners() {
      this.getServer().getPluginManager().registerEvents(new ChatListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ChatSecurityListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ChatToggleListener(this, this.chatManager), this);
      this.getServer().getPluginManager().registerEvents(new ChatGameListener(this), this.getCore());
   }

   public void reloadEconomyChat() {
      this.reloadConfig();
      this.configManager.loadConfigs();
      this.chatManager.loadState();
      if (this.chatGames != null) {
         this.chatGames.reload();
      }

   }

   public static EconomyChat getInstance() {
      return instance;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public ChatManager getChatManager() {
      return this.chatManager;
   }

   public DiscordSRVHook getDiscordSRVHook() {
      return this.discordSRVHook;
   }

   public SecurityLogger getSecurityLogger() {
      return this.securityLogger;
   }

   public ChatGameManager getChatGames() {
      return this.chatGames;
   }
}
