package com.austynzdevv.nyrethacore.auction.util;

import java.util.List;
import java.util.UUID;
import com.austynzdevv.nyrethacore.auction.EconomyAuction;
import com.austynzdevv.nyrethacore.auction.gui.MenuSession;
import com.austynzdevv.nyrethacore.util.SignInput;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class InputUtil {
   private InputUtil() {
   }

   public static boolean useSign() {
      return EconomyAuction.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static boolean useChat() {
      return !useSign() && EconomyAuction.get().getConfig().getBoolean("chat-input.ChatAPI", true);
   }

   public static void requestSearch(Player player) {
      if (useSign()) {
         openSign(player, "search");
      } else {
         MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
         player.closeInventory();
         EconomyAuction.get().getMessages().send(player, "search");
      }
   }

   public static void openSign(Player player, String type) {
      List<String> lines = EconomyAuction.get().getConfig().getStringList("sign-gui." + type);
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Search   ", "");
      }

      MenuSession.SIGN_SEARCH.put(player.getUniqueId(), true);
      SignInput.open(EconomyAuction.get().getCore(), player, lines, true, () -> fallbackChat(player), 600L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
      EconomyAuction.get().getMessages().send(player, "search");
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
