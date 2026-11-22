package com.austynzdevv.nyrethacore.keyall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import com.austynzdevv.nyrethacore.crates.EconomyCrates;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class KeyManager {
   private final EconomyKeyall plugin;
   private final List<KeyEntry> keys = new ArrayList();

   public KeyManager(EconomyKeyall plugin) {
      this.plugin = plugin;
      this.reload();
   }

   public void reload() {
      this.keys.clear();
      ConfigurationSection section = this.plugin.getConfig().getConfigurationSection("keys");
      if (section != null) {
         for(String id : section.getKeys(false)) {
            ConfigurationSection key = section.getConfigurationSection(id);
            if (key != null) {
               int chance = key.getInt("chance", 1);
               String display = key.getString("display", id);
               String command = key.getString("command", "");
               this.keys.add(new KeyEntry(id, chance, display, command));
            }
         }

      }
   }

   public boolean isEmpty() {
      return this.keys.isEmpty();
   }

   public KeyEntry pickRandom() {
      if (this.keys.isEmpty()) {
         return null;
      } else {
         int total = 0;

         for(KeyEntry entry : this.keys) {
            total += entry.getChance();
         }

         if (total <= 0) {
            return (KeyEntry)this.keys.get(ThreadLocalRandom.current().nextInt(this.keys.size()));
         } else {
            int roll = ThreadLocalRandom.current().nextInt(total) + 1;
            int cursor = 0;

            for(KeyEntry entry : this.keys) {
               cursor += entry.getChance();
               if (roll <= cursor) {
                  return entry;
               }
            }

            return (KeyEntry)this.keys.get(this.keys.size() - 1);
         }
      }
   }

   public void runKeyall() {
      this.runKeyall((Player)null);
   }

   public void runKeyall(Player only) {
      KeyEntry entry = this.pickRandom();
      if (entry != null) {
         this.execute(entry, only);
      }
   }

   public void execute(KeyEntry entry, Player only) {
      String display = entry.getDisplay();
      String actionbar = this.plugin.getConfig().getString("reward.actionbar", "");
      String chat = this.plugin.getConfig().getString("reward.chat-message", "");
      String clickCmd = this.plugin.getConfig().getString("reward.click-command", "/warp crates");
      if (clickCmd == null) {
         clickCmd = "/warp crates";
      }

      if (clickCmd.startsWith("/")) {
         clickCmd = clickCmd.substring(1);
      }

      if (only != null) {
         this.giveFromCommand(entry.getCommand(), only);
         this.sendReward(only, display, actionbar, chat, clickCmd);
         this.playSound(only);
      } else {
         for(Player player : Bukkit.getOnlinePlayers()) {
            this.giveFromCommand(entry.getCommand(), player);
            this.sendReward(player, display, actionbar, chat, clickCmd);
            this.playSound(player);
         }

      }
   }

   private void giveFromCommand(String cmd, Player player) {
      if (cmd != null && !cmd.isBlank() && player != null) {
         String raw = cmd.replace("%player%", player.getName()).trim();
         String[] parts = raw.split("\\s+");

         try {
            if (parts.length >= 4 && (parts[0].equalsIgnoreCase("crate") || parts[0].equalsIgnoreCase("crates"))) {
               String sub = parts[1].toLowerCase();
               boolean virtual = true;
               int amt = 1;
               String crateId;
               if (sub.equals("keyall")) {
                  crateId = parts[2];
                  if (parts.length >= 4) {
                     virtual = !parts[3].equalsIgnoreCase("physical");
                  }

                  if (parts.length >= 5) {
                     try {
                        amt = Integer.parseInt(parts[4]);
                     } catch (Exception var12) {
                     }
                  }
               } else if (sub.equals("givekey") && parts.length >= 5) {
                  crateId = parts[3];
                  if (parts.length >= 5) {
                     virtual = !parts[4].equalsIgnoreCase("physical");
                  }

                  if (parts.length >= 6) {
                     try {
                        amt = Integer.parseInt(parts[5]);
                     } catch (Exception var11) {
                     }
                  }
               } else {
                  crateId = null;
               }

               if (crateId != null) {
                  EconomyCrates crates = EconomyCrates.get();
                  if (crates != null && crates.getData() != null) {
                     if (virtual) {
                        crates.getData().addVirtual(player.getUniqueId(), crateId, amt);
                     } else if (crates.getKeys() != null) {
                        player.getInventory().addItem(new ItemStack[]{crates.getKeys().createPhysical(crateId, amt)});
                     }

                     return;
                  }
               }
            }
         } catch (Exception t) {
            this.plugin.getLogger().warning("keyall give failed: " + t.getMessage());
         }

         try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), raw);
         } catch (Exception var10) {
         }

      }
   }

   private void sendReward(Player player, String keyReward, String actionbar, String chat, String clickCmd) {
      if (actionbar != null && !actionbar.isBlank()) {
         HotbarUtil.send(player, ColorUtil.parse(actionbar.replace("%key_reward%", keyReward)));
      }

      if (chat != null && !chat.isBlank()) {
         String msg = chat.replace("%key_reward%", keyReward).replace("%click_command%", clickCmd).replace("%player%", player.getName());

         for(String line : msg.split("\\n")) {
            player.sendMessage(ColorUtil.parse(line));
         }
      }

   }

   private void playSound(Player player) {
      if (this.plugin.getConfig().getBoolean("reward.sound.enabled", true)) {
         String type = this.plugin.getConfig().getString("reward.sound.type", "ENTITY_EXPERIENCE_ORB_PICKUP");
         float volume = (float)this.plugin.getConfig().getDouble("reward.sound.volume", (double)1.0F);
         float pitch = (float)this.plugin.getConfig().getDouble("reward.sound.pitch", (double)1.0F);

         try {
            Sound sound = Sound.valueOf(type);
            SoundUtil.play(player, player.getLocation(), sound, volume, pitch);
         } catch (Exception var6) {
         }

      }
   }
}
