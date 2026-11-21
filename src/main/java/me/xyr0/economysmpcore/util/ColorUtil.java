package me.xyr0.economysmpcore.util;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class ColorUtil {
   private static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializer.builder().hexColors().character('&').hexCharacter('#').build();
   private static final LegacyComponentSerializer SECTION = LegacyComponentSerializer.legacySection();
   private static final MiniMessage MINI = MiniMessage.miniMessage();
   private static final Pattern HEX = Pattern.compile("(?i)&#([0-9a-f]{6})");
   private static final Pattern BARE_HEX = Pattern.compile("(?i)(?<![&<])#([0-9a-f]{6})");
   private static final Pattern LEGACY_HEX = Pattern.compile("(?i)&x(?:&[0-9a-f]){6}");
   private static final Pattern LEGACY_CODE = Pattern.compile("(?i)&([0-9a-fk-or])");
   private static final Pattern HEX_LEGACY = Pattern.compile("(?i)§x(?:§[0-9a-f]){6}");
   private static final Map<String, String> MINI_CODES = Map.ofEntries(Map.entry("0", "black"), Map.entry("1", "dark_blue"), Map.entry("2", "dark_green"), Map.entry("3", "dark_aqua"), Map.entry("4", "dark_red"), Map.entry("5", "dark_purple"), Map.entry("6", "gold"), Map.entry("7", "gray"), Map.entry("8", "dark_gray"), Map.entry("9", "blue"), Map.entry("a", "green"), Map.entry("b", "aqua"), Map.entry("c", "red"), Map.entry("d", "light_purple"), Map.entry("e", "yellow"), Map.entry("f", "white"), Map.entry("k", "obfuscated"), Map.entry("l", "bold"), Map.entry("m", "strikethrough"), Map.entry("n", "underlined"), Map.entry("o", "italic"), Map.entry("r", "reset"));

   private ColorUtil() {
   }

   public static Component parse(String text) {
      if (text != null && !text.isEmpty()) {
         String input = text.replace('§', '&');
         if (hasMiniMessageTags(input)) {
            return MINI.deserialize(toMiniMessage(input)).decoration(TextDecoration.ITALIC, false);
         } else {
            Matcher legacyHex = LEGACY_HEX.matcher(input);
            StringBuffer converted = new StringBuffer();

            while(legacyHex.find()) {
               String hex = legacyHex.group().replaceAll("(?i)&x|&", "");
               legacyHex.appendReplacement(converted, Matcher.quoteReplacement("&#" + hex));
            }

            legacyHex.appendTail(converted);
            input = BARE_HEX.matcher(converted.toString()).replaceAll("&#$1");
            return AMPERSAND.deserialize(input).decoration(TextDecoration.ITALIC, false);
         }
      } else {
         return Component.empty();
      }
   }

   public static Component colorComponent(String text) {
      return (Component)(text != null && !text.isEmpty() ? parse(text).decoration(TextDecoration.ITALIC, false) : Component.empty());
   }

   public static Component legacyComponent(String text) {
      return text != null && !text.isEmpty() ? AMPERSAND.deserialize(text.replace('§', '&')) : Component.empty();
   }

   public static String color(String text) {
      return text != null && !text.isEmpty() ? SECTION.serialize(parse(text)) : "";
   }

   public static Component component(String text) {
      return (Component)(text != null && !text.isEmpty() ? Component.text(text).color(TextColor.color(1295349)) : Component.empty());
   }

   public static String strip(String text) {
      return plain(text);
   }

   public static String plain(String text) {
      if (text != null && !text.isEmpty()) {
         String result = HEX.matcher(text).replaceAll("");
         result = BARE_HEX.matcher(result).replaceAll("");
         result = HEX_LEGACY.matcher(result).replaceAll("");
         return Pattern.compile("(?i)[§&][0-9a-fk-orx]").matcher(result).replaceAll("");
      } else {
         return "";
      }
   }

   public static String plain(Component component) {
      return component == null ? "" : PlainTextComponentSerializer.plainText().serialize(component);
   }

   private static boolean hasMiniMessageTags(String text) {
      return text.matches("(?s).*(?:<#[0-9a-fA-F]{6}>|</?(?:click|hover|gradient|rainbow|bold|italic|underlined|strikethrough|obfuscated|reset|newline|insertion|font|color|black|dark_blue|dark_green|dark_aqua|dark_red|dark_purple|gold|gray|dark_gray|blue|green|aqua|red|light_purple|yellow|white)(?::[^>]*)?>).*");
   }

   private static String toMiniMessage(String input) {
      String result = input.replaceAll("(?i)</#[0-9a-f]{6}>", "");
      Matcher legacyHex = LEGACY_HEX.matcher(result);
      StringBuffer converted = new StringBuffer();

      while(legacyHex.find()) {
         String hex = legacyHex.group().replaceAll("(?i)&x|&", "");
         legacyHex.appendReplacement(converted, Matcher.quoteReplacement("<#" + hex + ">"));
      }

      legacyHex.appendTail(converted);
      result = converted.toString();
      Matcher hex = BARE_HEX.matcher(result);
      converted = new StringBuffer();

      while(hex.find()) {
         hex.appendReplacement(converted, Matcher.quoteReplacement("<#" + hex.group(1) + ">"));
      }

      hex.appendTail(converted);
      result = HEX.matcher(converted.toString()).replaceAll("<#$1>");
      Matcher codes = LEGACY_CODE.matcher(result);
      converted = new StringBuffer();

      while(codes.find()) {
         String tag = (String)MINI_CODES.get(codes.group(1).toLowerCase(Locale.ROOT));
         codes.appendReplacement(converted, Matcher.quoteReplacement("<" + tag + ">"));
      }

      codes.appendTail(converted);
      return converted.toString();
   }
}
