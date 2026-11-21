package me.xyr0.economysmpcore.rtpq.util;

import java.util.Locale;
import me.xyr0.economysmpcore.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {
   public static void play(Player player, String soundName, float volume, float pitch) {
      try {
         Sound sound = Sound.valueOf(soundName.toUpperCase(Locale.ROOT));
         player.stopSound(sound);
         SoundUtil.play(player, player.getLocation(), sound, volume, pitch);
      } catch (IllegalArgumentException var5) {
      }

   }
}
