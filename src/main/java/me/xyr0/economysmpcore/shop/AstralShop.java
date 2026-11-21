package me.xyr0.economysmpcore.shop;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.shop.command.AdminShopCommand;
import me.xyr0.economysmpcore.shop.command.ShopCommand;
import me.xyr0.economysmpcore.shop.listener.GuiListener;
import me.xyr0.economysmpcore.shop.placeholders.ShopPlaceholders;
import me.xyr0.economysmpcore.util.YamlMessages;

public final class AstralShop extends SystemHost {
   private static AstralShop instance;
   private EconomyService economy;
   private SpendManager spend;
   private YamlMessages messages;
   private ShopRegistry shops;

   public AstralShop() {
      super("shop");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.economy = new EconomyService(this);
      if (!this.economy.setupVault()) {
         this.getLogger().warning("Using built-in/CoreEconomy for shop charges when available.");
      }

      this.messages = new YamlMessages(this);
      this.shops = new ShopRegistry(this);
      this.spend = new SpendManager(this);
      new ShopCommand(this);
      new AdminShopCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new ShopPlaceholders(this)).register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      this.getLogger().info("AstralShop enabled.");
   }

   public void stop() {
      if (this.spend != null) {
         this.spend.save();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.shops.reload();
   }

   public static AstralShop get() {
      return instance;
   }

   public EconomyService getEconomy() {
      return this.economy;
   }

   public SpendManager getSpend() {
      return this.spend;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public ShopRegistry getShops() {
      return this.shops;
   }
}
