package me.xyr0.economysmpcore.wl.listener;

import java.util.Iterator;
import java.util.Set;
import me.xyr0.economysmpcore.util.HotbarUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import me.xyr0.economysmpcore.wl.AstralWhitelister;
import me.xyr0.economysmpcore.wl.WhitelistSettings;
import me.xyr0.economysmpcore.wl.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandFilterListener implements Listener {
   private final AstralWhitelister plugin;

   public CommandFilterListener(AstralWhitelister plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onCmd(PlayerCommandPreprocessEvent e) {
      WhitelistSettings s = this.plugin.getSettings();
      if (s.isEnabled()) {
         Player p = e.getPlayer();
         if (!p.hasPermission("economysmpcore.system.whitelister.bypass")) {
            String msg = e.getMessage();
            if (msg != null && msg.length() >= 2) {
               String raw = msg.startsWith("/") ? msg.substring(1) : msg;
               String label = raw.split(" ")[0].toLowerCase();
               if (!s.canUse(p, label)) {
                  e.setCancelled(true);
                  Component comp = Text.color(s.getDenyMsg());
                  if (s.isChat()) {
                     p.sendMessage(comp);
                  }

                  if (s.isActionBar()) {
                     HotbarUtil.send(p, comp);
                  }

                  if (s.isSoundOn()) {
                     try {
                        SoundUtil.play(p, p.getLocation(), s.getSound(), 1.0F, 1.0F);
                     } catch (Exception var9) {
                     }
                  }

               }
            }
         }
      }
   }

   @EventHandler
   public void onTab(PlayerCommandSendEvent e) {
      WhitelistSettings s = this.plugin.getSettings();
      if (s.isEnabled()) {
         Player p = e.getPlayer();
         if (!p.hasPermission("economysmpcore.system.whitelister.bypass")) {
            Set<String> allowed = s.getAllowed(p);
            Iterator<String> it = e.getCommands().iterator();

            while(it.hasNext()) {
               String c = ((String)it.next()).toLowerCase();
               if (!allowed.contains(c)) {
                  it.remove();
               }
            }

         }
      }
   }
}
