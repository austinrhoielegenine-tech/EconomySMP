package com.austynzdevv.nyrethacore.rtpq;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class QueueDialog {
   private final EconomyRTPQ plugin;
   private static Boolean paperDialogAvailable = null;

   public QueueDialog(EconomyRTPQ plugin) {
      this.plugin = plugin;
   }

   public void openLeaveDialog(Player player) {
      this.plugin.getQueueManager().removePlayer(player);
      player.sendMessage(Component.text("You left the RTP queue."));
   }

   public void openJoinDialog(Player player) {
      Map<String, WorldConfig> worlds = this.plugin.getWorldManager().getWorlds();
      if (worlds != null && !worlds.isEmpty()) {
         if (worlds.size() == 1) {
            String only = (String)worlds.keySet().iterator().next();
            this.plugin.getQueueManager().addPlayer(player, only);
         } else if (!this.tryOpenPaperDialog(player, worlds)) {
            player.sendMessage("§8[§bRTP Queue§8] §7Pick a world:");

            for(String name : worlds.keySet()) {
               player.sendMessage("§7 - §b" + name + " §8(/rtpqueue join " + name + ")");
            }

         }
      } else {
         this.plugin.getQueueManager().addPlayer(player, "default");
      }
   }

   private boolean tryOpenPaperDialog(Player player, Map<String, WorldConfig> worlds) {
      if (paperDialogAvailable != null && !paperDialogAvailable) {
         return false;
      } else {
         try {
            Class<?> dialogClass = Class.forName("io.papermc.paper.dialog.Dialog");
            Class<?> builderClass = null;

            for(String name : List.of("io.papermc.paper.dialog.DialogBuilder", "io.papermc.paper.dialog.Dialog$Builder")) {
               try {
                  builderClass = Class.forName(name);
                  break;
               } catch (ClassNotFoundException var14) {
               }
            }

            if (builderClass == null) {
               Method show = Player.class.getMethod("showDialog", dialogClass);
               paperDialogAvailable = false;
               return false;
            } else {
               Object builder = builderClass.getDeclaredConstructor().newInstance();
               tryInvoke(builder, "title", Component.text("RTP Queue"));
               tryInvoke(builder, "body", Component.text("Select a world to queue into"));

               for(String worldName : worlds.keySet()) {
                  try {
                     Method button = null;

                     for(Method m : builderClass.getMethods()) {
                        if (m.getName().equals("button") || m.getName().equals("addButton")) {
                           button = m;
                           break;
                        }
                     }

                     if (button != null && button.getParameterCount() >= 1) {
                        button.invoke(builder, Component.text(worldName));
                     }
                  } catch (Exception var13) {
                  }
               }

               Method build = builderClass.getMethod("build");
               Object dialog = build.invoke(builder);
               Method show = Player.class.getMethod("showDialog", dialogClass);
               show.invoke(player, dialog);
               paperDialogAvailable = true;
               player.sendMessage("§7Or type §b/rtpqueue join <world>");
               return true;
            }
         } catch (Exception var15) {
            paperDialogAvailable = false;
            return false;
         }
      }
   }

   private static void tryInvoke(Object target, String method, Object arg) {
      try {
         for(Method m : target.getClass().getMethods()) {
            if (m.getName().equals(method) && m.getParameterCount() == 1) {
               m.invoke(target, arg);
               return;
            }
         }
      } catch (Exception var7) {
      }

   }
}
