package com.austynzdevv.economysmp.baltop;

import java.io.File;
import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.baltop.command.BaltopCommand;
import com.austynzdevv.economysmp.baltop.gui.BaltopMenus;
import com.austynzdevv.economysmp.baltop.gui.MenuSession;
import com.austynzdevv.economysmp.baltop.listener.GuiListener;
import com.austynzdevv.economysmp.baltop.util.InputUtil;
import com.austynzdevv.economysmp.listener.ProtectedSignListener;
import com.austynzdevv.economysmp.util.SearchInputListener;

public final class EconomyBaltop extends SystemHost {
   private static EconomyBaltop instance;
   private EconomyHook economy;
   private MessageManager messages;
   private BalanceCache cache;

   public EconomyBaltop() {
      super("baltop");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.saveResourceSafe("messages.yml");
      this.saveResourceSafe("gui/main-gui.yml");
      this.economy = new EconomyHook();
      if (!this.economy.setup()) {
         this.getLogger().severe("Economy not ready — using built-in when available.");
         this.getLogger().warning("[system] init warning (continuing)");
      }

      this.messages = new MessageManager(this);
      this.cache = new BalanceCache(this);
      this.cache.start();
      new BaltopCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new SearchInputListener(this.getCore(), (p) -> Boolean.TRUE.equals(MenuSession.CHAT_SEARCH.get(p.getUniqueId())), (p) -> InputUtil.isSignInput(p.getUniqueId()), (p) -> {
         MenuSession.CHAT_SEARCH.remove(p.getUniqueId());
         InputUtil.cleanup(p);
      }, (p, message) -> {
         MenuSession.SEARCH.put(p.getUniqueId(), message);
         BaltopMenus.open(p, 0);
      }, (p) -> {
         this.getMessages().send(p, "search-cancelled");
         BaltopMenus.open(p, 0);
      }, (p) -> MenuSession.clear(p.getUniqueId())), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ProtectedSignListener(InputUtil::isProtectedSign), this.getCore());
      this.getLogger().info("EconomyBaltop enabled.");
   }

   public void stop() {
      if (this.cache != null) {
         this.cache.stop();
      }

      instance = null;
   }

   private void saveResourceSafe(String name) {
      File out = new File(this.getDataFolder(), name);
      if (!out.exists()) {
         out.getParentFile().mkdirs();
         this.saveResource(name, false);
      }

   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.cache.stop();
      this.cache.start();
   }

   public static EconomyBaltop get() {
      return instance;
   }

   public EconomyHook getEconomy() {
      return this.economy;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public BalanceCache getCache() {
      return this.cache;
   }
}
