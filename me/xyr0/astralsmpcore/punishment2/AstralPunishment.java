package me.xyr0.astralsmpcore.punishment2;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.punishment2.command.CheckBanCommand;
import me.xyr0.astralsmpcore.punishment2.command.CheckMuteCommand;
import me.xyr0.astralsmpcore.punishment2.command.FreezeCommand;
import me.xyr0.astralsmpcore.punishment2.command.HistoryCommand;
import me.xyr0.astralsmpcore.punishment2.command.KickCommand;
import me.xyr0.astralsmpcore.punishment2.command.OffendCommand;
import me.xyr0.astralsmpcore.punishment2.command.OffendIPCommand;
import me.xyr0.astralsmpcore.punishment2.command.ReloadCommand;
import me.xyr0.astralsmpcore.punishment2.command.UnfreezeCommand;
import me.xyr0.astralsmpcore.punishment2.command.UnoffendCommand;
import me.xyr0.astralsmpcore.punishment2.database.DatabaseManager;
import me.xyr0.astralsmpcore.punishment2.listener.AsyncChatListener;
import me.xyr0.astralsmpcore.punishment2.listener.BanListener;
import me.xyr0.astralsmpcore.punishment2.listener.ChatListener;
import me.xyr0.astralsmpcore.punishment2.listener.FreezeListener;
import me.xyr0.astralsmpcore.punishment2.listener.HistoryListener;
import me.xyr0.astralsmpcore.punishment2.listener.IPBanListener;
import me.xyr0.astralsmpcore.punishment2.listener.JoinListener;
import me.xyr0.astralsmpcore.punishment2.listener.QuitListener;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AstralPunishment {
   private final JavaPlugin core;
   private static AstralPunishment instance;
   private DatabaseManager databaseManager;
   private PunishmentManager punishmentManager;
   private FreezeManager freezeManager;
   private HistoryManager historyManager;
   private WebhookManager webhookManager;
   private ReasonManager reasonManager;
   private IPBanManager ipBanManager;
   private BanScreenManager banScreenManager;
   private FileConfiguration messages;
   private FileConfiguration configCache;

   public AstralPunishment(JavaPlugin core) {
      this.core = core;
   }

   public static AstralPunishment getInstance() {
      return instance;
   }

   public JavaPlugin getPlugin() {
      return this.core;
   }

   public Plugin asPlugin() {
      return this.core;
   }

   public Logger getLogger() {
      return this.core.getLogger();
   }

   public Server getServer() {
      return this.core.getServer();
   }

   public File getDataFolder() {
      File d = new File(this.core.getDataFolder(), "punishment");
      if (!d.exists()) {
         d.mkdirs();
      }

      return d;
   }

   public FileConfiguration getConfig() {
      if (this.configCache == null) {
         File f = new File(this.getDataFolder(), "config.yml");
         this.configCache = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      }

      return this.configCache;
   }

   public void reloadConfig() {
      this.configCache = null;
      this.getConfig();
   }

   public void saveDefaultConfig() {
      File f = new File(this.getDataFolder(), "config.yml");
      if (!f.exists()) {
         try {
            this.core.saveResource("punishment/config.yml", false);
         } catch (Exception var3) {
         }
      }

   }

   public void saveResource(String name, boolean replace) {
      try {
         this.core.saveResource("punishment/" + name, replace);
      } catch (Exception var4) {
      }

   }

   public PluginCommand getCommand(String name) {
      return this.core.getCommand(name);
   }

   public FileConfiguration getMessages() {
      if (this.messages == null) {
         this.reloadMessages();
      }

      return this.messages;
   }

   public void reloadMessages() {
      File file = new File(this.getDataFolder(), "messages.yml");
      if (!file.exists()) {
         try {
            this.core.saveResource("punishment/messages.yml", false);
         } catch (Exception var3) {
         }
      }

      this.messages = YamlConfiguration.loadConfiguration(file);
   }

   public void enable() {
      instance = this;
      this.getDataFolder().mkdirs();
      this.saveDefaultConfig();

      for(String r : new String[]{"messages.yml", "reasons.yml", "webhook.yml"}) {
         File out = new File(this.getDataFolder(), r);
         if (!out.exists()) {
            try {
               this.core.saveResource("punishment/" + r, false);
            } catch (Exception var7) {
            }
         }
      }

      this.reloadMessages();
      this.databaseManager = new DatabaseManager(this);
      this.databaseManager.connect();
      this.databaseManager.createTables();
      this.punishmentManager = new PunishmentManager(this);
      this.freezeManager = new FreezeManager(this);
      this.historyManager = new HistoryManager(this);
      this.webhookManager = new WebhookManager(this);
      this.reasonManager = new ReasonManager(this);
      this.ipBanManager = new IPBanManager(this);
      this.banScreenManager = new BanScreenManager(this);
      this.registerCommands();
      this.registerListeners();
      this.getLogger().info("AstralPunishment enabled.");
   }

   public void disable() {
      if (this.databaseManager != null) {
         try {
            this.databaseManager.close();
         } catch (Exception var3) {
         }
      }

      if (this.ipBanManager != null) {
         try {
            this.ipBanManager.close();
         } catch (Exception var2) {
         }
      }

      this.getLogger().info("AstralPunishment disabled.");
   }

   private void registerCommands() {
      this.bind("offend", new OffendCommand(this));
      this.bind("offendip", new OffendIPCommand(this));
      this.bind("unoffend", new UnoffendCommand(this));
      this.bind("kick", new KickCommand(this));
      this.bind("freeze", new FreezeCommand(this));
      this.bind("unfreeze", new UnfreezeCommand(this));
      this.bind("history", new HistoryCommand(this));
      this.bind("checkban", new CheckBanCommand(this));
      this.bind("checkmute", new CheckMuteCommand(this));
      this.bind("astralpunishment", new ReloadCommand(this));
      this.bind("punishment", new HistoryCommand(this));
      this.bind("punish", new OffendCommand(this));
   }

   private void bind(String name, CommandExecutor executor) {
      PluginCommand pc = this.core.getCommand(name);
      if (pc != null) {
         pc.setExecutor(executor);
         if (executor instanceof TabCompleter) {
            TabCompleter tab = (TabCompleter)executor;
            pc.setTabCompleter(tab);
         }
      }

      JavaPlugin var5 = this.core;
      if (var5 instanceof AstralSMPcore asmp) {
         asmp.commands().registerHandler(name, (sender, args) -> {
            try {
               return executor.onCommand(sender, (Command)(pc != null ? pc : this.dummyCmd(name)), name, args);
            } catch (Exception ex) {
               sender.sendMessage("§cPunishment error: " + ex.getMessage());
               return true;
            }
         });
         if (executor instanceof TabCompleter tab) {
            asmp.commands().registerTabCompleter(name, (sender, command, alias, args) -> {
               try {
                  List<String> r = tab.onTabComplete(sender, command, alias, args);
                  return r != null ? r : List.of();
               } catch (Exception var6) {
                  return List.of();
               }
            });
         }
      }

   }

   private Command dummyCmd(String name) {
      return new Command(name) {
         public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return true;
         }
      };
   }

   private void registerListeners() {
      PluginManager pm = this.getServer().getPluginManager();
      pm.registerEvents(new ChatListener(this), this.core);
      pm.registerEvents(new FreezeListener(this), this.core);
      pm.registerEvents(new BanListener(this), this.core);
      pm.registerEvents(new JoinListener(this), this.core);
      pm.registerEvents(new IPBanListener(this), this.core);
      pm.registerEvents(new QuitListener(this), this.core);
      pm.registerEvents(new AsyncChatListener(this), this.core);
      pm.registerEvents(new HistoryListener(), this.core);
   }

   public DatabaseManager getDatabaseManager() {
      return this.databaseManager;
   }

   public PunishmentManager getPunishmentManager() {
      return this.punishmentManager;
   }

   public FreezeManager getFreezeManager() {
      return this.freezeManager;
   }

   public HistoryManager getHistoryManager() {
      return this.historyManager;
   }

   public WebhookManager getWebhookManager() {
      return this.webhookManager;
   }

   public ReasonManager getReasonManager() {
      return this.reasonManager;
   }

   public IPBanManager getIpBanManager() {
      return this.ipBanManager;
   }

   public BanScreenManager getBanScreenManager() {
      return this.banScreenManager;
   }
}
