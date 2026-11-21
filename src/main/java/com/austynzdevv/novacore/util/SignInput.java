package com.austynzdevv.novacore.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class SignInput {
   private static final Map<UUID, Session> ACTIVE = new ConcurrentHashMap();
   private static final Set<BlockKey> PROTECTED = ConcurrentHashMap.newKeySet();

   private SignInput() {
   }

   public static void open(Plugin plugin, Player player, List<String> configuredLines, boolean colored, Runnable fallback, long timeoutTicks, Runnable onAutoCleanup) {
      cleanup(player);
      Location base = player.getLocation();
      Location location = base.clone();
      location.setY((double)Math.max(player.getWorld().getMinHeight() + 1, base.getBlockY() - 2));
      Block block = location.getBlock();
      Inventory openInventory = player.getOpenInventory().getTopInventory();
      if (openInventory == player.getInventory() || player.getOpenInventory().getType() == InventoryType.CRAFTING) {
         openInventory = null;
      }

      Session session = new Session(location.clone(), block.getBlockData().clone(), plugin, openInventory);
      UUID id = player.getUniqueId();
      ACTIVE.put(id, session);
      PROTECTED.add(key(location));
      player.closeInventory();
      block.setType(Material.OAK_SIGN, false);
      BlockState var15 = block.getState();
      if (var15 instanceof Sign sign) {
         List var16 = lines(configuredLines);
         write(sign, var16, colored);
         sign.update(true, false);
         Tasks.runLater(plugin, () -> {
            if (player.isOnline() && ACTIVE.get(id) == session) {
               Block current = session.location().getBlock();
               BlockState patt0$temp = current.getState();
               if (patt0$temp instanceof Sign) {
                  Sign opened = (Sign)patt0$temp;
                  write(opened, var16, colored);
                  opened.update(true, false);

                  try {
                     player.openSign(opened, Side.FRONT);
                  } catch (Exception var13) {
                     fail(player, session, fallback);
                     return;
                  }

                  if (timeoutTicks > 0L) {
                     Tasks.runLater(plugin, () -> {
                        if (ACTIVE.get(id) == session) {
                           cleanup(player);
                           if (onAutoCleanup != null) {
                              onAutoCleanup.run();
                           }

                           if (player.isOnline() && session.previousInventory() != null) {
                              player.openInventory(session.previousInventory());
                           }
                        }

                     }, timeoutTicks);
                  }

               } else {
                  fail(player, session, fallback);
               }
            } else {
               if (!player.isOnline()) {
                  cleanup(player);
                  if (onAutoCleanup != null) {
                     onAutoCleanup.run();
                  }
               }

            }
         }, 2L);
      } else {
         fail(player, session, fallback);
      }
   }

   public static void cleanup(Player player) {
      if (player != null) {
         Session session = (Session)ACTIVE.remove(player.getUniqueId());
         if (session != null) {
            PROTECTED.remove(key(session.location()));
            session.location().getBlock().setBlockData(session.previous(), false);
         }
      }
   }

   public static boolean isActive(UUID id) {
      return id != null && ACTIVE.containsKey(id);
   }

   public static boolean isActive(UUID id, Plugin owner) {
      Session session = id == null ? null : (Session)ACTIVE.get(id);
      return session != null && session.owner() == owner;
   }

   public static boolean isProtected(Location location) {
      return location != null && location.getWorld() != null && PROTECTED.contains(key(location));
   }

   private static void fail(Player player, Session session, Runnable fallback) {
      if (ACTIVE.get(player.getUniqueId()) == session) {
         cleanup(player);
      }

      fallback.run();
   }

   private static List<String> lines(List<String> configured) {
      String[] result = new String[4];

      for(int i = 0; i < 4; ++i) {
         result[i] = configured != null && i < configured.size() && configured.get(i) != null ? (String)configured.get(i) : "";
      }

      return List.of(result);
   }

   private static void write(Sign sign, List<String> lines, boolean colored) {
      SignSide side = sign.getSide(Side.FRONT);

      for(int i = 0; i < 4; ++i) {
         String line = (String)lines.get(i);
         if (!colored) {
            side.setLine(i, line);
         } else {
            try {
               side.line(i, ColorUtil.parse(line));
            } catch (Exception var7) {
               side.setLine(i, line);
            }
         }
      }

   }

   private static BlockKey key(Location location) {
      return new BlockKey(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   private static record Session(Location location, BlockData previous, Plugin owner, Inventory previousInventory) {
   }

   private static record BlockKey(UUID world, int x, int y, int z) {
   }
}
