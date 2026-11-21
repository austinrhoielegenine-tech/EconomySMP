package com.austynzdevv.novacore.punishment2.util;

import com.austynzdevv.novacore.punishment2.EconomyPunishment;
import com.austynzdevv.novacore.punishment2.Punishment;
import com.austynzdevv.novacore.util.ColorUtil;

public class BanScreenUtil {
   public static String create(EconomyPunishment plugin, Punishment punishment) {
      StringBuilder screen = new StringBuilder();
      screen.append(ColorUtil.color(plugin.getMessages().getString("ban.screen.title", "&7Connection Lost")));

      for(String line : plugin.getMessages().getStringList("ban.screen.lines")) {
         screen.append("\n").append(ColorUtil.color(line.replace("{reason}", punishment.getReason()).replace("{time}", punishment.getDuration()).replace("{banid}", punishment.getBanId()).replace("{discord}", plugin.getConfig().getString("discord", "discord.gg/yourserver"))));
      }

      return screen.toString();
   }
}
