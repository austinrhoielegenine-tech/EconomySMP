package me.xyr0.economysmpcore.rtp;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.rtp.listener.GUIListener;
import me.xyr0.economysmpcore.rtp.util.ConfigUtil;
import me.xyr0.economysmpcore.rtp.util.MessageUtil;

public final class AstralRTP extends SystemHost {
   private static AstralRTP instance;
   private RTPManager rtpManager;
   private CooldownManager cooldownManager;

   public AstralRTP() {
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
      this.getLogger().info("AstralRTP v" + this.getDescription().getVersion() + " enabled");
   }

   public void stop() {
      this.getLogger().info("AstralRTP disabled");
   }

   public void reloadEverything() {
      this.reloadConfig();
      MessageUtil.reload(this);
      ConfigUtil.reloadAll(this);
      if (this.rtpManager != null) {
         this.rtpManager.reload();
      }

   }

   public static AstralRTP get() {
      return instance;
   }

   public static AstralRTP getInstance() {
      return instance;
   }

   public RTPManager getRTPManager() {
      return this.rtpManager;
   }

   public CooldownManager getCooldownManager() {
      return this.cooldownManager;
   }
}
