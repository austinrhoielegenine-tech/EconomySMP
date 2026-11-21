package me.xyr0.economysmpcore.wl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WhitelistSettings {
   private final AstralWhitelister plugin;
   private boolean enabled;
   private String denyMsg;
   private boolean chat;
   private boolean actionBar;
   private boolean soundOn;
   private Sound sound;
   private Map<String, Set<String>> groupCmds = new HashMap();
   private Map<String, List<String>> groupPerms = new HashMap();

   public WhitelistSettings(AstralWhitelister plugin) {
      this.plugin = plugin;
   }

   public void load() {
      FileConfiguration cfg = this.plugin.getConfig();
      this.enabled = cfg.getBoolean("whitelist.enabled", true);
      this.denyMsg = cfg.getString("feedback.message", "&cThis command is not available.");
      this.chat = cfg.getBoolean("feedback.chat", true);
      this.actionBar = cfg.getBoolean("feedback.action-bar", false);
      this.soundOn = cfg.getBoolean("feedback.sound.enabled", true);
      String sName = cfg.getString("feedback.sound.type", "ENTITY_VILLAGER_NO");

      try {
         this.sound = Sound.valueOf(sName.toUpperCase());
      } catch (Exception var14) {
         this.plugin.getLogger().warning("Bad sound name: " + sName);
         this.sound = Sound.ENTITY_VILLAGER_NO;
      }

      this.groupCmds.clear();
      this.groupPerms.clear();
      ConfigurationSection groups = cfg.getConfigurationSection("groups");
      if (groups == null) {
         try {
            InputStream in = this.plugin.getResource("wl.yml");

            try {
               if (in != null) {
                  FileConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                  groups = defaults.getConfigurationSection("groups");
               }
            } catch (Throwable var15) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (in != null) {
               in.close();
            }
         } catch (Exception var16) {
         }

         if (groups == null) {
            return;
         }
      }

      for(String g : groups.getKeys(false)) {
         ConfigurationSection sec = groups.getConfigurationSection(g);
         if (sec != null) {
            List<String> perms = sec.getStringList("permissions");
            List<String> cmds = sec.getStringList("commands");
            Set<String> set = new HashSet();

            for(String c : cmds) {
               if (c != null && !c.isEmpty()) {
                  String n = c.trim().toLowerCase();
                  if (n.startsWith("/")) {
                     n = n.substring(1);
                  }

                  if (!n.equals("*") && !n.equals("**")) {
                     set.add(n);
                  }
               }
            }

            this.groupCmds.put(g.toLowerCase(), set);
            this.groupPerms.put(g.toLowerCase(), perms != null ? perms : new ArrayList());
         }
      }

      this.plugin.getLogger().info("Loaded " + this.groupCmds.size() + " groups.");
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getDenyMsg() {
      return this.denyMsg;
   }

   public boolean isChat() {
      return this.chat;
   }

   public boolean isActionBar() {
      return this.actionBar;
   }

   public boolean isSoundOn() {
      return this.soundOn;
   }

   public Sound getSound() {
      return this.sound;
   }

   public boolean canUse(Player p, String cmd) {
      if (p.hasPermission("economysmpcore.system.whitelister.bypass")) {
         return true;
      } else {
         String label = cmd.toLowerCase();
         Set<String> def = (Set)this.groupCmds.getOrDefault("default", Collections.emptySet());
         if (def.contains(label)) {
            return true;
         } else {
            for(Map.Entry<String, Set<String>> e : this.groupCmds.entrySet()) {
               if (!((String)e.getKey()).equals("default")) {
                  List<String> perms = (List)this.groupPerms.getOrDefault(e.getKey(), Collections.emptyList());
                  if (!perms.isEmpty()) {
                     boolean ok = false;

                     for(String perm : perms) {
                        if (p.hasPermission(perm)) {
                           ok = true;
                           break;
                        }
                     }

                     if (ok && ((Set)e.getValue()).contains(label)) {
                        return true;
                     }
                  }
               }
            }

            return false;
         }
      }
   }

   public Set<String> getAllowed(Player p) {
      Set<String> out = new HashSet();
      if (p.hasPermission("economysmpcore.system.whitelister.bypass")) {
         return out;
      } else {
         out.addAll((Collection)this.groupCmds.getOrDefault("default", Collections.emptySet()));

         for(Map.Entry<String, Set<String>> e : this.groupCmds.entrySet()) {
            if (!((String)e.getKey()).equals("default")) {
               List<String> perms = (List)this.groupPerms.getOrDefault(e.getKey(), Collections.emptyList());
               boolean ok = false;

               for(String perm : perms) {
                  if (p.hasPermission(perm)) {
                     ok = true;
                     break;
                  }
               }

               if (ok) {
                  out.addAll((Collection)e.getValue());
               }
            }
         }

         return out;
      }
   }
}
