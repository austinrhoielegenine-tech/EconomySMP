package com.austynzdevv.economysmp.data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.plugin.java.JavaPlugin;

public final class DataPaths {
   private DataPaths() {
   }

   public static File file(JavaPlugin plugin, String path) {
      String p = path.startsWith("Data/") ? path : "Data/" + path;
      File file = new File(plugin.getDataFolder(), p);
      File parent = file.getParentFile();
      if (parent != null) {
         parent.mkdirs();
      }

      return file;
   }

   public static File migrate(JavaPlugin plugin, String oldPath, String newPath) {
      File target = file(plugin, newPath);
      if (!target.exists()) {
         File[] candidates = new File[]{new File(plugin.getDataFolder(), oldPath), new File(plugin.getDataFolder(), "Data/" + oldPath), new File(plugin.getDataFolder(), newPath)};

         for(File old : candidates) {
            if (old.isFile() && !old.equals(target)) {
               try {
                  Files.move(old.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                  break;
               } catch (Exception var12) {
                  try {
                     Files.copy(old.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                     old.delete();
                     break;
                  } catch (Exception var11) {
                  }
               }
            }
         }
      }

      return target;
   }
}
