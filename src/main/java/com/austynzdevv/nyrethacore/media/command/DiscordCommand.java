package com.austynzdevv.nyrethacore.media.command;

import com.austynzdevv.nyrethacore.media.EconomyMedia;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         return true;
      } else {
         EconomyMedia plugin = EconomyMedia.getInstance();
         if (plugin == null) {
            return true;
         } else {
            for(String message : plugin.getConfig().getStringList("discord.message")) {
               player.sendMessage(ColorUtil.color(message));
            }

            String soundName = plugin.getConfig().getString("discord.sound", "minecraft:block.note_block.pling");

            try {
               Sound sound = Sound.valueOf(soundName.replace("minecraft:", "").toUpperCase().replace(".", "_"));
               SoundUtil.play(player, player.getLocation(), sound, 1.0F, 1.0F);
            } catch (Exception var9) {
            }

            return true;
         }
      }
   }
}
