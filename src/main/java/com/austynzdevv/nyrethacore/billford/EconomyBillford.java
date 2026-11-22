package com.austynzdevv.nyrethacore.billford;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.billford.command.BillfordCommand;
import com.austynzdevv.nyrethacore.billford.listener.GuiListener;
import com.austynzdevv.nyrethacore.billford.placeholders.BillfordPlaceholders;
import com.austynzdevv.nyrethacore.util.YamlMessages;

public final class EconomyBillford extends SystemHost {
   private static EconomyBillford instance;
   private YamlMessages messages;
   private BillfordManager billford;

   public EconomyBillford() {
      super("billford");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = new YamlMessages(this);
      this.billford = new BillfordManager(this);
      this.billford.startRotationTask();
      new BillfordCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         (new BillfordPlaceholders(this)).register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      this.getLogger().info("EconomyBillford enabled.");
   }

   public void stop() {
      if (this.billford != null) {
         this.billford.save();
         this.billford.stopRotationTask();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.billford.reload();
   }

   public static EconomyBillford get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public BillfordManager getBillford() {
      return this.billford;
   }
}
