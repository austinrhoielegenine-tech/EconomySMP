package com.austynzdevv.nyrethacore.punishment2.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.austynzdevv.nyrethacore.punishment2.EconomyPunishment;
import com.austynzdevv.nyrethacore.punishment2.Punishment;

public class DatabaseManager {
   private final EconomyPunishment plugin;
   private Connection connection;

   public DatabaseManager(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public void connect() {
      try {
         File file = new File(this.plugin.getDataFolder(), "database.db");
         this.connection = DriverManager.getConnection("jdbc:sqlite:" + String.valueOf(file));
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void createTables() {
      try {
         Statement statement = this.connection.createStatement();
         statement.executeUpdate("CREATE TABLE IF NOT EXISTS punishments (\nid INTEGER PRIMARY KEY AUTOINCREMENT,\nuuid TEXT NOT NULL,\nplayer TEXT NOT NULL,\ntype TEXT NOT NULL,\nreason TEXT NOT NULL,\nduration TEXT,\nexpire_time LONG,\nstaff TEXT,\noffense INTEGER,\nban_id TEXT,\ncreated LONG,\nactive BOOLEAN\n)\n");
         statement.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void savePunishment(Punishment punishment) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("INSERT INTO punishments\n(uuid,player,type,reason,duration,expire_time,staff,offense,ban_id,created,active)\nVALUES (?,?,?,?,?,?,?,?,?,?,?)\n");
         ps.setString(1, punishment.getUuid().toString());
         ps.setString(2, punishment.getPlayer());
         ps.setString(3, punishment.getType());
         ps.setString(4, punishment.getReason());
         ps.setString(5, punishment.getDuration());
         ps.setLong(6, punishment.getExpireTime());
         ps.setString(7, punishment.getStaff());
         ps.setInt(8, punishment.getOffense());
         ps.setString(9, punishment.getBanId());
         ps.setLong(10, punishment.getDate());
         ps.setBoolean(11, punishment.isActive());
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public List<Punishment> getHistory(UUID uuid) {
      List<Punishment> list = new ArrayList();

      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM punishments\nWHERE uuid=?\nORDER BY id DESC\n");
         ps.setString(1, uuid.toString());
         ResultSet rs = ps.executeQuery();

         while(rs.next()) {
            list.add(this.fromResult(rs));
         }

         rs.close();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return list;
   }

   public Punishment getActivePunishment(UUID uuid, String type) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM punishments\nWHERE uuid=?\nAND type=?\nAND active=1\nORDER BY id DESC\nLIMIT 1\n");
         ps.setString(1, uuid.toString());
         ps.setString(2, type);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            Punishment p = this.fromResult(rs);
            long exp = p.getExpireTime();
            if (exp > 0L && System.currentTimeMillis() >= exp) {
               try {
                  this.removePunishment(p.getBanId());
               } catch (Exception var9) {
               }

               return null;
            }

            return p;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return null;
   }

   public void setPunishmentActive(String banId, boolean active) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("UPDATE punishments SET active=? WHERE ban_id=?");
         ps.setBoolean(1, active);
         ps.setString(2, banId);
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public int getOffenseCount(UUID uuid, String reason) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT COUNT(*) FROM punishments\nWHERE uuid=?\nAND LOWER(reason)=LOWER(?)\n");
         ps.setString(1, uuid.toString());
         ps.setString(2, reason);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            return rs.getInt(1);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return 0;
   }

   private Punishment fromResult(ResultSet rs) throws Exception {
      return new Punishment(UUID.fromString(rs.getString("uuid")), rs.getString("player"), rs.getString("type"), rs.getString("reason"), rs.getString("duration"), rs.getLong("expire_time"), rs.getString("staff"), rs.getInt("offense"), rs.getString("ban_id"), rs.getLong("created"), rs.getBoolean("active"));
   }

   public void close() {
      try {
         if (this.connection != null) {
            this.connection.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void removePunishment(String banId) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("UPDATE punishments SET active=0 WHERE ban_id=?");
         ps.setString(1, banId);
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public boolean banIdExists(String banId) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT id FROM punishments WHERE ban_id=?");
         ps.setString(1, banId);
         ResultSet rs = ps.executeQuery();
         boolean result = rs.next();
         rs.close();
         ps.close();
         return result;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}
