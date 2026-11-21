package com.austynzdevv.novacore.order;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.util.EconomyUtil;
import com.austynzdevv.novacore.util.ItemUtil;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WebhookManager {
   private final EconomyOrder plugin;
   private FileConfiguration cfg;

   public WebhookManager(EconomyOrder plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      File file = new File(this.plugin.getDataFolder(), "webhook.yml");
      if (!file.exists()) {
         this.plugin.saveResource("webhook.yml", false);
      }

      this.cfg = YamlConfiguration.loadConfiguration(file);
   }

   public boolean isEnabled() {
      return this.cfg.getBoolean("enabled", false) && this.cfg.getString("url", "").trim().length() > 10;
   }

   public void orderCreated(Order order) {
      if (this.isEnabled() && this.cfg.getBoolean("events.order-created", true)) {
         this.send("order-created", this.cfg.getString("message.embed-order-created", "New Order Created"), this.cfg.getInt("message.created-color", 5814783), order, (String)null);
      }
   }

   public void orderFilled(Order order, String fillerName) {
      if (this.isEnabled() && this.cfg.getBoolean("events.order-filled", true)) {
         this.send("order-filled", this.cfg.getString("message.embed-order-filled", "Order Filled"), this.cfg.getInt("message.filled-color", 16755200), order, fillerName);
      }
   }

   public void orderCancelled(Order order) {
      if (this.isEnabled() && this.cfg.getBoolean("events.order-cancelled", true)) {
         this.send("order-cancelled", this.cfg.getString("message.embed-order-cancelled", "Order Cancelled"), this.cfg.getInt("message.cancelled-color", 15158332), order, (String)null);
      }
   }

   private void send(String bodyKey, String title, int color, Order order, String filler) {
      List<String> lines = this.cfg.getStringList("message." + bodyKey);
      if (lines != null && !lines.isEmpty()) {
         String itemName = this.formatItem(order);
         String lore = this.formatLore(order);
         String price = EconomyUtil.formatOrder(order.getPriceEach());
         String total = EconomyUtil.formatOrder((double)order.getAmount() * order.getPriceEach());
         StringBuilder description = new StringBuilder();

         for(String line : lines) {
            String out = line.replace("%creator%", this.safe(order.getOwnerName())).replace("%Creator%", this.safe(order.getOwnerName())).replace("%filler%", this.safe(filler)).replace("%seller%", this.safe(order.getOwnerName())).replace("%amount%", String.valueOf(order.getAmount())).replace("%remaining%", String.valueOf(order.getRemaining())).replace("%item%", itemName).replace("%price%", price).replace("%total%", total).replace("%lore%", lore);
            description.append(out).append("\\n");
         }

         String username = this.cfg.getString("username", "EconomyOrder");
         String avatar = this.cfg.getString("avatar-url", "");
         String url = this.cfg.getString("url", "").trim();
         String json = this.buildJson(username, avatar, title, description.toString(), color);
         Tasks.runAsync(this.plugin.getCore(), () -> this.post(url, json));
      }
   }

   private String formatItem(Order order) {
      String name = ItemUtil.pretty(order.getMaterial());
      if (this.cfg.getBoolean("message.item-format.show-item-name-uppercase", false)) {
         name = name.toUpperCase();
      }

      return name;
   }

   private String formatLore(Order order) {
      if (!this.cfg.getBoolean("message.item-format.show-item-lore", true)) {
         return "None";
      } else {
         ItemStack display = order.getDisplay();
         if (display != null && display.hasItemMeta()) {
            ItemMeta meta = display.getItemMeta();
            return meta != null && meta.hasLore() && meta.lore() != null && !meta.lore().isEmpty() ? (String)meta.lore().stream().map((c) -> PlainTextComponentSerializer.plainText().serialize(c)).filter((s) -> s != null && !s.isEmpty()).collect(Collectors.joining(" | ")) : "None";
         } else {
            return "None";
         }
      }
   }

   private String safe(String s) {
      return s == null ? "Unknown" : s;
   }

   private String escape(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
   }

   private String buildJson(String username, String avatar, String title, String description, int color) {
      StringBuilder sb = new StringBuilder();
      sb.append("{");
      sb.append("\"username\":\"").append(this.escape(username)).append("\",");
      if (avatar != null && !avatar.isEmpty()) {
         sb.append("\"avatar_url\":\"").append(this.escape(avatar)).append("\",");
      }

      sb.append("\"embeds\":[{");
      sb.append("\"title\":\"").append(this.escape(title)).append("\",");
      sb.append("\"description\":\"").append(this.escape(description.replace("\\n", "\n"))).append("\",");
      sb.append("\"color\":").append(color);
      sb.append("}]}");
      return sb.toString();
   }

   private void post(String urlString, String json) {
      HttpURLConnection conn = null;

      try {
         URL url = new URL(urlString);
         conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setDoOutput(true);
         conn.setRequestProperty("Content-Type", "application/json");
         conn.setRequestProperty("User-Agent", "EconomyOrder");
         conn.setConnectTimeout(8000);
         conn.setReadTimeout(8000);
         byte[] body = json.getBytes(StandardCharsets.UTF_8);
         conn.setFixedLengthStreamingMode(body.length);
         OutputStream os = conn.getOutputStream();

         try {
            os.write(body);
         } catch (Throwable var15) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (os != null) {
            os.close();
         }

         int code = conn.getResponseCode();
         if (code < 200 || code >= 300) {
            this.plugin.getLogger().warning("Webhook response: " + code);
         }
      } catch (Exception e) {
         this.plugin.getLogger().warning("Webhook failed: " + e.getMessage());
      } finally {
         if (conn != null) {
            conn.disconnect();
         }

      }

   }
}
