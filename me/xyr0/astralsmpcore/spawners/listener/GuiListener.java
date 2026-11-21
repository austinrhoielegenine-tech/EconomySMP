package me.xyr0.astralsmpcore.spawners.listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.xyr0.astralsmpcore.spawners.AstralSpawners;
import me.xyr0.astralsmpcore.spawners.EntityDefinition;
import me.xyr0.astralsmpcore.spawners.SpawnerData;
import me.xyr0.astralsmpcore.spawners.gui.ConfirmSellGui;
import me.xyr0.astralsmpcore.spawners.gui.SpawnerGui;
import me.xyr0.astralsmpcore.spawners.gui.SpawnerPanelGui;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GuiListener implements Listener {
   private final AstralSpawners plugin;

   public GuiListener(AstralSpawners plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = false
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         UUID var15 = player.getUniqueId();
         if (ConfirmSellGui.OPEN.containsKey(var15)) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null) {
               if (event.getClickedInventory() == event.getView().getTopInventory()) {
                  if (event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.SWAP_OFFHAND && event.getClick() != ClickType.DROP && event.getClick() != ClickType.CONTROL_DROP) {
                     this.handleConfirm(player, (SpawnerData)ConfirmSellGui.OPEN.get(var15), event.getRawSlot());
                  }
               }
            }
         } else if (SpawnerPanelGui.isOpen(var15)) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               if (event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.SWAP_OFFHAND) {
                  this.handlePanel(player, event.getRawSlot(), event.isRightClick());
               }
            }
         } else {
            SpawnerData data = (SpawnerData)SpawnerGui.OPEN.get(var15);
            if (data != null) {
               event.setCancelled(true);
               if (event.getClickedInventory() != null) {
                  if (event.getClick() != ClickType.NUMBER_KEY && event.getClick() != ClickType.SWAP_OFFHAND) {
                     if (event.getClickedInventory() == event.getView().getTopInventory()) {
                        int slot = event.getRawSlot();
                        if (slot >= 0 && slot < 54) {
                           int page = SpawnerGui.getPage(player);
                           if (slot == 45) {
                              if (page > 0) {
                                 SpawnerGui.setPage(player, data, page - 1);
                              }

                           } else if (slot == 53) {
                              if (page < SpawnerGui.maxPage(data)) {
                                 SpawnerGui.setPage(player, data, page + 1);
                              }

                           } else if (slot == 48 && this.plugin.getSettings().economyEnabled()) {
                              ConfirmSellGui.open(player, data);
                           } else if (slot == 50) {
                              this.dropPage(player, data, page);
                           } else if (slot != 49) {
                              if (slot < 45) {
                                 ItemStack clicked = event.getCurrentItem();
                                 if (clicked == null || clicked.getType() == Material.AIR) {
                                    return;
                                 }

                                 Material mat = clicked.getType();
                                 int inStorage = (Integer)data.getStorage().getOrDefault(mat, 0);
                                 if (inStorage <= 0) {
                                    return;
                                 }

                                 int take = Math.min(Math.min(inStorage, clicked.getAmount()), 64);
                                 ItemStack give = new ItemStack(mat, take);
                                 Map<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack[]{give});
                                 int given = take - leftover.values().stream().mapToInt(ItemStack::getAmount).sum();
                                 if (given > 0) {
                                    int remaining = inStorage - given;
                                    if (remaining <= 0) {
                                       data.getStorage().remove(mat);
                                    } else {
                                       data.getStorage().put(mat, remaining);
                                    }

                                    data.setDirty(true);
                                    this.plugin.getMessages().send(player, "collected-items", Map.of("count", String.valueOf(given)));
                                    SpawnerGui.refresh(player, data);
                                 }
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = false
   )
   public void onDrag(InventoryDragEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         UUID var6 = player.getUniqueId();
         if (SpawnerGui.OPEN.containsKey(var6) || ConfirmSellGui.OPEN.containsKey(var6) || SpawnerPanelGui.isOpen(var6)) {
            for(int slot : event.getRawSlots()) {
               if (slot < 54) {
                  event.setCancelled(true);
                  return;
               }
            }

         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         UUID var4 = player.getUniqueId();
         if (!SpawnerGui.SWITCHING.contains(var4)) {
            SpawnerGui.OPEN.remove(var4);
            SpawnerGui.PAGE.remove(var4);
            ConfirmSellGui.OPEN.remove(var4);
            SpawnerPanelGui.clearSession(var4);
         }
      }
   }

   private void handlePanel(Player player, int slot, boolean rightClick) {
      UUID id = player.getUniqueId();
      SpawnerPanelGui.View view = (SpawnerPanelGui.View)SpawnerPanelGui.VIEW.get(id);
      if (view == SpawnerPanelGui.View.MAIN) {
         if (slot == 11) {
            SpawnerPanelGui.openDimension(player, SpawnerPanelGui.Dimension.OVERWORLD, 0);
         } else if (slot == 13) {
            SpawnerPanelGui.openDimension(player, SpawnerPanelGui.Dimension.NETHER, 0);
         } else if (slot == 15) {
            SpawnerPanelGui.openDimension(player, SpawnerPanelGui.Dimension.END, 0);
         }

      } else if (view == SpawnerPanelGui.View.DIMENSION) {
         SpawnerPanelGui.Dimension dim = (SpawnerPanelGui.Dimension)SpawnerPanelGui.DIM.get(id);
         int page = (Integer)SpawnerPanelGui.PAGE.getOrDefault(id, 0);
         List<SpawnerData> all = (List)SpawnerPanelGui.LIST.getOrDefault(id, List.of());
         if (slot == 49) {
            SpawnerPanelGui.openMain(player);
         } else if (slot == 45) {
            SpawnerPanelGui.openDimension(player, dim, page - 1);
         } else if (slot == 53) {
            SpawnerPanelGui.openDimension(player, dim, page + 1);
         } else {
            if (slot >= 0 && slot < 45) {
               int index = page * 45 + slot;
               if (index < 0 || index >= all.size()) {
                  return;
               }

               SpawnerData data = (SpawnerData)all.get(index);
               if (rightClick) {
                  Location loc = data.getLocation();
                  if (loc != null && loc.getWorld() != null) {
                     player.teleport(loc.clone().add((double)0.5F, (double)1.0F, (double)0.5F));
                     int var10001 = data.getX();
                     player.sendMessage("§7Teleported to spawner at §f" + var10001 + ", " + data.getY() + ", " + data.getZ());
                  }
               } else {
                  this.plugin.getSpawnerManager().remove(data);
                  Location loc = data.getLocation();
                  if (loc != null && loc.getBlock().getType() == Material.SPAWNER) {
                     loc.getBlock().setType(Material.AIR);
                  }

                  player.sendMessage("§7Removed Spawner Success.");
                  SpawnerPanelGui.openDimension(player, dim, page);
               }
            }

         }
      }
   }

   private void handleConfirm(Player player, SpawnerData data, int slot) {
      if (slot == 11) {
         ConfirmSellGui.OPEN.remove(player.getUniqueId());
         SpawnerGui.SWITCHING.add(player.getUniqueId());
         SpawnerGui.open(player, data);
         Tasks.run(this.plugin, () -> SpawnerGui.SWITCHING.remove(player.getUniqueId()));
      } else {
         if (slot == 15) {
            ConfirmSellGui.OPEN.remove(player.getUniqueId());
            this.sellAll(player, data);
            SpawnerGui.SWITCHING.add(player.getUniqueId());
            SpawnerGui.open(player, data);
            Tasks.run(this.plugin, () -> SpawnerGui.SWITCHING.remove(player.getUniqueId()));
         }

      }
   }

   private void dropPage(Player player, SpawnerData data, int page) {
      Map<Material, Integer> pageMap = SpawnerGui.pageContents(data, page);
      if (pageMap.isEmpty()) {
         this.plugin.getMessages().send(player, "no-items");
      } else {
         int count = 0;

         for(Map.Entry<Material, Integer> e : pageMap.entrySet()) {
            int remove = (Integer)e.getValue();
            int stored = (Integer)data.getStorage().getOrDefault(e.getKey(), 0);
            int actual = Math.min(remove, stored);

            int give;
            for(int left = actual; left > 0; count += give) {
               give = Math.min(left, 64);
               this.dropFromPlayer(player, new ItemStack((Material)e.getKey(), give));
               left -= give;
            }

            give = stored - actual;
            if (give <= 0) {
               data.getStorage().remove(e.getKey());
            } else {
               data.getStorage().put((Material)e.getKey(), give);
            }
         }

         data.setDirty(true);
         this.plugin.getMessages().send(player, "collected-items", Map.of("count", String.valueOf(count)));
         SpawnerGui.refresh(player, data);
      }
   }

   private void dropFromPlayer(Player player, ItemStack stack) {
      Vector dir = player.getLocation().getDirection().normalize().multiply(0.35);
      Location eye = player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.4));
      Item dropped = player.getWorld().dropItem(eye, stack);
      dropped.setVelocity(dir);
      dropped.setPickupDelay(40);
   }

   private void sellAll(Player player, SpawnerData data) {
      EntityDefinition def = this.plugin.getEntities().get(data.getEntityType());
      if (def != null && !data.getStorage().isEmpty()) {
         double total = (double)0.0F;

         for(Map.Entry<Material, Integer> e : data.getStorage().entrySet()) {
            double price = def.getPrice((Material)e.getKey());
            if (price > (double)0.0F) {
               total += price * (double)(Integer)e.getValue();
            }
         }

         if (total <= (double)0.0F) {
            this.plugin.getMessages().send(player, "no-items");
         } else if (this.plugin.getEconomy() != null && this.plugin.getEconomy().isReady()) {
            if (!this.plugin.getEconomy().deposit(player, total)) {
               player.sendMessage("§cCould not deposit money. Check your economy plugin.");
            } else {
               data.getStorage().clear();
               data.setDirty(true);
               String symbol = this.plugin.getSettings().getCurrencySymbol();
               this.plugin.getMessages().send(player, "sold-items", Map.of("price", symbol + String.format("%.2f", total)));
            }
         } else {
            this.plugin.getMessages().send(player, "no-items");
            player.sendMessage("§cEconomy is not available. Install Vault + EssentialsX.");
         }
      } else {
         this.plugin.getMessages().send(player, "no-items");
      }
   }
}
