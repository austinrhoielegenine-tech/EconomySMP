package me.xyr0.economysmpcore.homes;

import java.io.File;
import me.xyr0.economysmpcore.EconomySMPcore;
import me.xyr0.economysmpcore.CommandRegistry;
import me.xyr0.economysmpcore.Module;
import me.xyr0.economysmpcore.homes.command.HomeCommand;
import me.xyr0.economysmpcore.homes.listener.GUIListener;
import me.xyr0.economysmpcore.homes.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class HomesModule implements Module {
   private final EconomySMPcore core;
   private AstralHomes host;
   private HomeCommand command;
   private GUIListener guiListener;
   private PlayerListener playerListener;

   public HomesModule(EconomySMPcore core) {
      this.core = core;
   }

   public String getName() {
      return "homes";
   }

   public void onEnable() {
      this.host = new AstralHomes();
      this.host.init(this.core, new File(this.core.getDataFolder(), "homes"));
      this.host.start();
      this.command = new HomeCommand(this.host);
      this.guiListener = new GUIListener(this.host);
      this.playerListener = new PlayerListener(this.host);
      Bukkit.getPluginManager().registerEvents(this.guiListener, this.core);
      Bukkit.getPluginManager().registerEvents(this.playerListener, this.core);
      CommandRegistry reg = this.core.commands();
      reg.registerHandler("homes", this::home);
      reg.registerHandler("home", this::home);
      reg.registerHandler("sethome", this::sethome);
      reg.registerHandler("delhome", this::delhome);
   }

   public void onDisable() {
      if (this.host != null) {
         this.host.stop();
      }

      if (this.guiListener != null) {
         HandlerList.unregisterAll(this.guiListener);
      }

      if (this.playerListener != null) {
         HandlerList.unregisterAll(this.playerListener);
      }

   }

   public void reload() {
      if (this.host != null) {
         this.host.reloadPlugin();
      }

   }

   private boolean home(CommandSender sender, String[] args) {
      return this.command.onCommand(sender, new FakeCommand("homes"), "homes", args);
   }

   private boolean sethome(CommandSender sender, String[] args) {
      return this.command.onCommand(sender, new FakeCommand("sethome"), "sethome", args);
   }

   private boolean delhome(CommandSender sender, String[] args) {
      return this.command.onCommand(sender, new FakeCommand("delhome"), "delhome", args);
   }

   private static final class FakeCommand extends Command {
      FakeCommand(String name) {
         super(name);
      }

      public boolean execute(CommandSender sender, String commandLabel, String[] args) {
         return false;
      }
   }
}
