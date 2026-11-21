package com.austynzdevv.novacore.punishment2.listener;

import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Set;
import com.austynzdevv.novacore.punishment2.EconomyPunishment;
import com.austynzdevv.novacore.punishment2.Punishment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class IPBanListener implements Listener {
   private final EconomyPunishment plugin;

   public IPBanListener(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onLogin(PlayerLoginEvent event) {
      if (!event.getPlayer().hasPermission("novacorecore.system.punishment.bypass")) {
         Punishment ban = this.findBan(this.collectIps(event));
         if (ban != null) {
            event.disallow(Result.KICK_OTHER, this.plugin.getBanScreenManager().createScreen(ban));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (!player.hasPermission("novacorecore.system.punishment.bypass")) {
         Set<String> ips = new LinkedHashSet();

         try {
            if (player.getAddress() != null && player.getAddress().getAddress() != null) {
               ips.add(player.getAddress().getAddress().getHostAddress());
            }
         } catch (Exception var5) {
         }

         Punishment ban = this.findBan(ips);
         if (ban != null) {
            player.kickPlayer(this.plugin.getBanScreenManager().createScreen(ban));
         }
      }
   }

   private Set<String> collectIps(PlayerLoginEvent event) {
      Set<String> ips = new LinkedHashSet();

      try {
         if (event.getAddress() != null) {
            ips.add(event.getAddress().getHostAddress());
         }
      } catch (Exception var5) {
      }

      try {
         InetAddress real = event.getRealAddress();
         if (real != null) {
            ips.add(real.getHostAddress());
         }
      } catch (Exception var4) {
      }

      return ips;
   }

   private Punishment findBan(Set<String> ips) {
      for(String ip : ips) {
         if (ip != null && !ip.isEmpty()) {
            Punishment p = this.plugin.getIpBanManager().getIPBanPunishment(ip);
            if (p != null) {
               return p;
            }
         }
      }

      return null;
   }
}
