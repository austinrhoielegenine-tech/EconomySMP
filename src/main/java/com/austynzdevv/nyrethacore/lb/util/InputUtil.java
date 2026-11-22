package com.austynzdevv.nyrethacore.lb.util;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.lb.EconomyLeaderboards;
import com.austynzdevv.nyrethacore.lb.gui.MenuSession;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.SignInput;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class InputUtil {
   private static final Set<UUID> SIGN_WAIT = ConcurrentHashMap.newKeySet();

   private InputUtil() {
   }

   public static boolean useSign() {
      return EconomyLeaderboards.get().getConfig().getBoolean("chat-input.SignAPI", true);
   }

   public static void requestSearch(Player player) {
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SEARCH);
      if (useSign()) {
         openSign(player);
      } else {
         player.closeInventory();
         player.sendMessage(ColorUtil.parse(EconomyLeaderboards.get().getMessages().get("chat-input", "&cType the name of the player you want to search for!")));
      }
   }

   public static void openSign(Player player) {
      List<String> lines = EconomyLeaderboards.get().getConfig().getStringList("sign-gui");
      if (lines == null || lines.isEmpty()) {
         lines = List.of("", "^^^^^^^^^^^^^^^", "   Player    ", "");
      }

      SIGN_WAIT.add(player.getUniqueId());
      SignInput.open(EconomyLeaderboards.get().getCore(), player, lines, true, () -> fallbackChat(player), 600L, () -> cleanup(player));
   }

   private static void fallbackChat(Player player) {
      cleanup(player);
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SEARCH);
      player.sendMessage(ColorUtil.parse(EconomyLeaderboards.get().getMessages().get("chat-input", "&cType the name of the player you want to search for!")));
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
