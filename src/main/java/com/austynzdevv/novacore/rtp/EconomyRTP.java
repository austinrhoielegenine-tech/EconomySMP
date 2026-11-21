package com.austynzdevv.novacore.rtp;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.rtp.listener.GUIListener;
import com.austynzdevv.novacore.rtp.util.ConfigUtil;
import com.austynzdevv.novacore.rtp.util.MessageUtil;

public final class EconomyRTP extends SystemHost {
   private static EconomyRTP instance;
   private RTPManager rtpManager;
   private CooldownManager cooldownManager;

   public EconomyRTP() {
      super("rtp");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.saveResource("messages.yml", false);
      this.saveResource("guis.yml", false);
      MessageUtil.reload(this);
      ConfigUtil.reloadAll(this);
      this.rtpManager = new RTPManager(this);
      this.cooldownManager = new CooldownManager();
      this.getServer().getPluginManager().registerEvents(new GUIListener(this), this.getCore());
      this.getLogger().info("EconomyRTP v" + this.getDescription().getVersion() + " enabled");
   }

   public void stop() {
      this.getLogger().info("EconomyRTP disabled");
   }

   public void reloadEverything() {
      this.reloadConfig();
      MessageUtil.reload(this);
      ConfigUtil.reloadAll(this);
      if (this.rtpManager != null) {
         this.rtpManager.reload();
      }

   }

   public static EconomyRTP get() {
      return instance;
   }

   public static EconomyRTP getInstance() {
      return instance;
   }

   public RTPManager getRTPManager() {
      return this.rtpManager;
   }

   public CooldownManager getCooldownManager() {
      return this.cooldownManager;
   }
}
