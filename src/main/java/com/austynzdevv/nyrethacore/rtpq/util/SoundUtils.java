package com.austynzdevv.nyrethacore.rtpq.util;

import java.util.Locale;
import com.austynzdevv.nyrethacore.util.SoundUtil;
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
