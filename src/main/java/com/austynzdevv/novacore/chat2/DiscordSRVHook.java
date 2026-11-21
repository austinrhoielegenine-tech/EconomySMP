package com.austynzdevv.novacore.chat2;

import org.bukkit.Bukkit;

public class DiscordSRVHook {
   private final EconomyChat plugin;
   private boolean enabled;

   public DiscordSRVHook(EconomyChat plugin) {
      this.plugin = plugin;
      this.setup();
   }

   private void setup() {
      this.enabled = this.plugin.getConfigManager().isModuleEnabled("discordsrv") && Bukkit.getPluginManager().isPluginEnabled("DiscordSRV");
      if (this.enabled) {
         this.plugin.getLogger().info("DiscordSRV hook enabled.");
      } else {
         this.plugin.getLogger().info("DiscordSRV hook disabled.");
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
