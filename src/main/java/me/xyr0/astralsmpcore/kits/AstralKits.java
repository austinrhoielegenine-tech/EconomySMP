package me.xyr0.astralsmpcore.kits;

import java.io.File;
import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.kits.gui.GuiManager;
import me.xyr0.astralsmpcore.kits.listener.GuiListener;
import me.xyr0.astralsmpcore.kits.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class AstralKits {
   private AstralSMPcore core;
   private File dataFolder;
   private KitManager manager;
   private GuiManager guiManager;
   private GuiListener guiListener;
   private boolean enabled;

   public void init(AstralSMPcore core, File dataFolder) {
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

   public AstralSMPcore getCore() {
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
