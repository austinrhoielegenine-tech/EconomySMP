package com.austynzdevv.economysmp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandRegistry implements CommandExecutor, TabCompleter {
   private final EconomySMP plugin;
   private final Map<String, String> commandToSystem = new HashMap();
   private final Map<String, BiFunction<CommandSender, String[], Boolean>> handlers = new HashMap();
   private final Map<String, TabCompleter> tabCompleters = new HashMap();

   public BiFunction<CommandSender, String[], Boolean> getHandler(String name) {
      return (BiFunction)this.handlers.get(name == null ? null : name.toLowerCase(Locale.ROOT));
   }

   public boolean dispatch(CommandSender sender, String label, String[] args) {
      BiFunction<CommandSender, String[], Boolean> h = this.getHandler(label);
      return h == null ? false : Boolean.TRUE.equals(h.apply(sender, args == null ? new String[0] : args));
   }

   public CommandRegistry(EconomySMP plugin) {
      this.plugin = plugin;
      this.mapDefaults();
   }

   private void mapDefaults() {
      this.bind("ah", "auction-house");
      this.bind("homes", "homes");
      this.bind("home", "homes");
      this.bind("sethome", "homes");
      this.bind("delhome", "homes");
      this.bind("sell", "sell");
      this.bind("sellmulti", "sell");
      this.bind("worth", "sell");
      this.bind("worthtoggle", "sell");
      this.bind("sellaxe", "sell");
      this.bind("sellhistory", "sell");
      this.bind("orders", "orders");
      this.bind("crates", "crates");
      this.bind("crate", "crates");
      this.bind("baltop", "baltop");
      this.bind("billford", "billford");
      this.bind("bounty", "bounties");
      this.bind("teams", "teams");
      this.bind("team", "teams");
      this.bind("tpa", "tpa");
      this.bind("tpahere", "tpa");
      this.bind("tpaccept", "tpa");
      this.bind("tpadeny", "tpa");
      this.bind("tpdeny", "tpa");
      this.bind("tpacancel", "tpa");
      this.bind("tpauto", "tpa");
      this.bind("warps", "warps");
      this.bind("warp", "warps");
      this.bind("spawn", "warps");
      this.bind("setspawn", "warps");
      this.bind("delspawn", "warps");
      this.bind("afk", "warps");
      this.bind("setafk", "warps");
      this.bind("delafk", "warps");
      this.bind("setwarp", "warps");
      this.bind("delwarp", "warps");
      this.bind("shop", "shop");
      this.bind("shards", "shards");
      this.bind("spawners", "spawners");
      this.bind("as", "spawners");
      this.bind("tools", "tools");
      this.bind("echest", "enderchest");
      this.bind("rtp", "rtp");
      this.bind("rtpzone", "rtp");
      this.bind("rtpqueue", "rtp");
      this.bind("help", "help");
      this.bind("hide", "hide");
      this.bind("safety", "safety");
      this.bind("keyall", "keyall");
      this.bind("media", "media");
      this.bind("mobs", "mobs");
      this.bind("rules", "rules");
      this.bind("report", "reports");
      this.bind("reports", "reports");
      this.bind("maintenance", "maintenance");
      this.bind("combat", "combat");
      this.bind("chat", "chat");
      this.bind("cm", "chainmail");
      this.bind("cmtoggle", "chainmail");
      this.bind("chattoggle", "chat");
      this.bind("clearchat", "chat");
      this.bind("lb", "leaderboards");
      this.bind("punish", "punishment");
      this.bind("chainmail", "chainmail");
      this.bind("stats", "stats");
      this.bind("pay", "pay");
      this.bind("bal", "pay");
      this.bind("money", "pay");
      this.bind("balance", "pay");
      this.bind("paytoggle", "pay");
      this.bind("msg", "msg");
      this.bind("reply", "msg");
      this.bind("msgtoggle", "msg");
      this.bind("msgignore", "msg");
      this.bind("msgunignore", "msg");
      this.bind("settings", "settings");
      this.bind("holograms", "holograms");
      this.bind("aholograms", "holograms");
      this.bind("aholo", "holograms");
      this.bind("tpaheretoggle", "settings");
      this.bind("globaltoggle", "settings");
      this.bind("teamchattoggle", "settings");
      this.bind("duelrequesttoggle", "settings");
      this.bind("duelsongtoggle", "settings");
      this.bind("tpaconfirmtoggle", "settings");
      this.bind("mobtoggle", "settings");
      this.bind("soundstoggle", "settings");
      this.bind("fastcrystal", "settings");
      this.bind("servertoggle", "settings");
      this.bind("hotbartoggle", "settings");
      this.bind("bountytoggle", "settings");
      this.bind("auctiontoggle", "settings");
      this.bind("explosionsoundtoggle", "settings");
      this.bind("quickbuy", "settings");
      this.bind("visibilitytoggle", "settings");
      this.bind("playervisibility", "settings");
      this.bind("kill", "moderation");
      this.bind("die", "moderation");
      this.bind("duels", "duels");
      this.bind("queue", "duels");
      this.bind("leave", "duels");
      this.bind("draw", "duels");
      this.bind("spectate", "duels");
      this.bind("duelsadmin", "duels");
      this.bind("staffchat", "moderation");
      this.bind("st", "moderation");
      this.bind("wipe", "moderation");
      this.bind("effect", "moderation");
      this.bind("enchant", "moderation");
      this.bind("clear", "moderation");
      this.bind("give", "moderation");
      this.bind("eco", "moderation");
      this.bind("spawnfakeplayer", "moderation");
      this.bind("spawnfakestash", "moderation");
      this.bind("killconfirm", "moderation");
      this.bind("rtpq", "rtp");
      this.bind("v", "moderation");
      this.bind("vanish", "moderation");
      this.bind("sus", "moderation");
      this.bind("gtph", "moderation");
      this.bind("gtp", "moderation");
      this.bind("gmsp", "moderation");
      this.bind("gms", "moderation");
      this.bind("gmc", "moderation");
      this.bind("ping", "moderation");
      this.bind("fp", "moderation");
      this.bind("findplayer", "moderation");
      this.bind("sb", "scoreboard");
      this.bind("nv", "nightvision");
      this.bind("nightvision", "nightvision");
      this.bind("teamchat", "teams");
      this.bind("ordertoggle", "orders");
      this.bind("paymenttoggle", "pay");
      this.bind("soundstoggle", "settings");
      this.bind("teamtoggle", "teams");
      this.bind("fastcrystal", "settings");
      this.bind("servertoggle", "settings");
      this.bind("hotbartoggle", "settings");
      this.bind("bountytoggle", "settings");
      this.bind("auctiontoggle", "settings");
      this.bind("explosionsoundtoggle", "settings");
      this.bind("quickbuy", "settings");
      this.bind("visibilitytoggle", "settings");
      this.bind("playervisibility", "settings");
      this.bind("profile", "profile");
      this.bind("fly", "doublejump");
      this.bind("invsee", "moderation");
      this.bind("broadcast", "moderation");
      this.bind("bc", "moderation");
      this.bind("unlink", "links");
      this.bind("link", "links");
      this.bind("voicechat", "voicechat");
      this.bind("vc", "voicechat");
      this.bind("phantom", "phantoms");
      this.bind("phantoms", "phantoms");
      this.bind("offend", "punishment");
      this.bind("unoffend", "punishment");
      this.bind("offendip", "punishment");
      this.bind("freeze", "punishment");
      this.bind("unfreeze", "punishment");
      this.bind("kick", "punishment");
      this.bind("checkban", "punishment");
      this.bind("checkmute", "punishment");
      this.bind("history", "punishment");
      this.bind("punishment", "punishment");
      this.bind("punish", "punishment");
   }

   private void bind(String cmd, String system) {
      this.commandToSystem.put(cmd.toLowerCase(Locale.ROOT), system);
   }

   public void clearHandlers() {
      this.handlers.clear();
      this.tabCompleters.clear();
   }

   public void registerHandler(String command, BiFunction<CommandSender, String[], Boolean> handler) {
      this.handlers.put(command.toLowerCase(Locale.ROOT), handler);
   }

   public void registerHandlerIfAbsent(String command, BiFunction<CommandSender, String[], Boolean> handler) {
      this.handlers.putIfAbsent(command.toLowerCase(Locale.ROOT), handler);
   }

   public void registerTabCompleter(String command, TabCompleter completer) {
      if (completer != null) {
         this.tabCompleters.put(command.toLowerCase(Locale.ROOT), completer);
      }

   }

   public void registerAll() {
      Set<String> names = new HashSet(this.commandToSystem.keySet());
      names.addAll(this.handlers.keySet());

      for(String name : names) {
         String system = (String)this.commandToSystem.get(name.toLowerCase(Locale.ROOT));
         if (system != null && !this.plugin.configs().isEnabled(system)) {
            this.unregisterCommand(name);
         } else {
            PluginCommand pc = this.plugin.getCommand(name);
            if (pc != null) {
               TabCompleter completer = pc.getTabCompleter();
               if (completer != null && completer != this) {
                  this.tabCompleters.put(name.toLowerCase(Locale.ROOT), completer);
               }

               pc.setExecutor(this);
               pc.setTabCompleter(this);
               this.ensureCommandMapped(pc);
            }
         }
      }

      for(String extra : new String[]{"profile", "fly", "invsee", "wipe", "spawnfakeplayer"}) {
         String system = (String)this.commandToSystem.get(extra.toLowerCase(Locale.ROOT));
         if (system != null && !this.plugin.configs().isEnabled(system)) {
            this.unregisterCommand(extra);
         } else {
            PluginCommand pc = this.plugin.getCommand(extra);
            if (pc != null) {
               pc.setExecutor(this);
               pc.setTabCompleter(this);
               this.ensureCommandMapped(pc);
            }
         }
      }

      PluginCommand core = this.plugin.getCommand("economysmp");
      if (core != null) {
         CoreCommand cc = new CoreCommand(this.plugin);
         core.setExecutor(cc);
         core.setTabCompleter(cc);
         this.ensureCommandMapped(core);
      }

   }

   private void ensureCommandMapped(PluginCommand pc) {
      if (pc != null) {
         try {
            CommandMap map = this.getCommandMap();
            if (map == null) {
               return;
            }

            String label = pc.getName().toLowerCase(Locale.ROOT);
            Field knownField = map.getClass().getDeclaredField("knownCommands");
            knownField.setAccessible(true);
            Map<String, Command> known = (Map)knownField.get(map);
            if (known == null) {
               return;
            }

            if (!(known.get(label) instanceof PluginCommand)) {
               known.put(label, pc);
            }

            String var10000 = this.plugin.getDescription().getName().toLowerCase(Locale.ROOT);
            String prefix = var10000 + ":" + label;
            known.put(prefix, pc);

            for(String alias : pc.getAliases()) {
               if (alias != null && !alias.isBlank()) {
                  String a;
                  label40: {
                     a = alias.toLowerCase(Locale.ROOT);
                     Object var11 = known.get(a);
                     if (var11 instanceof PluginCommand) {
                        PluginCommand other = (PluginCommand)var11;
                        if (other.getPlugin() != this.plugin) {
                           break label40;
                        }
                     }

                     known.put(a, pc);
                  }

                  known.put(this.plugin.getDescription().getName().toLowerCase(Locale.ROOT) + ":" + a, pc);
               }
            }
         } catch (Exception var12) {
         }

      }
   }

   private void unregisterCommand(String name) {
      if (name != null && !name.isBlank()) {
         String key = name.toLowerCase(Locale.ROOT);
         this.handlers.remove(key);
         this.tabCompleters.remove(key);

         try {
            PluginCommand pc = this.plugin.getCommand(key);
            if (pc != null) {
               pc.setExecutor((CommandExecutor)null);
               pc.setTabCompleter((TabCompleter)null);
            }

            CommandMap map = this.getCommandMap();
            if (map == null) {
               return;
            }

            Field knownField = map.getClass().getDeclaredField("knownCommands");
            knownField.setAccessible(true);
            Map<String, Command> known = (Map)knownField.get(map);
            if (known == null) {
               return;
            }

            List<String> remove = new ArrayList();

            for(Map.Entry<String, Command> entry : known.entrySet()) {
               Command cmd = (Command)entry.getValue();
               if (cmd != null) {
                  if (cmd.getName().equalsIgnoreCase(key)) {
                     remove.add((String)entry.getKey());
                  } else {
                     for(String alias : cmd.getAliases()) {
                        if (alias != null && alias.equalsIgnoreCase(key)) {
                           remove.add((String)entry.getKey());
                           break;
                        }
                     }

                     if (pc != null && cmd == pc) {
                        remove.add((String)entry.getKey());
                     }
                  }
               }
            }

            for(String r : remove) {
               known.remove(r);
            }
         } catch (Exception var13) {
         }

      }
   }

   private CommandMap getCommandMap() {
      try {
         Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         field.setAccessible(true);
         return (CommandMap)field.get(Bukkit.getServer());
      } catch (Exception var2) {
         return null;
      }
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      String name = command.getName().toLowerCase(Locale.ROOT);
      String system = (String)this.commandToSystem.get(name);
      if (system != null && !this.plugin.configs().isEnabled(system)) {
         return false;
      } else {
         BiFunction<CommandSender, String[], Boolean> handler = (BiFunction)this.handlers.get(name);
         if (handler != null) {
            return Boolean.TRUE.equals(handler.apply(sender, args));
         } else {
            if (sender instanceof Player) {
               String var10002 = this.plugin.getConfig().getString("settings.prefix", "");
               sender.sendMessage(this.color(var10002 + "&e/" + label + " &7is registered. System module is loading handlers..."));
            }

            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      TabCompleter completer = (TabCompleter)this.tabCompleters.get(command.getName().toLowerCase(Locale.ROOT));
      if (completer != null) {
         List<String> result = completer.onTabComplete(sender, command, alias, args);
         if (result != null) {
            return result;
         }
      }

      return args.length == 1 && sender instanceof Player ? null : List.of();
   }

   private String color(String s) {
      return TextUtil.color(s);
   }
}
