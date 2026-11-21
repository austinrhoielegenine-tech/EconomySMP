package com.austynzdevv.economysmp.teams.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.austynzdevv.economysmp.SettingsModule;
import com.austynzdevv.economysmp.teams.EconomyTeams;
import com.austynzdevv.economysmp.teams.Team;
import com.austynzdevv.economysmp.teams.TeamManager;
import com.austynzdevv.economysmp.teams.gui.MenuSession;
import com.austynzdevv.economysmp.teams.gui.TeamMenus;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class TeamCommand implements CommandExecutor, TabCompleter {
   private final EconomyTeams plugin;
   private final Map<UUID, BukkitRunnable> teleporting = new HashMap();

   public TeamCommand(EconomyTeams plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         if (sender instanceof Player) {
            Player player = (Player)sender;
            TeamMenus.openMain(player, 0, MenuSession.Sort.ONLINE);
            return true;
         } else {
            this.msg(sender, "errors.player-only", "&cOnly players can use this!");
            return true;
         }
      } else {
         boolean var10000;
         switch (args[0].toLowerCase(Locale.ROOT)) {
            case "help":
               var10000 = this.help(sender);
               break;
            case "reload":
               var10000 = this.reload(sender);
               break;
            case "create":
               var10000 = this.create(sender, args);
               break;
            case "leave":
               var10000 = this.leave(sender);
               break;
            case "disband":
            case "delete":
            case "disabnd":
               var10000 = this.disband(sender);
               break;
            case "invite":
               var10000 = this.invite(sender, args);
               break;
            case "accept":
            case "join":
               var10000 = this.accept(sender, args);
               break;
            case "cancel":
            case "deny":
               var10000 = this.cancel(sender);
               break;
            case "kick":
               var10000 = this.kick(sender, args);
               break;
            case "info":
               var10000 = this.info(sender, args);
               break;
            case "home":
               var10000 = this.home(sender);
               break;
            case "sethome":
               var10000 = this.setHome(sender);
               break;
            case "chat":
               var10000 = this.chat(sender, args);
               break;
            case "transfer":
               var10000 = this.transfer(sender, args);
               break;
            case "pvp":
               var10000 = this.pvp(sender);
               break;
            default:
               if (sender instanceof Player) {
                  Player player = (Player)sender;
                  TeamMenus.openMain(player, 0, MenuSession.Sort.ONLINE);
               } else {
                  this.help(sender);
               }

               var10000 = true;
         }

         return var10000;
      }
   }

   private boolean help(CommandSender sender) {
      String help = this.plugin.getMessages().get("command-usage", "");

      for(String line : help.split("\n")) {
         sender.sendMessage(ColorUtil.parse(line));
      }

      if (sender.hasPermission("economysmpcore.system.teams.admin") || sender.hasPermission("economysmpcore.system.teams.admin")) {
         String admin = this.plugin.getMessages().get("command-usage-admin", "");

         for(String line : admin.split("\n")) {
            sender.sendMessage(ColorUtil.parse(line));
         }
      }

      return true;
   }

   private boolean reload(CommandSender sender) {
      if (!sender.hasPermission("economysmpcore.system.teams.admin") && !sender.hasPermission("economysmpcore.system.teams.admin")) {
         this.msg(sender, "errors.no-permission", "&cYou do not have permission!");
         return true;
      } else {
         this.plugin.reloadAll();
         this.msg(sender, "team.plugin-reloaded", "&#00FF00Team plugin has been reloaded!");
         this.play(sender, "plugin-reloaded");
         return true;
      }
   }

   private boolean create(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 2) {
            this.msg(player, "errors.usage-create", "&cUsage: /team create <name>");
            return true;
         } else {
            TeamManager mgr = this.plugin.getTeams();
            if (mgr.isInTeam(player.getUniqueId())) {
               this.msg(player, "errors.already-in-team", "&cYou are already in a team!");
               this.play(player, "error-sound");
               return true;
            } else {
               String name = args[1];
               if (!mgr.isValidName(name)) {
                  int min = this.plugin.getConfig().getInt("min-team-name-length", this.plugin.getConfig().getInt("mind-team-name-length", 3));
                  int max = this.plugin.getConfig().getInt("max-team-name-length", 9);
                  this.msg(player, "errors.invalid-team-name-length", "&cTeam name must be between %min% and %max% characters long!", "%min%", String.valueOf(min), "%max%", String.valueOf(max));
                  return true;
               } else if (mgr.isBlockedName(name)) {
                  this.msg(player, "errors.blocked-team-name", "&cYou cannot create a team with that name!");
                  return true;
               } else if (mgr.getByName(name) != null) {
                  this.msg(player, "errors.team-already-exists", "&cA team with this name already exists!");
                  return true;
               } else {
                  Team team = mgr.create(player, name);
                  this.msg(player, "team.created", "&7Your team &#34eb9b%economysmpcore_teams_name% &7has been created", "%economysmpcore_teams_name%", team.getName());
                  this.play(player, "success-sound");
                  return true;
               }
            }
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean leave(CommandSender sender) {
      if (sender instanceof Player player) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         } else {
            String teamName = team.getName();
            boolean wasLeader = team.isLeader(player.getUniqueId());
            this.broadcast(team, "team.member-left", "&7The player &#34eb9b%player% &7has left the team!", player.getUniqueId(), "%player%", player.getName());
            this.plugin.getTeams().leave(player, team);
            this.msg(player, "team.left", "&#FC0000You have left the team!");
            if (wasLeader && this.plugin.getTeams().getByName(teamName) == null) {
               this.msg(player, "team.deleted", "&cTeam %economysmpcore_teams_name% has been deleted!", "%economysmpcore_teams_name%", teamName);
            }

            return true;
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean disband(CommandSender sender) {
      if (sender instanceof Player player) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         } else if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.TEAM_ADMIN)) {
            this.msg(player, "errors.not-leader", "&cOnly the team leader can do that!");
            return true;
         } else {
            String name = team.getName();
            this.broadcast(team, "team.deleted", "&cTeam %economysmpcore_teams_name% has been deleted!", (UUID)null, "%economysmpcore_teams_name%", name);
            this.plugin.getTeams().disband(team);
            return true;
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean invite(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 2) {
            this.msg(player, "errors.usage-invite", "&cUsage: /team invite <player>");
            return true;
         } else {
            Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
            if (team == null) {
               this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
               return true;
            } else if (!team.isLeader(player.getUniqueId())) {
               this.msg(player, "errors.not-leader", "&cOnly the team leader can do that!");
               return true;
            } else {
               Player target = Bukkit.getPlayerExact(args[1]);
               if (target == null) {
                  this.msg(player, "errors.player-not-found", "&cPlayer not found!");
                  return true;
               } else if (this.plugin.getTeams().isInTeam(target.getUniqueId())) {
                  this.msg(player, "errors.player-already-in-team", "&cThis player is already in a team!");
                  return true;
               } else if (team.getMembers().size() >= 27) {
                  this.msg(player, "errors.team-full", "&cYour team is full!");
                  return true;
               } else if (SettingsModule.isTeamInvitesDisabled(target.getUniqueId())) {
                  this.msg(player, "errors.invite-disabled", "&cThat player has team invites disabled.");
                  return true;
               } else {
                  this.plugin.getTeams().invite(target.getUniqueId(), team.getName());
                  this.msg(player, "team.invite-sent", "&7Invitation sent to &#34eb9b%player%", "%player%", target.getName());
                  List<String> received = this.plugin.getMessages().getList("team.invite-received");
                  if (received != null && !received.isEmpty()) {
                     for(String line : received) {
                        target.sendMessage(ColorUtil.parse(line.replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName()).replace("%player%", player.getName())));
                     }
                  } else {
                     target.sendMessage(ColorUtil.parse("&7You have been invited to the team &#34eb9b" + team.getName() + "&7! Use /team accept"));
                  }

                  this.play(player, "success-sound");
                  return true;
               }
            }
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean accept(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (this.plugin.getTeams().isInTeam(player.getUniqueId())) {
            this.msg(player, "errors.already-in-team", "&cYou are already in a team!");
            return true;
         } else {
            String invite = this.plugin.getTeams().getInvite(player.getUniqueId());
            if (args.length >= 2) {
               invite = args[1];
            }

            if (invite != null && !invite.isBlank()) {
               Team team = this.plugin.getTeams().getByName(invite);
               if (team == null) {
                  this.plugin.getTeams().clearInvite(player.getUniqueId());
                  this.msg(player, "errors.team-not-found", "&cThat team no longer exists!");
                  return true;
               } else if (team.getMembers().size() >= 27) {
                  this.msg(player, "errors.team-full", "&cThat team is full!");
                  return true;
               } else {
                  this.plugin.getTeams().join(player, team);
                  this.msg(player, "team.joined", "&7You have joined the team &#34eb9b%economysmpcore_teams_name%", "%economysmpcore_teams_name%", team.getName());
                  this.broadcast(team, "team.member-joined", "&7Player &#34eb9b%player% &7has joined the team", player.getUniqueId(), "%player%", player.getName());
                  this.play(player, "success-sound");
                  return true;
               }
            } else {
               this.msg(player, "errors.no-invite", "&cYou have no pending team invite!");
               return true;
            }
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean cancel(CommandSender sender) {
      if (sender instanceof Player player) {
         if (this.plugin.getTeams().getInvite(player.getUniqueId()) == null) {
            this.msg(player, "errors.no-invite", "&cYou have no pending team invite!");
            return true;
         } else {
            this.plugin.getTeams().clearInvite(player.getUniqueId());
            this.msg(player, "team.invite-cancelled", "&cInvite cancelled!");
            return true;
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean kick(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 2) {
            this.msg(player, "errors.usage-kick", "&cUsage: /team kick <player>");
            return true;
         } else {
            Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
            if (team == null) {
               this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
               return true;
            } else if (!team.isLeader(player.getUniqueId())) {
               this.msg(player, "errors.not-leader", "&cOnly the team leader can do that!");
               return true;
            } else {
               Player target = Bukkit.getPlayerExact(args[1]);
               UUID targetId;
               String targetName;
               if (target != null) {
                  targetId = target.getUniqueId();
                  targetName = target.getName();
               } else {
                  targetId = null;
                  targetName = args[1];

                  for(UUID id : team.getMembers()) {
                     if (team.getMemberName(id).equalsIgnoreCase(args[1])) {
                        targetId = id;
                        targetName = team.getMemberName(id);
                        break;
                     }
                  }
               }

               if (targetId != null && team.isMember(targetId)) {
                  if (team.isLeader(targetId)) {
                     this.msg(player, "errors.cannot-manage-owner", "&cYou cannot manage the team owner!");
                     return true;
                  } else {
                     this.plugin.getTeams().kick(team, targetId);
                     this.msg(player, "team.player-kicked", "&cPlayer %player% has been kicked from the team!", "%player%", targetName);
                     Player online = Bukkit.getPlayer(targetId);
                     if (online != null) {
                        this.msg(online, "team.you-were-kicked", "&cYou were kicked from the team!");
                     }

                     return true;
                  }
               } else {
                  this.msg(player, "errors.player-not-found", "&cPlayer not found!");
                  return true;
               }
            }
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean info(CommandSender sender, String[] args) {
      Team team;
      if (args.length >= 2) {
         team = this.plugin.getTeams().getByName(args[1]);
         if (team == null) {
            this.msg(sender, "errors.team-not-found", "&cThat team no longer exists!");
            return true;
         }
      } else {
         if (!(sender instanceof Player)) {
            this.msg(sender, "errors.usage-info", "&cUsage: /team info <team>");
            return true;
         }

         Player player = (Player)sender;
         team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         }
      }

      String leaderName = team.getMemberName(team.getLeader());
      String var10001 = team.getName();
      sender.sendMessage(ColorUtil.parse("&#34eb9b&l" + var10001));
      sender.sendMessage(ColorUtil.parse("&7Leader: &#34eb9b" + leaderName));
      int var9 = team.getMembers().size();
      sender.sendMessage(ColorUtil.parse("&7Members: &#34eb9b" + var9 + "&7 (&#34eb9b" + team.onlineCount() + " &7online)"));
      String var10 = team.isPvpEnabled() ? "&#34eb9bON" : "&#FF0000OFF";
      sender.sendMessage(ColorUtil.parse("&7PvP: " + var10));
      sender.sendMessage(ColorUtil.parse("&7Home: " + (team.getHome() != null ? "&#34eb9bSet" : "&cNot set")));
      StringBuilder members = new StringBuilder("&7List: ");

      for(int i = 0; i < team.getMembers().size(); ++i) {
         UUID id = (UUID)team.getMembers().get(i);
         if (i > 0) {
            members.append("&7, ");
         }

         members.append("&#34eb9b").append(team.getMemberName(id));
      }

      sender.sendMessage(ColorUtil.parse(members.toString()));
      return true;
   }

   private boolean home(CommandSender sender) {
      if (sender instanceof Player player) {
         return this.home(player);
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   public boolean home(final Player player) {
      if (!this.plugin.getConfig().getBoolean("team-home.enabled", true) && !player.hasPermission("economysmpcore.system.teams.admin") && !player.hasPermission("economysmpcore.system.teams.admin")) {
         this.msg(player, "errors.team-home-disabled", "&cTeam homes are currently disabled on this server!");
         return true;
      } else {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         } else if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.TEAM_HOME_ACCESS)) {
            this.msg(player, "errors.not-leader", "&cYou do not have permission to use team home!");
            return true;
         } else {
            final Location home = team.getHome();
            if (home != null && home.getWorld() != null) {
               if (this.teleporting.containsKey(player.getUniqueId())) {
                  this.msg(player, "team.already-teleporting", "&cYou are already teleporting!");
                  return true;
               } else {
                  final int delay = Math.max(0, this.plugin.getConfig().getInt("team-home.teleport-delay", 5));
                  final double maxDist = this.plugin.getConfig().getDouble("team-home.teleport-distance", 0.3);
                  final Location start = player.getLocation().clone();
                  if (delay > 0 && !player.hasPermission("economysmpcore.system.teams.admin") && !player.hasPermission("economysmpcore.system.teams.admin")) {
                     final int[] tid = new int[]{-1};
                     BukkitRunnable task = new BukkitRunnable() {
                        int left = delay;

                        public void run() {
                           if (!player.isOnline()) {
                              TeamCommand.this.teleporting.remove(player.getUniqueId());
                              Tasks.cancel(tid[0]);
                           } else if (player.getLocation().distanceSquared(start) > maxDist * maxDist) {
                              TeamCommand.this.msg(player, "team.teleport-cancelled", "&cTeleport cancelled!");
                              TeamCommand.this.play(player, "teleport-cancel");
                              TeamCommand.this.teleporting.remove(player.getUniqueId());
                              Tasks.cancel(tid[0]);
                           } else if (this.left <= 0) {
                              player.teleport(home);
                              TeamCommand.this.msg(player, "team.teleport-completed", "&aYou have been teleported to Team home!");
                              TeamCommand.this.play(player, "teleport-complete");
                              TeamCommand.this.teleporting.remove(player.getUniqueId());
                              Tasks.cancel(tid[0]);
                           } else {
                              TeamCommand.this.msg(player, "team.teleport-countdown", "&7Teleport in &#34eb9b%count% &7seconds", "%count%", String.valueOf(this.left));
                              TeamCommand.this.play(player, "teleport-cooldown");
                              --this.left;
                           }
                        }
                     };
                     this.teleporting.put(player.getUniqueId(), task);
                     tid[0] = Tasks.runTimer(this.plugin.getCore(), task, 0L, 20L);
                     return true;
                  } else {
                     player.teleport(home);
                     this.msg(player, "team.teleport-completed", "&aYou have been teleported to Team home!");
                     this.play(player, "teleport-complete");
                     return true;
                  }
               }
            } else {
               this.msg(player, "errors.no-home", "&cYour team has no home set!");
               return true;
            }
         }
      }
   }

   private boolean setHome(CommandSender sender) {
      if (sender instanceof Player player) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         } else if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.TEAM_HOME_SET)) {
            this.msg(player, "errors.not-leader", "&cOnly the team leader can do that!");
            return true;
         } else {
            team.setHome(player.getLocation().clone());
            team.save(this.plugin.getTeams().getTeamsFolder());
            this.msg(player, "team.home-set", "&aTeam home has been set!");
            this.play(player, "success-sound");
            return true;
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean chat(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         } else if (args.length < 2) {
            this.msg(player, "errors.usage-chat", "&cUsage: /team chat <message> or /team chat toggle");
            return true;
         } else if (args[1].equalsIgnoreCase("toggle")) {
            boolean now = !this.plugin.getTeams().isChatToggled(player.getUniqueId());
            this.plugin.getTeams().setChatToggled(player.getUniqueId(), now);
            this.msg(player, now ? "team.chat-enabled" : "team.chat-disabled", now ? "&aTeam chat enabled!" : "&cTeam chat disabled!");
            return true;
         } else {
            StringBuilder sb = new StringBuilder();

            for(int i = 1; i < args.length; ++i) {
               if (i > 1) {
                  sb.append(' ');
               }

               sb.append(args[i]);
            }

            this.sendTeamChat(player, team, sb.toString());
            return true;
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   public void sendTeamChat(Player player, Team team, String message) {
      String format = this.plugin.getMessages().get("team.chat-format", "&#00a3fb[TeamChat] &#00a3fb%player%&7: &#00a3fb%message%");
      String out = format.replace("%player%", player.getName()).replace("%message%", message).replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName());

      for(UUID id : team.getMembers()) {
         Player p = Bukkit.getPlayer(id);
         if (p != null) {
            p.sendMessage(ColorUtil.parse(out));
         }
      }

   }

   private boolean transfer(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 2) {
            this.msg(player, "errors.usage-transfer", "&cUsage: /team transfer <player>");
            return true;
         } else {
            Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
            if (team == null) {
               this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
               return true;
            } else if (!team.isLeader(player.getUniqueId())) {
               this.msg(player, "errors.not-leader", "&cOnly the team leader can do that!");
               return true;
            } else {
               Player target = Bukkit.getPlayerExact(args[1]);
               if (target != null && team.isMember(target.getUniqueId())) {
                  team.setLeader(target.getUniqueId());
                  team.save(this.plugin.getTeams().getTeamsFolder());
                  this.broadcast(team, "team.new-owner", "&7Team leader has been transferred to &#34eb9b%player%", (UUID)null, "%player%", target.getName());
                  return true;
               } else {
                  this.msg(player, "errors.player-not-found", "&cPlayer not found!");
                  return true;
               }
            }
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private boolean pvp(CommandSender sender) {
      if (sender instanceof Player player) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            this.msg(player, "errors.not-in-team", "&cYou are not in a team!");
            return true;
         } else if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.PVP_TOGGLE)) {
            this.msg(player, "errors.not-leader", "&cOnly the team leader can do that!");
            return true;
         } else {
            team.setPvpEnabled(!team.isPvpEnabled());
            team.save(this.plugin.getTeams().getTeamsFolder());
            this.msg(player, team.isPvpEnabled() ? "team.pvp-enabled" : "team.pvp-disabled", team.isPvpEnabled() ? "&aTeam-PVP has been enabled!" : "&cTeam-PVP has been disabled!");
            return true;
         }
      } else {
         this.msg(sender, "errors.player-only", "&cOnly players can use this!");
         return true;
      }
   }

   private void broadcast(Team team, String key, String def, UUID exclude, String... placeholders) {
      String message = this.plugin.getMessages().get(key, def);

      for(int i = 0; i + 1 < placeholders.length; i += 2) {
         message = message.replace(placeholders[i], placeholders[i + 1]);
      }

      for(UUID id : team.getMembers()) {
         if (exclude == null || !exclude.equals(id)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
               p.sendMessage(ColorUtil.parse(message));
            }
         }
      }

   }

   private void msg(CommandSender sender, String key, String def, String... placeholders) {
      String message = this.plugin.getMessages().get(key, def);

      for(int i = 0; i + 1 < placeholders.length; i += 2) {
         message = message.replace(placeholders[i], placeholders[i + 1]);
      }

      sender.sendMessage(ColorUtil.parse(message));
   }

   private void play(CommandSender sender, String key) {
      if (sender instanceof Player player) {
         try {
            String sound = this.plugin.getConfig().getString("sounds." + key, "");
            if (sound == null || sound.isBlank()) {
               return;
            }

            SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
         } catch (Exception var5) {
         }

      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1) {
         List<String> subs = List.of("create", "leave", "disband", "invite", "kick", "info", "reload", "help", "home", "sethome", "chat", "accept", "cancel", "transfer", "pvp");
         String p = args[0].toLowerCase(Locale.ROOT);

         for(String s : subs) {
            if (s.startsWith(p)) {
               out.add(s);
            }
         }

         return out;
      } else {
         if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            String p = args[1].toLowerCase(Locale.ROOT);
            if (sub.equals("invite") || sub.equals("kick") || sub.equals("transfer")) {
               return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList());
            }

            if (sub.equals("info") || sub.equals("accept") || sub.equals("join")) {
               return (List)this.plugin.getTeams().getTeams().stream().map(Team::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(p)).collect(Collectors.toList());
            }

            if (sub.equals("chat") && "toggle".startsWith(p)) {
               out.add("toggle");
            }
         }

         return out;
      }
   }
}
