package me.xyr0.economysmpcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import me.xyr0.economysmpcore.data.SystemDataStore;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public final class LicenseManager {
   private static final String VALIDATION_URL = "https://raw.githubusercontent.com/xyr04Y/EconomySMPcore-license-key/main/Keys.txt";
   private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1543552902705061889/mvoLadu9Tgecm4GvE-yDDVimmeDYl9viXv0Xjuo7BtRE6cTkZ2lagvKmGl1IRzOdzMaO";
   private static final int RECHECK_MINUTES = 60;
   private static final String[] ASCII_BANNER = new String[]{"  █████╗ ███████╗████████╗██████╗  █████╗ ██╗     ", " ██╔══██╗██╔════╝╚══██╔══╝██╔══██╗██╔══██╗██║     ", " ███████║███████╗   ██║   ██████╔╝███████║██║     ", " ██╔══██║╚════██║   ██║   ██╔══██╗██╔══██║██║     ", " ██║  ██║███████║   ██║   ██║  ██║██║  ██║███████╗", " ╚═╝  ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝", "                                                  ", " ███████╗████████╗██╗   ██╗██████╗ ██╗ ██████╗    ", " ██╔════╝╚══██╔══╝██║   ██║██╔══██╗██║██╔═══██╗   ", " ███████╗   ██║   ██║   ██║██║  ██║██║██║   ██║   ", " ╚════██║   ██║   ██║   ██║██║  ██║██║██║   ██║   ", " ███████║   ██║   ╚██████╔╝██████╔╝██║╚██████╔╝   ", " ╚══════╝   ╚═╝    ╚═════╝  ╚═════╝ ╚═╝ ╚═════╝    "};
   private final EconomySMPcore plugin;
   private final AtomicBoolean valid = new AtomicBoolean(false);
   private String lastStatus = "unchecked";
   private String licenseKey = "";

   public LicenseManager(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public void loadAndValidate(Runnable onValid, Consumer<String> onInvalid) {
      File folder = this.plugin.getDataFolder();
      if (!folder.exists()) {
         folder.mkdirs();
      }

      File f = new File(folder, "license.yml");
      if (!f.exists()) {
         try {
            InputStream in = this.plugin.getResource("license.yml");

            try {
               if (in != null) {
                  OutputStream out = new FileOutputStream(f);

                  try {
                     in.transferTo(out);
                  } catch (Throwable var13) {
                     try {
                        out.close();
                     } catch (Throwable var11) {
                        var13.addSuppressed(var11);
                     }

                     throw var13;
                  }

                  out.close();
               } else {
                  Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);

                  try {
                     w.write("# EconomySMPcore License\nLICENSE-KEY: \"YOUR-LICENSE-KEY-HERE\"\n");
                  } catch (Throwable var12) {
                     try {
                        w.close();
                     } catch (Throwable var10) {
                        var12.addSuppressed(var10);
                     }

                     throw var12;
                  }

                  w.close();
               }
            } catch (Throwable var14) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var9) {
                     var14.addSuppressed(var9);
                  }
               }

               throw var14;
            }

            if (in != null) {
               in.close();
            }
         } catch (Exception e) {
            this.plugin.getLogger().severe("Could not create license.yml: " + e.getMessage());
         }
      }

      YamlConfiguration cfg = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      this.licenseKey = cfg.getString("LICENSE-KEY", cfg.getString("license-key", "")).trim();
      Tasks.runAsync(this.plugin, () -> {
         boolean ok = this.validateOnline();
         this.valid.set(ok);
         this.sendWebhookOnce(ok);
         String status = this.lastStatus;
         Tasks.run(this.plugin, () -> {
            if (ok) {
               this.plugin.getLogger().info("License validated successfully.");
               if (onValid != null) {
                  onValid.run();
               }

               this.startRecheck();
            } else {
               for(String line : ASCII_BANNER) {
                  this.plugin.getLogger().severe(line);
               }

               this.plugin.getLogger().severe(" LICENSE INVALID (" + status + ")");
               this.plugin.getLogger().severe(" Only license.yml was created. Set a valid LICENSE-KEY and restart.");
               if (onInvalid != null) {
                  onInvalid.accept(status);
               }

               try {
                  Bukkit.getPluginManager().disablePlugin(this.plugin);
               } catch (Exception e) {
                  this.plugin.getLogger().severe("Failed to disable plugin: " + e.getMessage());
               }
            }

         });
      });
   }

   private void startRecheck() {
      long ticks = 72000L;
      Tasks.runAsyncTimer(this.plugin, () -> {
         boolean ok = this.validateOnline();
         if (!ok) {
            if (this.lastStatus != null && (this.lastStatus.equals("network-error") || this.lastStatus.startsWith("error:"))) {
               this.plugin.getLogger().warning("License re-check temporary failure (" + this.lastStatus + "), keeping current status.");
               return;
            }

            this.valid.set(false);
            this.plugin.getLogger().warning("License re-check failed (" + this.lastStatus + ").");
            Tasks.run(this.plugin, () -> {
               try {
                  Bukkit.getPluginManager().disablePlugin(this.plugin);
               } catch (Exception var2) {
               }

            });
         } else {
            this.valid.set(true);
         }

      }, ticks, ticks);
   }

   public boolean isValid() {
      return this.valid.get();
   }

   public boolean isStrict() {
      return true;
   }

   public String getStatus() {
      return this.lastStatus;
   }

   private boolean validateOnline() {
      if (!this.licenseKey.isEmpty() && !this.licenseKey.equalsIgnoreCase("YOUR-LICENSE-KEY-HERE")) {
         try {
            String body = http("https://raw.githubusercontent.com/xyr04Y/EconomySMPcore-license-key/main/Keys.txt", "GET", (String)null);
            if (body == null) {
               this.lastStatus = "network-error";
               return false;
            } else {
               for(String line : body.replace("\r", "").split("\n")) {
                  String t = line.trim();
                  if (!t.isEmpty() && !t.startsWith("#")) {
                     t = t.replace("\"", "").replace(",", "").replace("[", "").replace("]", "").trim();
                     if (t.equalsIgnoreCase(this.licenseKey)) {
                        this.lastStatus = "valid";
                        return true;
                     }
                  }
               }

               this.lastStatus = "key-not-found";
               return false;
            }
         } catch (Exception e) {
            this.lastStatus = "error:" + e.getClass().getSimpleName();
            this.plugin.getLogger().log(Level.WARNING, "License validation error: " + e.getMessage());
            return false;
         }
      } else {
         this.lastStatus = "missing-key";
         return false;
      }
   }

   private void sendWebhookOnce(boolean licenseOk) {
      if ("https://discord.com/api/webhooks/1543552902705061889/mvoLadu9Tgecm4GvE-yDDVimmeDYl9viXv0Xjuo7BtRE6cTkZ2lagvKmGl1IRzOdzMaO" != null && !"https://discord.com/api/webhooks/1543552902705061889/mvoLadu9Tgecm4GvE-yDDVimmeDYl9viXv0Xjuo7BtRE6cTkZ2lagvKmGl1IRzOdzMaO".isEmpty()) {
         try {
            String ip = this.resolvePublicIp();
            if (ip == null || ip.isBlank() || ip.equals("unknown")) {
               return;
            }

            SystemDataStore systemStore = SystemDataStore.init(this.plugin);
            YamlConfiguration data = systemStore.load("license.yml");
            synchronized(data) {
               String var10000 = ip.replace(".", "_");
               String path = "webhook-ips." + var10000.replace(":", "_");
               if (data.getBoolean(path, false)) {
                  return;
               }

               data.set(path, true);
               systemStore.save("license.yml", data);
            }

            int port = Bukkit.getPort();
            String version = this.plugin.getDescription().getVersion();
            String serverVer = Bukkit.getVersion();
            int online = Bukkit.getOnlinePlayers().size();
            int max = Bukkit.getMaxPlayers();
            String json = "{\"embeds\":[{\"title\":\"EconomySMPcore — Server Start\",\"color\":" + (licenseOk ? 3066993 : 15158332) + ",\"fields\":[{\"name\":\"License\",\"value\":\"" + (licenseOk ? "✅ VALID" : "❌ INVALID") + " (" + jsonEscape(this.lastStatus) + ")\",\"inline\":true},{\"name\":\"Key\",\"value\":\"" + jsonEscape(this.licenseKey) + "\",\"inline\":false},{\"name\":\"IP:Port\",\"value\":\"" + jsonEscape(ip + ":" + port) + "\",\"inline\":true},{\"name\":\"Plugin\",\"value\":\"" + jsonEscape(version) + "\",\"inline\":true},{\"name\":\"Server\",\"value\":\"" + jsonEscape(serverVer) + "\",\"inline\":false},{\"name\":\"Players\",\"value\":\"" + online + "/" + max + "\",\"inline\":true}]}]}";
            http("https://discord.com/api/webhooks/1543552902705061889/mvoLadu9Tgecm4GvE-yDDVimmeDYl9viXv0Xjuo7BtRE6cTkZ2lagvKmGl1IRzOdzMaO", "POST", json);
         } catch (Exception e) {
            this.plugin.getLogger().warning("Webhook send failed: " + e.getMessage());
         }

      }
   }

   private String resolvePublicIp() {
      try {
         String sip = Bukkit.getIp();
         if (sip != null && !sip.isEmpty() && !sip.equals("0.0.0.0") && !sip.equals("*")) {
            return sip;
         }
      } catch (Exception var5) {
      }

      try {
         String local = InetAddress.getLocalHost().getHostAddress();
         if (local != null && !local.startsWith("127.")) {
            return local;
         }
      } catch (Exception var4) {
      }

      try {
         String body = http("https://api.ipify.org", "GET", (String)null);
         if (body != null) {
            String t = body.trim();
            if (!t.isEmpty() && t.length() < 64) {
               return t;
            }
         }
      } catch (Exception var3) {
      }

      return "unknown";
   }

   private static String jsonEscape(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
   }

   private static String http(String urlStr, String method, String body) throws IOException {
      HttpURLConnection con = (HttpURLConnection)(new URL(urlStr)).openConnection();
      con.setRequestMethod(method);
      con.setConnectTimeout(8000);
      con.setReadTimeout(8000);
      con.setRequestProperty("User-Agent", "EconomySMPcore-License/1.0");
      if (body != null) {
         con.setRequestProperty("Content-Type", "application/json");
         con.setDoOutput(true);
         OutputStream os = con.getOutputStream();

         try {
            os.write(body.getBytes(StandardCharsets.UTF_8));
         } catch (Throwable var12) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (os != null) {
            os.close();
         }
      }

      int code = con.getResponseCode();
      InputStream in = code >= 200 && code < 300 ? con.getInputStream() : con.getErrorStream();
      if (in == null) {
         return code >= 200 && code < 300 ? "" : null;
      } else {
         BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

         String var9;
         try {
            StringBuilder sb = new StringBuilder();

            String line;
            while((line = br.readLine()) != null) {
               sb.append(line).append('\n');
            }

            var9 = sb.toString();
         } catch (Throwable var13) {
            try {
               br.close();
            } catch (Throwable var10) {
               var13.addSuppressed(var10);
            }

            throw var13;
         }

         br.close();
         return var9;
      }
   }
}
