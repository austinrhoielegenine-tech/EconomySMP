package me.xyr0.astralsmpcore.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.CommandRegistry;
import me.xyr0.astralsmpcore.CoreSystemsModule;
import me.xyr0.astralsmpcore.DoubleJumpModule;
import me.xyr0.astralsmpcore.DuelsModule;
import me.xyr0.astralsmpcore.HologramsModule;
import me.xyr0.astralsmpcore.LinksModule;
import me.xyr0.astralsmpcore.Module;
import me.xyr0.astralsmpcore.MsgModule;
import me.xyr0.astralsmpcore.NightVisionModule;
import me.xyr0.astralsmpcore.PayModule;
import me.xyr0.astralsmpcore.PhantomsModule;
import me.xyr0.astralsmpcore.ProfileModule;
import me.xyr0.astralsmpcore.RtpZoneModule;
import me.xyr0.astralsmpcore.SettingsModule;
import me.xyr0.astralsmpcore.StatsModule;
import me.xyr0.astralsmpcore.TabModule;
import me.xyr0.astralsmpcore.VoiceChatModule;
import me.xyr0.astralsmpcore.WelcomerModule;
import me.xyr0.astralsmpcore.homes.HomesModule;
import me.xyr0.astralsmpcore.moderation.ModerationModule;
import me.xyr0.astralsmpcore.punishment2.PunishmentModule;
import me.xyr0.astralsmpcore.scoreboard.FoliaSidebar;
import me.xyr0.astralsmpcore.scoreboard.ScoreboardModule;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModuleManager {
   private final AstralSMPcore plugin;
   private final Map<String, Module> modules = new HashMap();

   public ModuleManager(AstralSMPcore plugin) {
      this.plugin = plugin;
   }

   public void loadModules() {
      this.modules.clear();
      this.registerIfEnabled("msg", new MsgModule(this.plugin));
      this.registerIfEnabled("pay", new PayModule(this.plugin));
      this.registerIfEnabled("stats", new StatsModule(this.plugin));
      this.tryRegisterAntiHealth();
      this.registerIfEnabled("settings", new SettingsModule(this.plugin));
      this.registerIfEnabled("scoreboard", new ScoreboardModule(this.plugin));
      this.modules.put("nightvision", new NightVisionModule(this.plugin));
      this.modules.put("tab", new TabModule(this.plugin));
      this.registerIfEnabled("homes", new HomesModule(this.plugin));
      this.registerIfEnabled("moderation", new ModerationModule(this.plugin));
      this.registerIfEnabled("welcomer", new WelcomerModule(this.plugin));
      this.registerIfEnabled("doublejump", new DoubleJumpModule(this.plugin));
      this.registerIfEnabled("profile", new ProfileModule(this.plugin));
      this.registerIfEnabled("duels", new DuelsModule(this.plugin));
      this.registerIfEnabled("links", new LinksModule(this.plugin));
      this.registerIfEnabled("voicechat", new VoiceChatModule(this.plugin));
      this.registerIfEnabled("punishment", new PunishmentModule(this.plugin));
      this.registerIfEnabled("phantoms", new PhantomsModule(this.plugin));
      this.registerIfEnabled("holograms", new HologramsModule(this.plugin));
      this.registerIfEnabled("rtp", new RtpZoneModule(this.plugin));
      this.modules.put("core-systems", new CoreSystemsModule(this.plugin));

      for(Module m : this.modules.values()) {
         try {
            m.onEnable();
            this.plugin.getLogger().info("Enabled system: " + m.getName());
         } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Failed enabling " + m.getName(), e);
         }
      }

      this.registerFallbacks();
      this.plugin.getLogger().info("Loaded " + this.modules.size() + " system modules.");
   }

   private void registerFallbacks() {
      CommandRegistry reg = this.plugin.commands();
      String[] cmds = new String[]{"ah", "sell", "sellmulti", "worth", "worthtoggle", "sellaxe", "sellhistory", "orders", "crates", "baltop", "billford", "bounty", "teams", "tpa", "tpahere", "tpaccept", "tpadeny", "warps", "setwarp", "delwarp", "shop", "shards", "spawners", "tools", "echest", "rtp", "rtpqueue", "daily", "help", "hide", "keyall", "media", "mobs", "rules", "report", "reports", "maintenance", "combat", "chat", "clearchat", "lb", "punish", "chainmail", "teamchat", "ordertoggle", "teamtoggle", "fastcrystal"};

      for(String cmd : cmds) {
         reg.registerHandlerIfAbsent(cmd, (sender, args) -> {
            String sys = this.systemOf(cmd);
            if (sys != null && !this.plugin.configs().isEnabled(sys)) {
               sender.sendMessage("§cSystem disabled: " + sys);
               return true;
            } else {
               sender.sendMessage("§8[§bAstral§fSMP§8] §7/" + cmd + " §fis available. Full GUI handlers load with system pack.");
               if (sender instanceof Player) {
                  Player p = (Player)sender;
                  Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, "§8" + cmd.toUpperCase());
                  ItemStack item = new ItemStack(Material.BOOK);
                  ItemMeta meta = item.getItemMeta();
                  if (meta != null) {
                     meta.setDisplayName("§b/" + cmd);
                     meta.setLore(List.of("§7System: §f" + (sys == null ? cmd : sys), "§7Enable in main.config.yml"));
                     item.setItemMeta(meta);
                  }

                  inv.setItem(13, item);
                  p.openInventory(inv);
               }

               return true;
            }
         });
      }

   }

   private String systemOf(String cmd) {
      String var10000;
      switch (cmd) {
         case "ah":
            var10000 = "auction-house";
            break;
         case "sell":
         case "sellmulti":
         case "worth":
         case "worthtoggle":
         case "sellaxe":
         case "sellhistory":
            var10000 = "sell";
            break;
         case "orders":
         case "ordertoggle":
            var10000 = "orders";
            break;
         case "crates":
            var10000 = "crates";
            break;
         case "baltop":
            var10000 = "baltop";
            break;
         case "billford":
            var10000 = "billford";
            break;
         case "bounty":
            var10000 = "bounties";
            break;
         case "teams":
         case "teamchat":
         case "teamtoggle":
            var10000 = "teams";
            break;
         case "tpa":
         case "tpahere":
         case "tpaccept":
         case "tpadeny":
            var10000 = "tpa";
            break;
         case "warps":
         case "setwarp":
         case "delwarp":
            var10000 = "warps";
            break;
         case "shop":
            var10000 = "shop";
            break;
         case "shards":
            var10000 = "shards";
            break;
         case "spawners":
            var10000 = "spawners";
            break;
         case "tools":
            var10000 = "tools";
            break;
         case "echest":
            var10000 = "enderchest";
            break;
         case "rtp":
            var10000 = "rtp";
            break;
         case "rtpqueue":
         case "rtpq":
         case "rtpzone":
            var10000 = "rtp";
            break;
         case "daily":
            var10000 = "daily";
            break;
         case "help":
            var10000 = "help";
            break;
         case "hide":
            var10000 = "hide";
            break;
         case "keyall":
            var10000 = "keyall";
            break;
         case "media":
            var10000 = "media";
            break;
         case "mobs":
            var10000 = "mobs";
            break;
         case "rules":
            var10000 = "rules";
            break;
         case "report":
         case "reports":
            var10000 = "reports";
            break;
         case "maintenance":
            var10000 = "maintenance";
            break;
         case "combat":
            var10000 = "combat";
            break;
         case "chat":
         case "clearchat":
            var10000 = "chat";
            break;
         case "lb":
            var10000 = "leaderboards";
            break;
         case "punish":
            var10000 = "punishment";
            break;
         case "chainmail":
            var10000 = "chainmail";
            break;
         case "fastcrystal":
            var10000 = "settings";
            break;
         default:
            var10000 = null;
      }

      return var10000;
   }

   private void tryRegisterAntiHealth() {
      if (this.plugin.configs().isEnabled("anti-health-indicator")) {
         if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            this.plugin.getLogger().warning("ProtocolLib not found. Anti health indicator skipped.");
         } else {
            try {
               Class<?> clazz = Class.forName("me.xyr0.astralsmpcore.antihealth.AntiHealthModule");
               Module mod = (Module)clazz.getConstructor(AstralSMPcore.class).newInstance(this.plugin);
               this.modules.put("anti-health-indicator", mod);
            } catch (LinkageError | Exception t) {
               this.plugin.getLogger().warning("Anti health indicator unavailable: " + ((Throwable)t).getMessage());
            }

         }
      }
   }

   private void registerIfEnabled(String key, Module module) {
      if (this.plugin.configs().isEnabled(key)) {
         this.modules.put(key.toLowerCase(), module);
      }

   }

   public void reloadModules() {
      this.disableAll();
      this.runCleanup("event listener cleanup", () -> HandlerList.unregisterAll(this.plugin));
      this.runCleanup("task cleanup", Tasks::cancelAll);
      if (Tasks.isFolia()) {
         this.runCleanup("Folia global task cleanup", () -> Bukkit.getGlobalRegionScheduler().cancelTasks(this.plugin));
         this.runCleanup("Folia async task cleanup", () -> Bukkit.getAsyncScheduler().cancelTasks(this.plugin));
      }

      if (this.plugin.commands() != null) {
         this.runCleanup("command cleanup", () -> this.plugin.commands().clearHandlers());
      }

      this.runCleanup("scoreboard cleanup", () -> {
         for(Player player : Bukkit.getOnlinePlayers()) {
            FoliaSidebar.remove(player);
         }

      });

      try {
         this.loadModules();
      } catch (Exception ex) {
         this.plugin.getLogger().log(Level.SEVERE, "Module load after reload failed", ex);
      }

      if (this.plugin.commands() != null) {
         this.runCleanup("command re-registration", () -> this.plugin.commands().registerAll());
      }

   }

   private void runCleanup(String operation, Runnable cleanup) {
      try {
         cleanup.run();
      } catch (RuntimeException ex) {
         this.plugin.getLogger().log(Level.WARNING, "Failed during " + operation + " while reloading AstralSMPcore", ex);
      }

   }

   public void disableAll() {
      for(Module m : this.modules.values()) {
         try {
            m.onDisable();
         } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, "Error disabling " + m.getName(), e);
         }
      }

      this.modules.clear();
   }

   public void enable(String name) {
      String key = name.toLowerCase().replace("_", "-");
      if (!this.modules.containsKey(key)) {
         this.plugin.configs().setEnabled(key, true);
         Module m = this.create(key);
         if (m != null) {
            this.modules.put(key, m);
            m.onEnable();
         }

      }
   }

   public void disable(String name) {
      String key = name.toLowerCase().replace("_", "-");
      Module m = (Module)this.modules.remove(key);
      if (m != null) {
         m.onDisable();
      }

      this.plugin.configs().setEnabled(key, false);
   }

   private Module create(String key) {
      Object var10000;
      switch (key) {
         case "msg" -> var10000 = new MsgModule(this.plugin);
         case "pay" -> var10000 = new PayModule(this.plugin);
         case "stats" -> var10000 = new StatsModule(this.plugin);
         case "settings" -> var10000 = new SettingsModule(this.plugin);
         case "scoreboard" -> var10000 = new ScoreboardModule(this.plugin);
         case "nightvision" -> var10000 = new NightVisionModule(this.plugin);
         case "tab" -> var10000 = new TabModule(this.plugin);
         case "homes" -> var10000 = new HomesModule(this.plugin);
         case "links" -> var10000 = new LinksModule(this.plugin);
         case "voicechat" -> var10000 = new VoiceChatModule(this.plugin);
         default -> var10000 = null;
      }

      return (Module)var10000;
   }

   public boolean isLoaded(String name) {
      return this.modules.containsKey(name.toLowerCase());
   }

   public Module get(String name) {
      return (Module)this.modules.get(name.toLowerCase());
   }
}
