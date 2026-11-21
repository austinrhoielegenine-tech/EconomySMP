package me.xyr0.economysmpcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.xyr0.economysmpcore.auction.AstralAuction;
import me.xyr0.economysmpcore.auction.command.AuctionCommand;
import me.xyr0.economysmpcore.baltop.AstralBaltop;
import me.xyr0.economysmpcore.baltop.command.BaltopCommand;
import me.xyr0.economysmpcore.billford.AstralBillford;
import me.xyr0.economysmpcore.billford.command.BillfordCommand;
import me.xyr0.economysmpcore.bounties.AstralBounties;
import me.xyr0.economysmpcore.bounties.command.BountyCommand;
import me.xyr0.economysmpcore.chat2.AstralChat;
import me.xyr0.economysmpcore.chat2.command.AstralChatCommand;
import me.xyr0.economysmpcore.chat2.command.ChatGamesCommand;
import me.xyr0.economysmpcore.chat2.command.ClearChatCommand;
import me.xyr0.economysmpcore.combat.AstralCombat;
import me.xyr0.economysmpcore.combat.command.CombatCommand;
import me.xyr0.economysmpcore.crates.AstralCrates;
import me.xyr0.economysmpcore.crates.command.CrateCommand;
import me.xyr0.economysmpcore.ec.AstralEC;
import me.xyr0.economysmpcore.ec.command.EcCommand;
import me.xyr0.economysmpcore.help.AstralHelp;
import me.xyr0.economysmpcore.help.command.HelpCommand;
import me.xyr0.economysmpcore.hide.AstralHide;
import me.xyr0.economysmpcore.hide.command.HideCommand;
import me.xyr0.economysmpcore.keyall.AstralKeyall;
import me.xyr0.economysmpcore.keyall.command.KeyallCommand;
import me.xyr0.economysmpcore.kits.AstralKits;
import me.xyr0.economysmpcore.kits.command.KitsCommand;
import me.xyr0.economysmpcore.lb.AstralLeaderboards;
import me.xyr0.economysmpcore.lb.command.LeaderboardCommand;
import me.xyr0.economysmpcore.maintenance.astralMaintenance;
import me.xyr0.economysmpcore.maintenance.command.MaintenanceCommand;
import me.xyr0.economysmpcore.media.AstralMedia;
import me.xyr0.economysmpcore.media.command.DiscordCommand;
import me.xyr0.economysmpcore.media.command.MediaCommand;
import me.xyr0.economysmpcore.media.command.StoreCommand;
import me.xyr0.economysmpcore.mobs.AstralMobs;
import me.xyr0.economysmpcore.mobs.AstralMobsCommand;
import me.xyr0.economysmpcore.mobs.command.MobsToggleCommand;
import me.xyr0.economysmpcore.order.AstralOrder;
import me.xyr0.economysmpcore.order.command.OrdersCommand;
import me.xyr0.economysmpcore.reports.AstralReports;
import me.xyr0.economysmpcore.reports.command.ReportCommand;
import me.xyr0.economysmpcore.reports.command.ReportsCommand;
import me.xyr0.economysmpcore.rtp.AstralRTP;
import me.xyr0.economysmpcore.rtp.command.RTPCommand;
import me.xyr0.economysmpcore.rtpq.AstralRTPQ;
import me.xyr0.economysmpcore.rtpq.command.RTPQueueCommand;
import me.xyr0.economysmpcore.rules.AstralRules;
import me.xyr0.economysmpcore.rules.command.RulesCommand;
import me.xyr0.economysmpcore.safety.AstralSafety;
import me.xyr0.economysmpcore.safety.command.SafetyCommand;
import me.xyr0.economysmpcore.sell.AstralSell;
import me.xyr0.economysmpcore.sell.command.HistoryCommand;
import me.xyr0.economysmpcore.sell.command.SellAxeCommand;
import me.xyr0.economysmpcore.sell.command.SellCommand;
import me.xyr0.economysmpcore.sell.command.SellMultiCommand;
import me.xyr0.economysmpcore.sell.command.WorthCommand;
import me.xyr0.economysmpcore.sell.command.WorthToggleCommand;
import me.xyr0.economysmpcore.shards.AstralShards;
import me.xyr0.economysmpcore.shards.command.ShardsCommand;
import me.xyr0.economysmpcore.shop.AstralShop;
import me.xyr0.economysmpcore.shop.command.ShopCommand;
import me.xyr0.economysmpcore.spawners.AstralSpawners;
import me.xyr0.economysmpcore.spawners.command.AstralCommand;
import me.xyr0.economysmpcore.teams.AstralTeams;
import me.xyr0.economysmpcore.teams.command.TeamCommand;
import me.xyr0.economysmpcore.tools.AstralTools;
import me.xyr0.economysmpcore.tools.command.ToolsCommand;
import me.xyr0.economysmpcore.tpa.AstralTPA;
import me.xyr0.economysmpcore.tpa.command.TpaCommand;
import me.xyr0.economysmpcore.util.TextUtil;
import me.xyr0.economysmpcore.warps.AstralWarps;
import me.xyr0.economysmpcore.warps.command.WarpCommand;
import me.xyr0.economysmpcore.wl.AstralWhitelister;
import me.xyr0.economysmpcore.wl.command.WhitelistCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CoreSystemsModule implements Module {
   private final EconomySMPcore plugin;
   private final List<Runnable> shutdown = new ArrayList();

   public CoreSystemsModule(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "core-systems";
   }

   public void onEnable() {
      this.shutdown.clear();
      this.boot("auction-house", () -> {
         AstralAuction h = new AstralAuction();
         h.init(this.plugin, this.data("auction"));
         h.start();
         this.bind("ah", new AuctionCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("baltop", () -> {
         AstralBaltop h = new AstralBaltop();
         h.init(this.plugin, this.data("baltop"));
         h.start();
         this.bind("baltop", new BaltopCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("orders", () -> {
         AstralOrder h = new AstralOrder();
         h.init(this.plugin, this.data("order"));
         h.start();
         this.bind("orders", new OrdersCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("shop", () -> {
         AstralShop h = new AstralShop();
         h.init(this.plugin, this.data("shop"));
         h.start();
         this.bind("shop", new ShopCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("sell", () -> {
         AstralSell h = new AstralSell();
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
         AstralLeaderboards h = new AstralLeaderboards();
         h.init(this.plugin, this.data("lb"));
         h.start();
         this.bind("lb", new LeaderboardCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("billford", () -> {
         AstralBillford h = new AstralBillford();
         h.init(this.plugin, this.data("billford"));
         h.start();
         this.bind("billford", new BillfordCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("bounties", () -> {
         AstralBounties h = new AstralBounties();
         h.init(this.plugin, this.data("bounties"));
         h.start();
         this.bind("bounty", new BountyCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("crates", () -> {
         AstralCrates h = new AstralCrates();
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
         AstralShards h = new AstralShards();
         h.init(this.plugin, this.data("shards"));
         h.start();
         this.bind("shards", new ShardsCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("teams", () -> {
         AstralTeams h = new AstralTeams();
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
         AstralTPA h = new AstralTPA();
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
         AstralWarps h = new AstralWarps();
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
         AstralTools h = new AstralTools();
         h.init(this.plugin, this.data("tools"));
         h.start();
         this.bind("tools", new ToolsCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("kits", () -> {
         AstralKits h = new AstralKits();
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
         AstralSpawners h = new AstralSpawners();
         h.init(this.plugin, this.data("spawners"));
         h.start();
         AstralCommand asCmd = new AstralCommand(h);
         this.bind("spawners", asCmd);
         this.bind("as", asCmd);
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("chat", () -> {
         AstralChat h = new AstralChat();
         h.init(this.plugin, this.data("chat2"));
         h.start();
         this.bind("chat", new AstralChatCommand(h));
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
         AstralCombat h = new AstralCombat();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("combat", new CombatCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("enderchest", () -> {
         AstralEC h = new AstralEC();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("echest", new EcCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("help", () -> {
         AstralHelp h = new AstralHelp();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("help", new HelpCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("hide", () -> {
         AstralHide h = new AstralHide();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("hide", new HideCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("keyall", () -> {
         AstralKeyall h = new AstralKeyall();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("keyall", new KeyallCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("maintenance", () -> {
         astralMaintenance h = new astralMaintenance();
         h.init(this.plugin, this.data("maintenance"));
         h.start();
         this.bind("maintenance", new MaintenanceCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("media", () -> {
         AstralMedia h = new AstralMedia();
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
         AstralMobs h = new AstralMobs();
         h.init(this.plugin, this.flatData());
         h.start();

         try {
            this.bind("mobs", new MobsToggleCommand(h));
         } catch (Exception var5) {
            try {
               this.bind("mobs", new AstralMobsCommand(h));
            } catch (Exception var4) {
            }
         }

         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("reports", () -> {
         AstralReports h = new AstralReports();
         h.init(this.plugin, this.data("reports"));
         h.start();
         this.bind("report", new ReportCommand(h));
         this.bind("reports", new ReportsCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("rtp", () -> {
         AstralRTP h = new AstralRTP();
         h.init(this.plugin, this.data("rtp"));
         h.start();
         this.bind("rtp", new RTPCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
         AstralRTPQ q = new AstralRTPQ();
         q.init(this.plugin, this.data("rtp"));
         q.start();
         this.bind("rtpqueue", new RTPQueueCommand(q));
         this.bind("rtpq", new RTPQueueCommand(q));
         var10000 = this.shutdown;
         Objects.requireNonNull(q);
         var10000.add(q::stop);
      });
      this.boot("rules", () -> {
         AstralRules h = new AstralRules();
         h.init(this.plugin, this.flatData());
         h.start();
         this.bind("rules", new RulesCommand(h));
         List<Runnable> var10000 = this.shutdown;
         Objects.requireNonNull(h);
         var10000.add(h::stop);
      });
      this.boot("safety", () -> {
         AstralSafety h = new AstralSafety();
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
         AstralWhitelister h = new AstralWhitelister();
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
