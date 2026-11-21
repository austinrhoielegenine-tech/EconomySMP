package me.xyr0.astralsmpcore.punishment2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.xyr0.astralsmpcore.punishment2.util.BanIdUtil;
import me.xyr0.astralsmpcore.punishment2.util.TimeUtil;

public class IPBanManager {
   private final AstralPunishment plugin;
   private Connection connection;

   public IPBanManager(AstralPunishment plugin) {
      this.plugin = plugin;
      this.connect();
      this.createTable();
      this.createIndexes();
   }

   private void connect() {
      try {
         File file = new File(this.plugin.getDataFolder(), "database.db");
         this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
         Statement st = this.connection.createStatement();

         try {
            st.execute("PRAGMA busy_timeout = 5000");
            st.execute("PRAGMA journal_mode = WAL");
         } catch (Throwable var6) {
            if (st != null) {
               try {
                  st.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (st != null) {
            st.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private void createTable() {
      try {
         Statement st = this.connection.createStatement();

         try {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS ip_bans (id INTEGER PRIMARY KEY AUTOINCREMENT,ip TEXT NOT NULL,player TEXT NOT NULL,type TEXT NOT NULL,reason TEXT NOT NULL,duration TEXT NOT NULL,expires LONG NOT NULL,staff TEXT NOT NULL,banid TEXT NOT NULL,created LONG NOT NULL)");
         } catch (Throwable var5) {
            if (st != null) {
               try {
                  st.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (st != null) {
            st.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private void createIndexes() {
      try {
         Statement st = this.connection.createStatement();

         try {
            st.executeUpdate("CREATE INDEX IF NOT EXISTS ip_bans_lookup ON ip_bans(ip, type)");
         } catch (Throwable var5) {
            if (st != null) {
               try {
                  st.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (st != null) {
            st.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static String normalizeIp(String ip) {
      if (ip == null) {
         return "";
      } else {
         String s = ip.trim().toLowerCase();
         if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
         }

         if (s.startsWith("::ffff:")) {
            s = s.substring(7);
         }

         int slash = s.indexOf(47);
         if (slash > 0) {
            s = s.substring(0, slash);
         }

         return s;
      }
   }

   public void banIP(String ip, String player, String type, String reason, String duration, String staff) {
      String norm = normalizeIp(ip);
      if (norm.isEmpty()) {
         this.plugin.getLogger().warning("Tried to IP ban with empty address for " + player);
      } else {
         try {
            long expires = this.calculateExpire(duration);
            PreparedStatement ps = this.connection.prepareStatement("INSERT INTO ip_bans (ip,player,type,reason,duration,expires,staff,banid,created) VALUES (?,?,?,?,?,?,?,?,?)");
            ps.setString(1, norm);
            ps.setString(2, player);
            ps.setString(3, type.toUpperCase());
            ps.setString(4, reason);
            ps.setString(5, duration);
            ps.setLong(6, expires);
            ps.setString(7, staff);
            ps.setString(8, BanIdUtil.generate());
            ps.setLong(9, System.currentTimeMillis());
            ps.executeUpdate();
            ps.close();
            this.plugin.getLogger().info("IP " + norm + " banned (" + type + ") for " + player);
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

   public boolean isBanned(String ip) {
      return this.getIPBanPunishment(ip) != null;
   }

   public Punishment getIPBanPunishment(String ip) {
      String norm = normalizeIp(ip);
      if (norm.isEmpty()) {
         return null;
      } else {
         try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM ip_bans WHERE (ip=? OR ip=?) AND UPPER(type)='BAN' ORDER BY id DESC LIMIT 1");
            ps.setString(1, norm);
            ps.setString(2, ip == null ? norm : ip.trim());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return null;
            } else {
               long expires = rs.getLong("expires");
               if (expires > 0L && System.currentTimeMillis() >= expires) {
                  String storedIp = rs.getString("ip");
                  rs.close();
                  ps.close();
                  this.removeExpired(storedIp, "BAN");
                  return null;
               } else {
                  String timeLeft;
                  if (expires <= 0L) {
                     timeLeft = "Permanent";
                  } else {
                     timeLeft = TimeUtil.formatTime(expires - System.currentTimeMillis());
                  }

                  Punishment punishment = new Punishment(UUID.randomUUID(), rs.getString("player"), "IPBAN", rs.getString("reason"), timeLeft, expires, rs.getString("staff"), 0, rs.getString("banid"), rs.getLong("created"), true);
                  rs.close();
                  ps.close();
                  return punishment;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }
   }

   public Punishment getIPMutePunishment(String ip) {
      String norm = normalizeIp(ip);
      if (norm.isEmpty()) {
         return null;
      } else {
         try {
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM ip_bans WHERE (ip=? OR ip=?) AND UPPER(type)='MUTE' ORDER BY id DESC LIMIT 1");
            ps.setString(1, norm);
            ps.setString(2, ip == null ? norm : ip.trim());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
               rs.close();
               ps.close();
               return null;
            } else {
               long expires = rs.getLong("expires");
               if (expires > 0L && System.currentTimeMillis() >= expires) {
                  String storedIp = rs.getString("ip");
                  rs.close();
                  ps.close();
                  this.removeExpired(storedIp, "MUTE");
                  return null;
               } else {
                  String timeLeft = expires <= 0L ? "Permanent" : TimeUtil.formatTime(expires - System.currentTimeMillis());
                  Punishment punishment = new Punishment(UUID.randomUUID(), rs.getString("player"), "IPMUTE", rs.getString("reason"), timeLeft, expires, rs.getString("staff"), 0, rs.getString("banid"), rs.getLong("created"), true);
                  rs.close();
                  ps.close();
                  return punishment;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
            return null;
         }
      }
   }

   private void removeExpired(String ip, String type) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("DELETE FROM ip_bans WHERE ip=? AND UPPER(type)=?");
         ps.setString(1, normalizeIp(ip));
         ps.setString(2, type.toUpperCase());
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void removeByBanId(String banId) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("DELETE FROM ip_bans WHERE banid=?");
         ps.setString(1, banId);
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void removeIp(String ip, String type) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("DELETE FROM ip_bans WHERE (ip=? OR ip=?) AND UPPER(type)=?");
         String norm = normalizeIp(ip);
         ps.setString(1, norm);
         ps.setString(2, ip);
         ps.setString(3, type.toUpperCase());
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private long calculateExpire(String duration) {
      if (duration == null) {
         return -1L;
      } else if (duration.equalsIgnoreCase("permanent")) {
         return -1L;
      } else {
         try {
            char unit = duration.charAt(duration.length() - 1);
            long amount = Long.parseLong(duration.substring(0, duration.length() - 1));
            long var10000;
            switch (unit) {
               case 'M' -> var10000 = amount * 30L * 24L * 60L * 60L * 1000L;
               case 'd' -> var10000 = amount * 24L * 60L * 60L * 1000L;
               case 'h' -> var10000 = amount * 60L * 60L * 1000L;
               case 'm' -> var10000 = amount * 60L * 1000L;
               case 's' -> var10000 = amount * 1000L;
               default -> var10000 = -1L;
            }

            long time = var10000;
            return time <= 0L ? -1L : System.currentTimeMillis() + time;
         } catch (Exception var7) {
            return -1L;
         }
      }
   }

   public void removeByPlayerAndReason(String player, String reason) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("DELETE FROM ip_bans WHERE LOWER(player)=LOWER(?) AND LOWER(reason)=LOWER(?)");
         ps.setString(1, player);
         ps.setString(2, reason);
         int n = ps.executeUpdate();
         ps.close();
         if (n > 0) {
            this.plugin.getLogger().info("Removed " + n + " IP punishment(s) for " + player + " / " + reason);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void removeByIpAndReason(String ip, String reason) {
      String norm = normalizeIp(ip);
      if (!norm.isEmpty()) {
         try {
            PreparedStatement ps = this.connection.prepareStatement("DELETE FROM ip_bans WHERE (ip=? OR ip=?) AND LOWER(reason)=LOWER(?)");
            ps.setString(1, norm);
            ps.setString(2, ip);
            ps.setString(3, reason);
            int n = ps.executeUpdate();
            ps.close();
            if (n > 0) {
               this.plugin.getLogger().info("Removed " + n + " IP row(s) for address " + norm + " / " + reason);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

   public List<String> getActiveReasonsForPlayer(String player) {
      List<String> list = new ArrayList();

      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT DISTINCT reason FROM ip_bans WHERE LOWER(player)=LOWER(?)");
         ps.setString(1, player);
         ResultSet rs = ps.executeQuery();

         while(rs.next()) {
            String r = rs.getString("reason");
            if (r != null && !list.contains(r)) {
               list.add(r);
            }
         }

         rs.close();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return list;
   }

   public void removeAllForPlayer(String player) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("DELETE FROM ip_bans WHERE LOWER(player)=LOWER(?)");
         ps.setString(1, player);
         ps.executeUpdate();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public boolean hasActiveForPlayer(String player, String reason) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT expires FROM ip_bans WHERE LOWER(player)=LOWER(?) AND LOWER(reason)=LOWER(?)");
         ps.setString(1, player);
         ps.setString(2, reason);
         ResultSet rs = ps.executeQuery();
         long now = System.currentTimeMillis();
         boolean ok = false;

         while(rs.next()) {
            long exp = rs.getLong("expires");
            if (exp <= 0L || exp > now) {
               ok = true;
               break;
            }
         }

         rs.close();
         ps.close();
         return ok;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public String getInfo(String ip) {
      try {
         PreparedStatement ps = this.connection.prepareStatement("SELECT player,type,reason,duration,staff FROM ip_bans WHERE ip=? OR ip=? ORDER BY id DESC LIMIT 1");
         String norm = normalizeIp(ip);
         ps.setString(1, norm);
         ps.setString(2, ip);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            String var10000 = rs.getString("player");
            String info = "Player: " + var10000 + "\nType: " + rs.getString("type") + "\nReason: " + rs.getString("reason") + "\nDuration: " + rs.getString("duration") + "\nStaff: " + rs.getString("staff");
            rs.close();
            ps.close();
            return info;
         }

         rs.close();
         ps.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      return "";
   }

   public void close() {
      try {
         if (this.connection != null) {
            this.connection.close();
         }
      } catch (Exception var2) {
      }

   }
}
