package com.austynzdevv.nyrethacore.baltop.util;

import java.util.List;
import java.util.UUID;
import com.austynzdevv.nyrethacore.baltop.EconomyBaltop;
import com.austynzdevv.nyrethacore.baltop.gui.MenuSession;
import com.austynzdevv.nyrethacore.util.SignInput;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class InputUtil {
   private InputUtil() {
   }

   public static boolean useSign() {
      return EconomyBaltop.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static boolean useChat() {
      return !useSign() && EconomyBaltop.get().getConfig().getBoolean("chat-input.ChatAPI", true);
   }

   public static void requestSearch(Player player) {
      if (useSign()) {
         openSign(player);
      } else {
         MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
         player.closeInventory();
         EconomyBaltop.get().getMessages().send(player, "chat-input");
      }
   }

   public static void openSign(Player player) {
      List<String> lines = EconomyBaltop.get().getConfig().getStringList("sign-gui");
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Player    ", "");
      }

      MenuSession.SIGN_SEARCH.put(player.getUniqueId(), true);
      SignInput.open(EconomyBaltop.get().getCore(), player, lines, true, () -> fallbackChat(player), 0L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
      EconomyBaltop.get().getMessages().send(player, "chat-input");
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
