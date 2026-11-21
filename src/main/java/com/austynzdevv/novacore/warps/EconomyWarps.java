package com.austynzdevv.novacore.warps;

import com.austynzdevv.novacore.SystemHost;
import com.austynzdevv.novacore.util.YamlMessages;
import com.austynzdevv.novacore.warps.command.WarpCommand;
import com.austynzdevv.novacore.warps.gui.WarpMenus;
import com.austynzdevv.novacore.warps.listener.GuiListener;
import com.austynzdevv.novacore.warps.listener.PlayerListener;
import org.bukkit.command.PluginCommand;

public final class EconomyWarps extends SystemHost {
   private static EconomyWarps instance;
   private YamlMessages messages;
   private WarpManager warps;
   private TeleportManager teleports;

   public EconomyWarps() {
      super("warps");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.messages = new YamlMessages(this);
      this.warps = new WarpManager(this);
      this.teleports = new TeleportManager(this);
      WarpMenus.load(this);
      WarpCommand cmd = new WarpCommand(this);

      for(String name : new String[]{"warp", "setwarp", "delwarp", "spawn", "setspawn", "delspawn", "afk", "setafk", "delafk", "warpreload"}) {
         PluginCommand c = this.getCommand(name);
         if (c != null) {
            c.setExecutor(cmd);
            c.setTabCompleter(cmd);
         }
      }

      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this.getCore());
      this.getLogger().info("EconomyWarps enabled.");
   }

   public void stop() {
      if (this.warps != null) {
         this.warps.saveAll();
      }

      instance = null;
   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.warps.reload();
      WarpMenus.load(this);
   }

   public static EconomyWarps get() {
      return instance;
   }

   public YamlMessages getMessages() {
      return this.messages;
   }

   public WarpManager getWarps() {
      return this.warps;
   }

   public TeleportManager getTeleports() {
      return this.teleports;
   }
}
