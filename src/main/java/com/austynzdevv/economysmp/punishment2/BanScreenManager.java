package com.austynzdevv.economysmp.punishment2;

import com.austynzdevv.economysmp.punishment2.util.TimeUtil;
import com.austynzdevv.economysmp.util.ColorUtil;

public class BanScreenManager {
   private final EconomyPunishment plugin;

   public BanScreenManager(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public String createScreen(Punishment punishment) {
      StringBuilder screen = new StringBuilder();
      String title = this.plugin.getMessages().getString("ban.screen.title", "&7Connection Lost");
      screen.append(ColorUtil.color(title));

      for(String line : this.plugin.getMessages().getStringList("ban.screen.lines")) {
         screen.append("\n").append(ColorUtil.color(this.replace(line, punishment)));
      }

      return screen.toString();
   }

   private String replace(String text, Punishment punishment) {
      String type = punishment.getType() == null ? "BAN" : punishment.getType().toUpperCase();
      String typeWord;
      if (type.contains("IP") && type.contains("BAN")) {
         typeWord = "IP-Banned";
      } else if (type.contains("IP") && type.contains("MUTE")) {
         typeWord = "IP-Muted";
      } else if (type.contains("MUTE")) {
         typeWord = "muted";
      } else {
         typeWord = "banned";
      }

      long exp = punishment.getExpireTime();
      String timeText;
      if (exp <= 0L) {
         timeText = punishment.getDuration() != null ? punishment.getDuration() : "Permanent";
         if (timeText.equalsIgnoreCase("permanent")) {
            timeText = "Permanent";
         }
      } else {
         long left = exp - System.currentTimeMillis();
         timeText = left <= 0L ? "Expired" : TimeUtil.formatTime(left);
      }

      return text.replace("{type}", typeWord).replace("{reason}", this.safe(punishment.getReason())).replace("{time}", timeText).replace("{banid}", this.safe(punishment.getBanId())).replace("{discord}", this.plugin.getConfig().getString("discord", "discord.gg/yourserver"));
   }

   private String safe(String s) {
      return s == null ? "" : s;
   }
}
