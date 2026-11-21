package com.austynzdevv.economysmp.auction;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.austynzdevv.economysmp.util.EconomyUtil;
import com.austynzdevv.economysmp.util.ItemUtil;
import com.austynzdevv.economysmp.util.Tasks;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class WebhookManager {
   private final EconomyAuction plugin;
   private FileConfiguration cfg;

   public WebhookManager(EconomyAuction plugin) {
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

   public void sendListed(AuctionListing listing) {
      if (this.enabled() && this.cfg.getBoolean("events.item-listed", true)) {
         this.sendEmbed(this.cfg.getString("message.embed-title-listing", "New Auction Listing"), this.cfg.getInt("message.listing-color", 5814783), this.formatLines(this.cfg.getStringList("message.webhook-sell"), listing, (String)null));
      }
   }

   public void sendBought(AuctionListing listing, String buyer) {
      if (this.enabled() && this.cfg.getBoolean("events.item-bought", true)) {
         this.sendEmbed(this.cfg.getString("message.embed-title-purchase", "Item Purchased"), this.cfg.getInt("message.purchase-color", 16755200), this.formatLines(this.cfg.getStringList("message.webhook-buy"), listing, buyer));
      }
   }

   public void sendRemoved(AuctionListing listing) {
      if (this.enabled() && this.cfg.getBoolean("events.item-removed", true)) {
         this.sendEmbed(this.cfg.getString("message.embed-title-remove", "Item Removed"), this.cfg.getInt("message.remove-color", 16711680), this.formatLines(this.cfg.getStringList("message.webhook-remove-item"), listing, (String)null));
      }
   }

   private boolean enabled() {
      if (this.cfg != null && this.cfg.getBoolean("enabled", false)) {
         String url = this.cfg.getString("url", "");
         return url != null && !url.isBlank();
      } else {
         return false;
      }
   }

   private List<String> formatLines(List<String> template, AuctionListing listing, String buyer) {
      List<String> out = new ArrayList();
      if (template == null) {
         return out;
      } else {
         String item = this.formatItem(listing.getItem());
         String lore = this.formatLore(listing.getItem());
         String amount = String.valueOf(listing.getItem().getAmount());
         String price = EconomyUtil.formatAuction(listing.getPrice());

         for(String line : template) {
            if (line == null) {
               line = "";
            }

            out.add(line.replace("%seller%", listing.getSellerName() == null ? "?" : listing.getSellerName()).replace("%buyer%", buyer == null ? "?" : buyer).replace("%item%", item).replace("%amount%", amount).replace("%price%", price).replace("%lore%", lore));
         }

         return out;
      }
   }

   private String formatItem(ItemStack item) {
      if (item == null) {
         return "UNKNOWN";
      } else {
         String name = ItemUtil.pretty(item);
         if (this.cfg.getBoolean("message.item-format.show-item-name-uppercase", true)) {
            name = name.toUpperCase();
         }

         return name;
      }
   }

   private String formatLore(ItemStack item) {
      if (!this.cfg.getBoolean("message.item-format.show-item-lore", true)) {
         return "";
      } else if (item != null && item.hasItemMeta()) {
         ItemMeta meta = item.getItemMeta();
         return meta != null && meta.lore() != null && !meta.lore().isEmpty() ? (String)meta.lore().stream().map((c) -> PlainTextComponentSerializer.plainText().serialize(c)).collect(Collectors.joining(" | ")) : "";
      } else {
         return "";
      }
   }

   private void sendEmbed(String title, int color, List<String> lines) {
      String url = this.cfg.getString("url", "");
      if (url != null && !url.isBlank()) {
         String username = this.cfg.getString("username", "EconomyAuction");
         String avatar = this.cfg.getString("avatar-url", "");
         String description = String.join("\\n", lines.stream().map(this::escapeJson).toList());
         StringBuilder json = new StringBuilder();
         json.append("{\"username\":\"").append(this.escapeJson(username)).append("\",");
         if (avatar != null && !avatar.isBlank()) {
            json.append("\"avatar_url\":\"").append(this.escapeJson(avatar)).append("\",");
         }

         json.append("\"embeds\":[{");
         json.append("\"title\":\"").append(this.escapeJson(title == null ? "" : title)).append("\",");
         json.append("\"description\":\"").append(description).append("\",");
         json.append("\"color\":").append(color);
         json.append("}]}");
         String body = json.toString();
         Tasks.runAsync(this.plugin.getCore(), () -> {
            try {
               HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
               conn.setRequestMethod("POST");
               conn.setDoOutput(true);
               conn.setRequestProperty("Content-Type", "application/json");
               conn.setConnectTimeout(5000);
               conn.setReadTimeout(5000);
               byte[] data = body.getBytes(StandardCharsets.UTF_8);
               OutputStream os = conn.getOutputStream();

               try {
                  os.write(data);
               } catch (Throwable var9) {
                  if (os != null) {
                     try {
                        os.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (os != null) {
                  os.close();
               }

               conn.getResponseCode();
               conn.disconnect();
            } catch (Exception e) {
               this.plugin.getLogger().warning("Webhook failed: " + e.getMessage());
            }

         });
      }
   }

   private String escapeJson(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
   }
}
