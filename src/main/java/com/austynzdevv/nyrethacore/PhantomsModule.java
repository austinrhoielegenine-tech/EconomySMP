package com.austynzdevv.nyrethacore;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import com.austynzdevv.nyrethacore.util.TextUtil;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PhantomsModule implements Module, Listener {
   private final NyrethaCore plugin;
   private FileConfiguration cfg;
   private final Set<UUID> disabled = new HashSet();
   private double range = (double)100.0F;

   public PhantomsModule(NyrethaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "phantoms";
   }

   public void onEnable() {
      this.reload();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("phantom", this::cmd);
      this.plugin.commands().registerHandler("phantoms", this::cmd);

      try {
         PluginCommand pc = this.plugin.getCommand("phantom");
         if (pc != null) {
            pc.setExecutor((s, c, l, a) -> this.cmd(s, a));
         }
      } catch (Exception var2) {
      }

   }

   public void onDisable() {
      this.disabled.clear();
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "phantoms.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("phantoms.yml", false);
         } catch (Exception var3) {
         }
      }

      this.cfg = YamlConfiguration.loadConfiguration(f);
      this.range = this.cfg.getDouble("Max-Range", (double)100.0F);
   }

   private boolean cmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         String var4 = p.getWorld().getName();
         List var5 = this.cfg.getStringList("blacklisted-world");
         UUID id = p.getUniqueId();
         if (this.disabled.contains(id)) {
            this.disabled.remove(id);
            this.msg(p, "disable");
            this.sound(p, "disable-sound");
         } else {
            this.disabled.add(id);
            this.msg(p, "enable");
            this.sound(p, "enable-sound");
         }

         return true;
      } else {
         return true;
      }
   }

   private void msg(Player p, String key) {
      boolean chat = this.cfg.getBoolean("messages." + key + ".chat", false);
      boolean ab = this.cfg.getBoolean("messages." + key + ".actionbar", true);
      String m = this.cfg.getString("messages." + key + ".message", "");
      if (m != null && !m.isBlank()) {
         if (chat) {
            TextUtil.send(p, m);
         }

         if (ab) {
            try {
               HotbarUtil.send(p, TextUtil.component(m));
            } catch (Exception var7) {
               TextUtil.send(p, m);
            }
         }

      }
   }

   private void sound(Player p, String key) {
      String name = this.cfg.getString("messages." + key + ".message", "");
      if (name != null && !name.isBlank()) {
         try {
            SoundUtil.play(p, p.getLocation(), Sound.valueOf(name), 1.0F, 1.0F);
         } catch (Exception var5) {
         }

      }
   }

   @EventHandler
   public void onSpawn(CreatureSpawnEvent e) {
      if (e.getEntityType() == EntityType.PHANTOM) {
         World w = e.getLocation().getWorld();
         if (w != null) {
            for(String b : this.cfg.getStringList("blacklisted-world")) {
               if (b != null && b.equalsIgnoreCase(w.getName())) {
                  e.setCancelled(true);
                  return;
               }
            }

            for(Player p : w.getPlayers()) {
               if (this.disabled.contains(p.getUniqueId()) && p.getLocation().distanceSquared(e.getLocation()) <= this.range * this.range) {
                  e.setCancelled(true);
                  return;
               }
            }

         }
      }
   }
}
