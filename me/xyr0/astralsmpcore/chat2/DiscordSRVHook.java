package me.xyr0.astralsmpcore.chat2;

import org.bukkit.Bukkit;

public class DiscordSRVHook {
   private final AstralChat plugin;
   private boolean enabled;

   public DiscordSRVHook(AstralChat plugin) {
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
