package com.austynzdevv.novacore.rtpq;

public class WorldConfig {
   private final String id;
   private final String displayName;
   private final String worldName;
   private final int minRadius;
   private final int radius;

   public WorldConfig(String id, String displayName, String worldName, int minRadius, int radius) {
      this.id = id;
      this.displayName = displayName;
      this.worldName = worldName;
      this.minRadius = minRadius;
      this.radius = radius;
   }

   public String getId() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public int getMinRadius() {
      return this.minRadius;
   }

   public int getRadius() {
      return this.radius;
   }
}
