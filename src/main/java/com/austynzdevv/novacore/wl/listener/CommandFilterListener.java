package com.austynzdevv.novacore.wl.listener;

import java.util.Iterator;
import java.util.Set;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.wl.EconomyWhitelister;
import com.austynzdevv.novacore.wl.WhitelistSettings;
import com.austynzdevv.novacore.wl.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandFilterListener implements Listener {
   private final EconomyWhitelister plugin;

   public CommandFilterListener(EconomyWhitelister plugin) {
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
         if (!p.hasPermission("novacorecore.system.whitelister.bypass")) {
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
         if (!p.hasPermission("novacorecore.system.whitelister.bypass")) {
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
