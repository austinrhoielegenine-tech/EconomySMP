package me.xyr0.astralsmpcore.billford;

import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.billford.command.BillfordCommand;
import me.xyr0.astralsmpcore.billford.listener.GuiListener;
import me.xyr0.astralsmpcore.billford.placeholders.BillfordPlaceholders;
import me.xyr0.astralsmpcore.util.YamlMessages;

public final class AstralBillford extends SystemHost {
   private static AstralBillford instance;
   private YamlMessages messages;
   private BillfordManager billford;

   public AstralBillford() {
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

      this.getLogger().info("AstralBillford enabled.");
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

   public static AstralBillford get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public BillfordManager getBillford() {
      return this.billford;
   }
}
