package me.xyr0.astralsmpcore.sell.listener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.xyr0.astralsmpcore.data.DataPaths;
import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.sell.SellAxeManager;
import me.xyr0.astralsmpcore.sell.SellRecord;
import me.xyr0.astralsmpcore.util.EconomyUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SellAxeListener implements Listener {
   private final AstralSell plugin;
   private final SellAxeManager manager;

   public SellAxeListener(AstralSell plugin, SellAxeManager manager) {
      this.plugin = plugin;
      this.manager = manager;
   }

   private boolean isSellableContainer(Material type) {
      return type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.BARREL || type.name().contains("SHULKER_BOX");
   }

   private Inventory resolveFullInventory(Block block) {
      BlockState state = block.getState();
      if (state instanceof Container container) {
         Inventory inv = container.getInventory();
         InventoryHolder holder = inv.getHolder();
         if (holder instanceof DoubleChest doubleChest) {
            return doubleChest.getInventory();
         } else if (state instanceof Chest chest) {
            InventoryHolder h = chest.getInventory().getHolder();
            if (h instanceof DoubleChest doubleChest) {
               return doubleChest.getInventory();
            } else {
               return chest.getInventory();
            }
         } else {
            return inv;
         }
      } else {
         return null;
      }
   }

   private void refreshContainer(Block block) {
      BlockState state = block.getState();
      if (state instanceof Chest chest) {
         InventoryHolder holder = chest.getInventory().getHolder();
         if (holder instanceof DoubleChest doubleChest) {
            InventoryHolder left = doubleChest.getLeftSide();
            InventoryHolder right = doubleChest.getRightSide();
            if (left instanceof Chest leftChest) {
               leftChest.update(true, false);
            }

            if (right instanceof Chest rightChest) {
               rightChest.update(true, false);
            }

         } else {
            chest.update(true, false);
         }
      } else {
         if (state instanceof Container container) {
            container.update(true, false);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      ItemStack hand = player.getInventory().getItemInMainHand();
      if (this.manager.isSellAxe(hand)) {
         if (this.manager.isExpired(hand)) {
            player.getInventory().setItemInMainHand((ItemStack)null);
            this.plugin.getMessages().send(player, "sellaxe-expired");
            event.setCancelled(true);
         } else {
            Block block = event.getBlock();
            if (this.isSellableContainer(block.getType())) {
               event.setCancelled(true);
               Inventory inv = this.resolveFullInventory(block);
               if (inv != null) {
                  Map<Material, int[]> sold = new HashMap();
                  double total = (double)0.0F;
                  int items = 0;
                  ItemStack[] contents = inv.getContents();
                  boolean changed = false;

                  for(int i = 0; i < contents.length; ++i) {
                     ItemStack stack = contents[i];
                     if (stack != null && !stack.getType().isAir() && !this.plugin.getBlacklist().isBlocked(stack)) {
                        double pay = this.plugin.getPrices().getSellPrice(player, stack);
                        if (!(pay <= (double)0.0F)) {
                           total += pay;
                           items += stack.getAmount();
                           sold.computeIfAbsent(stack.getType(), (k) -> new int[]{0, 0});
                           int[] var10000 = (int[])sold.get(stack.getType());
                           var10000[0] += stack.getAmount();
                           var10000 = (int[])sold.get(stack.getType());
                           var10000[1] += (int)Math.round(pay * (double)100.0F);
                           contents[i] = null;
                           changed = true;
                        }
                     }
                  }

                  if (changed && items > 0 && !(total <= (double)0.0F)) {
                     inv.setContents(contents);

                     for(int i = 0; i < inv.getSize(); ++i) {
                        ItemStack stack = inv.getItem(i);
                        if (stack != null && !stack.getType().isAir() && !this.plugin.getBlacklist().isBlocked(stack) && this.plugin.getPrices().getSellPrice(player, stack) > (double)0.0F) {
                           inv.setItem(i, (ItemStack)null);
                        }
                     }

                     this.refreshContainer(block);
                     this.plugin.getEconomy().deposit(player, total);

                     try {
                        File f = DataPaths.migrate(this.plugin.getCore(), "playerdata.yml", "sell/playerdata.yml");
                        YamlConfiguration y = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
                        String path = "earned." + String.valueOf(player.getUniqueId());
                        y.set(path, y.getDouble(path, (double)0.0F) + total);
                        y.save(f);
                     } catch (Exception var18) {
                     }

                     this.plugin.getProgress().addMoneyMade(player.getUniqueId(), total);
                     long now = System.currentTimeMillis();

                     for(Map.Entry<Material, int[]> e : sold.entrySet()) {
                        this.plugin.getHistory().add(new SellRecord(UUID.randomUUID(), player.getUniqueId(), (Material)e.getKey(), ((int[])e.getValue())[0], (double)((int[])e.getValue())[1] / (double)100.0F, now));
                     }

                     this.plugin.getMessages().send(player, "sellaxe-sold", Map.of("amount", EconomyUtil.formatSell(total), "items", String.valueOf(items)));
                     FileConfiguration cfg = this.manager.getConfig();
                     if (cfg.getBoolean("sound.enabled", true)) {
                        try {
                           Sound s = Sound.valueOf(cfg.getString("sound.type", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                           SoundUtil.play(player, player.getLocation(), s, (float)cfg.getDouble("sound.volume", (double)1.0F), (float)cfg.getDouble("sound.pitch", (double)1.0F));
                        } catch (Exception var17) {
                        }
                     }

                     if (cfg.getBoolean("particle.enabled", true)) {
                        try {
                           Particle p = Particle.valueOf(cfg.getString("particle.type", "CLOUD"));
                           player.getWorld().spawnParticle(p, block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), cfg.getInt("particle.count", 15), 0.3, 0.3, 0.3, 0.02);
                        } catch (Exception var16) {
                        }
                     }

                  } else {
                     this.plugin.getMessages().send(player, "sellaxe-empty");
                  }
               }
            }
         }
      }
   }
}
