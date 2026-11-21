package com.austynzdevv.economysmp.util;

import java.util.Locale;
import com.austynzdevv.economysmp.SettingsModule;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SoundUtil {
   private SoundUtil() {
   }

   public static void playConfigured(Player player, Plugin plugin, String path) {
      if (player != null && plugin != null && path != null && !path.isBlank()) {
         FileConfiguration config = plugin.getConfig();
         boolean section = config.isConfigurationSection(path) || config.contains(path + ".enabled");
         if (!section || config.getBoolean(path + ".enabled", true)) {
            String name = section ? config.getString(path + ".sound", "UI_BUTTON_CLICK") : config.getString(path, "UI_BUTTON_CLICK");
            if (name != null && !name.isBlank()) {
               float volume = section ? (float)config.getDouble(path + ".volume", (double)1.0F) : 1.0F;
               float pitch = section ? (float)config.getDouble(path + ".pitch", (double)1.0F) : 1.0F;

               Sound sound;
               try {
                  sound = Sound.valueOf(name.trim().toUpperCase(Locale.ROOT).replace('.', '_').replace('-', '_').replace(' ', '_'));
               } catch (IllegalArgumentException var10) {
                  sound = Sound.UI_BUTTON_CLICK;
               }

               play(player, player.getLocation(), sound, volume, pitch);
            }
         }
      }
   }

   public static void play(Player player, Location location, Sound sound, float volume, float pitch) {
      if (player != null && location != null && sound != null) {
         try {
            SettingsModule settings = SettingsModule.get();
            if (settings != null && settings.isSoundsOff(player.getUniqueId())) {
               return;
            }
         } catch (Exception var6) {
         }

         player.playSound(location, sound, volume, pitch);
      }
   }
}
