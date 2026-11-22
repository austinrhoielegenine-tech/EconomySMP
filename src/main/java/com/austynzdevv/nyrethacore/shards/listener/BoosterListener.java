package com.austynzdevv.nyrethacore.shards.listener;

import com.austynzdevv.nyrethacore.shards.EconomyShards;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class BoosterListener implements Listener {
   private final EconomyShards plugin;

   public BoosterListener(EconomyShards plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onInteract(PlayerInteractEvent event) {
      if (event.getHand() == EquipmentSlot.HAND) {
         Action action = event.getAction();
         if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (player.isSneaking()) {
               ItemStack item = player.getInventory().getItemInMainHand();
               if (this.plugin.getBoosterFactory().isBooster(item)) {
                  event.setCancelled(true);
                  if (this.plugin.getBoosters().isActive(player.getUniqueId())) {
                     String msg = this.plugin.getMessages().get("booster-already-active", "&cYou already have an active shard booster!");
                     String var11 = this.plugin.getMessages().prefix();
                     player.sendMessage(ColorUtil.parse(var11 + msg));
                  } else if (this.plugin.getBoosters().activate(player)) {
                     if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                     } else {
                        player.getInventory().setItemInMainHand((ItemStack)null);
                     }

                     long hours = this.plugin.getConfig().getLong("shard-booster.duration-hours", 24L);
                     double speed = this.plugin.getConfig().getDouble("shard-booster.speed-multiplier", (double)4.0F);
                     String activated = this.plugin.getMessages().get("booster-activated", "&fShard booster activated! &#A20AD6%speed%x &fspeed for &#A20AD6%time%h &fin the AFK zone.");
                     String var10001 = this.plugin.getMessages().prefix();
                     player.sendMessage(ColorUtil.parse(var10001 + activated.replace("%speed%", this.stripZero(speed)).replace("%time%", String.valueOf(hours))));
                     this.playFx(player);
                  }
               }
            }
         }
      }
   }

   private void playFx(Player player) {
      FileConfiguration itemCfg = this.plugin.getBoosterFactory().getItemCfg();
      FileConfiguration cfg = this.plugin.getConfig();

      try {
         String sound = cfg.getString("shard-booster.activate-sound", itemCfg.getString("sound.type", "BLOCK_BEACON_ACTIVATE"));
         float vol = (float)cfg.getDouble("shard-booster.activate-volume", itemCfg.getDouble("sound.volume", (double)1.0F));
         float pitch = (float)cfg.getDouble("shard-booster.activate-pitch", itemCfg.getDouble("sound.pitch", 1.2));

         for(Player listener : player.getWorld().getPlayers()) {
            SoundUtil.play(listener, player.getLocation(), Sound.valueOf(sound), vol, pitch);
         }
      } catch (Exception var10) {
      }

      try {
         if (itemCfg.getBoolean("particle.enabled", true)) {
            Particle p = Particle.valueOf(itemCfg.getString("particle.type", "PORTAL"));
            int count = itemCfg.getInt("particle.count", 40);
            player.getWorld().spawnParticle(p, player.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F), count, (double)0.5F, 0.6, (double)0.5F, 0.05);
         }

         if (itemCfg.getBoolean("particle.additional-particle.enabled", true)) {
            Particle p = Particle.valueOf(itemCfg.getString("particle.additional-particle.type", "DRAGON_BREATH"));
            int count = itemCfg.getInt("particle.additional-particle.count", 25);
            player.getWorld().spawnParticle(p, player.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F), count, 0.4, (double)0.5F, 0.4, 0.02);
         }
      } catch (Exception var9) {
      }

   }

   private String stripZero(double d) {
      return d == (double)((long)d) ? String.valueOf((long)d) : String.valueOf(d);
   }
}
