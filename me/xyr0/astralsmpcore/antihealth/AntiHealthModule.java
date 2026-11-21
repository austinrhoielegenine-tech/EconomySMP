package me.xyr0.astralsmpcore.antihealth;

import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.Module;
import org.bukkit.Bukkit;

public class AntiHealthModule implements Module {
   private final AstralSMPcore plugin;
   private Object handle;

   public AntiHealthModule(AstralSMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "anti-health-indicator";
   }

   public void onEnable() {
      if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
         this.plugin.getLogger().warning("ProtocolLib not found. Anti health indicator disabled.");
      } else {
         try {
            Class<?> impl = Class.forName("me.xyr0.astralsmpcore.antihealth.AntiHealthProtocol");
            this.handle = impl.getConstructor(AstralSMPcore.class).newInstance(this.plugin);
            impl.getMethod("start").invoke(this.handle);
         } catch (Exception t) {
            this.plugin.getLogger().warning("Anti health indicator failed to start: " + t.getMessage());
            this.handle = null;
         }

      }
   }

   public void onDisable() {
      if (this.handle != null) {
         try {
            this.handle.getClass().getMethod("stop").invoke(this.handle);
         } catch (Exception var2) {
         }

         this.handle = null;
      }
   }

   public void reload() {
      this.onDisable();
      this.onEnable();
   }
}
