package com.austynzdevv.nyrethacore.combat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.nyrethacore.combat.util.MessageUtil;
import com.austynzdevv.nyrethacore.util.HotbarUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import com.austynzdevv.nyrethacore.util.TextUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class CombatManager {
   private final EconomyCombat plugin;
   private final Map<UUID, Long> combatUntil = new ConcurrentHashMap();
   private final Map<UUID, Long> rewardCooldown = new ConcurrentHashMap();
   private int actionbarTask = -1;
   private int duration;
   private boolean actionbarEnabled;
   private int actionbarInterval;

   public CombatManager(EconomyCombat plugin) {
      this.plugin = plugin;
      this.reload();
      this.startActionbar();
   }

   public void reload() {
      this.duration = this.plugin.getConfig().getInt("settings.combat-duration", 20);
      this.actionbarEnabled = this.plugin.getConfig().getBoolean("settings.actionbar-enabled", true);
      this.actionbarInterval = this.plugin.getConfig().getInt("settings.actionbar-interval", 10);
      if (this.actionbarTask != -1) {
         Tasks.cancel(this.actionbarTask);
         this.actionbarTask = -1;
      }

      this.startActionbar();
   }

   private void startActionbar() {
      if (this.actionbarEnabled) {
         this.actionbarTask = Tasks.runTimer(this.plugin.getCore(), () -> {
            long now = System.currentTimeMillis();

            for(Map.Entry<UUID, Long> e : this.combatUntil.entrySet()) {
               Player p = Bukkit.getPlayer((UUID)e.getKey());
               if (p != null && p.isOnline()) {
                  long left = ((Long)e.getValue() - now) / 1000L;
                  if (left <= 0L) {
                     this.leaveCombat(p, true);
                  } else {
                     String msg = "&7Combat: &#37BFF9" + left + "s";
                     HotbarUtil.send(p, TextUtil.component(msg));
                  }
               }
            }

         }, (long)this.actionbarInterval, (long)this.actionbarInterval);
      }
   }

   public void tag(Player player) {
      if (!player.hasPermission("nyrethacorecore.system.combat.bypass")) {
         boolean was = this.isInCombat(player);
         long until = System.currentTimeMillis() + (long)this.duration * 1000L;
         this.combatUntil.put(player.getUniqueId(), until);
         if (!was) {
            MessageUtil.send(player, "enter-combat");
            this.playSound(player, "enter-combat");
         }

      }
   }

   public void tagBoth(Player a, Player b) {
      if (this.plugin.getConfig().getBoolean("settings.tag-attacker", true)) {
         this.tag(a);
      }

      if (this.plugin.getConfig().getBoolean("settings.tag-victim", true)) {
         this.tag(b);
      }

   }

   public void leaveCombat(Player player, boolean notify) {
      if (this.combatUntil.remove(player.getUniqueId()) != null && notify) {
         MessageUtil.send(player, "leave-combat");
         this.playSound(player, "leave-combat");
         player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageUtil.color("&aYou are no longer in combat")));
      }

   }

   public boolean isInCombat(Player player) {
      Long until = (Long)this.combatUntil.get(player.getUniqueId());
      if (until == null) {
         return false;
      } else if (System.currentTimeMillis() >= until) {
         this.combatUntil.remove(player.getUniqueId());
         return false;
      } else {
         return true;
      }
   }

   public int getTimeLeft(Player player) {
      Long until = (Long)this.combatUntil.get(player.getUniqueId());
      if (until == null) {
         return 0;
      } else {
         long left = (until - System.currentTimeMillis()) / 1000L;
         return (int)Math.max(0L, left);
      }
   }

   public boolean canReward(Player killer) {
      if (!this.plugin.getConfig().getBoolean("kill-reward.enabled", true)) {
         return false;
      } else {
         Long last = (Long)this.rewardCooldown.get(killer.getUniqueId());
         int cd = this.plugin.getConfig().getInt("kill-reward.cooldown", 30);
         return last == null || System.currentTimeMillis() - last >= (long)cd * 1000L;
      }
   }

   public void applyReward(Player killer) {
      if (!this.canReward(killer)) {
         MessageUtil.send(killer, "already-reward");
      } else {
         this.rewardCooldown.put(killer.getUniqueId(), System.currentTimeMillis());

         for(String cmd : this.plugin.getConfig().getStringList("kill-reward.commands")) {
            String parsed = cmd.replace("%killer%", killer.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
         }

      }
   }

   public void playSound(Player player, String path) {
      String name = this.plugin.getConfig().getString("sounds." + path);
      if (name != null && !name.isEmpty()) {
         try {
            Sound sound = Sound.valueOf(name);
            SoundUtil.play(player, player.getLocation(), sound, 1.0F, 1.0F);
         } catch (IllegalArgumentException var5) {
         }

      }
   }

   public void shutdown() {
      if (this.actionbarTask != -1) {
         Tasks.cancel(this.actionbarTask);
         this.actionbarTask = -1;
      }

      this.combatUntil.clear();
      this.rewardCooldown.clear();
   }

   public Map<UUID, Long> getCombatMap() {
      return this.combatUntil;
   }
}
