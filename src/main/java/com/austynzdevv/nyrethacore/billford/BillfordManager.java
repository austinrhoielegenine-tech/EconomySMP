package com.austynzdevv.nyrethacore.billford;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.austynzdevv.nyrethacore.data.PlayerDataStore;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class BillfordManager {
   private final EconomyBillford plugin;
   private final Map<UUID, Long> cooldowns = new HashMap();
   private final List<Preset> presets = new ArrayList();
   private int currentIndex;
   private long nextRotateAt;
   private int rotateTask = -1;

   public BillfordManager(EconomyBillford plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.loadPresets();
      YamlConfiguration cfg = PlayerDataStore.get().config();
      this.currentIndex = cfg.getInt("billford.current-preset", 0);
      this.nextRotateAt = cfg.getLong("billford.next-rotate-at", 0L);
      if (this.presets.isEmpty()) {
         this.currentIndex = 0;
      } else if (this.currentIndex < 0 || this.currentIndex >= this.presets.size()) {
         this.currentIndex = 0;
      }

      if (this.nextRotateAt <= 0L) {
         this.nextRotateAt = System.currentTimeMillis() + this.parseIntervalMillis();
         this.save();
      }

   }

   public void save() {
      YamlConfiguration cfg = PlayerDataStore.get().config();
      cfg.set("billford.current-preset", this.currentIndex);
      cfg.set("billford.next-rotate-at", this.nextRotateAt);
      PlayerDataStore.get().save();
   }

   private void loadPresets() {
      this.presets.clear();
      ConfigurationSection sec = this.plugin.getConfig().getConfigurationSection("presets");
      if (sec != null) {
         for(String key : sec.getKeys(false)) {
            ConfigurationSection p = sec.getConfigurationSection(key);
            if (p != null) {
               Material trade;
               try {
                  trade = Material.valueOf(p.getString("trade-item", "STONE").toUpperCase(Locale.ROOT));
               } catch (Exception var11) {
                  trade = Material.STONE;
               }

               int amount = Math.max(1, p.getInt("amount", 1));
               List<RequiredItem> required = new ArrayList();

               for(String line : p.getStringList("required-items")) {
                  RequiredItem ri = this.parseRequired(line);
                  if (ri != null) {
                     required.add(ri);
                  }
               }

               if (!required.isEmpty()) {
                  this.presets.add(new Preset(key, trade, amount, required));
               }
            }
         }

      }
   }

   private RequiredItem parseRequired(String line) {
      if (line != null && !line.isBlank()) {
         String[] parts = line.trim().split("\\s+");

         try {
            if (parts.length == 1) {
               return new RequiredItem(Material.valueOf(parts[0].toUpperCase(Locale.ROOT)), 1);
            } else {
               int amount = Integer.parseInt(parts[0]);
               Material mat = Material.valueOf(parts[1].toUpperCase(Locale.ROOT));
               return new RequiredItem(mat, Math.max(1, amount));
            }
         } catch (Exception var5) {
            return null;
         }
      } else {
         return null;
      }
   }

   public Preset getCurrentPreset() {
      if (this.presets.isEmpty()) {
         return null;
      } else {
         if (this.currentIndex < 0 || this.currentIndex >= this.presets.size()) {
            this.currentIndex = 0;
         }

         return (Preset)this.presets.get(this.currentIndex);
      }
   }

   public List<Preset> getPresets() {
      return this.presets;
   }

   public void startRotationTask() {
      this.stopRotationTask();
      this.rotateTask = Tasks.runTimer(this.plugin.getCore(), this::tickRotation, 20L, 600L);
   }

   public void stopRotationTask() {
      if (this.rotateTask != -1) {
         Tasks.cancel(this.rotateTask);
         this.rotateTask = -1;
      }

   }

   private void tickRotation() {
      if (this.presets.size() > 1) {
         if (System.currentTimeMillis() >= this.nextRotateAt) {
            this.currentIndex = (this.currentIndex + 1) % this.presets.size();
            this.nextRotateAt = System.currentTimeMillis() + this.parseIntervalMillis();
            this.save();
            String msg = this.plugin.getMessages().get("preset-changed", "&fThe preset has been changed to &#7afc00%preset%&f!");
            Preset preset = this.getCurrentPreset();
            if (preset != null) {
               msg = msg.replace("%preset%", preset.id());

               for(Player player : Bukkit.getOnlinePlayers()) {
                  player.sendMessage(ColorUtil.parse(msg));
               }
            }

         }
      }
   }

   private long parseIntervalMillis() {
      String raw = this.plugin.getConfig().getString("preset-change-interval", "12h");
      if (raw != null && !raw.isBlank()) {
         raw = raw.trim().toLowerCase(Locale.ROOT);

         try {
            if (raw.endsWith("h")) {
               return TimeUnit.HOURS.toMillis(Long.parseLong(raw.replace("h", "")));
            } else if (raw.endsWith("m")) {
               return TimeUnit.MINUTES.toMillis(Long.parseLong(raw.replace("m", "")));
            } else if (raw.endsWith("s")) {
               return TimeUnit.SECONDS.toMillis(Long.parseLong(raw.replace("s", "")));
            } else {
               return raw.endsWith("d") ? TimeUnit.DAYS.toMillis(Long.parseLong(raw.replace("d", ""))) : TimeUnit.HOURS.toMillis(Long.parseLong(raw));
            }
         } catch (Exception var3) {
            return TimeUnit.HOURS.toMillis(12L);
         }
      } else {
         return TimeUnit.HOURS.toMillis(12L);
      }
   }

   public long getCooldownLeft(Player player) {
      long end = (Long)this.cooldowns.getOrDefault(player.getUniqueId(), 0L);
      long left = end - System.currentTimeMillis();
      return Math.max(0L, left);
   }

   public boolean isOnCooldown(Player player) {
      return this.getCooldownLeft(player) > 0L;
   }

   public void setCooldown(Player player) {
      int seconds = Math.max(0, this.plugin.getConfig().getInt("cooldown", 300));
      this.cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (long)seconds * 1000L);
   }

   public boolean hasRequired(Player player, Preset preset) {
      for(RequiredItem req : preset.required()) {
         if (this.count(player, req.material()) < req.amount()) {
            return false;
         }
      }

      return true;
   }

   public int count(Player player, Material material) {
      int total = 0;

      for(ItemStack stack : player.getInventory().getContents()) {
         if (stack != null && stack.getType() == material) {
            total += stack.getAmount();
         }
      }

      return total;
   }

   public void removeRequired(Player player, Preset preset) {
      for(RequiredItem req : preset.required()) {
         int left = req.amount();
         ItemStack[] contents = player.getInventory().getContents();

         for(int i = 0; i < contents.length && left > 0; ++i) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == req.material()) {
               int take = Math.min(left, stack.getAmount());
               stack.setAmount(stack.getAmount() - take);
               if (stack.getAmount() <= 0) {
                  contents[i] = null;
               }

               left -= take;
            }
         }

         player.getInventory().setContents(contents);
      }

   }

   public boolean canFit(Player player, ItemStack item) {
      return player.getInventory().firstEmpty() != -1 || player.getInventory().containsAtLeast(item, 1);
   }

   public String formatRequired(Preset preset) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < preset.required().size(); ++i) {
         RequiredItem r = (RequiredItem)preset.required().get(i);
         if (i > 0) {
            sb.append(", ");
         }

         sb.append(r.amount()).append("x ").append(this.pretty(r.material()));
      }

      return sb.toString();
   }

   public String pretty(Material mat) {
      String n = mat.name().toLowerCase(Locale.ROOT).replace('_', ' ');
      String[] parts = n.split(" ");
      StringBuilder sb = new StringBuilder();

      for(String p : parts) {
         if (!p.isEmpty()) {
            if (!sb.isEmpty()) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
         }
      }

      return sb.toString();
   }

   public long getNextRotateAt() {
      return this.nextRotateAt;
   }

   public long getMillisUntilNextPreset() {
      return Math.max(0L, this.nextRotateAt - System.currentTimeMillis());
   }

   public String formatTimeUntilNextPreset() {
      long ms = this.getMillisUntilNextPreset();
      long totalSeconds = ms / 1000L;
      long days = totalSeconds / 86400L;
      long hours = totalSeconds % 86400L / 3600L;
      long minutes = totalSeconds % 3600L / 60L;
      StringBuilder sb = new StringBuilder();
      if (days > 0L) {
         sb.append(days).append("d ");
      }

      if (hours > 0L || days > 0L) {
         sb.append(hours).append("h ");
      }

      sb.append(minutes).append("m");
      return sb.toString().trim();
   }

   public static record RequiredItem(Material material, int amount) {
   }

   public static record Preset(String id, Material tradeItem, int tradeAmount, List<RequiredItem> required) {
   }
}
