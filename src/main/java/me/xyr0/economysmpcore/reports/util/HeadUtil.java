package me.xyr0.economysmpcore.reports.util;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadUtil {
   public static ItemStack getHead(OfflinePlayer player) {
      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta meta = (SkullMeta)head.getItemMeta();
      if (meta != null) {
         meta.setOwningPlayer(player);
         head.setItemMeta(meta);
      }

      return head;
   }
}
