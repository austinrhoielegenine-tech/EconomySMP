package com.austynzdevv.novacore.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportRepository {
   private final EconomyReports plugin;

   public ReportRepository(EconomyReports plugin) {
      this.plugin = plugin;
   }

   public List<Report> getAllReports() {
      List<Report> reports = new ArrayList();

      try {
         Connection connection = this.plugin.getDatabaseManager().getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports ORDER BY timestamp DESC");
         ResultSet result = statement.executeQuery();

         while(result.next()) {
            reports.add(new Report(UUID.fromString(result.getString("reported_uuid")), result.getString("reported_name"), result.getString("reason"), result.getString("date"), result.getString("reporter_name")));
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return reports;
   }

   public int getReportCount(UUID uuid) {
      try {
         Connection connection = this.plugin.getDatabaseManager().getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM reports WHERE reported_uuid=?");
         statement.setString(1, uuid.toString());
         ResultSet result = statement.executeQuery();
         if (result.next()) {
            return result.getInt(1);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return 0;
   }

   public String getLatestReport(UUID uuid) {
      try {
         Connection connection = this.plugin.getDatabaseManager().getConnection();
         PreparedStatement statement = connection.prepareStatement("SELECT date FROM reports WHERE reported_uuid=? ORDER BY timestamp DESC LIMIT 1");
         statement.setString(1, uuid.toString());
         ResultSet result = statement.executeQuery();
         if (result.next()) {
            return result.getString("date");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return "Unknown";
   }
}
