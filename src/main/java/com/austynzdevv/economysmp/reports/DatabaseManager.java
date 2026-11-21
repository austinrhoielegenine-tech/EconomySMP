package com.austynzdevv.economysmp.reports;

import java.sql.Connection;

public class DatabaseManager {
   private final EconomyReports plugin;
   private SQLiteManager sqliteManager;
   private MySQLManager mysqlManager;

   public DatabaseManager(EconomyReports plugin) {
      this.plugin = plugin;
   }

   public void initialize() {
      String type = this.plugin.getConfig().getString("database.type", "SQLITE");
      if (type.equalsIgnoreCase("MYSQL")) {
         this.mysqlManager = new MySQLManager(this.plugin);
         this.mysqlManager.connect();
      } else {
         this.sqliteManager = new SQLiteManager(this.plugin);
         this.sqliteManager.connect();
      }

   }

   public void createReport(String reporterUUID, String reporterName, String reportedUUID, String reportedName, String reason) {
      if (this.sqliteManager != null) {
         this.sqliteManager.createReport(reporterUUID, reporterName, reportedUUID, reportedName, reason);
      }

      if (this.mysqlManager != null) {
         this.mysqlManager.createReport(reporterUUID, reporterName, reportedUUID, reportedName, reason);
      }

   }

   public Connection getConnection() {
      try {
         if (this.sqliteManager != null) {
            return this.sqliteManager.getConnection();
         }

         if (this.mysqlManager != null) {
            return this.mysqlManager.getConnection();
         }
      } catch (Exception var2) {
      }

      return null;
   }

   public void close() {
      if (this.sqliteManager != null) {
         this.sqliteManager.close();
      }

      if (this.mysqlManager != null) {
         this.mysqlManager.close();
      }

   }
}
