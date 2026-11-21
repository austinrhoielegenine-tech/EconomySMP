package me.xyr0.economysmpcore.warps;

import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.util.YamlMessages;
import me.xyr0.economysmpcore.warps.command.WarpCommand;
import me.xyr0.economysmpcore.warps.gui.WarpMenus;
import me.xyr0.economysmpcore.warps.listener.GuiListener;
import me.xyr0.economysmpcore.warps.listener.PlayerListener;
import org.bukkit.command.PluginCommand;

public final class AstralWarps extends SystemHost {
   private static AstralWarps instance;
   private YamlMessages messages;
   private WarpManager warps;
   private TeleportManager teleports;

   public AstralWarps() {
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
      this.getLogger().info("AstralWarps enabled.");
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

   public static AstralWarps get() {
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
