package com.austynzdevv.novacore.sell.util;

import java.util.List;
import java.util.UUID;
import com.austynzdevv.novacore.sell.EconomySell;
import com.austynzdevv.novacore.sell.gui.MenuSession;
import com.austynzdevv.novacore.util.SignInput;
import org.bukkit.entity.Player;

public final class InputUtil {
   private InputUtil() {
   }

   public static boolean useSign() {
      return EconomySell.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static void requestSearch(Player player) {
      if (useSign()) {
         openSign(player, "search");
      } else {
         MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
         player.closeInventory();
         EconomySell.get().getMessages().send(player, "worth-search");
      }
   }

   public static void openSign(Player player, String type) {
      List<String> lines = EconomySell.get().getConfig().getStringList("sign-gui." + type);
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Search   ", "");
      }

      MenuSession.SIGN_SEARCH.put(player.getUniqueId(), true);
      SignInput.open(EconomySell.get().getCore(), player, lines, false, () -> fallbackChat(player), 0L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
      EconomySell.get().getMessages().send(player, "worth-search");
   }

   public static void cleanup(Player player) {
      MenuSession.SIGN_SEARCH.remove(player.getUniqueId());
      SignInput.cleanup(player);
   }

   public static boolean isSignInput(UUID id) {
      return Boolean.TRUE.equals(MenuSession.SIGN_SEARCH.get(id));
   }
}
