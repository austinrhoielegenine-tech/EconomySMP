package me.xyr0.astralsmpcore.moderation;

import java.util.UUID;

public final class SusFlag {
   private final UUID playerId;
   private final String playerName;
   private final String anticheat;
   private final String check;
   private final double vl;
   private final long time;

   public SusFlag(UUID playerId, String playerName, String anticheat, String check, double vl, long time) {
      this.playerId = playerId;
      this.playerName = playerName;
      this.anticheat = anticheat;
      this.check = check;
      this.vl = vl;
      this.time = time;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public String getAnticheat() {
      return this.anticheat;
   }

   public String getCheck() {
      return this.check;
   }

   public double getVl() {
      return this.vl;
   }

   public long getTime() {
      return this.time;
   }
}
