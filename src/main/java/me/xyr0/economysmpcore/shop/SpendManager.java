package me.xyr0.economysmpcore.shop;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.data.PlayerDataStore;
import org.bukkit.configuration.file.FileConfiguration;

public final class SpendManager {
   private final AstralShop plugin;
   private final Map<UUID, Double> vaultSpent = new ConcurrentHashMap();
   private final File file;

   public SpendManager(AstralShop plugin) {
      this.plugin = plugin;
      this.file = PlayerDataStore.get().file();
      this.load();
   }

   public void load() {
      this.vaultSpent.clear();
      if (this.file.exists()) {
         FileConfiguration cfg = PlayerDataStore.get().config();
         if (cfg.isConfigurationSection("vault-spent")) {
            for(String key : cfg.getConfigurationSection("vault-spent").getKeys(false)) {
               try {
                  this.vaultSpent.put(UUID.fromString(key), cfg.getDouble("vault-spent." + key, (double)0.0F));
               } catch (Exception var5) {
               }
            }

         }
      }
   }

   public void save() {
      FileConfiguration cfg = PlayerDataStore.get().config();

      for(Map.Entry<UUID, Double> e : this.vaultSpent.entrySet()) {
         cfg.set("vault-spent." + String.valueOf(e.getKey()), e.getValue());
      }

      PlayerDataStore.get().save();
   }

   public void addVaultSpend(UUID id, double amount) {
      if (id != null && !(amount <= (double)0.0F)) {
         this.vaultSpent.put(id, (Double)this.vaultSpent.getOrDefault(id, (double)0.0F) + amount);
         FileConfiguration cfg = PlayerDataStore.get().config();
         cfg.set("vault-spent." + String.valueOf(id), this.vaultSpent.get(id));
         PlayerDataStore.get().save();
      }
   }

   public double getVaultSpent(UUID id) {
      return id == null ? (double)0.0F : (Double)this.vaultSpent.getOrDefault(id, (double)0.0F);
   }

   public String formatVaultSpent(UUID id) {
      double value = this.getVaultSpent(id);
      return value == (double)((long)value) ? String.valueOf((long)value) : String.format("%.2f", value);
   }
}
