package com.austynzdevv.nyrethacore.chat2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class SecurityLogger {
   private final EconomyChat plugin;
   private final File logFile;

   public SecurityLogger(EconomyChat plugin) {
      this.plugin = plugin;
      File folder = new File(plugin.getDataFolder(), "logs");
      if (!folder.exists()) {
         folder.mkdirs();
      }

      this.logFile = new File(folder, "security.log");
   }

   public void log(String message) {
      String var10000 = String.valueOf(LocalDateTime.now());
      String finalMessage = "[" + var10000 + "] " + message;
      this.plugin.getLogger().warning(message);

      try {
         FileWriter writer = new FileWriter(this.logFile, true);
         writer.write(finalMessage + System.lineSeparator());
         writer.close();
      } catch (IOException var4) {
         this.plugin.getLogger().warning("Could not write security log.");
      }

   }
}
