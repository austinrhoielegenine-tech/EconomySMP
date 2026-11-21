package me.xyr0.economysmpcore;

import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ChainmailListener implements Listener {
   private final EconomySMPcore plugin;

   public ChainmailListener(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Player p = e.getPlayer();
      if (!p.hasPlayedBefore()) {
         Tasks.runLater(this.plugin, () -> this.giveKit(p), 10L);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onRespawn(PlayerRespawnEvent e) {
      Player p = e.getPlayer();
      Tasks.runLater(this.plugin, () -> this.giveKit(p), 5L);
   }

   public void giveKit(Player p) {
      if (p != null && p.isOnline()) {
         if (!this.isDisabled(p)) {
            PlayerInventory inv = p.getInventory();
            if (inv.getHelmet() == null || inv.getHelmet().getType().isAir()) {
               inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            }

            if (inv.getChestplate() == null || inv.getChestplate().getType().isAir()) {
               inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            }

            if (inv.getLeggings() == null || inv.getLeggings().getType().isAir()) {
               inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            }

            if (inv.getBoots() == null || inv.getBoots().getType().isAir()) {
               inv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
            }

            inv.addItem(new ItemStack[]{new ItemStack(Material.STONE_SWORD, 1)});
            inv.addItem(new ItemStack[]{new ItemStack(Material.COOKED_BEEF, 16)});
         }
      }
   }

   private boolean isDisabled(Player p) {
      try {
         Module mod = this.plugin.modules().get("settings");
         if (mod instanceof SettingsModule sm) {
            return sm.isChainmailOff(p.getUniqueId());
         }
      } catch (Exception var4) {
      }

      return false;
   }
}
