package me.xyr0.astralsmpcore.bounties;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.bounties.command.BountyCommand;
import me.xyr0.astralsmpcore.bounties.gui.BountyMenus;
import me.xyr0.astralsmpcore.bounties.listener.GuiListener;
import me.xyr0.astralsmpcore.bounties.listener.KillListener;
import me.xyr0.astralsmpcore.bounties.util.InputUtil;
import me.xyr0.astralsmpcore.listener.ProtectedSignListener;
import me.xyr0.astralsmpcore.util.YamlMessages;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class AstralBounties extends SystemHost {
   private static AstralBounties instance;
   private YamlMessages messages;
   private BountyManager bounties;
   private Economy economy;

   public AstralBounties() {
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
      this.getLogger().info("AstralBounties enabled.");
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

   public static AstralBounties get() {
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
