package com.austynzdevv.economysmp.chat2.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.austynzdevv.economysmp.chat2.EconomyChat;
import com.austynzdevv.economysmp.chat2.ChatGameManager;
import com.austynzdevv.economysmp.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ChatGamesCommand implements CommandExecutor, TabCompleter {
   private final EconomyChat plugin;

   public ChatGamesCommand(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.chat.chatgames")) {
         sender.sendMessage(ColorUtil.colorComponent("&cNo permission."));
         return true;
      } else {
         ChatGameManager games = this.plugin.getChatGames();
         if (games == null) {
            sender.sendMessage(ColorUtil.colorComponent("&cChat games unavailable."));
            return true;
         } else if (args.length == 0) {
            sender.sendMessage(ColorUtil.colorComponent("&#00fc88/chatgames force"));
            sender.sendMessage(ColorUtil.colorComponent("&#00fc88/chatgames start"));
            sender.sendMessage(ColorUtil.colorComponent("&#00fc88/chatgames stop"));
            sender.sendMessage(ColorUtil.colorComponent("&#00fc88/chatgames reload"));
            return true;
         } else {
            switch (args[0].toLowerCase(Locale.ROOT)) {
               case "force":
                  if (games.isActiveRound()) {
                     sender.sendMessage(ColorUtil.colorComponent(games.getConfig().getString("messages.already-running", "&cAlready running.")));
                     return true;
                  }

                  games.forceGame();
                  sender.sendMessage(ColorUtil.colorComponent(games.getConfig().getString("messages.forced", "&aForced.")));
                  break;
               case "start":
                  games.startInterval();
                  games.forceGame();
                  sender.sendMessage(ColorUtil.colorComponent(games.getConfig().getString("messages.enabled-msg", "&aEnabled.")));
                  break;
               case "stop":
                  games.stopInterval();
                  sender.sendMessage(ColorUtil.colorComponent(games.getConfig().getString("messages.stopped", "&cStopped.")));
                  break;
               case "reload":
                  games.reload();
                  sender.sendMessage(ColorUtil.colorComponent("&aChat games reloaded."));
                  break;
               default:
                  sender.sendMessage(ColorUtil.colorComponent("&cUsage: /chatgames <force|start|stop|reload>"));
            }

            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      return args.length == 1 ? (List)List.of("force", "start", "stop", "reload").stream().filter((s) -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).collect(Collectors.toCollection(ArrayList::new)) : List.of();
   }
}
