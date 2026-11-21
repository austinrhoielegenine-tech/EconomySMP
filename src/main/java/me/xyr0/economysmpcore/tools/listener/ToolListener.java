package me.xyr0.economysmpcore.tools.listener;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import me.xyr0.economysmpcore.tools.AstralTools;
import me.xyr0.economysmpcore.tools.ToolDefinition;
import me.xyr0.economysmpcore.tools.util.MessageUtil;
import me.xyr0.economysmpcore.tools.util.ToolItemFactory;
import me.xyr0.economysmpcore.util.SoundUtil;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ToolListener implements Listener {
   private final AstralTools plugin;
   private final ToolItemFactory factory;
   private final Map<UUID, Long> jumpCooldown = new ConcurrentHashMap();
   private final Set<UUID> processing = ConcurrentHashMap.newKeySet();

   public ToolListener(AstralTools plugin, ToolItemFactory factory) {
      this.plugin = plugin;
      this.factory = factory;
   }

   private HandTool findTool(Player player, EquipmentSlot preferred) {
      if (preferred == EquipmentSlot.OFF_HAND) {
         HandTool off = this.fromSlot(player, EquipmentSlot.OFF_HAND);
         return off != null ? off : this.fromSlot(player, EquipmentSlot.HAND);
      } else {
         HandTool main = this.fromSlot(player, EquipmentSlot.HAND);
         return main != null ? main : this.fromSlot(player, EquipmentSlot.OFF_HAND);
      }
   }

   private HandTool fromSlot(Player player, EquipmentSlot slot) {
      ItemStack item = slot == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
      String type = this.factory.getType(item);
      if (type == null) {
         return null;
      } else {
         ToolDefinition def = this.plugin.getToolManager().get(type);
         return def == null ? null : new HandTool(item, slot, type, def);
      }
   }

   private void setHand(Player player, EquipmentSlot slot, ItemStack item) {
      if (slot == EquipmentSlot.OFF_HAND) {
         player.getInventory().setItemInOffHand(item);
      } else {
         player.getInventory().setItemInMainHand(item);
      }

   }

   private void restoreHand(Player player, EquipmentSlot slot, ItemStack original, ToolDefinition def) {
      ItemStack restore = original.clone();
      restore.setAmount(1);
      this.factory.refreshLore(restore, def);
      Tasks.run(this.plugin.getCore(), () -> {
         if (player.isOnline()) {
            this.setHand(player, slot, restore.clone());
         }
      });
      Tasks.runLater(this.plugin, () -> {
         if (player.isOnline()) {
            ItemStack current = slot == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
            if (current == null || current.getType() == Material.AIR || current.getAmount() <= 0 || this.factory.getType(current) == null || !def.getId().equals(this.factory.getType(current))) {
               this.setHand(player, slot, restore.clone());
            }

         }
      }, 2L);
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (player.hasPermission("economysmpcore.system.tools.use")) {
         HandTool tool = this.findTool(player, EquipmentSlot.HAND);
         if (tool != null) {
            if (this.factory.isExpired(tool.item(), tool.def())) {
               event.setCancelled(true);
               this.setHand(player, tool.slot(), (ItemStack)null);
               MessageUtil.send(player, "tool-expired");
            } else if (!this.isSpawner(event.getBlock()) || !"drill".equals(tool.type()) && !"shovel".equals(tool.type()) && !"treechopper".equals(tool.type())) {
               UUID id = player.getUniqueId();
               if (!this.processing.contains(id)) {
                  Block origin = event.getBlock();

                  try {
                     this.processing.add(id);
                     switch (tool.type()) {
                        case "drill" -> this.breakFaceCube(player, origin, tool.item(), this::isMineable);
                        case "shovel" -> this.breakFaceCube(player, origin, tool.item(), this::isShovelable);
                        case "treechopper" -> this.breakTree(player, origin, tool.item());
                        case "hoe" -> this.harvestCube(player, origin, tool.item());
                     }
                  } finally {
                     this.processing.remove(id);
                  }

                  this.playFx(player, tool.def(), origin.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F));
                  this.afterUse(player, tool);
               }
            } else {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getHand() != null) {
         Player player = event.getPlayer();
         HandTool tool = this.findTool(player, event.getHand());
         if (tool != null) {
            if (event.getHand() == tool.slot()) {
               if (this.factory.isExpired(tool.item(), tool.def())) {
                  this.setHand(player, tool.slot(), (ItemStack)null);
                  MessageUtil.send(player, "tool-expired");
                  event.setCancelled(true);
               } else {
                  Action action = event.getAction();
                  String type = tool.type();
                  if (!"rocket".equals(type) || action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
                     if (event.getItem() == null || event.getItem().getType() != Material.FIREWORK_ROCKET) {
                        if ("hoe".equals(type) && action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                           this.tillCube(event.getClickedBlock());
                           event.setCancelled(true);
                           this.playFx(player, tool.def(), event.getClickedBlock().getLocation().add((double)0.5F, (double)0.5F, (double)0.5F));
                           this.afterUse(player, tool);
                        } else if (!"waterbucket".equals(type) || action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
                           if (!"lavabucket".equals(type) || action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
                              if (!"enderpearl".equals(type) || action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
                                 if (!"bucket".equals(type) || action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) {
                                    if ("jump".equals(type) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
                                       event.setCancelled(true);
                                       long now = System.currentTimeMillis();
                                       long ready = (Long)this.jumpCooldown.getOrDefault(player.getUniqueId(), 0L);
                                       if (now < ready) {
                                          long left = (ready - now + 999L) / 1000L;
                                          MessageUtil.send(player, "jump-cooldown", "%seconds%", String.valueOf(left));
                                          return;
                                       }

                                       double distance = Math.max((double)2.0F, (double)tool.def().getJumpDistance());
                                       Vector dir = player.getLocation().getDirection();
                                       if (dir.lengthSquared() < 0.001) {
                                          dir = new Vector(0, 1, 0);
                                       } else {
                                          dir = dir.normalize();
                                       }

                                       Vector boost = dir.multiply(distance * 1.15);
                                       if (boost.getY() < 0.65) {
                                          boost.setY(0.65 + Math.max((double)0.0F, dir.getY()) * 0.4);
                                       }

                                       player.setVelocity(boost);
                                       Vector finalBoost = boost.clone();
                                       Tasks.run(this.plugin.getCore(), () -> {
                                          if (player.isOnline()) {
                                             player.setVelocity(finalBoost);
                                          }

                                       });
                                       Tasks.runLater(this.plugin, () -> {
                                          if (player.isOnline()) {
                                             player.setVelocity(finalBoost);
                                          }

                                       }, 1L);
                                       this.jumpCooldown.put(player.getUniqueId(), now + (long)tool.def().getJumpCooldown() * 1000L);
                                       this.playFx(player, tool.def(), player.getLocation());
                                       this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                                       this.afterUse(player, tool);
                                    }

                                 } else {
                                    event.setCancelled(true);
                                    Block center = null;
                                    if (event.getClickedBlock() != null) {
                                       Block clicked = event.getClickedBlock();
                                       BlockFace face = event.getBlockFace() == null ? BlockFace.UP : event.getBlockFace();
                                       Block target = clicked.getRelative(face);
                                       if (this.isLiquid(clicked)) {
                                          center = clicked;
                                       } else if (this.isLiquid(target)) {
                                          center = target;
                                       }
                                    }

                                    if (center == null) {
                                       Block target = player.getTargetBlockExact(5);
                                       if (target != null && this.isLiquid(target)) {
                                          center = target;
                                       }
                                    }

                                    if (center == null) {
                                       this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                                    } else {
                                       int drained = this.drainLiquids(center);
                                       this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                                       if (drained > 0) {
                                          this.playFx(player, tool.def(), center.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F));
                                          this.afterUse(player, tool);
                                       }

                                    }
                                 }
                              } else {
                                 this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                              }
                           } else {
                              this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                           }
                        } else if (player.getWorld().getEnvironment() == Environment.NETHER) {
                           MessageUtil.send(player, "water-nether");
                           event.setCancelled(true);
                        } else {
                           this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                        }
                     }
                  } else if (event.getItem() != null && "rocket".equals(this.factory.getType(event.getItem()))) {
                     this.restoreHand(player, tool.slot(), tool.item(), tool.def());
                     if (player.isGliding()) {
                        this.playFx(player, tool.def(), player.getLocation());
                        this.afterUse(player, tool);
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBucketEmpty(PlayerBucketEmptyEvent event) {
      Player player = event.getPlayer();
      HandTool tool = this.findTool(player, event.getHand());
      if (tool != null) {
         if ("waterbucket".equals(tool.type()) || "lavabucket".equals(tool.type())) {
            if ("waterbucket".equals(tool.type()) && player.getWorld().getEnvironment() == Environment.NETHER) {
               event.setCancelled(true);
               MessageUtil.send(player, "water-nether");
            } else {
               ItemStack restore = tool.item().clone();
               restore.setAmount(1);
               this.factory.refreshLore(restore, tool.def());
               EquipmentSlot slot = tool.slot();
               Tasks.run(this.plugin.getCore(), () -> {
                  if (player.isOnline()) {
                     this.setHand(player, slot, restore.clone());
                  }
               });
               Tasks.runLater(this.plugin, () -> {
                  if (player.isOnline()) {
                     this.setHand(player, slot, restore.clone());
                  }
               }, 1L);
               Tasks.runLater(this.plugin, () -> {
                  if (player.isOnline()) {
                     this.setHand(player, slot, restore.clone());
                  }
               }, 3L);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBucketFill(PlayerBucketFillEvent event) {
      Player player = event.getPlayer();
      HandTool tool = this.findTool(player, event.getHand());
      if (tool != null) {
         if ("bucket".equals(tool.type()) || "waterbucket".equals(tool.type()) || "lavabucket".equals(tool.type())) {
            if ("bucket".equals(tool.type())) {
               event.setCancelled(true);
               Block block = event.getBlock();
               if (block != null && this.isLiquid(block)) {
                  int drained = this.drainLiquids(block);
                  if (drained > 0) {
                     this.playFx(player, tool.def(), block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F));
                     this.afterUse(player, tool);
                  }
               }

               this.restoreHand(player, tool.slot(), tool.item(), tool.def());
            } else {
               this.restoreHand(player, tool.slot(), tool.item(), tool.def());
            }
         }
      }
   }

   private void afterUse(Player player, HandTool tool) {
      ItemStack item = tool.item();
      if (!this.factory.consumeUse(item, tool.def())) {
         this.setHand(player, tool.slot(), (ItemStack)null);
         MessageUtil.send(player, "tool-broken");
      } else {
         this.factory.refreshLore(item, tool.def());
         this.setHand(player, tool.slot(), item);
      }

   }

   private BlockFace getLookFace(Player player) {
      Vector dir = player.getLocation().getDirection();
      double ax = Math.abs(dir.getX());
      double ay = Math.abs(dir.getY());
      double az = Math.abs(dir.getZ());
      if (ay >= ax && ay >= az) {
         return dir.getY() > (double)0.0F ? BlockFace.UP : BlockFace.DOWN;
      } else if (ax >= az) {
         return dir.getX() > (double)0.0F ? BlockFace.EAST : BlockFace.WEST;
      } else {
         return dir.getZ() > (double)0.0F ? BlockFace.SOUTH : BlockFace.NORTH;
      }
   }

   private void breakFaceCube(Player player, Block origin, ItemStack tool, Predicate<Block> filter) {
      BlockFace face = this.getLookFace(player);
      if (face != BlockFace.DOWN && face != BlockFace.UP) {
         int[][] offsets = this.facePlaneOffsets(face);

         for(int[] o : offsets) {
            if (o[0] != 0 || o[1] != 0 || o[2] != 0) {
               this.breakOne(player, origin.getRelative(o[0], o[1], o[2]), tool, filter);
            }
         }

      } else {
         this.breakVerticalTunnel(player, origin, tool, filter, face);
      }
   }

   private void breakVerticalTunnel(Player player, Block origin, ItemStack tool, Predicate<Block> filter, BlockFace face) {
      float yaw;
      for(yaw = player.getLocation().getYaw(); yaw < 0.0F; yaw += 360.0F) {
      }

      yaw %= 360.0F;
      boolean eastWest = yaw >= 45.0F && yaw < 135.0F || yaw >= 225.0F && yaw < 315.0F;
      int depthSign = face == BlockFace.DOWN ? -1 : 1;

      for(int d = 0; d < 3; ++d) {
         for(int w = -1; w <= 1; ++w) {
            int oy = d * depthSign;
            int ox;
            int oz;
            if (eastWest) {
               ox = 0;
               oz = w;
            } else {
               ox = w;
               oz = 0;
            }

            if (ox != 0 || oy != 0 || oz != 0) {
               this.breakOne(player, origin.getRelative(ox, oy, oz), tool, filter);
            }
         }
      }

   }

   private void breakOne(Player player, Block block, ItemStack tool, Predicate<Block> filter) {
      if (!this.isSpawner(block)) {
         if (filter.test(block)) {
            if (block.getType() != Material.BEDROCK && block.getType() != Material.BARRIER) {
               if (player.getGameMode() != GameMode.CREATIVE) {
                  block.breakNaturally(tool);
               } else {
                  block.setType(Material.AIR);
               }

            }
         }
      }
   }

   private int[][] facePlaneOffsets(BlockFace face) {
      int[][] var10000;
      switch (face) {
         case NORTH:
         case SOUTH:
            var10000 = new int[][]{{-1, -1, 0}, {0, -1, 0}, {1, -1, 0}, {-1, 0, 0}, {0, 0, 0}, {1, 0, 0}, {-1, 1, 0}, {0, 1, 0}, {1, 1, 0}};
            break;
         default:
            var10000 = new int[][]{{0, -1, -1}, {0, -1, 0}, {0, -1, 1}, {0, 0, -1}, {0, 0, 0}, {0, 0, 1}, {0, 1, -1}, {0, 1, 0}, {0, 1, 1}};
      }

      return var10000;
   }

   private void breakTree(Player player, Block origin, ItemStack tool) {
      if (Tag.LOGS.isTagged(origin.getType()) || origin.getType().name().contains("STEM")) {
         Material logType = origin.getType();
         ArrayDeque<Block> queue = new ArrayDeque();
         Set<Block> visited = new HashSet();
         queue.add(origin);
         visited.add(origin);
         int limit = 64;

         while(!queue.isEmpty() && visited.size() < limit) {
            Block b = (Block)queue.poll();

            for(BlockFace face : BlockFace.values()) {
               if (face.isCartesian()) {
                  Block n = b.getRelative(face);
                  if (!visited.contains(n) && (n.getType() == logType || Tag.LEAVES.isTagged(n.getType()))) {
                     visited.add(n);
                     if (n.getType() == logType || n.getType().name().contains("STEM")) {
                        queue.add(n);
                        if (n != origin && !this.isSpawner(n)) {
                           if (player.getGameMode() != GameMode.CREATIVE) {
                              n.breakNaturally(tool);
                           } else {
                              n.setType(Material.AIR);
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private void harvestCube(Player player, Block origin, ItemStack tool) {
      for(int x = -1; x <= 1; ++x) {
         for(int z = -1; z <= 1; ++z) {
            Block b = origin.getRelative(x, 0, z);
            BlockData var8 = b.getBlockData();
            if (var8 instanceof Ageable age) {
               if (age.getAge() >= age.getMaximumAge()) {
                  if (player.getGameMode() != GameMode.CREATIVE) {
                     b.breakNaturally(tool);
                  } else {
                     b.setType(Material.AIR);
                  }
               }
            }
         }
      }

   }

   private void tillCube(Block origin) {
      for(int x = -1; x <= 1; ++x) {
         for(int z = -1; z <= 1; ++z) {
            Block b = origin.getRelative(x, 0, z);
            Material type = b.getType();
            if (type == Material.DIRT || type == Material.GRASS_BLOCK || type == Material.DIRT_PATH) {
               b.setType(Material.FARMLAND);
            }
         }
      }

   }

   private boolean isLiquid(Block block) {
      if (block == null) {
         return false;
      } else {
         Material type = block.getType();
         return type == Material.WATER || type == Material.LAVA || type == Material.BUBBLE_COLUMN;
      }
   }

   private int drainLiquids(Block center) {
      int count = 0;

      for(int x = -1; x <= 1; ++x) {
         for(int y = -1; y <= 1; ++y) {
            for(int z = -1; z <= 1; ++z) {
               Block b = center.getRelative(x, y, z);
               if (this.isLiquid(b)) {
                  b.setType(Material.AIR, true);
                  ++count;
               }
            }
         }
      }

      return count;
   }

   private boolean isSpawner(Block b) {
      Material t = b.getType();
      return t == Material.SPAWNER || t.name().contains("SPAWNER");
   }

   private boolean isMineable(Block b) {
      Material t = b.getType();
      if (this.isSpawner(b)) {
         return false;
      } else {
         return t.isSolid() && t != Material.BEDROCK && t != Material.BARRIER && t != Material.COMMAND_BLOCK;
      }
   }

   private boolean isShovelable(Block b) {
      Material t = b.getType();
      return t == Material.DIRT || t == Material.GRASS_BLOCK || t == Material.SAND || t == Material.RED_SAND || t == Material.GRAVEL || t == Material.CLAY || t == Material.SOUL_SAND || t == Material.SOUL_SOIL || t == Material.MUD || t == Material.DIRT_PATH || t == Material.COARSE_DIRT || t == Material.ROOTED_DIRT || t == Material.SNOW_BLOCK || t == Material.SNOW;
   }

   private void playFx(Player player, ToolDefinition def, Location loc) {
      try {
         if (def.isSoundEnabled() && def.getSound() != null) {
            for(Player listener : player.getWorld().getPlayers()) {
               SoundUtil.play(listener, loc, def.getSound(), def.getVolume(), def.getPitch());
            }
         }
      } catch (Exception var8) {
      }

      try {
         if (def.isParticleEnabled() && def.getParticle() != null) {
            player.getWorld().spawnParticle(def.getParticle(), loc, def.getParticleCount(), 0.35, 0.35, 0.35, 0.02);
         }
      } catch (Exception var7) {
      }

      try {
         if (def.isAdditionalParticleEnabled() && def.getAdditionalParticle() != null) {
            player.getWorld().spawnParticle(def.getAdditionalParticle(), loc, def.getAdditionalParticleCount(), 0.4, 0.4, 0.4, 0.02);
         }
      } catch (Exception var6) {
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.jumpCooldown.remove(event.getPlayer().getUniqueId());
      this.processing.remove(event.getPlayer().getUniqueId());
   }

   private static record HandTool(ItemStack item, EquipmentSlot slot, String type, ToolDefinition def) {
   }
}
