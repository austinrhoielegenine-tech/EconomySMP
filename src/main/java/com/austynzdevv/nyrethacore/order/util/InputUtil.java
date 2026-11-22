package com.austynzdevv.nyrethacore.order.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.order.EconomyOrder;
import com.austynzdevv.nyrethacore.order.gui.MenuSession;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InputUtil implements Listener {
   private static final Map<UUID, MenuSession.ChatMode> SIGN_WAIT = new ConcurrentHashMap();
   private static final Map<UUID, Location> SIGN_LOC = new ConcurrentHashMap();
   private static final Map<UUID, Material> SIGN_PREV = new ConcurrentHashMap();
   private static final Set<String> PROTECTED = ConcurrentHashMap.newKeySet();
   private final EconomyOrder plugin;

   public InputUtil(EconomyOrder plugin) {
      this.plugin = plugin;
   }

   public static boolean useSign(EconomyOrder plugin) {
      return plugin.getConfig().getBoolean("chat-input.SignAPI", true) && !plugin.getConfig().getBoolean("chat-input.ChatAPI", false);
   }

   public static boolean useChat(EconomyOrder plugin) {
      if (plugin.getConfig().getBoolean("chat-input.ChatAPI", false)) {
         return true;
      } else {
         return !plugin.getConfig().getBoolean("chat-input.SignAPI", true);
      }
   }

   public void request(Player player, MenuSession.ChatMode mode) {
      UUID id = player.getUniqueId();
      MenuSession.View current = (MenuSession.View)MenuSession.VIEW.get(id);
      if (current != null) {
         MenuSession.INPUT_RETURN.put(id, current);
      }

      if (!useChat(this.plugin) && useSign(this.plugin)) {
         this.openSign(player, mode);
      } else {
         MenuSession.CHAT.put(id, mode);
         MenuSession.SWITCHING.add(id);
         player.closeInventory();
         Tasks.runLater(this.plugin.getCore(), () -> MenuSession.SWITCHING.remove(id), 2L);
         this.plugin.getMessages().send(player, "chat-input", Map.of("type", mode.name().toLowerCase()));
      }
   }

   private void openSign(Player player, MenuSession.ChatMode mode) {
      String var10000;
      switch (mode) {
         case AMOUNT -> var10000 = "sign-gui.amount";
         case PRICE -> var10000 = "sign-gui.price";
         case SEARCH -> var10000 = "sign-gui.item";
         default -> var10000 = "sign-gui.amount";
      }

      String path = var10000;
      List<String> lines = new ArrayList(this.plugin.getConfig().getStringList(path));

      while(lines.size() < 4) {
         lines.add("");
      }

      Block block = this.findAirBlock(player.getLocation());
      if (block == null) {
         MenuSession.CHAT.put(player.getUniqueId(), mode);
         player.closeInventory();
         this.plugin.getMessages().send(player, "chat-input", Map.of("type", mode.name().toLowerCase()));
      } else {
         UUID id = player.getUniqueId();
         Material prev = block.getType();
         SIGN_PREV.put(id, prev);
         SIGN_LOC.put(id, block.getLocation().clone());
         SIGN_WAIT.put(id, mode);
         PROTECTED.add(this.key(block.getLocation()));
         MenuSession.SWITCHING.add(id);
         player.closeInventory();
         block.setType(Material.OAK_SIGN, false);
         BlockState var9 = block.getState();
         if (var9 instanceof Sign) {
            Sign sign = (Sign)var9;

            for(int var11 = 0; var11 < 4; ++var11) {
               String raw = lines.get(var11) == null ? "" : (String)lines.get(var11);
               sign.getSide(Side.FRONT).line(var11, ColorUtil.parse(raw));
            }

            sign.setWaxed(false);
            sign.update(true, false);
            Tasks.runLater(this.plugin.getCore(), () -> {
               if (player.isOnline() && SIGN_WAIT.containsKey(id)) {
                  Block b = ((Location)SIGN_LOC.get(id)).getBlock();
                  BlockState patt0$temp = b.getState();
                  if (patt0$temp instanceof Sign) {
                     Sign openSign = (Sign)patt0$temp;

                     for(int i = 0; i < 4; ++i) {
                        String raw = lines.get(i) == null ? "" : (String)lines.get(i);
                        openSign.getSide(Side.FRONT).line(i, ColorUtil.parse(raw));
                     }

                     openSign.setWaxed(false);
                     openSign.update(true, false);
                     player.openSign(openSign, Side.FRONT);
                     Tasks.runLater(this.plugin.getCore(), () -> MenuSession.SWITCHING.remove(id), 2L);
                  } else {
                     this.restore(id);
                     MenuSession.SWITCHING.remove(id);
                  }
               } else {
                  this.restore(id);
                  MenuSession.SWITCHING.remove(id);
               }
            }, 2L);
         } else {
            this.restore(id);
            MenuSession.SWITCHING.remove(id);
         }
      }
   }

   private Block findAirBlock(Location base) {
      Block block = base.getBlock();
      if (block.getType().isAir()) {
         return block;
      } else {
         for(int dy = 0; dy >= -3; --dy) {
            Block b = base.clone().add((double)0.0F, (double)dy, (double)0.0F).getBlock();
            if (b.getType().isAir()) {
               return b;
            }
         }

         for(BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP}) {
            Block b = base.getBlock().getRelative(face);
            if (b.getType().isAir()) {
               return b;
            }
         }

         return null;
      }
   }

   private String key(Location loc) {
      String var10000 = loc.getWorld().getName();
      return var10000 + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
   }

   private boolean isProtected(Location loc) {
      return PROTECTED.contains(this.key(loc));
   }

   private void restore(UUID id) {
      Location loc = (Location)SIGN_LOC.remove(id);
      Material prev = (Material)SIGN_PREV.remove(id);
      SIGN_WAIT.remove(id);
      if (loc != null) {
         PROTECTED.remove(this.key(loc));
         Tasks.run(this.plugin.getCore(), () -> {
            Block block = loc.getBlock();
            Material type = block.getType();
            if (type == Material.OAK_SIGN || type == Material.OAK_WALL_SIGN || type.name().endsWith("_SIGN")) {
               block.setType(prev != null && !prev.name().endsWith("_SIGN") ? prev : Material.AIR, false);
            }

         });
      }

   }

   @EventHandler
   public void onSign(SignChangeEvent event) {
      Player player = event.getPlayer();
      UUID id = player.getUniqueId();
      MenuSession.ChatMode mode = (MenuSession.ChatMode)SIGN_WAIT.get(id);
      if (mode != null) {
         event.setCancelled(true);
         String value = "";

         for(int i = 0; i < 4; ++i) {
            String line = event.getLine(i);
            if (line != null && !line.trim().isEmpty()) {
               value = line.trim();
               break;
            }
         }

         this.restore(id);
         String finalValue = value;
         Tasks.run(this.plugin.getCore(), () -> this.plugin.handleInput(player, mode, finalValue));
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      if (this.isProtected(event.getBlock().getLocation())) {
         event.setCancelled(true);
         event.setDropItems(false);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onPhysics(BlockPhysicsEvent event) {
      if (this.isProtected(event.getBlock().getLocation())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onExplode(EntityExplodeEvent event) {
      event.blockList().removeIf((b) -> this.isProtected(b.getLocation()));
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBlockExplode(BlockExplodeEvent event) {
      event.blockList().removeIf((b) -> this.isProtected(b.getLocation()));
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.restore(event.getPlayer().getUniqueId());
      MenuSession.CHAT.remove(event.getPlayer().getUniqueId());
      MenuSession.INPUT_RETURN.remove(event.getPlayer().getUniqueId());
   }
}
