package me.xyr0.astralsmpcore.util;

import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TextUtil {
   private TextUtil() {
   }

   public static String color(String input) {
      return ColorUtil.color(input);
   }

   public static String replace(String input, String... replacements) {
      if (input != null && replacements != null) {
         String result = input;

         for(int i = 0; i + 1 < replacements.length; i += 2) {
            String key = replacements[i];
            String value = replacements[i + 1];
            if (key != null && value != null) {
               result = result.replace(key, value);
            }
         }

         return result;
      } else {
         return input == null ? "" : input;
      }
   }

   public static Component component(String input) {
      return ColorUtil.parse(input).decoration(TextDecoration.ITALIC, false);
   }

   public static List<Component> componentList(List<String> lines) {
      List<Component> out = new ArrayList();
      if (lines == null) {
         return out;
      } else {
         for(String line : lines) {
            out.add(component(line));
         }

         return out;
      }
   }

   public static void send(CommandSender sender, String msg) {
      if (sender != null && msg != null) {
         sender.sendMessage(component(msg));
      }
   }

   public static void actionBar(Player player, String msg) {
      if (player != null && msg != null) {
         HotbarUtil.send(player, component(msg));
      }
   }
}
