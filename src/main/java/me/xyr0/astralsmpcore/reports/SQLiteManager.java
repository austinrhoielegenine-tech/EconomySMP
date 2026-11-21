package me.xyr0.astralsmpcore.reports;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class SQLiteManager {
   private final AstralReports plugin;
   private Connection connection;

   public SQLiteManager(AstralReports plugin) {
      this.plugin = plugin;
   }

   public void connect() {
      try {
         File folder = this.plugin.getDataFolder();
         if (!folder.exists()) {
            folder.mkdirs();
         }

         File file = new File(folder, "reports.db");
         this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
         PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports (id INTEGER PRIMARY KEY AUTOINCREMENT,reporter_uuid TEXT,reporter_name TEXT,reported_uuid TEXT,reported_name TEXT,reason TEXT,date TEXT,timestamp BIGINT)");
         statement.executeUpdate();
         statement.close();
         this.plugin.getLogger().info("SQLite database connected.");
      } catch (SQLException e) {
         this.plugin.getLogger().severe("Failed connecting SQLite database.");
         e.printStackTrace();
      }

   }

   public void createReport(String reporterUUID, String reporterName, String reportedUUID, String reportedName, String reason) {
      try {
         PreparedStatement statement = this.connection.prepareStatement("INSERT INTO reports (reporter_uuid, reporter_name, reported_uuid, reported_name, reason, date, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)");
         statement.setString(1, reporterUUID);
         statement.setString(2, reporterName);
         statement.setString(3, reportedUUID);
         statement.setString(4, reportedName);
         statement.setString(5, reason);
         statement.setString(6, LocalDate.now().toString());
         statement.setLong(7, System.currentTimeMillis());
         statement.executeUpdate();
         statement.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }

   }

   public Connection getConnection() {
      return this.connection;
   }

   public void close() {
      try {
         if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }

   }
}
