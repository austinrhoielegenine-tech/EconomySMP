package me.xyr0.economysmpcore.rtp.command;

import me.xyr0.economysmpcore.rtp.AstralRTP;
import me.xyr0.economysmpcore.rtp.RTPCountdown;
import me.xyr0.economysmpcore.rtp.gui.RTPGUI;
import me.xyr0.economysmpcore.rtp.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommand implements CommandExecutor {
   private final AstralRTP plugin;

   public RTPCommand(AstralRTP plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.rtp.use")) {
            MessageUtil.send(player, "no-permission");
            return true;
         } else if (RTPCountdown.isRunning(player)) {
            MessageUtil.send(player, "errors.already-teleporting");
            return true;
         } else {
            long cd = this.plugin.getConfig().getLong("cooldown-seconds", 60L);
            if (!player.hasPermission("economysmpcore.system.rtp.admin") && this.plugin.getCooldownManager().hasCooldown(player.getUniqueId(), cd)) {
               long left = this.plugin.getCooldownManager().getRemaining(player.getUniqueId(), cd);
               MessageUtil.send(player, "rtp.cooldown", "%time%", String.valueOf(left));
               return true;
            } else if (args.length >= 1) {
               (new RTPCountdown(this.plugin, player, args[0])).start();
               return true;
            } else {
               (new RTPGUI(this.plugin)).open(player);
               return true;
            }
         }
      } else {
         MessageUtil.send(sender, "player-only");
         return true;
      }
   }
}
