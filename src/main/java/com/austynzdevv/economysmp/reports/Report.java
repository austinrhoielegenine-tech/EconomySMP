package com.austynzdevv.economysmp.reports;

import java.util.UUID;

public class Report {
   private final UUID reportedUUID;
   private final String reportedName;
   private final String reason;
   private final String date;
   private final String reporterName;

   public Report(UUID reportedUUID, String reportedName, String reason, String date, String reporterName) {
      this.reportedUUID = reportedUUID;
      this.reportedName = reportedName;
      this.reason = reason;
      this.date = date;
      this.reporterName = reporterName;
   }

   public UUID getReportedUUID() {
      return this.reportedUUID;
   }

   public String getReportedName() {
      return this.reportedName;
   }

   public String getReason() {
      return this.reason;
   }

   public String getDate() {
      return this.date;
   }

   public String getReporterName() {
      return this.reporterName;
   }
}
