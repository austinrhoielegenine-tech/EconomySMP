package com.austynzdevv.economysmp;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MsgModule implements Module {
   private final EconomySMP plugin;
   private FileConfiguration cfg;
   private final Map<UUID, UUID> lastMsg = new HashMap();
   private final Set<UUID> toggledOff = new HashSet();
   private final Map<UUID, Set<UUID>> ignores = new HashMap();

   public MsgModule(EconomySMP plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "msg";
   }

   public void onEnable() {
      this.reload();
      this.loadPersisted();
      CommandRegistry reg = this.plugin.commands();
      reg.registerHandler("msg", this::msg);
      reg.registerHandler("reply", this::reply);
      reg.registerHandler("msgtoggle", this::toggle);
      reg.registerHandler("msgignore", this::ignore);
      reg.registerHandler("msgunignore", this::unignore);
   }

   public void onDisable() {
      this.lastMsg.clear();
   }

   private void loadPersisted() {
      File f = new File(this.plugin.getDataFolder(), "settings-data.yml");
      if (f.exists()) {
         FileConfiguration data = YamlConfiguration.loadConfiguration(f);
         ConfigurationSection root = data.getConfigurationSection("players");
         if (root != null) {
            for(String raw : root.getKeys(false)) {
               try {
                  UUID id = UUID.fromString(raw);
                  if (!data.getBoolean("players." + raw + ".private_messages", true)) {
                     this.toggledOff.add(id);
                  }
               } catch (IllegalArgumentException var7) {
               }
            }

         }
      }
   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "msg/config.yml");
      if (!f.exists()) {
         this.plugin.saveResource("msg/config.yml", false);
      }

      this.cfg = YamlConfiguration.loadConfiguration(f);
   }

   private boolean msg(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (args.length < 2) {
            p.sendMessage(this.c("messages.usage-msg"));
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t == null) {
               p.sendMessage(this.c("messages.no-target"));
               return true;
            } else if (t.getUniqueId().equals(p.getUniqueId())) {
               p.sendMessage(this.c("messages.self-message"));
               return true;
            } else if (this.toggledOff.contains(t.getUniqueId())) {
               p.sendMessage(this.c("messages.msg-toggle-player").replace("%player%", t.getName()));
               return true;
            } else if (((Set)this.ignores.getOrDefault(t.getUniqueId(), Set.of())).contains(p.getUniqueId())) {
               p.sendMessage(this.c("messages.msg-blocked").replace("%player%", t.getName()));
               return true;
            } else {
               String message = String.join(" ", (CharSequence[])Arrays.copyOfRange(args, 1, args.length));
               String format = this.c("messages.msg-format").replace("%sender%", p.getName()).replace("%receiver%", t.getName()).replace("%message%", message);
               p.sendMessage(format);
               t.sendMessage(format);
               this.lastMsg.put(p.getUniqueId(), t.getUniqueId());
               this.lastMsg.put(t.getUniqueId(), p.getUniqueId());
               return true;
            }
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   private boolean reply(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID targetId = (UUID)this.lastMsg.get(p.getUniqueId());
         if (targetId == null) {
            p.sendMessage(this.c("messages.no-reply"));
            return true;
         } else {
            Player t = Bukkit.getPlayer(targetId);
            if (t == null) {
               p.sendMessage(this.c("messages.no-target"));
               return true;
            } else if (args.length < 1) {
               p.sendMessage(this.c("messages.usage-msg"));
               return true;
            } else {
               return this.msg(p, this.prepend(t.getName(), args));
            }
         }
      } else {
         return true;
      }
   }

   private boolean toggle(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         boolean enabled = this.togglePlayer(p);
         p.sendMessage(this.c(enabled ? "messages.msg-toggle-on" : "messages.msg-toggle-off"));
         return true;
      } else {
         return true;
      }
   }

   public boolean togglePlayer(Player p) {
      UUID id = p.getUniqueId();
      boolean enabled;
      if (this.toggledOff.remove(id)) {
         enabled = true;
      } else {
         this.toggledOff.add(id);
         enabled = false;
      }

      File f = new File(this.plugin.getDataFolder(), "settings-data.yml");
      FileConfiguration data = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      data.set("players." + String.valueOf(id) + ".private_messages", enabled);

      try {
         data.save(f);
      } catch (Exception var7) {
      }

      return enabled;
   }

   private boolean ignore(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (args.length < 1) {
            p.sendMessage(this.c("messages.usage-ignore"));
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t == null) {
               p.sendMessage(this.c("messages.no-target"));
               return true;
            } else {
               ((Set)this.ignores.computeIfAbsent(p.getUniqueId(), (k) -> new HashSet())).add(t.getUniqueId());
               p.sendMessage(this.c("messages.ignored").replace("%player%", t.getName()));
               return true;
            }
         }
      } else {
         return true;
      }
   }

   private boolean unignore(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (args.length < 1) {
            p.sendMessage(this.c("messages.usage-ignore"));
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(args[0]);
            if (t == null) {
               p.sendMessage(this.c("messages.no-target"));
               return true;
            } else {
               Set<UUID> set = (Set)this.ignores.get(p.getUniqueId());
               if (set != null) {
                  set.remove(t.getUniqueId());
               }

               p.sendMessage(this.c("messages.unignored").replace("%player%", t.getName()));
               return true;
            }
         }
      } else {
         return true;
      }
   }

   private String[] prepend(String first, String[] args) {
      String[] out = new String[args.length + 1];
      out[0] = first;
      System.arraycopy(args, 0, out, 1, args.length);
      return out;
   }

   private String c(String path) {
      String s = this.cfg.getString(path, path);
      return TextUtil.color(s == null ? path : s);
   }

   public boolean isMsgDisabled(UUID id) {
      return this.toggledOff.contains(id);
   }
}
