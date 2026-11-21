package com.austynzdevv.novacore.teams.listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import com.austynzdevv.novacore.teams.EconomyTeams;
import com.austynzdevv.novacore.teams.Team;
import com.austynzdevv.novacore.teams.gui.MenuSession;
import com.austynzdevv.novacore.teams.gui.TeamMenus;
import com.austynzdevv.novacore.teams.util.InputUtil;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GuiListener implements Listener {
   private final EconomyTeams plugin;
   private final Map<UUID, Boolean> searching = new HashMap();

   public GuiListener(EconomyTeams plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(player.getUniqueId());
         if (view != null) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               this.play(player, "click-sound");
               int slot = event.getRawSlot();
               if (view == MenuSession.View.MAIN) {
                  this.handleMain(player, slot);
               } else if (view == MenuSession.View.MEMBER) {
                  this.handleMember(player, slot);
               }

            }
         }
      }
   }

   private void handleMain(Player player, int slot) {
      Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
      if (team == null) {
         player.closeInventory();
      } else {
         FileConfiguration gui = TeamMenus.getGui("main");
         ConfigurationSection items = gui.getConfigurationSection("items");
         if (items != null) {
            int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
            MenuSession.Sort sort = (MenuSession.Sort)MenuSession.SORT.getOrDefault(player.getUniqueId(), MenuSession.Sort.ONLINE);
            if (this.isSlot(items, "back", slot)) {
               TeamMenus.openMain(player, Math.max(0, page - 1), sort);
            } else if (this.isSlot(items, "next", slot)) {
               TeamMenus.openMain(player, page + 1, sort);
            } else if (this.isSlot(items, "team-info", slot)) {
               TeamMenus.openMain(player, page, sort);
            } else if (this.isSlot(items, "sort", slot)) {
               TeamMenus.openMain(player, 0, MenuSession.next(sort));
            } else if (this.isSlot(items, "search", slot)) {
               this.searching.put(player.getUniqueId(), true);
               InputUtil.requestSearch(player);
            } else if (this.isSlot(items, "team-home", slot)) {
               player.closeInventory();
               player.performCommand("team home");
            } else if (this.isSlot(items, "pvp", slot)) {
               if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.PVP_TOGGLE)) {
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("errors.not-leader", "&cOnly the team leader can do that!")));
               } else {
                  team.setPvpEnabled(!team.isPvpEnabled());
                  team.save(this.plugin.getTeams().getTeamsFolder());
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get(team.isPvpEnabled() ? "team.pvp-enabled" : "team.pvp-disabled", team.isPvpEnabled() ? "&aTeam-PVP has been enabled!" : "&cTeam-PVP has been disabled!")));
                  TeamMenus.openMain(player, page, sort);
               }
            } else {
               if (slot >= 0 && slot < 45 && (team.isLeader(player.getUniqueId()) || team.hasPerm(player.getUniqueId(), Team.Perm.MANAGE_MEMBERS))) {
                  List<UUID> members = new ArrayList(team.getMembers());
                  Objects.requireNonNull(team);
                  members.sort(Comparator.comparing(team::getMemberName, String.CASE_INSENSITIVE_ORDER));
                  int index = page * 45 + slot;
                  if (index >= 0 && index < members.size()) {
                     UUID target = (UUID)members.get(index);
                     if (!team.isLeader(target)) {
                        TeamMenus.openMember(player, target);
                     }
                  }
               }

            }
         }
      }
   }

   private void handleMember(Player player, int slot) {
      Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
      UUID target = (UUID)MenuSession.TARGET.get(player.getUniqueId());
      if (team != null && target != null) {
         if (!team.isLeader(player.getUniqueId()) && !team.hasPerm(player.getUniqueId(), Team.Perm.MANAGE_MEMBERS)) {
            player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("errors.not-leader", "&cOnly the team leader can do that!")));
         } else if (!team.isLeader(target) || team.isLeader(player.getUniqueId())) {
            FileConfiguration gui = TeamMenus.getGui("member.manager");
            if (gui == null) {
               gui = TeamMenus.getGui("member");
            }

            ConfigurationSection items = gui.getConfigurationSection("items");
            if (items != null) {
               Team.Perm perm = null;
               if (this.isSlot(items, "pvp_toggle", slot)) {
                  perm = Team.Perm.PVP_TOGGLE;
               } else if (this.isSlot(items, "team_home_access", slot)) {
                  perm = Team.Perm.TEAM_HOME_ACCESS;
               } else if (this.isSlot(items, "team_home_set", slot)) {
                  perm = Team.Perm.TEAM_HOME_SET;
               } else if (this.isSlot(items, "create_delete_teams", slot)) {
                  perm = Team.Perm.TEAM_ADMIN;
               } else if (this.isSlot(items, "manage_members", slot)) {
                  perm = Team.Perm.MANAGE_MEMBERS;
               }

               if (perm != null) {
                  boolean now = team.togglePerm(target, perm);
                  team.save(this.plugin.getTeams().getTeamsFolder());
                  String status = now ? "&#02de4fAllowed" : "&#e00202Denied";
                  player.sendMessage(ColorUtil.parse("&7Permission updated: " + status));
                  TeamMenus.openMember(player, target);
               } else {
                  if (this.isSlot(items, "back", slot)) {
                     TeamMenus.openMain(player, (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0), (MenuSession.Sort)MenuSession.SORT.getOrDefault(player.getUniqueId(), MenuSession.Sort.ONLINE));
                  }

               }
            }
         }
      } else {
         player.closeInventory();
      }
   }

   private boolean isSlot(ConfigurationSection items, String key, int slot) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      return sec != null && sec.getInt("slot", -1) == slot;
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (Boolean.TRUE.equals(this.searching.get(player.getUniqueId()))) {
         if (!InputUtil.isSignInput(player.getUniqueId())) {
            event.setCancelled(true);
            this.searching.remove(player.getUniqueId());
            String msg = event.getMessage().trim();
            Tasks.run(this.plugin.getCore(), () -> this.handleSearchResult(player, msg));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onSign(SignChangeEvent event) {
      Player player = event.getPlayer();
      if (InputUtil.isSignInput(player.getUniqueId()) || Boolean.TRUE.equals(this.searching.get(player.getUniqueId()))) {
         event.setCancelled(true);
         String line = "";

         try {
            Component component = event.line(0);
            if (component != null) {
               line = PlainTextComponentSerializer.plainText().serialize(component);
            }
         } catch (Exception var6) {
            String legacy = event.getLine(0);
            if (legacy != null) {
               line = legacy;
            }
         }

         String finalLine = line.trim();
         this.searching.remove(player.getUniqueId());
         Tasks.run(this.plugin.getCore(), () -> {
            InputUtil.cleanup(player);
            this.handleSearchResult(player, finalLine);
         });
      }
   }

   private void handleSearchResult(Player player, String msg) {
      if (msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("cancel")) {
         Team team = this.plugin.getTeams().getByPlayer(player.getUniqueId());
         if (team != null) {
            UUID found = null;

            for(UUID id : team.getMembers()) {
               if (team.getMemberName(id).toLowerCase().contains(msg.toLowerCase())) {
                  found = id;
                  break;
               }
            }

            if (found == null) {
               player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("errors.player-not-found", "&cPlayer not found!")));
               TeamMenus.openMain(player, 0, MenuSession.Sort.ONLINE);
            } else {
               boolean canManage = team.isLeader(player.getUniqueId()) || team.hasPerm(player.getUniqueId(), Team.Perm.MANAGE_MEMBERS);
               if (canManage && !team.isLeader(found)) {
                  TeamMenus.openMember(player, found);
               } else {
                  TeamMenus.openMain(player, 0, MenuSession.Sort.ONLINE);
               }

            }
         }
      } else {
         player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("team.search-cancelled", "&cTeam search cancelled!")));
         TeamMenus.openMain(player, 0, MenuSession.Sort.ONLINE);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      InputUtil.cleanup(event.getPlayer());
      this.searching.remove(event.getPlayer().getUniqueId());
      MenuSession.clear(event.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (MenuSession.VIEW.get(player.getUniqueId()) != null) {
            if (!Boolean.TRUE.equals(this.searching.get(player.getUniqueId())) && !InputUtil.isSignInput(player.getUniqueId())) {
               Tasks.run(this.plugin.getCore(), () -> {
                  if (!player.isOnline()) {
                     MenuSession.clear(player.getUniqueId());
                  } else if (player.getOpenInventory().getTopInventory().getSize() <= 5 || !MenuSession.VIEW.containsKey(player.getUniqueId())) {
                     if (!Boolean.TRUE.equals(this.searching.get(player.getUniqueId())) && !InputUtil.isSignInput(player.getUniqueId())) {
                        MenuSession.clear(player.getUniqueId());
                     }
                  }
               });
            }
         }
      }
   }

   private void play(Player player, String key) {
      try {
         String sound = this.plugin.getConfig().getString("sounds." + key, "UI_BUTTON_CLICK");
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }
}
