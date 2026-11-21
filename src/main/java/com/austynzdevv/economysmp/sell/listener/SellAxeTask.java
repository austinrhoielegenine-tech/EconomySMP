package com.austynzdevv.economysmp.sell.listener;

import com.austynzdevv.economysmp.sell.EconomySell;
import com.austynzdevv.economysmp.sell.SellAxeManager;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SellAxeTask implements Runnable {
   private final EconomySell plugin;
   private final SellAxeManager manager;
   private int tick;

   public SellAxeTask(EconomySell plugin, SellAxeManager manager) {
      this.plugin = plugin;
      this.manager = manager;
   }

   public void run() {
      ++this.tick;

      for(Player player : Bukkit.getOnlinePlayers()) {
         if (Tasks.isFolia()) {
            Tasks.runAtEntity(this.plugin.getCore(), player, () -> this.updatePlayer(player));
         } else {
            this.updatePlayer(player);
         }
      }

      if (!Tasks.isFolia()) {
         if (this.tick % 15 == 0) {
            for(World world : Bukkit.getWorlds()) {
               for(Chunk chunk : world.getLoadedChunks()) {
                  BlockState[] tiles;
                  try {
                     tiles = chunk.getTileEntities();
                  } catch (Exception var13) {
                     continue;
                  }

                  for(BlockState state : tiles) {
                     if (state instanceof Container) {
                        Container container = (Container)state;
                        this.updateInv(container.getInventory());
                     }
                  }
               }
            }
         }

      }
   }

   private void updatePlayer(Player player) {
      if (player != null && player.isOnline()) {
         this.updateInv(player.getInventory());
         this.updateInv(player.getEnderChest());

         try {
            if (player.getOpenInventory() != null) {
               this.updateInv(player.getOpenInventory().getTopInventory());
            }
         } catch (Exception var3) {
         }

      }
   }

   private void updateInv(Inventory inv) {
      if (inv != null) {
         ItemStack[] contents = inv.getContents();
         boolean changed = false;

         for(int i = 0; i < contents.length; ++i) {
            ItemStack stack = contents[i];
            if (stack != null && this.manager.isSellAxe(stack)) {
               if (this.manager.isExpired(stack)) {
                  contents[i] = null;
                  changed = true;
               } else if (this.manager.updateLore(stack)) {
                  contents[i] = stack;
                  changed = true;
               }
            }
         }

         if (changed) {
            inv.setContents(contents);
         }

      }
   }
}
