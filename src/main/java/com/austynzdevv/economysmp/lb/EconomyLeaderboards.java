package com.austynzdevv.economysmp.lb;

import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.lb.command.LeaderboardCommand;
import com.austynzdevv.economysmp.lb.gui.LeaderboardMenus;
import com.austynzdevv.economysmp.lb.listener.GuiListener;
import com.austynzdevv.economysmp.lb.listener.StatsListener;
import com.austynzdevv.economysmp.lb.util.InputUtil;
import com.austynzdevv.economysmp.listener.ProtectedSignListener;
import com.austynzdevv.economysmp.util.Tasks;
import com.austynzdevv.economysmp.util.YamlMessages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyLeaderboards extends SystemHost {
   private static EconomyLeaderboards instance;
   private YamlMessages messages;
   private StatsManager stats;
   private Economy economy;

   public EconomyLeaderboards() {
      super("lb");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = new YamlMessages(this);
      this.stats = new StatsManager(this);
      this.setupEconomy();
      LeaderboardMenus.loadGuis(this);
      new LeaderboardCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ProtectedSignListener(InputUtil::isProtectedSign), this.getCore());
      this.getServer().getPluginManager().registerEvents(new StatsListener(this), this.getCore());
      Tasks.runAsyncTimer(this.getCore(), () -> this.stats.refreshCache(), 40L, 1200L);
      Tasks.runTimer(this.getCore(), () -> this.stats.save(), 2400L, 2400L);
      this.getLogger().info("EconomyLeaderboards enabled.");
   }

   public void stop() {
      if (this.stats != null) {
         this.stats.save();
      }

      instance = null;
   }

   private void setupEconomy() {
      if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
         RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
         if (rsp != null) {
            this.economy = (Economy)rsp.getProvider();
         }

      }
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.stats.reload();
      this.stats.refreshCache();
   }

   public static EconomyLeaderboards get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public StatsManager getStats() {
      return this.stats;
   }

   public Economy getEconomy() {
      return this.economy;
   }
}
