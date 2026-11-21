package me.xyr0.astralsmpcore.homes.listener;

import java.util.ArrayList;
import java.util.List;
import me.xyr0.astralsmpcore.homes.AstralHomes;
import me.xyr0.astralsmpcore.homes.gui.MainGUI;
import me.xyr0.astralsmpcore.teams.AstralTeams;
import me.xyr0.astralsmpcore.teams.Team;
import me.xyr0.astralsmpcore.teams.command.TeamCommand;
import me.xyr0.astralsmpcore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GUIListener implements Listener {
   private final AstralHomes plugin;

   public GUIListener(AstralHomes plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         InventoryHolder var4 = event.getInventory().getHolder();
         if (var4 instanceof MainGUI.Holder holder) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType() != Material.AIR) {
               int slot = event.getRawSlot();
               if (slot >= 0 && slot < event.getInventory().getSize()) {
                  if (holder.confirm) {
                     this.confirm(player, holder, slot);
                  } else {
                     this.main(player, holder, slot, clicked, event.getClick());
                  }
               }
            }
         }
      }
   }

   private void main(Player player, MainGUI.Holder holder, int slot, ItemStack clicked, ClickType click) {
      if (slot == 10) {
         this.handleTeamHomeBanner(player);
      } else if (slot == 19) {
         this.handleTeamHomeDye(player);
      } else {
         List<Integer> beds = this.slots(this.plugin.configs().getMainGui().getString("layout.beds", ""));
         List<Integer> dyes = this.slots(this.plugin.configs().getMainGui().getString("layout.dyes", ""));
         int index = -1;
         boolean bed = false;
         boolean dye = false;

         for(int i = 0; i < beds.size(); ++i) {
            if ((Integer)beds.get(i) == slot) {
               index = i;
               bed = true;
               break;
            }
         }

         if (index < 0) {
            for(int i = 0; i < dyes.size(); ++i) {
               if ((Integer)dyes.get(i) == slot) {
                  index = i;
                  dye = true;
                  break;
               }
            }
         }

         if (index >= 0) {
            String id = String.valueOf(index + 1);
            Player target = Bukkit.getPlayer(holder.target);
            if (target == null) {
               if (!holder.target.equals(player.getUniqueId())) {
                  player.closeInventory();
                  return;
               }

               target = player;
            }

            boolean self = target.getUniqueId().equals(player.getUniqueId());
            if (!self && !player.hasPermission("astralsmpcore.system.homes.others")) {
               player.sendMessage(ColorUtil.color(this.plugin.configs().msg("denied")));
            } else if (self && !this.plugin.homes().canUseSlot(player, index + 1)) {
               player.sendMessage(ColorUtil.color(this.plugin.configs().msg("limit").replace("%max_homes%", String.valueOf(this.plugin.homes().maxHomes(player)))));
               this.plugin.homes().sound(player, "denied");
            } else {
               String type = clicked.getType().name();
               if (!type.contains("RED_BED") && !type.contains("RED_DYE") && (!type.contains("GRAY_BED") || this.plugin.homes().canUseSlot(player, index + 1))) {
                  if (bed) {
                     if (type.contains("LIGHT_BLUE") || type.contains("BED") && !type.contains("GRAY") && !type.contains("RED")) {
                        if (self) {
                           player.closeInventory();
                           this.plugin.homes().teleport(player, id);
                        }
                     } else if (type.contains("GRAY") && self) {
                        this.plugin.homes().set(player, id);
                        this.plugin.mainGui().open(player);
                     }
                  } else if (dye) {
                     if (type.contains("LIGHT_BLUE")) {
                        if (self) {
                           this.plugin.confirmGui().open(player, id);
                        }
                     } else if (type.contains("GRAY") && self) {
                        this.plugin.homes().set(player, id);
                        this.plugin.mainGui().open(player);
                     }
                  }

               } else {
                  this.plugin.homes().sound(player, "denied");
               }
            }
         }
      }
   }

   private void confirm(Player player, MainGUI.Holder holder, int slot) {
      int cancel = this.plugin.configs().getConfirmGui().getInt("slots.no.position", 11);
      int ok = this.plugin.configs().getConfirmGui().getInt("slots.yes.position", 15);
      if (slot == cancel) {
         player.closeInventory();
         this.plugin.mainGui().open(player);
         this.plugin.homes().sound(player, "interact");
      } else if (slot == ok && holder.homeId != null) {
         if (!holder.teamHome) {
            this.plugin.homes().delete(player, holder.homeId);
         } else {
            try {
               AstralTeams teams = AstralTeams.get();
               Team team = teams != null && teams.getTeams() != null ? teams.getTeams().getByPlayer(player.getUniqueId()) : null;
               if (team != null && team.getHome() != null) {
                  team.setHome((Location)null);

                  try {
                     teams.getTeams().saveAll();
                  } catch (Exception var9) {
                  }

                  player.sendMessage(ColorUtil.color("&7Team home &#FF0000deleted&7."));
               }
            } catch (Exception var10) {
            }
         }

         player.closeInventory();
         this.plugin.mainGui().open(player);
      }

   }

   private List<Integer> slots(String raw) {
      List<Integer> out = new ArrayList();
      if (raw != null && !raw.isEmpty()) {
         for(String part : raw.split(",")) {
            try {
               out.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException var8) {
            }
         }

         return out;
      } else {
         return out;
      }
   }

   private void handleTeamHomeBanner(Player player) {
      try {
         AstralTeams teams = AstralTeams.get();
         if (teams == null || teams.getTeams() == null) {
            player.sendMessage(ColorUtil.color("&cTeams system unavailable."));
            return;
         }

         Team team = teams.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            player.sendMessage(ColorUtil.color("&cYou are not in a team."));
            return;
         }

         if (team.getHome() != null) {
            player.closeInventory();
            TeamCommand command = teams.getCommand();
            if (command != null) {
               command.home(player);
            }

            return;
         }

         team.setHome(player.getLocation());

         try {
            teams.getTeams().saveAll();
         } catch (Exception var5) {
         }

         player.sendMessage(ColorUtil.color("&7Team home &#00FF00set&7."));
         player.closeInventory();
         this.plugin.mainGui().open(player);
      } catch (Exception var6) {
         player.sendMessage(ColorUtil.color("&cCould not update team home."));
      }

   }

   private void handleTeamHomeDye(Player player) {
      try {
         AstralTeams teams = AstralTeams.get();
         if (teams == null || teams.getTeams() == null) {
            player.sendMessage(ColorUtil.color("&cTeams system unavailable."));
            return;
         }

         Team team = teams.getTeams().getByPlayer(player.getUniqueId());
         if (team == null) {
            player.sendMessage(ColorUtil.color("&cYou are not in a team."));
            return;
         }

         if (team.getHome() == null) {
            team.setHome(player.getLocation());

            try {
               teams.getTeams().saveAll();
            } catch (Exception var5) {
            }

            player.sendMessage(ColorUtil.color("&7Team home &#00FF00set&7."));
            this.plugin.mainGui().open(player);
            return;
         }

         this.plugin.confirmGui().openTeam(player);
      } catch (Exception var6) {
         player.sendMessage(ColorUtil.color("&cCould not update team home."));
      }

   }
}
