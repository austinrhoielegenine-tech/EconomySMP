package com.austynzdevv.novacore.media.command;

import com.austynzdevv.novacore.media.gui.MediaGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MediaCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         MediaGUI.open(player);
         return true;
      } else {
         return true;
      }
   }
}
