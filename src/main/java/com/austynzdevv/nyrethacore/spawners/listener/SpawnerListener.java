package com.austynzdevv.nyrethacore.spawners.listener;

import java.util.Iterator;
import java.util.Map;
import com.austynzdevv.nyrethacore.spawners.EconomySpawners;
import com.austynzdevv.nyrethacore.spawners.SpawnerData;
import com.austynzdevv.nyrethacore.spawners.gui.SpawnerGui;
import com.austynzdevv.nyrethacore.spawners.util.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class SpawnerListener implements Listener {
   private final EconomySpawners plugin;

   public SpawnerListener(EconomySpawners plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent event) {
      ItemStack item = event.getItemInHand();
      if (ItemBuilder.isEconomySpawner(item)) {
         EntityType type = ItemBuilder.getType(item);
         int perItem = ItemBuilder.getStack(item);
         if (type != null && this.plugin.getEntities().isSupported(type)) {
            Player player = event.getPlayer();
            Block block = event.getBlockPlaced();
            boolean shift = player.isSneaking();
            int handAmount = Math.max(1, item.getAmount());
            int totalUnits = shift ? perItem * handAmount : perItem;
            if (this.plugin.getSettings().mergeOnPlace()) {
               for(Block relative : new Block[]{block.getRelative(1, 0, 0), block.getRelative(-1, 0, 0), block.getRelative(0, 0, 1), block.getRelative(0, 0, -1)}) {
                  SpawnerData nearby = this.plugin.getSpawnerManager().getAt(relative.getLocation());
                  if (nearby != null && nearby.getEntityType() == type) {
                     int added = this.plugin.getSpawnerManager().tryStackPartial(nearby, totalUnits);
                     if (added > 0) {
                        event.setCancelled(true);
                        if (player.getGameMode() != GameMode.CREATIVE) {
                           this.consumeSpawnerUnits(item, added, perItem, player);
                        }

                        this.plugin.getMessages().send(player, "stack-success", Map.of("amount", String.valueOf(nearby.getStackSize())));
                        return;
                     }
                  }
               }
            }

            SpawnerData data = this.plugin.getSpawnerManager().create(block.getLocation(), type, totalUnits);
            if (data == null) {
               event.setCancelled(true);
            } else {
               if (player.getGameMode() != GameMode.CREATIVE && shift) {
                  item.setAmount(0);
                  if (event.getHand() != null) {
                     player.getInventory().setItem(event.getHand(), (ItemStack)null);
                  } else {
                     player.getInventory().setItemInMainHand((ItemStack)null);
                  }
               }

            }
         } else {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      Block block = event.getBlock();
      if (block.getType() == Material.SPAWNER) {
         SpawnerData data = this.plugin.getSpawnerManager().getAt(block.getLocation());
         Player player = event.getPlayer();
         if (data == null) {
            if (this.plugin.getSettings().convertNaturalSpawners()) {
               BlockState var16 = block.getState();
               if (var16 instanceof CreatureSpawner) {
                  CreatureSpawner creatureSpawner = (CreatureSpawner)var16;
                  EntityType type = creatureSpawner.getSpawnedType();
                  if (type != null && this.plugin.getEntities().isSupported(type)) {
                     if (player.hasPermission("nyrethacorecore.system.spawners.admin") || player.hasPermission("nyrethacorecore.system.spawners.use")) {
                        if (this.plugin.getSettings().silkTouchRequired() && player.getGameMode() != GameMode.CREATIVE) {
                           ItemStack tool = player.getInventory().getItemInMainHand();
                           if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) <= 0) {
                              return;
                           }
                        }

                        event.setCancelled(true);
                        event.setDropItems(false);
                        event.setExpToDrop(0);
                        block.setType(Material.AIR);
                        ItemStack spawnerItem = ItemBuilder.createSpawner(type, 1);
                        if (this.plugin.getSettings().directToInventory()) {
                           Map<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack[]{spawnerItem});
                           leftover.values().forEach((i) -> block.getWorld().dropItemNaturally(block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), i));
                        } else {
                           block.getWorld().dropItemNaturally(block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), spawnerItem);
                        }

                     }
                  }
               }
            }
         } else {
            event.setCancelled(true);
            event.setDropItems(false);
            if (this.plugin.getSettings().isBreakingEnabled()) {
               if (!player.hasPermission("nyrethacorecore.system.spawners.admin") && !player.hasPermission("nyrethacorecore.system.spawners.use")) {
                  this.plugin.getMessages().send(player, "no-permission");
               } else {
                  if (this.plugin.getSettings().silkTouchRequired() && player.getGameMode() != GameMode.CREATIVE) {
                     ItemStack tool = player.getInventory().getItemInMainHand();
                     if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) <= 0) {
                        this.plugin.getMessages().send(player, "break-denied");
                        return;
                     }
                  }

                  int chance = this.plugin.getSettings().getDropChance();
                  boolean drop = player.getGameMode() == GameMode.CREATIVE || chance >= 100 || chance > 0 && Math.random() * (double)100.0F < (double)chance;
                  int stack = Math.max(1, data.getStackSize());
                  int take;
                  if (player.isSneaking()) {
                     take = Math.min(64, stack);
                  } else {
                     take = 1;
                  }

                  take = Math.min(take, stack);
                  int remaining = stack - take;
                  if (!drop) {
                     if (remaining <= 0) {
                        data.getStorage().clear();
                        this.plugin.getSpawnerManager().remove(data);
                        block.setType(Material.AIR);
                     }

                  } else {
                     for(ItemStack spawnerItem : ItemBuilder.createSpawnerStacks(data.getEntityType(), take)) {
                        if (this.plugin.getSettings().directToInventory()) {
                           Map<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack[]{spawnerItem});
                           leftover.values().forEach((i) -> block.getWorld().dropItemNaturally(block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), i));
                        } else {
                           block.getWorld().dropItemNaturally(block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), spawnerItem);
                        }
                     }

                     if (remaining <= 0) {
                        data.getStorage().clear();
                        this.plugin.getSpawnerManager().remove(data);
                        block.setType(Material.AIR);
                     } else {
                        data.setStackSize(remaining);
                        data.setDirty(true);
                        if (this.plugin.getSettings().isHolograms()) {
                           this.plugin.getHologramManager().update(data);
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
      ignoreCancelled = true
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getHand() == EquipmentSlot.HAND) {
         if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.SPAWNER) {
               SpawnerData data = this.plugin.getSpawnerManager().getAt(block.getLocation());
               if (data != null) {
                  event.setCancelled(true);
                  Player player = event.getPlayer();
                  ItemStack hand = player.getInventory().getItemInMainHand();
                  if (hand.getType().name().endsWith("_SPAWN_EGG") && player.hasPermission("nyrethacorecore.system.spawners.admin")) {
                     String eggName = hand.getType().name().replace("_SPAWN_EGG", "");

                     try {
                        EntityType newType = EntityType.valueOf(eggName);
                        if (this.plugin.getEntities().isSupported(newType)) {
                           data.setEntityType(newType);
                           this.plugin.getSpawnerManager().applyBlock(block, newType);
                           if (this.plugin.getSettings().isHolograms()) {
                              this.plugin.getHologramManager().update(data);
                           }

                           if (player.getGameMode() != GameMode.CREATIVE) {
                              hand.setAmount(hand.getAmount() - 1);
                           }

                           this.plugin.getMessages().send(player, "type-changed", Map.of("entity", newType.name()));
                           return;
                        }
                     } catch (IllegalArgumentException var11) {
                     }
                  }

                  if (ItemBuilder.isEconomySpawner(hand) && player.hasPermission("nyrethacorecore.system.spawners.use")) {
                     EntityType handType = ItemBuilder.getType(hand);
                     int perItem = ItemBuilder.getStack(hand);
                     if (handType == data.getEntityType()) {
                        boolean shift = player.isSneaking();
                        int units = shift ? perItem * Math.max(1, hand.getAmount()) : perItem;
                        int added = this.plugin.getSpawnerManager().tryStackPartial(data, units);
                        if (added > 0) {
                           if (player.getGameMode() != GameMode.CREATIVE) {
                              this.consumeSpawnerUnits(hand, added, perItem, player);
                           }

                           this.plugin.getMessages().send(player, "stack-success", Map.of("amount", String.valueOf(data.getStackSize())));
                        } else {
                           this.plugin.getMessages().send(player, "stack-limit", Map.of("max", String.valueOf(this.plugin.getSettings().getMaxStack())));
                        }

                        return;
                     }
                  }

                  if (!player.hasPermission("nyrethacorecore.system.spawners.use")) {
                     this.plugin.getMessages().send(player, "no-permission");
                  } else {
                     SpawnerGui.open(player, data);
                  }
               }
            }
         }
      }
   }

   private void consumeSpawnerUnits(ItemStack hand, int unitsToRemove, int perItem, Player player) {
      if (perItem <= 0) {
         perItem = 1;
      }

      int itemsToRemove = (int)Math.ceil((double)unitsToRemove / (double)perItem);
      int amount = hand.getAmount();
      if (itemsToRemove >= amount) {
         player.getInventory().setItemInMainHand((ItemStack)null);
      } else {
         hand.setAmount(amount - itemsToRemove);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onExplode(EntityExplodeEvent event) {
      if (this.plugin.getSettings().explosionProtection()) {
         Iterator<Block> it = event.blockList().iterator();

         while(it.hasNext()) {
            Block b = (Block)it.next();
            if (b.getType() == Material.SPAWNER && this.plugin.getSpawnerManager().getAt(b.getLocation()) != null) {
               it.remove();
            }
         }

      }
   }
}
