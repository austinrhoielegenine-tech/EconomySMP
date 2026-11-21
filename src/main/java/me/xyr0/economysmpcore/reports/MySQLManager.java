package me.xyr0.economysmpcore.reports;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class MySQLManager {
   private final AstralReports plugin;
   private Connection connection;

   public MySQLManager(AstralReports plugin) {
      this.plugin = plugin;
   }

   public void connect() {
      try {
         String host = this.plugin.getConfig().getString("mysql.host");
         String port = this.plugin.getConfig().getString("mysql.port");
         String database = this.plugin.getConfig().getString("mysql.database");
         String username = this.plugin.getConfig().getString("mysql.username");
         String password = this.plugin.getConfig().getString("mysql.password");
         this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
         PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports (id INT AUTO_INCREMENT PRIMARY KEY,reporter_uuid VARCHAR(100),reporter_name VARCHAR(100),reported_uuid VARCHAR(100),reported_name VARCHAR(100),reason VARCHAR(100),date VARCHAR(50),timestamp BIGINT)");
         statement.executeUpdate();
         this.plugin.getLogger().info("MySQL database connected.");
      } catch (SQLException e) {
         this.plugin.getLogger().severe("Failed connecting MySQL database.");
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
