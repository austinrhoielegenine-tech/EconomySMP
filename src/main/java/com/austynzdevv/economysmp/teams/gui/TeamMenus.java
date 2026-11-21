package com.austynzdevv.economysmp.teams.gui;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import com.austynzdevv.economysmp.teams.EconomyTeams;
import com.austynzdevv.economysmp.teams.Team;
import com.austynzdevv.economysmp.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class TeamMenus {
   private static final Map<String, FileConfiguration> GUI = new HashMap();

   private TeamMenus() {
   }

   public static void load(EconomyTeams plugin) {
      GUI.clear();
      File dir = new File(plugin.getDataFolder(), "gui");
      if (!dir.exists()) {
         dir.mkdirs();
      }

      for(String name : List.of("main.yml", "member.yml", "member.manager.yml")) {
         File out = new File(dir, name);
         String resource = plugin.getResource("gui/" + name) != null ? "gui/" + name : "teams/gui/" + name;
         if (!out.exists()) {
            try {
               InputStream in = plugin.getResource(resource);

               try {
                  if (in != null) {
                     Files.copy(in, out.toPath(), new CopyOption[0]);
                  }
               } catch (Throwable var11) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (in != null) {
                  in.close();
               }
            } catch (Exception var12) {
            }
         }

         if (out.exists()) {
            GUI.put(name.replace(".yml", ""), YamlConfiguration.loadConfiguration(out));
         } else {
            try {
               InputStream in = plugin.getResource(resource);

               try {
                  if (in != null) {
                     GUI.put(name.replace(".yml", ""), YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
                  }
               } catch (Throwable var13) {
                  if (in != null) {
                     try {
                        in.close();
                     } catch (Throwable var9) {
                        var13.addSuppressed(var9);
                     }
                  }

                  throw var13;
               }

               if (in != null) {
                  in.close();
               }
            } catch (Exception var14) {
            }
         }
      }

   }

   public static FileConfiguration getGui(String name) {
      return (FileConfiguration)GUI.get(name);
   }

   public static void openMain(Player player, int page, MenuSession.Sort sort) {
      Team team = EconomyTeams.get().getTeams().getByPlayer(player.getUniqueId());
      if (team == null) {
         player.sendMessage(ColorUtil.parse(EconomyTeams.get().getMessages().get("errors.not-in-team", "&cYou are not in a team!")));
      } else {
         FileConfiguration gui = (FileConfiguration)GUI.get("main");
         if (gui != null) {
            List<UUID> members = new ArrayList(team.getMembers());
            Comparator var10000;
            switch (sort) {
               case ONLINE:
                  var10000 = Comparator.<UUID>comparingInt((idx) -> Bukkit.getPlayer(idx) != null ? 0 : 1).thenComparing((idx) -> team.getMemberName(idx), String.CASE_INSENSITIVE_ORDER);
                  break;
               case JOIN_DATE:
                  Objects.requireNonNull(team);
                  var10000 = Comparator.comparingLong(team::getJoinDate);
                  break;
               case ALPHABETICALLY:
                  Objects.requireNonNull(team);
                  var10000 = Comparator.comparing(team::getMemberName, String.CASE_INSENSITIVE_ORDER);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            Comparator<UUID> cmp = var10000;
            members.sort(cmp);
            int pageSize = 45;
            int totalPages = Math.max(1, (int)Math.ceil((double)members.size() / (double)pageSize));
            if (page < 0) {
               page = 0;
            }

            if (page >= totalPages) {
               page = totalPages - 1;
            }

            ConfigurationSection root = gui.getConfigurationSection("gui");
            String title = (root != null ? root.getString("title", "&8ᴛᴇᴀᴍ (Page %current_page%)") : gui.getString("title", "&8ᴛᴇᴀᴍ (Page %current_page%)")).replace("%current_page%", String.valueOf(page + 1)).replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName());
            int rows = root != null ? root.getInt("rows", 6) : gui.getInt("rows", 6);
            Inventory inv = Bukkit.createInventory(player, Math.min(6, Math.max(1, rows)) * 9, ColorUtil.parse(title));
            ConfigurationSection items = gui.getConfigurationSection("items");
            int start = page * pageSize;
            int end = Math.min(start + pageSize, members.size());
            ConfigurationSection headSec = items != null ? items.getConfigurationSection("source-item") : null;

            for(int i = start; i < end; ++i) {
               UUID id = (UUID)members.get(i);
               inv.setItem(i - start, memberHead(headSec, team, id));
            }

            if (items != null) {
               place(inv, items.getConfigurationSection("search"), (String)null, team, (UUID)null);
               placeSort(inv, items.getConfigurationSection("sort"), sort);
               place(inv, items.getConfigurationSection("back"), (String)null, team, (UUID)null);
               place(inv, items.getConfigurationSection("team-info"), (String)null, team, (UUID)null);
               place(inv, items.getConfigurationSection("next"), (String)null, team, (UUID)null);
               place(inv, items.getConfigurationSection("team-home"), (String)null, team, (UUID)null);
               placePvp(inv, items.getConfigurationSection("pvp"), team);
            }

            MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MAIN);
            MenuSession.PAGE.put(player.getUniqueId(), page);
            MenuSession.SORT.put(player.getUniqueId(), sort);
            player.openInventory(inv);
         }
      }
   }

   public static void openMember(Player player, UUID target) {
      Team team = EconomyTeams.get().getTeams().getByPlayer(player.getUniqueId());
      if (team != null && team.isMember(target)) {
         if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.MANAGE_MEMBERS)) {
            player.sendMessage(ColorUtil.parse(EconomyTeams.get().getMessages().get("errors.not-leader", "&cOnly the team leader can do that!")));
         } else if (!team.isLeader(target) || team.isLeader(player.getUniqueId())) {
            FileConfiguration gui = (FileConfiguration)GUI.get("member.manager");
            if (gui == null) {
               gui = (FileConfiguration)GUI.get("member");
            }

            if (gui != null) {
               ConfigurationSection root = gui.getConfigurationSection("gui");
               String title = root != null ? root.getString("title", "&8ᴍᴇᴍʙᴇʀ ᴍᴀɴᴀɢᴇʀ") : gui.getString("title", "&8ᴍᴇᴍʙᴇʀ ᴍᴀɴᴀɢᴇʀ");
               int rows = root != null ? root.getInt("rows", 3) : gui.getInt("rows", 3);
               Inventory inv = Bukkit.createInventory(player, Math.min(6, Math.max(1, rows)) * 9, ColorUtil.parse(title));
               ConfigurationSection items = gui.getConfigurationSection("items");
               if (items != null) {
                  placePerm(inv, items.getConfigurationSection("pvp_toggle"), team, target, Team.Perm.PVP_TOGGLE);
                  placePerm(inv, items.getConfigurationSection("team_home_access"), team, target, Team.Perm.TEAM_HOME_ACCESS);
                  placePerm(inv, items.getConfigurationSection("team_home_set"), team, target, Team.Perm.TEAM_HOME_SET);
                  placePerm(inv, items.getConfigurationSection("create_delete_teams"), team, target, Team.Perm.TEAM_ADMIN);
                  placePerm(inv, items.getConfigurationSection("manage_members"), team, target, Team.Perm.MANAGE_MEMBERS);
               }

               MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.MEMBER);
               MenuSession.TARGET.put(player.getUniqueId(), target);
               player.openInventory(inv);
            }
         }
      }
   }

   private static void placePerm(Inventory inv, ConfigurationSection sec, Team team, UUID target, Team.Perm perm) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.STONE;

            try {
               mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
            } catch (Exception var15) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               boolean allowed = team.hasPerm(target, perm);
               String status = allowed ? sec.getString("enabled_status", "&#02de4fAllowed") : sec.getString("disabled_status", "&#e00202Denied");
               String title = sec.getString("title", sec.getString("display-name", "&fPerm"));
               meta.displayName(ColorUtil.parse(title));
               List<Component> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(ColorUtil.parse(line.replace("%status%", status).replace("%player%", team.getMemberName(target))));
               }

               meta.lore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private static ItemStack memberHead(ConfigurationSection sec, Team team, UUID id) {
      ItemStack item = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta)item.getItemMeta();
      if (meta != null) {
         meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
         String name = sec != null ? sec.getString("display-name", sec.getString("name", "&#34eb9b%player%")) : "&#34eb9b%player%";
         name = name.replace("%player%", team.getMemberName(id)).replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName());
         if (team.isLeader(id)) {
            name = name + " &7(Leader)";
         }

         meta.displayName(ColorUtil.parse(name));
         List<String> loreCfg = sec != null ? sec.getStringList("lore") : List.of("&7Click to manage");
         List<Component> lore = new ArrayList();

         for(String line : loreCfg) {
            lore.add(ColorUtil.parse(line.replace("%player%", team.getMemberName(id)).replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName())));
         }

         meta.lore(lore);
         item.setItemMeta(meta);
      }

      return item;
   }

   private static void place(Inventory inv, ConfigurationSection sec, String playerName, Team team, UUID target) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.STONE;

            try {
               mat = Material.valueOf(sec.getString("material", "STONE").toUpperCase());
            } catch (Exception var14) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               String name = sec.getString("display-name", sec.getString("name", sec.getString("title", mat.name())));
               name = name.replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName());
               if (playerName != null) {
                  name = name.replace("%player%", playerName);
               }

               meta.displayName(ColorUtil.parse(name));
               List<Component> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  String l = line.replace("%economysmpcore_teams_name%", team.getName()).replace("%economyteams_name%", team.getName());
                  if (playerName != null) {
                     l = l.replace("%player%", playerName);
                  }

                  lore.add(ColorUtil.parse(l));
               }

               if (!lore.isEmpty()) {
                  meta.lore(lore);
               }

               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private static void placePvp(Inventory inv, ConfigurationSection sec, Team team) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.DIAMOND_SWORD;

            try {
               mat = Material.valueOf(sec.getString("material", "DIAMOND_SWORD").toUpperCase());
            } catch (Exception var12) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               String status = team.isPvpEnabled() ? sec.getString("status.enabled", "&#34eb9b&lON") : sec.getString("status.disabled", "&#FF0000&lOFF");
               String name = sec.getString("display-name", "&#34eb9bᴘᴠᴘ");
               meta.displayName(ColorUtil.parse(name));
               List<Component> lore = new ArrayList();

               for(String line : sec.getStringList("lore")) {
                  lore.add(ColorUtil.parse(line.replace("%pvp-status%", status)));
               }

               meta.lore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private static void placeSort(Inventory inv, ConfigurationSection sec, MenuSession.Sort sort) {
      if (sec != null) {
         int slot = sec.getInt("slot", -1);
         if (slot >= 0 && slot < inv.getSize()) {
            Material mat = Material.HOPPER;

            try {
               mat = Material.valueOf(sec.getString("material", "HOPPER").toUpperCase());
            } catch (Exception var14) {
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               meta.displayName(ColorUtil.parse(sec.getString("name", "&#34eb9bѕᴏʀᴛ")));
               String active = sec.getString("selected-prefix", "&#34eb9b• ");
               String inactive = sec.getString("unselected-prefix", "&7• ");
               List<Component> lore = new ArrayList();
               ConfigurationSection options = sec.getConfigurationSection("options");
               if (options != null) {
                  for(String key : options.getKeys(false)) {
                     boolean on = matches(key, sort);
                     lore.add(ColorUtil.parse((on ? active : inactive) + options.getString(key)));
                  }
               }

               meta.lore(lore);
               item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
         }
      }
   }

   private static boolean matches(String key, MenuSession.Sort sort) {
      boolean var10000;
      switch (key.toUpperCase()) {
         case "ONLINE_MEMBERS":
         case "ONLINE":
            var10000 = sort == MenuSession.Sort.ONLINE;
            break;
         case "ALPHABETICALLY":
            var10000 = sort == MenuSession.Sort.ALPHABETICALLY;
            break;
         case "JOIN_DATE":
            var10000 = sort == MenuSession.Sort.JOIN_DATE;
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }
}
