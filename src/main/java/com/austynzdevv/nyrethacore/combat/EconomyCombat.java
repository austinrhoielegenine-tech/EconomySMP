package com.austynzdevv.nyrethacore.combat;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.combat.command.CombatCommand;
import com.austynzdevv.nyrethacore.combat.listener.CombatListener;
import com.austynzdevv.nyrethacore.combat.listener.PlayerListener;
import com.austynzdevv.nyrethacore.combat.placeholders.CombatExpansion;
import com.austynzdevv.nyrethacore.combat.util.MessageUtil;
import org.bukkit.Bukkit;

public final class EconomyCombat extends SystemHost {
   private static EconomyCombat instance;
   private CombatManager combatManager;

   public EconomyCombat() {
      super("combat");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      MessageUtil.init(this);
      this.combatManager = new CombatManager(this);
      this.getServer().getPluginManager().registerEvents(new CombatListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this.getCore());
      new CombatCommand(this);
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new CombatExpansion(this)).register();
      }

      this.getLogger().info("EconomyCombat enabled.");
   }

   public void stop() {
      if (this.combatManager != null) {
         this.combatManager.shutdown();
      }

      this.getLogger().info("EconomyCombat disabled.");
   }

   public void reload() {
      this.reloadConfig();
      MessageUtil.init(this);
      this.combatManager.reload();
   }

   public static EconomyCombat getInstance() {
      return instance;
   }

   public CombatManager getCombatManager() {
      return this.combatManager;
   }
}
