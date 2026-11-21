package me.xyr0.economysmpcore.crates.listener;

import me.xyr0.economysmpcore.crates.AstralCrates;
import me.xyr0.economysmpcore.crates.Crate;
import me.xyr0.economysmpcore.crates.OpenManager;
import me.xyr0.economysmpcore.crates.Reward;
import me.xyr0.economysmpcore.crates.gui.MenuSession;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public final class GuiListener implements Listener {
   private final AstralCrates plugin;
   private final OpenManager openManager;

   public GuiListener(AstralCrates plugin) {
      this.plugin = plugin;
      this.openManager = new OpenManager(plugin);
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = false
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
            event.setCancelled(true);
            event.setResult(Result.DENY);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               MenuSession.Mode mode = (MenuSession.Mode)MenuSession.MODE.get(player.getUniqueId());
               String crateId = (String)MenuSession.CRATE.get(player.getUniqueId());
               Crate crate = this.plugin.getCrates().get(crateId);
               if (crate != null && mode != null) {
                  if (mode != MenuSession.Mode.ROLLING && mode != MenuSession.Mode.VIEW) {
                     if (mode != MenuSession.Mode.PREVIEW) {
                        if (mode == MenuSession.Mode.CONFIRM) {
                           int slot = event.getRawSlot();
                           if (slot == 11) {
                              MenuSession.clear(player.getUniqueId());
                              player.closeInventory();
                              this.plugin.getMessages().send(player, "open-cancelled", "&cCrate opening cancelled.");
                              return;
                           }

                           if (slot == 15) {
                              Reward reward = (Reward)MenuSession.PENDING.get(player.getUniqueId());
                              MenuSession.clear(player.getUniqueId());
                              player.closeInventory();
                              if (reward == null) {
                                 return;
                              }

                              if (!this.plugin.getKeys().consumeKey(player, crate.getId())) {
                                 this.plugin.getMessages().send(player, "no-key", "&cYou don't have a key for this crate!");
                                 return;
                              }

                              this.openManager.giveReward(player, crate, reward);
                           }
                        }

                     } else {
                        Reward reward = crate.getRewardBySlot(event.getRawSlot());
                        if (reward == null) {
                           ItemStack clicked = event.getCurrentItem();
                           if (clicked != null && !clicked.getType().isAir()) {
                              for(Reward r : crate.getRewards()) {
                                 if (r.getIcon() != null && r.getIcon().getType() == clicked.getType()) {
                                    reward = r;
                                    break;
                                 }
                              }
                           }
                        }

                        if (reward != null) {
                           MenuSession.MODE.put(player.getUniqueId(), MenuSession.Mode.CONFIRM);
                           MenuSession.PENDING.put(player.getUniqueId(), reward);
                           player.closeInventory();
                           Reward finalReward = reward;
                           Tasks.run(this.plugin.getCore(), () -> this.openManager.openConfirm(player, crate, finalReward));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
            MenuSession.Mode mode = (MenuSession.Mode)MenuSession.MODE.get(player.getUniqueId());
            if (mode != MenuSession.Mode.ROLLING) {
               Tasks.runLater(this.plugin.getCore(), () -> {
                  if (!player.isOnline()) {
                     MenuSession.clear(player.getUniqueId());
                  } else if (!Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId())) || player.getOpenInventory() == null || player.getOpenInventory().getTopInventory().getSize() < 9) {
                     if (MenuSession.MODE.get(player.getUniqueId()) != MenuSession.Mode.CONFIRM || !MenuSession.PENDING.containsKey(player.getUniqueId()) || Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
                        if (player.getOpenInventory() == null || player.getOpenInventory().getTopInventory().getSize() < 9) {
                           MenuSession.clear(player.getUniqueId());
                        }

                     }
                  }
               }, 2L);
            }
         }
      }
   }
}
