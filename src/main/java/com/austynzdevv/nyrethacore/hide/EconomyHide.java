package com.austynzdevv.nyrethacore.hide;

import com.austynzdevv.nyrethacore.SystemHost;
import com.austynzdevv.nyrethacore.hide.listener.PlayerJoinListener;
import com.austynzdevv.nyrethacore.hide.util.SkinManager;

public class EconomyHide extends SystemHost {
   private static EconomyHide instance;
   private DataManager dataManager;
   private HideManager hideManager;
   private NameManager nameManager;
   private NameTagManager nameTagManager;
   private SkinManager skinManager;

   public EconomyHide() {
      super("hide");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.dataManager = new DataManager(this);
      this.nameManager = new NameManager(this);
      this.nameTagManager = new NameTagManager(this);
      this.skinManager = new SkinManager(this);
      this.hideManager = new HideManager(this);
      this.hideManager.load();
      this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
      this.getLogger().info("EconomyHide has been enabled!");
   }

   public void stop() {
      if (this.hideManager != null) {
         this.hideManager.save();
      }

      if (this.dataManager != null) {
         this.dataManager.save();
      }

   }

   public static EconomyHide getInstance() {
      return instance;
   }

   public DataManager getDataManager() {
      return this.dataManager;
   }

   public HideManager getHideManager() {
      return this.hideManager;
   }

   public NameManager getNameManager() {
      return this.nameManager;
   }

   public NameTagManager getNameTagManager() {
      return this.nameTagManager;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }
}
