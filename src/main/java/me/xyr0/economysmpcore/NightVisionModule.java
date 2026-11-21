package me.xyr0.economysmpcore;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.xyr0.economysmpcore.data.PlayerDataStore;
import me.xyr0.economysmpcore.util.SoundUtil;
import me.xyr0.economysmpcore.util.Tasks;
import me.xyr0.economysmpcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionModule implements Module, Listener {
   private static final String DATA_PATH = "nightvision.enabled";
   private static final String MSG_ON = "&7ᴛᴏɢɢʟᴇᴅ ɴɪɢʜᴛ ᴠɪѕɪᴏɴ ᴇꜰꜰᴇᴄᴛ";
   private static final String MSG_OFF = "&7ᴜɴᴛᴏɢɢʟᴇᴅ ɴɪɢʜᴛ ᴠɪѕɪᴏɴ ᴇꜰꜰᴇᴄᴛ";
   private final EconomySMPcore plugin;
   private final Set<UUID> enabled = new HashSet();

   public NightVisionModule(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "nightvision";
   }

   public void onEnable() {
      this.load();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("nv", this::toggle);
      this.plugin.commands().registerHandler("nightvision", this::toggle);

      for(Player p : Bukkit.getOnlinePlayers()) {
         if (this.enabled.contains(p.getUniqueId())) {
            this.apply(p);
         }
      }

   }

   public void onDisable() {
      this.save();
   }

   public void reload() {
      this.load();

      for(Player p : Bukkit.getOnlinePlayers()) {
         if (this.enabled.contains(p.getUniqueId())) {
            this.apply(p);
         } else {
            this.remove(p);
         }
      }

   }

   private void load() {
      this.enabled.clear();

      try {
         PlayerDataStore store = PlayerDataStore.get();
         synchronized(store.lock()) {
            ConfigurationSection sec = store.config().getConfigurationSection("nightvision.enabled");
            if (sec != null) {
               for(String key : sec.getKeys(false)) {
                  if (sec.getBoolean(key, false)) {
                     try {
                        this.enabled.add(UUID.fromString(key));
                     } catch (Exception var8) {
                     }
                  }
               }
            }
         }
      } catch (Exception var10) {
      }

   }

   private void save() {
      try {
         PlayerDataStore.get().editAndSave((cfg) -> {
            cfg.set("nightvision.enabled", (Object)null);

            for(UUID id : this.enabled) {
               cfg.set("nightvision.enabled." + String.valueOf(id), true);
            }

         });
      } catch (Exception var2) {
      }

   }

   private void persist(UUID id, boolean on) {
      try {
         PlayerDataStore.get().editAndSave((cfg) -> cfg.set("nightvision.enabled." + String.valueOf(id), on ? true : null));
      } catch (Exception var4) {
      }

   }

   private boolean toggle(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         if (this.enabled.contains(id)) {
            this.enabled.remove(id);
            this.persist(id, false);
            this.remove(p);
            this.notify(p, "&7ᴜɴᴛᴏɢɢʟᴇᴅ ɴɪɢʜᴛ ᴠɪѕɪᴏɴ ᴇꜰꜰᴇᴄᴛ");
         } else {
            this.enabled.add(id);
            this.persist(id, true);
            this.apply(p);
            this.notify(p, "&7ᴛᴏɢɢʟᴇᴅ ɴɪɢʜᴛ ᴠɪѕɪᴏɴ ᴇꜰꜰᴇᴄᴛ");
         }

         return true;
      } else {
         TextUtil.send(sender, "&cPlayers only.");
         return true;
      }
   }

   private void notify(Player p, String msg) {
      try {
         if (!SettingsModule.isServerChatDisabled(p.getUniqueId())) {
            TextUtil.send(p, msg);
         }
      } catch (Exception var4) {
         TextUtil.send(p, msg);
      }

      TextUtil.actionBar(p, msg);
      SoundUtil.play(p, p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
   }

   private void apply(Player p) {
      if (p != null && p.isOnline()) {
         if (Tasks.isFolia()) {
            Tasks.runAtEntity(this.plugin, p, () -> this.applyNow(p));
         } else {
            this.applyNow(p);
         }
      }
   }

   private void applyNow(Player p) {
      if (p != null && p.isOnline()) {
         PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false, false);
         p.addPotionEffect(effect);
      }
   }

   private void remove(Player p) {
      if (p != null && p.isOnline()) {
         p.removePotionEffect(PotionEffectType.NIGHT_VISION);
      }
   }

   public boolean isEnabled(UUID id) {
      return this.enabled.contains(id);
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Player p = e.getPlayer();
      if (this.enabled.contains(p.getUniqueId())) {
         Tasks.run(this.plugin, () -> this.apply(p));
      }

   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent e) {
      Player p = e.getPlayer();
      if (this.enabled.contains(p.getUniqueId())) {
         Tasks.run(this.plugin, () -> this.apply(p));
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onEffect(EntityPotionEffectEvent e) {
      Entity var3 = e.getEntity();
      if (var3 instanceof Player p) {
         if (this.enabled.contains(p.getUniqueId())) {
            if (e.getAction() != Action.ADDED) {
               PotionEffectType type = e.getModifiedType();
               if (type != null && type == PotionEffectType.NIGHT_VISION || e.getOldEffect() != null && e.getOldEffect().getType() == PotionEffectType.NIGHT_VISION) {
                  if (e.getAction() == Action.REMOVED || e.getAction() == Action.CLEARED) {
                     Tasks.run(this.plugin, () -> {
                        if (p.isOnline() && this.enabled.contains(p.getUniqueId())) {
                           this.apply(p);
                        }

                     });
                  }

               }
            }
         }
      }
   }
}
