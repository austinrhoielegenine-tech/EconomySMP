package me.xyr0.economysmpcore.chat2;

import java.io.File;
import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.chat2.command.ChatGamesCommand;
import me.xyr0.economysmpcore.chat2.listener.ChatGameListener;
import me.xyr0.economysmpcore.chat2.listener.ChatListener;
import me.xyr0.economysmpcore.chat2.listener.ChatSecurityListener;
import me.xyr0.economysmpcore.chat2.listener.ChatToggleListener;

public class AstralChat extends SystemHost {
   private static AstralChat instance;
   private ConfigManager configManager;
   private ChatManager chatManager;
   private DiscordSRVHook discordSRVHook;
   private SecurityLogger securityLogger;
   private ChatGameManager chatGames;

   public AstralChat() {
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
      this.getLogger().info("AstralChat enabled!");
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

   public void reloadAstralChat() {
      this.reloadConfig();
      this.configManager.loadConfigs();
      this.chatManager.loadState();
      if (this.chatGames != null) {
         this.chatGames.reload();
      }

   }

   public static AstralChat getInstance() {
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
