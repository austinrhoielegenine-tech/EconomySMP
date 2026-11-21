package com.austynzdevv.economysmp.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public final class Tasks {
   private static final boolean FOLIA;
   private static final AtomicInteger IDS = new AtomicInteger(1);
   private static final Map<Integer, Object> HANDLES = new ConcurrentHashMap();
   private static final Map<Integer, AtomicBoolean> FLAGS = new ConcurrentHashMap();

   private Tasks() {
   }

   public static boolean isFolia() {
      return FOLIA;
   }

   public static void run(Plugin plugin, Runnable task) {
      if (plugin != null && task != null) {
         if (FOLIA) {
            try {
               Bukkit.getGlobalRegionScheduler().execute(plugin, () -> safeRun(task));
               return;
            } catch (Throwable var3) {
            }
         }

         Bukkit.getScheduler().runTask(plugin, () -> safeRun(task));
      }
   }

   public static void runAsync(Plugin plugin, Runnable task) {
      if (plugin != null && task != null) {
         if (FOLIA) {
            try {
               Bukkit.getAsyncScheduler().runNow(plugin, (t) -> safeRun(task));
               return;
            } catch (Throwable var3) {
            }
         }

         Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> safeRun(task));
      }
   }

   public static int runLater(Plugin plugin, Runnable task, long delayTicks) {
      if (plugin != null && task != null) {
         long delay = Math.max(1L, delayTicks);
         int id = IDS.getAndIncrement();
         AtomicBoolean flag = new AtomicBoolean(false);
         FLAGS.put(id, flag);
         Runnable wrap = () -> {
            if (!flag.get()) {
               safeRun(task);
            }
         };
         if (FOLIA) {
            try {
               Object scheduled = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (t) -> {
                  if (!flag.get()) {
                     safeRun(task);
                  }
               }, delay);
               HANDLES.put(id, scheduled);
               return id;
            } catch (Throwable var10) {
            }
         }

         int bukkitId = Bukkit.getScheduler().runTaskLater(plugin, wrap, delay).getTaskId();
         HANDLES.put(id, bukkitId);
         return id;
      } else {
         return -1;
      }
   }

   public static int runTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
      if (plugin != null && task != null) {
         long delay = Math.max(1L, delayTicks);
         long period = Math.max(1L, periodTicks);
         int id = IDS.getAndIncrement();
         AtomicBoolean flag = new AtomicBoolean(false);
         FLAGS.put(id, flag);
         if (FOLIA) {
            try {
               Object scheduled = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (t) -> {
                  if (flag.get()) {
                     try {
                        t.cancel();
                     } catch (Throwable var5) {
                     }

                  } else {
                     try {
                        task.run();
                     } catch (IllegalStateException var8) {
                        flag.set(true);

                        try {
                           t.cancel();
                        } catch (Throwable var7) {
                        }
                     } catch (Throwable var9) {
                        flag.set(true);

                        try {
                           t.cancel();
                        } catch (Throwable var6) {
                        }
                     }

                  }
               }, delay, period);
               HANDLES.put(id, scheduled);
               return id;
            } catch (Throwable var13) {
            }
         }

         int bukkitId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!flag.get()) {
               try {
                  task.run();
               } catch (IllegalStateException var6) {
                  flag.set(true);

                  try {
                     Bukkit.getScheduler().cancelTask(HANDLES.containsKey(id) && HANDLES.get(id) instanceof Integer ? (Integer)HANDLES.get(id) : -1);
                  } catch (Throwable var5) {
                  }
               }

            }
         }, delay, period).getTaskId();
         HANDLES.put(id, bukkitId);
         return id;
      } else {
         return -1;
      }
   }

   public static int runAsyncLater(Plugin plugin, Runnable task, long delayTicks) {
      if (plugin != null && task != null) {
         int id = IDS.getAndIncrement();
         AtomicBoolean flag = new AtomicBoolean(false);
         FLAGS.put(id, flag);
         if (FOLIA) {
            try {
               long ms = Math.max(1L, delayTicks) * 50L;
               Object scheduled = Bukkit.getAsyncScheduler().runDelayed(plugin, (t) -> {
                  if (!flag.get()) {
                     safeRun(task);
                  }
               }, ms, TimeUnit.MILLISECONDS);
               HANDLES.put(id, scheduled);
               return id;
            } catch (Throwable var9) {
            }
         }

         int bukkitId = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (!flag.get()) {
               safeRun(task);
            }
         }, Math.max(1L, delayTicks)).getTaskId();
         HANDLES.put(id, bukkitId);
         return id;
      } else {
         return -1;
      }
   }

   public static int runAsyncTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
      if (plugin != null && task != null) {
         int id = IDS.getAndIncrement();
         AtomicBoolean flag = new AtomicBoolean(false);
         FLAGS.put(id, flag);
         if (FOLIA) {
            try {
               long delayMs = Math.max(1L, delayTicks) * 50L;
               long periodMs = Math.max(1L, periodTicks) * 50L;
               Object scheduled = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (t) -> {
                  if (flag.get()) {
                     try {
                        t.cancel();
                     } catch (Throwable var5) {
                     }

                  } else {
                     try {
                        task.run();
                     } catch (Throwable var7) {
                        flag.set(true);

                        try {
                           t.cancel();
                        } catch (Throwable var6) {
                        }
                     }

                  }
               }, delayMs, periodMs, TimeUnit.MILLISECONDS);
               HANDLES.put(id, scheduled);
               return id;
            } catch (Throwable var13) {
            }
         }

         int bukkitId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!flag.get()) {
               safeRun(task);
            }
         }, Math.max(1L, delayTicks), Math.max(1L, periodTicks)).getTaskId();
         HANDLES.put(id, bukkitId);
         return id;
      } else {
         return -1;
      }
   }

   public static void runAtEntity(Plugin plugin, Entity entity, Runnable task) {
      if (plugin != null && task != null) {
         if (entity != null && entity.isValid()) {
            if (FOLIA) {
               try {
                  entity.getScheduler().run(plugin, (t) -> safeRun(task), (Runnable)null);
                  return;
               } catch (Throwable var4) {
               }
            }

            Bukkit.getScheduler().runTask(plugin, () -> safeRun(task));
         } else {
            run(plugin, task);
         }
      }
   }

   public static int runAtEntityLater(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
      if (plugin != null && task != null) {
         if (entity != null && entity.isValid()) {
            int id = IDS.getAndIncrement();
            AtomicBoolean flag = new AtomicBoolean(false);
            FLAGS.put(id, flag);
            if (FOLIA) {
               try {
                  Object scheduled = entity.getScheduler().runDelayed(plugin, (t) -> {
                     if (!flag.get()) {
                        safeRun(task);
                     }
                  }, (Runnable)null, Math.max(1L, delayTicks));
                  HANDLES.put(id, scheduled);
                  return id;
               } catch (Throwable var8) {
               }
            }

            int bukkitId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
               if (!flag.get()) {
                  safeRun(task);
               }
            }, Math.max(1L, delayTicks)).getTaskId();
            HANDLES.put(id, bukkitId);
            return id;
         } else {
            return runLater(plugin, task, delayTicks);
         }
      } else {
         return -1;
      }
   }

   public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
      if (plugin != null && task != null) {
         if (location != null && location.getWorld() != null) {
            if (FOLIA) {
               try {
                  Bukkit.getRegionScheduler().execute(plugin, location, () -> safeRun(task));
                  return;
               } catch (Throwable var4) {
               }
            }

            Bukkit.getScheduler().runTask(plugin, () -> safeRun(task));
         } else {
            run(plugin, task);
         }
      }
   }

   public static void cancel(int taskId) {
      if (taskId >= 0) {
         AtomicBoolean flag = (AtomicBoolean)FLAGS.remove(taskId);
         if (flag != null) {
            flag.set(true);
         }

         Object handle = HANDLES.remove(taskId);
         if (handle != null) {
            if (handle instanceof Integer) {
               Integer bukkitId = (Integer)handle;

               try {
                  Bukkit.getScheduler().cancelTask(bukkitId);
               } catch (Throwable var5) {
               }

            } else {
               try {
                  handle.getClass().getMethod("cancel").invoke(handle);
               } catch (Throwable var6) {
               }

            }
         }
      }
   }

   public static void cancelAll() {
      for(Integer id : (Integer[])HANDLES.keySet().toArray(new Integer[0])) {
         if (id != null) {
            cancel(id);
         }
      }

      HANDLES.clear();
      FLAGS.clear();

      try {
         Plugin pl = Bukkit.getPluginManager().getPlugin("EconomySMPcore");
         if (pl != null) {
            Bukkit.getScheduler().cancelTasks(pl);
            if (FOLIA) {
               try {
                  Bukkit.getGlobalRegionScheduler().cancelTasks(pl);
               } catch (Throwable var5) {
               }

               try {
                  Bukkit.getAsyncScheduler().cancelTasks(pl);
               } catch (Throwable var4) {
               }
            }
         }
      } catch (Throwable var6) {
      }

   }

   private static void safeRun(Runnable task) {
      try {
         task.run();
      } catch (IllegalStateException var4) {
      } catch (Throwable var5) {
         Throwable t = var5;

         try {
            Bukkit.getLogger().warning("[EconomySMPcore] Task error: " + t.getMessage());
         } catch (Throwable var3) {
         }
      }

   }

   static {
      boolean f = false;

      try {
         Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
         f = true;
      } catch (Throwable var2) {
      }

      FOLIA = f;
   }
}
