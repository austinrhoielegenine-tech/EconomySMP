package com.austynzdevv.novacore.punishment2;

import java.io.File;
import com.austynzdevv.novacore.NovaCore;
import com.austynzdevv.novacore.Module;

public class PunishmentModule implements Module {
   private final NovaCore plugin;
   private EconomyPunishment punishment;

   public PunishmentModule(NovaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "punishment";
   }

   public void onEnable() {
      File dir = new File(this.plugin.getDataFolder(), "punishment");
      dir.mkdirs();

      for(String f : new String[]{"config.yml", "messages.yml", "reasons.yml", "webhook.yml"}) {
         File out = new File(dir, f);
         if (!out.exists()) {
            try {
               this.plugin.saveResource("punishment/" + f, false);
            } catch (Exception var9) {
            }
         }
      }

      try {
         this.punishment = new EconomyPunishment(this.plugin);
         this.punishment.enable();
         this.plugin.getLogger().info("Punishment system enabled.");
      } catch (Exception t) {
         this.plugin.getLogger().warning("Punishment failed to start: " + t.getMessage());
         t.printStackTrace();
      }

   }

   public void onDisable() {
      if (this.punishment != null) {
         try {
            this.punishment.disable();
         } catch (Exception var2) {
         }
      }

   }

   public void reload() {
      this.onDisable();
      this.onEnable();
   }
}
