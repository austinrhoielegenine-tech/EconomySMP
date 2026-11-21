package me.xyr0.astralsmpcore.auction.util;

import java.util.List;
import java.util.UUID;
import me.xyr0.astralsmpcore.auction.AstralAuction;
import me.xyr0.astralsmpcore.auction.gui.MenuSession;
import me.xyr0.astralsmpcore.util.SignInput;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class InputUtil {
   private InputUtil() {
   }

   public static boolean useSign() {
      return AstralAuction.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static boolean useChat() {
      return !useSign() && AstralAuction.get().getConfig().getBoolean("chat-input.ChatAPI", true);
   }

   public static void requestSearch(Player player) {
      if (useSign()) {
         openSign(player, "search");
      } else {
         MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
         player.closeInventory();
         AstralAuction.get().getMessages().send(player, "search");
      }
   }

   public static void openSign(Player player, String type) {
      List<String> lines = AstralAuction.get().getConfig().getStringList("sign-gui." + type);
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Search   ", "");
      }

      MenuSession.SIGN_SEARCH.put(player.getUniqueId(), true);
      SignInput.open(AstralAuction.get().getCore(), player, lines, true, () -> fallbackChat(player), 600L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
      AstralAuction.get().getMessages().send(player, "search");
   }

   public static void cleanup(Player player) {
      MenuSession.SIGN_SEARCH.remove(player.getUniqueId());
      SignInput.cleanup(player);
   }

   public static boolean isSignInput(UUID id) {
      return Boolean.TRUE.equals(MenuSession.SIGN_SEARCH.get(id));
   }

   public static boolean isProtectedSign(Location location) {
      return SignInput.isProtected(location);
   }
}
