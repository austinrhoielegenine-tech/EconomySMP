package com.austynzdevv.economysmp.shop;

import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.shop.command.AdminShopCommand;
import com.austynzdevv.economysmp.shop.command.ShopCommand;
import com.austynzdevv.economysmp.shop.listener.GuiListener;
import com.austynzdevv.economysmp.shop.placeholders.ShopPlaceholders;
import com.austynzdevv.economysmp.util.YamlMessages;

public final class EconomyShop extends SystemHost {
   private static EconomyShop instance;
   private EconomyService economy;
   private SpendManager spend;
   private YamlMessages messages;
   private ShopRegistry shops;

   public EconomyShop() {
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

      this.getLogger().info("EconomyShop enabled.");
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

   public static EconomyShop get() {
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
