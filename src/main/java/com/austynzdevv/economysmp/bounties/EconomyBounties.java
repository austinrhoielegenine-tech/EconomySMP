package com.austynzdevv.economysmp.bounties;

import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.bounties.command.BountyCommand;
import com.austynzdevv.economysmp.bounties.gui.BountyMenus;
import com.austynzdevv.economysmp.bounties.listener.GuiListener;
import com.austynzdevv.economysmp.bounties.listener.KillListener;
import com.austynzdevv.economysmp.bounties.util.InputUtil;
import com.austynzdevv.economysmp.listener.ProtectedSignListener;
import com.austynzdevv.economysmp.util.YamlMessages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyBounties extends SystemHost {
   private static EconomyBounties instance;
   private YamlMessages messages;
   private BountyManager bounties;
   private Economy economy;

   public EconomyBounties() {
      super("bounties");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      if (!this.setupEconomy()) {
         this.getLogger().warning("Using built-in CoreEconomy for bounties.");
         this.getLogger().warning("[system] init warning (continuing)");
      }

      this.messages = new YamlMessages(this);
      this.bounties = new BountyManager(this);
      BountyMenus.load(this);
      new BountyCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ProtectedSignListener(InputUtil::isProtectedSign), this.getCore());
      this.getServer().getPluginManager().registerEvents(new KillListener(this), this.getCore());
      this.getLogger().info("EconomyBounties enabled.");
   }

   public void stop() {
      if (this.bounties != null) {
         this.bounties.save();
      }

      instance = null;
   }

   private boolean setupEconomy() {
      if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
         return false;
      } else {
         RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
         if (rsp == null) {
            return false;
         } else {
            this.economy = (Economy)rsp.getProvider();
            return this.economy != null;
         }
      }
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.bounties.reload();
      BountyMenus.load(this);
   }

   public static EconomyBounties get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public BountyManager getBounties() {
      return this.bounties;
   }

   public Economy getEconomy() {
      return this.economy;
   }
}
