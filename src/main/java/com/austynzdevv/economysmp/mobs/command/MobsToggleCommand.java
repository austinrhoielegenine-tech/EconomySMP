package com.austynzdevv.economysmp.mobs.command;

import com.austynzdevv.economysmp.mobs.EconomyMobs;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.HotbarUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class MobsToggleCommand implements CommandExecutor {
   private final EconomyMobs plugin;

   public MobsToggleCommand(EconomyMobs plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         sender.sendMessage("Only players can use this command.");
         return true;
      } else if (!player.hasPermission("EconomyMobs.use")) {
         player.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages.no-permission")));
         return true;
      } else {
         boolean currentlyOn = this.plugin.getPlayerDataManager().isMobSpawningEnabled(player.getUniqueId());
         boolean newStatus = !currentlyOn;
         this.plugin.getPlayerDataManager().setMobSpawning(player.getUniqueId(), newStatus);
         if (!newStatus) {
            double radius = this.plugin.getRadius();

            for(Monster monster : player.getLocation().getNearbyEntitiesByType(Monster.class, radius)) {
               monster.remove();
            }
         }

         String key = newStatus ? "enabled" : "disabled";
         player.sendMessage(ColorUtil.color(this.plugin.getConfig().getString("messages." + key)));
         HotbarUtil.send(player, TextUtil.component(this.plugin.getConfig().getString("actionbar." + key)));

         try {
            Sound sound = Sound.valueOf(this.plugin.getConfig().getString("sounds." + key, "ENTITY_EXPERIENCE_ORB_PICKUP"));
            float vol = (float)this.plugin.getConfig().getDouble("sounds.volume", (double)1.0F);
            float pitch = (float)this.plugin.getConfig().getDouble("sounds.pitch", (double)1.0F);
            SoundUtil.play(player, player.getLocation(), sound, vol, pitch);
         } catch (Exception var12) {
         }

         return true;
      }
   }
}
