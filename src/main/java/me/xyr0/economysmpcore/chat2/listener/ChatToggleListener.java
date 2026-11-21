package me.xyr0.economysmpcore.chat2.listener;

import me.xyr0.economysmpcore.EconomySMPcore;
import me.xyr0.economysmpcore.Module;
import me.xyr0.economysmpcore.SettingsModule;
import me.xyr0.economysmpcore.chat2.AstralChat;
import me.xyr0.economysmpcore.chat2.ChatManager;
import me.xyr0.economysmpcore.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatToggleListener implements Listener {
   private final AstralChat plugin;
   private final ChatManager chatManager;

   public ChatToggleListener(AstralChat plugin) {
      this(plugin, (ChatManager)null);
   }

   public ChatToggleListener(AstralChat plugin, ChatManager chatManager) {
      this.plugin = plugin;
      this.chatManager = chatManager;
   }

   private boolean isOff(Player player) {
      ChatManager cm = this.chatManager != null ? this.chatManager : this.plugin.getChatManager();
      if (cm != null && cm.isPersonalOff(player.getUniqueId())) {
         return true;
      } else {
         try {
            JavaPlugin var4 = this.plugin.getCore();
            if (var4 instanceof EconomySMPcore) {
               EconomySMPcore core = (EconomySMPcore)var4;
               Module mod = core.modules().get("settings");
               if (mod instanceof SettingsModule) {
                  SettingsModule sm = (SettingsModule)mod;
                  if (sm.isChatOff(player.getUniqueId())) {
                     return true;
                  }
               }
            }
         } catch (Exception var6) {
         }

         return false;
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (this.isOff(player)) {
         event.setCancelled(true);
         TextUtil.send(player, "&#fc0404You have general chat disabled. &7Use &#00a3fb/chattoggle&7 or /settings.");
      } else {
         event.getRecipients().removeIf((viewer) -> {
            try {
               return this.isOff(viewer);
            } catch (Exception var3) {
               return false;
            }
         });
      }
   }
}
