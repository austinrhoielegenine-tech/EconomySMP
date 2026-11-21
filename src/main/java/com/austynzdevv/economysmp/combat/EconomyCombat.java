package com.austynzdevv.economysmp.combat;

import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.combat.command.CombatCommand;
import com.austynzdevv.economysmp.combat.listener.CombatListener;
import com.austynzdevv.economysmp.combat.listener.PlayerListener;
import com.austynzdevv.economysmp.combat.placeholders.CombatExpansion;
import com.austynzdevv.economysmp.combat.util.MessageUtil;
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
