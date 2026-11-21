package me.xyr0.economysmpcore.moderation.listener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import me.xyr0.economysmpcore.moderation.ModerationModule;
import me.xyr0.economysmpcore.util.Tasks;
import me.xyr0.economysmpcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class CommandLogListener implements Listener {
   private final ModerationModule module;

   public CommandLogListener(ModerationModule module) {
      this.module = module;
   }

   private FileConfiguration cfg() {
      return this.module.getConfig();
   }

   private boolean enabled() {
      FileConfiguration c = this.cfg();
      return c != null && c.getBoolean("command-logs.enabled", true);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onCommand(PlayerCommandPreprocessEvent event) {
      if (this.enabled()) {
         Player player = event.getPlayer();
         String msg = event.getMessage();
         if (msg != null && msg.length() >= 2) {
            String without = msg.substring(1).trim();
            String label = without.split("\\s+")[0].toLowerCase(Locale.ROOT);
            if (label.contains(":")) {
               label = label.substring(label.indexOf(58) + 1);
            }

            List<String> watched = this.cfg().getStringList("command-logs.watched-commands");
            boolean hit = false;

            for(String w : watched) {
               if (w != null) {
                  String wl = w.toLowerCase(Locale.ROOT);
                  if (wl.contains(":")) {
                     wl = wl.substring(wl.indexOf(58) + 1);
                  }

                  if (label.equals(wl) || without.toLowerCase(Locale.ROOT).startsWith(wl + " ")) {
                     hit = true;
                     break;
                  }
               }
            }

            if (hit) {
               String alert = this.cfg().getString("command-logs.alert-message", "&c[CmdLog] &f%player% &7ran &f/%command%").replace("%player%", player.getName()).replace("%command%", without);
               this.broadcast(alert);
               this.webhook("Command", player.getName(), "/" + without);
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onCreativeClick(InventoryCreativeEvent event) {
      if (this.enabled()) {
         if (this.cfg().getBoolean("command-logs.creative-item-alerts", true)) {
            HumanEntity var3 = event.getWhoClicked();
            if (var3 instanceof Player) {
               Player player = (Player)var3;
               if (player.getGameMode() == GameMode.CREATIVE) {
                  ItemStack cur = event.getCursor();
                  if (cur != null && !cur.getType().isAir()) {
                     String var10000 = this.cfg().getString("command-logs.creative-alert-message", "&c[CmdLog] &f%player% &7took &f%item% &7from creative").replace("%player%", player.getName());
                     int var10002 = cur.getAmount();
                     String alert = var10000.replace("%item%", var10002 + "x " + cur.getType().name());
                     this.broadcast(alert);
                     String var6 = player.getName();
                     int var10003 = cur.getAmount();
                     this.webhook("Creative", var6, var10003 + "x " + cur.getType().name());
                  }
               }
            }
         }
      }
   }

   private void broadcast(String message) {
      String perm = this.cfg().getString("command-logs.alert-permission", "economysmpcore.system.moderation.commandlog");

      for(Player p : Bukkit.getOnlinePlayers()) {
         if (p.hasPermission(perm) || p.isOp()) {
            TextUtil.send(p, message);
         }
      }

      Bukkit.getConsoleSender().sendMessage(message.replace('&', '§'));
   }

   private void webhook(String type, String player, String detail) {
      String url = this.cfg().getString("command-logs.discord-webhook", "");
      if (url != null && !url.isBlank()) {
         Tasks.runAsync(this.module.getPlugin(), () -> {
            try {
               String var10000 = esc(type);
               String json = "{\"embeds\":[{\"title\":\"Command Log\",\"color\":16711680,\"fields\":[{\"name\":\"Type\",\"value\":\"" + var10000 + "\",\"inline\":true},{\"name\":\"Player\",\"value\":\"" + esc(player) + "\",\"inline\":true},{\"name\":\"Detail\",\"value\":\"" + esc(detail) + "\",\"inline\":false}]}]}";
               HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
               con.setRequestMethod("POST");
               con.setDoOutput(true);
               con.setRequestProperty("Content-Type", "application/json");
               byte[] data = json.getBytes(StandardCharsets.UTF_8);
               con.setFixedLengthStreamingMode(data.length);
               OutputStream os = con.getOutputStream();

               try {
                  os.write(data);
               } catch (Throwable var11) {
                  if (os != null) {
                     try {
                        os.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (os != null) {
                  os.close();
               }

               con.getInputStream().close();
               con.disconnect();
            } catch (Exception var12) {
            }

         });
      }
   }

   private static String esc(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
   }
}
