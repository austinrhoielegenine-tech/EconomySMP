package me.xyr0.economysmpcore.lb.util;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.lb.AstralLeaderboards;
import me.xyr0.economysmpcore.lb.gui.MenuSession;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.SignInput;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class InputUtil {
   private static final Set<UUID> SIGN_WAIT = ConcurrentHashMap.newKeySet();

   private InputUtil() {
   }

   public static boolean useSign() {
      return AstralLeaderboards.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static void requestSearch(Player player) {
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SEARCH);
      if (useSign()) {
         openSign(player);
      } else {
         player.closeInventory();
         player.sendMessage(ColorUtil.parse(AstralLeaderboards.get().getMessages().get("chat-input", "&cType the name of the player you want to search for!")));
      }
   }

   public static void openSign(Player player) {
      List<String> lines = AstralLeaderboards.get().getConfig().getStringList("sign-gui");
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Player    ", "");
      }

      SIGN_WAIT.add(player.getUniqueId());
      SignInput.open(AstralLeaderboards.get().getCore(), player, lines, true, () -> fallbackChat(player), 600L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SEARCH);
      player.sendMessage(ColorUtil.parse(AstralLeaderboards.get().getMessages().get("chat-input", "&cType the name of the player you want to search for!")));
   }

   public static void cleanup(Player player) {
      if (player != null) {
         SIGN_WAIT.remove(player.getUniqueId());
      }

      SignInput.cleanup(player);
   }

   public static boolean isSignInput(UUID id) {
      return id != null && SIGN_WAIT.contains(id);
   }

   public static boolean isProtectedSign(Location location) {
      return SignInput.isProtected(location);
   }
}
