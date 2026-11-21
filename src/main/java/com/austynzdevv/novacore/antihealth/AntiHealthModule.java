package com.austynzdevv.novacore.antihealth;

import com.austynzdevv.novacore.NovaCore;
import com.austynzdevv.novacore.Module;
import org.bukkit.Bukkit;

public class AntiHealthModule implements Module {
   private final NovaCore plugin;
   private Object handle;

   public AntiHealthModule(NovaCore plugin) {
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
            Class<?> impl = Class.forName("com.austynzdevv.novacore.antihealth.AntiHealthProtocol");
            this.handle = impl.getConstructor(NovaCore.class).newInstance(this.plugin);
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
