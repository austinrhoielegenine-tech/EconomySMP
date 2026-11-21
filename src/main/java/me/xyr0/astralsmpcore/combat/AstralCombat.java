package me.xyr0.astralsmpcore.combat;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.combat.command.CombatCommand;
import me.xyr0.astralsmpcore.combat.listener.CombatListener;
import me.xyr0.astralsmpcore.combat.listener.PlayerListener;
import me.xyr0.astralsmpcore.combat.placeholders.CombatExpansion;
import me.xyr0.astralsmpcore.combat.util.MessageUtil;
import org.bukkit.Bukkit;

public final class AstralCombat extends SystemHost {
   private static AstralCombat instance;
   private CombatManager combatManager;

   public AstralCombat() {
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

      this.getLogger().info("AstralCombat enabled.");
   }

   public void stop() {
      if (this.combatManager != null) {
         this.combatManager.shutdown();
      }

      this.getLogger().info("AstralCombat disabled.");
   }

   public void reload() {
      this.reloadConfig();
      MessageUtil.init(this);
      this.combatManager.reload();
   }

   public static AstralCombat getInstance() {
      return instance;
   }

   public CombatManager getCombatManager() {
      return this.combatManager;
   }
}
