package me.xyr0.economysmpcore.chat2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.xyr0.economysmpcore.EconomySMPcore;
import me.xyr0.economysmpcore.Module;
import me.xyr0.economysmpcore.SettingsModule;
import me.xyr0.economysmpcore.chat2.AstralChat;
import me.xyr0.economysmpcore.chat2.ChatManager;
import me.xyr0.economysmpcore.chat2.ChatMessageProcessor;
import me.xyr0.economysmpcore.chat2.HoverManager;
import me.xyr0.economysmpcore.chat2.placeholders.PlaceholderUtil;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener {
   private final AstralChat plugin;
   private final ChatMessageProcessor messageProcessor;

   public ChatListener(AstralChat plugin) {
      this.plugin = plugin;
      this.messageProcessor = new ChatMessageProcessor(plugin);
   }

   private boolean isChatOff(Player player) {
      try {
         ChatManager cm = this.plugin.getChatManager();
         if (cm != null && cm.isPersonalOff(player.getUniqueId())) {
            return true;
         }
      } catch (Exception var6) {
      }

      try {
         JavaPlugin var3 = this.plugin.getCore();
         if (var3 instanceof EconomySMPcore core) {
            Module mod = core.modules().get("settings");
            if (mod instanceof SettingsModule sm) {
               if (sm.isChatOff(player.getUniqueId())) {
                  return true;
               }
            }
         }
      } catch (Exception var5) {
      }

      return false;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onChatEarly(AsyncChatEvent event) {
      Player player = event.getPlayer();
      if (this.isChatOff(player)) {
         event.setCancelled(true);
         TextUtil.send(player, "&#fc0404You have general chat disabled. &7Use &#00a3fb/chattoggle&7 or /settings.");
      } else {
         event.viewers().removeIf((audience) -> {
            if (audience instanceof Player viewer) {
               return this.isChatOff(viewer);
            } else {
               return false;
            }
         });
      }
   }

   @EventHandler
   public void onChat(AsyncChatEvent event) {
      if (!event.isCancelled()) {
         if (this.plugin.getConfigManager().isModuleEnabled("chat-format")) {
            Player player = event.getPlayer();
            String rawMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
            Component messageComponent = this.messageProcessor.processMessage(player, rawMessage);
            String format = this.plugin.getConfig().getString("chat-format.format", "%player%: %message%");
            format = PlaceholderUtil.replace(player, format);
            format = format.replace("%player%", player.getName());
            int msgIndex = format.indexOf("%message%");
            Component full;
            if (msgIndex == -1) {
               Component prefixComp = ColorUtil.colorComponent(format);
               if (this.plugin.getConfigManager().isModuleEnabled("hover-chat")) {
                  prefixComp = HoverManager.applyHover(player, prefixComp);
               }

               full = prefixComp.append(messageComponent);
            } else {
               String prefix = format.substring(0, msgIndex);
               String suffix = format.substring(msgIndex + "%message%".length());
               Component prefixComp = ColorUtil.colorComponent(prefix);
               Component suffixComp = ColorUtil.colorComponent(suffix);
               if (this.plugin.getConfigManager().isModuleEnabled("hover-chat")) {
                  prefixComp = HoverManager.applyHover(player, prefixComp);
               }

               String colorCarry = lastLegacyColor(prefix);
               Component msgColored = messageComponent;
               if (!colorCarry.isEmpty()) {
                  String plain = PlainTextComponentSerializer.plainText().serialize(messageComponent);
                  msgColored = ColorUtil.colorComponent(colorCarry + plain);
               }

               full = prefixComp.append(msgColored).append(suffixComp);
            }

            event.renderer((source, sourceDisplayName, originalMessage, viewer) -> full);
         }
      }
   }

   private static String lastLegacyColor(String legacy) {
      if (legacy != null && !legacy.isEmpty()) {
         Matcher hex = Pattern.compile("(?i)&#([0-9a-f]{6})").matcher(legacy);

         String found;
         for(found = null; hex.find(); found = "&#" + hex.group(1)) {
         }

         if (found != null) {
            return found;
         } else {
            for(Matcher x = Pattern.compile("(?i)&x((&[0-9a-f]){6})").matcher(legacy); x.find(); found = "&x" + x.group(1)) {
            }

            if (found != null) {
               return found;
            } else {
               for(Matcher c = Pattern.compile("(?i)&([0-9a-fk-or])").matcher(legacy); c.find(); found = "&" + c.group(1)) {
               }

               return found == null ? "" : found;
            }
         }
      } else {
         return "";
      }
   }
}
