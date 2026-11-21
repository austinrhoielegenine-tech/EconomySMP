package me.xyr0.astralsmpcore.punishment2;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class WebhookManager {
   private final AstralPunishment plugin;
   private FileConfiguration webhook;

   public WebhookManager(AstralPunishment plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "webhook.yml");
      this.webhook = YamlConfiguration.loadConfiguration(file);
   }

   public void sendPunishment(String type, Punishment punishment) {
      this.send(type, punishment, (String)null, (String)null);
   }

   public void sendIPPunishment(String type, Punishment punishment, String ip) {
      String key = type.equalsIgnoreCase("BAN") ? "ipban" : "ipmute";
      this.send(key, punishment, (String)null, ip);
   }

   public void sendUnoffend(Punishment punishment, String staff) {
      this.send("unoffend", punishment, staff, (String)null);
   }

   private void send(String type, Punishment punishment, String overrideStaff, String ip) {
      if (this.webhook.getBoolean("enabled", false)) {
         String url = this.webhook.getString("url");
         if (url != null && !url.isEmpty()) {
            String embedKey = type.toLowerCase().replace("_", "").replace("-", "");
            if (this.webhook.getBoolean("embeds." + embedKey + ".enabled", true)) {
               Tasks.runAsync(this.plugin.getPlugin(), () -> {
                  HttpURLConnection connection = null;

                  try {
                     connection = (HttpURLConnection)(new URL(url)).openConnection();
                     connection.setRequestMethod("POST");
                     connection.setRequestProperty("Content-Type", "application/json");
                     connection.setConnectTimeout(5000);
                     connection.setReadTimeout(5000);
                     connection.setDoOutput(true);
                     String path = "embeds." + embedKey + ".";
                     String title = this.webhook.getString(path + "title", "Punishment");
                     List<String> fields = this.webhook.getStringList(path + "fields");
                     StringBuilder description = new StringBuilder();

                     for(String field : fields) {
                        description.append(this.replace(field, punishment, overrideStaff, ip)).append("\n");
                     }

                     String var10000 = this.escape(this.webhook.getString("username", "AstralPunishment"));
                     String json = "{\"username\":\"" + var10000 + "\",\"embeds\":[{\"title\":\"" + this.escape(title) + "\",\"description\":\"" + this.escape(description.toString()) + "\",\"color\":" + this.color(this.webhook.getString(path + "color", "#ff3333")) + "}]}";
                     OutputStream out = connection.getOutputStream();

                     try {
                        out.write(json.getBytes(StandardCharsets.UTF_8));
                     } catch (Throwable var21) {
                        if (out != null) {
                           try {
                              out.close();
                           } catch (Throwable var20) {
                              var21.addSuppressed(var20);
                           }
                        }

                        throw var21;
                     }

                     if (out != null) {
                        out.close();
                     }

                     int response = connection.getResponseCode();
                     if (response != 204 && response != 200) {
                        this.plugin.getLogger().warning("Webhook returned HTTP " + response);
                     }
                  } catch (Exception e) {
                     this.plugin.getLogger().warning("Webhook error: " + e.getMessage());
                  } finally {
                     if (connection != null) {
                        connection.disconnect();
                     }

                  }

               });
            }
         }
      }
   }

   private String replace(String text, Punishment punishment, String staff, String ip) {
      String s = text.replace("%player%", this.safe(punishment.getPlayer())).replace("%staff%", staff == null ? this.safe(punishment.getStaff()) : staff).replace("%reason%", this.safe(punishment.getReason())).replace("%duration%", this.safe(punishment.getDuration())).replace("%offense%", String.valueOf(punishment.getOffense())).replace("%banid%", this.safe(punishment.getBanId())).replace("%date%", Instant.ofEpochMilli(punishment.getDate()).toString()).replace("%ip%", ip == null ? "N/A" : ip);
      return s;
   }

   private String safe(String v) {
      return v == null ? "" : v;
   }

   private int color(String hex) {
      try {
         return Integer.parseInt(hex.replace("#", ""), 16);
      } catch (Exception var3) {
         return 16724787;
      }
   }

   private String escape(String text) {
      return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
   }
}
