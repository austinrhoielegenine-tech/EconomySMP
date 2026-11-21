package com.austynzdevv.economysmp.auction;

import java.io.File;
import com.austynzdevv.economysmp.EconomySMP;
import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.auction.command.AuctionCommand;
import com.austynzdevv.economysmp.auction.gui.AuctionMenus;
import com.austynzdevv.economysmp.auction.gui.MenuSession;
import com.austynzdevv.economysmp.auction.listener.GuiListener;
import com.austynzdevv.economysmp.auction.util.InputUtil;
import com.austynzdevv.economysmp.listener.ProtectedSignListener;
import com.austynzdevv.economysmp.util.SearchInputListener;
import org.bukkit.entity.Player;

public final class EconomyAuction extends SystemHost {
   private static EconomyAuction instance;
   private AuctionManager auctions;
   private MessageManager messages;
   private EconomyHook economy;
   private BlockManager blocks;
   private WebhookManager webhooks;

   public EconomyAuction() {
      super("auction");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      if (!(new File(this.getDataFolder(), "messages.yml")).exists()) {
         this.saveResource("messages.yml", false);
      }

      this.saveGuiDefaults();
      this.reloadConfig();
      this.economy = new EconomyHook();
      if (!this.economy.setup()) {
         this.getLogger().severe("Economy not ready — using built-in when available.");
         this.getLogger().warning("[auction] init warning (continuing)");
      }

      this.messages = new MessageManager(this);
      if (!(new File(this.getDataFolder(), "webhook.yml")).exists()) {
         this.saveResource("webhook.yml", false);
      }

      this.webhooks = new WebhookManager(this);
      this.auctions = new AuctionManager(this);
      this.blocks = new BlockManager(this);
      this.auctions.startTasks();
      new AuctionCommand(this);

      try {
         ((EconomySMP)this.getCore()).commands().registerHandler("ahfastbuy", (s, a) -> {
            if (s instanceof Player p) {
               if (!p.hasPermission("economysmp.system.auction.fastbuy") && !p.hasPermission("economysmp.system.auction.use") && !p.isOp()) {
                  p.sendMessage("§cNo permission.");
                  return true;
               } else {
                  boolean next = !Boolean.TRUE.equals(MenuSession.FAST_BUY.get(p.getUniqueId()));
                  MenuSession.FAST_BUY.put(p.getUniqueId(), next);
                  p.sendMessage(next ? "§aAH fast buy §fenabled." : "§cAH fast buy §fdisabled.");
                  return true;
               }
            } else {
               return true;
            }
         });
      } catch (Exception var3) {
      }

      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new SearchInputListener(this.getCore(), (p) -> Boolean.TRUE.equals(MenuSession.CHAT_SEARCH.get(p.getUniqueId())), (p) -> InputUtil.isSignInput(p.getUniqueId()), (p) -> {
         MenuSession.CHAT_SEARCH.remove(p.getUniqueId());
         InputUtil.cleanup(p);
      }, (p, message) -> {
         MenuSession.SEARCH.put(p.getUniqueId(), message);
         AuctionMenus.openMain(p, 0);
      }, (p) -> {
         this.getMessages().send(p, "search-cancelled");
         AuctionMenus.openMain(p, 0);
      }, (p) -> MenuSession.clear(p.getUniqueId())), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ProtectedSignListener(InputUtil::isProtectedSign), this.getCore());
      this.getLogger().info("EconomyAuction enabled.");
   }

   public void stop() {
      if (this.auctions != null) {
         this.auctions.save();
      }

      instance = null;
   }

   private void saveGuiDefaults() {
      String[] files = new String[]{"gui/main.gui.yml", "gui/confirm.listing.gui.yml", "gui/confirm.purchase.gui.yml", "gui/my.items.gui.yml", "gui/transactions.gui.yml", "gui/shulker.view.gui.yml", "gui/admin.gui.yml"};

      for(String name : files) {
         File out = new File(this.getDataFolder(), name);
         if (!out.exists()) {
            out.getParentFile().mkdirs();
            this.saveResource(name, false);
         }
      }

   }

   public void reloadAll() {
      this.reloadConfig();
      if (this.messages != null) {
         this.messages.reload();
      }

      if (this.webhooks != null) {
         this.webhooks.reload();
      }

   }

   public static EconomyAuction get() {
      return instance;
   }

   public AuctionManager getAuctions() {
      return this.auctions;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public EconomyHook getEconomy() {
      return this.economy;
   }

   public BlockManager getBlocks() {
      return this.blocks;
   }

   public WebhookManager getWebhooks() {
      return this.webhooks;
   }
}
