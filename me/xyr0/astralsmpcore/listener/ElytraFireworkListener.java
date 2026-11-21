package me.xyr0.astralsmpcore.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public final class ElytraFireworkListener implements Listener {
   private final NamespacedKey toolTypeKey = new NamespacedKey("astralsmpcore", "tool_type");

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = false
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getHand() != null) {
         Action action = event.getAction();
         if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (player.isGliding()) {
               ItemStack item = event.getItem();
               if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
                  event.setCancelled(true);
                  event.setUseItemInHand(Result.DENY);
                  event.setUseInteractedBlock(Result.DENY);
                  Vector dir = player.getLocation().getDirection();
                  if (!(dir.lengthSquared() < 1.0E-6)) {
                     dir.normalize();
                     int power = 1;

                     try {
                        ItemMeta var8 = item.getItemMeta();
                        if (var8 instanceof FireworkMeta) {
                           FireworkMeta fm = (FireworkMeta)var8;
                           power = Math.max(1, Math.min(3, fm.getPower()));
                        }
                     } catch (Exception var14) {
                     }

                     double strength = (double)1.5F + (double)(power - 1) * 0.45;
                     if (this.isInfiniteRocket(item)) {
                        strength = Math.max(strength, (double)1.5F);
                     }

                     player.setVelocity(player.getVelocity().add(dir.multiply(strength)));

                     try {
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F);
                     } catch (Exception var13) {
                     }

                     boolean infiniteRocket = this.isInfiniteRocket(item);
                     if (!infiniteRocket && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                        EquipmentSlot hand = event.getHand();
                        ItemStack held = hand == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
                        if (held != null && held.getType() == Material.FIREWORK_ROCKET) {
                           int amount = held.getAmount();
                           if (amount <= 1) {
                              if (hand == EquipmentSlot.OFF_HAND) {
                                 player.getInventory().setItemInOffHand((ItemStack)null);
                              } else {
                                 player.getInventory().setItemInMainHand((ItemStack)null);
                              }
                           } else {
                              held.setAmount(amount - 1);
                           }
                        }
                     }

                  }
               }
            }
         }
      }
   }

   private boolean isInfiniteRocket(ItemStack item) {
      if (item != null && item.hasItemMeta()) {
         try {
            String type = (String)item.getItemMeta().getPersistentDataContainer().get(this.toolTypeKey, PersistentDataType.STRING);
            return type != null && type.equalsIgnoreCase("rocket");
         } catch (Exception var3) {
            return false;
         }
      } else {
         return false;
      }
   }
}
