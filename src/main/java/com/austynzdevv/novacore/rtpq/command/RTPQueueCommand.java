package com.austynzdevv.novacore.rtpq.command;

import com.austynzdevv.novacore.rtpq.EconomyRTPQ;
import com.austynzdevv.novacore.rtpq.QueueManager;
import com.austynzdevv.novacore.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPQueueCommand implements CommandExecutor {
   private final EconomyRTPQ plugin;

   public RTPQueueCommand(EconomyRTPQ plugin) {
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
