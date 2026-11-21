package me.xyr0.astralsmpcore.moderation.listener;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;
import me.xyr0.astralsmpcore.moderation.SusManager;
import me.xyr0.astralsmpcore.moderation.gui.SusMenus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public final class SusListener implements Listener {
   private final Plugin plugin;
   private final SusManager manager;

   public SusListener(Plugin plugin, SusManager manager) {
      this.plugin = plugin;
      this.manager = manager;
      this.hookKnownEvents();
   }

   private void hookKnownEvents() {
      this.hook("ac.grim.grimac.api.events.FlagEvent", SusManager.Anticheat.GRIM, "getPlayer", "getCheckName", "getVl");
      this.hook("ac.grim.grimac.events.FlagEvent", SusManager.Anticheat.GRIM, "getPlayer", "getCheckName", "getVl");
      this.hook("me.frep.vulcan.api.event.VulcanFlagEvent", SusManager.Anticheat.VULCAN, "getPlayer", "getCheck", "getViolationLevel");
      this.hook("me.frep.vulcan.spigot.api.event.VulcanFlagEvent", SusManager.Anticheat.VULCAN, "getPlayer", "getCheck", "getViolationLevel");
      this.hook("me.frep.vulcan.spigot.event.VulcanFlagEvent", SusManager.Anticheat.VULCAN, "getPlayer", "getCheck", "getVl");
      this.hook("com.github.retrooper.packetevents.util.MatrixFlagEvent", SusManager.Anticheat.MATRIX, "getPlayer", "getCheck", "getVl");
      this.hook("me.rerere.matrix.api.events.PlayerViolationEvent", SusManager.Anticheat.MATRIX, "getPlayer", "getHackType", "getViolations");
      this.hook("com.github.yannicklamprecht.matrix.api.event.PlayerViolationEvent", SusManager.Anticheat.MATRIX, "getPlayer", "getCheck", "getViolations");
      this.hook("com.totemguard.api.event.FlagEvent", SusManager.Anticheat.TOTEMGUARD, "getPlayer", "getCheck", "getVl");
      this.hook("com.totemguard.api.events.FlagEvent", SusManager.Anticheat.TOTEMGUARD, "getPlayer", "getCheckName", "getVl");
      this.hook("dev.totemguard.api.event.FlagEvent", SusManager.Anticheat.TOTEMGUARD, "getPlayer", "getCheck", "getVl");
      this.hook("com.angelguard.api.event.FlagEvent", SusManager.Anticheat.ANGELGUARD, "getPlayer", "getCheck", "getVl");
      this.hook("dev.angelguard.api.event.FlagEvent", SusManager.Anticheat.ANGELGUARD, "getPlayer", "getCheckName", "getVl");
      this.hook("me.angelguard.api.event.PlayerFlagEvent", SusManager.Anticheat.ANGELGUARD, "getPlayer", "getCheck", "getViolations");
   }

   private void hook(String className, SusManager.Anticheat ac, String playerMethod, String checkMethod, String vlMethod) {
      try {
         Class<? extends Event> eventClass = Class.forName(className);
         EventExecutor executor = (listener, event) -> {
            if (eventClass.isInstance(event)) {
               if (this.manager.isEnabled(ac)) {
                  try {
                     this.handle(event, ac, playerMethod, checkMethod, vlMethod);
                  } catch (Exception var9) {
                  }

               }
            }
         };
         Bukkit.getPluginManager().registerEvent(eventClass, this, EventPriority.MONITOR, executor, this.plugin, true);
      } catch (ClassNotFoundException var8) {
      } catch (Exception var9) {
      }

   }

   private void handle(Event event, SusManager.Anticheat ac, String playerMethod, String checkMethod, String vlMethod) throws Exception {
      Object playerObj = this.invoke(event, playerMethod);
      Player player = null;
      UUID id = null;
      String name = null;
      if (playerObj instanceof Player p) {
         player = p;
         id = p.getUniqueId();
         name = p.getName();
      } else if (playerObj != null) {
         try {
            Method getUniqueId = playerObj.getClass().getMethod("getUniqueId");
            Method getName = playerObj.getClass().getMethod("getName");
            Object uid = getUniqueId.invoke(playerObj);
            if (uid instanceof UUID) {
               UUID u = (UUID)uid;
               id = u;
            }

            Object n = getName.invoke(playerObj);
            if (n != null) {
               name = n.toString();
            }
         } catch (Exception var15) {
         }
      }

      if (id != null) {
         String check = this.stringify(this.invoke(event, checkMethod));
         if (check == null || check.isEmpty()) {
            check = "Unknown";
         }

         check = SusMenus.shortCheck(check);
         double vl = this.toDouble(this.invoke(event, vlMethod));
         if (player != null) {
            this.manager.addFlag(player, ac, check, vl);
         } else {
            this.manager.addFlag(id, name != null ? name : id.toString().substring(0, 8), ac, check, vl);
         }

      }
   }

   private Object invoke(Object target, String methodName) {
      if (target != null && methodName != null) {
         try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
         } catch (NoSuchMethodException var11) {
            for(String alt : this.alternates(methodName)) {
               try {
                  Method m = target.getClass().getMethod(alt);
                  return m.invoke(target);
               } catch (Exception var10) {
               }
            }

            try {
               for(Method m : target.getClass().getMethods()) {
                  if (m.getParameterCount() == 0 && m.getName().equalsIgnoreCase(methodName)) {
                     return m.invoke(target);
                  }
               }
            } catch (Exception var9) {
            }
         } catch (Exception var12) {
         }

         return null;
      } else {
         return null;
      }
   }

   private String[] alternates(String method) {
      String lower = method.toLowerCase(Locale.ROOT);
      if (lower.contains("check")) {
         return new String[]{"getCheck", "getCheckName", "getHackType", "getType", "check"};
      } else if (!lower.contains("vl") && !lower.contains("violation")) {
         return lower.contains("player") ? new String[]{"getPlayer", "player", "getUser"} : new String[0];
      } else {
         return new String[]{"getVl", "getVL", "getViolationLevel", "getViolations", "getVerbose", "vl"};
      }
   }

   private String stringify(Object o) {
      if (o == null) {
         return null;
      } else if (o instanceof String) {
         String s = (String)o;
         return s;
      } else {
         try {
            Method name = o.getClass().getMethod("getName");
            Object n = name.invoke(o);
            if (n != null) {
               return n.toString();
            }
         } catch (Exception var4) {
         }

         return o.toString();
      }
   }

   private double toDouble(Object o) {
      if (o == null) {
         return (double)0.0F;
      } else if (o instanceof Number) {
         Number n = (Number)o;
         return n.doubleValue();
      } else {
         try {
            return Double.parseDouble(o.toString().replaceAll("[^0-9.\\-]", ""));
         } catch (Exception var3) {
            return (double)0.0F;
         }
      }
   }
}
