package me.xyr0.economysmpcore.ec.gui;

import java.util.UUID;
import me.xyr0.economysmpcore.ec.AstralEC;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class EcGui {
   private EcGui() {
   }

   public static int rowsFor(Player player, AstralEC plugin) {
      for(int r = 6; r >= 1; --r) {
         if (player.hasPermission("economysmpcore.system.ec.rows." + r) || player.hasPermission("economysmpcore.system.ec.raw." + r)) {
            return r;
         }
      }

      int def = plugin.getConfig().getInt("default-rows", 3);
      return Math.max(1, Math.min(6, def));
   }

   public static void openOwn(Player player, AstralEC plugin) {
      int rows = rowsFor(player, plugin);
      open(player, player.getUniqueId(), rows, false, plugin);
   }

   public static void openOther(Player viewer, Player target, AstralEC plugin) {
      int rows = rowsFor(target, plugin);
      open(viewer, target.getUniqueId(), rows, true, plugin);
   }

   public static void openOffline(Player viewer, UUID targetId, int rows, AstralEC plugin) {
      open(viewer, targetId, Math.max(1, Math.min(6, rows)), true, plugin);
   }

   private static void open(Player viewer, UUID owner, int rows, boolean admin, AstralEC plugin) {
      int size = rows * 9;
      EcHolder holder = new EcHolder(owner, admin);
      String title = plugin.getConfig().getString("title", "&8ᴇɴᴅᴇʀ ᴄʜᴇsᴛ");
      Inventory inv = Bukkit.createInventory(holder, size, ColorUtil.parse(title));
      holder.setInventory(inv);
      Player ownerPlayer = Bukkit.getPlayer(owner);
      ItemStack[] contents;
      if (!admin && ownerPlayer != null) {
         if (plugin.getConfig().getBoolean("seed-from-vanilla", true) && !plugin.getStorage().fileFor(owner).exists()) {
            contents = plugin.getStorage().seedFromVanilla(ownerPlayer, size);
         } else {
            contents = plugin.getStorage().loadForPlayer(ownerPlayer, size);
         }
      } else if (ownerPlayer != null) {
         contents = plugin.getStorage().loadForPlayer(ownerPlayer, size);
      } else {
         contents = plugin.getStorage().load(owner, size);
      }

      inv.setContents(contents);
      viewer.openInventory(inv);
      play(viewer, plugin, true);
   }

   public static void play(Player player, AstralEC plugin, boolean open) {
      if (plugin.getConfig().getBoolean("play-sounds", true)) {
         String path = open ? "open-sound" : "close-sound";
         String name = plugin.getConfig().getString(path, open ? "BLOCK_ENDER_CHEST_OPEN" : "BLOCK_ENDER_CHEST_CLOSE");

         try {
            SoundUtil.play(player, player.getLocation(), Sound.valueOf(name), 1.0F, 1.0F);
         } catch (Exception var6) {
         }

      }
   }
}
