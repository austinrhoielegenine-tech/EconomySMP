package me.xyr0.astralsmpcore.bounties.listener;

import me.xyr0.astralsmpcore.bounties.AstralBounties;
import me.xyr0.astralsmpcore.bounties.BountyManager;
import me.xyr0.astralsmpcore.economy.CoreEconomy;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.HotbarUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class KillListener implements Listener {
   private final AstralBounties plugin;

   public KillListener(AstralBounties plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onDeath(PlayerDeathEvent event) {
      Player victim = event.getEntity();
      Player killer = victim.getKiller();
      if (killer != null) {
         boolean selfReward = this.plugin.getConfig().getBoolean("if-kill-myself-get-reward", false);
         if (selfReward || !killer.getUniqueId().equals(victim.getUniqueId())) {
            BountyManager mgr = this.plugin.getBounties();
            if (mgr.hasBounty(victim.getUniqueId())) {
               double amount = mgr.clearBounty(victim.getUniqueId());
               if (!(amount <= (double)0.0F)) {
                  CoreEconomy.give(killer, amount);
                  String fmt = BountyManager.formatMoney(amount);
                  killer.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("bounty-claimed", "&fYou claimed &#fb0000$%amount_format% &fbounty for killing &#00a3fb%target_name%&f!").replace("%amount_format%", fmt).replace("%target_name%", victim.getName())));
                  victim.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("u-got-bounty-kill", "&#00a3fb%user_name% &fjust killed you and claimed your bounty!").replace("%user_name%", killer.getName()).replace("%amount_format%", fmt)));
                  String barKiller = this.plugin.getMessages().get("actionbar-bounty-claimed", "");
                  if (barKiller != null && !barKiller.isBlank()) {
                     HotbarUtil.send(killer, ColorUtil.parse(barKiller.replace("%amount_format%", fmt).replace("%target_name%", victim.getName())));
                  }

                  String barVictim = this.plugin.getMessages().get("actionbar-u-got-bounty-kill", this.plugin.getMessages().get("u-got-bounty-kill", ""));
                  if (barVictim != null && !barVictim.isBlank()) {
                     HotbarUtil.send(victim, ColorUtil.parse(barVictim.replace("%user_name%", killer.getName()).replace("%amount_format%", fmt)));
                  }

                  this.play(killer, "bounty-claimed");
               }
            }
         }
      }
   }

   private void play(Player player, String key) {
      try {
         String path = "sounds." + key;
         if (!this.plugin.getConfig().getBoolean(path + ".enabled", true)) {
            return;
         }

         String sound = this.plugin.getConfig().getString(path + ".sound", "ENTITY_PLAYER_LEVELUP");
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var5) {
      }

   }
}
