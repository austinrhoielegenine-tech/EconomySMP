package me.xyr0.astralsmpcore.sell.util;

import java.util.List;
import java.util.UUID;
import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.sell.gui.MenuSession;
import me.xyr0.astralsmpcore.util.SignInput;
import org.bukkit.entity.Player;

public final class InputUtil {
   private InputUtil() {
   }

   public static boolean useSign() {
      return AstralSell.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static void requestSearch(Player player) {
      if (useSign()) {
         openSign(player, "search");
      } else {
         MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
         player.closeInventory();
         AstralSell.get().getMessages().send(player, "worth-search");
      }
   }

   public static void openSign(Player player, String type) {
      List<String> lines = AstralSell.get().getConfig().getStringList("sign-gui." + type);
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Search   ", "");
      }

      MenuSession.SIGN_SEARCH.put(player.getUniqueId(), true);
      SignInput.open(AstralSell.get().getCore(), player, lines, false, () -> fallbackChat(player), 0L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.CHAT_SEARCH.put(player.getUniqueId(), true);
      AstralSell.get().getMessages().send(player, "worth-search");
   }

   public static void cleanup(Player player) {
      MenuSession.SIGN_SEARCH.remove(player.getUniqueId());
      SignInput.cleanup(player);
   }

   public static boolean isSignInput(UUID id) {
      return Boolean.TRUE.equals(MenuSession.SIGN_SEARCH.get(id));
   }
}
