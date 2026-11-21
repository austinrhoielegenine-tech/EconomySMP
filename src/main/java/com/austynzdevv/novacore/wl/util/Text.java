package com.austynzdevv.novacore.wl.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public class Text {
   private static final LegacyComponentSerializer SER = LegacyComponentSerializer.legacyAmpersand();

   public static String legacy(String s) {
      return s == null ? "" : ChatColor.translateAlternateColorCodes('&', s);
   }

   public static Component color(String s) {
      return s == null ? Component.empty() : SER.deserialize(s);
   }
}
