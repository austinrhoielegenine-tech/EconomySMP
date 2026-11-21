package me.xyr0.astralsmpcore.media.command;

import me.xyr0.astralsmpcore.media.AstralMedia;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StoreCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!(sender instanceof Player player)) {
         return true;
      } else {
         AstralMedia plugin = AstralMedia.getInstance();
         if (plugin == null) {
            return true;
         } else {
            for(String message : plugin.getConfig().getStringList("store.message")) {
               player.sendMessage(ColorUtil.color(message));
            }

            String soundName = plugin.getConfig().getString("store.sound", "minecraft:block.note_block.pling");

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
