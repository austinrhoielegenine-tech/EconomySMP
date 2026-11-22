package com.austynzdevv.nyrethacore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.austynzdevv.nyrethacore.auction.EconomyAuction;
import com.austynzdevv.nyrethacore.auction.command.AuctionCommand;
import com.austynzdevv.nyrethacore.baltop.EconomyBaltop;
import com.austynzdevv.nyrethacore.baltop.command.BaltopCommand;
import com.austynzdevv.nyrethacore.billford.EconomyBillford;
import com.austynzdevv.nyrethacore.billford.command.BillfordCommand;
import com.austynzdevv.nyrethacore.bounties.EconomyBounties;
import com.austynzdevv.nyrethacore.bounties.command.BountyCommand;
import com.austynzdevv.nyrethacore.chat2.EconomyChat;
import com.austynzdevv.nyrethacore.chat2.command.EconomyChatCommand;
import com.austynzdevv.nyrethacore.chat2.command.ChatGamesCommand;
import com.austynzdevv.nyrethacore.chat2.command.ClearChatCommand;
import com.austynzdevv.nyrethacore.combat.EconomyCombat;
import com.austynzdevv.nyrethacore.combat.command.CombatCommand;
import com.austynzdevv.nyrethacore.crates.EconomyCrates;
import com.austynzdevv.nyrethacore.crates.command.CrateCommand;
import com.austynzdevv.nyrethacore.ec.EconomyEC;
import com.austynzdevv.nyrethacore.ec.command.EcCommand;
import com.austynzdevv.nyrethacore.help.EconomyHelp;
import com.austynzdevv.nyrethacore.help.command.HelpCommand;
import com.austynzdevv.nyrethacore.hide.EconomyHide;
import com.austynzdevv.nyrethacore.hide.command.HideCommand;
import com.austynzdevv.nyrethacore.keyall.EconomyKeyall;
import com.austynzdevv.nyrethacore.keyall.command.KeyallCommand;
import com.austynzdevv.nyrethacore.kits.EconomyKits;
import com.austynzdevv.nyrethacore.kits.command.KitsCommand;
import com.austynzdevv.nyrethacore.lb.EconomyLeaderboards;
import com.austynzdevv.nyrethacore.lb.command.LeaderboardCommand;
import com.austynzdevv.nyrethacore.maintenance.economyMaintenance;
import com.austynzdevv.nyrethacore.maintenance.command.MaintenanceCommand;
import com.austynzdevv.nyrethacore.media.EconomyMedia;
import com.austynzdevv.nyrethacore.media.command.DiscordCommand;
import com.austynzdevv.nyrethacore.media.command.MediaCommand;
import com.austynzdevv.nyrethacore.media.command.StoreCommand;
import com.austynzdevv.nyrethacore.mobs.EconomyMobs;
import com.austynzdevv.nyrethacore.mobs.EconomyMobsCommand;
import com.austynzdevv.nyrethacore.mobs.command.MobsToggleCommand;
import com.austynzdevv.nyrethacore.order.EconomyOrder;
import com.austynzdevv.nyrethacore.order.command.OrdersCommand;
import com.austynzdevv.nyrethacore.reports.EconomyReports;
import com.austynzdevv.nyrethacore.reports.command.ReportCommand;
import com.austynzdevv.nyrethacore.reports.command.ReportsCommand;
import com.austynzdevv.nyrethacore.rtp.EconomyRTP;
import com.austynzdevv.nyrethacore.rtp.command.RTPCommand;
import com.austynzdevv.nyrethacore.rtpq.EconomyRTPQ;
import com.austynzdevv.nyrethacore.rtpq.command.RTPQueueCommand;
import com.austynzdevv.nyrethacore.rules.EconomyRules;
import com.austynzdevv.nyrethacore.rules.command.RulesCommand;
import com.austynzdevv.nyrethacore.safety.EconomySafety;
import com.austynzdevv.nyrethacore.safety.command.SafetyCommand;
import com.austynzdevv.nyrethacore.sell.EconomySell;
import com.austynzdevv.nyrethacore.sell.command.HistoryCommand;
import com.austynzdevv.nyrethacore.sell.command.SellAxeCommand;
import com.austynzdevv.nyrethacore.sell.command.SellCommand;
import com.austynzdevv.nyrethacore.sell.command.SellMultiCommand;
import com.austynzdevv.nyrethacore.sell.command.WorthCommand;
import com.austynzdevv.nyrethacore.sell.command.WorthToggleCommand;
import com.austynzdevv.nyrethacore.shards.EconomyShards;
import com.austynzdevv.nyrethacore.shards.command.ShardsCommand;
import com.austynzdevv.nyrethacore.shop.EconomyShop;
import com.austynzdevv.nyrethacore.shop.command.ShopCommand;
import com.austynzdevv.nyrethacore.spawners.EconomySpawners;
import com.austynzdevv.nyrethacore.spawners.command.EconomyCommand;
import com.austynzdevv.nyrethacore.teams.EconomyTeams;
import com.austynzdevv.nyrethacore.teams.command.TeamCommand;
import com.austynzdevv.nyrethacore.tools.EconomyTools;
import com.austynzdevv.nyrethacore.tools.command.ToolsCommand;
import com.austynzdevv.nyrethacore.tpa.EconomyTPA;
import com.austynzdevv.nyrethacore.tpa.command.TpaCommand;
import com.austynzdevv.nyrethacore.util.TextUtil;
import com.austynzdevv.nyrethacore.warps.EconomyWarps;
import com.austynzdevv.nyrethacore.warps.command.WarpCommand;
import com.austynzdevv.nyrethacore.wl.EconomyWhitelister;
import com.austynzdevv.nyrethacore.wl.command.WhitelistCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CoreSystemsModule implements Module {
   private final NyrethaCore plugin;
   private final List<Runnable> shutdown = new ArrayList();

   public CoreSystemsModule(NyrethaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "core-systems";
   }

   public void onEnable() {
      this.shutdown.clear();
      this.boot("auction-house", () -> {
         EconomyAuction h = new EconomyAuction();
         h.init(this.plugin, this.data("auction"));
         h.start();
         this.bind("ah", new AuctionCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("baltop", () -> {
         EconomyBaltop h = new EconomyBaltop();
         h.init(this.plugin, this.data("baltop"));
         h.start();
         this.bind("baltop", new BaltopCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("orders", () -> {
         EconomyOrder h = new EconomyOrder();
         h.init(this.plugin, this.data("order"));
         h.start();
         this.bind("orders", new OrdersCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("shop", () -> {
         EconomyShop h = new EconomyShop();
         h.init(this.plugin, this.data("shop"));
         h.start();
         this.bind("shop", new ShopCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("sell", () -> {
         EconomySell h = new EconomySell();
         h.init(this.plugin, this.data("sell"));
         h.start();
         this.bind("sell", new SellCommand(h));
         this.bind("worth", new WorthCommand(h));
         this.bind("worthtoggle", new WorthToggleCommand(h));
         this.bind("sellhistory", new HistoryCommand(h));

         try {
            this.bind("sellmulti", new SellMultiCommand(h));
         } catch (Exception var4) {
         }

         try {
            this.bind("sellaxe", new SellAxeCommand(h, h.getSellAxe()));
         } catch (Exception var3) {
         }

         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("leaderboards", () -> {
         EconomyLeaderboards h = new EconomyLeaderboards();
         h.init(this.plugin, this.data("lb"));
         h.start();
         this.bind("lb", new LeaderboardCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("billford", () -> {
         EconomyBillford h = new EconomyBillford();
         h.init(this.plugin, this.data("billford"));
         h.start();
         this.bind("billford", new BillfordCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("bounties", () -> {
         EconomyBounties h = new EconomyBounties();
         h.init(this.plugin, this.data("bounties"));
         h.start();
         this.bind("bounty", new BountyCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("crates", () -> {
         EconomyCrates h = new EconomyCrates();
         h.init(this.plugin, this.data("crates"));
         h.start();
         CrateCommand crateCmd = new CrateCommand(h);
         this.bind("crates", crateCmd);
         this.bind("crate", crateCmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("shards", () -> {
         EconomyShards h = new EconomyShards();
         h.init(this.plugin, this.data("shards"));
         h.start();
         this.bind("shards", new ShardsCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("teams", () -> {
         EconomyTeams h = new EconomyTeams();
         h.init(this.plugin, this.data("teams"));
         h.start();
         TeamCommand cmd = new TeamCommand(h);
         this.bind("teams", cmd);
         this.bind("team", cmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("tpa", () -> {
         EconomyTPA h = new EconomyTPA();
         h.init(this.plugin, this.data("tpa"));
         h.start();
         TpaCommand cmd = new TpaCommand(h);
         this.bind("tpa", cmd);
         this.bind("tpahere", cmd);
         this.bind("tpaccept", cmd);
         this.bind("tpadeny", cmd);
         this.bind("tpdeny", cmd);
         this.bind("tpacancel", cmd);
         this.bind("tpauto", cmd);
         this.bind("tpatoggle", cmd);
         this.bind("tpaheretoggle", cmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("warps", () -> {
         EconomyWarps h = new EconomyWarps();
         h.init(this.plugin, this.data("warps"));
         h.start();
         WarpCommand cmd = new WarpCommand(h);
         this.bind("warps", cmd);
         this.bind("warp", cmd);
         this.bind("setwarp", cmd);
         this.bind("delwarp", cmd);
         this.bind("spawn", cmd);
         this.bind("setspawn", cmd);
         this.bind("delspawn", cmd);
         this.bind("afk", cmd);
         this.bind("setafk", cmd);
         this.bind("delafk", cmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("tools", () -> {
         EconomyTools h = new EconomyTools();
         h.init(this.plugin, this.data("tools"));
         h.start();
         this.bind("tools", new ToolsCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("kits", () -> {
         EconomyKits h = new EconomyKits();
         h.init(this.plugin, this.data("kits"));
         h.start();
         KitsCommand kitsCmd = new KitsCommand(h);
         this.bind("kit", kitsCmd);
         this.bind("kits", kitsCmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("spawners", () -> {
         EconomySpawners h = new EconomySpawners();
         h.init(this.plugin, this.data("spawners"));
         h.start();
         EconomyCommand asCmd = new EconomyCommand(h);
         this.bind("spawners", asCmd);
         this.bind("as", asCmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("chat", () -> {
         EconomyChat h = new EconomyChat();
         h.init(this.plugin, this.data("chat2"));
         h.start();
         this.bind("chat", new EconomyChatCommand(h));
         this.plugin.commands().registerHandler("chattoggle", (sender, args) -> {
            if (sender instanceof Player pl) {
               boolean on = h.getChatManager().togglePersonal(pl.getUniqueId());

               try {
                  Module mod = this.plugin.modules().get("settings");
                  if (mod instanceof SettingsModule sm) {
                     if (on && sm.isChatOff(pl.getUniqueId())) {
                        sm.toggleChatOff(pl.getUniqueId());
                     }

                     if (!on && !sm.isChatOff(pl.getUniqueId())) {
                        sm.toggleChatOff(pl.getUniqueId());
                     }
                  }
               } catch (Exception var8) {
               }

               if (on) {
                  TextUtil.send(pl, "&#00a3fbGeneral chat &#00FF00enabled&7.");
               } else {
                  TextUtil.send(pl, "&#00a3fbGeneral chat &#fc0404disabled&7.");
               }

               return true;
            } else {
               return true;
            }
         });

         try {
            this.bind("clearchat", new ClearChatCommand(h));
         } catch (Exception var4) {
         }

         try {
            this.bind("chatgames", new ChatGamesCommand(h));
         } catch (Exception var3) {
         }

         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("combat", () -> {
         EconomyCombat h = new EconomyCombat();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("combat", new CombatCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("enderchest", () -> {
         EconomyEC h = new EconomyEC();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("echest", new EcCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("help", () -> {
         EconomyHelp h = new EconomyHelp();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("help", new HelpCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("hide", () -> {
         EconomyHide h = new EconomyHide();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("hide", new HideCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("keyall", () -> {
         EconomyKeyall h = new EconomyKeyall();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("keyall", new KeyallCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("maintenance", () -> {
         economyMaintenance h = new economyMaintenance();
         h.init(this.plugin, this.data("maintenance"));
         h.start();
         this.bind("maintenance", new MaintenanceCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("media", () -> {
         EconomyMedia h = new EconomyMedia();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("media", new MediaCommand());
         this.bind("discord", new DiscordCommand());
         this.bind("store", new StoreCommand());
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("mobs", () -> {
         EconomyMobs h = new EconomyMobs();
         h.init(this.plugin, this.flatData());
         h.start();

         try {
            this.bind("mobs", new MobsToggleCommand(h));
         } catch (Exception var5) {
            try {
               this.bind("mobs", new EconomyMobsCommand(h));
            } catch (Exception var4) {
            }
         }

         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("reports", () -> {
         EconomyReports h = new EconomyReports();
         h.init(this.plugin, this.data("reports"));
         h.start();
         this.bind("report", new ReportCommand(h));
         this.bind("reports", new ReportsCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("rtp", () -> {
         EconomyRTP h = new EconomyRTP();
         h.init(this.plugin, this.data("rtp"));
         h.start();
         this.bind("rtp", new RTPCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
         EconomyRTPQ q = new EconomyRTPQ();
         q.init(this.plugin, this.data("rtp"));
         q.start();
         this.bind("rtpqueue", new RTPQueueCommand(q));
         this.bind("rtpq", new RTPQueueCommand(q));
         var10000 = this.shutdown;
         Objects.requireNonNull(q);
         var10000.add(q::stop);
      });
      this.boot("rules", () -> {
         EconomyRules h = new EconomyRules();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("rules", new RulesCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("safety", () -> {
         EconomySafety h = new EconomySafety();
         h.init(this.plugin, this.flatData());
         h.start();

         try {
            this.bind("safety", new SafetyCommand(h));
         } catch (Exception var3) {
         }

         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("whitelist", () -> {
         EconomyWhitelister h = new EconomyWhitelister();
         h.init(this.plugin, this.flatData());
         h.start();

         try {
            this.bind("wl", new WhitelistCommand(h));
         } catch (Exception var3) {
         }

         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("chainmail", () -> {
         ChainmailListener listener = new ChainmailListener(this.plugin);
         this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
         CommandExecutor cm = (sender, command, label, args) -> {
            if (sender instanceof Player player) {
               Module mod = this.plugin.modules().get("settings");
               if (mod instanceof SettingsModule sm) {
                  boolean enabled = sm.toggleChainmailKit(player.getUniqueId());
                  player.sendMessage(enabled ? "§aChainmail kit enabled." : "§cChainmail kit disabled.");
               } else {
                  listener.giveKit(player);
                  player.sendMessage("§aChainmail kit given.");
               }

               return true;
            } else {
               sender.sendMessage("Players only.");
               return true;
            }
         };
         this.bind("chainmail", cm);
         this.bind("cm", cm);
      });
   }

   private File data(String folder) {
      return new File(this.plugin.getDataFolder(), folder);
   }

   private File flatData() {
      return this.plugin.getDataFolder();
   }

   private void boot(String configKey, Runnable action) {
      if (!this.plugin.configs().isEnabled(configKey)) {
         this.plugin.getLogger().info("Skipped (disabled): " + configKey);
      } else {
         try {
            action.run();
            this.plugin.getLogger().info("Booted system: " + configKey);
         } catch (Exception t) {
            this.plugin.getLogger().warning("Failed to boot " + configKey + ": " + t.getClass().getSimpleName() + " - " + t.getMessage());
            if (this.plugin.getConfig().getBoolean("settings.debug", false)) {
               t.printStackTrace();
            }
         }

      }
   }

   private void bind(String name, CommandExecutor executor) {
      if (executor != null) {
         this.plugin.commands().registerHandler(name, (sender, args) -> {
            try {
               Command fake = new Command(name) {
                  public boolean execute(CommandSender commandSender, String s, String[] strings) {
                     return false;
                  }
               };
               return executor.onCommand(sender, fake, name, args);
            } catch (Exception t) {
               sender.sendMessage("§cError running /" + name + ": " + t.getMessage());
               this.plugin.getLogger().warning("/" + name + " error: " + t.getMessage());
               t.printStackTrace();
               return true;
            }
         });

         try {
            PluginCommand pc = this.plugin.getCommand(name);
            if (pc != null) {
               pc.setExecutor((sender, command, label, args) -> {
                  try {
                     return executor.onCommand(sender, command, label, args);
                  } catch (Exception t) {
                     sender.sendMessage("§cError: " + t.getMessage());
                     return true;
                  }
               });
               if (executor instanceof TabCompleter) {
                  TabCompleter tc = (TabCompleter)executor;
                  pc.setTabCompleter(tc);
               }
            }
         } catch (Exception var5) {
         }

      }
   }

   public void onDisable() {
      for(Runnable r : this.shutdown) {
         try {
            r.run();
         } catch (Exception var4) {
         }
      }

      this.shutdown.clear();
   }

   public void reload() {
      this.onDisable();
      this.onEnable();
   }
}
