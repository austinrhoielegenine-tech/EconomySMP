package com.austynzdevv.nyrethacore.hide;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.nyrethacore.hide.util.HideNameUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HideManager {
   private final EconomyHide plugin;
   private final Set<UUID> hiddenPlayers = new HashSet();
   private final HashMap<UUID, String> hiddenNames = new HashMap();

   public HideManager(EconomyHide plugin) {
      this.plugin = plugin;
   }

   public boolean isHidden(Player player) {
      return this.hiddenPlayers.contains(player.getUniqueId());
   }

   public String getHiddenName(Player player) {
      return (String)this.hiddenNames.computeIfAbsent(player.getUniqueId(), (uuid) -> HideNameUtil.generate(this.plugin.getConfig().getInt("settings.hidden-name-length", 8)));
   }

   public void hide(Player player) {
      UUID uuid = player.getUniqueId();
      this.hiddenPlayers.add(uuid);
      this.hiddenNames.put(uuid, HideNameUtil.generate(this.plugin.getConfig().getInt("settings.hidden-name-length", 8)));
      this.applyHide(player);
      this.save();
   }

   public void unhide(Player player) {
      UUID uuid = player.getUniqueId();
      this.hiddenPlayers.remove(uuid);
      this.hiddenNames.remove(uuid);
      this.removeHide(player);
      this.save();
   }

   public void applyHide(Player player) {
      this.plugin.getNameManager().hideName(player);
      this.plugin.getNameTagManager().hideTag(player);
      if (this.plugin.getConfig().getBoolean("settings.steve-skin", true)) {
         this.plugin.getSkinManager().setSteveSkin(player);
      }

   }

   public void removeHide(Player player) {
      this.plugin.getNameManager().restoreName(player);
      this.plugin.getNameTagManager().showTag(player);
      this.plugin.getSkinManager().restoreSkin(player);

      for(Player other : Bukkit.getOnlinePlayers()) {
         if (!other.equals(player)) {
            try {
               other.showPlayer(this.plugin.getCore(), player);
            } catch (Exception var7) {
               try {
                  other.showPlayer(player);
               } catch (Exception var6) {
               }
            }
         }
      }

   }

   public void load() {
      if (this.plugin.getDataManager().getConfig().contains("hide.players")) {
         for(String uuid : this.plugin.getDataManager().getConfig().getConfigurationSection("hide.players").getKeys(false)) {
            UUID id = UUID.fromString(uuid);
            this.hiddenPlayers.add(id);
            String name = this.plugin.getDataManager().getConfig().getString("hide.players." + uuid + ".name");
            if (name != null) {
               this.hiddenNames.put(id, name);
            }
         }
      }

   }

   public void save() {
      this.plugin.getDataManager().getConfig().set("hide.players", (Object)null);

      for(UUID uuid : this.hiddenPlayers) {
         this.plugin.getDataManager().getConfig().set("hide.players." + String.valueOf(uuid) + ".name", this.hiddenNames.get(uuid));
      }

      this.plugin.getDataManager().save();
   }
}
