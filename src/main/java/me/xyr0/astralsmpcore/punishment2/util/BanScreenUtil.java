package me.xyr0.astralsmpcore.punishment2.util;

import me.xyr0.astralsmpcore.punishment2.AstralPunishment;
import me.xyr0.astralsmpcore.punishment2.Punishment;
import me.xyr0.astralsmpcore.util.ColorUtil;

public class BanScreenUtil {
   public static String create(AstralPunishment plugin, Punishment punishment) {
      StringBuilder screen = new StringBuilder();
      screen.append(ColorUtil.color(plugin.getMessages().getString("ban.screen.title", "&7Connection Lost")));

      for(String line : plugin.getMessages().getStringList("ban.screen.lines")) {
         screen.append("\n").append(ColorUtil.color(line.replace("{reason}", punishment.getReason()).replace("{time}", punishment.getDuration()).replace("{banid}", punishment.getBanId()).replace("{discord}", plugin.getConfig().getString("discord", "discord.gg/yourserver"))));
      }

      return screen.toString();
   }
}
