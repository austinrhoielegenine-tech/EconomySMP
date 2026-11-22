package com.austynzdevv.nyrethacore.combat.listener;

import com.austynzdevv.nyrethacore.combat.EconomyCombat;
import com.austynzdevv.nyrethacore.combat.CombatManager;
import com.austynzdevv.nyrethacore.combat.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
   private final EconomyCombat plugin;
   private final CombatManager manager;

   public PlayerListener(EconomyCombat plugin) {
      this.plugin = plugin;
      this.manager = plugin.getCombatManager();
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (this.manager.isInCombat(player)) {
         if (this.plugin.getConfig().getBoolean("settings.kill-on-quit", true)) {
            player.setHealth((double)0.0F);
            String msg = this.plugin.getConfig().getString("messages.quit-punish", "&c%player% logged out during combat and was punished.");
            msg = msg.replace("%player%", player.getName());
            Bukkit.broadcastMessage(MessageUtil.color(msg));
         }

         this.manager.leaveCombat(player, false);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onDeath(PlayerDeathEvent event) {
      Player victim = event.getEntity();
      Player killer = victim.getKiller();
      this.manager.leaveCombat(victim, false);
      if (this.plugin.getConfig().getBoolean("kill-message.enabled", true)) {
         if (killer != null) {
            String weapon = "unknown";
            ItemStack hand = killer.getInventory().getItemInMainHand();
            if (hand != null && hand.getType() != Material.AIR) {
               weapon = hand.getType().name().toLowerCase().replace("_", " ");
            }

            String format = this.plugin.getConfig().getString("kill-message.format", "&c%killer% &7eliminated &c%victim% &7with &f%weapon%");
            if (hand == null || hand.getType() == Material.AIR) {
               format = this.plugin.getConfig().getString("kill-message.no-weapon", "&c%killer% &7eliminated &c%victim%");
            }

            String msg = format.replace("%killer%", killer.getName()).replace("%victim%", victim.getName()).replace("%weapon%", weapon);
            event.setDeathMessage(MessageUtil.color(msg));
         } else {
            String format = this.plugin.getConfig().getString("kill-message.natural-death", "&7%victim% &7died");
            event.setDeathMessage(MessageUtil.color(format.replace("%victim%", victim.getName())));
         }
      }

      if (killer != null && this.plugin.getConfig().getBoolean("kill-reward.enabled", true)) {
         this.manager.applyReward(killer);
      }

      if (killer != null) {
         this.manager.leaveCombat(killer, false);
      }

   }
}
