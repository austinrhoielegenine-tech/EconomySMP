package com.austynzdevv.novacore.chat2.listener;

import com.austynzdevv.novacore.NovaCore;
import com.austynzdevv.novacore.Module;
import com.austynzdevv.novacore.SettingsModule;
import com.austynzdevv.novacore.chat2.EconomyChat;
import com.austynzdevv.novacore.chat2.ChatManager;
import com.austynzdevv.novacore.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatToggleListener implements Listener {
   private final EconomyChat plugin;
   private final ChatManager chatManager;

   public ChatToggleListener(EconomyChat plugin) {
      this(plugin, (ChatManager)null);
   }

   public ChatToggleListener(EconomyChat plugin, ChatManager chatManager) {
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
            if (var4 instanceof NovaCore) {
               NovaCore core = (NovaCore)var4;
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
