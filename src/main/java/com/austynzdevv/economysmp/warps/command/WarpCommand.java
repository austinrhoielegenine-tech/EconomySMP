package com.austynzdevv.economysmp.warps.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.warps.EconomyWarps;
import com.austynzdevv.economysmp.warps.NamedLocation;
import com.austynzdevv.economysmp.warps.gui.WarpMenus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class WarpCommand implements CommandExecutor, TabCompleter {
   private final EconomyWarps plugin;

   public WarpCommand(EconomyWarps plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      String name = command.getName().toLowerCase(Locale.ROOT);
      if (name.equals("warpreload")) {
         if (!sender.hasPermission("economysmpcore.system.warps.admin")) {
            this.msg(sender, "no-permission", "&cYou don't have permission!");
            return true;
         } else {
            this.plugin.reloadAll();
            this.msg(sender, "reload-success", "&aEconomyWarps reloaded.");
            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         boolean var10000;
         switch (name) {
            case "setwarp":
               if (!player.hasPermission("economysmpcore.system.warps.admin")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var49 = true;
                  var10000 = var49;
               } else if (args.length < 1) {
                  this.msg(player, "setwarp-usage", "&7Correct usage: &#00A0FC/setwarp [warp name]");
                  boolean var50 = true;
                  var10000 = var50;
               } else if (!this.plugin.getWarps().setWarp(args[0], player.getLocation())) {
                  this.msg(player, "warp-already-exists", "&7Warp &#00A0FC%warp% &7already exists.", "%warp%", args[0]);
                  boolean var51 = true;
                  var10000 = var51;
               } else {
                  this.msg(player, "warp-set", "&7Warp &#00A0FC%warp% &7has been set to your location.", "%warp%", args[0]);
                  boolean var52 = true;
                  var10000 = var52;
               }
               break;
            case "delwarp":
               if (!player.hasPermission("economysmpcore.system.warps.admin")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var45 = true;
                  var10000 = var45;
               } else if (args.length < 1) {
                  this.msg(player, "delwarp-usage", "&7Correct usage: &#00A0FC/delwarp [warp name]");
                  boolean var46 = true;
                  var10000 = var46;
               } else if (!this.plugin.getWarps().deleteWarp(args[0])) {
                  this.msg(player, "warp-not-found", "&7Warp &#00A0FC%warp% &7does not exist.", "%warp%", args[0]);
                  boolean var47 = true;
                  var10000 = var47;
               } else {
                  this.msg(player, "warp-deleted", "&#00A0FC%warp% &7warp has been removed.", "%warp%", args[0]);
                  boolean var48 = true;
                  var10000 = var48;
               }
               break;
            case "warp":
               if (!player.hasPermission("economysmpcore.system.warps.warp") && !player.hasPermission("economysmpcore.system.warps.use")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var44 = true;
                  var10000 = var44;
               } else if (args.length < 1) {
                  String list = String.join(", ", this.plugin.getWarps().getWarps().keySet());
                  if (list.isEmpty()) {
                     list = "none";
                  }

                  this.msg(player, "available-warps", "&cUsage: /warp (destination)\n&cDestinations: %warps%", "%warps%", list);
                  boolean var41 = true;
                  var10000 = var41;
               } else {
                  NamedLocation warp = this.plugin.getWarps().getWarp(args[0]);
                  if (warp == null) {
                     this.msg(player, "warp-not-found", "&7Warp &#00A0FC%warp% &7does not exist.", "%warp%", args[0]);
                     boolean var42 = true;
                     var10000 = var42;
                  } else {
                     this.plugin.getTeleports().start(player, warp, "warp");
                     boolean var43 = true;
                     var10000 = var43;
                  }
               }
               break;
            case "spawn":
               if (!player.hasPermission("economysmpcore.system.warps.spawn") && !player.hasPermission("economysmpcore.system.warps.use")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var40 = true;
                  var10000 = var40;
               } else if (args.length >= 1) {
                  NamedLocation spawn = this.plugin.getWarps().getSpawn(args[0]);
                  if (spawn == null) {
                     this.msg(player, "spawn-not-found", "&cSpawn not found!");
                     boolean var35 = true;
                     var10000 = var35;
                  } else {
                     this.plugin.getTeleports().start(player, spawn, "spawn");
                     boolean var36 = true;
                     var10000 = var36;
                  }
               } else {
                  if (this.plugin.getConfig().getBoolean("spawn-gui", true)) {
                     if (this.plugin.getWarps().getSpawns().isEmpty()) {
                        this.msg(player, "no-spawns", "&cNo spawns have been set yet!");
                        boolean var37 = true;
                        var10000 = var37;
                        break;
                     }

                     WarpMenus.openSpawn(player, 0);
                  } else {
                     NamedLocation spawn = this.plugin.getWarps().randomSpawn();
                     if (spawn == null) {
                        spawn = this.plugin.getWarps().defaultSpawn();
                     }

                     if (spawn == null) {
                        this.msg(player, "no-spawns", "&cNo spawns have been set yet!");
                        boolean var39 = true;
                        var10000 = var39;
                        break;
                     }

                     this.plugin.getTeleports().start(player, spawn, "spawn");
                  }

                  boolean var38 = true;
                  var10000 = var38;
               }
               break;
            case "setspawn":
               if (!player.hasPermission("economysmpcore.system.warps.admin")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var31 = true;
                  var10000 = var31;
               } else if (args.length < 1) {
                  this.msg(player, "setspawn-usage", "&cUsage: /setspawn <name> [maxPlayers]");
                  boolean var32 = true;
                  var10000 = var32;
               } else {
                  int max = -1;
                  if (args.length >= 2) {
                     try {
                        max = Integer.parseInt(args[1]);
                     } catch (NumberFormatException var13) {
                        this.msg(player, "invalid-number", "&cPlease provide a valid number for max players!");
                        boolean var33 = true;
                        var10000 = var33;
                        break;
                     }
                  }

                  this.plugin.getWarps().setSpawn(args[0], player.getLocation(), max);
                  this.msg(player, "setspawn-success", "&aSpawn %name% has been set!", "%name%", args[0]);
                  boolean var34 = true;
                  var10000 = var34;
               }
               break;
            case "delspawn":
               if (!player.hasPermission("economysmpcore.system.warps.admin")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var27 = true;
                  var10000 = var27;
               } else if (args.length < 1) {
                  this.msg(player, "delspawn-usage", "&cUsage: /delspawn <name>");
                  boolean var28 = true;
                  var10000 = var28;
               } else if (!this.plugin.getWarps().deleteSpawn(args[0])) {
                  this.msg(player, "spawn-not-found", "&cSpawn not found!");
                  boolean var29 = true;
                  var10000 = var29;
               } else {
                  this.msg(player, "delspawn-success", "&aSpawn %name% has been deleted!", "%name%", args[0]);
                  boolean var30 = true;
                  var10000 = var30;
               }
               break;
            case "afk":
               if (!player.hasPermission("economysmpcore.system.warps.afk") && !player.hasPermission("economysmpcore.system.warps.use")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var26 = true;
                  var10000 = var26;
               } else if (args.length >= 1) {
                  NamedLocation afk = this.plugin.getWarps().getAfk(args[0]);
                  if (afk == null) {
                     this.msg(player, "afk-not-found", "&cAFK area not found!");
                     boolean var21 = true;
                     var10000 = var21;
                  } else {
                     this.plugin.getTeleports().start(player, afk, "afk");
                     boolean var22 = true;
                     var10000 = var22;
                  }
               } else {
                  if (this.plugin.getConfig().getBoolean("afk-gui", true)) {
                     if (this.plugin.getWarps().getAfks().isEmpty()) {
                        this.msg(player, "no-afk-areas", "&cNo AFK areas have been set yet!");
                        boolean var23 = true;
                        var10000 = var23;
                        break;
                     }

                     WarpMenus.openAfk(player, 0);
                  } else {
                     NamedLocation afk = this.plugin.getWarps().randomAfk();
                     if (afk == null) {
                        this.msg(player, "no-afk-areas", "&cNo AFK areas have been set yet!");
                        boolean var25 = true;
                        var10000 = var25;
                        break;
                     }

                     this.plugin.getTeleports().start(player, afk, "afk");
                  }

                  boolean var24 = true;
                  var10000 = var24;
               }
               break;
            case "setafk":
               if (!player.hasPermission("economysmpcore.system.warps.admin")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var17 = true;
                  var10000 = var17;
               } else if (args.length < 1) {
                  this.msg(player, "setafk-usage", "&cUsage: /setafk <name> [maxPlayers]");
                  boolean var18 = true;
                  var10000 = var18;
               } else {
                  int max = -1;
                  if (args.length >= 2) {
                     try {
                        max = Integer.parseInt(args[1]);
                     } catch (NumberFormatException var12) {
                        this.msg(player, "invalid-number", "&cPlease provide a valid number for max players!");
                        boolean var19 = true;
                        var10000 = var19;
                        break;
                     }
                  }

                  this.plugin.getWarps().setAfk(args[0], player.getLocation(), max);
                  this.msg(player, "setafk-success", "&aAFK area %name% has been set!", "%name%", args[0]);
                  boolean var20 = true;
                  var10000 = var20;
               }
               break;
            case "delafk":
               if (!player.hasPermission("economysmpcore.system.warps.admin")) {
                  this.msg(player, "no-permission", "&cYou don't have permission!");
                  boolean var9 = true;
                  var10000 = var9;
               } else if (args.length < 1) {
                  this.msg(player, "delafk-usage", "&cUsage: /delafk <name>");
                  boolean var14 = true;
                  var10000 = var14;
               } else if (!this.plugin.getWarps().deleteAfk(args[0])) {
                  this.msg(player, "afk-not-found", "&cAFK area not found!");
                  boolean var15 = true;
                  var10000 = var15;
               } else {
                  this.msg(player, "delafk-success", "&aAFK area %name% has been deleted!", "%name%", args[0]);
                  boolean var16 = true;
                  var10000 = var16;
               }
               break;
            default:
               boolean var53 = true;
               var10000 = var53;
         }

         return var10000;
      } else {
         this.msg(sender, "player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private void msg(CommandSender sender, String key, String def, String... replacements) {
      String text = this.plugin.getMessages().get(key, def);
      if (text == null) {
         text = def;
      }

      if (replacements != null) {
         for(int i = 0; i + 1 < replacements.length; i += 2) {
            if (replacements[i] != null && replacements[i + 1] != null) {
               text = text.replace(replacements[i], replacements[i + 1]);
            }
         }
      }

      sender.sendMessage(ColorUtil.parse(text));
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      String name = command.getName().toLowerCase(Locale.ROOT);
      if (args.length == 1) {
         String p = args[0].toLowerCase(Locale.ROOT);
         List<String> options = new ArrayList();
         switch (name) {
            case "warp":
            case "delwarp":
               options.addAll(this.plugin.getWarps().getWarps().keySet());
               break;
            case "spawn":
            case "delspawn":
               options.addAll(this.plugin.getWarps().getSpawns().keySet());
               break;
            case "afk":
            case "delafk":
               options.addAll(this.plugin.getWarps().getAfks().keySet());
         }

         return (List)options.stream().filter((s) -> s.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList());
      } else {
         return new ArrayList();
      }
   }
}
