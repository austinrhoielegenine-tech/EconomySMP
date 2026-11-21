package com.austynzdevv.novacore.hide.util;

import com.austynzdevv.novacore.hide.EconomyHide;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkinManager {
   private final EconomyHide plugin;

   public SkinManager(EconomyHide plugin) {
      this.plugin = plugin;
   }

   public void setSteveSkin(Player player) {
      if (player != null) {
         try {
            if (Bukkit.getPluginManager().getPlugin("SkinRestorer") != null || Bukkit.getPluginManager().getPlugin("SkinsRestorer") != null) {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + player.getName() + " Steve");
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sr set skin " + player.getName() + " Steve");
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skinrestorer set " + player.getName() + " Steve");
            }
         } catch (Exception var6) {
         }

         try {
            Class<?> api = Class.forName("net.skinsrestorer.api.SkinsRestorerProvider");
            Object sr = api.getMethod("get").invoke((Object)null);
            Object var4 = sr.getClass().getMethod("getSkinStorage").invoke(sr);
         } catch (Exception var5) {
         }

      }
   }

   public void restoreSkin(Player player) {
      if (player != null) {
         try {
            if (Bukkit.getPluginManager().getPlugin("SkinRestorer") != null || Bukkit.getPluginManager().getPlugin("SkinsRestorer") != null) {
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin clear " + player.getName());
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sr clear " + player.getName());
            }
         } catch (Exception var3) {
         }

      }
   }
}
