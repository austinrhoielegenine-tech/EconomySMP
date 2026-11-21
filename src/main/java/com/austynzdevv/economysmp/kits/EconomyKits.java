package com.austynzdevv.economysmp.kits;

import java.io.File;
import com.austynzdevv.economysmp.EconomySMPcore;
import com.austynzdevv.economysmp.kits.gui.GuiManager;
import com.austynzdevv.economysmp.kits.listener.GuiListener;
import com.austynzdevv.economysmp.kits.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class EconomyKits {
   private EconomySMPcore core;
   private File dataFolder;
   private KitManager manager;
   private GuiManager guiManager;
   private GuiListener guiListener;
   private boolean enabled;

   public void init(EconomySMPcore core, File dataFolder) {
      this.core = core;
      this.dataFolder = dataFolder;
      if (!dataFolder.exists()) {
         dataFolder.mkdirs();
      }

      this.manager = new KitManager(this);
      this.guiManager = new GuiManager(this);
      this.guiListener = new GuiListener(this);
   }

   public void start() {
      this.enabled = true;
      this.manager.load();
      MessageUtil.reload(this.dataFolder);
      Bukkit.getPluginManager().registerEvents(this.guiListener, this.core);
   }

   public void stop() {
      this.enabled = false;
      HandlerList.unregisterAll(this.guiListener);
      this.manager.saveCooldowns();
   }

   public EconomySMPcore getCore() {
      return this.core;
   }

   public File getDataFolder() {
      return this.dataFolder;
   }

   public KitManager getManager() {
      return this.manager;
   }

   public GuiManager getGuiManager() {
      return this.guiManager;
   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
