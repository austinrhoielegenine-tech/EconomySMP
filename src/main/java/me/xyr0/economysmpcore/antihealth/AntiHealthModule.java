package me.xyr0.economysmpcore.antihealth;

import me.xyr0.economysmpcore.EconomySMPcore;
import me.xyr0.economysmpcore.Module;
import org.bukkit.Bukkit;

public class AntiHealthModule implements Module {
   private final EconomySMPcore plugin;
   private Object handle;

   public AntiHealthModule(EconomySMPcore plugin) {
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
            Class<?> impl = Class.forName("me.xyr0.economysmpcore.antihealth.AntiHealthProtocol");
            this.handle = impl.getConstructor(EconomySMPcore.class).newInstance(this.plugin);
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
