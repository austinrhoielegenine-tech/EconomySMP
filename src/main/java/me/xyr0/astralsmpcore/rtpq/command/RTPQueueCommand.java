package me.xyr0.astralsmpcore.rtpq.command;

import me.xyr0.astralsmpcore.rtpq.AstralRTPQ;
import me.xyr0.astralsmpcore.rtpq.QueueManager;
import me.xyr0.astralsmpcore.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPQueueCommand implements CommandExecutor {
   private final AstralRTPQ plugin;

   public RTPQueueCommand(AstralRTPQ plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         QueueManager qm = this.plugin.getQueueManager();
         if (qm.isQueued(player)) {
            qm.removePlayer(player);
            TextUtil.send(player, this.plugin.getConfig().getString("messages.left", "&7You left the RTP queue."));
            TextUtil.actionBar(player, this.plugin.getConfig().getString("messages.left-bar", "&7Left queue"));
            return true;
         } else {
            qm.addPlayer(player, "world");
            TextUtil.send(player, this.plugin.getConfig().getString("messages.joined", "&7You joined the &#37BFF9RTP queue&7. Waiting for an opponent..."));
            TextUtil.actionBar(player, this.plugin.getConfig().getString("messages.joined-bar", "&7Searching..."));
            return true;
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }
}
